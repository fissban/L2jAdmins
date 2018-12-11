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
public class Q608_SlayTheEnemyCommander extends Script
{
	// Quest Items
	private static final int HEAD_OF_MOS = 7236;
	private static final int TOTEM_OF_WISDOM = 7220;
	private static final int KETRA_ALLIANCE_4 = 7214;
	
	public Q608_SlayTheEnemyCommander()
	{
		super(608, "Slay the enemy commander!");
		
		registerItems(HEAD_OF_MOS);
		
		addStartNpc(8370); // Kadun Zu Ketra
		addTalkId(8370);
		
		addKillId(10312); // Mos
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
		
		if (event.equalsIgnoreCase("8370-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8370-07.htm"))
		{
			if (st.hasItems(HEAD_OF_MOS))
			{
				st.takeItems(HEAD_OF_MOS, -1);
				st.giveItems(TOTEM_OF_WISDOM, 1);
				st.rewardExpAndSp(10000, 0);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "8370-06.htm";
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
					if ((player.getAllianceWithVarkaKetra() >= 4) && st.hasItems(KETRA_ALLIANCE_4) && !st.hasItems(TOTEM_OF_WISDOM))
					{
						htmltext = "8370-01.htm";
					}
					else
					{
						htmltext = "8370-02.htm";
					}
				}
				else
				{
					htmltext = "8370-03.htm";
				}
				break;
			
			case STARTED:
				htmltext = (st.hasItems(HEAD_OF_MOS)) ? "8370-05.htm" : "8370-06.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "1"))
		{
			if (partyMember.getAllianceWithVarkaKetra() >= 4)
			{
				ScriptState st = partyMember.getScriptState(getName());
				if (st.hasItems(KETRA_ALLIANCE_4))
				{
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(HEAD_OF_MOS, 1);
				}
			}
		}
		
		return null;
	}
	
}
