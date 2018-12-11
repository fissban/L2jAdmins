package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.8.2.3.2.7 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestUnEquipItem extends AClientPacket
{
	// cd
	private int slot;
	
	@Override
	protected void readImpl()
	{
		slot = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		ItemInstance item = activeChar.getInventory().getPaperdollItemByL2ItemId(slot);
		if ((item != null) && item.isWear())
		{
			return;
		}
		
		// Prevent player to remove the weapon on special conditions
		if (activeChar.isCastingNow() || activeChar.isStunned() || activeChar.isSleeping() || activeChar.isParalyzed() || activeChar.isAlikeDead())
		{
			return;
		}
		
		List<ItemInstance> unequiped = activeChar.getInventory().unEquipItemInBodySlotAndRecord(SlotType.valueOfMask(slot));
		InventoryUpdate iu = new InventoryUpdate();
		
		for (ItemInstance element : unequiped)
		{
			iu.addModifiedItem(element);
		}
		activeChar.sendPacket(iu);
		
		activeChar.broadcastUserInfo();
		
		// this can be 0 if the user pressed the right mouse button twice very fast
		if (!unequiped.isEmpty())
		{
			SystemMessage sm = null;
			
			if (unequiped.get(0).getEnchantLevel() > 0)
			{
				sm = new SystemMessage(SystemMessage.EQUIPMENT_S1_S2_REMOVED);
				sm.addNumber(unequiped.get(0).getEnchantLevel());
				sm.addItemName(unequiped.get(0).getId());
			}
			else
			{
				sm = new SystemMessage(SystemMessage.S1_DISARMED);
				sm.addItemName(unequiped.get(0).getId());
			}
			
			activeChar.sendPacket(sm);
		}
	}
}
