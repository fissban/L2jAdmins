package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q364_JovialAccordion extends Script
{
	// NPCs
	private static final int BARBADO = 7959;
	private static final int SWAN = 7957;
	private static final int SABRIN = 7060;
	private static final int XABER = 7075;
	private static final int CLOTH_CHEST = 7961;
	private static final int BEER_CHEST = 7960;
	
	// ITEMs
	private static final int KEY_1 = 4323;
	private static final int KEY_2 = 4324;
	private static final int STOLEN_BEER = 4321;
	private static final int STOLEN_CLOTHES = 4322;
	private static final int ECHO = 4421;
	
	public Q364_JovialAccordion()
	{
		super(364, "Jovial Accordion");
		
		registerItems(KEY_1, KEY_2, STOLEN_BEER, STOLEN_CLOTHES);
		
		addStartNpc(BARBADO);
		addTalkId(BARBADO, SWAN, SABRIN, XABER, CLOTH_CHEST, BEER_CHEST);
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
		
		if (event.equalsIgnoreCase("7959-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("items", "0");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7957-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(KEY_1, 1);
			st.giveItems(KEY_2, 1);
		}
		else if (event.equalsIgnoreCase("7960-04.htm"))
		{
			if (st.hasItems(KEY_2))
			{
				st.takeItems(KEY_2, 1);
				if (Rnd.nextBoolean())
				{
					htmltext = "7960-02.htm";
					st.giveItems(STOLEN_BEER, 1);
					st.playSound(PlaySoundType.QUEST_ITEMGET);
				}
			}
		}
		else if (event.equalsIgnoreCase("7961-04.htm"))
		{
			if (st.hasItems(KEY_1))
			{
				st.takeItems(KEY_1, 1);
				if (Rnd.nextBoolean())
				{
					htmltext = "7961-02.htm";
					st.giveItems(STOLEN_CLOTHES, 1);
					st.playSound(PlaySoundType.QUEST_ITEMGET);
				}
			}
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
				htmltext = player.getLevel() < 15 ? "7959-00.htm" : "7959-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				final int stolenItems = st.getInt("items");
				
				switch (npc.getId())
				{
					case BARBADO:
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "7959-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7959-04.htm";
							st.giveItems(ECHO, 1);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case SWAN:
						if (cond == 1)
						{
							htmltext = "7957-01.htm";
						}
						else if (cond == 2)
						{
							if (stolenItems > 0)
							{
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								
								if (stolenItems == 2)
								{
									htmltext = "7957-04.htm";
									st.rewardItems(Inventory.ADENA_ID, 100);
								}
								else
								{
									htmltext = "7957-05.htm";
								}
							}
							else
							{
								if (!st.hasItems(KEY_1) && !st.hasItems(KEY_2))
								{
									htmltext = "7957-06.htm";
									st.playSound(PlaySoundType.QUEST_FINISH);
									st.exitQuest(true);
								}
								else
								{
									htmltext = "7957-03.htm";
								}
							}
						}
						else if (cond == 3)
						{
							htmltext = "7957-07.htm";
						}
						break;
					
					case BEER_CHEST:
						htmltext = "7960-03.htm";
						if ((cond == 2) && st.hasItems(KEY_2))
						{
							htmltext = "7960-01.htm";
						}
						break;
					
					case CLOTH_CHEST:
						htmltext = "7961-03.htm";
						if ((cond == 2) && st.hasItems(KEY_1))
						{
							htmltext = "7961-01.htm";
						}
						break;
					
					case SABRIN:
						if (st.hasItems(STOLEN_BEER))
						{
							htmltext = "7060-01.htm";
							st.set("items", String.valueOf(stolenItems + 1));
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.takeItems(STOLEN_BEER, 1);
						}
						else
						{
							htmltext = "7060-02.htm";
						}
						break;
					
					case XABER:
						if (st.hasItems(STOLEN_CLOTHES))
						{
							htmltext = "7075-01.htm";
							st.set("items", String.valueOf(stolenItems + 1));
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.takeItems(STOLEN_CLOTHES, 1);
						}
						else
						{
							htmltext = "7075-02.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
}
