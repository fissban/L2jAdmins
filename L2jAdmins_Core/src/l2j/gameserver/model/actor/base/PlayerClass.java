package l2j.gameserver.model.actor.base;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public enum PlayerClass
{
	HUMAN_FIGHTER(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FIRST),
	WARRIOR(Race.HUMAN, ClassType.FIGHTER, ClassLevel.SECOND),
	GLADIATOR(Race.HUMAN, ClassType.FIGHTER, ClassLevel.THIRD),
	WARLORD(Race.HUMAN, ClassType.FIGHTER, ClassLevel.THIRD),
	HUMAN_KNIGHT(Race.HUMAN, ClassType.FIGHTER, ClassLevel.SECOND),
	PALADIN(Race.HUMAN, ClassType.FIGHTER, ClassLevel.THIRD),
	DARK_AVENGER(Race.HUMAN, ClassType.FIGHTER, ClassLevel.THIRD),
	ROGUE(Race.HUMAN, ClassType.FIGHTER, ClassLevel.SECOND),
	TREASURE_HUNTER(Race.HUMAN, ClassType.FIGHTER, ClassLevel.THIRD),
	HAWKEYE(Race.HUMAN, ClassType.FIGHTER, ClassLevel.THIRD),
	HUMAN_MYSTIC(Race.HUMAN, ClassType.MYSTIC, ClassLevel.FIRST),
	HUMAN_WIZARD(Race.HUMAN, ClassType.MYSTIC, ClassLevel.SECOND),
	SORCEROR(Race.HUMAN, ClassType.MYSTIC, ClassLevel.THIRD),
	NECROMANCER(Race.HUMAN, ClassType.MYSTIC, ClassLevel.THIRD),
	WARLOCK(Race.HUMAN, ClassType.MYSTIC, ClassLevel.THIRD),
	CLERIC(Race.HUMAN, ClassType.PRIEST, ClassLevel.SECOND),
	BISHOP(Race.HUMAN, ClassType.PRIEST, ClassLevel.THIRD),
	PROPHET(Race.HUMAN, ClassType.PRIEST, ClassLevel.THIRD),
	
	ELVEN_FIGHTER(Race.ELF, ClassType.FIGHTER, ClassLevel.FIRST),
	ELVEN_KNIGHT(Race.ELF, ClassType.FIGHTER, ClassLevel.SECOND),
	TEMPLATE_KNIGHT(Race.ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	SWORD_SINGER(Race.ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	ELVEN_SCOUT(Race.ELF, ClassType.FIGHTER, ClassLevel.SECOND),
	PLAINS_WALKER(Race.ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	SILVER_RANGER(Race.ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	ELVEN_MYSTIC(Race.ELF, ClassType.MYSTIC, ClassLevel.FIRST),
	ELVEN_WIZARD(Race.ELF, ClassType.MYSTIC, ClassLevel.SECOND),
	SPELLSINGER(Race.ELF, ClassType.MYSTIC, ClassLevel.THIRD),
	ELEMENTAL_SUMMONER(Race.ELF, ClassType.MYSTIC, ClassLevel.THIRD),
	ELVEN_ORACLE(Race.ELF, ClassType.PRIEST, ClassLevel.SECOND),
	ELVEN_ELDER(Race.ELF, ClassType.PRIEST, ClassLevel.THIRD),
	
	DARK_ELVEN_FIGHTER(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.FIRST),
	PALUS_KNIGHT(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.SECOND),
	SHILLIEN_KNIGHT(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	BLADE_DANCER(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	ASSASSIN(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.SECOND),
	ABYSS_WALKER(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	PHANTOM_RANGER(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.THIRD),
	DARK_ELVEN_MYSTIC(Race.DARK_ELF, ClassType.MYSTIC, ClassLevel.FIRST),
	DARK_ELVEN_WIZARD(Race.DARK_ELF, ClassType.MYSTIC, ClassLevel.SECOND),
	SPELLHOWLER(Race.DARK_ELF, ClassType.MYSTIC, ClassLevel.THIRD),
	PHANTOM_SUMMONER(Race.DARK_ELF, ClassType.MYSTIC, ClassLevel.THIRD),
	SHILLIEN_ORACLE(Race.DARK_ELF, ClassType.PRIEST, ClassLevel.SECOND),
	SHILLIEND_ELDER(Race.DARK_ELF, ClassType.PRIEST, ClassLevel.THIRD),
	
	ORC_FIGHTER(Race.ORC, ClassType.FIGHTER, ClassLevel.FIRST),
	ORC_RAIDER(Race.ORC, ClassType.FIGHTER, ClassLevel.SECOND),
	DESTROYER(Race.ORC, ClassType.FIGHTER, ClassLevel.THIRD),
	ORC_MONK(Race.ORC, ClassType.FIGHTER, ClassLevel.SECOND),
	TYRANT(Race.ORC, ClassType.FIGHTER, ClassLevel.THIRD),
	ORC_MYSTIC(Race.ORC, ClassType.MYSTIC, ClassLevel.FIRST),
	ORC_SHAMAN(Race.ORC, ClassType.MYSTIC, ClassLevel.SECOND),
	OVERLORD(Race.ORC, ClassType.MYSTIC, ClassLevel.THIRD),
	WARCRYER(Race.ORC, ClassType.MYSTIC, ClassLevel.THIRD),
	
	DWARVEN_FIGHTER(Race.DWARF, ClassType.FIGHTER, ClassLevel.FIRST),
	DWARVEN_SCAVENGER(Race.DWARF, ClassType.FIGHTER, ClassLevel.SECOND),
	BOUNTY_HUNTER(Race.DWARF, ClassType.FIGHTER, ClassLevel.THIRD),
	DWARVEN_ARTISAN(Race.DWARF, ClassType.FIGHTER, ClassLevel.SECOND),
	WARSMITH(Race.DWARF, ClassType.FIGHTER, ClassLevel.THIRD),
	
	dummyEntry1(null, null, null),
	dummyEntry2(null, null, null),
	dummyEntry3(null, null, null),
	dummyEntry4(null, null, null),
	dummyEntry5(null, null, null),
	dummyEntry6(null, null, null),
	dummyEntry7(null, null, null),
	dummyEntry8(null, null, null),
	dummyEntry9(null, null, null),
	dummyEntry10(null, null, null),
	dummyEntry11(null, null, null),
	dummyEntry12(null, null, null),
	dummyEntry13(null, null, null),
	dummyEntry14(null, null, null),
	dummyEntry15(null, null, null),
	dummyEntry16(null, null, null),
	dummyEntry17(null, null, null),
	dummyEntry18(null, null, null),
	dummyEntry19(null, null, null),
	dummyEntry20(null, null, null),
	dummyEntry21(null, null, null),
	dummyEntry22(null, null, null),
	dummyEntry23(null, null, null),
	dummyEntry24(null, null, null),
	dummyEntry25(null, null, null),
	dummyEntry26(null, null, null),
	dummyEntry27(null, null, null),
	dummyEntry28(null, null, null),
	dummyEntry29(null, null, null),
	dummyEntry30(null, null, null),
	
	/*
	 * (3rd classes)
	 */
	DUELIST(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FOURTH),
	DREADNOUGHT(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FOURTH),
	PHOENIX_KNIGHT(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FOURTH),
	HELL_KNIGHT(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FOURTH),
	SAGITTARIUS(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FOURTH),
	ADVENTURER(Race.HUMAN, ClassType.FIGHTER, ClassLevel.FOURTH),
	ARCHMAGE(Race.HUMAN, ClassType.MYSTIC, ClassLevel.FOURTH),
	SOULTAKER(Race.HUMAN, ClassType.MYSTIC, ClassLevel.FOURTH),
	ARCANALORD(Race.HUMAN, ClassType.MYSTIC, ClassLevel.FOURTH),
	CARDINAL(Race.HUMAN, ClassType.PRIEST, ClassLevel.FOURTH),
	HIEROPHANT(Race.HUMAN, ClassType.PRIEST, ClassLevel.FOURTH),
	
	EVA_TEMPLATER(Race.ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	SWORD_MUSE(Race.ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	WIND_RIDER(Race.ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	MOONLIGHT_SENTINAL(Race.ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	MYSTIC_MUSE(Race.ELF, ClassType.MYSTIC, ClassLevel.FOURTH),
	ELEMENTAL_MASTER(Race.ELF, ClassType.MYSTIC, ClassLevel.FOURTH),
	EVA_SAINT(Race.ELF, ClassType.PRIEST, ClassLevel.FOURTH),
	
	SHILLIEN_TEMPLAR(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	SPECTRAL_DANCER(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	GHOST_HUNTER(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	GHOST_SENTINEL(Race.DARK_ELF, ClassType.FIGHTER, ClassLevel.FOURTH),
	STORM_SCREAMER(Race.DARK_ELF, ClassType.MYSTIC, ClassLevel.FOURTH),
	SPECTRAL_MASTER(Race.DARK_ELF, ClassType.MYSTIC, ClassLevel.FOURTH),
	SHILLIENT_SAINT(Race.DARK_ELF, ClassType.PRIEST, ClassLevel.FOURTH),
	
	TITAN(Race.ORC, ClassType.FIGHTER, ClassLevel.FOURTH),
	GRAND_KHAVATARI(Race.ORC, ClassType.FIGHTER, ClassLevel.FOURTH),
	DOMINATOR(Race.ORC, ClassType.MYSTIC, ClassLevel.FOURTH),
	DOOMCRYER(Race.ORC, ClassType.MYSTIC, ClassLevel.FOURTH),
	
	FORTUNE_SEEKER(Race.DWARF, ClassType.FIGHTER, ClassLevel.FOURTH),
	MAESTRO(Race.DWARF, ClassType.FIGHTER, ClassLevel.FOURTH);
	
	private Race race;
	private ClassLevel level;
	private ClassType type;
	
	private static final Set<PlayerClass> mainSubclassSet;
	private static final Set<PlayerClass> neverSubclassed = EnumSet.of(OVERLORD, WARSMITH);
	
	private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(DARK_AVENGER, PALADIN, TEMPLATE_KNIGHT, SHILLIEN_KNIGHT);
	private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(TREASURE_HUNTER, ABYSS_WALKER, PLAINS_WALKER);
	private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(HAWKEYE, SILVER_RANGER, PHANTOM_RANGER);
	private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(WARLOCK, ELEMENTAL_SUMMONER, PHANTOM_SUMMONER);
	private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(SORCEROR, SPELLSINGER, SPELLHOWLER);
	
	private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap<>(PlayerClass.class);
	
	static
	{
		Set<PlayerClass> subclasses = getSet(null, ClassLevel.THIRD);
		subclasses.removeAll(neverSubclassed);
		
		mainSubclassSet = subclasses;
		
		subclassSetMap.put(DARK_AVENGER, subclasseSet1);
		subclassSetMap.put(PALADIN, subclasseSet1);
		subclassSetMap.put(TEMPLATE_KNIGHT, subclasseSet1);
		subclassSetMap.put(SHILLIEN_KNIGHT, subclasseSet1);
		
		subclassSetMap.put(TREASURE_HUNTER, subclasseSet2);
		subclassSetMap.put(ABYSS_WALKER, subclasseSet2);
		subclassSetMap.put(PLAINS_WALKER, subclasseSet2);
		
		subclassSetMap.put(HAWKEYE, subclasseSet3);
		subclassSetMap.put(SILVER_RANGER, subclasseSet3);
		subclassSetMap.put(PHANTOM_RANGER, subclasseSet3);
		
		subclassSetMap.put(WARLOCK, subclasseSet4);
		subclassSetMap.put(ELEMENTAL_SUMMONER, subclasseSet4);
		subclassSetMap.put(PHANTOM_SUMMONER, subclasseSet4);
		
		subclassSetMap.put(SORCEROR, subclasseSet5);
		subclassSetMap.put(SPELLSINGER, subclasseSet5);
		subclassSetMap.put(SPELLHOWLER, subclasseSet5);
	}
	
	private PlayerClass(Race pRace, ClassType pType, ClassLevel pLevel)
	{
		race = pRace;
		level = pLevel;
		type = pType;
	}
	
	public final Set<PlayerClass> getAvailableSubclasses(L2PcInstance player)
	{
		Set<PlayerClass> subclasses = null;
		
		if (level == ClassLevel.THIRD)
		{
			subclasses = EnumSet.copyOf(mainSubclassSet);
			
			subclasses.remove(this);
			
			switch (player.getRace())
			{
				case ELF:
					subclasses.removeAll(getSet(Race.DARK_ELF, ClassLevel.THIRD));
					break;
				case DARK_ELF:
					subclasses.removeAll(getSet(Race.ELF, ClassLevel.THIRD));
					break;
			}
			
			Set<PlayerClass> unavailableClasses = subclassSetMap.get(this);
			
			if (unavailableClasses != null)
			{
				subclasses.removeAll(unavailableClasses);
			}
		}
		
		return subclasses;
	}
	
	public static final EnumSet<PlayerClass> getSet(Race race, ClassLevel level)
	{
		EnumSet<PlayerClass> allOf = EnumSet.noneOf(PlayerClass.class);
		
		for (PlayerClass playerClass : EnumSet.allOf(PlayerClass.class))
		{
			if ((race == null) || playerClass.isOfRace(race))
			{
				if ((level == null) || playerClass.isOfLevel(level))
				{
					allOf.add(playerClass);
				}
			}
		}
		
		return allOf;
	}
	
	public final boolean isOfRace(Race pRace)
	{
		return race == pRace;
	}
	
	public final boolean isOfType(ClassType pType)
	{
		return type == pType;
	}
	
	public final boolean isOfLevel(ClassLevel pLevel)
	{
		return level == pLevel;
	}
	
	public final ClassLevel getLevel()
	{
		return level;
	}
}
