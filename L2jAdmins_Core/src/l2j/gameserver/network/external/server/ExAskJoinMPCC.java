package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author chris_00 Asks the player to join a CC
 */
public class ExAskJoinMPCC extends AServerPacket
{
	private final String requestorName;
	
	/**
	 * @param requestorName
	 */
	public ExAskJoinMPCC(String requestorName)
	{
		this.requestorName = requestorName;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x27);
		writeS(requestorName); // name of CCLeader
	}
}
