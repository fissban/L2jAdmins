package l2j.gameserver.model.items.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.instancemanager.MercTicketManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.drop.task.DropProtectionTask;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.Item;
import l2j.gameserver.model.items.ItemArmor;
import l2j.gameserver.model.items.ItemEtcItem;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.enums.ItemType1;
import l2j.gameserver.model.items.enums.ItemType2;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.instance.enums.ChangeType;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.world.L2WorldRegion;
import l2j.gameserver.network.external.server.DropItem;
import l2j.gameserver.network.external.server.GetItem;
import l2j.gameserver.network.external.server.SpawnItem;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.task.continuous.ItemsOnGroundTaskManager;

/**
 * This class manages items.
 * @version $Revision: 1.4.2.1.2.11 $ $Date: 2005/03/31 16:07:50 $
 */
public final class ItemInstance extends L2Object
{
	private static final Logger LOG = Logger.getLogger(ItemInstance.class.getName());
	
	private static final String UPDATE_ITEMS = "UPDATE character_items SET owner_id=?,count=?,loc=?,loc_data=?,freightLocation=?,enchant_level=?,price_sell=?,price_buy=?,custom_type1=?,custom_type2=? WHERE object_id = ?";
	private static final String INSERT_ITEMS = "INSERT INTO character_items (owner_id,item_id,count,loc,loc_data,freightLocation,enchant_level,price_sell,price_buy,object_id,custom_type1,custom_type2) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_ITEMS = "DELETE FROM character_items WHERE object_id=?";
	private static final String UPDATE_BUYLIST = "UPDATE merchant_buylists SET savetimer=? WHERE time=?";
	
	/** ID of the owner */
	private int ownerId;
	private int dropperObjectId = 0;
	/** Quantity of the item */
	private int count;
	/** Initial Quantity of the item */
	private int initCount;
	/** Time after restore Item count (in Hours) */
	private int time;
	/** Quantity of the item can decrease */
	private boolean decrease = false;
	/** ID of the item */
	private final int itemId;
	/** Object Item associated to the item */
	private final Item item;
	/** Location of the item : Inventory, PaperDoll, WareHouse */
	private ItemLocationType loc;
	/** Slot where item is stored */
	private ParpedollType slot;
	/** Closest town number */
	private int freightLocation;
	/** Level of enchantment of the item */
	private int enchantLevel;
	/** Price of the item for selling */
	private int priceSell;
	/** Price of the item for buying */
	private int priceBuy;
	/** Wear Item */
	private boolean wear;
	/** Custom item types (used loto, race tickets) */
	private int type1;
	private int type2;
	
	private long dropTime;
	
	private int shotsMask = 0;
	
	private boolean isProtected;
	
	private ChangeType lastChange = ChangeType.MODIFIED; // 1 ??, 2 modified, 3 removed
	private boolean existsInDb; // if a record exists in DB.
	private boolean storedInDb; // if DB data is up-to-date.
	
	private ScheduledFuture<?> itemLootSchedule = null;
	
	private final DropProtectionTask dropProtectionTask = new DropProtectionTask();
	
	private final ReentrantLock dbLock = new ReentrantLock();
	
	/**
	 * Constructor of the ItemInstance from the objectId and the itemId.
	 * @param objectId : int designating the ID of the object in the world
	 * @param itemId   : int designating the ID of the item
	 */
	public ItemInstance(int objectId, int itemId)
	{
		super(objectId);
		
		setInstanceType(InstanceType.L2ItemInstance);
		
		this.itemId = itemId;
		item = ItemData.getInstance().getTemplate(itemId);
		
		if ((itemId == 0) || (item == null))
		{
			throw new IllegalArgumentException();
		}
		count = 1;
		loc = ItemLocationType.VOID;
		type1 = 0;
		type2 = 0;
		dropTime = 0;
	}
	
