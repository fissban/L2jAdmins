package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q362_BardsMandolin extends Script
{
	// ITEMs
	private static final int SWAN_FLUTE = 4316;
	private static final int SWAN_LETTER = 4317;
	
	// NPCs
	private static final int SWAN = 7957;
	private static final int NANARIN = 7956;
	private static final int GALION = 7958;
	private static final int WOODROW = 7837;
	
	public Q362_BardsMandolin()
	{
		super(362, "Bard's Mandolin");
		
		registerItems(SWAN_FLUTE, SWAN_LETTER);
		
		addStartNpc(SWAN);
		addTalkId(SWAN, NANARIN, GALION, WOODROW);
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
		
		if (event.equalsIgnoreCase("7957-3.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7957-7.htm") || event.equalsIgnoreCase("7957-8.htm"))
		{
			st.rewardItems(Inventory.ADENA_ID, 10000);
			st.giveItems(4410, 1);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() < 15 ? "7957-2.htm" : "7957-1.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case SWAN:
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "7957-4.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7957-5.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(SWAN_LETTER, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7957-5a.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7957-6.htm";
						}
						break;
					
					case WOODROW:
						if (cond == 1)
						{
							htmltext = "7837-1.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 2)
						{
							htmltext = "7837-2.htm";
						}
						else if (cond > 2)
						{
							htmltext = "7837-3.htm";
						}
						break;
					
					case GALION:
						if (cond == 2)
						{
							htmltext = "7958-1.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.giveItems(SWAN_FLUTE, 1);
						}
						else if (cond > 2)
						{
							htmltext = "7958-2.htm";
						}
						break;
					
					case NANARIN:
						if (cond == 4)
						{
							htmltext = "7956-1.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SWAN_FLUTE, 1);
							st.takeItems(SWAN_LETTER, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7956-2.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
}
