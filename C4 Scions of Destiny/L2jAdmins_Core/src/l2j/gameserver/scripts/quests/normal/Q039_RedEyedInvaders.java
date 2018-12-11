package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author Reynald0
 */
public class Q039_RedEyedInvaders extends Script
{
	// NPCs
	private static final int BABENCO = 7334;
	private static final int BATHIS = 7332;
	// MOBs
	private static final int MAILLE_LIZARDMAN = 919;
	private static final int MAILLE_LIZARDMAN_SCOUT = 920;
	private static final int MAILLE_LIZARDMAN_GUARD = 921;
	private static final int GIANT_ARANEID = 925;
	// ITEMs
	private static final int BLACK_BONE_NECKLACE = 7178;
	private static final int RED_BONE_NECKLACE = 7179;
	private static final int INCENSE_POUCH = 7180;
	private static final int GEM_OF_MAILLE_LIZARDMAN = 7181;
	// REWARDs
	private static final int GREEN_COLORED_LURE_HIGH_GRADE = 6521;
	private static final int BABY_DUCK_ROD = 6529;
	private static final int FISHING_SHOT_NON_GRADE = 6535;
	
	public Q039_RedEyedInvaders()
	{
		super(39, "Red-Eyed Invaders");
		addStartNpc(BABENCO);
		addTalkId(BABENCO, BATHIS);
		addKillId(MAILLE_LIZARDMAN, MAILLE_LIZARDMAN_SCOUT, MAILLE_LIZARDMAN_GUARD, GIANT_ARANEID);
		registerItems(BLACK_BONE_NECKLACE, RED_BONE_NECKLACE, INCENSE_POUCH, GEM_OF_MAILLE_LIZARDMAN);
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
		
		// BABENCO
		if (event.equalsIgnoreCase("7334-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7332-02.htm"))
		{
			st.setCond(2, true);
		}
		else if (event.equalsIgnoreCase("7332-05.htm"))
		{
			st.setCond(4, true);
			st.takeItems(RED_BONE_NECKLACE, 100);
			st.takeItems(BLACK_BONE_NECKLACE, 100);
		}
		else if (event.equalsIgnoreCase("7332-08.htm"))
		{
			st.rewardItems(GREEN_COLORED_LURE_HIGH_GRADE, 60);
			st.rewardItems(BABY_DUCK_ROD, 1);
			st.rewardItems(FISHING_SHOT_NON_GRADE, 500);
			st.exitQuest(false, true);
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
				htmltext = (player.getLevel() >= 20) && (player.getLevel() <= 28) ? "7334-02.htm" : "7334-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case BABENCO:
						if (cond == 1)
						{
							htmltext = "7334-04.htm";
						}
						break;
					case BATHIS:
						if (cond == 1)
						{
							htmltext = "7332-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7332-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7332-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7332-06.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7332-07.htm";
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
			case MAILLE_LIZARDMAN:
			case MAILLE_LIZARDMAN_SCOUT:
				if ((cond == 4) && (npc.getId() == MAILLE_LIZARDMAN_SCOUT))
				{
					if (st.dropItems(INCENSE_POUCH, 1, 30, 250000) && (st.getItemsCount(GEM_OF_MAILLE_LIZARDMAN) >= 30))
					{
						st.setCond(5);
					}
				}
				else if (cond == 2)
				{
					if (st.dropItems(BLACK_BONE_NECKLACE, 1, 100, 500000) && (st.getItemsCount(RED_BONE_NECKLACE) >= 100))
					{
						st.setCond(3);
					}
				}
				break;
			case MAILLE_LIZARDMAN_GUARD:
				if (cond == 4)
				{
					if (st.dropItems(INCENSE_POUCH, 1, 30, 250000) && (st.getItemsCount(GEM_OF_MAILLE_LIZARDMAN) >= 30))
					{
						st.setCond(5);
					}
				}
				else if (cond == 2)
				{
					if (st.dropItems(RED_BONE_NECKLACE, 1, 100, 300000) && (st.getItemsCount(BLACK_BONE_NECKLACE) >= 100))
					{
						st.setCond(3);
					}
				}
				break;
			case GIANT_ARANEID:
				if (cond == 4)
				{
					if (st.dropItems(GEM_OF_MAILLE_LIZARDMAN, 1, 30, 250000) && (st.getItemsCount(INCENSE_POUCH) >= 30))
					{
						st.setCond(5);
					}
				}
				break;
		}
		return null;
	}
}
