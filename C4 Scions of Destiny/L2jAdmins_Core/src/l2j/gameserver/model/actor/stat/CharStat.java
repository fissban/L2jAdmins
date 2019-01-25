package l2j.gameserver.model.actor.stat;

import l2j.Config;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.stats.Calculator;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import main.EngineModsManager;

public class CharStat
{
	private L2Character activeChar;
	private long exp = 0;
	private int sp = 0;
	private int level = 1;
	
	public CharStat(L2Character activeChar)
	{
		this.activeChar = activeChar;
	}
	
	/**
	 * Calculate the new value of the state with modifiers that will be applied on the targeted L2Character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * A L2Character owns a table of Calculators called <B>_calculators</B>. Each Calculator (a calculator per state) own a table of Func object. A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REG_HP_RATE...) : <BR>
	 * FuncAtkAccuracy -> Math.sqrt(player.getDEX())*6+_player.getLevel()<BR>
	 * When the calc method of a calculator is launched, each mathematics function is called according to its priority <B>_order</B>. Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order. The result of the calculation is stored in
	 * the value property of an Env class instance.<BR>
	 * @param  stat   The stat to calculate the new value with modifiers
	 * @param  init   The initial value of the stat before applying modifiers
	 * @param  target The L2Charcater whose properties will be used in the calculation (ex : CON, INT...)
	 * @param  skill  The Skill whose properties will be used in the calculation (ex : Level...)
	 * @return
	 */
	public final double calcStat(StatsType stat, double init, L2Character target, Skill skill)
	{
		if ((activeChar == null) || (stat == null))
		{
			return init;
		}
		
		// If no Func object found, no modifier is applied
		if (!activeChar.getCalculators().containsKey(stat))
		{
			return init;
		}
		
		Calculator c = activeChar.getCalculators().get(stat);
		
		// If no Func object found, no modifier is applied
		if (c.isEmpty())
		{
			return init;
		}
		
		// Create and init an Env object to pass parameters to the Calculator
		Env env = new Env();
		env.setPlayer(activeChar);
		env.setTarget(target);
		env.setSkill(skill);
		env.setValue(init);
		
		// Launch the calculation
		c.calc(env);
		
		// avoid some troubles with negative stats (some stats should never be negative)
		if (env.getValue() <= 0)
		{
			switch (stat)
			{
				case MAX_HP:
				case MAX_MP:
				case MAX_CP:
				case MAGICAL_DEFENCE:
				case PHYSICAL_DEFENCE:
				case PHYSICAL_ATTACK:
				case MAGICAL_ATTACK:
				case PHYSICAL_ATTACK_SPEED:
				case MAGICAL_ATTACK_SPEED:
				case SHIELD_DEFENCE_POWER:
				case STAT_CON:
				case STAT_DEX:
				case STAT_INT:
				case STAT_MEN:
				case STAT_STR:
				case STAT_WIT:
					env.setValue(1);
			}
		}
		
		return EngineModsManager.onStats(stat, activeChar, env.getValue());
	}
	
	public L2Character getActiveChar()
	{
		return activeChar;
	}
	
	// -------------------------------------------------------------------------------------------- //
	
	public long getExp()
	{
		return exp;
	}
	
	public void setExp(long value)
	{
		exp = value;
	}
	
	public int getSp()
	{
		return sp;
	}
	
	public void setSp(int value)
	{
		sp = value;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int value)
	{
		level = value;
	}
	
	// -------------------------------------------------------------------------------------------- //
	public int getMaxCp()
	{
		return (int) calcStat(StatsType.MAX_CP, activeChar.getTemplate().getBaseCpMax(), null, null);
	}
	
	public int getMaxHp()
	{
		return (int) calcStat(StatsType.MAX_HP, activeChar.getTemplate().getBaseHpMax(), null, null);
	}
	
	public int getMaxMp()
	{
		return (int) calcStat(StatsType.MAX_MP, activeChar.getTemplate().getBaseMpMax(), null, null);
	}
	
	// -------------------------------------------------------------------------------------------- //
	
	public final float getAttackSpeedMultiplier()
	{
		return (float) (((1.1) * getPAtkSpd()) / activeChar.getTemplate().getBasePAtkSpd());
	}
	
