package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.util.UtilPrint;

public class ClanHallData
{
	private static final Logger LOG = Logger.getLogger(ClanHallData.class.getName());
	// list that will contain list of CH ordered by their location
	private final static Map<String, List<ClanHall>> clanHallsLocation = new HashMap<>();
	// list that will contain all CH ordered by its Id
	private final static Map<Integer, ClanHall> clanHallsId = new HashMap<>();
	
	public final void reload()
	{
		clanHallsId.clear();
		clanHallsLocation.clear();
		load();
	}
	
	public void load()
	{
		try (var con = DatabaseManager.getConnection();
			var ps = con.prepareStatement("SELECT * FROM clanhall ORDER BY id");
			var rs = ps.executeQuery())
		{
			while (rs.next())
			{
				var id = rs.getInt("id");
				var name = rs.getString("name");
				var ownerId = rs.getInt("ownerId");
				var lease = rs.getInt("lease");
				var desc = rs.getString("desc");
				var location = rs.getString("location");
				var paidUntil = rs.getLong("paidUntil");
				var grade = rs.getInt("Grade");
				var paid = rs.getBoolean("paid");
				
				var ch = new ClanHall(id, name, ownerId, lease, desc, location, paidUntil, grade, paid);
				
				clanHallsId.put(id, ch);
				
				if (!clanHallsLocation.containsKey(location))
				{
					clanHallsLocation.put(location, new ArrayList<ClanHall>());
				}
				
				clanHallsLocation.get(location).add(ch);
				
				if (ownerId != 0)
				{
					var auc = AuctionData.getInstance().getAuction(id);
					if ((auc == null) && (lease > 0))
					{
						AuctionData.getInstance().initNPC(id);
					}
				}
			}
			
			UtilPrint.result("ClanHallData", "Loaded clan halls ", clanHallsId.size());
		}
		catch (Exception e)
		{
			LOG.info("ClanHall Manager: Exception: ClanHallManager.load(): " + e);
		}
	}
	
	/**
	 * @param  location
	 * @return          Map with all ClanHalls which are in location
	 */
	public static List<ClanHall> getClanHallsByLocation(String location)
	{
		return clanHallsLocation.get(location);
	}
	
	/**
	 * @param  clanHallId the id to use.
	 * @return            a clanHall by its id.
	 */
	public static ClanHall getClanHallById(int clanHallId)
	{
		return clanHallsId.get(clanHallId);
	}
	
	public static ClanHall getNearbyClanHall(int x, int y, int maxDist)
	{
		for (var ch : clanHallsId.values())
		{
			if ((ch.getZone() != null) && (ch.getZone().getDistanceToZone(x, y) < maxDist))
			{
				return ch;
			}
		}
		return null;
	}
	
	/**
	 * @param  clan the clan to use.
	 * @return      a clanHall by its owner.
	 */
	public static ClanHall getClanHallByOwner(Clan clan)
	{
		for (var clanHall : clanHallsId.values())
		{
			if (clan.getId() == clanHall.getOwnerId())
			{
				return clanHall;
			}
		}
		return null;
	}
	
	/**
	 * Get an unordered collection of all CHs
	 * @return Collection<ClanHall>
	 */
	public static Collection<ClanHall> getClanHalls()
	{
		return clanHallsId.values();
	}
	
	public static ClanHallData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanHallData INSTANCE = new ClanHallData();
	}
}
