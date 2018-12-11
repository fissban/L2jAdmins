package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.KeyPacket;

/**
 * This class ...
 * @version $Revision: 1.5.2.8.2.8 $ $Date: 2005/04/02 10:43:04 $
 */
public class ProtocolVersion extends AClientPacket
{
	private int version;
	
	@Override
	protected void readImpl()
	{
		version = readD();
	}
	
	@Override
	public void runImpl()
	{
		// this packet is never encrypted
		if (version == -2)
		{
			// this is just a ping attempt from the new C2 client
			getClient().close((AServerPacket) null);
		}
		else if ((version < Config.MIN_PROTOCOL_REVISION) || (version > Config.MAX_PROTOCOL_REVISION))
		{
			LOG.info("Client: " + getClient().toString() + " -> Protocol Revision: " + version + " is invalid. Minimum is " + Config.MIN_PROTOCOL_REVISION + " and Maximum is " + Config.MAX_PROTOCOL_REVISION + " are supported. Closing connection.");
			LOG.warning("Wrong Protocol Version " + version);
			
			getClient().close((AServerPacket) null);
		}
		else
		{
			sendPacket(new KeyPacket(getClient().enableCrypt()));
		}
	}
}
