package l2j.gameserver.instancemanager.zone;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.zone.type.TownZone;

public class ZoneTownManager
{
	private static final Map<Integer, TownZone> townZones = new HashMap<>();
	
	private static final int[] CASTLE_ID_ARRAY =
	{
		0,
		0,
		0,
		0,
		0,
		1,
		0,
		2,
		3,
		4,
		5,
		0,
		0,
		6,
		0,
		7,
		2
	};
	
	public ZoneTownManager()
	{
		//
	}
	
	public static void add(TownZone town)
	{
		townZones.put(town.getTownId(), town);
	}
	
	public static TownZone getClosestZone(L2Object activeObject)
	{
		switch (MapRegionData.getInstance().getMapRegion(activeObject.getX(), activeObject.getY()))
		{
			case 0:
				return getZone(2); // TI
			case 1:
				return getZone(3); // Elven
			case 2:
				return getZone(1); // DE
			case 3:
				return getZone(4); // Orc
			case 4:
				return getZone(6); // Dwarven
			case 5:
				return getZone(7); // Gludio
			case 6:
				return getZone(5); // Gludin
			case 7:
				return getZone(8); // Dion
			case 8:
				return getZone(9); // Giran
			case 9:
				return getZone(10); // Oren
			case 10:
				return getZone(12); // Aden
			case 11:
				return getZone(11); // HV
			case 12:
				return getZone(9); // Giran
			case 13:
				return getZone(15); // Heine
			case 14:
				return getZone(14); // Rune
			case 15:
				return getZone(13); // Goddard
			case 16:
				return getZone(8); // Dion
		}
		
		return getZone(12); // Default to Aden
	}
	
	public static boolean townHasCastleInSiege(int x, int y)
	{
		int curtown = (MapRegionData.getInstance().getMapRegion(x, y));
		
		// find an instance of the castle for this town.
		int castleIndex = CASTLE_ID_ARRAY[curtown];
		if (castleIndex > 0)
		{
			Castle castle = CastleData.getInstance().getCastleById(castleIndex);
			if (castle != null)
			{
				return castle.getSiege().isInProgress();
			}
		}
		return false;
	}
	
	public static TownZone getZone(int townId)
	{
		return townZones.get(townId);
	}
	
	public static TownZone getZone(int x, int y, int z)
	{
		for (TownZone temp : townZones.values())
		{
			if (temp.isInsideZone(x, y, z))
			{
				return temp;
			}
		}
		return null;
	}
	
	public static ZoneTownManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ZoneTownManager INSTANCE = new ZoneTownManager();
	}
}
