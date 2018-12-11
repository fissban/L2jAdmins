package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        CaFi
 * @originalQuest aCis & python
 */
public class Q617_GatherTheFlames extends Script
{
	// NPCs
	private static final int VULCAN = 8539;
	
	// Items
	private static final int TORCH = 7264;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1381, 510000);
		CHANCES.put(1653, 510000);
		CHANCES.put(1387, 530000);
		CHANCES.put(1655, 530000);
		CHANCES.put(1390, 560000);
		CHANCES.put(1656, 690000);
		CHANCES.put(1389, 550000);
		CHANCES.put(1388, 530000);
		CHANCES.put(1383, 510000);
		CHANCES.put(1392, 560000);
		CHANCES.put(1382, 600000);
		CHANCES.put(1654, 520000);
		CHANCES.put(1384, 640000);
		CHANCES.put(1394, 510000);
		CHANCES.put(1395, 560000);
		CHANCES.put(1385, 520000);
		CHANCES.put(1391, 550000);
		CHANCES.put(1393, 580000);
		CHANCES.put(1657, 570000);
		CHANCES.put(1386, 520000);
		CHANCES.put(1652, 490000);
		CHANCES.put(1378, 490000);
		CHANCES.put(1376, 480000);
		CHANCES.put(1377, 480000);
		CHANCES.put(1379, 590000);
		CHANCES.put(1380, 490000);
	}
	
	// Rewards
	private static final int REWARD[] =
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580
	};
	
	// Rewards2
	private static final int REWARD2[] =
	{
		6882,
		6884,
		6886,
		6888,
		6893,
		6894,
		6896,
		6898,
		6900,
		7581
	};
	
	public Q617_GatherTheFlames()
	{
		super(617, "Gather the Flames");
		
		registerItems(TORCH);
		
		addStartNpc(VULCAN);
		addTalkId(VULCAN);
		
		for (int mobs : CHANCES.keySet())
		{
			addKillId(mobs);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8539-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8539-05.htm"))
		{
			if (st.getItemsCount(TORCH) >= 1000)
			{
				htmltext = "8539-07.htm";
				st.takeItems(TORCH, 1000);
				st.giveItems(REWARD2[Rnd.get(REWARD2.length)], 1);
				st.giveItems(REWARD[Rnd.get(REWARD.length)], 1);
			}
		}
		else if (event.equalsIgnoreCase("8539-08.htm"))
		{
			st.takeItems(TORCH, -1);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = npc.getId() + ((player.getLevel() >= 74) ? "-01.htm" : "-02.htm");
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case VULCAN:
						htmltext = (st.getItemsCount(TORCH) >= 1000) ? "8539-04.htm" : "8539-05.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		partyMember.getScriptState(getName()).dropItems(TORCH, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
