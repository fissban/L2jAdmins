package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.model.skills.enums.SkillType;

/**
 * @author fissban
 */
public class TargetCorpseAlly implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Object> targetList = new ArrayList<>();
		
		if (activeChar instanceof L2PcInstance)
		{
			L2PcInstance player = (L2PcInstance) activeChar;
			Clan clan = player.getClan();
			
			if (player.isInOlympiadMode())
			{
				return List.of(player);
			}
			
			if (clan != null)
			{
				// Get all visible objects in area near the L2Character
				// Get Clan Members
				for (L2Character newTarget : activeChar.getKnownList().getObjectTypeInRadius(L2Character.class, skill.getSkillRadius()))
				{
					if (!(newTarget instanceof L2PcInstance))
					{
						continue;
					}
					if (((((L2PcInstance) newTarget).getAllyId() == 0) || (((L2PcInstance) newTarget).getAllyId() != player.getAllyId())) && ((((L2PcInstance) newTarget).getClan() == null) || (((L2PcInstance) newTarget).getClanId() != player.getClanId())))
					{
						continue;
					}
					
					if (!((L2PcInstance) newTarget).isDead())
					{
						continue;
					}
					
					if (skill.getSkillType() == SkillType.RESURRECT)
					{
						if (SiegeManager.getInstance().getSiege(newTarget) != null)
						{
							continue;
						}
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
		return targetList;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_CORPSE_ALLY;
	}
}
