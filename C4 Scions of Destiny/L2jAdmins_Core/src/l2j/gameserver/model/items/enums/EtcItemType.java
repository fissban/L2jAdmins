package l2j.gameserver.model.items.enums;

/**
 * Description of EtcItem Type
 */
public enum EtcItemType
{
	ARROW("Arrow"),
	MATERIAL("Material"),
	PET_COLLAR("PetCollar"),
	POTION("Potion"),
	RECEIPE("Receipe"),
	SCROLL("Scroll"),
	QUEST("Quest"),
	MONEY("Money"),
	OTHER("Other"),
	SPELLBOOK("Spellbook"),
	SEED("Seed"),
	SHOT("Shot");
	
	private final String name;
	
	/**
	 * Constructor of the EtcItemType.
	 * @param name : String designating the name of the EtcItemType
	 */
	EtcItemType(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return the ID of the item after applying the mask.
	 */
	public int mask()
	{
		return 1 << (ordinal() + 21);
	}
	
	/**
	 * @return the name of the EtcItemType
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
