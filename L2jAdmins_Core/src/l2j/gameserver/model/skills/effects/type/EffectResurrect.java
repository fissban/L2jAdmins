package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.Formulas;

/**
 * @author fissban
 */
public class EffectResurrect extends Effect
{
	
	public EffectResurrect(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return null;
	}
	
	@Override
	public void onStart()
	{
		// always is L2PcInstance
		L2PcInstance activeChar = (L2PcInstance) getEffector();
		
		if (getEffected() instanceof L2PcInstance)
		{
			((L2PcInstance) getEffected()).getRequestRevive().reviveRequest(activeChar, getSkill(), false);
		}
		else if (getEffected() instanceof L2PetInstance)
		{
			if (((L2PetInstance) getEffected()).getOwner() == getEffector())
			{
				getEffected().doRevive(Formulas.calculateSkillResurrectRestorePercent(getSkill().getPower(), activeChar));
			}
			else
			{
				((L2PetInstance) getEffected()).getOwner().getRequestRevive().reviveRequest(activeChar, getSkill(), true);
			}
		}
		else
		{
			getEffected().doRevive(Formulas.calculateSkillResurrectRestorePercent(getSkill().getPower(), activeChar));
		}
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
