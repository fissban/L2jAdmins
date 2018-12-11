package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.SSQStatus;

/**
 * Item Handler for Seven Signs Record
 * @author Tempy
 */
public class ItemSevenSignsRecord implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5707
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		L2PcInstance activeChar;
		
		if (playable instanceof L2PcInstance)
		{
			activeChar = (L2PcInstance) playable;
		}
		else if (playable instanceof L2PetInstance)
		{
			activeChar = ((L2PetInstance) playable).getOwner();
		}
		else
		{
			return;
		}
		
		activeChar.sendPacket(new SSQStatus(activeChar, 1));
	}
}
