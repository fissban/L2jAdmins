package l2j.gameserver.instancemanager;

import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.DimensionalRiftData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.entity.DimensionalRift;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.util.Rnd;

/**
 * @author L2Fortress, balancer.ru, kombat, zarie
 */
public class DimensionalRiftManager
{
	private static final int DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;
	
	protected DimensionalRiftManager()
	{
		//
	}
	
	public synchronized void start(L2PcInstance player, byte type, L2Npc npc)
	{
		final Party party = player.getParty();
		
		// No party.
		if (party == null)
		{
			showHtmlFile(player, "data/html/sevenSigns/rift/NoParty.htm", npc);
			return;
		}
		
		// Player isn't the party leader.
		if (!party.isLeader(player))
		{
			showHtmlFile(player, "data/html/sevenSigns/rift/NotPartyLeader.htm", npc);
			return;
		}
		
		// Party is already in rift.
		if (party.isInDimensionalRift())
		{
			return;
		}
		
		// Party members' count is lower than config.
		if (party.getMemberCount() < Config.RIFT_MIN_PARTY_SIZE)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/sevenSigns/rift/SmallParty.htm");
			html.replace("%npc_name%", npc.getName());
			html.replace("%count%", Integer.toString(Config.RIFT_MIN_PARTY_SIZE));
			player.sendPacket(html);
			return;
		}
		
