package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public class ListenerFormalWear implements IPaperdollListener
{
	@Override
	public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		if (item.getId() == 6408)
		{
			player.setIsWearingFormalWear(false);
		}
	}
	
	@Override
	public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		// If player equip Formal Wear unequip weapons and abort cast/attack
		if (item.getId() == 6408)
		{
			player.setIsWearingFormalWear(true);
			if (player.isCastingNow())
			{
				player.abortCast();
			}
			if (player.isAttackingNow())
			{
				player.abortAttack();
			}
			player.getInventory().setPaperdollItem(ParpedollType.LHAND, null);
			player.getInventory().setPaperdollItem(ParpedollType.RHAND, null);
			player.getInventory().setPaperdollItem(ParpedollType.LRHAND, null);
		}
		else
		{
			if (!player.isWearingFormalWear())
			{
				return;
			}
			
			// Don't let weapons be equipped if player is wearing Formal Wear
			if ((slot == ParpedollType.LHAND) || (slot == ParpedollType.RHAND) || (slot == ParpedollType.LRHAND))
			{
				player.getInventory().setPaperdollItem(slot, null);
			}
		}
	}
}
