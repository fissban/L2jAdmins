package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author fissban
 */
public class ShortBuffStatusUpdate extends AServerPacket
{
	private final int skillId;
	private final int skillLvl;
	private final int duration;
	
	public ShortBuffStatusUpdate(final int skillId, final int skillLvl, final int duration)
	{
		this.skillId = skillId;
		this.skillLvl = skillLvl;
		this.duration = duration;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xF4);
		writeD(skillId);
		writeD(skillLvl);
		writeD(duration);
	}
}