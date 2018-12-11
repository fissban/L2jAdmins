package l2j.gameserver.network.external.client;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2BoatInstance;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.NpcInfo;
import l2j.gameserver.network.external.server.PetItemList;
import l2j.gameserver.network.external.server.SpawnItem;
import l2j.gameserver.network.external.server.StaticObject;
import l2j.gameserver.network.external.server.UserInfo;

public class RequestRecordInfo extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.sendPacket(new UserInfo(activeChar));
		
		for (L2Object object : activeChar.getKnownList().getObjects())
		{
			if (object instanceof ItemInstance)
			{
				activeChar.sendPacket(new SpawnItem((ItemInstance) object));
			}
			else if (object instanceof L2StaticObjectInstance)
			{
				activeChar.sendPacket(new StaticObject((L2StaticObjectInstance) object));
			}
			else if ((object instanceof L2Npc) || (object instanceof L2BoatInstance) || (object instanceof L2DoorInstance))
			{
				object.sendInfo(activeChar);
			}
			else if (object instanceof L2Summon)
			{
				L2Summon summon = (L2Summon) object;
				
				// Check if the L2PcInstance is the owner of the Pet
				if (activeChar.equals(summon.getOwner()))
				{
					summon.broadcastStatusUpdate();
					
					if (summon instanceof L2PetInstance)
					{
						activeChar.sendPacket(new PetItemList((L2PetInstance) summon));
					}
				}
				else
				{
					activeChar.sendPacket(new NpcInfo(summon, activeChar, 1));
				}
			}
			
			if (object instanceof L2Character)
			{
				// Update the state of the L2Character object client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance
				L2Character obj = (L2Character) object;
				if (obj.getAI() != null)
				{
					obj.getAI().describeStateToPlayer(activeChar);
				}
			}
		}
	}
}
