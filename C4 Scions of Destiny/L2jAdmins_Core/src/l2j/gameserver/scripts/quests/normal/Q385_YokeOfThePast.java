package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @originalQuest aCis
 */
public class Q385_YokeOfThePast extends Script
{
	// NPCs
	private static final int GATEKEEPER_ZIGGURAT[] =
	{
		8095,
		8096,
		8097,
		8098,
		8099,
		8100,
		8101,
		8102,
		8103,
		8104,
		8105,
		8106,
		8107,
		8108,
		8109,
		8110,
		8114,
		8115,
		8116,
		8117,
		8118,
		8119,
		8120,
		8121,
		8122,
		8123,
		8124,
		8125,
		8126,
	};
	
	// Item
	private static final int ANCIENT_SCROLL = 5902;
	
	// Reward
	private static final int BLANK_SCROLL = 5965;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1208, 70000);
		CHANCES.put(1209, 80000);
		CHANCES.put(1210, 110000);
		CHANCES.put(1211, 110000);
		CHANCES.put(1213, 140000);
		CHANCES.put(1214, 190000);
		CHANCES.put(1215, 190000);
		CHANCES.put(1217, 240000);
		CHANCES.put(1218, 300000);
		CHANCES.put(1219, 300000);
		CHANCES.put(1221, 370000);
		CHANCES.put(1222, 460000);
		CHANCES.put(1223, 450000);
		CHANCES.put(1224, 500000);
		CHANCES.put(1225, 540000);
		CHANCES.put(1226, 660000);
		CHANCES.put(1227, 640000);
		CHANCES.put(1228, 700000);
		CHANCES.put(1229, 750000);
		CHANCES.put(1230, 910000);
		CHANCES.put(1231, 860000);
		CHANCES.put(1236, 120000);
		CHANCES.put(1237, 140000);
		CHANCES.put(1238, 190000);
		CHANCES.put(1239, 190000);
		CHANCES.put(1240, 220000);
		CHANCES.put(1241, 240000);
		CHANCES.put(1242, 300000);
		CHANCES.put(1243, 300000);
		CHANCES.put(1244, 340000);
		CHANCES.put(1245, 370000);
		CHANCES.put(1246, 460000);
		CHANCES.put(1247, 450000);
		CHANCES.put(1248, 500000);
		CHANCES.put(1249, 540000);
		CHANCES.put(1250, 660000);
		CHANCES.put(1251, 640000);
		CHANCES.put(1252, 700000);
		CHANCES.put(1253, 750000);
		CHANCES.put(1254, 910000);
		CHANCES.put(1255, 860000);
	}
	
	public Q385_YokeOfThePast()
	{
		super(385, "Yoke of the Past");
		
		registerItems(ANCIENT_SCROLL);
		
		addStartNpc(GATEKEEPER_ZIGGURAT);
		addTalkId(GATEKEEPER_ZIGGURAT);
		
		for (final int npcId : CHANCES.keySet())
		{
			addKillId(npcId);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("10.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 20 ? "02.htm" : "01.htm";
				break;
			
			case STARTED:
				if (!st.hasItems(ANCIENT_SCROLL))
				{
					htmltext = "08.htm";
				}
				else
				{
					htmltext = "09.htm";
					final int count = st.getItemsCount(ANCIENT_SCROLL);
					st.takeItems(ANCIENT_SCROLL, -1);
					st.rewardItems(BLANK_SCROLL, count);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		partyMember.getScriptState(getName()).dropItems(ANCIENT_SCROLL, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
