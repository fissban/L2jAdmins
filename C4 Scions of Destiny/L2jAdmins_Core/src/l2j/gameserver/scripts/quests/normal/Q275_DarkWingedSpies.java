package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q275_DarkWingedSpies extends Script
{
	// NPCs
	private static final int TANTUS = 7567;
	// MOBs
	private static final int DARKWING_BAT = 316;
	private static final int VARANGKA_TRACKER = 5043;
	// ITEMs
	private static final int DARKWING_BAT_FANG = 1478;
	private static final int VARANGKA_PARASITE = 1479;
	
	public Q275_DarkWingedSpies()
	{
		super(275, "Dark Winged Spies");
		
		registerItems(DARKWING_BAT_FANG, VARANGKA_PARASITE);
		
		addStartNpc(TANTUS);
		addTalkId(TANTUS);
		
		addKillId(DARKWING_BAT, VARANGKA_TRACKER);
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
		
		if (event.equalsIgnoreCase("7567-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7567-00.htm";
				}
				else if (player.getLevel() < 11)
				{
					htmltext = "7567-01.htm";
				}
				else
				{
					htmltext = "7567-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7567-04.htm";
				}
				else
				{
					htmltext = "7567-05.htm";
					st.takeItems(DARKWING_BAT_FANG, -1);
					st.takeItems(VARANGKA_PARASITE, -1);
					st.rewardItems(Inventory.ADENA_ID, 4200);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(true);
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
			case DARKWING_BAT:
				if (st.dropItemsAlways(DARKWING_BAT_FANG, 1, 70))
				{
					st.set("cond", "2");
				}
				else if ((Rnd.get(100) < 10) && (st.getItemsCount(DARKWING_BAT_FANG) > 10) && (st.getItemsCount(DARKWING_BAT_FANG) < 66))
				{
					// Spawn of Varangka Tracker on the npc position.
					addSpawn(VARANGKA_TRACKER, npc, true, 0);
					
					st.giveItems(VARANGKA_PARASITE, 1);
				}
				break;
			
			case VARANGKA_TRACKER:
				if (st.hasItems(VARANGKA_PARASITE))
				{
					st.takeItems(VARANGKA_PARASITE, -1);
					
					if (st.dropItemsAlways(DARKWING_BAT_FANG, 5, 70))
					{
						st.set("cond", "2");
					}
				}
				break;
		}
		
		return null;
	}
	
}
