package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.trade.MerchantTradeList;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.BuyList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban, Reynald0 <br>
 *         <br>
 *         Comandos relacionados con el administrador.<br>
 *         Contiene los comandos: <br>
 *         <li>admin
 *         <li>buy
 *         <li>diet
 *         <li>gmlistoff
 *         <li>gmliston
 *         <li>gmshop
 *         <li>gmspeed
 *         <li>gm
 *         <li>invis
 *         <li>invul
 *         <li>set
 *         <li>silence
 *         <li>tradeoff
 */
public class AdminAdmin implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		/** Commands in alphabetical order */
		// HTML
		"admin_admin",
		// MISC
		"admin_buy",
		"admin_diet",
		"admin_gmlistoff",
		"admin_gmliston",
		"admin_gmshop",
		"admin_gmspeed",
		"admin_gm",
		"admin_invis",
		"admin_invul",
		"admin_silence",
		"admin_tradeoff"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		/** ====================== [ HTML ] ====================== */
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_admin"))
		{
			AdminHelpPage.showHelpPage(activeChar, "menuChar.htm");
		}
		/** ====================== [ MISC ] ====================== */
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_buy")) // complemento del gmshop
		{
			try
			{
				String val = st.nextToken();
				MerchantTradeList list = TradeControllerData.getInstance().getBuyList(Integer.parseInt(val));
				
				if (list != null)
				{
					BuyList bl = new BuyList(list, activeChar.getInventory().getAdena(), 0);
					activeChar.sendPacket(bl);
				}
				else
				{
					activeChar.sendMessage("buylist " + val + " dont exist!");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage(".."); // send message?
			}
			
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			AdminHelpPage.showHelpPage(activeChar, "menuGmShops.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_diet"))
		{
			boolean diet = activeChar.getDietMode();
			activeChar.setDietMode(diet ? false : true);
			activeChar.sendMessage(diet ? "Diet mode off." : "Diet mode on.");
			activeChar.refreshOverloaded();
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_gmlistoff"))
		{
			GmListData.getInstance().deleteGm(activeChar);
			activeChar.sendMessage("Removed from GM list.");
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_gmliston"))
		{
			GmListData.getInstance().addGm(activeChar);
			activeChar.sendMessage("Registered into GM list.");
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_gmshop"))
		{
			AdminHelpPage.showHelpPage(activeChar, "menuGmShops.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_gmspeed"))
		{
			try
			{
				int val = Integer.parseInt(st.nextToken());
				activeChar.stopEffect(7029);
				
				if ((val >= 1) && (val <= 4))
				{
					activeChar.doCast(SkillData.getInstance().getSkill(7029, val));
				}
				
				activeChar.updateEffectIcons();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Correct command //gmspeed <value(0-4)>");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_gm"))
		{
			int oldAccesLevel = activeChar.getAccessLevel();
			activeChar.setAccessLevel(0);
			activeChar.sendMessage("You're not a game master anymore.");
			activeChar.broadcastUserInfo();
			
			ThreadPoolManager.schedule(() ->
			{
				activeChar.sendMessage("You're already a Game Master again.");
				activeChar.setAccessLevel(oldAccesLevel);
				activeChar.broadcastUserInfo();
			}, 60 * 1000);
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_invis"))
		{
			if (activeChar.getInvisible())
			{
				activeChar.setVisible();
			}
			else
			{
				activeChar.setInvisible();
			}
			
			activeChar.broadcastUserInfo();
			activeChar.decayMe();
			activeChar.spawnMe();
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_invul"))
		{
			boolean invul = activeChar.isInvul();
			activeChar.setIsInvul(invul ? false : true);
			activeChar.sendMessage(invul ? "You are now mortal" : "You are now invulnerable");
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_silence"))
		{
			boolean refusal = activeChar.isInRefusalMode();
			activeChar.setInRefusalMode(refusal ? false : true);
			activeChar.sendPacket(refusal ? SystemMessage.MESSAGE_ACCEPTANCE_MODE : SystemMessage.MESSAGE_REFUSAL_MODE);
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_tradeoff"))
		{
			try
			{
				String mode = st.nextToken();
				if (mode.equals("on"))
				{
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Tradeoff enabled.");
				}
				else if (mode.equals("off"))
				{
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Tradeoff disabled.");
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage(activeChar.getTradeRefusal() ? "Tradeoff currently enabled." : "Tradeoff currently disabled.");
			}
			AdminHelpPage.showHelpPage(activeChar, "menuAdmin.htm");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
