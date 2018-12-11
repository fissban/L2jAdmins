package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * Format: (ch) dc d: character object id c: 1 if won 0 if failed
 * @author -Wooden-
 */
public class ExFishingEnd extends AServerPacket
{
	private final boolean win;
	L2Character character;
	
	public ExFishingEnd(boolean win, L2PcInstance character)
	{
		this.win = win;
		this.character = character;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x14);
		writeD(character.getObjectId());
		writeC(win ? 1 : 0);
	}
}
