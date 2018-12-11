package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.StopRotation;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class FinishRotating extends AClientPacket
{
	private int degree;
	@SuppressWarnings("unused")
	private int unknown;
	
	@Override
	protected void readImpl()
	{
		degree = readD();
		unknown = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		getClient().getActiveChar().broadcastPacket(new StopRotation(getClient().getActiveChar(), degree, 0));
	}
}
