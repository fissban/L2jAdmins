package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * @author zabbix Lets drink to code!
 */
public class GameGuardQuery extends AServerPacket
{
	public GameGuardQuery()
	{
		// Lets make user as gg-unauthorized
		// We will set him as ggOK after reply from client
		// or kick
		if (getClient() != null)
		{
			getClient().setGameGuardOk(false);
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xf9);
	}
}
