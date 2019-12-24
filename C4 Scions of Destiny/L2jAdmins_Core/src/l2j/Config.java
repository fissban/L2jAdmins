package l2j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.holder.SiegeSpawnHolder;
import l2j.util.L2Properties;

/**
 * This class contains global server configuration.<br>
 * @author mkizub
 */
public final class Config
{
	private static final Logger LOG = Logger.getLogger(Config.class.getName());
	
	public static final String EOL = System.getProperty("line.separator");
	
	// Collection of all the game settings,
	// these are captured from L2Properties to be read each configuration.
	private static final Map<String, String> ALL_CONFIGS = new LinkedHashMap<>();
	
	// --------------------------------------------------
	// L2J Property File Definitions
	// --------------------------------------------------
	public static final String FILE_TELNET = "./config/telnet.properties";
	
	private static final String FILE_SERVER_CONFIG = "./config/server.properties";
	private static final String FILE_LOGIN_CONFIG = "./config/loginserver.properties";
	private static final String FILE_SIEGE_CONFIG = "./config/siege.properties";
	private static final String FILE_GM_CONFIG = "./config/gm.properties";
	private static final String FILE_OPTIONS_CONFIG = "./config/options.properties";
	private static final String FILE_IDFACTORY_CONFIG = "./config/idfactory.properties";
	private static final String FILE_RATES_CONFIG = "./config/rates.properties";
	private static final String FILE_PVP_CONFIG = "./config/pvp.properties";
	private static final String FILE_HEXID = "./config/hexid.txt";
	private static final String FILE_CLANHALL_CONFIG = "./config/clanhall.properties";
	private static final String FILE_CHARACTER_CONFIG = "./config/character.properties";
	private static final String FILE_NPC_CONFIG = "./config/npc.properties";
	private static final String FILE_EVENT_RETAIL_CONFIG = "./config/eventRetail.properties";
	private static final String FILE_SECURITY_CONFIG = "./config/security.properties";
	public static final String FILE_GEOENGINE = "./config/geoengine.properties";
	
	// ------------------------------------------------ //
	// XXX siege.properties //
	// ------------------------------------------------ //
	public static int SIEGE_ATTACKER_MAX_CLANS;
	public static int SIEGE_ATTACKER_RESPAWN_DELAY;
	public static int SIEGE_DEFENDER_MAX_CLANS;// TODO unused
	public static int SIEGE_FLAG_MAX_COUNT;
	public static int SIEGE_MIN_CLAN_LVL;
	public static int SIEGE_LENGTH;
	public final static Map<Integer, List<SiegeSpawnHolder>> SIEGE_ARTEFACT_SPAWN_LIST = new HashMap<>();
	public final static Map<Integer, List<SiegeSpawnHolder>> SIEGE_CONTROL_TOWER_SPAWN_LIST = new HashMap<>();
	
	// ------------------------------------------------ //
	// XXX character.properties
	// ------------------------------------------------ //
	/** Maximum character Evasion */
	public static int MAX_EVASION;
	/** Maximum character running speed */
	public static int MAX_RUN_SPEED;
	/** Maximum character Physical Critical Rate */
	public static int MAX_PCRIT_RATE;
	/** Maximum character Magic Critical Rate */
	public static int MAX_MCRIT_RATE;
	/** Maximum character Physical Attack Speed */
	public static int MAX_PATK_SPEED;
	/** Maximum character Magic Attack Speed */
	public static int MAX_MATK_SPEED;
	/** Maximum inventory slots limits for non dwarf characters */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	/** Maximum inventory slots limits for dwarf characters */
	public static int INVENTORY_MAXIMUM_DWARF;
	/** Maximum inventory slots limits for GM */
	public static int INVENTORY_MAXIMUM_GM;
	/** Maximum inventory slots limits for pet */
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	/** Maximum inventory slots limits for non dwarf warehouse */
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	/** Maximum inventory slots limits for dwarf warehouse */
	public static int WAREHOUSE_SLOTS_DWARF;
	/** Maximum inventory slots limits for clan warehouse */
	public static int WAREHOUSE_SLOTS_CLAN;
	/** Maximum inventory slots limits for freight */
	public static int FREIGHT_SLOTS;
	/** Alternative auto skill learning */
	public static boolean AUTO_LEARN_SKILLS;
	/** Alternative auto skill learning for 3rd class */
	public static boolean AUTO_LEARN_3RD_SKILLS;
	/** Cancel attack bow by hit */
	public static boolean ALT_GAME_CANCEL_BOW;
	/** Cancel cast by hit */
	public static boolean ALT_GAME_CANCEL_CAST;
	/** Alternative Perfect shield defense rate */
	public static int ALT_PERFECT_SHLD_BLOCK;
	/** Alternative freight modes - Freights can be withdrawed from any village */
	/** Alternative gaming - loss of XP on death */
	public static boolean ALT_GAME_DELEVEL;
	/** Alternative Weight Limit */
	public static double ALT_WEIGHT_LIMIT;
	/** Alternative gaming - magic dmg failures */
	public static boolean ALT_GAME_MAGICFAILURES;
	/** Spell Book needed to learn skill */
	public static boolean SP_BOOK_NEEDED;
	/** Spell Book needet to enchant skill */
	public static boolean ES_SP_BOOK_NEEDED;
	/** Alternative gaming - add more or less than 3 sub-classes. */
	public static int ALT_MAX_SUBCLASS;
	/** Alternative gaming - allow sub-class addition without quest completion. */
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	/** Alternative gaming - allow/disallow tutorial. */
	public static boolean ALT_ENABLE_TUTORIAL;
	/** Max number of buffs */
	public static int BUFFS_MAX_AMOUNT;
	/** Store skills cool time on char exit/relogin */
	public static boolean STORE_SKILL_COOLTIME;
	/**
	 * Allow lesser effects to be canceled if stronger effects are used when effects of the same stack group are used.<br>
	 * New effects that are added will be canceled if they are of lesser priority to the old one.
	 */
	public static boolean EFFECT_CANCELING;
	/** Allow player with karma to be killed in peace zone ? */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	/** Allow player with karma to shop ? */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	/** Allow player with karma to use gatekeepers ? */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
	/** Allow player with karma to use SOE or Return skill ? */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	/** Allow player with karma to trade ? */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	/** Allow player with karma to use warehouse ? */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	/** Accept auto-loot ? */
	public static boolean AUTO_LOOT;
	/** Accept auto-loot for RBs ? */
	public static boolean AUTO_LOOT_RAIDS;
	public static int UNSTUCK_INTERVAL;
	/** Player Protection control */
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	/** Percent HP is restore on respawn */
	public static double RESPAWN_RESTORE_HP;
	/** Allow randomizing of the respawn point in towns. */
	public static boolean RESPAWN_RANDOM_ENABLED;
	/** The maximum offset from the base respawn point to allow. */
	public static int RESPAWN_RANDOM_MAX_OFFSET;
	/** Maximum number of available slots for pvt stores (sell/buy) - Dwarfs */
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	/** Maximum number of available slots for pvt stores (sell/buy) - Others */
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	/** Number of members needed to request a clan war */
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	/** Number of days before joining a new clan */
	public static int ALT_CLAN_JOIN_DAYS;
	/** Number of days before creating a new clan */
	public static int ALT_CLAN_CREATE_DAYS;
	/** Number of days it takes to dissolve a clan */
	public static int ALT_CLAN_DISSOLVE_DAYS;
	/** Number of days it takes to dissolve a clan again */
	public static int ALT_RECOVERY_PENALTY;
	/** Number of days before joining a new alliance when clan voluntarily leave an alliance */
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	/** Number of days before joining a new alliance when clan was dismissed from an alliance */
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	/** Number of days before accepting a new clan for alliance when clan was dismissed from an alliance */
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	/** Number of days before creating a new alliance when dissolved an alliance */
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	/** Maximum number of clans in ally */
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	/** Multiplier for character HP regeneration */
	public static double HP_REGEN_MULTIPLIER;
	/** Multiplier for character MP regeneration */
	public static double MP_REGEN_MULTIPLIER;
	/** Multiplier for character CP regeneration */
	public static double CP_REGEN_MULTIPLIER;
	/** Alternative game crafting */
	public static boolean ALT_GAME_CREATION;
	/** Alternative game crafting speed multiplier - default 0 (fastest but still not instant) */
	public static double ALT_GAME_CREATION_SPEED;
	/** Alternative game crafting XP rate multiplier - default 1 */
	public static double ALT_GAME_CREATION_XP_RATE;
	/** Alternative game crafting SP rate multiplier - default 1 */
	public static double ALT_GAME_CREATION_SP_RATE;
	/** Enable modifying skill duration */
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	/** Skill duration list */
	public static Map<Integer, Integer> SKILL_DURATION_LIST = new HashMap<>();
	/** Chance that an item will successfully be enchanted */
	public static int ENCHANT_CHANCE_WEAPON;
	public static int ENCHANT_CHANCE_ARMOR;
	public static int ENCHANT_CHANCE_JEWELRY;
	public static int BLESSED_ENCHANT_CHANCE_WEAPON;
	public static int BLESSED_ENCHANT_CHANCE_ARMOR;
	public static int BLESSED_ENCHANT_CHANCE_JEWELRY;
	/** Maximum level of enchantment */
	public static int ENCHANT_MAX_WEAPON;
	public static int ENCHANT_MAX_ARMOR;
	public static int ENCHANT_MAX_JEWELRY;
	/** maximum level of safe enchantment for normal items */
	public static int ENCHANT_SAFE_MAX;
	/** maximum level of safe enchantment for full body armor */
	public static int ENCHANT_SAFE_MAX_FULL;
	/** Crafting Enabled? */
	public static boolean IS_CRAFTING_ENABLED;
	/** Recipe book limits */
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	/** Alternative gaming - allow free teleporting around the world. */
	public static boolean ALT_GAME_FREE_TELEPORT;
	
