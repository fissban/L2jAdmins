package l2j.gameserver.scripts.ai.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python by Elektra
 * @author fissban
 */
public class EchoCrystals extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		8042,
		8043
	};
	// Items
	private static final Map<Integer, Integer> ITEMS = new HashMap<>();
	{
		ITEMS.put(4411, 4410);// Theme of Travel
		ITEMS.put(4415, 4421);// Theme of Festival
		ITEMS.put(4414, 4420);// Theme of Lonely
		ITEMS.put(4413, 4408);// Theme of Love
		ITEMS.put(4412, 4409);// Theme of Battle
		ITEMS.put(4417, 4419);// Theme of Comedy
		ITEMS.put(4416, 4418);// Theme of Celebration
	}
	private static final ItemHolder PRICE = new ItemHolder(Inventory.ADENA_ID, 200);
	// html
	private static final String HTML_PATH = "data/html/echoCrystal/";
	
	public EchoCrystals()
	{
		super(-1, "ai/npc");
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		return HTML_PATH + "index.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("index.htm"))
		{
			return HTML_PATH + event;
		}
		
		ScriptState st = player.getScriptState(EchoCrystals.class.getSimpleName());
		
		if (!(st.getItemsCount(PRICE.getId()) >= PRICE.getCount()))
		{
			return HTML_PATH + "no-adena.htm";
		}
		
		int score = Integer.parseInt(event);
		
		for (Entry<Integer, Integer> entry : ITEMS.entrySet())
		{
			if (entry.getKey().equals(score))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				
				if (st.hasItems(entry.getValue()))
				{
					st.takeItems(PRICE.getId(), PRICE.getCount());
					st.giveItems(entry.getKey(), 1);
					
					html.setFile(HTML_PATH + "created.htm");
					html.replace("%item%", ItemData.getInstance().getTemplate(entry.getKey()).getName());// item name
				}
				else
				{
					html.setFile(HTML_PATH + "no-items.htm");
					html.replace("%item%", ItemData.getInstance().getTemplate(entry.getValue()).getName());// item name
				}
				
				player.sendPacket(html);
			}
		}
		
		return null;
	}
}
