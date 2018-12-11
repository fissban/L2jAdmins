package l2j.gameserver.handler.command.admin;

import java.util.ArrayList;
import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.CropProcureHolder;
import l2j.gameserver.model.holder.SeedProductionHolder;
import l2j.gameserver.network.external.server.NpcHtmlMessage;

/**
 * Admin comand handler for Manor System This class handles following admin commands: - manor_info = shows info about current manor state - manor_approve = approves settings for the next manor period - manor_setnext = changes manor settings to the next day's - manor_reset castle = resets all manor
 * data for specified castle (or all) - manor_setmaintenance = sets manor system under maintenance mode - manor_save = saves all manor data into database - manor_disable = disables manor system
 * @author l3x
 */
public class AdminManor implements IAdminCommandHandler
{
	private static final String[] ADMINCOMMAND =
	{
		"admin_manor",
		"admin_manor_approve",
		"admin_manor_setnext",
		"admin_manor_reset",
		"admin_manor_setmaintenance",
		"admin_manor_save",
		"admin_manor_disable"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		command = st.nextToken();
		
		// ----------~ COMMAND ~---------- //
		if (command.equals("admin_manor"))
		{
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_manor_setnext"))
		{
			CastleManorManager.getInstance().setNextPeriod();
			CastleManorManager.getInstance().setNewManorRefresh();
			CastleManorManager.getInstance().updateManorRefresh();
			activeChar.sendMessage("Manor System: set to next period");
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_manor_approve"))
		{
			CastleManorManager.getInstance().approveNextPeriod();
			CastleManorManager.getInstance().setNewPeriodApprove();
			CastleManorManager.getInstance().updatePeriodApprove();
			activeChar.sendMessage("Manor System: next period approved");
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_manor_reset"))
		{
			int castleId = 0;
			try
			{
				castleId = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)
			{
				//
			}
			
			if (castleId > 0)
			{
				Castle castle = CastleData.getInstance().getCastleById(castleId);
				castle.setCropProcure(new ArrayList<CropProcureHolder>(), CastleManorManager.PERIOD_CURRENT);
				castle.setCropProcure(new ArrayList<CropProcureHolder>(), CastleManorManager.PERIOD_NEXT);
				castle.setSeedProduction(new ArrayList<SeedProductionHolder>(), CastleManorManager.PERIOD_CURRENT);
				castle.setSeedProduction(new ArrayList<SeedProductionHolder>(), CastleManorManager.PERIOD_NEXT);
				
				if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
				{
					castle.saveCropData();
					castle.saveSeedData();
				}
				activeChar.sendMessage("Manor data for " + castle.getName() + " was nulled");
			}
			else
			{
				for (Castle castle : CastleData.getInstance().getCastles())
				{
					castle.setCropProcure(new ArrayList<CropProcureHolder>(), CastleManorManager.PERIOD_CURRENT);
					castle.setCropProcure(new ArrayList<CropProcureHolder>(), CastleManorManager.PERIOD_NEXT);
					castle.setSeedProduction(new ArrayList<SeedProductionHolder>(), CastleManorManager.PERIOD_CURRENT);
					castle.setSeedProduction(new ArrayList<SeedProductionHolder>(), CastleManorManager.PERIOD_NEXT);
					
					if (Config.ALT_MANOR_SAVE_ALL_ACTIONS)
					{
						castle.saveCropData();
						castle.saveSeedData();
					}
				}
				activeChar.sendMessage("Manor data was nulled");
			}
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_manor_setmaintenance"))
		{
			boolean mode = CastleManorManager.getInstance().isUnderMaintenance();
			CastleManorManager.getInstance().setUnderMaintenance(!mode);
			if (mode)
			{
				activeChar.sendMessage("Manor System: not under maintenance");
			}
			else
			{
				activeChar.sendMessage("Manor System: under maintenance");
			}
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_manor_save"))
		{
			CastleManorManager.getInstance().save();
			activeChar.sendMessage("Manor System: all data saved");
			showMainPage(activeChar);
		}
		// ----------~ COMMAND ~---------- //
		else if (command.equals("admin_manor_disable"))
		{
			boolean mode = CastleManorManager.getInstance().isDisabled();
			CastleManorManager.getInstance().setDisabled(!mode);
			if (mode)
			{
				activeChar.sendMessage("Manor System: enabled");
			}
			else
			{
				activeChar.sendMessage("Manor System: disabled");
			}
			showMainPage(activeChar);
		}
		
		return true;
	}
	
	private String formatTime(long millis)
	{
		String s = "";
		int secs = (int) millis / 1000;
		int mins = secs / 60;
		secs -= mins * 60;
		int hours = mins / 60;
		mins -= hours * 60;
		
		if (hours > 0)
		{
			s += hours + ":";
		}
		
		s += mins + ":";
		s += secs;
		return s;
	}
	
	private void showMainPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder("<html><body>");
		
		sb.append("<center><font color=\"LEVEL\"> [Manor System] </font></center><br>");
		sb.append("<table width=\"100%\"><tr><td>");
		sb.append("Disabled: " + (CastleManorManager.getInstance().isDisabled() ? "yes" : "no") + "</td><td>");
		sb.append("Under Maintenance: " + (CastleManorManager.getInstance().isUnderMaintenance() ? "yes" : "no") + "</td></tr><tr><td>");
		sb.append("Time to refresh: " + formatTime(CastleManorManager.getInstance().getMillisToManorRefresh()) + "</td><td>");
		sb.append("Time to approve: " + formatTime(CastleManorManager.getInstance().getMillisToNextPeriodApprove()) + "</td></tr>");
		sb.append("</table>");
		
		sb.append("<center><table><tr><td>");
		sb.append("<button value=\"Set Next\" action=\"bypass -h admin_manor_setnext\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		sb.append("<button value=\"Approve Next\" action=\"bypass -h admin_manor_approve\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr><tr><td>");
		sb.append("<button value=\"" + (CastleManorManager.getInstance().isUnderMaintenance() ? "Set normal" : "Set mainteance") + "\" action=\"bypass -h admin_manor_setmaintenance\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		sb.append("<button value=\"" + (CastleManorManager.getInstance().isDisabled() ? "Enable" : "Disable") + "\" action=\"bypass -h admin_manor_disable\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr><tr><td>");
		sb.append("<button value=\"Refresh\" action=\"bypass -h admin_manor\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td><td>");
		sb.append("<button value=\"Back\" action=\"bypass -h admin_admin\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td></tr>");
		sb.append("</table></center>");
		
		sb.append("<br><center>Castle Information:<table width=\"100%\">");
		sb.append("<tr><td></td><td>Current Period</td><td>Next Period</td></tr>");
		
		for (Castle c : CastleData.getInstance().getCastles())
		{
			sb.append("<tr><td>" + c.getName() + "</td>" + "<td>" + c.getManorCost(CastleManorManager.PERIOD_CURRENT) + "a</td>" + "<td>" + c.getManorCost(CastleManorManager.PERIOD_NEXT) + "a</td>" + "</tr>");
		}
		
		sb.append("</table><br>");
		
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
