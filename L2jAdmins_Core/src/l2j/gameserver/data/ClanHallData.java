package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.clanhalls.ClanHall;
import l2j.gameserver.model.entity.clanhalls.auction.Auction;
import l2j.util.UtilPrint;

public class ClanHallData
{
	private static final Logger LOG = Logger.getLogger(ClanHallData.class.getName());
	// lista que contendra lista de CH ordenados por su ubicacion
	private final Map<String, List<ClanHall>> clanHallsLocation = new HashMap<>();
	// lista que contendra todos los CH ordenados por su Id
	private final Map<Integer, ClanHall> clanHallsId = new HashMap<>();
	
	public final void reload()
	{
		clanHallsId.clear();
		clanHallsLocation.clear();
		load();
	}
	
	public final void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM clanhall ORDER BY id");
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int ownerId = rs.getInt("ownerId");
				int lease = rs.getInt("lease");
				String desc = rs.getString("desc");
				String location = rs.getString("location");
				long paidUntil = rs.getLong("paidUntil");
				int grade = rs.getInt("Grade");
				boolean paid = rs.getBoolean("paid");
				
				ClanHall ch = new ClanHall(id, name, ownerId, lease, desc, location, paidUntil, grade, paid);
				
				clanHallsId.put(id, ch);
				
				if (!clanHallsLocation.containsKey(location))
				{
					clanHallsLocation.put(location, new ArrayList<ClanHall>());
				}
				
				clanHallsLocation.get(location).add(ch);
				
				if (ownerId != 0)
				{
					Auction auc = AuctionData.getInstance().getAuction(id);
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
	public final List<ClanHall> getClanHallsByLocation(String location)
	{
		return clanHallsLocation.get(location);
	}
	
	/**
	 * @param  clanHallId the id to use.
	 * @return            a clanHall by its id.
	 */
	public final ClanHall getClanHallById(int clanHallId)
	{
		return clanHallsId.get(clanHallId);
	}
	
	public final ClanHall getNearbyClanHall(int x, int y, int maxDist)
	{
		for (ClanHall ch : clanHallsId.values())
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
	public final ClanHall getClanHallByOwner(Clan clan)
	{
		for (ClanHall clanHall : clanHallsId.values())
		{
			if (clan.getId() == clanHall.getOwnerId())
			{
				return clanHall;
			}
		}
		return null;
	}
	
	/**
	 * Obtenemos una coleccion sin ordenar de todos los CH
	 * @return Collection<ClanHall>
	 */
	public final Collection<ClanHall> getClanHalls()
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
