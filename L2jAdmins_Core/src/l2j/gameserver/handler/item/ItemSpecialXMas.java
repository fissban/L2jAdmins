package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ShowXMasSeal;

/**
 * @author devScarlet & mrTJO
 */
public class ItemSpecialXMas implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5555
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		// Token of Love
		((L2PcInstance) playable).broadcastPacket(new ShowXMasSeal(5555));
	}
}
