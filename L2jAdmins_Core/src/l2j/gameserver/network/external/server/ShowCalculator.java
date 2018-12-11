package l2j.gameserver.network.external.server;

import l2j.gameserver.network.AServerPacket;

/**
 * sample format d
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShowCalculator extends AServerPacket
{
	private final int calculatorId;
	
	public ShowCalculator(int calculatorId)
	{
		this.calculatorId = calculatorId;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xdc);
		writeD(calculatorId);
	}
}
