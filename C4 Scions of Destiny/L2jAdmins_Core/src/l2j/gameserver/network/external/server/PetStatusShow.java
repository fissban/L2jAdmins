package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @author  Yme
 * @version $Revision: 1.3.2.2.2.4 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusShow extends AServerPacket
{
	private final int summonType;
	
	public PetStatusShow(L2Summon summon)
	{
		summonType = summon.getSummonType();
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xB0);
		writeD(summonType);
	}
}
