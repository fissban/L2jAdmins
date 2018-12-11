package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.item.ItemAdvQuestItems;
import l2j.gameserver.handler.item.ItemBeastSoulShot;
import l2j.gameserver.handler.item.ItemBeastSpice;
import l2j.gameserver.handler.item.ItemBeastSpiritShot;
import l2j.gameserver.handler.item.ItemBlessedSpiritShot;
import l2j.gameserver.handler.item.ItemBook;
import l2j.gameserver.handler.item.ItemCalculator;
import l2j.gameserver.handler.item.ItemCharChangePotions;
import l2j.gameserver.handler.item.ItemChestKey;
import l2j.gameserver.handler.item.ItemChristmasTree;
import l2j.gameserver.handler.item.ItemCompBlessedSpiritShotPacks;
import l2j.gameserver.handler.item.ItemCompShotPacks;
import l2j.gameserver.handler.item.ItemCrystalCarol;
import l2j.gameserver.handler.item.ItemEnchantScrolls;
import l2j.gameserver.handler.item.ItemEnergyStone;
import l2j.gameserver.handler.item.ItemExtractableItems;
import l2j.gameserver.handler.item.ItemFirework;
import l2j.gameserver.handler.item.ItemFishShots;
import l2j.gameserver.handler.item.ItemHarvester;
import l2j.gameserver.handler.item.ItemMercTicket;
import l2j.gameserver.handler.item.ItemPetFood;
import l2j.gameserver.handler.item.ItemPotions;
import l2j.gameserver.handler.item.ItemRecipes;
import l2j.gameserver.handler.item.ItemRemedy;
import l2j.gameserver.handler.item.ItemRollingDice;
import l2j.gameserver.handler.item.ItemScrollOfEscape;
import l2j.gameserver.handler.item.ItemScrollOfResurrection;
import l2j.gameserver.handler.item.ItemScrolls;
import l2j.gameserver.handler.item.ItemSeed;
import l2j.gameserver.handler.item.ItemSevenSignsRecord;
import l2j.gameserver.handler.item.ItemSoulCrystals;
import l2j.gameserver.handler.item.ItemSoulShots;
import l2j.gameserver.handler.item.ItemSpecialXMas;
import l2j.gameserver.handler.item.ItemSpiritShot;
import l2j.gameserver.handler.item.ItemSummonItems;
import l2j.gameserver.handler.item.ItemWorldMap;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.items.instance.ItemInstance;

public class ItemHandler
{
	// Interface
	public interface IItemHandler
	{
		public void useItem(L2Playable playable, ItemInstance item);
		
		public int[] getItemIds();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(ItemHandler.class.getName());
	// Instance
	private static final Map<Integer, IItemHandler> items = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new ItemAdvQuestItems());
		registerHandler(new ItemBeastSoulShot());
		registerHandler(new ItemBeastSpice());
		registerHandler(new ItemBeastSpiritShot());
		registerHandler(new ItemBlessedSpiritShot());
		registerHandler(new ItemBook());
		registerHandler(new ItemCalculator());
		registerHandler(new ItemCharChangePotions());
		registerHandler(new ItemChestKey());
		registerHandler(new ItemChristmasTree());
		registerHandler(new ItemCompBlessedSpiritShotPacks());
		registerHandler(new ItemCompShotPacks());
		registerHandler(new ItemCrystalCarol());
		registerHandler(new ItemEnchantScrolls());
		registerHandler(new ItemEnergyStone());
		registerHandler(new ItemExtractableItems());
		registerHandler(new ItemFirework());
		registerHandler(new ItemFishShots());
		registerHandler(new ItemHarvester());
		registerHandler(new ItemMercTicket());
		registerHandler(new ItemPetFood());
		registerHandler(new ItemPotions());
		registerHandler(new ItemRecipes());
		registerHandler(new ItemRemedy());
		registerHandler(new ItemRollingDice());
		registerHandler(new ItemScrollOfEscape());
		registerHandler(new ItemScrollOfResurrection());
		registerHandler(new ItemScrolls());
		registerHandler(new ItemSeed());
		registerHandler(new ItemSevenSignsRecord());
		registerHandler(new ItemSoulCrystals());
		registerHandler(new ItemSoulShots());
		registerHandler(new ItemSpecialXMas());
		registerHandler(new ItemSpiritShot());
		registerHandler(new ItemSummonItems());
		registerHandler(new ItemWorldMap());
		
		LOG.info("ItemHandler: load " + size() + " handlers");
	}
	
	public static void registerHandler(IItemHandler handler)
	{
		for (int id : handler.getItemIds())
		{
			items.put(id, handler);
		}
	}
	
	public static IItemHandler getHandler(Integer itemId)
	{
		return items.get(itemId);
	}
	
	public static int size()
	{
		return items.size();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}
