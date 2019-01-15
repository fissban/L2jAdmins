package main.util;

import java.util.ArrayList;
import java.util.List;

import main.holders.objects.CharacterHolder;
import main.holders.objects.ObjectHolder;

/**
 * @author fissban
 */
public class Util
{
	public static final String SEPARATOR = "-----------------------------------------------------------";
	
	/**
	 * Check if the objects belong to a particular instance.
	 * @param  type
	 * @param  objects
	 * @return
	 */
	public static <A> boolean areObjectType(Class<A> type, ObjectHolder... objects)
	{
		if ((objects == null) || (objects.length <= 0))
		{
			return false;
		}
		
		for (var o : objects)
		{
			if ((o == null) || (o.getInstance() == null) || !type.isAssignableFrom(o.getInstance().getClass()))
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNumber(String text)
	{
		try
		{
			Integer.parseInt(text);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
	
	public static List<Integer> parseInt(String line, String split)
	{
		var list = new ArrayList<Integer>();
		
		for (var s : line.split(split))
		{
			list.add(Integer.parseInt(s));
		}
		
		return list;
	}
	
	/**
	 * Check the difference in level between 2 characters, return true if it is lower than lvl
	 * <li></li>
	 * <li></li>
	 * @param  ph
	 * @param  obj
	 * @param  lvl
	 * @return
	 */
	public static boolean checkLvlDifference(CharacterHolder ph, CharacterHolder obj, int lvl)
	{
		if (Math.abs(ph.getInstance().getLevel() - obj.getInstance().getLevel()) <= lvl)
		{
			return true;
		}
		
		return false;
	}
}
