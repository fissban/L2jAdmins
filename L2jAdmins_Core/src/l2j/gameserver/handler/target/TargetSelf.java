package l2j.gameserver.handler.target;

import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;

/**
 * @author fissban
 */
public class TargetSelf implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		return List.of(activeChar);
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_SELF;
	}
}
