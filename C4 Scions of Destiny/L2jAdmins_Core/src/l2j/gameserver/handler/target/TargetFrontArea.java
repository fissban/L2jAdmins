package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.model.zone.enums.ZoneType;

/**
 * @author fissban
 */
public class TargetFrontArea implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (onlyFirst)
		{
			List.of(target);
		}
		
		List<L2Object> targetList = new ArrayList<>();
		
		boolean srcInArena = (activeChar.isInsideZone(ZoneType.PVP) && !activeChar.isInsideZone(ZoneType.SIEGE));
		
		for (L2Character character : activeChar.getKnownList().getObjectTypeInRadius(L2Character.class, skill.getSkillRadius()))
		{
			if (!((character instanceof L2Attackable) || (character instanceof L2Playable)))
			{
				continue;
			}
			
			if (getTargetType() == SkillTargetType.TARGET_FRONT_AREA)
			{
				if (!target.isInFrontOf(activeChar))
				{
					continue;
				}
			}
			
			if (!skill.checkForAreaOffensiveSkills(activeChar, character, srcInArena))
			{
				continue;
			}
			
			targetList.add(character);
		}
		
		return targetList;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_FRONT_AREA;
	}
}