	public static boolean LOG_VERY_HIGH_DAMAGE;
	public static int LOG_DMG;
	// ------------------------------------------------ //
	// XXX npc.properties
	// ------------------------------------------------ //
	/** View npc stats/drop by shift-clicking it for nongm-players */
	public static boolean ALT_GAME_VIEWNPC;
	public static boolean ALT_MOB_AGGRO_IN_PEACEZONE;
	/** Alternative game - use tiredness, instead of CP */
	public static boolean ALT_GAME_TIREDNESS;
	/** Announce mammon spawn ? */
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_GAME_FREIGHTS;
	/** Alternative freight modes - Sets the price value for each freightened item */
	public static int ALT_GAME_FREIGHT_PRICE;
	/** Minimal time between 2 animations of a NPC */
	public static int MIN_NPC_ANIMATION;
	/** Maximal time between 2 animations of a NPC */
	public static int MAX_NPC_ANIMATION;
	/** Minimal time between animations of a monster */
	public static int MIN_MONSTER_ANIMATION;
	/** Maximal time between animations of a monster */
	public static int MAX_MONSTER_ANIMATION;
	/** Knownlist update time interval */
	public static int KNOWNLIST_UPDATE_INTERVAL;
	/** Show L2Monster level and aggro ? */
	public static boolean SHOW_NPC_LVL;
	/** Disable the use of guards against aggressive monsters ? */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	/** Multiplier for Raid boss HP regeneration */
	public static double RAID_HP_REGEN_MULTIPLIER;
	/** Multiplier for Raid boss MP regeneration */
	public static double RAID_MP_REGEN_MULTIPLIER;
	/** Multiplier for Raid boss power defense multiplier */
	public static double RAID_PDEFENCE_MULTIPLIER;
	/** Multiplier for Raid boss magic defense multiplier */
	public static double RAID_MDEFENCE_MULTIPLIER;
	/** Raid Boss Min Spawn Timer */
	public static double RAID_MINION_RESPAWN_TIMER;
	/** Mulitplier for Raid boss minimum time respawn */
	public static double RAID_MIN_RESPAWN_MULTIPLIER;
	/** Mulitplier for Raid boss maximum time respawn */
	public static double RAID_MAX_RESPAWN_MULTIPLIER;
	/** List of NPCs that rent pets */
	public static List<Integer> LIST_PET_RENT_NPC = new ArrayList<>();
	/** Allow Wyvern Upgrader ? */
	public static boolean ALLOW_WYVERN_UPGRADER;
	
	// ------------------------------------------------ //
	// XXX eventRetail.properties
	// ------------------------------------------------ //
	// dimensional rift
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static double RIFT_BOSS_ROOM_TIME_MUTIPLY;
	
	/** Olympiad */
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_OLY_WAIT_BATTLE;
	public static int ALT_OLY_WAIT_END;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static Map<Integer, Integer> ALT_OLY_CLASSED_REWARD;
	public static Map<Integer, Integer> ALT_OLY_NONCLASSED_REWARD;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	
	/** Minimum number of player to participate in SevenSigns Festival */
	// manor
	/** Manor Refresh Starting time */
	public static int ALT_MANOR_REFRESH_TIME;
	/** Manor Refresh Min */
	public static int ALT_MANOR_REFRESH_MIN;
	/** Manor Next Period Approve Starting time */
	public static int ALT_MANOR_APPROVE_TIME;
	/** Manor Next Period Approve Min */
	public static int ALT_MANOR_APPROVE_MIN;
	/** Manor Maintenance Time */
	public static int ALT_MANOR_MAINTENANCE_PERIOD;
	/** Manor Save All Actions */
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	/** Manor Save Period Rate */
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	// lotery
	/** Initial Lottery prize */
	public static int ALT_LOTTERY_PRIZE;
	/** Lottery Ticket Price */
	public static int ALT_LOTTERY_TICKET_PRICE;
	/** What part of jackpot amount should receive characters who pick 5 wining numbers */
	public static double ALT_LOTTERY_5_NUMBER_RATE;
	/** What part of jackpot amount should receive characters who pick 4 wining numbers */
	public static double ALT_LOTTERY_4_NUMBER_RATE;
	/** What part of jackpot amount should receive characters who pick 3 wining numbers */
	public static double ALT_LOTTERY_3_NUMBER_RATE;
	/** How much adena receive characters who pick two or less of the winning number */
	public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	// seven sign
	/** Alternative gaming - player must be in a castle-owning clan or ally to sign up for Dawn. */
	public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
	/** Alternative gaming - allow clan-based castle ownage check rather than ally-based. */
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	/** Maximum of player contrib during Festival */
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	/** Festival Manager start time. */
	public static long ALT_FESTIVAL_MANAGER_START;
	/** Festival Length */
	public static long ALT_FESTIVAL_LENGTH;
	/** Festival Cycle Length */
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	/** Festival First Spawn */
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	/** Festival First Swarm */
	public static long ALT_FESTIVAL_FIRST_SWARM;
	/** Festival Second Spawn */
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	/** Festival Second Swarm */
	public static long ALT_FESTIVAL_SECOND_SWARM;
	/** Festival Chest Spawn */
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	
	// ------------------------------------------------ //
	// XXX server.properties
	// ------------------------------------------------ //
	/** Host name of the Game Server */
	public static String GAMESERVER_HOSTNAME;
	/** Game Server ports */
	public static int PORT_GAME;
	/** External Host name */
	public static String EXTERNAL_HOSTNAME;
	/** Game Server login port */
	public static int GAME_SERVER_LOGIN_PORT;
	/** Game Server login Host */
	public static String GAME_SERVER_LOGIN_HOST;
	/** ID for request to the server */
	public static int REQUEST_ID;
	/** Accept alternate ID for server ? */
	public static boolean ACCEPT_ALTERNATE_ID;
	/** Driver to access to database */
	public static String DATABASE_DRIVER;
	/** Path to access to database */
	public static String DATABASE_URL;
	/** Database login */
	public static String DATABASE_LOGIN;
	/** Database password */
	public static String DATABASE_PASSWORD;
	/** Datapack root directory */
	public static File DATAPACK_ROOT = new File(".");
	/** Maximum number of characters per account */
	public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	/** Maximum number of players allowed to play simultaneously on server */
	public static int MAXIMUM_ONLINE_USERS;
	/** Minimal protocol revision */
	public static int MIN_PROTOCOL_REVISION;
	/** Maximal protocol revision */
	public static int MAX_PROTOCOL_REVISION;
	
	// ------------------------------------------------ //
	// XXX option.properties
	// ------------------------------------------------ //
	/** For test servers - everybody has admin rights */
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	/** Debug/release mode */
	public static boolean DEBUG;
	/** Set if this server is a test server used for development */
	public static boolean TEST_SERVER;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_SPAWNLIST_TABLE;
	/** Enable custom data tables ? */
	public static boolean SAVE_GMSPAWN_ON_CUSTOM;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_NPC_TABLE;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_NPC_SKILLS_TABLE;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_ITEM_TABLES;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_ARMORSETS_TABLE;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_TELEPORT_TABLE;
	/** Enable custom data tables ? */
	public static boolean CUSTOM_MERCHANT_TABLES;
	/** Display test server in the list of servers ? */
	public static boolean SERVER_LIST_TESTSERVER;
	/** Displays [] in front of server name ? */
	public static boolean SERVER_LIST_BRACKET;
	/** Displays a clock next to the server name ? */
	public static boolean SERVER_LIST_CLOCK;
	/** Set the server as gm only at startup ? */
	public static boolean SERVER_GMONLY;
	/** Time after which item will auto-destroy */
	public static int AUTODESTROY_ITEM_AFTER;
	/** List of items that will not be destroyed */
	public static List<Integer> LIST_PROTECTED_ITEMS = new ArrayList<>();
	/** Update items only when strictly necessary */
	public static boolean LAZY_ITEMS_UPDATE;
	/** Auto destroy nonequipable items dropped by players */
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	/** Auto destroy equipable items dropped by players */
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	/** Save items on ground for restoration on server restart */
	public static boolean SAVE_DROPPED_ITEM;
	/** Accept precise drop calculation ? */
	public static boolean PRECISE_DROP_CALCULATION;
	/** Accept multi-items drop ? */
	public static boolean MULTIPLE_ITEM_DROP;
	/** Falling Damage */
	public static boolean ENABLE_FALLING_DAMAGE;
	/** Allow warehouse ? */
	public static boolean ALLOW_WAREHOUSE;
	/** Allow warehouse cache? */
	public static boolean WAREHOUSE_CACHE;
	/** How long store WH data */
	public static int WAREHOUSE_CACHE_TIME;
	/** Allow Discard item ? */
	public static boolean ALLOW_DISCARDITEM;
	/** Allow freight ? */
	public static boolean ALLOW_FREIGHT;
	/** Allow wear ? (try on in shop) */
	public static boolean ALLOW_WEAR;
	/** Duration of the try on after which items are taken back */
	public static int WEAR_DELAY;
	/** Price of the try on of one item */
	public static int WEAR_PRICE;
	/** Allow lottery ? */
	public static boolean ALLOW_LOTTERY;
	/** Allow race ? */
	public static boolean ALLOW_RACE;
	/** Allow water ? */
	public static boolean ALLOW_WATER;
	/** Allow rent pet ? */
	public static boolean ALLOW_RENTPET;
	/** Allow boat ? */
	public static boolean ALLOW_BOAT;
	/** Allow fishing ? */
	public static boolean ALLOWFISHING;
	/** Allow Manor system */
	public static boolean ALLOW_MANOR;
	/** Allow NPC walkers */
	public static boolean ALLOW_NPC_WALKERS;
	/** Allow Pet walkers */
	public static boolean ALLOW_PET_WALKERS;
	/** Global chat state */
	public static String DEFAULT_GLOBAL_CHAT;
	/** Trade chat state */
	public static String DEFAULT_TRADE_CHAT;
	// Community Board
	/** Type of community */
	public static boolean COMMUNITY_ENABLE;
	public static String BBS_DEFAULT;
	
