package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class EffectHeal extends EffectHateDamage
{
	public EffectHeal(Env env, EffectTemplate template)
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
		
		// calculate heal power
		var sps = getEffector().isChargedShot(ShotType.SPIRITSHOTS);
		var bss = getEffector().isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		if (bss)
		{
			hp *= 1.5;
		}
		else if (sps)
		{
			hp *= 1.3;
		}
		
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
	
	@Override
	protected void mobSeeSpell(L2Attackable mob, L2PcInstance caster, L2Character target, Skill skill)
	{
		// Calculate hate depending on skill type
		if (target.getLastHealAmount() > (mob.getStat().getMaxHp() / 5))
		{
			target.setLastHealAmount(mob.getStat().getMaxHp() / 5);
		}
		
		var divisor = getHateDivisor(caster);
		var hate = (int) (target.getLastHealAmount() / divisor);
		
		// Add extra hate if target is party member
		if ((caster != target) && (skill.getTargetType() == SkillTargetType.TARGET_PARTY))
		{
			if ((mob.getStat().getMaxHp() / 3) < (((mob.getHating(target) - mob.getHating(caster)) + 800) / divisor))
			{
				hate += mob.getStat().getMaxHp() / 3;
			}
			else
			{
				hate += ((mob.getHating(target) - mob.getHating(caster)) + 800) / divisor;
			}
		}
		
		// finally apply hate
		mob.addDamageHate(caster, 0, hate);
	}
	
	protected void sendMessage(double hp)
	{
		// send packet for update hp
		((L2PcInstance) getEffected()).updateCurHp();
		// send packet for message
		if (getEffector() != getEffected())
		{
			getEffected().sendPacket(new SystemMessage(SystemMessage.S2_HP_RESTORED_BY_C1).addString(getEffector().getName()).addNumber((int) hp));
		}
		else
		{
			getEffected().sendPacket(new SystemMessage(SystemMessage.S1_HP_RESTORED).addNumber((int) hp));
		}
	}
}
