package l2j.gameserver.handler.action;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ValidateLocation;
import main.EngineModsManager;

/**
 * @author fissban
 */
public class PcInstanceOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		// Check if the L2PcInstance is confused
		if (player.isOutOfControl())
		{
			return false;
		}
		
		// Check if the player already target this L2PcInstance
		if (player.getTarget() != target)
		{
			// Set the target of the player
			player.setTarget(target);
		}
		else
		{
			if ((player != target) && !player.isInBoat())
			{
				player.sendPacket(new ValidateLocation((L2Character) target));
			}
			
			// Check if this L2PcInstance has a Private Store
			if (((L2PcInstance) target).getPrivateStore().isInStoreMode())
			{
				player.getAI().setIntention(CtrlIntentionType.INTERACT, target);
			}
			else
			{
				if ((target instanceof L2Character) && EngineModsManager.onInteract(player, (L2Character) target))
				{
					return false;
				}
				
				// Check if this L2PcInstance is autoAttackable
				if (target.isAutoAttackable(player))
				{
					if (GeoEngine.getInstance().canSeeTarget(player, target))
					{
						player.getAI().setIntention(CtrlIntentionType.ATTACK, target);
						player.onActionRequest();
					}
				}
				else
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					
					if ((player != target) && GeoEngine.getInstance().canSeeTarget(player, target))
					{
						player.getAI().setIntention(CtrlIntentionType.FOLLOW, target);
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2PcInstance;
	}
}
