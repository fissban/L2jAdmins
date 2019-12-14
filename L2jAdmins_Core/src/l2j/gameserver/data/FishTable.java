package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.manager.pc.fishing.FishingHolder;
import l2j.gameserver.model.actor.manager.pc.fishing.enums.FishLureType;
import l2j.util.UtilPrint;

/**
 * @author -Nemesiss-
 */
public class FishTable
{
	private static final Logger LOG = Logger.getLogger(FishTable.class.getName());
	
	private static final List<FishingHolder> fishs = new ArrayList<>();
	private static final List<FishingHolder> fishsNewbie = new ArrayList<>();
	
	public void load()
	{
		// Create table that contains all fish datas
		fishsNewbie.clear();
		fishs.clear();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT id, level, name, hp, hpregen, fish_type, fish_group, fish_guts, guts_check_time, wait_time, combat_time FROM fish ORDER BY id");
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				int id = rs.getInt("id");
				int lvl = rs.getInt("level");
				String name = rs.getString("name");
				int hp = rs.getInt("hp");
				int hpreg = rs.getInt("hpregen");
				int type = rs.getInt("fish_type");
				FishLureType lureType = FishLureType.values()[rs.getInt("fish_group")];// TODO cuando se ajuste en xml grabar este dato como enumerador
				int fishGuts = rs.getInt("fish_guts");
				int gutsCheck_time = rs.getInt("guts_check_time");
				int waitTime = rs.getInt("wait_time");
				int combatTime = rs.getInt("combat_time");
				
				FishingHolder fish = new FishingHolder(id, lvl, name, hp, hpreg, type, lureType, fishGuts, gutsCheck_time, waitTime, combatTime);
				
				switch (lureType)
				{
					case NEWBIE:
						fishsNewbie.add(fish);
						break;
					
					case NORMAL:
						fishs.add(fish);
						break;
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("FishTable: Error while creating fishes table" + e);
		}
		
		UtilPrint.result("FishTable", "Loaded Fishes", fishsNewbie.size() + fishs.size());
	}
	
	/**
	 * @param  lvl
	 * @param  type
	 * @param  group
	 * @return       List of Fish that can be fished
	 */
	public List<FishingHolder> getfish(int lvl, int type, int group)
	{
		List<FishingHolder> result = new ArrayList<>();
		List<FishingHolder> fishing = new ArrayList<>();
		if (group == 0)
		{
			fishing = fishsNewbie;
		}
		else
		{
			fishing = fishs;
		}
		
		if (fishing.isEmpty())
		{
			// the fish list is empty
			LOG.warning("Fish are not defined !");
			return fishing;
		}
		
		for (FishingHolder f : fishing)
		{
			if (f.getLevel() != lvl)
			{
				continue;
			}
			if (f.getType() != type)
			{
				continue;
			}
			
			result.add(f);
		}
		if (result.isEmpty())
		{
			LOG.warning("FishTable: Cant Find Any Fish!? - Lvl: " + lvl + " Type: " + type);
		}
		return result;
	}
	
	public static FishTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FishTable INSTANCE = new FishTable();
	}
}
