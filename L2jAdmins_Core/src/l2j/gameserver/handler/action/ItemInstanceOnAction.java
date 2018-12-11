package l2j.gameserver.handler.action;

import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.instancemanager.MercTicketManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public class ItemInstanceOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		// this causes the validate position handler to do the pickup if the location is reached.
		// mercenary tickets can only be picked up by the castle owner.
		int castleId = MercTicketManager.getInstance().getTicketCastleId(((ItemInstance) target).getId());
		
		if ((castleId > 0) && (!player.isCastleLord(castleId) || player.isInParty()))
		{
			if (player.isInParty())
			{
				player.sendMessage("You cannot pickup mercenaries while in party.");
			}
			else
			{
				player.sendMessage("Only the castle lord can pickup mercenaries.");
			}
			
			player.setTarget(target);
			player.getAI().setIntention(CtrlIntentionType.IDLE);
		}
		else
		{
			player.getAI().setIntention(CtrlIntentionType.PICK_UP, target);
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2ItemInstance;
	}
}
