package l2j.gameserver.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class stores references to all online game masters. (access level > 100)
 * @version $Revision: 1.2.2.1.2.7 $ $Date: 2005/04/05 19:41:24 $
 */
public class GmListData
{
	/** Set(L2PcInstance>) containing all the GM in game */
	private final List<L2PcInstance> gmList = new ArrayList<>();
	
	public Collection<L2PcInstance> getAllGms()
	{
		return gmList;
	}
	
	/**
	 * Add a L2PcInstance player to the Set gmList
	 * @param player
	 */
	public void addGm(L2PcInstance player)
	{
		gmList.add(player);
	}
	
	public void deleteGm(L2PcInstance player)
	{
		gmList.remove(player);
	}
	
	public boolean isGmOnline()
	{
		return (!gmList.isEmpty());
	}
	
	public void sendListToPlayer(L2PcInstance player)
	{
		if (gmList.isEmpty())
		{
			player.sendPacket(SystemMessage.NO_GM_PROVIDING_SERVICE_NOW);
		}
		else
		{
			player.sendPacket(SystemMessage.GM_LIST);
			gmList.forEach(gm -> gm.sendPacket(new SystemMessage(SystemMessage.GM_C1).addString(gm.getName())));
			player.sendPacket(SystemMessage.GM_LIST);
		}
	}
	
	public void broadcastToGMs(AServerPacket packet)
	{
		gmList.forEach(gm -> gm.sendPacket(packet));
	}
	
	public void broadcastMessageToGMs(String message)
	{
		gmList.forEach(gm -> gm.sendMessage(message));
	}
	
	public static GmListData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GmListData INSTANCE = new GmListData();
	}
}
