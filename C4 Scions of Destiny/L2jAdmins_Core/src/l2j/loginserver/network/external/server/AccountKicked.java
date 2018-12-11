package l2j.loginserver.network.external.server;

import l2j.loginserver.network.ALoginServerPacket;

public final class AccountKicked extends ALoginServerPacket
{
	public static enum AccountKickedReason
	{
		REASON_DATA_STEALER(0x01),
		REASON_GENERIC_VIOLATION(0x08),
		REASON_7_DAYS_SUSPENDED(0x10),
		REASON_PERMANENTLY_BANNED(0x20);
		
		private final int code;
		
		AccountKickedReason(int code)
		{
			this.code = code;
		}
		
		public final int getCode()
		{
			return code;
		}
	}
	
	private final AccountKickedReason reason;
	
	public AccountKicked(AccountKickedReason reason)
	{
		this.reason = reason;
	}
	
	@Override
	public void write()
	{
		writeC(0x02);
		writeD(reason.getCode());
	}
}