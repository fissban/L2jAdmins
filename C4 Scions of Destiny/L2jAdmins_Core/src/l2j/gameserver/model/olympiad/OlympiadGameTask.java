package l2j.gameserver.model.olympiad;

import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author DS
 */
public final class OlympiadGameTask implements Runnable
{
	protected static final Logger LOG = Logger.getLogger(OlympiadGameTask.class.getName());
	protected static final long BATTLE_PERIOD = Config.ALT_OLY_BATTLE; // 6 mins
	
	public static final int[] TELEPORT_TO_ARENA =
	{
		120,
		60,
		30,
		15,
		10,
		5,
		4,
		3,
		2,
		1,
		0
	};
	public static final int[] BATTLE_START_TIME =
	{
		60,
		50,
		40,
		30,
		20,
		10,
		5,
		4,
		3,
		2,
		1,
		0
	};
	public static final int[] TELEPORT_TO_TOWN =
	{
		40,
		30,
		20,
		10,
		5,
		4,
		3,
		2,
		1,
		0
	};
	
	private final OlympiadStadiumZone zone;
	private AbstractOlympiadGame game;
	private GameState state = GameState.IDLE;
	private boolean needAnnounce = false;
	private int countDown = 0;
	
	private static enum GameState
	{
		BEGIN,
		TELE_TO_ARENA,
		GAME_STARTED,
		BATTLE_COUNTDOWN,
		BATTLE_STARTED,
		BATTLE_IN_PROGRESS,
		GAME_STOPPED,
		TELE_TO_TOWN,
		CLEANUP,
		IDLE
	}
	
	public OlympiadGameTask(OlympiadStadiumZone zone)
	{
		this.zone = zone;
		this.zone.registerTask(this);
	}
	
	public final boolean isRunning()
	{
		return state != GameState.IDLE;
	}
	
	public final boolean isGameStarted()
	{
		return (state.ordinal() >= GameState.GAME_STARTED.ordinal()) && (state.ordinal() <= GameState.CLEANUP.ordinal());
	}
	
	public final boolean isInTimerTime()
	{
		return state == GameState.BATTLE_COUNTDOWN;
	}
	
	public final boolean isBattleStarted()
	{
		return state == GameState.BATTLE_IN_PROGRESS;
	}
	
	public final boolean isBattleFinished()
	{
		return state == GameState.TELE_TO_TOWN;
	}
	
	public final boolean needAnnounce()
	{
		if (needAnnounce)
		{
			needAnnounce = false;
			return true;
		}
		return false;
	}
	
	public final OlympiadStadiumZone getZone()
	{
		return zone;
	}
	
	public final AbstractOlympiadGame getGame()
	{
		return game;
	}
	
	public final void attachGame(AbstractOlympiadGame game)
	{
		if ((game != null) && (state != GameState.IDLE))
		{
			LOG.log(Level.WARNING, "Attempt to overwrite non-finished game in state " + state);
			return;
		}
		
		this.game = game;
		state = GameState.BEGIN;
		needAnnounce = false;
		ThreadPoolManager.execute(this);
	}
	
