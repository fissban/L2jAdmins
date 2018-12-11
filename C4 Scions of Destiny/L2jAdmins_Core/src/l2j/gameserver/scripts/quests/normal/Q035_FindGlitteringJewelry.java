package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q035_FindGlitteringJewelry extends Script
{
	// NPCs
	private static final int ELLIE = 7091;
	private static final int FELTON = 7879;
	// MOB
	private static final int ALLIGATOR = 135;
	// ITEMs
	private static final int SILVER_NUGGET = 1873;
	private static final int ORIHARUKON = 1893;
	private static final int THONS = 4044;
	private static final int ROUGH_JEWEL = 7162;
	// REWARD
	private static final int JEWEL_BOX = 7077;
	
	public Q035_FindGlitteringJewelry()
	{
		super(35, "Find Glittering Jewelry");
		addStartNpc(ELLIE);
		addTalkId(ELLIE, FELTON);
		addKillId(ALLIGATOR);
		registerItems(ROUGH_JEWEL);
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
		
		// ELLIE
		if (event.equalsIgnoreCase("7091-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7091-08.htm"))
		{
			st.setCond(4, true);
			st.takeItems(ROUGH_JEWEL);
		}
		else if (event.equalsIgnoreCase("7091-11.htm"))
		{
			if ((st.getItemsCount(ORIHARUKON) >= 5) && (st.getItemsCount(SILVER_NUGGET) >= 500) && (st.getItemsCount(THONS) >= 150))
			{
				st.takeItems(ORIHARUKON, 5);
				st.takeItems(SILVER_NUGGET, 500);
				st.takeItems(THONS, 150);
				st.rewardItems(JEWEL_BOX, 1);
				st.exitQuest(false, true);
			}
			else
			{
				htmltext = "7091-12.htm";
			}
		}
		// FELTON
		else if (event.equalsIgnoreCase("7879-02.htm"))
		{
			st.setCond(2, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q037_PleaseMakeMeFormalWear");
		String htmltext = getNoQuestMsg();
		if ((st == null) || (st2 == null))
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getLevel() >= 60)
				{
					htmltext = st2.getCond() >= 6 ? "7091-03.htm" : "7091-02.htm";
				}
				else
				{
					htmltext = "7091-01.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ELLIE:
						if (cond == 1)
						{
							htmltext = "7091-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7091-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7091-07.htm";
						}
						else if (cond == 4)
						{
							if ((st.getItemsCount(ORIHARUKON) >= 5) && (st.getItemsCount(SILVER_NUGGET) >= 500) && (st.getItemsCount(THONS) >= 150))
							{
								htmltext = "7091-10.htm";
							}
							else
							{
								htmltext = "7091-09.htm";
							}
						}
						break;
					case FELTON:
						if (cond == 1)
						{
							htmltext = "7879-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7879-03.htm";
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
			case ALLIGATOR:
				if (st.dropItems(ROUGH_JEWEL, 1, 10, 300000))
				{
					st.setCond(3);
				}
				break;
		}
		return null;
	}
}
