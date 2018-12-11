package l2j.gameserver.handler.actionshift;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.DoorStatusUpdate;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author fissban
 */
public class DoorOnActionShift implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		if (player == null)
		{
			return false;
		}
		
		if (player.isGM())
		{
			player.setTarget(target);
			
			if (target.isAutoAttackable(player))
			{
				player.sendPacket(new DoorStatusUpdate((L2DoorInstance) target));
			}
			
			L2DoorInstance door = (L2DoorInstance) target;
			
			NpcHtmlMessage html = new NpcHtmlMessage(door.getObjectId());
			StringBuilder sb = new StringBuilder("<html><body>");
			sb.append("<center>");
			sb.append("<br>");
			sb.append("<table>");
			sb.append("<tr>");
			sb.append("<td><button value=\"Open\" action=\"bypass -h admin_open " + door.getId() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
			sb.append("<td><button value=\"Close\" action=\"bypass -h admin_close " + door.getId() + "\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
			sb.append("</tr>");
			sb.append("<tr>");// TODO borrar el kill q solo trae problemas-
			sb.append("<td><button value=\"Kill\" action=\"bypass -h \" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
			sb.append("<td><button value=\"Delete\" action=\"bypass -h admin_delete\" width=80 height=22 back=L2UI_CH3.Btn1_normal fore=L2UI_CH3.btn1_normal_over></td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br>");
			sb.append("<br>");
			sb.append("<font color=\"3399FF\">== [ Door Information ] ==</font>");
			sb.append("<br>");
			sb.append("<br>");
			sb.append("<table border=\"0\" width=\"100%\">");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Current HP:</font></td><td>" + door.getCurrentHp() + "</td>");
			sb.append("<td><font color=\"LEVEL\">Max HP:</font></td><td>" + door.getStat().getMaxHp() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">Object ID:</font></td><td>" + door.getObjectId() + "</td>");
			sb.append("<td><font color=\"LEVEL\">Door ID:</font></td><td>" + door.getId() + "</td>");
			sb.append("</tr>");
			sb.append("<tr>");
			sb.append("<td><font color=\"LEVEL\">isOpen</font></td><td>" + (door.isOpen() ? "true" : "false") + "</td>");
			sb.append("<td></td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</center>");
			sb.append("</body></html>");
			
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2DoorInstance;
	}
}
