package l2j.gameserver.model.items;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.skills.Skill;

/**
 * This class is dedicated to the management of armors.
 * @version $Revision: 1.2.2.1.2.6 $ $Date: 2005/03/27 15:30:10 $
 */
public final class ItemArmor extends Item
{
	private final int avoidModifier;
	private final int pDef;
	private final int mDef;
	private final int mpBonus;
	private final int hpBonus;
	private Skill itemSkill = null; // for passive skill
	
	/**
	 * Constructor for Armor.<BR>
	 * <BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <li>_avoidModifier</li>
	 * <li>_pDef & mDef</li>
	 * <li>_mpBonus & hpBonus</li>
	 * @param type : L2ArmorType designating the type of armor
	 * @param set  : StatsSet designating the set of couples (key,value) characterizing the armor
	 * @see        Item constructor
	 */
	public ItemArmor(ArmorType type, StatsSet set)
	{
		super(type, set);
		avoidModifier = set.getInteger("avoid_modify");
		pDef = set.getInteger("p_def");
		mDef = set.getInteger("m_def");
		mpBonus = set.getInteger("mp_bonus", 0);
		hpBonus = set.getInteger("hp_bonus", 0);
		
		int sId = set.getInteger("item_skill_id");
		int sLv = set.getInteger("item_skill_lvl");
		if ((sId > 0) && (sLv > 0))
		{
			itemSkill = SkillData.getInstance().getSkill(sId, sLv);
		}
	}
	
	/**
	 * Returns the type of the armor.
	 * @return L2ArmorType
	 */
	@Override
	public ArmorType getType()
	{
		return (ArmorType) super.type;
	}
	
	/**
	 * Returns the ID of the item after applying the mask.
	 * @return int
	 */
	@Override
	public final int getMask()
	{
		return getType().mask();
	}
	
	/**
	 * Returns the magical defense of the armor
	 * @return int
	 */
	public final int getMDef()
	{
		return mDef;
	}
	
	/**
	 * Returns the physical defense of the armor
	 * @return int
	 */
	public final int getPDef()
	{
		return pDef;
	}
	
	/**
	 * Returns avoid modifier given by the armor
	 * @return int
	 */
	public final int getAvoidModifier()
	{
		return avoidModifier;
	}
	
	/**
	 * Returns magical bonus given by the armor
	 * @return int
	 */
	public final int getMpBonus()
	{
		return mpBonus;
	}
	
	/**
	 * Returns physical bonus given by the armor
	 * @return int
	 */
	public final int getHpBonus()
	{
		return hpBonus;
	}
	
	/**
	 * Returns passive skill linked to that armor
	 * @return Skill
	 */
	public Skill getSkill()
	{
		return itemSkill;
	}
}
