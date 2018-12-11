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
public class Q298_LizardmensConspiracy extends Script
{
	// NPCs
	private static final int PRAGA = 7333;
	private static final int ROHMER = 7344;
	
	// ITEMs
	private static final int PATROL_REPORT = 7182;
	private static final int WHITE_GEM = 7183;
	private static final int RED_GEM = 7184;
	
	public Q298_LizardmensConspiracy()
	{
		super(298, "Lizardmen's Conspiracy");
		
		registerItems(PATROL_REPORT, WHITE_GEM, RED_GEM);
		
		addStartNpc(PRAGA);
		addTalkId(PRAGA, ROHMER);
		// FIXME identificar estos npc
		addKillId(926, 927, 922, 923, 924);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7333-1.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(PATROL_REPORT, 1);
		}
		else if (event.equalsIgnoreCase("7344-1.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(PATROL_REPORT, 1);
		}
		else if (event.equalsIgnoreCase("7344-4.htm"))
		{
			if (st.getInt("cond") == 3)
			{
				htmltext = "7344-3.htm";
				st.takeItems(WHITE_GEM, -1);
				st.takeItems(RED_GEM, -1);
				st.rewardExpAndSp(0, 42000);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
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
				htmltext = player.getLevel() < 25 ? "7333-0b.htm" : "7333-0a.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case PRAGA:
						htmltext = "7333-2.htm";
						break;
					
					case ROHMER:
						if (st.getInt("cond") == 1)
						{
							htmltext = st.hasItems(PATROL_REPORT) ? "7344-0.htm" : "7344-0a.htm";
						}
						else
						{
							htmltext = "7344-2.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMember(player, npc, "2");
		if (partyMember == null)
		{
			return null;
		}
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		switch (npc.getId())
		{
			case 922:
				if (st.dropItems(WHITE_GEM, 1, 50, 400000) && (st.getItemsCount(RED_GEM) >= 50))
				{
					st.set("cond", "3");
				}
				break;
			
			case 923:
				if (st.dropItems(WHITE_GEM, 1, 50, 450000) && (st.getItemsCount(RED_GEM) >= 50))
				{
					st.set("cond", "3");
				}
				break;
			
			case 924:
				if (st.dropItems(WHITE_GEM, 1, 50, 350000) && (st.getItemsCount(RED_GEM) >= 50))
				{
					st.set("cond", "3");
				}
				break;
			
			case 926:
			case 927:
				if (st.dropItems(RED_GEM, 1, 50, 400000) && (st.getItemsCount(WHITE_GEM) >= 50))
				{
					st.set("cond", "3");
				}
				break;
		}
		
		return null;
	}
	
}
