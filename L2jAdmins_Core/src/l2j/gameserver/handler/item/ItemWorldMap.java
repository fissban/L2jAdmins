package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ShowMiniMap;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:30:07 $
 */
public class ItemWorldMap implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			1665,
			1863
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		((L2PcInstance) playable).sendPacket(new ShowMiniMap(item.getId()));
		return;
	}
}
