package l2j.gameserver.network;

import java.util.logging.Logger;

import l2j.mmocore.SendablePacket;

/**
 * @author -Wooden-
 */
public abstract class AServerPacket extends SendablePacket<GameClient>
{
	protected static final Logger LOG = Logger.getLogger(AServerPacket.class.getName());
	
	protected abstract void writeImpl();
	
	@Override
	public void write()
	{
		try
		{
			writeImpl();
		}
		catch (Throwable t)
		{
			LOG.severe("Failed writing " + getClass().getSimpleName() + " for " + getClient().toString());
		}
	}
	
	public void runImpl()
	{
		//
	}
}
