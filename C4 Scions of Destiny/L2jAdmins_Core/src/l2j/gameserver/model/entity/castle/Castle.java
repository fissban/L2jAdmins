package l2j.gameserver.model.entity.castle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.DoorData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.clan.ClanMemberInstance;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.model.zone.type.CastleTeleportZone;
import l2j.gameserver.model.zone.type.SiegeZone;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.PledgeShowInfoUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

public class Castle
{
	protected static final Logger LOG = Logger.getLogger(Castle.class.getName());
	
	private static final String CASTLE_MANOR_DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?";
	private static final String CASTLE_MANOR_DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?";
	private static final String CASTLE_MANOR_DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?";
	private static final String CASTLE_MANOR_DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?";
	private static final String CASTLE_UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
	private static final String CASTLE_UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";
	
	private List<CropProcureHolder> procure = new ArrayList<>();
	private List<SeedProductionHolder> production = new ArrayList<>();
	private List<CropProcureHolder> procureNext = new ArrayList<>();
	private List<SeedProductionHolder> productionNext = new ArrayList<>();
	private boolean isNextPeriodApproved = false;
	
	private int id = 0;
	private final List<L2DoorInstance> doors = new ArrayList<>();
	private String name = "";
	private int ownerId = 0;
	private Siege siege = null;
	private Calendar siegeDate;
	private int siegeDayOfWeek = 7; // Default to Saturday
	private int siegeHourOfDay = 20; // Default to 8 pm server time
	// Actual Tax Rate
	private int taxPercent = 0;
	// Next Tax Rate
	private int nextTaxPorcent = 0;
	private boolean applyToDayNextTaxRate = true;
	
	private int treasury = 0;
	
	CastleTeleportZone teleZone;
	
	private SiegeZone zone;
	
	private final Map<Integer, Integer> engrave = new HashMap<>();
	
	public Castle(int castleId)
	{
		id = castleId;
		
		load();
		loadDoors();
		loadDoorUpgrade();
	}
	
	public void engrave(Clan clan, int objId)
	{
		engrave.put(objId, clan.getId());
		if (engrave.size() == getSiege().getArtifactMngr().getCount())
		{
			for (int id : engrave.values())
			{
				if (id != clan.getId())
				{
					getSiege().announceToPlayer("Clan " + clan.getName() + " has finished engraving one of the rulers.", true);
					return;
				}
			}
			
			engrave.clear();
			
			setOwner(clan);
		}
		else
		{
			getSiege().announceToPlayer("Clan " + clan.getName() + " has finished engraving one of the rulers.", true);
		}
	}
	
	/**
	 * Add amount to castle instance's treasury (warehouse).
	 * @param amount
	 */
	public void addToTreasury(int amount)
	{
		if (getOwnerId() <= 0)
		{
			return;
		}
		
		// If current castle instance is not Aden
		if (!name.equalsIgnoreCase("aden"))
		{
			Castle aden = CastleData.getInstance().getCastleByName("aden");
			if (aden != null)
			{
				// Find out what Aden gets from the current castle instance's income
				int adenTax = amount * (aden.getTaxPercent() / 100);
				if (aden.getOwnerId() > 0)
				{
					// Only bother to really add the tax to the treasury if not npc owned
					aden.addToTreasury(adenTax);
				}
				
				// Subtract Aden's income from current castle instance's income
				amount -= adenTax;
			}
		}
		
		addToTreasuryNoTax(amount);
	}
	
