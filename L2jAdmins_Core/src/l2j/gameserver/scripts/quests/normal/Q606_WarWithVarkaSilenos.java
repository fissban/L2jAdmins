package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi The onKill section of that quest is directly written on Q605.
 * @originalQuest aCis
 */
public class Q606_WarWithVarkaSilenos extends Script
{
	// Items
	private static final int HORN_OF_BUFFALO = 7186;
	private static final int VARKA_MANE = 7233;
	
	public Q606_WarWithVarkaSilenos()
	{
		super(606, "War with Varka Silenos");
		
		registerItems(VARKA_MANE);
		
		addStartNpc(8370); // Kadun Zu Ketra
		addTalkId(8370);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8370-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8370-07.htm"))
		{
			if (st.getItemsCount(VARKA_MANE) >= 100)
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(VARKA_MANE, 100);
				st.giveItems(HORN_OF_BUFFALO, 20);
			}
			else
			{
				htmltext = "8370-08.htm";
			}
		}
		else if (event.equalsIgnoreCase("8370-09.htm"))
		{
			st.takeItems(VARKA_MANE, -1);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = ((player.getLevel() >= 74) && player.isAlliedWithKetra()) ? "8370-01.htm" : "8370-02.htm";
				break;
			
			case STARTED:
				htmltext = (st.hasItems(VARKA_MANE)) ? "8370-04.htm" : "8370-05.htm";
				break;
		}
		
		return htmltext;
	}
	
}
