package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q011_SecretMeetingWithKetraOrcs extends Script
{
	// NPCs
	private static final int LEON = 8256;
	private static final int CADMON = 8296;
	private static final int WAHKAN = 8371;
	// ITEM
	private static final int BOX = 7231;
	
	public Q011_SecretMeetingWithKetraOrcs()
	{
		super(11, "Secret Meeting With Ketra Orcs");
		addStartNpc(CADMON);
		addTalkId(CADMON, LEON, WAHKAN);
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
		
		// CADMON
		if (event.equalsIgnoreCase("8296-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8256-02.htm"))
		{
			st.setCond(2, true);
			st.giveItems(BOX, 1);
		}
		// WAHKAN
		else if (event.equalsIgnoreCase("8371-02.htm"))
		{
			st.rewardExpAndSp(22787, 0);
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
				htmltext = player.getLevel() < 74 ? "8296-02.htm" : "8296-01.htm";
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
					case LEON:
						if (cond == 1)
						{
							htmltext = "8256-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8256-03.htm";
						}
						break;
					case WAHKAN:
						if (cond == 2)
						{
							htmltext = "8371-01.htm";
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
