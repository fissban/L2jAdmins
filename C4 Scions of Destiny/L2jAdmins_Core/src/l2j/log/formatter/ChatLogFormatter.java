package l2j.log.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class ...
 * @version $Revision: 1.1.4.1 $ $Date: 2005/02/06 16:14:46 $
 */
public class ChatLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	
	private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM H:mm:ss");
	
	@Override
	public String format(LogRecord record)
	{
		Object[] params = record.getParameters();
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(dateFmt.format(new Date(record.getMillis())));
		sb.append(']');
		sb.append(' ');
		if (params != null)
		{
			for (Object p : params)
			{
				sb.append(p);
				sb.append(' ');
			}
		}
		sb.append(record.getMessage());
		sb.append(CRLF);
		
		return sb.toString();
	}
}
