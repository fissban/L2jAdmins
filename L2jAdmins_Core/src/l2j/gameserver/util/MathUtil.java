package l2j.gameserver.util;

public class MathUtil
{
	/**
	 * @param  objectsSize : The overall elements size.
	 * @param  pageSize    : The number of elements per page.
	 * @return             The number of pages, based on the number of elements and the number of elements we want per page.
	 */
	public static int countPagesNumber(int objectsSize, int pageSize)
	{
		return (objectsSize / pageSize) + ((objectsSize % pageSize) == 0 ? 0 : 1);
	}
	
	/**
	 * @param  numToTest : The number to test.
	 * @param  min       : The minimum limit.
	 * @param  max       : The maximum limit.
	 * @return           the number or one of the limit (mininum / maximum).
	 */
	public static int limit(int numToTest, int min, int max)
	{
		return (numToTest > max) ? max : ((numToTest < min) ? min : numToTest);
	}
}
