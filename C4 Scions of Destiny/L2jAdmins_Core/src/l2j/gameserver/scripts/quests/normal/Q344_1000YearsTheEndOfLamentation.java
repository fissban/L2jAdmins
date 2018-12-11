package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

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
public class Q344_1000YearsTheEndOfLamentation extends Script
{
	// NPCs
	private static final int GILMORE = 7754;
	private static final int RODEMAI = 7756;
	private static final int ORVEN = 7857;
	private static final int KAIEN = 7623;
	private static final int GARVARENTZ = 7704;
	
	// ITEMs
	private static final int ARTICLE_DEAD_HERO = 4269;
	private static final int OLD_KEY = 4270;
	private static final int OLD_HILT = 4271;
	private static final int OLD_TOTEM = 4272;
	private static final int CRUCIFIX = 4273;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(236, 380000);
		CHANCES.put(237, 490000);
		CHANCES.put(238, 460000);
		CHANCES.put(239, 490000);
		CHANCES.put(240, 530000);
		CHANCES.put(272, 380000);
		CHANCES.put(273, 490000);
		CHANCES.put(274, 460000);
		CHANCES.put(275, 490000);
		CHANCES.put(276, 530000);
	}
	
	public Q344_1000YearsTheEndOfLamentation()
	{
		super(344, "1000 Years, the End of Lamentation");
		
		registerItems(ARTICLE_DEAD_HERO, OLD_KEY, OLD_HILT, OLD_TOTEM, CRUCIFIX);
		
		addStartNpc(GILMORE);
		addTalkId(GILMORE, RODEMAI, ORVEN, GARVARENTZ, KAIEN);
		
		addKillId(236, 237, 238, 239, 240, 272, 273, 274, 275, 276);
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
		
		if (event.equalsIgnoreCase("7754-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7754-07.htm"))
		{
			if (st.get("success") != null)
			{
				st.set("cond", "1");
				st.unset("success");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("7754-08.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7754-06.htm"))
		{
			if (!st.hasItems(ARTICLE_DEAD_HERO))
			{
				htmltext = "7754-06a.htm";
			}
			else
			{
				final int amount = st.getItemsCount(ARTICLE_DEAD_HERO);
				
				st.takeItems(ARTICLE_DEAD_HERO, -1);
				st.giveItems(Inventory.ADENA_ID, amount * 60);
				
				// Special item, % based on actual number of qItems.
				if (Rnd.get(1000) < Math.min(10, Math.max(1, amount / 10)))
				{
					htmltext = "7754-10.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("7754-11.htm"))
		{
			final int random = Rnd.get(4);
			if (random < 1)
			{
				htmltext = "7754-12.htm";
				st.giveItems(OLD_KEY, 1);
			}
			else if (random < 2)
			{
				htmltext = "7754-13.htm";
				st.giveItems(OLD_HILT, 1);
			}
			else if (random < 3)
			{
				htmltext = "7754-14.htm";
				st.giveItems(OLD_TOTEM, 1);
			}
			else
			{
				st.giveItems(CRUCIFIX, 1);
			}
			
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
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
				htmltext = player.getLevel() < 48 ? "7754-01.htm" : "7754-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case GILMORE:
						if (cond == 1)
						{
							htmltext = st.hasItems(ARTICLE_DEAD_HERO) ? "7754-05.htm" : "7754-09.htm";
						}
						else if (cond == 2)
						{
							htmltext = st.get("success") != null ? "7754-16.htm" : "7754-15.htm";
						}
						break;
					
					default:
						if (cond == 2)
						{
							if (st.get("success") != null)
							{
								htmltext = npc.getId() + "-02.htm";
							}
							else
							{
								rewards(st, npc.getId());
								htmltext = npc.getId() + "-01.htm";
							}
						}
						break;
				}
				break;
		}
		return htmltext;
	}
	
	private void rewards(ScriptState st, int npcId)
	{
		switch (npcId)
		{
			case ORVEN:
				if (st.hasItems(CRUCIFIX))
				{
					st.set("success", "1");
					st.takeItems(CRUCIFIX, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 80)
					{
						st.giveItems(1875, 19);
					}
					else if (chance < 95)
					{
						st.giveItems(952, 5);
					}
					else
					{
						st.giveItems(2437, 1);
					}
				}
				break;
			
			case GARVARENTZ:
				if (st.hasItems(OLD_TOTEM))
				{
					st.set("success", "1");
					st.takeItems(OLD_TOTEM, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 55)
					{
						st.giveItems(1882, 70);
					}
					else if (chance < 99)
					{
						st.giveItems(1881, 50);
					}
					else
					{
						st.giveItems(191, 1);
					}
				}
				break;
			
			case KAIEN:
				if (st.hasItems(OLD_HILT))
				{
					st.set("success", "1");
					st.takeItems(OLD_HILT, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 60)
					{
						st.giveItems(1874, 25);
					}
					else if (chance < 85)
					{
						st.giveItems(1887, 10);
					}
					else if (chance < 99)
					{
						st.giveItems(951, 1);
					}
					else
					{
						st.giveItems(133, 1);
					}
				}
				break;
			
			case RODEMAI:
				if (st.hasItems(OLD_KEY))
				{
					st.set("success", "1");
					st.takeItems(OLD_KEY, -1);
					
					final int chance = Rnd.get(100);
					if (chance < 80)
					{
						st.giveItems(1879, 55);
					}
					else if (chance < 95)
					{
						st.giveItems(951, 1);
					}
					else
					{
						st.giveItems(885, 1);
					}
				}
				break;
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		st.dropItems(ARTICLE_DEAD_HERO, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
