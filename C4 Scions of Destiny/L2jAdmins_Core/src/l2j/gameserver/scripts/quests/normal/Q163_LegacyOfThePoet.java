package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q163_LegacyOfThePoet extends Script
{
	// NPC
	private static final int STARDEN = 7220;
	// MOBs
	private static final int BARAQ_ORC_FIGHTER = 372;
	private static final int BARAQ_ORC_WARRIOR_LEADER = 373;
	// ITEMs
	private static final int[] RUMIELS_POEMS =
	{
		1038,
		1039,
		1040,
		1041
	};
	// DROPLIST
	private static final int[][] DROPLIST =
	{
		{
			RUMIELS_POEMS[0],
			1,
			1,
			100000
		},
		{
			RUMIELS_POEMS[1],
			1,
			1,
			200000
		},
		{
			RUMIELS_POEMS[2],
			1,
			1,
			200000
		},
		{
			RUMIELS_POEMS[3],
			1,
			1,
			400000
		}
	};
	
	public Q163_LegacyOfThePoet()
	{
		super(163, "Legacy of the Poet");
		
		registerItems(RUMIELS_POEMS);
		
		addStartNpc(STARDEN);
		addTalkId(STARDEN);
		addKillId(BARAQ_ORC_FIGHTER, BARAQ_ORC_WARRIOR_LEADER);
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
		
		if (event.equalsIgnoreCase("7220-07.htm"))
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
				if (player.getRace() == Race.DARK_ELF)
				{
					htmltext = "7220-00.htm";
				}
				else if (player.getLevel() < 11)
				{
					htmltext = "7220-02.htm";
				}
				else
				{
					htmltext = "7220-03.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 2)
				{
					htmltext = "7220-09.htm";
					
					for (final int poem : RUMIELS_POEMS)
					{
						st.takeItems(poem, -1);
					}
					
					st.rewardItems(Inventory.ADENA_ID, 13890);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
				}
				else
				{
					htmltext = "7220-08.htm";
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
		
		if (st.dropMultipleItems(DROPLIST))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
