package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * format d rev 417
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class AuthLoginFail extends AServerPacket
{
	public enum AuthLoginFailType
	{
		NO_TEXT,
		SYSTEM_ERROR_LOGIN_LATER,
		PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT,
		PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2,
		ACCESS_FAILED_TRY_LATER,
		INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT,
		ACCESS_FAILED_TRY_LATER2,
		ACOUNT_ALREADY_IN_USE,
		ACCESS_FAILED_TRY_LATER3,
		ACCESS_FAILED_TRY_LATER4,
		ACCESS_FAILED_TRY_LATER5,
	}
	
	private final AuthLoginFailType reason;
	
	/**
	 * @param reason
	 */
	public AuthLoginFail(AuthLoginFailType reason)
	{
		this.reason = reason;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x14);
		writeD(reason.ordinal());
	}
}
