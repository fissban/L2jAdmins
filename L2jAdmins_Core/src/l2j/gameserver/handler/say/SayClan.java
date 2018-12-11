package l2j.gameserver.handler.say;

import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * @author fissban
 */
public class SayClan implements ISayHandler
{
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.CLAN
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		if (activeChar.getClan() != null)
		{
			activeChar.getClan().broadcastToOnlineMembers(new CreatureSay(activeChar, type, activeChar.getName(), text));
		}
	}
}
