package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Q213_TrialOfSeeker extends Script
{
	// Npc's
	private static final int DUFNER = 7106;
	private static final int TERRY = 7064;
	private static final int BRUNON = 7526;
	private static final int VIKTOR = 7684;
	private static final int MARINA = 7715;
	// Monster's
	private static final int MEDUSA = 158;
	private static final int NEER_GHOUL_BERSERKER = 198;
	private static final int OL_MAHUM_CAPTAIN = 211;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int TURAK_BUGBEAR_WARRIOR = 249;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int TUREK_ORC_WARLORD = 495;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int ANT_CAPTAIN = 80;
	private static final int ANT_WARRIOR_CAPTAIN = 88;
	// Item's
	private static final int DUFNERS_LETTER = 2647;
	private static final int TERYS_ORDER1 = 2648;
	private static final int TERYS_ORDER2 = 2649;
	private static final int TERYS_LETTER = 2650;
	private static final int VIKTORS_LETTER = 2651;
	private static final int HAWKEYES_LETTER = 2652;
	private static final int MYSTERIOUS_RUNESTONE = 2653;
	private static final int OL_MAHUM_RUNESTONE = 2654;
	private static final int TUREK_RUNESTONE = 2655;
	private static final int ANT_RUNESTONE = 2656;
	private static final int TURAK_BUGBEAR_RUNESTONE = 2657;
	private static final int TERYS_BOX = 2658;
	private static final int VIKTORS_REQUEST = 2659;
	private static final int MEDUSAS_SCALES = 2660;
	private static final int SILENS_RUNESTONE = 2661;
	private static final int ANALYSIS_REQUEST = 2662;
	private static final int MARINAS_LETTER = 2663;
	private static final int EXPERIMENT_TOOLS = 2664;
	private static final int ANALYSIS_RESULT = 2665;
	private static final int TERYS_ORDER3 = 2666;
	private static final int LIST_OF_HOST = 2667;
	private static final int ABYSS_RUNESTONE1 = 2668;
	private static final int ABYSS_RUNESTONE2 = 2669;
	private static final int ABYSS_RUNESTONE3 = 2670;
	private static final int ABYSS_RUNESTONE4 = 2671;
	private static final int TERYS_REPORT = 2672;
	private static final int MARK_OF_SEEKER = 2673;
	
	private static final Map<Integer, DropListTrialOfSeeker> DROPLIST = new HashMap<>();
	{
		DROPLIST.put(NEER_GHOUL_BERSERKER, new DropListTrialOfSeeker(TERYS_ORDER1, MYSTERIOUS_RUNESTONE, 10, 1));
		DROPLIST.put(OL_MAHUM_CAPTAIN, new DropListTrialOfSeeker(TERYS_ORDER2, OL_MAHUM_RUNESTONE, 25, 1));
		DROPLIST.put(TUREK_ORC_WARLORD, new DropListTrialOfSeeker(TERYS_ORDER2, TUREK_RUNESTONE, 25, 1));
		DROPLIST.put(ANT_CAPTAIN, new DropListTrialOfSeeker(TERYS_ORDER2, ANT_RUNESTONE, 25, 1));
		DROPLIST.put(TURAK_BUGBEAR_WARRIOR, new DropListTrialOfSeeker(TERYS_ORDER2, TURAK_BUGBEAR_RUNESTONE, 25, 1));
		DROPLIST.put(MARSH_STAKATO_DRONE, new DropListTrialOfSeeker(LIST_OF_HOST, ABYSS_RUNESTONE1, 25, 1));
		DROPLIST.put(BREKA_ORC_OVERLORD, new DropListTrialOfSeeker(LIST_OF_HOST, ABYSS_RUNESTONE2, 25, 1));
		DROPLIST.put(ANT_WARRIOR_CAPTAIN, new DropListTrialOfSeeker(LIST_OF_HOST, ABYSS_RUNESTONE3, 25, 1));
		DROPLIST.put(LETO_LIZARDMAN_WARRIOR, new DropListTrialOfSeeker(LIST_OF_HOST, ABYSS_RUNESTONE4, 25, 1));
		DROPLIST.put(MEDUSA, new DropListTrialOfSeeker(VIKTORS_REQUEST, MEDUSAS_SCALES, 30, 10));
	}
	
	public Q213_TrialOfSeeker()
	{
		super(213, "Trial Of Seeker");
		addStartNpc(DUFNER);
		addTalkId(DUFNER, TERRY, BRUNON, VIKTOR, MARINA);
		addKillId(MEDUSA, NEER_GHOUL_BERSERKER, OL_MAHUM_CAPTAIN, MARSH_STAKATO_DRONE, TURAK_BUGBEAR_WARRIOR, BREKA_ORC_OVERLORD, TUREK_ORC_WARLORD, LETO_LIZARDMAN_WARRIOR, ANT_CAPTAIN, ANT_WARRIOR_CAPTAIN);
		registerItems(TERYS_REPORT, DUFNERS_LETTER, MYSTERIOUS_RUNESTONE, TERYS_ORDER1, OL_MAHUM_RUNESTONE, TUREK_RUNESTONE, ANT_RUNESTONE, TURAK_BUGBEAR_RUNESTONE, TERYS_ORDER2, ANALYSIS_RESULT, VIKTORS_LETTER, TERYS_ORDER3, LIST_OF_HOST, ABYSS_RUNESTONE1, ABYSS_RUNESTONE2, ABYSS_RUNESTONE3, ABYSS_RUNESTONE4, TERYS_LETTER, TERYS_BOX, HAWKEYES_LETTER, VIKTORS_REQUEST, MEDUSAS_SCALES, SILENS_RUNESTONE, ANALYSIS_REQUEST, EXPERIMENT_TOOLS, MARINAS_LETTER);
		
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
		
		if (event.equalsIgnoreCase("7106-05.htm"))
		{
			st.set("cond", "1");
			st.setState(ScriptStateType.STARTED);
			st.playSound("ItemSound.quest_accept");
			st.giveItems(DUFNERS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7064-03.htm"))
		{
			st.giveItems(TERYS_ORDER1, 1);
			st.takeItems(DUFNERS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7064-06.htm"))
		{
			st.takeItems(MYSTERIOUS_RUNESTONE, 1);
			st.giveItems(TERYS_ORDER2, 1);
			st.takeItems(TERYS_ORDER1, 1);
		}
		else if (event.equalsIgnoreCase("7064-10.htm"))
		{
			st.takeItems(OL_MAHUM_RUNESTONE, 1);
			st.takeItems(TUREK_RUNESTONE, 1);
			st.takeItems(ANT_RUNESTONE, 1);
			st.takeItems(TURAK_BUGBEAR_RUNESTONE, 1);
			st.takeItems(TERYS_ORDER2, 1);
			st.giveItems(TERYS_LETTER, 1);
			st.giveItems(TERYS_BOX, 1);
		}
		else if (event.equalsIgnoreCase("7064-18.htm"))
		{
			if (st.getPlayer().getLevel() < 36)
			{
				htmltext = "7064-17.htm";
				st.giveItems(TERYS_ORDER3, 1);
				st.takeItems(ANALYSIS_RESULT, 1);
			}
			else
			{
				st.giveItems(LIST_OF_HOST, 1);
				st.takeItems(ANALYSIS_RESULT, 1);
			}
		}
		else if (event.equalsIgnoreCase("7684-05.htm"))
		{
			st.giveItems(VIKTORS_LETTER, 1);
			st.takeItems(TERYS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7684-11.htm"))
		{
			st.giveItems(VIKTORS_REQUEST, 1);
			st.takeItems(TERYS_LETTER, 1);
			st.takeItems(TERYS_BOX, 1);
			st.takeItems(HAWKEYES_LETTER, 1);
			st.takeItems(VIKTORS_LETTER, st.getItemsCount(VIKTORS_LETTER));
		}
		else if (event.equalsIgnoreCase("7684-15.htm"))
		{
			st.takeItems(VIKTORS_REQUEST, 1);
			st.takeItems(MEDUSAS_SCALES, st.getItemsCount(MEDUSAS_SCALES));
			st.giveItems(SILENS_RUNESTONE, 1);
			st.giveItems(ANALYSIS_REQUEST, 1);
		}
		else if (event.equalsIgnoreCase("7715-02.htm"))
		{
			st.takeItems(SILENS_RUNESTONE, 1);
			st.takeItems(ANALYSIS_REQUEST, 1);
			st.giveItems(MARINAS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7715-05.htm"))
		{
			st.takeItems(EXPERIMENT_TOOLS, 1);
			st.giveItems(ANALYSIS_RESULT, 1);
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
		
		int npcId = npc.getId();
		ScriptStateType id = st.getState();
		
		if (id == ScriptStateType.CREATED)
		{
			st.startQuest();
			
			st.set("cond", "0");
			st.set("onlyone", "0");
			st.set("id", "0");
		}
		
		if ((npcId == DUFNER) && (st.getInt("cond") == 0) && (st.getInt("onlyone") == 0))
		{
			switch (st.getPlayer().getClassId())
			{
				case ROGUE:
				case SCOUT:
				case ASSASSIN:
					if (st.getPlayer().getLevel() >= 35)
					{
						htmltext = "7106-03.htm";
					}
					else
					{
						htmltext = "7106-02.htm";
						st.exitQuest(true);
					}
					break;
				default:
					htmltext = "7106-00.htm";
					st.exitQuest(true);
					break;
			}
			
		}
		else if ((npcId == DUFNER) && (st.getInt("cond") == 0) && (st.getInt("onlyone") == 1))
		{
			htmltext = getAlreadyCompletedMsg();
		}
		else if ((npcId == DUFNER) && (st.getInt("cond") == 1) && (st.getInt("onlyone") == 0))
		{
			if ((st.getItemsCount(DUFNERS_LETTER) == 1) && (st.getItemsCount(TERYS_REPORT) == 0))
			{
				htmltext = "7106-06.htm";
			}
			else if ((st.getItemsCount(DUFNERS_LETTER) == 0) && (st.getItemsCount(TERYS_REPORT) == 0))
			{
				htmltext = "7106-07.htm";
			}
			else if ((st.getItemsCount(DUFNERS_LETTER) == 0) && (st.getItemsCount(TERYS_REPORT) == 1))
			{
				st.rewardExpAndSp(72126, 11000);
				st.giveItems(7562, 8);
				htmltext = "7106-08.htm";
				st.set("cond", "0");
				st.set("onlyone", "1");
				st.setState(ScriptStateType.COMPLETED);
				st.playSound("ItemSound.quest_finish");
				st.takeItems(TERYS_REPORT, 1);
				st.giveItems(7562, 8);
				st.giveItems(MARK_OF_SEEKER, 1);
			}
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(DUFNERS_LETTER) == 1))
		{
			htmltext = "7064-01.htm";
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(TERYS_ORDER1) == 1))
		{
			if (st.getItemsCount(MYSTERIOUS_RUNESTONE) == 0)
			{
				htmltext = "7064-04.htm";
			}
			else
			{
				htmltext = "7064-05.htm";
			}
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(TERYS_ORDER2) == 1))
		{
			if (st.getItemsCount(TERYS_ORDER2) == 1)
			{
				if ((st.getItemsCount(OL_MAHUM_RUNESTONE) + st.getItemsCount(TUREK_RUNESTONE) + st.getItemsCount(ANT_RUNESTONE) + st.getItemsCount(TURAK_BUGBEAR_RUNESTONE)) < 4)
				{
					htmltext = "7064-08.htm";
				}
				else
				{
					htmltext = "7064-09.htm";
				}
			}
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(TERYS_LETTER) == 1))
		{
			htmltext = "7064-11.htm";
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(VIKTORS_LETTER) == 1))
		{
			htmltext = "7064-12.htm";
			st.takeItems(VIKTORS_LETTER, 1);
			st.giveItems(HAWKEYES_LETTER, 1);
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(HAWKEYES_LETTER) == 1))
		{
			htmltext = "7064-13.htm";
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && ((st.getItemsCount(VIKTORS_REQUEST) == 1) || (st.getItemsCount(ANALYSIS_REQUEST) == 1) || (st.getItemsCount(MARINAS_LETTER) == 1) || (st.getItemsCount(EXPERIMENT_TOOLS) == 1)))
		{
			htmltext = "7064-14.htm";
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(ANALYSIS_RESULT) == 1))
		{
			htmltext = "7064-15.htm";
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(TERYS_ORDER3) == 1))
		{
			if (st.getPlayer().getLevel() < 36)
			{
				htmltext = "7064-20.htm";
			}
			else
			{
				htmltext = "7064-21.htm";
				st.giveItems(LIST_OF_HOST, 1);
				st.takeItems(TERYS_ORDER3, 1);
			}
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(LIST_OF_HOST) == 1))
		{
			if ((st.getItemsCount(ABYSS_RUNESTONE1) + st.getItemsCount(ABYSS_RUNESTONE2) + st.getItemsCount(ABYSS_RUNESTONE3) + st.getItemsCount(ABYSS_RUNESTONE4)) < 4)
			{
				htmltext = "7064-22.htm";
			}
			else
			{
				htmltext = "7064-23.htm";
				st.giveItems(TERYS_REPORT, 1);
				st.takeItems(LIST_OF_HOST, 1);
				st.takeItems(ABYSS_RUNESTONE1, 1);
				st.takeItems(ABYSS_RUNESTONE2, 1);
				st.takeItems(ABYSS_RUNESTONE3, 1);
				st.takeItems(ABYSS_RUNESTONE4, 1);
			}
		}
		else if ((npcId == TERRY) && (st.getInt("cond") == 1) && (st.getItemsCount(TERYS_REPORT) == 1))
		{
			htmltext = "7064-24.htm";
		}
		else if ((npcId == VIKTOR) && (st.getInt("cond") == 1) && (st.getItemsCount(TERYS_LETTER) == 1))
		{
			htmltext = "7684-01.htm";
		}
		else if ((npcId == VIKTOR) && (st.getInt("cond") == 1) && (st.getItemsCount(HAWKEYES_LETTER) == 1))
		{
			htmltext = "7684-12.htm";
		}
		else if ((npcId == VIKTOR) && (st.getInt("cond") == 1) && (st.getItemsCount(VIKTORS_REQUEST) == 1))
		{
			if (st.getItemsCount(MEDUSAS_SCALES) < 10)
			{
				htmltext = "7684-13.htm";
			}
			else
			{
				htmltext = "7684-14.htm";
			}
		}
		else if ((npcId == VIKTOR) && (st.getInt("cond") == 1) && (st.getItemsCount(SILENS_RUNESTONE) == 1) && (st.getItemsCount(ANALYSIS_REQUEST) == 1))
		{
			htmltext = "7684-16.htm";
		}
		else if ((npcId == VIKTOR) && (st.getInt("cond") == 1) && ((st.getItemsCount(MARINAS_LETTER) == 1) && (st.getItemsCount(EXPERIMENT_TOOLS) == 1) && (st.getItemsCount(ANALYSIS_RESULT) == 1) && (st.getItemsCount(TERYS_REPORT) == 1)))
		{
			htmltext = "7684-17.htm";
		}
		else if ((npcId == MARINA) && (st.getInt("cond") == 1) && (st.getItemsCount(SILENS_RUNESTONE) == 1) && (st.getItemsCount(ANALYSIS_REQUEST) == 1))
		{
			htmltext = "7715-01.htm";
		}
		else if ((npcId == MARINA) && (st.getInt("cond") == 1) && (st.getItemsCount(MARINAS_LETTER) == 1))
		{
			htmltext = "7715-03.htm";
		}
		else if ((npcId == MARINA) && (st.getInt("cond") == 1) && (st.getItemsCount(EXPERIMENT_TOOLS) == 1))
		{
			htmltext = "7715-04.htm";
		}
		else if ((npcId == MARINA) && (st.getInt("cond") == 1) && ((st.getItemsCount(ANALYSIS_RESULT) == 1) || (st.getItemsCount(TERYS_REPORT) == 1)))
		{
			htmltext = "7715-06.htm";
		}
		else if ((npcId == BRUNON) && (st.getInt("cond") == 1) && (st.getItemsCount(MARINAS_LETTER) == 1))
		{
			htmltext = "7526-01.htm";
			st.takeItems(MARINAS_LETTER, 1);
			st.giveItems(EXPERIMENT_TOOLS, 1);
		}
		else if ((npcId == BRUNON) && (st.getInt("cond") == 1) && (st.getItemsCount(EXPERIMENT_TOOLS) == 1))
		{
			htmltext = "7526-02.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st != null)
		{
			if (st.getState() != ScriptStateType.STARTED)
			{
				return null;
			}
			
			int npcId = npc.getId();
			
			int required = DROPLIST.get(npcId).requiered;
			int item = DROPLIST.get(npcId).item;
			int chance = DROPLIST.get(npcId).chance;
			int maxqty = DROPLIST.get(npcId).maxqty;
			int count = st.getItemsCount(item);
			
			if (st.hasItems(required) && (count < maxqty))
			{
				if (Rnd.get(100) < chance)
				{
					st.giveItems(item, 1);
					if ((count + 1) == maxqty)
					{
						st.playSound("Itemsound.quest_middle");
					}
					else
					{
						st.playSound("Itemsound.quest_itemget");
						
					}
				}
			}
		}
		return null;
	}
	
	public class DropListTrialOfSeeker
	{
		// required, item, chance, maxqty = DROPLIST[npcId];
		public int requiered;
		public int item;
		public int chance;
		public int maxqty;
		
		public DropListTrialOfSeeker(int requiered, int item, int chance, int maxqty)
		{
			this.requiered = requiered;
			this.item = item;
			this.chance = chance;
			this.maxqty = maxqty;
		}
	}
}
