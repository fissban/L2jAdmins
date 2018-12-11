package l2j.gameserver.network;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.holder.CharSelectInfoHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.LeaveWorld;
import l2j.gameserver.network.thread.LoginServerThread;
import l2j.loginserver.network.SessionKey;
import l2j.mmocore.MMOClient;
import l2j.mmocore.MMOConnection;
import l2j.mmocore.ReceivablePacket;
import main.data.ObjectData;
import main.holders.objects.PlayerHolder;

/**
 * This class ...
 * @version $Revision: 1.21.2.19.2.12 $ $Date: 2005/04/04 19:47:01 $
 */
public final class GameClient extends MMOClient<MMOConnection<GameClient>> implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(GameClient.class.getName());
	
	public static enum GameClientState
	{
		CONNECTED, // client has just connected
		AUTHED, // client has authed but doesnt has character attached to it yet
		IN_GAME // client has selected a char and is in game
	}
	
	public GameClientState state;
	
	// Info
	private String accountName;
	private SessionKey sessionId;
	private L2PcInstance activeChar;
	private final ReentrantLock activeCharLock = new ReentrantLock();
	
	@SuppressWarnings("unused")
	private boolean isAuthedGG;
	private final long connectionStartTime;
	private List<CharSelectInfoHolder> slots;
	
	// Task
	protected final ScheduledFuture<?> autoSaveInDB;
	protected ScheduledFuture<?> cleanupTask = null;
	
	public GameCrypt crypt;
	private final ClientStats stats;
	
	private boolean isDetached = false;
	
	private final ArrayBlockingQueue<ReceivablePacket<GameClient>> packetQueue;
	private final ReentrantLock queueLock = new ReentrantLock();
	
	public GameClient(MMOConnection<GameClient> con)
	{
		super(con);
		state = GameClientState.CONNECTED;
		connectionStartTime = System.currentTimeMillis();
		crypt = new GameCrypt();
		stats = new ClientStats();
		packetQueue = new ArrayBlockingQueue<>(Config.CLIENT_PACKET_QUEUE_SIZE);
		
		autoSaveInDB = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> autoSaveTask(), 300000L, 900000L);
	}
	
	public byte[] enableCrypt()
	{
		var key = BlowFishKeygen.getKey();
		
		crypt.setKey(key);
		return key;
	}
	
	public GameClientState getState()
	{
		return state;
	}
	
	public void setState(GameClientState pState)
	{
		if (state != pState)
		{
			state = pState;
			packetQueue.clear();
		}
	}
	
	public ClientStats getStats()
	{
		return stats;
	}
	
	public long getConnectionStartTime()
	{
		return connectionStartTime;
	}
	
	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		crypt.decrypt(buf.array(), buf.position(), size);
		return true;
	}
	
	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}
	
	public L2PcInstance getActiveChar()
	{
		return activeChar;
	}
	
	public void setActiveChar(L2PcInstance pActiveChar)
	{
		activeChar = pActiveChar;
	}
	
	public ReentrantLock getActiveCharLock()
	{
		return activeCharLock;
	}
	
	public void setGameGuardOk(boolean val)
	{
		isAuthedGG = val;
	}
	
	public void setAccountName(String pAccountName)
	{
		accountName = pAccountName;
	}
	
	public String getAccountName()
	{
		return accountName;
	}
	
	public void setSessionId(SessionKey sk)
	{
		sessionId = sk;
	}
	
	public SessionKey getSessionId()
	{
		return sessionId;
	}
	
	public void sendPacket(AServerPacket gsp)
	{
		if (isDetached)
		{
			return;
		}
		
		getConnection().sendPacket(gsp);
		gsp.runImpl();
	}
	
	public boolean isDetached()
	{
		return isDetached;
	}
	
	public void setDetached(boolean b)
	{
		isDetached = b;
	}
	
	/**
	 * Method to handle character deletion
	 * @param  charslot The slot to check.
	 * @return          a byte:
	 *                  <li>-1: Error: No char was found for such charslot, caught exception, etc...
	 *                  <li>0: character is not member of any clan, proceed with deletion
	 *                  <li>1: character is member of a clan, but not clan leader
	 *                  <li>2: character is clan leader
	 */
	public int markToDeleteChar(int charslot)
	{
		var objid = getObjectIdForSlot(charslot);
		
		if (objid < 0)
		{
			return -1;
		}
		
		try (var con = L2DatabaseFactory.getInstance().getConnection())
		{
			var answer = 0;
			
			try (var statement = con.prepareStatement("SELECT clanId FROM characters WHERE obj_id=?"))
			{
				statement.setInt(1, objid);
				try (var rs = statement.executeQuery())
				{
					rs.next();
					
					int clanId = rs.getInt(1);
					
					if (clanId != 0)
					{
						Clan clan = ClanData.getInstance().getClanById(clanId);
						
						if (clan == null)
						{
							answer = 0; // jeezes!
						}
						else if (clan.getLeaderId() == objid)
						{
							answer = 2;
						}
						else
						{
							answer = 1;
						}
					}
				}
			}
			
			// Setting delete time
			if (answer == 0)
			{
				if (Config.DELETE_DAYS == 0)
				{
					deleteCharByObjId(objid);
				}
				else
				{
					try (var ps = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?"))
					{
						ps.setLong(1, System.currentTimeMillis() + (Config.DELETE_DAYS * 86400000L));
						ps.setInt(2, objid);
						ps.execute();
					}
				}
			}
			
			return answer;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error updating delete time of character.", e);
			return -1;
		}
	}
	
	public void markRestoredChar(int charslot)
	{
		var objid = getObjectIdForSlot(charslot);
		if (objid < 0)
		{
			return;
		}
		
		try (var con = L2DatabaseFactory.getInstance().getConnection();
			var ps = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?"))
		{
			ps.setInt(1, objid);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error restoring character.", e);
		}
	}
	
	@SuppressWarnings("resource")
	public static void deleteCharByObjId(int objid)
	{
		if (objid < 0)
		{
			return;
		}
		
		CharNameData.getInstance().removeName(objid);
		
		try (var con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps;
			
			ps = con.prepareStatement("DELETE FROM character_friends WHERE char_id=? OR friend_id=?");
			ps.setInt(1, objid);
			ps.setInt(2, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_macroses WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_quests WHERE charId=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM heroes WHERE char_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM olympiad_nobles WHERE char_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM seven_signs WHERE char_obj_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM augmentations WHERE item_id IN (SELECT object_id FROM items WHERE items.owner_id=?)");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM items WHERE owner_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM character_raid_points WHERE char_id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM characters WHERE obj_Id=?");
			ps.setInt(1, objid);
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error deleting character.", e);
		}
	}
	
	public L2PcInstance loadCharFromDisk(int slot)
	{
		var objectId = getObjectIdForSlot(slot);
		if (objectId < 0)
		{
			return null;
		}
		
		var player = L2World.getInstance().getPlayer(objectId);
		if (player != null)
		{
			// exploit prevention, should not happens in normal way
			LOG.severe("Attempt of double login: " + player.getName() + "(" + objectId + ") " + getAccountName());
			
			if (player.getClient() != null)
			{
				player.getClient().closeNow();
			}
			else
			{
				player.deleteMe();
			}
			
			return null;
		}
		
		player = L2PcInstance.load(objectId);
		if (player != null)
		{
			player.setRunning(); // running is default
			player.standUp(); // standing is default
			
			player.setOnlineStatus(true);
			// L2World.getInstance().addPlayer(player);
		}
		else
		{
			LOG.severe("L2GameClient: could not restore in slot: " + slot);
		}
		
		return player;
	}
	
	/**
	 * Get a {@link CharSelectInfoHolder} based on its id. Integrity checks are included.
	 * @param  id : The slot id to call.
	 * @return    the associated slot informations based on slot id.
	 */
	public CharSelectInfoHolder getCharSelectSlot(int id)
	{
		if ((slots == null) || (id < 0) || (id >= slots.size()))
		{
			return null;
		}
		
		return slots.get(id);
	}
	
	/**
	 * Set the character selection slots.
	 * @param list : Use the List as character slots.
	 */
	public void setCharSelectSlot(List<CharSelectInfoHolder> list)
	{
		slots = list;
	}
	
	/**
	 * @param  charslot
	 * @return
	 */
	private int getObjectIdForSlot(int charslot)
	{
		final CharSelectInfoHolder info = getCharSelectSlot(charslot);
		if (info == null)
		{
			LOG.warning(toString() + " tried to delete Character in slot " + charslot + " but no characters exits at that slot.");
			return -1;
		}
		return info.getObjectId();
	}
	
	@Override
	public void onForcedDisconnection()
	{
		LOG.fine("Client " + toString() + " disconnected abnormally.");
	}
	
	@Override
	public void onDisconnection()
	{
		// no long running tasks here, do it async
		try
		{
			ThreadPoolManager.getInstance().execute(new DisconnectTask());
		}
		catch (RejectedExecutionException e)
		{
			// server is closing
		}
	}
	
	/**
	 * Close client connection with {@link LeaveWorld} packet
	 */
	public void closeNow()
	{
		isDetached = true; // prevents more packets execution
		close(LeaveWorld.STATIC_PACKET);
		synchronized (this)
		{
			if (cleanupTask != null)
			{
				cancelCleanup();
			}
			
			cleanupTask = ThreadPoolManager.getInstance().schedule(new CleanupTask(), 0); // instant
		}
	}
	
	public void close(AServerPacket gsp)
	{
		if (getConnection() == null)
		{
			return;
		}
		
		getConnection().close(gsp);
	}
	
	/**
	 * Produces the best possible string representation of this client.
	 */
	@Override
	public String toString()
	{
		try
		{
			final InetAddress address = getConnection().getInetAddress();
			switch (getState())
			{
				case CONNECTED:
					return "[IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				case AUTHED:
					return "[Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				case IN_GAME:
					return "[Character: " + (getActiveChar() == null ? "disconnected" : getActiveChar().getName()) + " - Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				default:
					throw new IllegalStateException("Missing state on switch");
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}
	
	protected class DisconnectTask implements Runnable
	{
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			boolean fast = true;
			
			try
			{
				if ((getActiveChar() != null) && !isDetached())
				{
					setDetached(true);
					if (ObjectData.get(PlayerHolder.class, getActiveChar()).isOffline())
					{
						return;
					}
					
					fast = !getActiveChar().isInCombat() && !getActiveChar().isLocked();
				}
				cleanMe(fast);
			}
			catch (Exception e1)
			{
				LOG.log(Level.WARNING, "error while disconnecting client", e1);
			}
		}
	}
	
	public void cleanMe(boolean fast)
	{
		try
		{
			synchronized (this)
			{
				if (cleanupTask == null)
				{
					System.out.println("cleanMe Task start");
					cleanupTask = ThreadPoolManager.getInstance().schedule(new CleanupTask(), fast ? 5 : 15000L);
				}
			}
		}
		catch (Exception e1)
		{
			LOG.log(Level.WARNING, "Error during cleanup.", e1);
		}
	}
	
	protected class CleanupTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				// we are going to manually save the char below thus we can force the cancel
				if (autoSaveInDB != null)
				{
					autoSaveInDB.cancel(true);
				}
				
				if (getActiveChar() != null) // this should only happen on connection loss
				{
					if (getActiveChar().isLocked())
					{
						LOG.log(Level.WARNING, getActiveChar().getName() + " is still performing subclass actions during disconnect.");
					}
					
					if (getActiveChar().isOnline())
					{
						getActiveChar().deleteMe();
					}
					
					// prevent closing again
					getActiveChar().setClient(null);
				}
				setActiveChar(null);
			}
			catch (Exception e1)
			{
				LOG.log(Level.WARNING, "Error while cleanup client.", e1);
			}
			finally
			{
				LoginServerThread.getInstance().sendLogout(getAccountName());
			}
		}
	}
	
	public void autoSaveTask()
	{
		try
		{
			if ((getActiveChar() != null) && getActiveChar().isOnline())
			{
				getActiveChar().store();
				
				if (getActiveChar().getPet() != null)
				{
					getActiveChar().getPet().store();
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Error on AutoSaveTask.", e);
		}
	}
	
	/**
	 * @return false if client can receive packets. True if detached, or flood detected, or queue overflow detected and queue still not empty.
	 */
	public boolean dropPacket()
	{
		if (isDetached)
		{
			return true;
		}
		
		// flood protection
		if (getStats().countPacket(packetQueue.size()))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		return getStats().dropPacket();
	}
	
	/**
	 * Counts buffer underflow exceptions.
	 */
	public void onBufferUnderflow()
	{
		if (getStats().countUnderflowException())
		{
			LOG.severe("Client " + toString() + " - Disconnected: Too many buffer underflow exceptions.");
			closeNow();
			return;
		}
		if (state == GameClientState.CONNECTED) // in CONNECTED state kick client immediately
		{
			closeNow();
		}
	}
	
	/**
	 * Counts unknown packets
	 */
	public void onUnknownPacket()
	{
		if (getStats().countUnknownPacket())
		{
			LOG.severe("Client " + toString() + " - Disconnected: Too many unknown packets.");
			closeNow();
			return;
		}
		if (state == GameClientState.CONNECTED) // in CONNECTED state kick client immediately
		{
			closeNow();
		}
	}
	
	/**
	 * Add packet to the queue and start worker thread if needed
	 * @param packet The packet to execute.
	 */
	public void execute(ReceivablePacket<GameClient> packet)
	{
		if (getStats().countFloods())
		{
			LOG.severe("Client " + toString() + " - Disconnected, too many floods:" + getStats().longFloods + " long and " + getStats().shortFloods + " short.");
			closeNow();
			return;
		}
		
		if (!packetQueue.offer(packet))
		{
			if (getStats().countQueueOverflow())
			{
				LOG.severe("Client " + toString() + " - Disconnected, too many queue overflows.");
				closeNow();
			}
			else
			{
				sendPacket(ActionFailed.STATIC_PACKET);
			}
			
			return;
		}
		
		if (queueLock.isLocked())
		{
			return;
		}
		
		try
		{
			if ((state == GameClientState.CONNECTED) && (getStats().processedPackets > 3))
			{
				closeNow();
				return;
			}
			
			ThreadPoolManager.getInstance().execute(this);
		}
		catch (RejectedExecutionException e)
		{
		}
	}
	
	@Override
	public void run()
	{
		if (!queueLock.tryLock())
		{
			return;
		}
		
		try
		{
			int count = 0;
			ReceivablePacket<GameClient> packet;
			while (true)
			{
				packet = packetQueue.poll();
				if (packet == null)
				{
					return;
				}
				
				if (isDetached) // clear queue immediately after detach
				{
					packetQueue.clear();
					return;
				}
				
				try
				{
					packet.run();
				}
				catch (Exception e)
				{
					LOG.severe("Exception during execution " + packet.getClass().getSimpleName() + ", client: " + toString() + "," + e.getMessage());
				}
				
				count++;
				if (getStats().countBurst(count))
				{
					return;
				}
			}
		}
		finally
		{
			queueLock.unlock();
		}
	}
	
	private boolean cancelCleanup()
	{
		final Future<?> task = cleanupTask;
		if (task != null)
		{
			cleanupTask = null;
			return task.cancel(true);
		}
		return false;
	}
}
