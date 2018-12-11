package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CrestData.CrestType;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExPledgeCrestLarge;

/**
 * Fomat : chd c: (id) 0xD0 h: (subid) 0x10 d: the crest id This is a trigger
 * @author -Wooden-
 */
public class RequestExPledgeCrestLarge extends AClientPacket
{
	private int crestId;
	
	@Override
	protected void readImpl()
	{
		crestId = readD();
	}
	
	@Override
	public void runImpl()
	{
		byte[] data = CrestData.getCrest(CrestType.PLEDGE_LARGE, crestId);
		
		if (data != null)
		{
			sendPacket(new ExPledgeCrestLarge(crestId, data));
		}
	}
}
