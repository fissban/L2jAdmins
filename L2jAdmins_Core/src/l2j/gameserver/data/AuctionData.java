package l2j.gameserver.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.L2DatabaseFactory;
import l2j.gameserver.model.entity.clanhalls.auction.Auction;
import l2j.util.UtilPrint;

public class AuctionData
{
	protected static final Logger LOG = Logger.getLogger(AuctionData.class.getName());
	
	private final Map<Integer, Auction> auctions = new HashMap<>();
	
	private static final String[] ITEM_INIT_DATA =
	{
		"(22, 0, 'NPC', 'NPC Clan', 22, 'Moonstone Hall', 20000000, 0, 1164841200000)",
		"(23, 0, 'NPC', 'NPC Clan', 23, 'Onyx Hall', 20000000, 0, 1164841200000)",
		"(24, 0, 'NPC', 'NPC Clan', 24, 'Topaz Hall', 20000000, 0, 1164841200000)",
		"(25, 0, 'NPC', 'NPC Clan', 25, 'Ruby Hall', 20000000, 0, 1164841200000)",
		"(26, 0, 'NPC', 'NPC Clan', 26, 'Crystal Hall', 20000000, 0, 1164841200000)",
		"(27, 0, 'NPC', 'NPC Clan', 27, 'Onyx Hall', 20000000, 0, 1164841200000)",
		"(28, 0, 'NPC', 'NPC Clan', 28, 'Sapphire Hall', 20000000, 0, 1164841200000)",
		"(29, 0, 'NPC', 'NPC Clan', 29, 'Moonstone Hall', 20000000, 0, 1164841200000)",
		"(30, 0, 'NPC', 'NPC Clan', 30, 'Emerald Hall', 20000000, 0, 1164841200000)",
		"(31, 0, 'NPC', 'NPC Clan', 31, 'The Atramental Barracks', 8000000, 0, 1164841200000)",
		"(32, 0, 'NPC', 'NPC Clan', 32, 'The Scarlet Barracks', 8000000, 0, 1164841200000)",
		"(33, 0, 'NPC', 'NPC Clan', 33, 'The Viridian Barracks', 8000000, 0, 1164841200000)",
		"(36, 0, 'NPC', 'NPC Clan', 36, 'The Golden Chamber', 50000000, 0, 1164841200000)",
		"(37, 0, 'NPC', 'NPC Clan', 37, 'The Silver Chamber', 50000000, 0, 1164841200000)",
		"(38, 0, 'NPC', 'NPC Clan', 38, 'The Mithril Chamber', 50000000, 0, 1164841200000)",
		"(39, 0, 'NPC', 'NPC Clan', 39, 'Silver Manor', 50000000, 0, 1164841200000)",
		"(40, 0, 'NPC', 'NPC Clan', 40, 'Gold Manor', 50000000, 0, 1164841200000)",
		"(41, 0, 'NPC', 'NPC Clan', 41, 'The Bronze Chamber', 50000000, 0, 1164841200000)",
		"(42, 0, 'NPC', 'NPC Clan', 42, 'The Golden Chamber', 50000000, 0, 1164841200000)",
		"(43, 0, 'NPC', 'NPC Clan', 43, 'The Silver Chamber', 50000000, 0, 1164841200000)",
		"(44, 0, 'NPC', 'NPC Clan', 44, 'The Mithril Chamber', 50000000, 0, 1164841200000)",
		"(45, 0, 'NPC', 'NPC Clan', 45, 'The Bronze Chamber', 50000000, 0, 1164841200000)",
		"(46, 0, 'NPC', 'NPC Clan', 46, 'Silver Manor', 50000000, 0, 1164841200000)",
		"(47, 0, 'NPC', 'NPC Clan', 47, 'Moonstone Hall', 50000000, 0, 1164841200000)",
		"(48, 0, 'NPC', 'NPC Clan', 48, 'Onyx Hall', 50000000, 0, 1164841200000)",
		"(49, 0, 'NPC', 'NPC Clan', 49, 'Emerald Hall', 50000000, 0, 1164841200000)",
		"(50, 0, 'NPC', 'NPC Clan', 50, 'Sapphire Hall', 50000000, 0, 1164841200000)"
	};
	
	//@formatter:off
	private static final Integer[] ITEM_INIT_DATA_ID =
	{
		22,	23,	24,	25,	26,	27,	28,	29,	30,	31,	32,	33,	36,	37,	38,	39,	40,	41,	42,	43,	44,	45,	46,	47,	48,	49,	50
	};
	//@formatter:on
	// SQL
	private static final String SELECT = "SELECT id FROM auction ORDER BY id";
	private static final String INSERT = "INSERT INTO auction VALUES %item%";
	
	public void reload()
	{
		auctions.clear();
		load();
	}
	
	public final void load()
	{
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var ps = con.prepareStatement(SELECT);
			var rs = ps.executeQuery())
		{
			while (rs.next())
			{
				var id = rs.getInt("id");
				auctions.put(id, new Auction(id));
			}
		}
		catch (Exception e)
		{
			LOG.warning(AuctionData.class.getSimpleName() + ": Exception: Auction.load(): " + e.getMessage() + e);
		}
		
		UtilPrint.result("AuctionData", "Loaded auctions", auctions.size());
	}
	
	public final Auction getAuction(int auctionId)
	{
		return auctions.getOrDefault(auctionId, null);
	}
	
	public final Collection<Auction> getAuctions()
	{
		return auctions.values();
	}
	
	/**
	 * Init Clan NPC aution
	 * @param  id
	 * @return
	 */
	public boolean initNPC(int id)
	{
		var i = 0;
		
		for (; i < ITEM_INIT_DATA_ID.length; i++)
		{
			if (ITEM_INIT_DATA_ID[i] == id)
			{
				break;
			}
		}
		
		if (i >= ITEM_INIT_DATA_ID.length)
		{
			LOG.warning(AuctionData.class.getSimpleName() + ": Clan Hall auction not found for Id :" + id);
			return false;
		}
		
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var ps = con.prepareStatement(INSERT.replace("%item%", ITEM_INIT_DATA[i])))
		{
			ps.execute();
			LOG.info(AuctionData.class.getSimpleName() + ": Created auction for ClanHall: " + id);
		}
		catch (final Exception e)
		{
			LOG.warning(AuctionData.class.getSimpleName() + ": Exception: Auction.initNPC(): " + e.getMessage() + e);
			return false;
		}
		return true;
	}
	
	public static AuctionData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionData INSTANCE = new AuctionData();
	}
}
