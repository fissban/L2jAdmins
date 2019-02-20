package l2j.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.BoatData.BoatCycleType;
import l2j.gameserver.data.BoatData.BoatMessageType;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.L2BoatAI;
import l2j.gameserver.model.actor.knownlist.BoatKnownList;
import l2j.gameserver.model.actor.stat.BoatStat;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.holder.BoatHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.VehicleDeparture;
import l2j.gameserver.network.external.server.VehicleInfo;
import l2j.gameserver.network.external.server.VehicleStarted;
import l2j.gameserver.task.continuous.MovementTaskManager;
import l2j.gameserver.util.Broadcast;
import l2j.gameserver.util.Util;

/**
 * @author fissban
 */
public class L2BoatInstance extends L2Npc
{
	// General information about the boat.
	private BoatHolder boatInfo = null;
	// Point of the route that is plotting the boat.
	private int boatPatch = 0;
	// Cycle of the boat.
	private BoatCycleType boatCycle = null;
	// Announcements once the boat is in the port.
	private int sayState = 10;
	// Passengers on board
	private final List<L2PcInstance> passengers = new ArrayList<>();
	
	public L2BoatInstance(int objectId, NpcTemplate template, BoatHolder info)
	{
		super(objectId, template);
		
		setAI(new L2BoatAI(this));
		
		boatInfo = info;
		boatCycle = info.getCycleStart();
		
		ThreadPoolManager.schedule(() -> boatCaptain(), 10);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new BoatKnownList(this));
	}
	
	@Override
	public BoatKnownList getKnownList()
	{
		return (BoatKnownList) super.getKnownList();
	}
	
	@Override
	public void initStat()
	{
		setStat(new BoatStat(this));
	}
	
	@Override
	public final BoatStat getStat()
	{
		return (BoatStat) super.getStat();
	}
	
	@Override
	public boolean moveToNextRoutePoint()
	{
		moveNextPoint();
		return true;
	}
	
	@Override
	public int getLevel()
	{
		return 0;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	@Override
	public boolean updatePosition()
	{
		// update position all passengers
		for (var p : passengers)
		{
			if ((p != null) && (p.getBoatId() == getObjectId()))
			{
				p.setXYZ(getX(), getY(), getZ());
				p.revalidateZone(false);
			}
		}
		
		return super.updatePosition();
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		activeChar.sendPacket(new VehicleInfo(this));
		if (isMoving())
		{
			activeChar.sendPacket(new VehicleStarted(getObjectId(), 1));
		}
	}
	
	/**
	 * Start of the trip
	 * <li>All players are searched within a 1000 yard radius and are added as passengers.</li>
	 * <li>The boat route begins.</li>
	 */
	public void begin()
	{
		passengers.clear();
		
		for (var player : getKnownList().getObjectTypeInRadius(L2PcInstance.class, 1000))
		{
			if (player == null)
			{
				continue;
			}
			
			if (player.isInBoat() && (player.getBoatId() == getObjectId()))
			{
				addPassenger(player);
			}
		}
		
		moveNextPoint();
	}
	
	/**
	 * We added a player as a passenger of the boat<br>
	 * <li>It is checked that the player has the ticket corresponding to the trip,</li><br1>
	 * <li>If you do not have it send it to a safe place inside the port to see inconveniences.</li><br1>
	 * @param player
	 */
	private void addPassenger(L2PcInstance player)
	{
		if ((player == null) || passengers.contains(player))
		{
			return;
		}
		
		var item = boatInfo.getTicket(boatCycle);
		
		if (item != null)
		{
			final ItemInstance it = player.getInventory().getItemById(item.getId());
			if ((it != null) && (it.getCount() >= 1))
			{
				player.getInventory().destroyItem("Boat", it, 1, player, this);
				passengers.add(player);
			}
			else
			{
				oustPlayer(player);
				player.sendPacket(SystemMessage.NOT_CORRECT_BOAT_TICKET);
			}
		}
	}
	
	/**
	 * A character is removed from our passenger list.
	 * @param player
	 */
	public void removePassenger(L2PcInstance player)
	{
		try
		{
			passengers.remove(player);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * The boat cycle is changed to the next.
	 */
	public void changeBoatCycle()
	{
		if (boatInfo.getBoatCycle().size() >= 1)
		{
			return;
		}
		
		boatPatch = 0; // seguro que volvemos a la posicion 0 ??
		
		for (var cycle : boatInfo.getBoatCycle())
		{
			var nextValue = false;
			
			if (nextValue)
			{
				boatCycle = cycle;
				return;
			}
			
			if (cycle.equals(boatCycle))
			{
				nextValue = true;
			}
		}
		
		// If we get to this point, the state of the boat is initialized.
		boatCycle = boatInfo.getBoatCycle().get(0);
	}
	
	/**
	 * The character is sent to a safe position on the boat and is removed from the passenger list.
	 * @param player
	 */
	public void oustPlayer(L2PcInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		var x = boatInfo.getOustPlayers(boatCycle).getX();
		var y = boatInfo.getOustPlayers(boatCycle).getY();
		var z = boatInfo.getOustPlayers(boatCycle).getZ();
		
		if (player.isOnline())
		{
			player.teleToLocation(x, y, z);
		}
		else
		{
			player.setXYZInvisible(x, y, z); // disconnects handling
		}
		
		// It is removed as a passenger and the boat assigned to the player is canceled.
		player.setBoat(null);
	}
	
	/**
	 * The next move of the boat is sought.<br>
	 * If you have reached the last, change the cycle of the same. <br>
	 */
	public void moveNextPoint()
	{
		if (moveNextPoint(boatCycle, boatPatch, this))
		{
			boatPatch++;
		}
		else
		{
			changeBoatCycle();
			ThreadPoolManager.schedule(() -> boatCaptain(), 10);
		}
	}
	
	/**
	 * @param  type
	 * @param  boatPatch
	 * @param  boat
	 * @return
	 */
	public boolean moveNextPoint(BoatCycleType type, int boatPatch, L2BoatInstance boat)
	{
		var rout = boatInfo.getRout(type);
		
		if (boatPatch >= rout.size())
		{
			return false;
		}
		
		var loc = rout.get(boatPatch);
		
		boat.getStat().setMoveSpeed(loc.getMovementSpeed());
		boat.getStat().setRotationSpeed(loc.getRotacionSpeed());
		
		final MoveData m = new MoveData();
		m.disregardingGeodata = false;
		m.onGeodataPathIndex = -1;
		m.xDestination = loc.getX();
		m.yDestination = loc.getY();
		m.zDestination = loc.getZ();
		m.heading = 0;
		m.moveStartTime = System.currentTimeMillis();
		boat.move = m;
		
		var dx = loc.getX() - boat.getX();
		var dy = loc.getY() - boat.getY();
		var distance = Math.sqrt((dx * dx) + (dy * dy));
		if (distance > 1)
		{
			boat.setHeading(Util.calculateHeadingFrom(boat.getX(), boat.getY(), loc.getX(), loc.getY()));
		}
		
		boat.broadcastPacket(new VehicleDeparture(boat));
		
		MovementTaskManager.getInstance().add(boat);
		return true;
	}
	
	public void say(BoatMessageType message)
	{
		Broadcast.toKnownPlayers(this, new SystemMessage(boatInfo.getMessage(boatCycle, message)));
	}
	
	public static void sound(L2Npc boat)
	{
		Broadcast.toKnownPlayers(boat, new PlaySound(PlaySoundType.SHIP_ARRIVAL_DEPARTURE, boat.getX(), boat.getY(), boat.getZ()));
	}
	
	// XXX TASK --------------------------------------------------------------------------------------------
	
	/**
	 * Ads are generated by being on the dock and the journey begins.
	 */
	public void boatCaptain()
	{
		switch (sayState)
		{
			case 10:
				say(BoatMessageType.MESSAGE_10);
				ThreadPoolManager.schedule(() -> boatCaptain(), 30 * 1000);// 600000
				sayState = 5;
				break;
			case 5:
				say(BoatMessageType.MESSAGE_5);
				ThreadPoolManager.schedule(() -> boatCaptain(), 30 * 1000);// 300000
				sayState = 1;
				break;
			case 1:
				say(BoatMessageType.MESSAGE_1);
				ThreadPoolManager.schedule(() -> boatCaptain(), 30 * 1000);// 40000
				sayState = 0;
				break;
			case 0:
				say(BoatMessageType.MESSAGE_0);
				ThreadPoolManager.schedule(() -> boatCaptain(), 30 * 1000);// 20000
				sayState = -1;
				break;
			case -1:
				begin();
				say(BoatMessageType.MESSAGE_BEGIN);
				sayState = 10;// init state
				break;
		}
	}
}
