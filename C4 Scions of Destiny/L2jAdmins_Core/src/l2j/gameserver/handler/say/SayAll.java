package l2j.gameserver.handler.say;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandVoicedHandler;
import l2j.gameserver.handler.CommandVoicedHandler.IVoicedCommandHandler;
import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * @author fissban
 */
public class SayAll implements ISayHandler
{
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.ALL
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		if (text.startsWith("."))
		{
			var st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			var command = "";
			if (st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				target = text.substring(command.length() + 2);
				vch = CommandVoicedHandler.getHandler(command);
			}
			else
			{
				command = text.substring(1);
				vch = CommandVoicedHandler.getHandler(command);
			}
			
			if (vch != null)
			{
				vch.useVoicedCommand(command, activeChar, target);
			}
			else
			{
				activeChar.sendMessage(command + " no register");
			}
		}
		else
		{
			for (var player : activeChar.getKnownList().getObjectTypeInRadius(L2PcInstance.class, 1250))
			{
				if ((player != null))
				{
					player.sendPacket(new CreatureSay(activeChar, type, activeChar.getName(), text));
				}
			}
			activeChar.sendPacket(new CreatureSay(activeChar, type, activeChar.getName(), text));
		}
	}
}
