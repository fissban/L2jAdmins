package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q371_ShriekOfGhosts extends Script
{
	// NPCs
	private static final int REVA = 7867;
	private static final int PATRIN = 7929;
	
	// Item
	private static final int URN = 5903;
	private static final int PORCELAIN = 6002;
	
	// Drop chances
	private static final Map<Integer, int[]> CHANCES = new HashMap<>();
	{
		CHANCES.put(818, new int[]
		{
			38,
			43
		});
		CHANCES.put(820, new int[]
		{
			48,
			56
		});
		CHANCES.put(824, new int[]
		{
			50,
			58
		});
	}
	
	public Q371_ShriekOfGhosts()
	{
		super(371, "Shriek of Ghosts");
		
		registerItems(URN, PORCELAIN);
		
		addStartNpc(REVA);
		addTalkId(REVA, PATRIN);
		
		addKillId(818, 820, 824);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7867-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7867-07.htm"))
		{
			int urns = st.getItemsCount(URN);
			if (urns > 0)
			{
				st.takeItems(URN, urns);
				if (urns >= 100)
				{
					urns += 13;
					htmltext = "7867-08.htm";
				}
				else
				{
					urns += 7;
				}
				st.rewardItems(Inventory.ADENA_ID, urns * 1000);
			}
		}
		else if (event.equalsIgnoreCase("7867-10.htm"))
		{
			st.playSound(PlaySoundType.QUEST_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("APPR"))
		{
			if (st.hasItems(PORCELAIN))
			{
				final int chance = Rnd.get(100);
				
				st.takeItems(PORCELAIN, 1);
				
				if (chance < 2)
				{
					st.giveItems(6003, 1);
					htmltext = "7929-03.htm";
				}
				else if (chance < 32)
				{
					st.giveItems(6004, 1);
					htmltext = "7929-04.htm";
				}
				else if (chance < 62)
				{
					st.giveItems(6005, 1);
					htmltext = "7929-05.htm";
				}
				else if (chance < 77)
				{
					st.giveItems(6006, 1);
					htmltext = "7929-06.htm";
				}
				else
				{
					htmltext = "7929-07.htm";
				}
			}
			else
			{
				htmltext = "7929-02.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 59 ? "7867-01.htm" : "7867-02.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case REVA:
						if (st.hasItems(URN))
						{
							htmltext = st.hasItems(PORCELAIN) ? "7867-05.htm" : "7867-04.htm";
						}
						else
						{
							htmltext = "7867-06.htm";
						}
						break;
					
					case PATRIN:
						htmltext = "7929-01.htm";
						break;
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
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		final int[] chances = CHANCES.get(npc.getId());
		final int random = Rnd.get(100);
		
		if (random < chances[1])
		{
			st.dropItemsAlways(random < chances[0] ? URN : PORCELAIN, 1, 0);
		}
		
		return null;
	}
	
}
