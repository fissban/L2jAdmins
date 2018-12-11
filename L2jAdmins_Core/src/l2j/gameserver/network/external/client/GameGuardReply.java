package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;

/**
 * @author zabbix Lets drink to code! Unknown Packet:ca 0000: 45 00 01 00 1e 37 a2 f5 00 00 00 00 00 00 00 00 E....7..........
 */
public class GameGuardReply extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		getClient().setGameGuardOk(true);
	}
}
