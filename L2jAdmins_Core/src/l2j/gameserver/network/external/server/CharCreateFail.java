package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class CharCreateFail extends AServerPacket
{
	public enum CharCreateFailType
	{
		REASON_CREATION_FAILED,
		REASON_TOO_MANY_CHARACTERS,
		REASON_NAME_ALREADY_EXISTS,
		REASON_16_ENG_CHARS
	}
	
	private final CharCreateFailType error;
	
	public CharCreateFail(CharCreateFailType errorCode)
	{
		error = errorCode;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x1a);
		writeD(error.ordinal());
	}
}
