package l2j.gameserver.scripts.ai.npc.teleports;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author CaFi
 */
public class RaceTrack extends Script
{
	private static final int RACE_MANAGER = 7995;
	
	private static final Map<Integer, Integer> NPCS = new HashMap<>();
	{
		NPCS.put(7320, 1); // RICHLIN
		NPCS.put(7256, 2); // BELLA
		NPCS.put(7059, 3); // TRISHA
		NPCS.put(7080, 4); // CLARISSA
		NPCS.put(7899, 5); // FLAUEN
		NPCS.put(7177, 6); // VALENTIA
		NPCS.put(7848, 7); // ELISA
		NPCS.put(7233, 8); // ESMERALDA
		NPCS.put(8320, 9); // ILYANA
		NPCS.put(8275, 10); // TATIANA
		NPCS.put(7727, 11); // VERONA
		NPCS.put(7836, 12); // MINERVA
		NPCS.put(8210, 13); // RACE TRACK GK
	}
	
	private static final LocationHolder[] RETURN_LOCS =
	{
		new LocationHolder(-80826, 149775, -3043),
		new LocationHolder(-12672, 122776, -3116),
		new LocationHolder(15670, 142983, -2705),
		new LocationHolder(83400, 147943, -3404),
		new LocationHolder(111409, 219364, -3545),
		new LocationHolder(82956, 53162, -1495),
		new LocationHolder(146331, 25762, -2018),
		new LocationHolder(116819, 76994, -2714),
		new LocationHolder(43835, -47749, -792),
		new LocationHolder(147930, -55281, -2728),
		new LocationHolder(85335, 16177, -3694),
		new LocationHolder(105857, 109763, -3202),
		new LocationHolder(12882, 181053, -3560)
	};
	
	public RaceTrack()
	{
		super(-1, "ai/npc/teleports");
		
		for (int npcId : NPCS.keySet())
		{
			addStartNpc(npcId);
			addTalkId(npcId);
		}
		
		addTalkId(RACE_MANAGER);
		addStartNpc(RACE_MANAGER);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		
		if (NPCS.containsKey(npc.getId()))
		{
			st.startQuest();
			st.getPlayer().teleToLocation(12661, 181687, -3560);
			st.set("id", String.valueOf(NPCS.get(npc.getId())));
		}
		else if (st.isStarted() && (npc.getId() == RACE_MANAGER))
		{
			// back to start location
			st.getPlayer().teleToLocation(RETURN_LOCS[st.getInt("id") - 1], true);
			st.exitQuest(true);
		}
		
		return null;
	}
}
