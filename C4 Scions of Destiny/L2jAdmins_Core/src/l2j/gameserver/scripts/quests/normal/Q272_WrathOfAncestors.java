package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q272_WrathOfAncestors extends Script
{
	// NPCs
	private static final int LIVINA = 7572;
	// MOBs
	private static final int GOBLIN_GRAVE_ROBBER = 319;
	private static final int GOBLIN_TOMB_RIDER_LEADER = 320;
	// ITEMs
	private static final int GRAVE_ROBBERS_HEAD = 1474;
	
	public Q272_WrathOfAncestors()
	{
		super(272, "Wrath of Ancestors");
		
		registerItems(GRAVE_ROBBERS_HEAD);
		
		addStartNpc(LIVINA);
		addTalkId(LIVINA);
		
		addKillId(GOBLIN_GRAVE_ROBBER, GOBLIN_TOMB_RIDER_LEADER);
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
		
		if (event.equalsIgnoreCase("7572-03.htm"))
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7572-00.htm";
				}
				else if (player.getLevel() < 5)
				{
					htmltext = "7572-01.htm";
				}
				else
				{
					htmltext = "7572-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7572-04.htm";
				}
				else
				{
					htmltext = "7572-05.htm";
					st.takeItems(GRAVE_ROBBERS_HEAD, -1);
					st.rewardItems(Inventory.ADENA_ID, 1500);
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
		
		if (st.dropItemsAlways(GRAVE_ROBBERS_HEAD, 1, 50))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
