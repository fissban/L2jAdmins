package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

public class SkillCpDamPercent implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.CPDAMPERCENT
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		// boolean ss = skill.useSoulShot() && activeChar.isChargedShot(ShotType.SOULSHOTS);
		var sps = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		var bss = skill.useSpiritShot() && activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (var target2 : targets)
		{
			var target = (L2Character) target2;
			if ((activeChar instanceof L2PcInstance) && (target instanceof L2PcInstance) && ((L2PcInstance) target).isFakeDeath())
			{
				target.stopFakeDeath(true);
			}
			else if (target.isDead() || target.isInvul())
			{
				continue;
			}
			
			var damage = (int) (target.getCurrentCp() * (skill.getPower() / 100));
			
			if (damage > 0)
			{
				activeChar.sendDamageMessage(target, damage, false, false, false);
				
				target.setCurrentCp(target.getCurrentCp() - damage);
				target.sendPacket(new SystemMessage(SystemMessage.C1_GAVE_YOU_S2_DMG).addString(activeChar.getName()).addNumber(damage));
				
			}
			else
			{
				activeChar.sendPacket(SystemMessage.ATTACK_FAILED);
			}
		}
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : (sps ? ShotType.SPIRITSHOTS : null), false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
