package l2j.gameserver.model.actor.base;

/**
 * This class defines all classes (ex : Human fighter, darkFighter...) that a player can chose.<BR>
 * Data :<BR>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : true if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId or null if this class is the root</li>
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:33 $
 */
public enum ClassId
{
	HUMAN_FIGHTER(0, false, Race.HUMAN, null),
	WARRIOR(1, false, Race.HUMAN, HUMAN_FIGHTER),
	GLADIATOR(2, false, Race.HUMAN, WARRIOR),
	WARLORD(3, false, Race.HUMAN, WARRIOR),
	KNIGHT(4, false, Race.HUMAN, HUMAN_FIGHTER),
	PALADIN(5, false, Race.HUMAN, KNIGHT),
	DARK_AVENGER(6, false, Race.HUMAN, KNIGHT),
	ROGUE(7, false, Race.HUMAN, HUMAN_FIGHTER),
	TREASURE_HUNTER(8, false, Race.HUMAN, ROGUE),
	HAWKEYE(9, false, Race.HUMAN, ROGUE),
	
	HUMAN_MAGE(10, true, Race.HUMAN, null),
	WIZARD(11, true, Race.HUMAN, HUMAN_MAGE),
	SORCERER(12, true, Race.HUMAN, WIZARD),
	NECROMANCER(13, true, Race.HUMAN, WIZARD),
	WARLOCK(14, true, Race.HUMAN, WIZARD),
	CLERIC(15, true, Race.HUMAN, HUMAN_MAGE),
	BISHOP(16, true, Race.HUMAN, CLERIC),
	PROPHET(17, true, Race.HUMAN, CLERIC),
	
	ELF_FIGHTER(18, false, Race.ELF, null),
	ELF_KNIGHT(19, false, Race.ELF, ELF_FIGHTER),
	TEMPLE_KNIGHT(20, false, Race.ELF, ELF_KNIGHT),
	SWORD_SINGER(21, false, Race.ELF, ELF_KNIGHT),
	SCOUT(22, false, Race.ELF, ELF_FIGHTER),
	PLAINS_WALKER(23, false, Race.ELF, SCOUT),
	SILVER_RANGER(24, false, Race.ELF, SCOUT),
	
	ELF_MAGE(25, true, Race.ELF, null),
	ELF_WIZARD(26, true, Race.ELF, ELF_MAGE),
	SPELLSINGER(27, true, Race.ELF, ELF_WIZARD),
	ELEMENTAL_SUMMONER(28, true, Race.ELF, ELF_WIZARD),
	ORACLE(29, true, Race.ELF, ELF_MAGE),
	ELDER(30, true, Race.ELF, ORACLE),
	
	DARK_ELF_FIGHTER(31, false, Race.DARK_ELF, null),
	PALUS_KNIGHT(32, false, Race.DARK_ELF, DARK_ELF_FIGHTER),
	SHILLIEN_KNIGHT(33, false, Race.DARK_ELF, PALUS_KNIGHT),
	BLADE_DANCER(34, false, Race.DARK_ELF, PALUS_KNIGHT),
	ASSASSIN(35, false, Race.DARK_ELF, DARK_ELF_FIGHTER),
	ABYSS_WALKER(36, false, Race.DARK_ELF, ASSASSIN),
	PHANTOM_RANGER(37, false, Race.DARK_ELF, ASSASSIN),
	
	DARK_ELF_MAGE(38, true, Race.DARK_ELF, null),
	DARK_ELF_WIZARD(39, true, Race.DARK_ELF, DARK_ELF_MAGE),
	SPELLHOWLER(40, true, Race.DARK_ELF, DARK_ELF_WIZARD),
	PHANTOM_SUMMONER(41, true, Race.DARK_ELF, DARK_ELF_WIZARD),
	SHILLIEN_ORACLE(42, true, Race.DARK_ELF, DARK_ELF_MAGE),
	SHILLIEN_ELDER(43, true, Race.DARK_ELF, SHILLIEN_ORACLE),
	
	ORC_FIGHTER(44, false, Race.ORC, null),
	RAIDER(45, false, Race.ORC, ORC_FIGHTER),
	DESTROYER(46, false, Race.ORC, RAIDER),
	MONK(47, false, Race.ORC, ORC_FIGHTER),
	TYRANT(48, false, Race.ORC, MONK),
	
	ORC_MAGE(49, true, Race.ORC, null),
	SHAMAN(50, true, Race.ORC, ORC_MAGE),
	OVERLORD(51, true, Race.ORC, SHAMAN),
	WARCRYER(52, true, Race.ORC, SHAMAN),
	
	DWARF_FIGHTER(53, false, Race.DWARF, null),
	SCAVENGER(54, false, Race.DWARF, DWARF_FIGHTER),
	BOUNTY_HUNTER(55, false, Race.DWARF, SCAVENGER),
	ARTISAN(56, false, Race.DWARF, DWARF_FIGHTER),
	WARSMITH(57, false, Race.DWARF, ARTISAN),
	
