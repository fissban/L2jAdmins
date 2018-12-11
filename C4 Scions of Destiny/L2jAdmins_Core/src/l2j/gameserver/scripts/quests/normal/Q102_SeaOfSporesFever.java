package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q102_SeaOfSporesFever extends Script
{
	// NPC
	private static final int VELTRESS = 7219;
	private static final int COBENDELL = 7156;
	private static final int BERROS = 7217;
	private static final int RAYEN = 7221;
	private static final int ALBERIUS = 7284;
	private static final int GARTRANDELL = 7285;
	// ITEMs
	private static final int ALBERIUS_LIST = 746;
	private static final int ALBERIUS_LETTER = 964;
	private static final int EVERGREEN_AMULET = 965;
	private static final int DRYAD_TEARS = 966;
	private static final int COBENDELL_MEDICINE_1 = 1130;
	private static final int COBENDELL_MEDICINE_2 = 1131;
	private static final int COBENDELL_MEDICINE_3 = 1132;
	private static final int COBENDELL_MEDICINE_4 = 1133;
	private static final int COBENDELL_MEDICINE_5 = 1134;
	// MOBs
	private static final int DRYAD = 13;
	private static final int DRYAD_ELDER = 19;
	// REWARDs
	private static final int SWORD_OF_SENTINEL = 743;
	private static final int STAFF_OF_SENTINEL = 744;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	
	public Q102_SeaOfSporesFever()
	{
		super(102, "Sea of Spores Fever");
		addStartNpc(ALBERIUS);
		addTalkId(COBENDELL, BERROS, VELTRESS, RAYEN, ALBERIUS, GARTRANDELL);
		addKillId(DRYAD, DRYAD_ELDER);
		registerItems(ALBERIUS_LIST, ALBERIUS_LETTER, EVERGREEN_AMULET, DRYAD_TEARS, COBENDELL_MEDICINE_1, COBENDELL_MEDICINE_2, COBENDELL_MEDICINE_3, COBENDELL_MEDICINE_4, COBENDELL_MEDICINE_5);
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
		
		// ALBERIUS
		if (event.equalsIgnoreCase("7284-02.htm"))
		{
			st.startQuest();
			st.giveItems(ALBERIUS_LETTER, 1);
		}
		// COBENDELL
		else if (event.equalsIgnoreCase("7156-03.htm"))
		{
			st.setCond(2, true);
			st.takeItems(ALBERIUS_LETTER);
			st.giveItems(EVERGREEN_AMULET, 1);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7284-01.htm";
				}
				else if ((player.getLevel() < 12) || (player.getLevel() > 18))
				{
					htmltext = "7284-08.htm";
				}
				else
				{
					htmltext = "7284-07.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ALBERIUS:
						if (cond == 1)
						{
							htmltext = "7284-03.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7284-09.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7284-04.htm";
							st.setCond(5, true);
							st.takeItems(COBENDELL_MEDICINE_1);
							st.giveItems(ALBERIUS_LIST, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7284-05.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7284-06.htm";
							st.giveItems(player.isMageClass() ? SPIRITSHOT_FOR_BEGINNERS : SOULSHOT_FOR_BEGINNERS, 1000);
							st.giveItems(player.isMageClass() ? STAFF_OF_SENTINEL : SWORD_OF_SENTINEL, 1);
							st.exitQuest(false, true);
						}
						break;
					case COBENDELL:
						if (cond == 1)
						{
							htmltext = "7156-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7156-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7156-05.htm";
							st.setCond(4, true);
							st.takeItems(DRYAD_TEARS);
							st.takeItems(EVERGREEN_AMULET);
							st.giveItems(COBENDELL_MEDICINE_1, 1);
							st.giveItems(COBENDELL_MEDICINE_2, 1);
							st.giveItems(COBENDELL_MEDICINE_3, 1);
							st.giveItems(COBENDELL_MEDICINE_4, 1);
							st.giveItems(COBENDELL_MEDICINE_5, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7156-06.htm";
						}
						break;
					case GARTRANDELL:
						if (cond == 5)
						{
							htmltext = "7285-01.htm";
							st.takeItems(COBENDELL_MEDICINE_2);
							if (!st.hasItems(COBENDELL_MEDICINE_3) && !st.hasItems(COBENDELL_MEDICINE_4) && !st.hasItems(COBENDELL_MEDICINE_5))
							{
								st.setCond(6, true);
							}
						}
						break;
					case VELTRESS:
						if (cond == 5)
						{
							htmltext = "7219-01.htm";
							st.takeItems(COBENDELL_MEDICINE_3);
							if (!st.hasItems(COBENDELL_MEDICINE_2) && !st.hasItems(COBENDELL_MEDICINE_4) && !st.hasItems(COBENDELL_MEDICINE_5))
							{
								st.setCond(6, true);
							}
						}
						break;
					case BERROS:
						if (cond == 5)
						{
							htmltext = "7217-01.htm";
							st.takeItems(COBENDELL_MEDICINE_4);
							if (!st.hasItems(COBENDELL_MEDICINE_5) && !st.hasItems(COBENDELL_MEDICINE_2) && !st.hasItems(COBENDELL_MEDICINE_3))
							{
								st.setCond(6, true);
							}
						}
						break;
					case RAYEN:
						if (cond == 5)
						{
							htmltext = "7221-01.htm";
							st.takeItems(COBENDELL_MEDICINE_5);
							if (!st.hasItems(COBENDELL_MEDICINE_2) && !st.hasItems(COBENDELL_MEDICINE_3) && !st.hasItems(COBENDELL_MEDICINE_4))
							{
								st.setCond(6, true);
							}
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
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "2");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case DRYAD:
			case DRYAD_ELDER:
				if (st.dropItems(DRYAD_TEARS, 1, 10, 300000))
				{
					st.setCond(3);
				}
				break;
		}
		return null;
	}
}
