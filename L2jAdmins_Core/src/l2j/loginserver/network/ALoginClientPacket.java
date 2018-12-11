package l2j.loginserver.network;

import java.util.logging.Logger;

import l2j.mmocore.ReceivablePacket;

public abstract class ALoginClientPacket extends ReceivablePacket<LoginClient>
{
	private static Logger LOG = Logger.getLogger(ALoginClientPacket.class.getName());
	
	@Override
	public final boolean read()
	{
		try
		{
			return readImpl();
		}
		catch (Exception e)
		{
			LOG.severe("ERROR READING: " + this.getClass().getSimpleName());
			e.printStackTrace();
			return false;
		}
	}
	
	protected abstract boolean readImpl();
}
