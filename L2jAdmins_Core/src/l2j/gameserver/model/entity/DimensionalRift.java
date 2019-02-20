package l2j.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.DimensionalRiftData;
import l2j.gameserver.instancemanager.DimensionalRiftManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.network.external.server.Earthquake;
import l2j.util.Rnd;

/**
 * Thanks to L2Fortress and balancer.ru - kombat
 */
public class DimensionalRift
{
	private static final long SECONDS_5 = 5000L;
	
	protected byte type;
	protected Party party;
	protected List<Byte> completedRooms = new ArrayList<>();
	protected byte currentJumps = 0;
	
	private Timer teleporterTimer;
	private TimerTask teleporterTimerTask;
	private Timer spawnTimer;
	private TimerTask spawnTimerTask;
	
	private Future<?> earthQuakeTask;
	
	protected byte choosenRoom = -1;
	private boolean hasJumped = false;
	protected Set<L2PcInstance> deadPlayers = ConcurrentHashMap.newKeySet();
	protected Set<L2PcInstance> revivedInWaitingRoom = ConcurrentHashMap.newKeySet();
	
	private boolean isBossRoom = false;
	
	public DimensionalRift(Party party, byte type, byte room)
	{
		DimensionalRiftData.getInstance().getRoom(type, room).setPartyInside(true);
		
		this.type = type;
		this.party = party;
		choosenRoom = room;
		
		party.setDimensionalRift(this);
		
		LocationHolder coords = getRoomCoord(room);
		for (L2PcInstance p : party.getMembers())
		{
			p.teleToLocation(coords, false);
		}
		
		createSpawnTimer(choosenRoom);
		createTeleporterTimer(true);
	}
	
	public byte getType()
	{
		return type;
	}
	
	public byte getCurrentRoom()
	{
		return choosenRoom;
	}
	
