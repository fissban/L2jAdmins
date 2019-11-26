package l2j.gameserver.network.external.client;

import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ManufactureItemHolder;
import l2j.gameserver.model.privatestore.PcStoreType;
import l2j.gameserver.model.privatestore.PrivateStoreList;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.RecipeShopMsg;

/**
 * This class ... cd(dd)
 * @version $Revision: 1.1.2.3.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestRecipeShopListSet extends AClientPacket
{
	private int count;
	private int[] items; // count*2
	
	@Override
	protected void readImpl()
	{
		count = readD();
		items = new int[count * 2];
		for (int x = 0; x < count; x++)
		{
			int recipeID = readD();
			items[(x * 2) + 0] = recipeID;
			int cost = readD();
			items[(x * 2) + 1] = cost;
		}
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (player.cantAttack() || player.isAttackingNow() || player.isImmobilized() || player.isCastingNow())
		{
			return;
		}
		
		if (count == 0)
		{
			player.getPrivateStore().setStoreType(PcStoreType.NONE);
			player.broadcastUserInfo();
		}
		else
		{
			PrivateStoreList createList = new PrivateStoreList();
			
			for (int x = 0; x < count; x++)
			{
				int recipeID = items[(x * 2) + 0];
				int cost = items[(x * 2) + 1];
				
				if (!player.hasRecipeList(recipeID))
				{
					IllegalAction.report(player, "Warning!! Player " + player.getName() + " of account " + player.getAccountName() + " tried to set recipe which he dont have.");
					return;
				}
				
				createList.add(new ManufactureItemHolder(recipeID, cost));
			}
			createList.setStoreName(player.getPrivateStore().getCreateList() != null ? player.getPrivateStore().getCreateList().getStoreName() : "");
			player.getPrivateStore().setCreateList(createList);
			
			player.getPrivateStore().setStoreType(PcStoreType.MANUFACTURE);
			player.sitDown();
			player.broadcastUserInfo();
			player.broadcastPacket(new RecipeShopMsg(player));
		}
	}
}
