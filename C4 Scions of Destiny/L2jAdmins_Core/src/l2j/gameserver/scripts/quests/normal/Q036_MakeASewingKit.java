package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q036_MakeASewingKit extends Script
{
	// NPCs
	private static final int FERRIS = 7847;
	// MOB
	private static final int ENCHANTED_IRON_GOLEM = 566;
	// ITEMs
	private static final int ARTISANS_FRAME = 1891;
	private static final int ORIHARUKON = 1893;
	private static final int PIECE_OF_REINFORCED_STEEL = 7163;
	private static final int SIGNET_RING = 7164;
	// REWARD
	private static final int SEWING_KIT = 7078;
	
	public Q036_MakeASewingKit()
	{
		super(36, "Make a Sewing Kit");
		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		addKillId(ENCHANTED_IRON_GOLEM);
		registerItems(PIECE_OF_REINFORCED_STEEL);
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
		
		// FERRIS
		if (event.equalsIgnoreCase("7847-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("7847-07.htm"))
		{
			st.setCond(3, true);
			st.takeItems(PIECE_OF_REINFORCED_STEEL, 5);
		}
		else if (event.equalsIgnoreCase("7847-11.htm"))
		{
			if ((st.getItemsCount(ARTISANS_FRAME) >= 10) && (st.getItemsCount(ORIHARUKON) >= 10))
			{
				st.takeItems(ORIHARUKON, 10);
				st.takeItems(ARTISANS_FRAME, 10);
				st.rewardItems(SEWING_KIT, 1);
				st.exitQuest(false, true);
			}
			else
			{
				htmltext = "7847-10.htm";
			}
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
					htmltext = st.hasItems(SIGNET_RING) && (st2.getCond() >= 6) ? "7847-03.htm" : "7847-02.htm";
				}
				else
				{
					htmltext = "7847-01.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case FERRIS:
						if (cond == 1)
						{
							htmltext = "7847-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7847-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = (st.getItemsCount(ARTISANS_FRAME) >= 10) && (st.getItemsCount(ORIHARUKON) >= 10) ? "7847-09.htm" : "7847-08.htm";
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
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case ENCHANTED_IRON_GOLEM:
				if (st.dropItems(PIECE_OF_REINFORCED_STEEL, 1, 5, 300000))
				{
					st.setCond(2);
				}
				break;
		}
		return null;
	}
}
