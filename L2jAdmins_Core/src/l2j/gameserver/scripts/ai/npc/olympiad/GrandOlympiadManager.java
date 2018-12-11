package l2j.gameserver.scripts.ai.npc.olympiad;

import java.util.List;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.MultisellData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.olympiad.CompetitionType;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.model.olympiad.OlympiadGameManager;
import l2j.gameserver.model.olympiad.OlympiadGameTask;
import l2j.gameserver.model.olympiad.OlympiadManager;
import l2j.gameserver.network.external.server.ExHeroList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.Script;

/**
 * Original code in phyton by godson
 * @author fissban
 */
public class GrandOlympiadManager extends Script
{
	// Npc
	private static final int NPC = 8688;
	// Item
	private static final int GATE_PASS = 6651;
	// Html
	private static final String HTML_PATH = "data/html/olympiad/";
	
	public GrandOlympiadManager()
	{
		super(-1, "ai/npc/olympiad");
		
		addStartNpc(NPC);
		addFirstTalkId(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.isNoble())
		{
			return HTML_PATH + "noble_si.htm";
		}
		
		return "data/html/default/8688.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		event = st.nextToken(); // actual command
		
		StringBuilder sb = new StringBuilder();
		
		switch (event)
		{
			case "Main":
			{
				if (player.isNoble())
				{
					return HTML_PATH + "noble_si.htm";
				}
				
				return "data/html/default/8688.htm";
			}
			case "OlympiadDesc":
			{
				String suffix = st.nextToken();
				
				if (suffix.equals("0"))
				{
					return HTML_PATH + "noble_si.htm";
				}
				
				return HTML_PATH + "noble_desc" + suffix + ".htm";
			}
			case "OlympiadNoble":
			{
				if (!player.isNoble() || (player.getClassId().getId() < 88))
				{
					return null;
				}
				
				int val = Integer.parseInt(st.nextToken());
				
				NpcHtmlMessage reply = new NpcHtmlMessage(npc.getObjectId());
				
				switch (val)
				{
					case 1:
					{
						OlympiadManager.getInstance().unRegisterNoble(player);
						break;
					}
					case 2:
					{
						final int nonClassed = OlympiadManager.getInstance().getRegisteredNonClassBased().size();
						final int classed = OlympiadManager.getInstance().getRegisteredClassBased().size();
						
						reply.setFile(HTML_PATH + "noble_waiting_list.htm");
						reply.replace("%classed%", classed);
						reply.replace("%nonClassed%", nonClassed);
						player.sendPacket(reply);
						break;
					}
					case 3:
					{
						int points = Olympiad.getInstance().getNoblePoints(player.getObjectId());
						if (points >= 0)
						{
							reply.setFile(HTML_PATH + "noble_points.htm");
							reply.replace("%points%", points);
							player.sendPacket(reply);
						}
						break;
					}
					case 4:
					{
						OlympiadManager.getInstance().registerNoble(player, CompetitionType.NON_CLASSED);
						break;
					}
					case 5:
					{
						OlympiadManager.getInstance().registerNoble(player, CompetitionType.CLASSED);
						break;
					}
					case 6:
					{
						int points = Olympiad.getInstance().getNoblessePointEom(player, false);
						
						if (points == 0)
						{
							return HTML_PATH + "olympiad_no_have_enough_points.htm";
						}
						else if (points < 49)
						{
							if (!player.isHero())
							{
								return HTML_PATH + "olympiad_no_have_enough_points.htm";
							}
						}
						
						return HTML_PATH + "olympiad_have_points.htm";
					}
					case 7:
					{
						MultisellData.getInstance().createMultiSell(102, player, false, npc);
						break;
					}
					case 8:
					{
						int points = Olympiad.getInstance().getNoblessePointEom(player, true);
						// give noble passes
						if (points > 49)
						{
							points += (player.isHero() ? Config.ALT_OLY_HERO_POINTS : 0);
							
							points *= Config.ALT_OLY_GP_PER_POINT;
							
							player.getInventory().addItem("Olympiad", GATE_PASS, points, npc, true);
						}
						break;
					}
				}
				break;
			}
			case "Olympiad":
			{
				int val = Integer.parseInt(st.nextToken());
				
				switch (val)
				{
					case 1:
					{
						NpcHtmlMessage reply = new NpcHtmlMessage(npc.getObjectId());
						reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "olympiad_observe_list.htm");
						
						int i = 0;
						
						sb = new StringBuilder(2000);
						for (OlympiadGameTask task : OlympiadGameManager.getInstance().getOlympiadTasks())
						{
							sb.append("<a action=\"bypass arenachange " + i + "\">Arena " + (++i) + "&nbsp;");
							
							if (task.isGameStarted())
							{
								if (task.isInTimerTime())
								{
									sb.append("(&$907;)"); // Counting In Progress
								}
								else if (task.isBattleStarted())
								{
									sb.append("(&$829;)"); // In Progress
								}
								else
								{
									sb.append("(&$908;)"); // Terminate
								}
								
								sb.append("&nbsp;" + task.getGame().getPlayerNames()[0] + "&nbsp; : &nbsp;" + task.getGame().getPlayerNames()[1]);
							}
							else
							{
								sb.append("(&$906;)</td><td>&nbsp;"); // Initial State
							}
							
							sb.append("</a><br>");
						}
						reply.replace("%list%", sb.toString());
						reply.replace("%objectId%", npc.getObjectId());
						player.sendPacket(reply);
						break;
					}
					case 2:
					{
						int classId = Integer.parseInt(st.nextToken());
						if (classId >= 88)
						{
							sb.append("<html><body>");
							sb.append("<center>Grand Olympiad Ranking");
							sb.append("<img src=L2UI.SquareWhite width=270 height=1><img src=L2UI.SquareBlank width=1 height=3>");
							
							List<String> names = Olympiad.getInstance().getClassLeaderBoard(classId);
							if (names.size() != 0)
							{
								sb.append("<table width=270 border=0 bgcolor=111111>");
								
								int index = 1;
								
								for (String name : names)
								{
									sb.append("<tr>");
									sb.append("<td fixwidth=135 align=center>" + index + "</td>");
									sb.append("<td fixwidth=135 align=center>" + name + "</td>");
									sb.append("</tr>");
									index++;
								}
								
								sb.append("</table>");
							}
							
							sb.append("<img src=L2UI.SquareWhite width=270 height=1><img src=L2UI.SquareBlank width=1 height=3>");
							sb.append("</center>");
							sb.append("</body></html>");
							
							NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
							html.setHtml(sb.toString());
							player.sendPacket(html);
						}
						break;
					}
					case 3:
					{
						// FIXME not implemented yet!
						player.sendMessage("not implemented yet!");
						// int id = Integer.parseInt(st.nextToken());
						// OlympiadManager.getInstance().addSpectator(id, player, true);
						break;
					}
					case 4:
					{
						player.sendPacket(new ExHeroList());
						break;
					}
				}
				break;
			}
		}
		
		return null;
	}
}
