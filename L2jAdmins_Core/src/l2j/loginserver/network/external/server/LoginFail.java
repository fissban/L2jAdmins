package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;

/**
 * Fromat: d d: the failure reason
 */
public final class LoginFail extends ALoginServerPacket
{
	public static enum LoginFailReason
	{
		REASON_SYSTEM_ERROR(0x01),
		REASON_PASS_WRONG(0x02),
		REASON_USER_OR_PASS_WRONG(0x03),
		REASON_ACCESS_FAILED(0x04),
		REASON_ACCOUNT_IN_USE(0x07),
		REASON_SERVER_OVERLOADED(0x0f),
		REASON_SERVER_MAINTENANCE(0x10),
		REASON_TEMP_PASS_EXPIRED(0x11),
		REASON_DUAL_BOX(0x23);
		
		private final int code;
		
		LoginFailReason(int code)
		{
			this.code = code;
		}
		
		public final int getCode()
		{
			return code;
		}
	}
	
	private final LoginFailReason reason;
	
	public LoginFail(LoginFailReason reason)
	{
		this.reason = reason;
	}
	
	@Override
	public void write()
	{
		writeC(0x01);
		writeD(reason.getCode());
	}
}