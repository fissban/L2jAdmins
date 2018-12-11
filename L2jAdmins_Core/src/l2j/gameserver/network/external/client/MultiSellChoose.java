package l2j.gameserver.network.external.client;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.MultisellData;
import l2j.gameserver.floodprotector.FloodProtector;
import l2j.gameserver.floodprotector.enums.FloodProtectorType;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.multisell.MultisellContainer;
import l2j.gameserver.model.multisell.MultisellEntry;
import l2j.gameserver.model.multisell.MultisellIngredient;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;

public class MultiSellChoose extends AClientPacket
{
	private int listId;
	private int entryId;
	private long amount;
	private int enchantment;
	private int transactionTax; // local handling of taxation
	
	@Override
	protected void readImpl()
	{
		listId = readD();
		entryId = readD();
		amount = readD();
		// enchantment = readH(); // <---commented this line because it did NOT work!
		enchantment = entryId % 100000;
		entryId = entryId / 100000;
		transactionTax = 0; // initialize tax amount to 0...
	}
	
	@Override
	public void runImpl()
	{
		if ((amount < 1) || (amount > 5000))
		{
			return;
		}
		
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			return;
		}
		
		MultisellContainer list = MultisellData.getInstance().getList(listId);
		if (list == null)
		{
			return;
		}
		
		L2Object target = player.getTarget();
		if (!player.isGM() && ((target == null) || !(target instanceof L2Npc) || !list.checkNpcId(((L2Npc) target).getId()) || !((L2Npc) target).canInteract(player)))
		{
			return;
		}
		
		if (!FloodProtector.getInstance().tryPerformAction(player, FloodProtectorType.MULTISELL))
		{
			return;
		}
		
