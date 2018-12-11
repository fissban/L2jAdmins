package l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.model.itemcontainer.ItemContainer;
import l2j.gameserver.model.itemcontainer.warehouse.ClanWarehouse;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;
import l2j.util.UtilPrint;

/**
 * Class For Castle Manor Manager Load manor data from DB Update/Reload/Delete Handles all schedule for manor
 * @author l3x
 */
public class CastleManorManager
{
	protected static final Logger LOG = Logger.getLogger(CastleManorManager.class.getName());
	
	public static final byte PERIOD_CURRENT = 0;
	public static final byte PERIOD_NEXT = 1;
	
	private static final String CASTLE_MANOR_LOAD_PROCURE = "SELECT * FROM castle_manor_procure WHERE castle_id=?";
	private static final String CASTLE_MANOR_LOAD_PRODUCTION = "SELECT * FROM castle_manor_production WHERE castle_id=?";
	
	private Calendar manorRefresh;
	private Calendar periodApprove;
	
	private boolean underMaintenance;
	private boolean disabled;
	
	protected ScheduledFuture<?> scheduledManorRefresh;
	protected ScheduledFuture<?> scheduledMaintenanceEnd;
	protected ScheduledFuture<?> scheduledNextPeriodapprove;
	
	public CastleManorManager()
	{
		UtilPrint.result("CastleManorManager", "", "OK");
		
		load(); // load data from database
		init(); // schedule all manor related events
		underMaintenance = false;
		disabled = !Config.ALLOW_MANOR;
		
		boolean isApproved;
		if (periodApprove.getTimeInMillis() > manorRefresh.getTimeInMillis())
		{
			// Next approve period already scheduled
			isApproved = (manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis());
		}
		else
		{
			isApproved = ((periodApprove.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) && (manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()));
		}
		
		for (Castle c : CastleData.getInstance().getCastles())
		{
			c.setNextPeriodApproved(isApproved);
		}
	}
	
	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			for (Castle castle : CastleData.getInstance().getCastles())
			{
				List<SeedProductionHolder> production = new ArrayList<>();
				List<SeedProductionHolder> productionNext = new ArrayList<>();
				List<CropProcureHolder> procure = new ArrayList<>();
				List<CropProcureHolder> procureNext = new ArrayList<>();
				
				// restore seed production info
				try (PreparedStatement ps = con.prepareStatement(CASTLE_MANOR_LOAD_PRODUCTION))
				{
					ps.setInt(1, castle.getId());
					try (ResultSet rs = ps.executeQuery())
					{
						while (rs.next())
						{
							int seedId = rs.getInt("seed_id");
							int canProduce = rs.getInt("can_produce");
							int startProduce = rs.getInt("start_produce");
							int price = rs.getInt("seed_price");
							int period = rs.getInt("period");
							if (period == PERIOD_CURRENT)
							{
								production.add(new SeedProductionHolder(seedId, canProduce, price, startProduce));
							}
							else
							{
								productionNext.add(new SeedProductionHolder(seedId, canProduce, price, startProduce));
							}
						}
					}
				}
				
				castle.setSeedProduction(production, PERIOD_CURRENT);
				castle.setSeedProduction(productionNext, PERIOD_NEXT);
				
				// restore procure info
				try (PreparedStatement ps = con.prepareStatement(CASTLE_MANOR_LOAD_PROCURE))
				{
					ps.setInt(1, castle.getId());
					try (ResultSet rs = ps.executeQuery())
					{
						while (rs.next())
						{
							int cropId = rs.getInt("crop_id");
							int canBuy = rs.getInt("can_buy");
							int startBuy = rs.getInt("start_buy");
							int rewardType = rs.getInt("reward_type");
							int price = rs.getInt("price");
							int period = rs.getInt("period");
							if (period == PERIOD_CURRENT)
							{
								procure.add(new CropProcureHolder(cropId, canBuy, rewardType, startBuy, price));
							}
							else
							{
								procureNext.add(new CropProcureHolder(cropId, canBuy, rewardType, startBuy, price));
							}
						}
					}
				}
				
				castle.setCropProcure(procure, PERIOD_CURRENT);
				castle.setCropProcure(procureNext, PERIOD_NEXT);
				
				if (!procure.isEmpty() || !procureNext.isEmpty() || !production.isEmpty() || !productionNext.isEmpty())
				{
					LOG.info(castle.getName() + ": Data loaded");
				}
			}
		}
		catch (Exception e)
		{
			LOG.info("Error restoring manor data: " + e.getMessage());
		}
	}
	
	protected void init()
	{
		manorRefresh = Calendar.getInstance();
		manorRefresh.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_REFRESH_TIME);
		manorRefresh.set(Calendar.MINUTE, Config.ALT_MANOR_REFRESH_MIN);
		
		periodApprove = Calendar.getInstance();
		periodApprove.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_APPROVE_TIME);
		periodApprove.set(Calendar.MINUTE, Config.ALT_MANOR_APPROVE_MIN);
		
		updateManorRefresh();
		updatePeriodApprove();
	}
	
	public void updateManorRefresh()
	{
		LOG.info("Manor System: Manor refresh updated");
		scheduledManorRefresh = ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!isDisabled())
			{
				setUnderMaintenance(true);
				LOG.info("Manor System: Under maintenance mode started");
				
				scheduledMaintenanceEnd = ThreadPoolManager.getInstance().schedule(() ->
				{
					LOG.info("Manor System: Next period started");
					setNextPeriod();
					try
					{
						save();
					}
					catch (Exception e)
					{
						LOG.info("Manor System: Failed to save manor data: " + e);
					}
					setUnderMaintenance(false);
				}, Config.ALT_MANOR_MAINTENANCE_PERIOD);
			}
			updateManorRefresh();
		}, getMillisToManorRefresh());
	}
	
	public void updatePeriodApprove()
	{
		LOG.info("Manor System: Manor period approve updated");
		scheduledNextPeriodapprove = ThreadPoolManager.getInstance().schedule(() ->
		{
			if (!isDisabled())
			{
				approveNextPeriod();
				LOG.info("Manor System: Next period approved");
			}
			updatePeriodApprove();
		}, getMillisToNextPeriodApprove());
	}
	
	public long getMillisToManorRefresh()
	{
		if ((manorRefresh.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) < 120000)
		{
			setNewManorRefresh();
		}
		
		LOG.info("Manor System: New Schedule for manor refresh @ " + manorRefresh.getTime());
		
		return (manorRefresh.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}
	
	public void setNewManorRefresh()
	{
		manorRefresh = Calendar.getInstance();
		manorRefresh.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_REFRESH_TIME);
		manorRefresh.set(Calendar.MINUTE, Config.ALT_MANOR_REFRESH_MIN);
		manorRefresh.set(Calendar.SECOND, 0);
		manorRefresh.add(Calendar.HOUR_OF_DAY, 24);
	}
	
	public long getMillisToNextPeriodApprove()
	{
		if ((periodApprove.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) < 120000)
		{
			setNewPeriodApprove();
		}
		
		return (periodApprove.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}
	
	public long setNewPeriodApprove()
	{
		periodApprove = Calendar.getInstance();
		periodApprove.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_APPROVE_TIME);
		periodApprove.set(Calendar.MINUTE, Config.ALT_MANOR_APPROVE_MIN);
		periodApprove.set(Calendar.SECOND, 0);
		periodApprove.add(Calendar.HOUR_OF_DAY, 24);
		
		LOG.info("Manor System: New Schedule for period approve @ " + periodApprove.getTime());
		
		return (periodApprove.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}
	
	public void setNextPeriod()
	{
		for (Castle c : CastleData.getInstance().getCastles())
		{
			if (c.getOwnerId() <= 0)
			{
				continue;
			}
			
			Clan clan = ClanData.getInstance().getClanById(c.getOwnerId());
			if (clan == null)
			{
				continue;
			}
			
			ItemContainer cwh = clan.getWarehouse();
			if (!(cwh instanceof ClanWarehouse))
			{
				LOG.info("Can't get clan warehouse for clan " + ClanData.getInstance().getClanById(c.getOwnerId()));
				return;
			}
			
			for (CropProcureHolder crop : c.getCropProcure(PERIOD_CURRENT))
			{
				if (crop.getStartAmount() == 0)
				{
					continue;
				}
				
				// adding bought crops to clan warehouse
				if ((crop.getStartAmount() - crop.getAmount()) > 0)
				{
					int count = crop.getStartAmount() - crop.getAmount();
					count = (count * 90) / 100;
					if (count < 1)
					{
						if (Rnd.nextInt(99) < 90)
						{
							count = 1;
						}
					}
					
					if (count > 0)
					{
						cwh.addItem("Manor", ManorData.getInstance().getMatureCrop(crop.getId()), count, null, null);
					}
				}
				
				// reserved and not used money giving back to treasury
				if (crop.getAmount() > 0)
				{
					c.addToTreasuryNoTax(crop.getAmount() * crop.getPrice());
				}
			}
			
			c.setSeedProduction(c.getSeedProduction(PERIOD_NEXT), PERIOD_CURRENT);
			c.setCropProcure(c.getCropProcure(PERIOD_NEXT), PERIOD_CURRENT);
			
			if (c.getTreasury() < c.getManorCost(PERIOD_CURRENT))
			{
				c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
				c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
			}
			else
			{
				List<SeedProductionHolder> production = new ArrayList<>();
				for (SeedProductionHolder s : c.getSeedProduction(PERIOD_CURRENT))
				{
					s.setCanProduce(s.getStartProduce());
					production.add(s);
				}
				c.setSeedProduction(production, PERIOD_NEXT);
				
				List<CropProcureHolder> procure = new ArrayList<>();
				for (CropProcureHolder cr : c.getCropProcure(PERIOD_CURRENT))
				{
					cr.setAmount(cr.getStartAmount());
					procure.add(cr);
				}
				
				c.setCropProcure(procure, PERIOD_NEXT);
			}
			
			if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
			{
				c.saveCropData();
				c.saveSeedData();
			}
			
			// Sending notification to a clan leader
			L2PcInstance clanLeader = L2World.getInstance().getPlayer(clan.getLeader().getObjectId());
			if (clanLeader != null)
			{
				clanLeader.sendPacket(new SystemMessage(SystemMessage.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED));
			}
			
			c.setNextPeriodApproved(false);
		}
	}
	
	public void approveNextPeriod()
	{
		for (Castle c : CastleData.getInstance().getCastles())
		{
			boolean notFunc = false;
			
			// Castle has no owner
			if (c.getOwnerId() <= 0)
			{
				c.setCropProcure(new ArrayList<CropProcureHolder>(), PERIOD_NEXT);
				c.setSeedProduction(new ArrayList<SeedProductionHolder>(), PERIOD_NEXT);
			}
			else if (c.getTreasury() < c.getManorCost(PERIOD_NEXT))
			{
				notFunc = true;
				c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
				c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
			}
			else
			{
				ItemContainer cwh = ClanData.getInstance().getClanById(c.getOwnerId()).getWarehouse();
				if (!(cwh instanceof ClanWarehouse))
				{
					LOG.info("Can't get clan warehouse for clan " + ClanData.getInstance().getClanById(c.getOwnerId()));
					return;
				}
				
				int slots = 0;
				for (CropProcureHolder crop : c.getCropProcure(PERIOD_NEXT))
				{
					if (crop.getStartAmount() > 0)
					{
						if (cwh.getItemById(ManorData.getInstance().getMatureCrop(crop.getId())) == null)
						{
							slots++;
						}
					}
				}
				
				if (!cwh.validateCapacity(slots))
				{
					notFunc = true;
					c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
					c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
				}
			}
			
			c.setNextPeriodApproved(true);
			c.addToTreasuryNoTax((-1) * c.getManorCost(PERIOD_NEXT));
			
			if (notFunc)
			{
				Clan clan = ClanData.getInstance().getClanById(c.getOwnerId());
				L2PcInstance clanLeader = null;
				if (clan != null)
				{
					clanLeader = L2World.getInstance().getPlayer(clan.getLeader().getObjectId());
				}
				if (clanLeader != null)
				{
					clanLeader.sendPacket(new SystemMessage(SystemMessage.THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION));
				}
			}
		}
	}
	
	private List<SeedProductionHolder> getNewSeedsList(int castleId)
	{
		List<SeedProductionHolder> seeds = new ArrayList<>();
		
		for (int sd : ManorData.getInstance().getSeedsForCastle(castleId))
		{
			seeds.add(new SeedProductionHolder(sd));
		}
		
		return seeds;
	}
	
	private List<CropProcureHolder> getNewCropsList(int castleId)
	{
		List<CropProcureHolder> crops = new ArrayList<>();
		
		for (int cr : ManorData.getInstance().getCropsForCastle(castleId))
		{
			crops.add(new CropProcureHolder(cr));
		}
		
		return crops;
	}
	
	public boolean isUnderMaintenance()
	{
		return underMaintenance;
	}
	
	public void setUnderMaintenance(boolean mode)
	{
		underMaintenance = mode;
	}
	
	public boolean isDisabled()
	{
		return disabled;
	}
	
	public void setDisabled(boolean mode)
	{
		disabled = mode;
	}
	
	public SeedProductionHolder getNewSeedProduction(int id, int amount, int price, int sales)
	{
		return new SeedProductionHolder(id, amount, price, sales);
	}
	
	public CropProcureHolder getNewCropProcure(int id, int amount, int type, int price, int buy)
	{
		return new CropProcureHolder(id, amount, type, buy, price);
	}
	
	public void save()
	{
		for (Castle c : CastleData.getInstance().getCastles())
		{
			c.saveSeedData();
			c.saveCropData();
		}
	}
	
	public static CastleManorManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CastleManorManager INSTANCE = new CastleManorManager();
	}
}
