package l2j.gameserver.task.scheduled;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.task.AbstractTask;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class RecomTaskManager extends AbstractTask implements Runnable
{
	public class PlayerRecomHolder
	{
		public int objId;
		public int level;
		public int recomHave;
		
		public PlayerRecomHolder(int objId, int level, int recomHave)
		{
			this.objId = objId;
			this.level = level;
			this.recomHave = recomHave;
		}
	}
	
	public RecomTaskManager()
	{
		specificSchedule(this, 1, "06:30:00");
		UtilPrint.result("RecomTaskManager", "", "OK");
	}
	
	@Override
	public void run()
	{
		List<PlayerRecomHolder> players = new ArrayList<>();
		
		// Read all players in db
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT obj_id,level,rec_have FROM characters"))
		{
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					int obj_id = rset.getInt("obj_id");
					int level = rset.getInt("level");
					int recomHave = rset.getInt("rec_have");
					
					// only select offline players
					if (L2World.getInstance().getPlayer(obj_id) == null)
					{
						players.add(new PlayerRecomHolder(obj_id, level, recomHave));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.severe(getClass().getSimpleName() + ": Could not reset Recommendations System: " + e);
		}
		
		// Update rec left and rec have for offline players
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE characters SET rec_left=?,rec_have=? WHERE obj_id=?"))
		{
			int count = 0;
			for (PlayerRecomHolder prh : players)
			{
				if (prh.level < 20)
				{
					ps.setInt(1, 3);
					prh.recomHave -= 1;
				}
				else if (prh.level < 40)
				{
					ps.setInt(1, 6);
					prh.recomHave -= 2;
				}
				else
				{
					ps.setInt(1, 9);
					prh.recomHave -= 3;
				}
				
				if (prh.recomHave < 0)
				{
					prh.recomHave = 0;
				}
				
				ps.setInt(2, prh.recomHave);
				ps.setInt(3, prh.objId);
				ps.addBatch();
				count++;
			}
			
			if (count > 0)
			{
				ps.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOG.severe(getClass().getSimpleName() + ": Could not reset Recommendations System: " + e);
		}
		
		// Update rec left and rec have for online players
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			player.restartRecom();
			player.sendPacket(new UserInfo(player));
		}
		LOG.config("Recommendation Global Task: launched.");
	}
	
	public final static RecomTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RecomTaskManager INSTANCE = new RecomTaskManager();
	}
}
