package l2j.gameserver.model.olympiad;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2j.Config;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author DS
 */
public class OlympiadManager
{
	private final List<Integer> nonClassBasedRegisters;
	private final Map<Integer, List<Integer>> classBasedRegisters;
	
	protected OlympiadManager()
	{
		nonClassBasedRegisters = new CopyOnWriteArrayList<>();
		classBasedRegisters = new ConcurrentHashMap<>();
	}
	
	public static final OlympiadManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public final List<Integer> getRegisteredNonClassBased()
	{
		return nonClassBasedRegisters;
	}
	
	public final Map<Integer, List<Integer>> getRegisteredClassBased()
	{
		return classBasedRegisters;
	}
	
	protected final List<List<Integer>> hasEnoughRegisteredClassed()
	{
		List<List<Integer>> result = null;
		for (Map.Entry<Integer, List<Integer>> classList : classBasedRegisters.entrySet())
		{
			if ((classList.getValue() != null) && (classList.getValue().size() >= Config.ALT_OLY_CLASSED))
			{
				if (result == null)
				{
					result = new ArrayList<>();
				}
				
				result.add(classList.getValue());
			}
		}
		return result;
	}
	
	protected final boolean hasEnoughRegisteredNonClassed()
	{
		return nonClassBasedRegisters.size() >= Config.ALT_OLY_NONCLASSED;
	}
	
	protected final void clearRegistered()
	{
		nonClassBasedRegisters.clear();
		classBasedRegisters.clear();
	}
	
	public final boolean isRegistered(L2PcInstance noble)
	{
		return isRegistered(noble, false);
	}
	
	private final boolean isRegistered(L2PcInstance player, boolean showMessage)
	{
		final Integer objId = Integer.valueOf(player.getObjectId());
		
		if (nonClassBasedRegisters.contains(objId))
		{
			if (showMessage)
			{
				player.sendPacket(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST);
			}
			
			return true;
		}
		
		final List<Integer> classed = classBasedRegisters.get(player.getBaseClass());
		if ((classed != null) && classed.contains(objId))
		{
			if (showMessage)
			{
				player.sendPacket(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST);
			}
			
			return true;
		}
		
		return false;
	}
	
	public final boolean isRegisteredInComp(L2PcInstance noble)
	{
		return isRegistered(noble, false) || isInCompetition(noble, false);
	}
	
