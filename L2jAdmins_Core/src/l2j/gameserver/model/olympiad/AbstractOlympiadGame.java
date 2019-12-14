package l2j.gameserver.model.olympiad;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.zone.type.OlympiadStadiumZone;
import l2j.gameserver.network.AServerPacket;
import l2j.gameserver.network.external.server.ExOlympiadMode;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author godson, GodKratos, Pere, DS
 */
public abstract class AbstractOlympiadGame
{
	protected static final Logger LOG = Logger.getLogger(AbstractOlympiadGame.class.getName());
	
	protected static final String POINTS = "olympiad_points";
	protected static final String COMP_DONE = "competitions_done";
	protected static final String COMP_WON = "competitions_won";
	protected static final String COMP_LOST = "competitions_lost";
	protected static final String COMP_DRAWN = "competitions_drawn";
	
	protected long startTime = 0;
	protected boolean aborted = false;
	protected final int stadiumID;
	
	protected AbstractOlympiadGame(int id)
	{
		stadiumID = id;
	}
	
	public final boolean isAborted()
	{
		return aborted;
	}
	
	public final int getStadiumId()
	{
		return stadiumID;
	}
	
	protected boolean makeCompetitionStart()
	{
		startTime = System.currentTimeMillis();
		return !aborted;
	}
	
	protected final void addPointsToParticipant(Participant par, int points)
	{
		par.updateStat(POINTS, points);
		final SystemMessage sm = new SystemMessage(SystemMessage.C1_HAS_GAINED_S2_OLYMPIAD_POINTS);
		sm.addString(par.name);
		sm.addNumber(points);
		broadcastPacket(sm);
	}
	
	protected final void removePointsFromParticipant(Participant par, int points)
	{
		par.updateStat(POINTS, -points);
		final SystemMessage sm = new SystemMessage(SystemMessage.C1_HAS_LOST_S2_OLYMPIAD_POINTS);
		sm.addString(par.name);
		sm.addNumber(points);
		broadcastPacket(sm);
	}
	
