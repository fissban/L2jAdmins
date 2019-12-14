package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public class ListenerBow implements IPaperdollListener
{
	@Override
	public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (slot != ParpedollType.LRHAND)
		{
			return;
		}
		
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		assert null == player.getInventory().getPaperdollItem(ParpedollType.LRHAND);
		
		if (item.getType() == WeaponType.BOW)
		{
			ItemInstance arrow = player.getInventory().getPaperdollItem(ParpedollType.LHAND);
			if (arrow != null)
			{
				player.getInventory().setPaperdollItem(ParpedollType.LHAND, null);
			}
		}
	}
	
	@Override
	public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (slot != ParpedollType.LRHAND)
		{
			return;
		}
		
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance player = (L2PcInstance) playable;
		
		assert item == player.getInventory().getPaperdollItem(ParpedollType.LRHAND);
		
		if (item.getType() == WeaponType.BOW)
		{
			ItemInstance arrow = player.getInventory().findArrowForBow(item.getItem());
			if (arrow != null)
			{
				player.getInventory().setPaperdollItem(ParpedollType.LHAND, arrow);
			}
		}
	}
}
