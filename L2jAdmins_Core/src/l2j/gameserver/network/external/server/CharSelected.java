package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.task.continuous.GameTimeTaskManager;

/**
 * This class ...
 * @version $Revision: 1.4.2.5.2.6 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharSelected extends AServerPacket
{
	// SdSddddddddddffddddddddddddddddddddddddddddddddddddddddd d
	private final L2PcInstance cha;
	private final int sessionId;
	
	/**
	 * @param cha
	 * @param sessionId
	 */
	public CharSelected(L2PcInstance cha, int sessionId)
	{
		this.cha = cha;
		this.sessionId = sessionId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x15);
		
		writeS(cha.getName());
		writeD(cha.getObjectId()); // ??
		writeS(cha.getTitle());
		writeD(sessionId);
		writeD(cha.getClanId());
		writeD(0x00); // ??
		writeD(cha.getSex().ordinal());
		writeD(cha.getRace().ordinal());
		writeD(cha.getClassId().getId());
		writeD(0x01); // active ??
		writeD(cha.getX());
		writeD(cha.getY());
		writeD(cha.getZ());
		
		writeF(cha.getCurrentHp());
		writeF(cha.getCurrentMp());
		writeD(cha.getSp());
		writeD((int) cha.getExp());
		writeD(cha.getLevel());
		writeD(cha.getKarma());
		writeD(cha.getPkKills()); // ?
		writeD(cha.getStat().getINT());
		writeD(cha.getStat().getSTR());
		writeD(cha.getStat().getCON());
		writeD(cha.getStat().getMEN());
		writeD(cha.getStat().getDEX());
		writeD(cha.getStat().getWIT());
		
		for (int i = 0; i < 30; i++)
		{
			writeD(0x00);
		}
		
		writeD(0x00); // c3 work
		writeD(0x00); // c3 work
		
		// extra info
		writeD(GameTimeTaskManager.getInstance().getGameTime()); // in-game time
		
		writeD(0x00); //
		
		writeD(0x00); // c3
		
		writeD(0x00); // c3 InspectorBin
		writeD(0x00); // c3
		writeD(0x00); // c3
		writeD(0x00); // c3
		
		writeD(0x00); // c3 InspectorBin for 528 client
		writeD(0x00); // c3
		writeD(0x00); // c3
		
		writeD(0x00); // c3
		writeD(0x00); // c3
		writeD(0x00); // c3
		writeD(0x00); // c3
		writeD(0x00); // c3
	}
}
