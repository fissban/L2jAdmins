package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.handler.CommunityHandler;
import l2j.gameserver.handler.community.AbstractCommunityHandler;
import l2j.gameserver.network.AClientPacket;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestShowBoard extends AClientPacket
{
	@SuppressWarnings("unused")
	private int unknown;
	
	@Override
	protected void readImpl()
	{
		unknown = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (EngineModsManager.onCommunityBoard(getClient().getActiveChar(), Config.BBS_DEFAULT))
		{
			return;
		}
		
		AbstractCommunityHandler ach = CommunityHandler.getHandler(Config.BBS_DEFAULT);
		if (ach != null)
		{
			ach.parseCmd(Config.BBS_DEFAULT, getClient().getActiveChar());
		}
		else
		{
			LOG.warning("No handler registered for community '" + Config.BBS_DEFAULT + "'");
		}
	}
}
