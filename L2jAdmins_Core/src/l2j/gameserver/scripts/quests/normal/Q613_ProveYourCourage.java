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
public class Q613_ProveYourCourage extends Script
{
	// Items
	private static final int HEAD_OF_HEKATON = 7240;
	private static final int FEATHER_OF_VALOR = 7229;
	private static final int VARKA_ALLIANCE_3 = 7223;
	
	// Npcs
	private static final int ASHAS_VARKA_DURAI = 8377;
	private static final int HEKATON = 10299;
	
	public Q613_ProveYourCourage()
	{
		super(613, "Prove your courage!");
		
		registerItems(HEAD_OF_HEKATON);
		
		addStartNpc(ASHAS_VARKA_DURAI); // Ashas Varka Durai
		addTalkId(ASHAS_VARKA_DURAI);
		
		addKillId(HEKATON); // Hekaton
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
			if (st.hasItems(HEAD_OF_HEKATON))
			{
				st.takeItems(HEAD_OF_HEKATON, -1);
				st.giveItems(FEATHER_OF_VALOR, 1);
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
				if (player.getLevel() < 75)
				{
					htmltext = "8377-03.htm";
				}
				else if ((player.getAllianceWithVarkaKetra() <= -3) && st.hasItems(VARKA_ALLIANCE_3) && !st.hasItems(FEATHER_OF_VALOR))
				{
					htmltext = "8377-01.htm";
				}
				else
				{
					htmltext = "8377-02.htm";
				}
				break;
			
			case STARTED:
				htmltext = (st.hasItems(HEAD_OF_HEKATON)) ? "8377-05.htm" : "8377-06.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		for (L2PcInstance partyMember : getPartyMembers(player, npc, "cond", "1"))
		{
			if (partyMember.getAllianceWithVarkaKetra() <= -3)
			{
				ScriptState st = partyMember.getScriptState(getName());
				if (st.hasItems(VARKA_ALLIANCE_3))
				{
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(HEAD_OF_HEKATON, 1);
				}
			}
		}
		
		return null;
	}
	
}
