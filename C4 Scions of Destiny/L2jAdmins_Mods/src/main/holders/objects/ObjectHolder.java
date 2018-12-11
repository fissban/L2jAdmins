package main.holders.objects;

import main.data.ObjectData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author fissban
 */
public class ObjectHolder
{
	// object
	private L2Object obj = null;
	// world
	private int worldId = 0;
	
	public ObjectHolder(L2Object obj)
	{
		this.obj = obj;
	}
	
	/**
	 * ObjectId
	 * @return int
	 */
	public int getObjectId()
	{
		return obj.getObjectId();
	}
	
	public L2Object getInstance()
	{
		return obj;
	}
	
	public void setWorldId(int id)
	{
		if (worldId == id)
		{
			return;
		}
		
		worldId = id;
		
		if (getInstance() == null)
		{
			return;
		}
		
		// remove from old world
		removeDifferentWorldObjects();
		
		if (this instanceof PlayerHolder && ((L2PcInstance) getInstance()).getPet() != null)
		{
			ObjectData.get(ObjectHolder.class, ((L2PcInstance) getInstance()).getPet()).setWorldId(id);
		}
		
		if (!(this instanceof ItemHolder) && getInstance().isVisible() && !getInstance().getKnownList().getObjectType(L2Object.class).isEmpty())
		{
			getInstance().decayMe();
			getInstance().spawnMe();
		}
	}
	
	public int getWorldId()
	{
		return worldId;
	}
	
	public void removeDifferentWorldObjects()
	{
		for (var obj : getInstance().getKnownList().getObjectType(L2Object.class))
		{
			if (isDifferentWorld(obj))
			{
				var ch = ObjectData.get(ObjectHolder.class, obj);
				
				if (ch == null)
				{
					continue;
				}
				
				// remove known objects
				ch.getInstance().getKnownList().removeObject(getInstance());
				// remove known objects
				getInstance().getKnownList().removeObject(ch.getInstance());
			}
		}
	}
	
	public boolean isDifferentWorld(L2Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		
		var oh = ObjectData.get(ObjectHolder.class, obj);
		
		if (oh == null)
		{
			return false;
		}
		
		if (oh.getWorldId() == getWorldId())
		{
			return false;
		}
		
		return true;
	}
}
