package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.CharSelectInfo;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.2 $ $Date: 2005/03/27 15:29:29 $
 */
public class CharacterRestore extends AClientPacket
{
	// cd
	private int charSlot;
	
	@Override
	protected void readImpl()
	{
		charSlot = readD();
	}
	
	@Override
	public void runImpl()
	{
		try
		{
			getClient().markRestoredChar(charSlot);
		}
		catch (Exception e)
		{
		}
		
		CharSelectInfo cl = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
		sendPacket(cl);
		getClient().setCharSelectSlot(cl.getCharInfo());
	}
}
