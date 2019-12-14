package l2j.gameserver.model.actor.manager.character.templates;

import java.util.Map;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.manager.character.skills.Skill;

/**
 * @author fissban
 */
public class CharTemplate
{
	// baseStats
	private final int baseSTR;
	private final int baseCON;
	private final int baseDEX;
	private final int baseINT;
	private final int baseWIT;
	private final int baseMEN;
	private final float baseHpMax;
	private final float baseCpMax;
	private final float baseMpMax;
	
	/** HP Regen base */
	private final float baseHpReg;
	
	/** MP Regen base */
	private final float baseMpReg;
	
	private final float basePAtk;
	private final float baseMAtk;
	private final float basePDef;
	private final float baseMDef;
	private final int basePAtkSpd;
	private final int baseMAtkSpd;
	private final float baseMReuseRate;
	private final int baseShldDef;
	private final int baseAtkRange;
	private final int baseShldRate;
	private final int baseCritRate;
	private final int baseMCritRate;
	private final int baseWalkSpd;
	private final int baseRunSpd;
	
	// SpecialStats
	private final double baseAggressionVuln;
	private final double baseBleedVuln;
	private final double basePoisonVuln;
	private final double baseStunVuln;
	private final double baseRootVuln;
	private final double baseMovementVuln;
	private final double baseConfusionVuln;
	private final double baseDerangementVul;
	private final double baseSleepVuln;
	private final double baseFireVuln;
	private final double baseWindVuln;
	private final double baseWaterVuln;
	private final double baseEarthVuln;
	private final double baseHolyVuln;
	private final double baseDarkVuln;
	
	// C4 Stats
	private final int baseMpConsumeRate;
	private final int baseHpConsumeRate;
	
	// by fissban
	private final int baseEvasion;
	private final int baseAccuracy;
	
	protected double collisionRadius;
	protected double collisionHeight;
	
	public CharTemplate(StatsSet set)
	{
		// base stats
		baseSTR = set.getInteger("str", 0);
		baseCON = set.getInteger("con", 0);
		baseDEX = set.getInteger("dex", 0);
		baseINT = set.getInteger("int", 0);
		baseWIT = set.getInteger("wit", 0);
		baseMEN = set.getInteger("men", 0);
		baseHpMax = set.getFloat("hpBase", 0);
		baseCpMax = set.getFloat("cpBase", 0);
		baseMpMax = set.getFloat("mpBase", 0);
		baseHpReg = set.getFloat("hpReg", 0);
		baseMpReg = set.getFloat("mpReg", 0);
		basePAtk = set.getFloat("pAtk", 0);
		baseMAtk = set.getFloat("mAtk", 0);
		basePDef = set.getFloat("pDef", 0);
		baseMDef = set.getFloat("mDef", 0);
		basePAtkSpd = set.getInteger("pAtkSpd", 0);
		baseMAtkSpd = set.getInteger("mAtkSpd", 0);
		baseMReuseRate = set.getFloat("mReuseDelay", 1.f);
		baseShldDef = set.getInteger("shldDef", 0);
		baseAtkRange = set.getInteger("atkRange", 40);
		baseShldRate = set.getInteger("shldRate", 0);
		baseCritRate = set.getInteger("critRate", 0);
		baseMCritRate = set.getInteger("mCritRate", 8);
		baseWalkSpd = set.getInteger("walkSpd", 0);
		baseRunSpd = set.getInteger("runSpd", 0);
		
		// by fissban
		baseEvasion = set.getInteger("evasion", 0);
		baseAccuracy = set.getInteger("accuracy", 0);
		
		// SpecialStats
		baseAggressionVuln = set.getDouble("aggressionVuln", 1.0);
		baseBleedVuln = set.getDouble("bleedVuln", 1.0);
		basePoisonVuln = set.getDouble("poisonVuln", 1.0);
		baseStunVuln = set.getDouble("stunVuln", 1.0);
		baseRootVuln = set.getDouble("rootVuln", 1.0);
		baseMovementVuln = set.getDouble("movementVuln", 1.0);
		baseConfusionVuln = set.getDouble("confusionVuln", 1.0);
		baseDerangementVul = set.getDouble("derangementVuln", 1.0);
		baseSleepVuln = set.getDouble("sleepVuln", 1.0);
		baseFireVuln = set.getDouble("fireVuln", 1.0);
		baseWindVuln = set.getDouble("windVuln", 1.0);
		baseWaterVuln = set.getDouble("waterVuln", 1.0);
		baseEarthVuln = set.getDouble("earthVuln", 1.0);
		baseHolyVuln = set.getDouble("holyVuln", 1.0);
		baseDarkVuln = set.getDouble("darkVuln", 1.0);
		
		// C4 Stats
		baseMpConsumeRate = set.getInteger("mpConsumeRate", 0);
		baseHpConsumeRate = set.getInteger("hpConsumeRate", 0);
		
		collisionRadius = set.getDouble("collisionRadius", 0);
		collisionHeight = set.getDouble("collisionHeight", 0);
	}
	
