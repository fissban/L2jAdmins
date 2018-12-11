package l2j.log.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author zabbix Lets drink to code!
 */
public class AuditFormatter extends Formatter
{
	private static final String CRLF = "\r\n";
	private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM H:mm:ss");
	
	@Override
	public String format(LogRecord record)
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(dateFmt.format(new Date(record.getMillis())));
		sb.append(']');
		sb.append(' ');
		sb.append(record.getMessage());
		for (Object p : record.getParameters())
		{
			if (p == null)
			{
				continue;
			}
			sb.append(',');
			sb.append(' ');
			sb.append(p.toString());
		}
		sb.append(CRLF);
		
		return sb.toString();
	}
}
