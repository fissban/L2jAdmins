package l2j.gameserver.util.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.illegalaction.enums.IllegalActionType;

/**
 * @author fissban
 */
public class IllegalActionAudit
{
	private static final Logger LOG = Logger.getLogger(IllegalActionAudit.class.getName());
	
	static
	{
		new File("log/IllegalActionAudit").mkdirs();
	}
	
	public static void auditIlegalAction(IllegalActionType punishmentType, String targetName, String rason)
	{
		final String date = new SimpleDateFormat("dd/MM/yyyy H:mm:ss").format(new Date());
		
		try (FileWriter save = new FileWriter(new File("log/IllegalActionAudit/" + targetName + ".txt"), true))
		{
			save.write(date + " >" + targetName + " >" + punishmentType.name() + " >" + rason + " >" + Config.EOL);
		}
		catch (IOException e)
		{
			LOG.severe("IllegalAction for player " + targetName + " could not be saved: " + e);
		}
	}
}
