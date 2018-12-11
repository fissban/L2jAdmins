package l2j.gameserver.model.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.spawn.Spawn;

/**
 * AutoSpawnInstance Class <BR>
 * Stores information about a registered auto spawn.
 * @author Tempy, fissban
 */
public class AutoSpawnHolder
{
	private int objectId;
	private int npcId;
	private int initDelay;
	private int respawnDelay;
	private int despawnDelay;
	private int lastLocIndex = -1;
	
	private final List<L2Npc> npcList = new CopyOnWriteArrayList<>();
	private List<LocationHolder> locList = new ArrayList<>();
	
	private boolean randomSpawn = false;
	private boolean broadcastAnnouncement = false;
	
	public AutoSpawnHolder(int objectId, int npcId, int initDelay, int respawnDelay, int despawnDelay, boolean broadcastAnnouncement, List<LocationHolder> locList)
	{
		this.objectId = objectId;
		this.npcId = npcId;
		this.initDelay = initDelay;
		this.respawnDelay = respawnDelay;
		this.despawnDelay = despawnDelay;
		this.broadcastAnnouncement = broadcastAnnouncement;
		this.locList = locList;
		
		if (locList.size() > 1)
		{
			randomSpawn = true;
		}
	}
	
	public boolean addNpcInstance(L2Npc npcInst)
	{
		return npcList.add(npcInst);
	}
	
	public boolean removeNpcInstance(L2Npc npcInst)
	{
		return npcList.remove(npcInst);
	}
	
	public int getObjectId()
	{
		return objectId;
	}
	
	public int getInitialDelay()
	{
		return initDelay;
	}
	
	public int getRespawnDelay()
	{
		return respawnDelay;
	}
	
	public int getDespawnDelay()
	{
		return despawnDelay;
	}
	
	public int getNpcId()
	{
		return npcId;
	}
	
	public List<LocationHolder> getLocationList()
	{
		return locList;
	}
	
	public List<L2Npc> getNpcInstanceList()
	{
		return npcList;
	}
	
	public List<Spawn> getSpawns()
	{
		List<Spawn> npcSpawns = new ArrayList<>();
		
		for (L2Npc npcInst : npcList)
		{
			npcSpawns.add(npcInst.getSpawn());
		}
		
		return npcSpawns;
	}
	
	public boolean isRandomSpawn()
	{
		return randomSpawn;
	}
	
	public boolean isBroadcasting()
	{
		return broadcastAnnouncement;
	}
	
	public void setBroadcast(boolean broadcastAnnouncement)
	{
		this.broadcastAnnouncement = broadcastAnnouncement;
	}
	
	public int getLastLocIndex()
	{
		return lastLocIndex;
	}
	
	public void setLastLocIndex(int loc)
	{
		lastLocIndex = loc;
	}
}
