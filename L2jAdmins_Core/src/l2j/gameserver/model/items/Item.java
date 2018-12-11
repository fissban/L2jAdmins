package l2j.gameserver.model.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.model.items.enums.ItemType1;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.funcs.FuncTemplate;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.scripts.Script;

/**
 * This class contains all informations concerning the item (weapon, armor, etc).<BR>
 * Mother class of : <BR>
 * <li>ItemArmor</li>
 * <li>ItemEtcItem</li>
 * <li>ItemWeapon</li>
 * @version $Revision: 1.7.2.2.2.5 $ $Date: 2005/04/06 18:25:18 $
 */
public abstract class Item
{
	public static final int[] CRYSTAL_ENCHANT_BONUS_ARMOR =
	{
		0,
		11,
		6,
		11,
		19,
		25
	};
	
	public static final int[] CRYSTAL_ENCHANT_BONUS_WEAPON =
	{
		0,
		90,
		45,
		67,
		144,
		250
	};
	
	public static final int[] CRYSTAL_SCROLLS =
	{
		731,
		732,
		949,
		950,
		953,
		954,
		957,
		958,
		961,
		962
	};
	
	private final int itemId;
	private final String name;
	private final ItemType1 type1; // needed for item list (inventory)
	private final ItemType2 type2; // different lists for armor, weapon, etc
	private final int weight;
	private final boolean crystallizable;
	private final boolean stackable;
	private final CrystalType crystalType; // default to none-grade
	private final SlotType bodyPart;
	private final int referencePrice;
	private final int crystalCount;
	private final boolean sellable;
	private final boolean dropable;
	private final boolean destroyable;
	private final boolean tradeable;
	
	protected final Enum<?> type;
	
	protected List<FuncTemplate> funcTemplates = new ArrayList<>();
	protected List<EffectTemplate> effectTemplates = new ArrayList<>();
	protected List<Skill> skills = new ArrayList<>();
	
	private final List<Script> questEvents = new ArrayList<>();
	
	private static final List<Func> EMPY_FUNCTIOM_SET = Collections.emptyList();
	protected static final List<Effect> EMPY_EFFECT_SET = Collections.emptyList();
	
	/**
	 * Constructor of the L2Item that fill class variables.
	 * @param type : Enum designating the type of the item
	 * @param set  : StatsSet corresponding to a set of couples (key,value) for description of the item
	 */
	protected Item(Enum<?> type, StatsSet set)
	{
		this.type = type;
		itemId = set.getInteger("item_id");
		name = set.getString("name");
		type1 = set.getEnum("type1", ItemType1.class); // needed for item list (inventory)
		type2 = set.getEnum("type2", ItemType2.class); // different lists for armor, weapon, etc
		weight = set.getInteger("weight");
		crystallizable = set.getBool("crystallizable");
		stackable = set.getBool("stackable", false);
		crystalType = set.getEnum("crystal_type", CrystalType.class, CrystalType.CRYSTAL_NONE); // default to none-grade
		crystalCount = set.getInteger("crystal_count", 0);
		bodyPart = set.getEnum("bodypart", SlotType.class, SlotType.NONE);
		referencePrice = set.getInteger("price");
		sellable = set.getBool("sellable", true);
		dropable = set.getBool("dropable", true);
		destroyable = set.getBool("destroyable", true);
		tradeable = set.getBool("tradeable", true);
	}
	
	/**
	 * Returns the itemType.
	 * @return Enum
	 */
	public Enum<?> getType()
	{
		return type;
	}
	
	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public final int getId()
	{
		return itemId;
	}
	
	public abstract int getMask();
	
	/**
	 * Returns the type 2 of the item
	 * @return int
	 */
	public final ItemType2 getType2()
	{
		return type2;
	}
	
	/**
	 * Returns the weight of the item
	 * @return int
	 */
	public final int getWeight()
	{
		return weight;
	}
	
	/**
	 * Returns if the item is crystallizable
	 * @return boolean
	 */
	public final boolean isCrystallizable()
	{
		return crystallizable;
	}
	
	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final CrystalType getCrystalType()
	{
		return crystalType;
	}
	
	/**
	 * Return the type of crystal if item is crystallizable
	 * @return int
	 */
	public final int getCrystalItemId()
	{
		return crystalType.getCrystalId();
	}
	
