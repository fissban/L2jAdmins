package l2j.gameserver.handler.bypass;

import java.util.StringTokenizer;

import l2j.gameserver.handler.BypassHandler.IBypassHandler;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author fissban
 */
public class BypassPlayerHelp implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"player_help"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken(); // actual command
		
		String path = st.nextToken();
		
		if (path.indexOf("..") != -1)
		{
			return false;
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/help/" + path);
		activeChar.sendPacket(html);
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
