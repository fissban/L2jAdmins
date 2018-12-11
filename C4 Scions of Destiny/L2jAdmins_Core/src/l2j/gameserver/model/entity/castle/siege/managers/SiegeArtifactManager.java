package l2j.gameserver.model.entity.castle.siege.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.instance.L2ArtefactInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.holder.SiegeSpawnHolder;

/**
 * @author fissban
 */
public class SiegeArtifactManager
{
	private final int castleId;
	private final List<L2ArtefactInstance> artifacts = new ArrayList<>();
	
	public SiegeArtifactManager(Castle castle)
	{
		castleId = castle.getId();
	}
	
	private List<SiegeSpawnHolder> getSpawnList()
	{
		if (Config.SIEGE_ARTEFACT_SPAWN_LIST.containsKey(castleId))
		{
			return Config.SIEGE_ARTEFACT_SPAWN_LIST.get(castleId);
		}
		
		return Collections.emptyList();
	}
	
	public int getCount()
	{
		return artifacts.size();
	}
	
	/**
	 * Spawn artifact.
	 */
	public void spawnAll()
	{
		for (SiegeSpawnHolder sp : getSpawnList())
		{
			L2ArtefactInstance art = new L2ArtefactInstance(IdFactory.getInstance().getNextId(), NpcData.getInstance().getTemplate(sp.getNpcId()));
			art.setCurrentHpMp(art.getStat().getMaxHp(), art.getStat().getMaxMp());
			art.setHeading(sp.getLocation().getHeading());
			art.spawnMe(sp.getLocation().getX(), sp.getLocation().getY(), sp.getLocation().getZ() + 50);
			
			artifacts.add(art);
		}
	}
	
	public void removeAll()
	{
		// Remove all instance of artifact for this castle
		for (L2ArtefactInstance art : artifacts)
		{
			if (art != null)
			{
				art.decayMe();
			}
		}
		artifacts.clear();
	}
}
