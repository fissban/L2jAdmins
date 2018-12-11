package l2j.gameserver.model.actor.instance;

import l2j.Config;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.clan.enums.ClanPrivilegesType;
import l2j.gameserver.model.itemcontainer.inventory.PcFreightManager;
import l2j.gameserver.model.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.network.external.server.PackageToList;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.WareHouseDepositList;
import l2j.gameserver.network.external.server.WareHouseWithdrawalList;

/**
 * This class ...
 * @version $Revision: 1.3.4.10 $ $Date: 2005/04/06 16:13:41 $
 */
public final class L2WarehouseInstance extends L2Npc
{
	/**
	 * @param objectId
	 * @param template
	 */
	public L2WarehouseInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2WarehouseInstance);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/warehouse/" + pom + ".htm";
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		// little check to prevent enchant exploit
		if (player.getActiveEnchantItem() != null)
		{
			LOG.info("Player " + player.getName() + " trying to use enchant exploit, ban this player!");
			player.closeConnection();
			return;
		}
		
		if (command.startsWith("WithdrawP"))
		{
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.PRIVATE));
		}
		else if (command.equals("DepositP"))
		{
			player.sendPacket(new WareHouseDepositList(player, WareHouseType.PRIVATE));
		}
		else if (command.equals("WithdrawC"))
		{
			if (!player.hasClanPrivilege(ClanPrivilegesType.CL_VIEW_WAREHOUSE))
			{
				player.sendPacket(SystemMessage.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
				return;
			}
			if (player.getClan().getLevel() == 0)
			{
				player.sendPacket(SystemMessage.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
				return;
			}
			
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.CLAN));
		}
		else if (command.equals("DepositC"))
		{
			if (player.getClan() != null)
			{
				if (player.getClan().getLevel() == 0)
				{
					player.sendPacket(SystemMessage.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
					return;
				}
				
				player.sendPacket(new WareHouseDepositList(player, WareHouseType.CLAN));
			}
		}
		else if (command.startsWith("WithdrawF"))
		{
			if (Config.ALLOW_FREIGHT)
			{
				PcFreightManager freight = player.getFreight();
				
				if (freight != null)
				{
					if (freight.getSize() > 0)
					{
						if (!Config.ALT_GAME_FREIGHTS)
						{
							int region = 1 + MapRegionData.getInstance().getClosestTownNumber(player);
							freight.setActiveLocation(region);
						}
						
						if (freight.getAvailablePackages())
						{
							player.sendPacket(SystemMessage.PACKAGE_IN_ANOTHER_WAREHOUSE);
							return;
						}
						
						player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.FREIGHT));
					}
					else
					{
						player.sendPacket(SystemMessage.NO_PACKAGES_ARRIVED);
					}
				}
			}
		}
		else if (command.startsWith("DepositF"))
		{
			if (Config.ALLOW_FREIGHT)
			{
				// No other chars in the account of this player
				if (player.getAccountChars().isEmpty())
				{
					player.sendPacket(SystemMessage.CHARACTER_DOES_NOT_EXIST);
				}
				else
				{
					player.sendPacket(new PackageToList(player.getAccountChars()));
					
					if (Config.DEBUG)
					{
						LOG.fine("Showing destination chars to freight - char src: " + player.getName());
					}
				}
			}
		}
		else
		{
			// this class dont know any other commands, let forward
			// the command to the parent class
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public boolean isWarehouse()
	{
		return true;
	}
}
