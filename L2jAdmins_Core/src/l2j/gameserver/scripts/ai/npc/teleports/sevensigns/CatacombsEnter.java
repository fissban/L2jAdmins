package l2j.gameserver.scripts.ai.npc.teleports.sevensigns;

import java.util.HashMap;
import java.util.Map;

import l2j.Config;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;

/**
 * Ai que se encarga de manejar los teleports para entrar a las catacombs
 * @author fissban
 */
public class CatacombsEnter extends Script
{
	// NPC
	private static final Map<Integer, LocationHolder> NPCS = new HashMap<>();
	{
		NPCS.put(8114, new LocationHolder(43050, 143933, -5383));
		NPCS.put(8115, new LocationHolder(46217, 170290, -4983));
		NPCS.put(8116, new LocationHolder(78006, 78402, -5122));
		NPCS.put(8117, new LocationHolder(140404, 79678, -5431));
		NPCS.put(8118, new LocationHolder(-19500, 13508, -4905));
		NPCS.put(8119, new LocationHolder(113865, 84543, -6545));
	}
	// Html
	private static final String HTML_PATH = "data/html/teleporter/sevensigns/catacombs/";
	
	public CatacombsEnter()
	{
		super(-1, "ai/npc/teleports");
		
		for (int npcId : NPCS.keySet())
		{
			addStartNpc(npcId);
			addTalkId(npcId);
			addFirstTalkId(npcId);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		// int sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SevenSignsManager.SEAL_AVARICE);
		CabalType sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
		CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
		boolean isSealValidationPeriod = SevenSignsManager.getInstance().isSealValidationPeriod();
		CabalType compWinner = SevenSignsManager.getInstance().getCabalHighestScore();
		
		if (isSealValidationPeriod)
		{
			if ((playerCabal != compWinner) || (sealGnosisOwner != compWinner))
			{
				switch (compWinner)
				{
					case DAWN:
						player.sendPacket(SystemMessage.CAN_BE_USED_BY_DAWN);
						return HTML_PATH + "cata_no_enter.htm";
					case DUSK:
						player.sendPacket(SystemMessage.CAN_BE_USED_BY_DUSK);
						return HTML_PATH + "cata_no_enter.htm";
					case NULL:
						return HTML_PATH + "cata_si_enter.htm";
				}
			}
			
			return HTML_PATH + "cata_si_enter.htm";
			
		}
		if (playerCabal == CabalType.NULL)
		{
			return HTML_PATH + "cata_no_enter.htm";
		}
		
		return HTML_PATH + "cata_si_enter.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((player.getKarma() > 0) && !Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK)
		{
			player.sendMessage("Go away, you're not welcome here.");
			return null;
		}
		
		if (event.equals("teleport"))
		{
			final LocationHolder loc = NPCS.get(npc.getId());
			player.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), true);
		}
		
		return null;
	}
	
}
