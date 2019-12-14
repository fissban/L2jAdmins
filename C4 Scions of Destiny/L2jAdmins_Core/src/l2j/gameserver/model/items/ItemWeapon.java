package l2j.gameserver.model.items;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.conditions.ConditionGameChance;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.model.items.enums.WeaponType;

/**
 * This class is dedicated to the management of weapons.
 * @version $Revision: 1.4.2.3.2.5 $ $Date: 2005/04/02 15:57:51 $
 */
public final class ItemWeapon extends Item
{
	private final int soulShotCount;
	private final int spiritShotCount;
	private final int pDam;
	private final int rndDam;
	private final int critical;
	private final double hitModifier;
	private final int avoidModifier;
	private final int shieldDef;
	private final double shieldDefRate;
	private final int atkSpeed;
	private final int atkReuse;
	private final int mpConsume;
	private final int mDam;
	
	private Skill itemSkill = null; // for passive skill
	
	// Attached skills for Special Abilities
	protected List<Skill> skillsOnCast = new ArrayList<>();
	protected List<Skill> skillsOnCrit = new ArrayList<>();
	
	/**
	 * Constructor for Weapon.<BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <li>_soulShotCount & spiritShotCount</li>
	 * <li>_pDam & mDam & rndDam</li>
	 * <li>_critical</li>
	 * <li>_hitModifier</li>
	 * <li>_avoidModifier</li>
	 * <li>_shieldDes & shieldDefRate</li>
	 * <li>_atkSpeed & AtkReuse</li>
	 * <li>_mpConsume</li>
	 * @param type : L2ArmorType designating the type of armor
	 * @param set  : StatsSet designating the set of couples (key,value) caracterizing the armor
	 * @see        Item constructor
	 */
	public ItemWeapon(WeaponType type, StatsSet set)
	{
		super(type, set);
		soulShotCount = set.getInteger("soulshots");
		spiritShotCount = set.getInteger("spiritshots");
		pDam = set.getInteger("p_dam");
		rndDam = set.getInteger("rnd_dam");
		critical = set.getInteger("critical");
		hitModifier = set.getDouble("hit_modify");
		avoidModifier = set.getInteger("avoid_modify");
		shieldDef = set.getInteger("shield_def");
		shieldDefRate = set.getDouble("shield_def_rate");
		atkSpeed = set.getInteger("atk_speed");
		atkReuse = set.getInteger("atk_reuse", type == WeaponType.BOW ? 1500 : 0);
		mpConsume = set.getInteger("mp_consume");
		mDam = set.getInteger("m_dam");
		
		int sId = set.getInteger("item_skill_id");
		int sLv = set.getInteger("item_skill_lvl");
		if ((sId > 0) && (sLv > 0))
		{
			itemSkill = SkillData.getInstance().getSkill(sId, sLv);
		}
		
		sId = set.getInteger("onCast_skill_id");
		sLv = set.getInteger("onCast_skill_lvl");
		int sCh = set.getInteger("onCast_skill_chance");
		if ((sId > 0) && (sLv > 0) && (sCh > 0))
		{
			Skill skill = SkillData.getInstance().getSkill(sId, sLv);
			skill.attach(new ConditionGameChance(sCh), true);
			attachOnCast(skill);
		}
		
		sId = set.getInteger("onCrit_skill_id");
		sLv = set.getInteger("onCrit_skill_lvl");
		sCh = set.getInteger("onCrit_skill_chance");
		if ((sId > 0) && (sLv > 0) && (sCh > 0))
		{
			Skill skill = SkillData.getInstance().getSkill(sId, sLv);
			skill.attach(new ConditionGameChance(sCh), true);
			attachOnCrit(skill);
		}
	}
	
	/**
	 * Returns the type of Weapon
	 * @return L2WeaponType
	 */
	@Override
	public WeaponType getType()
	{
		return (WeaponType) super.type;
	}
	
	/**
	 * Returns the ID of the Etc item after applying the mask.
	 * @return int
	 */
	@Override
	public int getMask()
	{
		return getType().mask();
	}
	
	/**
	 * Returns the quantity of SoulShot used.
	 * @return int
	 */
	public int getSoulShotCount()
	{
		return soulShotCount;
	}
	
