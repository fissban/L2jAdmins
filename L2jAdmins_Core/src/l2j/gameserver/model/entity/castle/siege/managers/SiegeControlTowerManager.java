package l2j.gameserver.model.entity.castle.siege.managers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2ControlTowerInstance;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.SiegeSpawnHolder;

/**
 * @author fissban
 */
public class SiegeControlTowerManager
{
	private final int castleId;
	private final Map<Integer, L2ControlTowerInstance> controlTowers = new HashMap<>();
	private int controlTowerCount;
	
	public SiegeControlTowerManager(Castle castle)
	{
		castleId = castle.getId();
	}
	
	private final List<SiegeSpawnHolder> getSpawnList()
	{
		if (Config.SIEGE_CONTROL_TOWER_SPAWN_LIST.containsKey(castleId))
		{
			return Config.SIEGE_CONTROL_TOWER_SPAWN_LIST.get(castleId);
		}
		return Collections.emptyList();
	}
	
	/**
	 * Spawn all control tower.
	 */
	public void spawnAll()
	{
		for (SiegeSpawnHolder sp : getSpawnList())
		{
			NpcTemplate template = NpcData.getInstance().getTemplate(sp.getNpcId());
			
			L2ControlTowerInstance ct = new L2ControlTowerInstance(IdFactory.getInstance().getNextId(), template);
			ct.setCurrentHpMp(sp.getHp(), ct.getStat().getMaxMp());
			ct.setHeading(sp.getLocation().getHeading());
			ct.spawnMe(sp.getLocation().getX(), sp.getLocation().getY(), sp.getLocation().getZ() + 20);
			
			controlTowers.put(ct.getObjectId(), ct);
		}
	}
	
	/**
	 * Remove all control tower spawned.
	 */
	public void removeAll()
	{
		// Remove all instance of control tower for this castle
		for (L2ControlTowerInstance ct : controlTowers.values())
		{
			if (ct != null)
			{
				ct.decayMe();
			}
		}
		
		controlTowers.clear();
	}
	
	public int getCount()
	{
		return controlTowerCount;
	}
	
	public Collection<L2ControlTowerInstance> getAll()
	{
		return controlTowers.values();
	}
	
	/**
	 * Control Tower was killed
	 * @param ct
	 */
	public void killed(L2ControlTowerInstance ct)
	{
		controlTowers.remove(ct.getObjectId());
	}
}
