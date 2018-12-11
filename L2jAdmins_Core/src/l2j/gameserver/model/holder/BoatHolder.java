package l2j.gameserver.model.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.BoatData.BoatCycleType;
import l2j.gameserver.data.BoatData.BoatMessageType;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.items.Item;

/**
 * Info load in boat.xml
 * @author fissban
 */
public class BoatHolder
{
	// Cycle in which to start
	private BoatCycleType cycleStart;
	// Directions to where you travel.
	private List<BoatCycleType> cycles = new ArrayList<>();
	// Listing the routes that the boat makes.
	private final Map<BoatCycleType, List<BoatPatchHolder>> rout = new LinkedHashMap<>();
	// Messages that come out every certain period.
	private final Map<BoatCycleType, Map<BoatMessageType, Integer>> message = new HashMap<>();
	
	private final Map<BoatCycleType, Integer> ticket = new HashMap<>();
	private final Map<BoatCycleType, LocationHolder> oustPlayers = new HashMap<>();
	
	public BoatHolder(BoatCycleType type1, BoatCycleType type2)
	{
		cycles = Arrays.asList(type1, type2);
		// init routs
		rout.put(type1, new ArrayList<>());
		rout.put(type2, new ArrayList<>());
		// init messages
		message.put(type1, new HashMap<>());
		message.put(type2, new HashMap<>());
	}
	
	public void setCycleStart(BoatCycleType start)
	{
		cycleStart = start;
	}
	
	public BoatCycleType getCycleStart()
	{
		return cycleStart;
	}
	
	public List<BoatCycleType> getBoatCycle()
	{
		return cycles;
	}
	
	public List<BoatPatchHolder> getRout(BoatCycleType type)
	{
		return rout.get(type);
	}
	
	public void addRout(BoatCycleType type, BoatPatchHolder rout)
	{
		this.rout.get(type).add(rout);
	}
	
	public void addMessage(BoatCycleType type, BoatMessageType messageType, int message)
	{
		this.message.get(type).put(messageType, message);
	}
	
	public int getMessage(BoatCycleType type, BoatMessageType messageType)
	{
		return message.get(type).get(messageType);
	}
	
	public LocationHolder getOustPlayers(BoatCycleType type)
	{
		return oustPlayers.get(type);
	}
	
	public void addOustPlayers(BoatCycleType type, LocationHolder oustPlayers)
	{
		this.oustPlayers.put(type, oustPlayers);
	}
	
	public Item getTicket(BoatCycleType type)
	{
		return ItemData.getInstance().getTemplate(ticket.get(type));
	}
	
	public void addTicket(BoatCycleType type, int ticket)
	{
		this.ticket.put(type, ticket);
	}
}
