package l2j.gameserver.scripts.ai.npc.teleports;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original Code by DraX (python)
 * @author fissban
 */
public class WithCharm extends Script
{
	// Npc
	private static final int WHIRPY = 7540;
	private static final int TAMIL = 7576;
	// Items
	private static final ItemHolder ORC_GATEKEEPER_CHARM = new ItemHolder(1658, 1);
	private static final ItemHolder DWARF_GATEKEEPER_TOKEN = new ItemHolder(1659, 1);
	// Loc
	private static final LocationHolder TELEPORT = new LocationHolder(-80826, 149775, -3043);
	// Html
	private static final String HTML_PATH = "data/html/teleporter/withCharm/";
	
	public WithCharm()
	{
		super(-1, "ai/npc/teleports");
		
		addStartNpc(WHIRPY, TAMIL);
		addTalkId(WHIRPY, TAMIL);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		if (npc.getId() == WHIRPY)
		{
			if (st.takeItems(DWARF_GATEKEEPER_TOKEN.getId(), DWARF_GATEKEEPER_TOKEN.getCount()))
			{
				player.teleToLocation(TELEPORT.getX(), TELEPORT.getY(), TELEPORT.getZ());
			}
			else
			{
				return HTML_PATH + "7540-01.htm";
			}
		}
		else if (npc.getId() == TAMIL)
		{
			if (st.takeItems(ORC_GATEKEEPER_CHARM.getId(), ORC_GATEKEEPER_CHARM.getCount()))
			{
				player.teleToLocation(TELEPORT.getX(), TELEPORT.getY(), TELEPORT.getZ());
			}
			else
			{
				return HTML_PATH + "7576-01.htm";
			}
		}
		
		return null;
	}
}
