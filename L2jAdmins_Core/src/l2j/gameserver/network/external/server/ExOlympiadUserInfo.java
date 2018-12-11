package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 * @author  godson
 */
public class ExOlympiadUserInfo extends AServerPacket
{
	private final int side;
	private final int objectId;
	private final String name;
	private final int classId;
	private final int curHp;
	private final int maxHp;
	private final int curCp;
	private final int maxCp;
	
	public ExOlympiadUserInfo(L2PcInstance player)
	{
		side = player.getOlympiadSide();
		objectId = player.getObjectId();
		name = player.getName();
		classId = player.getClassId().getId();
		curHp = (int) player.getCurrentHp();
		maxHp = player.getStat().getMaxHp();
		curCp = (int) player.getCurrentCp();
		maxCp = player.getStat().getMaxCp();
	}
	
	@Override
	public final void writeImpl()
	{
		writeC(0xfe);
		writeH(0x29);
		writeC(side);
		writeD(objectId);
		writeS(name);
		writeD(classId);
		writeD(curHp);
		writeD(maxHp);
		writeD(curCp);
		writeD(maxCp);
	}
}
