package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.instance.enums.PetItemType;

/**
 * @author -Nemesiss-
 */
public class SummonItemHolder
{
	private final String name;
	private final int itemId;
	private final int npcId;
	private final PetItemType type;
	
	public SummonItemHolder(String name, int itemId, int npcId, PetItemType type)
	{
		this.name = name;
		this.itemId = itemId;
		this.npcId = npcId;
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public int getNpcId()
	{
		return npcId;
	}
	
	public PetItemType getType()
	{
		return type;
	}
	
	public boolean isPetSummon()
	{
		return (type == PetItemType.PET) || (type == PetItemType.WYVERN);
	}
}
