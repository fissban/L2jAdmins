package l2j.gameserver.handler.command.admin;

import java.util.Map.Entry;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.ShowBoard;

/**
 * A class that allows an administrator to change almost any game settings.<br>
 * Any changes made will return to normal one you see rebooted the server.<br>
 * @author fissban
 */
public class AdminConfigs implements IAdminCommandHandler
{
	@Override
	public String[] getAdminCommandList()
	{
		return new String[]
		{
			"admin_config",
			"admin_changeConfig",
		};
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		int page = 0;
		switch (actualCommand)
		{
			// ----------~ COMMAND ~---------- //
			case "admin_config":
				if (st.hasMoreTokens())
				{
					page = Integer.parseInt(st.nextToken());
				}
				break;
			
			// ----------~ COMMAND ~---------- //
			case "admin_changeConfig":
				try
				{
					String config = st.nextToken();
					String value = st.nextToken();
					page = Integer.parseInt(st.nextToken());
					
					Config.changeConfig(config, value);
					separateAndSend(htmlCommunity(page), activeChar);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				break;
		}
		
		separateAndSend(htmlCommunity(page), activeChar);
		return false;
	}
	
	private static String htmlCommunity(int page)
	{
		int CONFIG_PER_PAGE = 50;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<center>");
		
		sb.append("<font color=\"LEVEL\">Here you will be able to change all server configurations.</font>");
		sb.append("<font color=\"LEVEL\">* </font>These will be adjusted in real time.");
		sb.append("<font color=\"LEVEL\">* </font>After the restart will return to its original value.");
		
		sb.append("<table width=610>");
		sb.append("<tr width=60>");
		sb.append("<td width=40><font color=\"LEVEL\">new value:</font></td>");
		sb.append("<td width=180><edit var=box width=180 height=16></td>");
		sb.append("</tr>");
		sb.append("</table>");
		
		sb.append("<table width=610>");
		sb.append("<tr width=60>");
		
		// variable para lleavr la cantidad de configs que usaremos por linea.
		int i = 0;
		// variable para determinar los configs a mostrar en cada pag.
		int j = 0;
		
		// Se recorren todos las configuraciones.
		for (Entry<String, String> config : Config.getAllConfigs().entrySet())
		{
			j++;
			
			if (page > 1)
			{
				if (j < ((page - 1) * CONFIG_PER_PAGE))
				{
					continue;
				}
			}
			
			// definimos la pag
			if (j == (page * CONFIG_PER_PAGE))
			{
				break;
			}
			
			// cantidad de configs q pondremos por linea.
			if (i == 5)
			{
				sb.append("</tr>");
				sb.append("<tr width=60>");
				i = 0;
			}
			
			sb.append("<td>");
			sb.append("<font color=LEVEL>value: " + config.getValue() + "</font> + <br1>");
			sb.append("<button value=\"" + config.getKey().replace("_", " ").toLowerCase() + "\" action=\"bypass -h admin_changeConfig " + config.getKey() + " box\" back=L2UI_CT1.Button_DF_Down width=50 height=20 fore=L2UI_CT1.Button_DF>");
			sb.append("</td>");
			i++;
		}
		
		sb.append("</tr>");
		sb.append("</table");
		sb.append("</center>");
		
		sb.append("</body></html>");
		return sb.toString();
	}
	
	// UTIL
	
	/**
	 * Mostramos al player un html dentro del community.
	 * @param html
	 * @param player
	 */
	private static void separateAndSend(String html, L2PcInstance player)
	{
		if (html == null)
		{
			return;
		}
		
		if (html.length() < 8180)
		{
			player.sendPacket(new ShowBoard(html, "101"));
			player.sendPacket(new ShowBoard(null, "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < 16360)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			player.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < 24540)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			player.sendPacket(new ShowBoard(html.substring(8180, 16360), "102"));
			player.sendPacket(new ShowBoard(html.substring(16360, html.length()), "103"));
		}
	}
}
