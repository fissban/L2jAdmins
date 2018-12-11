package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;

/**
 * @author fissban
 */
public class TargetAlly implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return Collections.emptyList();
		}
		
		L2PcInstance player = (L2PcInstance) activeChar;
		
		if (player.isInOlympiadMode())
		{
			return List.of(player);
		}
		
		if (onlyFirst || player.isInOlympiadMode())
		{
			return List.of(player);
		}
		
		List<L2Object> targetList = new ArrayList<>();
		
		targetList.add(player);
		
		Clan clan = player.getClan();
		
		if (clan != null)
		{
			// Get all visible objects in a spheric area near the L2Character
			// Get Clan Members
			for (L2PcInstance newTarget : activeChar.getKnownList().getObjectTypeInRadius(L2PcInstance.class, skill.getSkillRadius()))
			{
				if ((newTarget == null) || newTarget.isDead())
				{
					continue;
				}
				
				if (((newTarget.getAllyId() == 0) || (newTarget.getAllyId() != player.getAllyId())) && ((newTarget.getClan() == null) || (newTarget.getClanId() != player.getClanId())))
				{
					continue;
				}
				
				// Don't add this target if this is a Pc->Pc pvp casting and pvp condition not met
				if (!player.checkPvpSkill(newTarget, skill))
				{
					continue;
				}
				
				targetList.add(newTarget);
			}
		}
		return targetList;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_ALLY;
	}
}
