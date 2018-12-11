package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowTownMap extends AServerPacket
{
	private final String texture;
	private final int x;
	private final int y;
	
	public ShowTownMap(String texture, int x, int y)
	{
		this.texture = texture;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xde);
		writeS(texture);
		writeD(x);
		writeD(y);
	}
}
