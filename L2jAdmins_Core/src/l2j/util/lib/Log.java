package l2j.util.lib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @version 0.1, 2005-06-06
 * @author  Balancer
 */
public class Log
{
	private static final Logger LOG = Logger.getLogger(Log.class.getName());
	
	public static final void add(String text, String cat)
	{
		String date = (new SimpleDateFormat("yy.MM.dd H:mm:ss")).format(new Date());
		
		new File("log/game").mkdirs();
		
		File file = new File("log/game/" + (cat != null ? cat : "_all") + ".txt");
		try (FileWriter save = new FileWriter(file, true))
		{
			save.write("[" + date + "] '---': " + text + "\n");
			save.flush();
		}
		catch (IOException e)
		{
			LOG.warning("Error saving chat log failed: " + e);
		}
		
		if (cat != null)
		{
			add(text, null);
		}
	}
}
