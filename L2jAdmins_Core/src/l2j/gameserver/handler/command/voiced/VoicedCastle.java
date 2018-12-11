package l2j.gameserver.handler.command.voiced;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.handler.CommandVoicedHandler.IVoicedCommandHandler;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.Castle;

public class VoicedCastle implements IVoicedCommandHandler
{
	@Override
	public String[] getVoicedCommandList()
	{
		return new String[]
		{
			"open doors",
			"close doors",
			"ride wyvern"
		};
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (target.equals("castle") && activeChar.isClanLeader())
		{
			L2DoorInstance door = (L2DoorInstance) activeChar.getTarget();
			Castle castle = CastleData.getInstance().getCastleById(activeChar.getClan().getCastleId());
			
			if ((door == null) || (castle == null))
			{
				return false;
			}
			
			if (command.startsWith("open doors"))
			{
				if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
				{
					door.openMe();
				}
			}
			else if (command.startsWith("close doors"))
			{
				if (castle.checkIfInZone(door.getX(), door.getY(), door.getZ()))
				{
					door.closeMe();
				}
			}
			else if (command.startsWith("ride wyvern"))
			{
				if ((activeChar.getClan().hasCastle()) && activeChar.isClanLeader())
				{
					activeChar.mount(12621, 0, true);
				}
			}
		}
		
		return true;
	}
}
