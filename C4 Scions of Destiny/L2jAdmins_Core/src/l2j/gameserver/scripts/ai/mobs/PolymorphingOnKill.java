package l2j.gameserver.scripts.ai.mobs;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class PolymorphingOnKill extends Script
{
	// SPAWN LIST
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	{
		MOBS.put(830, 859); // Guardian Angel -> Guardian Angel
		MOBS.put(831, 860); // Seal Angel -> Seal Angel
		MOBS.put(1062, 1063); // Messenger Angel -> Messenger Angel
		MOBS.put(1067, 1068); // Guardian Archangel -> Guardian Archangel
		MOBS.put(1070, 1071); // Seal Archangel -> Seal Archangel
	}
	
	public PolymorphingOnKill()
	{
		super(-1, "ai/mobs");
		
		for (int mob : MOBS.keySet())
		{
			addKillId(mob);
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final L2Attackable newNpc = (L2Attackable) addSpawn(MOBS.get(npc.getId()), npc, false, 0);
		
		L2Character originalKiller = isSummon ? killer.getPet() : killer;
		newNpc.setRunning();
		newNpc.addDamageHate(originalKiller, 0, 999);
		newNpc.getAI().setIntention(CtrlIntentionType.ATTACK, originalKiller);
		
		return null;
	}
}
