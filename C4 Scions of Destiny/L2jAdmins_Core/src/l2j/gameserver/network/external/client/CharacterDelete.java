package l2j.gameserver.network.external.client;

import java.util.logging.Level;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.CharDeleteFail;
import l2j.gameserver.network.external.server.CharDeleteFail.CharDeleteFailType;
import l2j.gameserver.network.external.server.CharDeleteOk;
import l2j.gameserver.network.external.server.CharSelectInfo;

/**
 * This class ...
 * @version $Revision: 1.8.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class CharacterDelete extends AClientPacket
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
			int answer = getClient().markToDeleteChar(charSlot);
			switch (answer)
			{
				case -1: // Error
					break;
				case 0: // Success!
					sendPacket(CharDeleteOk.STATIC_PACKET);
					break;
				case 1:
					sendPacket(new CharDeleteFail(CharDeleteFailType.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
					break;
				case 2:
					sendPacket(new CharDeleteFail(CharDeleteFailType.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
					break;
			}
			
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error:", e);
			
		}
		
		CharSelectInfo cs = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
		sendPacket(cs);
		getClient().setCharSelectSlot(cs.getCharInfo());
	}
}
