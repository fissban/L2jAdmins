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
 * Ai que se encarga de manejar los teleports para entrar a las necropolis
 * @author fissban, zarie
 */
public class NecropolisesEnter extends Script
{
	// NPC
	private static final Map<Integer, LocationHolder> NPCS = new HashMap<>();
	{
		// NpcId - teleport loc
		NPCS.put(8095, new LocationHolder(-41570, 209785, -5089));
		NPCS.put(8096, new LocationHolder(45281, 123691, -5410));
		NPCS.put(8097, new LocationHolder(111273, 174015, -5417));
		NPCS.put(8098, new LocationHolder(-21726, 77385, -5177));
		NPCS.put(8099, new LocationHolder(-52254, 79103, -4743));
		NPCS.put(8100, new LocationHolder(118308, 132797, -4833));
		NPCS.put(8101, new LocationHolder(83000, 209213, -5443));
		NPCS.put(8102, new LocationHolder(172251, -17605, -4903));
	}
	// Html
	private static final String HTML_PATH = "data/html/teleporter/sevensigns/necropolises/";
	
	public NecropolisesEnter()
	{
		super(-1, "ai/npc/teleports");
		
		for (int npcId : NPCS.keySet())
		{
			addStartNpc(npcId);
			addFirstTalkId(npcId);
			addTalkId(npcId);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		CabalType sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
		// int sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SevenSignsManager.SEAL_GNOSIS);
		CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
		boolean isSealValidationPeriod = SevenSignsManager.getInstance().isSealValidationPeriod();
		CabalType compWinner = SevenSignsManager.getInstance().getCabalHighestScore();
		
		if (isSealValidationPeriod)
		{
			if ((playerCabal != compWinner) || (sealAvariceOwner != compWinner))
			{
				switch (compWinner)
				{
					case DAWN:
						player.sendPacket(SystemMessage.CAN_BE_USED_BY_DAWN);
						return HTML_PATH + "necro_no_enter.htm";
					case DUSK:
						player.sendPacket(SystemMessage.CAN_BE_USED_BY_DUSK);
						return HTML_PATH + "necro_no_enter.htm";
					case NULL:
						return HTML_PATH + "necro_si_enter.htm";
				}
			}
			
			return HTML_PATH + "necro_si_enter.htm";
		}
		
		if (playerCabal == CabalType.NULL)
		{
			return HTML_PATH + "necro_no_enter.htm";
		}
		
		return HTML_PATH + "necro_si_enter.htm";
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
