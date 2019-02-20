package l2j.gameserver.handler.command.admin;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.instancemanager.race.MonsterRace;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.DeleteObject;
import l2j.gameserver.network.external.server.MonRaceInfo;
import l2j.gameserver.network.external.server.PlaySound;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.1.6.4 $ $Date: 2005/04/11 10:06:00 $
 */
public class AdminMonsterRace implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_mons"
	};
	
	protected static int state = -1;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equalsIgnoreCase("admin_mons"))
		{
			handleSendPacket(activeChar);
		}
		return true;
	}
	
	private void handleSendPacket(L2PcInstance activeChar)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race 8003 to 8027
		 */
		int[][] codes =
		{
			{
				-1,
				0
			},
			{
				0,
				15322
			},
			{
				13765,
				-1
			},
			{
				-1,
				0
			}
		};
		MonsterRace race = MonsterRace.getInstance();
		
		if (state == -1)
		{
			state++;
			race.newRace();
			race.newSpeeds();
			MonRaceInfo spk = new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds());
			activeChar.sendPacket(spk);
			activeChar.broadcastPacket(spk);
		}
		else if (state == 0)
		{
			state++;
			activeChar.sendPacket(new SystemMessage(SystemMessage.MONSRACE_RACE_START).addNumber(0));
			
			Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new PlaySound(PlaySoundType.MUSIC_S_RACE), -1);
			Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new PlaySound(PlaySoundType.RACE_START, 12125, 182487, -3559), -1);
			Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()), -1);
			
			ThreadPoolManager.schedule(new RunRace(codes, activeChar), 5000);
		}
	}
	
	class RunRace implements Runnable
	{
		
		private final int[][] codes;
		private final L2PcInstance activeChar;
		
		public RunRace(int[][] pCodes, L2PcInstance pActiveChar)
		{
			codes = pCodes;
			activeChar = pActiveChar;
		}
		
		@Override
		public void run()
		{
			MonRaceInfo spk = new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
			Broadcast.toSelfAndKnownPlayersInRadius(activeChar, spk, -1);
			ThreadPoolManager.schedule(new RunEnd(activeChar), 30000);
		}
	}
	
	class RunEnd implements Runnable
	{
		private final L2PcInstance activeChar;
		
		public RunEnd(L2PcInstance pActiveChar)
		{
			activeChar = pActiveChar;
		}
		
		@Override
		public void run()
		{
			DeleteObject obj = null;
			for (int i = 0; i < 8; i++)
			{
				obj = new DeleteObject(MonsterRace.getInstance().getMonsters()[i]);
				Broadcast.toSelfAndKnownPlayersInRadius(activeChar, obj, -1);
			}
			state = -1;
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
