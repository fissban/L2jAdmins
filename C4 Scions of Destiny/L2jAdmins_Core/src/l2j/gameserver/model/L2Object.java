package l2j.gameserver.model;

import java.util.logging.Logger;

import l2j.gameserver.handler.ActionHandler;
import l2j.gameserver.handler.ActionHandler.IActionHandler;
import l2j.gameserver.handler.ActionShiftHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.knownlist.ObjectKnownList;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.model.world.L2WorldRegion;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.task.continuous.ItemsOnGroundTaskManager;
import main.data.ObjectData;

/**
 * Mother class of all objects in the world which ones is it possible to interact (PC, NPC, Item...)
 */
public abstract class L2Object
{
	private static final Logger LOG = Logger.getLogger(L2Object.class.getName());
	
	private String name;
	private int objectId; // Object identifier
	// Object poly
	private int polyId = -1;
	// private ObjectPosition position;
	// Object position in world
	private LocationHolder worldPosition = new LocationHolder(0, 0, 0);
	// Object localization : Used for items/chars that are seen in the world
	private L2WorldRegion worldRegion;
	
	// used in EffectZone & DamageZone: objects affected
	private InstanceType instanceType = null;
	
	public L2Object(int objectId)
	{
		this.objectId = objectId;
		ObjectData.addObject(this);
		setWorldRegion(L2World.getInstance().getRegion(worldPosition));
	}
	