	DUELIST(88, false, Race.HUMAN, GLADIATOR),
	DREADNOUGHT(89, false, Race.HUMAN, WARLORD),
	PHOENIX_KNIGHT(90, false, Race.HUMAN, PALADIN),
	HELL_KNIGHT(91, false, Race.HUMAN, DARK_AVENGER),
	SAGITTARIUS(92, false, Race.HUMAN, HAWKEYE),
	ADVENTURER(93, false, Race.HUMAN, TREASURE_HUNTER),
	ARCHMAGE(94, true, Race.HUMAN, SORCERER),
	SOULTAKER(95, true, Race.HUMAN, NECROMANCER),
	ARCANA_LORD(96, true, Race.HUMAN, WARLOCK),
	CARDINAL(97, true, Race.HUMAN, BISHOP),
	HIEROPHANT(98, true, Race.HUMAN, PROPHET),
	
	EVA_TEMPLAR(99, false, Race.ELF, TEMPLE_KNIGHT),
	SWORD_MUSE(100, false, Race.ELF, SWORD_SINGER),
	WIND_RIDER(101, false, Race.ELF, PLAINS_WALKER),
	MOONLIGHT_SENTINEL(102, false, Race.ELF, SILVER_RANGER),
	MYSTIC_MUSE(103, true, Race.ELF, SPELLSINGER),
	ELEMENTAL_MASTER(104, true, Race.ELF, ELEMENTAL_SUMMONER),
	EVA_SAINT(105, true, Race.ELF, ELDER),
	
	SHILLIEN_TEMPLAR(106, false, Race.DARK_ELF, SHILLIEN_KNIGHT),
	SPECTRAL_DANCER(107, false, Race.DARK_ELF, BLADE_DANCER),
	GHOST_HUNTER(108, false, Race.DARK_ELF, ABYSS_WALKER),
	GHOST_SENTINEL(109, false, Race.DARK_ELF, PHANTOM_RANGER),
	STORM_SCREAMER(110, true, Race.DARK_ELF, SPELLHOWLER),
	SPECTRAL_MASTER(111, true, Race.DARK_ELF, PHANTOM_SUMMONER),
	SHILLIEN_SAINT(112, true, Race.DARK_ELF, SHILLIEN_ELDER),
	
	TITAN(113, false, Race.ORC, DESTROYER),
	GRAND_KHAVATARI(114, false, Race.ORC, TYRANT),
	DOMINATOR(115, true, Race.ORC, OVERLORD),
	DOOM_CRYER(116, true, Race.ORC, WARCRYER),
	
	FORTUNE_SEEKER(117, false, Race.DWARF, BOUNTY_HUNTER),
	MAESTRO(118, false, Race.DWARF, WARSMITH);
	
	/** The Identifier of the Class */
	private final int id;
	
	/** true if the class is a mage class */
	private final boolean isMage;
	
	/** The Race object of the class */
	private final Race race;
	
	/** The parent ClassId or null if this class is a root */
	private final ClassId parent;
	
	/**
	 * Constructor of ClassId.
	 * @param pId
	 * @param pIsMage
	 * @param pRace
	 * @param pParent
	 */
	private ClassId(int pId, boolean pIsMage, Race pRace, ClassId pParent)
	{
		id = pId;
		isMage = pIsMage;
		race = pRace;
		parent = pParent;
	}
	
	/**
	 * @return the Identifier of the Class.
	 */
	public final int getId()
	{
		return id;
	}
	
	/**
	 * Get class name
	 * @return
	 */
	public String getName()
	{
		String name = "";
		
		boolean upperCase = true;
		for (char c : toString().toLowerCase().toCharArray())
		{
			if (String.valueOf(c).equals("_"))
			{
				name += " ";
				upperCase = true;
			}
			else if (upperCase)
			{
				name += String.valueOf(c).toUpperCase();
				upperCase = false;
			}
			else
			{
				name += c;
			}
		}
		
		return name;
	}
	
	/**
	 * @return true if the class is a mage class.
	 */
	public final boolean isMage()
	{
		return isMage;
	}
	
	/**
	 * @return the Race object of the class.
	 */
	public final Race getRace()
	{
		return race;
	}
	
	public static ClassId getById(int id)
	{
		for (ClassId c : values())
		{
			if (c.getId() == id)
			{
				return c;
			}
		}
		
		return null;
	}
	
	/**
	 * @return     true if this Class is a child of the selected ClassId.
	 * @param  cid The parent ClassId to check
	 */
	public final boolean childOf(ClassId cid)
	{
		if (parent == null)
		{
			return false;
		}
		
		if (parent == cid)
		{
			return true;
		}
		
		return parent.childOf(cid);
	}
	
	/**
	 * @return     true if this Class is equal to the selected ClassId or a child of the selected ClassId.
	 * @param  cid The parent ClassId to check
	 */
	public final boolean equalsOrChildOf(ClassId cid)
	{
		return (this == cid) || childOf(cid);
	}
	
	/**
	 * @return the child level of this Class (0=root, 1=child level 1...).
	 */
	public final int level()
	{
		if (parent == null)
		{
			return 0;
		}
		
		return 1 + parent.level();
	}
	
	/**
	 * @return its parent ClassId
	 */
	public final ClassId getParent()
	{
		return parent;
	}
}
