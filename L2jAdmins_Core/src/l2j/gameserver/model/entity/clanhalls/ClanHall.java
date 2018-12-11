package l2j.gameserver.model.entity.clanhalls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.AuctionData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.clanhalls.auction.Auction;
import l2j.gameserver.model.entity.clanhalls.task.ClanHallRentTask;
import l2j.gameserver.model.entity.clanhalls.type.ClanHallFunctionType;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.zone.type.ClanHallZone;
import l2j.gameserver.network.external.server.PledgeShowInfoUpdate;

public class ClanHall
{
	protected static final Logger LOG = Logger.getLogger(ClanHall.class.getName());
	
	public static final int CH_RATE = 604800000;
	
	private int clanHallId = 0;
	private List<L2DoorInstance> doors = new ArrayList<>();
	private String name = "";
	private int ownerId = 0;
	private int lease = 0;
	private String desc = "";
	private String location = "";
	long paidUntil;
	
	private int grade;
	
	private ClanHallZone zone;
	
	private final Map<ClanHallFunctionType, ClanHallFunction> functions = new HashMap<>();
	protected boolean paid;
	
	public ClanHall(int clanHallId, String name, int ownerId, int lease, String desc, String location, long paidUntil, int grade, boolean paid)
	{
		this.clanHallId = clanHallId;
		this.name = name;
		this.ownerId = ownerId;
		this.lease = lease;
		this.desc = desc;
		this.location = location;
		this.grade = grade;
		this.paidUntil = paidUntil;
		this.paid = paid;
		
		loadFunctions();
		
		if (ClanData.getInstance().getClanById(ownerId) != null)
		{
			// the clan hall clan in question is assigned.
			ClanData.getInstance().getClanById(ownerId).setClanHallId(clanHallId);
			startRentTask(false);
		}
	}
	
