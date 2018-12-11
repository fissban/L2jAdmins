package l2j.gameserver.network;

import java.nio.BufferUnderflowException;
import java.util.logging.Logger;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.EnterWorld;
import l2j.mmocore.ReceivablePacket;
import l2j.util.ConsoleColor;

/**
 * This class ...
 * @version $Revision: 1.5.4.9 $ $Date: 2005/03/27 15:29:30 $
 */
public abstract class AClientPacket extends ReceivablePacket<GameClient>
{
	public static final Logger LOG = Logger.getLogger(AServerPacket.class.getName());
	
	protected abstract void readImpl();
	
	protected abstract void runImpl();
	
	@Override
	public boolean read()
	{
		try
		{
			readImpl();
			return true;
		}
		catch (Exception e)
		{
			LOG.severe("Failed reading " + ConsoleColor.RED + getType() + ConsoleColor.RESET + " for " + ConsoleColor.GREEN + getClient().toString());
			
			if (e instanceof BufferUnderflowException)
			{
				getClient().onBufferUnderflow();
			}
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void run()
	{
		try
		{
			runImpl();
			
			// Depending of the packet send, removes spawn protection
			if (triggersOnActionRequest())
			{
				final L2PcInstance player = getClient().getActiveChar();
				if ((player != null) && player.isSpawnProtected())
				{
					player.onActionRequest();
				}
			}
		}
		catch (Throwable t)
		{
			LOG.severe("Failed reading " + getType() + " for " + getClient().toString());
			
			if (this instanceof EnterWorld)
			{
				getClient().closeNow();
			}
			
			t.printStackTrace();
		}
	}
	
	protected final void sendPacket(AServerPacket gsp)
	{
		getClient().sendPacket(gsp);
	}
	
	/**
	 * @return A String with this packet name for debuging purposes
	 */
	public String getType()
	{
		return "[C] " + getClass().getSimpleName();
	}
	
	/**
	 * Overriden with true value on some packets that should disable spawn protection
	 * @return
	 */
	protected boolean triggersOnActionRequest()
	{
		return true;
	}
}
