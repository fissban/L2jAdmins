package l2j.gameserver.network.external.server;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.party.enums.PartyItemDitributionType;
import l2j.gameserver.network.AServerPacket;

/**
 * sample
 * <p>
 * 4b c1 b2 e0 4a 00 00 00 00
 * <p>
 * format cdd
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class AskJoinParty extends AServerPacket
{
	private final String requestorName;
	private PartyItemDitributionType itemDistribution;
	
	/**
	 * @param requestor
	 * @param itemDistribution
	 */
	public AskJoinParty(L2PcInstance requestor, PartyItemDitributionType itemDistribution)
	{
		requestorName = requestor.getName();
		this.itemDistribution = itemDistribution;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0x39);
		writeS(requestorName);
		writeD(itemDistribution.ordinal());
	}
}
