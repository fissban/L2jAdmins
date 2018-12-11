package l2j.gameserver.model.holder;

/**
 * @author -Nemesiss-
 */
public class ExtractableProductItemHolder
{
	private final int id;
	private final int ammount;
	private final int chance;
	
	public ExtractableProductItemHolder(int id, int ammount, int chance)
	{
		this.id = id;
		this.ammount = ammount;
		this.chance = chance;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getAmmount()
	{
		return ammount;
	}
	
	public int getChance()
	{
		return chance;
	}
}
