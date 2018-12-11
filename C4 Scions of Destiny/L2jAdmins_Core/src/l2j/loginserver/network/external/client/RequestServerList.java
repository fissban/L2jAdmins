package l2j.loginserver.network.external.client;

import l2j.loginserver.network.ALoginClientPacket;
import l2j.loginserver.network.external.server.LoginFail.LoginFailReason;
import l2j.loginserver.network.external.server.ServerList;

/**
 * Format: ddc d: fist part of session id d: second part of session id c: ?
 */
public class RequestServerList extends ALoginClientPacket
{
	private int skey1;
	private int skey2;
	private int data3;
	
	public int getSessionKey1()
	{
		return skey1;
	}
	
	public int getSessionKey2()
	{
		return skey2;
	}
	
	public int getData3()
	{
		return data3;
	}
	
	@Override
	public boolean readImpl()
	{
		if (super.buff.remaining() >= 8)
		{
			skey1 = readD(); // loginOk 1
			skey2 = readD(); // loginOk 2
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		if (getClient().getSessionKey().checkLoginPair(skey1, skey2))
		{
			getClient().sendPacket(new ServerList(getClient()));
		}
		else
		{
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}