package l2j.log.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author Advi
 */
public class ItemFilter implements Filter
{
	// This is example how to exclude consuming of shots and arrows from logging
	private final String excludeProcess = "Consume";
	private final String excludeItemType = "Arrow, Shot";
	
	@Override
	public boolean isLoggable(LogRecord record)
	{
		if (!record.getLoggerName().equals("item"))
		{
			return false;
		}
		
		if (excludeProcess != null)
		{
			String[] messageList = record.getMessage().split(":");
			if ((messageList.length < 2) || !excludeProcess.contains(messageList[1]))
			{
				return true;
			}
		}
		
		if (excludeItemType != null)
		{
			ItemInstance item = ((ItemInstance) record.getParameters()[0]);
			if (!excludeItemType.contains(item.getType().toString()))
			{
				return true;
			}
		}
		return ((excludeProcess == null) && (excludeItemType == null));
	}
}
