package l2j.gameserver.scripts.ai.npc;

import java.util.StringTokenizer;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.external.server.HennaEquipList;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class SymbolMaker extends Script
{
	private static int[] NPC =
	{
		8046, // Marsden
		8047, // Kell
		8048, // McDermott
		8049, // Pepper
		8050, // Thora
		8051, // Keach
		8052, // Heid
		8053, // Kidder
		8264, // Olsun
		8308,// Achim
	};
	
	// Html
	private static final String HTML_PATH = "data/html/symbolMaker/";
	
	public SymbolMaker()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPC);
		addFirstTalkId(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return HTML_PATH + "symbolMaker.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		
		switch (st.nextToken())
		{
			case "Draw":
				player.sendPacket(new HennaEquipList(player));
				break;
			
			case "RemoveList":
				showRemoveChat(player, npc);
				break;
			
			case "Remove":
				player.removeHenna(Integer.parseInt(st.nextToken()));
				break;
		}
		
		return null;
	}
	
	private static void showRemoveChat(L2PcInstance player, L2Npc npc)
	{
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("Select the symbol you wish to remove:<br><br>");
		boolean hasHennas = false;
		
		for (int i = 1; i <= 3; i++)
		{
			HennaHolder henna = player.getHenna(i);
			
			if (henna != null)
			{
				hasHennas = true;
				sb.append("<a action=\"bypass -h Quest " + SymbolMaker.class.getSimpleName() + " Remove " + i + "\">" + henna.getName() + "</a><br>");
			}
		}
		
		if (!hasHennas)
		{
			sb.append("You don't have any symbol to remove!");
		}
		
		sb.append("</body></html>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
}
