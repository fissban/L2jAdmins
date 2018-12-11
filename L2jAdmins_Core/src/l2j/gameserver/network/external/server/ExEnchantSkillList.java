package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

public class ExEnchantSkillList extends AServerPacket
{
	private final List<SkillExEnchant> skills;
	
	public ExEnchantSkillList()
	{
		skills = new ArrayList<>();
	}
	
	private class SkillExEnchant
	{
		public int id;
		public int nextLevel;
		public int sp;
		public int exp;
		
		SkillExEnchant(int pId, int pNextLevel, int pSp, int pExp)
		{
			id = pId;
			nextLevel = pNextLevel;
			sp = pSp;
			exp = pExp;
		}
	}
	
	public void addSkill(int id, int level, int sp, int exp)
	{
		skills.add(new SkillExEnchant(id, level, sp, exp));
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x17);
		
		writeD(skills.size());
		for (SkillExEnchant sk : skills)
		{
			writeD(sk.id);
			writeD(sk.nextLevel);
			writeD(sk.sp);
			writeD(sk.exp);
		}
	}
}