		for (MultisellEntry entry : list.getEntries())
		{
			if (entry.getEntryId() == entryId)
			{
				doExchange(player, entry, list.getApplyTaxes(), list.getMaintainEnchantment(), enchantment);
				return;
			}
		}
	}
	
	private void doExchange(L2PcInstance player, MultisellEntry templateEntry, boolean applyTaxes, boolean maintainEnchantment, int enchantment)
	{
		PcInventory inv = player.getInventory();
		
		// given the template entry and information about maintaining enchantment and applying taxes
		// re-create the instance of the entry that will be used for this exchange
		// i.e. change the enchantment level of select ingredient/products and adena amount appropriately.
		L2Npc merchant = (player.getTarget() instanceof L2Npc) ? (L2Npc) player.getTarget() : null;
		if (merchant == null)
		{
			return;
		}
		
		MultisellEntry entry = prepareEntry(merchant, templateEntry, applyTaxes, maintainEnchantment, enchantment);
		
		int slots = 0;
		int weight = 0;
		for (MultisellIngredient e : entry.getProducts())
		{
			if (e.getItemId() < 0)
			{
				continue;
			}
			Item template = ItemData.getInstance().getTemplate(e.getItemId());
			if (template == null)
			{
				continue;
			}
			if (!template.isStackable())
			{
				slots += e.getItemCount() * amount;
			}
			else if (player.getInventory().getItemById(e.getItemId()) == null)
			{
				slots++;
			}
			weight += e.getItemCount() * amount * template.getWeight();
		}
		
		if (!inv.validateWeight(weight))
		{
			player.sendPacket(new SystemMessage(SystemMessage.WEIGHT_LIMIT_EXCEEDED));
			return;
		}
		
		if (!inv.validateCapacity(slots))
		{
			player.sendPacket(new SystemMessage(SystemMessage.SLOTS_FULL));
			return;
		}
		
		// Generate a list of distinct ingredients and counts in order to check if the correct item-counts
		// are possessed by the player
		List<MultisellIngredient> ingredientsList = new ArrayList<>();
		boolean newIng = true;
		for (MultisellIngredient e : entry.getIngredients())
		{
			newIng = true;
			
			// at this point, the template has already been modified so that enchantments are properly included
			// whenever they need to be applied. Uniqueness of items is thus judged by item id AND enchantment level
			for (MultisellIngredient ex : ingredientsList)
			{
				// if the item was already added in the list, merely increment the count
				// this happens if 1 list entry has the same ingredient twice (example 2 swords = 1 dual)
				if ((ex.getItemId() == e.getItemId()) && (ex.getEnchantmentLevel() == e.getEnchantmentLevel()))
				{
					if ((ex.getItemCount() + e.getItemCount()) > Integer.MAX_VALUE)
					{
						player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
						ingredientsList.clear();
						ingredientsList = null;
						return;
					}
					ex.setItemCount(ex.getItemCount() + e.getItemCount());
					newIng = false;
				}
			}
			if (newIng)
			{
				// if it's a new ingredient, just store its info directly (item id, count, enchantment)
				ingredientsList.add(new MultisellIngredient(e));
			}
		}
		// now check if the player has sufficient items in the inventory to cover the ingredients' expences
		for (MultisellIngredient e : ingredientsList)
		{
			if ((e.getItemCount() * amount) > Integer.MAX_VALUE)
			{
				player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED));
				ingredientsList.clear();
				ingredientsList = null;
				return;
			}
			
			// if this is not a list that maintains enchantment, check the count of all items that have the given id.
			// otherwise, check only the count of items with exactly the needed enchantment level
			if (inv.getItemCount(e.getItemId(), maintainEnchantment ? e.getEnchantmentLevel() : -1) < (e.getItemCount() * amount))
			{
				player.sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_ITEMS));
				ingredientsList.clear();
				ingredientsList = null;
				return;
			}
		}
		
		ingredientsList.clear();
		ingredientsList = null;
		
		/** All ok, remove items and add final product */
		for (MultisellIngredient e : entry.getIngredients())
		{
			ItemInstance itemToTake = inv.getItemById(e.getItemId()); // initialize and initial guess for the item to take.
			
			// this is a cheat, transaction will be aborted and if any items already tanken will not be returned back to inventory!
			if (itemToTake == null)
			{
				LOG.severe("Character: " + player.getName() + " is trying to cheat in multisell, merchant id:" + merchant.getId());
				return;
			}
			
			if (itemToTake.isWear())
			{
				LOG.severe("Character: " + player.getName() + " is trying to cheat in multisell, merchant id:" + merchant.getId());
				return;
			}
			
			// if it's a stackable item, just reduce the amount from the first (only) instance that is found in the inventory
			if (itemToTake.isStackable())
			{
				if (!player.getInventory().destroyItem("Multisell", itemToTake.getObjectId(), (int) (e.getItemCount() * amount), player.getTarget(), true))
				{
					return;
				}
			}
			else
			{
				// for non-stackable items, one of two scenario are possible:
				// a) list maintains enchantment: get the instances that exactly match the requested enchantment level
				// b) list does not maintain enchantment: get the instances with the LOWEST enchantment level
				
				// a) if enchantment is maintained, then get a list of items that exactly match this enchantment
				if (maintainEnchantment)
				{
					List<ItemInstance> inventoryContents = inv.getAllItemsByItemId(e.getItemId(), e.getEnchantmentLevel());
					if ((inventoryContents != null) && (inventoryContents.size() > 0))
					{
						for (int i = 0; i < (e.getItemCount() * amount); i++)
						{
							if (!player.getInventory().destroyItem("Multisell", inventoryContents.get(i).getObjectId(), 1, player.getTarget(), true))
							{
								return;
							}
						}
					}
				}
				else
				// b) enchantment is not maintained. Get the instances with the LOWEST enchantment level
				{
					/*
					 * NOTE: There are 2 ways to achieve the above goal. 1) Get all items that have the correct itemId, loop through them until the lowest enchantment level is found. Repeat all this for the next item until proper count of items is reached. 2) Get all items that have the correct
					 * itemId, sort them once based on enchantment level, and get the range of items that is necessary. Method 1 is faster for a small number of items to be exchanged. Method 2 is faster for large amounts. EXPLANATION: Worst case scenario for algorithm 1 will make it run in a number
					 * of cycles given by: m*(2n-m+1)/2 where m is the number of items to be exchanged and n is the total number of inventory items that have a matching id. With algorithm 2 (sort), sorting takes n*log(n) time and the choice is done in a single cycle for case b (just grab the m first
					 * items) or in linear time for case a (find the beginning of items with correct enchantment, index x, and take all items from x to x+m). Basically, whenever m > log(n) we have: m*(2n-m+1)/2 = (2nm-m*m+m)/2 > (2nlogn-logn*logn+logn)/2 = nlog(n) - log(n*n) + log(n) = nlog(n) +
					 * log(n/n*n) = nlog(n) + log(1/n) = nlog(n) - log(n) = (n-1)log(n) So for m < log(n) then m*(2n-m+1)/2 > (n-1)log(n) and m*(2n-m+1)/2 > nlog(n) IDEALLY: In order to best optimize the performance, choose which algorithm to run, based on whether 2^m > n if ( (2<<(e.getItemCount()
					 * * amount)) < inventoryContents.length ) // do Algorithm 1, no sorting else // do Algorithm 2, sorting CURRENT IMPLEMENTATION: In general, it is going to be very rare for a person to do a massive exchange of non-stackable items For this reason, we assume that algorithm 1 will
					 * always suffice and we keep things simple. If, in the future, it becomes necessary that we optimize, the above discussion should make it clear what optimization exactly is necessary (based on the comments under "IDEALLY").
					 */
					
					// choice 1. Small number of items exchanged. No sorting.
					for (int i = 1; i <= (e.getItemCount() * amount); i++)
					{
						List<ItemInstance> inventoryContents = inv.getAllItemsByItemId(e.getItemId());
						itemToTake = inventoryContents.get(0);
						// get item with the LOWEST enchantment level from the inventory...+0 is lowest by default...
						if (itemToTake.getEnchantLevel() > 0)
						{
							for (ItemInstance inventoryContent : inventoryContents)
							{
								if (inventoryContent.getEnchantLevel() < itemToTake.getEnchantLevel())
								{
									itemToTake = inventoryContent;
									// nothing will have enchantment less than 0. If a zero-enchanted item is found, just take it
									if (itemToTake.getEnchantLevel() == 0)
									{
										break;
									}
								}
							}
						}
						
						if (!player.getInventory().destroyItem("Multisell", itemToTake.getObjectId(), 1, player.getTarget(), true))
						{
							return;
						}
					}
				}
			}
		}
		
		// Generate the appropriate items
		for (MultisellIngredient e : entry.getProducts())
		{
			ItemInstance tempItem = ItemData.getInstance().createDummyItem(e.getItemId());
			if (tempItem == null)
			{
				LOG.severe("Problem with multisell ID:" + listId + " entry ID:" + entryId + " - Product ID:" + e.getItemId() + " does not exist.");
				return;
			}
			
			if (tempItem.isStackable())
			{
				inv.addItem("Multisell", e.getItemId(), (int) (e.getItemCount() * amount), player, player.getTarget());
			}
			else
			{
				ItemInstance product = null;
				for (int i = 0; i < (e.getItemCount() * amount); i++)
				{
					product = inv.addItem("Multisell", e.getItemId(), 1, player, player.getTarget());
					if (maintainEnchantment)
					{
						product.setEnchantLevel(e.getEnchantmentLevel());
						product.updateDatabase();
					}
				}
			}
			// msg part
			SystemMessage sm;
			
			if ((e.getItemCount() * amount) > 1)
			{
				sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S);
				sm.addItemName(e.getItemId());
				sm.addNumber((int) (e.getItemCount() * amount));
				player.sendPacket(sm);
				sm = null;
			}
			else
			{
				if (maintainEnchantment && (enchantment > 0))
				{
					sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S);
					sm.addNumber(enchantment);
					sm.addItemName(e.getItemId());
				}
				else
				{
					sm = new SystemMessage(SystemMessage.EARNED_ITEM_S1);
					sm.addItemName(e.getItemId());
				}
				player.sendPacket(sm);
			}
		}
		player.sendPacket(new ItemList(player, false));
		
		// Update current load as well
		player.updateCurLoad();
		
		// finally, give the tax to the castle...
		if (merchant.getIsInCastleTown() && (merchant.getCastle().getOwnerId() > 0))
		{
			merchant.getCastle().addToTreasury(transactionTax * (int) amount);
		}
	}
	
	// Regarding taxation, the following appears to be the case:
	// a) The count of aa remains unchanged (taxes do not affect aa directly).
	// b) 5/6 of the amount of aa is taxed by the normal tax rate.
	// c) the resulting taxes are added as normal adena value.
	// d) normal adena are taxed fully.
	// e) Items other than adena and ancient adena are not taxed even when the list is taxable.
	// example: If the template has an item worth 120aa, and the tax is 10%,
	// then from 120aa, take 5/6 so that is 100aa, apply the 10% tax in adena (10a)
	// so the final price will be 120aa and 10a!
	private MultisellEntry prepareEntry(L2Npc merchant, MultisellEntry templateEntry, boolean applyTaxes, boolean maintainEnchantment, int enchantLevel)
	{
		MultisellEntry newEntry = new MultisellEntry();
		newEntry.setEntryId(templateEntry.getEntryId());
		
		for (MultisellIngredient ing : templateEntry.getIngredients())
		{
			// load the ingredient from the template
			MultisellIngredient newIngredient = new MultisellIngredient(ing);
			
			// if taxes are to be applied, modify/add the adena count based on the template adena/ancient adena count
			if (applyTaxes && ((newIngredient.getItemId() == Inventory.ADENA_ID) || (newIngredient.getItemId() == Inventory.ANCIENT_ADENA_ID)))
			{
				
				double taxRate = 0.0;
				if ((merchant != null) && merchant.getIsInCastleTown())
				{
					taxRate = merchant.getCastle().getTaxRate();
				}
				
				if (newIngredient.getItemId() == Inventory.ADENA_ID)
				{
					transactionTax = (int) Math.round(newIngredient.getItemCount() * taxRate);
					newIngredient.setItemCount(newIngredient.getItemCount() + transactionTax);
				}
				else
				// ancient adena
				{
					// add the ancient adena count normally
					newEntry.addIngredient(newIngredient);
					double taxableCount = (newIngredient.getItemCount() * 5.0) / 6;
					transactionTax = (int) Math.round(taxableCount * taxRate);
					if (transactionTax == 0)
					{
						continue;
					}
					newIngredient = new MultisellIngredient(Inventory.ADENA_ID, transactionTax);
				}
			}
			// if it is an armor/weapon, modify the enchantment level appropriately, if necessary
			else if (maintainEnchantment)
			{
				Item tempItem = ItemData.getInstance().createDummyItem(newIngredient.getItemId()).getItem();
				if ((tempItem instanceof ItemArmor) || (tempItem instanceof ItemWeapon))
				{
					newIngredient.setEnchantmentLevel(enchantLevel);
				}
			}
			
			// finally, add this ingredient to the entry
			newEntry.addIngredient(newIngredient);
		}
		// Now modify the enchantment level of products, if necessary
		for (MultisellIngredient ing : templateEntry.getProducts())
		{
			// load the ingredient from the template
			MultisellIngredient newIngredient = new MultisellIngredient(ing);
			
			if (maintainEnchantment)
			{
				// if it is an armor/weapon, modify the enchantment level appropriately
				// (note, if maintain enchantment is "false" this modification will result to a +0)
				Item tempItem = ItemData.getInstance().createDummyItem(newIngredient.getItemId()).getItem();
				if ((tempItem instanceof ItemArmor) || (tempItem instanceof ItemWeapon))
				{
					newIngredient.setEnchantmentLevel(enchantLevel);
				}
			}
			newEntry.addProduct(newIngredient);
		}
		return newEntry;
	}
}
