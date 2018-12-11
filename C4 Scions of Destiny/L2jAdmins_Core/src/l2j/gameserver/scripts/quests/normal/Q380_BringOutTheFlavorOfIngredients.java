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
public class Q380_BringOutTheFlavorOfIngredients extends Script
{
	// Monsters
	private static final int DIRE_WOLF = 205;
	private static final int KADIF_WEREWOLF = 206;
	private static final int GIANT_MIST_LEECH = 225;
	
	// Items
	private static final int RITRON_FRUIT = 5895;
	private static final int MOON_FACE_FLOWER = 5896;
	private static final int LEECH_FLUIDS = 5897;
	private static final int ANTIDOTE = 1831;
	
	// Rewards
	private static final int RITRON_JELLY = 5960;
	private static final int JELLY_RECIPE = 5959;
	
	public Q380_BringOutTheFlavorOfIngredients()
	{
		super(380, "Bring Out the Flavor of Ingredients!");
		
		registerItems(RITRON_FRUIT, MOON_FACE_FLOWER, LEECH_FLUIDS);
		
		addStartNpc(7069); // Rollant
		addTalkId(7069);
		
		addKillId(DIRE_WOLF, KADIF_WEREWOLF, GIANT_MIST_LEECH);
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
		
		if (event.equalsIgnoreCase("7069-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7069-12.htm"))
		{
			st.giveItems(JELLY_RECIPE, 1);
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
				htmltext = player.getLevel() < 24 ? "7069-00.htm" : "7069-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7069-06.htm";
				}
				else if (cond == 2)
				{
					if (st.getItemsCount(ANTIDOTE) >= 2)
					{
						htmltext = "7069-07.htm";
						st.set("cond", "3");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(RITRON_FRUIT, -1);
						st.takeItems(MOON_FACE_FLOWER, -1);
						st.takeItems(LEECH_FLUIDS, -1);
						st.takeItems(ANTIDOTE, 2);
					}
					else
					{
						htmltext = "7069-06.htm";
					}
				}
				else if (cond == 3)
				{
					htmltext = "7069-08.htm";
					st.set("cond", "4");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
				else if (cond == 4)
				{
					htmltext = "7069-09.htm";
					st.set("cond", "5");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
				else if (cond == 5)
				{
					htmltext = "7069-10.htm";
					st.set("cond", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
				else if (cond == 6)
				{
					st.giveItems(RITRON_JELLY, 1);
					if (Rnd.get(100) < 55)
					{
						htmltext = "7069-11.htm";
					}
					else
					{
						htmltext = "7069-13.htm";
						st.playSound(PlaySoundType.QUEST_FINISH);
						st.exitQuest(true);
					}
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case DIRE_WOLF:
				if (st.dropItems(RITRON_FRUIT, 1, 4, 100000))
				{
					if ((st.getItemsCount(MOON_FACE_FLOWER) == 20) && (st.getItemsCount(LEECH_FLUIDS) == 10))
					{
						st.set("cond", "2");
					}
				}
				break;
			
			case KADIF_WEREWOLF:
				if (st.dropItems(MOON_FACE_FLOWER, 1, 20, 500000))
				{
					if ((st.getItemsCount(RITRON_FRUIT) == 4) && (st.getItemsCount(LEECH_FLUIDS) == 10))
					{
						st.set("cond", "2");
					}
				}
				break;
			
			case GIANT_MIST_LEECH:
				if (st.dropItems(LEECH_FLUIDS, 1, 10, 500000))
				{
					if ((st.getItemsCount(RITRON_FRUIT) == 4) && (st.getItemsCount(MOON_FACE_FLOWER) == 20))
					{
						st.set("cond", "2");
					}
				}
				break;
		}
		
		return null;
	}
	
}
