package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public class ListenerStats implements IPaperdollListener
{
	@Override
	public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (slot == ParpedollType.LRHAND)
		{
			return;
		}
		playable.removeStatsOwner(item);
	}
	
	@Override
	public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable playable)
	{
		if (slot == ParpedollType.LRHAND)
		{
			return;
		}
		playable.addStatFuncs(item.getStatFuncs(playable));
	}
}
