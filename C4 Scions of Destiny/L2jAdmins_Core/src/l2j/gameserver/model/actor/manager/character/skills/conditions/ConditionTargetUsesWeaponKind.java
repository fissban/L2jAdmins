package l2j.gameserver.model.actor.manager.character.skills.conditions;

import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.items.ItemWeapon;

/**
 * @author mkizub
 */
public class ConditionTargetUsesWeaponKind extends Condition
{
	final int weaponMask;
	
	public ConditionTargetUsesWeaponKind(int weaponMask)
	{
		this.weaponMask = weaponMask;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (env.getTarget() == null)
		{
			return false;
		}
		
		ItemWeapon item = env.getTarget().getActiveWeaponItem();
		
		if (item == null)
		{
			return false;
		}
		
		return (item.getType().mask() & weaponMask) != 0;
	}
}
