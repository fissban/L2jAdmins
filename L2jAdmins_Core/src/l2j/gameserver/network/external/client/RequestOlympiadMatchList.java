package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.OlympiadGameManager;
import l2j.gameserver.model.olympiad.OlympiadGameTask;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * format ch c: (id) 0xD0 h: (subid) 0x13
 * @author -Wooden-
 */
public class RequestOlympiadMatchList extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.inObserverMode())
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			StringBuilder sb = new StringBuilder("<html><body>");
			
			sb.append("Grand Olympiad Games Overview<br><br>* Note: Keep in mind that once you click Return button, you will leave Olympiad observer mode, and be teleported back to town.<br>");
			
			sb.append("<table width=270 border=0 bgcolor=000000>");
			sb.append("<tr>");
			sb.append("<td fixwidth=10>N.</td>");
			sb.append("<td fixwidth=80>Status</td>");
			sb.append("<td>Player1 / Player2</td>");
			sb.append("</tr>");
			
			for (int i = 0; i <= 21; i++)
			{
				OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(i);
				if (task != null)
				{
					sb.append("<tr><td fixwidth=10><a action=\"bypass -h OlympiadArenaChange " + i + "\">" + (i + 1) + "</a></td><td fixwidth=80>");
					if (task.isGameStarted())
					{
						if (task.isInTimerTime())
						{
							sb.append("&$907;"); // Counting In Progress
						}
						else if (task.isBattleStarted())
						{
							sb.append("&$829;"); // In Progress
						}
						else
						{
							sb.append("&$908;"); // Terminate
						}
						
						sb.append("</td><td>" + task.getGame().getPlayerNames()[0] + "&nbsp; / &nbsp;" + task.getGame().getPlayerNames()[1]);
					}
					else
					{
						// Initial State
						sb.append("&$906;</td><td>&nbsp;");
					}
					
				}
			}
			sb.append("</td><td><font color=aaccff></font></td></tr></table>");
			sb.append("</body></html>");
			
			html.setHtml(sb.toString());
			activeChar.sendPacket(html);
		}
	}
}
