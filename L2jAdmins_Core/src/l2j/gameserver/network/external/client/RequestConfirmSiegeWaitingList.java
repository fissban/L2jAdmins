package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.ClanData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.clan.Clan;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SiegeDefenderList;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestConfirmSiegeWaitingList extends AClientPacket
{
	private int approved;
	private int castleId;
	private int clanId;
	
	@Override
	protected void readImpl()
	{
		castleId = readD();
		clanId = readD();
		approved = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Check if the player has a clan
		if (activeChar.getClan() == null)
		{
			return;
		}
		
		Castle castle = CastleData.getInstance().getCastleById(castleId);
		if (castle == null)
		{
			return;
		}
		
		// Check if leader of the clan who owns the castle?
		if ((castle.getOwnerId() != activeChar.getClanId()) || (!activeChar.isClanLeader()))
		{
			return;
		}
		
		Clan clan = ClanData.getInstance().getClanById(clanId);
		if (clan == null)
		{
			return;
		}
		
		if (!castle.getSiege().isRegistrationOver())
		{
			if (approved == 1)
			{
				if (castle.getSiege().isDefenderWaiting(clan))
				{
					castle.getSiege().approveSiegeDefenderClan(clanId);
				}
				else
				{
					return;
				}
			}
			else
			{
				if ((castle.getSiege().isDefenderWaiting(clan)) || (castle.getSiege().isDefender(clan)))
				{
					castle.getSiege().removeSiegeClan(clanId);
				}
			}
		}
		
		// Update the defender list
		activeChar.sendPacket(new SiegeDefenderList(castle));
	}
}
