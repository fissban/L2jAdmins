package l2j.gameserver.model.holder;

/**
 * This class contains all infos of the L2Attackable against the absorber Creature.
 * <ul>
 * <li>_absorbedHP : The amount of HP at the moment attacker used the item.</li>
 * <li>_itemObjectId : The item id of the Soul Crystal used.</li>
 * </ul>
 */
public final class AbsorbInfoHolder
{
	private boolean registered;
	private int itemId;
	private int absorbedHpPercent;
	
	public AbsorbInfoHolder(int itemId)
	{
		this.itemId = itemId;
	}
	
	public boolean isRegistered()
	{
		return registered;
	}
	
	public void setRegistered(boolean state)
	{
		registered = state;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}
	
	public void setAbsorbedHpPercent(int percent)
	{
		absorbedHpPercent = percent;
	}
	
	public boolean isValid(int itemId)
	{
		return (this.itemId == itemId) && (absorbedHpPercent < 50);
	}
}
