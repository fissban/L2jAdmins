package main.engine.mods;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import main.data.ObjectData;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.UtilMessage;
import main.util.builders.html.Html;
import main.util.builders.html.HtmlBuilder;
import main.util.builders.html.HtmlBuilder.HtmlType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;

/**
 * @author fissban
 */
public class SystemFakeHero extends AbstractMod
{
	public SystemFakeHero()
	{
		registerMod(true);// TODO missing config
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
			{
				loadValuesFromDb();
				loadAllFakeHero();
				break;
			}
			case END:
			{
				break;
			}
		}
	}
	
	@Override
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		switch (timerName)
		{
			case "removeFakeHero":
			{
				// TODO missing onExitWorld
				if (ph.getInstance() == null)
				{
					return;
				}
				
				informeExpireFakeHero(ph);
				removeFakeHero(ph);
				break;
			}
		}
	}
	
	@Override
	public void onEvent(PlayerHolder ph, CharacterHolder npc, String command)
	{
		var st = new StringTokenizer(command, " ");
		
		var event = st.nextToken();
		switch (event)
		{
			case "allFakeHero":
			{
				if (ph.getInstance().getAccessLevel() < 1)
				{
					break;
				}
				
				getAllPlayerFakeHeros(ph, Integer.parseInt(st.nextToken()));
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
			case "allfakehero":
			{
				getAllPlayerFakeHeros(ph, 1);
				return true;
			}
			case "removefakehero":
			{
				if (!checkTarget(ph))
				{
					return true;
				}
				
				informeExpireFakeHero(ph);
				removeFakeHero((PlayerHolder) ph.getTarget());
				return true;
			}
			case "setfakehero":
			{
				if (!checkTarget(ph))
				{
					return true;
				}
				
				if (!st.hasMoreTokens())
				{
					ph.getInstance().sendMessage("Correct command:");
					ph.getInstance().sendMessage("//setFakeHero days");
					return true;
				}
				
				var days = st.nextToken();
				
				if (!Util.isNumber(days))
				{
					ph.getInstance().sendMessage("Correct command:");
					ph.getInstance().sendMessage("//setFakeHero days");
					return true;
				}
				
				var fakeHero = (PlayerHolder) ph.getTarget();
				
				// Create calendar
				var time = new GregorianCalendar();
				time.add(Calendar.DAY_OF_YEAR, Integer.parseInt(days));
				// save values in DB
				setValueDB(fakeHero, "fakeHero", time.getTimeInMillis() + "");
				// saved state in memory
				fakeHero.setFakeHero(true, time.getTimeInMillis());
				
				addFakeHero(fakeHero, time.getTimeInMillis());
				
				// Informed admin
				UtilMessage.sendCreatureMsg(ph, SayType.TELL, "[System]", "player: " + fakeHero.getName() + " is FakeHero now");
				// Informed player
				UtilMessage.sendCreatureMsg(fakeHero, SayType.TELL, "[System]", "Dear " + fakeHero.getName() + " your are now FakeHero");
				
				informeExpireFakeHero(fakeHero);
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onEnterWorld(PlayerHolder ph)
	{
		if (ph.isFakeHero())
		{
			if (ph.getFakeHeroExpireDate() < System.currentTimeMillis())
			{
				removeFakeHero(ph);
				return;
			}
			
			addFakeHero(ph, ph.getFakeHeroExpireDate());
			
			informeExpireFakeHero(ph);
		}
	}
	
	/**
	 * Send the character html informing the time expire FakeHero. (format: dd-MMM-yyyy)
	 * @param ph
	 * @param dayTime
	 */
	private void informeExpireFakeHero(PlayerHolder ph)
	{
		var hb = new HtmlBuilder(HtmlType.HTML);
		
		hb.append(Html.START);
		hb.append("<br>");
		hb.append(Html.head("FakeHero"));
		hb.append("<br>");
		hb.append("<font color=9900CC>FakeHero Expire Date: </font>", ph.getFakeHeroExpireDateFormat(), "<br>");
		hb.append(Html.END);
		sendHtml(null, hb, ph);
	}
	
	public void addFakeHero(PlayerHolder ph, long dayTime)
	{
		startTimer("removeFakeHero", dayTime - System.currentTimeMillis(), null, ph, false);
		
		// clear karma
		if (ph.getInstance().getKarma() > 0)
		{
			ph.getInstance().setKarma(0);
		}
		
		ph.getInstance().setHero(true);
	}
	
	public void removeFakeHero(PlayerHolder ph)
	{
		// remove state in memory
		ph.setFakeHero(false, 0);
		ph.getInstance().setHero(false);
		// init title
		ph.getInstance().setTitle("");
		ph.getInstance().broadcastUserInfo();
		
		setValueDB(ph, "fakeHero", System.currentTimeMillis() + "");
	}
	
	public void getAllPlayerFakeHeros(PlayerHolder player, int page)
	{
		var hb = new HtmlBuilder(HtmlType.HTML);
		
		hb.append("<html><body>");
		hb.append("<br>");
		hb.append(Html.head("All FakeHero Players"));
		hb.append("<br>");
		
		hb.append("<table>");
		hb.append("<tr>");
		hb.append("<td width=64><font color=LEVEL>Player:</font></td><td width=200><font color=LEVEL>ExpireDate:</font></td>");
		hb.append("</tr>");
		hb.append("</table>");
		
		var MAX_PER_PAGE = 12;
		var searchPage = MAX_PER_PAGE * (page - 1);
		var count = 0;
		var countFakeHero = 0;
		
		for (var ph : ObjectData.getAll(PlayerHolder.class))
		{
			if (ph.isFakeHero())
			{
				countFakeHero++;
				// min
				if (count < searchPage)
				{
					count++;
					continue;
				}
				// max
				if (count >= searchPage + MAX_PER_PAGE)
				{
					continue;
				}
				
				hb.append("<table", count % 2 == 0 ? " bgcolor=000000>" : ">");
				hb.append("<tr>");
				hb.append("<td width=64>", ph.getName(), "</td><td width=200>", ph.getFakeHeroExpireDateFormat(), "</td>");
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
		
		for (int i = 0; i < countFakeHero; i++)
		{
			if (i % MAX_PER_PAGE == 0)
			{
				hb.append("<td width=18 align=center><a action=\"bypass -h Engine SystemFakeHero allFakeHero ", currentPage, "\">", currentPage, "</a></td>");
				currentPage++;
			}
		}
		
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=264 height=1>");
		hb.append("</center>");
		
		hb.append("</body></html>");
		sendHtml(null, hb, player);
	}
	
	private void loadAllFakeHero()
	{
		for (var ph : ObjectData.getAll(PlayerHolder.class))
		{
			var dayTime = getValueDB(ph.getObjectId(), "fakeHero").getLong();
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
			ph.setFakeHero(true, dayTime);
		}
	}
	
	// MISC ---------------------------------------------------------------------------------------------------------
	
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
