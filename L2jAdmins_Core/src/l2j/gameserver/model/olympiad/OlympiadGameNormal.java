package l2j.gameserver.model.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ExOlympiadUserInfo;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

/**
 * @author GodKratos, Pere, DS
 */
abstract public class OlympiadGameNormal extends AbstractOlympiadGame
{
	protected int damageP1 = 0;
	protected int damageP2 = 0;
	
	protected Participant playerOne;
	protected Participant playerTwo;
	
	protected OlympiadGameNormal(int id, Participant[] opponents)
	{
		super(id);
		
		playerOne = opponents[0];
		playerTwo = opponents[1];
		
		playerOne.player.setOlympiadGameId(id);
		playerTwo.player.setOlympiadGameId(id);
	}
	
	protected static final Participant[] createListOfParticipants(List<Integer> list)
	{
		if ((list == null) || list.isEmpty() || (list.size() < 2))
		{
			return null;
		}
		
		int playerOneObjectId = 0;
		L2PcInstance playerOne = null;
		L2PcInstance playerTwo = null;
		
		while (list.size() > 1)
		{
			playerOneObjectId = list.remove(Rnd.get(list.size()));
			playerOne = L2World.getInstance().getPlayer(playerOneObjectId);
			if ((playerOne == null) || !playerOne.isOnline())
			{
				continue;
			}
			
			playerTwo = L2World.getInstance().getPlayer(list.remove(Rnd.get(list.size())));
			if ((playerTwo == null) || !playerTwo.isOnline())
			{
				list.add(playerOneObjectId);
				continue;
			}
			
			Participant[] result = new Participant[2];
			result[0] = new Participant(playerOne, 1);
			result[1] = new Participant(playerTwo, 2);
			
			return result;
		}
		return null;
	}
	
	@Override
	public final boolean containsParticipant(int playerId)
	{
		return (playerOne.objectId == playerId) || (playerTwo.objectId == playerId);
	}
	
	@Override
	/**
	 * Sends olympiad info to the new spectator.
	 */
	public final void sendOlympiadInfo(L2Character player)
	{
		player.sendPacket(new ExOlympiadUserInfo(playerOne.player));
		playerOne.player.updateEffectIcons();
		player.sendPacket(new ExOlympiadUserInfo(playerTwo.player));
		playerTwo.player.updateEffectIcons();
	}
	
	@Override
	/**
	 * Broadcasts olympiad info to participants and spectators on battle start.
	 */
	public final void broadcastOlympiadInfo(OlympiadStadiumZone stadium)
	{
		stadium.broadcastPacket(new ExOlympiadUserInfo(playerOne.player));
		playerOne.player.updateEffectIcons();
		stadium.broadcastPacket(new ExOlympiadUserInfo(playerTwo.player));
		playerTwo.player.updateEffectIcons();
	}
	
	@Override
	/**
	 * Broadcasts packet to participants only.
	 */
	protected final void broadcastPacket(AServerPacket packet)
	{
		playerOne.updatePlayer();
		if (playerOne.player != null)
		{
			playerOne.player.sendPacket(packet);
		}
		
		playerTwo.updatePlayer();
		if (playerTwo.player != null)
		{
			playerTwo.player.sendPacket(packet);
		}
	}
	
	@Override
	public final boolean portPlayersToArena(List<LocationHolder> spawns)
	{
		boolean result = true;
		try
		{
			result &= portPlayerToArena(playerOne, spawns.get(0), stadiumID);
			result &= portPlayerToArena(playerTwo, spawns.get(1), stadiumID);
		}
		catch (Exception e)
		{
			return false;
		}
		return result;
	}
	
	@Override
	protected final void removals()
	{
		if (aborted)
		{
			return;
		}
		
		removals(playerOne.player, true);
		removals(playerTwo.player, true);
	}
	
