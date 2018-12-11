package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:33 $
 */
public class RequestGiveItemToPet extends AClientPacket
{
	private int objectId;
	private int amount;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		amount = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if ((player == null) || (player.getPet() == null) || !(player.getPet() instanceof L2PetInstance))
		{
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			return;
		}
		
		// Alt game - Karma punishment
		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getKarma() > 0))
		{
			return;
		}
		
		if (player.getPrivateStore().isInStoreMode())
		{
			player.sendMessage("Cannot exchange items while trading.");
			return;
		}
		
		if (player.isMounted())
		{
			return;
		}
		
		ItemInstance item = player.getInventory().getItemByObjectId(objectId);
		
		if ((item == null) || (amount == 0))
		{
			return;
		}
		
		if (item.getType() == EtcItemType.QUEST)
		{
			return;
		}
		
		if (!item.isDropable() || !item.isDestroyable() || !item.isTradeable())
		{
			return;
		}
		
		int itemId = item.getId();
		
		if (((itemId >= 6611) && (itemId <= 6621)) || (itemId == 6842))
		{
			return;
		}
		
		if ((itemId >= 6834) && (itemId <= 6841))
		{
			return;
		}
		
		L2PetInstance pet = (L2PetInstance) player.getPet();
		if (pet.isDead())
		{
			sendPacket(new SystemMessage(SystemMessage.CANNOT_GIVE_ITEMS_TO_DEAD_PET));
			return;
		}
		
		if (amount < 0)
		{
			return;
		}
		
		if (!pet.getInventory().validateCapacity(item))
		{
			pet.getOwner().sendMessage("Your pet cannot carry any more items.");
			return;
		}
		
		if (!pet.getInventory().validateWeight(item, amount))
		{
			pet.getOwner().sendMessage("Your pet is overweight and cannot carry any more items.");
			return;
		}
		
		if (player.getInventory().transferItem("Transfer", objectId, amount, pet.getInventory(), pet) == null)
		{
			LOG.warning("Invalid Item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return true;
	}
}
