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

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q324_SweetestVenom extends Script
{
	// Item
	private static final int VENOM_SAC = 1077;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(34, 220000);
		CHANCES.put(38, 230000);
		CHANCES.put(43, 250000);
	}
	
	public Q324_SweetestVenom()
	{
		super(324, "Sweetest Venom");
		
		registerItems(VENOM_SAC);
		
		addStartNpc(7351); // Astaron
		addTalkId(7351);
		
		addKillId(34, 38, 43);
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
		
		if (event.equalsIgnoreCase("7351-04.htm"))
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
				htmltext = player.getLevel() < 18 ? "7351-02.htm" : "7351-03.htm";
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7351-05.htm";
				}
				else
				{
					htmltext = "7351-06.htm";
					st.takeItems(VENOM_SAC, -1);
					st.rewardItems(Inventory.ADENA_ID, 5810);
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
		
		if (st.dropItems(VENOM_SAC, 1, 10, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
}
