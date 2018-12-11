package l2j.gameserver.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mkizub <BR>
 *         This class is used in order to have a set of couples (key,value).<BR>
 *         Methods deployed are accessors to the set (add/get value from its key) and addition of a whole set in the current one.
 */
public final class StatsSet
{
	private final Map<String, Object> set = new HashMap<>();
	
	/**
	 * Returns the set of values
	 * @return HashMap
	 */
	public final Map<String, Object> getSet()
	{
		return set;
	}
	
	/**
	 * Add a set of couple values in the current set
	 * @param newSet : StatsSet pointing out the list of couples to add in the current set
	 */
	public void add(StatsSet newSet)
	{
		Map<String, Object> newMap = newSet.getSet();
		for (String key : newMap.keySet())
		{
			Object value = newMap.get(key);
			set.put(key, value);
		}
	}
	
	public boolean getBool(final String key)
	{
		final Object val = set.get(key);
		
		if (val instanceof Boolean)
		{
			return (Boolean) val;
		}
		if (val instanceof String)
		{
			return Boolean.parseBoolean((String) val);
		}
		if (val instanceof Number)
		{
			return ((Number) val).intValue() != 0;
		}
		
		throw new IllegalArgumentException("StatsSet : Boolean value required, but found: " + val + " for key: " + key + ".");
	}
	
	public boolean getBool(final String key, final boolean defaultValue)
	{
		final Object val = set.get(key);
		
		if (val instanceof Boolean)
		{
			return (Boolean) val;
		}
		if (val instanceof String)
		{
			return Boolean.parseBoolean((String) val);
		}
		if (val instanceof Number)
		{
			return ((Number) val).intValue() != 0;
		}
		
		return defaultValue;
	}
	
