package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.gameserver.model.itemcontainer.inventory.PcFreightManager;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PackageSendableList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Format: (c)d d: char object id (?)
 * @author -Wooden-
 */
public class RequestPackageSendableItemList extends AClientPacket
{
	private int targetId;
	
	@Override
	protected void readImpl()
	{
		targetId = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		List<ItemInstance> items = getClient().getActiveChar().getInventory().getAvailableItems(true);
		
		getClient().getActiveChar().setActiveWarehouse(new PcFreightManager(null));
		
		// build list...
		sendPacket(new PackageSendableList(items, targetId));
		sendPacket(new SystemMessage(SystemMessage.ITEMS_SENT_BY_FREIGHT_PICKED_UP_FROM_ANYWHERE));
	}
}
