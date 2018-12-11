package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q291_RevengeOfTheRedbonnet extends Script
{
	// NPCs
	private static final int MARYSE_DEDBONNET = 7553;
	// MOBs
	private static final int DARKWING_BAT = 317;
	// ITEMs
	private static final int BLACK_WOLF_PELT = 1482;
	// REWARDs
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int GRANDMA_PEARL = 1502;
	private static final int GRANDMA_MIRROR = 1503;
	private static final int GRANDMA_NECKLACE = 1504;
	private static final int GRANDMA_HAIRPIN = 1505;
	
	public Q291_RevengeOfTheRedbonnet()
	{
		super(291, "Revenge of the Redbonnet");
		
		registerItems(BLACK_WOLF_PELT);
		
		addStartNpc(MARYSE_DEDBONNET);
		addTalkId(MARYSE_DEDBONNET);
		
		addKillId(DARKWING_BAT);
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
		
		if (event.equalsIgnoreCase("7553-03.htm"))
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
				htmltext = player.getLevel() < 4 ? "7553-01.htm" : "7553-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7553-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7553-05.htm";
					st.takeItems(BLACK_WOLF_PELT, -1);
					
					final int random = Rnd.get(100);
					if (random < 3)
					{
						st.rewardItems(GRANDMA_PEARL, 1);
					}
					else if (random < 21)
					{
						st.rewardItems(GRANDMA_MIRROR, 1);
					}
					else if (random < 46)
					{
						st.rewardItems(GRANDMA_NECKLACE, 1);
					}
					else
					{
						st.rewardItems(SCROLL_OF_ESCAPE, 1);
						st.rewardItems(GRANDMA_HAIRPIN, 1);
					}
					
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
		
		if (st.dropItemsAlways(BLACK_WOLF_PELT, 1, 40))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
