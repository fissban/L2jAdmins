package l2j.gameserver.handler.command.admin;

import java.util.List;
import java.util.StringTokenizer;

import l2j.gameserver.data.SpawnData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.spawn.AutoSpawnManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.AutoSpawnHolder;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Admin Command Handler for Mammon NPCs
 * @author Tempy
 */
public class AdminMammon implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_mammon_find",
		"admin_mammon_respawn",
		"admin_list_spawns",
		"admin_msg"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		boolean isSealValidation = SevenSignsManager.getInstance().isSealValidationPeriod();
		
		int npcId = 0;
		int teleportIndex = -1;
		AutoSpawnHolder blackSpawnInst = AutoSpawnManager.getInstance().getSpawns(SevenSignsManager.MAMMON_BLACKSMITH_ID, false);
		AutoSpawnHolder merchSpawnInst = AutoSpawnManager.getInstance().getSpawns(SevenSignsManager.MAMMON_MERCHANT_ID, false);
		
		// ----------~ COMMAND ~---------- //
		if (event.equals("admin_mammon_find"))
		{
			try
			{
				if (st.hasMoreTokens())
				{
					teleportIndex = Integer.parseInt(st.nextToken());
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Command format is //mammon_find <teleportIndex> (where 1 = Blacksmith, 2 = Merchant)");
			}
			
			if (!isSealValidation)
			{
				activeChar.sendMessage("The competition period is currently in effect.");
				return true;
			}
			
			List<L2Npc> blackInst = blackSpawnInst.getNpcInstanceList();
			List<L2Npc> merchInst = merchSpawnInst.getNpcInstanceList();
			
			if (blackInst.size() > 0)
			{
				activeChar.sendMessage("Blacksmith of Mammon: " + blackInst.get(0).getX() + " " + blackInst.get(0).getY() + " " + blackInst.get(0).getZ());
				
				if (teleportIndex == 0)
				{
					activeChar.teleToLocation(blackInst.get(0).getX(), blackInst.get(0).getY(), blackInst.get(0).getZ(), true);
				}
			}
			
			if (merchInst.size() > 0)
			{
				activeChar.sendMessage("Merchant of Mammon: " + merchInst.get(0).getX() + " " + merchInst.get(0).getY() + " " + merchInst.get(0).getZ());
				
				if (teleportIndex == 1)
				{
					activeChar.teleToLocation(merchInst.get(0).getX(), merchInst.get(0).getY(), merchInst.get(0).getZ(), true);
				}
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_mammon_respawn"))
		{
			if (!isSealValidation)
			{
				activeChar.sendMessage("The competition period is currently in effect.");
				return true;
			}
			
			long blackRespawn = AutoSpawnManager.getInstance().getTimeToNextSpawn(blackSpawnInst);
			long merchRespawn = AutoSpawnManager.getInstance().getTimeToNextSpawn(merchSpawnInst);
			
			activeChar.sendMessage("The Merchant of Mammon will respawn in " + (merchRespawn / 60000) + " minute(s).");
			activeChar.sendMessage("The Blacksmith of Mammon will respawn in " + (blackRespawn / 60000) + " minute(s).");
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_list_spawns"))
		{
			try
			{
				npcId = Integer.parseInt(st.nextToken());
				
				if (st.hasMoreTokens())
				{
					teleportIndex = Integer.parseInt(st.nextToken());
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Command format is //list_spawns <NPC_ID> <TELE_INDEX>");
			}
			
			SpawnData.getInstance().findNPCInstances(activeChar, npcId, teleportIndex);
		}
		// ----------~ COMMAND ~---------- //
		else if (event.equals("admin_msg"))
		{
			int msgId = -1;
			
			try
			{
				msgId = Integer.parseInt(st.nextToken());
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Command format: //msg <SYSTEM_MSG_ID>");
				return true;
			}
			
			activeChar.sendPacket(new SystemMessage(msgId));
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
