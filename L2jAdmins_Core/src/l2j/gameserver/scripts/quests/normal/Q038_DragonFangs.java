package l2j.gameserver.scripts.quests.normal;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author Reynald0
 */
public class Q038_DragonFangs extends Script
{
	// NPCs
	private static final int IRIS = 7034;
	private static final int ROHMER = 7344;
	private static final int LUIS = 7386;
	// MOBs
	private static final int LANGK_LIZARDMAN_LEADER = 356;
	private static final int LANGK_LIZARDMAN_LIEUTENANT = 357;
	private static final int LANGK_LIZARDMAN_SENTINEL = 1100;
	private static final int LANGK_LIZARDMAN_SHAMAN = 1101;
	// ITEMs
	private static final int FEATHER_ORNAMENT = 7173;
	private static final int TOOTH_OF_TOTEM = 7174;
	private static final int TOOTH_OF_DRAGON = 7175;
	private static final int LETTER_OF_IRIS = 7176;
	private static final int LETTER_OF_ROHMER = 7177;
	// REWARD
	private static final int BONE_HELMET = 45;
	private static final int ADENA = 57;
	private static final int LEATHER_GAUNTLETS = 605;
	private static final int ASPIS = 627;
	private static final int BLUE_BUCKSKIN_BOOTS = 1123;
	
	private static final List<int[]> REWARDS = new ArrayList<>();
	{
		REWARDS.add(new int[]
		{
			BONE_HELMET, // Item
			5200
			// Amount of adena
		});
		REWARDS.add(new int[]
		{
			ASPIS,
			1500
		});
		REWARDS.add(new int[]
		{
			BLUE_BUCKSKIN_BOOTS,
			3200
		});
		REWARDS.add(new int[]
		{
			LEATHER_GAUNTLETS,
			3200
		});
	}
	
	public Q038_DragonFangs()
	{
		super(38, "Dragon Fangs");
		addStartNpc(LUIS);
		addTalkId(LUIS, IRIS, ROHMER);
		addKillId(LANGK_LIZARDMAN_SENTINEL, LANGK_LIZARDMAN_LIEUTENANT, LANGK_LIZARDMAN_LEADER, LANGK_LIZARDMAN_SHAMAN);
		registerItems(FEATHER_ORNAMENT, TOOTH_OF_TOTEM, TOOTH_OF_DRAGON);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		// LUIS
		if (event.equalsIgnoreCase("7386-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7386-07.htm"))
		{
			if (st.getItemsCount(FEATHER_ORNAMENT) >= 100)
			{
				st.setCond(3, true);
				st.takeItems(FEATHER_ORNAMENT);
				st.giveItems(TOOTH_OF_TOTEM, 1);
			}
			else
			{
				htmltext = "7386-06.htm";
			}
		}
		// IRIS
		else if (event.equalsIgnoreCase("7034-03.htm"))
		{
			st.setCond(4, true);
			st.takeItems(TOOTH_OF_TOTEM);
			st.giveItems(LETTER_OF_IRIS, 1);
		}
		else if (event.equalsIgnoreCase("7034-07.htm"))
		{
			st.setCond(6, true);
			st.takeItems(LETTER_OF_ROHMER);
		}
		else if (event.equalsIgnoreCase("7034-11.htm"))
		{
			
			if (st.getItemsCount(TOOTH_OF_DRAGON) >= 50)
			{
				int reward = Rnd.get(REWARDS.size());
				st.rewardItems(REWARDS.get(reward)[0], 1);
				st.rewardItems(ADENA, REWARDS.get(reward)[1]);
				st.exitQuest(false, true);
			}
			else
			{
				htmltext = "7034-09.htm";
			}
		}
		// ROHMER
		else if (event.equalsIgnoreCase("7344-03.htm"))
		{
			st.setCond(5, true);
			st.takeItems(LETTER_OF_IRIS);
			st.giveItems(LETTER_OF_ROHMER, 1);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = ((player.getLevel() >= 19) && (player.getLevel() <= 29)) ? "7386-02.htm" : "7386-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case LUIS:
						if (cond == 1)
						{
							htmltext = "7386-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = st.getItemsCount(FEATHER_ORNAMENT) >= 100 ? "7386-05.htm" : "7386-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7386-08.htm";
						}
						break;
					case IRIS:
						if (cond == 3)
						{
							htmltext = st.hasItems(TOOTH_OF_TOTEM) ? "7034-02.htm" : "7034-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = st.getItemsCount(TOOTH_OF_DRAGON) >= 50 ? "7034-10.htm" : "7034-09.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7034-08.htm";
						}
						else if (cond == 5)
						{
							htmltext = st.hasItems(LETTER_OF_ROHMER) ? "7034-05.htm" : "7034-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7034-04.htm";
						}
						break;
					case ROHMER:
						if (cond == 4)
						{
							htmltext = st.hasItems(LETTER_OF_IRIS) ? "7344-02.htm" : "7344-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7344-04.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = checkPlayerState(killer, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		int cond = st.getCond();
		switch (npc.getId())
		{
			case LANGK_LIZARDMAN_LIEUTENANT:
			case LANGK_LIZARDMAN_SENTINEL:
				if (cond == 1)
				{
					if (st.dropItems(FEATHER_ORNAMENT, 1, 100, 300000))
					{
						st.setCond(2);
					}
				}
				break;
			case LANGK_LIZARDMAN_LEADER:
			case LANGK_LIZARDMAN_SHAMAN:
				if (cond == 6)
				{
					if (st.dropItems(TOOTH_OF_DRAGON, 1, 50, 300000))
					{
						st.setCond(7);
					}
				}
				break;
		}
		return null;
	}
}
