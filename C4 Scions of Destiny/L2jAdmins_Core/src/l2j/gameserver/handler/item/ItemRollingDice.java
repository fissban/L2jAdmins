package l2j.gameserver.handler.item;

import l2j.gameserver.floodprotector.FloodProtector;
import l2j.gameserver.floodprotector.enums.FloodProtectorType;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.Dice;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.util.Broadcast;
import l2j.util.Rnd;

/**
 * This class ...
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:30:07 $
 */

public class ItemRollingDice implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			4625,
			4626,
			4627,
			4628
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessage.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		if (!FloodProtector.getInstance().tryPerformAction(activeChar, FloodProtectorType.ROLLDICE))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER));
			return;
		}
		
		int number = Rnd.get(1, 6);
		
		Broadcast.toSelfAndKnownPlayers(activeChar, new Dice(activeChar, item.getId(), number, activeChar.getX() - 30, activeChar.getY() - 30, activeChar.getZ()));
		
		SystemMessage sm = new SystemMessage(SystemMessage.C1_ROLLED_S2).addString(activeChar.getName()).addNumber(number);
		activeChar.sendPacket(sm);
		
		if (activeChar.isInsideZone(ZoneType.PEACE))
		{
			Broadcast.toKnownPlayers(activeChar, sm);
		}
		else if (activeChar.isInParty())
		{
			activeChar.getParty().broadcastToPartyMembers(activeChar, sm);
		}
	}
}
