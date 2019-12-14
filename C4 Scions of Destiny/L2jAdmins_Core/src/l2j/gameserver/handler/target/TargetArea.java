package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class TargetArea implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return Collections.emptyList();
		}
		
		if (onlyFirst)
		{
			return List.of(target);
		}
		
		if (target.isDead() || (target == activeChar) || (!((target instanceof L2Attackable) || (target instanceof L2Playable))))
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return Collections.emptyList();
		}
		
		boolean srcInArena = (activeChar.isInsideZone(ZoneType.PVP) && !activeChar.isInsideZone(ZoneType.SIEGE));
		
		List<L2Object> targetList = new ArrayList<>();
		
		targetList.add(target);
		for (L2Character character : target.getKnownList().getObjectType(L2Character.class))
		{
			if (!checkCondition(skill, srcInArena, activeChar, character))
			{
				continue;
			}
			
			targetList.add(character);
		}
		
		// add target
		if (checkCondition(skill, srcInArena, activeChar, target))
		{
			targetList.add(target);
		}
		
		return targetList;
	}
	
	private static boolean checkCondition(Skill skill, boolean srcInArena, L2Character activeChar, L2Character character)
	{
		if (character == activeChar)
		{
			return false;
		}
		
		if (!((character instanceof L2Attackable) || (character instanceof L2Playable)))
		{
			return false;
		}
		
		if (!Util.checkIfInRange(skill.getSkillRadius(), activeChar, character, true))
		{
			return false;
		}
		
		if (!skill.checkForAreaOffensiveSkills(activeChar, character, srcInArena))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_AREA;
	}
}
