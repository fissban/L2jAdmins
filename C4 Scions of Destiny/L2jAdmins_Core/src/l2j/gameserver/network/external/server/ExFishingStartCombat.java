package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.fishing.enums.FishLureType;
import l2j.gameserver.model.actor.manager.pc.fishing.enums.FishModeType;
import l2j.gameserver.network.AServerPacket;

/**
 * Format (ch)dddcc
 * @author -Wooden-
 */
public class ExFishingStartCombat extends AServerPacket
{
	private final int playerObjId;
	private final int time, hp;
	private final FishLureType lureType;
	private final FishModeType modeType;
	
	public ExFishingStartCombat(L2PcInstance player, int time, int hp, FishModeType modeType, FishLureType lureType)
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
