package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q277_GatekeepersOffering extends Script
{
	// NPCs
	private static final int TAMIL = 7576;
	// MOBs
	private static final int GOLEM = 333;
	// ITEMs
	private static final int STARSTONE = 1572;
	// REWARDs
	private static final int GATEKEEPER_CHARM = 1658;
	
	public Q277_GatekeepersOffering()
	{
		super(277, "Gatekeeper's Offering");
		
		addStartNpc(TAMIL);
		addTalkId(TAMIL);
		
		addKillId(GOLEM);
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
		
		if (event.equalsIgnoreCase("7576-03.htm"))
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
				htmltext = player.getLevel() < 15 ? "7576-01.htm" : "7576-02.htm";
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7576-04.htm";
				}
				else
				{
					htmltext = "7576-05.htm";
					st.takeItems(STARSTONE, -1);
					st.rewardItems(GATEKEEPER_CHARM, 2);
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
		
		if (st.dropItems(STARSTONE, 1, 20, 500000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
