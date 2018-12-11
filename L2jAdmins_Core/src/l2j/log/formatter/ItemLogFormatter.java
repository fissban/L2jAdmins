package l2j.log.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import l2j.gameserver.model.items.instance.ItemInstance;

/**
 * @author Advi
 */
public class ItemLogFormatter extends Formatter
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
			if (p instanceof ItemInstance)
			{
				ItemInstance item = (ItemInstance) p;
				sb.append("item " + item.getObjectId() + ":");
				if (item.getEnchantLevel() > 0)
				{
					sb.append("+" + item.getEnchantLevel() + " ");
				}
				sb.append(item.getItem().getName());
				sb.append("(" + item.getCount() + ")");
			}
			// else if (p instanceof L2PcInstance)
			// output.append(((L2PcInstance)p).getName());
			else
			{
				sb.append(p.toString()/* + ":" + ((L2Object)p).getObjectId() */);
			}
		}
		sb.append(CRLF);
		
		return sb.toString();
	}
	
}
