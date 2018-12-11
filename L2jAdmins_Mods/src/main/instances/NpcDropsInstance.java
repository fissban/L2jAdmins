package main.instances;

import java.util.HashMap;
import java.util.Map;

import main.enums.ItemDropType;
import main.holders.DropBonusHolder;
import l2j.Config;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.drop.DropCategory;
import l2j.gameserver.model.drop.DropInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class NpcDropsInstance
{
	private final Map<ItemDropType, DropBonusHolder> dropsSettings = new HashMap<>();
	{
		dropsSettings.put(ItemDropType.NORMAL, new DropBonusHolder());
		dropsSettings.put(ItemDropType.SPOIL, new DropBonusHolder());
		dropsSettings.put(ItemDropType.SEED, new DropBonusHolder());
	}
	
	public NpcDropsInstance()
	{
		//
	}
	
	public void increaseDrop(ItemDropType type, double chance, double amount)
	{
		dropsSettings.get(type).increaseAmountBonus(amount);
		dropsSettings.get(type).increaseChanceBonus(chance);
	}
	
	public boolean hasSettings()
	{
		for (DropBonusHolder holder : dropsSettings.values())
		{
			if (holder.getAmountBonus() > 1.0 || holder.getChanceBonus() > 1.0)
			{
				return true;
			}
		}
		return false;
	}
	
	public void init(L2Attackable npc, L2Character mainDamageDealer)
	{
		if (mainDamageDealer == null)
		{
			return;
		}
		
		L2PcInstance player = mainDamageDealer.getActingPlayer();
		
		if (player == null)
		{
			return; // Don't drop anything if the last attacker or owner isn't L2PcInstance
		}
		
		final int levelModifier = calculateLevelModifierForDrop(npc, player); // level modifier in %'s (will be subtracted from drop chance)
		
		// now throw all categorized drops and handle spoil.
		for (final DropCategory cat : npc.getTemplate().getDropsCategory())
		{
			ItemHolder item = null;
			if (cat.isSweep())
			{
				// according to sh1ny, seeded mobs CAN be spoiled and sweep.
				if (npc.isSpoil())
				{
					for (DropInstance drop : cat.getAllDrops())
					{
						item = calculateSweepDrop(npc, player, drop, levelModifier, true);
						if (item == null)
						{
							continue;
						}
						
						npc.takeSweep().add(item);
					}
				}
			}
			else
			{
				item = calculateCategoryDrop(npc, player, cat, levelModifier);
				
				if (item != null)
				{
					// Check if the autoLoot mode is active
					if (!player.isFullAdenaInventory(item.getId()) && (npc.isRaid() && Config.AUTO_LOOT_RAIDS || Config.AUTO_LOOT && !npc.isRaid()))
					{
						player.doAutoLoot(npc, item); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable
					}
					else
					{
						npc.dropItem(player, item); // drop the item on the ground
					}
					
					// Broadcast message if RaidBoss was defeated
					if (npc instanceof L2RaidBossInstance)
					{
						npc.broadcastPacket(new SystemMessage(SystemMessage.C1_DIED_DROPPED_S3_S2).addString(npc.getName()).addItemName(item.getId()).addNumber(item.getCount()));
					}
				}
			}
		}
		
	}
	
	// ----------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Calculates quantity of items for specific drop CATEGORY according to current situation <br>
	 * Only a max of ONE item from a category is allowed to be dropped.
	 * @param drop The L2DropData count is being calculated for
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @param categoryDrops
	 * @param deepBlueDrop Factor to divide the drop chance
	 * @param levelModifier level modifier in %'s (will be subtracted from drop chance)
	 * @return
	 */
	private ItemHolder calculateCategoryDrop(L2Attackable npc, L2PcInstance lastAttacker, DropCategory categoryDrops, int levelModifier)
	{
		if (categoryDrops == null)
		{
			return null;
		}
		
		// Get default drop chance for the category (that's the sum of chances for all items in the category)
		// keep track of the base category chance as it'll be used later, if an item is drop from the category.
		// for everything else, use the total "categoryDropChance"
		int categoryDropChance = categoryDrops.getCategoryChance();
		
		if (Config.DEEPBLUE_DROP_RULES)
		{
			// We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
			// NOTE: This is valid only for adena drops! Others drops will still obey server's rate
			int deepBlueDrop = levelModifier > 0 ? 3 : 1;
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			categoryDropChance = (categoryDropChance - categoryDropChance * levelModifier / 100) / deepBlueDrop;
		}
		
		// Applies Drop rates
		categoryDropChance *= npc.isRaid() ? Config.DROP_CHANCE_RAID : Config.DROP_CHANCE_ITEMS;
		// Applie bonus
		categoryDropChance *= dropsSettings.get(ItemDropType.NORMAL).getChanceBonus();
		
		// Check if an Item from this category must be dropped
		if (Rnd.get(DropInstance.MAX_CHANCE) < Math.max(1, categoryDropChance))
		{
			final DropInstance drop = categoryDrops.dropOne(npc.isRaid());
			if (drop == null)
			{
				return null;
			}
			
			// Define the number of items to be obtained from what defined within our configs files.
			int itemCount = getAmountDropItems(npc, drop, false);
			
			if (itemCount > 0)
			{
				return new ItemHolder(drop.getItemId(), itemCount);
			}
		}
		return null;
	}
	
	/**
	 * Calculates quantity of items for specific drop according to current situation <br>
	 * @param drop The L2DropData count is being calculated for
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @param deepBlueDrop Factor to divide the drop chance
	 * @param levelModifier level modifier in %'s (will be subtracted from drop chance)
	 * @param isSweep
	 * @return
	 */
	private ItemHolder calculateSweepDrop(L2Attackable npc, L2PcInstance lastAttacker, DropInstance drop, int levelModifier, boolean isSweep)
	{
		// Get default drop chance
		int dropChance = drop.getChance();
		
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int deepBlueDrop = 1;
			
			if (levelModifier > 0)
			{
				// We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
				// NOTE: This is valid only for adena drops! Others drops will still obey server's rate
				deepBlueDrop = 3;
				if (drop.getItemId() == Inventory.ADENA_ID)
				{
					deepBlueDrop *= npc.isRaid() ? (int) Config.DROP_CHANCE_RAID : (int) Config.DROP_CHANCE_ITEMS;
					
					// avoid div by 0
					if (deepBlueDrop == 0)
					{
						deepBlueDrop = 1;
					}
				}
			}
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			dropChance = (drop.getChance() - drop.getChance() * levelModifier / 100) / deepBlueDrop;
		}
		
		// Define a chance to drop depending on the defined configs.
		dropChance = getChanceDropItems(npc, drop);
		
		dropChance *= dropsSettings.get(ItemDropType.SPOIL).getChanceBonus();
		
		// Set our limits for chance of drop
		if (dropChance < 1)
		{
			dropChance = 1;
		}
		
		if (dropChance > Rnd.get(DropInstance.MAX_CHANCE))
		{
			// Define the number of items to be obtained from what defined within our configs files.
			int itemCount = getAmountDropItems(npc, drop, isSweep);
			
			if (itemCount > 0)
			{
				return new ItemHolder(drop.getItemId(), itemCount);
			}
		}
		
		return null;
	}
	
	/**
	 * We define the number of items to be obtained from what defined within our configs files.
	 * @param drop
	 * @param itemChance
	 * @param isSweep
	 * @return
	 */
	private int getAmountDropItems(L2Attackable npc, DropInstance drop, boolean isSweep)
	{
		int itemId = drop.getItemId();
		int itemAmount = 0;
		
		// Get min and max Item quantity
		final int minCount = drop.getMinDrop();
		final int maxCount = drop.getMaxDrop();
		
		// Get the item quantity dropped
		if (minCount < maxCount)
		{
			itemAmount += Rnd.get(minCount, maxCount);
		}
		else if (minCount == maxCount)
		{
			itemAmount += minCount;
		}
		else
		{
			itemAmount++;
		}
		
		// Prevents continue if the result is 0
		if (itemAmount == 0)
		{
			return 0;
		}
		
		switch (itemId)
		{
			case Inventory.ADENA_ID:
				itemAmount *= Config.DROP_AMOUNT_ADENA;
				break;
			
			case SevenSignsManager.SEAL_STONE_BLUE_ID:
			case SevenSignsManager.SEAL_STONE_RED_ID:
			case SevenSignsManager.SEAL_STONE_GREEN_ID:
				itemAmount *= Config.DROP_AMOUNT_SEAL_STONE;
				break;
			
			default:
				if (Config.DROP_AMOUNT_ITEMS_BY_ID.containsKey(itemId))
				{
					itemAmount *= Config.DROP_AMOUNT_ITEMS_BY_ID.get(itemId);
				}
				else if (isSweep)
				{
					itemAmount *= Config.DROP_AMOUNT_SPOIL;
				}
				else if (npc.isRaid())
				{
					itemAmount *= Config.DROP_AMOUNT_RAID;
				}
				else
				{
					itemAmount *= Config.DROP_AMOUNT_ITEMS;
				}
		}
		
		if (isSweep)
		{
			itemAmount *= dropsSettings.get(ItemDropType.SPOIL).getAmountBonus();
		}
		else
		{
			itemAmount *= dropsSettings.get(ItemDropType.NORMAL).getAmountBonus();
		}
		
		return itemAmount;
	}
	
	/**
	 * We define a chance to drop depending on the defined configs.
	 * @param drop
	 * @return
	 */
	private static int getChanceDropItems(L2Attackable npc, DropInstance drop)
	{
		int dropChance = drop.getChance();
		
		if (drop.getItemId() == Inventory.ADENA_ID)
		{
			dropChance *= Config.DROP_CHANCE_ADENA;
		}
		else if (Config.DROP_CHANCE_ITEMS_BY_ID.containsKey(drop.getItemId()))
		{
			dropChance *= Config.DROP_CHANCE_ITEMS_BY_ID.get(drop.getItemId());
		}
		else
		{
			dropChance *= npc.isRaid() ? Config.DROP_CHANCE_RAID : Config.DROP_CHANCE_ITEMS;
		}
		
		return dropChance;
	}
	
	/**
	 * Calculates the level modifier for drop<br>
	 * @param lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @return
	 */
	private static int calculateLevelModifierForDrop(L2Attackable npc, L2PcInstance lastAttacker)
	{
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int highestLevel = lastAttacker.getLevel();
			
			// Check to prevent very high level player to nearly kill mob and let low level player do the last hit.
			if (npc.getAttackByList() != null && !npc.getAttackByList().isEmpty())
			{
				for (L2Character atkChar : npc.getAttackByList())
				{
					if (atkChar != null && atkChar.getLevel() > highestLevel)
					{
						highestLevel = atkChar.getLevel();
					}
				}
			}
			
			// According to official data (Prima), deep blue mobs are 9 or more levels below players
			if (highestLevel - 9 >= npc.getLevel())
			{
				return (highestLevel - (npc.getLevel() + 8)) * 9;
			}
		}
		
		return 0;
	}
}
