package l2j.gameserver.instancemanager.sevensigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2FestivalMonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.util.Rnd;

/**
 * Each running festival is represented by an L2DarknessFestival class.<br>
 * It contains all the spawn information and data for the running festival.<br>
 * All festivals are managed by the FestivalManager class, which must be initialized first.
 * @author Tempy
 */
public class SevenSignsDarknessFestival
{
	protected static final Logger LOG = Logger.getLogger(SevenSignsDarknessFestival.class.getName());
	
	private static final int FESTIVAL_MAX_OFFSET_X = 230;
	private static final int FESTIVAL_MAX_OFFSET_Y = 230;
	
	public static final List<LocationHolder> FESTIVAL_DAWN_PLAYER_SPAWNS = new ArrayList<>();
	static
	{
		FESTIVAL_DAWN_PLAYER_SPAWNS.add(new LocationHolder(-79187, 113186, -4895, 90)); // 31 and below
		FESTIVAL_DAWN_PLAYER_SPAWNS.add(new LocationHolder(-75918, 110137, -4895, 0)); // 42 and below
		FESTIVAL_DAWN_PLAYER_SPAWNS.add(new LocationHolder(-73835, 111969, -4895, 0)); // 53 and below
		FESTIVAL_DAWN_PLAYER_SPAWNS.add(new LocationHolder(-76170, 113804, -4895, 0)); // 64 and below
		FESTIVAL_DAWN_PLAYER_SPAWNS.add(new LocationHolder(-78927, 109528, -4895, 0)); // No level limit
	}
	
	private static final List<LocationHolder> FESTIVAL_DAWN_WITCH_SPAWNS = new ArrayList<>();
	static
	{
		FESTIVAL_DAWN_WITCH_SPAWNS.add(new LocationHolder(-79183, 113052, -4891, 0, 8132)); // 31 and below
		FESTIVAL_DAWN_WITCH_SPAWNS.add(new LocationHolder(-75916, 110270, -4891, 0, 8133)); // 42 and below
		FESTIVAL_DAWN_WITCH_SPAWNS.add(new LocationHolder(-73979, 111970, -4891, 0, 8134)); // 53 and below
		FESTIVAL_DAWN_WITCH_SPAWNS.add(new LocationHolder(-76174, 113663, -4891, 0, 8135)); // 64 and below
		FESTIVAL_DAWN_WITCH_SPAWNS.add(new LocationHolder(-78930, 109664, -4891, 0, 8136)); // No level limit
	}
	
	public static final List<LocationHolder> FESTIVAL_DUSK_PLAYER_SPAWNS = new ArrayList<>();
	static
	{
		FESTIVAL_DUSK_PLAYER_SPAWNS.add(new LocationHolder(-77200, 88966, -5151, 0)); // 31 and below
		FESTIVAL_DUSK_PLAYER_SPAWNS.add(new LocationHolder(-76941, 85307, -5151, 0)); // 42 and below
		FESTIVAL_DUSK_PLAYER_SPAWNS.add(new LocationHolder(-74855, 87135, -5151, 0)); // 53 and below
		FESTIVAL_DUSK_PLAYER_SPAWNS.add(new LocationHolder(-80208, 88222, -5151, 0)); // 64 and below
		FESTIVAL_DUSK_PLAYER_SPAWNS.add(new LocationHolder(-79954, 84697, -5151, 0)); // No level limit
	}
	
	private static final List<LocationHolder> FESTIVAL_DUSK_WITCH_SPAWNS = new ArrayList<>();
	static
	{
		FESTIVAL_DUSK_WITCH_SPAWNS.add(new LocationHolder(-77199, 88830, -5147, 0, 8142)); // 31 and below
		FESTIVAL_DUSK_WITCH_SPAWNS.add(new LocationHolder(-76942, 85438, -5147, 0, 8143)); // 42 and below
		FESTIVAL_DUSK_WITCH_SPAWNS.add(new LocationHolder(-74990, 87135, -5147, 0, 8144)); // 53 and below
		FESTIVAL_DUSK_WITCH_SPAWNS.add(new LocationHolder(-80207, 88222, -5147, 0, 8145)); // 64 and below
		FESTIVAL_DUSK_WITCH_SPAWNS.add(new LocationHolder(-79952, 84833, -5147, 0, 8146));// No level limit
	}
	
