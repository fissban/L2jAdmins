package l2j.gameserver.handler.action;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;

/**
 * @author fissban
 */
public class ArtefactOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		L2Npc artefect = ((L2Npc) target);
		
		if (!artefect.canTarget(player))
		{
			return false;
		}
		
		player.setLastTalkNpc(artefect);
		
		if (artefect != player.getTarget())
		{
			// Set the target of the L2PcInstance player
			player.setTarget(artefect);
		}
		else
		{
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!artefect.canInteract(player))
			{
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				player.getAI().setIntention(CtrlIntentionType.INTERACT, artefect);
			}
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2ArtefactInstance;
	}
}
