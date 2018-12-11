package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q045_ToTalkingIsland extends Script
{
	// NPCs
	private static final int SANDRA = 7090;
	private static final int GENTLER = 7094;
	private static final int GALLADUCCI = 7097;
	private static final int DUSTIN = 7116;
	// ITEMs
	private static final int GALLADUCCI_ORDER_DOCUMENT_1 = 7563;
	private static final int GALLADUCCI_ORDER_DOCUMENT_2 = 7564;
	private static final int GALLADUCCI_ORDER_DOCUMENT_3 = 7565;
	private static final int PURIFIED_MAGIC_NECKLACE = 7566;
	private static final int GEMSTONE_POWDER = 7567;
	private static final int MAGIC_SWORD_HILT = 7568;
	private static final int MARK_OF_TRAVELER = 7570;
	// REWARD
	private static final int SCROLL_OF_ESCAPE_TALKING_ISLAND = 7554;
	
	public Q045_ToTalkingIsland()
	{
		super(45, "To Talking Island");
		addStartNpc(GALLADUCCI);
		addTalkId(GALLADUCCI, GENTLER, SANDRA, DUSTIN);
		registerItems(GALLADUCCI_ORDER_DOCUMENT_1, GALLADUCCI_ORDER_DOCUMENT_2, GALLADUCCI_ORDER_DOCUMENT_3, MAGIC_SWORD_HILT, GEMSTONE_POWDER, PURIFIED_MAGIC_NECKLACE);
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
		
		// GALLADUCCI
		if (event.equalsIgnoreCase("7097-03.htm"))
		{
			st.startQuest();
			st.takeItems(MARK_OF_TRAVELER);
			st.giveItems(GALLADUCCI_ORDER_DOCUMENT_1, 1);
		}
		else if (event.equalsIgnoreCase("7097-06.htm"))
		{
			st.setCond(3, true);
			st.takeItems(MAGIC_SWORD_HILT);
			st.giveItems(GALLADUCCI_ORDER_DOCUMENT_2, 1);
		}
		else if (event.equalsIgnoreCase("7097-09.htm"))
		{
			st.setCond(5, true);
			st.takeItems(GEMSTONE_POWDER);
			st.giveItems(GALLADUCCI_ORDER_DOCUMENT_3, 1);
		}
		else if (event.equalsIgnoreCase("7097-12.htm"))
		{
			st.rewardItems(SCROLL_OF_ESCAPE_TALKING_ISLAND, 1);
			st.exitQuest(false, true);
		}
		// GENTLER
		else if (event.equalsIgnoreCase("7094-02.htm"))
		{
			st.setCond(2, true);
			st.takeItems(GALLADUCCI_ORDER_DOCUMENT_1);
			st.giveItems(MAGIC_SWORD_HILT, 1);
		}
		// SANDRA
		else if (event.equalsIgnoreCase("7090-02.htm"))
		{
			st.setCond(4, true);
			st.takeItems(GALLADUCCI_ORDER_DOCUMENT_2);
			st.giveItems(GEMSTONE_POWDER, 1);
		}
		// DUSTIN
		else if (event.equalsIgnoreCase("7116-02.htm"))
		{
			st.setCond(6, true);
			st.takeItems(GALLADUCCI_ORDER_DOCUMENT_3);
			st.giveItems(PURIFIED_MAGIC_NECKLACE, 1);
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
				htmltext = (player.getLevel() >= 3) && st.hasItems(MARK_OF_TRAVELER) && (player.getRace() == Race.HUMAN) ? "7097-02.htm" : "7097-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case GALLADUCCI:
						if (cond == 1)
						{
							htmltext = "7097-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7097-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7097-07.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7097-08.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7097-10.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7097-11.htm";
						}
						break;
					case GENTLER:
						if (cond == 1)
						{
							htmltext = "7094-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7094-03.htm";
						}
						break;
					case SANDRA:
						if (cond == 3)
						{
							htmltext = "7090-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7090-03.htm";
						}
						break;
					case DUSTIN:
						if (cond == 5)
						{
							htmltext = "7116-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7116-03.htm";
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
}
