package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 8e d8 a8 10 48 10 04 00 00 01 00 00 00 01 00 00 ....H........... 0010: 00 d8 a8 10 48 ....H format ddddd d
 */
public class MagicSkillLaunched extends AServerPacket
{
	private final int charObjId;
	private final int skillId;
	private final int skillLevel;
	private List<L2Object> targets = new ArrayList<>();
	
	public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel, List<L2Object> targets)
	{
		charObjId = cha.getObjectId();
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.targets = targets;
	}
	
	public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel)
	{
		charObjId = cha.getObjectId();
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		targets.add(cha);
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x76);
		writeD(charObjId);
		writeD(skillId);
		writeD(skillLevel);
		writeD(targets.size()); // also failed or not?
		
		for (L2Object target : targets)
		{
			writeD(target.getObjectId());
		}
	}
}
