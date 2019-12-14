package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

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
public class Q341_HuntingForWildBeasts extends Script
{
	// Item
	private static final int BEAR_SKIN = 4259;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	
	{
		CHANCES.put(21, 500000); // Red Bear
		CHANCES.put(203, 900000); // Dion Grizzly
		CHANCES.put(310, 500000); // Brown Bear
		CHANCES.put(335, 700000); // Grizzly Bear
	}
	
	public Q341_HuntingForWildBeasts()
	{
		super(341, "Hunting for Wild Beasts");
		
		registerItems(BEAR_SKIN);
		
		addStartNpc(7078); // Pano
		addTalkId(7078);
		
		addKillId(21, 203, 310, 335);
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
		
		if (event.equalsIgnoreCase("7078-02.htm"))
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
				htmltext = player.getLevel() < 20 ? "7078-00.htm" : "7078-01.htm";
				break;
			
			case STARTED:
				if (st.getItemsCount(BEAR_SKIN) < 20)
				{
					htmltext = "7078-03.htm";
				}
				else
				{
					htmltext = "7078-04.htm";
					st.takeItems(BEAR_SKIN, -1);
					st.rewardItems(Inventory.ADENA_ID, 3710);
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		st.dropItems(BEAR_SKIN, 1, 20, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
