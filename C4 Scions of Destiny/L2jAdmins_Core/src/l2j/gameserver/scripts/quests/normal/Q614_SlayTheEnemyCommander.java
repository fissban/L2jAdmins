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
public class Q614_SlayTheEnemyCommander extends Script
{
	// Quest Items
	private static final int HEAD_OF_TAYR = 7241;
	private static final int FEATHER_OF_WISDOM = 7230;
	private static final int VARKA_ALLIANCE_4 = 7224;
	
	// Npcs
	private static final int ASHAS_VARKA_DURAI = 8377;
	private static final int TAYR = 10302;
	
	public Q614_SlayTheEnemyCommander()
	{
		super(614, "Slay the enemy commander!");
		
		registerItems(HEAD_OF_TAYR);
		
		addStartNpc(ASHAS_VARKA_DURAI); // Ashas Varka Durai
		addTalkId(ASHAS_VARKA_DURAI);
		
		addKillId(TAYR); // Tayr
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8377-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8377-07.htm"))
		{
			if (st.hasItems(HEAD_OF_TAYR))
			{
				st.takeItems(HEAD_OF_TAYR, -1);
				st.giveItems(FEATHER_OF_WISDOM, 1);
				st.rewardExpAndSp(10000, 0);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8377-06.htm";
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getLevel() >= 75)
				{
					if ((player.getAllianceWithVarkaKetra() <= -4) && st.hasItems(VARKA_ALLIANCE_4) && !st.hasItems(FEATHER_OF_WISDOM))
					{
						htmltext = "8377-01.htm";
					}
					else
					{
						htmltext = "8377-02.htm";
					}
				}
				else
				{
					htmltext = "8377-03.htm";
				}
				break;
			
			case STARTED:
				htmltext = (st.hasItems(HEAD_OF_TAYR)) ? "8377-05.htm" : "8377-06.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "1"))
		{
			if (partyMember.getAllianceWithVarkaKetra() <= -4)
			{
				ScriptState st = partyMember.getScriptState(getName());
				if (st.hasItems(VARKA_ALLIANCE_4))
				{
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(HEAD_OF_TAYR, 1);
				}
			}
		}
		
		return null;
	}
	
}
