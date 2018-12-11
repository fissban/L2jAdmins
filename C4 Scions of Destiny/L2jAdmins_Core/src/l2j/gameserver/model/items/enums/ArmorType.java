package l2j.gameserver.model.items.enums;

/**
 * Description of Armor Type
 */
public enum ArmorType
{
	NONE("None"),
	LIGHT("Light"),
	HEAVY("Heavy"),
	MAGIC("Magic"),
	PET("Pet");
	
	private final String name;
	
	/**
	 * Constructor of the ArmorType.
	 * @param name : String designating the name of the ArmorType
	 */
	ArmorType(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return the ID of the ArmorType after applying a mask.
	 */
	public int mask()
	{
		return 1 << ((ordinal() + 1) + 16);// ordinal()+1 because the first value should be 1
	}
	
	/**
	 * @return the name of the ArmorType
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
