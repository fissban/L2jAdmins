package l2j.gameserver.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.model.holder.SeedDataHolder;
import l2j.gameserver.model.items.Item;
import l2j.util.UtilPrint;

/**
 * Service class for manor
 * @author l3x
 */
public class ManorData
{
	private static final Logger LOG = Logger.getLogger(ManorData.class.getName());
	
	private static Map<Integer, SeedDataHolder> seeds = new HashMap<>();
	
	public ManorData()
	{
		//
	}
	
	// TODO pasar a xml
	public void load()
	{
		File seedData = new File(Config.DATAPACK_ROOT, "data/seeds.csv");
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(seedData))))
		{
			String line = null;
			while ((line = lnr.readLine()) != null)
			{
				if ((line.trim().length() == 0) || line.startsWith("#"))
				{
					continue;
				}
				
				SeedDataHolder seed = parseList(line);
				seeds.put(seed.getId(), seed);
			}
		}
		catch (Exception e)
		{
			LOG.info("ManorData: seeds.csv is missing in data folder");
		}
		
		UtilPrint.result("ManorData", "Loaded seeds", seeds.size());
	}
	
	public List<Integer> getAllCrops()
	{
		List<Integer> crops = new ArrayList<>();
		
		for (SeedDataHolder seed : seeds.values())
		{
			if (!crops.contains(seed.getCrop()) && (seed.getCrop() != 0) && !crops.contains(seed.getCrop()))
			{
				crops.add(seed.getCrop());
			}
		}
		
		return crops;
	}
	
	public int getSeedBasicPrice(int seedId)
	{
		Item seedItem = ItemData.getInstance().getTemplate(seedId);
		
		if (seedItem == null)
		{
			return 0;
		}
		
		return seedItem.getReferencePrice();
	}
	
	public int getSeedBasicPriceByCrop(int cropId)
	{
		for (SeedDataHolder seed : seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return getSeedBasicPrice(seed.getId());
			}
		}
		
		return 0;
	}
	
	public int getCropBasicPrice(int cropId)
	{
		Item cropItem = ItemData.getInstance().getTemplate(cropId);
		
		if (cropItem == null)
		{
			return 0;
		}
		
		return cropItem.getReferencePrice();
	}
	
	public int getMatureCrop(int cropId)
	{
		for (SeedDataHolder seed : seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getMature();
			}
		}
		return 0;
	}
	
	public int getSeedBuyPrice(int seedId)
	{
		int buyPrice = getSeedBasicPrice(seedId) / 10;
		return (buyPrice > 0 ? buyPrice : 1);
	}
	
	public int getSeedMinLevel(int seedId)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return -1;
		}
		
		return seed.getLevel() - 5;
	}
	
	public int getSeedMaxLevel(int seedId)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return -1;
		}
		
		return seed.getLevel() + 5;
	}
	
	public int getSeedLevelByCrop(int cropId)
	{
		for (SeedDataHolder seed : seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getLevel();
			}
		}
		
		return 0;
	}
	
	public int getSeedLevel(int seedId)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return -1;
		}
		
		return seed.getLevel();
	}
	
	public boolean isAlternative(int seedId)
	{
		for (SeedDataHolder seed : seeds.values())
		{
			if (seed.getId() == seedId)
			{
				return seed.isAlternative();
			}
		}
		return false;
	}
	
	public int getCropType(int seedId)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return -1;
		}
		
		return seed.getCrop();
	}
	
	public synchronized int getRewardItem(int cropId, int type)
	{
		for (SeedDataHolder seed : seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getReward(type);
			}
		}
		return -1;
	}
	
	public synchronized int getRewardItemBySeed(int seedId, int type)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return 0;
		}
		
		return seed.getReward(type);
	}
	
	/**
	 * Return all crops which can be purchased by given castle
	 * @param  castleId
	 * @return
	 */
	public List<Integer> getCropsForCastle(int castleId)
	{
		List<Integer> crops = new ArrayList<>();
		
		for (SeedDataHolder seed : seeds.values())
		{
			if ((seed.getManorId() == castleId) && !crops.contains(seed.getCrop()))
			{
				crops.add(seed.getCrop());
			}
		}
		
		return crops;
	}
	
	/**
	 * Return list of seed ids, which belongs to castle with given id
	 * @param  castleId - id of the castle
	 * @return          seedIds - list of seed ids
	 */
	public List<Integer> getSeedsForCastle(int castleId)
	{
		List<Integer> seedsID = new ArrayList<>();
		
		for (SeedDataHolder seed : seeds.values())
		{
			if ((seed.getManorId() == castleId) && !seedsID.contains(seed.getId()))
			{
				seedsID.add(seed.getId());
			}
		}
		
		return seedsID;
	}
	
	public int getCastleIdForSeed(int seedId)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return 0;
		}
		
		return seed.getManorId();
	}
	
	public int getSeedSaleLimit(int seedId)
	{
		SeedDataHolder seed = seeds.get(seedId);
		
		if (seed == null)
		{
			return 0;
		}
		
		return seed.getSeedLimit();
	}
	
	public int getCropPuchaseLimit(int cropId)
	{
		for (SeedDataHolder seed : seeds.values())
		{
			if (seed.getCrop() == cropId)
			{
				return seed.getCropLimit();
			}
		}
		
		return 0;
	}
	
	private SeedDataHolder parseList(String line)
	{
		StringTokenizer st = new StringTokenizer(line, ";");
		
		int seedId = Integer.parseInt(st.nextToken()); // seed id
		int level = Integer.parseInt(st.nextToken()); // seed level
		int cropId = Integer.parseInt(st.nextToken()); // crop id
		int matureId = Integer.parseInt(st.nextToken()); // mature crop id
		int type1R = Integer.parseInt(st.nextToken()); // type I reward
		int type2R = Integer.parseInt(st.nextToken()); // type II reward
		int manorId = Integer.parseInt(st.nextToken()); // id of manor, where seed can be farmed
		int isAlt = Integer.parseInt(st.nextToken()); // alternative seed
		int limitSeeds = Integer.parseInt(st.nextToken()); // limit for seeds
		int limitCrops = Integer.parseInt(st.nextToken()); // limit for crops
		
		SeedDataHolder seed = new SeedDataHolder(level, cropId, matureId);
		seed.setData(seedId, type1R, type2R, manorId, isAlt, limitSeeds, limitCrops);
		
		return seed;
	}
	
	public static ManorData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ManorData INSTANCE = new ManorData();
	}
}
