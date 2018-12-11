package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * @author fissban, Reynald0 Comandos para la administracion de creacion de items.<br>
 *         Contiene el comando: <br>
 *         <li>create_item
 */
public class AdminCreateItem implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_create_item",
	};
	private static L2PcInstance target = null;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_create_item"))
		{
			if (st.countTokens() > 2)
			{
				activeChar.sendMessage("Correct command //create_item <itemId> <amount>");
			}
			else
			{
				target = AdminHelpTarget.getPlayer(activeChar);
				
				if (target != null)
				{
					try
					{
						int itemId = Integer.parseInt(st.nextToken());
						int amount = 1;
						
						if (st.hasMoreTokens())
						{
							amount = Integer.parseInt(st.nextToken());
						}
						
						createItem(activeChar, target, itemId, amount);
					}
					catch (Exception e)
					{
						activeChar.sendMessage("Invalid number");
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * @param activeChar : Character that executed the command
	 * @param target     : Player who receives items
	 * @param id         : Item id
	 * @param num        : Amount of items
	 */
	private static void createItem(L2PcInstance activeChar, L2PcInstance target, int id, int num)
	{
		String itemName = ItemData.getInstance().getTemplate(id).getName();
		
		if (num > 20)
		{
			Item template = ItemData.getInstance().getTemplate(id);
			if (!template.isStackable())
			{
				activeChar.sendMessage("This item does not stack - Creation aborted.");
				return;
			}
		}
		target.getInventory().addItem("Admin", id, num, target, null);
		target.sendPacket(new ItemList(target, true));
		
		if (activeChar != target)
		{
			target.sendMessage("An Admin has spawned " + num + " " + itemName + " in your inventory.");
		}
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<table width=260><tr>");
		sb.append("<td width=80><button value=\"Main\" action=\"bypass -h admin_admin\" width=80 height=22 back=L2UI_CH3.btn1_normal_down fore=L2UI_CH3.btn1_normal></td>");
		sb.append("<td width=100><center>Item Creation Menu</center></td>");
		sb.append("<td width=80><button value=\"Back\" action=\"bypass -h admin_help itemcreation.htm\" width=80 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
		sb.append("</tr></table>");
		sb.append("<br><br>");
		sb.append("<table width=270><tr><td>Item Creation Complete.<br></td></tr></table>");
		sb.append("<table width=270><tr><td>You have spawned " + num + " " + itemName + " (ID: " + id + ") in " + target.getName() + "'s inventory.</td></tr></table>");
		sb.append("</body></html>");
		
		adminReply.setHtml(sb.toString());
		activeChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
