package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public final class ConditionUsingItemType extends Condition
{
	private final boolean armor;
	private final int mask;
	
	public ConditionUsingItemType(int mask)
	{
		this.mask = mask;
		armor = (mask & (ArmorType.MAGIC.mask() | ArmorType.LIGHT.mask() | ArmorType.HEAVY.mask())) != 0;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		if (!(env.getPlayer() instanceof L2PcInstance))
		{
			return false;
		}
		Inventory inv = ((L2PcInstance) env.getPlayer()).getInventory();
		
		if (armor)
		{
			// Get the itemMask of the weared chest (if exists)
			ItemInstance chest = inv.getPaperdollItem(ParpedollType.CHEST);
			if (chest == null)
			{
				return false;
			}
			int chestMask = chest.getItem().getMask();
			
			// If chest armor is different from the condition one return false
			if ((mask & chestMask) == 0)
			{
				return false;
			}
			
			// So from here, chest armor matches conditions
			
			// return True if chest armor is a Full Armor
			if (chest.getItem().getBodyPart() == SlotType.FULL_ARMOR)
			{
				return true;
			}
			
			// check legs armor
			ItemInstance legs = inv.getPaperdollItem(ParpedollType.LEGS);
			if (legs == null)
			{
				return false;
			}
			
			int legMask = legs.getItem().getMask();
			
			// return true if legs armor matches too
			return (mask & legMask) != 0;
		}
		
		return (mask & inv.getWearedMask()) != 0;
	}
}
