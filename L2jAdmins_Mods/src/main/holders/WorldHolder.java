package main.holders;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import main.data.memory.ObjectData;
import main.holders.objects.ObjectHolder;
import main.holders.objects.PlayerHolder;
import main.holders.objects.SummonHolder;

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
		
		if (o instanceof PlayerHolder)
		{
			var summon = ((L2PcInstance) o.getInstance()).getPet();
			if (summon != null)
			{
				var sh = ObjectData.get(SummonHolder.class, summon);
				sh.setWorldId(id);
				objects.add(sh);
			}
		}
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
