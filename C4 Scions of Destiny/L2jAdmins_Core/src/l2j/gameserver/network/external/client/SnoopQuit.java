package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;

/**
 * @author -Wooden-
 */
public class SnoopQuit extends AClientPacket
{
	@SuppressWarnings("unused")
	private int snoopID;
	
	@Override
	protected void readImpl()
	{
		snoopID = readD();
	}
	
	@Override
	public void runImpl()
	{
		// FIXME Q uso tiene este paquete?
	}
}
