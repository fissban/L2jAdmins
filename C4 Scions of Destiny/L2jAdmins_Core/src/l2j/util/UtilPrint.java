package l2j.util;

import java.util.logging.Logger;

/**
 * @author fissban
 */
public class UtilPrint
{
	private static final Logger LOG = Logger.getLogger(UtilPrint.class.getName());
	
	public static void section(String s)
	{
		s = "=[ " + s + " ]";
		while (s.length() < 100)
		{
			s = "-" + s;
		}
		LOG.info(s);
	}
	
	/**
	 * Print on the screen and save the messages generated in the load of the gameserver in the logs.
	 * @param classLoad
	 * @param message
	 * @param result
	 */
	public static void result(String classLoad, String message, String result)
	{
		String parse = "[" + classLoad + "] " + message;
		while (parse.length() < 78)
		{
			parse = parse + ".";
		}
		LOG.info(parse + " " + result);
	}
	
	/**
	 * Print on the screen and save the messages generated in the load of the gameserver in the logs.
	 * @param classLoad
	 * @param message
	 * @param result
	 */
	public static void result(String classLoad, String message, int result)
	{
		result(classLoad, message, result + "");
	}
}