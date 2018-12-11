package l2j.gameserver.handler.command.user;

import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.handler.CommandUserHandler.IUserCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.SystemMessage;

public class UserLoc implements IUserCommandHandler
{
	@Override
	public int[] getUserCommandList()
	{
		return new int[]
		{
			0
		};
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		int nearestTown = MapRegionData.getInstance().getClosestTownNumber(activeChar);
		int msg = 0;
		switch (nearestTown)
		{
			case 0:
				msg = SystemMessage.LOC_TI_S1_S2_S3;
				break;
			case 1:
				msg = SystemMessage.LOC_ELVEN_S1_S2_S3;
				break;
			case 2:
				msg = SystemMessage.LOC_DARK_ELVEN_S1_S2_S3;
				break;
			case 3:
				msg = SystemMessage.LOC_ORC_S1_S2_S3;
				break;
			case 4:
				msg = SystemMessage.LOC_DWARVEN_S1_S2_S3;
				break;
			case 5:
				msg = SystemMessage.LOC_GLUDIO_S1_S2_S3;
				break;
			case 6:
				msg = SystemMessage.LOC_GLUDIN_S1_S2_S3;
				break;
			case 7:
				msg = SystemMessage.LOC_DION_S1_S2_S3;
				break;
			case 8:
				msg = SystemMessage.LOC_GIRAN_S1_S2_S3;
				break;
			case 9:
				msg = SystemMessage.LOC_OREN_S1_S2_S3;
				break;
			case 10:
				msg = SystemMessage.LOC_ADEN_S1_S2_S3;
				break;
			case 11:
				msg = SystemMessage.LOC_HUNTER_S1_S2_S3;
				break;
			case 12:
				msg = SystemMessage.LOC_GIRAN_HARBOR_S1_S2_S3;
				break;
			case 13:
				msg = SystemMessage.LOC_HEINE_S1_S2_S3;
				break;
			case 14:
				msg = SystemMessage.LOC_RUNE_S1_S2_S3;
				break;
			case 15:
				msg = SystemMessage.LOC_GODDARD_S1_S2_S3;
				break;
			case 16:
				msg = SystemMessage.LOC_FLORAN_S1_S2_S3;
				break;
			default:
				msg = SystemMessage.LOC_ADEN_S1_S2_S3;
		}
		
		SystemMessage sm = new SystemMessage(msg);
		sm.addNumber(activeChar.getX());
		sm.addNumber(activeChar.getY());
		sm.addNumber(activeChar.getZ());
		activeChar.sendPacket(sm);
		return true;
	}
}
