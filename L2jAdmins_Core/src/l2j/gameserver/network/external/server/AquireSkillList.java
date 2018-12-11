package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

/**
 * sample a3 05000000 03000000 03000000 06000000 3c000000 00000000 power strike 10000000 02000000 06000000 3c000000 00000000 mortal blow 38000000 04000000 06000000 36010000 00000000 power shot 4d000000 01000000 01000000 98030000 01000000 ATTACK aura 920sp 8e000000 03000000 03000000 cc010000 00000000
 * Armor Mastery format d (ddddd) skillid, level, maxlevel?, C4 format changes: 0000: [8a] [00 00 00 00] [35 00 00 00] 92 00 00 00 01 00 00 .....5.......... ^^^^^^^^^^^^^ 0010: 00 2d 00 00 00 04 01 00 00 00 00 00 00 a4 00 00 .-.............. 0020: 00 01 00 00 00 03 00 00 00 e4 0c 00 00 00 00 00
 * ................ 0030: 00 d4 00 00 00 01 00 00 00 06 00 00 00 08 52 00 ..............R.
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/27 15:29:57 $
 */
public class AquireSkillList extends AServerPacket
{
	private final List<SkillAquire> skills;
	private final boolean fishingSkills;
	
	private class SkillAquire
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int spCost;
		public int requirements;
		
		public SkillAquire(int id, int nextLevel, int maxLevel, int spCost, int requirements)
		{
			this.id = id;
			this.nextLevel = nextLevel;
			this.maxLevel = maxLevel;
			this.spCost = spCost;
			this.requirements = requirements;
		}
	}
	
	public AquireSkillList(boolean fishingSkill)
	{
		skills = new ArrayList<>();
		fishingSkills = fishingSkill;
	}
	
	public void addSkill(int id, int nextLevel, int maxLevel, int spCost, int requirements)
	{
		skills.add(new SkillAquire(id, nextLevel, maxLevel, spCost, requirements));
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x8a);
		writeD(fishingSkills ? 1 : 0); // c4
		writeD(skills.size());
		
		for (SkillAquire temp : skills)
		{
			writeD(temp.id);
			writeD(temp.nextLevel);
			writeD(temp.maxLevel);
			writeD(temp.spCost);
			writeD(temp.requirements);
		}
	}
}
