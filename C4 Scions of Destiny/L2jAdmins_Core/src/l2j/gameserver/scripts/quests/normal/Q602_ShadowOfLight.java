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
public class Q602_ShadowOfLight extends Script
{
	private static final int EYE_OF_DARKNESS = 7189;
	
	private static final int[][] REWARDS =
	{
		{
			6699,
			40000,
			120000,
			20000,
			20
		},
		{
			6698,
			60000,
			110000,
			15000,
			40
		},
		{
			6700,
			40000,
			150000,
			10000,
			50
		},
		{
			0,
			100000,
			140000,
			11250,
			100
		}
	};
	
	public Q602_ShadowOfLight()
	{
		super(602, "Shadow of Light");
		
		registerItems(EYE_OF_DARKNESS);
		
		addStartNpc(8683); // Eye of Argos
		addTalkId(8683);
		
		addKillId(1299, 1304);
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
		
		if (event.equalsIgnoreCase("8683-02.htm"))
		{
			if (player.getLevel() < 68)
			{
				htmltext = "8683-02a.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("8683-05.htm"))
		{
			st.takeItems(EYE_OF_DARKNESS, -1);
			
			final int random = Rnd.get(100);
			for (int[] element : REWARDS)
			{
				if (random < element[4])
				{
					st.rewardItems(57, element[1]);
					
					if (element[0] != 0)
					{
						st.giveItems(element[0], 3);
					}
					
					st.rewardExpAndSp(element[2], element[3]);
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
				int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8683-03.htm";
				}
				else if (cond == 2)
				{
					htmltext = "8683-04.htm";
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
		
		if (st.dropItems(EYE_OF_DARKNESS, 1, 100, (npc.getId() == 21299) ? 450000 : 500000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