	public byte getByte(final String key)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).byteValue();
		}
		if (val instanceof String)
		{
			return Byte.parseByte((String) val);
		}
		
		throw new IllegalArgumentException("StatsSet : Byte value required, but found: " + val + " for key: " + key + ".");
	}
	
	public byte getByte(final String key, final byte defaultValue)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).byteValue();
		}
		if (val instanceof String)
		{
			return Byte.parseByte((String) val);
		}
		
		return defaultValue;
	}
	
	public double getDouble(final String key)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).doubleValue();
		}
		if (val instanceof String)
		{
			return Double.parseDouble((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1. : 0.;
		}
		
		throw new IllegalArgumentException("StatsSet : Double value required, but found: " + val + " for key: " + key + ".");
	}
	
	public double getDouble(final String key, final double defaultValue)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).doubleValue();
		}
		if (val instanceof String)
		{
			return Double.parseDouble((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1. : 0.;
		}
		
		return defaultValue;
	}
	
	public float getFloat(final String key)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).floatValue();
		}
		if (val instanceof String)
		{
			return Float.parseFloat((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1 : 0;
		}
		
		throw new IllegalArgumentException("StatsSet : Float value required, but found: " + val + " for key: " + key + ".");
	}
	
	public float getFloat(final String key, final float defaultValue)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).floatValue();
		}
		if (val instanceof String)
		{
			return Float.parseFloat((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1 : 0;
		}
		
		return defaultValue;
	}
	
	public int getInteger(final String key)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).intValue();
		}
		if (val instanceof String)
		{
			return Integer.parseInt((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1 : 0;
		}
		
		throw new IllegalArgumentException("StatsSet : Integer value required, but found: " + val + " for key: " + key + ".");
	}
	
	public int getInteger(final String key, final int defaultValue)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).intValue();
		}
		if (val instanceof String)
		{
			return Integer.parseInt((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1 : 0;
		}
		
		return defaultValue;
	}
	
	public long getLong(final String key)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).longValue();
		}
		if (val instanceof String)
		{
			return Long.parseLong((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1L : 0L;
		}
		
		throw new IllegalArgumentException("StatsSet : Long value required, but found: " + val + " for key: " + key + ".");
	}
	
	public long getLong(final String key, final long defaultValue)
	{
		final Object val = set.get(key);
		
		if (val instanceof Number)
		{
			return ((Number) val).longValue();
		}
		if (val instanceof String)
		{
			return Long.parseLong((String) val);
		}
		if (val instanceof Boolean)
		{
			return (Boolean) val ? 1L : 0L;
		}
		
		return defaultValue;
	}
	
	/**
	 * Returns the String associated to the key put in parameter ("name").
	 * @param  name : String designating the key in the set
	 * @return      String : value associated to the key
	 */
	public String getString(String name)
	{
		Object val = set.get(name);
		if (val == null)
		{
			throw new IllegalArgumentException("String value required, but not specified");
		}
		return String.valueOf(val);
	}
	
	/**
	 * Returns the String associated to the key put in parameter ("name"). If the value associated to the key is null, this method returns the value of the parameter deflt.
	 * @param  name  : String designating the key in the set
	 * @param  deflt : String designating the default value if value associated with the key is null
	 * @return       String : value associated to the key
	 */
	public String getString(String name, String deflt)
	{
		Object val = set.get(name);
		if (val == null)
		{
			return deflt;
		}
		return String.valueOf(val);
	}
	
	/**
	 * Returns an enumeration of &lt;T&gt; from the set
	 * @param            <T> : Class of the enumeration returned
	 * @param  name      : String designating the key in the set
	 * @param  enumClass : Class designating the class of the value associated with the key in the set
	 * @return           Enum<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T getEnum(String name, Class<T> enumClass)
	{
		Object val = set.get(name);
		if (val == null)
		{
			throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + " required, but not specified");
		}
		if (enumClass.isInstance(val))
		{
			return (T) val;
		}
		try
		{
			return Enum.valueOf(enumClass, String.valueOf(val));
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + " required, but found: " + val);
		}
	}
	
	/**
	 * Returns an enumeration of &lt;T&gt; from the set. If the enumeration is empty, the method returns the value of the parameter "deflt".
	 * @param            <T> : Class of the enumeration returned
	 * @param  name      : String designating the key in the set
	 * @param  enumClass : Class designating the class of the value associated with the key in the set
	 * @param  deflt     : <T> designating the value by default
	 * @return           Enum<T>
	 */
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T getEnum(String name, Class<T> enumClass, T deflt)
	{
		Object val = set.get(name);
		if (val == null)
		{
			return deflt;
		}
		if (enumClass.isInstance(val))
		{
			return (T) val;
		}
		try
		{
			return Enum.valueOf(enumClass, String.valueOf(val));
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + " required, but found: " + val);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getObject(final String key, final Class<A> type)
	{
		final Object val = set.get(key);
		
		if ((val == null) || !type.isAssignableFrom(val.getClass()))
		{
			return null;
		}
		
		return (A) val;
	}
	
	public void set(final String key, final Object value)
	{
		set.put(key, value);
	}
	
	/**
	 * Add the String hold in param "value" for the key "name"
	 * @param name  : String designating the key in the set
	 * @param value : String corresponding to the value associated with the key
	 */
	public void set(String name, String value)
	{
		set.put(name, value);
	}
	
	/**
	 * Add the boolean hold in param "value" for the key "name"
	 * @param name  : String designating the key in the set
	 * @param value : boolean corresponding to the value associated with the key
	 */
	public void set(String name, boolean value)
	{
		set.put(name, value);
	}
	
	/**
	 * Add the int hold in param "value" for the key "name"
	 * @param name  : String designating the key in the set
	 * @param value : int corresponding to the value associated with the key
	 */
	public void set(String name, int value)
	{
		set.put(name, value);
	}
	
	/**
	 * Add the double hold in param "value" for the key "name"
	 * @param name  : String designating the key in the set
	 * @param value : double corresponding to the value associated with the key
	 */
	public void set(String name, double value)
	{
		set.put(name, value);
	}
	
	/**
	 * Add the long hold in param "value" for the key "name"
	 * @param name  : String designating the key in the set
	 * @param value : double corresponding to the value associated with the key
	 */
	public void set(String name, long value)
	{
		set.put(name, value);
	}
	
	/**
	 * Add the Enum hold in param "value" for the key "name"
	 * @param name  : String designating the key in the set
	 * @param value : Enum corresponding to the value associated with the key
	 */
	public void set(String name, Enum<?> value)
	{
		set.put(name, value);
	}
}
