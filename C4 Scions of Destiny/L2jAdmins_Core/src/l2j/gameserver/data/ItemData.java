package l2j.gameserver.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import l2j.Config;
import l2j.DatabaseManager;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.engines.DocumentEngine;
import l2j.gameserver.data.engines.item.DocumentItemHolder;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemEtcItem;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.CrystalType;
import l2j.gameserver.model.items.enums.EtcItemType;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.enums.ItemType1;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.items.instance.enums.ChangeType;
import l2j.gameserver.model.world.L2World;
import l2j.util.UtilPrint;

/**
 * This class ...
 * @version $Revision: 1.9.2.6.2.9 $ $Date: 2005/04/02 15:57:34 $
 */
public class ItemData
{
	private static final Logger LOG = Logger.getLogger(ItemData.class.getName());
	private static final Logger LOG_ITEMS = Logger.getLogger("item");
	
	private static final Map<String, WeaponType> weaponTypes = new HashMap<>();
	private static final Map<String, ArmorType> armorTypes = new HashMap<>();
	
	private Item[] allTemplates;
	private final Map<Integer, ItemEtcItem> etcItems;
	private final Map<Integer, ItemArmor> armors;
	private final Map<Integer, ItemWeapon> weapons;
	
	private final boolean initialized = true;
	
	static
	{
		weaponTypes.put("blunt", WeaponType.BLUNT);
		weaponTypes.put("bow", WeaponType.BOW);
		weaponTypes.put("dagger", WeaponType.DAGGER);
		weaponTypes.put("dual", WeaponType.DUAL);
		weaponTypes.put("dualfist", WeaponType.DUALFIST);
		weaponTypes.put("etc", WeaponType.ETC);
		weaponTypes.put("fist", WeaponType.FIST);
		weaponTypes.put("none", WeaponType.NONE); // these are shields !
		weaponTypes.put("pole", WeaponType.POLE);
		weaponTypes.put("sword", WeaponType.SWORD);
		weaponTypes.put("bigsword", WeaponType.BIGSWORD); // Two-Handed Swords
		weaponTypes.put("pet", WeaponType.PET); // Pet Weapon
		weaponTypes.put("rod", WeaponType.ROD); // Fishing Rods
		
		weaponTypes.put("bigblunt", WeaponType.BIGBLUNT); // Two handed blunt
		
		armorTypes.put("none", ArmorType.NONE);
		armorTypes.put("light", ArmorType.LIGHT);
		armorTypes.put("heavy", ArmorType.HEAVY);
		armorTypes.put("magic", ArmorType.MAGIC);
		armorTypes.put("pet", ArmorType.PET);
	}
	
	private static ItemData INSTANCE;
	
	/** Table of SQL request in order to obtain items from tables [etcitem], [armor], [weapon] */
	private static final String[] SQL_ITEM_SELECTS =
	{
		"SELECT item_id, name, crystallizable, item_type, weight, consume_type, crystal_type, price, crystal_count, sellable, dropable, destroyable, tradeable FROM etcitem",
		"SELECT item_id, name, bodypart, crystallizable, armor_type, weight, crystal_type, avoid_modify, p_def, m_def, mp_bonus, price, crystal_count, sellable, dropable, destroyable, tradeable, item_skill_id, item_skill_lvl FROM armor",
		"SELECT item_id, name, bodypart, crystallizable, weight, soulshots, spiritshots, crystal_type, p_dam, rnd_dam, weaponType, critical, hit_modify, avoid_modify, shield_def, shield_def_rate, atk_speed, mp_consume,"
			+ " m_dam, price, crystal_count, sellable, dropable, destroyable, tradeable, item_skill_id, item_skill_lvl, onCast_skill_id, onCast_skill_lvl, onCast_skill_chance, onCrit_skill_id, onCrit_skill_lvl, onCrit_skill_chance FROM weapon"
	};
	
