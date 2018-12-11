package main.engine;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import main.EngineModsManager;
import main.data.ModsData;
import main.enums.EngineStateType;
import main.holders.DataValueHolder;
import main.holders.ModTimerHolder;
import main.holders.objects.CharacterHolder;
import main.holders.objects.ItemHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.instances.NpcDropsInstance;
import main.instances.NpcExpInstance;
import main.util.builders.html.HtmlBuilder;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.model.zone.Zone;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.ShowBoard;

/**
 * @author fissban
 */
public abstract class AbstractMod
{
	public static final Logger LOG = Logger.getLogger(AbstractMod.class.getName());
	
	/** State */
	protected EngineStateType state = EngineStateType.END;
	/** Timers */
	private final Map<Integer, List<ModTimerHolder>> eventTimers = new ConcurrentHashMap<>();
	
	/**
	 * Constructor
	 */
	public AbstractMod()
	{
		//
	}
	
	/**
	 * Event State<br>
	 * <li>{@link EngineStateType#START}</li>
	 * <li>{@link EngineStateType#END}</li>
	 * @return
	 */
	public EngineStateType getState()
	{
		return state;
	}
	
	/**
	 * Here the specific actions that will have an event during actions will be defined:<br>
	 * {@link EngineStateType#START}<br>
	 * {@link EngineStateType#END}<br>
	 */
	public abstract void onModState();
	
	// XXX DB DATA ----------------------------------------------------------------------------------------------- //
	
	/**
	 * You get the value of a player in a certain event
	 * @param objectId
	 * @param event
	 * @return
	 */
	public DataValueHolder getValueDB(int objectId, String event)
	{
		return ModsData.get(objectId, event, this);
	}
	
	/**
	 * You get the value of a player in a certain event
	 * @param ph
	 * @param event
	 * @return
	 */
	public DataValueHolder getValueDB(PlayerHolder ph, String event)
	{
		return ModsData.get(ph.getObjectId(), event, this);
	}
	
	/**
	 * You get the value of a player in a certain event
	 * @param ph
	 * @param event
	 * @return
	 */
	public Map<String, DataValueHolder> getCollectionValuesDB(PlayerHolder ph, String event)
	{
		return ModsData.getCollection(ph.getObjectId(), event, this);
	}
	
	/**
	 * You get the value of a player in a certain event
	 * @param objId
	 * @param event
	 * @return
	 */
	public Map<String, DataValueHolder> getCollectionValuesDB(int objId, String event)
	{
		return ModsData.getCollection(objId, event, this);
	}
	
	/**
	 * Information about an event and its value for a particular player is stored
	 * @param player
	 * @param event
	 * @param value
	 */
	public void setValueDB(PlayerHolder ph, String event, String value)
	{
		ModsData.set(ph.getObjectId(), event, value, this);
	}
	
	/**
	 * Information about an event and its value for a particular player is stored
	 * @param objectId
	 * @param event
	 * @param value
	 */
	public void setValueDB(int objectId, String event, String value)
	{
		ModsData.set(objectId, event, value, this);
	}
	
	/**
	 * All events and values are removed from a mod.
	 */
	public void clearValueDB()
	{
		ModsData.remove(this);
	}
	
	/**
	 * It removes a certain value from a character.
	 * @param objectId
	 * @param event
	 */
	public void removeValueDB(int objectId, String event)
	{
		ModsData.remove(objectId, event, this);
	}
	
	/**
	 * It removes a certain value from a character.
	 * @param objectId
	 * @param event
	 */
	public void removeValueDB(PlayerHolder ph, String event)
	{
		ModsData.remove(ph.getObjectId(), event, this);
	}
	
	// XXX TIMERS ------------------------------------------------------------------------------------------------ //
	
	/**
	 * Add a timer to the mod, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name name of the timer (also passed back as "event" in onTimer)
	 * @param time time in ms for when to fire the timer
	 * @param npc npc associated with this timer (can be null)
	 * @param ph player associated with this timer (can be null)
	 * @param repeating indicates if the timer is repeatable or one-time.
	 */
	public void startTimer(String name, long time, NpcHolder npc, PlayerHolder ph, boolean repeating)
	{
		// Get mod timers for this timer type.
		var timers = eventTimers.get(name.hashCode());
		if (timers == null)
		{
			// None timer exists, create new list.
			timers = new CopyOnWriteArrayList<>();
			// Add new timer to the list.
			timers.add(new ModTimerHolder(this, name, npc, ph, time, repeating));
			// Add timer list to the map.
			eventTimers.put(name.hashCode(), timers);
		}
		else
		{
			// Check, if specific timer already exists.
			for (var timer : timers)
			{
				// If so, return.
				if (timer != null && timer.equals(name, npc, ph))
				{
					return;
				}
			}
			// Add new timer to the list.
			timers.add(new ModTimerHolder(this, name, npc, ph, time, repeating));
		}
	}
	
