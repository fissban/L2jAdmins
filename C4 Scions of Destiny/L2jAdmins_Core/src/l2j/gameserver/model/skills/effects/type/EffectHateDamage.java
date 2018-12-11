package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectHateDamage extends Effect
{
	public EffectHateDamage(Env env, EffectTemplate template)
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
		//
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
	
	/**
	 * increase hate for mobs in knownlist,<br>
	 * need override {@link #mobSeeSpell(L2Attackable, L2PcInstance, L2Character, Skill)}
	 */
	protected void hateForKnownList()
	{
		var caster = getEffector().getActingPlayer();
		
		if (caster != null)
		{
			if (getEffected() == caster.getPet())
			{
				return;
			}
			
			for (var mob : getEffector().getKnownList().getObjectTypeInRadius(L2Attackable.class, 1000))
			{
				if (mob.hasAI() && (mob.getAI().getIntention() == CtrlIntentionType.ATTACK) && (mob.getTarget() != null) && (mob.getTarget() == getEffector()))
				{
					mobSeeSpell(mob, (L2PcInstance) getEffector(), getEffected(), getSkill());
				}
			}
		}
	}
	
	/**
	 * Gets the hate divider that will be used for some effects.<br>
	 * Use in Buffs & Heals
	 * @param  caster
	 * @return
	 */
	public double getHateDivisor(L2PcInstance caster)
	{
		var lvl = caster.getLevel();
		
		if (lvl < 10)
		{
			return 15.0;
		}
		if ((lvl >= 10) && (lvl < 20))
		{
			return 11.5;
		}
		if ((lvl >= 20) && (lvl < 30))
		{
			return 8.5;
		}
		if ((lvl >= 30) && (lvl < 40))
		{
			return 6.0;
		}
		if ((lvl >= 40) && (lvl < 50))
		{
			return 4.0;
		}
		if ((lvl >= 50) && (lvl < 60))
		{
			return 2.5;
		}
		if ((lvl >= 60) && (lvl < 70))
		{
			return 1.5;
		}
		
		return 1.0;
	}
	
	protected void mobSeeSpell(L2Attackable mob, L2PcInstance caster, L2Character target, Skill skill)
	{
		//
	}
}
