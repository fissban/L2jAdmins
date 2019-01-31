package l2j.gameserver.scripts.ai.npc;

import java.text.DateFormat;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.Lottery;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * 0 - first buy lottery ticket window <br>
 * 1-20 - buttons <br>
 * 21 - second buy lottery ticket window <br>
 * 22 - selected ticket with 5 numbers <br>
 * 23 - current lottery jackpot <br>
 * 24 - Previous winning numbers/Prize claim <br>
 * >24 - check lottery ticket by item object id <br>
 * Original code in L2Npc <br>
 * @author fissban
 */
public class LoteryTicketSeller extends Script
{
	private static final int[] NPCS =
	{
		7990,
		7991,
		7992,
		7993,
		7994
	};
	
	public LoteryTicketSeller()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		st.nextToken();// actual command
		
		if (event.startsWith("Loto"))
		{
			int val = 0;
			
			if (st.hasMoreTokens())
			{
				val = Integer.parseInt(st.nextToken());
			}
			
			if (val == 0)
			{
				// new loto ticket
				for (int i = 0; i < 5; i++)
				{
					player.setLoto(i, 0);
				}
			}
			
			String filename;
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			
			if (val == 0) // 0 - first buy lottery ticket window
			{
				html.setFile(npc.getHtmlPath(npc.getId(), 1));
			}
			else if ((val >= 1) && (val <= 21)) // 1-20 - buttons, 21 - second buy lottery ticket window
			{
				if (!Lottery.getInstance().isStarted())
				{
					// tickets can't be sold
					player.sendPacket(SystemMessage.NO_LOTTERY_TICKETS_CURRENT_SOLD);
					return null;
				}
				if (!Lottery.getInstance().isSellableTickets())
				{
					// tickets can't be sold
					player.sendPacket(SystemMessage.NO_LOTTERY_TICKETS_AVAILABLE);
					return null;
				}
				
				html.setFile(npc.getHtmlPath(npc.getId(), 5));
				
				int count = 0;
				int found = 0;
				// counting buttons and unsetting button if found
				for (int i = 0; i < 5; i++)
				{
					if (player.getLoto(i) == val)
					{
						// unsetting button
						player.setLoto(i, 0);
						found = 1;
					}
					else if (player.getLoto(i) > 0)
					{
						count++;
					}
				}
				
				// if not reached limit 5 and not unseted value
				if ((count < 5) && (found == 0) && (val <= 20))
				{
					for (int i = 0; i < 5; i++)
					{
						if (player.getLoto(i) == 0)
						{
							player.setLoto(i, val);
							break;
						}
					}
				}
				
				// setting pushed buttons
				count = 0;
				for (int i = 0; i < 5; i++)
				{
					if (player.getLoto(i) > 0)
					{
						count++;
						String button = String.valueOf(player.getLoto(i));
						if (player.getLoto(i) < 10)
						{
							button = "0" + button;
						}
						String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
						String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
						html.replace(search, replace);
					}
				}
				
				if (count == 5)
				{
					String search = "0\">Return";
					String replace = "22\">The winner selected the numbers above.";
					html.replace(search, replace);
				}
			}
			else if (val == 22) // 22 - selected ticket with 5 numbers
			{
				if (!Lottery.getInstance().isStarted())
				{
					// tickets can't be sold
					player.sendPacket(SystemMessage.NO_LOTTERY_TICKETS_CURRENT_SOLD);
					return null;
				}
				if (!Lottery.getInstance().isSellableTickets())
				{
					// tickets can't be sold
					player.sendPacket(SystemMessage.NO_LOTTERY_TICKETS_AVAILABLE);
					return null;
				}
				
				int price = Config.ALT_LOTTERY_TICKET_PRICE;
				int lotonumber = Lottery.getInstance().getId();
				int enchant = 0;
				int type2 = 0;
				
				for (int i = 0; i < 5; i++)
				{
					if (player.getLoto(i) == 0)
					{
						return null;
					}
					
					if (player.getLoto(i) < 17)
					{
						enchant += Math.pow(2, player.getLoto(i) - 1);
					}
					else
					{
						type2 += Math.pow(2, player.getLoto(i) - 17);
					}
				}
				
				if (player.getInventory().getAdena() < price)
				{
					player.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
					return null;
				}
				if (!player.getInventory().reduceAdena("Loto", price, npc, true))
				{
					return null;
				}
				
				Lottery.getInstance().increasePrize(price);
				
				player.sendPacket(new SystemMessage(SystemMessage.ACQUIRED_S1_S2).addNumber(lotonumber).addItemName(4442));
				
				ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), 4442);
				item.setCount(1);
				item.setCustomType1(lotonumber);
				item.setEnchantLevel(enchant);
				item.setCustomType2(type2);
				player.getInventory().addItem("Loto", item, player, npc);
				