	protected final CabalType cabal;
	protected final int levelRange;
	protected boolean challengeIncreased;
	
	private LocationHolder startLocation;
	private LocationHolder witchSpawn;
	
	private L2Npc witchInst;
	private final List<L2FestivalMonsterInstance> npcInsts;
	
	private List<L2PcInstance> participants;
	private final Map<L2PcInstance, LocationHolder> originalLocations;
	
	public SevenSignsDarknessFestival(CabalType cabalType, int levelRange)
	{
		cabal = cabalType;
		this.levelRange = levelRange;
		originalLocations = new HashMap<>();
		npcInsts = new ArrayList<>();
		
		participants = SevenSignsFestival.getInstance().getParticipants(cabalType, levelRange);
		
		if (cabalType == CabalType.DAWN)
		{
			witchSpawn = FESTIVAL_DAWN_WITCH_SPAWNS.get(levelRange);
			startLocation = FESTIVAL_DAWN_PLAYER_SPAWNS.get(levelRange);
		}
		else
		{
			witchSpawn = FESTIVAL_DUSK_WITCH_SPAWNS.get(levelRange);
			startLocation = FESTIVAL_DAWN_PLAYER_SPAWNS.get(levelRange);
		}
		
		// FOR TESTING!
		if (participants == null)
		{
			participants = new ArrayList<>();
		}
		
		festivalInit();
	}
	
	protected void festivalInit()
	{
		// Teleport all players to arena and notify them.
		if ((participants != null) && (participants.size() > 0))
		{
			for (L2PcInstance participant : participants)
			{
				originalLocations.put(participant, new LocationHolder(participant.getX(), participant.getY(), participant.getZ(), participant.getHeading()));
				
				// Randomize the spawn point around the specific centerpoint for each player.
				int x = startLocation.getX();
				int y = startLocation.getY();
				
				if (Rnd.nextBoolean())
				{
					x += Rnd.nextInt(FESTIVAL_MAX_OFFSET_X);
					y += Rnd.nextInt(FESTIVAL_MAX_OFFSET_Y);
				}
				else
				{
					x -= Rnd.nextInt(FESTIVAL_MAX_OFFSET_X);
					y -= Rnd.nextInt(FESTIVAL_MAX_OFFSET_Y);
				}
				
				participant.getAI().setIntention(CtrlIntentionType.IDLE);
				participant.teleToLocation(x, y, startLocation.getZ(), true);
				
				// Remove all buffs from all participants on entry. Works like the skill Cancel.
				for (Effect e : participant.getAllEffects())
				{
					e.exit();
				}
				
				// Remove any stray blood offerings in inventory
				ItemInstance bloodOfferings = participant.getInventory().getItemById(SevenSignsFestival.FESTIVAL_OFFERING_ID);
				if (bloodOfferings != null)
				{
					participant.getInventory().destroyItem("SevenSigns", bloodOfferings, null, true);
				}
			}
		}
		
		NpcTemplate witchTemplate = NpcData.getInstance().getTemplate(witchSpawn.getNpcId());
		
		// Spawn the festival witch for this arena
		try
		{
			Spawn npcSpawn = new Spawn(witchTemplate);
			
			npcSpawn.setX(witchSpawn.getX());
			npcSpawn.setY(witchSpawn.getY());
			npcSpawn.setZ(witchSpawn.getZ());
			npcSpawn.setHeading(witchSpawn.getHeading());
			npcSpawn.setAmount(1);
			npcSpawn.setRespawnDelay(1);
			
			// Needed as doSpawn() is required to be called also for the NpcInstance it returns.
			npcSpawn.startRespawn();
			
			SpawnData.getInstance().addNewSpawn(npcSpawn, false);
			witchInst = npcSpawn.doSpawn();
		}
		catch (Exception e)
		{
			LOG.warning("SevenSignsFestival: Error while spawning Festival Witch ID " + witchSpawn.getNpcId() + ": " + e);
		}
		
		// Make it appear as though the Witch has apparated there.
		witchInst.broadcastPacket(new MagicSkillUse(witchInst, witchInst, 2003, 1, 1, 0));
		
		// And another one...:D
		witchInst.broadcastPacket(new MagicSkillUse(witchInst, witchInst, 2133, 1, 1, 0));
		
		// Send a message to all participants from the witch.
		sendMessageToParticipants("The festival will begin in 2 minutes.");
	}
	