	/** Zone Setting */
	public static int ZONE_TOWN;
	/** Maximum range mobs can randomly go from spawn point */
	public static int MAX_DRIFT_RANGE;
	/**
	 * Force full item inventory packet to be sent for any item change ?<br>
	 * <u><i>Note:</i></u> This can increase network traffic
	 */
	/** Auto-delete invalid quest data ? */
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	// Thread pools size
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	/** Period in days after which character is deleted */
	public static int DELETE_DAYS;
	/** Jail config **/
	public static boolean JAIL_IS_PVP;
	/** Cand pvp in jail ? */
	public static boolean JAIL_DISABLE_CHAT;
	/** Allow Offline Trade ? */
	public static boolean OFFLINE_TRADE_ENABLE;
	/** Allow Offline Craft ? */
	public static boolean OFFLINE_CRAFT_ENABLE;
	/** Restore Offliners ? */
	public static boolean RESTORE_OFFLINERS;
	/** Max Days for Offline Stores ? */
	public static int OFFLINE_MAX_DAYS;
	/** Disconnect shops that finished selling ? */
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	/** Allow color for offline mode ? */
	public static boolean OFFLINE_SET_NAME_COLOR;
	/** Color for offline mode */
	public static int OFFLINE_NAME_COLOR;
	
	/** Geodata */
	public static String GEODATA_PATH;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	
	/** Path finding */
	public static boolean PATHFINDING;
	public static String PATHFIND_BUFFERS;
	public static int BASE_WEIGHT;
	public static int DIAGONAL_WEIGHT;
	public static int HEURISTIC_WEIGHT;
	public static int OBSTACLE_MULTIPLIER;
	public static int MAX_ITERATIONS;
	public static boolean DEBUG_PATH;
	public static boolean DEBUG_GEO_NODE;
	
	/** Time after which a packet is considered as lost */
	public static int PACKET_LIFETIME;
	/** Detects server deadlocks */
	public static boolean DEADLOCK_DETECTOR;
	/** Check interval in seconds */
	public static int DEADLOCK_CHECK_INTERVAL;
	/** Restarts server to remove deadlocks */
	public static boolean RESTART_ON_DEADLOCK;
	
	// ------------------------------------------------ //
	// XXX security.properties
	// ------------------------------------------------ //
	/** Default punishment for illegal actions */
	public static int DEFAULT_PUNISH;
	/** Parameter for default punishment */
	public static int DEFAULT_PUNISH_PARAM;
	/** Bypass exploit protection ? */
	public static boolean BYPASS_VALIDATION;
	/** Only GM buy items for free **/
	public static boolean ONLY_GM_ITEMS_FREE;
	/** Enforce game guard query on character login ? */
	public static boolean GAMEGUARD_ENFORCE;
	/** Don't allow player to perform trade,talk with npc and move until gameguard reply received ? */
	public static boolean GAMEGUARD_PROHIBITACTION;
	/** Allow L2Walker */
	public static boolean ALLOW_L2WALKER;
	/** Logging Chat Window */
	public static boolean LOG_CHAT;
	/** Logging Item Window */
	public static boolean LOG_ITEMS;
	/** GM Audit ? */
	public static boolean GMAUDIT;
	public static boolean ILLEGAL_ACTION_AUDIT;
	
	public static int PROTECTED_ROLLDICE;
	public static int PROTECTED_FIREWORK;
	public static int PROTECTED_ITEMPETSUMMON;
	public static int PROTECTED_HEROVOICE;
	public static int PROTECTED_GLOBALCHAT;
	public static int PROTECTED_MULTISELL;
	public static int PROTECTED_SUBCLASS;
	public static int PROTECTED_DROPITEM;
	public static int PROTECTED_BYPASS;
	
	// ------------------------------------------------ //
	// XXX GM
	// ------------------------------------------------ //
	/** GM secure check privileges */
	public static boolean GM_SECURE_CHECK;
	/** Disable transaction on AccessLevel **/
	public static boolean GM_DISABLE_TRANSACTION;
	
	/** Enable colored name for GM ? */
	public static boolean GM_NAME_COLOR_ENABLED;
	/** Color of GM name */
	public static int GM_NAME_COLOR;
	/** Color of admin name */
	public static int ADMIN_NAME_COLOR;
	/** Place an aura around the GM ? */
	public static boolean GM_HERO_AURA;
	/** Set the GM invulnerable at startup ? */
	public static boolean GM_STARTUP_INVULNERABLE;
	/** Set the GM invisible at startup ? */
	public static boolean GM_STARTUP_INVISIBLE;
	/** Set silence to GM at startup ? */
	public static boolean GM_STARTUP_SILENCE;
	/** Add GM in the GM list at startup ? */
	public static boolean GM_STARTUP_AUTO_LIST;
	
	/** Allow petition ? */
	public static boolean PETITIONING_ALLOWED;
	/** Maximum number of petitions per player */
	public static int MAX_PETITIONS_PER_PLAYER;
	/** Maximum number of petitions pending */
	public static int MAX_PETITIONS_PENDING;
	
	// ------------------------------------------------ //
	// XXX rate.properties
	// ------------------------------------------------ //
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	
	// Drop Amount
	public static double DROP_AMOUNT_ITEMS;
	public static Map<Integer, Double> DROP_AMOUNT_ITEMS_BY_ID = new HashMap<>();
	public static double DROP_AMOUNT_ADENA;
	public static double DROP_AMOUNT_SEAL_STONE;
	public static double DROP_AMOUNT_SPOIL;
	public static double DROP_AMOUNT_RAID;
	public static int DROP_AMOUNT_MANOR;
	// Drop Chance
	public static double DROP_CHANCE_QUESTS_REWARD;
	public static double DROP_CHANCE_ADENA;
	public static double RATE_CONSUMABLE_COST;
	public static double DROP_CHANCE_ITEMS;
	public static Map<Integer, Double> DROP_CHANCE_ITEMS_BY_ID = new HashMap<>();
	public static double DROP_CHANCE_RAID;
	public static double DROP_CHANCE_DROP_QUEST;
	
	/** Rate for karma and experience lose */
	public static double RATE_KARMA_EXP_LOST;
	/** Rate siege guards prices */
	public static double RATE_SIEGE_GUARDS_PRICE;
	/** Alternative eXperience Point rewards */
	public static double ALT_GAME_EXPONENT_XP;
	/** Alternative Spirit Point rewards */
	public static double ALT_GAME_EXPONENT_SP;
	/** Deep Blue Mobs' Drop Rules Enabled */
	public static boolean DEEPBLUE_DROP_RULES;
	/** Define Party XP cutoff point method - Possible values: level and percentage */
	public static String PARTY_XP_CUTOFF_METHOD;
	/** Define the cutoff point value for the "level" method */
	public static int PARTY_XP_CUTOFF_LEVEL;
	/** Define the cutoff point value for the "percentage" method */
	public static double PARTY_XP_CUTOFF_PERCENT;
	/** Limit for player drop */
	public static int PLAYER_DROP_LIMIT;
	/** Rate for drop */
	public static int PLAYER_RATE_DROP;
	/** Rate for player's item drop */
	public static int PLAYER_RATE_DROP_ITEM;
	/** Rate for player's equipment drop */
	public static int PLAYER_RATE_DROP_EQUIP;
	/** Rate for player's equipment and weapon drop */
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	/** Rate for experience rewards of the pet */
	public static double PET_XP_RATE;
	/** Rate for food consumption of the pet */
	public static int PET_FOOD_RATE;
	/** Rate for experience rewards of the Sin Eater */
	public static double SINEATER_XP_RATE;
	/** Karma drop limit */
	public static int KARMA_DROP_LIMIT;
	/** Karma drop rate */
	public static int KARMA_RATE_DROP;
	/** Karma drop rate for item */
	public static int KARMA_RATE_DROP_ITEM;
	/** Karma drop rate for equipment */
	public static int KARMA_RATE_DROP_EQUIP;
	/** Karma drop rate for equipment and weapon */
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	// ------------------------------------------------ //
	// XXX pvp.properties
	// ------------------------------------------------ //
	/** Minimum karma gain/loss */
	public static int KARMA_MIN_KARMA;
	/** Maximum karma gain/loss */
	public static int KARMA_MAX_KARMA;
	/** Number to divide the xp recieved by, to calculate karma lost on xp gain/lost */
	public static int KARMA_XP_DIVIDER;
	/** The Minimum Karma lost if 0 karma is to be removed */
	public static int KARMA_LOST_BASE;
	/** Can a GM drop item ? */
	public static boolean KARMA_DROP_GM;
	/** Should award a pvp point for killing a player with karma ? */
	public static boolean KARMA_AWARD_PK_KILL;
	/** Minimum PK required to drop */
	public static int KARMA_PK_LIMIT;
	/** List of pet items that cannot be dropped when PVP */
	public static List<Integer> KARMA_LIST_NONDROPPABLE_PET_ITEMS = new ArrayList<>();
	/** List of items that cannot be dropped when PVP */
	public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
	/** Duration (in ms) while a player stay in PVP mode after hitting an innocent */
	public static int PVP_NORMAL_TIME;
	/** Duration (in ms) while a player stay in PVP mode after hitting a purple player */
	public static int PVP_PVP_TIME;
	
	/** List of items that cannot be dropped */
	public static List<Integer> LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
	
	/** Time between 2 updates of IP */
	public static int IP_UPDATE_TIME;
	
	// Spoil Rates
	/** Allow spoil on lower level mobs than the character */
	// public static boolean CAN_SPOIL_LOWER_LEVEL_MOBS;
	/** Allow delevel and spoil mob ? */
	// public static boolean CAN_DELEVEL_AND_SPOIL_MOBS;
	/** Maximum level difference between player and mob level */
	// public static float MAXIMUM_PLAYER_AND_MOB_LEVEL_DIFFERENCE;
	/** Base rate for spoil */
	// public static float BASE_SPOIL_RATE;
	/** Minimum spoil rate */
	// public static float MINIMUM_SPOIL_RATE;
	/** Maximum level difference between player and spoil level to allow before decreasing spoil chance */
	// public static float SPOIL_LEVEL_DIFFERENCE_LIMIT;
	/** Spoil level multiplier */
	// public static float SPOIL_LEVEL_DIFFERENCE_MULTIPLIER;
	/** Last level spoil learned */
	// public static int LAST_LEVEL_SPOIL_IS_LEARNED;
	
	/** Enumeration for type of ID Factory */
	public static enum IdFactoryType
	{
		COMPACTION,
		BITSET,
		STACK
	}
	