	/**
	 * Return null if player passed all checks or broadcast the reason to opponent.
	 * @param  player to check.
	 * @return        null or reason.
	 */
	protected static SystemMessage checkDefaulted(L2PcInstance player)
	{
		if ((player == null) || !player.isOnline())
		{
			return new SystemMessage(SystemMessage.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		}
		
		// safety precautions
		if (player.inObserverMode())
		{
			return new SystemMessage(SystemMessage.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isDead())
		{
			player.sendMessage("Cannot participate Olympiad while dead");
			return new SystemMessage(SystemMessage.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isSubClassActive())
		{
			player.sendMessage("You have changed from your main class to a subclass and therefore are removed from the Grand Olympiad Games waiting list");
			return new SystemMessage(SystemMessage.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if ((player.getInventoryLimit() * 0.8) <= player.getInventory().getSize())
		{
			player.sendMessage("You can't join a Grand Olympiad Game match with that much stuff on you! Reduce your weight to below 80 percent full and request to join again!");
			return new SystemMessage(SystemMessage.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		return null;
	}
	
	protected static final boolean portPlayerToArena(Participant par, LocationHolder loc, int id)
	{
		final L2PcInstance player = par.player;
		if ((player == null) || !player.isOnline())
		{
			return false;
		}
		
		try
		{
			player.getSavedLocation().setXYZ(player.getX(), player.getY(), player.getZ());
			
			player.standUp();
			player.setTarget(null);
			
			player.setOlympiadGameId(id);
			player.setIsInOlympiadMode(true);
			player.setIsOlympiadStart(false);
			player.setOlympiadSide(par.side);
			player.teleToLocation(loc, false);
			player.sendPacket(new ExOlympiadMode(par.side));
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	protected static final void removals(L2PcInstance player, boolean removeParty)
	{
		try
		{
			if (player == null)
			{
				return;
			}
			
			// Remove Buffs
			player.stopAllEffects();
			
			// Abort casting if player casting
			player.abortAttack();
			player.abortCast();
			
			// Force the character to be visible
			player.setVisible();
			
			// Heal Player fully
			player.setCurrentCp(player.getStat().getMaxCp());
			player.setCurrentHp(player.getStat().getMaxHp());
			player.setCurrentMp(player.getStat().getMaxMp());
			
			// Remove Summon's Buffs
			final L2Summon summon = player.getPet();
			if (summon != null)
			{
				summon.stopAllEffects();
				summon.abortAttack();
				summon.abortCast();
				
				if (summon instanceof L2PetInstance)
				{
					summon.unSummon();
				}
			}
			
			// stop any cubic that has been given by other player.
			player.removeCubicsByOthers();
			
			// Remove player from his party
			if (removeParty)
			{
				final Party party = player.getParty();
				if (party != null)
				{
					party.removePartyMember(player, true);
				}
			}
			
			checkItemRestriction(player);
			
			// Remove shot automation
			player.disableAutoShotsAll();
			
			// Discharge any active shots
			ItemInstance item = player.getActiveWeaponInstance();
			if (item != null)
			{
				item.unChargeAllShots();
			}
			
			player.sendSkillList();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	private static void checkItemRestriction(L2PcInstance player)
	{
		// Remove Hero Weapons
		// check to prevent the using of weapon/shield on strider/wyvern
		ItemInstance wpn = player.getInventory().getPaperdollItem(ParpedollType.RHAND);
		if (wpn == null)
		{
			wpn = player.getInventory().getPaperdollItem(ParpedollType.LRHAND);
		}
		
		if (wpn == null)
		{
			return;
		}
		
		int itemId = wpn.getId();
		if ((itemId >= 6611) && (itemId <= 6621))
		{
			List<ItemInstance> unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			
			player.abortAttack();
			player.broadcastUserInfo();
			
			// this can be 0 if the user pressed the right mouse button twice very fast
			if (unequiped.size() > 0)
			{
				if (unequiped.get(0).isWear())
				{
					return;
				}
				
				SystemMessage sm = null;
				if (unequiped.get(0).getEnchantLevel() > 0)
				{
					sm = new SystemMessage(SystemMessage.EQUIPMENT_S1_S2_REMOVED).addNumber(unequiped.get(0).getEnchantLevel()).addItemName(unequiped.get(0).getId());
				}
				else
				{
					sm = new SystemMessage(SystemMessage.S1_DISARMED).addItemName(unequiped.get(0).getId());
				}
				player.sendPacket(sm);
			}
		}
	}
	
	/**
	 * Buff and heal the player. WW1 for fighter/mage + haste 2 if fighter.
	 * @param player : the happy benefactor.
	 */
	protected static final void buffAndHealPlayer(L2PcInstance player)
	{
		Skill skill = SkillData.getInstance().getSkill(1204, 1); // Windwalk 1
		if (skill != null)
		{
			skill.getEffects(player, player);
			player.sendPacket(new SystemMessage(SystemMessage.YOU_FEEL_S1_EFFECT).addSkillName(1204));
		}
		
		if (!player.isMageClass())
		{
			skill = SkillData.getInstance().getSkill(1086, 2); // Haste 2
			if (skill != null)
			{
				skill.getEffects(player, player);
				player.sendPacket(new SystemMessage(SystemMessage.YOU_FEEL_S1_EFFECT).addSkillName(1086));
			}
		}
		
		// Heal Player fully
		player.setCurrentCp(player.getStat().getMaxCp());
		player.setCurrentHp(player.getStat().getMaxHp());
		player.setCurrentMp(player.getStat().getMaxMp());
	}
	
	protected static final void cleanEffects(L2PcInstance player)
	{
		try
		{
			// prevent players kill each other
			player.setIsOlympiadStart(false);
			player.setTarget(null);
			player.abortAttack();
			player.abortCast();
			player.getAI().setIntention(CtrlIntentionType.IDLE);
			
			if (player.isDead())
			{
				player.setIsDead(false);
			}
			
			final L2Summon summon = player.getPet();
			if ((summon != null) && !summon.isDead())
			{
				summon.setTarget(null);
				summon.abortAttack();
				summon.abortCast();
				summon.getAI().setIntention(CtrlIntentionType.IDLE);
			}
			
			player.setCurrentCp(player.getStat().getMaxCp());
			player.setCurrentHp(player.getStat().getMaxHp());
			player.setCurrentMp(player.getStat().getMaxMp());
			player.getStatus().startHpMpRegeneration();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected static final void playerStatusBack(L2PcInstance player)
	{
		try
		{
			player.standUp();
			
			player.setIsInOlympiadMode(false);
			player.setIsOlympiadStart(false);
			player.setOlympiadSide(-1);
			player.setOlympiadGameId(-1);
			player.sendPacket(new ExOlympiadMode(0));
			
			player.stopAllEffects();
			player.clearCharges();
			
			final L2Summon summon = player.getPet();
			if ((summon != null) && !summon.isDead())
			{
				summon.stopAllEffects();
			}
			
			player.sendSkillList();
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	protected static final void portPlayerBack(L2PcInstance player)
	{
		if (player == null)
		{
			return;
		}
		
		final LocationHolder loc = player.getSavedLocation();
		if (loc.equals(0, 0, 0))
		{
			return;
		}
		
		player.teleToLocation(loc, false);
		player.getSavedLocation().setXYZ(player.getX(), player.getY(), player.getZ());
	}
	
	public static final void rewardParticipant(L2PcInstance player, Map<Integer, Integer> map)
	{
		if ((player == null) || !player.isOnline() || (map.isEmpty()))
		{
			return;
		}
		
		try
		{
			for (Entry<Integer, Integer> it : map.entrySet())
			{
				var id = it.getKey();
				var count = it.getValue();
				
				final ItemInstance item = player.getInventory().addItem("Olympiad", id, count, player, null);
				if (item == null)
				{
					continue;
				}
				
				player.sendPacket(new SystemMessage(SystemMessage.EARNED_S2_S1_S).addItemName(id).addNumber(count));
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, e.getMessage(), e);
		}
	}
	
	public abstract CompetitionType getType();
	
	public abstract String[] getPlayerNames();
	
	public abstract boolean containsParticipant(int playerId);
	
	public abstract void sendOlympiadInfo(L2Character player);
	
	public abstract void broadcastOlympiadInfo(OlympiadStadiumZone stadium);
	
	protected abstract void broadcastPacket(AServerPacket packet);
	
	protected abstract boolean checkDefaulted();
	
	protected abstract void removals();
	
	protected abstract void buffAndHealPlayers();
	
	protected abstract boolean portPlayersToArena(List<LocationHolder> spawns);
	
	protected abstract void cleanEffects();
	
	protected abstract void portPlayersBack();
	
	protected abstract void playersStatusBack();
	
	protected abstract void clearPlayers();
	
	protected abstract void handleDisconnect(L2PcInstance player);
	
	protected abstract void resetDamage();
	
	protected abstract void addDamage(L2PcInstance player, int damage);
	
	protected abstract boolean checkBattleStatus();
	
	protected abstract boolean haveWinner();
	
	protected abstract void validateWinner(OlympiadStadiumZone stadium);
	
	protected abstract int getDivider();
	
	protected abstract Map<Integer, Integer> getReward();
}
