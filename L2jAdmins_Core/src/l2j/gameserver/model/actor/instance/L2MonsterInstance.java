package l2j.gameserver.model.actor.instance;

import l2j.gameserver.instancemanager.MinionsListManager;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.MonsterKnownList;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;

/**
 * This class manages all Monsters. L2MonsterInstance :<br>
 * <br>
 * <li>L2MinionInstance
 * <li>L2RaidBossInstance
 * <li>L2GrandBossInstance
 */
public class L2MonsterInstance extends L2Attackable
{
	/**
	 * Constructor of L2MonsterInstance (use L2Character and L2NpcInstance constructor).<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Call the L2Character constructor to set the template of the L2MonsterInstance (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)
	 * <li>Set the name of the L2MonsterInstance
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it
	 * @param objectId Identifier of the object to initialized
	 * @param template
	 */
	public L2MonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2MonsterInstance);
		
		initMinions();
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new MonsterKnownList(this));
	}
	
	@Override
	public final MonsterKnownList getKnownList()
	{
		return (MonsterKnownList) super.getKnownList();
	}
	
	/**
	 * Return True if the attacker is not another L2MonsterInstance.
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		if (attacker instanceof L2MonsterInstance)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Return True if the L2MonsterInstance is Aggressive (aggroRange > 0).
	 */
	@Override
	public boolean isAggressive()
	{
		return (getTemplate().getAggroRange() > 0);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		startMinionsSpawnMaintenanceTask();
	}
	
	/**
	 * Overriden in<br>
	 * <li>L2RaidBossInstance
	 * <li>L2GrandBossInstance
	 * @return
	 */
	protected int getMinionsSpawnMaintenanceInterval()
	{
		return 0;
	}
	
	/**
	 * Spawn all minions at a regular interval<br>
	 * Override in<br>
	 * <li>L2RaidBossInstance
	 */
	protected void startMinionsSpawnMaintenanceTask()
	{
		if (!hasMinions())
		{
			return;
		}
		
		minionList.deleteAllSpawnedMinions();
		minionList.spawnMinions();
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (hasMinions())
		{
			minionList.deleteAllSpawnedMinions();
		}
		super.deleteMe();
	}
	
	// MINIONS -------------------------------------------------------------------------
	
	protected volatile MinionsListManager minionList = null;
	
	public MinionsListManager getMinionList()
	{
		return minionList;
	}
	
	private void initMinions()
	{
		if (!getTemplate().getMinions().isEmpty())
		{
			minionList = new MinionsListManager(this);
		}
	}
	
	public boolean hasMinions()
	{
		return minionList != null;
	}
	
	/**
	 * Obs: Used in GameStatusThread
	 * @return
	 */
	public int getTotalSpawnedMinionsGroups()
	{
		if (minionList == null)
		{
			return 0;
		}
		
		return minionList.lazyCountSpawnedMinionsGroups();
	}
}