				filename = (npc.getHtmlPath(npc.getId(), 3));
				html.setFile(filename);
			}
			else if (val == 23) // 23 - current lottery jackpot
			{
				filename = (npc.getHtmlPath(npc.getId(), 3));
				html.setFile(filename);
			}
			else if (val == 24) // 24 - Previous winning numbers/Prize claim
			{
				filename = (npc.getHtmlPath(npc.getId(), 4));
				html.setFile(filename);
				
				int lotonumber = Lottery.getInstance().getId();
				String message = "";
				for (ItemInstance item : player.getInventory().getItems())
				{
					if (item == null)
					{
						continue;
					}
					if ((item.getId() == 4442) && (item.getCustomType1() < lotonumber))
					{
						message = message + "<a action=\"bypass -h npc_%objectId%_Loto " + item.getObjectId() + "\">" + item.getCustomType1() + " Event Number ";
						int[] numbers = Lottery.getInstance().decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
						for (int i = 0; i < 5; i++)
						{
							message += numbers[i] + " ";
						}
						int[] check = Lottery.getInstance().checkTicket(item);
						if (check[0] > 0)
						{
							switch (check[0])
							{
								case 1:
									message += "- 1st Prize";
									break;
								case 2:
									message += "- 2nd Prize";
									break;
								case 3:
									message += "- 3th Prize";
									break;
								case 4:
									message += "- 4th Prize";
									break;
							}
							message += " " + check[1] + "a.";
						}
						message += "</a><br>";
					}
				}
				if (message.isEmpty())
				{
					message += "There is no winning lottery ticket...<br>";
				}
				html.replace("%result%", message);
			}
			else if (val > 24) // >24 - check lottery ticket by item object id
			{
				int lotonumber = Lottery.getInstance().getId();
				ItemInstance item = player.getInventory().getItemByObjectId(val);
				
				if ((item == null) || (item.getId() != 4442) || (item.getCustomType1() >= lotonumber))
				{
					return null;
				}
				
				int[] check = Lottery.getInstance().checkTicket(item);
				
				player.sendPacket(new SystemMessage(SystemMessage.S2_S1_DISAPPEARED).addItemName(4442));
				
				int adena = check[1];
				if (adena > 0)
				{
					player.getInventory().addAdena("Loto", adena, npc, true);
				}
				player.getInventory().destroyItem("Loto", item, npc, false);
				return null;
			}
			html.replace("%objectId%", String.valueOf(npc.getObjectId()));
			html.replace("%race%", "" + Lottery.getInstance().getId());
			html.replace("%adena%", "" + Lottery.getInstance().getPrize());
			html.replace("%ticket_price%", "" + Config.ALT_LOTTERY_TICKET_PRICE);
			html.replace("%prize5%", "" + (Config.ALT_LOTTERY_5_NUMBER_RATE * 100));
			html.replace("%prize4%", "" + (Config.ALT_LOTTERY_4_NUMBER_RATE * 100));
			html.replace("%prize3%", "" + (Config.ALT_LOTTERY_3_NUMBER_RATE * 100));
			html.replace("%prize2%", "" + Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE);
			html.replace("%enddate%", "" + DateFormat.getDateInstance().format(Lottery.getInstance().getEndDate()));
			player.sendPacket(html);
			// Send a Server->Client packet ActionFailed to the L2PcInstance
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		
		return null;
	}
}
