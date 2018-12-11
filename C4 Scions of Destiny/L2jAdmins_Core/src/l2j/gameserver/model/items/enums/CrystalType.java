package l2j.gameserver.model.items.enums;

/**
 * @author fissban
 */
public enum CrystalType
{
	// TODO
	// Eliminar el "name" cuando se pase a xml todos los items
	// Los q no tienen un "name" definido jamas son usados en ItemTable
	
	// name, id, crystalItemId
	CRYSTAL_NONE("none", 0, 0),
	CRYSTAL_D("d", 1, 1458),
	CRYSTAL_C("c", 2, 1459),
	CRYSTAL_B("b", 3, 1460),
	CRYSTAL_A("a", 4, 1461),
	CRYSTAL_S("s", 5, 1462);
	
	private final String name;
	private final int id;
	private final int crystalId;
	
	CrystalType(String name, int id, int crystalId)
	{
		this.name = name;
		this.id = id;
		this.crystalId = crystalId;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getCrystalId()
	{
		return crystalId;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Encontramos un enumerador segun su name
	 * @param  name
	 * @return
	 */
	public static CrystalType valueOfName(String name)
	{
		for (CrystalType slot : CrystalType.values())
		{
			if (slot.getName().equals(name))
			{
				return slot;
			}
		}
		return null;
	}
}
