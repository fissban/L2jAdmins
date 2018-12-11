package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;

/**
 * This class manages all Minions. In a group mob, there are one master called RaidBoss and several slaves called Minions.
 * @version $Revision: 1.20.4.6 $ $Date: 2005/04/06 16:13:39 $
 */
public final class L2MinionInstance extends L2MonsterInstance
{
	/** The master L2Character whose depends this L2MinionInstance on */
	private L2MonsterInstance master;
	
	/**
	 * Constructor of L2MinionInstance (use L2Character and L2NpcInstance constructor).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Call the L2Character constructor to set the template of the L2MinionInstance (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2MinionInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template
	 */
	public L2MinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2MinionInstance);
	}
	
	/** Return True if the L2Character is minion of RaidBoss. */
	@Override
	public boolean isRaid()
	{
		return (getLeader() instanceof L2RaidBossInstance);
	}
	
	/**
	 * Return the master of this L2MinionInstance.<BR>
	 * @return
	 */
	public L2MonsterInstance getLeader()
	{
		return master;
	}
	
	@Override
	public void onSpawn()
	{
		// Notify Leader that Minion has Spawned
		super.onSpawn();
	}
	
	/**
	 * Set the master of this L2MinionInstance.<BR>
	 * @param leader The L2Character that leads this L2MinionInstance
	 */
	public void setLeader(L2MonsterInstance leader)
	{
		master = leader;
	}
	
	/**
	 * Manages the doDie event for this L2MinionInstance.<BR>
	 * @param killer The L2Character that killed this L2MinionInstance.<BR>
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		master.getMinionList().moveMinionToRespawnList(this);
		return true;
	}
}
