package l2j.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.Server;
import l2j.gameserver.data.AdminCommandData;
import l2j.gameserver.data.AnnouncementsData;
import l2j.gameserver.data.ArmorSetsData;
import l2j.gameserver.data.AuctionData;
import l2j.gameserver.data.AutoSpawnData;
import l2j.gameserver.data.BoatData;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.CharNameData;
import l2j.gameserver.data.CharTemplateData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.data.ClanHallData;
import l2j.gameserver.data.CrestData;
import l2j.gameserver.data.CubicData;
import l2j.gameserver.data.DimensionalRiftData;
import l2j.gameserver.data.DoorData;
import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.ExtractableItemsData;
import l2j.gameserver.data.FishTable;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.data.HennaData;
import l2j.gameserver.data.HennaTreeData;
import l2j.gameserver.data.HeroData;
import l2j.gameserver.data.HitConditionBonusData;
import l2j.gameserver.data.HtmData;
import l2j.gameserver.data.InitialEquipamentData;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.data.MultisellData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.NpcDrops;
import l2j.gameserver.data.NpcWalkerRoutesData;
import l2j.gameserver.data.OfflineTradersData;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.data.RecipeData;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillSpellbookData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.data.SoulCrystalData;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.data.StaticObjects;
import l2j.gameserver.data.SummonItemsData;
import l2j.gameserver.data.TeleportLocationData;
import l2j.gameserver.data.TradeControllerData;
import l2j.gameserver.data.ZoneData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.ActionHandler;
import l2j.gameserver.handler.ActionShiftHandler;
import l2j.gameserver.handler.BypassHandler;
import l2j.gameserver.handler.CommandAdminHandler;
import l2j.gameserver.handler.CommandUserHandler;
import l2j.gameserver.handler.CommandVoicedHandler;
import l2j.gameserver.handler.CommunityHandler;
import l2j.gameserver.handler.ItemHandler;
import l2j.gameserver.handler.SayHandler;
import l2j.gameserver.handler.SkillHandler;
import l2j.gameserver.handler.TargetHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.instancemanager.MercTicketManager;
import l2j.gameserver.instancemanager.communitybbs.Community;
import l2j.gameserver.instancemanager.race.MonsterRace;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsFestival;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.spawn.DayNightSpawnManager;
import l2j.gameserver.model.olympiad.Olympiad;
import l2j.gameserver.model.olympiad.OlympiadGameManager;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.GameClient;
import l2j.gameserver.network.PacketHandler;
import l2j.gameserver.network.thread.LoginServerThread;
import l2j.gameserver.task.TaskManager;
import l2j.mmocore.SelectorConfig;
import l2j.mmocore.SelectorThread;
import l2j.status.Status;
import l2j.util.DeadLockDetector;
import l2j.util.IPv4Filter;
import l2j.util.UtilPrint;
import main.EngineModsManager;

/**
 * This class ...
 * @version $Revision: 1.29.2.15.2.19 $ $Date: 2005/04/05 19:41:23 $
 */
public class GameServer
{
	private static final Logger LOG = Logger.getLogger(GameServer.class.getName());
	
	private final SelectorThread<GameClient> selectorThread;
	private final IdFactory idFactory;
	public static GameServer gameServer;
	
