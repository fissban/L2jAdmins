package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.Config;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.EnchantResult;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;
import main.EngineModsManager;

public class RequestEnchantItem extends AClientPacket
{
	private int objectId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (objectId == 0))
		{
			return;
		}
		
		if (!activeChar.isOnline())
		{
			activeChar.setActiveEnchantItem(null);
			return;
		}
		
		if (activeChar.isRequestActive() || activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_ENCHANT_WHILE_STORE);
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
		ItemInstance scroll = activeChar.getActiveEnchantItem();
		
		if ((item == null) || (scroll == null))
		{
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		// FIXME HARDCODE
		// replazarlo por el nuevo sistema q se implementara para los items.
		
		// can't enchant rods and hero weapons
		if ((item.getItem().getType() == WeaponType.ROD) || ((item.getId() >= 6611) && (item.getId() <= 6621)) || ((item.getId() >= 7816) && (item.getId() <= 7831)))
		{
			activeChar.sendPacket(SystemMessage.INAPPROPRIATE_ENCHANT_CONDITION);
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		if (item.isWear())
		{
			IllegalAction.report(activeChar, "Player " + activeChar.getName() + " tried to enchant a weared Item", IllegalActionType.PUNISH_KICK);
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		switch (item.getLocation())
		{
			case INVENTORY:
			case PAPERDOLL:
			{
				if (item.getOwnerId() != activeChar.getObjectId())
				{
					activeChar.setActiveEnchantItem(null);
					activeChar.sendPacket(new EnchantResult(2));
					return;
				}
				break;
			}
			default:
			{
				IllegalAction.report(activeChar, "Player " + activeChar.getName() + " tried to use enchant Exploit!", IllegalActionType.PUNISH_KICKBAN);
				activeChar.setActiveEnchantItem(null);
				activeChar.sendPacket(new EnchantResult(2));
				return;
			}
		}
		
		if (activeChar.getActiveWarehouse() != null)
		{
			activeChar.setActiveWarehouse(null);
		}
		
		ItemType2 itemType2 = item.getItem().getType2();
		
		boolean blessedScroll = false;
		boolean enchantItem = false;
		int crystalId = item.getItem().getCrystalType().getCrystalId();
		
		/** pretty code ;D */
		switch (item.getItem().getCrystalType())
		{
			case CRYSTAL_S:
				switch (scroll.getId())
				{
					case 959:
					case 961:
					case 6577:
						if (itemType2 == ItemType2.WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 960:
					case 962:
					case 6578:
						if ((itemType2 == ItemType2.SHIELD_ARMOR) || (itemType2 == ItemType2.ACCESSORY))
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case CRYSTAL_A:
				switch (scroll.getId())
				{
					case 729:
					case 731:
					case 6569:
						if (itemType2 == ItemType2.WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 730:
					case 732:
					case 6570:
						if ((itemType2 == ItemType2.SHIELD_ARMOR) || (itemType2 == ItemType2.ACCESSORY))
						{
							enchantItem = true;
						}
						
						break;
				}
				break;
			case CRYSTAL_B:
				switch (scroll.getId())
				{
					case 947:
					case 949:
					case 6571:
						if (itemType2 == ItemType2.WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 948:
					case 950:
					case 6572:
						if ((itemType2 == ItemType2.SHIELD_ARMOR) || (itemType2 == ItemType2.ACCESSORY))
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case CRYSTAL_C:
				switch (scroll.getId())
				{
					case 951:
					case 953:
					case 6573:
						if (itemType2 == ItemType2.WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 952:
					case 954:
					case 6574:
						if ((itemType2 == ItemType2.SHIELD_ARMOR) || (itemType2 == ItemType2.ACCESSORY))
						{
							enchantItem = true;
						}
						break;
				}
				break;
			case CRYSTAL_D:
				switch (scroll.getId())
				{
					case 955:
					case 957:
					case 6575:
						if (itemType2 == ItemType2.WEAPON)
						{
							enchantItem = true;
						}
						break;
					case 956:
					case 958:
					case 6576:
						if ((itemType2 == ItemType2.SHIELD_ARMOR) || (itemType2 == ItemType2.ACCESSORY))
						{
							enchantItem = true;
						}
						break;
				}
				break;
		}
		
		if (!enchantItem)
		{
			activeChar.sendPacket(SystemMessage.INAPPROPRIATE_ENCHANT_CONDITION);
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		// Get the scroll type - Yesod
		if ((scroll.getId() >= 6569) && (scroll.getId() <= 6578))
		{
			blessedScroll = true;
		}
		else
		{
			for (int crystalscroll : Item.CRYSTAL_SCROLLS)
			{
				if (scroll.getId() == crystalscroll)
				{
					blessedScroll = true;
				}
				
				break;
			}
		}
		
		int chance = 0;
		int maxEnchantLevel = 0;
		
		if (item.getItem().getType2() == ItemType2.WEAPON)
		{
			if (blessedScroll)
			{
				chance = Config.BLESSED_ENCHANT_CHANCE_WEAPON;
			}
			else
			{
				chance = Config.ENCHANT_CHANCE_WEAPON;
			}
			maxEnchantLevel = Config.ENCHANT_MAX_WEAPON;
		}
		else if (item.getItem().getType2() == ItemType2.SHIELD_ARMOR)
		{
			if (blessedScroll)
			{
				chance = Config.BLESSED_ENCHANT_CHANCE_ARMOR;
			}
			else
			{
				chance = Config.ENCHANT_CHANCE_ARMOR;
			}
			maxEnchantLevel = Config.ENCHANT_MAX_ARMOR;
		}
		else if (item.getItem().getType2() == ItemType2.ACCESSORY)
		{
			if (blessedScroll)
			{
				chance = Config.BLESSED_ENCHANT_CHANCE_JEWELRY;
			}
			else
			{
				chance = Config.ENCHANT_CHANCE_JEWELRY;
			}
			maxEnchantLevel = Config.ENCHANT_MAX_JEWELRY;
		}
		
		if ((item.getEnchantLevel() >= maxEnchantLevel) && (maxEnchantLevel != 0))
		{
			activeChar.sendPacket(SystemMessage.INAPPROPRIATE_ENCHANT_CONDITION);
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		scroll = activeChar.getInventory().destroyItem("Enchant", scroll, activeChar, item);
		if (scroll == null)
		{
			IllegalAction.report(activeChar, "Player " + activeChar.getName() + " tried to enchant with a scroll he doesnt have");
			
			activeChar.sendPacket(SystemMessage.NOT_ENOUGH_ITEMS);
			activeChar.setActiveEnchantItem(null);
			activeChar.sendPacket(new EnchantResult(2));
			return;
		}
		
		if ((item.getEnchantLevel() < Config.ENCHANT_SAFE_MAX) || ((item.getItem().getBodyPart() == SlotType.FULL_ARMOR) && (item.getEnchantLevel() < Config.ENCHANT_SAFE_MAX_FULL)))
		{
			chance = 100;
		}
		
		boolean failed = false;
		SystemMessage sm;
		
		if (Rnd.get(100) < chance)
		{
			synchronized (item)
			{
				if (item.getOwnerId() != activeChar.getObjectId())
				{
					activeChar.sendPacket(SystemMessage.INAPPROPRIATE_ENCHANT_CONDITION);
					activeChar.setActiveEnchantItem(null);
					activeChar.sendPacket(new EnchantResult(2));
					return;
				}
				
				if ((item.getLocation() != ItemLocationType.INVENTORY) && (item.getLocation() != ItemLocationType.PAPERDOLL))
				{
					activeChar.sendPacket(SystemMessage.INAPPROPRIATE_ENCHANT_CONDITION);
					activeChar.setActiveEnchantItem(null);
					activeChar.sendPacket(new EnchantResult(2));
					return;
				}
				
				if (item.getEnchantLevel() == 0)
				{
					sm = new SystemMessage(SystemMessage.S1_SUCCESSFULLY_ENCHANTED);
					sm.addItemName(item.getId());
					activeChar.sendPacket(sm);
				}
				else
				{
					sm = new SystemMessage(SystemMessage.S1_S2_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getId());
					activeChar.sendPacket(sm);
				}
				
				activeChar.playSound(PlaySoundType.SYS_ENCHANT_SUCCES);
				item.setEnchantLevel(item.getEnchantLevel() + 1);
				item.updateDatabase();
			}
		}
		else
		{
			failed = true;
			
			if (!blessedScroll)
			{
				if (item.getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessage.ENCHANTMENT_FAILED_S1_S2_EVAPORATED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getId());
					activeChar.sendPacket(sm);
				}
				else
				{
					sm = new SystemMessage(SystemMessage.ENCHANTMENT_FAILED_S1_EVAPORATED);
					sm.addItemName(item.getId());
					activeChar.sendPacket(sm);
				}
				
				if (item.isEquipped())
				{
					if (item.getEnchantLevel() > 0)
					{
						sm = new SystemMessage(SystemMessage.EQUIPMENT_S1_S2_REMOVED);
						sm.addNumber(item.getEnchantLevel());
						sm.addItemName(item.getId());
						activeChar.sendPacket(sm);
					}
					else
					{
						sm = new SystemMessage(SystemMessage.S1_DISARMED);
						sm.addItemName(item.getId());
						activeChar.sendPacket(sm);
					}
					
					List<ItemInstance> unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(item.getEquipSlot());
					
					InventoryUpdate iu = new InventoryUpdate();
					for (ItemInstance element : unequiped)
					{
						iu.addModifiedItem(element);
					}
					
					activeChar.sendPacket(iu);
					activeChar.broadcastUserInfo();
				}
				
				int count = item.getCrystalCount() - ((item.getItem().getCrystalCount() + 1) / 2);
				if (count < 1)
				{
					count = 1;
				}
				
				ItemInstance destroyItem = activeChar.getInventory().destroyItem("Enchant", item, activeChar, null);
				if (destroyItem == null)
				{
					IllegalAction.report(activeChar, "Unable to delete item on enchant failure from player " + activeChar.getName() + ", possible cheater !", IllegalActionType.PUNISH_KICK);
					activeChar.setActiveEnchantItem(null);
					activeChar.sendPacket(new EnchantResult(2));
					return;
				}
				
				ItemInstance crystals = activeChar.getInventory().addItem("Enchant", crystalId, count, activeChar, destroyItem);
				
				sm = new SystemMessage(SystemMessage.EARNED_S2_S1_S);
				sm.addItemName(crystals.getId());
				sm.addNumber(count);
				activeChar.sendPacket(sm);
				
				InventoryUpdate iu = new InventoryUpdate();
				if (destroyItem.getCount() == 0)
				{
					iu.addRemovedItem(destroyItem);
				}
				else
				{
					iu.addModifiedItem(destroyItem);
				}
				iu.addItem(crystals);
				
				activeChar.sendPacket(iu);
				
				activeChar.broadcastUserInfo();
				
				L2World.getInstance().removeObject(destroyItem);
				activeChar.sendPacket(new EnchantResult(1));
			}
			else
			{
				activeChar.sendPacket(SystemMessage.BLESSED_ENCHANT_FAILED);
				
				item.setEnchantLevel(0);
				item.updateDatabase();
				activeChar.sendPacket(new EnchantResult(2));
			}
			
			activeChar.playSound(PlaySoundType.SYS_ENCHANT_FAILED);
		}
		sm = null;
		
		// Update current load as well
		activeChar.updateCurLoad();
		
		if (!failed)
		{
			activeChar.sendPacket(new EnchantResult(0));
		}
		
		EngineModsManager.onEnchant(activeChar);
		
		activeChar.sendPacket(new ItemList(activeChar, false)); // TODO update only the enchanted item
		activeChar.broadcastUserInfo();
		activeChar.setActiveEnchantItem(null);
	}
}
