package l2j.log.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * @author zabbix Lets drink to code!
 */
public class AuditFilter implements Filter
{
	@Override
	public boolean isLoggable(LogRecord record)
	{
		return record.getLoggerName().equalsIgnoreCase("audit");
	}
}
