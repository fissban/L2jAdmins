package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.2.2.3.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestCrystallizeItem extends AClientPacket
{
	private int objectId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		readD(); // count
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isInCrystallize())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		int skillLevel = activeChar.getSkillLevel(Skill.SKILL_CRYSTALLIZE);
		if (skillLevel <= 0)
		{
			activeChar.sendPacket(SystemMessage.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
		
		if ((item == null) || item.isWear())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!item.isDestroyable() || !item.getItem().isCrystallizable())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((item.getItem().getCrystalCount() <= 0) || (item.getItem().getCrystalType() == CrystalType.CRYSTAL_NONE))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		// Check if the char can crystallize C items and return if false
		if ((item.getItem().getCrystalType() == CrystalType.CRYSTAL_C) && (skillLevel <= 1))
		{
			activeChar.sendPacket(SystemMessage.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		// Check if the user can crystallize B items and return if false
		if ((item.getItem().getCrystalType() == CrystalType.CRYSTAL_B) && (skillLevel <= 2))
		{
			activeChar.sendPacket(SystemMessage.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		// Check if the user can crystallize A items and return if false
		if ((item.getItem().getCrystalType() == CrystalType.CRYSTAL_A) && (skillLevel <= 3))
		{
			activeChar.sendPacket(SystemMessage.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		// Check if the user can crystallize S items and return if false;
		if ((item.getItem().getCrystalType() == CrystalType.CRYSTAL_S) && (skillLevel <= 4))
		{
			activeChar.sendPacket(SystemMessage.CRYSTALLIZE_LEVEL_TOO_LOW);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		activeChar.setInCrystallize(true);
		
		// Unequip if needed
		if (item.isEquipped())
		{
			List<ItemInstance> unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(item.getEquipSlot());
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance element : unequiped)
			{
				iu.addModifiedItem(element);
			}
			activeChar.sendPacket(iu);
		}
		
		// Remove from inventory
		activeChar.getInventory().destroyItem("Crystalize", objectId, item.getCount(), activeChar, null);
		
		// Add crystals
		int crystalId = item.getItem().getCrystalItemId();
		int crystalAmount = item.getCrystalCount();
		ItemInstance createdItem = activeChar.getInventory().addItem("Crystalize", crystalId, crystalAmount, activeChar, item);
		// Send message
		activeChar.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(crystalId).addNumber(crystalAmount));
		
		// Send inventory update
		InventoryUpdate iu = new InventoryUpdate();
		if (item.getCount() == 0)
		{
			iu.addRemovedItem(item);
		}
		else
		{
			iu.addModifiedItem(item);
		}
		
		if (createdItem.getCount() != crystalAmount)
		{
			iu.addModifiedItem(createdItem);
		}
		else
		{
			iu.addNewItem(createdItem);
		}
		activeChar.sendPacket(iu);
		
		// Update current load as well
		activeChar.updateCurLoad();
		// Update user info
		activeChar.broadcastUserInfo();
		// Remove object from world
		L2World.getInstance().removeObject(item);
		
		activeChar.setInCrystallize(false);
	}
}
