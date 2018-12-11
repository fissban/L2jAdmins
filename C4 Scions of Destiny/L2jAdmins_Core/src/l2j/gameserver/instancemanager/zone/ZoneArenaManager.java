package l2j.gameserver.instancemanager.zone;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.zone.type.ArenaZone;

public class ZoneArenaManager
{
	private static final Map<Integer, ArenaZone> arenaZones = new HashMap<>();
	
	public ZoneArenaManager()
	{
		//
	}
	
	public static void add(ArenaZone arena)
	{
		arenaZones.put(arena.getId(), arena);
	}
	
	public static ArenaZone isInsideInZone(L2Character character)
	{
		return arenaZones.values().stream().filter(zone -> zone.isCharacterInZone(character)).findFirst().orElse(null);
	}
	
	public static ArenaZone getZone(int arenaId)
	{
		return arenaZones.get(arenaId);
	}
	
	public static ZoneArenaManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneArenaManager INSTANCE = new ZoneArenaManager();
	}
}
