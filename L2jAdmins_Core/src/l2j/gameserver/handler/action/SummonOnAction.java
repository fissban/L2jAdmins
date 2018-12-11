package l2j.gameserver.handler.action;

import l2j.Config;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.PetStatusShow;

/**
 * @author fissban
 */
public class SummonOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		L2Summon summon = ((L2Summon) target);
		
		if ((player == summon.getOwner()) && (player.getTarget() == summon))
		{
			player.sendPacket(new PetStatusShow(summon));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (player.getTarget() != summon)
		{
			player.setTarget(summon);
		}
		else if (player.getTarget() == summon)
		{
			if (summon.isAutoAttackable(player))
			{
				if (Config.PATHFINDING)
				{
					if (GeoEngine.getInstance().canSeeTarget(player, summon))
					{
						player.getAI().setIntention(CtrlIntentionType.ATTACK, summon);
						player.onActionRequest();
					}
				}
				else
				{
					player.getAI().setIntention(CtrlIntentionType.ATTACK, summon);
					player.onActionRequest();
				}
			}
			else
			{
				// This Action Failed packet avoids player getting stuck when clicking three or more times
				player.sendPacket(ActionFailed.STATIC_PACKET);
				if (Config.PATHFINDING)
				{
					if (GeoEngine.getInstance().canSeeTarget(player, summon))
					{
						player.getAI().setIntention(CtrlIntentionType.FOLLOW, summon);
					}
				}
				else
				{
					player.getAI().setIntention(CtrlIntentionType.FOLLOW, summon);
				}
			}
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2Summon;
	}
}
