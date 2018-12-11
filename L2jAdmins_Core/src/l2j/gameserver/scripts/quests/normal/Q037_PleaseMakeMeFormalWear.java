package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q037_PleaseMakeMeFormalWear extends Script
{
	// NPCs
	private static final int ALEXIS = 7842;
	private static final int LEIKAR = 8520;
	private static final int JEREMY = 8521;
	private static final int MIST = 8627;
	// ITEMs
	private static final int SIGNET_RING = 7164;
	private static final int LUXURY_WINE = 7160;
	private static final int BOX_OF_COOKIES = 7159;
	private static final int MYSTERIOUS_CLOTH = 7076;
	private static final int JEWEL_BOX = 7077;
	private static final int SEWING_KIT = 7078;
	private static final int DRESS_SHOES_BOX = 7113;
	// REWARD
	private static final int FORMAL_WEAR = 6408;
	
	public Q037_PleaseMakeMeFormalWear()
	{
		super(37, "Make Formal Wear");
		addStartNpc(ALEXIS);
		addTalkId(ALEXIS, LEIKAR, JEREMY, MIST);
		registerItems(SIGNET_RING, LUXURY_WINE, BOX_OF_COOKIES);
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
		
		// ALEXIS
		if (event.equalsIgnoreCase("7842-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8520-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(SIGNET_RING, 1);
		}
		else if (event.equalsIgnoreCase("8520-05.htm"))
		{
			st.setCond(6, true);
			st.takeItems(BOX_OF_COOKIES);
		}
		else if (event.equalsIgnoreCase("8520-08.htm"))
		{
			if (st.hasItems(MYSTERIOUS_CLOTH, JEWEL_BOX, SEWING_KIT))
			{
				st.setCond(7, true);
				st.takeItems(MYSTERIOUS_CLOTH, 1);
				st.takeItems(JEWEL_BOX, 1);
				st.takeItems(SEWING_KIT, 1);
			}
			else
			{
				htmltext = "You don't have enough materials.";
			}
		}
		else if (event.equalsIgnoreCase("8520-12.htm"))
		{
			if (st.hasItems(DRESS_SHOES_BOX))
			{
				st.takeItems(DRESS_SHOES_BOX, 1);
				st.rewardItems(FORMAL_WEAR, 1);
				st.exitQuest(false, true);
			}
			else
			{
				htmltext = "8520-11.htm";
			}
		}
		// JEREMY
		else if (event.equalsIgnoreCase("8521-02.htm"))
		{
			st.setCond(3, true);
			st.giveItems(LUXURY_WINE, 1);
		}
		else if (event.equalsIgnoreCase("8521-05.htm"))
		{
			st.setCond(5, true);
			st.giveItems(BOX_OF_COOKIES, 1);
		}
		// MIST
		else if (event.equalsIgnoreCase("8627-02.htm"))
		{
			st.setCond(4, true);
			st.takeItems(LUXURY_WINE);
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
				htmltext = player.getLevel() >= 60 ? "7842-02.htm" : "7842-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ALEXIS:
						if (cond == 1)
						{
							htmltext = "7842-04.htm";
						}
						break;
					case LEIKAR:
						if (cond == 1)
						{
							htmltext = "8520-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8520-03.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8520-04.htm";
						}
						else if (cond == 6)
						{
							htmltext = st.hasItems(MYSTERIOUS_CLOTH, JEWEL_BOX, SEWING_KIT) ? "8520-07.htm" : "8520-06.htm";
						}
						else if (cond == 7)
						{
							htmltext = st.hasItems(DRESS_SHOES_BOX) ? "8520-10.htm" : "8520-09.htm";
						}
						break;
					case JEREMY:
						if (cond == 2)
						{
							htmltext = "8521-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8521-03.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8521-04.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8521-06.htm";
						}
						break;
					case MIST:
						if (cond == 3)
						{
							htmltext = "8627-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8627-03.htm";
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