	public void festivalStart()
	{
		spawnFestivalMonsters(SevenSignsFestival.FESTIVAL_DEFAULT_RESPAWN, 0);
	}
	
	public void moveMonstersToCenter()
	{
		for (L2FestivalMonsterInstance festivalMob : npcInsts)
		{
			if (festivalMob.isDead())
			{
				continue;
			}
			
			// Only move monsters that are idle or doing their usual functions.
			CtrlIntentionType currIntention = festivalMob.getAI().getIntention();
			
			if ((currIntention != CtrlIntentionType.IDLE) && (currIntention != CtrlIntentionType.ACTIVE))
			{
				continue;
			}
			
			int x = startLocation.getX();
			int y = startLocation.getY();
			
			/*
			 * Random X and Y coords around the player start location, up to half of the maximum allowed offset are generated to prevent the mobs from all moving to the exact same place.
			 */
			if (Rnd.nextBoolean())
			{
				x += Rnd.nextInt(FESTIVAL_MAX_OFFSET_X);
				y += Rnd.nextInt(FESTIVAL_MAX_OFFSET_Y);
			}
			else
			{
				x -= Rnd.nextInt(FESTIVAL_MAX_OFFSET_X);
				y -= Rnd.nextInt(FESTIVAL_MAX_OFFSET_Y);
			}
			
			festivalMob.setRunning();
			festivalMob.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(x, y, startLocation.getZ(), Rnd.nextInt(65536)));
		}
	}
	
	/**
	 * Used to spawn monsters unique to the festival. <br>
	 * Valid SpawnTypes:<br>
	 * 0 - All Primary Monsters (starting monsters) <br>
	 * 1 - Same as 0, but without archers/marksmen. (used for challenge increase) <br>
	 * 2 - Secondary Monsters (archers) <br>
	 * 3 - Festival Chests
	 * @param respawnDelay
	 * @param spawnType
	 */
	public void spawnFestivalMonsters(int respawnDelay, int spawnType)
	{
		List<LocationHolder> locs = null;
		
		switch (spawnType)
		{
			case 0:
			case 1:
				locs = (cabal == CabalType.DAWN) ? SevenSignsFestival.FESTIVAL_DAWN_PRIMARY_SPAWNS.get(levelRange) : SevenSignsFestival.FESTIVAL_DUSK_PRIMARY_SPAWNS.get(levelRange);
				break;
			case 2:
				locs = (cabal == CabalType.DAWN) ? SevenSignsFestival.FESTIVAL_DAWN_SECONDARY_SPAWNS.get(levelRange) : SevenSignsFestival.FESTIVAL_DUSK_SECONDARY_SPAWNS.get(levelRange);
				break;
			case 3:
				locs = (cabal == CabalType.DAWN) ? SevenSignsFestival.FESTIVAL_DAWN_CHEST_SPAWNS.get(levelRange) : SevenSignsFestival.FESTIVAL_DUSK_CHEST_SPAWNS.get(levelRange);
				break;
		}
		
		if (locs == null)
		{
			LOG.warning("Error in spawn Npc Seven Sign Festival");
			return;
		}
		
		for (LocationHolder currSpawn : locs)
		{
			// Only spawn archers/marksmen if specified to do so.
			if ((spawnType == 1) && isFestivalArcher(currSpawn.getNpcId()))
			{
				continue;
			}
			
			NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(currSpawn.getNpcId());
			
			try
			{
				Spawn npcSpawn = new Spawn(npcTemplate);
				
				npcSpawn.setX(currSpawn.getX());
				npcSpawn.setY(currSpawn.getY());
				npcSpawn.setZ(currSpawn.getZ());
				npcSpawn.setHeading(Rnd.nextInt(65536));
				npcSpawn.setAmount(1);
				npcSpawn.setRespawnDelay(respawnDelay);
				
				// Needed as doSpawn() is required to be called also for the NpcInstance it returns.
				npcSpawn.startRespawn();
				
				SpawnData.getInstance().addNewSpawn(npcSpawn, false);
				L2FestivalMonsterInstance festivalMob = (L2FestivalMonsterInstance) npcSpawn.doSpawn();
				
				// Set the offering bonus to 2x or 5x the amount per kill,
				// if this spawn is part of an increased challenge or is a festival chest.
				if (spawnType == 1)
				{
					festivalMob.setOfferingBonus(2);
				}
				else if (spawnType == 3)
				{
					festivalMob.setOfferingBonus(5);
				}
				
				npcInsts.add(festivalMob);
			}
			catch (Exception e)
			{
				LOG.warning("SevenSignsFestival: Error while spawning NPC ID " + currSpawn.getNpcId() + ": " + e);
			}
		}
	}
	
	public boolean increaseChallenge()
	{
		if (challengeIncreased)
		{
			return false;
		}
		
		// Set this flag to true to make sure that this can only be done once.
		challengeIncreased = true;
		
		// Spawn more festival monsters, but this time with a twist.
		spawnFestivalMonsters(SevenSignsFestival.FESTIVAL_DEFAULT_RESPAWN, 1);
		return true;
	}
	
	public void sendMessageToParticipants(String message)
	{
		if ((participants != null) && (participants.size() > 0))
		{
			CreatureSay cs = new CreatureSay(witchInst, SayType.ALL, "Festival Witch", message);
			
			participants.forEach(p -> p.sendPacket(cs));
		}
	}
	
	public void festivalEnd()
	{
		if ((participants != null) && (participants.size() > 0))
		{
			for (L2PcInstance participant : participants)
			{
				relocatePlayer(participant, false);
				participant.sendMessage("The festival has ended. Your party leader must now register your score before the next festival takes place.");
			}
			
			if (cabal == CabalType.DAWN)
			{
				SevenSignsFestival.getInstance().putDawnPreviousParticipants(levelRange, participants);
			}
			else
			{
				SevenSignsFestival.getInstance().putDuskPreviousParticipants(levelRange, participants);
			}
		}
		participants = null;
		
		unspawnMobs();
	}
	
	public void unspawnMobs()
	{
		// Delete all the NPCs in the current festival arena.
		if (witchInst != null)
		{
			witchInst.deleteMe();
		}
		
		if (npcInsts != null)
		{
			npcInsts.stream().filter(monster -> monster != null).forEach(monster -> monster.deleteMe());
		}
	}
	
	public void relocatePlayer(L2PcInstance participant, boolean isRemoving)
	{
		try
		{
			LocationHolder loc = originalLocations.get(participant);
			
			if (isRemoving)
			{
				originalLocations.remove(participant);
			}
			
			participant.getAI().setIntention(CtrlIntentionType.IDLE);
			participant.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), true);
		}
		catch (Exception e)
		{
			// If an exception occurs, just move the player to the nearest town.
			participant.teleToLocation(MapRegionData.TeleportWhereType.TOWN);
		}
		
		participant.sendMessage("You have been removed from the festival arena.");
	}
	
	/**
	 * Returns true if the monster ID given is of an archer/marksman type.
	 * @param  npcId
	 * @return       boolean isArcher
	 */
	private static final boolean isFestivalArcher(int npcId)
	{
		if ((npcId < 12622) || (npcId > 12721))
		{
			return false;
		}
		
		String npcIdStr = String.valueOf(npcId);
		return (npcIdStr.substring(4).equals("4") || npcIdStr.substring(4).equals("9"));
	}
	
	/**
	 * Returns true if the monster ID given is a festival chest.
	 * @param  npcId
	 * @return       boolean isChest
	 */
	private static final boolean isFestivalChest(int npcId)
	{
		return ((npcId < 12764) || (npcId > 12773));
	}
}
