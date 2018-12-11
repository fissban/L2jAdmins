package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;

/**
 * Format chS c (id) 0xD0 h (subid) 0x0C S the hero's words :)
 * @author -Wooden-
 */
public class RequestWriteHeroWords extends AClientPacket
{
	@SuppressWarnings("unused")
	private String heroWords;
	
	@Override
	protected void readImpl()
	{
		heroWords = readS();
	}
	
	@Override
	public void runImpl()
	{
		//
	}
}
