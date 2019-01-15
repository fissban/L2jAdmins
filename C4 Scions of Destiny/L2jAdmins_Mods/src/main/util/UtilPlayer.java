package main.util;

import java.util.concurrent.locks.ReentrantLock;

import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.GameClient;
import l2j.gameserver.network.GameClient.GameClientState;
import l2j.gameserver.network.thread.LoginServerThread;

/**
 * @author fissban
 */
public class UtilPlayer
{
	private static final ReentrantLock locker = new ReentrantLock();
	
	public static L2PcInstance spawnPlayer(int objectId)
	{
		locker.lock();
		
		L2PcInstance player = null;
		try
		{
			GameClient client = new GameClient(null);
			
			player = L2PcInstance.load(objectId);
			client.setActiveChar(player);
			client.setAccountName(player.getAccountName());
			client.setState(GameClientState.IN_GAME);
			client.setDetached(true);
			player.setConnected(false);
			player.setClient(client);
			player.spawnMe(player.getX(), player.getY(), player.getZ());
			LoginServerThread.getInstance().addClient(player.getAccountName(), client);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			locker.unlock();
		}
		
		return player;
	}
	
	// XXX Change class
	/**
	 * Returns true if class change is possible
	 * @param  oldCID current player ClassId
	 * @param  newCID new ClassId
	 * @return        true if class change is possible
	 */
	public static final boolean validateChangeClassId(ClassId oldCID, int val)
	{
		try
		{
			return UtilPlayer.validateChangeClassId(oldCID, ClassId.values()[val]);
		}
		catch (Exception e)
		{
			// possible ArrayOutOfBoundsException
		}
		return false;
	}
	
	/**
	 * Returns true if class change is possible
	 * @param  oldCID current player ClassId
	 * @param  newCID new ClassId
	 * @return        true if class change is possible
	 */
	public static final boolean validateChangeClassId(ClassId oldCID, ClassId newCID)
	{
		if ((newCID == null) || (newCID.getRace() == null))
		{
			return false;
		}
		
		if (oldCID.equals(newCID.getParent()))
		{
			return true;
		}
		
		// if (Config.ALLOW_ENTIRE_TREE && newCID.childOf(oldCID))
		// {
		// return true;
		// }
		
		return false;
	}
	
	/**
	 * Returns minimum player level required for next class transfer
	 * @param  level - current skillId level (0 - start, 1 - first, etc)
	 * @return
	 */
	public static final int getMinLevelChangeClass(int level)
	{
		switch (level)
		{
			case 0:
				return 20;
			case 1:
				return 40;
			case 2:
				return 76;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
