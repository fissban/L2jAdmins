package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowMiniMap extends AServerPacket
{
	private final int mapId;
	
	public ShowMiniMap(int mapId)
	{
		this.mapId = mapId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x9d);
		writeD(mapId);
	}
}
