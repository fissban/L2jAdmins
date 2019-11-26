package l2j.gameserver.network.external.client;

import java.nio.BufferUnderflowException;

import l2j.Config;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.PartyMemberPosition;
import l2j.gameserver.network.external.server.StopMove;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.11.2.4.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class MoveBackwardToLocation extends AClientPacket
{
	// cdddddd
	private int targetX;
	private int targetY;
	private int targetZ;
	private int originX;
	private int originY;
	private int originZ;
	@SuppressWarnings("unused")
	private int moveMovement;
	
	@Override
	protected void readImpl()
	{
		targetX = readD();
		targetY = readD();
		targetZ = readD();
		originX = readD();
		originY = readD();
		originZ = readD();
		try
		{
			moveMovement = readD(); // is 0 if cursor keys are used 1 if mouse is used
		}
		catch (BufferUnderflowException e)
		{
			if (Config.ALLOW_L2WALKER)
			{
				getClient().getActiveChar().sendPacket(SystemMessage.HACKING_TOOL);
				IllegalAction.report(getClient().getActiveChar(), "Player " + getClient().getActiveChar().getName() + " is trying to use L2Walker and got kicked.");
			}
		}
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isOutOfControl())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// if (activeChar.isCastingNow())
		// {
		// activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		// return;
		// }
		
		if ((targetX == originX) && (targetY == originY) && (targetZ == originZ))
		{
			activeChar.sendPacket(new StopMove(activeChar));
			return;
		}
		
		double dx = targetX - originX;
		double dy = targetY - originY;
		
		if (((dx * dx) + (dy * dy)) > 98010000) // 9900*9900
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		activeChar.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(targetX, targetY, targetZ));
		
		if (activeChar.getParty() != null)
		{
			activeChar.getParty().broadcastToPartyMembers(activeChar, new PartyMemberPosition(activeChar));
		}
	}
}
