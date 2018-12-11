package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ShowCalculator;

/**
 * @author fissban
 */
public class ItemCalculator implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			4393
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		playable.sendPacket(new ShowCalculator(4393));
	}
}