	public final int getId()
	{
		return clanHallId;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public final int getOwnerId()
	{
		return ownerId;
	}
	
	public final int getLease()
	{
		return lease;
	}
	
	public final String getDesc()
	{
		return desc;
	}
	
	public final String getLocation()
	{
		return location;
	}
	
	public final long getPaidUntil()
	{
		return paidUntil;
	}
	
	public void setPaidUntil(long paid)
	{
		paidUntil = paid;
	}
	
	public final int getGrade()
	{
		return grade;
	}
	
	public final boolean getPaid()
	{
		return paid;
	}
	
	public void setPaid(boolean paid)
	{
		this.paid = paid;
	}
	
	public void setZone(ClanHallZone zone)
	{
		this.zone = zone;
	}
	
	public ClanHallZone getZone()
	{
		return zone;
	}
	
	public void banishForeigners()
	{
		zone.banishForeigners(getOwnerId());
	}
	
	public ClanHallFunction getFunction(ClanHallFunctionType func)
	{
		if (functions.containsKey(func))
		{
			return functions.get(func);
		}
		return null;
	}
	
	public void setOwner(Clan clan)
	{
		// Remove old owner
		if ((getOwnerId() > 0) && ((clan == null) || (clan.getId() != getOwnerId())))
		{
			// Try to find clan instance
			Clan oldOwner = ClanData.getInstance().getClanById(getOwnerId());
			if (oldOwner != null)
			{
				oldOwner.setClanHallId(0); // Unset hasClanHallflag for old owner
			}
		}
		
		updateOwnership(clan); // Update in database
	}
	
	/**
	 * Open or close the doors of the Clan Hall<br>
	 * <u> Actions: </u><br>
	 * <li>true: Open Doors
	 * <li>false: Close Doors
	 * @param open
	 */
	public void openCloseDoors(boolean open)
	{
		for (L2DoorInstance door : doors)
		{
			if (door != null)
			{
				if (open)
				{
					door.openMe();
				}
				else
				{
					door.closeMe();
				}
			}
		}
	}
	
	public final L2DoorInstance getDoor(int doorId)
	{
		if (doorId <= 0)
		{
			return null;
		}
		
		for (L2DoorInstance door : doors)
		{
			if (door.getId() == doorId)
			{
				return door;
			}
		}
		return null;
	}
	
	public final List<L2DoorInstance> getDoors()
	{
		return doors;
	}
	
	private void startRentTask(boolean forced)
	{
		long currentTime = System.currentTimeMillis();
		if (paidUntil > currentTime)
		{
			ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(this), paidUntil - currentTime);
		}
		else if (!paid && !forced)
		{
			if ((System.currentTimeMillis() + (86400000)) <= (paidUntil + CH_RATE))
			{
				ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(this), (86400000));
			}
			else
			{
				ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(this), (paidUntil + CH_RATE) - System.currentTimeMillis());
			}
		}
		else
		{
			ThreadPoolManager.getInstance().schedule(new ClanHallRentTask(this), 0);
		}
	}
	
	private void updateOwnership(Clan clan)
	{
		if (clan != null)
		{
			// Update owner id property
			ownerId = clan.getId();
			
			// Announce to clan members
			clan.setClanHallId(getId()); // Set has hideout flag for new owner
			clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
			
			paidUntil = System.currentTimeMillis();
			
			// start rent task
			startRentTask(true);
		}
		else
		{
			// Removals
			paidUntil = 0;
			ownerId = 0;
			paid = false;
			
			// Reset functions
			for (Entry<ClanHallFunctionType, ClanHallFunction> fc : functions.entrySet())
			{
				removeFunction(fc.getKey());
			}
			functions.clear();
			
			if (AuctionData.getInstance().initNPC(getId()))
			{
				AuctionData.getInstance().getAuctions().add(new Auction(getId()));
			}
		}
		
		updateDb();
	}
	
	public void updateDb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE clanhall SET ownerId=?, paidUntil=?, paid=? WHERE id=?"))
		{
			ps.setInt(1, ownerId);
			ps.setLong(2, paidUntil);
			ps.setInt(3, (paid) ? 1 : 0);
			ps.setInt(4, clanHallId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception: updateDb: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void updateFunctionRent(ClanHallFunctionType chType, long endTime)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE clanhall_functions SET endTime=? WHERE type=? AND hall_id=?"))
		{
			ps.setLong(1, endTime);
			ps.setInt(2, chType.ordinal());
			ps.setInt(3, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception ClanHall.ClanHallFunction.updateFunctionRent(int functionType): " + e.getMessage());
		}
	}
	
	private void loadFunctions()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM clanhall_functions WHERE hall_id = ?"))
		{
			ps.setInt(1, getId());
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					ClanHallFunctionType funcType = ClanHallFunctionType.values()[rs.getInt("type")];
					
					functions.put(funcType, new ClanHallFunction(this, funcType, rs.getInt("lvl"), rs.getInt("lease"), rs.getLong("rate"), rs.getLong("endTime")));
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Exception: ClanHall.loadFunctions(): " + e.getMessage(), e);
		}
	}
	
	public void removeFunction(ClanHallFunctionType functionType)
	{
		ClanHallFunction function = functions.remove(functionType);
		if ((function != null) && (function.getFunctionTask() != null))
		{
			function.cancelFunctionTask();
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM clanhall_functions WHERE hall_id=? AND type=?"))
		{
			ps.setInt(1, getId());
			ps.setInt(2, functionType.ordinal());
			ps.execute();
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception: ClanHall.removeFunction(int functionType): " + e.getMessage());
		}
	}
	
	public boolean updateFunctions(ClanHallFunctionType type, int lvl, int lease, long rate, long time, boolean addNew)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if (addNew)
			{
				if ((ClanData.getInstance().getClanById(getOwnerId()) != null) && (ClanData.getInstance().getClanById(getOwnerId()).getWarehouse().getAdena() >= lease))
				{
					ClanData.getInstance().getClanById(getOwnerId()).getWarehouse().destroyItemByItemId("CH_function_fee", Inventory.ADENA_ID, lease, null, null);
				}
				else
				{
					return false;
				}
				
				try (PreparedStatement statement = con.prepareStatement("INSERT INTO clanhall_functions (hall_id, type, lvl, lease, rate, endTime) VALUES (?,?,?,?,?,?)"))
				{
					statement.setInt(1, getId());
					statement.setInt(2, type.ordinal());
					statement.setInt(3, lvl);
					statement.setInt(4, lease);
					statement.setLong(5, rate);
					statement.setLong(6, time);
					statement.execute();
				}
				
				functions.put(type, new ClanHallFunction(this, type, lvl, lease, rate, time));
				
				if (Config.DEBUG)
				{
					LOG.warning("INSERT INTO clanhall_functions (hall_id, type, lvl, lease, rate, endTime) VALUES (?,?,?,?,?,?)");
				}
			}
			else if (getFunction(type) != null)
			{
				if ((lvl == 0) && (lease == 0))
				{
					removeFunction(type);
					return true;
				}
				
				ClanHallFunction function = getFunction(type);
				
				if (ClanData.getInstance().getClanById(getOwnerId()).getWarehouse().getAdena() >= (lease - function.getLease()))
				{
					if ((lease - function.getLease()) > 0)
					{
						ClanData.getInstance().getClanById(getOwnerId()).getWarehouse().destroyItemByItemId("CH_function_fee", Inventory.ADENA_ID, lease - function.getLease(), null, null);
					}
				}
				else
				{
					return false;
				}
				
				try (PreparedStatement statement = con.prepareStatement("UPDATE clanhall_functions SET lvl=?, lease=? WHERE hall_id=? AND type=?"))
				{
					statement.setInt(1, lvl);
					statement.setInt(2, lease);
					statement.setInt(3, getId());
					statement.setInt(4, type.ordinal());
					statement.execute();
				}
				
				function.setLvl(lvl);
				function.setLease(lease);
				
				if (Config.DEBUG)
				{
					LOG.warning("UPDATE clanhall_functions WHERE hall_id=? AND id=? SET lvl, lease");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning(getClass().getSimpleName() + ": Exception ClanHall.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + e.getMessage());
		}
		return true;
	}
}
