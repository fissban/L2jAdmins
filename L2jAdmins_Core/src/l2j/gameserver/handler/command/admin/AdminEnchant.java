package l2j.gameserver.handler.command.admin;

import java.util.StringTokenizer;

import l2j.gameserver.handler.CommandAdminHandler.IAdminCommandHandler;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.CharInfo;
import l2j.gameserver.network.external.server.InventoryUpdate;
import l2j.gameserver.network.external.server.UserInfo;

/**
 * This class handles following admin commands: - enchant_armor
 */
public class AdminEnchant implements IAdminCommandHandler
{
	private static String[] ADMINCOMMAND =
	{
		"admin_seteh", // 6
		"admin_setec", // 10
		"admin_seteg", // 9
		"admin_setel", // 11
		"admin_seteb", // 12
		"admin_setew", // 7
		"admin_setes", // 8
		"admin_setle", // 1
		"admin_setre", // 2
		"admin_setlf", // 4
		"admin_setrf", // 5
		"admin_seten", // 3
		"admin_setun", // 0
		"admin_setba",// 13
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String event = st.nextToken();// actual command
		
		ParpedollType armorType = null;
		
		if (event.equals("admin_seteh"))
		{
			armorType = ParpedollType.HEAD;
		}
		else if (event.equals("admin_setec"))
		{
			armorType = ParpedollType.CHEST;
		}
		else if (event.equals("admin_seteg"))
		{
			armorType = ParpedollType.GLOVES;
		}
		else if (event.equals("admin_seteb"))
		{
			armorType = ParpedollType.FEET;
		}
		else if (event.equals("admin_setel"))
		{
			armorType = ParpedollType.LEGS;
		}
		else if (event.equals("admin_setew"))
		{
			armorType = ParpedollType.RHAND;
		}
		else if (event.equals("admin_setes"))
		{
			armorType = ParpedollType.LHAND;
		}
		else if (event.equals("admin_setle"))
		{
			armorType = ParpedollType.LEAR;
		}
		else if (event.equals("admin_setre"))
		{
			armorType = ParpedollType.REAR;
		}
		else if (event.equals("admin_setlf"))
		{
			armorType = ParpedollType.LFINGER;
		}
		else if (event.equals("admin_setrf"))
		{
			armorType = ParpedollType.RFINGER;
		}
		else if (event.equals("admin_seten"))
		{
			armorType = ParpedollType.NECK;
		}
		else if (event.equals("admin_setun"))
		{
			armorType = ParpedollType.UNDER;
		}
		else if (event.equals("admin_setba"))
		{
			armorType = ParpedollType.BACK;
		}
		
		if (armorType != null)
		{
			try
			{
				int ench = Integer.parseInt(st.nextToken());
				
				// check value
				if ((ench < 0) || (ench > 65535))
				{
					activeChar.sendMessage("You must set the enchant level to be between 0-65535.");
				}
				else
				{
					setEnchant(activeChar, ench, armorType);
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Please specify a valid new enchant value(0-65535).");
			}
		}
		
		// show the enchant menu after an action
		AdminHelpPage.showHelpPage(activeChar, "menuEnchant.htm");
		
		return true;
	}
	
	private void setEnchant(L2PcInstance activeChar, int ench, ParpedollType armorType)
	{
		L2PcInstance player = AdminHelpTarget.getPlayer(activeChar);
		
		if (player == null)
		{
			return;
		}
		
		// now we need to find the equipped weapon of the targeted character...
		int curEnchant = 0; // display purposes only
		ItemInstance itemInstance = null;
		
		// only attempt to enchant if there is a weapon equipped
		ItemInstance parmorInstance = player.getInventory().getPaperdollItem(armorType);
		if ((parmorInstance != null) && (parmorInstance.getEquipSlot() == armorType))
		{
			itemInstance = parmorInstance;
		}
		else
		{
			// for bows and double handed weapons
			parmorInstance = player.getInventory().getPaperdollItem(ParpedollType.LRHAND);
			if ((parmorInstance != null) && (parmorInstance.getEquipSlot() == ParpedollType.LRHAND))
			{
				itemInstance = parmorInstance;
			}
		}
		
		if (itemInstance != null)
		{
			curEnchant = itemInstance.getEnchantLevel();
			
			// set enchant value
			player.getInventory().unEquipItemInSlotAndRecord(armorType);
			itemInstance.setEnchantLevel(ench);
			player.getInventory().equipItemAndRecord(itemInstance);
			
			// send packets
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(itemInstance);
			player.sendPacket(iu);
			player.broadcastPacket(new CharInfo(player));
			player.sendPacket(new UserInfo(player));
			
			// informations
			activeChar.sendMessage("Changed enchantment of " + player.getName() + "'s " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
			player.sendMessage("Admin has changed the enchantment of your " + itemInstance.getItem().getName() + " from " + curEnchant + " to " + ench + ".");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMINCOMMAND;
	}
}
