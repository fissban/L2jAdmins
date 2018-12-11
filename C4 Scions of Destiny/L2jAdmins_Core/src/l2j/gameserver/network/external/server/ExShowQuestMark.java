package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author Luca Baldi
 */
public class ExShowQuestMark extends AServerPacket
{
	private final int questId;
	
	public ExShowQuestMark(int questId)
	{
		this.questId = questId;
	}
	
	@Override
	public final void writeImpl()
	{
		writeC(0xfe);
		writeH(0x1a);
		writeD(questId);
	}
}