	@Override
	protected final void buffAndHealPlayers()
	{
		if (aborted)
		{
			return;
		}
		
		buffAndHealPlayer(playerOne.player);
		buffAndHealPlayer(playerTwo.player);
	}
	
	@Override
	protected final boolean makeCompetitionStart()
	{
		if (!super.makeCompetitionStart())
		{
			return false;
		}
		
		if ((playerOne.player == null) || (playerTwo.player == null))
		{
			return false;
		}
		
		playerOne.player.setIsOlympiadStart(true);
		playerTwo.player.setIsOlympiadStart(true);
		return true;
	}
	
	@Override
	protected final void cleanEffects()
	{
		if ((playerOne.player != null) && !playerOne.defaulted && !playerOne.disconnected && (playerOne.player.getOlympiadGameId() == getStadiumId()))
		{
			cleanEffects(playerOne.player);
		}
		
		if ((playerTwo.player != null) && !playerTwo.defaulted && !playerTwo.disconnected && (playerTwo.player.getOlympiadGameId() == getStadiumId()))
		{
			cleanEffects(playerTwo.player);
		}
	}
	
	@Override
	protected final void portPlayersBack()
	{
		if ((playerOne.player != null) && !playerOne.defaulted && !playerOne.disconnected)
		{
			portPlayerBack(playerOne.player);
		}
		if ((playerTwo.player != null) && !playerTwo.defaulted && !playerTwo.disconnected)
		{
			portPlayerBack(playerTwo.player);
		}
	}
	
	@Override
	protected final void playersStatusBack()
	{
		if ((playerOne.player != null) && !playerOne.defaulted && !playerOne.disconnected && (playerOne.player.getOlympiadGameId() == getStadiumId()))
		{
			playerStatusBack(playerOne.player);
		}
		
		if ((playerTwo.player != null) && !playerTwo.defaulted && !playerTwo.disconnected && (playerTwo.player.getOlympiadGameId() == getStadiumId()))
		{
			playerStatusBack(playerTwo.player);
		}
	}
	
	@Override
	protected final void clearPlayers()
	{
		playerOne.player = null;
		playerOne = null;
		playerTwo.player = null;
		playerTwo = null;
	}
	
	@Override
	protected final void handleDisconnect(L2PcInstance player)
	{
		if (player.getObjectId() == playerOne.objectId)
		{
			playerOne.disconnected = true;
		}
		else if (player.getObjectId() == playerTwo.objectId)
		{
			playerTwo.disconnected = true;
		}
	}
	
