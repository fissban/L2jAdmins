package l2j.gameserver.handler.say;

import l2j.gameserver.handler.SayHandler.ISayHandler;
import l2j.gameserver.model.PcBlockList;
import l2j.gameserver.model.actor.enums.FloodProtectorType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * @author fissban
 */
public class SayHeroe implements ISayHandler
{
	@Override
	public SayType[] getSayTypeList()
	{
		return new SayType[]
		{
			SayType.HERO_VOICE
		};
	}
	
	@Override
	public void handleSay(SayType type, L2PcInstance activeChar, String target, String text)
	{
		if (activeChar.isHero())
		{
			if (!activeChar.tryToUseAction(FloodProtectorType.HERO_VOICE))
			{
				activeChar.sendMessage("Do not spam heroe channel.");
				return;
			}
			
			for (L2PcInstance player : L2World.getInstance().getAllPlayers())
			{
				if (!PcBlockList.isBlocked(player, activeChar))
				{
					player.sendPacket(new CreatureSay(activeChar, type, activeChar.getName(), text));
				}
			}
		}
	}
}
