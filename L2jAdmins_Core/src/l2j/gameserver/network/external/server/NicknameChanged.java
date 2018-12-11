package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * @author devScarlet
 */
public class NicknameChanged extends AServerPacket
{
	private final String title;
	private final int objectId;
	
	public NicknameChanged(L2PcInstance cha)
	{
		objectId = cha.getObjectId();
		title = cha.getTitle();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xcc);
		writeD(objectId);
		writeS(title);
	}
}
