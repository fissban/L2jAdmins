package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2CubicInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SkillCubic implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.CUBIC
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (activeChar.isAlikeDead() || !(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		if (((L2PcInstance) activeChar).inObserverMode())
		{
			return;
		}
		
		for (var object : targets)
		{
			if (!(object instanceof L2PcInstance))
			{
				continue;
			}
			
			var player = (L2PcInstance) object;
			
			var mastery = player.getSkillLevel(Skill.SKILL_CUBIC_MASTERY);
			if (mastery < 0)
			{
				mastery = 0;
			}
			
			var cubicType = skill.getCubicType();
			
			// If the character has already replaced the new hub for the old.
			if (player.getCubics().containsKey(cubicType))
			{
				L2CubicInstance cubic = player.getCubic(cubicType);
				cubic.stopAction();
				cubic.stopDisappear();
				player.delCubic(cubicType);
			}
			else if (player.getCubics().size() > mastery)
			{
				activeChar.sendPacket(SystemMessage.CUBIC_SUMMONING_FAILED);
				continue;
			}
			
			if (player == activeChar)
			{
				player.addCubic(cubicType, skill.getLevel(), false);
			}
			else
			{
				player.addCubic(cubicType, skill.getLevel(), true);
			}
			
			player.broadcastUserInfo();
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