	/**
	 * Add amount to castle instance's treasury (warehouse), no tax paying.
	 * @param  amount
	 * @return
	 */
	public boolean addToTreasuryNoTax(int amount)
	{
		if (getOwnerId() <= 0)
		{
			return false;
		}
		
		if (amount < 0)
		{
			amount *= -1;
			if (treasury < amount)
			{
				return false;
			}
			treasury -= amount;
		}
		else
		{
			if (((long) treasury + amount) > Integer.MAX_VALUE)
			{
				treasury = Integer.MAX_VALUE;
			}
			else
			{
				treasury += amount;
			}
		}
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE castle SET treasury = ? WHERE id = ?"))
		{
			ps.setInt(1, getTreasury());
			ps.setInt(2, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Move non clan members off castle area and to nearest town.
	 */
	public void banishForeigners()
	{
		zone.banishForeigners(getOwnerId());
	}
	
	/**
	 * We open or close a certain door of the castle in question<br>
	 * <u>Actions : </u><br>
	 * <li>true -> Open Doors</li>
	 * <li>false -> Close Doors</li>
	 * @param doorId : int
	 * @param open   : boolean
	 */
	public void openCloseDoor(int doorId, boolean open)
	{
		L2DoorInstance door = DoorData.getInstance().getDoor(doorId);
		
		if (door != null)
		{
			if (open)
			{
				door.openMe();
			}
			else
			{
				door.closeMe();
			}
		}
		else
		{
			LOG.warning(getClass().getSimpleName() + ": doorId not exist => " + doorId);
		}
	}
	
	public void setOwner(Clan clan)
	{
		// Remove old owner
		if ((getOwnerId() > 0) && ((clan == null) || (clan.getId() != getOwnerId())))
		{
			// Try to find clan instance
			Clan oldOwner = ClanData.getInstance().getClanById(getOwnerId());
			if (oldOwner != null)
			{
				CastleData.getInstance().removeCirclet(oldOwner, getId());
				// Unset has castle flag for old owner
				oldOwner.setHasCastle(0);
			}
		}
		
		// Update in database
		updateOwnerInDB(clan);
		
		if (getSiege().isInProgress())
		{
			// Mid victory phase of siege
			getSiege().midVictory();
			
			// "There is a new castle Lord" message when the castle change of hands. Message sent for both sides.
			getSiege().announceToPlayer(new SystemMessage(SystemMessage.NEW_CASTLE_LORD), true);
		}
	}
	
	/**
	 * Adjust the tax rate that will have our castle
	 * @param taxPercent
	 */
	public void setTaxRateInDB(int taxPercent)
	{
		this.taxPercent = taxPercent;
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE castle SET taxPercent = ? WHERE id = ?"))
		{
			ps.setInt(1, taxPercent);
			ps.setInt(2, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * We set the new tax rate that will take our castle.<br>
	 * <u><b>Note: </b></u><br>
	 * <li>We update our DB the new value will have our next tax rate
	 * <li>This value will only be read in class TaskAdjustNewTaskRate
	 * @param taxPercent
	 */
	public void setNextTaxRateInDB(int taxPercent)
	{
		int maxTax;
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DAWN:
				maxTax = 25;
				break;
			case DUSK:
				maxTax = 5;
				break;
			default: // no owner (NULL)
				maxTax = 15;
		}
		
		if (taxPercent < 0)
		{
			taxPercent = 0;
		}
		
		if (taxPercent > maxTax)
		{
			taxPercent = maxTax;
		}
		
		applyToDayNextTaxRate = false;
		nextTaxPorcent = taxPercent;
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE castle SET nextTaxPercent = ? WHERE id = ?"))
		{
			ps.setInt(1, taxPercent);
			ps.setInt(2, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Respawn all doors on castle grounds.
	 */
	public void spawnDoors()
	{
		spawnDoors(false);
	}
	
	/**
	 * Respawn all doors on castle grounds
	 * @param isDoorWeak
	 */
	public void spawnDoors(boolean isDoorWeak)
	{
		for (L2DoorInstance door : doors)
		{
			door.spawnMe(door.getX(), door.getY(), door.getZ());
			
			if (door.isDead())
			{
				door.doRevive();
				door.setCurrentHp((isDoorWeak) ? (door.getStat().getMaxHp() / 2) : (door.getStat().getMaxHp()));
			}
			
			if (door.isOpen())
			{
				door.closeMe();
			}
			
			door.broadcastStatusUpdate();
		}
	}
	
	public void upgradeDoor(int doorId, int hp)
	{
		L2DoorInstance door = getDoorById(doorId);
		if (door == null)
		{
			return;
		}
		
		if (door.getId() == doorId)
		{
			door.setCurrentHp(door.getStat().getMaxHp() + hp);
			saveDoorUpgrade(doorId, hp);
			return;
		}
	}
	
	private void load()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("SELECT * FROM castle WHERE id = ?"))
			{
				ps.setInt(1, getId());
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						name = rs.getString("name");
						
						siegeDate = Calendar.getInstance();
						siegeDate.setTimeInMillis(rs.getLong("siegeDate"));
						
						siegeDayOfWeek = rs.getInt("siegeDayOfWeek");
						if ((siegeDayOfWeek < 1) || (siegeDayOfWeek > 7))
						{
							siegeDayOfWeek = 7;
						}
						
						siegeHourOfDay = rs.getInt("siegeHourOfDay");
						if ((siegeHourOfDay < 0) || (siegeHourOfDay > 23))
						{
							siegeHourOfDay = 20;
						}
						
						taxPercent = rs.getInt("taxPercent");
						nextTaxPorcent = rs.getInt("nextTaxPercent");
						treasury = rs.getInt("treasury");
					}
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement("SELECT clan_id FROM clan_data WHERE hasCastle = ?"))
			{
				ps.setInt(1, getId());
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						ownerId = rs.getInt("clan_id");
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception loadCastleData(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// This method loads castle door data
	private void loadDoors()
	{
		for (L2DoorInstance door : DoorData.getInstance().getCastleDoors())
		{
			if (door.getCastleId() == getId())
			{
				doors.add(door);
			}
		}
	}
	
	// This method loads castle door upgrade data from database
	private void loadDoorUpgrade()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM castle_doorupgrade WHERE castleId=?"))
		{
			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery())
			{
				while (rs.next())
				{
					int id = rs.getInt("id");
					int hp = rs.getInt("hp");
					upgradeDoor(id, hp);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception loadCastleDoorUpgrade(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void removeDoorUpgrade()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM castle_doorupgrade WHERE castleId=?"))
		{
			ps.setInt(1, id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception removeDoorUpgrade(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void saveDoorUpgrade(int doorId, int hp)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO castle_doorupgrade (doorId, hp, castleId) VALUES (?,?,?)"))
		{
			ps.setInt(1, doorId);
			ps.setInt(2, hp);
			ps.setInt(3, id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception saveDoorUpgrade(int doorId, int hp, int pDef, int mDef): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void updateOwnerInDB(Clan clan)
	{
		if (clan != null)
		{
			// Update owner id property
			ownerId = clan.getId();
		}
		else
		{
			// Remove owner
			ownerId = 0;
			resetManor();
		}
		
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=?"))
			{
				ps.setInt(1, id);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=?"))
			{
				ps.setInt(1, id);
				ps.setInt(2, getOwnerId());
				ps.execute();
			}
			
			// Announce to clan members
			if (clan != null)
			{
				// Set has castle flag for new owner
				clan.setHasCastle(getId());
				AnnouncementsData.getInstance().announceToAll(clan.getName() + " has taken " + getName() + " castle!");
				
				for (ClanMemberInstance member : clan.getMembers())
				{
					if (member.isOnline() && (member.getPlayerInstance() != null))
					{
						member.getPlayerInstance().sendPacket(new PledgeShowInfoUpdate(clan));
					}
				}
				
				clan.broadcastToOnlineMembers(new PlaySound(PlaySoundType.MUSIC_SIEGE_VITORY));
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception updateOwnerInDB(L2Clan clan): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public final int getId()
	{
		return id;
	}
	
	public final L2DoorInstance getDoorById(int doorId)
	{
		if (doorId <= 0)
		{
			return null;
		}
		
		for (L2DoorInstance door : getDoors())
		{
			if (door.getId() == doorId)
			{
				return door;
			}
		}
		return null;
	}
	
	public final List<L2DoorInstance> getDoors()
	{
		return doors;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public final int getOwnerId()
	{
		return ownerId;
	}
	
	public final Siege getSiege()
	{
		if (siege == null)
		{
			siege = new Siege(this);
		}
		return siege;
	}
	
	public final Calendar getSiegeDate()
	{
		return siegeDate;
	}
	
	public final int getSiegeDayOfWeek()
	{
		return siegeDayOfWeek;
	}
	
	public final int getSiegeHourOfDay()
	{
		return siegeHourOfDay;
	}
	
	public final int getNextTaxRatePorcent()
	{
		return nextTaxPorcent;
	}
	
	/**
	 * It indicates that a new day passes, and can apply the new tax rate.
	 */
	public void setNewDay()
	{
		applyToDayNextTaxRate = true;
	}
	
	/**
	 * It verified if already runs one day and may or may not apply the new tax rate.
	 * @return boolean
	 */
	public boolean isApplyNewTaxRate()
	{
		return applyToDayNextTaxRate;
	}
	
	/**
	 * Tax rate in percentage
	 * @return int
	 */
	public final int getTaxPercent()
	{
		return taxPercent;
	}
	
	public final double getTaxRate()
	{
		return taxPercent / 100.0;
	}
	
	public final int getTreasury()
	{
		return treasury;
	}
	
	public boolean checkIfInZone(int x, int y, int z)
	{
		return zone.isInsideZone(x, y, z);
	}
	
	public void setZone(SiegeZone zone)
	{
		this.zone = zone;
	}
	
	public SiegeZone getZone()
	{
		return zone;
	}
	
	public void setTeleZone(CastleTeleportZone zone)
	{
		teleZone = zone;
	}
	
	public CastleTeleportZone getTeleZone()
	{
		return teleZone;
	}
	
	public void oustAllPlayers()
	{
		getTeleZone().oustAllPlayers();
	}
	
	public double getDistance(L2Object obj)
	{
		return zone.getDistanceToZone(obj);
	}
	
	public List<SeedProductionHolder> getSeedProduction(int period)
	{
		return (period == CastleManorManager.PERIOD_CURRENT ? production : productionNext);
	}
	
	public List<CropProcureHolder> getCropProcure(int period)
	{
		return (period == CastleManorManager.PERIOD_CURRENT ? procure : procureNext);
	}
	
	public void setSeedProduction(List<SeedProductionHolder> list, int period)
	{
		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			production = list;
		}
		else
		{
			productionNext = list;
		}
	}
	
	public void setCropProcure(List<CropProcureHolder> crop, int period)
	{
		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			procure = crop;
		}
		else
		{
			procureNext = crop;
		}
	}
	
	public synchronized SeedProductionHolder getSeed(int seedId, int period)
	{
		for (SeedProductionHolder seed : getSeedProduction(period))
		{
			if (seed.getId() == seedId)
			{
				return seed;
			}
		}
		
		return null;
	}
	
	public synchronized CropProcureHolder getCrop(int cropId, int period)
	{
		for (CropProcureHolder crop : getCropProcure(period))
		{
			if (crop.getId() == cropId)
			{
				return crop;
			}
		}
		return null;
	}
	
	public int getManorCost(int period)
	{
		List<CropProcureHolder> procure;
		List<SeedProductionHolder> production;
		
		if (period == CastleManorManager.PERIOD_CURRENT)
		{
			procure = this.procure;
			production = this.production;
		}
		else
		{
			procure = procureNext;
			production = productionNext;
		}
		
		int total = 0;
		if (production != null)
		{
			for (SeedProductionHolder seed : production)
			{
				total += ManorData.getInstance().getSeedBuyPrice(seed.getId()) * seed.getStartProduce();
			}
		}
		
		if (procure != null)
		{
			for (CropProcureHolder crop : procure)
			{
				total += crop.getPrice() * crop.getStartAmount();
			}
		}
		return total;
	}
	
	/**
	 * Save manor production data
	 */
	public void saveSeedData()
	{
		// TODO dejar estos querys igual que en el resto del source
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION))
		{
			ps.setInt(1, getId());
			ps.execute();
			
			if (production != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_production VALUES ";
				String values[] = new String[production.size()];
				for (SeedProductionHolder s : production)
				{
					values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + CastleManorManager.PERIOD_CURRENT + ")";
					count++;
				}
				
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					
					try (PreparedStatement ps1 = con.prepareStatement(query))
					{
						ps1.execute();
					}
				}
			}
			
			if (productionNext != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_production VALUES ";
				String values[] = new String[productionNext.size()];
				for (SeedProductionHolder s : productionNext)
				{
					values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + CastleManorManager.PERIOD_NEXT + ")";
					count++;
				}
				
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					
					try (PreparedStatement ps2 = con.prepareStatement(query))
					{
						ps2.execute();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Error adding seed production data for castle " + getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Save manor production data for specified period
	 * @param period
	 */
	public void saveSeedData(int period)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION_PERIOD))
		{
			ps.setInt(1, getId());
			ps.setInt(2, period);
			ps.execute();
			
			List<SeedProductionHolder> prod = getSeedProduction(period);
			
			if (prod != null)
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_production VALUES ";
				String values[] = new String[prod.size()];
				for (SeedProductionHolder s : prod)
				{
					values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + period + ")";
					count++;
				}
				
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					
					try (PreparedStatement ps1 = con.prepareStatement(query))
					{
						ps1.execute();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Error adding seed production data for castle " + getName() + ": " + e.getMessage());
		}
	}
	
	// Save crop procure data
	public void saveCropData()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE))
		{
			ps.setInt(1, getId());
			ps.execute();
			
			if (!procure.isEmpty())
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				String values[] = new String[procure.size()];
				for (CropProcureHolder cp : procure)
				{
					values[count] = "(" + getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + CastleManorManager.PERIOD_CURRENT + ")";
					count++;
				}
				
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					
					try (PreparedStatement ps1 = con.prepareStatement(query))
					{
						ps1.execute();
					}
				}
			}
			
			if ((procureNext != null) && (procureNext.size() > 0))
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				String values[] = new String[procureNext.size()];
				for (CropProcureHolder cp : procureNext)
				{
					values[count] = "(" + getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + CastleManorManager.PERIOD_NEXT + ")";
					count++;
				}
				
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					
					try (PreparedStatement ps2 = con.prepareStatement(query))
					{
						ps2.execute();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Error adding crop data for castle " + getName() + ": " + e.getMessage());
		}
	}
	
	// Save crop procure data for specified period
	public void saveCropData(int period)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE_PERIOD))
		{
			ps.setInt(1, getId());
			ps.setInt(2, period);
			ps.execute();
			
			List<CropProcureHolder> proc = getCropProcure(period);
			
			if ((proc != null) && (proc.size() > 0))
			{
				int count = 0;
				String query = "INSERT INTO castle_manor_procure VALUES ";
				String values[] = new String[proc.size()];
				
				for (CropProcureHolder cp : proc)
				{
					values[count] = "(" + getId() + "," + cp.getId() + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.getPrice() + "," + cp.getReward() + "," + period + ")";
					count++;
				}
				
				if (values.length > 0)
				{
					query += values[0];
					for (int i = 1; i < values.length; i++)
					{
						query += "," + values[i];
					}
					
					try (PreparedStatement ps1 = con.prepareStatement(query))
					{
						ps1.execute();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Error adding crop data for castle " + getName() + ": " + e.getMessage());
		}
	}
	
	public void updateCrop(int cropId, int amount, int period)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(CASTLE_UPDATE_CROP))
		{
			ps.setInt(1, amount);
			ps.setInt(2, cropId);
			ps.setInt(3, getId());
			ps.setInt(4, period);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.info(getClass().getSimpleName() + ": Error adding crop data for castle " + getName() + ": " + e.getMessage());
		}
	}
	
	public void updateSeed(int seedId, int amount, int period)
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(CASTLE_UPDATE_SEED))
		{
			ps.setInt(1, amount);
			ps.setInt(2, seedId);
			ps.setInt(3, getId());
			ps.setInt(4, period);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Error adding seed production data for castle " + getName() + ": " + e.getMessage());
		}
	}
	
	public boolean isNextPeriodApproved()
	{
		return isNextPeriodApproved;
	}
	
	public void setNextPeriodApproved(boolean val)
	{
		isNextPeriodApproved = val;
	}
	
	public void resetManor()
	{
		setCropProcure(new ArrayList<CropProcureHolder>(), CastleManorManager.PERIOD_CURRENT);
		setCropProcure(new ArrayList<CropProcureHolder>(), CastleManorManager.PERIOD_NEXT);
		setSeedProduction(new ArrayList<SeedProductionHolder>(), CastleManorManager.PERIOD_CURRENT);
		setSeedProduction(new ArrayList<SeedProductionHolder>(), CastleManorManager.PERIOD_NEXT);
		if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
		{
			saveCropData();
			saveSeedData();
		}
	}
}
