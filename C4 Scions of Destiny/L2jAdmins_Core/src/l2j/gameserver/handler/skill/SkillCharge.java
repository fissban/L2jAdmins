package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

public class SkillCharge implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.CHARGE
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		var player = activeChar.getActingPlayer();
		
		if (player.getCharges() >= skill.getMaxCharges())
		{
			player.sendPacket(SystemMessage.FORCE_MAXIMUM);
			return;
		}
		
		player.addCharge(1);
		activeChar.sendPacket(new SystemMessage(SystemMessage.FORCE_INCREASED_TO_S1).addNumber(player.getCharges()));
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
