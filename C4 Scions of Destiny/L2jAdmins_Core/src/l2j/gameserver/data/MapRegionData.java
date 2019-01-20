package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.instancemanager.zone.ZoneArenaManager;
import l2j.gameserver.instancemanager.zone.ZoneTownManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.model.zone.type.ArenaZone;
import l2j.gameserver.model.zone.type.ClanHallZone;
import l2j.util.UtilPrint;

/**
 * This class ...
 */
public class MapRegionData
{
	private static final Logger LOG = Logger.getLogger(MapRegionData.class.getName());
	
	private static MapRegionData INSTANCE;
	
	private final int[][] regions = new int[19][21];
	
	public static enum TeleportWhereType
	{
		CASTLE,
		CLAN_HALL,
		SIEGE_FLAG,
		TOWN,
	}
	
	public static MapRegionData getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new MapRegionData();
		}
		return INSTANCE;
	}
	
	private MapRegionData()
	{
		int count = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT region, sec0, sec1, sec2, sec3, sec4, sec5, sec6, sec7, sec8, sec9, sec10 FROM mapregion");
			ResultSet rs = ps.executeQuery())
		{
			int region;
			while (rs.next())
			{
				region = rs.getInt(1);
				
				for (int j = 0; j < 11; j++)
				{
					regions[j][region] = rs.getInt(j + 2);
					count++;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("error while creating map region data: " + e);
		}
		
		UtilPrint.result("MapRegionData", "Loaded maps region", count);
	}
	
	public final int getMapRegion(int posX, int posY)
	{
		return regions[getMapRegionX(posX)][getMapRegionY(posY)];
	}
	
	public final int getMapRegionX(int posX)
	{
		return (posX >> 15) + 4;// + centerTileX;
	}
	
	public final int getMapRegionY(int posY)
	{
		return (posY >> 15) + 10;// + centerTileX;
	}
	
	public int getAreaCastle(L2Character activeChar)
	{
		int area = getClosestTownNumber(activeChar);
		
		switch (area)
		{
			case 0:
				return 1;
			case 1:
				return 4;
			case 2:
				return 4;
			case 5:
				return 1;
			case 6:
				return 1;
			case 7:
				return 2;
			case 8:
				return 3;
			case 9:
				return 4;
			case 10:
				return 5;
			case 11:
				return 5;
			case 12:
				return 3;
			case 13:
				return 6;
			case 15:
				return 7;
			case 16:
				return 2;
			default:
				return 5;
		}
	}
	
	public int getClosestTownNumber(L2Character activeChar)
	{
		return getMapRegion(activeChar.getX(), activeChar.getY());
	}
	
	public String getClosestTownName(L2Character activeChar)
	{
		return getClosestTownName(activeChar.getX(), activeChar.getY());
	}
	
	public String getClosestTownName(int x, int y)
	{
		int nearestTownId = getMapRegion(x, y);
		
		switch (nearestTownId)
		{
			case 0:
				return "Talking Island Village";
			case 1:
				return "Elven Village";
			case 2:
				return "Dark Elven Village";
			case 3:
				return "Orc Village";
			case 4:
				return "Dwarven Village";
			case 5:
				return "Gludio Castle Town";
			case 6:
				return "Gludin Village";
			case 7:
				return "Dion Castle Town";
			case 8:
				return "Giran Castle Town";
			case 9:
				return "Oren Castle Town";
			case 10:
				return "Aden Castle Town";
			case 11:
				return "Hunters Village";
			case 12:
				return "Giran Harbor";
			case 13:
				return "Innadril Castle Town";
			case 14:
				return "Rune Castle Town";
			case 15:
				return "Goddard Castle Town";
			case 16:
				return "Floran Village";
			default:
				return "Aden Castle Town";
		}
	}
	
	public LocationHolder getTeleToLocation(L2Character activeChar, TeleportWhereType teleportWhere)
	{
		// int[] coord;
		
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = ((L2PcInstance) activeChar);
			
			// If in Monster Derby Track
			if (player.isInsideZone(ZoneType.MONSTERTRACK))
			{
				return new LocationHolder(12661, 181687, -3560);
			}
			
			Castle castle = null;
			ClanHall clanhall = null;
			
			if (player.getClan() != null)
			{
				// If teleport to clan hall
				if (teleportWhere == TeleportWhereType.CLAN_HALL)
				{
					clanhall = ClanHallData.getClanHallByOwner(player.getClan());
					if (clanhall != null)
					{
						ClanHallZone zone = clanhall.getZone();
						if (zone != null)
						{
							return zone.getSpawnLoc();
						}
						
					}
				}
				
				if (teleportWhere == TeleportWhereType.CASTLE)
				{
					castle = CastleData.getInstance().getCastleByOwner(player.getClan());
				}
				
				if (castle == null)
				{
					castle = CastleData.getInstance().getCastle(player);
				}
				
				if ((castle != null) && (castle.getId() > 0))
				{
					// If teleport to castle
					if ((teleportWhere == TeleportWhereType.CASTLE) || ((teleportWhere == TeleportWhereType.CASTLE) && castle.getSiege().isInProgress() && (castle.getSiege().isDefender(player.getClan()))))
					{
						return castle.getZone().getSpawnLoc();
					}
				}
				
				if (teleportWhere == TeleportWhereType.SIEGE_FLAG)
				{
					Siege siege = SiegeManager.getInstance().getSiege(player);
					if (siege != null)
					{
						// Check if player's clan is attacker
						List<L2SiegeFlagInstance> flags = siege.getFlags(player.getClan());
						if ((flags != null) && !flags.isEmpty())
						{
							// TODO Spawn to flag - Need more work to get player to the nearest flag
							L2Npc flag = flags.get(0);
							return new LocationHolder(flag.getX(), flag.getY(), flag.getZ());
						}
					}
				}
			}
			
			// Karma player land out of city
			if (player.getKarma() > 0)
			{
				try
				{
					return ZoneTownManager.getClosestZone(activeChar).getChaoticSpawnLoc();
				}
				catch (Exception e)
				{
					return new LocationHolder(17817, 170079, -3530);
				}
			}
			
			// Checking if in arena
			ArenaZone arena = ZoneArenaManager.isInsideInZone(player);
			
			if (arena != null)
			{
				return arena.getSpawnLoc();
			}
		}
		
		// Get the nearest town
		try
		{
			return ZoneTownManager.getClosestZone(activeChar).getSpawnLoc();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			// port to the Talking Island if no closest town found
			return new LocationHolder(-84176, 243382, -3126);
		}
	}
}
