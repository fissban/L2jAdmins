package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q013_ParcelDelivery extends Script
{
	// NPCs
	private static final int FUNDIN = 8274;
	private static final int VULCAN = 8539;
	// ITEM
	private static final int PACKAGE = 7263;
	// REWARD
	private static final int ADENA = 57;
	
	public Q013_ParcelDelivery()
	{
		super(13, "Parcel Delivery");
		addStartNpc(FUNDIN);
		addTalkId(FUNDIN, VULCAN);
		registerItems(PACKAGE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState("Q013_ParcelDelivery");
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		// FUNDIN
		if (event.equalsIgnoreCase("8274-03.htm"))
		{
			st.startQuest();
			st.giveItems(PACKAGE, 1);
		}
		// VULCAN
		else if (event.equalsIgnoreCase("8539-02.htm"))
		{
			st.rewardItems(ADENA, 82656);
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
				htmltext = player.getLevel() < 74 ? "8274-01.htm" : "8274-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case FUNDIN:
						if (cond == 1)
						{
							htmltext = "8274-03.htm";
						}
						break;
					case VULCAN:
						if (cond == 1)
						{
							htmltext = "8539-01.htm";
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
