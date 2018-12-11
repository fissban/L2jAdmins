package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.thread.LoginServerThread;
import l2j.loginserver.network.SessionKey;

/**
 * This class ...
 * @version $Revision: 1.9.2.3.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class AuthLogin extends AClientPacket
{
	private String loginName;
	
	private int playKey1;
	private int playKey2;
	private int loginKey1;
	private int loginKey2;
	
	@Override
	protected void readImpl()
	{
		loginName = readS().toLowerCase();
		playKey2 = readD();
		playKey1 = readD();
		loginKey1 = readD();
		loginKey2 = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getAccountName() != null)
		{
			return;
		}
		
		getClient().setAccountName(loginName);
		getClient().setSessionId(new SessionKey(loginKey1, loginKey2, playKey1, playKey2));
		
		LoginServerThread.getInstance().addClient(loginName, getClient());
	}
}
