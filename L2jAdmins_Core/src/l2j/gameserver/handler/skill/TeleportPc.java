package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;

/**
 * @author fissban
 */
public class TeleportPc implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.TELEPORT_PC
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		for (var object : targets)
		{
			if ((object != null) && (object instanceof L2Character))
			{
				var character = (L2Character) object;
				
				if (character.isAlikeDead())
				{
					continue;
				}
				
				// teleport player
				character.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ());
				// set agresion for next action
				
				ThreadPoolManager.schedule(() ->
				{
					try
					{
						activeChar.getAI().notifyEvent(CtrlEventType.AGGRESSION, character, 9999);
					}
					catch (Exception e)
					{
						//
					}
					
				}, skill.getHitTime() + 500);
			}
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