	/**
	 * Returns the quantity of SpiritShot used.
	 * @return int
	 */
	public int getSpiritShotCount()
	{
		return spiritShotCount;
	}
	
	/**
	 * Returns the physical damage.
	 * @return int
	 */
	public int getPDamage()
	{
		return pDam;
	}
	
	/**
	 * Returns the random damage inflicted by the weapon
	 * @return int
	 */
	public int getRandomDamage()
	{
		return rndDam;
	}
	
	/**
	 * Returns the attack speed of the weapon
	 * @return int
	 */
	public int getAttackSpeed()
	{
		return atkSpeed;
	}
	
	/**
	 * Return the Attack Reuse Delay of the L2Weapon.
	 * @return int
	 */
	public int getAttackReuseDelay()
	{
		return atkReuse;
	}
	
	/**
	 * Returns the avoid modifier of the weapon
	 * @return int
	 */
	public int getAvoidModifier()
	{
		return avoidModifier;
	}
	
	/**
	 * Returns the rate of critical hit
	 * @return int
	 */
	public int getCritical()
	{
		return critical;
	}
	
	/**
	 * Returns the hit modifier of the weapon
	 * @return double
	 */
	public double getHitModifier()
	{
		return hitModifier;
	}
	
	/**
	 * Returns the magical damage inflicted by the weapon
	 * @return int
	 */
	public int getMDamage()
	{
		return mDam;
	}
	
	/**
	 * Returns the MP consumption with the weapon
	 * @return int
	 */
	public int getMpConsume()
	{
		return mpConsume;
	}
	
	/**
	 * Returns the shield defense of the weapon
	 * @return int
	 */
	public int getShieldDef()
	{
		return shieldDef;
	}
	
	/**
	 * Returns the rate of shield defense of the weapon
	 * @return double
	 */
	public double getShieldDefRate()
	{
		return shieldDefRate;
	}
	
	/**
	 * Returns passive skill linked to that weapon
	 * @return Skill
	 */
	public Skill getSkill()
	{
		return itemSkill;
	}
	
	/**
	 * Returns effects of skills associated with the item to be triggered onHit.
	 * @param caster : L2Character pointing out the caster
	 * @param target : L2Character pointing out the target
	 */
	public void getOnCrit(L2Character caster, L2Character target)
	{
		if (skillsOnCrit == null)
		{
			return;
		}
		
		for (Skill skill : skillsOnCrit)
		{
			if (!skill.checkCondition(caster, true))
			{
				continue;
			}
			
			if (!Formulas.calcEffectSuccess(caster, target, skill, skill.getEffectPower(), false, false, false, false))
			{
				continue;
			}
			
			skill.getEffects(caster, target);
		}
	}
	
	/**
	 * Returns effects of skills associated with the item to be triggered onCast.
	 * @param caster  : L2Character pointing out the caster
	 * @param target  : L2Character pointing out the target
	 * @param trigger : Skill pointing out the skill triggering this action
	 */
	public void getOnCast(L2Character caster, L2Character target, Skill trigger)
	{
		for (var skill : skillsOnCast)
		{
			if (!skill.checkCondition(caster, true))
			{
				continue;
			}
			
			if (trigger.isOffensive() != skill.isOffensive())
			{
				continue; // Trigger only same type of skill
			}
			
			if (trigger.isToggle() && (skill.getSkillType() == SkillType.BUFF))
			{
				continue; // No buffing with toggle skills
			}
			
			if (skill.isOffensive() && !Formulas.calcEffectSuccess(caster, target, skill, skill.getEffectPower(), false, false, false, false))
			{
				continue;
			}
			
			skill.getEffects(caster, target);
		}
	}
	
	/**
	 * Add the Skill skill to the list of skills generated by the item triggered by critical hit
	 * @param skill : Skill
	 */
	public void attachOnCrit(Skill skill)
	{
		skillsOnCrit.add(skill);
	}
	
	/**
	 * Add the Skill skill to the list of skills generated by the item triggered by casting spell
	 * @param skill : Skill
	 */
	public void attachOnCast(Skill skill)
	{
		skillsOnCast.add(skill);
	}
}
