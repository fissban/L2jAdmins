package l2j.gameserver.scripts.ai.npc.building.castle;

import l2j.gameserver.data.NpcData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.model.actor.manager.character.itemcontainer.warehouse.enums.WareHouseType;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPrivilegesType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.WareHouseDepositList;
import l2j.gameserver.network.external.server.WareHouseWithdrawalList;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class CastleWarehouse extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		8068, // Gludio
		8069, // Dion
		8070, // Giran
		8071, // Oren
		8072, // Aden
		8073, // Innadril
		8754,// Goddard
	};
	// html
	private static final String HTML_PATH = "data/html/castle/warehouse/";
	
	public CastleWarehouse()
	{
		super(-1, "ai/npc/castle");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
		
		for (int npcId : NPCS)
		{
			NpcData.getInstance().getTemplate(npcId).setWarehouse(true);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
				return HTML_PATH + "no.htm";
			
			case BUSY_BECAUSE_OF_SIEGE:
				return HTML_PATH + "busy.htm";
			
			case CASTLE_OWNER:
				switch (event)
				{
					case "WithdrawP":
						player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.PRIVATE));
						break;
					
					case "DepositP":
						player.sendPacket(new WareHouseDepositList(player, WareHouseType.PRIVATE));
						break;
					
					case "WithdrawC":
						if (!player.hasClanPrivilege(ClanPrivilegesType.CL_VIEW_WAREHOUSE))
						{
							player.sendPacket(SystemMessage.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
							return null;
						}
						
						if (player.getClan().getLevel() == 0)
						{
							player.sendPacket(SystemMessage.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
							return null;
						}
						
						player.sendPacket(new WareHouseWithdrawalList(player, WareHouseType.CLAN));
						break;
					
					case "DepositC":
						if (player.getClan() != null)
						{
							if (player.getClan().getLevel() == 0)
							{
								player.sendPacket(SystemMessage.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
								return null;
							}
							
							player.sendPacket(new WareHouseDepositList(player, WareHouseType.CLAN));
						}
						break;
				}
		}
		
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (validateCondition(npc, player))
		{
			case ALL_FALSE:
				return HTML_PATH + "no.htm";
			
			case BUSY_BECAUSE_OF_SIEGE:
				return HTML_PATH + "busy.htm";
			
			case CASTLE_OWNER:
				return HTML_PATH + "index.htm";
		}
		
		return null;
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if ((npc.getCastle() != null) && (player.getClan() != null))
		{
			if (npc.getCastle().getSiege().isInProgress())
			{
				return ConditionInteractNpcType.BUSY_BECAUSE_OF_SIEGE;
			}
			
			if (npc.getCastle().getOwnerId() == player.getClanId())
			{
				return ConditionInteractNpcType.CASTLE_OWNER;
			}
		}
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
