package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;

public final class PlayFail extends ALoginServerPacket
{
	public static enum PlayFailReason
	{
		REASON_SYSTEM_ERROR(0x01),
		REASON_USER_OR_PASS_WRONG(0x02),
		REASON3(0x03),
		REASON4(0x04),
		REASON_TOO_MANY_PLAYERS(0x0f);
		
		private final int code;
		
		PlayFailReason(int code)
		{
			this.code = code;
		}
		
		public final int getCode()
		{
			return code;
		}
	}
	
	private final PlayFailReason reason;
	
	public PlayFail(PlayFailReason reason)
	{
		this.reason = reason;
	}
	
	@Override
	public void write()
	{
		writeC(0x06);
		writeC(reason.getCode());
	}
}