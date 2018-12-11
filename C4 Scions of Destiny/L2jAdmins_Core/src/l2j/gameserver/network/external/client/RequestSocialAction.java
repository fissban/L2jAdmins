package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.6.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSocialAction extends AClientPacket
{
	// format cd
	private int actionId;
	
	@Override
	protected void readImpl()
	{
		actionId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// You cannot do anything else while fishing
		if (activeChar.getFishing().isFishing())
		{
			activeChar.sendPacket(SystemMessage.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		// check if its the actionId is allowed
		if ((actionId < 2) || (actionId > 13))
		{
			IllegalAction.report(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " requested an internal Social Action.");
			return;
		}
		
		if (!(activeChar.getPrivateStore().isInStoreMode()) && !activeChar.isAlikeDead() && (activeChar.getAI().getIntention() == CtrlIntentionType.IDLE))
		{
			if (Config.DEBUG)
			{
				LOG.fine("Social Action:" + actionId);
			}
			
			activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), SocialActionType.values()[actionId]));
		}
	}
}
