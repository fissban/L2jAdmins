package l2j.gameserver.model.actor.instance;

import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;

/**
 * This class manages all Grand Bosses.
 * @version $Revision: 1.0.0.0 $ $Date: 2006/06/16 $
 */
public final class L2GrandBossInstance extends L2MonsterInstance
{
	private static final int BOSS_MAINTENANCE_INTERVAL = 10000;
	
	/**
	 * Constructor for L2GrandBossInstance. This represents all grandbosses:
	 * <ul>
	 * <li>12001 Queen Ant</li>
	 * <li>12052 Core</li>
	 * <li>12169 Orfen</li>
	 * <li>12211 Antharas</li>
	 * <li>12372 Baium</li>
	 * <li>12374 Zaken</li>
	 * <li>12899 Valakas</li>
	 * </ul>
	 * <br>
	 * @param objectId ID of the instance
	 * @param template L2NpcTemplate of the instance
	 */
	public L2GrandBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2GrandBossInstance);
	}
	
	@Override
	protected int getMinionsSpawnMaintenanceInterval()
	{
		return BOSS_MAINTENANCE_INTERVAL;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
	}
	
	@Override
	public boolean isRaid()
	{
		return true;
	}
}
