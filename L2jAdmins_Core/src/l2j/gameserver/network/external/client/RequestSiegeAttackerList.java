package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SiegeAttackerList;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestSiegeAttackerList extends AClientPacket
{
	private int castleId;
	
	@Override
	protected void readImpl()
	{
		castleId = readD();
	}
	
	@Override
	public void runImpl()
	{
		Castle castle = CastleData.getInstance().getCastleById(castleId);
		if (castle == null)
		{
			return;
		}
		
		sendPacket(new SiegeAttackerList(castle));
	}
}
