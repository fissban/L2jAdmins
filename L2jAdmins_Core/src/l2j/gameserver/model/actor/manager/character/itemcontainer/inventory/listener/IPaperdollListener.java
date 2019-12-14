package l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.listener;

import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author fissban
 */
public interface IPaperdollListener
{
	public void notifyEquiped(ParpedollType slot, ItemInstance item, L2Playable player);
	
	public void notifyUnequiped(ParpedollType slot, ItemInstance item, L2Playable player);
}
