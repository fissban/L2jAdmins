package l2j.log.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class ...
 * @version $Revision: 1.1.4.1 $ $Date: 2005/03/27 15:30:08 $
 */
public class FileLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	private static final String TAB = "\t";
	private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss,SSS");
	
	@Override
	public String format(LogRecord record)
	{
		StringBuilder sb = new StringBuilder();
		return sb.append(dateFmt.format(new Date(record.getMillis()))).append(TAB).append(record.getLevel().getName()).append(TAB).append(record.getThreadID()).append(TAB).append(record.getLoggerName()).append(TAB).append(record.getMessage()).append(CRLF).toString();
	}
}
