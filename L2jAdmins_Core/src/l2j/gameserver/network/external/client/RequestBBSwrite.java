package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.handler.CommunityHandler;
import l2j.gameserver.handler.community.AbstractCommunityHandler;
import l2j.gameserver.network.AClientPacket;

/**
 * Format SSSSSS
 * @author -Wooden-
 */
public class RequestBBSwrite extends AClientPacket
{
	private String index;
	private String arg1;
	private String arg2;
	private String arg3;
	private String arg4;
	private String arg5;
	
	@Override
	protected void readImpl()
	{
		index = readS();
		arg1 = readS();
		arg2 = readS();
		arg3 = readS();
		arg4 = readS();
		arg5 = readS();
	}
	
	@Override
	public void runImpl()
	{
		AbstractCommunityHandler ach = CommunityHandler.getHandler(index);
		if (ach != null)
		{
			ach.parseWrite(index, getClient().getActiveChar(), arg1, arg2, arg3, arg4, arg5);
		}
		else
		{
			LOG.warning("No handler registered for community '" + Config.BBS_DEFAULT + "'");
		}
	}
}
