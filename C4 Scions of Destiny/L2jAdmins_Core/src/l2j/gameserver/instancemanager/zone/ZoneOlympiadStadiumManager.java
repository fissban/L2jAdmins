package l2j.gameserver.instancemanager.zone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;

public class ZoneOlympiadStadiumManager
{
	private static final Map<Integer, OlympiadStadiumZone> olympiadStadiumsZones = new HashMap<>();
	
	public ZoneOlympiadStadiumManager()
	{
		//
	}
	
	public static void add(OlympiadStadiumZone stadium)
	{
		olympiadStadiumsZones.put(stadium.getId(), stadium);
	}
	
	public static OlympiadStadiumZone isInsideInZone(L2Character character)
	{
		return olympiadStadiumsZones.values().stream().filter(zone -> zone.isCharacterInZone(character)).findFirst().orElse(null);
	}
	
	public static OlympiadStadiumZone getZone(int olympiadStadiumId)
	{
		return olympiadStadiumsZones.get(olympiadStadiumId);
	}
	
	public static Collection<OlympiadStadiumZone> getAll()
	{
		return olympiadStadiumsZones.values();
	}
	
	public static ZoneOlympiadStadiumManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneOlympiadStadiumManager INSTANCE = new ZoneOlympiadStadiumManager();
	}
}