	/** Table of SQL request in order to obtain items from tables [custom_etcitem], [custom_armor], [custom_weapon] */
	private static final String[] SQL_CUSTOM_ITEM_SELECTS =
	{
		"SELECT item_id, name, crystallizable, item_type, weight, consume_type, crystal_type, price, crystal_count, sellable, dropable, destroyable, tradeable FROM custom_etcitem",
		"SELECT item_id, name, bodypart, crystallizable, armor_type, weight, crystal_type, avoid_modify, p_def, m_def, mp_bonus, price, crystal_count, sellable, dropable, destroyable, tradeable, item_skill_id, item_skill_lvl FROM custom_armor",
		"SELECT item_id, name, bodypart, crystallizable, weight, soulshots, spiritshots, crystal_type, p_dam, rnd_dam, weaponType, critical, hit_modify, avoid_modify, shield_def, shield_def_rate, atk_speed, mp_consume,"
			+ " m_dam, price, crystal_count, sellable, dropable, destroyable, tradeable, item_skill_id, item_skill_lvl, onCast_skill_id, onCast_skill_lvl, onCast_skill_chance, onCrit_skill_id, onCrit_skill_lvl, onCrit_skill_chance FROM custom_weapon"
	};
	
	/** List of etcItem */
	private static final Map<Integer, DocumentItemHolder> itemData = new HashMap<>();
	/** List of weapons */
	private static final Map<Integer, DocumentItemHolder> weaponData = new HashMap<>();
	/** List of armor */
	private static final Map<Integer, DocumentItemHolder> armorData = new HashMap<>();
	
