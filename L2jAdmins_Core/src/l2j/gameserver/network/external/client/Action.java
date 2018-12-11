package l2j.gameserver.network.external.client;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.7.4.4 $ $Date: 2005/03/27 18:46:19 $
 */
public class Action extends AClientPacket
{
	private boolean removeSpawnProtection = false;
	
	// cddddc
	private int objectId;
	@SuppressWarnings("unused")
	private int originX;
	@SuppressWarnings("unused")
	private int originY;
	@SuppressWarnings("unused")
	private int originZ;
	private int actionId;
	
	@Override
	protected void readImpl()
	{
		objectId = readD(); // Target object Identifier
		originX = readD();
		originY = readD();
		originZ = readD();
		actionId = readC(); // Action identifier : 0-Simple click, 1-Shift click
	}
	
	@Override
	public void runImpl()
	{
		// Get the current L2PcInstance of the player
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.inObserverMode())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.OBSERVERS_CANNOT_PARTICIPATE));
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		L2Object obj;
		if (activeChar.getTargetId() == objectId)
		{
			obj = activeChar.getTarget();
			removeSpawnProtection = true;
		}
		else
		{
			obj = L2World.getInstance().getObject(objectId);
		}
		
		// If object requested does not exist, add warn msg into logs
		if (obj == null)
		{
			// pressing e.g. pickup many times quickly would get you here
			// log.warning("Character: " + activeChar.getName() + " request action with non existent ObjectID:" + objectId);
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (obj instanceof ItemInstance)
		{
			removeSpawnProtection = true;
		}
		
		// Check if the target is valid, if the player haven't a shop or isn't the requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...)
		if ((!activeChar.getPrivateStore().isInStoreMode()) && (!activeChar.isRequestActive()))
		{
			switch (actionId)
			{
				case 0:
					obj.onAction(activeChar, true);
					break;
				case 1:
					if ((obj instanceof L2Character) && ((L2Character) obj).isAlikeDead())
					{
						obj.onAction(activeChar, false);
					}
					else
					{
						obj.onActionShift(getClient().getActiveChar());
					}
					break;
				default:
					// Invalid action detected (probably client cheating), log this
					LOG.warning("Character: " + activeChar.getName() + " requested invalid action: " + actionId);
					sendPacket(ActionFailed.STATIC_PACKET);
					break;
			}
		}
		else
		{
			// Actions prohibited when in trade
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return removeSpawnProtection;
	}
}
