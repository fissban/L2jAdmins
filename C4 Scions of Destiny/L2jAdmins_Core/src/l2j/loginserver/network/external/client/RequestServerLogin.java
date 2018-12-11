package l2j.loginserver.network.external.client;

import l2j.Config;
import l2j.loginserver.LoginController;
import l2j.loginserver.network.ALoginClientPacket;
import l2j.loginserver.network.SessionKey;
import l2j.loginserver.network.external.server.LoginFail.LoginFailReason;
import l2j.loginserver.network.external.server.PlayFail.PlayFailReason;
import l2j.loginserver.network.external.server.PlayOk;

/**
 * Fromat is ddc d: first part of session id d: second part of session id c: server ID
 */
public class RequestServerLogin extends ALoginClientPacket
{
	private int skey1;
	private int skey2;
	private int serverId;
	
	public int getSessionKey1()
	{
		return skey1;
	}
	
	public int getSessionKey2()
	{
		return skey2;
	}
	
	public int getServerID()
	{
		return serverId;
	}
	
	@Override
	public boolean readImpl()
	{
		if (super.buff.remaining() >= 9)
		{
			skey1 = readD();
			skey2 = readD();
			serverId = readC();
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		SessionKey sk = getClient().getSessionKey();
		
		// if we didnt showed the license we cant check these values
		if (!Config.SHOW_LICENCE || sk.checkLoginPair(skey1, skey2))
		{
			if (LoginController.getInstance().isLoginPossible(getClient(), serverId))
			{
				getClient().setJoinedGS(true);
				getClient().sendPacket(new PlayOk(sk));
			}
			else
			{
				getClient().close(PlayFailReason.REASON_TOO_MANY_PLAYERS);
			}
		}
		else
		{
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
