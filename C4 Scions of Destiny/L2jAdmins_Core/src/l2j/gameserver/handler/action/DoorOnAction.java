package l2j.gameserver.handler.action;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.ConfirmDlg;
import l2j.gameserver.network.external.server.DoorStatusUpdate;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class DoorOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		L2DoorInstance door = ((L2DoorInstance) target);
		
		if (player == null)
		{
			return false;
		}
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (door != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(door);
			// Send a Server->Client packet DoorStatusUpdate
			player.sendPacket(new DoorStatusUpdate(door));
		}
		else
		{
			if (door.isAutoAttackable(player))
			{
				if (Math.abs(player.getZ() - door.getZ()) < 400)
				{
					player.getAI().setIntention(CtrlIntentionType.ATTACK, door);
				}
			}
			else if ((player.getClan() != null) && (door.getClanHall() != null) && (player.getClanId() == door.getClanHall().getOwnerId()))
			{
				if (!door.isInsideRadius(player, L2Npc.INTERACTION_DISTANCE, false, false))
				{
					player.getAI().setIntention(CtrlIntentionType.INTERACT, door);
				}
				else
				{
					player.getRequestDoor().setDoor(door);
					
					if (door.isOpen())
					{
						player.sendPacket(new ConfirmDlg(SystemMessage.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE));
					}
					else
					{
						player.sendPacket(new ConfirmDlg(SystemMessage.WOULD_YOU_LIKE_TO_OPEN_THE_GATE));
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2DoorInstance;
	}
}
