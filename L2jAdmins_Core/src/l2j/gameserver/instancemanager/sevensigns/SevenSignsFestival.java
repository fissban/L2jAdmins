package l2j.gameserver.instancemanager.sevensigns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.util.Util;

/**
 * TODO: - Archer mobs should target healer characters over other party members. - ADDED 29 Sep: Players that leave a party during the Seven Signs Festival will now take damage and cannot be healed.
 * @author Tempy
 */
public class SevenSignsFestival
{
	protected static final Logger LOG = Logger.getLogger(SevenSignsFestival.class.getName());
	
	/**
	 * These length settings are important! :) All times are relative to the ELAPSED time (in ms) since a festival begins. Festival manager start is the time after the server starts to begin the first festival cycle. The cycle length should ideally be at least 2x longer than the festival length.
	 * This allows ample time for players to sign-up to participate in the festival. The intermission is the time between the festival participants being moved to the "arenas" and the spawning of the first set of mobs. The monster swarm time is the time before the monsters swarm to the center of the
	 * arena, after they are spawned. The chest spawn time is for when the bonus festival chests spawn, usually towards the end of the festival.
	 */
	public static final long FESTIVAL_SIGNUP_TIME = Config.ALT_FESTIVAL_CYCLE_LENGTH - Config.ALT_FESTIVAL_LENGTH - 60000;
	
	public static final int FESTIVAL_DEFAULT_RESPAWN = 60; // Specify in seconds!
	
	public static final byte FESTIVAL_COUNT = 5;
	public static final byte FESTIVAL_LEVEL_MAX_31 = 0;
	public static final byte FESTIVAL_LEVEL_MAX_42 = 1;
	public static final byte FESTIVAL_LEVEL_MAX_53 = 2;
	public static final byte FESTIVAL_LEVEL_MAX_64 = 3;
	public static final byte FESTIVAL_LEVEL_MAX_NONE = 4;
	public static final int[] FESTIVAL_LEVEL_SCORES =
	{
		60,
		70,
		100,
		120,
		150,
	}; // 500 maximum possible score
	
	public static final int FESTIVAL_OFFERING_ID = 5901;
	public static final int FESTIVAL_OFFERING_VALUE = 5;
	
	/*
	 * The following contains all the necessary spawn data for: - Player Start Locations - Witches - Monsters - Chests All data is given by: X, Y, Z (coords), Heading, NPC ID (if necessary) This may be moved externally in time, but the data should not change.
	 */
	public static final List<List<LocationHolder>> FESTIVAL_DAWN_PRIMARY_SPAWNS = new ArrayList<>();
	