	/**
	 * Constructor of the ItemInstance from the objetId and the description of the item given by the Item.
	 * @param objectId : int designating the ID of the object in the world
	 * @param item     : Item containing informations of the item
	 */
	public ItemInstance(int objectId, Item item)
	{
		super(objectId);
		
		setInstanceType(InstanceType.L2ItemInstance);
		itemId = item.getId();
		this.item = item;
		
		if ((itemId == 0) || (this.item == null))
		{
			throw new IllegalArgumentException();
		}
		count = 1;
		loc = ItemLocationType.VOID;
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param process   : String Identifier of process triggering this action
	 * @param owner_id  : int designating the ID of the owner
	 * @param creator   : L2PcInstance Player requesting the item creation
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void setOwnerId(String process, int owner_id, L2PcInstance creator, L2Object reference)
	{
		setOwnerId(owner_id);
		
		if (Config.LOG_ITEMS)
		{
			LogRecord record = new LogRecord(Level.INFO, "CHANGE:" + process);
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				this,
				creator,
				reference
			});
			LOG.log(record);
		}
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param ownerId : int designating the ID of the owner
	 */
	public void setOwnerId(int ownerId)
	{
		if (this.ownerId == ownerId)
		{
			return;
		}
		
		this.ownerId = ownerId;
		storedInDb = false;
	}
	
	/**
	 * Returns the ownerID of the item
	 * @return int
	 */
	public int getOwnerId()
	{
		return ownerId;
	}
	
	/**
	 * Sets the location of the item
	 * @param loc : ItemLocation (enumeration)
	 */
	public void setLocation(ItemLocationType loc)
	{
		setLocation(loc, ParpedollType.UNDER);
	}
	
	/**
	 * Sets the location of the item.<br>
	 * <u><i>Remark :</i></u> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc  : ItemLocation (enumeration)
	 * @param slot : ParpedollType (enumeration) designating the slot where the item is stored or the village for freights
	 */
	public void setLocation(ItemLocationType loc, ParpedollType slot)
	{
		if ((this.loc == loc) && (this.slot == slot))
		{
			return;
		}
		this.loc = loc;
		this.slot = slot;
		storedInDb = false;
	}
	
	public ItemLocationType getLocation()
	{
		return loc;
	}
	
	/**
	 * Remove a ItemInstance from the world and send server->client GetItem packets.<br>
	 * <B><u> Actions</u> :</B><br>
	 * <li>Send a Server->Client Packet GetItem to player that pick up and its knowPlayers member</li>
	 * <li>Remove the L2Object from the world</li> <FONT COLOR=#FF0000><B> <u>Caution</u> : This method DOESN'T REMOVE the object from allObjects of L2World </B></FONT><br>
	 * <B><u> Assert </u> :</B><br>
	 * <li>this instanceof ItemInstance</li>
	 * <li>_worldRegion != null <i>(L2Object is visible at the beginning)</i></li> <B><u> Example of use </u> :</B><br>
	 * <li>Do Pickup Item : PCInstance and Pet</li>
	 * @param player Player that pick up the item
	 */
	public final void pickupMe(L2Character player) // NOTE: Should move this function into ItemInstance because it does not apply to L2Character
	{
		assert getWorldRegion() != null;
		
		L2WorldRegion oldregion = getWorldRegion();
		
		// Create a server->client GetItem packet to pick up the ItemInstance
		player.broadcastPacket(new GetItem(this, player));
		
		synchronized (this)
		{
			setIsVisible(false);
			setWorldRegion(null);
		}
		
		// if this item is a mercenary ticket, remove the spawns!
		if (MercTicketManager.getInstance().getTicketCastleId(getId()) > 0)
		{
			MercTicketManager.getInstance().removeTicket(this);
			ItemsOnGroundTaskManager.getInstance().remove(this);
		}
		
		if ((getId() == Inventory.ADENA_ID) || (getId() == 6353))
		{
			ScriptState qs = ((L2PcInstance) player).getScriptState("Q255_Tutorial");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("CE" + getId() + "", null, (L2PcInstance) player);
			}
		}
		
