package main.holders;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import main.holders.objects.ObjectHolder;

/**
 * @author fissban
 */
public class WorldHolder
{
	// world id
	private int id = 0;
	// remove from WorldData if no have players inside
	private boolean removeIfEmpty = true;
	// objects inside world
	private volatile List<ObjectHolder> objects = new CopyOnWriteArrayList<>();
	
	public WorldHolder(int id, boolean removeIfEmpty)
	{
		this.id = id;
		this.removeIfEmpty = removeIfEmpty;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void add(ObjectHolder o)
	{
		o.setWorldId(id);
		objects.add(o);
	}
	
	public void remove(ObjectHolder o)
	{
		o.setWorldId(0);
		if (objects.contains(o))
		{
			objects.remove(o);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <A> List<A> getAll(Class<A> type)
	{
		return (List<A>) objects.stream().filter(c -> type.isAssignableFrom(c.getClass())).collect(Collectors.toList());
	}
	
	public boolean isRemoveIfEmpty()
	{
		return removeIfEmpty;
	}
}
