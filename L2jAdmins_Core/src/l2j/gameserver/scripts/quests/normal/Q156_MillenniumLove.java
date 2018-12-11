package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @originalQuest aCis
 */
public class Q156_MillenniumLove extends Script
{
	// ITEMs
	private static final int LILITH_LETTER = 1022;
	private static final int THEON_DIARY = 1023;
	// NPCs
	private static final int LILITH = 7368;
	private static final int BAENEDES = 7369;
	
	public Q156_MillenniumLove()
	{
		super(156, "Millennium Love");
		
		registerItems(LILITH_LETTER, THEON_DIARY);
		
		addStartNpc(LILITH);
		addTalkId(LILITH, BAENEDES);
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
		
		if (event.equalsIgnoreCase("7368-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(LILITH_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7369-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(LILITH_LETTER, 1);
			st.giveItems(THEON_DIARY, 1);
		}
		else if (event.equalsIgnoreCase("7369-03.htm"))
		{
			st.takeItems(LILITH_LETTER, 1);
			st.rewardExpAndSp(3000, 0);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 15 ? "7368-00.htm" : "7368-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case LILITH:
						if (st.hasItems(LILITH_LETTER))
						{
							htmltext = "7368-05.htm";
						}
						else if (st.hasItems(THEON_DIARY))
						{
							htmltext = "7368-06.htm";
							st.takeItems(THEON_DIARY, 1);
							st.giveItems(5250, 1);
							st.rewardExpAndSp(3000, 0);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case BAENEDES:
						if (st.hasItems(LILITH_LETTER))
						{
							htmltext = "7369-01.htm";
						}
						else if (st.hasItems(THEON_DIARY))
						{
							htmltext = "7369-04.htm";
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
