package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestJoinSiege extends AClientPacket
{
	private int castleId;
	private int isAttacker;
	private int isJoining;
	
	@Override
	protected void readImpl()
	{
		castleId = readD();
		isAttacker = readD();
		isJoining = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (!activeChar.isClanLeader())
		{
			return;
		}
		
		Castle castle = CastleData.getInstance().getCastleById(castleId);
		if (castle == null)
		{
			LOG.warning("RequestJoinSiege -> missing clastleId: " + castleId);
			return;
		}
		
		if (isJoining == 1)
		{
			if (activeChar.getClan().getDissolvingExpiryTime() > System.currentTimeMillis())
			{
				activeChar.sendPacket(SystemMessage.NOBODY_IN_GAME_TO_CHAT);
				return;
			}
			
			if (isAttacker == 1)
			{
				castle.getSiege().registerAttacker(activeChar);
			}
			else
			{
				castle.getSiege().registerDefender(activeChar);
			}
		}
		else
		{
			castle.getSiege().removeSiegeClan(activeChar);
		}
		
		castle.getSiege().listRegisterClan(activeChar);
	}
}
