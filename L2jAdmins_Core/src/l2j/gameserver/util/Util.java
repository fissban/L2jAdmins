package l2j.gameserver.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;

/**
 * General Utility functions related to Gameserver
 */
public final class Util
{
	public static String scrambleString(String string)
	{
		final List<String> letters = Arrays.asList(string.split(""));
		Collections.shuffle(letters);
		
		final StringBuilder sb = new StringBuilder(string.length());
		for (final String c : letters)
		{
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	public static String getRelativePath(File base, File file)
	{
		return file.toURI().getPath().substring(base.toURI().getPath().length());
	}
	
	public static boolean isValidNameTitle(String text)
	{
		var regexp = Pattern.compile("^[A-Za-z0-9]{3,16}$").matcher(text);
		
		if (!regexp.matches())
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Return degree value of object 2 to the horizontal line with object 1 being the origin
	 * @param  obj1
	 * @param  obj2
	 * @return
	 */
	public static double calculateAngleFrom(L2Object obj1, L2Object obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}
	
	/**
	 * Return degree value of object 2 to the horizontal line with object 1 being the origin
	 * @param  obj1X
	 * @param  obj1Y
	 * @param  obj2X
	 * @param  obj2Y
	 * @return
	 */
	public final static double calculateAngleFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return angleTarget;
	}
	
	public final static double convertHeadingToDegree(int clientHeading)
	{
		final double degree = clientHeading / 182.044444444;
		return degree;
	}
	
	public final static int convertDegreeToClientHeading(double degree)
	{
		if (degree < 0)
		{
			degree = 360 + degree;
		}
		return (int) (degree * 182.044444444);
	}
	
	public static int calculateHeadingFrom(L2Object obj1, L2Object obj2)
	{
		return calculateHeadingFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}
	
	public static int calculateHeadingFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return (int) (angleTarget * 182.044444444);
	}
	
	public final static int calculateHeadingFrom(double dx, double dy)
	{
		double angleTarget = Math.toDegrees(Math.atan2(dy, dx));
		if (angleTarget < 0)
		{
			angleTarget = 360 + angleTarget;
		}
		return (int) (angleTarget * 182.044444444);
	}
	
	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2)
	{
		return calculateDistance(x1, y1, 0, x2, y2, 0, false);
	}
	
	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		final double dx = (double) x1 - x2;
		final double dy = (double) y1 - y2;
		
		if (includeZAxis)
		{
			final double dz = z1 - z2;
			return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
		}
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	public static double calculateDistance(L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null))
		{
			return 1000000;
		}
		return calculateDistance(obj1.getX(), obj1.getY(), obj1.getZ(), obj2.getX(), obj2.getY(), obj2.getZ(), includeZAxis);
	}
	
	/*
	 * Checks if object is within range, adding collisionRadius
	 */
	public static boolean checkIfInRange(int range, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null))
		{
			return false;
		}
		
		if (range == -1)
		{
			return true; // not limited
		}
		
		int rad = 0;
		if (obj1 instanceof L2Character)
		{
			rad += ((L2Character) obj1).getTemplate().getCollisionRadius();
		}
		if (obj2 instanceof L2Character)
		{
			rad += ((L2Character) obj2).getTemplate().getCollisionRadius();
		}
		
		final double dx = obj1.getX() - obj2.getX();
		final double dy = obj1.getY() - obj2.getY();
		
		if (includeZAxis)
		{
			final double dz = obj1.getZ() - obj2.getZ();
			final double d = (dx * dx) + (dy * dy) + (dz * dz);
			
			return d <= ((range * range) + (2 * range * rad) + (rad * rad));
		}
		final double d = (dx * dx) + (dy * dy);
		
		return d <= ((range * range) + (2 * range * rad) + (rad * rad));
	}
	
	/*
	 * Checks if object is within short (sqrt(int.max_value)) radius, not using collisionRadius. Faster calculation than checkIfInRange if distance is short and collisionRadius isn't needed. Not for long distance checks (potential teleports, far away castles etc)
	 */
	public static boolean checkIfInShortRadius(int radius, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if ((obj1 == null) || (obj2 == null))
		{
			return false;
		}
		if (radius == -1)
		{
			return true; // not limited
		}
		
		final int dx = obj1.getX() - obj2.getX();
		final int dy = obj1.getY() - obj2.getY();
		
		if (includeZAxis)
		{
			final int dz = obj1.getZ() - obj2.getZ();
			return ((dx * dx) + (dy * dy) + (dz * dz)) <= (radius * radius);
		}
		return ((dx * dx) + (dy * dy)) <= (radius * radius);
	}
	
	/**
	 * Returns a delimited string for an given collection of string elements.<BR>
	 * (Based on implode() in PHP)
	 * @param  strCollection
	 * @param  strDelim
	 * @return               String implodedString
	 */
	public static String implodeString(Collection<String> strCollection, String strDelim)
	{
		String result = "";
		
		for (final String strValue : strCollection)
		{
			result += strValue + strDelim;
		}
		
		return result;
	}
	
	public static boolean isAlphaNumeric(String text)
	{
		if (text == null)
		{
			return false;
		}
		
		boolean result = true;
		
		for (final char c : text.toCharArray())
		{
			if (!Character.isLetterOrDigit(c))
			{
				result = false;
				break;
			}
		}
		return result;
	}
	
	/**
	 * @param  text - the text to check
	 * @return      {@code true} if {@code text} contains only numbers, {@code false} otherwise
	 */
	public static boolean isDigit(String text)
	{
		if ((text == null) || text.isEmpty())
		{
			return false;
		}
		for (final char c : text.toCharArray())
		{
			if (!Character.isDigit(c))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Verificamos que 2 mapas contengan los mismos objetos
	 * @param       <K>
	 * @param       <V>
	 * @param  map1
	 * @param  map2
	 * @return
	 */
	public static <K, V> boolean isEqualMap(Map<K, V> map1, Map<K, V> map2)
	{
		// Realizamos una primera verificacion obteniendo las dimensiones de los maps
		if (map1.size() > map2.size())
		{
			return false;
		}
		if (map2.size() > map1.size())
		{
			return false;
		}
		
		// Recorremos uno de los mapas y verificamos en cada paso q tenga cada key y que sus onjetos sean iguales
		for (final Entry<K, V> mp1 : map1.entrySet())
		{
			if (!map2.containsKey(mp1.getKey()))
			{
				return false;
			}
			
			if (!map2.get(mp1.getKey()).equals(mp1.getValue()))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param        <T>
	 * @param  array - the array to look into
	 * @param  obj   - the object to search for
	 * @return       {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static <T> boolean contains(T[] array, T obj)
	{
		for (final T element : array)
		{
			if (element == obj)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param  array - the array to look into
	 * @param  obj   - the integer to search for
	 * @return       {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise
	 */
	public static boolean contains(int[] array, int obj)
	{
		for (final int element : array)
		{
			if (element == obj)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Format the given date on the given format
	 * @param  date   : the date to format.
	 * @param  format : the format to correct by.
	 * @return        a string representation of the formatted date.
	 */
	public static String formatDate(Date date, String format)
	{
		final DateFormat dateFormat = new SimpleDateFormat(format);
		if (date != null)
		{
			return dateFormat.format(date);
		}
		
		return null;
	}
	
	public static String formatDate(long date, String format)
	{
		final DateFormat dateFormat = new SimpleDateFormat(format);
		if (date > 0)
		{
			return dateFormat.format(date);
		}
		
		return null;
	}
}
