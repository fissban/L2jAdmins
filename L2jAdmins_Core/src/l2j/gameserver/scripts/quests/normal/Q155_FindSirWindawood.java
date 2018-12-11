package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q155_FindSirWindawood extends Script
{
	// ITEMs
	private static final int OFFICIAL_LETTER = 1019;
	private static final int HASTE_POTION = 734;
	// NPCs
	private static final int ABELLOS = 7042;
	private static final int WINDAWOOD = 7311;
	
	public Q155_FindSirWindawood()
	{
		super(155, "Find Sir Windawood");
		
		registerItems(OFFICIAL_LETTER);
		
		addStartNpc(ABELLOS);
		addTalkId(WINDAWOOD, ABELLOS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7042-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(OFFICIAL_LETTER, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 3 ? "7042-01a.htm" : "7042-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case ABELLOS:
						htmltext = "7042-03.htm";
						break;
					
					case WINDAWOOD:
						if (st.hasItems(OFFICIAL_LETTER))
						{
							htmltext = "7311-01.htm";
							st.takeItems(OFFICIAL_LETTER, 1);
							st.rewardItems(HASTE_POTION, 1);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
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