	public ModTimerHolder getTimer(String name)
	{
		return getTimer(name, null, null);
	}
	
	public ModTimerHolder getTimer(String name, PlayerHolder player)
	{
		return getTimer(name, null, player);
	}
	
	public ModTimerHolder getTimer(String name, NpcHolder npc, PlayerHolder player)
	{
		// Get mod timers for this timer type.
		var timers = eventTimers.get(name.hashCode());
		
		// Timer list does not exists or is empty, return.
		if (timers == null || timers.isEmpty())
		{
			return null;
		}
		
		return timers.stream().filter(timer -> timer != null && timer.equals(name, npc, player)).findAny().orElse(null);
	}
	
	public void cancelTimer(String name, NpcHolder npc, PlayerHolder ph)
	{
		// If specified timer exists, cancel him.
		var timer = getTimer(name, npc, ph);
		
		if (timer != null)
		{
			timer.cancel();
		}
	}
	
	public void cancelTimers(String name)
	{
		// Get mod timers for this timer type.
		var timers = eventTimers.get(name.hashCode());
		
		// Timer list does not exists or is empty, return.
		if (timers == null || timers.isEmpty())
		{
			return;
		}
		
		// Cancel all mod timers.
		timers.stream().filter(t -> t != null).forEach(t -> t.cancel());
	}
	
	/**
	 * Removes modTimer from timer list, when it terminates.
	 * @param timer : modTimer, which is being terminated.
	 */
	public void removeTimer(ModTimerHolder timer)
	{
		// Timer does not exist, return.
		if (timer == null)
		{
			return;
		}
		
		// Get mod timers for this timer type.
		var timers = eventTimers.get(timer.getName().hashCode());
		
		// Timer list does not exists or is empty, return.
		if (timers == null || timers.isEmpty())
		{
			return;
		}
		
		// Remove timer from the list.
		timers.remove(timer);
	}
	
	// XXX MISC -------------------------------------------------------------------------------------------------- //
	
	/**
	 * @return Event is starting
	 */
	public boolean isStarting()
	{
		if (state == EngineStateType.START)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * An html is sent to a character
	 * @param npc
	 * @param content
	 * @param ph
	 */
	public static final void sendHtml(NpcHolder npc, PlayerHolder ph, HtmlBuilder... content)
	{
		var send = "";
		
		for (var hb : content)
		{
			send += hb.toString();
		}
		var html = new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId());
		html.setHtml(send);
		ph.getInstance().sendPacket(html);
		
	}
	
	/**
	 * An html is sent to a character
	 * @param npc
	 * @param content
	 * @param players
	 */
	public static final void sendHtml(L2Npc npc, HtmlBuilder content, List<PlayerHolder> players)
	{
		var html = new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId());
		html.setHtml(content.toString());
		
