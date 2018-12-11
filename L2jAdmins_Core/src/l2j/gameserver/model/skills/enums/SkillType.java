package l2j.gameserver.model.skills.enums;

import java.lang.reflect.Constructor;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.skills.L2SkillDefault;
import l2j.gameserver.model.skills.Skill;

/**
 * @author fissban
 */
public enum SkillType
{
	PDAM,
	MDAM,
	HEAL,
	COMBATPOINTHEAL,
	MANAHEAL,
	MANARECHARGE,
	AGGDAMAGE,
	BUFF,
	DEBUFF,
	RESURRECT,
	PASSIVE,
	UNLOCK,
	CHARGE,
	MHOT,
	DRAIN,
	AGGREDUCE,
	AGGREMOVE,
	AGGREDUCE_CHAR,
	CHARGEDAM,
	DETECT_WEAKNESS,
	ENCHANT_ARMOR,
	ENCHANT_WEAPON,
	FEED_PET,
	LUCK,
	MANADAM,
	MUTE,
	RECALL,
	SOULSHOT,
	SPIRITSHOT,
	SPOIL,
	SWEEP,
	SUMMON,
	CUBIC,
	DEATHLINK_PET,
	MANA_BY_LEVEL,
	SIEGEFLAG,
	TAKECASTLE,
	SEED,
	DRAIN_SOUL,
	COMMON_CRAFT,
	DWARVEN_CRAFT,
	ITEM_SA,
	FISHING,
	PUMPING,
	REELING,
	CREATE_ITEM,
	STRSIEGEASSAULT,
	BALANCE_LIFE,
	BLOW,
	CPDAMPERCENT,
	
	SOW,
	HARVEST,
	DELUXE_KEY_UNLOCK,
	BEAST_FEED,
	TELEPORT_PC,
	DUMMY,
	// unimplemented
	NOTDONE;
	
	private final Class<? extends Skill> getClass;
	
	public Skill makeSkill(StatsSet set)
	{
		try
		{
			Constructor<? extends Skill> c = getClass.getConstructor(StatsSet.class);
			return c.newInstance(set);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private SkillType()
	{
		getClass = L2SkillDefault.class;
	}
	
	private SkillType(Class<? extends Skill> classType)
	{
		getClass = classType;
	}
}
