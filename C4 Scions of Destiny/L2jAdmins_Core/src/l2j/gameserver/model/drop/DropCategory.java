package l2j.gameserver.model.drop;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class DropCategory
{
	/** all drops in category */
	private List<DropInstance> drops;
	/** category chance */
	private int categoryChance;
	/** category type */
	private int categoryType;
	
	public DropCategory(int type, double chance, boolean isRaid)
	{
		categoryType = type;
		categoryChance = (int) ((chance / 100) * DropInstance.MAX_CHANCE);
		
		// increase category chance by configs
		if (isRaid)
		{
			categoryChance *= isRaid ? Config.DROP_CHANCE_RAID : Config.DROP_CHANCE_ITEMS;
		}
		else if (categoryType == 0) // always adena in category 0
		{
			categoryChance *= Config.DROP_CHANCE_ADENA;
		}
		
		// max chance is DropInstance.MAX_CHANCE
		categoryChance = Math.min(categoryChance, DropInstance.MAX_CHANCE);
		
		drops = new ArrayList<>(1);
	}
	
	/**
	 * Add new drop
	 * @param drop
	 * @param raid
	 */
	public void addDrop(DropInstance drop, boolean raid)
	{
		drops.add(drop);
	}
	
	/**
	 * Check Config.DROP_CHANCE_ITEMS_BY_ID and increase or deacrese chances
	 */
	public void balancedDrops()
	{
		var chanceToDecrease = 0;
		for (var d : getAllDrops())
		{
			if (Config.DROP_CHANCE_ITEMS_BY_ID.containsKey(d.getItemId()))
			{
				var chanceIncrease = Config.DROP_CHANCE_ITEMS_BY_ID.get(d.getItemId());
				
				d.increaseChance(chanceIncrease);
				chanceToDecrease += chanceIncrease;
			}
		}
		// if we raise some value by config we must lower the others so that their sum continues giving 100%
		if (chanceToDecrease > 0)
		{
			for (var d : getAllDrops())
			{
				if (!Config.DROP_CHANCE_ITEMS_BY_ID.containsKey(d.getItemId()))
				{
					d.decreaseChance(chanceToDecrease);
				}
			}
		}
	}
	
	/**
	 * Get all drops
	 * @return
	 */
	public List<DropInstance> getAllDrops()
	{
		return drops;
	}
	
	/**
	 * Is category sweep
	 * @return
	 */
	public boolean isSweep()
	{
		return getCategoryType() == -1;
	}
	
	/**
	 * Get category chance
	 * @return
	 */
	public int getCategoryChance()
	{
		return categoryChance;
	}
	
	/**
	 * Get category type<br>
	 * -1 is category sweep
	 * @return
	 */
	public int getCategoryType()
	{
		return categoryType;
	}
	
	/**
	 * One of the drops in this category is to be dropped.
	 * @param  raid
	 * @return      selected drop from category, or null if nothing is dropped.
	 */
	public DropInstance dropOne(boolean raid)
	{
		for (var drop : getAllDrops())
		{
			// drop this item and exit the function
			if (drop.getChance() >= Rnd.get(DropInstance.MAX_CHANCE))
			{
				return drop;
			}
		}
		return null;
	}
}
