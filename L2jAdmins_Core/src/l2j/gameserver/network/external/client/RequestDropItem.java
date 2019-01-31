package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.audit.GMAudit;

/**
 * This class ...
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/02 21:25:21 $
 */
public class RequestDropItem extends AClientPacket
{
	private int objectId;
	private int count;
	private int x;
	private int y;
	private int z;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		count = readD();
		x = readD();
		y = readD();
		z = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isDead())
		{
			return;
		}
		
		if (!activeChar.tryToUseAction(FloodProtectorType.DROP_ITEM))
		{
			return;
		}
		
		ItemInstance item = activeChar.getInventory().getItemByObjectId(objectId);
		
		if ((item == null) || (count == 0) || !activeChar.validateItemManipulation(objectId, "drop") || (!Config.ALLOW_DISCARDITEM && !activeChar.isGM()) || !item.isDropable())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		// TODO
		// Cambiar esto por el nuevo sistema de items al crear los neuvos xml
		if ((item.getType() == EtcItemType.QUEST) && !activeChar.isGM())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_THIS_ITEM);
			return;
		}
		
		if (count > item.getCount())
		{
			activeChar.sendPacket(SystemMessage.NOTHING_HAPPENED);
			return;
		}
		
		if (count < 0)
		{
			IllegalAction.report(activeChar, "[RequestDropItem] count <= 0! ban! oid: " + objectId + " owner: " + activeChar.getName(), IllegalActionType.PUNISH_KICK);
			return;
		}
		
		if (!item.isStackable() && (count > 1))
		{
			IllegalAction.report(activeChar, "[RequestDropItem] count > 1 but item is not stackable! ban! oid: " + objectId + " owner: " + activeChar.getName(), IllegalActionType.PUNISH_KICK);
			return;
		}
		
		if (Config.GM_DISABLE_TRANSACTION && (activeChar.isGM()))
		{
			activeChar.sendMessage("Transactions are disable for your Access Level");
			activeChar.sendPacket(SystemMessage.NOTHING_HAPPENED);
			return;
		}
		
		if (activeChar.isRequestActive() || activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE);
			return;
		}
		
		if (activeChar.getFishing().isFishing())
		{
			// You can't mount, dismount, break and drop items while fishing
			activeChar.sendPacket(SystemMessage.CANNOT_DO_WHILE_FISHING_2);
			return;
		}
		
		// Cannot discard an equipped item or an item that the skill is consuming
		if (activeChar.isCastingNow())
		{
			if (item.isEquipped() || ((activeChar.getCurrentSkill() != null) && (activeChar.getCurrentSkill().getSkill().getItemConsumeId() == item.getId())))
			{
				activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_THIS_ITEM);
				return;
			}
		}
		
		if ((ItemType2.QUEST == item.getItem().getType2()) && !activeChar.isGM())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_EXCHANGE_ITEM);
			
			return;
		}
		
		if (!activeChar.isInsideRadius(x, y, 150, false) || (Math.abs(z - activeChar.getZ()) > 50))
		{
			activeChar.sendPacket(SystemMessage.CANNOT_DISCARD_DISTANCE_TOO_FAR);
			return;
		}
		
		if (item.isEquipped())
		{
			activeChar.getInventory().unEquipItemInBodySlotAndRecord(item.getItem().getBodyPart());
			activeChar.sendPacket(new ItemList(activeChar, true));
			activeChar.broadcastUserInfo();
		}
		
		ItemInstance dropedItem = activeChar.getInventory().dropItem("Drop", objectId, count, x, y, z, null, false, false);
		
		if (activeChar.isGM())
		{
			String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
			GMAudit.auditGMAction(activeChar.getName(), "drop", target, dropedItem.getId() + " - " + dropedItem.getItemName() + " - " + dropedItem.getObjectId());
		}
		
		if ((dropedItem != null) && (dropedItem.getId() == Inventory.ADENA_ID) && (dropedItem.getCount() >= 1000000))
		{
			String msg = "Character (" + activeChar.getName() + ") has dropped (" + dropedItem.getCount() + ")adena at (" + x + "," + y + "," + z + ")";
			LOG.warning(msg);
			GmListData.getInstance().broadcastMessageToGMs(msg);
		}
	}
}
