package l2j.gameserver.network.external.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import l2j.L2DatabaseFactory;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.7.2.4.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestDestroyItem extends AClientPacket
{
	private int objectId;
	private int count;
	
	@Override
	protected void readImpl()
	{
		objectId = 0;
		count = 0;
		
		try
		{
			objectId = readD();
			count = readD();
		}
		catch (Exception e)
		{
		}
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (count <= 0)
		{
			if (count < 0)
			{
				IllegalAction.report(activeChar, "[RequestDestroyItem] count < 0! ban! oid: " + objectId + " owner: " + activeChar.getName());
			}
			return;
		}
		
		if (activeChar.isRequestActive() || activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(objectId);
		
		// if we cant find requested item, its actually a cheat!
		if (itemToRemove == null)
		{
			return;
		}
		
		if (itemToRemove.isWear())
		{
			return;
		}
		
		if (!itemToRemove.isDestroyable())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if (!itemToRemove.isStackable() && (count > 1))
		{
			IllegalAction.report(activeChar, "[RequestDestroyItem] count > 1 but item is not stackable! oid: " + objectId + " owner: " + activeChar.getName());
			return;
		}
		
		// Cannot discard item that the skill is consuming
		if (activeChar.isCastingNow())
		{
			if ((activeChar.getCurrentSkill() != null) && (activeChar.getCurrentSkill().getSkill().getItemConsumeId() == itemToRemove.getId()))
			{
				activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}
		
		if (count > itemToRemove.getCount())
		{
			count = itemToRemove.getCount();
		}
		
		if (itemToRemove.isEquipped())
		{
			activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getEquipSlot());
		}
		
		int itemId = itemToRemove.getId();
		if (PetDataData.isPetItem(itemId))
		{
			if ((activeChar.getPet() != null) && (activeChar.getPet().getControlItemId() == objectId))
			{
				activeChar.getPet().unSummon();
			}
			
			// if it's a pet control item, delete the pet
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "could not delete pet objectid: ", e);
			}
		}
		
		// Destroy item
		activeChar.getInventory().destroyItem("Destroy", objectId, count, activeChar, null);
		// Update user info
		activeChar.broadcastUserInfo();
	}
}