	{
		List<LocationHolder> spawns = new ArrayList<>();
		/** Level 31 and Below - Offering of the Branded */
		spawns.add(new LocationHolder(-78537, 113839, -4895, -1, 12622));
		spawns.add(new LocationHolder(-78466, 113852, -4895, -1, 12623));
		spawns.add(new LocationHolder(-78509, 113899, -4895, -1, 12623));
		spawns.add(new LocationHolder(-78481, 112557, -4895, -1, 12622));
		spawns.add(new LocationHolder(-78559, 112504, -4895, -1, 12623));
		spawns.add(new LocationHolder(-78489, 112494, -4895, -1, 12623));
		spawns.add(new LocationHolder(-79803, 112543, -4895, -1, 12625));
		spawns.add(new LocationHolder(-79854, 112492, -4895, -1, 12626));
		spawns.add(new LocationHolder(-79886, 112557, -4895, -1, 12627));
		spawns.add(new LocationHolder(-79821, 113811, -4895, -1, 12628));
		spawns.add(new LocationHolder(-79857, 113896, -4895, -1, 12630));
		spawns.add(new LocationHolder(-79878, 113816, -4895, -1, 12631));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-79190, 113660, -4895, -1, 12624));
		spawns.add(new LocationHolder(-78710, 113188, -4895, -1, 12624));
		spawns.add(new LocationHolder(-79190, 112730, -4895, -1, 12629));
		spawns.add(new LocationHolder(-79656, 113188, -4895, -1, 12629));
		FESTIVAL_DAWN_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 42 and Below - Apostate Offering */
		// South West
		spawns.add(new LocationHolder(-76558, 110784, -4895, -1, 12632));
		spawns.add(new LocationHolder(-76607, 110815, -4895, -1, 12633));
		spawns.add(new LocationHolder(-76559, 110820, -4895, -1, 12633));
		// South East
		spawns.add(new LocationHolder(-75277, 110792, -4895, -1, 12632));
		spawns.add(new LocationHolder(-75225, 110801, -4895, -1, 12633));
		spawns.add(new LocationHolder(-75262, 110832, -4895, -1, 12633));
		// North East
		spawns.add(new LocationHolder(-75249, 109441, -4895, -1, 12635));
		spawns.add(new LocationHolder(-75278, 109495, -4895, -1, 12636));
		spawns.add(new LocationHolder(-75223, 109489, -4895, -1, 12637));
		// North West
		spawns.add(new LocationHolder(-76556, 109490, -4895, -1, 12638));
		spawns.add(new LocationHolder(-76607, 109469, -4895, -1, 12640));
		spawns.add(new LocationHolder(-76561, 109450, -4895, -1, 12641));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-76399, 110144, -4895, -1, 12634));
		spawns.add(new LocationHolder(-75912, 110606, -4895, -1, 12634));
		spawns.add(new LocationHolder(-75444, 110144, -4895, -1, 12639));
		spawns.add(new LocationHolder(-75930, 109665, -4895, -1, 12639));
		FESTIVAL_DAWN_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 53 and Below - Witch's Offering */
		// South West
		spawns.add(new LocationHolder(-73184, 111319, -4895, -1, 12642));
		spawns.add(new LocationHolder(-73135, 111294, -4895, -1, 12643));
		spawns.add(new LocationHolder(-73185, 111281, -4895, -1, 12643));
		// South East
		spawns.add(new LocationHolder(-74477, 111321, -4895, -1, 12642));
		spawns.add(new LocationHolder(-74523, 111293, -4895, -1, 12643));
		spawns.add(new LocationHolder(-74481, 111280, -4895, -1, 12643));
		// North East
		spawns.add(new LocationHolder(-74489, 112604, -4895, -1, 12645));
		spawns.add(new LocationHolder(-74491, 112660, -4895, -1, 12646));
		spawns.add(new LocationHolder(-74527, 112629, -4895, -1, 12647));
		// North West
		spawns.add(new LocationHolder(-73197, 112621, -4895, -1, 12648));
		spawns.add(new LocationHolder(-73142, 112631, -4895, -1, 12650));
		spawns.add(new LocationHolder(-73182, 112656, -4895, -1, 12651));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-73834, 112430, -4895, -1, 12644));
		spawns.add(new LocationHolder(-74299, 111959, -4895, -1, 12644));
		spawns.add(new LocationHolder(-73841, 111491, -4895, -1, 12649));
		spawns.add(new LocationHolder(-73363, 111959, -4895, -1, 12649));
		FESTIVAL_DAWN_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 64 and Below - Dark Omen Offering */
		// South West
		spawns.add(new LocationHolder(-75543, 114461, -4895, -1, 12652));
		spawns.add(new LocationHolder(-75514, 114493, -4895, -1, 12653));
		spawns.add(new LocationHolder(-75488, 114456, -4895, -1, 12653));
		// South East
		spawns.add(new LocationHolder(-75521, 113158, -4895, -1, 12652));
		spawns.add(new LocationHolder(-75504, 113110, -4895, -1, 12653));
		spawns.add(new LocationHolder(-75489, 113142, -4895, -1, 12653));
		// North East
		spawns.add(new LocationHolder(-76809, 113143, -4895, -1, 12655));
		spawns.add(new LocationHolder(-76860, 113138, -4895, -1, 12656));
		spawns.add(new LocationHolder(-76831, 113112, -4895, -1, 12657));
		// North West
		spawns.add(new LocationHolder(-76831, 114441, -4895, -1, 12658));
		spawns.add(new LocationHolder(-76840, 114490, -4895, -1, 12660));
		spawns.add(new LocationHolder(-76864, 114455, -4895, -1, 12661));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-75703, 113797, -4895, -1, 12654));
		spawns.add(new LocationHolder(-76180, 114263, -4895, -1, 12654));
		spawns.add(new LocationHolder(-76639, 113797, -4895, -1, 12659));
		spawns.add(new LocationHolder(-76180, 113337, -4895, -1, 12659));
		FESTIVAL_DAWN_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** No Level Limit - Offering of Forbidden Path */
		// South West
		spawns.add(new LocationHolder(-79576, 108881, -4895, -1, 12662));
		spawns.add(new LocationHolder(-79592, 108835, -4895, -1, 12663));
		spawns.add(new LocationHolder(-79614, 108871, -4895, -1, 12663));
		// South East
		spawns.add(new LocationHolder(-79586, 110171, -4895, -1, 12662));
		spawns.add(new LocationHolder(-79589, 110216, -4895, -1, 12663));
		spawns.add(new LocationHolder(-79620, 110177, -4895, -1, 12663));
		// North East
		spawns.add(new LocationHolder(-78825, 110182, -4895, -1, 12665));
		spawns.add(new LocationHolder(-78238, 110182, -4895, -1, 12666));
		spawns.add(new LocationHolder(-78266, 110218, -4895, -1, 12667));
		// North West
		spawns.add(new LocationHolder(-78275, 108883, -4895, -1, 12668));
		spawns.add(new LocationHolder(-78267, 108839, -4895, -1, 12670));
		spawns.add(new LocationHolder(-78241, 108871, -4895, -1, 12671));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-79394, 109538, -4895, -1, 12664));
		spawns.add(new LocationHolder(-78929, 109992, -4895, -1, 12664));
		spawns.add(new LocationHolder(-78454, 109538, -4895, -1, 12669));
		spawns.add(new LocationHolder(-78929, 109053, -4895, -1, 12669));
		FESTIVAL_DAWN_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
	}
	
	public static final List<List<LocationHolder>> FESTIVAL_DUSK_PRIMARY_SPAWNS = new ArrayList<>();
	
	{
		List<LocationHolder> spawns = new ArrayList<>();
		/** Level 31 and Below - Offering of the Branded */
		spawns.add(new LocationHolder(-76542, 89653, -5151, -1, 12622));
		spawns.add(new LocationHolder(-76509, 89637, -5151, -1, 12623));
		spawns.add(new LocationHolder(-76548, 89614, -5151, -1, 12623));
		spawns.add(new LocationHolder(-76539, 88326, -5151, -1, 12622));
		spawns.add(new LocationHolder(-76512, 88289, -5151, -1, 12623));
		spawns.add(new LocationHolder(-76546, 88287, -5151, -1, 12623));
		spawns.add(new LocationHolder(-77879, 88308, -5151, -1, 12625));
		spawns.add(new LocationHolder(-77886, 88310, -5151, -1, 12626));
		spawns.add(new LocationHolder(-77879, 88278, -5151, -1, 12627));
		spawns.add(new LocationHolder(-77857, 89605, -5151, -1, 12628));
		spawns.add(new LocationHolder(-77858, 89658, -5151, -1, 12630));
		spawns.add(new LocationHolder(-77891, 89633, -5151, -1, 12631));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-76728, 88962, -5151, -1, 12624));
		spawns.add(new LocationHolder(-77194, 88494, -5151, -1, 12624));
		spawns.add(new LocationHolder(-77660, 88896, -5151, -1, 12629));
		spawns.add(new LocationHolder(-77195, 89438, -5151, -1, 12629));
		FESTIVAL_DUSK_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 42 and Below - Apostate's Offering */
		spawns.add(new LocationHolder(-77585, 84650, -5151, -1, 12632));
		spawns.add(new LocationHolder(-77628, 84643, -5151, -1, 12633));
		spawns.add(new LocationHolder(-77607, 84613, -5151, -1, 12633));
		spawns.add(new LocationHolder(-76603, 85946, -5151, -1, 12632));
		spawns.add(new LocationHolder(-77606, 85994, -5151, -1, 12633));
		spawns.add(new LocationHolder(-77638, 85959, -5151, -1, 12633));
		spawns.add(new LocationHolder(-76301, 85960, -5151, -1, 12635));
		spawns.add(new LocationHolder(-76257, 85972, -5151, -1, 12636));
		spawns.add(new LocationHolder(-76286, 85992, -5151, -1, 12637));
		spawns.add(new LocationHolder(-76281, 84667, -5151, -1, 12638));
		spawns.add(new LocationHolder(-76291, 84611, -5151, -1, 12640));
		spawns.add(new LocationHolder(-76257, 84616, -5151, -1, 12641));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-77419, 85307, -5151, -1, 12634));
		spawns.add(new LocationHolder(-76952, 85768, -5151, -1, 12634));
		spawns.add(new LocationHolder(-76477, 85312, -5151, -1, 12639));
		spawns.add(new LocationHolder(-76942, 84832, -5151, -1, 12639));
		FESTIVAL_DUSK_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 53 and Below - Witch's Offering */
		spawns.add(new LocationHolder(-74211, 86494, -5151, -1, 12642));
		spawns.add(new LocationHolder(-74200, 86449, -5151, -1, 12643));
		spawns.add(new LocationHolder(-74167, 86464, -5151, -1, 12643));
		spawns.add(new LocationHolder(-75495, 86482, -5151, -1, 12642));
		spawns.add(new LocationHolder(-75540, 86473, -5151, -1, 12643));
		spawns.add(new LocationHolder(-75509, 86445, -5151, -1, 12643));
		spawns.add(new LocationHolder(-75509, 87775, -5151, -1, 12645));
		spawns.add(new LocationHolder(-75518, 87826, -5151, -1, 12646));
		spawns.add(new LocationHolder(-75542, 87780, -5151, -1, 12647));
		spawns.add(new LocationHolder(-74214, 87789, -5151, -1, 12648));
		spawns.add(new LocationHolder(-74169, 87801, -5151, -1, 12650));
		spawns.add(new LocationHolder(-74198, 87827, -5151, -1, 12651));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-75324, 87135, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74852, 87606, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74388, 87146, -5151, -1, 12649));
		spawns.add(new LocationHolder(-74856, 86663, -5151, -1, 12649));
		FESTIVAL_DUSK_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 64 and Below - Dark Omen Offering */
		spawns.add(new LocationHolder(-79560, 89007, -5151, -1, 12652));
		spawns.add(new LocationHolder(-79521, 89016, -5151, -1, 12653));
		spawns.add(new LocationHolder(-79544, 89047, -5151, -1, 12653));
		spawns.add(new LocationHolder(-79552, 87717, -5151, -1, 12652));
		spawns.add(new LocationHolder(-79552, 87673, -5151, -1, 12653));
		spawns.add(new LocationHolder(-79510, 87702, -5151, -1, 12653));
		spawns.add(new LocationHolder(-80866, 87719, -5151, -1, 12655));
		spawns.add(new LocationHolder(-80897, 87689, -5151, -1, 12656));
		spawns.add(new LocationHolder(-80850, 87685, -5151, -1, 12657));
		spawns.add(new LocationHolder(-80848, 89013, -5151, -1, 12658));
		spawns.add(new LocationHolder(-80887, 89051, -5151, -1, 12660));
		spawns.add(new LocationHolder(-80891, 89004, -5151, -1, 12661));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-80205, 87895, -5151, -1, 12654));
		spawns.add(new LocationHolder(-80674, 88350, -5151, -1, 12654));
		spawns.add(new LocationHolder(-80209, 88833, -5151, -1, 12659));
		spawns.add(new LocationHolder(-79743, 88364, -5151, -1, 12659));
		FESTIVAL_DUSK_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** No Level Limit - Offering of Forbidden Path */
		spawns.add(new LocationHolder(-80624, 84060, -5151, -1, 12662));
		spawns.add(new LocationHolder(-80621, 84007, -5151, -1, 12663));
		spawns.add(new LocationHolder(-80590, 84039, -5151, -1, 12663));
		spawns.add(new LocationHolder(-80605, 85349, -5151, -1, 12662));
		spawns.add(new LocationHolder(-80639, 85363, -5151, -1, 12663));
		spawns.add(new LocationHolder(-80611, 85385, -5151, -1, 12663));
		spawns.add(new LocationHolder(-79311, 85353, -5151, -1, 12665));
		spawns.add(new LocationHolder(-79277, 85384, -5151, -1, 12666));
		spawns.add(new LocationHolder(-79273, 85539, -5151, -1, 12667));
		spawns.add(new LocationHolder(-79297, 84054, -5151, -1, 12668));
		spawns.add(new LocationHolder(-79285, 84006, -5151, -1, 12670));
		spawns.add(new LocationHolder(-79260, 84040, -5151, -1, 12671));
		// Archers and Marksmen
		spawns.add(new LocationHolder(-79945, 85171, -5151, -1, 12664));
		spawns.add(new LocationHolder(-79489, 84707, -5151, -1, 12664));
		spawns.add(new LocationHolder(-79952, 84222, -5151, -1, 12669));
		spawns.add(new LocationHolder(-80423, 84703, -5151, -1, 12669));
		FESTIVAL_DUSK_PRIMARY_SPAWNS.add(spawns);
		spawns.clear();
	}
	
	public static final List<List<LocationHolder>> FESTIVAL_DAWN_SECONDARY_SPAWNS = new ArrayList<>();
	
	{
		List<LocationHolder> spawns = new ArrayList<>();
		/** 31 and Below */
		spawns.add(new LocationHolder(-78757, 112834, -4895, -1, 12629));
		spawns.add(new LocationHolder(-78581, 112834, -4895, -1, 12629));
		spawns.add(new LocationHolder(-78822, 112526, -4895, -1, 12624));
		spawns.add(new LocationHolder(-78822, 113702, -4895, -1, 12624));
		spawns.add(new LocationHolder(-78822, 113874, -4895, -1, 12624));
		spawns.add(new LocationHolder(-79524, 113546, -4895, -1, 12624));
		spawns.add(new LocationHolder(-79693, 113546, -4895, -1, 12624));
		spawns.add(new LocationHolder(-79858, 113546, -4895, -1, 12624));
		spawns.add(new LocationHolder(-79545, 112757, -4895, -1, 12629));
		spawns.add(new LocationHolder(-79545, 112586, -4895, -1, 12629));
		FESTIVAL_DAWN_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** 42 and Below */
		spawns.add(new LocationHolder(-75565, 110580, -4895, -1, 12639));
		spawns.add(new LocationHolder(-75565, 110740, -4895, -1, 12639));
		spawns.add(new LocationHolder(-75577, 109776, -4895, -1, 12634));
		spawns.add(new LocationHolder(-75413, 109776, -4895, -1, 12634));
		spawns.add(new LocationHolder(-75237, 109776, -4895, -1, 12634));
		spawns.add(new LocationHolder(-76274, 109468, -4895, -1, 12634));
		spawns.add(new LocationHolder(-76274, 109635, -4895, -1, 12634));
		spawns.add(new LocationHolder(-76274, 109795, -4895, -1, 12634));
		spawns.add(new LocationHolder(-76351, 110500, -4895, -1, 12669));
		spawns.add(new LocationHolder(-76528, 110500, -4895, -1, 12669));
		FESTIVAL_DAWN_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** 53 and Below */
		spawns.add(new LocationHolder(-74191, 111527, -4895, -1, 12649));
		spawns.add(new LocationHolder(-74191, 111362, -4895, -1, 12649));
		spawns.add(new LocationHolder(-73495, 111611, -4895, -1, 12644));
		spawns.add(new LocationHolder(-73327, 111611, -4895, -1, 12644));
		spawns.add(new LocationHolder(-73154, 111611, -4895, -1, 12644));
		spawns.add(new LocationHolder(-73473, 112301, -4895, -1, 12644));
		spawns.add(new LocationHolder(-73473, 112475, -4895, -1, 12644));
		spawns.add(new LocationHolder(-73473, 112649, -4895, -1, 12644));
		spawns.add(new LocationHolder(-74270, 112326, -4895, -1, 12649));
		spawns.add(new LocationHolder(-74443, 112326, -4895, -1, 12649));
		FESTIVAL_DAWN_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** 64 and Below */
		spawns.add(new LocationHolder(-75738, 113439, -4895, -1, 12659));
		spawns.add(new LocationHolder(-75571, 113439, -4895, -1, 12659));
		spawns.add(new LocationHolder(-75824, 114141, -4895, -1, 12654));
		spawns.add(new LocationHolder(-75824, 114309, -4895, -1, 12654));
		spawns.add(new LocationHolder(-75824, 114477, -4895, -1, 12654));
		spawns.add(new LocationHolder(-76513, 114158, -4895, -1, 12654));
		spawns.add(new LocationHolder(-76683, 114158, -4895, -1, 12654));
		spawns.add(new LocationHolder(-76857, 114158, -4895, -1, 12654));
		spawns.add(new LocationHolder(-76535, 113357, -4895, -1, 12669));
		spawns.add(new LocationHolder(-76535, 113190, -4895, -1, 12669));
		FESTIVAL_DAWN_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** No Level Limit */
		spawns.add(new LocationHolder(-79350, 109894, -4895, -1, 12669));
		spawns.add(new LocationHolder(-79534, 109894, -4895, -1, 12669));
		spawns.add(new LocationHolder(-79285, 109187, -4895, -1, 12664));
		spawns.add(new LocationHolder(-79285, 109019, -4895, -1, 12664));
		spawns.add(new LocationHolder(-79285, 108860, -4895, -1, 12664));
		spawns.add(new LocationHolder(-78587, 109172, -4895, -1, 12664));
		spawns.add(new LocationHolder(-78415, 109172, -4895, -1, 12664));
		spawns.add(new LocationHolder(-78249, 109172, -4895, -1, 12664));
		spawns.add(new LocationHolder(-78575, 109961, -4895, -1, 12669));
		spawns.add(new LocationHolder(-78575, 110130, -4895, -1, 12669));
		FESTIVAL_DAWN_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
	}
	
	public static final List<List<LocationHolder>> FESTIVAL_DUSK_SECONDARY_SPAWNS = new ArrayList<>();
	
	{
		List<LocationHolder> spawns = new ArrayList<>();
		/** 31 and Below */
		spawns.add(new LocationHolder(-76844, 89304, -5151, -1, 12624));
		spawns.add(new LocationHolder(-76844, 89479, -5151, -1, 12624));
		spawns.add(new LocationHolder(-76844, 89649, -5151, -1, 12624));
		spawns.add(new LocationHolder(-77544, 89326, -5151, -1, 12624));
		spawns.add(new LocationHolder(-77716, 89326, -5151, -1, 12624));
		spawns.add(new LocationHolder(-77881, 89326, -5151, -1, 12624));
		spawns.add(new LocationHolder(-77561, 88530, -5151, -1, 12629));
		spawns.add(new LocationHolder(-77561, 88364, -5151, -1, 12629));
		spawns.add(new LocationHolder(-76762, 88615, -5151, -1, 12629));
		spawns.add(new LocationHolder(-76594, 88615, -5151, -1, 12629));
		FESTIVAL_DUSK_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** 42 and Below */
		spawns.add(new LocationHolder(-77307, 84969, -5151, -1, 12634));
		spawns.add(new LocationHolder(-77307, 84795, -5151, -1, 12634));
		spawns.add(new LocationHolder(-77307, 84623, -5151, -1, 12634));
		spawns.add(new LocationHolder(-76614, 84944, -5151, -1, 12634));
		spawns.add(new LocationHolder(-76433, 84944, -5151, -1, 12634));
		spawns.add(new LocationHolder(-76268, 84944, -5151, -1, 12634));// ??
		spawns.add(new LocationHolder(-76594, 85745, -5151, -1, 12639));
		spawns.add(new LocationHolder(-76594, 85910, -5151, -1, 12639));
		spawns.add(new LocationHolder(-77384, 85660, -5151, -1, 12639));
		spawns.add(new LocationHolder(-77555, 85660, -5151, -1, 12639));
		FESTIVAL_DUSK_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** 53 and Below */
		spawns.add(new LocationHolder(-74517, 86782, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74344, 86782, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74185, 86782, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74496, 87464, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74496, 87636, -5151, -1, 12644));
		spawns.add(new LocationHolder(-74496, 87815, -5151, -1, 12644));
		spawns.add(new LocationHolder(-75298, 87497, -5151, -1, 12649));
		spawns.add(new LocationHolder(-75460, 87497, -5151, -1, 12649));
		spawns.add(new LocationHolder(-75219, 86712, -5151, -1, 12649));
		spawns.add(new LocationHolder(-75219, 86531, -5151, -1, 12649));
		FESTIVAL_DUSK_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** 64 and Below */
		spawns.add(new LocationHolder(-79851, 88703, -5151, -1, 12654));
		spawns.add(new LocationHolder(-79851, 88868, -5151, -1, 12654));
		spawns.add(new LocationHolder(-79851, 89040, -5151, -1, 12654));
		spawns.add(new LocationHolder(-80548, 88722, -5151, -1, 12654));
		spawns.add(new LocationHolder(-80711, 88722, -5151, -1, 12654));
		spawns.add(new LocationHolder(-80883, 88722, -5151, -1, 12654));
		spawns.add(new LocationHolder(-80565, 87916, -5151, -1, 12659));
		spawns.add(new LocationHolder(-80565, 87752, -5151, -1, 12659));
		spawns.add(new LocationHolder(-79779, 87996, -5151, -1, 12659));
		spawns.add(new LocationHolder(-79613, 87996, -5151, -1, 12659));
		FESTIVAL_DUSK_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
		
		/** No Level Limit */
		spawns.add(new LocationHolder(-79271, 84330, -5151, -1, 12664));
		spawns.add(new LocationHolder(-79448, 84330, -5151, -1, 12664));
		spawns.add(new LocationHolder(-79601, 84330, -5151, -1, 12664));
		spawns.add(new LocationHolder(-80311, 84367, -5151, -1, 12664));
		spawns.add(new LocationHolder(-80311, 84196, -5151, -1, 12664));
		spawns.add(new LocationHolder(-80311, 84015, -5151, -1, 12664));
		spawns.add(new LocationHolder(-80556, 85049, -5151, -1, 12669));
		spawns.add(new LocationHolder(-80384, 85049, -5151, -1, 12669));
		spawns.add(new LocationHolder(-79598, 85127, -5151, -1, 12669));
		spawns.add(new LocationHolder(-79598, 85303, -5151, -1, 12669));
		FESTIVAL_DUSK_SECONDARY_SPAWNS.add(spawns);
		spawns.clear();
	}
	
	public static final List<List<LocationHolder>> FESTIVAL_DAWN_CHEST_SPAWNS = new ArrayList<>();
	
	{
		List<LocationHolder> spawns = new ArrayList<>();
		/** Level 31 and Below */
		spawns.add(new LocationHolder(-78999, 112957, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79153, 112873, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79256, 112873, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79368, 112957, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79481, 113124, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79481, 113275, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79364, 113398, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79213, 113500, -4927, -1, 12764));
		spawns.add(new LocationHolder(-79099, 113500, -4927, -1, 12764));
		spawns.add(new LocationHolder(-78960, 113398, -4927, -1, 12764));
		spawns.add(new LocationHolder(-78882, 113235, -4927, -1, 12764));
		spawns.add(new LocationHolder(-78882, 113099, -4927, -1, 12764));
		FESTIVAL_DAWN_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 42 and Below */
		spawns.add(new LocationHolder(-76119, 110383, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75980, 110442, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75848, 110442, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75720, 110383, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75625, 110195, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75625, 110063, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75722, 109908, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75863, 109832, -4927, -1, 12765));
		spawns.add(new LocationHolder(-75989, 109832, -4927, -1, 12765));
		spawns.add(new LocationHolder(-76130, 109908, -4927, -1, 12765));
		spawns.add(new LocationHolder(-76230, 110079, -4927, -1, 12765));
		spawns.add(new LocationHolder(-76230, 110215, -4927, -1, 12765));
		FESTIVAL_DAWN_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 53 and Below */
		spawns.add(new LocationHolder(-74055, 111781, -4927, -1, 12766));
		spawns.add(new LocationHolder(-74144, 111938, -4927, -1, 12766));
		spawns.add(new LocationHolder(-74144, 112075, -4927, -1, 12766));
		spawns.add(new LocationHolder(-74055, 112173, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73885, 112289, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73756, 112289, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73574, 112141, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73511, 112040, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73511, 111912, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73574, 111772, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73767, 111669, -4927, -1, 12766));
		spawns.add(new LocationHolder(-73899, 111669, -4927, -1, 12766));
		FESTIVAL_DAWN_CHEST_SPAWNS.add(spawns);
		
		/** Level 64 and Below */
		spawns.add(new LocationHolder(-76008, 113566, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76159, 113485, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76267, 113485, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76386, 113566, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76482, 113748, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76482, 113885, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76371, 114029, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76220, 114118, -4927, -1, 12767));
		spawns.add(new LocationHolder(-76092, 114118, -4927, -1, 12767));
		spawns.add(new LocationHolder(-75975, 114029, -4927, -1, 12767));
		spawns.add(new LocationHolder(-75861, 113851, -4927, -1, 12767)); // ?? revisar loc
		spawns.add(new LocationHolder(-75861, 113713, -4927, -1, 12767));
		FESTIVAL_DAWN_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** No Level Limit */
		spawns.add(new LocationHolder(-79100, 109782, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78962, 109853, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78851, 109853, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78721, 109782, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78615, 109596, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78615, 109453, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78746, 109300, -4927, -1, 12768));
		spawns.add(new LocationHolder(-78881, 109203, -4927, -1, 12768));
		spawns.add(new LocationHolder(-79027, 109203, -4927, -1, 12768));
		spawns.add(new LocationHolder(-79159, 109300, -4927, -1, 12768));
		spawns.add(new LocationHolder(-79240, 109480, -4927, -1, 12768));
		spawns.add(new LocationHolder(-79240, 109615, -4927, -1, 12768));
		FESTIVAL_DAWN_CHEST_SPAWNS.add(spawns);
		spawns.clear();
	}
	
	public static final List<List<LocationHolder>> FESTIVAL_DUSK_CHEST_SPAWNS = new ArrayList<>();
	
	{
		List<LocationHolder> spawns = new ArrayList<>();
		/** Level 31 and Below */
		spawns.add(new LocationHolder(-77016, 88726, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77136, 88646, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77247, 88646, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77380, 88726, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77512, 88883, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77512, 89053, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77378, 89287, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77254, 89238, -5183, -1, 12769));
		spawns.add(new LocationHolder(-77095, 89238, -5183, -1, 12769));
		spawns.add(new LocationHolder(-76996, 89287, -5183, -1, 12769));
		spawns.add(new LocationHolder(-76901, 89025, -5183, -1, 12769));
		spawns.add(new LocationHolder(-76901, 88891, -5183, -1, 12769));
		FESTIVAL_DUSK_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 42 and Below */
		spawns.add(new LocationHolder(-77128, 85553, -5183, -1, 12770));
		spawns.add(new LocationHolder(-77036, 85594, -5183, -1, 12770));
		spawns.add(new LocationHolder(-76919, 85594, -5183, -1, 12770));
		spawns.add(new LocationHolder(-76755, 85553, -5183, -1, 12770));
		spawns.add(new LocationHolder(-76635, 85392, -5183, -1, 12770));
		spawns.add(new LocationHolder(-76635, 85216, -5183, -1, 12770));
		spawns.add(new LocationHolder(-76761, 85025, -5183, -1, 12770));
		spawns.add(new LocationHolder(-76908, 85004, -5183, -1, 12770));
		spawns.add(new LocationHolder(-77041, 85004, -5183, -1, 12770));
		spawns.add(new LocationHolder(-77138, 85025, -5183, -1, 12770));
		spawns.add(new LocationHolder(-77268, 85219, -5183, -1, 12770));
		spawns.add(new LocationHolder(-77268, 85410, -5183, -1, 12770));
		FESTIVAL_DUSK_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 53 and Below */
		spawns.add(new LocationHolder(-75150, 87303, -5183, -1, 12771));
		spawns.add(new LocationHolder(-75150, 87175, -5183, -1, 12771));
		spawns.add(new LocationHolder(-75150, 87175, -5183, -1, 12771));
		spawns.add(new LocationHolder(-75150, 87303, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74943, 87433, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74767, 87433, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74556, 87306, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74556, 87184, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74556, 87184, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74556, 87306, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74757, 86830, -5183, -1, 12771));
		spawns.add(new LocationHolder(-74927, 86830, -5183, -1, 12771));
		FESTIVAL_DUSK_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** Level 64 and Below */
		spawns.add(new LocationHolder(-80010, 88128, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80113, 88066, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80220, 88066, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80359, 88128, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80467, 88267, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80467, 88436, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80381, 88639, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80278, 88577, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80142, 88577, -5183, -1, 12772));
		spawns.add(new LocationHolder(-80028, 88639, -5183, -1, 12772));
		spawns.add(new LocationHolder(-79915, 88466, -5183, -1, 12772));
		spawns.add(new LocationHolder(-79915, 88322, -5183, -1, 12772));
		FESTIVAL_DUSK_CHEST_SPAWNS.add(spawns);
		spawns.clear();
		
		/** No Level Limit */
		spawns.add(new LocationHolder(-80153, 84947, -5183, -1, 12773));
		spawns.add(new LocationHolder(-80003, 84962, -5183, -1, 12773));
		spawns.add(new LocationHolder(-79848, 84962, -5183, -1, 12773));
		spawns.add(new LocationHolder(-79742, 84947, -5183, -1, 12773));
		spawns.add(new LocationHolder(-79668, 84772, -5183, -1, 12773));
		spawns.add(new LocationHolder(-79668, 84619, -5183, -1, 12773));
		spawns.add(new LocationHolder(-79772, 84471, -5183, -1, 12773));
		spawns.add(new LocationHolder(-79888, 84414, -5183, -1, 12773));
		spawns.add(new LocationHolder(-80023, 84414, -5183, -1, 12773));
		spawns.add(new LocationHolder(-80166, 84471, -5183, -1, 12773));
		spawns.add(new LocationHolder(-80253, 84600, -5183, -1, 12773));
		spawns.add(new LocationHolder(-80253, 84780, -5183, -1, 12773));
		FESTIVAL_DUSK_CHEST_SPAWNS.add(spawns);
		spawns.clear();
	}
	
	protected FestivalManager managerInstance;
	protected ScheduledFuture<?> managerScheduledTask;
	
	protected int signsCycle = SevenSignsManager.getInstance().getCurrentCycle();
	protected int festivalCycle;
	protected long nextFestivalCycleStart;
	protected long nextFestivalStart;
	protected boolean festivalInitialized;
	protected boolean festivalInProgress;
	protected List<Integer> accumulatedBonuses; // The total bonus available (in Ancient Adena)
	
	protected Map<Integer, List<L2PcInstance>> dawnFestivalParticipants;
	protected Map<Integer, List<L2PcInstance>> duskFestivalParticipants;
	
	protected Map<Integer, List<L2PcInstance>> dawnPreviousParticipants;
	protected Map<Integer, List<L2PcInstance>> duskPreviousParticipants;
	
	private Map<Integer, Integer> dawnFestivalScores;
	private Map<Integer, Integer> duskFestivalScores;
	
	/**
	 * festivalData is essentially an instance of the seven_signs_festival table and should be treated as such. Data is initially accessed by the related Seven Signs cycle, with signsCycle representing data for the current round of Festivals. The actual table data is stored as a series of StatsSet
	 * constructs. These are accessed by the use of an offset based on the number of festivals, thus: offset = FESTIVAL_COUNT + festivalId (Data for Dawn is always accessed by offset > FESTIVAL_COUNT)
	 */
	private Map<Integer, Map<Integer, StatsSet>> festivalData;
	
	public SevenSignsFestival()
	{
		accumulatedBonuses = new ArrayList<>();
		
		dawnFestivalParticipants = new HashMap<>();
		dawnPreviousParticipants = new HashMap<>();
		dawnFestivalScores = new HashMap<>();
		
		duskFestivalParticipants = new HashMap<>();
		duskPreviousParticipants = new HashMap<>();
		duskFestivalScores = new HashMap<>();
		
		festivalData = new HashMap<>();
		
		restoreFestivalData();
		
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			LOG.info("SevenSignsFestival: Initialization bypassed due to Seal Validation in effect.");
			return;
		}
		
		startFestivalManager();
	}
	
	/**
	 * Returns the associated name (level range) to a given festival ID.
	 * @param  festivalID
	 * @return            String festivalName
	 */
	public static final String getFestivalName(int festivalID)
	{
		String festivalName;
		
		switch (festivalID)
		{
			case FESTIVAL_LEVEL_MAX_31:
				festivalName = "Level 31 or lower";
				break;
			case FESTIVAL_LEVEL_MAX_42:
				festivalName = "Level 42 or lower";
				break;
			case FESTIVAL_LEVEL_MAX_53:
				festivalName = "Level 53 or lower";
				break;
			case FESTIVAL_LEVEL_MAX_64:
				festivalName = "Level 64 or lower";
				break;
			default:
				festivalName = "No Level Limit";
				break;
		}
		
		return festivalName;
	}
	
	/**
	 * Returns the maximum allowed player level for the given festival type.
	 * @param  festivalId
	 * @return            int maxLevel
	 */
	public static final int getMaxLevelForFestival(int festivalId)
	{
		int maxLevel = 78;
		
		switch (festivalId)
		{
			case SevenSignsFestival.FESTIVAL_LEVEL_MAX_31:
				maxLevel = 31;
				break;
			case SevenSignsFestival.FESTIVAL_LEVEL_MAX_42:
				maxLevel = 42;
				break;
			case SevenSignsFestival.FESTIVAL_LEVEL_MAX_53:
				maxLevel = 53;
				break;
			case SevenSignsFestival.FESTIVAL_LEVEL_MAX_64:
				maxLevel = 64;
				break;
		}
		
		return maxLevel;
	}
	
	/**
	 * Returns true if the monster ID given is of an archer/marksman type.
	 * @param  npcId
	 * @return       boolean isArcher
	 */
	protected static final boolean isFestivalArcher(int npcId)
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
	protected static final boolean isFestivalChest(int npcId)
	{
		return ((npcId < 12764) || (npcId > 12773));
	}
	
	/**
	 * Primarily used to terminate the Festival Manager, when the Seven Signs period changes.
	 * @return ScheduledFuture festManagerScheduler
	 */
	protected final ScheduledFuture<?> getFestivalManagerSchedule()
	{
		if (managerScheduledTask == null)
		{
			startFestivalManager();
		}
		
		return managerScheduledTask;
	}
	
	/**
	 * Used to start the Festival Manager, if the current period is not Seal Validation.
	 */
	public void startFestivalManager()
	{
		// Start the Festival Manager for the first time after the server has started
		// at the specified time, then invoke it automatically after every cycle.
		setNextFestivalStart(Config.ALT_FESTIVAL_MANAGER_START + FESTIVAL_SIGNUP_TIME);
		managerScheduledTask = ThreadPoolManager.scheduleAtFixedRate(new FestivalManager(), Config.ALT_FESTIVAL_MANAGER_START, Config.ALT_FESTIVAL_CYCLE_LENGTH);
		
		LOG.info("SevenSignsFestival: The first Festival of Darkness cycle begins in " + (Config.ALT_FESTIVAL_MANAGER_START / 60000) + " minute(s).");
	}
	
	/**
	 * Restores saved festival data, basic settings from the properties file and past high score data from the database.
	 */
	protected void restoreFestivalData()
	{
		try (Connection con = DatabaseManager.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("SELECT festivalId, cabal, cycle, date, score, members " + "FROM seven_signs_festival");
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					int festivalCycle = rset.getInt("cycle");
					int festivalId = rset.getInt("festivalId");
					String cabal = rset.getString("cabal");
					
					StatsSet festivalDat = new StatsSet();
					festivalDat.set("festivalId", festivalId);
					festivalDat.set("cabal", cabal);
					festivalDat.set("cycle", festivalCycle);
					festivalDat.set("date", rset.getString("date"));
					festivalDat.set("score", rset.getInt("score"));
					festivalDat.set("members", rset.getString("members"));
					
					if (cabal.equals("dawn"))
					{
						festivalId += FESTIVAL_COUNT;
					}
					
					Map<Integer, StatsSet> tempData = festivalData.get(festivalCycle);
					
					if (tempData == null)
					{
						tempData = new HashMap<>();
					}
					
					tempData.put(festivalId, festivalDat);
					festivalData.put(festivalCycle, tempData);
				}
			}
			
			StringBuilder sbQuery = new StringBuilder(50);
			
			sbQuery.append("SELECT festival_cycle, ");
			
			for (int i = 0; i < (FESTIVAL_COUNT - 1); i++)
			{
				sbQuery.append("accumulated_bonus" + String.valueOf(i) + ", ");
			}
			
			sbQuery.append("accumulated_bonus" + String.valueOf(FESTIVAL_COUNT - 1) + " ");
			sbQuery.append("FROM seven_signs_status WHERE id=0");
			
			try (PreparedStatement ps = con.prepareStatement(sbQuery.toString());
				ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					festivalCycle = rset.getInt("festival_cycle");
					
					for (int i = 0; i < FESTIVAL_COUNT; i++)
					{
						accumulatedBonuses.add(i, rset.getInt("accumulated_bonus" + String.valueOf(i)));
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.severe("SevenSignsFestival: Failed to load configuration: " + e);
		}
	}
	
	/**
	 * Stores current festival data, basic settings to the properties file and past high score data to the database. If updateSettings = true, then all Seven Signs data is updated in the database.
	 * @param updateSettings
	 */
	public void saveFestivalData(boolean updateSettings)
	{
		LOG.info("SevenSignsFestival: Saving festival data to disk.");
		
		try (Connection con = DatabaseManager.getConnection())
		{
			PreparedStatement ps = null;
			
			for (Map<Integer, StatsSet> currCycleData : festivalData.values())
			{
				for (StatsSet festivalDat : currCycleData.values())
				{
					int festivalCycle = festivalDat.getInteger("cycle");
					int festivalId = festivalDat.getInteger("festivalId");
					String cabal = festivalDat.getString("cabal");
					
					// Try to update an existing record.
					ps = con.prepareStatement("UPDATE seven_signs_festival SET date=?, score=?, members=? WHERE cycle=? AND cabal=? AND festivalId=?");
					ps.setLong(1, Long.valueOf(festivalDat.getString("date")));
					ps.setInt(2, festivalDat.getInteger("score"));
					ps.setString(3, festivalDat.getString("members"));
					ps.setInt(4, festivalCycle);
					ps.setString(5, cabal);
					ps.setInt(6, festivalId);
					
					// If there was no record to update, assume it doesn't exist and add a new one,
					// otherwise continue with the next record to store.
					if (ps.executeUpdate() > 0)
					{
						continue;
					}
					
					ps.close();
					
					ps = con.prepareStatement("INSERT INTO seven_signs_festival (festivalId, cabal, cycle, date, score, members) VALUES (?,?,?,?,?,?)");
					ps.setInt(1, festivalId);
					ps.setString(2, cabal);
					ps.setInt(3, festivalCycle);
					ps.setLong(4, Long.valueOf(festivalDat.getString("date")));
					ps.setInt(5, festivalDat.getInteger("score"));
					ps.setString(6, festivalDat.getString("members"));
					ps.execute();
					ps.close();
				}
			}
			
			// Updates Seven Signs DB data also, so call only if really necessary.
			if (updateSettings)
			{
				SevenSignsManager.getInstance().saveSevenSignsData(null, true);
			}
		}
		catch (SQLException e)
		{
			LOG.severe("SevenSignsFestival: Failed to save configuration: " + e);
		}
	}
	
	/**
	 * Used to reset all festival data at the beginning of a new quest event period.
	 * @param updateSettings
	 */
	public void resetFestivalData(boolean updateSettings)
	{
		festivalCycle = 0;
		signsCycle = SevenSignsManager.getInstance().getCurrentCycle();
		
		// Set all accumulated bonuses back to 0.
		for (int i = 0; i < FESTIVAL_COUNT; i++)
		{
			accumulatedBonuses.set(i, 0);
		}
		
		dawnFestivalParticipants.clear();
		dawnPreviousParticipants.clear();
		dawnFestivalScores.clear();
		
		duskFestivalParticipants.clear();
		duskPreviousParticipants.clear();
		duskFestivalScores.clear();
		
		// Set up a new data set for the current cycle of festivals
		Map<Integer, StatsSet> newData = new HashMap<>();
		
		for (int i = 0; i < (FESTIVAL_COUNT * 2); i++)
		{
			int festivalId = i;
			
			if (i >= FESTIVAL_COUNT)
			{
				festivalId -= FESTIVAL_COUNT;
			}
			
			// Create a new StatsSet with "default" data for Dusk
			StatsSet tempStats = new StatsSet();
			tempStats.set("festivalId", festivalId);
			tempStats.set("cycle", signsCycle);
			tempStats.set("date", "0");
			tempStats.set("score", 0);
			tempStats.set("members", "");
			
			if (i >= FESTIVAL_COUNT)
			{
				tempStats.set("cabal", CabalType.DAWN.getShortName());
			}
			else
			{
				tempStats.set("cabal", CabalType.DUSK.getShortName());
			}
			
			newData.put(i, tempStats);
		}
		
		// Add the newly created cycle data to the existing festival data, and
		// subsequently save it to the database.
		festivalData.put(signsCycle, newData);
		
		saveFestivalData(updateSettings);
		
		// Remove any unused blood offerings from online players.
		for (L2PcInstance player : L2World.getInstance().getAllPlayers())
		{
			ItemInstance bloodOfferings = player.getInventory().getItemById(FESTIVAL_OFFERING_ID);
			
			if (bloodOfferings != null)
			{
				player.getInventory().destroyItem("SevenSigns", bloodOfferings, null, false);
			}
		}
		
		LOG.info("SevenSignsFestival: Reinitialized engine for next competition period.");
	}
	
	public final int getCurrentFestivalCycle()
	{
		return festivalCycle;
	}
	
	public final boolean isFestivalInitialized()
	{
		return festivalInitialized;
	}
	
	public final boolean isFestivalInProgress()
	{
		return festivalInProgress;
	}
	
	public void setNextCycleStart()
	{
		nextFestivalCycleStart = System.currentTimeMillis() + Config.ALT_FESTIVAL_CYCLE_LENGTH;
	}
	
	public void setNextFestivalStart(long milliFromNow)
	{
		nextFestivalStart = System.currentTimeMillis() + milliFromNow;
	}
	
	public final int getMinsToNextCycle()
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			return -1;
		}
		
		return Math.round((nextFestivalCycleStart - System.currentTimeMillis()) / 60000);
	}
	
	public final int getMinsToNextFestival()
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			return -1;
		}
		
		return Math.round((nextFestivalStart - System.currentTimeMillis()) / 60000) + 1;
	}
	
	public final String getTimeToNextFestivalStr()
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			return "<font color=\"FF0000\">This is the Seal Validation period. Festivals will resume next week.</font>";
		}
		
		return "<font color=\"FF0000\">The next festival will begin in " + getMinsToNextFestival() + " minute(s).</font>";
	}
	
	/**
	 * Returns the current festival ID and oracle ID that the specified player is in, but will return the default of {-1, -1} if the player is not found as a participant.
	 * @param  player
	 * @return        int[] playerFestivalInfo
	 */
	public final int[] getFestivalForPlayer(L2PcInstance player)
	{
		int[] playerFestivalInfo =
		{
			-1,
			-1
		};
		int festivalId = 0;
		
		while (festivalId < FESTIVAL_COUNT)
		{
			List<L2PcInstance> participants = dawnFestivalParticipants.get(festivalId);
			
			// If there are no participants in this festival, move on to the next.
			if ((participants != null) && participants.contains(player))
			{
				playerFestivalInfo[0] = CabalType.DAWN.ordinal();
				playerFestivalInfo[1] = festivalId;
				
				return playerFestivalInfo;
			}
			
			festivalId++;
			
			participants = duskFestivalParticipants.get(festivalId);
			
			if ((participants != null) && participants.contains(player))
			{
				playerFestivalInfo[0] = CabalType.DUSK.ordinal();
				playerFestivalInfo[1] = festivalId;
				
				return playerFestivalInfo;
			}
			
			festivalId++;
		}
		
		// Return default data if the player is not found as a participant.
		return playerFestivalInfo;
	}
	
	public final boolean isParticipant(L2PcInstance player)
	{
		if (SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			return false;
		}
		
		if (managerInstance == null)
		{
			return false;
		}
		
		for (List<L2PcInstance> participants : dawnFestivalParticipants.values())
		{
			if ((participants != null) && participants.contains(player))
			{
				return true;
			}
		}
		
		for (List<L2PcInstance> participants : duskFestivalParticipants.values())
		{
			if ((participants != null) && participants.contains(player))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public final List<L2PcInstance> getParticipants(CabalType oracle, int festivalId)
	{
		if (oracle == CabalType.DAWN)
		{
			return dawnFestivalParticipants.get(festivalId);
		}
		
		return duskFestivalParticipants.get(festivalId);
	}
	
	public final List<L2PcInstance> getPreviousParticipants(CabalType oracle, int festivalId)
	{
		if (oracle == CabalType.DAWN)
		{
			return dawnPreviousParticipants.get(festivalId);
		}
		
		return duskPreviousParticipants.get(festivalId);
	}
	
	public void setParticipants(CabalType oracle, int festivalId, Party festivalParty)
	{
		List<L2PcInstance> participants = new ArrayList<>();
		
		if (festivalParty != null)
		{
			participants = festivalParty.getMembers();
			
			if (Config.DEBUG)
			{
				LOG.info("SevenSignsFestival: " + festivalParty.getMembers().toString() + " have signed up to the " + oracle.getShortName() + " " + getFestivalName(festivalId) + " festival.");
			}
		}
		
		if (oracle == CabalType.DAWN)
		{
			dawnFestivalParticipants.put(festivalId, participants);
		}
		else
		{
			duskFestivalParticipants.put(festivalId, participants);
		}
	}
	
	public void updateParticipants(L2PcInstance player, Party festivalParty)
	{
		if (!isParticipant(player))
		{
			return;
		}
		
		final int[] playerFestInfo = getFestivalForPlayer(player);
		final CabalType oracle = CabalType.values()[playerFestInfo[0]];
		final int festivalId = playerFestInfo[1];
		
		if (festivalId > -1)
		{
			if (festivalInitialized)
			{
				SevenSignsDarknessFestival festivalInst = managerInstance.getFestivalInstance(oracle, festivalId);
				
				if (festivalParty == null)
				{
					for (L2PcInstance partyMember : getParticipants(oracle, festivalId))
					{
						festivalInst.relocatePlayer(partyMember, true);
					}
				}
				else
				{
					festivalInst.relocatePlayer(player, true);
				}
			}
			
			setParticipants(oracle, festivalId, festivalParty);
		}
	}
	
	public final int getFinalScore(CabalType oracle, int festivalId)
	{
		if (oracle == CabalType.DAWN)
		{
			return dawnFestivalScores.get(festivalId);
		}
		
		return duskFestivalScores.get(festivalId);
	}
	
	public final int getHighestScore(CabalType oracle, int festivalId)
	{
		return getHighestScoreData(oracle, festivalId).getInteger("score");
	}
	
	/**
	 * Returns a stats set containing the highest score <b>this cycle</b> for the the specified cabal and associated festival ID.
	 * @param  oracle
	 * @param  festivalId
	 * @return            StatsSet festivalDat
	 */
	public final StatsSet getHighestScoreData(CabalType oracle, int festivalId)
	{
		int offsetId = festivalId;
		
		if (oracle == CabalType.DAWN)
		{
			offsetId += 5;
		}
		
		// Attempt to retrieve existing score data (if found), otherwise create a
		// new blank data set and display a console warning.
		StatsSet currData = null;
		
		try
		{
			currData = festivalData.get(signsCycle).get(offsetId);
		}
		catch (Exception e)
		{
			currData = new StatsSet();
			currData.set("score", 0);
			currData.set("members", "");
			
			if (Config.DEBUG)
			{
				LOG.info("SevenSignsFestival: Data missing for " + oracle.getName() + ", FestivalID = " + festivalId + " (Current Cycle " + signsCycle + ")");
			}
		}
		
		return currData;
	}
	
	/**
	 * Returns a stats set containing the highest ever recorded score data for the specified festival.
	 * @param  festivalId
	 * @return            StatsSet result
	 */
	public final StatsSet getOverallHighestScoreData(int festivalId)
	{
		StatsSet result = null;
		int highestScore = 0;
		
		for (Map<Integer, StatsSet> currCycleData : festivalData.values())
		{
			for (StatsSet currFestData : currCycleData.values())
			{
				int currFestID = currFestData.getInteger("festivalId");
				int festivalScore = currFestData.getInteger("score");
				
				if (currFestID != festivalId)
				{
					continue;
				}
				
				if (festivalScore > highestScore)
				{
					highestScore = festivalScore;
					result = currFestData;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Set the final score details for the last participants of the specified festival data. Returns <b>true</b> if the score is higher than that previously recorded <b>this cycle</b>.
	 * @param  player
	 * @param  oracle
	 * @param  festivalId
	 * @param  offeringScore
	 * @return               boolean isHighestScore
	 */
	public boolean setFinalScore(L2PcInstance player, CabalType oracle, int festivalId, int offeringScore)
	{
		int currDawnHighScore = getHighestScore(CabalType.DAWN, festivalId);
		int currDuskHighScore = getHighestScore(CabalType.DUSK, festivalId);
		
		int thisCabalHighScore = 0;
		int otherCabalHighScore = 0;
		
		if (oracle == CabalType.DAWN)
		{
			thisCabalHighScore = currDawnHighScore;
			otherCabalHighScore = currDuskHighScore;
			
			dawnFestivalScores.put(festivalId, offeringScore);
		}
		else
		{
			thisCabalHighScore = currDuskHighScore;
			otherCabalHighScore = currDawnHighScore;
			
			duskFestivalScores.put(festivalId, offeringScore);
		}
		
		StatsSet currFestData = getHighestScoreData(oracle, festivalId);
		
		// Check if this is the highest score for this level range so far for the player's cabal.
		if (offeringScore > thisCabalHighScore)
		{
			// If the current score is greater than that for the other cabal,
			// then they already have the points from this festival.
			if (thisCabalHighScore > otherCabalHighScore)
			{
				return false;
			}
			
			List<String> partyMembers = new ArrayList<>();
			
			// Record a string list of the party members involved.
			getPreviousParticipants(oracle, festivalId).forEach(p -> partyMembers.add(p.getName()));
			
			// Update the highest scores and party list.
			currFestData.set("date", String.valueOf(System.currentTimeMillis()));
			currFestData.set("score", offeringScore);
			currFestData.set("members", Util.implodeString(partyMembers, ","));
			
			// Only add the score to the cabal's overall if it's higher than the other cabal's score.
			if (offeringScore > otherCabalHighScore)
			{
				int contribPoints = FESTIVAL_LEVEL_SCORES[festivalId];
				int subtractPoints;
				
				if (otherCabalHighScore > 0)
				{
					subtractPoints = FESTIVAL_LEVEL_SCORES[festivalId];
				}
				else
				{
					subtractPoints = 0;
				}
				
				// Give this cabal the festival points, while deducting them from the other.
				SevenSignsManager.getInstance().addFestivalScore(oracle, contribPoints, subtractPoints);
				
				// if (Config.DEBUG)
				LOG.info("SevenSignsFestival: This is the highest score overall so far for the " + getFestivalName(festivalId) + " festival!");
			}
			
			saveFestivalData(true);
			
			return true;
		}
		
		return false;
	}
	
	public final int getAccumulatedBonus(int festivalId)
	{
		return accumulatedBonuses.get(festivalId);
	}
	
	public final int getTotalAccumulatedBonus()
	{
		int totalAccumBonus = 0;
		
		for (int accumBonus : accumulatedBonuses)
		{
			totalAccumBonus += accumBonus;
		}
		
		return totalAccumBonus;
	}
	
	public void addAccumulatedBonus(int festivalId, int stoneType, int stoneAmount)
	{
		int eachStoneBonus = 0;
		
		switch (stoneType)
		{
			case SevenSignsManager.SEAL_STONE_BLUE_ID:
				eachStoneBonus = SevenSignsManager.SEAL_STONE_BLUE_VALUE;
				break;
			case SevenSignsManager.SEAL_STONE_GREEN_ID:
				eachStoneBonus = SevenSignsManager.SEAL_STONE_GREEN_VALUE;
				break;
			case SevenSignsManager.SEAL_STONE_RED_ID:
				eachStoneBonus = SevenSignsManager.SEAL_STONE_RED_VALUE;
				break;
		}
		
		int newTotalBonus = accumulatedBonuses.get(festivalId) + (stoneAmount * eachStoneBonus);
		accumulatedBonuses.set(festivalId, newTotalBonus);
	}
	
	/**
	 * Calculate and return the proportion of the accumulated bonus for the festival where the player was in the winning party, if the winning party's cabal won the event. The accumulated bonus is then updated, with the player's share deducted.
	 * @param  player
	 * @return        playerBonus (the share of the bonus for the party)
	 */
	public final int distribAccumulatedBonus(L2PcInstance player)
	{
		int playerBonus = 0;
		String playerName = player.getName();
		CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
		
		if (playerCabal != SevenSignsManager.getInstance().getCabalHighestScore())
		{
			return 0;
		}
		
		if (festivalData.containsKey(signsCycle))
		{
			for (StatsSet festivalData : festivalData.get(signsCycle).values())
			{
				if (festivalData.getString("members").indexOf(playerName) > -1)
				{
					int festivalId = festivalData.getInteger("festivalId");
					int numPartyMembers = festivalData.getString("members").split(",").length;
					int totalAccumBonus = accumulatedBonuses.get(festivalId);
					
					playerBonus = totalAccumBonus / numPartyMembers;
					accumulatedBonuses.set(festivalId, totalAccumBonus - playerBonus);
					break;
				}
			}
		}
		
		return playerBonus;
	}
	
	/**
	 * Used to send a "shout" message to all players currently present in an Oracle. Primarily used for Festival Guide and Witch related speech.
	 * @param senderName
	 * @param message
	 */
	public void sendMessageToAll(String senderName, String message)
	{
		// dawnChatGuide
		L2World.getInstance().getAllObjects().stream().filter(o -> (o instanceof L2Npc) && (((L2Npc) o).getId() == 8127)).forEach(o -> ((L2Npc) o).broadcastPacket(new CreatureSay((L2Npc) o, SayType.SHOUT, senderName, message)));
		// duskChatGuide
		L2World.getInstance().getAllObjects().stream().filter(o -> (o instanceof L2Npc) && (((L2Npc) o).getId() == 8137)).forEach(o -> ((L2Npc) o).broadcastPacket(new CreatureSay((L2Npc) o, SayType.SHOUT, senderName, message)));
	}
	
	/**
	 * Basically a wrapper-call to signal to increase the challenge of the specified festival.
	 * @param  oracle
	 * @param  festivalId
	 * @return            boolean isChalIncreased
	 */
	public final boolean increaseChallenge(CabalType oracle, int festivalId)
	{
		return managerInstance.getFestivalInstance(oracle, festivalId).increaseChallenge();
	}
	
	/**
	 * The FestivalManager class is the main runner of all the festivals. It is used for easier integration and management of all running festivals.
	 * @author Tempy
	 */
	private class FestivalManager implements Runnable
	{
		protected Map<Integer, SevenSignsDarknessFestival> festivalInstances;
		
		public FestivalManager()
		{
			festivalInstances = new HashMap<>();
			managerInstance = this;
			
			// Increment the cycle counter.
			festivalCycle++;
			
			// Set the next start timers.
			setNextCycleStart();
			setNextFestivalStart(Config.ALT_FESTIVAL_CYCLE_LENGTH - FESTIVAL_SIGNUP_TIME);
		}
		
		@Override
		public synchronized void run()
		{
			// The manager shouldn't be running if Seal Validation is in effect.
			if (SevenSignsManager.getInstance().isSealValidationPeriod())
			{
				return;
			}
			
			// If the next period is due to start before the end of this
			// festival cycle, then don't run it.
			if (SevenSignsManager.getInstance().getMilliToPeriodChange() < Config.ALT_FESTIVAL_CYCLE_LENGTH)
			{
				return;
			}
			
			if (Config.DEBUG)
			{
				LOG.info("SevenSignsFestival: Festival manager initialized. Those wishing to participate have " + getMinsToNextFestival() + " minute(s) to sign up.");
			}
			
			sendMessageToAll("Festival Guide", "The main event will start in " + getMinsToNextFestival() + " minutes. Please register now.");
			
			// Stand by until the allowed signup period has elapsed.
			try
			{
				wait(FESTIVAL_SIGNUP_TIME);
			}
			catch (InterruptedException e)
			{
			}
			
			// Clear past participants, they can no longer register their score if not done so already.
			dawnPreviousParticipants.clear();
			duskPreviousParticipants.clear();
			
			// Get rid of random monsters that avoided deletion after last festival
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.unspawnMobs();
			}
			
			/* INITIATION */
			// Set the festival timer to 0, as it is just beginning.
			long elapsedTime = 0;
			
			// Create the instances for the festivals in both Oracles,
			// but only if they have participants signed up for them.
			for (int i = 0; i < FESTIVAL_COUNT; i++)
			{
				if (duskFestivalParticipants.containsKey(i))
				{
					festivalInstances.put(10 + i, new SevenSignsDarknessFestival(CabalType.DUSK, i));
				}
				
				if (dawnFestivalParticipants.containsKey(i))
				{
					festivalInstances.put(20 + i, new SevenSignsDarknessFestival(CabalType.DAWN, i));
				}
			}
			
			// Prevent future signups while festival is in progress.
			festivalInitialized = true;
			
			setNextFestivalStart(Config.ALT_FESTIVAL_CYCLE_LENGTH);
			sendMessageToAll("Festival Guide", "The main event is now starting.");
			
			// Stand by for a short length of time before starting the festival.
			try
			{
				wait(Config.ALT_FESTIVAL_FIRST_SPAWN);
			}
			catch (InterruptedException e)
			{
			}
			
			elapsedTime = Config.ALT_FESTIVAL_FIRST_SPAWN;
			
			// Participants can now opt to increase the challenge, if desired.
			festivalInProgress = true;
			
			/* PROPOGATION */
			// Sequentially set all festivals to begin, spawn the Festival Witch and notify participants.
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.festivalStart();
				festivalInst.sendMessageToParticipants("The festival is about to begin!");
			}
			
			// After a short time period, move all idle spawns to the center of the arena.
			try
			{
				wait(Config.ALT_FESTIVAL_FIRST_SWARM - Config.ALT_FESTIVAL_FIRST_SPAWN);
			}
			catch (InterruptedException e)
			{
			}
			
			elapsedTime += Config.ALT_FESTIVAL_FIRST_SWARM - Config.ALT_FESTIVAL_FIRST_SPAWN;
			
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.moveMonstersToCenter();
			}
			
			// Stand by until the time comes for the second spawn.
			try
			{
				wait(Config.ALT_FESTIVAL_SECOND_SPAWN - Config.ALT_FESTIVAL_FIRST_SWARM);
			}
			catch (InterruptedException e)
			{
			}
			
			// Spawn an extra set of monsters (archers) on the free platforms with
			// a faster respawn when killed.
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.spawnFestivalMonsters(FESTIVAL_DEFAULT_RESPAWN / 2, 2);
				festivalInst.sendMessageToParticipants("The festival will end in " + ((Config.ALT_FESTIVAL_LENGTH - Config.ALT_FESTIVAL_SECOND_SPAWN) / 60000) + " minute(s).");
			}
			
			elapsedTime += Config.ALT_FESTIVAL_SECOND_SPAWN - Config.ALT_FESTIVAL_FIRST_SWARM;
			
			// After another short time period, again move all idle spawns to the center of the arena.
			try
			{
				wait(Config.ALT_FESTIVAL_SECOND_SWARM - Config.ALT_FESTIVAL_SECOND_SPAWN);
			}
			catch (InterruptedException e)
			{
			}
			
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.moveMonstersToCenter();
			}
			
			elapsedTime += Config.ALT_FESTIVAL_SECOND_SWARM - Config.ALT_FESTIVAL_SECOND_SPAWN;
			
			// Stand by until the time comes for the chests to be spawned.
			try
			{
				wait(Config.ALT_FESTIVAL_CHEST_SPAWN - Config.ALT_FESTIVAL_SECOND_SWARM);
			}
			catch (InterruptedException e)
			{
			}
			
			// Spawn the festival chests, which enable the team to gain greater rewards
			// for each chest they kill.
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.spawnFestivalMonsters(FESTIVAL_DEFAULT_RESPAWN, 3);
				festivalInst.sendMessageToParticipants("The chests have spawned! Be quick, the festival will end soon.");
			}
			
			elapsedTime += Config.ALT_FESTIVAL_CHEST_SPAWN - Config.ALT_FESTIVAL_SECOND_SWARM;
			
			// Stand by and wait until it's time to end the festival.
			try
			{
				wait(Config.ALT_FESTIVAL_LENGTH - elapsedTime);
			}
			catch (InterruptedException e)
			{
			}
			
			// Participants can no longer opt to increase the challenge, as the festival will soon close.
			festivalInProgress = false;
			
			/* TERMINATION */
			// Sequentially begin the ending sequence for all running festivals.
			for (SevenSignsDarknessFestival festivalInst : festivalInstances.values())
			{
				festivalInst.festivalEnd();
			}
			
			// Clear the participants list for the next round of signups.
			dawnFestivalParticipants.clear();
			duskFestivalParticipants.clear();
			
			// Allow signups for the next festival cycle.
			festivalInitialized = false;
			
			sendMessageToAll("Festival Witch", "That will do! I'll move you to the outside soon.");
		}
		
		/**
		 * Returns the running instance of a festival for the given Oracle and festivalID. <BR>
		 * A <B>null</B> value is returned if there are no participants in that festival.
		 * @param  oracle
		 * @param  festivalId
		 * @return            DarknessFestival festivalInst
		 */
		public final SevenSignsDarknessFestival getFestivalInstance(CabalType oracle, int festivalId)
		{
			if (!isFestivalInitialized())
			{
				return null;
			}
			
			/*
			 * Compute the offset if a Dusk instance is required. ID: 0 1 2 3 4 Dusk 1: 10 11 12 13 14 Dawn 2: 20 21 22 23 24
			 */
			
			festivalId += (oracle == CabalType.DUSK) ? 10 : 20;
			return festivalInstances.get(festivalId);
		}
		
		/**
		 * Returns the number of currently running festivals <b>WITH</b> participants.
		 * @return int Count
		 */
		@SuppressWarnings("unused")
		public final int getInstanceCount()
		{
			return festivalInstances.size();
		}
	}
	
	public void putDawnPreviousParticipants(int key, List<L2PcInstance> val)
	{
		dawnPreviousParticipants.put(key, val);
	}
	
	public void putDuskPreviousParticipants(int key, List<L2PcInstance> val)
	{
		duskPreviousParticipants.put(key, val);
	}
	
	public static SevenSignsFestival getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SevenSignsFestival INSTANCE = new SevenSignsFestival();
	}
}
