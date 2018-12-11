package l2j.gameserver.scripts.ai.npc.teleports.grandboss;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Teleport into Hall of Flames
 * @author fissban
 */
public class Klein extends Script
{
	// Npc
	private static final int NPC = 8540;
	// Item
	private static final ItemHolder FLOATING_STONE = new ItemHolder(7267, 1);
	// Loc
	private static final LocationHolder LOCATION = new LocationHolder(183813, -115157, -3303);
	// Html
	private static final String HTML_PATH = "data/html/teleporter/grandboss/klein/";
	
	public Klein()
	{
		super(-1, "ai/npc/teleports/grandboss");
		
		addStartNpc(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		switch (event)
		{
			case "Enter":
				if (st.takeItems(FLOATING_STONE.getId(), FLOATING_STONE.getCount()))
				{
					player.teleToLocation(LOCATION.getX(), LOCATION.getY(), LOCATION.getZ());
				}
				else
				{
					return HTML_PATH + "noItem.htm";
				}
		}
		return null;
	}
}
