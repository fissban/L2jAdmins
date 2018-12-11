package l2j.gameserver.handler.say;

import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.instancemanager.PetitionManager;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SayPetition implements ISayHandler
{
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.PETITION_GM,
			SayType.PETITION_PLAYER
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		if (!PetitionManager.getInstance().isPlayerInConsultation(activeChar))
		{
			activeChar.sendPacket(SystemMessage.YOU_ARE_NOT_IN_PETITION_CHAT);
			return;
		}
		
		PetitionManager.getInstance().sendActivePetitionMessage(activeChar, text);
	}
}
