package l2j.gameserver.network.external.server;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.network.AServerPacket;

/**
 * Format : (h) d [dS] h sub id d: number of manors [ d: id S: manor name ]
 * @author l3x
 */
public class ExSendManorList extends AServerPacket
{
	private static final List<String> MANORS = new ArrayList<>(7);
	
	static
	{
		MANORS.add("gludio");
		MANORS.add("dion");
		MANORS.add("giran");
		MANORS.add("oren");
		MANORS.add("aden");
		MANORS.add("innadril");
		MANORS.add("goddard");
	}
	
	public ExSendManorList()
	{
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1B);
		writeD(MANORS.size());
		for (int i = 0; i < MANORS.size(); i++)
		{
			writeD(i + 1);
			writeS(MANORS.get(i));
		}
	}
}
