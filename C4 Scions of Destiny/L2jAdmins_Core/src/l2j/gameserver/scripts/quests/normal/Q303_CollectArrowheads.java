package l2j.gameserver.scripts.quests.normal;

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
public class Q303_CollectArrowheads extends Script
{
	// Item
	private static final int ORCISH_ARROWHEAD = 963;
	
	public Q303_CollectArrowheads()
	{
		super(303, "Collect Arrowheads");
		
		registerItems(ORCISH_ARROWHEAD);
		
		addStartNpc(7029); // Minia
		addTalkId(7029);
		
		addKillId(361);
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
		
		if (event.equalsIgnoreCase("7029-03.htm"))
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
				htmltext = player.getLevel() < 10 ? "7029-01.htm" : "7029-02.htm";
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7029-04.htm";
				}
				else
				{
					htmltext = "7029-05.htm";
					st.takeItems(ORCISH_ARROWHEAD, -1);
					st.rewardItems(Inventory.ADENA_ID, 1000);
					st.rewardExpAndSp(2000, 0);
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
		
		if (st.dropItems(ORCISH_ARROWHEAD, 1, 10, 400000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
