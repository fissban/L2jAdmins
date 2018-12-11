package l2j.gameserver.network.external.client;

import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.BeginRotation;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class StartRotating extends AClientPacket
{
	private int degree;
	private int side;
	
	@Override
	protected void readImpl()
	{
		degree = readD();
		side = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (getClient().getActiveChar() == null)
		{
			return;
		}
		
		getClient().getActiveChar().broadcastPacket(new BeginRotation(getClient().getActiveChar(), degree, side, 0));
	}
}
