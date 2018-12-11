package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public final class ConditionSlotItemType extends ConditionInventory
{
	final int mask;
	
	public ConditionSlotItemType(ParpedollType slot, int mask)
	{
		super(slot);
		this.mask = mask;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.getPlayer() instanceof L2PcInstance))
		{
			return false;
		}
		Inventory inv = ((L2PcInstance) env.getPlayer()).getInventory();
		ItemInstance item = inv.getPaperdollItem(slot);
		if (item == null)
		{
			return false;
		}
		return (item.getItem().getMask() & mask) != 0;
	}
}
