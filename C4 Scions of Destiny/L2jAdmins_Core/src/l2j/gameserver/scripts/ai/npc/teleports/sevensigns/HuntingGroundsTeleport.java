package l2j.gameserver.scripts.ai.npc.teleports.sevensigns;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Ai que se encarga de manejar los teleports hunting ground de seven signs por los priest
 * @author mauronob
 */
public class HuntingGroundsTeleport extends Script
{
	private static final String HTML_PATH = "data/html/teleporter/sevensigns/huntingGrounds/";
	
	private final static int[] PRIESTS =
	{
		8078,
		8079,
		8080,
		8081,
		8082,
		8083,
		8084,
		8085,
		8086,
		8087,
		8088,
		8089,
		8090,
		8091,
		8168,
		8169,
		8692,
		8693,
		8694,
		8695
	};
	
	public HuntingGroundsTeleport()
	{
		super(-1, "ai/npc/teleports");
		addStartNpc(PRIESTS);
		addTalkId(PRIESTS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		
		String htmltext = "";
		
		switch (npc.getId())
		{
			case 8078:
			case 8085:
				htmltext = HTML_PATH + "gludin.htm";
				break;
			
			case 8079:
			case 8086:
				htmltext = HTML_PATH + "gludio.htm";
				break;
			
			case 8080:
			case 8087:
				htmltext = HTML_PATH + "dion.htm";
				break;
			
			case 8081:
			case 8088:
				htmltext = HTML_PATH + "giran.htm";
				break;
			
			case 8082:
			case 8089:
				htmltext = HTML_PATH + "heine.htm";
				break;
			
			case 8083:
			case 8090:
				htmltext = HTML_PATH + "oren.htm";
				break;
			
			case 8084:
			case 8091:
				htmltext = HTML_PATH + "aden.htm";
				break;
			
			case 8168:
			case 8169:
				htmltext = HTML_PATH + "hunters.htm";
				break;
			
			case 8692:
			case 8693:
				htmltext = HTML_PATH + "goddard.htm";
				break;
			
			case 8694:
			case 8695:
				htmltext = HTML_PATH + "rune.htm";
				break;
			
		}
		return htmltext;
	}
	
}
