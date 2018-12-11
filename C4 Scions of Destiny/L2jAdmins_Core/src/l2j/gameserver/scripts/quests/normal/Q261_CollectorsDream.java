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
public class Q261_CollectorsDream extends Script
{
	// NPCs
	private static final int ALSHUPES = 7222;
	// MOBs
	private static final int HOOK_SPIDER = 308;
	private static final int CRIMSON_SPIDER = 460;
	private static final int PINCER_SPIDER = 466;
	// ITEMs
	private static final int GIANT_SPIDER_LEG = 1087;
	
	public Q261_CollectorsDream()
	{
		super(261, "Collector's Dream");
		
		registerItems(GIANT_SPIDER_LEG);
		
		addStartNpc(ALSHUPES);
		addTalkId(ALSHUPES);
		
		addKillId(HOOK_SPIDER, CRIMSON_SPIDER, PINCER_SPIDER);
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
		
		if (event.equalsIgnoreCase("7222-03.htm"))
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
				htmltext = player.getLevel() < 15 ? "7222-01.htm" : "7222-02.htm";
				break;
			
			case STARTED:
				if (st.getInt("cond") == 2)
				{
					htmltext = "7222-05.htm";
					st.takeItems(GIANT_SPIDER_LEG, -1);
					st.rewardItems(Inventory.ADENA_ID, 1000);
					st.rewardExpAndSp(2000, 0);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(true);
				}
				else
				{
					htmltext = "7222-04.htm";
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
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
		
		if (st.dropItemsAlways(GIANT_SPIDER_LEG, 1, 8))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
