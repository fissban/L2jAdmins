package l2j.gameserver.handler.target;

import java.util.Collections;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;

/**
 * @author fissban
 */
public class TargetOwnerPet implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (activeChar instanceof L2Summon)
		{
			target = ((L2Summon) activeChar).getOwner();
			if ((target != null) && !target.isDead())
			{
				return List.of(target);
			}
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_OWNER_PET;
	}
}
