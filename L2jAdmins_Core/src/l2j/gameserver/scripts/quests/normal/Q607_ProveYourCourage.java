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
public class Q607_ProveYourCourage extends Script
{
	// Items
	private static final int HEAD_OF_SHADITH = 7235;
	private static final int TOTEM_OF_VALOR = 7219;
	private static final int KETRA_ALLIANCE_3 = 7213;
	
	public Q607_ProveYourCourage()
	{
		super(607, "Prove your courage!");
		
		registerItems(HEAD_OF_SHADITH);
		
		addStartNpc(8370); // Kadun Zu Ketra
		addTalkId(8370);
		
		addKillId(10309); // Shadith
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
			if (st.hasItems(HEAD_OF_SHADITH))
			{
				st.takeItems(HEAD_OF_SHADITH, -1);
				st.giveItems(TOTEM_OF_VALOR, 1);
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
				if (player.getLevel() < 75)
				{
					htmltext = "8370-03.htm";
				}
				else if ((player.getAllianceWithVarkaKetra() >= 3) && st.hasItems(KETRA_ALLIANCE_3) && !st.hasItems(TOTEM_OF_VALOR))
				{
					htmltext = "8370-01.htm";
				}
				else
				{
					htmltext = "8370-02.htm";
				}
				break;
			
			case STARTED:
				htmltext = (st.hasItems(HEAD_OF_SHADITH)) ? "8370-05.htm" : "8370-06.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "1"))
		{
			if (partyMember.getAllianceWithVarkaKetra() >= 3)
			{
				ScriptState st = partyMember.getScriptState(getName());
				if (st.hasItems(KETRA_ALLIANCE_3))
				{
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(HEAD_OF_SHADITH, 1);
				}
			}
		}
		
		return null;
	}
	
}
