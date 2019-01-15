package main.engine.stats;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.builders.html.Html;
import main.util.builders.html.HtmlBuilder;
import main.util.builders.html.HtmlBuilder.HtmlType;
import main.util.builders.html.L2UI;
import main.util.builders.html.L2UI_CH3;

/**
 * @author fissban
 */
public class StatsPlayer extends AbstractMod
{
	private enum BonusType
	{
		NORMAL,
		HERO,
		NOBLE,
		OLY
	}
	
	private static class StatsHolder
	{
		private final Map<BonusType, LinkedHashMap<StatsType, Integer>> stats = new LinkedHashMap<>();
		
		public StatsHolder()
		{
			initBonus();
		}
		
		private void initBonus()
		{
			// inicializamos todos los stats
			for (var bt : BonusType.values())
			{
				for (var sts : StatsType.values())
				{
					if (!stats.containsKey(bt))
					{
						stats.put(bt, new LinkedHashMap<>());
					}
					
					stats.get(bt).put(sts, 0);
				}
			}
		}
		
		public void setBonus(BonusType type, StatsType stat, int bonus)
		{
			stats.get(type).put(stat, bonus);
		}
		
		public int getBonus(BonusType type, StatsType stat)
		{
			return stats.get(type).get(stat);
		}
		
		public LinkedHashMap<StatsType, Integer> getAllBonus(BonusType type)
		{
			return stats.get(type);
		}
		
		public void increaseBonus(BonusType type, StatsType stat)
		{
			var oldBonus = stats.get(type).get(stat);
			stats.get(type).put(stat, oldBonus + 1);
		}
		
		public void decreaseBonus(BonusType type, StatsType stat)
		{
			var oldBonus = stats.get(type).get(stat);
			stats.get(type).put(stat, oldBonus - 1);
		}
	}
	
	private static final Map<String, StatsHolder> classStats = new HashMap<>();
	
