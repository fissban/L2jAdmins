package l2j.gameserver.model.items.enums;

/**
 * @author mkizub
 */
public enum WeaponType
{
	NONE("Shield"), // Shields!!!
	SWORD("Sword"),
	BLUNT("Blunt"),
	DAGGER("Dagger"),
	BOW("Bow"),
	POLE("Pole"),
	ETC("Etc"),
	FIST("Fist"),
	DUAL("Dual Sword"),
	DUALFIST("Dual Fist"),
	BIGSWORD("Big Sword"), // Two Handed Sword
	PET("Pet"),
	ROD("Rod"),
	BIGBLUNT("Big Blunt"); // Two Handed Blunt
	
	private final String name;
	
	/**
	 * Constructor of the L2WeaponType.
	 * @param id   : int designating the ID of the WeaponType
	 * @param name : String designating the name of the WeaponType
	 */
	private WeaponType(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return the ID of the item after applying the mask.
	 */
	public int mask()
	{
		return 1 << (ordinal() + 1);
	}
	
	/**
	 * @return the name of the WeaponType
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
