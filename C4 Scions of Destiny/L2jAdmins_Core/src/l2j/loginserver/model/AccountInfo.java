package l2j.loginserver.model;

import java.util.Objects;

public final class AccountInfo
{
	private String login;
	private String passHash;
	private int accessLevel;
	private int lastServer;
	
	public AccountInfo(final String login, final String passHash, final int accessLevel, final int lastServer)
	{
		Objects.requireNonNull(login, "login");
		Objects.requireNonNull(passHash, "passHash");
		
		if (login.isEmpty())
		{
			throw new IllegalArgumentException("login");
		}
		
		if (passHash.isEmpty())
		{
			throw new IllegalArgumentException("passHash");
		}
		
		this.login = login.toLowerCase();
		this.passHash = passHash;
		this.accessLevel = accessLevel;
		this.lastServer = lastServer;
	}
	
	public boolean checkPassHash(final String passHash)
	{
		return this.passHash.equals(passHash);
	}
	
	public String getLogin()
	{
		return login;
	}
	
	public int getAccessLevel()
	{
		return accessLevel;
	}
	
	public int getLastServer()
	{
		return lastServer;
	}
}