package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.fishing.enums.PcFishAnimationType;
import l2j.gameserver.model.fishing.enums.PcFishModeType;
import l2j.gameserver.network.AServerPacket;

/**
 * Format (ch)dddcccd d: character oid d: time left d: fish hp c: c: c: 00 if fish gets damage 02 if fish regens d:
 * @author -Wooden-
 */
public class ExFishingHpRegen extends AServerPacket
{
	private final L2PcInstance player;
	private final int time;
	private final int fishHp;
	private final PcFishModeType fishMode; // Hp mode
	private final PcFishAnimationType animType;
	private final int goodUse;
	private final int penalty;
	
	public ExFishingHpRegen(L2PcInstance player, int time, int fishHp, PcFishModeType fishMode, int goodUse, PcFishAnimationType animType, int penalty)
	{
		this.player = player;
		this.time = time;
		this.fishHp = fishHp;
		this.fishMode = fishMode;
		this.goodUse = goodUse;
		this.animType = animType;
		this.penalty = penalty;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xfe);
		writeH(0x16);
		
		writeD(player.getObjectId());
		writeD(time);
		writeD(fishHp);
		writeC(fishMode.ordinal()); // HP -> raise:1 stop:0
		writeC(goodUse); // its 1 when skill is correct used
		writeC(animType.ordinal()); // Anim -> 1:realing 2:pumping 0:none
		writeD(penalty); // Penalty
	}
}
