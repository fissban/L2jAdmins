package l2j.gameserver.handler.say;

import l2j.Config;
import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SayTell implements ISayHandler
{
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.TELL
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		var receiver = L2World.getInstance().getPlayer(target);
		
		if ((receiver == null))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ONLINE).addString(target));
			return;
		}
		
		if (PcBlockList.isBlocked(receiver, activeChar))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addString(target));
			return;
		}
		
		if (Config.JAIL_DISABLE_CHAT && receiver.isInJail())
		{
			activeChar.sendMessage("Player is in jail.");
			return;
		}
		
		if (receiver.isChatBanned())
		{
			activeChar.sendMessage("Player is chat-banned.");
			return;
		}
		
		if (receiver.isInRefusalMode())
		{
			activeChar.sendPacket(SystemMessage.THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			return;
		}
		
		receiver.sendPacket(new CreatureSay(activeChar, type, activeChar.getName(), text));
		activeChar.sendPacket(new CreatureSay(activeChar, type, "->" + receiver.getName(), text));
		
	}
}
