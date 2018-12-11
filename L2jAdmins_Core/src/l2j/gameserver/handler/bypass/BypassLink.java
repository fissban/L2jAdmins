package l2j.gameserver.handler.bypass;

import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author fissban
 */
public class BypassLink implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Link"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!(target instanceof L2Npc))
		{
			return false;
		}
		
		String path = command.substring(5).trim();
		if (path.indexOf("..") != -1)
		{
			return false;
		}
		
		String filename = "data/html/" + path;
		NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(target.getObjectId()));
		activeChar.sendPacket(html);
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
