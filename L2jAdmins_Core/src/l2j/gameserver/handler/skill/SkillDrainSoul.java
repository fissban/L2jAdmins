package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author drunk_
 */
public class SkillDrainSoul implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.DRAIN_SOUL
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		// Register.
		((L2Attackable) targets.get(0)).registerAbsorber((L2PcInstance) activeChar);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		// Check player.
		if ((activeChar == null) || activeChar.isDead())
		{
			return false;
		}
		
		// Check quest condition.
		var st = activeChar.getScriptState("Q350_EnhanceYourWeapon");
		if ((st == null) || !st.isStarted())
		{
			return false;
		}
		
		// Get target.
		if ((target == null) || !(target instanceof L2Attackable))
		{
			return false;
		}
		
		// Check monster.
		var mob = (L2Attackable) target;
		if (mob.isDead())
		{
			return false;
		}
		
		// Range condition, cannot be higher than skill's effectRange.
		if (!activeChar.isInsideRadius(mob, skill.getEffectRange(), true, true))
		{
			return false;
		}
		
		// Check if the skill is Drain Soul (Soul Crystals) and if the target is a MOB
		if (!(target instanceof L2MonsterInstance))
		{
			// Send a System Message to the L2PcInstance
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			return false;
		}
		
		return true;
	}
}
