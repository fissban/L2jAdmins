package main.engine.mods;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.network.external.client.Say2.SayType;
import main.data.memory.ObjectData;
import main.data.properties.ConfigData;
import main.engine.AbstractMod;
import main.enums.ExpSpType;
import main.enums.ItemDropType;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.instances.NpcDropsInstance;
import main.instances.NpcExpInstance;
import main.util.Util;
import main.util.UtilMessage;
import main.util.builders.html.Html;
import main.util.builders.html.HtmlBuilder;
import main.util.builders.html.HtmlBuilder.HtmlType;

/**
 * @author fissban
 */
public class SystemVip extends AbstractMod
{
	public SystemVip()
	{
		registerMod(true);// TODO missing config
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				loadValuesFromDb();
				loadAllVips();
				break;
			case END:
				//
				break;
		}
	}
	
	@Override
	public void onNpcExpSp(PlayerHolder killer, NpcHolder npc, NpcExpInstance instance)
	{
		if (!killer.isVip())
		{
			return;
		}
		
		// ExpSpBonusHolder (bonusType, amountBonus)
		// Example: 110 -> 110%
		// if you use 100% exp will be normal, to earn bonus use values greater than 100%.
		// increase normal exp/sp amount
		instance.increaseRate(ExpSpType.EXP, ConfigData.VIP_BONUS_XP);
		instance.increaseRate(ExpSpType.SP, ConfigData.VIP_BONUS_SP);
		return;
	}
	
	@Override
	public void onNpcDrop(PlayerHolder killer, NpcHolder npc, NpcDropsInstance instance)
	{
		if (!killer.isVip())
		{
			return;
		}
		// DropBonusHolder (dropType, amountBonus, chanceBonus)
		// Example: 110 -> 110%
		// if you use 100% drop will be normal, to earn bonus use values greater than 100%.
		
		// increase normal drop amount and chance
		instance.increaseDrop(ItemDropType.NORMAL, ConfigData.VIP_BONUS_DROP_NORMAL_AMOUNT, ConfigData.VIP_BONUS_DROP_NORMAL_CHANCE);
		// increase spoil drop amount and chance
		instance.increaseDrop(ItemDropType.SPOIL, ConfigData.VIP_BONUS_DROP_SPOIL_AMOUNT, ConfigData.VIP_BONUS_DROP_SPOIL_CHANCE);
		// increase seed drop amount and chance
		instance.increaseDrop(ItemDropType.SEED, ConfigData.VIP_BONUS_DROP_SEED_AMOUNT, ConfigData.VIP_BONUS_DROP_SEED_CHANCE);
	}
	
	@Override
	public void onEvent(PlayerHolder ph, CharacterHolder npc, String command)
	{
		var st = new StringTokenizer(command, " ");
		
		var event = st.nextToken();
		switch (event)
		{
			case "allVip":
			{
				if (ph.getInstance().getAccessLevel() < 1)
				{
					break;
				}
				
				getAllPlayerVips(ph, Integer.parseInt(st.nextToken()));
				break;
			}
		}
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		switch (timerName)
		{
			case "removeVip":
			{
				// TODO missing onExitWorld
				if (ph.getInstance() == null)
				{
					return;
				}
				
				informeExpireVip(ph);
				removeVip(ph);
				break;
			}
		}
	}
	
	@Override
	public boolean onAdminCommand(PlayerHolder ph, String chat)
	{
		var st = new StringTokenizer(chat, " ");
		
		var command = st.nextToken().toLowerCase();
		switch (command)
		{
			case "allvip":
			{
				getAllPlayerVips(ph, 1);
				return true;
			}
			case "removevip":
			{
				if (!checkTarget(ph))
				{
					return true;
				}
				
				informeExpireVip((PlayerHolder) ph.getTarget());
				removeVip((PlayerHolder) ph.getTarget());
				return true;
			}
			case "setvip":
			{
				if (!checkTarget(ph))
				{
					return true;
				}
				
				if (!st.hasMoreTokens())
				{
					ph.getInstance().sendMessage("Correct command:");
					ph.getInstance().sendMessage("//setVip days");
					return true;
				}
				
				var days = st.nextToken();
				
				if (!Util.isNumber(days))
				{
					ph.getInstance().sendMessage("Correct command:");
					ph.getInstance().sendMessage("//setVip days");
					return true;
				}
				
				var vip = (PlayerHolder) ph.getTarget();
				
				// Create calendar
				var time = new GregorianCalendar();
				time.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
				// save values in DB
				setValueDB(vip, "vip", time.getTimeInMillis() + "");
				// saved state in memory
				vip.setVip(true, time.getTimeInMillis());
				
				addVip(vip, time.getTimeInMillis());
				
				// Informed admin
				UtilMessage.sendCreatureMsg(ph, SayType.TELL, "[System]", "player: " + vip.getName() + " is Vip now");
				// Informed player
				UtilMessage.sendCreatureMsg(vip, SayType.TELL, "[System]", "Dear " + vip.getName() + " your are now Vip");
				
				informeExpireVip(vip);
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		if (ph.isVip())
		{
			if (ph.getVipExpireDate() < System.currentTimeMillis())
			{
				removeVip(ph);
				return;
			}
			
			addVip(ph, ph.getVipExpireDate());
			informeExpireVip(ph);
		}
	}
	
	@Override
	public double onStats(StatsType stat, CharacterHolder character, double value)
	{
		if (!Util.areObjectType(L2PcInstance.class, character))
		{
			return value;
		}
		
		if (!ObjectData.get(PlayerHolder.class, character.getObjectId()).isVip())
		{
			return value;
		}
		
		if (ConfigData.VIP_STATS.containsKey(stat))
		{
			return value *= ConfigData.VIP_STATS.get(stat);
		}
		
		return value;
	}
	
	/**
	 * Send the character html informing the time expire VIP. (format: dd-MMM-yyyy)
	 * @param ph
	 * @param dayTime
	 */
	private void informeExpireVip(PlayerHolder ph)
	{
		var hb = new HtmlBuilder(HtmlType.HTML);
		
		hb.append("<html><body>");
		hb.append(Html.head("VIP"));
		hb.append("<br>");
		// date
		hb.append("<font color=9900CC>VIP Expire Date: </font>", ph.getVipExpireDateFormat(), "<br>");
		
		hb.append("<font color=LEVEL>The VIP have exp/sp rate:</font><br>");
		
		// hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264 border=0 cellspacing=0 cellpadding=0>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100 align=center><button value=\"Type\" width=100 height=22 back=L2UI_CH3.FrameBackMid fore=L2UI_CH3.FrameBackMid></td>");
		hb.append("<td fixwidth=164 align=center><button value=\"Bonus\" width=164 height=22 back=L2UI_CH3.FrameBackMid fore=L2UI_CH3.FrameBackMid></td>");
		hb.append("</tr>");
		hb.append("</table>");
		// hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100 align=center><font color=3366FF>EXP:</font></td>");
		hb.append("<td fixwidth=164 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_XP + 100, "%</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100 align=center><font color=3366FF>SP:</font></td>");
		hb.append("<td fixwidth=164 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_SP + 100, "%</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		
		hb.append("<br><br><font color=LEVEL>The VIP have drop rate:</font><br>");
		
		// hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264 border=0 cellspacing=0 cellpadding=0 bgcolor=CC99FF>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100><button value=\"Type\" width=100 height=19 back=L2UI_CH3.FrameBackMid fore=L2UI_CH3.FrameBackMid></td>");
		hb.append("<td fixwidth=82><button value=\"Bonus Amount\" width=82 height=22 back=L2UI_CH3.FrameBackMid fore=L2UI_CH3.FrameBackMid></td>");
		hb.append("<td fixwidth=82><button value=\"Bonus Chance\" width=82 height=22 back=L2UI_CH3.FrameBackMid fore=L2UI_CH3.FrameBackMid></td>");
		hb.append("</tr>");
		hb.append("</table>");
		// hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264 border=0 cellspacing=0 cellpadding=0>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100 align=center><font color=3366FF>Normal:</font></td>");
		hb.append("<td fixwidth=82 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_DROP_NORMAL_AMOUNT + 100, "%</font></td>");
		hb.append("<td fixwidth=82 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_DROP_NORMAL_AMOUNT + 100, "%</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264 border=0 cellspacing=0 cellpadding=0>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100 align=center><font color=3366FF>Spoil:</font></td>");
		hb.append("<td fixwidth=82 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_DROP_SPOIL_AMOUNT + 100, "%</font></td>");
		hb.append("<td fixwidth=82 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_DROP_SPOIL_CHANCE + 100, "%</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table fixwidth=264 border=0 cellspacing=0 cellpadding=0>");
		hb.append("<tr>");
		hb.append("<td fixwidth=100 align=center><font color=3366FF>Seed:</font></td>");
		hb.append("<td fixwidth=82 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_DROP_SEED_AMOUNT + 100, "%</font></td>");
		hb.append("<td fixwidth=82 align=center><font color=LEVEL>", (int) ConfigData.VIP_BONUS_DROP_SEED_AMOUNT + 100, "%</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		
		hb.append("</body></html>");
		sendHtml(null, ph, hb);
	}
	
	public void addVip(PlayerHolder ph, long dayTime)
	{
		startTimer("removeVip", dayTime - System.currentTimeMillis(), null, ph, false);
		
		if (ConfigData.ALLOW_VIP_NCOLOR)
		{
			ph.getInstance().setNameColor(ConfigData.VIP_NCOLOR);
		}
		ph.getInstance().broadcastUserInfo();
	}
	
	public void removeVip(PlayerHolder ph)
	{
		// remove state in memory
		ph.setVip(false, 0);
		
		if (ConfigData.ALLOW_VIP_NCOLOR)
		{
			ph.getInstance().setNameColor(Integer.decode("0x" + "000000"));
		}
		
		ph.getInstance().broadcastUserInfo();
	}
	
	public void getAllPlayerVips(PlayerHolder ph, int page)
	{
		var hb = new HtmlBuilder(HtmlType.HTML);
		
		hb.append(Html.START);
		hb.append("<br>");
		hb.append(Html.head("All VIP Players"));
		hb.append("<br>");
		
		hb.append("<table>");
		hb.append("<tr>");
		hb.append("<td width=64><font color=LEVEL>Player:</font></td><td width=200><font color=LEVEL>ExpireDate:</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		
		var MAX_PER_PAGE = 12;
		var searchPage = MAX_PER_PAGE * (page + 100);
		var count = 0;
		var countVip = 0;
		
		for (var p : ObjectData.getAll(PlayerHolder.class))
		{
			if (p.isVip())
			{
				countVip++;
				// min
				if (count < searchPage)
				{
					count++;
					continue;
				}
				// max
				if (count >= (searchPage + MAX_PER_PAGE))
				{
					continue;
				}
				
				hb.append("<table", (count % 2) == 0 ? " bgcolor=000000>" : ">");
				hb.append("<tr>");
				hb.append("<td width=64>", p.getName(), "</td><td width=200>" + p.getVipExpireDateFormat(), "</td>");
				hb.append("</tr>");
				hb.append("</table>");
				count++;
			}
		}
		
		hb.append("<center>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("<table bgcolor=CC99FF>");
		hb.append("<tr>");
		
		var currentPage = 1;
		
		for (int i = 0; i < countVip; i++)
		{
			if ((i % MAX_PER_PAGE) == 0)
			{
				hb.append("<td width=18><center><a action=\"bypass -h Engine SystemVip allVip ", currentPage, "\">" + currentPage, "</center></a></td>");
				currentPage++;
			}
		}
		
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("</center>");
		
		hb.append(Html.END);
		sendHtml(null, hb, ph);
	}
	
	private void loadAllVips()
	{
		for (var ph : ObjectData.getAll(PlayerHolder.class))
		{
			long dayTime = getValueDB(ph.getObjectId(), "vip").getLong();
			// Don't has value in db
			if (dayTime == 0)
			{
				continue;
			}
			
			if (dayTime < System.currentTimeMillis())
			{
				continue;
			}
			
			// saved state in memory
			ph.setVip(true, dayTime);
		}
	}
	
	private static boolean checkTarget(PlayerHolder ph)
	{
		if (ph.getInstance().getTarget() == null)
		{
			ph.getInstance().sendMessage("this command need target");
			return false;
		}
		
		if (!Util.areObjectType(L2PcInstance.class, ph.getTarget()))
		{
			ph.getInstance().sendMessage("this command need player target");
			return false;
		}
		
		return true;
	}
}
