package l2j.gameserver.network.external.server;

import java.util.Collection;
import java.util.Collections;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AServerPacket;

public class GMViewSkillInfo extends AServerPacket
{
	private final L2PcInstance cha;
	private Collection<Skill> skills;
	
	public GMViewSkillInfo(L2PcInstance cha)
	{
		this.cha = cha;
		skills = cha.getAllSkills();
		if (skills.isEmpty())
		{
			skills = Collections.emptyList();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x91);
		writeS(cha.getName());
		writeD(skills.size());
		
		for (Skill skill : skills)
		{
			writeD(skill.isPassive() ? 1 : 0);
			writeD(skill.getLevel());
			writeD(skill.getId());
		}
	}
}
