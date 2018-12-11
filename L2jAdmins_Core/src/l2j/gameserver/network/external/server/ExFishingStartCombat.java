package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.fishing.enums.PcFishLureType;
import l2j.gameserver.model.fishing.enums.PcFishModeType;
import l2j.gameserver.network.AServerPacket;

/**
 * Format (ch)dddcc
 * @author -Wooden-
 */
public class ExFishingStartCombat extends AServerPacket
{
	private final int playerObjId;
	private final int time, hp;
	private final PcFishLureType lureType;
	private final PcFishModeType modeType;
	
	public ExFishingStartCombat(L2PcInstance player, int time, int hp, PcFishModeType modeType, PcFishLureType lureType)
	{
		playerObjId = player.getObjectId();
		this.time = time;
		this.hp = hp;
		this.modeType = modeType;
		this.lureType = lureType;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x15);
		
		writeD(playerObjId);
		writeD(time);
		writeD(hp);
		writeC(modeType.ordinal()); // mode: 0 = resting, 1 = fighting
		writeC(lureType.ordinal());// 0 = newbie lure, 1 = normal lure
	}
}
