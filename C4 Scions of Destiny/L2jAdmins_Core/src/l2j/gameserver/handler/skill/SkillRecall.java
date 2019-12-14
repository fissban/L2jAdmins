package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.Config;
import l2j.gameserver.data.MapRegionData.TeleportWhereType;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SkillRecall implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.RECALL
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		for (var object : targets)
		{
			if (!(object instanceof L2PcInstance))
			{
				continue;
			}
			
			var player = (L2PcInstance) object;
			
			// If Alternate rule Karma punishment is set to true, forbid skill Recall to player with Karma
			if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (player.getKarma() > 0))
			{
				continue;
			}
			
			if (player.isInOlympiadMode())
			{
				player.sendPacket(SystemMessage.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
				continue;
			}
			
			// Check to see if the current player target is in a festival.
			if (player.isFestivalParticipant())
			{
				player.sendMessage("You may not use an escape skill in a festival.");
				continue;
			}
			
			// Check to see if player is in jail
			if (player.isInJail())
			{
				player.sendMessage("You cannot escape from jail.");
				continue;
			}
			
			if (skill.getTeleLocation() != null)
			{
				player.teleToLocation(skill.getTeleLocation(), true);
			}
			else if (skill.getRecallType() != null)
			{
				if (player.getClan() != null)
				{
					if ((skill.getRecallType() == TeleportWhereType.CLAN_HALL) && player.getClan().hasClanHall())
					{
						player.teleToLocation(TeleportWhereType.CLAN_HALL);
						return;
					}
					if ((skill.getRecallType() == TeleportWhereType.CASTLE) && player.getClan().hasCastle())
					{
						player.teleToLocation(TeleportWhereType.CASTLE);
						return;
					}
				}
				
				player.teleToLocation(TeleportWhereType.TOWN);
			}
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		// If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (activeChar.getKarma() > 0))
		{
			return false;
		}
		return true;
	}
}