	public final double getCriticalDmg(L2Character target, double init)
	{
		return calcStat(StatsType.PHYSICAL_CRITICAL_POWER, init, target, null);
	}
	
	public int getCriticalHit(L2Character target, Skill skill)
	{
		int criticalHit = (int) calcStat(StatsType.PHYSICAL_CRITICAL_RATE, activeChar.getTemplate().getBaseCritRate(), target, skill);
		
		if (criticalHit > Config.MAX_PCRIT_RATE)
		{
			criticalHit = Config.MAX_PCRIT_RATE;
		}
		
		return criticalHit;
	}
	
	public int getAccuracy()
	{
		return (int) Math.round(calcStat(StatsType.ACCURACY_COMBAT, 0, null, null));
	}
	
	public int getEvasionRate(L2Character target)
	{
		int evasion = (int) Math.round(calcStat(StatsType.EVASION_RATE, 0, target, null));
		
		if (evasion > Config.MAX_EVASION)
		{
			evasion = Config.MAX_EVASION;
		}
		
		return evasion;
	}
	
	public final int getMCriticalHit(L2Character target, Skill skill)
	{
		double mrate = calcStat(StatsType.MAGICAL_CRITICAL_RATE, activeChar.getTemplate().getBaseMCritRate(), target, skill);
		
		if (mrate > Config.MAX_MCRIT_RATE)
		{
			mrate = Config.MAX_MCRIT_RATE;
		}
		
		return (int) mrate;
	}
	
	public int getPDef(L2Character target)
	{
		// Get the base PDef of the L2Character
		double defence = activeChar.getTemplate().getBasePDef();
		
		// Calculate modifier for Raid Bosses
		if (activeChar.isRaid())
		{
			defence *= Config.RAID_PDEFENCE_MULTIPLIER;
		}
		
		return (int) calcStat(StatsType.PHYSICAL_DEFENCE, defence, target, null);
	}
	
	public int getMDef(L2Character target, Skill skill)
	{
		// Get the base MDef of the L2Character
		double defence = activeChar.getTemplate().getBaseMDef();
		
		// Calculate modifier for Raid Bosses
		if (activeChar.isRaid())
		{
			defence *= Config.RAID_MDEFENCE_MULTIPLIER;
		}
		
		// Calculate modifiers Magic Attack
		return (int) calcStat(StatsType.MAGICAL_DEFENCE, defence, target, skill);
	}
	
	public float getMovementSpeedMultiplier()
	{
		return getRunSpeed() / (float) activeChar.getTemplate().getBaseRunSpd();
	}
	
	public float getMoveSpeed()
	{
		return activeChar.isRunning() ? getRunSpeed() : getWalkSpeed();
	}
	
	public int getWalkSpeed()
	{
		return (int) calcStat(StatsType.WALK_SPEED, activeChar.getTemplate().getBaseWalkSpd(), null, null);
	}
	
	public int getRunSpeed()
	{
		return (int) (calcStat(StatsType.RUN_SPEED, activeChar.getTemplate().getBaseRunSpd(), null, null));
	}
	
	public final double getMReuseRate(Skill skill)
	{
		return calcStat(StatsType.MAGICAL_SKILL_REUSE, activeChar.getTemplate().getBaseMReuseRate(), null, skill);
	}
	
	public int getMAtk(L2Character target, Skill skill)
	{
		return (int) calcStat(StatsType.MAGICAL_ATTACK, activeChar.getTemplate().getBaseMAtk(), target, skill);
	}
	
	public int getPAtk(L2Character target)
	{
		return (int) calcStat(StatsType.PHYSICAL_ATTACK, activeChar.getTemplate().getBasePAtk(), target, null);
	}
	
	public int getMAtkSpd()
	{
		double val = calcStat(StatsType.MAGICAL_ATTACK_SPEED, activeChar.getTemplate().getBaseMAtkSpd(), null, null);
		
		if (activeChar instanceof L2PcInstance)
		{
			if ((val > Config.MAX_MATK_SPEED) && !((L2PcInstance) activeChar).isGM())
			{
				val = Config.MAX_MATK_SPEED;
			}
		}
		return (int) val;
	}
	
