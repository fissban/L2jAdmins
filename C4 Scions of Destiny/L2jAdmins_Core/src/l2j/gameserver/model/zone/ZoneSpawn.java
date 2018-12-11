package l2j.gameserver.model.zone;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.gameserver.model.holder.LocationHolder;
import l2j.util.Rnd;

/**
 * Abstract zone with spawn locations
 * @author DS
 */
public abstract class ZoneSpawn extends Zone
{
	private final List<LocationHolder> spawnLocs = new CopyOnWriteArrayList<>();
	private final List<LocationHolder> chaoticSpawnLocs = new CopyOnWriteArrayList<>();
	
	public ZoneSpawn(int id)
	{
		super(id);
	}
	
	public final void addSpawn(int x, int y, int z)
	{
		spawnLocs.add(new LocationHolder(x, y, z));
	}
	
	public final void addChaoticSpawn(int x, int y, int z)
	{
		chaoticSpawnLocs.add(new LocationHolder(x, y, z));
	}
	
	public LocationHolder getSpawnLoc()
	{
		return spawnLocs.get(Rnd.get(spawnLocs.size()));
	}
	
	public List<LocationHolder> getSpawns()
	{
		return spawnLocs;
	}
	
	public LocationHolder getChaoticSpawnLoc()
	{
		if (chaoticSpawnLocs != null)
		{
			return chaoticSpawnLocs.get(Rnd.get(chaoticSpawnLocs.size()));
		}
		return getSpawnLoc();
	}
}
