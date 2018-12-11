package l2j.gameserver.network.external.client;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;

/**
 * This class ...
 * @version $Revision: 1.7.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class AttackRequest extends AClientPacket
{
	// cddddc
	private int objectId;
	@SuppressWarnings("unused")
	private int originX;
	@SuppressWarnings("unused")
	private int originY;
	@SuppressWarnings("unused")
	private int originZ;
	@SuppressWarnings("unused")
	private int attackId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD();
		originX = readD();
		originY = readD();
		originZ = readD();
		attackId = readC(); // 0 for simple click 1 for shift-click
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		L2Object target;
		if (activeChar.getTargetId() == objectId)
		{
			target = activeChar.getTarget();
		}
		else
		{
			target = L2World.getInstance().getObject(objectId);
		}
		
		if (target == null)
		{
			return;
		}
		
		if (activeChar.getTarget() != target)
		{
			target.onAction(activeChar, true);
		}
		else
		{
			if ((target.getObjectId() != activeChar.getObjectId()) && (!activeChar.getPrivateStore().isInStoreMode()) && (!activeChar.isRequestActive()))
			{
				target.onForcedAttack(activeChar);
			}
			else
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return true;
	}
}
