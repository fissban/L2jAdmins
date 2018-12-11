package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.AServerPacket;

/**
 * @author Micr0(Rework for L2jAdmins)
 */
public class HennaItemRemoveInfo extends AServerPacket
{
	private final L2PcInstance player;
	private final HennaHolder henna;
	
	public HennaItemRemoveInfo(HennaHolder henna, L2PcInstance player)
	{
		this.henna = henna;
		this.player = player;
	}
	
	@Override
	public final void writeImpl()
	{
		writeC(0xe6);
		writeD(henna.getSymbolId()); // symbol Id
		writeD(henna.getItemIdDye()); // item id of dye
		writeD(henna.getAmountDyeRequire() / 2); // amount of given dyes
		writeD(henna.getCancelFee()); // amount of required adenas
		writeD(1); // able to remove or not 0 is false and 1 is true
		writeD(player.getInventory().getAdena());
		
		writeD(player.getStat().getINT()); // current INT
		writeC(player.getStat().getINT() - henna.getStatINT()); // equip INT
		writeD(player.getStat().getSTR()); // current STR
		writeC(player.getStat().getSTR() - henna.getStatSTR()); // equip STR
		writeD(player.getStat().getCON()); // current CON
		writeC(player.getStat().getCON() - henna.getStatCON()); // equip CON
		writeD(player.getStat().getMEN()); // current MEM
		writeC(player.getStat().getMEN() - henna.getStatMEN()); // equip MEM
		writeD(player.getStat().getDEX()); // current DEX
		writeC(player.getStat().getDEX() - henna.getStatDEX()); // equip DEX
		writeD(player.getStat().getWIT()); // current WIT
		writeC(player.getStat().getWIT() - henna.getStatWIT()); // equip WIT
	}
}
