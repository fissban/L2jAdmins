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
public class Q347_GoGetTheCalculator extends Script
{
	// NPCs
	private static final int BRUNON = 7526;
	private static final int SILVERA = 7527;
	private static final int SPIRON = 7532;
	private static final int BALANKI = 7533;
	
	// ITEMs
	private static final int GEMSTONE_BEAST_CRYSTAL = 4286;
	private static final int CALCULATOR_QUEST = 4285;
	private static final int CALCULATOR_REAL = 4393;
	
	public Q347_GoGetTheCalculator()
	{
		super(347, "Go Get the Calculator");
		
		registerItems(GEMSTONE_BEAST_CRYSTAL, CALCULATOR_QUEST);
		
		addStartNpc(BRUNON);
		addTalkId(BRUNON, SILVERA, SPIRON, BALANKI);
		
		addKillId(540);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7526-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7533-03.htm"))
		{
			if (st.getItemsCount(57) >= 100)
			{
				htmltext = "7533-02.htm";
				st.takeItems(Inventory.ADENA_ID, 100);
				
				if (st.getInt("cond") == 3)
				{
					st.set("cond", "4");
				}
				else
				{
					st.set("cond", "2");
				}
				
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("7532-02.htm"))
		{
			if (st.getInt("cond") == 2)
			{
				st.set("cond", "4");
			}
			else
			{
				st.set("cond", "3");
			}
			
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7526-08.htm"))
		{
			st.takeItems(CALCULATOR_QUEST, -1);
			st.giveItems(CALCULATOR_REAL, 1);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7526-09.htm"))
		{
			st.takeItems(CALCULATOR_QUEST, -1);
			st.rewardItems(Inventory.ADENA_ID, 1000);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() < 12 ? "7526-00.htm" : "7526-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case BRUNON:
						htmltext = !st.hasItems(CALCULATOR_QUEST) ? "7526-06.htm" : "7526-07.htm";
						break;
					
					case SPIRON:
						htmltext = cond < 4 ? "7532-01.htm" : "7532-05.htm";
						break;
					
					case BALANKI:
						htmltext = cond < 4 ? "7533-01.htm" : "7533-04.htm";
						break;
					
					case SILVERA:
						if (cond < 4)
						{
							htmltext = "7527-00.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7527-01.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 5)
						{
							if (st.getItemsCount(GEMSTONE_BEAST_CRYSTAL) < 10)
							{
								htmltext = "7527-02.htm";
							}
							else
							{
								htmltext = "7527-03.htm";
								st.set("cond", "6");
								st.takeItems(GEMSTONE_BEAST_CRYSTAL, -1);
								st.giveItems(CALCULATOR_QUEST, 1);
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (cond == 6)
						{
							htmltext = "7527-04.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "5");
		if (st == null)
		{
			return null;
		}
		
		st.dropItems(GEMSTONE_BEAST_CRYSTAL, 1, 10, 500000);
		
		return null;
	}
}
