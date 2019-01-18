package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.handler.ItemHandler;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.18.2.7.2.9 $ $Date: 2005/03/27 15:29:30 $
 */
public class UseItem extends AClientPacket
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
		
		if (activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		if (activeChar.getActiveTradeList() != null)
		{
			activeChar.cancelActiveTrade();
		}
		
		ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
		
		if (item == null)
		{
			return;
		}
		
		if (item.isWear())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (item.getItem().getType2() == ItemType2.QUEST)
		{
			activeChar.sendPacket(SystemMessage.CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		int itemId = item.getId();
		
		// Char cannot use item when dead
		if (activeChar.isDead())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED).addItemName(itemId));
			return;
		}
		
		if (activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
		{
			return;
		}
		
		// Items that cannot be used
		if (itemId == Inventory.ADENA_ID)
		{
			return;
		}
		
		if (activeChar.getFishing().isFishing() && ((itemId < 6535) || (itemId > 6540)))
		{
			// You cannot do anything else while fishing
			activeChar.sendPacket(new SystemMessage(SystemMessage.CANNOT_DO_WHILE_FISHING_3));
			return;
		}
		
		if (item.isEquipable())
		{
			if (activeChar.isCastingNow())
			{
				// activeChar.breakCast();
				return;
			}
			
			if (!checkSpecialItems(item, activeChar))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED).addItemName(itemId));
				return;
			}
			
			// Char cannot use pet items
			if (((item.getItem() instanceof ItemArmor) && (item.getItem().getType() == ArmorType.PET)) || ((item.getItem() instanceof ItemWeapon) && (item.getItem().getType() == WeaponType.PET)))
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.CANNOT_EQUIP_PET_ITEM).addItemName(itemId));
				return;
			}
			
			SlotType bodyPart = item.getItem().getBodyPart();
			
			// Prevent player to remove the weapon while mounted
			if (activeChar.isMounted() && ((bodyPart == SlotType.LR_HAND) || (bodyPart == SlotType.L_HAND) || (bodyPart == SlotType.R_HAND)))
			{
				return;
			}
			
			// Don't allow weapon/shield equipment if wearing formal wear
			if (activeChar.isWearingFormalWear() && ((bodyPart == SlotType.LR_HAND) || (bodyPart == SlotType.L_HAND) || (bodyPart == SlotType.R_HAND)))
			{
				activeChar.sendPacket(SystemMessage.CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR);
				return;
			}
			
			// Don't allow hero equipment during Olympiad
			if (activeChar.isInOlympiadMode() && ((item.getId() >= 6611) && (item.getId() <= 6621)))
			{
				return;
			}
			
			// Equip or unEquip
			List<ItemInstance> items = null;
			
			if (item.isEquipped())
			{
				SlotType slot = activeChar.getInventory().getSlotFromItem(item);
				items = activeChar.getInventory().unEquipItemInBodySlotAndRecord(slot);
				
				sendMessageEquippedOrUnequippedItem(item, activeChar, false);
			}
			else
			{
				items = activeChar.getInventory().equipItemAndRecord(item);
				
				sendMessageEquippedOrUnequippedItem(item, activeChar, true);
			}
			
			activeChar.refreshExpertisePenalty();
			activeChar.broadcastUserInfo();
			
			InventoryUpdate iu = new InventoryUpdate();
			iu.addItems(items);
			activeChar.sendPacket(iu);
		}
		else
		{
			if (activeChar.isCastingNow() && !(item.isConsumable()))
			{
				return;
			}
			
			ItemWeapon weaponItem = activeChar.getActiveWeaponItem();
			
			if (((weaponItem != null) && (weaponItem.getType() == WeaponType.ROD)) && (((itemId >= 6519) && (itemId <= 6527)) || ((itemId >= 7610) && (itemId <= 7613)) || ((itemId >= 7807) && (itemId <= 7809))))
			{
				activeChar.getInventory().setPaperdollItem(ParpedollType.LHAND, item);
				activeChar.broadcastUserInfo();
				// Send a Server->Client packet ItemList to this L2PcInstance to update left hand equipment
				sendPacket(new ItemList(activeChar, false));
				return;
			}
			
			IItemHandler handler = ItemHandler.getHandler(itemId);
			
			if (handler != null)
			{
				handler.useItem(activeChar, item);
			}
		}
	}
	
	/**
	 * @param item
	 * @param activeChar
	 * @param equipped
	 */
	private void sendMessageEquippedOrUnequippedItem(ItemInstance item, L2PcInstance activeChar, boolean equipped)
	{
		SystemMessage sm = null;
		if (equipped)
		{
			if (item.getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessage.S1_S2_EQUIPPED).addNumber(item.getEnchantLevel()).addItemName(item.getId());
			}
			else
			{
				sm = new SystemMessage(SystemMessage.S1_EQUIPPED).addItemName(item.getId());
			}
		}
		else
		{
			if (item.getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessage.EQUIPMENT_S1_S2_REMOVED).addNumber(item.getEnchantLevel()).addItemName(item.getId());
			}
			else
			{
				sm = new SystemMessage(SystemMessage.S1_DISARMED).addItemName(item.getId());
			}
		}
		
		activeChar.sendPacket(sm);
	}
	
	/**
	 * <u>Hardcode</u><br>
	 * Check items that need to have a clan or clan hall
	 * @param  item
	 * @param  activeChar
	 * @return
	 */
	private boolean checkSpecialItems(ItemInstance item, L2PcInstance activeChar)
	{
		Clan cl = activeChar.getClan();
		int itemId = item.getId();
		
		switch (itemId)
		{
			// A shield that can only be used by the members of a clan that owns a clan hall.
			case 6902:
				if ((cl == null) || !cl.hasClanHall())
				{
					return false;
				}
				break;
			// A shield that can only be used by the members of a clan that owns a castle.
			case 7015:
				if ((cl == null) || !cl.hasCastle())
				{
					return false;
				}
				break;
			// The Lord's Crown used by castle lords only
			case 6841:
				if ((cl == null) || !activeChar.isClanLeader())
				{
					return false;
				}
				break;
			// Castle circles used by the members of a clan that owns a castle.
			case 6834:
			case 6835:
			case 6836:
			case 6837:
			case 6838:
			case 6839:
			case 6840:
				int circletId = CastleData.getInstance().getCircletByCastleId(cl.getCastleId());
				if (circletId != itemId)
				{
					return false;
				}
				break;
		}
		
		return true;
	}
}
