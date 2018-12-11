package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class Q622_SpecialtyLiquorDelivery extends Script
{
	// Items
	private static final int SPECIAL_DRINK = 7197;
	private static final int FEE_OF_SPECIAL_DRINK = 7198;
	
	// NPCs
	private static final int JEREMY = 8521;
	private static final int PULIN = 8543;
	private static final int NAFF = 8544;
	private static final int CROCUS = 8545;
	private static final int KUBER = 8546;
	private static final int BEOLIN = 8547;
	private static final int LIETTA = 8267;
	
	// Rewards
	private static final int ADENA = 57;
	private static final int HASTE_POTION = 1062;
	private static final int[] RECIPES =
	{
		6847,
		6849,
		6851
	};
	
	public Q622_SpecialtyLiquorDelivery()
	{
		super(622, "Specialty Liquor Delivery");
		
		registerItems(SPECIAL_DRINK, FEE_OF_SPECIAL_DRINK);
		
		addStartNpc(JEREMY);
		addTalkId(JEREMY, PULIN, NAFF, CROCUS, KUBER, BEOLIN, LIETTA);
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
		
		if (event.equalsIgnoreCase("8521-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(SPECIAL_DRINK, 5);
		}
		else if (event.equalsIgnoreCase("8547-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SPECIAL_DRINK, 1);
			st.giveItems(FEE_OF_SPECIAL_DRINK, 1);
		}
		else if (event.equalsIgnoreCase("8546-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SPECIAL_DRINK, 1);
			st.giveItems(FEE_OF_SPECIAL_DRINK, 1);
		}
		else if (event.equalsIgnoreCase("8545-02.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SPECIAL_DRINK, 1);
			st.giveItems(FEE_OF_SPECIAL_DRINK, 1);
		}
		else if (event.equalsIgnoreCase("8544-02.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SPECIAL_DRINK, 1);
			st.giveItems(FEE_OF_SPECIAL_DRINK, 1);
		}
		else if (event.equalsIgnoreCase("8543-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SPECIAL_DRINK, 1);
			st.giveItems(FEE_OF_SPECIAL_DRINK, 1);
		}
		else if (event.equalsIgnoreCase("8521-06.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(FEE_OF_SPECIAL_DRINK, 5);
		}
		else if (event.equalsIgnoreCase("8267-02.htm"))
		{
			if (Rnd.get(5) < 1)
			{
				st.giveItems(RECIPES[Rnd.get(RECIPES.length)], 1);
			}
			else
			{
				st.rewardItems(ADENA, 18800);
				st.rewardItems(HASTE_POTION, 1);
			}
			st.playSound(PlaySoundType.QUEST_FINISH);
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
				htmltext = (player.getLevel() < 68) ? "8521-03.htm" : "8521-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case JEREMY:
						if (cond < 6)
						{
							htmltext = "8521-04.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8521-05.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8521-06.htm";
						}
						break;
					
					case BEOLIN:
						if ((cond == 1) && (st.getItemsCount(SPECIAL_DRINK) == 5))
						{
							htmltext = "8547-01.htm";
						}
						else if (cond > 1)
						{
							htmltext = "8547-03.htm";
						}
						break;
					
					case KUBER:
						if ((cond == 2) && (st.getItemsCount(SPECIAL_DRINK) == 4))
						{
							htmltext = "8546-01.htm";
						}
						else if (cond > 2)
						{
							htmltext = "8546-03.htm";
						}
						break;
					
					case CROCUS:
						if ((cond == 3) && (st.getItemsCount(SPECIAL_DRINK) == 3))
						{
							htmltext = "8545-01.htm";
						}
						else if (cond > 3)
						{
							htmltext = "8545-03.htm";
						}
						break;
					
					case NAFF:
						if ((cond == 4) && (st.getItemsCount(SPECIAL_DRINK) == 2))
						{
							htmltext = "8544-01.htm";
						}
						else if (cond > 4)
						{
							htmltext = "8544-03.htm";
						}
						break;
					
					case PULIN:
						if ((cond == 5) && (st.getItemsCount(SPECIAL_DRINK) == 1))
						{
							htmltext = "8543-01.htm";
						}
						else if (cond > 5)
						{
							htmltext = "8543-03.htm";
						}
						break;
					
					case LIETTA:
						if (cond == 7)
						{
							htmltext = "8267-01.htm";
						}
						break;
				}
		}
		
		return htmltext;
	}
	
}
