package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class SurrenderPledgeWar extends AServerPacket
{
	private final String pledgeName;
	
	private final String cha;
	
	public SurrenderPledgeWar(String pledge, String charName)
	{
		pledgeName = pledge;
		cha = charName;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x69);
		writeS(pledgeName);
		writeS(cha);
	}
}
