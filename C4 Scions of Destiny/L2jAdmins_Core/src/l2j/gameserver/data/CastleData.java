package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.ClanMemberInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.util.UtilPrint;

public class CastleData
{
	private static final Logger LOG = Logger.getLogger(CastleData.class.getName());
	
	private final Map<Integer, Castle> castles = new HashMap<>();
	private static final int[] CASTLE_CIRCLETS =
	{
		0,
		6838,
		6835,
		6839,
		6837,
		6840,
		6834,
		6836,
	};
	// SQL
	private static final String SELECT = "SELECT id FROM castle ORDER BY id";
	private static final String DELETE_1 = "DELETE FROM items WHERE owner_id=? AND item_id=?";
	private static final String DELETE_2 = "DELETE FROM items WHERE owner_id=? AND item_id=?";
	
	public final void load()
	{
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				int id = rs.getInt("id");
				castles.put(id, new Castle(id));
			}
		}
		catch (Exception e)
		{
			LOG.info("Exception: load() CastleData: " + e);
		}
		
		UtilPrint.result("CastleData", "Loaded castles", castles.size());
	}
	
	public Collection<Castle> getCastles()
	{
		return castles.values();
	}
	
	public final int findNearestCastleId(L2Object obj)
	{
		int id = getCastleId(obj);
		if (id < 0)
		{
			double closestDistance = 99999999;
			double distance;
			
			for (Castle castle : castles.values())
			{
				if (castle == null)
				{
					continue;
				}
				
				distance = castle.getDistance(obj);
				if (closestDistance > distance)
				{
					closestDistance = distance;
					id = castle.getId();
				}
			}
		}
		return id;
	}
	
	public final Castle getCastleByOwner(Clan clan)
	{
		for (Castle castle : castles.values())
		{
			if (castle.getOwnerId() == clan.getId())
			{
				return castle;
			}
		}
		return null;
	}
	
	public final Castle getCastleByName(String name)
	{
		for (Castle castle : castles.values())
		{
			if (castle.getName().equalsIgnoreCase(name.trim()))
			{
				return castle;
			}
		}
		return null;
	}
	
	public final Castle getCastle(int x, int y, int z)
	{
		for (Castle castle : castles.values())
		{
			if (castle.checkIfInZone(x, y, z))
			{
				return castle;
			}
		}
		return null;
	}
	
	public final Castle getCastle(L2Object activeObject)
	{
		return getCastle(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public final Castle getCastleById(int castleId)
	{
		return castles.get(castleId);
	}
	
	public final int getCastleId(L2Object activeObject)
	{
		return getCastleId(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}
	
	public final int getCastleId(int x, int y, int z)
	{
		for (Castle castle : castles.values())
		{
			if ((castle != null) && castle.checkIfInZone(x, y, z))
			{
				return castle.getId();
			}
		}
		
		return -1;
	}
	
	public final void validateTaxes(CabalType newSealOwner)
	{
		int maxTax;
		switch (newSealOwner)
		{
			case DUSK:
				maxTax = 5;
				break;
			case DAWN:
				maxTax = 25;
				break;
			default: // no owner
				maxTax = 15;
				break;
		}
		
		for (Castle castle : castles.values())
		{
			if (castle.getTaxPercent() > maxTax)
			{
				castle.setTaxRateInDB(maxTax);
			}
		}
	}
	
	public int getCircletByCastleId(int castleId)
	{
		if ((castleId > 0) && (castleId < 8))
		{
			return CASTLE_CIRCLETS[castleId];
		}
		
		return 0;
	}
	
	// remove this castle's circlets from the clan
	public void removeCirclet(Clan clan, int castleId)
	{
		for (ClanMemberInstance member : clan.getMembers())
		{
			if (member == null)
			{
				return;
			}
			
			L2PcInstance player = member.getPlayerInstance();
			final int circletId = getCircletByCastleId(castleId);
			
			if (circletId != 0)
			{
				// online-player circlet removal
				if (player != null)
				{
					try
					{
						ItemInstance circlet = player.getInventory().getItemById(circletId);
						if (circlet != null)
						{
							if (circlet.isEquipped())
							{
								player.getInventory().unEquipItemInSlotAndRecord(circlet.getEquipSlot());
							}
							player.getInventory().destroyItemByItemId("CastleCircletRemoval", circletId, 1, player, true);
						}
						
						if (player.isClanLeader())
						{
							ItemInstance crown = player.getInventory().getItemById(6841);
							if (crown != null)
							{
								if (crown.isEquipped())
								{
									player.getInventory().unEquipItemInSlotAndRecord(crown.getEquipSlot());
								}
								player.getInventory().destroyItemByItemId("CastleCircletRemoval", 6841, 1, player, true);
							}
						}
						
						return;
					}
					catch (NullPointerException e)
					{
						// continue removing offline
					}
				}
				
				// else offline-player circlet removal
				try (Connection con = DatabaseManager.getConnection())
				{
					try (PreparedStatement ps = con.prepareStatement(DELETE_1))
					{
						ps.setInt(1, member.getObjectId());
						ps.setInt(2, circletId);
						ps.execute();
					}
					
					if (member.getObjectId() == clan.getLeaderId())
					{
						try (PreparedStatement ps = con.prepareStatement(DELETE_2))
						{
							ps.setInt(1, member.getObjectId());
							ps.setInt(2, 6841);
							ps.execute();
						}
					}
				}
				catch (Exception e)
				{
					LOG.warning("Failed to remove castle circlets offline for player " + member.getName());
				}
			}
		}
	}
	
	public static CastleData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CastleData INSTANCE = new CastleData();
	}
}
