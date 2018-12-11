package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectHealStatic extends EffectHeal
{
	public EffectHealStatic(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.HEAL;
	}
	
	@Override
	public void onStart()
	{
		// Should not heal walls, doors, headquarters, and grand bosses
		if ((getEffected() instanceof L2DoorInstance) || (getEffected() instanceof L2GrandBossInstance) || (getEffected() instanceof L2SiegeFlagInstance))
		{
			return;
		}
		
		if (getEffected().isDead())
		{
			return;
		}
		
		// get heal power
		var hp = getPower();
		
		// getEffected increaseCurrentHp & setLastHealAmount
		getEffected().setCurrentHp(hp + getEffected().getCurrentHp());
		getEffected().setLastHealAmount((int) hp);
		
		if (getEffected() instanceof L2PcInstance)
		{
			// send packet for update hp
			((L2PcInstance) getEffected()).updateCurHp();
			// send packet for message
			sendMessage(hp);
		}
		
		// increase hate for mobs in knownlist
		hateForKnownList();
	}
	
	@Override
	public void onExit()
	{
		//
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
