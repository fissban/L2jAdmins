package l2j.gameserver.handler.action;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.ShowTownMap;

/**
 * @author fissban
 */
public class StaticObjectInstanceOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		L2StaticObjectInstance object = ((L2StaticObjectInstance) target);
		
		if (object.getType() < 0)
		{
			LOG.warning("L2StaticObjectInstance: StaticObject with invalid type! StaticObjectId: " + object.getStaticObjectId());
		}
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (object != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(object);
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!player.isInsideRadius(object, L2StaticObjectInstance.INTERACTION_DISTANCE, false, false))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntentionType.INTERACT, object);
			}
			else
			{
				if (object.getType() == 2)
				{
					NpcHtmlMessage html = new NpcHtmlMessage(object.getObjectId());
					html.setFile("data/html/signboard.htm");
					player.sendPacket(html);
				}
				else if (object.getType() == 0)
				{
					player.sendPacket(new ShowTownMap(object.getTexture(), object.getMapX(), object.getMapY()));
				}
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2StaticObjectInstance;
	}
}
