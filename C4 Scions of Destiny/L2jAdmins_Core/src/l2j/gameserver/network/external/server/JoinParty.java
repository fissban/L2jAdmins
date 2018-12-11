package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample
 * <p>
 * 4c 01 00 00 00
 * <p>
 * format cd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class JoinParty extends AServerPacket
{
	private final int response;
	
	/**
	 * @param response
	 */
	public JoinParty(int response)
	{
		this.response = response;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x3a);
		writeD(response);
	}
}
