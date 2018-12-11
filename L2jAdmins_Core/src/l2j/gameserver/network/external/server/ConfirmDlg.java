package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author Dezmond_snz Format: cdddsdd
 */
public class ConfirmDlg extends AServerPacket
{
	private final int requestId;
	private String name = "";
	
	public ConfirmDlg(int requestId, String requestorName)
	{
		this.requestId = requestId;
		name = requestorName;
	}
	
	public ConfirmDlg(int requestId)
	{
		this.requestId = requestId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xed);
		writeD(requestId);
		writeD(0x02); // ??
		writeD(0x00); // ??
		writeS(name);
		writeD(0x01); // ??
	}
}
