package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import l2j.DatabaseManager;
import l2j.gameserver.model.holder.NpcWalkerHolder;
import l2j.util.UtilPrint;

/**
 * Main Table to Load Npc Walkers Routes and Chat SQL Table.<br>
 * @author Rayan RPG for L2Emu Project
 * @since  927
 */
public class NpcWalkerRoutesData
{
	private static final Logger LOG = Logger.getLogger(NpcWalkerRoutesData.class.getName());
	
	private final List<NpcWalkerHolder> routes = new ArrayList<>();
	
	public NpcWalkerRoutesData()
	{
		load();
	}
	
	public void load()
	{
		routes.clear();
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT npc_id, chatText, move_x, move_y, move_z, delay, running FROM walker_routes ORDER By move_point ASC");
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				// route.setRouteId(rset.getInt("route_id")); TODO remover cuando se pase a xml
				// route.setMovePoint(rset.getString("move_point")); TODO remover cuando se pase a xml
				
				routes.add(new NpcWalkerHolder(rset.getInt("npc_id"), rset.getString("chatText"), rset.getInt("move_x"), rset.getInt("move_y"), rset.getInt("move_z"), rset.getBoolean("running"), rset.getInt("delay")));
			}
		}
		catch (final Exception e)
		{
			LOG.warning("WalkerRoutesTable: Error while loading Npc Walkers Routes: " + e.getMessage());
		}
		
		UtilPrint.result("WalkerRoutesData", "Loaded npc walker routers", routes.size());
	}
	
	public List<NpcWalkerHolder> getRouteForNpc(int id)
	{
		return routes.stream().filter(n -> n.getNpcId() == id).collect(Collectors.toList());
	}
	
	public static NpcWalkerRoutesData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final NpcWalkerRoutesData INSTANCE = new NpcWalkerRoutesData();
	}
}
