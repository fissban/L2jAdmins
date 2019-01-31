package l2j.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.stat.PetStat;
import l2j.gameserver.model.actor.status.PetStatus;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.actor.templates.PetTemplate;
import l2j.gameserver.model.holder.TimeStampHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.itemcontainer.inventory.PetInventory;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.enums.ArmorType;
import l2j.gameserver.model.items.enums.ItemLocationType;
import l2j.gameserver.model.items.enums.SlotType;
import l2j.gameserver.model.items.enums.WeaponType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.PetInventoryUpdate;
import l2j.gameserver.network.external.server.PetItemList;
import l2j.gameserver.network.external.server.StopMove;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.DecayTaskManager;
import l2j.gameserver.task.continuous.ItemsOnGroundTaskManager;
import l2j.util.Rnd;

/**
 * This class ...
 * @version $Revision: 1.15.2.10.2.16 $ $Date: 2005/04/06 16:13:40 $
 */
public class L2PetInstance extends L2Summon
{
	private int curFed;
	private final PetInventory inventory;
	private final int controlItemId;
	private boolean respawned;
	private final boolean mountable;
	private int maxload;
	
	private Future<?> feedTask;
	
	private int weapon;
	private int armor;
	private int jewel;
	
	private PetTemplate data;
	
	/** The Experience before the last Death Penalty */
	private long expBeforeDeath = 0;
	private int curWeightPenalty = 0;
	
	private volatile Map<Integer, TimeStampHolder> reuseTimeStamps = new ConcurrentHashMap<>();
	
	/**
	 * Index according to skill id the current timestamp of use.
	 * @param skill
	 * @param reuse delay
	 */
	@Override
	public void addTimeStamp(Skill skill, long reuse)
	{
		reuseTimeStamps.put(skill.getReuseHashCode(), new TimeStampHolder(skill, reuse));
	}
	
	public final PetTemplate getPetData()
	{
		if (data == null)
		{
			data = PetDataData.getInstance().getPetData(getTemplate().getId(), getStat().getLevel());
		}
		
		return data;
	}
	
	public final void setPetData(PetTemplate value)
	{
		data = value;
	}
	
	@Override
	public float getExpPenalty()
	{
		return getPetData().getOwnerExpTaken();
	}
	
	/**
	 * Manage Feeding Task. <br>
	 * <U> Actions</U> :</B><BR>
	 * <li>Feed or kill the pet depending on hunger level</li>
	 * <li>If pet has food in inventory and feed level drops below 55% then consume food from inventory</li>
	 * <li>Send a broadcastStatusUpdate packet for this L2PetInstance</li>
	 */
	class FeedTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if ((getOwner() == null) || (getOwner().getPet() == null) || (getOwner().getPet().getObjectId() != getObjectId()))
				{
					stopFeed();
					return;
				}
				else if (getCurrentFed() > getFeedConsume())
				{
					setCurrentFed(getCurrentFed() - getFeedConsume());
				}
				else
				{
					setCurrentFed(0);
				}
				
				final int[] foodIds = PetDataData.getFoodItemId(getTemplate().getId());
				if (foodIds[0] == 0)
				{
					return;
				}
				
				ItemInstance food = getInventory().getItemById(foodIds[0]);
				
				// use better strider food if exists
				if (PetDataData.isStrider(getId()))
				{
					if (getInventory().getItemById(foodIds[1]) != null)
					{
						food = getInventory().getItemById(foodIds[1]);
					}
				}
				if (isRunning() && isHungry())
				{
					setWalking();
				}
				else if (!isHungry() && !isRunning())
				{
					setRunning();
				}
				
