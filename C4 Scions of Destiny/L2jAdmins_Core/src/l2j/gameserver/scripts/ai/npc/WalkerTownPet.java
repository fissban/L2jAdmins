package l2j.gameserver.scripts.ai.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class WalkerTownPet extends Script
{
	// Spawns
	private static final Map<Integer, LocationHolder> SPAWNS_LOC = new HashMap<>();
	{
		SPAWNS_LOC.put(8202, new LocationHolder(-84608, 150809, -3120));// Maximus
		SPAWNS_LOC.put(8203, new LocationHolder(-13909, 123306, -3112));// Moon Dancer
		SPAWNS_LOC.put(8204, new LocationHolder(19323, 142878, -3032));// Georgio
		SPAWNS_LOC.put(8205, new LocationHolder(81076, 145813, -3528));// Katz
		SPAWNS_LOC.put(8206, new LocationHolder(81947, 54040, -1488));// Ten Ten
		SPAWNS_LOC.put(8207, new LocationHolder(117439, 76028, -2720));// Sardinia
		SPAWNS_LOC.put(8208, new LocationHolder(145188, 30448, -2456));// La Grange
		SPAWNS_LOC.put(8209, new LocationHolder(110572, 219501, -3664));// Misty Rain
		SPAWNS_LOC.put(8266, new LocationHolder(43477, -49561, -792));// Kaiser
		SPAWNS_LOC.put(8758, new LocationHolder(148952, -58402, -2976));// Rafi
	}
	// Instancia de los npc
	private static List<L2Npc> walkers = new ArrayList<>();
	
	public WalkerTownPet()
	{
		super(-1, "ai/npc");
		
		for (Entry<Integer, LocationHolder> spawns : SPAWNS_LOC.entrySet())
		{
			addStartNpc(spawns.getKey());
			addTalkId(spawns.getKey());
			addSpawnId(spawns.getKey());
			// Generamos los spawns
			addSpawn(spawns.getKey(), spawns.getValue(), false, 0);
		}
		
		// Generamos el random walk
		startTimer("walker_route", 2000, null, null, true);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "walker_route":
				if (walkers.isEmpty())
				{
					return null;
				}
				
				for (L2Npc walker : walkers)
				{
					if (walker == null)
					{
						continue;
					}
					// Generamos un nuevo destino
					int randomX = (walker.getSpawn().getX() + Rnd.get(2 * 50)) - 50;
					int randomY = (walker.getSpawn().getY() + Rnd.get(2 * 50)) - 50;
					
					if ((randomX != walker.getX()) && (randomY != walker.getY()))
					{
						walker.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(randomX, randomY, walker.getZ(), 0));
					}
				}
				
				break;
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		walkers.add(npc);
		npc.setRunning();
		return null;
	}
}
