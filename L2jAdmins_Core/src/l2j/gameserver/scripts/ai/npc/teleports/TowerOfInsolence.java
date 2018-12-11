package l2j.gameserver.scripts.ai.npc.teleports;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban, zarie
 */
public class TowerOfInsolence extends Script
{
	// Npcs
	private static final int[] NPCS =
	{
		7952,
		7953,
		7954,
	};
	// Item
	private static final ItemHolder GREEN_DIMENSION_STONE = new ItemHolder(4401, 1);
	private static final ItemHolder BLUE_DIMENSION_STONE = new ItemHolder(4402, 1);
	private static final ItemHolder RED_DIMENSION_STONE = new ItemHolder(4403, 1);
	// Loc
	private static final LocationHolder GREEN_TELEPORT = new LocationHolder(118558, 16659, 5987);
	private static final LocationHolder BLUE_TELEPORT = new LocationHolder(114097, 19935, 935);
	private static final LocationHolder RED_TELEPORT = new LocationHolder(110930, 15963, -4378);
	// html
	private static final String HTML_PATH = "data/html/teleporter/towerOfInsolence/";
	
	public TowerOfInsolence()
	{
		super(-1, "ai/npc/teleports");
		
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		switch (event)
		{
			case "red":
				if (st.takeItems(RED_DIMENSION_STONE.getId(), RED_DIMENSION_STONE.getCount()))
				{
					player.teleToLocation(RED_TELEPORT.getX(), RED_TELEPORT.getY(), RED_TELEPORT.getZ());
				}
				else
				{
					return HTML_PATH + "no-items.htm";
				}
				break;
			case "blue":
				if (st.takeItems(BLUE_DIMENSION_STONE.getId(), BLUE_DIMENSION_STONE.getCount()))
				{
					player.teleToLocation(BLUE_TELEPORT.getX(), BLUE_TELEPORT.getY(), BLUE_TELEPORT.getZ());
				}
				else
				{
					return HTML_PATH + "no-items.htm";
				}
				break;
			case "green":
				if (st.takeItems(GREEN_DIMENSION_STONE.getId(), GREEN_DIMENSION_STONE.getCount()))
				{
					player.teleToLocation(GREEN_TELEPORT.getX(), GREEN_TELEPORT.getY(), GREEN_TELEPORT.getZ());
				}
				else
				{
					return HTML_PATH + "no-items.htm";
				}
				break;
		}
		return null;
	}
}
