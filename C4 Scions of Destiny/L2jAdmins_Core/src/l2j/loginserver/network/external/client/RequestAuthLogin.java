package l2j.loginserver.network.external.client;

import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;

import l2j.Config;
import l2j.loginserver.LoginController;
import l2j.loginserver.network.ALoginClientPacket;
import l2j.loginserver.network.LoginClient.LoginClientState;
import l2j.loginserver.network.SessionKey;
import l2j.loginserver.network.external.server.AccountKicked;
import l2j.loginserver.network.external.server.AccountKicked.AccountKickedReason;
import l2j.loginserver.network.external.server.LoginFail.LoginFailReason;
import l2j.loginserver.network.external.server.LoginOk;
import l2j.loginserver.network.external.server.ServerList;
import l2j.util.Rnd;

public class RequestAuthLogin extends ALoginClientPacket
{
	private static Logger LOG = Logger.getLogger(RequestAuthLogin.class.getName());
	
	private byte[] raw = new byte[128];
	private String user;
	private String password;
	// Use in C6+
	// private int _ncotp;
	
	@Override
	public boolean readImpl()
	{
		if (super.buff.remaining() >= 128)
		{
			readB(raw);
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		byte[] decrypted = null;
		var client = getClient();
		try
		{
			var rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			// C4: 0, 128 ???
			// C6: 1, 128
			decrypted = rsaCipher.doFinal(raw, 0, 128);
		}
		catch (GeneralSecurityException e)
		{
			LOG.log(Level.INFO, "", e);
			e.printStackTrace();
			return;
		}
		
		try
		{
			user = new String(decrypted, 98, 14).trim().toLowerCase();
			password = new String(decrypted, 112, 16).trim();
			// Use in C6+
			// _ncotp = decrypted[0x7c];
			// _ncotp |= decrypted[0x7d] << 8;
			// _ncotp |= decrypted[0x7e] << 16;
			// _ncotp |= decrypted[0x7f] << 24;
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "", e);
			return;
		}
		
		var clientAddr = client.getConnection().getInetAddress();
		
		var info = LoginController.getInstance().retrieveAccountInfo(clientAddr, user, password);
		if (info == null)
		{
			client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
			return;
		}
		
		var result = LoginController.getInstance().tryCheckinAccount(client, clientAddr, info);
		switch (result)
		{
			case AUTH_SUCCESS:
				client.setAccount(info.getLogin());
				client.setState(LoginClientState.AUTHED_LOGIN);
				client.setSessionKey(new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt()));
				client.sendPacket((Config.SHOW_LICENCE) ? new LoginOk(client.getSessionKey()) : new ServerList(client));
				break;
			
			case INVALID_PASSWORD:
				client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
				break;
			
			case ACCOUNT_BANNED:
				client.close(new AccountKicked(AccountKickedReason.REASON_PERMANENTLY_BANNED));
				break;
			
			case ALREADY_ON_LS:
				var oldClient = LoginController.getInstance().getAuthedClient(info.getLogin());
				if (oldClient != null)
				{
					oldClient.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
					LoginController.getInstance().removeAuthedLoginClient(info.getLogin());
				}
				client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
				break;
			
			case ALREADY_ON_GS:
				var gsi = LoginController.getInstance().getAccountOnGameServer(info.getLogin());
				if (gsi != null)
				{
					client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);
					
					if (gsi.isAuthed())
					{
						gsi.getGameServerThread().kickPlayer(info.getLogin());
					}
				}
				break;
		}
	}
}