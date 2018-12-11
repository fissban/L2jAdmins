package l2j.gameserver.model.skills.stats;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;

/**
 * An Env object is just a class to pass parameters to a calculator such as L2PcInstance, L2ItemInstance, Initial value.
 */
public final class Env
{
	private L2Character player;
	private L2Character target;
	private ItemInstance item;
	private Skill skill;
	private double value;
	private double baseValue;
	private boolean skillMastery = false;
	
	// ---------- GET ---------- //
	
	/**
	 * @return player (L2Character)
	 */
	public L2Character getPlayer()
	{
		return player;
	}
	
	/**
	 * @return target (L2Character)
	 */
	public L2Character getTarget()
	{
		return target;
	}
	
	public ItemInstance getItem()
	{
		return item;
	}
	
	/**
	 * @return skill (Skill)
	 */
	public Skill getSkill()
	{
		return skill;
	}
	
	/**
	 * @return value
	 */
	public double getValue()
	{
		return value;
	}
	
	/**
	 * @return
	 */
	public boolean isSkillMastery()
	{
		return skillMastery;
	}
	
	// ---------- SET ---------- //
	
	/**
	 * @param player (L2Character)
	 */
	public void setPlayer(L2Character player)
	{
		this.player = player;
	}
	
	/**
	 * @param target (L2Character)
	 */
	public void setTarget(L2Character target)
	{
		this.target = target;
	}
	
	public void setItem(ItemInstance item)
	{
		this.item = item;
	}
	
	/**
	 * @param skill (Skill)
	 */
	public void setSkill(Skill skill)
	{
		this.skill = skill;
	}
	
	/**
	 * Set value
	 * @param value
	 */
	public void setValue(double value)
	{
		this.value = value;
	}
	
	/**
	 * Increase value
	 * @param value
	 */
	public void incValue(double value)
	{
		this.value += value;
	}
	
	/**
	 * Decrease value
	 * @param value
	 */
	public void decValue(double value)
	{
		this.value -= value;
	}
	
	/**
	 * Divide value
	 * @param value
	 */
	public void divValue(double value)
	{
		this.value /= value;
	}
	
	/**
	 * Multiply value
	 * @param value
	 */
	public void mulValue(double value)
	{
		this.value *= value;
	}
	
	public void setSkillMastery(boolean skillMastery)
	{
		this.skillMastery = skillMastery;
	}
	
	/**
	 * @return the baseValue
	 */
	public double getBaseValue()
	{
		return baseValue;
	}
	
	/**
	 * @param baseValue the baseValue to set
	 */
	public void setBaseValue(double baseValue)
	{
		this.baseValue = baseValue;
	}
}