	public int getBaseSTR()
	{
		return baseSTR;
	}
	
	public int getBaseCON()
	{
		return baseCON;
	}
	
	public int getBaseDEX()
	{
		return baseDEX;
	}
	
	public int getBaseINT()
	{
		return baseINT;
	}
	
	public int getBaseWIT()
	{
		return baseWIT;
	}
	
	public int getBaseMEN()
	{
		return baseMEN;
	}
	
	public float getBaseHpMax()
	{
		return baseHpMax;
	}
	
	public float getBaseCpMax()
	{
		return baseCpMax;
	}
	
	public float getBaseMpMax()
	{
		return baseMpMax;
	}
	
	public float getBaseHpReg()
	{
		return baseHpReg;
	}
	
	public float getBaseMpReg()
	{
		return baseMpReg;
	}
	
	public float getBasePAtk()
	{
		return basePAtk;
	}
	
	public float getBaseMAtk()
	{
		return baseMAtk;
	}
	
	public float getBasePDef()
	{
		return basePDef;
	}
	
	public float getBaseMDef()
	{
		return baseMDef;
	}
	
	public int getBasePAtkSpd()
	{
		return basePAtkSpd;
	}
	
	public int getBaseMAtkSpd()
	{
		return baseMAtkSpd;
	}
	
	public float getBaseMReuseRate()
	{
		return baseMReuseRate;
	}
	
	public int getBaseShldDef()
	{
		return baseShldDef;
	}
	
	public int getBaseAtkRange()
	{
		return baseAtkRange;
	}
	
	public int getBaseShldRate()
	{
		return baseShldRate;
	}
	
	public int getBaseCritRate()
	{
		return baseCritRate;
	}
	
	public int getBaseMCritRate()
	{
		return baseMCritRate;
	}
	
	public int getBaseWalkSpd()
	{
		return baseWalkSpd;
	}
	
	public int getBaseRunSpd()
	{
		return baseRunSpd;
	}
	
	public double getBaseAggressionVuln()
	{
		return baseAggressionVuln;
	}
	
	public double getBaseBleedVuln()
	{
		return baseBleedVuln;
	}
	
	public double getBasePoisonVuln()
	{
		return basePoisonVuln;
	}
	
	public double getBaseStunVuln()
	{
		return baseStunVuln;
	}
	
	public double getBaseRootVuln()
	{
		return baseRootVuln;
	}
	
	public double getBaseMovementVuln()
	{
		return baseMovementVuln;
	}
	
	public double getBaseConfusionVuln()
	{
		return baseConfusionVuln;
	}
	
	public double getBaseDerangementVuln()
	{
		return baseDerangementVul;
	}
	
	public double getBaseSleepVuln()
	{
		return baseSleepVuln;
	}
	
	public double getBaseFireVuln()
	{
		return baseFireVuln;
	}
	
	public double getBaseWindVuln()
	{
		return baseWindVuln;
	}
	
	public double getBaseWaterVuln()
	{
		return baseWaterVuln;
	}
	
	public double getBaseEarthVuln()
	{
		return baseEarthVuln;
	}
	
	public double getBaseHolyVuln()
	{
		return baseHolyVuln;
	}
	
	public double getBaseDarkVuln()
	{
		return baseDarkVuln;
	}
	
	public int getBaseMpConsumeRate()
	{
		return baseMpConsumeRate;
	}
	
	public int getBaseHpConsumeRate()
	{
		return baseHpConsumeRate;
	}
	
	public int getBaseEvasion()
	{
		return baseEvasion;
	}
	
	public int getBaseAccuracy()
	{
		return baseAccuracy;
	}
	
	/**
	 * Overridden in NpcTemplate
	 * @return the characters skills
	 */
	public Map<Integer, Skill> getSkills()
	{
		return null;
	}
	
	public final double getCollisionRadius()
	{
		return collisionRadius;
	}
	
	public final double getCollisionHeight()
	{
		return collisionHeight;
	}
}
