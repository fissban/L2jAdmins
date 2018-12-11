package l2j.gameserver.model.items;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.items.enums.EtcItemType;

/**
 * This class is dedicated to the management of EtcItem.
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:30:10 $
 */
public final class ItemEtcItem extends Item
{
	/**
	 * Constructor for EtcItem.
	 * @see        Item constructor
	 * @param type : L2EtcItemType designating the type of object Etc
	 * @param set  : StatsSet designating the set of couples (key,value) for description of the Etc
	 */
	public ItemEtcItem(EtcItemType type, StatsSet set)
	{
		super(type, set);
	}
	
	/**
	 * The type of Etc Item
	 * @return L2EtcItemType
	 */
	@Override
	public EtcItemType getType()
	{
		return (EtcItemType) super.type;
	}
	
	/**
	 * Item is consumable
	 * @return boolean
	 */
	@Override
	public final boolean isConsumable()
	{
		return ((getType() == EtcItemType.SHOT) || (getType() == EtcItemType.POTION)); // || (type == L2EtcItemType.SCROLL));
	}
	
	/**
	 * The ID of the Etc item after applying the mask.
	 * @return int
	 */
	@Override
	public int getMask()
	{
		return getType().mask();
	}
}