	@Override
	protected final boolean checkBattleStatus()
	{
		if (aborted)
		{
			return false;
		}
		
		if ((playerOne.player == null) || playerOne.disconnected)
		{
			return false;
		}
		
		if ((playerTwo.player == null) || playerTwo.disconnected)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	protected final boolean haveWinner()
	{
		if (!checkBattleStatus())
		{
			return true;
		}
		
		boolean playerOneLost = true;
		try
		{
			if (playerOne.player.getOlympiadGameId() == stadiumID)
			{
				playerOneLost = playerOne.player.isDead();
			}
		}
		catch (Exception e)
		{
			playerOneLost = true;
		}
		
		boolean playerTwoLost = true;
		try
		{
			if (playerTwo.player.getOlympiadGameId() == stadiumID)
			{
				playerTwoLost = playerTwo.player.isDead();
			}
		}
		catch (Exception e)
		{
			playerTwoLost = true;
		}
		
		return playerOneLost || playerTwoLost;
	}
	
	@Override
	protected void validateWinner(OlympiadStadiumZone stadium)
	{
		if (aborted)
		{
			return;
		}
		
		final boolean pOneCrash = ((playerOne.player == null) || playerOne.disconnected);
		final boolean pTwoCrash = ((playerTwo.player == null) || playerTwo.disconnected);
		
		final int playerOnePoints = playerOne.stats.getInteger(POINTS);
		final int playerTwoPoints = playerTwo.stats.getInteger(POINTS);
		
		int pointDiff = Math.min(playerOnePoints, playerTwoPoints) / getDivider();
		if (pointDiff <= 0)
		{
			pointDiff = 1;
		}
		else if (pointDiff > Config.ALT_OLY_MAX_POINTS)
		{
			pointDiff = Config.ALT_OLY_MAX_POINTS;
		}
		
		int points;
		
		// Check for if a player defaulted before battle started
		if (playerOne.defaulted || playerTwo.defaulted)
		{
			try
			{
				if (playerOne.defaulted)
				{
					try
					{
						points = Math.min(playerOnePoints / 3, Config.ALT_OLY_MAX_POINTS);
						removePointsFromParticipant(playerOne, points);
					}
					catch (Exception e)
					{
						LOG.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
					}
				}
				
				if (playerTwo.defaulted)
				{
					try
					{
						points = Math.min(playerTwoPoints / 3, Config.ALT_OLY_MAX_POINTS);
						removePointsFromParticipant(playerTwo, points);
					}
					catch (Exception e)
					{
						LOG.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
					}
				}
				return;
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
				return;
			}
		}
		
		// Create results for players if a player crashed
		if (pOneCrash || pTwoCrash)
		{
			try
			{
				if (pTwoCrash && !pOneCrash)
				{
					stadium.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_WON_THE_GAME).addString(playerOne.name));
					
					playerOne.updateStat(COMP_WON, 1);
					addPointsToParticipant(playerOne, pointDiff);
					
					playerTwo.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(playerTwo, pointDiff);
					
					rewardParticipant(playerOne.player, getReward());
				}
				else if (pOneCrash && !pTwoCrash)
				{
					stadium.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_WON_THE_GAME).addString(playerTwo.name));
					
					playerTwo.updateStat(COMP_WON, 1);
					addPointsToParticipant(playerTwo, pointDiff);
					
					playerOne.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(playerOne, pointDiff);
					
					rewardParticipant(playerTwo.player, getReward());
				}
				else if (pOneCrash && pTwoCrash)
				{
					stadium.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_ENDED_IN_A_TIE));
					
					playerOne.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(playerOne, pointDiff);
					
					playerTwo.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(playerTwo, pointDiff);
				}
				
				playerOne.updateStat(COMP_DONE, 1);
				playerTwo.updateStat(COMP_DONE, 1);
				
				return;
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
				return;
			}
		}
		
		try
		{
			// Calculate Fight time
			long fightTime = (System.currentTimeMillis() - startTime);
			
			double playerOneHp = 0;
			if ((playerOne.player != null) && !playerOne.player.isDead())
			{
				playerOneHp = playerOne.player.getCurrentHp() + playerOne.player.getCurrentCp();
				if (playerOneHp < 0.5)
				{
					playerOneHp = 0;
				}
			}
			
			double playerTwoHp = 0;
			if ((playerTwo.player != null) && !playerTwo.player.isDead())
			{
				playerTwoHp = playerTwo.player.getCurrentHp() + playerTwo.player.getCurrentCp();
				if (playerTwoHp < 0.5)
				{
					playerTwoHp = 0;
				}
			}
			
			// if players crashed, search if they've relogged
			playerOne.updatePlayer();
			playerTwo.updatePlayer();
			
			if (((playerOne.player == null) || !playerOne.player.isOnline()) && ((playerTwo.player == null) || !playerTwo.player.isOnline()))
			{
				playerOne.updateStat(COMP_DRAWN, 1);
				playerTwo.updateStat(COMP_DRAWN, 1);
				stadium.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_ENDED_IN_A_TIE));
			}
			else if ((playerTwo.player == null) || !playerTwo.player.isOnline() || ((playerTwoHp == 0) && (playerOneHp != 0)) || ((damageP1 > damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)))
			{
				stadium.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_WON_THE_GAME).addString(playerOne.name));
				
				playerOne.updateStat(COMP_WON, 1);
				playerTwo.updateStat(COMP_LOST, 1);
				
				addPointsToParticipant(playerOne, pointDiff);
				removePointsFromParticipant(playerTwo, pointDiff);
				
				// Save Fight Result
				saveResults(playerOne, playerTwo, 1, startTime, fightTime, getType());
				rewardParticipant(playerOne.player, getReward());
			}
			else if ((playerOne.player == null) || !playerOne.player.isOnline() || ((playerOneHp == 0) && (playerTwoHp != 0)) || ((damageP2 > damageP1) && (playerOneHp != 0) && (playerTwoHp != 0)))
			{
				stadium.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_WON_THE_GAME).addString(playerTwo.name));
				
				playerTwo.updateStat(COMP_WON, 1);
				playerOne.updateStat(COMP_LOST, 1);
				
				addPointsToParticipant(playerTwo, pointDiff);
				removePointsFromParticipant(playerOne, pointDiff);
				
				// Save Fight Result
				saveResults(playerOne, playerTwo, 2, startTime, fightTime, getType());
				rewardParticipant(playerTwo.player, getReward());
			}
			else
			{
				// Save Fight Result
				saveResults(playerOne, playerTwo, 0, startTime, fightTime, getType());
				
				stadium.broadcastPacket(new SystemMessage(SystemMessage.THE_GAME_ENDED_IN_A_TIE));
				
				removePointsFromParticipant(playerOne, Math.min(playerOnePoints / getDivider(), Config.ALT_OLY_MAX_POINTS));
				removePointsFromParticipant(playerTwo, Math.min(playerTwoPoints / getDivider(), Config.ALT_OLY_MAX_POINTS));
			}
			
			playerOne.updateStat(COMP_DONE, 1);
			playerTwo.updateStat(COMP_DONE, 1);
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
		}
	}
	
	@Override
	protected final void addDamage(L2PcInstance player, int damage)
	{
		if ((playerOne.player == null) || (playerTwo.player == null))
		{
			return;
		}
		if (player == playerOne.player)
		{
			damageP1 += damage;
		}
		else if (player == playerTwo.player)
		{
			damageP2 += damage;
		}
	}
	
	@Override
	public final String[] getPlayerNames()
	{
		return new String[]
		{
			playerOne.name,
			playerTwo.name
		};
	}
	
	@Override
	public boolean checkDefaulted()
	{
		SystemMessage reason;
		playerOne.updatePlayer();
		playerTwo.updatePlayer();
		
		reason = checkDefaulted(playerOne.player);
		if (reason != null)
		{
			playerOne.defaulted = true;
			if (playerTwo.player != null)
			{
				playerTwo.player.sendPacket(reason);
			}
		}
		
		reason = checkDefaulted(playerTwo.player);
		if (reason != null)
		{
			playerTwo.defaulted = true;
			if (playerOne.player != null)
			{
				playerOne.player.sendPacket(reason);
			}
		}
		
		return playerOne.defaulted || playerTwo.defaulted;
	}
	
	@Override
	public final void resetDamage()
	{
		damageP1 = 0;
		damageP2 = 0;
	}
	
	protected static final void saveResults(Participant one, Participant two, int winner, long startTime, long fightTime, CompetitionType type)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO olympiad_fights (charOneId, charTwoId, charOneClass, charTwoClass, winner, start, time, classed) values(?,?,?,?,?,?,?,?)"))
		{
			ps.setInt(1, one.objectId);
			ps.setInt(2, two.objectId);
			ps.setInt(3, one.baseClass);
			ps.setInt(4, two.baseClass);
			ps.setInt(5, winner);
			ps.setLong(6, startTime);
			ps.setLong(7, fightTime);
			ps.setInt(8, (type == CompetitionType.CLASSED ? 1 : 0));
			ps.execute();
			
		}
		catch (SQLException e)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.log(Level.SEVERE, "SQL exception while saving olympiad fight.", e);
			}
		}
	}
}
