package l2j.gameserver.handler.item;

import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.MagicSkillUse;

/**
 * Itemhhandler for Character Appearance Change Potions
 * @author Tempy
 */
public class ItemCharChangePotions implements IItemHandler
{
	@Override
	public int[] getItemIds()
	{
		return new int[]
		{
			5235,
			5236,
			5237, // Face
			5238,
			5239,
			5240,
			5241, // Hair Color
			5242,
			5243,
			5244,
			5245,
			5246,
			5247,
			5248, // Hair Style
		};
	}
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item)
	{
		int itemId = item.getId();
		
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (activeChar.isAllSkillsDisabled())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		switch (itemId)
		{
			case 5235:
				activeChar.setFace(0);
				break;
			case 5236:
				activeChar.setFace(1);
				break;
			case 5237:
				activeChar.setFace(2);
				break;
			case 5238:
				activeChar.setHairColor(0);
				break;
			case 5239:
				activeChar.setHairColor(1);
				break;
			case 5240:
				activeChar.setHairColor(2);
				break;
			case 5241:
				activeChar.setHairColor(3);
				break;
			case 5242:
				activeChar.setHairStyle(0);
				break;
			case 5243:
				activeChar.setHairStyle(1);
				break;
			case 5244:
				activeChar.setHairStyle(2);
				break;
			case 5245:
				activeChar.setHairStyle(3);
				break;
			case 5246:
				activeChar.setHairStyle(4);
				break;
			case 5247:
				activeChar.setHairStyle(5);
				break;
			case 5248:
				activeChar.setHairStyle(6);
				break;
		}
		
		// Create a summon effect
		activeChar.broadcastPacket(new MagicSkillUse(playable, activeChar, 2003, 1, 1, 0));
		
		// Update the changed stat for the character in the DB.
		activeChar.store();
		
		// Remove the item from inventory.
		activeChar.getInventory().destroyItem("Consume", item.getObjectId(), 1, null, false);
		
		// Broadcast the changes to the char and all those nearby.
		activeChar.broadcastUserInfo();
	}
}
