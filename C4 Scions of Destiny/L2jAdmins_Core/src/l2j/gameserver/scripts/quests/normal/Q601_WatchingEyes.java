package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q601_WatchingEyes extends Script
{
	// Items
	private static final int PROOF_OF_AVENGER = 7188;
	
	// Rewards
	private static final int[][] REWARDS =
	{
		{
			6699,
			90000,
			20
		},
		{
			6698,
			80000,
			40
		},
		{
			6700,
			40000,
			50
		},
		{
			0,
			230000,
			100
		}
	};
	
	public Q601_WatchingEyes()
	{
		super(601, "Watching Eyes");
		
		registerItems(PROOF_OF_AVENGER);
		
		addStartNpc(8683); // Eye of Argos
		addTalkId(8683);
		
		addKillId(1306, 1308, 1308, 1310, 1310);
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
		
		if (event.equalsIgnoreCase("8683-03.htm"))
		{
			if (player.getLevel() < 71)
			{
				htmltext = "8683-02.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("8683-07.htm"))
		{
			st.takeItems(PROOF_OF_AVENGER, -1);
			
			final int random = Rnd.get(100);
			for (int[] element : REWARDS)
			{
				if (random < element[2])
				{
					st.rewardItems(57, element[1]);
					
					if (element[0] != 0)
					{
						st.giveItems(element[0], 5);
						st.rewardExpAndSp(120000, 10000);
					}
					break;
				}
			}
			st.playSound(PlaySoundType.QUEST_FINISH);
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
				htmltext = "8683-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = (st.hasItems(PROOF_OF_AVENGER)) ? "8683-05.htm" : "8683-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "8683-06.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "cond", "1");
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		if (st.dropItems(PROOF_OF_AVENGER, 1, 100, 500000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
