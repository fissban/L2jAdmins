package l2j.loginserver.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;
import java.util.logging.Logger;

import l2j.loginserver.LoginController;
import l2j.loginserver.crypt.LoginCrypt;
import l2j.loginserver.crypt.ScrambledKeyPair;
import l2j.loginserver.network.external.server.LoginFail;
import l2j.loginserver.network.external.server.LoginFail.LoginFailReason;
import l2j.loginserver.network.external.server.PlayFail;
import l2j.loginserver.network.external.server.PlayFail.PlayFailReason;
import l2j.mmocore.MMOClient;
import l2j.mmocore.MMOConnection;
import l2j.mmocore.SendablePacket;
import l2j.util.Rnd;

/**
 * Represents a client connected into the LoginServer
 */
public final class LoginClient extends MMOClient<MMOConnection<LoginClient>>
{
	private static Logger LOG = Logger.getLogger(LoginClient.class.getName());
	
	public static enum LoginClientState
	{
		CONNECTED,
		AUTHED_GG,
		AUTHED_LOGIN
	}
	
	private LoginClientState state;
	
	private final LoginCrypt loginCrypt;
	private final ScrambledKeyPair scrambledPair;
	private final byte[] blowfishKey;
	
	private String account;
	private int accessLevel;
	private int lastServer;
	private SessionKey sessionKey;
	private final int sessionId;
	private boolean joinedGS;
	
	private final long connectionStartTime;
	
	public LoginClient(MMOConnection<LoginClient> con)
	{
		super(con);
		
		state = LoginClientState.CONNECTED;
		scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		blowfishKey = LoginController.getInstance().getBlowfishKey();
		sessionId = Rnd.nextInt();
		connectionStartTime = System.currentTimeMillis();
		loginCrypt = new LoginCrypt();
		loginCrypt.setKey(blowfishKey);
	}
	
	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		try
		{
			if (!loginCrypt.decrypt(buf.array(), buf.position(), size))
			{
				LOG.warning("Wrong checksum from client: " + toString() + " packet: " + (buf.get(0) & 0xff));
				super.getConnection().close((SendablePacket<LoginClient>) null);
				return false;
			}
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			super.getConnection().close((SendablePacket<LoginClient>) null);
			return false;
		}
	}
	
	@Override
	public boolean encrypt(ByteBuffer buf, int size)
	{
		var offset = buf.position();
		try
		{
			size = loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		
		buf.position(offset + size);
		return true;
	}
	
	public LoginClientState getState()
	{
		return state;
	}
	
	public void setState(LoginClientState state)
	{
		this.state = state;
	}
	
	public byte[] getBlowfishKey()
	{
		return blowfishKey;
	}
	
	public byte[] getScrambledModulus()
	{
		return scrambledPair.getScrambledModulus();
	}
	
	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) scrambledPair.getKeyPair().getPrivate();
	}
	
	public String getAccount()
	{
		return account;
	}
	
	public void setAccount(String account)
	{
		this.account = account;
	}
	
	public void setAccessLevel(int accessLevel)
	{
		this.accessLevel = accessLevel;
	}
	
	public int getAccessLevel()
	{
		return accessLevel;
	}
	
	public void setLastServer(int lastServer)
	{
		this.lastServer = lastServer;
	}
	
	public int getLastServer()
	{
		return lastServer;
	}
	
	public int getSessionId()
	{
		return sessionId;
	}
	
	public boolean hasJoinedGS()
	{
		return joinedGS;
	}
	
	public void setJoinedGS(boolean val)
	{
		joinedGS = val;
	}
	
	public void setSessionKey(SessionKey sessionKey)
	{
		this.sessionKey = sessionKey;
	}
	
	public SessionKey getSessionKey()
	{
		return sessionKey;
	}
	
	public long getConnectionStartTime()
	{
		return connectionStartTime;
	}
	
	public void sendPacket(ALoginServerPacket lsp)
	{
		getConnection().sendPacket(lsp);
	}
	
	public void close(LoginFailReason reason)
	{
		getConnection().close(new LoginFail(reason));
	}
	
	public void close(PlayFailReason reason)
	{
		getConnection().close(new PlayFail(reason));
	}
	
	public void close(ALoginServerPacket lsp)
	{
		getConnection().close(lsp);
	}
	
	@Override
	public void onDisconnection()
	{
		if (!hasJoinedGS() || ((getConnectionStartTime() + LoginController.LOGIN_TIMEOUT) < System.currentTimeMillis()))
		{
			LoginController.getInstance().removeAuthedLoginClient(getAccount());
		}
	}
	
	@Override
	public String toString()
	{
		var address = getConnection().getInetAddress();
		if (getState() == LoginClientState.AUTHED_LOGIN)
		{
			return "[" + getAccount() + " (" + (address == null ? "disconnected" : address.getHostAddress()) + ")]";
		}
		
		return "[" + (address == null ? "disconnected" : address.getHostAddress()) + "]";
	}
	
	@Override
	public void onForcedDisconnection()
	{
	}
}