package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AServerPacket;

/**
 * sample 0000: 5a d8 a8 10 48 d8 a8 10 48 10 04 00 00 01 00 00 Z...H...H....... 0010: 00 f0 1a 00 00 68 28 00 00 .....h(.. format dddddd dddh (h)
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class MagicSkillUse extends AServerPacket
{
	private final int targetId;
	private final int skillId;
	private final int skillLevel;
	private final int hitTime;
	private final int reuseDelay;
	private final int chaId, x, y, z;
	private boolean critical = false;
	
	public MagicSkillUse(L2Character cha, L2Character target, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		chaId = cha.getObjectId();
		targetId = target.getObjectId();
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.hitTime = hitTime;
		this.reuseDelay = reuseDelay;
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
	}
	
	public MagicSkillUse(L2Character cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		chaId = cha.getObjectId();
		targetId = cha.getTargetId();
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.hitTime = hitTime;
		this.reuseDelay = reuseDelay;
		x = cha.getX();
		y = cha.getY();
		z = cha.getZ();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x48);
		writeD(chaId);
		writeD(targetId);
		writeD(skillId);
		writeD(skillLevel);
		writeD(hitTime);
		writeD(reuseDelay);
		writeD(x);
		writeD(y);
		writeD(z);
		
		if (critical)
		{
			writeD(0x01);
			writeH(0x00);
		}
		else
		{
			writeD(0x00);
		}
		
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
