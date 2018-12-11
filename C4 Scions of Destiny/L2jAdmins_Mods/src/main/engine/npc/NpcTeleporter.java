package main.engine.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.holder.LocationHolder;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.UtilSpawn;
import main.util.builders.html.Html;
import main.util.builders.html.HtmlBuilder;
import main.util.builders.html.HtmlBuilder.HtmlType;

/**
 * @author fissban
 */
public class NpcTeleporter extends AbstractMod
{
	private static final int NPC = 60011;
	private static final Map<String, LocationHolder> TELEPORTS = new HashMap<>();
	{
		// 30 missing
		TELEPORTS.put("40", new LocationHolder(121980, -118800, -2574));
		// 50 missing
		TELEPORTS.put("60 TOP", new LocationHolder(174528, 52683, -4369));
		TELEPORTS.put("60 UNDER", new LocationHolder(170327, 53985, -4583));
		TELEPORTS.put("70", new LocationHolder(188191, -74959, -2738));
		// ......missing
	}

	public NpcTeleporter()
	{
		registerMod(true);// TODO missing config
		spawnGuards();
	}

	@Override
	public void onModState()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Se spawnean guardias en las zonas de teleports
	 */
	private void spawnGuards()
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			for (LocationHolder loc : TELEPORTS.values())
			{
				UtilSpawn.npc(60010, loc, 50, 0, TeamType.RED, 0);
				UtilSpawn.npc(60010, loc, 50, 0, TeamType.RED, 0);
			}

		}, 20000); // 20 seg es hardcode
	}

	@Override
	public boolean onInteract(PlayerHolder ph, CharacterHolder npc)
	{
		if (!Util.areObjectType(L2Npc.class, npc))
		{
			return false;
		}

		if (((NpcHolder) npc).getId() != NPC)
		{
			return false;
		}

		var hb = new HtmlBuilder(HtmlType.HTML);
		hb.append(Html.START);
		hb.append(Html.head("TELEPORT MASTER"));
		hb.append("Puedo mostrarte las zonas donde");
		hb.append("los hombres se convierten en <font color=LEVEL>dioses!</font>");

		for (String tele : TELEPORTS.keySet())
		{
			hb.append("<table width=280>");
			hb.append("<tr>");
			hb.append("<td align=center>", Html.image("L2UI.bbs_folder", 32, 32), "</td>");
			hb.append("<td><button value=\"", tele, "\" action=\"bypass -h Engine NpcTeleporter teleport ", tele, "\" width=216 height=32 back=L2UI_CH5.UI_metro_orange2 fore=L2UI_CH5.UI_metro_orange1></td>");
			hb.append("<td align=center>", Html.image("L2UI.bbs_folder", 32, 32), "</td>");
			hb.append("</tr>");
			hb.append("</table>");
		}

		hb.append(Html.END);

		sendHtml((NpcHolder) npc, hb, ph);

		return true;
	}

	@Override
	public void onEvent(PlayerHolder ph, CharacterHolder npc, String command)
	{
		if (!Util.areObjectType(L2Npc.class, npc))
		{
			return;
		}

		if (((NpcHolder) npc).getId() != NPC)
		{
			return;
		}

		StringTokenizer st = new StringTokenizer(command, " ");

		switch (st.nextToken())
		{
			case "teleport":
				String locName = st.nextToken();

				if (!TELEPORTS.containsKey(locName))
				{
					// posible bypass....juaz!
					break;
				}
				ph.getInstance().teleToLocation(TELEPORTS.get(locName), 30);
				break;
		}
	}
}
