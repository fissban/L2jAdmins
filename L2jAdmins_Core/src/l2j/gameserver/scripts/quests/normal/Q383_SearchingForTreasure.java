package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q383_SearchingForTreasure extends Script
{
	// NPCs
	private static final int ESPEN = 7890;
	private static final int PIRATE_CHEST = 8148;
	
	// Items
	private static final int PIRATE_TREASURE_MAP = 5915;
	private static final int THIEF_KEY = 1661;
	
	public Q383_SearchingForTreasure()
	{
		super(383, "Searching for Treasure");
		
		addStartNpc(ESPEN);
		addTalkId(ESPEN, PIRATE_CHEST);
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
		
		if (event.equalsIgnoreCase("7890-04.htm"))
		{
			// Sell the map.
			if (st.hasItems(PIRATE_TREASURE_MAP))
			{
				st.takeItems(PIRATE_TREASURE_MAP, 1);
				st.rewardItems(Inventory.ADENA_ID, 1000);
			}
			else
			{
				htmltext = "7890-06.htm";
			}
		}
		else if (event.equalsIgnoreCase("7890-07.htm"))
		{
			// Listen the story.
			if (st.hasItems(PIRATE_TREASURE_MAP))
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "7890-06.htm";
			}
		}
		else if (event.equalsIgnoreCase("7890-11.htm"))
		{
			// Decipher the map.
			if (st.hasItems(PIRATE_TREASURE_MAP))
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(PIRATE_TREASURE_MAP, 1);
			}
			else
			{
				htmltext = "7890-06.htm";
			}
		}
		else if (event.equalsIgnoreCase("8148-02.htm"))
		{
			if (st.hasItems(THIEF_KEY))
			{
				st.takeItems(THIEF_KEY, 1);
				
				// Adena reward.
				int i1 = 0;
				
				int i0 = Rnd.get(100);
				if (i0 < 5)
				{
					st.giveItems(2450, 1);
				}
				else if (i0 < 6)
				{
					st.giveItems(2451, 1);
				}
				else if (i0 < 18)
				{
					st.giveItems(956, 1);
				}
				else if (i0 < 28)
				{
					st.giveItems(952, 1);
				}
				else
				{
					i1 += 500;
				}
				
				i0 = Rnd.get(1000);
				if (i0 < 25)
				{
					st.giveItems(4481, 1);
				}
				else if (i0 < 50)
				{
					st.giveItems(4482, 1);
				}
				else if (i0 < 75)
				{
					st.giveItems(4483, 1);
				}
				else if (i0 < 100)
				{
					st.giveItems(4484, 1);
				}
				else if (i0 < 125)
				{
					st.giveItems(4485, 1);
				}
				else if (i0 < 150)
				{
					st.giveItems(4486, 1);
				}
				else if (i0 < 175)
				{
					st.giveItems(4487, 1);
				}
				else if (i0 < 200)
				{
					st.giveItems(4488, 1);
				}
				else if (i0 < 225)
				{
					st.giveItems(4489, 1);
				}
				else if (i0 < 250)
				{
					st.giveItems(4490, 1);
				}
				else if (i0 < 275)
				{
					st.giveItems(4491, 1);
				}
				else if (i0 < 300)
				{
					st.giveItems(4492, 1);
				}
				else
				{
					i1 += 300;
				}
				
				i0 = Rnd.get(100);
				if (i0 < 4)
				{
					st.giveItems(1337, 1);
				}
				else if (i0 < 8)
				{
					st.giveItems(1338, 2);
				}
				else if (i0 < 12)
				{
					st.giveItems(1339, 2);
				}
				else if (i0 < 16)
				{
					st.giveItems(3447, 2);
				}
				else if (i0 < 20)
				{
					st.giveItems(3450, 1);
				}
				else if (i0 < 25)
				{
					st.giveItems(3453, 1);
				}
				else if (i0 < 27)
				{
					st.giveItems(3456, 1);
				}
				else
				{
					i1 += 500;
				}
				
				i0 = Rnd.get(100);
				if (i0 < 20)
				{
					st.giveItems(4408, 1);
				}
				else if (i0 < 40)
				{
					st.giveItems(4409, 1);
				}
				else if (i0 < 60)
				{
					st.giveItems(4418, 1);
				}
				else if (i0 < 80)
				{
					st.giveItems(4419, 1);
				}
				else
				{
					i1 += 500;
				}
				
				st.rewardItems(Inventory.ADENA_ID, i1);
				
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8148-03.htm";
			}
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
				htmltext = (player.getLevel() < 42) || !st.hasItems(PIRATE_TREASURE_MAP) ? "7890-01.htm" : "7890-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case ESPEN:
						if (cond == 1)
						{
							htmltext = "7890-07a.htm";
						}
						else
						{
							htmltext = "7890-12.htm";
						}
						break;
					
					case PIRATE_CHEST:
						if (cond == 2)
						{
							htmltext = "8148-01.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
}