	private final static boolean isInCompetition(L2PcInstance player, boolean showMessage)
	{
		if (!Olympiad.inCompPeriod)
		{
			return false;
		}
		
		for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0;)
		{
			AbstractOlympiadGame game = OlympiadGameManager.getInstance().getOlympiadTask(i).getGame();
			if (game == null)
			{
				continue;
			}
			
			if (game.containsParticipant(player.getObjectId()))
			{
				if (showMessage)
				{
					player.sendPacket(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST);
				}
				
				return true;
			}
		}
		return false;
	}
	
	public final boolean registerNoble(L2PcInstance player, CompetitionType type)
	{
		if (!Olympiad.inCompPeriod)
		{
			player.sendPacket(SystemMessage.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if (Olympiad.getInstance().getMillisToCompEnd() < 600000)
		{
			player.sendMessage("The request to participate in the game cannot be made starting from 10 minutes before the end of the game");
			// player.sendPacket(SystemMessage.GAME_REQUEST_CANNOT_BE_MADE);
			return false;
		}
		
		switch (type)
		{
			case CLASSED:
			{
				if (!checkNoble(player))
				{
					return false;
				}
				
				List<Integer> classed = classBasedRegisters.get(player.getBaseClass());
				if (classed != null)
				{
					classed.add(player.getObjectId());
				}
				else
				{
					classed = new CopyOnWriteArrayList<>();
					classed.add(player.getObjectId());
					classBasedRegisters.put(player.getBaseClass(), classed);
				}
				
				player.sendPacket(SystemMessage.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES);
				break;
			}
			
			case NON_CLASSED:
			{
				if (!checkNoble(player))
				{
					return false;
				}
				
				nonClassBasedRegisters.add(player.getObjectId());
				player.sendPacket(SystemMessage.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES);
				break;
			}
			
		}
		
		return true;
	}
	
	public final boolean unRegisterNoble(L2PcInstance noble)
	{
		if (!Olympiad.inCompPeriod)
		{
			noble.sendPacket(SystemMessage.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if (!noble.isNoble())
		{
			noble.sendPacket(SystemMessage.NOBLESSE_ONLY);
			return false;
		}
		
		if (!isRegistered(noble, false))
		{
			noble.sendPacket(SystemMessage.YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME);
			return false;
		}
		
		if (isInCompetition(noble, false))
		{
			return false;
		}
		
		Integer objId = Integer.valueOf(noble.getObjectId());
		if (nonClassBasedRegisters.remove(objId))
		{
			noble.sendPacket(SystemMessage.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
			return true;
		}
		
		final List<Integer> classed = classBasedRegisters.get(noble.getBaseClass());
		if ((classed != null) && classed.remove(objId))
		{
			classBasedRegisters.remove(noble.getBaseClass());
			classBasedRegisters.put(noble.getBaseClass(), classed);
			
			noble.sendPacket(SystemMessage.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
			return true;
		}
		
		return false;
	}
	
	public final void removeDisconnectedCompetitor(L2PcInstance player)
	{
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
		if ((task != null) && task.isGameStarted())
		{
			task.getGame().handleDisconnect(player);
		}
		
		final Integer objId = Integer.valueOf(player.getObjectId());
		if (nonClassBasedRegisters.remove(objId))
		{
			return;
		}
		
		final List<Integer> classed = classBasedRegisters.get(player.getBaseClass());
		if ((classed != null) && classed.remove(objId))
		{
			return;
		}
	}
	
	/**
	 * @param  player - messages will be sent to this L2PcInstance
	 * @return        true if all requirements are met
	 */
	private final boolean checkNoble(L2PcInstance player)
	{
		if (!player.isNoble())
		{
			player.sendPacket(SystemMessage.C1_DOES_NOT_MEET_REQUIREMENTS_ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			return false;
		}
		
		if (player.isSubClassActive())
		{
			player.sendPacket(SystemMessage.C1_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_CLASS_CHARACTER);
			return false;
		}
		
		if ((player.getInventoryLimit() * 0.8) <= player.getInventory().getSize())
		{
			player.sendMessage("You can't join a Grand Olympiad Game match with that much stuff on you! Reduce your weight to below 80 percent full and request to join again!");
			return false;
		}
		
		if (isRegistered(player, true))
		{
			return false;
		}
		
		if (isInCompetition(player, true))
		{
			return false;
		}
		
		StatsSet statDat = Olympiad.getNobleStats(player.getObjectId());
		if (statDat == null)
		{
			statDat = new StatsSet();
			statDat.set(Olympiad.CLASS_ID, player.getBaseClass());
			statDat.set(Olympiad.CHAR_NAME, player.getName());
			statDat.set(Olympiad.POINTS, Olympiad.DEFAULT_POINTS);
			statDat.set(Olympiad.COMP_DONE, 0);
			statDat.set(Olympiad.COMP_WON, 0);
			statDat.set(Olympiad.COMP_LOST, 0);
			statDat.set(Olympiad.COMP_DRAWN, 0);
			statDat.set("to_save", true);
			
			Olympiad.addNobleStats(player.getObjectId(), statDat);
		}
		
		final int points = Olympiad.getInstance().getNoblePoints(player.getObjectId());
		if (points <= 0)
		{
			NpcHtmlMessage message = new NpcHtmlMessage(0);
			message.setFile("data/html/olympiad/noble_nopoints1.htm");
			player.sendPacket(message);
			return false;
		}
		
		return true;
	}
	
	private static class SingletonHolder
	{
		protected static final OlympiadManager INSTANCE = new OlympiadManager();
	}
}
