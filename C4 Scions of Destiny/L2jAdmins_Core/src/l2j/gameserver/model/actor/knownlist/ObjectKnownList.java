package l2j.gameserver.model.actor.knownlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.util.Util;
import main.data.ObjectData;
import main.holders.objects.ObjectHolder;

public class ObjectKnownList
{
	protected L2Object activeObject;
	protected Map<Integer, L2Object> knownObjects = new ConcurrentHashMap<>();
	
	public ObjectKnownList(L2Object activeObject)
	{
		this.activeObject = activeObject;
	}
	
	/**
	 * Add object to known list.<br>
	 * <b>Is overridden by children in most cases.</b>
	 * @param  object : {@link L2Object} to be added.
	 * @return        boolean : True, when object was successfully added.
	 */
	public boolean addObject(L2Object object)
	{
		// object must exist, cannot add self
		if (getObject(object))
		{
			return false;
		}
		
		if (ObjectData.get(ObjectHolder.class, activeObject).isDifferentWorld(object))
		{
			return false;
		}
		
		// object must be inside distance to watch
		if (!Util.checkIfInShortRadius(getDistanceToWatchObject(object), activeObject, object, true))
		{
			return false;
		}
		
		// add object to known list and check if object already existed there
		return knownObjects.put(object.getObjectId(), object) == null;
	}
	
	/**
	 * Remove object from known list.<br>
	 * <b>Is overridden by children in most cases.</b>
	 * @param  object : {@link L2Object} to be removed.
	 * @return        boolean : True, when object was successfully removed.
	 */
	public boolean removeObject(L2Object object)
	{
		// object must exist.
		if (object == null)
		{
			return false;
		}
		
		// remove object from known list and check if object existed in there
		return knownObjects.remove(object.getObjectId()) != null;
	}
	
	/**
	 * Remove object from known list, which are beyond distance to forget.
	 */
	public final void forgetObjects()
	{
		// for all objects in known list
		for (L2Object object : knownObjects.values())
		{
			// object is not visible or out of distance to forget, remove from known list
			if (!object.isVisible() || !Util.checkIfInShortRadius(getDistanceToForgetObject(object), activeObject, object, true))
			{
				removeObject(object);
			}
		}
	}
	
	/**
	 * Remove all objects from known list.
	 */
	public void removeAllObjects()
	{
		knownObjects.clear();
	}
	
	/**
	 * Check if object is in known list.
	 * @param  object : {@link L2Object} to be checked.
	 * @return        boolean : True, when object is in known list.
	 */
	public final boolean getObject(L2Object object)
	{
		// object does not exist, false
		if (object == null)
		{
			return false;
		}
		
		// object is known list owner or is in known list
		return (activeObject == object) || knownObjects.containsKey(object.getObjectId());
	}
	
	/**
	 * Return the known list.
	 * @return Collection<L2Object> : The known list.
	 */
	public final Collection<L2Object> getObjects()
	{
		return knownObjects.values();
	}
	
	/**
	 * Return the known list of given object type.
	 * @param       <A> : Object type must be instanceof {@link L2Object}.
	 * @param  type : Class specifying object type.
	 * @return      List<A> : Known list of given object type.
	 */
	@SuppressWarnings("unchecked")
	public final <A> List<A> getObjectType(Class<A> type)
	{
		// create result list
		List<A> result = new ArrayList<>();
		
		// for all objects in known list
		for (L2Object obj : knownObjects.values())
		{
			// object type is correct, add to the list
			if (type.isAssignableFrom(obj.getClass()))
			{
				result.add((A) obj);
			}
		}
		
		// return result
		return result;
	}
	
	/**
	 * Return the known list of given object type within specified radius.
	 * @param         <A> : Object type must be instanceof {@link L2Object}.
	 * @param  type   : Class specifying object type.
	 * @param  radius : Radius to in which object must be located.
	 * @return        List<A> : Known list of given object type.
	 */
	@SuppressWarnings("unchecked")
	public final <A> List<A> getObjectTypeInRadius(Class<A> type, int radius)
	{
		// create result list
		List<A> result = new ArrayList<>();
		
		// for all objects in known list
		for (L2Object obj : knownObjects.values())
		{
			// object type is correct and object in given radius, add to the list
			if (type.isAssignableFrom(obj.getClass()) && Util.checkIfInRange(radius, activeObject, obj, true))
			{
				result.add((A) obj);
			}
		}
		
		// return result
		return result;
	}
	
	/**
	 * Returns the distance to watch object, aka distance to add object to known list.<br>
	 * <b>Is overridden by children in most cases.</b>
	 * @param  object : {@link L2Object} to be checked.
	 * @return        int : Distance.
	 */
	public int getDistanceToWatchObject(L2Object object)
	{
		return 0;
	}
	
	/**
	 * Returns the distance to forget object, aka distance to remove object from known list.<br>
	 * <b>Is overridden by children in most cases.</b>
	 * @param  object : {@link L2Object} to be checked.
	 * @return        int : Distance.
	 */
	public int getDistanceToForgetObject(L2Object object)
	{
		return 0;
	}
}
