package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.ClanMemberInstance;
import l2j.gameserver.util.Util;

/**
 * @author fissban, zarie
 */
public class TargetClan implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Object> targetList = new ArrayList<>();
		
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) activeChar;
			
			if (player.isInOlympiadMode())
			{
				return List.of(player);
			}
			
			targetList.add(player);
			
			Clan clan = player.getClan();
			
			if (clan != null)
			{
				// Get all visible objects in a spheric area near the L2Character
				// Get Clan Members
				for (ClanMemberInstance member : clan.getMembers())
				{
					L2PcInstance newTarget = member.getPlayerInstance();
					
					if ((newTarget == null) || (newTarget == player))
					{
						continue;
					}
					
					if (!Util.checkIfInRange(skill.getSkillRadius(), activeChar, newTarget, true))
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
		}
		else if (activeChar instanceof L2Npc)
		{
			final L2Npc npc = (L2Npc) activeChar;
			
			if ((npc.getFactionId() == null) || npc.getFactionId().isEmpty())
			{
				targetList.add(activeChar);
			}
			else
			{
				targetList.add(activeChar);
				for (L2Npc newTarget : activeChar.getKnownList().getObjectTypeInRadius(L2Npc.class, skill.getCastRange()))
				{
					if (npc.getFactionId().equals(newTarget.getFactionId()))
					{
						targetList.add(newTarget);
					}
				}
			}
		}
		
		return targetList;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_CLAN;
	}
}