		players.forEach(ph -> ph.getInstance().sendPacket(html));
	}
	
	/**
	 * An html is sent to a character
	 * @param npc
	 * @param content
	 * @param ph
	 */
	public static final void sendHtml(NpcHolder npc, HtmlBuilder content, PlayerHolder ph)
	{
		var html = new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId());
		html.setHtml(content.toString());
		ph.getInstance().sendPacket(html);
	}
	
	/**
	 * An html is sent to a character
	 * @param ph
	 * @param npc
	 * @param htmlFile -> setFile
	 */
	public static final void sendHtmlFile(PlayerHolder ph, L2Npc npc, String htmlFile)
	{
		var html = new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId());
		html.setFile(htmlFile);
		ph.getInstance().sendPacket(html);
	}
	
	/**
	 * An html is sent to a character on the Community Board
	 * @param ph
	 * @param html
	 */
	public static void sendCommunity(PlayerHolder ph, String html)
	{
		if (html == null || ph == null)
		{
			return;
		}
		
		if (html.length() < 8180)
		{
			ph.getInstance().sendPacket(new ShowBoard(html, "101"));
			ph.getInstance().sendPacket(new ShowBoard(null, "102"));
			ph.getInstance().sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < 16360)
		{
			ph.getInstance().sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			ph.getInstance().sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
			ph.getInstance().sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < 24540)
		{
			ph.getInstance().sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			ph.getInstance().sendPacket(new ShowBoard(html.substring(8180, 16360), "102"));
			ph.getInstance().sendPacket(new ShowBoard(html.substring(16360, html.length()), "103"));
		}
		else
		{
			System.out.println("community html muy largo-> " + (html.length() - 12270));
		}
	}
	
	// REGISTER START & END - EVENT -------------------------------------------------------------------------- //
	
	public void loadValuesFromDb()
	{
		ModsData.loadValuesFromMod(this);
	}
	
	/**
	 * Register mod<br>
	 */
	public void registerMod(boolean config)
	{
		EngineModsManager.registerMod(this);
		
		if (config)
		{
			startMod();
		}
	}
	
	public void endMod()
	{
		// It fits the event status
		state = EngineStateType.END;
		// Action for new state
		onModState();
	}
	
	public void startMod()
	{
		// It fits the event status
		state = EngineStateType.START;
		// Action for new state
		onModState();
	}
	
	public void cancelScheduledState()
	{
		//
	}
	
	// XXX LISTENERS --------------------------------------------------------------------------------------------- //
	
	public boolean onUseSkill(PlayerHolder ph, Skill skill)
	{
		return true;
	}
	
	public boolean onUseItem(PlayerHolder ph, ItemHolder item)
	{
		return true;
	}
	
	public void onSellItems(PlayerHolder ph, ItemHolder item)
	{
		//
	}
	
	public boolean onCommunityBoard(PlayerHolder ph, String command)
	{
		return false;
	}
	
	public void onShutDown()
	{
		//
	}
	
	public boolean onExitWorld(PlayerHolder ph)
	{
		return false;
	}
	
	public void onNpcExpSp(PlayerHolder phKiller, NpcHolder npc, NpcExpInstance instance)
	{
		//
	}
	
	/**
	 * @param killer
	 * @param npc
	 * @return
	 */
	public void onNpcDrop(PlayerHolder killer, NpcHolder npc, NpcDropsInstance instance)
	{
		//
	}
	
	public void onEnterZone(CharacterHolder player, Zone zone)
	{
		//
	}
	
	public void onExitZone(CharacterHolder player, Zone zone)
	{
		//
	}
	
	public void onCreateCharacter(PlayerHolder ph)
	{
		//
	}
	
	public boolean onChat(PlayerHolder ph, String chat)
	{
		return false;
	}
	
	public boolean onAdminCommand(PlayerHolder ph, String chat)
	{
		return false;
	}
	
	public boolean onVoicedCommand(PlayerHolder ph, String chat)
	{
		return false;
	}
	
	public boolean onInteract(PlayerHolder ph, CharacterHolder character)
	{
		return false;
	}
	
	public void onEvent(PlayerHolder ph, CharacterHolder character, String event)
	{
		//
	}
	
	public void onTimer(String timerName, NpcHolder npc, PlayerHolder ph)
	{
		//
	}
	
	public void onSpawn(NpcHolder npc)
	{
		//
	}
	
	public void onEnterWorld(PlayerHolder ph)
	{
		//
	}
	
	public boolean onAttack(CharacterHolder killer, CharacterHolder victim)
	{
		return true;
	}
	
	/**
	 * Mainly used in events such as TvT, DM, etc etc
	 * @param killer
	 * @param victim
	 * @return
	 */
	public boolean canAttack(CharacterHolder killer, CharacterHolder victim)
	{
		return false;
	}
	
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		//
	}
	
	public void onDeath(CharacterHolder player)
	{
		//
	}
	
	public void onEnchant(PlayerHolder player)
	{
		//
	}
	
	public void onEquip(CharacterHolder player)
	{
		//
	}
	
	public void onUnequip(CharacterHolder player)
	{
		//
	}
	
	public boolean onRestoreSkills(PlayerHolder ph)
	{
		return false;
	}
	
	/**
	 * This method multiplies any stat of the characters, so we return "1.0" if we want to realize any increase.<br>
	 * Example: 1.1 -> 10% more stat
	 * @param stat
	 * @param character
	 * @return
	 */
	public double onStats(StatsType stat, CharacterHolder character, double value)
	{
		return value;
	}
	
	/**
	 * @param ph
	 */
	public void onIncreaseLvl(PlayerHolder ph)
	{
		//
	}
	
	public void onCreatedItem(ItemHolder item)
	{
		//
	}
	
	public void onSendData(ByteBuffer data)
	{
		
	}
	
	public void onReceiveData(ByteBuffer data)
	{
		
	}
}
