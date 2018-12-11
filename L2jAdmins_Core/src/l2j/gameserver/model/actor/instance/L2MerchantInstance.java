package l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SellList;
import l2j.gameserver.network.external.server.SetupGauge;
import l2j.gameserver.network.external.server.SetupGauge.SetupGaugeType;
import l2j.gameserver.network.external.server.WearList;

/**
 * This class ...
 * @version $Revision: 1.10.4.9 $ $Date: 2005/04/11 10:06:08 $
 */
public class L2MerchantInstance extends L2Npc
{
	/**
	 * @param objectId
	 * @param template
	 */
	public L2MerchantInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2MerchantInstance);
	}
	
	@Override
	public boolean isMerchant()
	{
		return true;
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/merchant/" + pom + ".htm";
	}
	
	private void showWearWindow(L2PcInstance player, int val)
	{
		player.tempInventoryDisable();
		
		if (Config.DEBUG)
		{
			LOG.fine("Showing wearlist");
		}
		
		MerchantTradeList list = TradeControllerData.getInstance().getBuyList(val);
		
		if (list != null)
		{
			player.sendPacket(new WearList(list, player.getInventory().getAdena(), player.getExpertiseIndex()));
		}
		else
		{
			LOG.warning("no buylist with id:" + val);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	private void showSellWindow(L2PcInstance player)
	{
		if (Config.DEBUG)
		{
			LOG.fine("Showing selllist");
		}
		
		player.sendPacket(new SellList(player));
		
		if (Config.DEBUG)
		{
			LOG.fine("Showing sell window");
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("Buy"))
		{
			if (st.countTokens() < 1)
			{
				return;
			}
			
			showBuyWindow(player, Integer.parseInt(st.nextToken()));
		}
		else if (actualCommand.equalsIgnoreCase("Sell"))
		{
			showSellWindow(player);
		}
		else if (actualCommand.equalsIgnoreCase("RentPet"))
		{
			if (Config.ALLOW_RENTPET)
			{
				if (st.countTokens() < 1)
				{
					showRentPetWindow(player);
				}
				else
				{
					tryRentPet(player, Integer.parseInt(st.nextToken()));
				}
			}
		}
		else if (actualCommand.equalsIgnoreCase("Wear") && Config.ALLOW_WEAR)
		{
			if (st.countTokens() < 1)
			{
				return;
			}
			
			showWearWindow(player, Integer.parseInt(st.nextToken()));
		}
		else
		{
			// this class dont know any other commands, let forward
			// the command to the parent class
			
			super.onBypassFeedback(player, command);
		}
	}
	
	// TODO hardcode..podriamos pasarlo a un html
	public void showRentPetWindow(L2PcInstance player)
	{
		if (!Config.LIST_PET_RENT_NPC.contains(getId()))
		{
			return;
		}
		
		StringBuilder sb = new StringBuilder("<html><body>Pet Manager:<br>");
		sb.append("You can rent a wyvern or strider for adena.<br>My prices:<br1>");
		sb.append("<table border=0><tr><td>Ride</td></tr>");
		sb.append("<tr><td>Wyvern</td><td>Strider</td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_%objectId%_RentPet 1\">30 sec/1800 adena</a></td><td><a action=\"bypass -h npc_%objectId%_RentPet 11\">30 sec/900 adena</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_%objectId%_RentPet 2\">1 min/7200 adena</a></td><td><a action=\"bypass -h npc_%objectId%_RentPet 12\">1 min/3600 adena</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_%objectId%_RentPet 3\">10 min/720000 adena</a></td><td><a action=\"bypass -h npc_%objectId%_RentPet 13\">10 min/360000 adena</a></td></tr>");
		sb.append("<tr><td><a action=\"bypass -h npc_%objectId%_RentPet 4\">30 min/6480000 adena</a></td><td><a action=\"bypass -h npc_%objectId%_RentPet 14\">30 min/3240000 adena</a></td></tr>");
		sb.append("</table>");
		sb.append("</body></html>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setHtml(sb.toString());
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
	
	private static void tryRentPet(L2PcInstance player, int val)
	{
		if ((player == null) || (player.getPet() != null) || player.isMounted() || player.isRentedPet())
		{
			return;
		}
		
		if (!player.disarmWeapons())
		{
			return;
		}
		
		player.abortCast();
		
		int petId;
		double price = 1;
		int cost[] =
		{
			1800,
			7200,
			720000,
			6480000
		};
		int ridetime[] =
		{
			30,
			60,
			600,
			1800
		};
		
		if (val > 10)
		{
			petId = 12526;
			val -= 10;
			price /= 2;
		}
		else
		{
			petId = 12621;
		}
		
		if ((val < 1) || (val > 4))
		{
			return;
		}
		
		price *= cost[val - 1];
		int time = ridetime[val - 1];
		
		if (!player.getInventory().reduceAdena("Rent", (int) price, player.getLastTalkNpc(), true))
		{
			return;
		}
		
		player.mount(petId, 0, false);
		player.sendPacket(new SetupGauge(SetupGaugeType.GREEN, time * 1000));
		
		player.startRentPet(time);
	}
	
}
