package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class StopPledgeWar extends AServerPacket
{
	private final String pledgeName;
	
	private final String cha;
	
	public StopPledgeWar(String pledge, String charName)
	{
		pledgeName = pledge;
		cha = charName;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x67);
		writeS(pledgeName);
		writeS(cha);
	}
}
