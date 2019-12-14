package l2j.gameserver.model.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.RecipeData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.ManufactureItemHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.RecipeBookItemList;
import l2j.gameserver.network.external.server.RecipeItemMakeInfo;
import l2j.gameserver.network.external.server.RecipeShopItemInfo;
import l2j.gameserver.network.external.server.SetupGauge;
import l2j.gameserver.network.external.server.SetupGauge.SetupGaugeType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class RecipeController
{
	protected static final Logger LOG = Logger.getLogger(RecipeController.class.getName());
	
	protected static final Map<L2PcInstance, RecipeItemMaker> activeMakers = new ConcurrentHashMap<>();
	
	protected RecipeController()
	{
		// activeMakers.shared();
	}
	
	public void requestBookOpen(L2PcInstance player, boolean isDwarvenCraft)
	{
		RecipeItemMaker maker = null;
		if (Config.ALT_GAME_CREATION)
		{
			maker = activeMakers.get(player);
		}
		
		if (maker == null)
		{
			RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getStat().getMaxMp());
			response.addRecipes(isDwarvenCraft ? player.getDwarvenRecipeBookList() : player.getCommonRecipeBookList());
			player.sendPacket(response);
			return;
		}
		
		player.sendPacket(SystemMessage.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
		return;
	}
	
	public void requestMakeItemAbort(L2PcInstance player)
	{
		if (activeMakers.containsKey(player))
		{
			activeMakers.remove(player); // TODO: anything else here?
		}
	}
	
	public void requestManufactureItem(L2PcInstance manufacturer, int recipeListId, L2PcInstance player)
	{
		RecipeList recipeList = RecipeData.getValidRecipeList(player, recipeListId);
		
		if (recipeList == null)
		{
			return;
		}
		
		Collection<RecipeList> dwarfRecipes = manufacturer.getDwarvenRecipeBookList();
		Collection<RecipeList> commonRecipes = manufacturer.getCommonRecipeBookList();
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.");
			return;
		}
		
		RecipeItemMaker maker;
		
		if (Config.ALT_GAME_CREATION && ((maker = activeMakers.get(manufacturer)) != null)) // check if busy
		{
			player.sendMessage("Manufacturer is busy, please try later.");
			return;
		}
		
		maker = new RecipeItemMaker(manufacturer, recipeList, player);
		if (maker.isValid())
		{
			if (Config.ALT_GAME_CREATION)
			{
				activeMakers.put(manufacturer, maker);
				ThreadPoolManager.schedule(maker, 100);
			}
			else
			{
				maker.run();
			}
		}
	}
	
	public void requestMakeItem(L2PcInstance player, int recipeListId)
	{
		RecipeList recipeList = RecipeData.getValidRecipeList(player, recipeListId);
		
		if (recipeList == null)
		{
			return;
		}
		
		Collection<RecipeList> dwarfRecipes = player.getDwarvenRecipeBookList();
		Collection<RecipeList> commonRecipes = player.getCommonRecipeBookList();
		
		if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList))
		{
			IllegalAction.report(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.");
			return;
		}
		
		RecipeItemMaker maker;
		
		// check if already busy (possible in alt mode only)
		if (Config.ALT_GAME_CREATION && ((maker = activeMakers.get(player)) != null))
		{
			player.sendMessage("You are busy creating " + recipeList.getItemId());
			return;
		}
		
		maker = new RecipeItemMaker(player, recipeList, player);
		if (maker.isValid())
		{
			if (Config.ALT_GAME_CREATION)
			{
				activeMakers.put(player, maker);
				ThreadPoolManager.schedule(maker, 100);
			}
			else
			{
				maker.run();
			}
		}
	}
	
	public class RecipeItemMaker implements Runnable
	{
		private boolean isValid;
		private List<ItemHolder> items = null;
		private final RecipeList recipeList;
		private final L2PcInstance player; // "crafter"
		private final L2PcInstance target; // "customer"
		private final Skill skill;
		private final int skillId;
		private final int skillLevel;
		private double creationPasses;
		private double manaRequired;
		private int price;
		private int totalItems;
		// private int materialsRefPrice;
		private int delay;
		
		public RecipeItemMaker(L2PcInstance pPlayer, RecipeList pRecipeList, L2PcInstance pTarget)
		{
			player = pPlayer;
			target = pTarget;
			recipeList = pRecipeList;
			
			isValid = false;
			skillId = recipeList.isDwarvenRecipe() ? Skill.SKILL_CREATE_DWARVEN : Skill.SKILL_CREATE_COMMON;
			skillLevel = player.getSkillLevel(skillId);
			skill = player.getSkill(skillId);
			
			player.getPrivateStore().isInCraftMode(true);
			
			if (player.isAlikeDead())
			{
				player.sendMessage("Dead people can't craft.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (target.isAlikeDead())
			{
				target.sendMessage("Dead customers can't use manufacture.");
				target.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (target.isRequestActive())
			{
				target.sendMessage("You are busy.");
				target.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			if (player.isRequestActive())
			{
				if (player != target)
				{
					target.sendMessage("Manufacturer " + player.getName() + " is busy.");
				}
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// validate recipe list
			if ((recipeList == null) || (recipeList.getRecipes().isEmpty()))
			{
				player.sendMessage("No such recipe.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			manaRequired = recipeList.getMpCost();
			
			// validate skill level
			if (recipeList.getLevel() > skillLevel)
			{
				player.sendMessage("Need skill level " + recipeList.getLevel());
				player.sendPacket(ActionFailed.STATIC_PACKET);
				abort();
				return;
			}
			
			// check that customer can afford to pay for creation services
			if (player != target)
			{
				for (ManufactureItemHolder temp : player.getPrivateStore().getCreateList().getList())
				{
					if (temp.getRecipeId() == recipeList.getId()) // find recipe for item we want manufactured
					{
						price = temp.getCost();
						if (target.getInventory().getAdena() < price) // check price
						{
							target.sendPacket(new SystemMessage(SystemMessage.YOU_NOT_ENOUGH_ADENA));
							abort();
							return;
						}
						break;
					}
				}
			}
			
			// make temporary items
			if ((items = listItems(false)) == null)
			{
				abort();
				return;
			}
			
			// calculate reference price
			for (ItemHolder i : items)
			{
				// materialsRefPrice += i.getReferencePrice() * i.getQuantity();
				totalItems += i.getCount();
			}
			// initial mana check requires MP as written on recipe
			if (player.getCurrentMp() < manaRequired)
			{
				target.sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_MP));
				abort();
				return;
			}
			
			// determine number of creation passes needed
			// can "equip" skillLevel items each pass
			creationPasses = (totalItems / skillLevel) + ((totalItems % skillLevel) != 0 ? 1 : 0);
			
			if (Config.ALT_GAME_CREATION && (creationPasses != 0))
			{
				manaRequired /= creationPasses; // checks to validateMp() will only need portion of mp for one pass
			}
			
			updateMakeInfo(true);
			// Update current mp status on player
			player.updateCurMp();
			
			player.updateCurLoad();
			
			player.getPrivateStore().isInCraftMode(false);
			isValid = true;
		}
		
		@Override
		public void run()
		{
			if (!Config.IS_CRAFTING_ENABLED)
			{
				target.sendMessage("Item creation is currently disabled.");
				abort();
				return;
			}
			
			if ((player == null) || (target == null))
			{
				LOG.warning("player or target == null (disconnected?), aborting" + target + player);
				abort();
				return;
			}
			
			if ((!player.isOnline()) || (!target.isOnline()))
			{
				LOG.warning("player or target is not online, aborting " + target + player);
				abort();
				return;
			}
			
			if (Config.ALT_GAME_CREATION && (activeMakers.get(player) == null))
			{
				if (target != player)
				{
					target.sendMessage("Manufacture aborted");
					player.sendMessage("Manufacture aborted");
				}
				else
				{
					player.sendMessage("Item creation aborted");
				}
				
				abort();
				return;
			}
			
			if (Config.ALT_GAME_CREATION && !items.isEmpty())
			{
				if (!validateMp())
				{
					return; // check mana
				}
				player.reduceCurrentMp(manaRequired); // use some mp
				player.updateCurMp(); // update craft window mp bar
				
				grabSomeItems(); // grab (equip) some more items with a nice msg to player
				
				// if still not empty, schedule another pass
				if (!items.isEmpty())
				{
					// divided by RATE_CONSUMABLES_COST to remove craft time increase on higher consumables rates
					delay = (int) ((Config.ALT_GAME_CREATION_SPEED * player.getStat().getMReuseRate(skill) * 10) / Config.RATE_CONSUMABLE_COST) * 1000;
					
					player.broadcastPacket(new MagicSkillUse(player, skillId, skillLevel, delay, 0));
					
					player.sendPacket(new SetupGauge(SetupGaugeType.BLUE, delay));
					ThreadPoolManager.schedule(this, 100 + delay);
				}
				else
				{
					// for alt mode, sleep delay msec before finishing
					player.sendPacket(new SetupGauge(SetupGaugeType.BLUE, delay));
					
					try
					{
						Thread.sleep(delay);
					}
					catch (InterruptedException e)
					{
					}
					finally
					{
						finishCrafting();
					}
				}
			} // for old craft mode just finish
			else
			{
				finishCrafting();
			}
		}
		
		private void finishCrafting()
		{
			if (!Config.ALT_GAME_CREATION)
			{
				player.reduceCurrentMp(manaRequired);
			}
			
			// first take adena for manufacture
			if ((target != player) && (price > 0)) // customer must pay for services
			{
				// attempt to pay for item
				ItemInstance adenatransfer = target.getInventory().transferItem("PayManufacture", target.getInventory().getAdenaInstance().getObjectId(), price, player.getInventory(), player);
				
				if (adenatransfer == null)
				{
					target.sendPacket(SystemMessage.YOU_NOT_ENOUGH_ADENA);
					abort();
					return;
				}
			}
			
			if ((items = listItems(true)) == null) // this line actually takes materials from inventory
			{ // handle possible cheaters here
				// (they click craft then try to get rid of items in order to get free craft)
			}
			else if (Rnd.get(100) < recipeList.getSuccessRate())
			{
				RewardPlayer(); // and immediately puts created item in its place
				updateMakeInfo(true);
			}
			else
			{
				player.sendMessage("Item(s) failed to create.");
				if (target != player)
				{
					target.sendMessage("Item(s) failed to create.");
				}
				
				updateMakeInfo(false);
			}
			
			player.updateCurMp();
			player.updateCurLoad();
			
			activeMakers.remove(player);
			player.getPrivateStore().isInCraftMode(false);
			target.sendPacket(new ItemList(target, false));
		}
		
		private void updateMakeInfo(boolean success)
		{
			if (target == player)
			{
				target.sendPacket(new RecipeItemMakeInfo(recipeList.getId(), target, success));
			}
			else
			{
				target.sendPacket(new RecipeShopItemInfo(player.getObjectId(), recipeList.getId()));
			}
		}
		
		private void grabSomeItems()
		{
			int numItems = skillLevel;
			
			while ((numItems > 0) && !items.isEmpty())
			{
				ItemHolder item = items.get(0);
				
				int count = item.getCount();
				if (count >= numItems)
				{
					count = numItems;
				}
				
				item.setCount(item.getCount() - count);
				if (item.getCount() <= 0)
				{
					items.remove(0);
				}
				else
				{
					items.set(0, item);
				}
				
				numItems -= count;
				
				if (target == player)
				{
					SystemMessage sm = new SystemMessage(SystemMessage.S1_S2_EQUIPPED); // you equipped ...
					sm.addNumber(count);
					sm.addItemName(item.getId());
					player.sendPacket(sm);
				}
				else
				{
					target.sendMessage("Manufacturer " + player.getName() + " used " + count + " " + item.getItemName());
				}
			}
		}
		
		private boolean validateMp()
		{
			if (player.getCurrentMp() < manaRequired)
			{
				if (Config.ALT_GAME_CREATION)
				{
					// rest (wait for MP)
					player.sendPacket(new SetupGauge(SetupGaugeType.BLUE, delay));
					ThreadPoolManager.schedule(this, 100 + delay);
				}
				else
				{
					// no rest - report no mana
					target.sendPacket(SystemMessage.NOT_ENOUGH_MP);
					abort();
				}
				return false;
			}
			return true;
		}
		
		private List<ItemHolder> listItems(boolean remove)
		{
			Inventory inv = target.getInventory();
			List<ItemHolder> materials = new ArrayList<>();
			
			for (RecipeInstance recipe : recipeList.getRecipes())
			{
				int quantity = recipeList.isConsumable() ? (int) (recipe.getQuantity() * Config.RATE_CONSUMABLE_COST) : recipe.getQuantity();
				
				if (quantity > 0)
				{
					ItemInstance item = inv.getItemById(recipe.getItemId());
					
					// check materials
					if ((item == null) || (item.getCount() < quantity))
					{
						target.sendMessage("You dont have the right elements for making this item" + ((recipeList.isConsumable() && (Config.RATE_CONSUMABLE_COST != 1)) ? ".\nDue to server rates you need " + Config.RATE_CONSUMABLE_COST + "x more material than listed in recipe" : ""));
						abort();
						return null;
					}
					
					// make new temporary object, just for counting puroses
					materials.add(new ItemHolder(item.getId(), quantity));
				}
			}
			
			if (remove)
			{
				for (ItemHolder item : materials)
				{
					inv.destroyItemByItemId("Manufacture", item.getId(), item.getCount(), target, player);
				}
			}
			return materials;
		}
		
		private void abort()
		{
			updateMakeInfo(false);
			player.getPrivateStore().isInCraftMode(false);
			activeMakers.remove(player);
		}
		
		private void RewardPlayer()
		{
			int itemId = recipeList.getItemId();
			int itemCount = recipeList.getCount();
			
			ItemInstance createdItem = target.getInventory().addItem("Manufacture", itemId, itemCount, target, player);
			
			// inform customer of earned item
			if (itemCount > 1)
			{
				target.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(itemId).addNumber(itemCount));
			}
			else
			{
				target.sendPacket(new SystemMessage(SystemMessage.EARNED_ITEM_S1).addItemName(itemId));
			}
			
			if (target != player)
			{
				// inform manufacturer of earned profit
				player.sendPacket(new SystemMessage(SystemMessage.EARNED_S1_ADENA).addNumber(price));
			}
			
			if (Config.ALT_GAME_CREATION)
			{
				int recipeLevel = recipeList.getLevel();
				long exp = createdItem.getReferencePrice() * itemCount;
				// one variation
				
				// exp -= materialsRefPrice; // mat. ref. price is not accurate so other method is better
				
				if (exp < 0)
				{
					exp = 0;
				}
				
				// another variation
				exp /= recipeLevel;
				for (int i = skillLevel; i > recipeLevel; i--)
				{
					exp /= 4;
				}
				
				long sp = exp / 10;
				
				// Added multiplication of Creation speed with XP/SP gain
				// slower crafting -> more XP, faster crafting -> less XP
				// you can use ALT_GAME_CREATION_XP_RATE/SP to
				// modify XP/SP gained (default = 1)
				
				player.addExpAndSp((long) player.calcStat(StatsType.EXPSP_RATE, exp * Config.ALT_GAME_CREATION_XP_RATE * Config.ALT_GAME_CREATION_SPEED, null, null), (int) player.calcStat(StatsType.EXPSP_RATE, sp * Config.ALT_GAME_CREATION_SP_RATE * Config.ALT_GAME_CREATION_SPEED, null, null));
			}
			updateMakeInfo(true); // success
		}
		
		public boolean isValid()
		{
			return isValid;
		}
	}
	
	public static RecipeController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RecipeController INSTANCE = new RecipeController();
	}
}
