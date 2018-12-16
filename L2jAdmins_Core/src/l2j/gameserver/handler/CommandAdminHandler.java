package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.command.admin.AdminAdmin;
import l2j.gameserver.handler.command.admin.AdminAnnouncements;
import l2j.gameserver.handler.command.admin.AdminBanChar;
import l2j.gameserver.handler.command.admin.AdminBanChat;
import l2j.gameserver.handler.command.admin.AdminConfigs;
import l2j.gameserver.handler.command.admin.AdminCreateItem;
import l2j.gameserver.handler.command.admin.AdminDelete;
import l2j.gameserver.handler.command.admin.AdminDoorControl;
import l2j.gameserver.handler.command.admin.AdminEditChar;
import l2j.gameserver.handler.command.admin.AdminEditNpc;
import l2j.gameserver.handler.command.admin.AdminEffects;
import l2j.gameserver.handler.command.admin.AdminEnchant;
import l2j.gameserver.handler.command.admin.AdminFightCalculator;
import l2j.gameserver.handler.command.admin.AdminGmChat;
import l2j.gameserver.handler.command.admin.AdminHelpPage;
import l2j.gameserver.handler.command.admin.AdminHelpTarget;
import l2j.gameserver.handler.command.admin.AdminKill;
import l2j.gameserver.handler.command.admin.AdminMammon;
import l2j.gameserver.handler.command.admin.AdminManor;
import l2j.gameserver.handler.command.admin.AdminMonsterRace;
import l2j.gameserver.handler.command.admin.AdminOlympiad;
import l2j.gameserver.handler.command.admin.AdminPForge;
import l2j.gameserver.handler.command.admin.AdminPetition;
import l2j.gameserver.handler.command.admin.AdminPledge;
import l2j.gameserver.handler.command.admin.AdminQuest;
import l2j.gameserver.handler.command.admin.AdminReload;
import l2j.gameserver.handler.command.admin.AdminRepairChar;
import l2j.gameserver.handler.command.admin.AdminRes;
import l2j.gameserver.handler.command.admin.AdminRideWyvern;
import l2j.gameserver.handler.command.admin.AdminShutdown;
import l2j.gameserver.handler.command.admin.AdminSiege;
import l2j.gameserver.handler.command.admin.AdminSkill;
import l2j.gameserver.handler.command.admin.AdminSpawn;
import l2j.gameserver.handler.command.admin.AdminTarget;
import l2j.gameserver.handler.command.admin.AdminTeleport;
import l2j.gameserver.handler.command.admin.AdminTest;
import l2j.gameserver.handler.command.admin.AdminUnblockIp;
import l2j.gameserver.handler.command.admin.AdminZone;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.util.UtilPrint;

/**
 * @author fissban
 */
public class CommandAdminHandler
{
	// Interface
	public interface IAdminCommandHandler
	{
		public boolean useAdminCommand(String command, L2PcInstance activeChar);
		
		public String[] getAdminCommandList();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(CommandAdminHandler.class.getName());
	// Instances
	private static final Map<String, IAdminCommandHandler> admin = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerAdminCommand(new AdminAdmin());
		registerAdminCommand(new AdminAnnouncements());
		registerAdminCommand(new AdminBanChar());
		registerAdminCommand(new AdminBanChat());
		registerAdminCommand(new AdminConfigs());
		registerAdminCommand(new AdminCreateItem());
		registerAdminCommand(new AdminDelete());
		registerAdminCommand(new AdminDoorControl());
		registerAdminCommand(new AdminEditChar());
		registerAdminCommand(new AdminEditNpc());
		registerAdminCommand(new AdminEffects());
		registerAdminCommand(new AdminEnchant());
		registerAdminCommand(new AdminFightCalculator());
		registerAdminCommand(new AdminGmChat());
		registerAdminCommand(new AdminHelpPage());
		registerAdminCommand(new AdminHelpTarget());
		registerAdminCommand(new AdminKill());
		registerAdminCommand(new AdminMammon());
		registerAdminCommand(new AdminManor());
		registerAdminCommand(new AdminMonsterRace());
		registerAdminCommand(new AdminOlympiad());
		registerAdminCommand(new AdminPetition());
		registerAdminCommand(new AdminPForge());
		registerAdminCommand(new AdminPledge());
		registerAdminCommand(new AdminQuest());
		registerAdminCommand(new AdminReload());
		registerAdminCommand(new AdminRepairChar());
		registerAdminCommand(new AdminRes());
		registerAdminCommand(new AdminRideWyvern());
		registerAdminCommand(new AdminShutdown());
		registerAdminCommand(new AdminSiege());
		registerAdminCommand(new AdminSkill());
		registerAdminCommand(new AdminSpawn());
		registerAdminCommand(new AdminTarget());
		registerAdminCommand(new AdminTeleport());
		registerAdminCommand(new AdminTest());
		registerAdminCommand(new AdminUnblockIp());
		registerAdminCommand(new AdminZone());
		
		UtilPrint.result("CommandAdminHandler", "Loaded handlers", size());
	}
	
	/**
	 * For admin commands
	 * @param handler
	 */
	public static void registerAdminCommand(IAdminCommandHandler handler)
	{
		for (String id : handler.getAdminCommandList())
		{
			admin.put(id.toLowerCase(), handler);
		}
	}
	
	public static IAdminCommandHandler getHandler(String adminCommand)
	{
		String command = adminCommand;
		if (adminCommand.contains(" "))
		{
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}
		return admin.get(command.toLowerCase());
	}
	
	public static int size()
	{
		return admin.size();
	}
	
	public static CommandAdminHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommandAdminHandler INSTANCE = new CommandAdminHandler();
	}
}
