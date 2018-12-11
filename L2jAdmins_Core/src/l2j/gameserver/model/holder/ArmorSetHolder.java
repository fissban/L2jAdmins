package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author Luno
 */
public final class ArmorSetHolder
{
	private final int chest;
	private final int legs;
	private final int head;
	private final int gloves;
	private final int feet;
	private final int skillId;
	
	private final int shield;
	private final int shieldSkillId;
	
	public ArmorSetHolder(int chest, int legs, int head, int gloves, int feet, int skillId, int shield, int shieldSkillId)
	{
		this.chest = chest;
		this.legs = legs;
		this.head = head;
		this.gloves = gloves;
		this.feet = feet;
		this.skillId = skillId;
		
		this.shield = shield;
		this.shieldSkillId = shieldSkillId;
	}
	
	/**
	 * Checks if player have equipped all items from set (not checking shield)
	 * @param  player whose inventory is being checked
	 * @return        True if player equips whole set
	 */
	public boolean containAll(L2PcInstance player)
	{
		Inventory inv = player.getInventory();
		
		ItemInstance legsItem = inv.getPaperdollItem(ParpedollType.LEGS);
		ItemInstance headItem = inv.getPaperdollItem(ParpedollType.HEAD);
		ItemInstance glovesItem = inv.getPaperdollItem(ParpedollType.GLOVES);
		ItemInstance feetItem = inv.getPaperdollItem(ParpedollType.FEET);
		
		int legs = (legsItem != null) ? legsItem.getId() : 0;
		int head = (headItem != null) ? headItem.getId() : 0;
		int gloves = (glovesItem != null) ? gloves = glovesItem.getId() : 0;
		int feet = (feetItem != null) ? feet = feetItem.getId() : 0;
		
		return containAll(chest, legs, head, gloves, feet);
		
	}
	
	/**
	 * Checks if player have equipped all items from set (not checking shield)
	 * @param  chest
	 * @param  legs
	 * @param  head
	 * @param  gloves
	 * @param  feet
	 * @return        True if player equips whole set
	 */
	public boolean containAll(int chest, int legs, int head, int gloves, int feet)
	{
		if ((chest != 0) && (this.chest != chest))
		{
			return false;
		}
		if ((legs != 0) && (this.legs != legs))
		{
			return false;
		}
		if ((head != 0) && (this.head != head))
		{
			return false;
		}
		if ((gloves != 0) && (this.gloves != gloves))
		{
			return false;
		}
		if ((feet != 0) && (this.feet != feet))
		{
			return false;
		}
		
		return true;
	}
	
	public boolean containItem(ParpedollType slot, int itemId)
	{
		switch (slot)
		{
			case CHEST:
				return chest == itemId;
			case LEGS:
				return legs == itemId;
			case HEAD:
				return head == itemId;
			case GLOVES:
				return gloves == itemId;
			case FEET:
				return feet == itemId;
			default:
				return false;
		}
	}
	
	public int getSkillId()
	{
		return skillId;
	}
	
	public boolean containShield(L2PcInstance player)
	{
		Inventory inv = player.getInventory();
		
		ItemInstance shieldItem = inv.getPaperdollItem(ParpedollType.LHAND);
		if ((shieldItem != null) && (shieldItem.getId() == shield))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean containShield(int shield_id)
	{
		if (shield == 0)
		{
			return false;
		}
		
		return shield == shield_id;
	}
	
	public int getShieldSkillId()
	{
		return shieldSkillId;
	}
}
