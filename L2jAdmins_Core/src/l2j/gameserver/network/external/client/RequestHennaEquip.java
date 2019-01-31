package l2j.gameserver.network.external.client;

import l2j.gameserver.data.HennaData;
import l2j.gameserver.data.HennaTreeData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.HennaHolder;
import l2j.gameserver.model.items.ItemHenna;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision$ $Date$
 */
public class RequestHennaEquip extends AClientPacket
{
	private int SymbolId;
	
	@Override
	protected void readImpl()
	{
		SymbolId = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		ItemHenna template = HennaData.getInstance().getTemplate(SymbolId);
		
		if (template == null)
		{
			return;
		}
		
		HennaHolder temp = new HennaHolder(template);
		int count = 0;
		
		/*
		 * Prevents henna drawing exploit: 1) talk to L2SymbolMakerInstance 2) RequestHennaList 3) Don't close the window and go to a GrandMaster and change your subclass 4) Get SymbolMaker range again and press draw You could draw any kind of henna just having the required subclass...
		 */
		boolean cheater = true;
		for (HennaHolder h : HennaTreeData.getInstance().getAvailableHenna(activeChar.getClassId()))
		{
			if (h.getSymbolId() == temp.getSymbolId())
			{
				cheater = false;
				break;
			}
		}
		
		try
		{
			count = activeChar.getInventory().getItemById(temp.getItemIdDye()).getCount();
		}
		catch (Exception e)
		{
		}
		
		if (!cheater && (count >= temp.getAmountDyeRequire()) && (activeChar.getInventory().getAdena() >= temp.getPrice()) && activeChar.addHenna(temp))
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1_DISAPPEARED);
			sm.addNumber(temp.getItemIdDye());
			activeChar.sendPacket(sm);
			
			sm = null;
			activeChar.sendPacket(new SystemMessage(SystemMessage.SYMBOL_ADDED));
			
			activeChar.getInventory().reduceAdena("Henna", temp.getPrice(), activeChar, activeChar.getLastTalkNpc());
			activeChar.getInventory().destroyItemByItemId("Henna", temp.getItemIdDye(), temp.getAmountDyeRequire(), activeChar, activeChar.getLastTalkNpc());
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.CANT_DRAW_SYMBOL));
			if ((!activeChar.isGM()) && (cheater))
			{
				IllegalAction.report(activeChar, "Exploit attempt: Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to add a forbidden henna.");
			}
		}
	}
}