	/**
	 * Returns the grade of the item.<BR>
	 * <U><I>Concept :</I></U><BR>
	 * In fact, this function returns the type of crystal of the item.
	 * @return int
	 */
	public final CrystalType getGrade()
	{
		return crystalType;
	}
	
	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return crystalCount;
	}
	
	/**
	 * Returns the quantity of crystals for crystallization on specific enchant level
	 * @param  enchantLevel
	 * @return              int
	 */
	public final int getCrystalCount(int enchantLevel)
	{
		if (enchantLevel > 3)
		{
			switch (type2)
			{
				case SHIELD_ARMOR:
				case ACCESSORY:
					return crystalCount + (CRYSTAL_ENCHANT_BONUS_ARMOR[getCrystalType().ordinal()] * ((3 * enchantLevel) - 6));
				case WEAPON:
					return crystalCount + (CRYSTAL_ENCHANT_BONUS_WEAPON[getCrystalType().ordinal()] * ((2 * enchantLevel) - 3));
				default:
					return crystalCount;
			}
		}
		else if (enchantLevel > 0)
		{
			switch (type2)
			{
				case SHIELD_ARMOR:
				case ACCESSORY:
					return crystalCount + (CRYSTAL_ENCHANT_BONUS_ARMOR[getCrystalType().ordinal()] * enchantLevel);
				case WEAPON:
					return crystalCount + (CRYSTAL_ENCHANT_BONUS_WEAPON[getCrystalType().ordinal()] * enchantLevel);
				default:
					return crystalCount;
			}
		}
		else
		{
			return crystalCount;
		}
	}
	
	/**
	 * Returns the name of the item
	 * @return String
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Return the part of the body used with the item.
	 * @return int
	 */
	public final SlotType getBodyPart()
	{
		return bodyPart;
	}
	
	/**
	 * Returns the type 1 of the item
	 * @return int
	 */
	public final ItemType1 getType1()
	{
		return type1;
	}
	
	/**
	 * Returns if the item is stackable
	 * @return boolean
	 */
	public final boolean isStackable()
	{
		return stackable;
	}
	
	/**
	 * Returns if the item is consumable
	 * @return boolean
	 */
	public boolean isConsumable()
	{
		return false;
	}
	
	/**
	 * Returns the price of reference of the item
	 * @return int
	 */
	public final int getReferencePrice()
	{
		return (isConsumable() ? (int) (referencePrice * Config.RATE_CONSUMABLE_COST) : referencePrice);
	}
	
	/**
	 * Returns if the item can be sold
	 * @return boolean
	 */
	public final boolean isSellable()
	{
		return sellable;
	}
	
	/**
	 * Returns if the item can be dropped
	 * @return boolean
	 */
	public final boolean isDropable()
	{
		return dropable;
	}
	
	/**
	 * Returns if the item can be destroyed
	 * @return boolean
	 */
	public final boolean isDestroyable()
	{
		return destroyable;
	}
	
	/**
	 * Returns if the item can be traded
	 * @return boolean
	 */
	public final boolean isTradeable()
	{
		return tradeable;
	}
	
	/**
	 * Returns if item is for hatchling
	 * @return boolean
	 */
	public boolean isForHatchling()
	{
		return (type2 == ItemType2.PET_HATCHLING);
	}
	
	/**
	 * Returns if item is for strider
	 * @return boolean
	 */
	public boolean isForStrider()
	{
		return (type2 == ItemType2.PET_STRIDER);
	}
	
	/**
	 * Returns if item is for wolf
	 * @return boolean
	 */
	public boolean isForWolf()
	{
		return (type2 == ItemType2.PET_WOLF);
	}
	
	/**
	 * Returns array of Func objects containing the list of functions used by the item
	 * @param  item   : L2ItemInstance pointing out the item
	 * @param  player : L2Character pointing out the player
	 * @return        List<Func>
	 */
	public List<Func> getStatFuncs(ItemInstance item, L2Character player)
	{
		if ((funcTemplates == null) || funcTemplates.isEmpty())
		{
			return EMPY_FUNCTIOM_SET;
		}
		List<Func> funcs = new ArrayList<>(funcTemplates.size());
		Env env = new Env();
		env.setPlayer(player);
		env.setTarget(player);
		env.setItem(item);
		
		for (FuncTemplate t : funcTemplates)
		{
			Func f = t.getFunc(env, item); // skill is owner
			if (f != null)
			{
				funcs.add(f);
			}
		}
		
		return funcs;
	}
	
	/**
	 * Add the FuncTemplate f to the list of functions used with the item
	 * @param f : FuncTemplate to add
	 */
	public void attach(FuncTemplate f)
	{
		funcTemplates.add(f);
	}
	
	/**
	 * Add the EffectTemplate effect to the list of effects generated by the item
	 * @param effect : EffectTemplate
	 */
	public void attach(EffectTemplate effect)
	{
		effectTemplates.add(effect);
	}
	
	/**
	 * Add the Skill skill to the list of skills generated by the item
	 * @param skill : Skill
	 */
	public void attach(Skill skill)
	{
		skills.add(skill);
	}
	
	/**
	 * Returns the name of the item
	 * @return String
	 */
	@Override
	public String toString()
	{
		return name;
	}
	
	public void addQuestEvent(Script q)
	{
		questEvents.add(q);
	}
	
	public List<Script> getQuestEvents()
	{
		return questEvents;
	}
}