	public StatsPlayer()
	{
		registerMod(true);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				loadValuesFromDb();
				initStats();
				break;
			case END:
				//
				break;
		}
	}
	
	private void initStats()
	{
		// for all ---------------------------------------------------------------------------------------
		
		classStats.put("all", new StatsHolder());
		
		for (var bt : BonusType.values())
		{
			var values = getValueDB(99999, bt.name()).getString(); // 99999 for all
			
			if (values != null)
			{
				for (var split : values.split(";"))
				{
					var parse = split.split(",");
					
					var stat = StatsType.valueOf(parse[0]);
					var bonus = Integer.parseInt(parse[1]);
					
					classStats.get("all").setBonus(bt, stat, bonus);
				}
			}
		}
		
		// for class ---------------------------------------------------------------------------------------
		for (var cs : ClassId.values())
		{
			// solo los de 3ra clase vamos a balancear
			if (cs.level() < 3)
			{
				continue;
			}
			
			classStats.put(cs.name(), new StatsHolder());
			
			for (var bt : BonusType.values())
			{
				// en lugar de usar el objectId usaremos el id de la clase para almacenar en la DB.
				var values = getValueDB(cs.getId(), bt.name()).getString();
				
				if (values != null)
				{
					for (var split : values.split(";"))
					{
						var parse = split.split(",");
						
						var stat = StatsType.valueOf(parse[0]);
						var bonus = Integer.parseInt(parse[1]);
						
						classStats.get(cs.name()).setBonus(bt, stat, bonus);
					}
				}
			}
		}
	}
	
	@Override
	public boolean onAdminCommand(PlayerHolder player, String chat)
	{
		if (chat.equals("balance"))
		{
			htmlIndexClass(player);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onCommunityBoard(PlayerHolder player, String command)
	{
		var st = new StringTokenizer(command, ",");
		// bbshome
		var event = st.nextToken();
		
		if (!event.equals("_bbshome"))
		{
			return false;
		}
		if (!st.hasMoreTokens())
		{
			return false;
		}
		
		event = st.nextToken();
		if (event.equals("balance"))
		{
			htmlIndexClass(player);
			return true;
		}
		if (event.equals("class"))
		{
			var className = st.nextToken();
			var bonusType = st.hasMoreTokens() ? BonusType.valueOf(st.nextToken()) : BonusType.NORMAL;
			var page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			htmlIndex(player, className, bonusType, page);
			
			return true;
		}
		if (event.equals("modified"))
		{
			var className = st.nextToken();
			var bonusType = BonusType.valueOf(st.nextToken());
			var stat = StatsType.valueOf(st.nextToken());
			var type = st.nextToken(); // add - sub
			
			// page
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			switch (type)
			{
				case "add":
					classStats.get(className).increaseBonus(bonusType, stat);
					break;
				case "sub":
					classStats.get(className).decreaseBonus(bonusType, stat);
					break;
			}
			
			var parse = "";
			for (Entry<StatsType, Integer> map : classStats.get(className).getAllBonus(bonusType).entrySet())
			{
				parse += map.getKey().name() + "," + map.getValue() + ";";
			}
			
			setValueDB(className.equals("all") ? 99999 : ClassId.valueOf(className).getId(), bonusType.name(), parse);
			
			htmlIndex(player, className, bonusType, page);
			
			return true;
		}
		return false;
	}
	
	private static void htmlIndexClass(PlayerHolder player)
	{
		var hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append(Html.START);
		hb.append("<br>");
		hb.append("<center>");
		
		hb.append("Selecciona la clase a la que quieres ajustar su balance<br>");
		hb.append("<br>");
		
		hb.append("<td><button value=ALL action=\"bypass _bbshome,class,all\" width=93 height=22 back=", L2UI_CH3.bigbutton_down, " fore=", L2UI_CH3.bigbutton, "></td>");
		hb.append("<br>");
		hb.append(Html.fontColor("LEVEL", "HUMAN"));
		hb.append("<table bgcolor=000000>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.DUELIST));
		hb.append(buttonClassId(ClassId.DREADNOUGHT));
		hb.append(buttonClassId(ClassId.PHOENIX_KNIGHT));
		hb.append(buttonClassId(ClassId.HELL_KNIGHT));
		hb.append(buttonClassId(ClassId.SAGITTARIUS));
		hb.append(buttonClassId(ClassId.ADVENTURER));
		hb.append("</tr>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.ARCHMAGE));
		hb.append(buttonClassId(ClassId.SOULTAKER));
		hb.append(buttonClassId(ClassId.ARCANA_LORD));
		hb.append(buttonClassId(ClassId.CARDINAL));
		hb.append(buttonClassId(ClassId.HIEROPHANT));
		hb.append("<td></td>");
		hb.append("</tr>");
		hb.append("</table>");
		// --------------------------------------------------------------------------------
		hb.append(Html.fontColor("LEVEL", "ELF"));
		hb.append("<table bgcolor=000000>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.EVA_TEMPLAR));
		hb.append(buttonClassId(ClassId.SWORD_MUSE));
		hb.append(buttonClassId(ClassId.WIND_RIDER));
		hb.append(buttonClassId(ClassId.MOONLIGHT_SENTINEL));
		hb.append(buttonClassId(ClassId.MYSTIC_MUSE));
		hb.append(buttonClassId(ClassId.ELEMENTAL_MASTER));
		hb.append("</tr>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.EVA_SAINT));
		hb.append("<td></td>");
		hb.append("<td></td>");
		hb.append("<td></td>");
		hb.append("<td></td>");
		hb.append("</tr>");
		hb.append("</table>");
		// --------------------------------------------------------------------------------
		hb.append(Html.fontColor("LEVEL", "DARK ELF"));
		hb.append("<table bgcolor=000000>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.SHILLIEN_TEMPLAR));
		hb.append(buttonClassId(ClassId.SPECTRAL_DANCER));
		hb.append(buttonClassId(ClassId.GHOST_HUNTER));
		hb.append(buttonClassId(ClassId.GHOST_SENTINEL));
		hb.append(buttonClassId(ClassId.STORM_SCREAMER));
		hb.append(buttonClassId(ClassId.SPECTRAL_MASTER));
		hb.append("</tr>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.SHILLIEN_SAINT));
		hb.append("<td></td>");
		hb.append("<td></td>");
		hb.append("<td></td>");
		hb.append("<td></td>");
		hb.append("</tr>");
		hb.append("</table>");
		// --------------------------------------------------------------------------------
		hb.append(Html.fontColor("LEVEL", "ORC"));
		hb.append("<table bgcolor=000000>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.TITAN));
		hb.append(buttonClassId(ClassId.GRAND_KHAVATARI));
		hb.append(buttonClassId(ClassId.DOMINATOR));
		hb.append(buttonClassId(ClassId.DOOM_CRYER));
		hb.append("</tr>");
		hb.append("</table>");
		// --------------------------------------------------------------------------------
		hb.append(Html.fontColor("LEVEL", "DWARF"));
		hb.append("<table bgcolor=000000>");
		hb.append("<tr>");
		hb.append(buttonClassId(ClassId.FORTUNE_SEEKER));
		hb.append(buttonClassId(ClassId.MAESTRO));
		hb.append("</tr>");
		hb.append("</table>");
		
		hb.append("<br>");
		hb.append("</center>");
		hb.append(Html.END);
		sendCommunity(player, hb.toString());
	}
	
	private static String buttonClassId(ClassId classId)
	{
		var hb = new HtmlBuilder(HtmlType.HTML);
		hb.append("<td><button value=", classId.toString().replace("_", " ").toLowerCase(), " action=\"bypass _bbshome,class,", classId.name(), "\" width=93 height=22 back=", L2UI_CH3.bigbutton_down, " fore=", L2UI_CH3.bigbutton, "></td>");
		return hb.toString();
	}
	
	private static void htmlIndex(PlayerHolder player, String className, BonusType bonusType, int page)
	{
		var hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append(Html.START);
		hb.append("<br>");
		hb.append("<center>");
		
		hb.append("<button value=INDEX action=\"bypass _bbshome,balance\" width=93 height=22 back=", L2UI_CH3.bigbutton_down, " fore=", L2UI_CH3.bigbutton, ">");
		hb.append("<br>");
		hb.append(Html.headCommunity(className));
		hb.append("<br>");
		hb.append("<table width=460 height=22>");
		hb.append("<tr>");
		for (var bt : BonusType.values())
		{
			hb.append("<td><button value=", bt.name(), " action=\"bypass _bbshome,class,", className, ",", bt.name(), "\" width=93 height=22 back=", L2UI_CH3.bigbutton_down, " fore=", L2UI_CH3.bigbutton, "></td>");
		}
		hb.append("</tr>");
		hb.append("</table>");
		
		hb.append("<br>");
		var MAX_PER_PAGE = 13;
		var searchPage = MAX_PER_PAGE * (page - 1);
		var count = 0;
		var color = 0;
		
		for (var stat : StatsType.values())
		{
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
			
			double value = classStats.get(className).getBonus(bonusType, stat);
			hb.append("<table width=460 height=22 ", (color % 2) == 0 ? "bgcolor=000000 " : "", "cellspacing=0 cellpadding=0>");
			hb.append("<tr>");
			hb.append("<td fixwidth=16 height=22 align=center>", Html.image(L2UI_CH3.ps_sizecontrol2_over, 16, 16), "</td>");
			hb.append("<td width=100 height=22 align=center>", Html.fontColor("LEVEL", stat.toString().replace("_", " ").toLowerCase()), " </td>");
			hb.append("<td width=62 align=center>", value, "%</td>");
			hb.append("<td width=32><button action=\"bypass _bbshome,modified,", className, ",", bonusType.name(), ",", stat, ",add\" width=16 height=16 back=sek.cbui343 fore=sek.cbui343></td>");
			hb.append("<td width=32><button action=\"bypass _bbshome,modified,", className, ",", bonusType.name(), ",", stat, ",sub\" width=16 height=16 back=sek.cbui347 fore=sek.cbui347></td>");
			hb.append("</tr>");
			hb.append("</table>");
			hb.append(Html.image(L2UI.SquareGray, 460, 1));
			
			color++;
			count++;
		}
		
		var currentPage = 1;
		var size = StatsType.values().length;
		
		hb.append("<br>");
		hb.append("<table>");
		hb.append("<tr>");
		for (int i = 0; i < size; i++)
		{
			if ((i % MAX_PER_PAGE) == 0)
			{
				if (currentPage == page)
				{
					hb.append("<td width=20>", Html.fontColor("LEVEL", currentPage), "</td>");
				}
				else
				{
					hb.append("<td width=20><a action=\"bypass _bbshome,class,", className, ",", bonusType.name(), ",", currentPage, "\">", currentPage, "</a></td>");
				}
				
				currentPage++;
			}
		}
		hb.append("</tr>");
		hb.append("</table>");
		
		hb.append("</center>");
		hb.append(Html.END);
		sendCommunity(player, hb.toString());
	}
	
	@Override
	public double onStats(StatsType stat, CharacterHolder ch, double value)
	{
		if (!Util.areObjectType(L2Playable.class, ch))
		{
			return value;
		}
		
		var player = ch.getInstance().getActingPlayer();
		
		var bonusType = BonusType.NORMAL;
		
		if (player.isInOlympiadMode())
		{
			bonusType = BonusType.OLY;
		}
		if (player.isNoble())
		{
			bonusType = BonusType.NOBLE;
		}
		if (player.isHero())
		{
			bonusType = BonusType.HERO;
		}
		
		if (classStats.containsKey(player.getClassId().name()))
		{
			value *= (classStats.get(player.getClassId().name()).getBonus(bonusType, stat) / 10.0) + 1.0;
		}
		
		return value * ((classStats.get("all").getBonus(bonusType, stat) / 100.0) + 1.0);
	}
}
