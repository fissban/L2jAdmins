package l2j.gameserver.network.external.client;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class ChangeMoveType2 extends AClientPacket
{
	private boolean typeRun;
	
	@Override
	protected void readImpl()
	{
		typeRun = readD() == 1;
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (typeRun)
		{
			player.setRunning();
		}
		else
		{
			player.setWalking();
		}
	}
}
