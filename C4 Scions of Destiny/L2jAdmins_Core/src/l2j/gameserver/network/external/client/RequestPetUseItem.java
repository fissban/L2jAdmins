package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.handler.ItemHandler;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PetItemList;
import l2j.gameserver.network.external.server.SystemMessage;

public class RequestPetUseItem extends AClientPacket
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
		
		if (activeChar == null)
		{
			return;
		}
		
		L2PetInstance pet = (L2PetInstance) activeChar.getPet();
		
		if (pet == null)
		{
			return;
		}
		
		ItemInstance item = pet.getInventory().getItemByObjectId(objectId);
		
		if (item == null)
		{
			return;
		}
		
		if (item.isWear())
		{
			return;
		}
		
		int itemId = item.getId();
		if (activeChar.isAlikeDead() || pet.isDead())
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1_CANNOT_BE_USED);
			sm.addItemName(item.getId());
			activeChar.sendPacket(sm);
			
			sm = null;
			return;
		}
		
		if (Config.DEBUG)
		{
			LOG.finest(activeChar.getObjectId() + ": pet use item " + objectId);
		}
		
		// Check if the item matches the pet
		if (item.isEquipable())
		{
			if (item.getItem().getBodyPart() == SlotType.NECK)
			{
				if (item.getItem().getType() == ArmorType.PET)
				{
					useItem(pet, item, activeChar);
					return;
				}
			}
			
			if (PetDataData.isWolf(pet.getId()) && // wolf
				
				item.getItem().isForWolf())
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isHatchling(pet.getId()) && // hatchlings
				item.getItem().isForHatchling())
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isStrider(pet.getId()) && // striders
				item.getItem().isForStrider())
			{
				useItem(pet, item, activeChar);
				return;
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.ITEM_NOT_FOR_PETS));
				return;
			}
		}
		else if (PetDataData.isPetFood(itemId))
		{
			if (PetDataData.isWolf(pet.getId()) && PetDataData.isWolfFood(itemId))
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isSinEater(pet.getId()) && PetDataData.isSinEaterFood(itemId))
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isHatchling(pet.getId()) && PetDataData.isHatchlingFood(itemId))
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isStrider(pet.getId()) && PetDataData.isStriderFood(itemId))
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isWyvern(pet.getId()) && PetDataData.isWyvernFood(itemId))
			{
				useItem(pet, item, activeChar);
				return;
			}
			else if (PetDataData.isBaby(pet.getId()) && PetDataData.isBabyFood(itemId))
			{
				useItem(pet, item, activeChar);
				return;
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.PET_CANNOT_USE_ITEM));
				return;
			}
		}
		
		IItemHandler handler = ItemHandler.getHandler(item.getId());
		
		if (handler != null)
		{
			useItem(pet, item, activeChar);
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.PET_CANNOT_USE_ITEM));
		}
		
		return;
	}
	
	private synchronized void useItem(L2PetInstance pet, ItemInstance item, L2PcInstance activeChar)
	{
		if (item.isEquipable())
		{
			if (item.isEquipped())
			{
				pet.getInventory().unEquipItemInSlot(item.getEquipSlot());
				switch (item.getItem().getBodyPart())
				{
					case R_HAND:
						pet.setWeapon(0);
						break;
					case CHEST:
						pet.setArmor(0);
						break;
					case NECK:
						pet.setJewel(0);
						break;
				}
			}
			else
			{
				pet.getInventory().equipItem(item);
				switch (item.getItem().getBodyPart())
				{
					case R_HAND:
						pet.setWeapon(item.getId());
						break;
					case CHEST:
						pet.setArmor(item.getId());
						break;
					case NECK:
						pet.setJewel(item.getId());
						break;
				}
			}
			
			activeChar.sendPacket(new PetItemList(pet));
			pet.updateAndBroadcastStatus(1);
		}
		else
		{
			IItemHandler handler = ItemHandler.getHandler(item.getId());
			
			if (handler == null)
			{
				LOG.warning("no itemhandler registered for itemId:" + item.getId());
			}
			else
			{
				handler.useItem(pet, item);
				pet.updateAndBroadcastStatus(1);
			}
		}
	}
}
