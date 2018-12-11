package l2j.log.formatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class ...
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:30:08 $
 */

public class ConsoleLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	
	@Override
	public String format(LogRecord record)
	{
		StringBuilder sb = new StringBuilder();
		// output.append(record.getLevel().getName());
		// output.append();
		// output.append(record.getLoggerName());
		// output.append();
		sb.append(record.getMessage());
		sb.append(CRLF);
		if (record.getThrown() != null)
		{
			try (StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);)
			{
				record.getThrown().printStackTrace(pw);
				sb.append(sw.toString());
				sb.append(CRLF);
			}
			catch (Exception ex)
			{
				
			}
		}
		
		return sb.toString();
	}
}
