package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

public class StartPledgeWar extends AServerPacket
{
	private final String pledgeName;
	
	private final String charName;
	
	public StartPledgeWar(String pledgeName, String charName)
	{
		this.pledgeName = pledgeName;
		this.charName = charName;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x65);
		writeS(charName);
		writeS(pledgeName);
	}
}
