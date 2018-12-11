package l2j.gameserver.handler.item;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.instancemanager.MercTicketManager;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.entity.castle.Castle;
import l2j.gameserver.model.items.instance.ItemInstance;

public class ItemMercTicket implements IItemHandler
{
	// TODO crear un AI ?
	@SuppressWarnings("unused")
	private static final String[] MESSAGES =
	{
		"To arms!.",
		"I am ready to serve you my lord when the time comes.",
		"You summon me."
	};
	
	@Override
	public int[] getItemIds()
	{
		return MercTicketManager.getInstance().getItemIds();
	}
	
	/**
	 * handler for using mercenary tickets. Things to do: 1) Check constraints: 1.a) Tickets may only be used in a castle 1.b) Only specific tickets may be used in each castle (different tickets for each castle) 1.c) only the owner of that castle may use them 1.d) tickets cannot be used during siege
	 * 1.e) Check if max number of tickets has been reached 1.f) Check if max number of tickets from this ticket's TYPE has been reached 2) If allowed, call the MercTicketManager to add the item and spawn in the world 3) Remove the item from the person's inventory
	 */
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		int itemId = item.getId();
		L2PcInstance activeChar = (L2PcInstance) playable;
		Castle castle = CastleData.getInstance().getCastle(activeChar);
		
		if (castle == null)
		{
			activeChar.sendMessage("Mercenary Tickets can only be used in castles.");
			return;
		}
		
		int castleId = castle.getId();
		
		// add check that certain tickets can only be placed in certain castles
		if (MercTicketManager.getInstance().getTicketCastleId(itemId) != castleId)
		{
			String castleName = null;
			switch (MercTicketManager.getInstance().getTicketCastleId(itemId))
			{
				case 1:
					castleName = "Gludio";
					break;
				case 2:
					castleName = "Dion";
					break;
				case 3:
					castleName = "Giran";
					break;
				case 4:
					castleName = "Oren";
					break;
				case 5:
					castleName = "Aden";
					break;
				case 6:
					castleName = "Innadril";
					break;
				case 7:
					castleName = "Goddard";
					break;
			}
			
			activeChar.sendMessage("This Mercenary Ticket can only be used in " + castleName + " castle.");
			return;
			
		}
		
		if (!activeChar.isCastleLord(castleId))
		{
			activeChar.sendMessage("You are not the lord of this castle.");
			return;
		}
		
		if (castle.getSiege().isInProgress())
		{
			activeChar.sendMessage("You cannot hire a mercenary while siege is in progress.");
			return;
		}
		
		if (MercTicketManager.getInstance().isAtCastleLimit(item.getId()))
		{
			activeChar.sendMessage("You cannot hire any more mercenaries.");
			return;
		}
		
		if (MercTicketManager.getInstance().isAtTypeLimit(item.getId()))
		{
			activeChar.sendMessage("You cannot hire any more mercenaries of this type.You may still hire other types of mercenaries.");
			return;
		}
		
		if (MercTicketManager.getInstance().isTooCloseToAnotherTicket(activeChar.getX(), activeChar.getY(), activeChar.getZ()))
		{
			activeChar.sendMessage("The distance between mercenaries is too short.");
			return;
		}
		
		int npcId = MercTicketManager.getInstance().addTicket(item.getId(), activeChar);
		activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false); // Remove item from char's inventory
		activeChar.sendMessage("Hired mercenary (" + itemId + "," + npcId + ") at coords:" + activeChar.getX() + "," + activeChar.getY() + "," + activeChar.getZ() + " heading:" + activeChar.getHeading());
		
	}
}
