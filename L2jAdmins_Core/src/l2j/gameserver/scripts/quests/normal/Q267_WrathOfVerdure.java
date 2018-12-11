package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q267_WrathOfVerdure extends Script
{
	// NPCs
	private static final int BREMAC = 12092;
	// MOBs
	private static final int GOBLIN = 325;
	// ITEMs
	private static final int GOBLIN_CLUB = 1335;
	// REWARDs
	private static final int SILVERY_LEAF = 1340;
	
	public Q267_WrathOfVerdure()
	{
		super(267, "Wrath of Verdure");
		
		registerItems(GOBLIN_CLUB);
		
		addStartNpc(BREMAC);
		addTalkId(BREMAC);
		addKillId(GOBLIN);
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
		
		if (event.equalsIgnoreCase("8853-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8853-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "8853-00.htm";
				}
				else if (player.getLevel() < 4)
				{
					htmltext = "8853-01.htm";
				}
				else
				{
					htmltext = "8853-02.htm";
				}
				break;
			
			case STARTED:
				final int count = st.getItemsCount(GOBLIN_CLUB);
				if (count > 0)
				{
					htmltext = "8853-05.htm";
					st.takeItems(GOBLIN_CLUB, -1);
					st.rewardItems(SILVERY_LEAF, count);
				}
				else
				{
					htmltext = "8853-04.htm";
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
		
		st.dropItems(GOBLIN_CLUB, 1, 0, 500000);
		
		return null;
	}
	
}