		// this can synchronize on others instances, so it's out of
		// synchronized, to avoid deadlocks
		// Remove the ItemInstance from the world
		L2World.getInstance().removeVisibleObject(this, oldregion);
	}
	
	/**
	 * Returns the quantity of item
	 * @return int
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Sets the quantity of the item.<br>
	 * <u><i>Remark :</i></u> If loc and loc_data different from database, say datas not up-to-date
	 * @param process   : String Identifier of process triggering this action
	 * @param itemCount : int
	 * @param creator   : L2PcInstance Player requesting the item creation
	 * @param reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 */
	public void changeCount(String process, int itemCount, L2PcInstance creator, L2Object reference)
	{
		if (itemCount == 0)
		{
			return;
		}
		
		if ((itemCount > 0) && (count > (Integer.MAX_VALUE - itemCount)))
		{
			count = Integer.MAX_VALUE;
		}
		else
		{
			count += itemCount;
		}
		
		if (count < 0)
		{
			count = 0;
		}
		
		storedInDb = false;
		
		if (Config.LOG_ITEMS && (process != null))
		{
			LogRecord record = new LogRecord(Level.INFO, "CHANGE:" + process);
			record.setLoggerName("item");
			record.setParameters(new Object[]
			{
				this,
				creator,
				reference
			});
			LOG.log(record);
		}
	}
	
	/**
	 * Sets the quantity of the item.<br>
	 * <u><i>Remark :</i></u> If loc and loc_data different from database, say datas not up-to-date
	 * @param count : int
	 */
	public void setCount(int count)
	{
		if (this.count == count)
		{
			return;
		}
		
		this.count = count >= -1 ? count : 0;
		storedInDb = false;
	}
	
	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return !((item.getBodyPart() == SlotType.NONE) || (item instanceof ItemEtcItem));
	}
	
	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return (loc == ItemLocationType.PAPERDOLL) || (loc == ItemLocationType.PET_EQUIP);
	}
	
	/**
	 * Returns the slot where the item is stored
	 * @return int
	 */
	public ParpedollType getEquipSlot()
	{
		assert (loc == ItemLocationType.PAPERDOLL) || (loc == ItemLocationType.PET_EQUIP) || (loc == ItemLocationType.FREIGHT);
		
		return slot;
	}
	
	/**
	 * Returns the characteristics of the item
	 * @return Item
	 */
	public Item getItem()
	{
		return item;
	}
	
	public int getCustomType1()
	{
		return type1;
	}
	
	public int getCustomType2()
	{
		return type2;
	}
	
	public void setCustomType1(int newtype)
	{
		type1 = newtype;
	}
	
	public void setCustomType2(int newtype)
	{
		type2 = newtype;
	}
	
	public void setDropTime(long time)
	{
		dropTime = time;
	}
	
	public long getDropTime()
	{
		return dropTime;
	}
	
	public boolean isWear()
	{
		return wear;
	}
	
	public void setWear(boolean newwear)
	{
		wear = newwear;
	}
	
	/**
	 * Returns the type of item
	 * @return Enum
	 */
	public Enum<?> getType()
	{
		return item.getType();
	}
	
	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public int getId()
	{
		return itemId;
	}
	
	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return item.getCrystalCount(enchantLevel);
	}
	
	/**
	 * Returns the reference price of the item
	 * @return int
	 */
	public int getReferencePrice()
	{
		return item.getReferencePrice();
	}
	
	/**
	 * Returns the name of the item
	 * @return String
	 */
	public String getItemName()
	{
		return item.getName();
	}
	
	/**
	 * Returns the price of the item for selling
	 * @return int
	 */
	public int getPriceToSell()
	{
		return (isConsumable() ? (int) (priceSell * Config.RATE_CONSUMABLE_COST) : priceSell);
	}
	
	/**
	 * Sets the price of the item for selling<br>
	 * <u><i>Remark :</i></u><br>
	 * If loc and loc_data different from database, say datas not up-to-date
	 * @param price : int designating the price
	 */
	public void setPriceToSell(int price)
	{
		priceSell = price;
		storedInDb = false;
	}
	
	/**
	 * Returns the price of the item for buying
	 * @return int
	 */
	public int getPriceToBuy()
	{
		return (isConsumable() ? (int) (priceBuy * Config.RATE_CONSUMABLE_COST) : priceBuy);
	}
	
	/**
	 * Sets the price of the item for buying<br>
	 * <u><i>Remark :</i></u><br>
	 * If loc and loc_data different from database, say datas not up-to-date
	 * @param price : int
	 */
	public void setPriceToBuy(int price)
	{
		priceBuy = price;
		storedInDb = false;
	}
	
	/**
	 * Returns the last change of the item
	 * @return ChangeType
	 */
	public ChangeType getLastChange()
	{
		return lastChange;
	}
	
	/**
	 * Sets the last change of the item
	 * @param lastChange : ChangeType
	 */
	public void setLastChange(ChangeType lastChange)
	{
		this.lastChange = lastChange;
	}
	
	/**
	 * Returns if item is stackable
	 * @return boolean
	 */
	public boolean isStackable()
	{
		return item.isStackable();
	}
	
	/**
	 * Returns if item is dropable
	 * @return boolean
	 */
	public boolean isDropable()
	{
		return item.isDropable();
	}
	
	/**
	 * Returns if item is destroyable
	 * @return boolean
	 */
	public boolean isDestroyable()
	{
		return item.isDestroyable();
	}
	
	/**
	 * Returns if item is tradeable
	 * @return boolean
	 */
	public boolean isTradeable()
	{
		return item.isTradeable();
	}
	
	/**
	 * Returns if item is consumable
	 * @return boolean
	 */
	public boolean isConsumable()
	{
		return item.isConsumable();
	}
	
	/**
	 * Returns if item is available for manipulation
	 * @param  player
	 * @param  allowAdena
	 * @return            boolean
	 */
	public boolean isAvailable(L2PcInstance player, boolean allowAdena)
	{
		return ((!isEquipped()) // Not equipped
			&& (getItem().getType2() != ItemType2.QUEST)//
			&& ((getItem().getType2() != ItemType2.MONEY) || (getItem().getType1() != ItemType1.SHIELD_ARMOR)) // TODO: what does this mean?
			&& ((player.getPet() == null) || (getObjectId() != player.getPet().getControlItemId())) // Not Control item of currently summoned pet
			&& (player.getActiveEnchantItem() != this) // Not momentarily used enchant scroll
			&& (allowAdena || (itemId != Inventory.ADENA_ID)) //
			&& ((player.getCurrentSkill().getSkill() == null) || (player.getCurrentSkill().getSkill().getItemConsumeId() != itemId))//
			&& (isTradeable()));
	}
	
	/**
	 * Returns the level of enchantment of the item
	 * @return int
	 */
	public int getEnchantLevel()
	{
		return enchantLevel;
	}
	
	/**
	 * Sets the level of enchantment of the item
	 * @param enchantLevel
	 */
	public void setEnchantLevel(int enchantLevel)
	{
		if (this.enchantLevel == enchantLevel)
		{
			return;
		}
		this.enchantLevel = enchantLevel;
		storedInDb = false;
	}
	
	/**
	 * Returns the physical defense of the item
	 * @return int
	 */
	public int getPDef()
	{
		if (item instanceof ItemArmor)
		{
			return ((ItemArmor) item).getPDef();
		}
		return 0;
	}
	
	/**
	 * Returns false cause item can't be attacked
	 * @return boolean false
	 */
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return false;
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
		{
			shotsMask |= type.getMask();
		}
		else
		{
			shotsMask &= ~type.getMask();
		}
	}
	
	public void unChargeAllShots()
	{
		shotsMask = 0;
	}
	
	/**
	 * This function basically returns a set of functions from Item/L2Armor/L2Weapon, but may add additional functions, if this particular item instance is enhanced for a particular player.
	 * @param  player : L2Character designating the player
	 * @return        List<Func>
	 */
	public List<Func> getStatFuncs(L2Character player)
	{
		return getItem().getStatFuncs(this, player);
	}
	
	/**
	 * Updates the database.
	 */
	public void updateDatabase()
	{
		updateDatabase(false);
	}
	
	/**
	 * Updates the database.<br>
	 * @param force if the update should necessarily be done.
	 */
	public void updateDatabase(boolean force)
	{
		if (isWear())
		{
			return;
		}
		
		dbLock.lock();
		try
		{
			if (existsInDb)
			{
				if ((ownerId == 0) || (loc == ItemLocationType.VOID) || ((count == 0) && (loc != ItemLocationType.LEASE)))
				{
					removeFromDb();
				}
				else if (!Config.LAZY_ITEMS_UPDATE || force)
				{
					updateInDb();
				}
			}
			else
			{
				if ((count == 0) && (loc != ItemLocationType.LEASE))
				{
					return;
				}
				
				if ((loc == ItemLocationType.VOID) || (loc == ItemLocationType.NPC) || (ownerId == 0))
				{
					return;
				}
				
				insertIntoDb();
			}
		}
		finally
		{
			dbLock.unlock();
		}
	}
	
	/**
	 * Returns a ItemInstance stored in database from its objectID
	 * @param  rs
	 * @return    ItemInstance
	 */
	public static ItemInstance restoreFromDb(ResultSet rs)
	{
		ItemInstance inst = null;
		
		try
		{
			int objectId = rs.getInt("object_id");
			int itemId = rs.getInt("item_id");
			Item item = ItemData.getInstance().getTemplate(itemId);
			
			if (item == null)
			{
				LOG.severe("ItemInstance: Item item_id=" + itemId + " not known, object_id=" + objectId);
				return null;
			}
			
			inst = new ItemInstance(objectId, item);
			inst.ownerId = rs.getInt("owner_id");
			inst.count = rs.getInt("count");
			inst.enchantLevel = rs.getInt("enchant_level");
			inst.type1 = rs.getInt("custom_type1");
			inst.type2 = rs.getInt("custom_type2");
			inst.loc = ItemLocationType.valueOf(rs.getString("loc"));
			inst.slot = ParpedollType.values()[rs.getInt("loc_data")];
			inst.freightLocation = rs.getInt("freightLocation");
			inst.priceSell = rs.getInt("price_sell");
			inst.priceBuy = rs.getInt("price_buy");
			inst.existsInDb = true;
			inst.storedInDb = true;
		}
		catch (Exception e)
		{
			LOG.severe("Item not found");
			e.printStackTrace();
			return null;
		}
		
		return inst;
	}
	
	public void changeCountWithoutTrace(int count, L2PcInstance creator, L2Object reference)
	{
		changeCount(null, count, creator, reference);
	}
	
	/**
	 * Init a dropped ItemInstance and add it in the world as a visible object.<br>
	 * <B><u> Actions</u> :</B><br>
	 * <li>Set the x,y,z position of the ItemInstance dropped and update its worldregion</li>
	 * <li>Add the ItemInstance dropped to visibleObjects of its L2WorldRegion</li>
	 * <li>Add the ItemInstance dropped in the world as a <B>visible</B> object</li> <FONT COLOR=#FF0000><B> <u>Caution</u> : This method DOESN'T ADD the object to allObjects of L2World </B></FONT><br>
	 * <B><u> Assert </u> :</B><br>
	 * <li>_worldRegion == null <i>(L2Object is invisible at the beginning)</i></li> <B><u> Example of use </u> :</B><br>
	 * <li>Drop item</li>
	 * <li>Call Pet</li>
	 * @param dropper
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void dropMe(L2Character dropper, int x, int y, int z)
	{
		assert getWorldRegion() == null;
		
		if (dropper != null)
		{
			LocationHolder dropDest = GeoEngine.getInstance().canMoveToTargetLoc(dropper.getX(), dropper.getY(), dropper.getZ(), x, y, z);
			x = dropDest.getX();
			y = dropDest.getY();
			z = dropDest.getZ();
		}
		
		synchronized (this)
		{
			// Set the x,y,z position of the ItemInstance dropped and update its worldregion
			setIsVisible(true);
			setWorldPosition(x, y, z);
			setWorldRegion(L2World.getInstance().getRegion(getWorldPosition()));
			
			// Add the ItemInstance dropped to visibleObjects of its L2WorldRegion
			getWorldRegion().addVisibleObject(this);
			
			setDropperObjectId(dropper != null ? dropper.getObjectId() : 0); // Set the dropper Id for the knownlist packets in sendInfo
			
		}
		setDropTime(System.currentTimeMillis());
		
		// this can synchronize on others instances, so it's out of
		// synchronized, to avoid deadlocks
		// Add the ItemInstance dropped in the world as a visible object
		L2World.getInstance().addVisibleObject(this, getWorldRegion());
		ItemsOnGroundTaskManager.getInstance().add(this, ((dropper != null) && (dropper instanceof L2Playable)));
	}
	
	public void setDropperObjectId(int id)
	{
		dropperObjectId = id;
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		if (dropperObjectId != 0)
		{
			activeChar.sendPacket(new DropItem(this, dropperObjectId));
		}
		else
		{
			activeChar.sendPacket(new SpawnItem(this));
		}
	}
	
	/**
	 * Update the database with values of the item
	 */
	private void updateInDb()
	{
		assert existsInDb;
		
		if (wear)
		{
			return;
		}
		
		if (storedInDb)
		{
			return;
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_ITEMS))
		{
			ps.setInt(1, ownerId);
			ps.setInt(2, getCount());
			ps.setString(3, loc.name());
			ps.setInt(4, slot.ordinal());
			ps.setInt(5, freightLocation);
			ps.setInt(6, getEnchantLevel());
			ps.setInt(7, priceSell);
			ps.setInt(8, priceBuy);
			ps.setInt(9, getCustomType1());
			ps.setInt(10, getCustomType2());
			ps.setInt(11, getObjectId());
			ps.executeUpdate();
			existsInDb = true;
			storedInDb = true;
		}
		catch (Exception e)
		{
			LOG.severe("ItemInstance: Could not update item " + getObjectId() + " in DB: Reason: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert the item in database
	 */
	private void insertIntoDb()
	{
		if (wear)
		{
			return;
		}
		
		assert !existsInDb && (getObjectId() != 0);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_ITEMS))
		{
			ps.setInt(1, ownerId);
			ps.setInt(2, itemId);
			ps.setInt(3, getCount());
			ps.setString(4, loc.name());
			ps.setInt(5, slot.ordinal());
			ps.setInt(6, freightLocation);
			ps.setInt(7, getEnchantLevel());
			ps.setInt(8, priceSell);
			ps.setInt(9, priceBuy);
			ps.setInt(10, getObjectId());
			ps.setInt(11, type1);
			ps.setInt(12, type2);
			ps.executeUpdate();
			existsInDb = true;
			storedInDb = true;
		}
		catch (Exception e)
		{
			LOG.severe("ItemInstance: Could not insert item " + getObjectId() + " into DB: Reason: " + e);
		}
	}
	
	/**
	 * Delete item from database
	 */
	private void removeFromDb()
	{
		if (wear)
		{
			return;
		}
		
		assert existsInDb;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_ITEMS))
		{
			ps.setInt(1, getObjectId());
			ps.executeUpdate();
			existsInDb = false;
			storedInDb = false;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Could not delete item " + getObjectId() + " in DB: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the item in String format
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "" + item;
	}
	
	public void resetOwnerTimer()
	{
		if (itemLootSchedule != null)
		{
			itemLootSchedule.cancel(true);
		}
		itemLootSchedule = null;
	}
	
	public void setItemLootSchedule(ScheduledFuture<?> sf)
	{
		itemLootSchedule = sf;
	}
	
	public ScheduledFuture<?> getItemLootSchedule()
	{
		return itemLootSchedule;
	}
	
	public void setProtected(boolean isProtected)
	{
		this.isProtected = isProtected;
	}
	
	public boolean isProtected()
	{
		return isProtected;
	}
	
	public void setCountDecrease(boolean decrease)
	{
		this.decrease = decrease;
	}
	
	public boolean getCountDecrease()
	{
		return decrease;
	}
	
	public void setInitCount(int InitCount)
	{
		initCount = InitCount;
	}
	
	public int getInitCount()
	{
		return initCount;
	}
	
	public void setTime(int time)
	{
		if (time > 0)
		{
			this.time = time;
		}
		else
		{
			this.time = 0;
		}
	}
	
	public void setRestoreTime(long savedTimer)
	{
		long remainingTime = savedTimer - System.currentTimeMillis();
		if (remainingTime < 0)
		{
			remainingTime = 0;
		}
		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			// restore init count
			if (decrease)
			{
				count = initCount;
			}
			
			// data timer save
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_BUYLIST))
			{
				ps.setLong(1, System.currentTimeMillis() + ((long) getTime() * 3600000));
				ps.setInt(2, getTime());
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOG.severe("ItemInstance: Could not update Timer save in Buylist" + e);
				e.printStackTrace();
			}
			
		}, remainingTime, (long) getTime() * 3600000);
	}
	
	public int getTime()
	{
		return time;
	}
	
	public final DropProtectionTask getDropProtection()
	{
		return dropProtectionTask;
	}
	
	// FREIGT ----------------
	
	/**
	 * Sets the location of the item.<br>
	 * <u><i>Remark :</i></u> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc : ItemLocation (enumeration)
	 * @param map : the village for freights
	 */
	public void setFreigtLocation(ItemLocationType loc, int map)
	{
		this.loc = loc;
		freightLocation = map;
		
		storedInDb = false;
	}
	
	/**
	 * Returns the slot where the item is stored
	 * @return int
	 */
	public int getFreigtLocation()
	{
		assert (loc == ItemLocationType.PAPERDOLL) || (loc == ItemLocationType.PET_EQUIP) || (loc == ItemLocationType.FREIGHT);
		
		return freightLocation;
	}
}
