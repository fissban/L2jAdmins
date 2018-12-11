package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.AServerPacket;

public class HennaItemInfo extends AServerPacket
{
	private final L2PcInstance player;
	private final HennaHolder henna;
	
	public HennaItemInfo(HennaHolder henna, L2PcInstance player)
	{
		this.henna = henna;
		this.player = player;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xe3);
		writeD(henna.getSymbolId()); // symbol Id
		writeD(henna.getItemIdDye()); // item id of dye
		writeD(henna.getAmountDyeRequire()); // total amount of dye require
		writeD(henna.getPrice()); // total amount of aden require to draw symbol
		writeD(1); // able to draw or not 0 is false and 1 is true
		writeD(player.getInventory().getAdena());
		
		writeD(player.getStat().getINT()); // current INT
		writeC(player.getStat().getINT() + henna.getStatINT()); // equip INT
		writeD(player.getStat().getSTR()); // current STR
		writeC(player.getStat().getSTR() + henna.getStatSTR()); // equip STR
		writeD(player.getStat().getCON()); // current CON
		writeC(player.getStat().getCON() + henna.getStatCON()); // equip CON
		writeD(player.getStat().getMEN()); // current MEN
		writeC(player.getStat().getMEN() + henna.getStatMEN()); // equip MEN
		writeD(player.getStat().getDEX()); // current DEX
		writeC(player.getStat().getDEX() + henna.getStatDEX()); // equip DEX
		writeD(player.getStat().getWIT()); // current WIT
		writeC(player.getStat().getWIT() + henna.getStatWIT()); // equip WIT
	}
}