	@Override
	public final void run()
	{
		try
		{
			int delay = 1; // schedule next call after 1s
			switch (state)
			{
				// Game created
				case BEGIN:
				{
					state = GameState.TELE_TO_ARENA;
					countDown = Config.ALT_OLY_WAIT_TIME;
					break;
				}
				// Teleport to arena countdown
				case TELE_TO_ARENA:
				{
					game.broadcastPacket(new SystemMessage(SystemMessage.YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S).addNumber(countDown));
					
					delay = getDelay(TELEPORT_TO_ARENA);
					if (countDown <= 0)
					{
						state = GameState.GAME_STARTED;
					}
					break;
				}
				// Game start, port players to arena
				case GAME_STARTED:
				{
					if (!startGame())
					{
						state = GameState.GAME_STOPPED;
						break;
					}
					
					state = GameState.BATTLE_COUNTDOWN;
					countDown = Config.ALT_OLY_WAIT_BATTLE;
					delay = getDelay(BATTLE_START_TIME);
					break;
				}
				// Battle start countdown, first part (60-10)
				case BATTLE_COUNTDOWN:
				{
					zone.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_WILL_START_IN_S1_SECOND_S).addNumber(countDown));
					
					if (countDown == 20)
					{
						game.buffAndHealPlayers();
					}
					
					delay = getDelay(BATTLE_START_TIME);
					if (countDown <= 0)
					{
						state = GameState.BATTLE_STARTED;
					}
					
					break;
				}
				// Beginning of the battle
				case BATTLE_STARTED:
				{
					countDown = 0;
					game.resetDamage();
					state = GameState.BATTLE_IN_PROGRESS; // set state first, used in zone update
					if (!startBattle())
					{
						state = GameState.GAME_STOPPED;
					}
					
					break;
				}
				// Checks during battle
				case BATTLE_IN_PROGRESS:
				{
					countDown += 1000;
					if (checkBattle() || (countDown > Config.ALT_OLY_BATTLE))
					{
						state = GameState.GAME_STOPPED;
					}
					
					break;
				}
				// End of the battle
				case GAME_STOPPED:
				{
					state = GameState.TELE_TO_TOWN;
					countDown = Config.ALT_OLY_WAIT_END;
					stopGame();
					delay = getDelay(TELEPORT_TO_TOWN);
					break;
				}
				// Teleport to town countdown
				case TELE_TO_TOWN:
				{
					game.broadcastPacket(new SystemMessage(SystemMessage.YOU_WILL_BE_MOVED_TO_TOWN_IN_S1_SECONDS).addNumber(countDown));
					
					delay = getDelay(TELEPORT_TO_TOWN);
					if (countDown <= 0)
					{
						state = GameState.CLEANUP;
					}
					
					break;
				}
				// Removals
				case CLEANUP:
				{
					cleanupGame();
					state = GameState.IDLE;
					game = null;
					return;
				}
			}
			ThreadPoolManager.schedule(this, delay * 1000);
		}
		catch (Exception e)
		{
			switch (state)
			{
				case GAME_STOPPED:
				case TELE_TO_TOWN:
				case CLEANUP:
				case IDLE:
				{
					LOG.log(Level.WARNING, "Unable to return players back in town, exception: " + e.getMessage());
					state = GameState.IDLE;
					game = null;
					return;
				}
			}
			
			LOG.log(Level.WARNING, "Exception in " + state + ", trying to port players back: " + e.getMessage(), e);
			state = GameState.GAME_STOPPED;
			ThreadPoolManager.schedule(this, 1000);
		}
	}
	
	private final int getDelay(int[] times)
	{
		int time;
		for (int i = 0; i < (times.length - 1); i++)
		{
			time = times[i];
			if (time >= countDown)
			{
				continue;
			}
			
			final int delay = countDown - time;
			countDown = time;
			return delay;
		}
		// should not happens
		countDown = -1;
		return 1;
	}
	
	/**
	 * Second stage: check for defaulted, port players to arena, announce game.
	 * @return true if no participants defaulted.
	 */
	private final boolean startGame()
	{
		try
		{
			// Checking for opponents and teleporting to arena
			if (game.checkDefaulted())
			{
				return false;
			}
			
			if (!game.portPlayersToArena(zone.getSpawns()))
			{
				return false;
			}
			
			game.removals();
			needAnnounce = true;
			OlympiadGameManager.getInstance().startBattle(); // inform manager
			return true;
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Fourth stage: last checks, start competition itself.
	 * @return true if all participants online and ready on the stadium.
	 */
	private final boolean startBattle()
	{
		try
		{
			if (game.checkBattleStatus() && game.makeCompetitionStart())
			{
				// game successfully started
				game.broadcastOlympiadInfo(zone);
				zone.broadcastPacket(new SystemMessage(SystemMessage.STARTS_THE_GAME));
				zone.updateZoneStatusForCharactersInside();
				return true;
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Fifth stage: battle is running, returns true if winner found.
	 * @return
	 */
	private final boolean checkBattle()
	{
		try
		{
			return game.haveWinner();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
		
		return true;
	}
	
	/**
	 * Sixth stage: winner's validations
	 */
	private final void stopGame()
	{
		try
		{
			game.validateWinner(zone);
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
		
		try
		{
			zone.updateZoneStatusForCharactersInside();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
		
		try
		{
			game.cleanEffects();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	/**
	 * Seventh stage: game cleanup (port players back, closing doors, etc)
	 */
	private final void cleanupGame()
	{
		try
		{
			game.playersStatusBack();
			game.portPlayersBack();
			game.clearPlayers();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
	}
}
