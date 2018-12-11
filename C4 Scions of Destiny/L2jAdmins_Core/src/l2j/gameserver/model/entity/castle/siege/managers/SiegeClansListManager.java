package l2j.gameserver.model.entity.castle.siege.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import l2j.gameserver.model.entity.castle.siege.SiegeClanHolder;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;

/**
 * @author fissban
 */
public class SiegeClansListManager
{
	private final Map<Integer, SiegeClanHolder> siegeClans = new HashMap<>();
	
	public SiegeClansListManager()
	{
		
	}
	
	/**
	 * It is added to the list of clans participating in the siege a new clan.
	 * @param type
	 * @param sc
	 */
	public void addClan(SiegeClanType type, SiegeClanHolder sc)
	{
		if (siegeClans.containsKey(sc.getClanId()))
		{
			siegeClans.get(sc.getClanId()).setSiegeClanType(type);
		}
		else
		{
			siegeClans.put(sc.getClanId(), sc);
		}
	}
	
	/**
	 * It is added to the list of clans participating in the siege a new clan.
	 * @param type
	 * @param clanId
	 */
	public void addClan(SiegeClanType type, int clanId)
	{
		if (siegeClans.containsKey(clanId))
		{
			siegeClans.get(clanId).setSiegeClanType(type);
		}
		else
		{
			siegeClans.put(clanId, new SiegeClanHolder(clanId, type));
		}
	}
	
	public void clearAll()
	{
		siegeClans.clear();
	}
	
	public void clearByType(SiegeClanType... siegeClanType)
	{
		Map<Integer, SiegeClanHolder> list = siegeClans;
		
		for (Entry<Integer, SiegeClanHolder> sc : list.entrySet())
		{
			for (SiegeClanType type : siegeClanType)
			{
				if (sc.getValue().getSiegeClanType() == type)
				{
					siegeClans.remove(sc.getKey());
				}
			}
		}
	}
	
	/**
	 * The list of participants clans of harvest is obtained according to the type of participation (SiegeClanType)
	 * @param  siegeClanType
	 * @return
	 */
	public List<SiegeClanHolder> getClanList(SiegeClanType... siegeClanType)
	{
		List<SiegeClanHolder> list = new ArrayList<>();
		
		for (SiegeClanHolder sc : siegeClans.values())
		{
			for (SiegeClanType type : siegeClanType)
			{
				if (sc.getSiegeClanType() == type)
				{
					list.add(sc);
				}
			}
		}
		return list;
	}
	
	/**
	 * the list of participants clans of harvest is obtained according to the type of participation (SiegeClanType) and clanId
	 * @param  siegeClanType
	 * @param  clanId
	 * @return
	 */
	public SiegeClanHolder getClan(SiegeClanType siegeClanType, int clanId)
	{
		for (SiegeClanHolder sc : siegeClans.values())
		{
			if (sc.getSiegeClanType().equals(siegeClanType) && (sc.getClanId() == clanId))
			{
				return sc;
			}
		}
		
		return null;
	}
	
	/**
	 * Remove all flags.
	 * @param siegeClanType
	 */
	public void removeFlags(SiegeClanType... siegeClanType)
	{
		for (SiegeClanHolder sc : getClanList(siegeClanType))
		{
			if (sc != null)
			{
				sc.removeAllFlags();
			}
		}
	}
}
