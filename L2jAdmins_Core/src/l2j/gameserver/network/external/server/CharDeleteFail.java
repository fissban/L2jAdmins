package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharDeleteFail extends AServerPacket
{
	public enum CharDeleteFailType
	{
		REASON_DELETION_FAILED,
		REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER,
		REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED;
		
		public int getValue()
		{
			return ordinal() + 1;
		}
	}
	
	private final CharDeleteFailType error;
	
	public CharDeleteFail(CharDeleteFailType errorCode)
	{
		error = errorCode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x24);
		writeD(error.getValue());
	}
}
