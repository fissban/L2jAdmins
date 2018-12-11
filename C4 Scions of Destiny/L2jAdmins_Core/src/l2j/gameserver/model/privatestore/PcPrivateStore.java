package l2j.gameserver.model.privatestore;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.trade.CharacterTradeList;
import l2j.gameserver.network.external.server.PledgeShowMemberListUpdate;
import main.data.ObjectData;
import main.holders.objects.PlayerHolder;

/**
 * @author fissban
 */
public class PcPrivateStore
{
	private final L2PcInstance player;
	private PrivateStoreList createList;
	/** sell list */
	private CharacterTradeList sellList;
	/** buy list */
	private CharacterTradeList buyList;
	
	private boolean inOfflineMode = false;
	private long offlineShopStart = 0;
	
	/** The Private Store type of the L2PcInstance */
	private PcStoreType privatestore = PcStoreType.NONE;
	
	private boolean inCraftMode;
	
	public PcPrivateStore(L2PcInstance player)
	{
		this.player = player;
	}
	
	/**
	 * createList object of the L2PcInstance.
	 * @return PrivateStoreList
	 */
	public PrivateStoreList getCreateList()
	{
		return createList;
	}
	
	/**
	 * Set the createList object of the L2PcInstance -> PrivateStoreManager.
	 * @param x : PrivateStoreList
	 */
	public void setCreateList(PrivateStoreList x)
	{
		createList = x;
	}
	
	/**
	 * sellList object of the L2PcInstance.
	 * @return CharacterTradeList
	 */
	public CharacterTradeList getSellList()
	{
		if (sellList == null)
		{
			sellList = new CharacterTradeList(player);
		}
		return sellList;
	}
	
	/**
	 * buyList object of the L2PcInstance -> PrivateStoreManager.
	 * @return CharacterTradeList
	 */
	public CharacterTradeList getBuyList()
	{
		if (buyList == null)
		{
			buyList = new CharacterTradeList(player);
		}
		return buyList;
	}
	
	/**
	 * Set the Private Store type of the L2PcInstance -> PrivateStoreManager.<BR>
	 * <B><U> Values </U> :</B><BR>
	 * <li>0 : NONE</li>
	 * <li>1 : SELL</li>
	 * <li>2 : SELL_MANAGE</li>
	 * <li>3 : BUY</li>
	 * <li>4 : BUY_MANAGE</li>
	 * <li>5 : MANUFACTURE</li>
	 * <li>8 : PACKAGE_SELL</li>
	 * @param type : StoreType
	 */
	public void setStoreType(PcStoreType type)
	{
		privatestore = type;
		
		if (Config.OFFLINE_DISCONNECT_FINISHED && (privatestore == PcStoreType.NONE) && inOfflineMode())
		{
			ObjectData.get(PlayerHolder.class, player).setOffline(false);
			player.closeConnection();
		}
	}
	
	/**
	 * Private Store type of the L2PcInstance -> PrivateStoreManager.
	 * @return StoreType
	 */
	public PcStoreType getStoreType()
	{
		return privatestore;
	}
	
	public boolean isInStoreMode()
	{
		return (privatestore != PcStoreType.NONE);
	}
	
	public boolean isInCraftMode()
	{
		return inCraftMode;
	}
	
	public void isInCraftMode(boolean b)
	{
		inCraftMode = b;
	}
	
	public long getOfflineStartTime()
	{
		return offlineShopStart;
	}
	
	public void setOfflineStartTime(long time)
	{
		offlineShopStart = time;
	}
	
	/**
	 * We determined whether the character is sitting selling/buying in off mode.
	 * @return boolean
	 */
	public boolean inOfflineMode()
	{
		return inOfflineMode;
	}
	
	/**
	 * Indicate that the character will be off to a sale/purchase private.
	 */
	public void setInOfflineMode()
	{
		inOfflineMode = true;
		
		if (player.getClan() != null)
		{
			player.getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(player), player);
		}
		
		player.leaveParty();
		
		if (player.getPet() != null)
		{
			player.getPet().unSummon();
		}
	}
}