	public static Status statusServer;
	
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	private long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576
	}
	
	public SelectorThread<GameClient> getSelectorThread()
	{
		return selectorThread;
	}
	
	public GameServer() throws Exception
	{
		final long serverLoadStart = System.currentTimeMillis();
		
		LOG.finest(getClass().getSimpleName() + " used mem:" + getUsedMemoryMB() + "MB");
		
		idFactory = IdFactory.getInstance();
		if (!idFactory.isInitialized())
		{
			LOG.severe("GameServer: Could not read object IDs from DB. Please Check Your Data.");
			throw new Exception("GameServer: Could not initialize the ID factory");
		}
		
		ThreadPoolManager.getInstance();
		
		new File(Config.DATAPACK_ROOT, "data/clans").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/crests").mkdirs();
		
		UtilPrint.section("EngineMods Data");
		EngineModsManager.loadData();
		
		// Start game time control early
		UtilPrint.section("World");
		L2World.getInstance();
		MapRegionData.getInstance();
		
		UtilPrint.section("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		UtilPrint.section("Task Managers");
		TaskManager.getInstance().init();
		
		UtilPrint.section("Skills");
		SkillData.getInstance().load();
		SkillTreeData.getInstance().load();
		ArmorSetsData.getInstance().load();
		FishTable.getInstance().load();
		HitConditionBonusData.getInstance().load();
		if (Config.SP_BOOK_NEEDED)
		{
			SkillSpellbookData.getInstance();
		}
		CubicData.getInstance().load();
		
		UtilPrint.section("Items");
		ExtractableItemsData.getInstance();
		SummonItemsData.getInstance().load();
		TradeControllerData.getInstance().load();
		MultisellData.getInstance().load();
		
		ItemData.getInstance();
		RecipeData.getInstance().load();
		
		UtilPrint.section("Community");
		if (Config.COMMUNITY_ENABLE)
		{
			Community.getInstance();
		}
		
		UtilPrint.section("Clans");
		// Load clan hall data before zone data
		ClanData.getInstance();
		CrestData.load();
		
		UtilPrint.section("Auctions");
		AuctionData.getInstance().load();
		
		UtilPrint.section("Npc");
		NpcData.getInstance();
		NpcDrops.getInstance().load();
		StaticObjects.getInstance();
		if (Config.ALLOW_NPC_WALKERS)
		{
			NpcWalkerRoutesData.getInstance();
		}
		PetDataData.getInstance().loadPetsData();
		
		UtilPrint.section("Spawns Manager");
		SpawnData.getInstance().load();
		RaidBossSpawnData.getInstance().load();
		DayNightSpawnManager.getInstance().notifyChangeMode();
		GrandBossSpawnData.getInstance().load();
		AutoSpawnData.getInstance().load();
		BoatData.getInstance().load();
		
		UtilPrint.section("Soul Crystal");
		SoulCrystalData.getInstance().load();
		
		UtilPrint.section("Castles");
		CastleData.getInstance().load();
		
		UtilPrint.section("Clan Halls");
		ClanHallData.getInstance().load();
		
		UtilPrint.section("Zones");
		ZoneData.getInstance();
		
		UtilPrint.section("Doors");
		DoorData.getInstance().load();
		
		UtilPrint.section("Cache");
		HtmData.getInstance();
		// CrestCache.getInstance();
		TeleportLocationData.getInstance().load();
		AdminCommandData.getInstance().load();
		
		UtilPrint.section("Announcement");
		AnnouncementsData.getInstance().load();
		
		UtilPrint.section("Characters");
		ExperienceData.getInstance().load();
		CharTemplateData.getInstance().load();
		GmListData.getInstance();
		CharNameData.getInstance();
		HennaData.getInstance();
		HennaTreeData.getInstance();
		InitialEquipamentData.getInstance().load();
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
		{
			OfflineTradersData.restoreOfflineTraders();
		}
		
		UtilPrint.section("Olympiad & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		HeroData.getInstance();
		
		UtilPrint.section("Manor");
		ManorData.getInstance().load();
		CastleManorManager.getInstance();
		MercTicketManager.getInstance();
		// PetitionManager.getInstance();
		
		UtilPrint.section("Monster Race");
		MonsterRace.getInstance();
		UtilPrint.section("Seven Signs");
		SevenSignsManager.getInstance();
		SevenSignsFestival.getInstance();
		UtilPrint.section("Dimensional Rift");
		DimensionalRiftData.getInstance().load();
		
		UtilPrint.section("Handlers");
		ActionHandler.getInstance().init();
		ActionShiftHandler.getInstance().init();
		BypassHandler.getInstance().init();
		CommandAdminHandler.getInstance().init();
		CommandUserHandler.getInstance().init();
		CommandVoicedHandler.getInstance().init();
		CommunityHandler.getInstance().init();
		ItemHandler.getInstance().init();
		SayHandler.getInstance().init();
		SkillHandler.getInstance().init();
		TargetHandler.getInstance().init();
		
		UtilPrint.section("Scripts");
		ScriptsData.getInstance().load();
		
		// Start Engine scripts
		UtilPrint.section("EngineMods Scripts");
		EngineModsManager.loadScripts();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		
		System.gc();
		
		UtilPrint.section("GameServer");
		// maxMemory is the upper limit the jvm can use, totalMemory the size of the current allocation pool, freeMemory the unused memory in the allocation pool
		long freeMem = ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory()) + Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		LOG.info("GameServer: Started, free memory " + freeMem + " Mb of " + totalMem + " Mb");
		LOG.info("GameServer: Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS);
		
		UtilPrint.section("Login");
		LoginServerThread.getInstance().start();
		
		var sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		var handler = new PacketHandler();
		selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (Exception e)
			{
				LOG.severe("The GameServer bind address is invalid, using all available IPs.");
			}
		}
		
		try
		{
			selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (Exception e)
		{
			LOG.severe("Failed to open server socket.");
			System.exit(1);
		}
		selectorThread.start();
		
		UtilPrint.result("GameServer", "Server loaded in", ((System.currentTimeMillis() - serverLoadStart) / 1000) + " seconds");
	}
	
	public static void main(String[] args) throws Exception
	{
		Server.SERVER_MODE = Server.MODE_GAMESERVER;
		
		// Local Constants
		final String LOG_FOLDER = "log"; // Name of folder for log file
		final String LOG_NAME = "./log.cfg"; // Name of log file
		
		/*** Main ***/
		// Create log folder
		File logFolder = new File(Config.DATAPACK_ROOT, LOG_FOLDER);
		logFolder.mkdir();
		
		Config.load();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File(LOG_NAME)))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		catch (Exception e)
		{
			LOG.warning("GameServer: Error loading " + LOG_NAME);
		}
		
		UtilPrint.section("Database");
		DatabaseManager.getInstance();
		gameServer = new GameServer();
		
		if (Config.IS_TELNET_ENABLED)
		{
			statusServer = new Status(Server.SERVER_MODE);
			statusServer.start();
		}
		else
		{
			LOG.info("Telnet server is currently disabled.");
		}
	}
}
