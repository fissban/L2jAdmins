package l2j.gameserver.model.actor.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.DeleteObject;

public class PcKnownList extends PlayableKnownList
{
	public PcKnownList(L2PcInstance activeChar)
	{
		super(activeChar);
	}
	
	/**
	 * Add a visible L2Object to L2PcInstance knownObjects and knownPlayer (if necessary) and send Server-Client Packets needed to inform the L2PcInstance of its state and actions in progress.<BR>
	 * <B><U> object is a ItemInstance </U> :</B><BR>
	 * <li>Send Server-Client Packet DropItem/SpawnItem to the L2PcInstance</li> <B><U> object is a L2DoorInstance </U> :</B><BR>
	 * <li>Send Server-Client Packets DoorInfo and DoorStatusUpdate to the L2PcInstance</li>
	 * <li>Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance</li> <B><U> object is a L2Npc </U> :</B><BR>
	 * <li>Send Server-Client Packet NpcInfo to the L2PcInstance</li>
	 * <li>Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance</li> <B><U> object is a L2Summon </U> :</B><BR>
	 * <li>Send Server-Client Packet NpcInfo/PetItemList (if the L2PcInstance is the owner) to the L2PcInstance</li>
	 * <li>Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance</li> <B><U> object is a L2PcInstance </U> :</B><BR>
	 * <li>Send Server-Client Packet CharInfo to the L2PcInstance</li>
	 * <li>If the object has a private store, Send Server-Client Packet PrivateStoreMsgSell to the L2PcInstance</li>
	 * <li>Send Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance</li> <BR>
	 * @param object The L2Object to add to knownObjects and knownPlayer
	 */
	@Override
	public boolean addObject(L2Object object)
	{
		if (!super.addObject(object))
		{
			return false;
		}
		
		sendInfoFrom(object);
		return true;
	}
	
	/**
	 * Remove a L2Object from L2PcInstance knownObjects and knownPlayer (if necessary) and send Server-Client Packet DeleteObject to the L2PcInstance.<BR>
	 * @param object The L2Object to remove from knownObjects and knownPlayer
	 */
	@Override
	public boolean removeObject(L2Object object)
	{
		if (!super.removeObject(object))
		{
			return false;
		}
		
		// get player
		final L2PcInstance player = (L2PcInstance) activeObject;
		
		// send Server-Client Packet DeleteObject to the L2PcInstance
		player.sendPacket(new DeleteObject(object));
		return true;
	}
	
	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		if (object instanceof L2BoatInstance)
		{
			return 8000;
		}
		
		return Math.max(1800, 3600 - (knownObjects.size() * 20));
	}
	
	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		// distance to watch + 50%
		return (int) Math.round(1.5 * getDistanceToWatchObject(object));
	}
	
	public final void refreshInfos()
	{
		for (L2Object object : knownObjects.values())
		{
			if ((object instanceof L2PcInstance) && ((L2PcInstance) object).inObserverMode())
			{
				continue;
			}
			
			sendInfoFrom(object);
		}
	}
	
	private final void sendInfoFrom(L2Object object)
	{
		// get player
		final L2PcInstance player = (L2PcInstance) activeObject;
		
		// send object info to player
		object.sendInfo(player);
		
		if (object instanceof L2Character)
		{
			// Update the state of the L2Character object client side by sending Server->Client packet MoveToPawn/MoveToLocation and AutoAttackStart to the L2PcInstance
			L2Character obj = (L2Character) object;
			if (obj.hasAI())
			{
				obj.getAI().describeStateToPlayer(player);
			}
		}
	}
}
