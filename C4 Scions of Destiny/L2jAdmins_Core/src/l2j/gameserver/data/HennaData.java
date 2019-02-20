package l2j.gameserver.data;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.items.ItemHenna;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class HennaData
{
	private static final Logger LOG = Logger.getLogger(HennaData.class.getName());
	
	private static HennaData INSTANCE;
	
	private final Map<Integer, ItemHenna> henna;
	
	public static HennaData getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new HennaData();
		}
		return INSTANCE;
	}
	
	private HennaData()
	{
		henna = new HashMap<>();
		RestoreHennaData();
	}
	
	/**
	 *
	 */
	private void RestoreHennaData()
	{
		try (var con = DatabaseManager.getConnection();
			var ps = con.prepareStatement("SELECT symbol_id, symbol_name, dye_id, dye_amount, price, cancel_fee, stat_INT, stat_STR, stat_CON, stat_MEN, stat_DEX, stat_WIT FROM henna");
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				StatsSet hennaDat = new StatsSet();
				int id = rs.getInt("symbol_id");
				
				hennaDat.set("symbol_id", id);
				// hennaDat.set("symbol_name", rset.getString("symbol_name"));
				hennaDat.set("dye", rs.getInt("dye_id"));
				hennaDat.set("price", rs.getInt("price"));
				hennaDat.set("cancel_fee", rs.getInt("cancel_fee"));
				// amount of dye required
				hennaDat.set("amount", rs.getInt("dye_amount"));
				hennaDat.set("stat_INT", rs.getInt("stat_INT"));
				hennaDat.set("stat_STR", rs.getInt("stat_STR"));
				hennaDat.set("stat_CON", rs.getInt("stat_CON"));
				hennaDat.set("stat_MEN", rs.getInt("stat_MEN"));
				hennaDat.set("stat_DEX", rs.getInt("stat_DEX"));
				hennaDat.set("stat_WIT", rs.getInt("stat_WIT"));
				
				henna.put(id, new ItemHenna(hennaDat));
			}
		}
		catch (Exception e)
		{
			LOG.severe("error while creating henna table " + e);
			e.printStackTrace();
		}
		
		UtilPrint.result("HennaData", "Loaded henna template", henna.size());
		
	}
	
	public ItemHenna getTemplate(int id)
	{
		return henna.get(id);
	}
}
