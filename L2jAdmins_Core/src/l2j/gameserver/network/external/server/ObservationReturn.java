package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class ObservationReturn extends AServerPacket
{
	// ddSS
	private final L2PcInstance player;
	
	/**
	 * @param observer
	 */
	public ObservationReturn(L2PcInstance observer)
	{
		player = observer;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xe0);
		writeD(player.getSavedLocation().getX());
		writeD(player.getSavedLocation().getY());
		writeD(player.getSavedLocation().getZ());
	}
}
