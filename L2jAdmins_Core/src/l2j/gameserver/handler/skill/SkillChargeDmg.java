package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SkillChargeDmg implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.CHARGEDAM
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		var player = (L2PcInstance) activeChar;
		
		if (player.isAlikeDead())
		{
			return;
		}
		
		player.addCharge(-skill.getNumCharges());
		
		var ss = activeChar.isChargedShot(ShotType.SOULSHOTS);
		for (var target2 : targets)
		{
			var target = (L2Character) target2;
			if (target.isAlikeDead())
			{
				continue;
			}
			
			var shld = Formulas.calcShldUse(player, target);
			
			var damage = Formulas.calcPhysDam(player, target, skill, shld, false, ss);
			
			if (damage > 0)
			{
				// Formula tested by L2Guru
				damage *= 0.8 + (0.201 * player.getCharges());
				target.reduceCurrentHp(damage, player);
				
				player.sendDamageMessage(target, (int) damage, false, false, false);
			}
		}
		
		activeChar.setChargedShot(ShotType.SOULSHOTS, false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		if (activeChar.getCharges() < skill.getNumCharges())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED).addSkillName(skill.getId()));
			return false;
		}
		
		return true;
	}
}
