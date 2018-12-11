package main.engine.admin;

import java.util.StringTokenizer;

import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;
import main.util.builders.html.Html;
import main.util.builders.html.HtmlBuilder;
import main.util.builders.html.HtmlBuilder.HtmlType;

/**
 * @author fissban
 */
public class ServerStatistics extends AbstractMod
{
	public ServerStatistics()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				//
				break;
			case END:
				//
				break;
		}
	}
	
	@Override
	public void onEvent(PlayerHolder player, CharacterHolder character, String command)
	{
		//
	}
	
	@Override
	public boolean onAdminCommand(PlayerHolder ph, String chat)
	{
		StringTokenizer st = new StringTokenizer(chat, " ");
		
		if (!st.nextToken().equals("engine"))
		{
			return false;
		}
		
		if (!st.hasMoreTokens())
		{
			return false;
		}
		
		String event = st.nextToken();
		switch (event)
		{
			// recargamos los configs
			case "statistics":
			{
				htmlIndexClass(ph);
				return true;
			}
		}
		
		return false;
	}
	
	private static void htmlIndexClass(PlayerHolder ph)
	{
		var hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append(Html.START);
		hb.append("<br>");
		hb.append("<center>");
		
		var usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		var totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		
		hb.append("MEMORY");
		hb.append(Html.fontColor("LEVEL", "Total memory: "), totalMem);
		hb.append(Html.fontColor("LEVEL", "Used memory: "), usedMem);
		hb.append("Your memory is currently ", Html.fontColor("LEVEL", totalMem * 100 / usedMem + "%"), " busy.");
		
		hb.append("Threads");
		
		hb.append("</center>");
		hb.append(Html.END);
		
		sendCommunity(ph, hb.toString());
	}
}
