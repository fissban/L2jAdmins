package l2j.gameserver.scripts.ai.npc.teleports.sevensigns;

import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.instancemanager.sevensigns.enums.CabalType;
import l2j.gameserver.instancemanager.sevensigns.enums.SealType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;

/**
 * Ai que se encarga de manejar el teleport del seven signs para entrar/salir a Anakim/Lilith
 * @author fissban
 */
public class Spirit extends Script
{
	private static final int[] NPCS =
	{
		8111,
		8112,
	};
	// Locs
	private static final LocationHolder ENTER_ANAKIM = new LocationHolder(184397, -11957, -5498);
	private static final LocationHolder ENTER_LILITH = new LocationHolder(185551, -9298, -5498);
	private static final LocationHolder EXIT = new LocationHolder(183225, -11911, -4897);
	// Html
	private static final String HTML_PATH = "data/html/teleporter/sevensigns/spirit/";
	
	public Spirit()
	{
		super(-1, "ai/npc/teleports/sevensigns/");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		CabalType sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
		// int sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
		CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player);
		// boolean isSealValidationPeriod = SevenSignsManager.getInstance().isSealValidationPeriod();
		CabalType compWinner = SevenSignsManager.getInstance().getCabalHighestScore();
		
		switch (npc.getId())
		{
			case 8111:
				if ((playerCabal == sealAvariceOwner) && (playerCabal == compWinner))
				{
					switch (sealAvariceOwner)
					{
						case DAWN:
							return HTML_PATH + "spirit_dawn.htm";
						case DUSK:
							return HTML_PATH + "spirit_dusk.htm";
						case NULL:
							return HTML_PATH + "spirit_null.htm";
					}
				}
				else
				{
					return HTML_PATH + "spirit_null.htm";
				}
				break;
			case 8112:
				return HTML_PATH + "spirit_exit.htm";
		}
		
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "enterAnakim":
				player.teleToLocation(ENTER_ANAKIM, false);
				break;
			
			case "enterLilith":
				player.teleToLocation(ENTER_LILITH, false);
				break;
			
			case "exit":
				player.teleToLocation(EXIT, false);
				break;
		}
		
		return null;
	}
}
