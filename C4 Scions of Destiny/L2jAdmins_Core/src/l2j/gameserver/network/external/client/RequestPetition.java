package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.instancemanager.PetitionManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * <p>
 * Format: (c) Sd
 * <ul>
 * <li>S: content</li>
 * <li>d: type</li>
 * </ul>
 * </p>
 * @author -Wooden-, TempyIncursion
 */
public class RequestPetition extends AClientPacket
{
	private String content;
	private int type; // 1 = on : 0 = off;
	
	@Override
	protected void readImpl()
	{
		content = readS();
		type = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (!GmListData.getInstance().isGmOnline())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.NO_GM_PROVIDING_SERVICE_NOW));
			return;
		}
		
		if (!PetitionManager.getInstance().isPetitioningAllowed())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.GAME_CLIENT_UNABLE_TO_CONNECT_TO_PETITION_SERVER));
			return;
		}
		
		if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.ONLY_ONE_ACTIVE_PETITION_AT_TIME));
			return;
		}
		
		if (PetitionManager.getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.PETITION_SYSTEM_CURRENT_UNAVAILABLE));
			return;
		}
		
		int totalPetitions = PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar) + 1;
		
		if (totalPetitions > Config.MAX_PETITIONS_PER_PLAYER)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.WE_HAVE_RECEIVED_S1_PETITIONS_TODAY);
			sm.addNumber(totalPetitions);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}
		
		if (content.length() > 255)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.PETITION_MAX_CHARS_255));
			return;
		}
		
		int petitionId = PetitionManager.getInstance().submitPetition(activeChar, content, type);
		
		SystemMessage sm = new SystemMessage(SystemMessage.PETITION_ACCEPTED_RECENT_NO_S1);
		sm.addNumber(petitionId);
		activeChar.sendPacket(sm);
		
		sm = new SystemMessage(SystemMessage.SUBMITTED_YOU_S1_TH_PETITION_S2_LEFT);
		sm.addNumber(totalPetitions);
		sm.addNumber(Config.MAX_PETITIONS_PER_PLAYER - totalPetitions);
		activeChar.sendPacket(sm);
		
		sm = new SystemMessage(SystemMessage.S1_PETITION_ON_WAITING_LIST);
		sm.addNumber(PetitionManager.getInstance().getPendingPetitionCount());
		activeChar.sendPacket(sm);
		sm = null;
	}
}
