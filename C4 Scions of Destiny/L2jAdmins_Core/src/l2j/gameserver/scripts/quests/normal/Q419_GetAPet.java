package l2j.gameserver.scripts.quests.normal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class Q419_GetAPet extends Script
{
	// Items
	private static final int ANIMAL_LOVER_LIST = 3417;
	private static final int ANIMAL_SLAYER_LIST_1 = 3418;
	private static final int ANIMAL_SLAYER_LIST_2 = 3419;
	private static final int ANIMAL_SLAYER_LIST_3 = 3420;
	private static final int ANIMAL_SLAYER_LIST_4 = 3421;
	private static final int ANIMAL_SLAYER_LIST_5 = 3422;
	private static final int BLOODY_FANG = 3423;
	private static final int BLOODY_CLAW = 3424;
	private static final int BLOODY_NAIL = 3425;
	private static final int BLOODY_KASHA_FANG = 3426;
	private static final int BLOODY_TARANTULA_NAIL = 3427;
	
	// Reward
	private static final int WOLF_COLLAR = 2375;
	
	// NPCs
	private static final int MARTIN = 7731;
	private static final int BELLA = 7256;
	private static final int METTY = 7072;
	private static final int ELLIE = 7091;
	
	// Droplist
	private static final Map<Integer, int[]> DROPLIST = new HashMap<>();
	{
		DROPLIST.put(103, new int[]
		{
			BLOODY_FANG,
			600000
		});
		DROPLIST.put(106, new int[]
		{
			BLOODY_FANG,
			750000
		});
		DROPLIST.put(108, new int[]
		{
			BLOODY_FANG,
			1000000
		});
		DROPLIST.put(460, new int[]
		{
			BLOODY_CLAW,
			600000
		});
		DROPLIST.put(308, new int[]
		{
			BLOODY_CLAW,
			750000
		});
		DROPLIST.put(466, new int[]
		{
			BLOODY_CLAW,
			1000000
		});
		DROPLIST.put(25, new int[]
		{
			BLOODY_NAIL,
			600000
		});
		DROPLIST.put(105, new int[]
		{
			BLOODY_NAIL,
			750000
		});
		DROPLIST.put(34, new int[]
		{
			BLOODY_NAIL,
			1000000
		});
		DROPLIST.put(474, new int[]
		{
			BLOODY_KASHA_FANG,
			600000
		});
		DROPLIST.put(476, new int[]
		{
			BLOODY_KASHA_FANG,
			750000
		});
		DROPLIST.put(478, new int[]
		{
			BLOODY_KASHA_FANG,
			1000000
		});
		DROPLIST.put(403, new int[]
		{
			BLOODY_TARANTULA_NAIL,
			750000
		});
		DROPLIST.put(508, new int[]
		{
			BLOODY_TARANTULA_NAIL,
			1000000
		});
	}
	
	public Q419_GetAPet()
	{
		super(419, "Get a Pet");
		
		registerItems(ANIMAL_LOVER_LIST, ANIMAL_SLAYER_LIST_1, ANIMAL_SLAYER_LIST_2, ANIMAL_SLAYER_LIST_3, ANIMAL_SLAYER_LIST_4, ANIMAL_SLAYER_LIST_5, BLOODY_FANG, BLOODY_CLAW, BLOODY_NAIL, BLOODY_KASHA_FANG, BLOODY_TARANTULA_NAIL);
		
		addStartNpc(MARTIN);
		addTalkId(MARTIN, BELLA, ELLIE, METTY);
		
		for (int npcId : DROPLIST.keySet())
		{
			addKillId(npcId);
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
		
		if (event.equalsIgnoreCase("task"))
		{
			final int race = player.getRace().ordinal();
			
			htmltext = "7731-0" + (race + 4) + ".htm";
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(ANIMAL_SLAYER_LIST_1 + race, 1);
		}
		else if (event.equalsIgnoreCase("7731-12.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ANIMAL_SLAYER_LIST_1, 1);
			st.takeItems(ANIMAL_SLAYER_LIST_2, 1);
			st.takeItems(ANIMAL_SLAYER_LIST_3, 1);
			st.takeItems(ANIMAL_SLAYER_LIST_4, 1);
			st.takeItems(ANIMAL_SLAYER_LIST_5, 1);
			st.takeItems(BLOODY_FANG, -1);
			st.takeItems(BLOODY_CLAW, -1);
			st.takeItems(BLOODY_NAIL, -1);
			st.takeItems(BLOODY_KASHA_FANG, -1);
			st.takeItems(BLOODY_TARANTULA_NAIL, -1);
			st.giveItems(ANIMAL_LOVER_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7256-03.htm"))
		{
			st.set("progress", String.valueOf(st.getInt("progress") | 1));
			if (st.getInt("progress") == 7)
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("7072-02.htm"))
		{
			st.set("progress", String.valueOf(st.getInt("progress") | 2));
			if (st.getInt("progress") == 7)
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("7091-02.htm"))
		{
			st.set("progress", String.valueOf(st.getInt("progress") | 4));
			if (st.getInt("progress") == 7)
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("test"))
		{
			st.set("answers", "0");
			st.set("quiz", "20 21 22 23 24 25 26 27 28 29 30 31 32 33");
			return checkQuestions(st);
		}
		else if (event.equalsIgnoreCase("wrong"))
		{
			st.set("wrong", String.valueOf(st.getInt("wrong") + 1));
			return checkQuestions(st);
		}
		else if (event.equalsIgnoreCase("right"))
		{
			st.set("correct", String.valueOf(st.getInt("correct") + 1));
			return checkQuestions(st);
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
				htmltext = (player.getLevel() < 15) ? "7731-01.htm" : "7731-02.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case MARTIN:
						if (st.hasAtLeastOneItem(ANIMAL_SLAYER_LIST_1, ANIMAL_SLAYER_LIST_2, ANIMAL_SLAYER_LIST_3, ANIMAL_SLAYER_LIST_4, ANIMAL_SLAYER_LIST_5))
						{
							final int proofs = st.getItemsCount(BLOODY_FANG) + st.getItemsCount(BLOODY_CLAW) + st.getItemsCount(BLOODY_NAIL) + st.getItemsCount(BLOODY_KASHA_FANG) + st.getItemsCount(BLOODY_TARANTULA_NAIL);
							if (proofs == 0)
							{
								htmltext = "7731-09.htm";
							}
							else if (proofs < 50)
							{
								htmltext = "7731-10.htm";
							}
							else
							{
								htmltext = "7731-11.htm";
							}
						}
						else if (st.getInt("progress") == 7)
						{
							htmltext = "7731-13.htm";
						}
						else
						{
							htmltext = "7731-16.htm";
						}
						break;
					
					case BELLA:
						htmltext = "7256-01.htm";
						break;
					
					case METTY:
						htmltext = "7072-01.htm";
						break;
					
					case ELLIE:
						htmltext = "7091-01.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int[] drop = DROPLIST.get(npc.getId());
		
		if (st.hasItems(drop[0] - 5))
		{
			st.dropItems(drop[0], 1, 50, drop[1]);
		}
		
		return null;
	}
	
	private String join(List<String> list)
	{
		StringBuilder sb = new StringBuilder();
		String loopDelim = "";
		for (String s : list)
		{
			sb.append(loopDelim);
			sb.append(s);
			loopDelim = " ";
		}
		return sb.toString();
	}
	
	private String checkQuestions(ScriptState st)
	{
		final int answers = st.getInt("correct") + (st.getInt("wrong"));
		if (answers < 10)
		{
			String[] questions = st.get("quiz").split(" ");
			int index = Rnd.get(questions.length - 1);
			String question = questions[index];
			
			if (questions.length > (10 - answers))
			{
				questions[index] = questions[questions.length - 1];
				List<String> list = new ArrayList<>(Arrays.asList(questions));
				list.remove(questions.length - 1);
				questions = list.toArray(questions);
				st.set("quiz", join(list));
			}
			return "7731-" + question + ".htm";
		}
		
		if (st.getInt("wrong") > 0)
		{
			st.unset("progress");
			st.unset("answers");
			st.unset("quiz");
			st.unset("wrong");
			st.unset("correct");
			return "7731-14.htm";
		}
		
		st.takeItems(ANIMAL_LOVER_LIST, 1);
		st.giveItems(WOLF_COLLAR, 1);
		st.playSound(PlaySoundType.QUEST_FINISH);
		st.exitQuest(true);
		
		return "7731-15.htm";
	}
	
}