	/** ID Factory type */
	public static IdFactoryType IDFACTORY_TYPE;
	/** Check for bad ID ? */
	public static boolean BAD_ID_CHECKING;
	
	/** Login Server port */
	public static int PORT_LOGIN;
	/** Number of tries of login before ban */
	public static int LOGIN_TRY_BEFORE_BAN;
	/** Number of seconds the IP ban will last, default 10 minutes */
	public static int LOGIN_BLOCK_AFTER_BAN;
	/** Is telnet enabled ? */
	public static boolean IS_TELNET_ENABLED;
	/** Show licence or not just after login (if False, will directly go to the Server List */
	public static boolean SHOW_LICENCE;
	/** Force GameGuard authorization in loginserver */
	public static boolean FORCE_GGAUTH;
	/** Accept new game server ? */
	public static boolean ACCEPT_NEW_GAMESERVER;
	/** Hexadecimal ID of the game server */
	public static byte[] HEX_ID;
	public static boolean RESERVE_HOST_ON_LOGIN = false;
	public static boolean LAZY_CACHE;
	
	/** Allow auto-create account ? */
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	/** Grid Options */
	public static boolean GRIDS_ALWAYS_ON;
	public static int GRID_NEIGHBOR_TURNON_TIME;
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	/** Clan Hall function related configs */
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static int CH_TELE3_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	
	// XXX PACKETS
	/** MMO settings */
	public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20
	public static int MMO_MAX_SEND_PER_PASS = 80; // default 80
	public static int MMO_MAX_READ_PER_PASS = 80; // default 80
	public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20
	
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = 14; // default MMO_MAX_READ_PER_PASS + 2
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = 13; // default MMO_MAX_READ_PER_PASS + 1
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 5; // default 5
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 80; // default 80
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2; // default 2
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5; // default 5
	
	/**
	 * This class initializes all global variables for configuration.<br>
	 * If key doesn't appear in properties file, a default value is setting on by this class.<br>
	 * properties file for configuring your server.
	 */
	public static void load()
	{
		System.out.println("#_____________________________________________________________________________#");
		System.out.println("#                __   ___     _ ___       __          _                       #");
		System.out.println("#               / /  |__ \\   (_)   | ____/ /___ ___  (_)___  _____            #");
		System.out.println("#              / /   __/ /  / / /| |/ __  / __ `__ \\/ / __ \\/ ___/            #");
		System.out.println("#             / /___/ __/  / / ___ / /_/ / / / / / / / / / (__  )             #");
		System.out.println("#            /_____/____/_/ /_/  |_\\__,_/_/ /_/ /_/_/_/ /_/____/              #");
		System.out.println("#                      /___/                                                  #");
		System.out.println("#_____________________________________________________________________________#");
		
		if (Server.SERVER_MODE == Server.MODE_GAMESERVER)
		{
			readGeoEngine();
			readCharacters();
			readNpc();
			readEventRetail();
			readServer();
			readOptions();
			readSecurity();
			readTelnet();
			readFactory();
			readGm();
			readRates();
			readClanHall();
			readPvP();
			
			var settings = new L2Properties(FILE_HEXID);
			HEX_ID = new BigInteger(settings.getString("HexID", ""), 16).toByteArray();
		}
		else if (Server.SERVER_MODE == Server.MODE_LOGINSERVER)
		{
			readLogin();
		}
		else
		{
			LOG.severe("Could not Load Config: server mode was not set");
		}
	}
	
	public static void loadSiegeProperties()
	{
		var config = new L2Properties(FILE_SIEGE_CONFIG);
		
		// Siege setting
		SIEGE_ATTACKER_MAX_CLANS = config.getInteger("AttackerMaxClans", 500);
		SIEGE_ATTACKER_RESPAWN_DELAY = config.getInteger("AttackerRespawn", 0);
		SIEGE_DEFENDER_MAX_CLANS = config.getInteger("DefenderMaxClans", 500);
		SIEGE_FLAG_MAX_COUNT = config.getInteger("MaxFlags", 1);
		SIEGE_MIN_CLAN_LVL = config.getInteger("SiegeClanMinLevel", 4);
		SIEGE_LENGTH = config.getInteger("SiegeLength", 120);
		
		for (var castle : CastleData.getInstance().getCastles())
		{
			var controlTowersSpawns = new ArrayList<SiegeSpawnHolder>();
			for (int i = 1; i < 10; i++)
			{
				var spawnParams = config.getString(castle.getName() + "ControlTower" + i, "");
				
				if (spawnParams.length() == 0)
				{
					break;
				}
				
				var st = new StringTokenizer(spawnParams.trim(), ",");
				
				try
				{
					var x = Integer.parseInt(st.nextToken());
					var y = Integer.parseInt(st.nextToken());
					var z = Integer.parseInt(st.nextToken());
					var npcId = Integer.parseInt(st.nextToken());
					var hp = Integer.parseInt(st.nextToken());
					
					controlTowersSpawns.add(new SiegeSpawnHolder(castle.getId(), x, y, z, 0, npcId, hp));
				}
				catch (final Exception e)
				{
					LOG.warning("Error while loading control tower(s) for " + castle.getName() + " castle.");
				}
			}
			
			var artefactSpawns = new ArrayList<SiegeSpawnHolder>();
			for (int i = 1; i < 10; i++)
			{
				var spawnParams = config.getString(castle.getName() + "Artefact" + i, "");
				
				if (spawnParams.length() == 0)
				{
					break;
				}
				
				var st = new StringTokenizer(spawnParams.trim(), ",");
				
				try
				{
					var x = Integer.parseInt(st.nextToken());
					var y = Integer.parseInt(st.nextToken());
					var z = Integer.parseInt(st.nextToken());
					var heading = Integer.parseInt(st.nextToken());
					var npcId = Integer.parseInt(st.nextToken());
					
					artefactSpawns.add(new SiegeSpawnHolder(castle.getId(), x, y, z, heading, npcId));
				}
				catch (final Exception e)
				{
					LOG.warning("Error while loading artefact(s) for " + castle.getName() + " castle.");
				}
			}
			
			SIEGE_CONTROL_TOWER_SPAWN_LIST.put(castle.getId(), controlTowersSpawns);
			SIEGE_ARTEFACT_SPAWN_LIST.put(castle.getId(), artefactSpawns);
		}
	}
	
	/**
	 * Save hexadecimal ID of the server in the properties file.
	 * @param string (String) : hexadecimal ID of the server to store
	 */
	public static void saveHexid(String string)
	{
		saveHexid(string, FILE_HEXID);
	}
	
