package l2j.gameserver.network.external.client;

import l2j.gameserver.data.HennaData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class RequestHennaEquip extends AClientPacket
{
	private int symbolId;
	
	@Override
	protected void readImpl()
	{
		symbolId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		HennaHolder henna = HennaData.getById(symbolId);
		int count = 0;
		
		if (!HennaData.getByClass(activeChar.getClassId().getId()).contains(henna))
		{
			// prevent exploit
			return;
		}
		
		try
		{
			count = activeChar.getInventory().getItemById(henna.getDyeId()).getCount();
		}
		catch (Exception e)
		{
		}
		
		if ((count >= henna.getDyeAmount()) && (activeChar.getInventory().getAdena() >= henna.getPrice()) && activeChar.addHenna(henna))
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1_DISAPPEARED);
			sm.addNumber(henna.getDyeId());
			activeChar.sendPacket(sm);
			
			activeChar.sendPacket(new SystemMessage(SystemMessage.SYMBOL_ADDED));
			
			activeChar.getInventory().reduceAdena("Henna", henna.getPrice(), activeChar, activeChar.getLastTalkNpc());
			activeChar.getInventory().destroyItemByItemId("Henna", henna.getDyeId(), henna.getDyeAmount(), activeChar, activeChar.getLastTalkNpc());
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.CANT_DRAW_SYMBOL));
		}
	}
}
