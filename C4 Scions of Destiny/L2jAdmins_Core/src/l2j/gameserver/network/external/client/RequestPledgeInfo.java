package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.PledgeInfo;
import l2j.gameserver.network.external.server.PledgeShowMemberListAll;

/**
 * This class ...
 * @version $Revision: 1.5.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPledgeInfo extends AClientPacket
{
	private int clanId;
	
	@Override
	protected void readImpl()
	{
		clanId = readD();
	}
	
	@Override
	public void runImpl()
	{
		if (Config.DEBUG)
		{
			LOG.fine("infos for clan " + clanId + " requested");
		}
		
		L2PcInstance activeChar = getClient().getActiveChar();
		Clan clan = ClanData.getInstance().getClanById(clanId);
		if (clan == null)
		{
			// log.warning("Clan data for clanId " + clanId + " is missing");
			return; // we have no clan data ?!? should not happen
		}
		
		if (activeChar != null)
		{
			activeChar.sendPacket(new PledgeInfo(clan));
			
			if (clan.getId() == activeChar.getClanId())
			{
				activeChar.sendPacket(new PledgeShowMemberListAll(clan, activeChar));
			}
		}
	}
}
