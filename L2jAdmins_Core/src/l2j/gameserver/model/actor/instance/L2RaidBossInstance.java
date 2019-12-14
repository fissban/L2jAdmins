package l2j.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * This class manages all Raid Bosses.<br>
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public final class L2RaidBossInstance extends L2MonsterInstance
{
	private static final int RAIDBOSS_MAINTENANCE_INTERVAL = 30000; // 30 sec
	
	private RaidBossSpawnData.StatusEnum raidStatus;
	
	private ScheduledFuture<?> minionsSpawnTask = null;
	
	/**
	 * Constructor of L2RaidBossInstance (use L2Character and L2NpcInstance constructor).<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Call the L2Character constructor to set the template of the L2RaidBossInstance (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)
	 * <li>Set the name of the L2RaidBossInstance
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it
	 * @param objectId Identifier of the object to initialized
	 * @param template
	 */
	public L2RaidBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2RaidBossInstance);
	}
	
	@Override
	public boolean isRaid()
	{
		return true;
	}
	
	@Override
	protected int getMinionsSpawnMaintenanceInterval()
	{
		return RAIDBOSS_MAINTENANCE_INTERVAL;
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (killer instanceof L2Playable)
		{
			broadcastPacket(new SystemMessage(SystemMessage.RAID_WAS_SUCCESSFUL));
			broadcastPacket(new PlaySound(PlaySoundType.SYS_MSG_1209));
		}
		
		cancelRespawnsMinions();
		if (minionList != null)
		{
			minionList.deleteAllSpawnedMinions();
		}
		RaidBossSpawnData.getInstance().updateStatus(this, true);
		
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (hasMinions())
		{
			cancelRespawnsMinions();
		}
		
		super.deleteMe();
	}
	
	/**
	 * Spawn all minions at a regular interval Also if boss is too far from home location at the time of this check, teleport it home
	 */
	@Override
	protected void startMinionsSpawnMaintenanceTask()
	{
		if (minionList != null)
		{
			minionList.spawnMinions();
		}
		
		minionsSpawnTask = ThreadPoolManager.scheduleAtFixedRate(() ->
		{
			// teleport raid boss home if it's too far from home location
			Spawn bossSpawn = getSpawn();
			if (!isInsideRadius(bossSpawn.getX(), bossSpawn.getY(), bossSpawn.getZ(), 5000, true, false))
			{
				teleToLocation(bossSpawn.getX(), bossSpawn.getY(), bossSpawn.getZ(), true);
				// Heal - prevents minor exploiting with it
				setCurrentHp(getStat().getMaxHp());
				setCurrentMp(getStat().getMaxMp());
			}
			
			if (minionList != null)
			{
				minionList.maintainSpawnMinions();
			}
		}, 60000, getMinionsSpawnMaintenanceInterval() + Rnd.get(5000));
	}
	
	public void setRaidStatus(RaidBossSpawnData.StatusEnum status)
	{
		raidStatus = status;
	}
	
	public RaidBossSpawnData.StatusEnum getRaidStatus()
	{
		return raidStatus;
	}
	
	/**
	 * Reduce the current HP of the L2Attackable, update its aggroList and launch the doDie Task if necessary.
	 */
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		super.reduceCurrentHp(damage, attacker, awake);
	}
	
	/**
	 * Cancel spawn minions task.
	 */
	private void cancelRespawnsMinions()
	{
		if (minionsSpawnTask != null)
		{
			minionsSpawnTask.cancel(true); // doesn't do it?
		}
	}
}
