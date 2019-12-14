package l2j.gameserver.model.actor.instance;

import java.util.List;
import java.util.Locale;

import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.race.MonsterRace;
import l2j.gameserver.instancemanager.race.MonsterRaceHistoryInfo;
import l2j.gameserver.instancemanager.race.MonsterRaceState;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.RaceManagerKnownList;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;

public class L2RaceManagerInstance extends L2Npc
{
	protected static final int TICKET_PRICES[] =
	{
		100,
		500,
		1000,
		5000,
		10000,
		20000,
		50000,
		100000
	};
	
	public L2RaceManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2RaceManagerInstance);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new RaceManagerKnownList(this));
	}
	
	@Override
	public final RaceManagerKnownList getKnownList()
	{
		return (RaceManagerKnownList) super.getKnownList();
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("BuyTicket"))
		{
			if (MonsterRace.getInstance().getCurrentRaceState() != MonsterRaceState.ACCEPTING_BETS)
			{
				player.sendPacket(SystemMessage.MONSRACE_TICKETS_NOT_AVAILABLE);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			int val = Integer.parseInt(command.substring(10));
			if (val == 0)
			{
				player.setRace(0, 0);
				player.setRace(1, 0);
			}
			
			if (((val == 10) && (player.getRace(0) == 0)) || ((val == 20) && (player.getRace(0) == 0) && (player.getRace(1) == 0)))
			{
				val = 0;
			}
			
			int npcId = getTemplate().getId();
			String search, replace;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (val < 10)
			{
				html.setFile(getHtmlPath(npcId, 2));
				for (int i = 0; i < 8; i++)
				{
					int n = i + 1;
					search = "Mob" + n;
					html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
				}
				search = "No1";
				if (val == 0)
				{
					html.replace(search, "");
				}
				else
				{
					html.replace(search, val);
					player.setRace(0, val);
				}
			}
			else if (val < 20)
			{
				if (player.getRace(0) == 0)
				{
					return;
				}
				
				html.setFile(getHtmlPath(npcId, 3));
				html.replace("0place", player.getRace(0));
				search = "Mob1";
				replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().getName();
				html.replace(search, replace);
				search = "0adena";
				
				if (val == 10)
				{
					html.replace(search, "");
				}
				else
				{
					html.replace(search, TICKET_PRICES[val - 11]);
					player.setRace(1, val - 10);
				}
			}
			else if (val == 20)
			{
				if ((player.getRace(0) == 0) || (player.getRace(1) == 0))
				{
					return;
				}
				
				html.setFile(getHtmlPath(npcId, 4));
				html.replace("0place", player.getRace(0));
				search = "Mob1";
				replace = MonsterRace.getInstance().getMonsters()[player.getRace(0) - 1].getTemplate().getName();
				html.replace(search, replace);
				search = "0adena";
				int price = TICKET_PRICES[player.getRace(1) - 1];
				html.replace(search, price);
				search = "0tax";
				int tax = 0;
				html.replace(search, tax);
				search = "0total";
				int total = price + tax;
				html.replace(search, total);
			}
			else
			{
				if ((player.getRace(0) == 0) || (player.getRace(1) == 0))
				{
					return;
				}
				
				int ticket = player.getRace(0);
				int priceId = player.getRace(1);
				
				if (!player.getInventory().reduceAdena("Race", TICKET_PRICES[priceId - 1], this, true))
				{
					return;
				}
				
				player.setRace(0, 0);
				player.setRace(1, 0);
				
				ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), 4443);
				item.setCount(1);
				item.setEnchantLevel(MonsterRace.getInstance().getRaceNumber());
				item.setCustomType1(ticket);
				item.setCustomType2(TICKET_PRICES[priceId - 1] / 100);
				
				player.getInventory().addItem("Race", item, player, false);
				player.sendPacket(new SystemMessage(SystemMessage.ACQUIRED_S1_S2).addNumber(MonsterRace.getInstance().getRaceNumber()).addItemName(4443));
				
				// Refresh lane bet.
				MonsterRace.setBetOnLane(ticket, TICKET_PRICES[priceId - 1], true);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			html.replace("1race", MonsterRace.getInstance().getRaceNumber());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowOdds"))
		{
			if (MonsterRace.getInstance().getCurrentRaceState() == MonsterRaceState.ACCEPTING_BETS)
			{
				player.sendPacket(SystemMessage.MONSRACE_NO_PAYOUT_INFO);
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(getHtmlPath(getTemplate().getId(), 5));
			for (int i = 0; i < 8; i++)
			{
				final int n = i + 1;
				
				html.replace("Mob" + n, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
				
				// Odd
				final double odd = MonsterRace.getInstance().getOdds().get(i);
				html.replace("Odd" + n, (odd > 0D) ? String.format(Locale.ENGLISH, "%.1f", odd) : "&$804;");
			}
			html.replace("1race", MonsterRace.getInstance().getRaceNumber());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowInfo"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(getHtmlPath(getTemplate().getId(), 6));
			
			for (int i = 0; i < 8; i++)
			{
				int n = i + 1;
				String search = "Mob" + n;
				html.replace(search, MonsterRace.getInstance().getMonsters()[i].getTemplate().getName());
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.equals("ShowTickets"))
		{
			// Generate data.
			final StringBuilder sb = new StringBuilder();
			
			// Retrieve player's tickets.
			for (ItemInstance ticket : player.getInventory().getAllItemsByItemId(4443))
			{
				// Don't list current race tickets.
				if (ticket.getEnchantLevel() == MonsterRace.getInstance().getRaceNumber())
				{
					continue;
				}
				sb.append("<tr><td><a action=\"bypass -h npc_%objectId%_ShowTicket ");
				sb.append(ticket.getObjectId());
				sb.append("\">");
				sb.append(ticket.getEnchantLevel());
				sb.append(" Race Number</a></td><td align=right><font color=\"LEVEL\">");
				sb.append(ticket.getCustomType1());
				sb.append("</font> Number</td><td align=right><font color=\"LEVEL\">");
				sb.append(ticket.getCustomType2() * 100);
				sb.append("</font> Adena</td></tr>");
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(getHtmlPath(getTemplate().getId(), 7));
			html.replace("%tickets%", sb.toString());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("ShowTicket"))
		{
			// Retrieve ticket objectId.
			final int val = Integer.parseInt(command.substring(11));
			if (val == 0)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Retrieve ticket on player's inventory.
			final ItemInstance ticket = player.getInventory().getItemByObjectId(val);
			if (ticket == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final int raceId = ticket.getEnchantLevel();
			final int lane = ticket.getCustomType1();
			final int bet = ticket.getCustomType2() * 100;
			
			// Retrieve HistoryInfo for that race.
			final MonsterRaceHistoryInfo info = MonsterRace.getInstance().getHistory().get(raceId - 1);
			if (info == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(getHtmlPath(getTemplate().getId(), 8));
			html.replace("%raceId%", raceId);
			html.replace("%lane%", lane);
			html.replace("%bet%", bet);
			html.replace("%firstLane%", info.getFirst());
			html.replace("%odd%", (lane == info.getFirst()) ? String.format(Locale.ENGLISH, "%.2f", info.getOddRate()) : "0.01");
			html.replace("%objectId%", getObjectId());
			html.replace("%ticketObjectId%", val);
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (command.startsWith("CalculateWin"))
		{
			// Retrieve ticket objectId.
			final int val = Integer.parseInt(command.substring(13));
			if (val == 0)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Delete ticket on player's inventory.
			final ItemInstance ticket = player.getInventory().getItemByObjectId(val);
			if (ticket == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			final int raceId = ticket.getEnchantLevel();
			final int lane = ticket.getCustomType1();
			final int bet = ticket.getCustomType2() * 100;
			
			// Retrieve HistoryInfo for that race.
			final MonsterRaceHistoryInfo info = MonsterRace.getInstance().getHistory().get(raceId - 1);
			if (info == null)
			{
				super.onBypassFeedback(player, "Chat 0");
				return;
			}
			
			// Destroy the ticket.
			if (player.getInventory().destroyItem("MonsterTrack", ticket, this, true))
			{
				player.getInventory().addAdena("MonsterTrack", (int) (bet * ((lane == info.getFirst()) ? info.getOddRate() : 0.01)), this, true);
			}
			
			super.onBypassFeedback(player, "Chat 0");
			return;
		}
		else if (command.equals("ViewHistory"))
		{
			// Generate data.
			final StringBuilder sb = new StringBuilder();
			
			// Use whole history, pickup from 'last element' and stop at 'latest element - 7'.
			final List<MonsterRaceHistoryInfo> history = MonsterRace.getInstance().getHistory();
			for (int i = history.size() - 1; i >= Math.max(0, history.size() - 7); i--)
			{
				final MonsterRaceHistoryInfo info = history.get(i);
				sb.append("<tr><td><font color=\"LEVEL\">");
				sb.append(info.getRaceId());
				sb.append("</font> th</td><td><font color=\"LEVEL\">");
				sb.append(info.getFirst());
				sb.append("</font> Lane </td><td><font color=\"LEVEL\">");
				sb.append(info.getSecond());
				sb.append("</font> Lane</td><td align=right><font color=00ffff>");
				sb.append(String.format(Locale.ENGLISH, "%.2f", info.getOddRate()));
				sb.append("</font> Times</td></tr>");
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(getHtmlPath(getTemplate().getId(), 9));
			html.replace("%infos%", sb.toString());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
