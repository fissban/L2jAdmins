package l2j.gameserver.handler.actionshift;

import l2j.Config;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.drop.DropCategory;
import l2j.gameserver.model.drop.DropInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;

/**
 * @author fissban
 */
public class NpcOnActionShift implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		if (player == null)
		{
			return false;
		}
		
		L2Npc npc = ((L2Npc) target);
		
		// Check if the L2PcInstance is a GM
		if (player.isGM())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(target);
			
			// Check if the player is attackable (without a forced attack)
			if (target.isAutoAttackable(player))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(target.getObjectId());
				su.addAttribute(StatusUpdateType.CUR_HP, (int) ((L2Character) target).getCurrentHp());
				su.addAttribute(StatusUpdateType.MAX_HP, ((L2Character) target).getStat().getMaxHp());
				player.sendPacket(su);
			}
			
			// Send a Server->Client NpcHtmlMessage() containing the GM console about this L2NpcInstance
			NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
			
			StringBuilder sb = new StringBuilder("<html><body>");
			sb.append("<center>");
			sb.append("<br>");
			sb.append("<table border=\"0\" width=\"100%\">");
			sb.append("<tr>");
			sb.append("<td align=center><button value=\"Quest\" action=\"bypass -h admin_show_quests\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
			sb.append("<td align=center><button value=\"Kill\" action=\"bypass -h admin_kill\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
			sb.append("<td align=center><button value=\"Delete\" action=\"bypass -h admin_delete\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td align=center><button value=\"Edit NPC\" action=\"bypass -h admin_edit_npc " + npc.getTemplate().getId() + "\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal><br1></td>");
			sb.append("<td align=center><button value=\"DropList\" action=\"bypass -h admin_show_droplist " + npc.getTemplate().getId() + "\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
			sb.append("<td align=center><button value=\"SkillList\" action=\"bypass -h admin_show_skilllist_npc " + npc.getId() + "\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
			sb.append("</tr>");
			sb.append("<tr>");
			if (target.isMerchant())
			{
				sb.append("<td align=center></td>");
				sb.append("<td align=center><button value=\"ShopList\" action=\"bypass -h admin_showShop " + npc.getTemplate().getId() + "\" width=80 height=22 back=L2UI_CH3.btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
				sb.append("<td align=center></td>");
			}
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br>");
			sb.append("<font color=\"3399FF\">== [ Npc Information ] ==</font>");
			sb.append("<br>");
			sb.append("<table border=\"0\" width=\"100%\">");
			sb.append("<tr>");
			sb.append("<td align=center><img src=\"L2UI_CH3.questwndplusbtn_over\" width=16 height=16></td>");
			sb.append("<td><font color=\"LEVEL\">Type: </font></td>");
			sb.append("<td>" + npc.getClass().getSimpleName() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td align=center><img src=\"L2UI_CH3.questwndplusbtn_over\" width=16 height=16></td>");
			sb.append("<td><font color=\"LEVEL\">Faction: </font></td>");
			sb.append("<td>" + npc.getFactionId() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td align=center><img src=\"L2UI_CH3.questwndplusbtn_over\" width=16 height=16></td>");
			sb.append("<td><font color=\"LEVEL\">Location ID: </font></td>");
			sb.append("<td>" + (npc.getSpawn() != null ? npc.getSpawn().getSpawnLocation() : 0) + "</td>");
			sb.append("</tr>");
			sb.append("<tr><td align=center><img src=\"L2UI_CH3.questwndplusbtn_over\" width=16 height=16></td><td><font color=\"LEVEL\"> Respawn Time: </font></td><td>" + (npc.getSpawn() != null ? (npc.getSpawn().getRespawnDelay() / 1000) + "  Seconds</td></tr>" : "?  Seconds</td></tr>"));
			sb.append("</table>");
			sb.append("<br>");
			sb.append("<table border=\"0\" width=\"100%\">");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Object ID</font></td><td>" + npc.getObjectId() + "</td>");
			sb.append("<td><font color=\"LEVEL\">NPC ID</font></td><td>" + npc.getTemplate().getId() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Castle</font></td><td>" + (npc.getCastle() != null ? npc.getCastle().getId() : 0) + "</td>");
			sb.append("<td><font color=\"LEVEL\">Coords</font></td><td>" + target.getX() + "," + target.getY() + "," + target.getZ() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Level</font></td><td>" + npc.getLevel() + "</td>");
			sb.append("<td><font color=\"LEVEL\">Aggro</font></td><td>" + ((target instanceof L2Attackable) ? ((L2Attackable) target).getAggroRange() : 0) + "</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br>");
			sb.append("<font color=\"3399FF\">== [ Combat ] ==</font>");
			sb.append("<table border=\"0\" width=\"100%\">");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Max.HP</font></td><td>" + npc.getStat().getMaxHp() + "</td>");
			sb.append("<td><font color=\"LEVEL\">Max.MP</font></td><td>" + npc.getStat().getMaxMp() + "</td></tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">P.Atk.</font></td><td>" + npc.getStat().getPAtk(null) + "</td>");
			sb.append("<td><font color=\"LEVEL\">M.Atk.</font></td><td>" + npc.getStat().getMAtk(null, null) + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">P.Def.</font></td><td>" + npc.getStat().getPDef(null) + "</td>");
			sb.append("<td><font color=\"LEVEL\">M.Def.</font></td><td>" + npc.getStat().getMDef(null, null) + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Accuracy</font></td><td>" + npc.getStat().getAccuracy() + "</td>");
			sb.append("<td><font color=\"LEVEL\">Evasion</font></td><td>" + npc.getStat().getEvasionRate(null) + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Critical</font></td><td>" + npc.getStat().getCriticalHit(null, null) + "</td>");
			sb.append("<td><font color=\"LEVEL\">Speed</font></td><td>" + npc.getStat().getRunSpeed() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Atk.Speed</font></td><td>" + npc.getStat().getPAtkSpd() + "</td>");
			sb.append("<td><font color=\"LEVEL\">Cast.Speed</font></td><td>" + npc.getStat().getMAtkSpd() + "</td>");
			sb.append("</tr>");
			sb.append("</table><br>");
			
			sb.append("<font color=\"3399FF\">== [ Basic Stats ] ==</font>");
			sb.append("<table border=\"0\" width=\"100%\">");
			sb.append("<tr><td><font color=\"LEVEL\">STR</font></td><td>" + npc.getStat().getSTR() + "</td><td><font color=\"LEVEL\">DEX</font></td><td>" + npc.getStat().getDEX() + "</td><td><font color=\"LEVEL\">CON</font></td><td>" + npc.getStat().getCON() + "</td></tr>");
			sb.append("<tr><td><font color=\"LEVEL\">INT</font></td><td>" + npc.getStat().getINT() + "</td><td><font color=\"LEVEL\">WIT</font></td><td>" + npc.getStat().getWIT() + "</td><td><font color=\"LEVEL\">MEN</font></td><td>" + npc.getStat().getMEN() + "</td></tr>");
			sb.append("</table>");
			
			sb.append("<br>");
			sb.append("</center>");
			sb.append("</body></html>");
			
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		else if (Config.ALT_GAME_VIEWNPC)
		{
			// Set the target of the L2PcInstance player
			player.setTarget(target);
			
			// Check if the player is attackable (without a forced attack)
			if (target.isAutoAttackable(player))
			{
				// Send a Server->Client packet StatusUpdate of the L2NpcInstance to the L2PcInstance to update its HP bar
				StatusUpdate su = new StatusUpdate(target.getObjectId());
				su.addAttribute(StatusUpdateType.CUR_HP, (int) npc.getCurrentHp());
				su.addAttribute(StatusUpdateType.MAX_HP, npc.getStat().getMaxHp());
				player.sendPacket(su);
			}
			
			NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
			StringBuilder sb = new StringBuilder("<html><body>");
			
			sb.append("<br><center><font color=LEVEL>== [ Combat Stats ] ==</font></center>");
			sb.append("<table border=0 width=100%>");
			sb.append("<tr><td>Max.HP</td><td>" + (int) (npc.getStat().getMaxHp() / npc.getStat().calcStat(StatsType.MAX_HP, 1, (L2Npc) target, null)) + "*" + (int) npc.getStat().calcStat(StatsType.MAX_HP, 1, (L2Npc) target, null) + "</td><td>Max.MP</td><td>" + npc.getStat().getMaxMp()
				+ "</td></tr>");
			sb.append("<tr><td>P.Atk.</td><td>" + npc.getStat().getPAtk(null) + "</td><td>M.Atk.</td><td>" + npc.getStat().getMAtk(null, null) + "</td></tr>");
			sb.append("<tr><td>P.Def.</td><td>" + npc.getStat().getPDef(null) + "</td><td>M.Def.</td><td>" + npc.getStat().getMDef(null, null) + "</td></tr>");
			sb.append("<tr><td>Accuracy</td><td>" + npc.getStat().getAccuracy() + "</td><td>Evasion</td><td>" + npc.getStat().getEvasionRate(null) + "</td></tr>");
			sb.append("<tr><td>Critical</td><td>" + npc.getStat().getCriticalHit(null, null) + "</td><td>Speed</td><td>" + npc.getStat().getRunSpeed() + "</td></tr>");
			sb.append("<tr><td>Atk.Speed</td><td>" + npc.getStat().getPAtkSpd() + "</td><td>Cast.Speed</td><td>" + npc.getStat().getMAtkSpd() + "</td></tr>");
			sb.append("<tr><td>Race</td><td>" + npc.getTemplate().getRace() + "</td><td></td><td></td></tr>");
			sb.append("</table>");
			
			sb.append("<br><center><font color=LEVEL>== [ Basic Stats ] ==</font></center>");
			sb.append("<table border=0 width=\"100%\">");
			sb.append("<tr><td>STR</td><td>" + npc.getStat().getSTR() + "</td><td>DEX</td><td>" + npc.getStat().getDEX() + "</td><td>CON</td><td>" + npc.getStat().getCON() + "</td></tr>");
			sb.append("<tr><td>INT</td><td>" + npc.getStat().getINT() + "</td><td>WIT</td><td>" + npc.getStat().getWIT() + "</td><td>MEN</td><td>" + npc.getStat().getMEN() + "</td></tr>");
			sb.append("</table>");
			
			sb.append("<br><center><font color=LEVEL>== [ Drop Info ] ==</font></center>");
			sb.append("Rates legend: <font color=ff0000>50%+</font> <font color=00ff00>30%+</font> <font color=0000ff>less than 30%</font>");
			sb.append("<table border=0 width=100%>");
			
			for (DropCategory cat : npc.getTemplate().getDropsCategory())
			{
				for (DropInstance drop : cat.getAllDrops())
				{
					String name = ItemData.getInstance().getTemplate(drop.getItemId()).getName();
					
					if (drop.getChance() >= 600000)
					{
						sb.append("<tr><td><font color=ff0000>" + name + "</font></td><td>" + ((cat.isSweep() ? "Sweep" : "Drop")) + "</td></tr>");
					}
					else if (drop.getChance() >= 300000)
					{
						sb.append("<tr><td><font color=00ff00>" + name + "</font></td><td>" + ((cat.isSweep() ? "Sweep" : "Drop")) + "</td></tr>");
					}
					else
					{
						sb.append("<tr><td><font color=0000ff>" + name + "</font></td><td>" + ((cat.isSweep() ? "Sweep" : "Drop")) + "</td></tr>");
					}
				}
			}
			
			sb.append("</table>");
			sb.append("</body></html>");
			
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Npc;
	}
}
