package l2j.gameserver.handler.command.admin;

import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.data.SpawnData;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * Comando para la eliminacion de los mobs o npcs.<br>
 * Contiene el comando: <br>
 * <li>delete
 */
public class AdminDelete implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_delete"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_delete"))
		{
			L2Object obj = activeChar.getTarget();
			if ((obj != null) && (obj instanceof L2Npc))
			{
				L2Npc target = (L2Npc) obj;
				target.deleteMe();
				
				Spawn spawn = target.getSpawn();
				if (spawn != null)
				{
					spawn.stopRespawn();
					
					if (RaidBossSpawnData.getInstance().isDefined(spawn.getNpcId()))
					{
						RaidBossSpawnData.getInstance().deleteSpawn(spawn, true);
					}
					else
					{
						SpawnData.getInstance().deleteSpawn(spawn, true);
					}
				}
				activeChar.sendMessage("Deleted " + target.getName() + ".");
			}
			else
			{
				activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
