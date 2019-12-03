package l2j.gameserver.instancemanager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2MinionInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.holder.MinionHolder;
import l2j.util.Rnd;
import main.data.memory.ObjectData;
import main.holders.objects.ObjectHolder;

/**
 * This class ...
 */
public class MinionsListManager
{
	/** List containing the current spawned minions for this L2MonsterInstance */
	private final List<L2MinionInstance> minions = new CopyOnWriteArrayList<>();
	protected Map<Long, Integer> respawnTasks = new ConcurrentHashMap<>();
	private final L2MonsterInstance master;
	
	public MinionsListManager(L2MonsterInstance pMaster)
	{
		master = pMaster;
	}
	
	public int countSpawnedMinions()
	{
		return minions.size();
	}
	
	private int countSpawnedMinionsById(int minionId)
	{
		int count = 0;
		for (L2MinionInstance minion : minions)
		{
			if (minion.getId() == minionId)
			{
				count++;
			}
		}
		
		return count;
	}
	
	public List<L2MinionInstance> getMinions()
	{
		return minions;
	}
	
	public void addSpawnedMinion(L2MinionInstance minion)
	{
		minions.add(minion);
	}
	
	/**
	 * Used in GameStatusThread
	 * @return
	 */
	public int lazyCountSpawnedMinionsGroups()
	{
		Set<Integer> seenGroups = new HashSet<>();
		for (L2MinionInstance minion : minions)
		{
			seenGroups.add(minion.getId());
		}
		return seenGroups.size();
	}
	
	public void removeSpawnedMinion(L2MinionInstance minion)
	{
		synchronized (minions)
		{
			minions.remove(minion);
		}
	}
	
	public void moveMinionToRespawnList(L2MinionInstance minion)
	{
		Long current = System.currentTimeMillis();
		
		minions.remove(minion);
		if (respawnTasks.get(current) == null)
		{
			respawnTasks.put(current, minion.getId());
		}
		else
		{
			// nice AoE
			for (int i = 1; i < 30; i++)
			{
				if (respawnTasks.get(current + i) == null)
				{
					respawnTasks.put(current + i, minion.getId());
					break;
				}
			}
		}
	}
	
	public void clearRespawnList()
	{
		respawnTasks.clear();
	}
	
	/**
	 * Manage respawning of minions for this RaidBoss.<br>
	 */
	public void maintainSpawnMinions()
	{
		if ((master == null) || master.isAlikeDead())
		{
			return;
		}
		Long current = System.currentTimeMillis();
		if (respawnTasks != null)
		{
			for (long deathTime : respawnTasks.keySet())
			{
				double delay = Config.RAID_MINION_RESPAWN_TIMER;
				if ((current - deathTime) > delay)
				{
					spawnSingleMinion(respawnTasks.get(deathTime));
					respawnTasks.remove(deathTime);
				}
			}
		}
	}
	
	/**
	 * Manage the spawn of all Minions of this RaidBoss.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Get the Minion data of all Minions that must be spawn
	 * <li>For each Minion type, spawn the amount of Minion needed
	 */
	public void spawnMinions()
	{
		if ((master == null) || master.isAlikeDead())
		{
			return;
		}
		
		for (MinionHolder minion : master.getTemplate().getMinions())
		{
			int minionCount = minion.getAmount();
			int minionId = minion.getId();
			int minionsToSpawn = minionCount - countSpawnedMinionsById(minionId);
			
			for (int i = 0; i < minionsToSpawn; i++)
			{
				spawnSingleMinion(minionId);
			}
		}
	}
	
	/**
	 * Init a Minion and add it in the world as a visible object.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Get the template of the Minion to spawn
	 * <li>Create and Init the Minion and generate its Identifier
	 * <li>Set the Minion HP, MP and Heading
	 * <li>Set the Minion leader to this RaidBoss
	 * <li>Init the position of the Minion and add it in the world as a visible object
	 * @param minionid The L2NpcTemplate Identifier of the Minion to spawn
	 */
	private void spawnSingleMinion(int minionid)
	{
		// Get the template of the Minion to spawn
		NpcTemplate minionTemplate = NpcData.getInstance().getTemplate(minionid);
		
		// Create and Init the Minion and generate its Identifier
		L2MinionInstance minion = new L2MinionInstance(IdFactory.getInstance().getNextId(), minionTemplate);
		
		// Set the Minion HP, MP and Heading
		minion.setCurrentHpMp(minion.getStat().getMaxHp(), minion.getStat().getMaxMp());
		minion.setHeading(master.getHeading());
		
		// Set the Minion leader to this RaidBoss
		minion.setLeader(master);
		
		ObjectHolder leader = ObjectData.get(ObjectHolder.class, master);
		if ((leader != null) && (leader.getWorldId() != 0))
		{
			ObjectData.get(ObjectHolder.class, minion).setWorldId(leader.getWorldId());
		}
		
		// Init the position of the Minion and add it in the world as a visible object
		int randSpawnLim = 170;
		// randomize x +/-
		int newX = master.getX() + Rnd.get(-randSpawnLim, randSpawnLim);
		// randomize y +/-
		int newY = master.getY() + Rnd.get(-randSpawnLim, randSpawnLim);
		
		minions.add(minion);
		
		minion.spawnMe(newX, newY, master.getZ());
	}
	
	public void deleteAllSpawnedMinions()
	{
		if (minions.isEmpty())
		{
			return;
		}
		
		for (L2MinionInstance minion : minions)
		{
			minion.setLeader(null);
			minion.abortAttack();
			minion.abortCast();
			minion.deleteMe();
		}
		
		minions.clear();
		respawnTasks.clear();
	}
	
	public void callToAssist(L2Character attacker)
	{
		for (L2MinionInstance minion : minions)
		{
			// Trigger the aggro condition of the minion
			if ((minion != null) && (attacker != null) && !minion.isDead())
			{
				if (master instanceof L2RaidBossInstance)
				{
					minion.addDamageHate(attacker, 0, 100);
				}
				else
				{
					minion.addDamageHate(attacker, 0, 1);
				}
			}
		}
	}
}
