package l2j.gameserver.model.actor.manager.character.skills;

import java.util.List;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.external.server.ActionFailed;

public class L2SkillDefault extends Skill
{
	public L2SkillDefault(StatsSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(L2Character caster, List<L2Object> targets)
	{
		caster.sendPacket(ActionFailed.STATIC_PACKET);
		caster.sendMessage("Skill not implemented.  Skill ID: " + getId() + " " + getSkillType());
	}
}
