package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.network.AServerPacket;

/**
 * This class ...
 * @version $Revision: 1.5.2.3.2.5 $ $Date: 2005/03/29 23:15:10 $
 */
public class PetStatusUpdate extends AServerPacket
{
	private final L2Summon summon;
	private int maxFed, curFed;
	
	public PetStatusUpdate(L2Summon summon)
	{
		this.summon = summon;
		
		if (summon instanceof L2PetInstance)
		{
			curFed = summon.getCurrentFed(); // how fed it is
			maxFed = summon.getMaxFeed(); // max fed it can be
		}
		else if (summon instanceof L2SummonInstance)
		{
			curFed = ((L2SummonInstance) summon).getTimeRemaining();
			maxFed = ((L2SummonInstance) summon).getTotalLifeTime();
		}
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xb5);
		writeD(summon.getSummonType());
		writeD(summon.getObjectId());
		writeD(summon.getX());
		writeD(summon.getY());
		writeD(summon.getZ());
		writeS("");
		writeD(curFed);
		writeD(maxFed);
		writeD((int) summon.getCurrentHp());
		writeD(summon.getStat().getMaxHp());
		writeD((int) summon.getCurrentMp());
		writeD(summon.getStat().getMaxMp());
		writeD(summon.getLevel());
		writeD((int) summon.getStat().getExp());
		writeD((int) summon.getExpForThisLevel());// 0% absolute value
		writeD((int) summon.getExpForNextLevel());// 100% absolute value
	}
}
