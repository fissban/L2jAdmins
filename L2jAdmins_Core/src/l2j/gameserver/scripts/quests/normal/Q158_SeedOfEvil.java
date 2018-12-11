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
public class Q158_SeedOfEvil extends Script
{
	// NPCs
	private static final int BIOIN = 7031;
	// MOBs
	private static final int NERKAS = 5016;
	// ITEMs
	private static final int CLAY_TABLET = 1025;
	// REWARDs
	private static final int ENCHANT_ARMOR_D = 956;
	
	public Q158_SeedOfEvil()
	{
		super(158, "Seed of Evil");
		
		registerItems(CLAY_TABLET);
		
		addStartNpc(BIOIN);
		addTalkId(BIOIN);
		addKillId(NERKAS);
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
		
		if (event.equalsIgnoreCase("7031-04.htm"))
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
				htmltext = player.getLevel() < 21 ? "7031-02.htm" : "7031-03.htm";
				break;
			
			case STARTED:
				if (!st.hasItems(CLAY_TABLET))
				{
					htmltext = "7031-05.htm";
				}
				else
				{
					htmltext = "7031-06.htm";
					st.takeItems(CLAY_TABLET, 1);
					st.giveItems(ENCHANT_ARMOR_D, 1);
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
		
		st.set("cond", "2");
		st.playSound(PlaySoundType.QUEST_MIDDLE);
		st.giveItems(CLAY_TABLET, 1);
		
		return null;
	}
	
}
