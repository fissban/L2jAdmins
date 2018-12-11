package l2j.gameserver.handler.action;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.network.external.server.PetStatusShow;
import l2j.gameserver.network.external.server.ValidateLocation;

/**
 * @author fissban
 */
public class PetInstanceOnAction implements IActionHandler
{
	@Override
	public boolean action(L2PcInstance player, L2Object target, boolean interact)
	{
		L2PetInstance pet = ((L2PetInstance) target);
		
		if (player == null)
		{
			return false;
		}
		
		boolean isOwner = player.getObjectId() == pet.getOwner().getObjectId();
		
		player.sendPacket(new ValidateLocation(pet));
		if (isOwner && (player != pet.getOwner()))
		{
			pet.updateRefOwner(player);
		}
		
		if (player.getTarget() != pet)
		{
			// Set the target of the L2PcInstance player
			player.setTarget(pet);
		}
		else
		{
			// Check if the pet is attackable (without a forced attack) and isn't dead
			if (pet.isAutoAttackable(player) && !isOwner)
			{
				if (GeoEngine.getInstance().canSeeTarget(player, pet))
				{
					// Set the L2PcInstance Intention to AI_INTENTION_ATTACK
					player.getAI().setIntention(CtrlIntentionType.ATTACK, pet);
					player.onActionRequest();
				}
			}
			else if (!pet.isInsideRadius(player, 150, false, false))
			{
				if (GeoEngine.getInstance().canSeeTarget(player, pet))
				{
					player.getAI().setIntention(CtrlIntentionType.INTERACT, pet);
					player.onActionRequest();
				}
			}
			else
			{
				if (isOwner)
				{
					player.sendPacket(new PetStatusShow(pet));
				}
			}
		}
		
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2PetInstance;
	}
}
