package l2j.log.formatter;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class GMAuditFormatter extends Formatter
{
	@Override
	public String format(LogRecord record)
	{
		return record.getMessage() + "\r\n";
	}
}
