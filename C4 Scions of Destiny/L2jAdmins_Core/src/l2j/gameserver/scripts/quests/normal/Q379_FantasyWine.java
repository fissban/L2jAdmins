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
public class Q379_FantasyWine extends Script
{
	// NPCs
	private static final int HARLAN = 7074;
	
	// Monsters
	private static final int ENKU_CHAMPION = 291;
	private static final int ENKU_SHAMAN = 292;
	
	// Items
	private static final int LEAF = 5893;
	private static final int STONE = 5894;
	
	public Q379_FantasyWine()
	{
		super(379, "Fantasy Wine");
		
		registerItems(LEAF, STONE);
		
		addStartNpc(HARLAN);
		addTalkId(HARLAN);
		
		addKillId(ENKU_CHAMPION, ENKU_SHAMAN);
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
		
		if (event.equalsIgnoreCase("7074-3.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7074-6.htm"))
		{
			st.takeItems(LEAF, 80);
			st.takeItems(STONE, 100);
			
			final int rand = Rnd.get(10);
			if (rand < 3)
			{
				htmltext = "7074-6.htm";
				st.giveItems(5956, 1);
			}
			else if (rand < 9)
			{
				htmltext = "7074-7.htm";
				st.giveItems(5957, 1);
			}
			else
			{
				htmltext = "7074-8.htm";
				st.giveItems(5958, 1);
			}
			
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7074-2a.htm"))
		{
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
				htmltext = player.getLevel() < 20 ? "7074-0a.htm" : "7074-0.htm";
				break;
			
			case STARTED:
				final int leaf = st.getItemsCount(LEAF);
				final int stone = st.getItemsCount(STONE);
				
				if ((leaf == 80) && (stone == 100))
				{
					htmltext = "7074-5.htm";
				}
				else if (leaf == 80)
				{
					htmltext = "7074-4a.htm";
				}
				else if (stone == 100)
				{
					htmltext = "7074-4b.htm";
				}
				else
				{
					htmltext = "7074-4.htm";
				}
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
		
		if (npc.getId() == ENKU_CHAMPION)
		{
			if (st.dropItemsAlways(LEAF, 1, 80) && (st.getItemsCount(STONE) >= 100))
			{
				st.set("cond", "2");
			}
		}
		else if (st.dropItemsAlways(STONE, 1, 100) && (st.getItemsCount(LEAF) >= 80))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