	public final void onAction(L2PcInstance player, boolean interact)
	{
		IActionHandler handler = ActionHandler.getAction(getInstanceType());
		if (handler != null)
		{
			handler.action(player, this, interact);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public final void onActionShift(L2PcInstance player)
	{
		IActionHandler handler = ActionShiftHandler.getHandler(getInstanceType());
		if (handler != null)
		{
			handler.action(player, this, true);
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void onForcedAttack(L2PcInstance player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	protected final void setInstanceType(InstanceType i)
	{
		instanceType = i;
	}
	
	public final InstanceType getInstanceType()
	{
		return instanceType;
	}
	
	public final boolean isInstanceType(InstanceType i)
	{
		return instanceType.isType(i);
	}
	
	public final boolean isInstanceTypes(InstanceType... i)
	{
		return instanceType.isTypes(i);
	}
	
	/**
	 * Do Nothing.<BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <li>L2GuardInstance : Set the home location of its L2GuardInstance</li>
	 * <li>L2Attackable : Reset the Spoiled flag</li>
	 */
	public void onSpawn()
	{
	}
	
	// Position - Should remove to fully move to L2ObjectPosition
	public final void setXYZ(int x, int y, int z)
	{
		assert getWorldRegion() != null;
		
		setWorldPosition(x, y, z);
		
		try
		{
			if (L2World.getInstance().getRegion(worldPosition) != getWorldRegion())
			{
				updateWorldRegion();
			}
		}
		catch (final Exception e)
		{
			LOG.warning("Object Id at bad coords: (x: " + getX() + ", y: " + getY() + ", z: " + getZ() + ").");
			
			// Bad Coords
			if (this instanceof L2Character)
			{
				decayMe();
			}
			else if (this instanceof L2PcInstance)
			{
				((L2PcInstance) this).teleToLocation(0, 0, 0);
				((L2PcInstance) this).sendMessage("Error with your coords, Please ask a GM for help!");
			}
		}
	}
	
	public final void setXYZInvisible(int x, int y, int z)
	{
		assert getWorldRegion() == null;
		
		if (x > L2World.WORLD_X_MAX)
		{
			x = L2World.WORLD_X_MAX - 5000;
		}
		if (x < L2World.WORLD_X_MIN)
		{
			x = L2World.WORLD_X_MIN + 5000;
		}
		if (y > L2World.WORLD_Y_MAX)
		{
			y = L2World.WORLD_Y_MAX - 5000;
		}
		if (y < L2World.WORLD_Y_MIN)
		{
			y = L2World.WORLD_Y_MIN + 5000;
		}
		
		worldPosition = new LocationHolder(x, y, z);
		
		setIsVisible(false);
	}
	
	public int getX()
	{
		return worldPosition.getX();
	}
	
	public int getY()
	{
		return worldPosition.getY();
	}
	
	public int getZ()
	{
		return worldPosition.getZ();
	}
	
	public void setWorldPosition(int x, int y, int z)
	{
		worldPosition.setXYZ(x, y, z);
	}
	
	public void setWorldPosition(LocationHolder loc)
	{
		worldPosition = loc;
	}
	
	public LocationHolder getWorldPosition()
	{
		return worldPosition;
	}
	
	/**
	 * Remove a L2Object from the world.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Remove the L2Object from the world</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packets to players</B></FONT><BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Delete NPC/PC or Unsummon</li>
	 */
	public final void decayMe()
	{
		L2WorldRegion reg = getWorldRegion();
		
		synchronized (this)
		{
			setWorldRegion(null);
		}
		
		// this can synchronize on others instances, so it's out of
		// synchronized, to avoid deadlocks
		// Remove the L2Object from the world
		L2World.getInstance().removeVisibleObject(this, reg);
		L2World.getInstance().removeObject(this);
		if (this instanceof ItemInstance)
		{
			ItemsOnGroundTaskManager.getInstance().remove((ItemInstance) this);
		}
	}
	
	public void refreshID()
	{
		L2World.getInstance().removeObject(this);
		IdFactory.getInstance().releaseId(getObjectId());
		objectId = IdFactory.getInstance().getNextId();
	}
	
	/**
	 * Init the position of a L2Object spawn and add it in the world as a visible object.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Set the x,y,z position of the L2Object spawn and update its worldregion</li>
	 * <li>Add the L2Object spawn in the allobjects of L2World</li>
	 * <li>Add the L2Object spawn to visibleObjects of its L2WorldRegion</li>
	 * <li>Add the L2Object spawn in the world as a <B>visible</B> object</li> <B><U> Assert </U> :</B><BR>
	 * <li>_worldRegion == null <I>(L2Object is invisible at the beginning)</I></li> <B><U> Example of use </U> :</B><BR>
	 * <li>Create Door</li>
	 * <li>Spawn : Monster, Minion, CTs, Summon...</li>
	 */
	public final void spawnMe()
	{
		synchronized (this)
		{
			// Set the x,y,z position of the L2Object spawn and update its worldregion
			setWorldRegion(L2World.getInstance().getRegion(worldPosition));
			
			// Add the L2Object spawn in the allobjects of L2World
			L2World.getInstance().addObject(this);
			
			// Add the L2Object spawn to visibleObjects and if necessary to allplayers of its L2WorldRegion
			getWorldRegion().addVisibleObject(this);
		}
		
		// this can synchronize on others instances, so it's out of
		// synchronized, to avoid deadlocks
		
		// Add the L2Object spawn in the world as a visible object
		L2World.getInstance().addVisibleObject(this, getWorldRegion());
		
		onSpawn();
	}
	
	public final void spawnMe(int x, int y, int z)
	{
		synchronized (this)
		{
			// Set the x,y,z position of the L2Object spawn and update its worldregion
			
			if (x > L2World.WORLD_X_MAX)
			{
				x = L2World.WORLD_X_MAX - 5000;
			}
			if (x < L2World.WORLD_X_MIN)
			{
				x = L2World.WORLD_X_MIN + 5000;
			}
			if (y > L2World.WORLD_Y_MAX)
			{
				y = L2World.WORLD_Y_MAX - 5000;
			}
			if (y < L2World.WORLD_Y_MIN)
			{
				y = L2World.WORLD_Y_MIN + 5000;
			}
			
			setWorldPosition(x, y, z);
			setWorldRegion(L2World.getInstance().getRegion(worldPosition));
		}
		
		// Add the L2Object spawn in the allobjects of L2World
		L2World.getInstance().addObject(this);
		
		// Add the L2Object spawn to visibleObjects and if necessary to allplayers of its L2WorldRegion
		getWorldRegion().addVisibleObject(this);
		
		// this can synchronize on others instances, so it's out of
		// synchronized, to avoid deadlocks
		// Add the L2Object spawn in the world as a visible object
		L2World.getInstance().addVisibleObject(this, getWorldRegion());
		
		onSpawn();
	}
	
	public boolean isAttackable()
	{
		return false;
	}
	
	public abstract boolean isAutoAttackable(L2Character attacker);
	
	public boolean isMerchant()
	{
		return false;
	}
	
	/**
	 * Return the visibility state of the L2Object. <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * A L2Object is visible if <B>__isVisible</B>=true and <B>_worldregion</B>!=null <BR>
	 * @return
	 */
	public final boolean isVisible()
	{
		// return getPosition().getWorldRegion() != null && isVisible;
		return getWorldRegion() != null;
	}
	
	public final void setIsVisible(boolean value)
	{
		if (!value && (getWorldRegion() != null))
		{
			getWorldRegion().removeVisibleObject(this);
			setWorldRegion(null);
		}
	}
	
	public final String getName()
	{
		return name;
	}
	
	public void setName(String value)
	{
		name = value;
	}
	
	public final int getObjectId()
	{
		return objectId;
	}
	
	/**
	 * @return reference to region this object is in
	 */
	public L2WorldRegion getWorldRegion()
	{
		return worldRegion;
	}
	
	public ObjectKnownList getKnownList()
	{
		return null;
	}
	
	/**
	 * Sends the Server->Client info packet for the object. Is Overridden in:
	 * <li>L2BoatInstance</li>
	 * <li>L2DoorInstance</li>
	 * <li>L2PcInstance</li>
	 * <li>L2StaticObjectInstance</li>
	 * <li>L2Npc</li>
	 * <li>L2Summon</li>
	 * <li>ItemInstance</li>
	 * @param activeChar
	 */
	public void sendInfo(L2PcInstance activeChar)
	{
		//
	}
	
	/**
	 * Check if current object has charged shot.
	 * @param  type of the shot to be checked.
	 * @return      {@code true} if the object has charged shot
	 */
	public boolean isChargedShot(ShotType type)
	{
		return false;
	}
	
	/**
	 * Charging shot into the current object.
	 * @param type    of the shot to be charged.
	 * @param charged
	 */
	public void setChargedShot(ShotType type, boolean charged)
	{
		//
	}
	
	/**
	 * Try to recharge a shot.
	 * @param physical skill are using Soul shots.
	 * @param magical  skill are using Spirit shots.
	 */
	public void rechargeShots(boolean physical, boolean magical)
	{
		//
	}
	
	@Override
	public String toString()
	{
		return "" + getObjectId();
	}
	
	public L2PcInstance getActingPlayer()
	{
		return null;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof L2Object)
		{
			if (((L2Object) obj).getObjectId() == getObjectId())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public final boolean isPoly()
	{
		return polyId != -1;
	}
	
	public final int getPolyId()
	{
		return polyId;
	}
	
	/**
	 * Set poly npc id, use -1 for unpoly
	 * @param value
	 */
	public final void setPolyId(int value)
	{
		polyId = value;
	}
	
	/**
	 * checks if current object changed its region, if so, update referencies
	 */
	private void updateWorldRegion()
	{
		if (!isVisible())
		{
			return;
		}
		
		final L2WorldRegion newRegion = L2World.getInstance().getRegion(worldPosition);
		if (newRegion != getWorldRegion())
		{
			// remove object from old region
			getWorldRegion().removeVisibleObject(this);
			
			setWorldRegion(newRegion);
			
			// Add the L2Oject spawn to visibleObjects and if necessary to allplayers of its L2WorldRegion
			getWorldRegion().addVisibleObject(this);
		}
	}
	
	public void setWorldRegion(L2WorldRegion value)
	{
		// confirm revalidation of old region's zones
		if ((worldRegion != null) && (this instanceof L2Character))
		{
			if (value != null)
			{
				worldRegion.revalidateZones((L2Character) this);
			}
			else
			{
				worldRegion.removeFromZones((L2Character) this);
			}
		}
		
		worldRegion = value;
	}
}
