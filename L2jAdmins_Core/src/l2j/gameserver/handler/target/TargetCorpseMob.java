package l2j.gameserver.handler.target;

import java.util.Collections;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class TargetCorpseMob implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return Collections.emptyList();
		}
		
		if (!((target instanceof L2SummonInstance) || (target instanceof L2Attackable)) || !target.isDead())
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return Collections.emptyList();
		}
		
		return List.of(target);
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_CORPSE_MOB;
	}
}
