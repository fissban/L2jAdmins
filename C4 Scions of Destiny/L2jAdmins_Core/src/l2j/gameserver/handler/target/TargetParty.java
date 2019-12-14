package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class TargetParty implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (onlyFirst)
		{
			return List.of(activeChar);
		}
		
		List<L2Object> targetList = new ArrayList<>();
		
		targetList.add(activeChar);
		
		L2PcInstance player = null;
		
		if (activeChar instanceof L2Summon)
		{
			player = ((L2Summon) activeChar).getOwner();
			targetList.add(player);
		}
		else if (activeChar instanceof L2PcInstance)
		{
			player = (L2PcInstance) activeChar;
			if ((player.getPet() != null) && !player.getPet().isDead())
			{
				targetList.add(player.getPet());
			}
		}
		
		if ((player != null) && (player.getParty() != null))
		{
			// Get a list of Party Members
			for (L2PcInstance partyMember : player.getParty().getMembers())
			{
				if ((partyMember == null) || (partyMember == player))
				{
					continue;
				}
				
				// Get all visible objects in a spheric area near the L2Character
				if (!partyMember.isDead() && Util.checkIfInRange(skill.getSkillRadius(), player, partyMember, true))
				{
					targetList.add(partyMember);
					
					if ((partyMember.getPet() != null) && !partyMember.getPet().isDead())
					{
						targetList.add(partyMember.getPet());
					}
				}
			}
		}
		
		return targetList;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_PARTY;
	}
}