	/**
	 * Returns INSTANCE of ItemTable
	 * @return ItemTable
	 */
	public static ItemData getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new ItemData();
		}
		return INSTANCE;
	}
	
	/**
	 * Returns a new object Item
	 * @return
	 */
	public DocumentItemHolder newItem()
	{
		return new DocumentItemHolder();
	}
	
	/**
	 * Constructor.
	 */
	public ItemData()
	{
		etcItems = new HashMap<>();
		armors = new HashMap<>();
		weapons = new HashMap<>();
		
		try (Connection con = DatabaseManager.getConnection())
		{
			for (String selectQuery : SQL_ITEM_SELECTS)
			{
				try (PreparedStatement ps = con.prepareStatement(selectQuery);
					ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						if (selectQuery.endsWith("etcitem"))
						{
							DocumentItemHolder newItem = readItem(rs);
							itemData.put(newItem.id, newItem);
						}
						else if (selectQuery.endsWith("armor"))
						{
							DocumentItemHolder newItem = readArmor(rs);
							armorData.put(newItem.id, newItem);
						}
						else if (selectQuery.endsWith("weapon"))
						{
							DocumentItemHolder newItem = readWeapon(rs);
							weaponData.put(newItem.id, newItem);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "data error on item: ", e);
		}
		
		if (Config.CUSTOM_ITEM_TABLES)
		{
			try (Connection con = DatabaseManager.getConnection())
			{
				for (String selectQuery : SQL_CUSTOM_ITEM_SELECTS)
				{
					try (PreparedStatement ps = con.prepareStatement(selectQuery);
						ResultSet rs = ps.executeQuery())
					{
						// Add item in correct Map
						while (rs.next())
						{
							if (selectQuery.endsWith("etcitem"))
							{
								DocumentItemHolder newItem = readItem(rs);
								
								if (itemData.containsKey(newItem.id))
								{
									itemData.remove(newItem.id);
								}
								
								itemData.put(newItem.id, newItem);
							}
							else if (selectQuery.endsWith("armor"))
							{
								DocumentItemHolder newItem = readArmor(rs);
								
								if (armorData.containsKey(newItem.id))
								{
									armorData.remove(newItem.id);
								}
								
								armorData.put(newItem.id, newItem);
							}
							else if (selectQuery.endsWith("weapon"))
							{
								DocumentItemHolder newItem = readWeapon(rs);
								
								if (weaponData.containsKey(newItem.id))
								{
									weaponData.remove(newItem.id);
								}
								
								weaponData.put(newItem.id, newItem);
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "data error on custom item: ", e);
			}
		}
		
		DocumentEngine.getInstance().loadItems(itemData).forEach(item -> etcItems.put(item.getId(), item));
		DocumentEngine.getInstance().loadArmors(armorData).forEach(item -> armors.put(item.getId(), item));
		DocumentEngine.getInstance().loadWeapons(weaponData).forEach(item -> weapons.put(item.getId(), item));
		
		UtilPrint.result("ItemTable", "Loaded armors", armors.size());
		UtilPrint.result("ItemTable", "Loaded items", etcItems.size());
		UtilPrint.result("ItemTable", "Loaded weapons", weapons.size());
		
		buildFastLookupTable();
	}
	
	/**
	 * Returns object Item from the record of the database
	 * @param  rset         : ResultSet designating a record of the [weapon] table of database
	 * @return              Item : object created from the database record
	 * @throws SQLException
	 */
	private DocumentItemHolder readWeapon(ResultSet rset) throws SQLException
	{
		DocumentItemHolder item = new DocumentItemHolder();
		item.set = new StatsSet();
		item.type = weaponTypes.get(rset.getString("weaponType"));
		item.id = rset.getInt("item_id");
		item.name = rset.getString("name");
		
		item.set.set("item_id", item.id);
		item.set.set("name", item.name);
		
		// lets see if this is a shield
		if (item.type == WeaponType.NONE)
		{
			item.set.set("type1", ItemType1.SHIELD_ARMOR);
			item.set.set("type2", ItemType2.SHIELD_ARMOR);
		}
		else
		{
			item.set.set("type1", ItemType1.WEAPON_RING_EARRING_NECKLACE);
			item.set.set("type2", ItemType2.WEAPON);
		}
		
		item.set.set("bodypart", SlotType.valueOfName(rset.getString("bodypart")));
		item.set.set("crystal_type", CrystalType.valueOfName(rset.getString("crystal_type")));
		item.set.set("crystallizable", Boolean.valueOf(rset.getString("crystallizable")).booleanValue());
		item.set.set("weight", rset.getInt("weight"));
		item.set.set("soulshots", rset.getInt("soulshots"));
		item.set.set("spiritshots", rset.getInt("spiritshots"));
		item.set.set("p_dam", rset.getInt("p_dam"));
		item.set.set("rnd_dam", rset.getInt("rnd_dam"));
		item.set.set("critical", rset.getInt("critical"));
		item.set.set("hit_modify", rset.getDouble("hit_modify"));
		item.set.set("avoid_modify", rset.getInt("avoid_modify"));
		item.set.set("shield_def", rset.getInt("shield_def"));
		item.set.set("shield_def_rate", rset.getInt("shield_def_rate"));
		item.set.set("atk_speed", rset.getInt("atk_speed"));
		item.set.set("mp_consume", rset.getInt("mp_consume"));
		item.set.set("m_dam", rset.getInt("m_dam"));
		
		item.set.set("price", rset.getInt("price"));
		item.set.set("crystal_count", rset.getInt("crystal_count"));
		item.set.set("sellable", Boolean.valueOf(rset.getString("sellable")));
		
		item.set.set("dropable", Boolean.valueOf(rset.getString("dropable")));
		item.set.set("destroyable", Boolean.valueOf(rset.getString("destroyable")));
		item.set.set("tradeable", Boolean.valueOf(rset.getString("tradeable")));
		item.set.set("item_skill_id", rset.getInt("item_skill_id"));
		item.set.set("item_skill_lvl", rset.getInt("item_skill_lvl"));
		
		item.set.set("onCast_skill_id", rset.getInt("onCast_skill_id"));
		item.set.set("onCast_skill_lvl", rset.getInt("onCast_skill_lvl"));
		item.set.set("onCast_skill_chance", rset.getInt("onCast_skill_chance"));
		item.set.set("onCrit_skill_id", rset.getInt("onCrit_skill_id"));
		item.set.set("onCrit_skill_lvl", rset.getInt("onCrit_skill_lvl"));
		item.set.set("onCrit_skill_chance", rset.getInt("onCrit_skill_chance"));
		
		if (item.type == WeaponType.PET)
		{
			item.set.set("type1", ItemType1.WEAPON_RING_EARRING_NECKLACE);
			if (item.set.getEnum("bodypart", SlotType.class, SlotType.NONE) == SlotType.WOLF)
			{
				item.set.set("type2", ItemType2.PET_WOLF);
			}
			else if (item.set.getEnum("bodypart", SlotType.class, SlotType.NONE) == SlotType.HATCHLING)
			{
				item.set.set("type2", ItemType2.PET_HATCHLING);
			}
			else
			{
				item.set.set("type2", ItemType2.PET_STRIDER);
			}
			
			item.set.set("bodypart", SlotType.R_HAND);
		}
		
		return item;
	}
	
	/**
	 * Returns object Item from the record of the database
	 * @param  rset         : ResultSet designating a record of the [armor] table of database
	 * @return              Item : object created from the database record
	 * @throws SQLException
	 */
	private DocumentItemHolder readArmor(ResultSet rset) throws SQLException
	{
		DocumentItemHolder item = new DocumentItemHolder();
		item.set = new StatsSet();
		item.type = armorTypes.get(rset.getString("armor_type"));
		item.id = rset.getInt("item_id");
		item.name = rset.getString("name");
		
		item.set.set("item_id", item.id);
		item.set.set("name", item.name);
		SlotType bodypart = SlotType.valueOfName(rset.getString("bodypart"));
		item.set.set("bodypart", bodypart);
		item.set.set("crystallizable", Boolean.valueOf(rset.getString("crystallizable")));
		item.set.set("crystal_count", rset.getInt("crystal_count"));
		item.set.set("sellable", Boolean.valueOf(rset.getString("sellable")));
		item.set.set("dropable", Boolean.valueOf(rset.getString("dropable")));
		item.set.set("destroyable", Boolean.valueOf(rset.getString("destroyable")));
		item.set.set("tradeable", Boolean.valueOf(rset.getString("tradeable")));
		
		if ((bodypart == SlotType.NECK) || (bodypart == SlotType.HAIR) || ((bodypart.getMask() & SlotType.L_EAR.getMask()) != 0) || ((bodypart.getMask() & SlotType.L_FINGER.getMask()) != 0))
		{
			item.set.set("type1", ItemType1.WEAPON_RING_EARRING_NECKLACE);
			item.set.set("type2", ItemType2.ACCESSORY);
		}
		else
		{
			item.set.set("type1", ItemType1.SHIELD_ARMOR);
			item.set.set("type2", ItemType2.SHIELD_ARMOR);
		}
		
		item.set.set("weight", rset.getInt("weight"));
		item.set.set("crystal_type", CrystalType.valueOfName(rset.getString("crystal_type")));
		item.set.set("avoid_modify", rset.getInt("avoid_modify"));
		
		item.set.set("p_def", rset.getInt("p_def"));
		item.set.set("m_def", rset.getInt("m_def"));
		item.set.set("mp_bonus", rset.getInt("mp_bonus"));
		item.set.set("price", rset.getInt("price"));
		item.set.set("item_skill_id", rset.getInt("item_skill_id"));
		item.set.set("item_skill_lvl", rset.getInt("item_skill_lvl"));
		
		if (item.type == ArmorType.PET)
		{
			if (bodypart == SlotType.NECK)
			{
				item.set.set("type1", ItemType1.WEAPON_RING_EARRING_NECKLACE);
				item.set.set("type2", ItemType2.ACCESSORY);
				item.set.set("bodypart", SlotType.NECK);
			}
			else
			{
				item.set.set("type1", ItemType1.SHIELD_ARMOR);
				switch (item.set.getEnum("bodypart", SlotType.class, SlotType.NONE))
				{
					case WOLF:
						item.set.set("type2", ItemType2.PET_WOLF);
						break;
					case HATCHLING:
						item.set.set("type2", ItemType2.PET_HATCHLING);
						break;
					default:
						item.set.set("type2", ItemType2.PET_STRIDER);
						break;
				}
				item.set.set("bodypart", SlotType.CHEST);
			}
		}
		
		return item;
	}
	
	/**
	 * Returns object Item from the record of the database
	 * @param  rset         : ResultSet designating a record of the [etcitem] table of database
	 * @return              Item : object created from the database record
	 * @throws SQLException
	 */
	private DocumentItemHolder readItem(ResultSet rset) throws SQLException
	{
		DocumentItemHolder item = new DocumentItemHolder();
		item.set = new StatsSet();
		item.id = rset.getInt("item_id");
		
		item.set.set("item_id", item.id);
		item.set.set("crystallizable", Boolean.valueOf(rset.getString("crystallizable")));
		item.set.set("type1", ItemType1.ITEM_QUESTITEM_ADENA);
		item.set.set("type2", ItemType2.OTHER);
		item.set.set("bodypart", SlotType.NONE);
		item.set.set("crystal_count", rset.getInt("crystal_count"));
		item.set.set("sellable", Boolean.valueOf(rset.getString("sellable")));
		item.set.set("dropable", Boolean.valueOf(rset.getString("dropable")));
		item.set.set("destroyable", Boolean.valueOf(rset.getString("destroyable")));
		item.set.set("tradeable", Boolean.valueOf(rset.getString("tradeable")));
		String itemType = rset.getString("item_type");
		if (itemType.equals("none"))
		{
			item.type = EtcItemType.OTHER; // only for default
		}
		else if (itemType.equals("castle_guard"))
		{
			item.type = EtcItemType.SCROLL; // dummy
		}
		else if (itemType.equals("pet_collar"))
		{
			item.type = EtcItemType.PET_COLLAR;
		}
		else if (itemType.equals("potion"))
		{
			item.type = EtcItemType.POTION;
		}
		else if (itemType.equals("recipe"))
		{
			item.type = EtcItemType.RECEIPE;
		}
		else if (itemType.equals("scroll"))
		{
			item.type = EtcItemType.SCROLL;
		}
		else if (itemType.equals("seed"))
		{
			item.type = EtcItemType.SEED;
		}
		else if (itemType.equals("shot"))
		{
			item.type = EtcItemType.SHOT;
		}
		else if (itemType.equals("spellbook"))
		{
			item.type = EtcItemType.SPELLBOOK; // Spellbook, Amulet, Blueprint
		}
		else if (itemType.equals("arrow"))
		{
			item.type = EtcItemType.ARROW;
			item.set.set("bodypart", SlotType.L_HAND);
		}
		else if (itemType.equals("quest"))
		{
			item.type = EtcItemType.QUEST;
			item.set.set("type2", ItemType2.QUEST);
		}
		else if (itemType.equals("lure"))
		{
			item.type = EtcItemType.OTHER;
			item.set.set("bodypart", SlotType.L_HAND);
		}
		else
		{
			LOG.fine("unknown etcitem type:" + itemType);
			item.type = EtcItemType.OTHER;
		}
		
		String consume = rset.getString("consume_type");
		if (consume.equals("asset"))
		{
			item.type = EtcItemType.MONEY;
			item.set.set("stackable", true);
			item.set.set("type2", ItemType2.MONEY);
		}
		else if (consume.equals("stackable"))
		{
			item.set.set("stackable", true);
		}
		else
		{
			item.set.set("stackable", false);
		}
		
		item.set.set("crystal_type", CrystalType.valueOfName(rset.getString("crystal_type")));
		
		item.set.set("weight", rset.getInt("weight"));
		item.name = rset.getString("name");
		item.set.set("name", item.name);
		
		item.set.set("price", rset.getInt("price"));
		
		return item;
	}
	
	/**
	 * Returns if ItemTable initialized
	 * @return boolean
	 */
	public boolean isInitialized()
	{
		return initialized;
	}
	
	/**
	 * Builds a variable in which all items are putting in in function of their ID.
	 */
	private void buildFastLookupTable()
	{
		int highestId = 0;
		
		// Get highest ID of item in armor Map, then in weapon tMap, and finally in etcitem Map
		for (int id : armors.keySet())
		{
			ItemArmor item = armors.get(id);
			if (item.getId() > highestId)
			{
				highestId = item.getId();
			}
		}
		for (int id : weapons.keySet())
		{
			ItemWeapon item = weapons.get(id);
			if (item.getId() > highestId)
			{
				highestId = item.getId();
			}
		}
		for (int id : etcItems.keySet())
		{
			ItemEtcItem item = etcItems.get(id);
			if (item.getId() > highestId)
			{
				highestId = item.getId();
			}
		}
		
		// Create a FastLookUp Table called allTemplates of size : value of the highest item ID
		allTemplates = new Item[highestId + 1];
		
		// Insert armor item in Fast Look Up Table
		for (int id : armors.keySet())
		{
			ItemArmor item = armors.get(id);
			assert allTemplates[id] == null;
			allTemplates[id] = item;
		}
		
		// Insert weapon item in Fast Look Up Table
		for (int id : weapons.keySet())
		{
			ItemWeapon item = weapons.get(id);
			assert allTemplates[id] == null;
			allTemplates[id] = item;
		}
		
		// Insert etcItem item in Fast Look Up Table
		for (int id : etcItems.keySet())
		{
			ItemEtcItem item = etcItems.get(id);
			assert allTemplates[id] == null;
			allTemplates[id] = item;
		}
	}
	
	/**
	 * Returns the item corresponding to the item ID
	 * @param  id : int designating the item
	 * @return    L2Item
	 */
	public Item getTemplate(int id)
	{
		if (id > allTemplates.length)
		{
			return null;
		}
		return allTemplates[id];
	}
	
	/**
	 * Create the L2ItemInstance corresponding to the Item Identifier and quantitiy add logs the activity.<br>
	 * <B><U> Actions</U> :</B><br>
	 * <li>Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity</li>
	 * <li>Add the L2ItemInstance object to allObjects of L2world</li>
	 * <li>Logs Item creation according to log settings</li>
	 * @param  process   : String Identifier of process triggering this action
	 * @param  itemId    : int Item Identifier of the item to be created
	 * @param  count     : int Quantity of items to be created for stackable items
	 * @param  actor     : L2PcInstance Player requesting the item creation
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           L2ItemInstance corresponding to the new item
	 */
	public ItemInstance createItem(String process, int itemId, int count, L2PcInstance actor, L2Object reference)
	{
		// Create and Init the L2ItemInstance corresponding to the Item Identifier
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		
		if (process.equalsIgnoreCase("loot"))
		{
			ScheduledFuture<?> itemLootShedule;
			if ((reference != null) && (reference instanceof L2Attackable) && ((L2Attackable) reference).isRaid() && !Config.AUTO_LOOT_RAIDS)
			{
				item.setOwnerId(actor.getObjectId());
				itemLootShedule = ThreadPoolManager.schedule(new resetOwner(item), 15000);
				item.setItemLootSchedule(itemLootShedule);
			}
			else if (!Config.AUTO_LOOT)
			{
				item.setOwnerId(actor.getObjectId());
				itemLootShedule = ThreadPoolManager.schedule(new resetOwner(item), 15000);
				item.setItemLootSchedule(itemLootShedule);
			}
		}
		
		if (Config.DEBUG)
		{
			LOG.fine("ItemTable: Item created  oid:" + item.getObjectId() + " itemid:" + itemId);
		}
		
		// Add the L2ItemInstance object to allObjects of L2world
		L2World.getInstance().addObject(item);
		
		// Set Item parameters
		if (item.isStackable() && (count > 1))
		{
			item.setCount(count);
		}
		
		if (Config.LOG_ITEMS)
		{
			LogRecord record = new LogRecord(Level.INFO, "CREATE:" + process);
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				item,
				actor,
				reference
			});
			LOG_ITEMS.log(record);
		}
		
		return item;
	}
	
	public ItemInstance createItem(String process, int itemId, int count, L2PcInstance actor)
	{
		return createItem(process, itemId, count, actor, null);
	}
	
	/**
	 * Returns a dummy (fr = factice) item.<br>
	 * <U><I>Concept :</I></U><br>
	 * Dummy item is created by setting the ID of the object in the world at null value
	 * @param  itemId : int designating the item
	 * @return        L2ItemInstance designating the dummy item created
	 */
	public ItemInstance createDummyItem(int itemId)
	{
		Item item = getTemplate(itemId);
		if (item == null)
		{
			return null;
		}
		ItemInstance temp = new ItemInstance(0, item);
		try
		{
			temp = new ItemInstance(0, itemId);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// this can happen if the item templates were not initialized
		}
		
		if (temp.getItem() == null)
		{
			LOG.warning("ItemTable: Item Template missing for Id: " + itemId);
		}
		
		return temp;
	}
	
	/**
	 * Destroys the L2ItemInstance.<br>
	 * <B><U> Actions</U> :</B><br>
	 * <li>Sets L2ItemInstance parameters to be unusable</li>
	 * <li>Removes the L2ItemInstance object to allObjects of L2world</li>
	 * <li>Logs Item deletion according to log settings</li>
	 * @param process   : String Identifier of process triggering this action
	 * @param item      : L2ItemInstance Item Identifier of the item to be created
	 * @param actor     : L2PcInstance Player requesting the item destroy
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void destroyItem(String process, ItemInstance item, L2PcInstance actor, L2Object reference)
	{
		synchronized (item)
		{
			item.setCount(0);
			item.setOwnerId(0);
			item.setLocation(ItemLocationType.VOID);
			item.setLastChange(ChangeType.REMOVED);
			
			L2World.getInstance().removeObject(item);
			IdFactory.getInstance().releaseId(item.getObjectId());
			
			if (Config.LOG_ITEMS)
			{
				LogRecord record = new LogRecord(Level.INFO, "DELETE:" + process);
				record.setLoggerName("item");
				record.setParameters(new Object[]
				{
					item,
					actor,
					reference
				});
				LOG_ITEMS.log(record);
			}
			
			// if it's a pet control item, delete the pet as well
			if (PetDataData.isPetItem(item.getId()))
			{
				try (Connection con = DatabaseManager.getConnection();
					PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
				{
					ps.setInt(1, item.getObjectId());
					ps.execute();
				}
				catch (Exception e)
				{
					LOG.log(Level.WARNING, "could not delete pet objectid:", e);
				}
			}
		}
	}
	
	public void reload()
	{
		synchronized (INSTANCE)
		{
			INSTANCE = null;
			INSTANCE = new ItemData();
		}
	}
	
	protected class resetOwner implements Runnable
	{
		ItemInstance item;
		
		public resetOwner(ItemInstance item)
		{
			this.item = item;
		}
		
		@Override
		public void run()
		{
			item.setOwnerId(0);
			item.setItemLootSchedule(null);
		}
	}
}
