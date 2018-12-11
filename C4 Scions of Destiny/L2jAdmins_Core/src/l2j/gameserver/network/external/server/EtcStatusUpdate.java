package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.AServerPacket;

/* Packet format: F3 XX000000 YY000000 ZZ000000 */

/**
 * @author Luca Baldi
 */
public class EtcStatusUpdate extends AServerPacket
{
	private final L2PcInstance activeChar;
	
	public EtcStatusUpdate(L2PcInstance activeChar)
	{
		this.activeChar = activeChar;
	}
	
	@Override
	public void writeImpl()
	{
		
		writeC(0xF3); // several icons to a separate line (0 = disabled)
		
		writeD(activeChar.getCharges()); // 1-7 increase force, lvl
		writeD(activeChar.getWeightPenalty()); // 1-4 weight penalty, lvl (1=50%, 2=66.6%, 3=80%, 4=100%)
		writeD((activeChar.isInRefusalMode() || activeChar.isChatBanned()) ? 1 : 0); // 1 = block all chat
		writeD(activeChar.isInsideZone(ZoneType.EFFECT) ? 1 : 0); // 1 = danger area
		writeD(Math.min(activeChar.getExpertisePenalty(), 1)); // 1 = grade penalty
	}
}