	protected void createTeleporterTimer(final boolean reasonTP)
	{
		if (teleporterTimerTask != null)
		{
			teleporterTimerTask.cancel();
			teleporterTimerTask = null;
		}
		
		if (teleporterTimer != null)
		{
			teleporterTimer.cancel();
			teleporterTimer = null;
		}
		
		if (earthQuakeTask != null)
		{
			earthQuakeTask.cancel(false);
			earthQuakeTask = null;
		}
		
		teleporterTimer = new Timer();
		teleporterTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				if (choosenRoom > -1)
				{
					DimensionalRiftData.getInstance().getRoom(type, choosenRoom).unspawn().setPartyInside(false);
				}
				
				if (reasonTP && (currentJumps < getMaxJumps()) && (party.getMemberCount() > deadPlayers.size()))
				{
					currentJumps++;
					
					completedRooms.add(choosenRoom);
					choosenRoom = -1;
					
					for (L2PcInstance p : party.getMembers())
					{
						if (!revivedInWaitingRoom.contains(p))
						{
							teleportToNextRoom(p, false);
						}
					}
					
					createTeleporterTimer(true);
					createSpawnTimer(choosenRoom);
				}
				else
				{
					for (L2PcInstance p : party.getMembers())
					{
						if (!revivedInWaitingRoom.contains(p))
						{
							teleportToWaitingRoom(p);
						}
					}
					
					killRift();
					cancel();
				}
			}
		};
		
		if (reasonTP)
		{
			long jumpTime = calcTimeToNextJump();
			teleporterTimer.schedule(teleporterTimerTask, jumpTime); // Teleporter task, 8-10 minutes
			
			earthQuakeTask = ThreadPoolManager.schedule(() ->
			{
				for (L2PcInstance p : party.getMembers())
				{
					if (!revivedInWaitingRoom.contains(p))
					{
						p.sendPacket(new Earthquake(p.getX(), p.getY(), p.getZ(), 65, 9));
					}
				}
			}, jumpTime - 7000);
		}
		else
		{
			teleporterTimer.schedule(teleporterTimerTask, SECONDS_5); // incorrect party member invited.
		}
	}
	
	public void createSpawnTimer(final byte room)
	{
		if (spawnTimerTask != null)
		{
			spawnTimerTask.cancel();
			spawnTimerTask = null;
		}
		
		if (spawnTimer != null)
		{
			spawnTimer.cancel();
			spawnTimer = null;
		}
		
		spawnTimer = new Timer();
		spawnTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				DimensionalRiftData.getInstance().getRoom(type, room).spawn();
			}
		};
		
		spawnTimer.schedule(spawnTimerTask, Config.RIFT_SPAWN_DELAY);
	}
	
	public void partyMemberInvited()
	{
		createTeleporterTimer(false);
	}
	
	public void partyMemberExited(L2PcInstance player)
	{
		if (deadPlayers.contains(player))
		{
			deadPlayers.remove(player);
		}
		
		if (revivedInWaitingRoom.contains(player))
		{
			revivedInWaitingRoom.remove(player);
		}
		
		if ((party.getMemberCount() < Config.RIFT_MIN_PARTY_SIZE) || (party.getMemberCount() == 1))
		{
			for (L2PcInstance p : party.getMembers())
			{
				teleportToWaitingRoom(p);
			}
			
			killRift();
		}
	}
	
	public void manualTeleport(L2PcInstance player, L2Npc npc)
	{
		final Party party = player.getParty();
		if ((party == null) || !party.isInDimensionalRift())
		{
			return;
		}
		
		if (!party.isLeader(player))
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/sevenSigns/rift/NotPartyLeader.htm", npc);
			return;
		}
		
		if (currentJumps == Config.RIFT_MAX_JUMPS)
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/sevenSigns/rift/UsedAllJumps.htm", npc);
			return;
		}
		
		if (hasJumped)
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/sevenSigns/rift/AlreadyTeleported.htm", npc);
			return;
		}
		hasJumped = true;
		
		DimensionalRiftData.getInstance().getRoom(type, choosenRoom).unspawn().setPartyInside(false);
		completedRooms.add(choosenRoom);
		choosenRoom = -1;
		
		for (L2PcInstance p : party.getMembers())
		{
			teleportToNextRoom(p, true);
		}
		
		DimensionalRiftData.getInstance().getRoom(type, choosenRoom).setPartyInside(true);
		
		createSpawnTimer(choosenRoom);
		createTeleporterTimer(true);
	}
	
	public void manualExitRift(L2PcInstance player, L2Npc npc)
	{
		if (!player.isInParty() || !player.getParty().isInDimensionalRift())
		{
			return;
		}
		
		if (player.getObjectId() != player.getParty().getLeader().getObjectId())
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/sevenSigns/rift/NotPartyLeader.htm", npc);
			return;
		}
		
		for (L2PcInstance p : player.getParty().getMembers())
		{
			teleportToWaitingRoom(p);
		}
		
		killRift();
	}
	
	/**
	 * This method allows to jump from one room to another. It calculates the next roomId.
	 * @param player             to teleport
	 * @param cantJumpToBossRoom if true, Anakazel room can't be choosen (case of manual teleport).
	 */
	protected void teleportToNextRoom(L2PcInstance player, boolean cantJumpToBossRoom)
	{
		if (choosenRoom == -1)
		{
			List<Byte> emptyRooms;
			
			do
			{
				emptyRooms = DimensionalRiftManager.getInstance().getFreeRooms(type);
				
				// Do not tp in the same room a second time
				emptyRooms.removeAll(completedRooms);
				
				// If no room left, find any empty
				if (emptyRooms.isEmpty())
				{
					emptyRooms = DimensionalRiftManager.getInstance().getFreeRooms(type);
				}
				
				// Pickup a random room
				choosenRoom = emptyRooms.get(Rnd.get(1, emptyRooms.size()) - 1);
				
				// This code handles Anakazel's room special behavior.
				if (cantJumpToBossRoom)
				{
					while (choosenRoom == 9)
					{
						choosenRoom = emptyRooms.get(Rnd.get(1, emptyRooms.size()) - 1);
					}
				}
			}
			while (DimensionalRiftData.getInstance().getRoom(type, choosenRoom).isPartyInside());
		}
		
		DimensionalRiftData.getInstance().getRoom(type, choosenRoom).setPartyInside(true);
		LocationHolder coords = getRoomCoord(choosenRoom);
		player.teleToLocation(coords, false);
	}
	
	protected void teleportToWaitingRoom(L2PcInstance player)
	{
		DimensionalRiftData.getInstance().teleportToWaitingRoom(player);
	}
	
	public void killRift()
	{
		completedRooms = null;
		
		if (party != null)
		{
			party.setDimensionalRift(null);
		}
		
		party = null;
		revivedInWaitingRoom = null;
		deadPlayers = null;
		
		if (earthQuakeTask != null)
		{
			earthQuakeTask.cancel(false);
			earthQuakeTask = null;
		}
		
		DimensionalRiftData.getInstance().getRoom(type, choosenRoom).unspawn().setPartyInside(false);
		DimensionalRiftManager.getInstance().killRift(this);
	}
	
	public Timer getTeleportTimer()
	{
		return teleporterTimer;
	}
	
	public TimerTask getTeleportTimerTask()
	{
		return teleporterTimerTask;
	}
	
	public Timer getSpawnTimer()
	{
		return spawnTimer;
	}
	
	public TimerTask getSpawnTimerTask()
	{
		return spawnTimerTask;
	}
	
	public void setTeleportTimer(Timer t)
	{
		teleporterTimer = t;
	}
	
	public void setTeleportTimerTask(TimerTask tt)
	{
		teleporterTimerTask = tt;
	}
	
	public void setSpawnTimer(Timer t)
	{
		spawnTimer = t;
	}
	
	public void setSpawnTimerTask(TimerTask st)
	{
		spawnTimerTask = st;
	}
	
	private long calcTimeToNextJump()
	{
		int time = Rnd.get(Config.RIFT_AUTO_JUMPS_TIME_MIN, Config.RIFT_AUTO_JUMPS_TIME_MAX) * 1000;
		
		if (isBossRoom)
		{
			return (long) (time * Config.RIFT_BOSS_ROOM_TIME_MUTIPLY);
		}
		
		return time;
	}
	
	public void memberDead(L2PcInstance player)
	{
		if (!deadPlayers.contains(player))
		{
			deadPlayers.add(player);
		}
	}
	
	public void memberRessurected(L2PcInstance player)
	{
		if (deadPlayers.contains(player))
		{
			deadPlayers.remove(player);
		}
	}
	
	public void usedTeleport(L2PcInstance player)
	{
		if (!revivedInWaitingRoom.contains(player))
		{
			revivedInWaitingRoom.add(player);
		}
		
		if (!deadPlayers.contains(player))
		{
			deadPlayers.add(player);
		}
		
		if ((party.getMemberCount() - revivedInWaitingRoom.size()) < Config.RIFT_MIN_PARTY_SIZE)
		{
			for (L2PcInstance p : party.getMembers())
			{
				if ((p != null) && !revivedInWaitingRoom.contains(p))
				{
					teleportToWaitingRoom(p);
				}
			}
			
			killRift();
		}
	}
	
	public void checkBossRoom(byte room)
	{
		isBossRoom = DimensionalRiftData.getInstance().getRoom(type, room).isBossRoom();
	}
	
	public LocationHolder getRoomCoord(byte room)
	{
		return DimensionalRiftData.getInstance().getRoom(type, room).getTeleportCoords();
	}
	
	public byte getMaxJumps()
	{
		if ((Config.RIFT_MAX_JUMPS <= 8) && (Config.RIFT_MAX_JUMPS >= 1))
		{
			return (byte) Config.RIFT_MAX_JUMPS;
		}
		
		return 4;
	}
}
