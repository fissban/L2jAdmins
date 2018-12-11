package l2j.gameserver.illegalaction;

import l2j.Config;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.util.audit.IllegalActionAudit;

/**
 * This class ...
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public final class IllegalActionTask implements Runnable
{
	String rason;
	IllegalActionType punishmentType;
	L2PcInstance actor;
	
	public IllegalActionTask(L2PcInstance actor, String rason, IllegalActionType punishmentType)
	{
		this.rason = rason;
		this.punishmentType = punishmentType;
		this.actor = actor;
		
		switch (punishmentType)
		{
			case PUNISH_KICK:
				actor.sendMessage("You will be kicked for illegal action, GM informed.");
				break;
			case PUNISH_KICKBAN:
				actor.setAccessLevel(-1);
				actor.setAccountAccesslevel(-1);
				actor.sendMessage("You are banned for illegal action, GM informed.");
				break;
			case PUNISH_JAIL:
				actor.sendMessage("Illegal action performed!");
				actor.sendMessage("You will be teleported to GM Consultation Service area and be jailed.");
				break;
		}
	}
	
	@Override
	public void run()
	{
		if (Config.ILLEGAL_ACTION_AUDIT)
		{
			IllegalActionAudit.auditIlegalAction(punishmentType, actor.getName(), rason);
		}
		
		GmListData.getInstance().broadcastMessageToGMs(rason);
		
		switch (punishmentType)
		{
			case PUNISH_BROADCAST:
				return;
			case PUNISH_KICK:
				actor.closeConnection();
				break;
			case PUNISH_KICKBAN:
				actor.closeConnection();
				break;
			case PUNISH_JAIL:
				if (actor.isInJail())
				{
					actor.closeConnection();
				}
				else
				{
					actor.setInJail(true, Config.DEFAULT_PUNISH_PARAM);
				}
				break;
		}
	}
}
