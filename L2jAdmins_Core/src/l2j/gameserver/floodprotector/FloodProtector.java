package l2j.gameserver.floodprotector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2j.Config;
import l2j.gameserver.floodprotector.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.util.UtilPrint;

/**
 * Flood protector
 * @author durgus, fissban
 */
public class FloodProtector
{
	private final Map<Integer, long[]> players;
	
	// reuse delays for protected actions (in game ticks 1 tick = 100ms)
	private static final int[] REUSEDELAY =
	{
		Config.PROTECTED_ROLLDICE,
		Config.PROTECTED_FIREWORK,
		Config.PROTECTED_ITEMPETSUMMON,
		Config.PROTECTED_HEROVOICE,
		Config.PROTECTED_GLOBALCHAT,
		Config.PROTECTED_MULTISELL,
		Config.PROTECTED_SUBCLASS,
		Config.PROTECTED_DROPITEM,
		Config.PROTECTED_BYPASS,
		Config.PROTECTED_POTION,
	};
	
	public FloodProtector()
	{
		UtilPrint.result("FloodProtector", "", "OK");
		players = new ConcurrentHashMap<>(Config.FLOODPROTECTOR_INITIALSIZE);
	}
	
	/**
	 * Add a new player to the flood protector (should be done for all players when they enter the world)
	 * @param player
	 */
	public void registerNewPlayer(L2PcInstance player)
	{
		// register the player with an empty array
		players.put(player.getObjectId(), new long[REUSEDELAY.length]);
	}
	
	/**
	 * Remove a player from the flood protector (should be done if player log off)
	 * @param player
	 */
	public void removePlayer(L2PcInstance player)
	{
		players.remove(player.getObjectId());
	}
	
	/**
	 * @return the size of the flood protector
	 */
	public int getSize()
	{
		return players.size();
	}
	
	/**
	 * Try to perform the requested action
	 * @param  player
	 * @param  action
	 * @return        true if the action may be performed
	 */
	public boolean tryPerformAction(L2PcInstance player, FloodProtectorType action)
	{
		if (players.get(player.getObjectId())[action.ordinal()] < System.currentTimeMillis())
		{
			players.get(player.getObjectId())[action.ordinal()] = System.currentTimeMillis() + REUSEDELAY[action.ordinal()];
			return true;
		}
		return false;
	}
	
	public static FloodProtector getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FloodProtector INSTANCE = new FloodProtector();
	}
}
