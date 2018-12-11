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
public class Q157_RecoverSmuggledGoods extends Script
{
	// NPCs
	private static final int WILFORD = 7005;
	// MOBs
	private static final int GIANT_TOAD = 121;
	// ITEMs
	private static final int ADAMANTITE_ORE = 1024;
	// REWARDs
	private static final int BUCKLER = 20;
	
	public Q157_RecoverSmuggledGoods()
	{
		super(157, "Recover Smuggled Goods");
		
		registerItems(ADAMANTITE_ORE);
		
		addStartNpc(WILFORD);
		addTalkId(WILFORD);
		addKillId(GIANT_TOAD);
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
		
		if (event.equalsIgnoreCase("7005-05.htm"))
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
				htmltext = player.getLevel() < 5 ? "7005-02.htm" : "7005-03.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7005-06.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7005-07.htm";
					st.takeItems(ADAMANTITE_ORE, -1);
					st.giveItems(BUCKLER, 1);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
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
		
		if (st.dropItems(ADAMANTITE_ORE, 1, 20, 400000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
