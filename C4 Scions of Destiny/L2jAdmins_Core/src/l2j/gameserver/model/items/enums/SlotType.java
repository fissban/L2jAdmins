package l2j.gameserver.model.items.enums;

/**
 * @author fissban
 */
public enum SlotType
{
	// TODO
	// Eliminar el "name" cuando se pase a xml todos los items
	NONE("none", 0x0000), // 0
	UNDERWEAR("underwear", 0x0001), // 1
	R_EAR("rear", 0x0002), // 2
	L_EAR("lear", 0x0004), // 4
	// R_EAR_L_EAR("rear,lear", 0x0002 | 0x0004), // 6 -> Special by fissban -> R_EAR | L_EAR
	R_EAR_L_EAR("rear,lear", 0x0006), // 6 -> Special by fissban -> R_EAR | L_EAR
	NECK("neck", 0x0008), // 8
	R_FINGER("rfinger", 0x0010), // 16
	L_FINGER("lfinger", 0x0020), // 32
	// R_FINGER_L_FINGER("rfinger,lfinger", 0x0010 | 0x0020), // 48 -> Special by fissban -> R_FINGER | L_FINGER
	R_FINGER_L_FINGER("rfinger,lfinger", 0x0030), // 48 -> Special by fissban -> R_FINGER | L_FINGER
	HEAD("head", 0x0040), // 64
	R_HAND("rhand", 0x0080), // 128
	L_HAND("lhand", 0x0100), // 256
	GLOVES("gloves", 0x0200), // 512
	CHEST("chest", 0x0400), // 1024
	LEGS("legs", 0x0800), // 2048
	// CHEST_LEGS("chest,legs", 0x0400 | 0x0800),
	CHEST_LEGS("chest,legs", 0xc00), // 3072 -> Special by fissban -> CHEST | LEGS
	FEET("feet", 0x1000), // 4096
	BACK("back", 0x2000), // 8192
	LR_HAND("lrhand", 0x4000), // 16384
	FULL_ARMOR("fullarmor", 0x8000), // 32768
	HAIR("hair", 0x010000), // 65536
	WOLF("wolf", 0x020000), // 131072
	HATCHLING("hatchling", 0x040000), // 262144
	STRIDER("strider", 0x080000);// 524288
	
	String name;
	int mask;
	
	SlotType(String name, int mask)
	{
		this.name = name;
		this.mask = mask;
	}
	
	public int getMask()
	{
		return mask;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Encontramos un enumerador segun su mask
	 * @param  value
	 * @return
	 */
	public static SlotType valueOfMask(int value)
	{
		for (SlotType slot : SlotType.values())
		{
			if (slot.getMask() == value)
			{
				return slot;
			}
		}
		return null;
	}
	
	/**
	 * Encontramos un enumerador segun su name
	 * @param  name
	 * @return
	 */
	public static SlotType valueOfName(String name)
	{
		for (SlotType slot : SlotType.values())
		{
			if (slot.getName().equals(name))
			{
				return slot;
			}
		}
		return null;
	}
}
