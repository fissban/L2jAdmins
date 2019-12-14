package l2j.gameserver.handler.target;

import java.util.Collections;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class TargetPartyMember implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return Collections.emptyList();
		}
		
		if (target.isAlikeDead())
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return Collections.emptyList();
		}
		
		if ((target == activeChar) || ((activeChar.getParty() != null) && (target.getParty() != null) && (activeChar.getParty().getLeader().getObjectId() == target.getParty().getLeader().getObjectId()))
			|| ((activeChar instanceof L2PcInstance) && (target instanceof L2Summon) && (((L2PcInstance) activeChar).getPet() == target)) || ((activeChar instanceof L2Summon) && (target instanceof L2PcInstance) && (activeChar == ((L2PcInstance) target).getPet())))
		{
			return List.of(target);
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_PARTY_MEMBER;
	}
}