	/**
	 * Save hexadecimal ID of the server in the properties file.
	 * @param string   (String) : hexadecimal ID of the server to store
	 * @param fileName (String) : name of the properties file
	 */
	public static void saveHexid(String string, String fileName)
	{
		final Properties hexSetting = new Properties();
		final File file = new File(fileName);
		try (OutputStream out = new FileOutputStream(file))
		{
			// Create a new empty file only if it doesn't exist
			file.createNewFile();
			hexSetting.setProperty("HexID", string);
			hexSetting.store(out, "the hexID to auth into login");
		}
		catch (final Exception e)
		{
			LOG.warning("Failed to save hex id to " + fileName + " File.");
			e.printStackTrace();
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	// METODOS
	// ---------------------------------------------------------------------------------------------
	
	/**
	 * Read {@link #FILE_GEOENGINE}
	 */
	private static void readGeoEngine()
	{
		var config = new L2Properties(FILE_GEOENGINE);
		
		GEODATA_PATH = config.getString("GeoDataPath", "./data/geodata/");
		PART_OF_CHARACTER_HEIGHT = config.getInteger("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = config.getInteger("MaxObstacleHeight", 32);
		PATHFINDING = config.getBoolean("PathFinding", true);
		PATHFIND_BUFFERS = config.getString("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
		BASE_WEIGHT = config.getInteger("BaseWeight", 10);
		DIAGONAL_WEIGHT = config.getInteger("DiagonalWeight", 14);
		OBSTACLE_MULTIPLIER = config.getInteger("ObstacleMultiplier", 10);
		HEURISTIC_WEIGHT = config.getInteger("HeuristicWeight", 20);
		MAX_ITERATIONS = config.getInteger("MaxIterations", 500);
		DEBUG_PATH = config.getBoolean("DebugPath", false);
		DEBUG_GEO_NODE = config.getBoolean("DebugGeoNode", false);
	}
	
	/**
	 * Read {@link #FILE_CHARACTER_CONFIG}
	 */
	private static void readCharacters()
	{
		var config = new L2Properties(FILE_CHARACTER_CONFIG);
		
		MAX_EVASION = config.getInteger("MaxEvasion", 250);
		MAX_RUN_SPEED = config.getInteger("MaxRunSpeed", 250);
		MAX_PCRIT_RATE = config.getInteger("MaxPCritRate", 500);
		MAX_MCRIT_RATE = config.getInteger("MaxMCritRate", 300);
		MAX_PATK_SPEED = config.getInteger("MaxPAtkSpeed", 1500);
		MAX_MATK_SPEED = config.getInteger("MaxMAtkSpeed", 1999);
		// Inventory slots limits
		INVENTORY_MAXIMUM_NO_DWARF = config.getInteger("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = config.getInteger("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_GM = config.getInteger("MaximumSlotsForGMPlayer", 250);
		INVENTORY_MAXIMUM_PET = config.getInteger("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
		// Warehouse slots limits
		WAREHOUSE_SLOTS_NO_DWARF = config.getInteger("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = config.getInteger("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = config.getInteger("MaximumWarehouseSlotsForClan", 200);
		FREIGHT_SLOTS = config.getInteger("MaximumFreightSlots", 20);
		// Skills
		AUTO_LEARN_SKILLS = config.getBoolean("AutoLearnSkills", false);
		AUTO_LEARN_3RD_SKILLS = config.getBoolean("AutoLearn3rdClassSkills", false);
		ALT_GAME_CANCEL_BOW = config.getString("AltGameCancelByHit", "cast").equalsIgnoreCase("bow") || config.getString("AltGameCancelByHit", "cast").equalsIgnoreCase("all");
		ALT_GAME_CANCEL_CAST = config.getString("AltGameCancelByHit", "cast").equalsIgnoreCase("cast") || config.getString("AltGameCancelByHit", "cast").equalsIgnoreCase("all");
		ALT_PERFECT_SHLD_BLOCK = config.getInteger("AltPerfectShieldBlockRate", 5);
		ALT_GAME_DELEVEL = config.getBoolean("Delevel", true);
		ALT_WEIGHT_LIMIT = config.getDouble("AltWeightLimit", 1.0);
		ALT_GAME_MAGICFAILURES = config.getBoolean("MagicFailures", false);
		SP_BOOK_NEEDED = config.getBoolean("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = config.getBoolean("EnchantSkillSpBookNeeded", true);
		ENABLE_MODIFY_SKILL_DURATION = config.getBoolean("EnableModifySkillDuration", false);
		// Create Map only if enabled
		if (ENABLE_MODIFY_SKILL_DURATION)
		{
			SKILL_DURATION_LIST = config.getMapInteger("SkillDurationList");
		}
		// Buff
		BUFFS_MAX_AMOUNT = config.getInteger("MaxBuffAmount", 20);
		STORE_SKILL_COOLTIME = config.getBoolean("StoreSkillCoolTime", true);
		EFFECT_CANCELING = config.getBoolean("CancelLesserEffect", true);
		// SubClass
		ALT_MAX_SUBCLASS = config.getInteger("AltMaxSubClasses", 3);
		ALT_GAME_SUBCLASS_WITHOUT_QUESTS = config.getBoolean("AltSubClassWithoutQuests", false);
		// Crafting
		IS_CRAFTING_ENABLED = config.getBoolean("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = config.getInteger("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = config.getInteger("CommonRecipeLimit", 50);
		ALT_GAME_CREATION = config.getBoolean("AltGameCreation", false);
		ALT_GAME_CREATION_SPEED = config.getDouble("AltGameCreationSpeed", 1.0);
		ALT_GAME_CREATION_XP_RATE = config.getDouble("AltGameCreationRateXp", 1.0);
		ALT_GAME_CREATION_SP_RATE = config.getDouble("AltGameCreationRateSp", 1.0);
		// karma
		ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = config.getBoolean("AltKarmaPlayerCanBeKilledInPeaceZone", false);
		ALT_GAME_KARMA_PLAYER_CAN_SHOP = config.getBoolean("AltKarmaPlayerCanShop", true);
		ALT_GAME_KARMA_PLAYER_CAN_USE_GK = config.getBoolean("AltKarmaPlayerCanUseGK", false);
		ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = config.getBoolean("AltKarmaPlayerCanTeleport", true);
		ALT_GAME_KARMA_PLAYER_CAN_TRADE = config.getBoolean("AltKarmaPlayerCanTrade", true);
		ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = config.getBoolean("AltKarmaPlayerCanUseWareHouse", true);
		ALT_GAME_FREE_TELEPORT = config.getBoolean("AltFreeTeleporting", false);
		// Clan nad ally
		ALT_CLAN_MEMBERS_FOR_WAR = config.getInteger("AltClanMembersForWar", 15);
		ALT_CLAN_JOIN_DAYS = config.getInteger("DaysBeforeJoinAClan", 5);
		ALT_CLAN_CREATE_DAYS = config.getInteger("DaysBeforeCreateAClan", 10);
		ALT_CLAN_DISSOLVE_DAYS = config.getInteger("DaysToPassToDissolveAClan", 7);
		ALT_RECOVERY_PENALTY = config.getInteger("DaysToPassToDissolveAgain", 7);
		ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = config.getInteger("DaysBeforeJoinAllyWhenLeaved", 1);
		ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = config.getInteger("DaysBeforeJoinAllyWhenDismissed", 1);
		ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = config.getInteger("DaysBeforeAcceptNewClanWhenDismissed", 1);
		ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = config.getInteger("DaysBeforeCreateNewAllyWhenDissolved", 10);
		ALT_MAX_NUM_OF_CLANS_IN_ALLY = config.getInteger("AltMaxNumOfClansInAlly", 12);
		// enchant
		ENCHANT_CHANCE_WEAPON = config.getInteger("EnchantChanceWeapon", 68);
		ENCHANT_CHANCE_ARMOR = config.getInteger("EnchantChanceArmor", 52);
		ENCHANT_CHANCE_JEWELRY = config.getInteger("EnchantChanceJewelry", 54);
		BLESSED_ENCHANT_CHANCE_WEAPON = config.getInteger("BlessedEnchantChanceWeapon", 68);
		BLESSED_ENCHANT_CHANCE_ARMOR = config.getInteger("BlessedEnchantChanceArmor", 52);
		BLESSED_ENCHANT_CHANCE_JEWELRY = config.getInteger("BlessedEnchantChanceJewelry", 54);
		ENCHANT_MAX_WEAPON = config.getInteger("EnchantMaxWeapon", 255);
		ENCHANT_MAX_ARMOR = config.getInteger("EnchantMaxArmor", 255);
		ENCHANT_MAX_JEWELRY = config.getInteger("EnchantMaxJewelry", 255);
		ENCHANT_SAFE_MAX = config.getInteger("EnchantSafeMax", 3);
		ENCHANT_SAFE_MAX_FULL = config.getInteger("EnchantSafeMaxFull", 4);
		// Misc
		AUTO_LOOT = config.getBoolean("AutoLoot", false);
		AUTO_LOOT_RAIDS = config.getBoolean("AutoLootRaids", false);
		ALT_ENABLE_TUTORIAL = config.getBoolean("AltEnableTutorial", true);
		
		// if different from 100 (ie 100%) heal rate is modified accordingly
		HP_REGEN_MULTIPLIER = config.getDouble("HpRegenMultiplier", 100) / 100;
		MP_REGEN_MULTIPLIER = config.getDouble("MpRegenMultiplier", 100) / 100;
		CP_REGEN_MULTIPLIER = config.getDouble("CpRegenMultiplier", 100) / 100;
		
		UNSTUCK_INTERVAL = config.getInteger("UnstuckInterval", 300);
		PLAYER_SPAWN_PROTECTION = config.getInteger("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = config.getInteger("PlayerFakeDeathUpProtection", 0);
		RESPAWN_RESTORE_HP = config.getDouble("RespawnRestoreHP", 70) / 100;
		RESPAWN_RANDOM_ENABLED = config.getBoolean("RespawnRandomOffset", true);
		RESPAWN_RANDOM_MAX_OFFSET = config.getInteger("RespawnRandomMaxOffset", 20);
		MAX_PVTSTORE_SLOTS_DWARF = config.getInteger("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = config.getInteger("MaxPvtStoreSlotsOther", 4);
		
		LOG_VERY_HIGH_DAMAGE = config.getBoolean("LogVeryHighDamage", true);
		LOG_DMG = config.getInteger("LogMaxDamage", 5000);
	}
	
	/**
	 * Read {@link #FILE_NPC_CONFIG}
	 */
	private static void readNpc()
	{
		var config = new L2Properties(FILE_NPC_CONFIG);
		
		ALLOW_WYVERN_UPGRADER = config.getBoolean("AllowWyvernUpgrader", false);
		
		ALT_GAME_VIEWNPC = config.getBoolean("AltGameViewNpc", false);
		ALT_GAME_TIREDNESS = config.getBoolean("AltGameTiredness", false);
		ALT_MOB_AGGRO_IN_PEACEZONE = config.getBoolean("AltMobAggroInPeaceZone", true);
		ANNOUNCE_MAMMON_SPAWN = config.getBoolean("AnnounceMammonSpawn", true);
		
		ALT_GAME_FREIGHTS = config.getBoolean("AltGameFreights", false);
		ALT_GAME_FREIGHT_PRICE = config.getInteger("AltGameFreightPrice", 1000);
		
		LIST_PET_RENT_NPC = config.getList("ListPetRentNpc");
		RAID_HP_REGEN_MULTIPLIER = config.getDouble("RaidHpRegenMultiplier", 100) / 100;
		RAID_MP_REGEN_MULTIPLIER = config.getDouble("RaidMpRegenMultiplier", 100) / 100;
		RAID_PDEFENCE_MULTIPLIER = config.getDouble("RaidPDefenceMultiplier", 100) / 100;
		RAID_MDEFENCE_MULTIPLIER = config.getDouble("RaidMDefenceMultiplier", 100) / 100;
		RAID_MINION_RESPAWN_TIMER = config.getInteger("RaidMinionRespawnTime", 300000);
		RAID_MIN_RESPAWN_MULTIPLIER = config.getDouble("RaidMinRespawnMultiplier", 1.0);
		RAID_MAX_RESPAWN_MULTIPLIER = config.getDouble("RaidMaxRespawnMultiplier", 1.0);
		
		MIN_NPC_ANIMATION = config.getInteger("MinNPCAnimation", 10);
		MAX_NPC_ANIMATION = config.getInteger("MaxNPCAnimation", 20);
		MIN_MONSTER_ANIMATION = config.getInteger("MinMonsterAnimation", 5);
		MAX_MONSTER_ANIMATION = config.getInteger("MaxMonsterAnimation", 20);
		KNOWNLIST_UPDATE_INTERVAL = config.getInteger("KnownListUpdateInterval", 1250);
		SHOW_NPC_LVL = config.getBoolean("ShowNpcLevel", false);
		
		GUARD_ATTACK_AGGRO_MOB = config.getBoolean("AllowGuards", false);
	}
	
	/**
	 * Read {@link #FILE_EVENT_RETAIL_CONFIG}
	 */
	private static void readEventRetail()
	{
		var config = new L2Properties(FILE_EVENT_RETAIL_CONFIG);
		
		// dimensional rift
		RIFT_MIN_PARTY_SIZE = config.getInteger("RiftMinPartySize", 2);
		RIFT_MAX_JUMPS = config.getInteger("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = config.getInteger("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = config.getInteger("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = config.getInteger("AutoJumpsDelayMax", 600);
		RIFT_ENTER_COST_RECRUIT = config.getInteger("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = config.getInteger("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = config.getInteger("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = config.getInteger("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = config.getInteger("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = config.getInteger("HeroCost", 33);
		RIFT_BOSS_ROOM_TIME_MUTIPLY = config.getDouble("BossRoomTimeMultiply", 1.0);
		// olympiad
		ALT_OLY_START_TIME = config.getInteger("AltOlyStartTime", 18);
		ALT_OLY_MIN = config.getInteger("AltOlyMin", 0);
		ALT_OLY_CPERIOD = config.getInteger("AltOlyCPeriod", 21600000);
		ALT_OLY_BATTLE = config.getLong("AltOlyBattle", 180000);
		ALT_OLY_WPERIOD = config.getLong("AltOlyWPeriod", 604800000);
		ALT_OLY_VPERIOD = config.getLong("AltOlyVPeriod", 86400000);
		ALT_OLY_WAIT_TIME = config.getInteger("AltOlyWaitTime", 30);
		ALT_OLY_WAIT_BATTLE = config.getInteger("AltOlyWaitBattle", 60);
		ALT_OLY_WAIT_END = config.getInteger("AltOlyWaitEnd", 40);
		ALT_OLY_START_POINTS = config.getInteger("AltOlyStartPoints", 18);
		ALT_OLY_WEEKLY_POINTS = config.getInteger("AltOlyWeeklyPoints", 3);
		ALT_OLY_MIN_MATCHES = config.getInteger("AltOlyMinMatchesToBeClassed", 5);
		ALT_OLY_CLASSED = config.getInteger("AltOlyClassedParticipants", 5);
		ALT_OLY_NONCLASSED = config.getInteger("AltOlyNonClassedParticipants", 9);
		ALT_OLY_CLASSED_REWARD = config.getMapInteger("AltOlyClassedReward");
		ALT_OLY_NONCLASSED_REWARD = config.getMapInteger("AltOlyNonClassedReward");
		ALT_OLY_GP_PER_POINT = config.getInteger("AltOlyGPPerPoint", 1000);
		ALT_OLY_HERO_POINTS = config.getInteger("AltOlyHeroPoints", 300);
		ALT_OLY_MAX_POINTS = config.getInteger("AltOlyMaxPoints", 10);
		ALT_OLY_DIVIDER_CLASSED = config.getInteger("AltOlyDividerClassed", 3);
		ALT_OLY_DIVIDER_NON_CLASSED = config.getInteger("AltOlyDividerNonClassed", 3);
		ALT_OLY_ANNOUNCE_GAMES = config.getBoolean("AltOlyAnnounceGames", true);
		// lotery
		ALT_LOTTERY_PRIZE = config.getInteger("AltLotteryPrize", 50000);
		ALT_LOTTERY_TICKET_PRICE = config.getInteger("AltLotteryTicketPrice", 2000);
		ALT_LOTTERY_5_NUMBER_RATE = config.getDouble("AltLottery5NumberRate", 0.6);
		ALT_LOTTERY_4_NUMBER_RATE = config.getDouble("AltLottery4NumberRate", 0.2);
		ALT_LOTTERY_3_NUMBER_RATE = config.getDouble("AltLottery3NumberRate", 0.2);
		ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = config.getInteger("AltLottery2and1NumberPrize", 200);
		// manor
		ALT_MANOR_REFRESH_TIME = config.getInteger("AltManorRefreshTime", 20);
		ALT_MANOR_REFRESH_MIN = config.getInteger("AltManorRefreshMin", 00);
		ALT_MANOR_APPROVE_TIME = config.getInteger("AltManorApproveTime", 6);
		ALT_MANOR_APPROVE_MIN = config.getInteger("AltManorApproveMin", 00);
		ALT_MANOR_MAINTENANCE_PERIOD = config.getInteger("AltManorMaintenancePeriod", 360000);
		ALT_MANOR_SAVE_ALL_ACTIONS = config.getBoolean("AltManorSaveAllActions", true);
		ALT_MANOR_SAVE_PERIOD_RATE = config.getInteger("AltManorSavePeriodRate", 2);
		// seven sign
		ALT_GAME_REQUIRE_CASTLE_DAWN = config.getBoolean("AltRequireCastleForDawn", false);
		ALT_GAME_REQUIRE_CLAN_CASTLE = config.getBoolean("AltRequireClanCastle", false);
		ALT_FESTIVAL_MIN_PLAYER = config.getInteger("AltFestivalMinPlayer", 5);
		ALT_MAXIMUM_PLAYER_CONTRIB = config.getInteger("AltMaxPlayerContrib", 1000000);
		ALT_FESTIVAL_MANAGER_START = config.getLong("AltFestivalManagerStart", 120000);
		ALT_FESTIVAL_LENGTH = config.getLong("AltFestivalLength", 1080000);
		ALT_FESTIVAL_CYCLE_LENGTH = config.getLong("AltFestivalCycleLength", 2280000);
		ALT_FESTIVAL_FIRST_SPAWN = config.getLong("AltFestivalFirstSpawn", 120000);
		ALT_FESTIVAL_FIRST_SWARM = config.getLong("AltFestivalFirstSwarm", 300000);
		ALT_FESTIVAL_SECOND_SPAWN = config.getLong("AltFestivalSecondSpawn", 540000);
		ALT_FESTIVAL_SECOND_SWARM = config.getLong("AltFestivalSecondSwarm", 720000);
		ALT_FESTIVAL_CHEST_SPAWN = config.getLong("AltFestivalChestSpawn", 900000);
	}
	
	/**
	 * Read {@link #FILE_SERVER_CONFIG}
	 */
	private static void readServer()
	{
		var config = new L2Properties(FILE_SERVER_CONFIG);
		
		GAMESERVER_HOSTNAME = config.getString("GameserverHostname", "*");
		PORT_GAME = config.getInteger("GameserverPort", 7777);
		EXTERNAL_HOSTNAME = config.getString("ExternalHostname", "*");
		GAME_SERVER_LOGIN_PORT = config.getInteger("LoginPort", 9014);
		GAME_SERVER_LOGIN_HOST = config.getString("LoginHost", "127.0.0.1");
		REQUEST_ID = config.getInteger("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = config.getBoolean("AcceptAlternateID", true);
		DATABASE_DRIVER = config.getString("Driver", "com.mysql.jdbc.Driver");
		DATABASE_URL = config.getString("URL", "jdbc:mysql://localhost/l2jdb");
		DATABASE_LOGIN = config.getString("Login", "root");
		DATABASE_PASSWORD = config.getString("Password", "");
		MAX_CHARACTERS_NUMBER_PER_ACCOUNT = config.getInteger("CharMaxNumber", 7);
		MAXIMUM_ONLINE_USERS = config.getInteger("MaximumOnlineUsers", 100);
		MIN_PROTOCOL_REVISION = config.getInteger("MinProtocolRevision", 660);
		MAX_PROTOCOL_REVISION = config.getInteger("MaxProtocolRevision", 665);
		if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION)
		{
			LOG.warning("MinProtocolRevision is bigger than MaxProtocolRevision in server configuration file.");
		}
	}
	
	/**
	 * Read {@link #FILE_OPTIONS_CONFIG}
	 */
	private static void readOptions()
	{
		var config = new L2Properties(FILE_OPTIONS_CONFIG);
		
		OFFLINE_TRADE_ENABLE = config.getBoolean("OfflineTradeEnable", false);
		OFFLINE_CRAFT_ENABLE = config.getBoolean("OfflineCraftEnable", false);
		RESTORE_OFFLINERS = config.getBoolean("RestoreOffliners", true);
		OFFLINE_MAX_DAYS = config.getInteger("OfflineMaxDays", 10);
		OFFLINE_DISCONNECT_FINISHED = config.getBoolean("OfflineDisconnectFinished", true);
		OFFLINE_SET_NAME_COLOR = config.getBoolean("OfflineSetNameColor", false);
		OFFLINE_NAME_COLOR = Integer.decode("0x" + config.getString("OfflineNameColor", "808080"));
		
		EVERYBODY_HAS_ADMIN_RIGHTS = config.getBoolean("EverybodyHasAdminRights", false);
		DEBUG = config.getBoolean("Debug", false);
		TEST_SERVER = config.getBoolean("TestServer", false);
		CUSTOM_SPAWNLIST_TABLE = config.getBoolean("CustomSpawnlistTable", false);
		SAVE_GMSPAWN_ON_CUSTOM = config.getBoolean("SaveGmSpawnOnCustom", false);
		CUSTOM_NPC_TABLE = config.getBoolean("CustomNpcTable", false);
		CUSTOM_NPC_SKILLS_TABLE = config.getBoolean("CustomNpcSkillsTable", false);
		CUSTOM_ITEM_TABLES = config.getBoolean("CustomItemTables", false);
		CUSTOM_ARMORSETS_TABLE = config.getBoolean("CustomArmorSetsTable", false);
		CUSTOM_TELEPORT_TABLE = config.getBoolean("CustomTeleportTable", false);
		CUSTOM_MERCHANT_TABLES = config.getBoolean("CustomMerchantTables", false);
		SERVER_LIST_TESTSERVER = config.getBoolean("TestServer", false);
		SERVER_LIST_BRACKET = config.getBoolean("ServerListBrackets", false);
		SERVER_LIST_CLOCK = config.getBoolean("ServerListClock", false);
		SERVER_GMONLY = config.getBoolean("ServerGMOnly", false);
		AUTODESTROY_ITEM_AFTER = config.getInteger("AutoDestroyDroppedItemAfter", 0);
		var proetctedItems = config.getString("ListOfProtectedItems", "57");
		for (var id : proetctedItems.split(","))
		{
			LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
		}
		LAZY_ITEMS_UPDATE = config.getBoolean("LazyItemsUpdate", false);
		DESTROY_DROPPED_PLAYER_ITEM = config.getBoolean("DestroyPlayerDroppedItem", false);
		DESTROY_EQUIPABLE_PLAYER_ITEM = config.getBoolean("DestroyEquipableItem", false);
		SAVE_DROPPED_ITEM = config.getBoolean("SaveDroppedItem", false);
		PRECISE_DROP_CALCULATION = config.getBoolean("PreciseDropCalculation", true);
		MULTIPLE_ITEM_DROP = config.getBoolean("MultipleItemDrop", true);
		ENABLE_FALLING_DAMAGE = config.getBoolean("EnableFallingDamage", true);
		ALLOW_WAREHOUSE = config.getBoolean("AllowWarehouse", true);
		WAREHOUSE_CACHE = config.getBoolean("WarehouseCache", false);
		WAREHOUSE_CACHE_TIME = config.getInteger("WarehouseCacheTime", 15);
		ALLOW_FREIGHT = config.getBoolean("AllowFreight", true);
		ALLOW_WEAR = config.getBoolean("AllowWear", false);
		WEAR_DELAY = config.getInteger("WearDelay", 5);
		WEAR_PRICE = config.getInteger("WearPrice", 10);
		ALLOW_LOTTERY = config.getBoolean("AllowLottery", false);
		ALLOW_RACE = config.getBoolean("AllowRace", false);
		ALLOW_WATER = config.getBoolean("AllowWater", false);
		ALLOW_RENTPET = config.getBoolean("AllowRentPet", false);
		ALLOW_DISCARDITEM = config.getBoolean("AllowDiscardItem", true);
		ALLOWFISHING = config.getBoolean("AllowFishing", true);
		ALLOW_BOAT = config.getBoolean("AllowBoat", false);
		ALLOW_MANOR = config.getBoolean("AllowManor", true);
		ALLOW_NPC_WALKERS = config.getBoolean("AllowNpcWalkers", true);
		ALLOW_PET_WALKERS = config.getBoolean("AllowPetWalkers", true);
		DEFAULT_GLOBAL_CHAT = config.getString("GlobalChat", "ON");
		DEFAULT_TRADE_CHAT = config.getString("TradeChat", "ON");
		COMMUNITY_ENABLE = config.getBoolean("CommunityEnable", true);
		BBS_DEFAULT = config.getString("BBSDefault", "_bbshome");
		ZONE_TOWN = config.getInteger("ZoneTown", 0);
		MAX_DRIFT_RANGE = config.getInteger("MaxDriftRange", 300);
		AUTODELETE_INVALID_QUEST_DATA = config.getBoolean("AutoDeleteInvalidQuestData", false);
		THREADS_PER_SCHEDULED_THREAD_POOL = config.getInteger("ThreadsPerSheduledThreadPool", 6);
		THREADS_PER_INSTANT_THREAD_POOL = config.getInteger("ThreadsPerInstantThreadPool", 4);
		DELETE_DAYS = config.getInteger("DeleteCharAfterDays", 7);
		LAZY_CACHE = config.getBoolean("LazyCache", false);
		PACKET_LIFETIME = config.getInteger("PacketLifeTime", 0);
		DEADLOCK_DETECTOR = config.getBoolean("DeadLockDetector", false);
		DEADLOCK_CHECK_INTERVAL = config.getInteger("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = config.getBoolean("RestartOnDeadlock", false);
		GRIDS_ALWAYS_ON = config.getBoolean("GridsAlwaysOn", false);
		GRID_NEIGHBOR_TURNON_TIME = config.getInteger("GridNeighborTurnOnTime", 1);
		GRID_NEIGHBOR_TURNOFF_TIME = config.getInteger("GridNeighborTurnOffTime", 90);
		JAIL_IS_PVP = config.getBoolean("JailIsPvp", true);
		JAIL_DISABLE_CHAT = config.getBoolean("JailDisableChat", true);
	}
	
	/**
	 * Read {@link #FILE_SECURITY_CONFIG}
	 */
	private static void readSecurity()
	{
		var config = new L2Properties(FILE_SECURITY_CONFIG);
		
		DEFAULT_PUNISH = config.getInteger("DefaultPunish", 2);
		DEFAULT_PUNISH_PARAM = config.getInteger("DefaultPunishTime", 0);
		BYPASS_VALIDATION = config.getBoolean("BypassValidation", true);
		GAMEGUARD_ENFORCE = config.getBoolean("GameGuardEnforce", false);
		GAMEGUARD_PROHIBITACTION = config.getBoolean("GameGuardProhibitAction", false);
		ONLY_GM_ITEMS_FREE = config.getBoolean("OnlyGMItemsFree", true);
		ALLOW_L2WALKER = config.getBoolean("AllowL2Walker", false);
		LOG_CHAT = config.getBoolean("LogChat", false);
		LOG_ITEMS = config.getBoolean("LogItems", false);
		GMAUDIT = config.getBoolean("GMAudit", false);
		ILLEGAL_ACTION_AUDIT = config.getBoolean("IllegalActionAudit", false);
		PROTECTED_ROLLDICE = config.getInteger("floodProtectorRollDice", 4200);
		PROTECTED_FIREWORK = config.getInteger("floodProtectorFireWork", 4200);
		PROTECTED_ITEMPETSUMMON = config.getInteger("floodProtectorItemPetSummon", 1600);
		PROTECTED_HEROVOICE = config.getInteger("floodProtectorHeroVoice", 10000);
		PROTECTED_GLOBALCHAT = config.getInteger("floodProtectorGlobalChat", 10000);
		PROTECTED_MULTISELL = config.getInteger("floodProtectorMultiSell", 1000);
		PROTECTED_SUBCLASS = config.getInteger("floodProtectorSubClass", 2000);
		PROTECTED_DROPITEM = config.getInteger("floodProtectorDropItem", 1000);
		PROTECTED_BYPASS = config.getInteger("floodProtectorBypass", 500);
	}
	
	/**
	 * Read {@link #FILE_TELNET}
	 */
	private static void readTelnet()
	{
		var telnetSettings = new L2Properties(FILE_TELNET);
		IS_TELNET_ENABLED = telnetSettings.getBoolean("EnableTelnet", false);
	}
	
	/**
	 * Read {@link #FILE_GM_CONFIG}
	 */
	private static void readGm()
	{
		var config = new L2Properties(FILE_GM_CONFIG);
		
		GM_SECURE_CHECK = config.getBoolean("GMSecureCheck", true);
		GM_DISABLE_TRANSACTION = config.getBoolean("GMDisableTransaction", false);
		GM_NAME_COLOR_ENABLED = config.getBoolean("GMNameColorEnabled", false);
		GM_NAME_COLOR = Integer.decode("0x" + config.getString("GMNameColor", "FFFF00"));
		ADMIN_NAME_COLOR = Integer.decode("0x" + config.getString("AdminNameColor", "00FF00"));
		GM_HERO_AURA = config.getBoolean("GMHeroAura", true);
		GM_STARTUP_INVULNERABLE = config.getBoolean("GMStartupInvulnerable", true);
		GM_STARTUP_INVISIBLE = config.getBoolean("GMStartupInvisible", true);
		GM_STARTUP_SILENCE = config.getBoolean("GMStartupSilence", true);
		GM_STARTUP_AUTO_LIST = config.getBoolean("GMStartupAutoList", true);
		PETITIONING_ALLOWED = config.getBoolean("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = config.getInteger("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = config.getInteger("MaxPetitionsPending", 25);
	}
	
	/**
	 * Read {@link #FILE_RATES_CONFIG}
	 */
	private static void readRates()
	{
		var config = new L2Properties(FILE_RATES_CONFIG);
		
		// chance items
		DROP_CHANCE_ITEMS = config.getDouble("DropChanceItems", 1.00);
		DROP_CHANCE_ITEMS_BY_ID = config.getMapDouble("DropChanceItemsById");
		DROP_CHANCE_ADENA = config.getDouble("DropChanceAdena", 1.00);
		DROP_CHANCE_QUESTS_REWARD = config.getDouble("DropChanceQuestsReward", 1.00);
		DROP_CHANCE_DROP_QUEST = config.getDouble("DropChanceQuest", 1.00);
		DROP_CHANCE_RAID = config.getDouble("DropChanceRaid", 1.00);
		// amount items
		DROP_AMOUNT_ITEMS = config.getDouble("DropAmountItems", 1.00);
		DROP_AMOUNT_ITEMS_BY_ID = config.getMapDouble("DropAmountItemsById");
		
		DROP_AMOUNT_ADENA = config.getDouble("DropAmountAdena", 1.00);
		DROP_AMOUNT_SEAL_STONE = config.getDouble("DropAmountSealStone", 1.00);
		DROP_AMOUNT_SPOIL = config.getDouble("DropAmountSpoil", 1.00);
		DROP_AMOUNT_RAID = config.getDouble("DropAmountRaid", 1.00);
		DROP_AMOUNT_MANOR = config.getInteger("DropAmountManor", 1);
		
		RATE_XP = config.getDouble("RateXp", 1.00);
		RATE_SP = config.getDouble("RateSp", 1.00);
		RATE_PARTY_XP = config.getDouble("RatePartyXp", 1.00);
		RATE_PARTY_SP = config.getDouble("RatePartySp", 1.00);
		// Defines some Party XP related values
		PARTY_XP_CUTOFF_METHOD = config.getString("PartyXpCutoffMethod", "percentage");
		PARTY_XP_CUTOFF_PERCENT = config.getDouble("PartyXpCutoffPercent", 3.0);
		PARTY_XP_CUTOFF_LEVEL = config.getInteger("PartyXpCutoffLevel", 30);
		// alternate rate xp/sp
		ALT_GAME_EXPONENT_XP = config.getDouble("AltGameExponentXp", 0.00);
		ALT_GAME_EXPONENT_SP = config.getDouble("AltGameExponentSp", 0.00);
		
		RATE_CONSUMABLE_COST = config.getDouble("RateConsumableCost", 1.00);
		RATE_KARMA_EXP_LOST = config.getDouble("RateKarmaExpLost", 1.0);
		RATE_SIEGE_GUARDS_PRICE = config.getDouble("RateSiegeGuardsPrice", 1.0);
		PLAYER_DROP_LIMIT = config.getInteger("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = config.getInteger("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = config.getInteger("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = config.getInteger("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = config.getInteger("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = config.getDouble("PetXpRate", 1.0);
		PET_FOOD_RATE = config.getInteger("PetFoodRate", 1);
		SINEATER_XP_RATE = config.getDouble("SinEaterXpRate", 1.0);
		KARMA_DROP_LIMIT = config.getInteger("KarmaDropLimit", 10);
		KARMA_RATE_DROP = config.getInteger("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = config.getInteger("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = config.getInteger("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = config.getInteger("KarmaRateDropEquipWeapon", 10);
		
		DEEPBLUE_DROP_RULES = config.getBoolean("UseDeepBlueDropRules", true);
		
		// CAN_SPOIL_LOWER_LEVEL_MOBS = Boolean.parseBoolean(ratesSettings.getProperty("CanSpoilLowerLevelMobs", false);
		// CAN_DELEVEL_AND_SPOIL_MOBS = Boolean.parseBoolean(ratesSettings.getProperty("CanDelevelToSpoil", true);
		// MAXIMUM_PLAYER_AND_MOB_LEVEL_DIFFERENCE = Float.parseFloat(ratesSettings.getProperty("MaximumPlayerAndMobLevelDifference", "9.00"));
		// BASE_SPOIL_RATE = Float.parseFloat(ratesSettings.getProperty("BasePercentChanceOfSpoilSuccess", "40.00"));
		// MINIMUM_SPOIL_RATE = Float.parseFloat(ratesSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", "3."));
		// SPOIL_LEVEL_DIFFERENCE_LIMIT = Float.parseFloat(ratesSettings.getProperty("SpoilLevelDifferenceLimit", "5.00"));
		// SPOIL_LEVEL_DIFFERENCE_MULTIPLIER = Float.parseFloat(ratesSettings.getProperty("SpoilLevelMultiplier", "7.00"));
		// LAST_LEVEL_SPOIL_IS_LEARNED = Integer.parseInt(ratesSettings.getProperty("LastLevelSpoilIsLearned", "72"));
	}
	
	/**
	 * Read {@link #FILE_CLANHALL_CONFIG}
	 */
	private static void readClanHall()
	{
		var config = new L2Properties(FILE_CLANHALL_CONFIG);
		
		CH_TELE_FEE_RATIO = config.getLong("ClanHallTeleportFunctionFeeRatio", 604800000);
		CH_TELE1_FEE = config.getInteger("ClanHallTeleportFunctionFeeLvl1", 86400000);
		CH_TELE2_FEE = config.getInteger("ClanHallTeleportFunctionFeeLvl2", 86400000);
		CH_TELE3_FEE = config.getInteger("ClanHallTeleportFunctionFeeLvl3", 86400000);
		CH_SUPPORT_FEE_RATIO = config.getLong("ClanHallSupportFunctionFeeRatio", 86400000);
		CH_SUPPORT1_FEE = config.getInteger("ClanHallSupportFeeLvl1", 86400000);
		CH_SUPPORT2_FEE = config.getInteger("ClanHallSupportFeeLvl2", 86400000);
		CH_SUPPORT3_FEE = config.getInteger("ClanHallSupportFeeLvl3", 86400000);
		CH_SUPPORT4_FEE = config.getInteger("ClanHallSupportFeeLvl4", 86400000);
		CH_SUPPORT5_FEE = config.getInteger("ClanHallSupportFeeLvl5", 86400000);
		CH_SUPPORT6_FEE = config.getInteger("ClanHallSupportFeeLvl6", 86400000);
		CH_SUPPORT7_FEE = config.getInteger("ClanHallSupportFeeLvl7", 86400000);
		CH_SUPPORT8_FEE = config.getInteger("ClanHallSupportFeeLvl8", 86400000);
		CH_MPREG_FEE_RATIO = config.getLong("ClanHallMpRegenerationFunctionFeeRatio", 86400000);
		CH_MPREG1_FEE = config.getInteger("ClanHallMpRegenerationFeeLvl1", 86400000);
		CH_MPREG2_FEE = config.getInteger("ClanHallMpRegenerationFeeLvl2", 86400000);
		CH_MPREG3_FEE = config.getInteger("ClanHallMpRegenerationFeeLvl3", 86400000);
		CH_MPREG4_FEE = config.getInteger("ClanHallMpRegenerationFeeLvl4", 86400000);
		CH_MPREG5_FEE = config.getInteger("ClanHallMpRegenerationFeeLvl5", 86400000);
		CH_HPREG_FEE_RATIO = config.getLong("ClanHallHpRegenerationFunctionFeeRatio", 86400000);
		CH_HPREG1_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl1", 86400000);
		CH_HPREG2_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl2", 86400000);
		CH_HPREG3_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl3", 86400000);
		CH_HPREG4_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl4", 86400000);
		CH_HPREG5_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl5", 86400000);
		CH_HPREG6_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl6", 86400000);
		CH_HPREG7_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl7", 86400000);
		CH_HPREG8_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl8", 86400000);
		CH_HPREG9_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl9", 86400000);
		CH_HPREG10_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl10", 86400000);
		CH_HPREG11_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl11", 86400000);
		CH_HPREG12_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl12", 86400000);
		CH_HPREG13_FEE = config.getInteger("ClanHallHpRegenerationFeeLvl13", 86400000);
		CH_EXPREG_FEE_RATIO = config.getLong("ClanHallExpRegenerationFunctionFeeRatio", 86400000);
		CH_EXPREG1_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl1", 86400000);
		CH_EXPREG2_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl2", 86400000);
		CH_EXPREG3_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl3", 86400000);
		CH_EXPREG4_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl4", 86400000);
		CH_EXPREG5_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl5", 86400000);
		CH_EXPREG6_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl6", 86400000);
		CH_EXPREG7_FEE = config.getInteger("ClanHallExpRegenerationFeeLvl7", 86400000);
		CH_ITEM_FEE_RATIO = config.getLong("ClanHallItemCreationFunctionFeeRatio", 86400000);
		CH_ITEM1_FEE = config.getInteger("ClanHallItemCreationFunctionFeeLvl1", 86400000);
		CH_ITEM2_FEE = config.getInteger("ClanHallItemCreationFunctionFeeLvl2", 86400000);
		CH_ITEM3_FEE = config.getInteger("ClanHallItemCreationFunctionFeeLvl3", 86400000);
		CH_CURTAIN_FEE_RATIO = config.getLong("ClanHallCurtainFunctionFeeRatio", 86400000);
		CH_CURTAIN1_FEE = config.getInteger("ClanHallCurtainFunctionFeeLvl1", 86400000);
		CH_CURTAIN2_FEE = config.getInteger("ClanHallCurtainFunctionFeeLvl2", 86400000);
		CH_FRONT_FEE_RATIO = config.getLong("ClanHallFrontPlatformFunctionFeeRatio", 86400000);
		CH_FRONT1_FEE = config.getInteger("ClanHallFrontPlatformFunctionFeeLvl1", 86400000);
		CH_FRONT2_FEE = config.getInteger("ClanHallFrontPlatformFunctionFeeLvl2", 86400000);
	}
	
	/**
	 * Read {@link #FILE_PVP_CONFIG}
	 */
	private static void readPvP()
	{
		var config = new L2Properties(FILE_PVP_CONFIG);
		
		KARMA_MIN_KARMA = config.getInteger("MinKarma", 240);
		KARMA_MAX_KARMA = config.getInteger("MaxKarma", 10000);
		KARMA_XP_DIVIDER = config.getInteger("XPDivider", 260);
		KARMA_LOST_BASE = config.getInteger("BaseKarmaLost", 0);
		KARMA_DROP_GM = config.getBoolean("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = config.getBoolean("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = config.getInteger("MinimumPKRequiredToDrop", 5);
		KARMA_LIST_NONDROPPABLE_PET_ITEMS = config.getList("ListOfPetItems");
		KARMA_LIST_NONDROPPABLE_ITEMS = config.getList("ListOfNonDroppableItems");
		PVP_NORMAL_TIME = config.getInteger("PvPVsNormalTime", 15000);
		PVP_PVP_TIME = config.getInteger("PvPVsPvPTime", 30000);
		LIST_NONDROPPABLE_ITEMS = config.getList("ListOfNonDroppableItems");
	}
	
	/**
	 * Read {@link #FILE_IDFACTORY_CONFIG}
	 */
	private static void readFactory()
	{
		var config = new L2Properties(FILE_IDFACTORY_CONFIG);
		IDFACTORY_TYPE = IdFactoryType.valueOf(config.getString("IDFactory", "Compaction"));
		BAD_ID_CHECKING = config.getBoolean("BadIdChecking", true);
	}
	
	/**
	 * Read {@link #FILE_LOGIN_CONFIG}
	 */
	private static void readLogin()
	{
		var config = new L2Properties(FILE_LOGIN_CONFIG);
		
		GAME_SERVER_LOGIN_HOST = config.getString("LoginserverHostname", "127.0.0.1");
		GAME_SERVER_LOGIN_PORT = config.getInteger("LoginPort", 9013);
		PORT_LOGIN = config.getInteger("LoginserverPort", 2106);
		
		ACCEPT_NEW_GAMESERVER = config.getBoolean("AcceptNewGameServer", true);
		REQUEST_ID = config.getInteger("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = config.getBoolean("AcceptAlternateID", true);
		
		LOGIN_TRY_BEFORE_BAN = config.getInteger("LoginTryBeforeBan", 10);
		
		LOGIN_BLOCK_AFTER_BAN = config.getInteger("LoginBlockAfterBan", 600);
		
		EXTERNAL_HOSTNAME = config.getString("ExternalHostname", "localhost");
		
		DATABASE_DRIVER = config.getString("Driver", "com.mysql.jdbc.Driver");
		DATABASE_URL = config.getString("URL", "jdbc:mysql://localhost/l2jdb");
		DATABASE_LOGIN = config.getString("Login", "root");
		DATABASE_PASSWORD = config.getString("Password", "");
		
		SHOW_LICENCE = config.getBoolean("ShowLicence", true);
		IP_UPDATE_TIME = config.getInteger("IpUpdateTime", 15);
		FORCE_GGAUTH = config.getBoolean("ForceGGAuth", false);
		
		AUTO_CREATE_ACCOUNTS = config.getBoolean("AutoCreateAccounts", true);
		
		FLOOD_PROTECTION = config.getBoolean("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = config.getInteger("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = config.getInteger("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = config.getInteger("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = config.getInteger("MaxConnectionPerIP", 50);
	}
}
