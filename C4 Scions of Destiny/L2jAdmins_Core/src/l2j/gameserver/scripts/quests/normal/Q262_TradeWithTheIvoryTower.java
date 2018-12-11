package l2j.gameserver.scripts.quests.normal;

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
public class Q262_TradeWithTheIvoryTower extends Script
{
	// NPCs
	private static final int VOLLODOS = 7137;
	// MOBs
	private static final int GREEN_FUNGUS = 7;
	private static final int BLOOD_FUNGUS = 400;
	// ITEMs
	private static final int FUNGUS_SAC = 707;
	
	public Q262_TradeWithTheIvoryTower()
	{
		super(262, "Trade with the Ivory Tower");
		
		registerItems(FUNGUS_SAC);
		
		addStartNpc(VOLLODOS);
		addTalkId(VOLLODOS);
		addKillId(BLOOD_FUNGUS, GREEN_FUNGUS);
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
		
		if (event.equalsIgnoreCase("7137-03.htm"))
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
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 8 ? "7137-01.htm" : "7137-02.htm";
				break;
			
			case STARTED:
				if (st.getItemsCount(FUNGUS_SAC) < 10)
				{
					htmltext = "7137-04.htm";
				}
				else
				{
					htmltext = "7137-05.htm";
					st.takeItems(FUNGUS_SAC, -1);
					st.rewardItems(Inventory.ADENA_ID, 3000);
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
		
		int rate = 0;
		switch (npc.getId())
		{
			case BLOOD_FUNGUS:
				rate = 400000;
				break;
			
			case GREEN_FUNGUS:
				rate = 300000;
				break;
		}
		
		if (st.dropItems(FUNGUS_SAC, 1, 10, rate))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
