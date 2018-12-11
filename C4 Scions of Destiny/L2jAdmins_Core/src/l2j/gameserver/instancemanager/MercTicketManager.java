package l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.entity.castle.siege.managers.SiegeGuardManager;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;

/**
 * @author yellowperil & Fulminus This class is similar to the SiegeGuardManager, except it handles the loading of the mercenary tickets that are dropped on castle floors by the castle lords. These tickets (aka badges) need to be readded after each server reboot except when the server crashed in the
 *         middle of an ongoig siege. In addition, this class keeps track of the added tickets, in order to properly limit the number of mercenaries in each castle and the number of mercenaries from each mercenary type. Finally, we provide auxilary functions to identify the castle in which each item
 *         (and its corresponding NPC) belong to, in order to help avoid mixing them up.
 */
public class MercTicketManager
{
	private static final Logger LOG = Logger.getLogger(MercTicketManager.class.getName());
	
	private final Set<ItemInstance> droppedTickets = new HashSet<>(); // to keep track of items on the ground
	
	// TODO move all these values into siege.properties
	// max tickets per merc type = 10 + (castleid * 2)?
	// max ticker per castle = 40 + (castleid * 20)?
	// @formatter:off
	private static final int[] MAX_MERC_PER_TYPE =
	{
		10,	10,	10,	10,	10,	10,	10,	10,	10,	10, // Gludio
		15,	15,	15,	15,	15,	15,	15,	15,	15,	15, // Dion
		10,	10,	10,	10,	10,	10,	10,	10,	10,	10, // Giran
		10,	10,	10,	10,	10,	10,	10,	10,	10,	10, // Oren
		20,	20,	20,	20,	20, 20,	20,	20,	20,	20, // Aden
		20,	20,	20,	20,	20,	20,	20,	20,	20,	20, // Heine
		20,	20,	20,	20,	20,	20,	20,	20,	20,	20  // Goddard
	};

	private static final int[] MERCS_MAX_PER_CASTLE =
	{
		50,  // Gludio
		75,  // Dion
		100, // Giran
		150, // Oren
		200, // Aden
		200, // Heine
		200  // Goddard
	};

	private static final int[] ITEM_IDS =
	{
		3960,	3961,	3962,	3963,	3964,	3965,	3966,	3967,	3968,	3969, // Gludio
		3973,	3974,	3975,	3976,	3977,	3978,	3979,	3980,	3981,	3982, // Dion
		3986,	3987,	3988,	3989,	3990,	3991,	3992,	3993,	3994,	3995, // Giran
		3999,	4000,	4001,	4002,	4003,	4004,	4005,	4006,	4007,	4008, // Oren
		4012,	4013,	4014,	4015,	4016,	4017,	4018,	4019,	4020,	4021, // Aden
		5205,	5206,	5207,	5208,	5209,	5210,	5211,	5212,	5213,	5214, // Heine
		6779,	6780,	6781,	6782,	6783,	6784,	6785,	6786,	6787,	6788  // Goddard
	};

	private static final int[] NPC_IDS =
	{
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310, // Gludio
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310, // Dion
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310, // Giran
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310, // Oren
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310, // Aden
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310, // Heine
		12301,	12302,	12303,	12304,	12305,	12306,	12307,	12308,	12309,	12310  // Goddard
	};
	// @formatter:on
	
	public MercTicketManager()
	{
		//
	}
	
	public void reload()
	{
		droppedTickets.clear();
		load();
	}
	