		// Rift is full.
		if (!isAllowedEnter(type))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile("data/html/sevenSigns/rift/Full.htm");
			html.replace("%npc_name%", npc.getName());
			player.sendPacket(html);
			return;
		}
		
		// One of teammates isn't on peace zone or hasn't required amount of items.
		for (L2PcInstance p : party.getMembers())
		{
			if (!DimensionalRiftData.getInstance().checkIfInPeaceZone(p.getX(), p.getY(), p.getZ()))
			{
				showHtmlFile(player, "data/html/sevenSigns/rift/NotInWaitingRoom.htm", npc);
				return;
			}
		}
		
		ItemInstance i;
		final int count = getNeededItems(type);
		
		for (L2PcInstance p : party.getMembers())
		{
			i = p.getInventory().getItemById(DIMENSIONAL_FRAGMENT_ITEM_ID);
			
			if ((i == null) || (i.getCount() < getNeededItems(type)))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile("data/html/sevenSigns/rift/NoFragments.htm");
				html.replace("%npc_name%", npc.getName());
				html.replace("%count%", Integer.toString(count));
				player.sendPacket(html);
				return;
			}
		}
		
		for (L2PcInstance p : party.getMembers())
		{
			if (!p.getInventory().destroyItemByItemId("RiftEntrance", DIMENSIONAL_FRAGMENT_ITEM_ID, count, null, true))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile("data/html/sevenSigns/rift/NoFragments.htm");
				html.replace("%npc_name%", npc.getName());
				html.replace("%count%", Integer.toString(count));
				player.sendPacket(html);
				return;
			}
		}
		
		byte room;
		List<Byte> emptyRooms;
		do
		{
			emptyRooms = getFreeRooms(type);
			room = emptyRooms.get(Rnd.get(1, emptyRooms.size()) - 1);
			
			// Relaunch random number until another room than room boss popups.
			while (room == 9)
			{
				room = emptyRooms.get(Rnd.get(1, emptyRooms.size()) - 1);
			}
		}
		// Find empty room
		
		while (DimensionalRiftData.getInstance().getRoom(type, room).isPartyInside());
		
		// Creates an instance of the rift.
		new DimensionalRift(party, type, room);
	}
	
	public void killRift(DimensionalRift d)
	{
		if (d.getTeleportTimerTask() != null)
		{
			d.getTeleportTimerTask().cancel();
		}
		d.setTeleportTimerTask(null);
		
		if (d.getTeleportTimer() != null)
		{
			d.getTeleportTimer().cancel();
		}
		d.setTeleportTimer(null);
		
		if (d.getSpawnTimerTask() != null)
		{
			d.getSpawnTimerTask().cancel();
		}
		d.setSpawnTimerTask(null);
		
		if (d.getSpawnTimer() != null)
		{
			d.getSpawnTimer().cancel();
		}
		d.setSpawnTimer(null);
	}
	
	public static class DimensionalRiftRoomHolder
	{
		protected final byte type;
		protected final byte room;
		private final int xMin;
		private final int xMax;
		private final int yMin;
		private final int yMax;
		private final LocationHolder teleportCoords;
		private final Shape s;
		private final boolean isBossRoom;
		private final List<Spawn> roomSpawns;
		protected final List<L2Npc> roomMobs;
		private boolean partyInside = false;
		
		public DimensionalRiftRoomHolder(byte type, byte room, int xMin, int xMax, int yMin, int yMax, int xT, int yT)
		{
			this.type = type;
			this.room = room;
			this.xMin = (xMin + 128);
			this.xMax = (xMax - 128);
			this.yMin = (yMin + 128);
			this.yMax = (yMax - 128);
			
			teleportCoords = new LocationHolder(xT, yT, -6752);
			
			isBossRoom = (room == 9);
			roomSpawns = new ArrayList<>();
			roomMobs = new ArrayList<>();
			
			s = new Polygon(new int[]
			{
				xMin,
				xMax,
				xMax,
				xMin
			}, new int[]
			{
				yMin,
				yMin,
				yMax,
				yMax
			}, 4);
		}
		
		public int getRandomX()
		{
			return Rnd.get(xMin, xMax);
		}
		
		public int getRandomY()
		{
			return Rnd.get(yMin, yMax);
		}
		
		public LocationHolder getTeleportCoords()
		{
			return teleportCoords;
		}
		
		public boolean checkIfInZone(int x, int y, int z)
		{
			return s.contains(x, y) && (z >= -6816) && (z <= -6240);
		}
		
		public boolean isBossRoom()
		{
			return isBossRoom;
		}
		
		public List<Spawn> getSpawns()
		{
			return roomSpawns;
		}
		
		public void spawn()
		{
			for (Spawn spawn : roomSpawns)
			{
				spawn.doSpawn();
				spawn.startRespawn();
			}
		}
		
		public DimensionalRiftRoomHolder unspawn()
		{
			for (Spawn spawn : roomSpawns)
			{
				spawn.stopRespawn();
				if (spawn.getLastSpawn() != null)
				{
					spawn.getLastSpawn().deleteMe();
				}
			}
			return this;
		}
		
		/**
		 * @return the partyInside
		 */
		public boolean isPartyInside()
		{
			return partyInside;
		}
		
		public void setPartyInside(boolean partyInside)
		{
			this.partyInside = partyInside;
		}
	}
	
	private static int getNeededItems(byte type)
	{
		switch (type)
		{
			case 1:
				return Config.RIFT_ENTER_COST_RECRUIT;
			case 2:
				return Config.RIFT_ENTER_COST_SOLDIER;
			case 3:
				return Config.RIFT_ENTER_COST_OFFICER;
			case 4:
				return Config.RIFT_ENTER_COST_CAPTAIN;
			case 5:
				return Config.RIFT_ENTER_COST_COMMANDER;
			case 6:
				return Config.RIFT_ENTER_COST_HERO;
			default:
				throw new IndexOutOfBoundsException();
		}
	}
	
	public void showHtmlFile(L2PcInstance player, String file, L2Npc npc)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(file);
		html.replace("%npc_name%", npc.getName());
		player.sendPacket(html);
	}
	
	public boolean isAllowedEnter(byte type)
	{
		int count = 0;
		for (DimensionalRiftRoomHolder room : DimensionalRiftData.getInstance().getAllRoomsType(type).values())
		{
			if (room.isPartyInside())
			{
				count++;
			}
		}
		return count < (DimensionalRiftData.getInstance().getAllRoomsType(type).size() - 1);
	}
	
	public List<Byte> getFreeRooms(byte type)
	{
		List<Byte> list = new ArrayList<>();
		for (DimensionalRiftRoomHolder room : DimensionalRiftData.getInstance().getAllRoomsType(type).values())
		{
			if (!room.isPartyInside())
			{
				list.add(room.room);
			}
		}
		return list;
	}
	
	public static DimensionalRiftManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DimensionalRiftManager INSTANCE = new DimensionalRiftManager();
	}
}
