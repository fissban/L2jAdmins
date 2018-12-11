package l2j.gameserver.network.internal.loginserver;

import l2j.gameserver.network.ALoginPacket;

public class LoginServerFail extends ALoginPacket
{
	private static final String[] REASONS =
	{
		"None",
		"Reason: ip banned",
		"Reason: ip reserved",
		"Reason: wrong hexid",
		"Reason: id reserved",
		"Reason: no free ID",
		"Not authed",
		"Reason: already logged in"
	};
	private final int reason;
	
	/**
	 * @param decrypt
	 */
	public LoginServerFail(byte[] decrypt)
	{
		super(decrypt);
		reason = readC();
	}
	
	public String getReasonString()
	{
		return REASONS[reason];
	}
	
	public int getReason()
	{
		return reason;
	}
}