				if ((food != null) && isHungry())
				{
					final IItemHandler handler = ItemHandler.getHandler(food.getId());
					if (handler != null)
					{
						getOwner().sendPacket(new SystemMessage(SystemMessage.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(food.getId()));
						handler.useItem(L2PetInstance.this, food);
					}
				}
				else
				{
					if (getCurrentFed() == 0)
					{
						getOwner().sendPacket(SystemMessage.YOUR_PET_IS_VERY_HUNGRY);
						if (Rnd.get(100) < 30)
						{
							stopFeed();
							getOwner().sendPacket(SystemMessage.STARVING_GRUMPY_AND_FED_UP_YOUR_PET_HAS_LEFT);
							
							if (Config.DEBUG)
							{
								LOG.info("Hungry pet deleted for player :" + getOwner().getName() + " Control Item Id :" + getControlItemId());
							}
							
							deleteMe();
						}
					}
					broadcastStatusUpdate();
				}
			}
			catch (final Throwable e)
			{
				if (Config.DEBUG)
				{
					LOG.warning("Pet [#" + getObjectId() + "] a feed task error has occurred: " + e);
				}
			}
		}
	}
	
	public int getFeedConsume()
	{
		if (isAttackingNow())
		{
			return getPetData().getPetFedBattle();
		}
		return getPetData().getPetFedNormal();
	}
	
	public static L2PetInstance spawnPet(NpcTemplate template, L2PcInstance owner, ItemInstance control)
	{
		if (L2World.getInstance().getPet(owner.getObjectId()) != null)
		{
			return null;
		}
		
		final L2PetInstance pet = restore(control, template, owner);
		
		// add the pet instance to world
		if (pet != null)
		{
			pet.setTitle(owner.getName());
			L2World.getInstance().addPet(owner.getObjectId(), pet);
		}
		
		return pet;
	}
	
	public L2PetInstance(int objectId, NpcTemplate template, L2PcInstance owner, ItemInstance control)
	{
		super(objectId, template, owner);
		
		setInstanceType(InstanceType.L2PetInstance);
		
		controlItemId = control.getObjectId();
		
		// Pet's initial level is supposed to be read from DB
		// Pets start at :
		// Wolf : Level 15
		// Hatching : Level 35
		// Sin-eaters are defaulted at the owner's level
		// Tested and confirmed on official servers
		if (template.getId() == 12564)
		{
			// init exp
			getStat().setExp(0);
			
			// calculate exp for give
			var tXp = ExperienceData.getInstance().getExpForLevel((byte) getOwner().getLevel());
			
			// give exp for owner level
			getStat().addExpAndSp(tXp, 0);
			
			// set level
			getStat().setLevel((byte) getOwner().getLevel());
		}
		else
		{
			getStat().setLevel(template.getLevel());
		}
		
		inventory = new PetInventory(this);
		inventory.restore();
		
		final int npcId = template.getId();
		mountable = PetDataData.isMountable(npcId);
		maxload = getPetData().getPetMaxLoad();
	}
	
	@Override
	public void initStatus()
	{
		setStatus(new PetStatus(this));
	}
	
	@Override
	public void initStat()
	{
		setStat(new PetStat(this));
	}
	
	@Override
	public PetStat getStat()
	{
		return (PetStat) super.getStat();
	}
	
	public boolean isRespawned()
	{
		return respawned;
	}
	
	@Override
	public int getSummonType()
	{
		return 2;
	}
	
	@Override
	public int getControlItemId()
	{
		return controlItemId;
	}
	
	public ItemInstance getControlItem()
	{
		return getOwner().getInventory().getItemByObjectId(controlItemId);
	}
	
	@Override
	public int getCurrentFed()
	{
		return curFed;
	}
	
	public void setCurrentFed(int num)
	{
		curFed = num > getMaxFeed() ? getMaxFeed() : num;
	}
	
	/**
	 * Returns the pet's currently equipped weapon instance (if any).
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		for (final ItemInstance item : getInventory().getItems())
		{
			if ((item.getLocation() == ItemLocationType.PET_EQUIP) && (item.getItem().getBodyPart() == SlotType.R_HAND))
			{
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the pet's currently equipped weapon (if any).
	 */
	@Override
	public ItemWeapon getActiveWeaponItem()
	{
		final ItemInstance weapon = getActiveWeaponInstance();
		
		if (weapon == null)
		{
			return null;
		}
		
		return (ItemWeapon) weapon.getItem();
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		// temporary? unavailable
		return null;
	}
	
	@Override
	public ItemWeapon getSecondaryWeaponItem()
	{
		// temporary? unavailable
		return null;
	}
	
	@Override
	public PetInventory getInventory()
	{
		return inventory;
	}
	
	@Override
	public void doPickupItem(L2Object object)
	{
		final boolean follow = getFollowStatus();
		
		if (isDead())
		{
			return;
		}
		
		getAI().setIntention(CtrlIntentionType.IDLE);
		
		broadcastPacket(new StopMove(getObjectId(), getX(), getY(), getZ(), getHeading()));
		
		if (!(object instanceof ItemInstance))
		{
			// dont try to pickup anything that is not an item :)
			LOG.warning("trying to pickup wrong target." + object);
			getOwner().sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final ItemInstance target = (ItemInstance) object;
		
		synchronized (target)
		{
			if (!target.isVisible())
			{
				getOwner().sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!target.getDropProtection().tryPickUp(getOwner()))
			{
				getOwner().sendPacket(ActionFailed.STATIC_PACKET);
				getOwner().sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1).addItemName(target.getId()));
				return;
			}
			
			if (!inventory.validateCapacity(target))
			{
				getOwner().sendPacket(new SystemMessage(SystemMessage.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS));
				return;
			}
			
			if (!inventory.validateWeight(target, target.getCount()))
			{
				getOwner().sendMessage("Your pet is overweight and cannot carry any more items.");
				return;
			}
			
			if ((target.getOwnerId() != 0) && (target.getOwnerId() != getOwner().getObjectId()) && !getOwner().isInLooterParty(target.getOwnerId()))
			{
				if (target.getId() == Inventory.ADENA_ID)
				{
					getOwner().sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1_ADENA).addNumber(target.getCount()));
				}
				else if (target.getCount() > 1)
				{
					getOwner().sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S2_S1_S).addItemName(target.getId()).addNumber(target.getCount()));
				}
				else
				{
					getOwner().sendPacket(new SystemMessage(SystemMessage.FAILED_TO_PICKUP_S1).addItemName(target.getId()));
				}
				
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if ((target.getItemLootSchedule() != null) && ((target.getOwnerId() == getOwner().getObjectId()) || getOwner().isInLooterParty(target.getOwnerId())))
			{
				target.resetOwnerTimer();
			}
			
			target.pickupMe(this);
			ItemsOnGroundTaskManager.getInstance().remove(target);
		}
		
		// if item is instance of L2ArmorType or L2WeaponType broadcast an "Attention" system message
		if ((target.getType() instanceof ArmorType) || (target.getType() instanceof WeaponType))
		{
			if (target.getEnchantLevel() > 0)
			{
				broadcastPacket(new SystemMessage(SystemMessage.ANNOUNCEMENT_C1_PET_PICKED_UP_S2_S3).addString(getOwner().getName()).addNumber(target.getEnchantLevel()).addItemName(target.getId()));
			}
			else
			{
				broadcastPacket(new SystemMessage(SystemMessage.ANNOUNCEMENT_C1_PET_PICKED_UP_S2).addString(getOwner().getName()).addItemName(target.getId()));
			}
		}
		
		getInventory().addItem("Pickup", target, getOwner(), this);
		getOwner().sendPacket(new PetItemList(this));
		
		getAI().setIntention(CtrlIntentionType.IDLE);
		
		if (follow)
		{
			followOwner();
		}
	}
	
	@Override
	public void deleteMe()
	{
		super.deleteMe();
		
		destroyControlItem(getOwner()); // this should also delete the pet from the db
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		stopFeed();
		getOwner().sendPacket(SystemMessage.MAKE_SURE_YOU_RESSURECT_YOUR_PET_WITHIN_20_MINUTES);
		DecayTaskManager.getInstance().addDecayTask(this, 1200000);
		deathPenalty();
		return true;
	}
	
	@Override
	public void doRevive()
	{
		getOwner().getRequestRevive().removeReviving();
		
		super.doRevive();
		
		// stopDecay
		DecayTaskManager.getInstance().cancelDecayTask(this);
		startFeed();
		if (!isHungry())
		{
			setRunning();
		}
		getAI().setIntention(CtrlIntentionType.ACTIVE, null);
	}
	
	@Override
	public void doRevive(double revivePower)
	{
		// Restore the pet's lost experience,
		// depending on the % return of the skill used (based on its power).
		restoreExp(revivePower);
		doRevive();
	}
	
	/**
	 * Transfers item to another inventory
	 * @param  process   : String Identifier of process triggering this action
	 * @param  objectId
	 * @param  count     : int Quantity of items to be transfered
	 * @param  target
	 * @param  actor     : L2PcInstance Player requesting the item transfer
	 * @param  reference : L2Object Object referencing current action like NPC selling item or previous item in transformation
	 * @return           L2ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance transferItem(String process, int objectId, int count, Inventory target, L2PcInstance actor, L2Object reference)
	{
		final ItemInstance oldItem = getInventory().getItemByObjectId(objectId);
		final ItemInstance newItem = getInventory().transferItem(process, objectId, count, target, actor, reference);
		
		if (newItem == null)
		{
			return null;
		}
		
		// Send inventory update packet
		PetInventoryUpdate petIU = new PetInventoryUpdate();
		if ((oldItem.getCount() > 0) && (oldItem != newItem))
		{
			petIU.addModifiedItem(oldItem);
		}
		else
		{
			petIU.addRemovedItem(oldItem);
		}
		getOwner().sendPacket(petIU);
		
		// Send target update packet
		if (target instanceof PcInventory)
		{
			final L2PcInstance targetPlayer = ((PcInventory) target).getOwner();
			targetPlayer.sendPacket(new InventoryUpdate(newItem));
			
			// Update current load as well
			targetPlayer.updateCurLoad();
		}
		else if (target instanceof PetInventory)
		{
			petIU = new PetInventoryUpdate();
			if (newItem.getCount() > count)
			{
				petIU.addRemovedItem(newItem);
			}
			else
			{
				petIU.addNewItem(newItem);
			}
			((PetInventory) target).getOwner().getOwner().sendPacket(petIU);
		}
		
		getInventory().refreshWeight();
		return newItem;
	}
	
	@Override
	public void giveAllToOwner()
	{
		try
		{
			final Inventory petInventory = getInventory();
			
			for (final ItemInstance giveit : petInventory.getItems())
			{
				if (((giveit.getItem().getWeight() * giveit.getCount()) + getOwner().getInventory().getTotalWeight()) < getOwner().getMaxLoad())
				{
					// If the owner can carry it give it to him/her
					giveItemToOwner(giveit);
				}
				else
				{
					// If he/she can't carry it, drop it on the floor :)
					dropItemHere(giveit);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Give all items error " + e);
		}
	}
	
	public void giveItemToOwner(ItemInstance item)
	{
		try
		{
			getInventory().transferItem("PetTransfer", item.getObjectId(), item.getCount(), getOwner().getInventory(), getOwner(), this);
			final PetInventoryUpdate petiu = new PetInventoryUpdate();
			petiu.addRemovedItem(item);
			getOwner().sendPacket(petiu);
			getOwner().sendPacket(new ItemList(getOwner(), false));
		}
		catch (final Exception e)
		{
			LOG.warning("Error while giving item to owner: " + e);
		}
	}
	
	/**
	 * Remove the Pet from DB and its associated item from the player inventory
	 * @param owner The owner from whose inventory we should delete the item
	 */
	public void destroyControlItem(L2PcInstance owner)
	{
		// remove the pet instance from world
		L2World.getInstance().removePet(owner.getObjectId());
		
		// delete from inventory
		try
		{
			final ItemInstance removedItem = owner.getInventory().destroyItem("PetDestroy", getControlItemId(), 1, getOwner(), this);
			if (removedItem != null)
			{
				owner.sendPacket(new SystemMessage(SystemMessage.S1_DISAPPEARED).addItemName(removedItem.getId()));
				
				owner.sendPacket(new InventoryUpdate(removedItem));
				
				// Update current load status on player
				owner.updateCurLoad();
				
				owner.broadcastUserInfo();
				
				L2World.getInstance().removeObject(removedItem);
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Error while destroying control item: " + e);
		}
		
		// pet control item no longer exists, delete the pet from the db
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
		{
			ps.setInt(1, getControlItemId());
			ps.execute();
		}
		catch (final Exception e)
		{
			LOG.warning("could not delete pet:" + e);
		}
	}
	
	public void dropAllItems()
	{
		try
		{
			for (final ItemInstance item : getInventory().getItems())
			{
				dropItemHere(item);
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Pet Drop Error: " + e);
		}
	}
	
	public void dropItemHere(ItemInstance dropit, boolean protect)
	{
		dropit = getInventory().dropItem("Drop", dropit.getObjectId(), dropit.getCount(), getOwner(), this);
		if (dropit != null)
		{
			if (protect)
			{
				dropit.getDropProtection().protect(getOwner());
			}
			LOG.finer("Item id to drop: " + dropit.getId() + " amount: " + dropit.getCount());
			dropit.dropMe(this, getX(), getY(), getZ() + 100);
		}
	}
	
	public void dropItemHere(ItemInstance dropit)
	{
		dropItemHere(dropit, false);
	}
	
	/**
	 * @return the mountable.
	 */
	@Override
	public boolean isMountable()
	{
		return mountable;
	}
	
	private static L2PetInstance restore(ItemInstance control, NpcTemplate template, L2PcInstance owner)
	{
		L2PetInstance pet;
		
		if (template.isType("L2BabyPet"))
		{
			pet = new L2BabyPetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
		}
		else
		{
			pet = new L2PetInstance(IdFactory.getInstance().getNextId(), template, owner, control);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT item_obj_id, name, level, curHp, curMp, exp, sp, fed, weapon, armor, jewel FROM pets WHERE item_obj_id=?"))
		{
			ps.setInt(1, control.getObjectId());
			try (ResultSet rset = ps.executeQuery())
			{
				if (!rset.next())
				{
					return pet;
				}
				
				pet.respawned = true;
				pet.setName(rset.getString("name"));
				
				pet.getStat().setLevel(rset.getInt("level"));
				pet.getStat().setExp(rset.getInt("exp"));
				pet.getStat().setSp(rset.getInt("sp"));
				
				pet.getStatus().setCurrentHp(rset.getDouble("curHp"));
				pet.getStatus().setCurrentMp(rset.getDouble("curMp"));
				
				pet.getStatus().setCurrentCp(pet.getStat().getMaxCp());
				
				pet.setWeapon(rset.getInt("weapon"));
				pet.setArmor(rset.getInt("armor"));
				pet.setJewel(rset.getInt("jewel"));
				
				if (rset.getDouble("curHp") < 0.5)
				{
					pet.setIsDead(true);
					pet.stopHpMpRegeneration();
				}
				
				pet.setCurrentFed(rset.getInt("fed"));
				
				return pet;
			}
		}
		catch (final Exception e)
		{
			LOG.warning("could not restore pet data: " + e);
			return null;
		}
	}
	
	@Override
	public void store()
	{
		if (getControlItemId() == 0)
		{
			// this is a summon, not a pet, don't store anything
			return;
		}
		
		String SQL;
		if (!isRespawned())
		{
			SQL = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,weapon,armor,jewel,item_obj_id) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
			// Pet's initial data
			getStat().setExp(getPetData().getPetMaxExp());
			getStatus().setCurrentHp(getPetData().getPetMaxHP());
			getStatus().setCurrentMp(getPetData().getPetMaxMP());
		}
		else
		{
			SQL = "UPDATE pets SET name=?,level=?,curHp=?,curMp=?,exp=?,sp=?,fed=?,weapon=?,armor=?,jewel=? " + "WHERE item_obj_id = ?";
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(SQL))
		{
			ps.setString(1, getName());
			ps.setInt(2, getStat().getLevel());
			ps.setDouble(3, getStatus().getCurrentHp());
			ps.setDouble(4, getStatus().getCurrentMp());
			ps.setLong(5, getStat().getExp());
			ps.setInt(6, getStat().getSp());
			ps.setInt(7, getCurrentFed());
			ps.setInt(8, getWeapon());
			ps.setInt(9, getArmor());
			ps.setInt(10, getJewel());
			ps.setInt(11, getControlItemId());
			ps.executeUpdate();
			respawned = true;
		}
		catch (final Exception e)
		{
			LOG.warning("could not store pet data: " + e);
		}
		
		final ItemInstance itemInst = getControlItem();
		if ((itemInst != null) && (itemInst.getEnchantLevel() != getStat().getLevel()))
		{
			itemInst.setEnchantLevel(getStat().getLevel());
			itemInst.updateDatabase();
		}
	}
	
	public synchronized void stopFeed()
	{
		if (feedTask != null)
		{
			feedTask.cancel(false);
			feedTask = null;
		}
	}
	
	public synchronized void startFeed()
	{
		// stop feeding task if its active
		stopFeed();
		
		if (!isDead() && (getOwner().getPet() == this))
		{
			feedTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FeedTask(), 10000, 10000);
		}
	}
	
	@Override
	public synchronized void unSummon()
	{
		stopFeed();
		
		// Then drop inventory.
		if (!isDead())
		{
			if (getInventory() != null)
			{
				getInventory().deleteMe();
			}
		}
		
		if (!isDead())
		{
			L2World.getInstance().removePet(getOwner().getObjectId());
		}
		
		super.unSummon();
	}
	
	/**
	 * Restore the specified % of experience this L2PetInstance has lost.
	 * @param restorePercent
	 */
	public void restoreExp(double restorePercent)
	{
		if (expBeforeDeath > 0)
		{
			// Restore the specified % of lost experience.
			getStat().addExp(Math.round(((expBeforeDeath - getStat().getExp()) * restorePercent) / 100));
			expBeforeDeath = 0;
		}
	}
	
	private void deathPenalty()
	{
		// TODO Need Correct Penalty
		
		final int lvl = getStat().getLevel();
		final double percentLost = (-0.07 * lvl) + 6.5;
		
		// Calculate the Experience loss
		final long lostExp = Math.round(((getStat().getExpForLevel(lvl + 1) - getStat().getExpForLevel(lvl)) * percentLost) / 100);
		
		// Get the Experience before applying penalty
		expBeforeDeath = getStat().getExp();
		
		// Set the new Experience value of the L2PetInstance
		getStat().addExp(-lostExp);
	}
	
	@Override
	public void addExpAndSp(long addToExp, int addToSp)
	{
		if (getId() == 12564)
		{
			getStat().addExpAndSp(Math.round(addToExp * Config.SINEATER_XP_RATE), addToSp);
		}
		else
		{
			getStat().addExpAndSp(Math.round(addToExp * Config.PET_XP_RATE), addToSp);
		}
	}
	
	@Override
	public int getMaxFeed()
	{
		return getStat().getMaxFeed();
	}
	
	@Override
	public final int getSkillLevel(int skillId)
	{
		if ((getSkills().isEmpty()) || (getSkills().get(skillId) == null))
		{
			return -1;
		}
		
		final int lvl = getStat().getLevel();
		return lvl > 70 ? 7 + ((lvl - 70) / 5) : lvl / 10;
	}
	
	public void updateRefOwner(L2PcInstance owner)
	{
		final int oldOwnerId = getOwner().getObjectId();
		
		setOwner(owner);
		// L2World.getInstance().removePet(oldOwnerId);
		L2World.getInstance().addPet(oldOwnerId, this);
	}
	
	public int getCurrentLoad()
	{
		return inventory.getTotalWeight();
	}
	
	public final void setMaxLoad(int maxLoad)
	{
		maxload = maxLoad;
	}
	
	public final int getMaxLoad()
	{
		return maxload;
	}
	
	public int getInventoryLimit()
	{
		return Config.INVENTORY_MAXIMUM_PET;
	}
	
	public void refreshOverloaded()
	{
		final int maxLoad = getMaxLoad();
		if (maxLoad > 0)
		{
			final long weightproc = (long) (((getCurrentLoad() - calcStat(StatsType.MAX_LOAD, 1, this, null)) * 1000) / maxLoad);
			int newWeightPenalty;
			if ((weightproc < 500) || getOwner().getDietMode())
			{
				newWeightPenalty = 0;
			}
			else if (weightproc < 666)
			{
				newWeightPenalty = 1;
			}
			else if (weightproc < 800)
			{
				newWeightPenalty = 2;
			}
			else if (weightproc < 1000)
			{
				newWeightPenalty = 3;
			}
			else
			{
				newWeightPenalty = 4;
			}
			
			if (curWeightPenalty != newWeightPenalty)
			{
				curWeightPenalty = newWeightPenalty;
				if (newWeightPenalty > 0)
				{
					addSkill(SkillData.getInstance().getSkill(4270, newWeightPenalty));
					setIsOverloaded(getCurrentLoad() >= maxLoad);
				}
				else
				{
					super.removeSkill(getSkill(4270));
					setIsOverloaded(false);
				}
			}
		}
	}
	
	@Override
	public void updateAndBroadcastStatus(int val)
	{
		refreshOverloaded();
		super.updateAndBroadcastStatus(val);
	}
	
	@Override
	public final boolean isHungry()
	{
		return getCurrentFed() < (0.55 * getPetData().getPetMaxFed());
	}
	
	@Override
	public int getPetSpeed()
	{
		return getPetData().getPetSpeed();
	}
	
	public final void setWeapon(int id)
	{
		weapon = id;
	}
	
	public final void setArmor(int id)
	{
		armor = id;
	}
	
	public final void setJewel(int id)
	{
		jewel = id;
	}
	
	@Override
	public final int getWeapon()
	{
		return weapon;
	}
	
	@Override
	public final int getArmor()
	{
		return armor;
	}
	
	public final int getJewel()
	{
		return jewel;
	}
	
	@Override
	public final void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss)
		{
			getOwner().sendPacket(SystemMessage.MISSED_TARGET);
			return;
		}
		
		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
			{
				getOwner().sendPacket(SystemMessage.CRITICAL_HIT);
			}
			
			getOwner().sendPacket(new SystemMessage(SystemMessage.PET_HIT_FOR_S1_DAMAGE).addNumber(damage));
		}
	}
	
	@Override
	public void setName(String name)
	{
		final ItemInstance controlItem = getControlItem();
		if (getControlItem().getCustomType2() == (name == null ? 1 : 0))
		{
			// name not set yet
			controlItem.setCustomType2(name != null ? 1 : 0);
			controlItem.updateDatabase();
			getOwner().sendPacket(new InventoryUpdate(controlItem));
		}
		
		super.setName(name);
	}
	
	@Override
	public long getExpForThisLevel()
	{
		if (getStat().getLevel() >= ExperienceData.getInstance().getMaxLevel())
		{
			return 0;
		}
		return PetDataData.getInstance().getPetData(getId(), getStat().getLevel()).getPetMaxExp();
	}
	
	@Override
	public long getExpForNextLevel()
	{
		if (getStat().getLevel() >= (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			return 0;
		}
		return PetDataData.getInstance().getPetData(getId(), getStat().getLevel() + 1).getPetMaxExp() - 1;
	}
}
