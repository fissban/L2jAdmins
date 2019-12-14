package l2j.gameserver.scripts.ai.npc.building.castle;

import l2j.gameserver.instancemanager.CastleManorManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.model.actor.manager.pc.clan.enums.ClanPrivilegesType;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class CastleBlacksmith extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		8061, // Gludio
		8062, // Dion
		8063, // Giran
		8064, // Oren
		8065, // Aden
		8066, // Innadril
		8753,// Goddard
	};
	
	// html
	private static final String HTML_PATH = "data/html/castle/blacksmith/";
	
	public CastleBlacksmith()
	{
		super(-1, "ai/npc/castle");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (CastleManorManager.getInstance().isDisabled())
		{
			return HTML_PATH + "data/html/npcdefault.htm";
		}
		
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
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("manufacture"))
		{
			return HTML_PATH + npc.getId() + ".htm";
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	private static ConditionInteractNpcType validateCondition(L2Npc npc, L2PcInstance player)
	{
		if ((npc.getCastle() != null) && (player.getClan() != null))
		{
			if (npc.getCastle().getSiege().isInProgress())
			{
				return ConditionInteractNpcType.BUSY_BECAUSE_OF_SIEGE;
			}
			
			if ((npc.getCastle().getOwnerId() == player.getClanId()) && (player.hasClanPrivilege(ClanPrivilegesType.CS_OTHER_RIGHTS)))
			{
				return ConditionInteractNpcType.CASTLE_OWNER;
			}
		}
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
