package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q012_SecretMeetingWithVarkaSilenos extends Script
{
	// NPCS
	private static final int HELMUT = 8258;
	private static final int CADMON = 8296;
	private static final int NARAM = 8378;
	// ITEM
	private static final int BOX = 7232;
	
	public Q012_SecretMeetingWithVarkaSilenos()
	{
		super(12, "Secret Meeting With Varka Silenos");
		addStartNpc(CADMON);
		addTalkId(CADMON, HELMUT, NARAM);
		registerItems(BOX);
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
		
		if (event.equalsIgnoreCase("8296-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8258-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(BOX, 1);
		}
		else if (event.equalsIgnoreCase("8378-02.htm"))
		{
			st.rewardExpAndSp(79761, 0);
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
				if (player.getLevel() < 74)
				{
					htmltext = player.getLevel() < 74 ? "8296-02.htm" : "8296-01.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case CADMON:
						if (cond == 1)
						{
							htmltext = "8296-04.htm";
						}
						break;
					case HELMUT:
						if (cond == 1)
						{
							htmltext = "8258-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8258-03.htm";
						}
						break;
					case NARAM:
						if (cond == 2)
						{
							htmltext = "8378-01.htm";
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
