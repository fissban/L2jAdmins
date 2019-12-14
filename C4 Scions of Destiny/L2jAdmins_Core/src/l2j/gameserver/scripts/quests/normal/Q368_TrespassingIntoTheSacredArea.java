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
public class Q368_TrespassingIntoTheSacredArea extends Script
{
	// NPC
	private static final int RESTINA = 7926;
	
	// Item
	private static final int FANG = 5881;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(794, 500000);
		CHANCES.put(795, 770000);
		CHANCES.put(796, 500000);
		CHANCES.put(797, 480000);
	}
	
	public Q368_TrespassingIntoTheSacredArea()
	{
		super(368, "Trespassing into the Sacred Area");
		
		registerItems(FANG);
		
		addStartNpc(RESTINA);
		addTalkId(RESTINA);
		
		addKillId(794, 795, 796, 797);
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
		
		if (event.equalsIgnoreCase("7926-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7926-05.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
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
				htmltext = player.getLevel() < 36 ? "7926-01a.htm" : "7926-01.htm";
				break;
			
			case STARTED:
				final int fangs = st.getItemsCount(FANG);
				if (fangs == 0)
				{
					htmltext = "7926-03.htm";
				}
				else
				{
					final int reward = (250 * fangs) + (fangs > 10 ? 5730 : 2000);
					
					htmltext = "7926-04.htm";
					st.takeItems(5881, -1);
					st.rewardItems(Inventory.ADENA_ID, reward);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		partyMember.getScriptState(getName()).dropItems(FANG, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