	public int getPAtkSpd()
	{
		int val = (int) Math.round(calcStat(StatsType.PHYSICAL_ATTACK_SPEED, activeChar.getTemplate().getBasePAtkSpd(), null, null));
		if (activeChar instanceof L2PcInstance)
		{
			if ((val > Config.MAX_PATK_SPEED) && !((L2PcInstance) activeChar).isGM())
			{
				val = Config.MAX_PATK_SPEED;
			}
		}
		return val;
	}
	
	public final int getMagicalAttackRange(Skill skill)
	{
		if (skill != null)
		{
			return (int) calcStat(StatsType.MAGICAL_ATTACK_RANGE, skill.getCastRange(), null, skill);
		}
		return activeChar.getTemplate().getBaseAtkRange();
	}
	
	public final int getPhysicalAttackRange()
	{
		// Polearm handled here for now.
		ItemWeapon weaponItem = activeChar.getActiveWeaponItem();
		if ((weaponItem != null) && (weaponItem.getType() == WeaponType.POLE))
		{
			return (int) calcStat(StatsType.PHYSICAL_ATTACK_RANGE, 80, null, null); // C4off base attack_range for weapon type pole
		}
		// Bows
		else if ((weaponItem != null) && (weaponItem.getType() == WeaponType.BOW))
		{
			return (int) calcStat(StatsType.PHYSICAL_ATTACK_RANGE, 500, null, null); // C4off base attack_range for weapon type bow
		}
		
		return (int) calcStat(StatsType.PHYSICAL_ATTACK_RANGE, activeChar.getTemplate().getBaseAtkRange(), null, null);
	}
	
	public final double getWeaponReuseModifier(L2Character target)
	{
		return calcStat(StatsType.PHYSICAL_ATTACK_REUSE, 1, target, null);
	}
	
	public final int getShldDef()
	{
		return (int) calcStat(StatsType.SHIELD_DEFENCE_POWER, 0, null, null);
	}
	
	public final int getMpConsume(Skill skill)
	{
		if (skill == null)
		{
			return 1;
		}
		
		double mpConsume = skill.getMpConsume();
		if ((skill.getNextDanceMpCost() > 0) && (activeChar.getDanceCount() > 0))
		{
			mpConsume += activeChar.getDanceCount() * skill.getNextDanceMpCost();
		}
		
		mpConsume = calcStat(StatsType.MP_CONSUME_RATE, mpConsume, null, skill);
		
		if (skill.isMagic())
		{
			return (int) calcStat(StatsType.MAGICAL_MP_CONSUME_RATE, mpConsume, null, null);
		}
		return (int) calcStat(StatsType.PHYSICAL_MP_CONSUME_RATE, mpConsume, null, null);
	}
	
	public final int getMpInitialConsume(Skill skill)
	{
		if (skill == null)
		{
			return 1;
		}
		
		double mpConsume = calcStat(StatsType.MP_CONSUME_RATE, skill.getMpInitialConsume(), null, skill);
		
		if (skill.isMagic())
		{
			return (int) calcStat(StatsType.MAGICAL_MP_CONSUME_RATE, mpConsume, null, null);
		}
		return (int) calcStat(StatsType.PHYSICAL_MP_CONSUME_RATE, mpConsume, null, null);
	}
	
	public final int getCON()
	{
		return (int) calcStat(StatsType.STAT_CON, activeChar.getTemplate().getBaseCON(), null, null);
	}
	
	public final int getDEX()
	{
		return (int) calcStat(StatsType.STAT_DEX, activeChar.getTemplate().getBaseDEX(), null, null);
	}
	
	public final int getINT()
	{
		return (int) calcStat(StatsType.STAT_INT, activeChar.getTemplate().getBaseINT(), null, null);
	}
	
	public final int getMEN()
	{
		return (int) calcStat(StatsType.STAT_MEN, activeChar.getTemplate().getBaseMEN(), null, null);
	}
	
	public final int getSTR()
	{
		return (int) calcStat(StatsType.STAT_STR, activeChar.getTemplate().getBaseSTR(), null, null);
	}
	
	public final int getWIT()
	{
		return (int) calcStat(StatsType.STAT_WIT, activeChar.getTemplate().getBaseWIT(), null, null);
	}
}
