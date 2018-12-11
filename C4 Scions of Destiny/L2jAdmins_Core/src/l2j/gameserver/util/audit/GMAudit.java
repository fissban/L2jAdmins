package l2j.gameserver.util.audit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;

public class GMAudit
{
	private static final Logger LOG = Logger.getLogger("gmaudit");
	
	public static void auditGMAction(String gmName, String action, String target, String params)
	{
		if (Config.GMAUDIT)
		{
			String today;
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
			today = formatter.format(new Date());
			
			LOG.log(Level.INFO, today + ">" + gmName + ">" + action + ">" + target + ">" + params);
		}
	}
}
