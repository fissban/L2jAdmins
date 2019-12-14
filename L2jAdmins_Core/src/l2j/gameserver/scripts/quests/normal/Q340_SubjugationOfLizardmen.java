package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q340_SubjugationOfLizardmen extends Script
{
	// NPCs
	private static final int WEISZ = 7385;
	private static final int ADONIUS = 7375;
	private static final int LEVIAN = 7037;
	private static final int CHEST = 7989;
	
	// ITEMs
	private static final int CARGO = 4255;
	private static final int HOLY = 4256;
	private static final int ROSARY = 4257;
	private static final int TOTEM = 4258;
	
	public Q340_SubjugationOfLizardmen()
	{
		super(340, "Subjugation of Lizardmen");
		
		registerItems(CARGO, HOLY, ROSARY, TOTEM);
		
		addStartNpc(WEISZ);
		addTalkId(WEISZ, ADONIUS, LEVIAN, CHEST);
		
		addKillId(8, 10, 14, 24, 27, 30, 10146);
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
		
		if (event.equalsIgnoreCase("7385-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7385-07.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(CARGO, -1);
		}
		else if (event.equalsIgnoreCase("7385-09.htm"))
		{
			st.takeItems(CARGO, -1);
			st.rewardItems(Inventory.ADENA_ID, 4090);
		}
		else if (event.equalsIgnoreCase("7385-10.htm"))
		{
			st.takeItems(CARGO, -1);
			st.rewardItems(Inventory.ADENA_ID, 4090);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7375-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7037-02.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7989-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(TOTEM, 1);
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
				htmltext = player.getLevel() < 17 ? "7385-01.htm" : "7385-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case WEISZ:
						if (cond == 1)
						{
							htmltext = st.getItemsCount(CARGO) < 30 ? "7385-05.htm" : "7385-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7385-11.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7385-13.htm";
							st.rewardItems(Inventory.ADENA_ID, 14700);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case ADONIUS:
						if (cond == 2)
						{
							htmltext = "7375-01.htm";
						}
						else if (cond == 3)
						{
							if (st.hasItems(ROSARY, HOLY))
							{
								htmltext = "7375-04.htm";
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(HOLY, -1);
								st.takeItems(ROSARY, -1);
							}
							else
							{
								htmltext = "7375-03.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "7375-05.htm";
						}
						break;
					
					case LEVIAN:
						if (cond == 4)
						{
							htmltext = "7037-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7037-03.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7037-04.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(TOTEM, -1);
						}
						else if (cond == 7)
						{
							htmltext = "7037-05.htm";
						}
						break;
					
					case CHEST:
						if (cond == 5)
						{
							htmltext = "7989-01.htm";
						}
						else
						{
							htmltext = "7989-03.htm";
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
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case 8:
				if (st.getInt("cond") == 1)
				{
					st.dropItems(CARGO, 1, 30, 500000);
				}
				break;
			
			case 10:
				if (st.getInt("cond") == 1)
				{
					st.dropItems(CARGO, 1, 30, 520000);
				}
				break;
			
			case 14:
				if (st.getInt("cond") == 1)
				{
					st.dropItems(CARGO, 1, 30, 550000);
				}
				break;
			
			case 24:
			case 27:
			case 30:
				if (st.getInt("cond") == 3)
				{
					if (st.dropItems(HOLY, 1, 1, 100000))
					{
						st.dropItems(ROSARY, 1, 1, 100000);
					}
				}
				break;
			
			case 10146:
				addSpawn(CHEST, npc, false, 30000);
				break;
		}
		return null;
	}
	
}
