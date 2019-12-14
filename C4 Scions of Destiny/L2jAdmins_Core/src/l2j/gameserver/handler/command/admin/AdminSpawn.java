package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2j.Config;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.data.TeleportLocationData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.instancemanager.spawn.DayNightSpawnManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;
import l2j.gameserver.util.audit.GMAudit;

/**
 * This class handles following admin commands: - show_spawns = shows menu - spawn_index lvl = shows menu for monsters with respective level - spawn_monster id = spawns monster id on target
 * @version $Revision: 1.2.2.5.2.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminSpawn implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_spawn",
		"admin_spawn_monster",
		"admin_unspawnall",
		"admin_respawnall",
		"admin_spawn_reload",
		"admin_teleport_reload",
		"admin_spawnnight",
		"admin_spawnday"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		// Generamos un log con el COMMAND usado por el GM
		GMAudit.auditGMAction(activeChar.getName(), command, "", "");
		
		// ----------~ COMMAND ~---------- //
		if (command.startsWith("admin_spawn") || command.startsWith("admin_spawn_monster"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			try
			{
				st.nextToken();
				String id = st.nextToken();
				int respawnTime = 0;
				// FIXME: 0 time should mean never respawn.
				// At the moment it will just be set to d else where.
				int mobCount = 1;
				if (st.hasMoreTokens())
				{
					mobCount = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens())
				{
					respawnTime = Integer.parseInt(st.nextToken());
				}
				spawnMonster(activeChar, id, respawnTime, mobCount);
			}
			catch (Exception e)
			{
				// Case of wrong monster data
			}
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_unspawnall"))
		{
			Broadcast.toAllOnlinePlayers(SystemMessage.NPC_SERVER_NOT_OPERATING);
			
			RaidBossSpawnData.getInstance().saveAllBoss();
			DayNightSpawnManager.getInstance().cleanUp();
			L2World.getInstance().deleteVisibleNpcSpawns();
			GmListData.getInstance().broadcastMessageToGMs("NPC Unspawn completed!");
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_spawnday"))
		{
			DayNightSpawnManager.getInstance().spawnDayCreatures();
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_spawnnight"))
		{
			DayNightSpawnManager.getInstance().spawnNightCreatures();
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_respawnall") || command.startsWith("admin_spawn_reload"))
		{
			// make sure all spawns are deleted
			RaidBossSpawnData.getInstance().saveAllBoss();
			DayNightSpawnManager.getInstance().cleanUp();
			L2World.getInstance().deleteVisibleNpcSpawns();
			
			// now respawn all
			NpcData.getInstance().reload();
			SpawnData.getInstance().reloadAll();
			RaidBossSpawnData.getInstance().reload();
			GmListData.getInstance().broadcastMessageToGMs("NPC Respawn completed!");
		}
		// ----------~ COMMAND ~---------- //
		else if (command.startsWith("admin_teleport_reload"))
		{
			TeleportLocationData.getInstance().reload();
			GmListData.getInstance().broadcastMessageToGMs("Teleport List Table reloaded.");
		}
		return true;
	}
	
	private void spawnMonster(L2PcInstance activeChar, String monsterId, int respawnTime, int mobCount)
	{
		L2Object target = activeChar.getTarget();
		if (target == null)
		{
			target = activeChar;
		}
		if (target != activeChar)
		{
			return;
		}
		
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher regexp = pattern.matcher(monsterId);
		NpcTemplate template1;
		if (regexp.matches())
		{
			// First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId);
			template1 = NpcData.getInstance().getTemplate(monsterTemplate);
		}
		else
		{
			// //First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template1 = NpcData.getInstance().getTemplateByName(monsterId);
		}
		
		try
		{
			Spawn spawn = new Spawn(template1);
			if (Config.SAVE_GMSPAWN_ON_CUSTOM)
			{
				spawn.setCustom(true);
			}
			
			spawn.setX(target.getX());
			spawn.setY(target.getY());
			spawn.setZ(target.getZ());
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			
			if (RaidBossSpawnData.getInstance().isDefined(spawn.getNpcId()))
			{
				activeChar.sendMessage("You cannot spawn another instance of " + template1.getName() + ".");
			}
			else
			{
				if (RaidBossSpawnData.getInstance().getValidTemplate(spawn.getNpcId()) != null)
				{
					spawn.setRespawnMinDelay(43200);
					spawn.setRespawnMaxDelay(129600);
					RaidBossSpawnData.getInstance().addNewSpawn(spawn, 0, template1.getBaseHpMax(), template1.getBaseMpMax(), true);
				}
				else
				{
					SpawnData.getInstance().addNewSpawn(spawn, true);
					spawn.init();
				}
				
				activeChar.sendMessage("Created " + template1.getName() + " on " + target.getObjectId() + ".");
			}
		}
		catch (Exception e)
		{
			activeChar.sendPacket(SystemMessage.TARGET_IS_NOT_FOUND_IN_THE_GAME);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