	public void load()
	{
		// load merc tickets into the world
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM castle_siege_guards WHERE isHired = 1");
			ResultSet rs = ps.executeQuery())
		{
			
			int npcId;
			int itemId;
			int x, y, z;
			int mercPlaced[] = new int[10];
			// start index to begin the search for the itemId corresponding to this NPC
			// this will help with:
			// a) skip unnecessary iterations in the search loop
			// b) avoid finding the wrong itemId whenever tickets of different spawn the same npc!
			int startindex = 0;
			
			while (rs.next())
			{
				npcId = rs.getInt("npcId");
				x = rs.getInt("x");
				y = rs.getInt("y");
				z = rs.getInt("z");
				
				Castle castle = CastleData.getInstance().getCastle(x, y, z);
				if (castle != null)
				{
					if (mercPlaced[castle.getId() - 1] >= MERCS_MAX_PER_CASTLE[castle.getId() - 1])
					{
						continue;
					}
					startindex = 10 * (castle.getId() - 1);
					mercPlaced[castle.getId() - 1] += 1;
					
				}
				
				// find the FIRST ticket itemId with spawns the saved NPC in the saved location
				for (int i = startindex; i < NPC_IDS.length; i++)
				{
					if (NPC_IDS[i] == npcId) // Find the index of the item used
					{
						// only handle tickets if a siege is not ongoing in this npc's castle
						
						if ((castle != null) && !(castle.getSiege().isInProgress()))
						{
							itemId = ITEM_IDS[i];
							// create the ticket in the gameworld
							ItemInstance dropticket = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
							dropticket.setDropTime(0); // avoids it from beeing removed by the auto item destroyer
							dropticket.dropMe(null, x, y, z);
							L2World.getInstance().addObject(dropticket);
							droppedTickets.add(dropticket);
						}
						break;
					}
				}
			}
			
			LOG.info("MercTicketManager: Loaded " + droppedTickets.size() + " Mercenary Tickets");
		}
		catch (Exception e)
		{
			LOG.info("MercTicketManager: Exception loadMercenaryData(): " + e);
		}
	}
	
	/**
	 * @param  itemId
	 * @return        the castleId for the passed ticket item id
	 */
	public int getTicketCastleId(int itemId)
	{
		if ((itemId >= ITEM_IDS[0]) && (itemId <= ITEM_IDS[9]))
		{
			return 1; // Gludio
		}
		if ((itemId >= ITEM_IDS[10]) && (itemId <= ITEM_IDS[19]))
		{
			return 2; // Dion
		}
		if ((itemId >= ITEM_IDS[20]) && (itemId <= ITEM_IDS[29]))
		{
			return 3; // Giran
		}
		if ((itemId >= ITEM_IDS[30]) && (itemId <= ITEM_IDS[39]))
		{
			return 4; // Oren
		}
		if ((itemId >= ITEM_IDS[40]) && (itemId <= ITEM_IDS[49]))
		{
			return 5; // Aden
		}
		if ((itemId >= ITEM_IDS[50]) && (itemId <= ITEM_IDS[59]))
		{
			return 6; // Heine
		}
		if ((itemId >= ITEM_IDS[60]) && (itemId <= ITEM_IDS[69]))
		{
			return 7; // Goddard
		}
		return -1;
	}
	
	/**
	 * Checks if the passed item has reached the limit of number of dropped tickets that this SPECIFIC item may have in its castle
	 * @param  itemId
	 * @return
	 */
	public boolean isAtTypeLimit(int itemId)
	{
		int limit = -1;
		// find the max value for this item
		for (int i = 0; i < ITEM_IDS.length; i++)
		{
			if (ITEM_IDS[i] == itemId) // Find the index of the item used
			{
				limit = MAX_MERC_PER_TYPE[i];
				break;
			}
		}
		
		if (limit <= 0)
		{
			return true;
		}
		
		int count = 0;
		
		for (ItemInstance ticket : droppedTickets)
		{
			if ((ticket != null) && (ticket.getId() == itemId))
			{
				count++;
			}
		}
		
		if (count >= limit)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the passed item belongs to a castle which has reached its limit of number of dropped tickets.
	 * @param  itemId
	 * @return
	 */
	public boolean isAtCastleLimit(int itemId)
	{
		int castleId = getTicketCastleId(itemId);
		if (castleId <= 0)
		{
			return true;
		}
		int limit = MERCS_MAX_PER_CASTLE[castleId - 1];
		if (limit <= 0)
		{
			return true;
		}
		
		int count = 0;
		
		for (ItemInstance ticket : droppedTickets)
		{
			if ((ticket != null) && (getTicketCastleId(ticket.getId()) == castleId))
			{
				count++;
			}
		}
		
		if (count >= limit)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isTooCloseToAnotherTicket(int x, int y, int z)
	{
		for (ItemInstance item : droppedTickets)
		{
			double dx = x - item.getX();
			double dy = y - item.getY();
			double dz = z - item.getZ();
			
			if (((dx * dx) + (dy * dy) + (dz * dz)) < (25 * 25))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * addTicket actions<br>
	 * 1) find the npc that needs to be saved in the mercenary spawns, given this item<br>
	 * 2) Use the passed character's location info to add the spawn<br>
	 * 3) create a copy of the item to drop in the world returns the id of the mercenary npc that was added to the spawn returns -1 if this fails.
	 * @param  itemId
	 * @param  activeChar
	 * @return
	 */
	public int addTicket(int itemId, L2PcInstance activeChar)
	{
		int x = activeChar.getX();
		int y = activeChar.getY();
		int z = activeChar.getZ();
		int heading = activeChar.getHeading();
		
		Castle castle = CastleData.getInstance().getCastle(activeChar);
		if (castle == null)
		{
			return -1;
		}
		
		// check if this item can be added here
		for (int i = 0; i < ITEM_IDS.length; i++)
		{
			if (ITEM_IDS[i] == itemId) // Find the index of the item used
			{
				spawnMercenary(NPC_IDS[i], x, y, z, 3000, 0);
				
				// Hire merc for this caslte. NpcId is at the same index as the item used.
				castle.getSiege().getGuardMngr().hireMerc(x, y, z, heading, NPC_IDS[i]);
				
				// create the ticket in the gameworld
				ItemInstance dropticket = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
				dropticket.setDropTime(0); // avoids it from beeing removed by the auto item destroyer
				dropticket.dropMe(null, x, y, z);
				
				L2World.getInstance().addObject(dropticket); // add to the world
				// and keep track of this ticket in the list
				droppedTickets.add(dropticket);
				
				return NPC_IDS[i];
			}
		}
		
		return -1;
	}
	
	private void spawnMercenary(int npcId, int x, int y, int z, int despawnDelay, int chatDelay)
	{
		NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		if (template != null)
		{
			final L2SiegeGuardInstance npc = new L2SiegeGuardInstance(IdFactory.getInstance().getNextId(), template);
			npc.setCurrentHpMp(npc.getStat().getMaxHp(), npc.getStat().getMaxMp());
			npc.spawnMe(x, y, (z + 20));
			
			if (despawnDelay > 0)
			{
				ThreadPoolManager.getInstance().schedule(() -> npc.deleteMe(), despawnDelay);
			}
		}
	}
	
	/**
	 * Delete all tickets from a castle; remove the items from the world and remove references to them from this class
	 * @param castleId
	 */
	public void deleteTickets(int castleId)
	{
		Iterator<ItemInstance> it = droppedTickets.iterator();
		while (it.hasNext())
		{
			ItemInstance item = it.next();
			
			if ((item != null) && (getTicketCastleId(item.getId()) == castleId))
			{
				item.decayMe();
				L2World.getInstance().removeObject(item);
				
				// remove from the list
				it.remove();
				// droppedTickets.remove(item);
			}
		}
	}
	
	/**
	 * remove a single ticket and its associated spawn from the world (used when the castle lord picks up a ticket, for example)
	 * @param item
	 */
	public void removeTicket(ItemInstance item)
	{
		int itemId = item.getId();
		int npcId = -1;
		
		// find the FIRST ticket itemId with spawns the saved NPC in the saved location
		for (int i = 0; i < ITEM_IDS.length; i++)
		{
			if (ITEM_IDS[i] == itemId) // Find the index of the item used
			{
				npcId = NPC_IDS[i];
				break;
			}
		}
		
		// find the castle where this item is
		Castle castle = CastleData.getInstance().getCastleById(getTicketCastleId(itemId));
		if ((npcId > 0) && (castle != null))
		{
			(new SiegeGuardManager(castle)).removeMerc(npcId, item.getX(), item.getY(), item.getZ());
		}
		
		droppedTickets.remove(item);
	}
	
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
	
	public static MercTicketManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MercTicketManager INSTANCE = new MercTicketManager();
	}
}
