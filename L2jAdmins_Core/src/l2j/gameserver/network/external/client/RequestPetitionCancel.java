package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.GmListData;
import l2j.gameserver.instancemanager.PetitionManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * <p>
 * Format: (c) d
 * <ul>
 * <li>d: Unknown</li>
 * </ul>
 * </p>
 * @author -Wooden-, TempyIncursion
 */
public class RequestPetitionCancel extends AClientPacket
{
	@Override
	protected void readImpl()
	{
		// unknown = readD(); This is pretty much a trigger packet.
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (PetitionManager.getInstance().isPlayerInConsultation(activeChar))
		{
			if (activeChar.isGM())
			{
				PetitionManager.getInstance().endActivePetition(activeChar);
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.PETITION_UNDER_PROCESS));
			}
		}
		else
		{
			if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar))
			{
				if (PetitionManager.getInstance().cancelActivePetition(activeChar))
				{
					int numRemaining = Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar);
					
					SystemMessage sm = new SystemMessage(SystemMessage.PETITION_CANCELED_SUBMIT_S1_MORE_TODAY);
					sm.addString(String.valueOf(numRemaining));
					activeChar.sendPacket(sm);
					sm = null;
					
					// Notify all GMs that the player's pending petition has been cancelled.
					String msgContent = activeChar.getName() + " has canceled a pending petition.";
					GmListData.getInstance().broadcastToGMs(new CreatureSay(activeChar, SayType.HERO_VOICE, "Petition System", msgContent));
				}
				else
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.FAILED_CANCEL_PETITION_TRY_LATER));
				}
			}
			else
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.PETITION_NOT_SUBMITTED));
			}
		}
	}
}
