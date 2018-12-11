package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestTargetCanceld extends AClientPacket
{
	private int unselect;
	
	@Override
	protected void readImpl()
	{
		unselect = readH();
	}
	
	@Override
	public void runImpl()
	{
		L2Character activeChar = getClient().getActiveChar();
		if (activeChar != null)
		{
			if (unselect == 0)
			{
				if (activeChar.isCastingNow() && activeChar.canAbortCast())
				{
					activeChar.abortCast();
				}
				else if (activeChar.getTarget() != null)
				{
					activeChar.setTarget(null);
				}
			}
			else if (activeChar.getTarget() != null)
			{
				activeChar.setTarget(null);
			}
		}
	}
}
