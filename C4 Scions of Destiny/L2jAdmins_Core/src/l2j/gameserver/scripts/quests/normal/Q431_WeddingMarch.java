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
public class Q431_WeddingMarch extends Script
{
	// NPC
	private static final int KANTABILON = 8042;
	
	// Item
	private static final int SILVER_CRYSTAL = 7540;
	
	// Reward
	private static final int WEDDING_ECHO_CRYSTAL = 7062;
	
	public Q431_WeddingMarch()
	{
		super(431, "Wedding March");
		
		registerItems(SILVER_CRYSTAL);
		
		addStartNpc(KANTABILON);
		addTalkId(KANTABILON);
		
		addKillId(786, 787);
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
		
		if (event.equalsIgnoreCase("8042-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8042-05.htm"))
		{
			if (st.getItemsCount(SILVER_CRYSTAL) < 50)
			{
				htmltext = "8042-03.htm";
			}
			else
			{
				st.takeItems(SILVER_CRYSTAL, -1);
				st.giveItems(WEDDING_ECHO_CRYSTAL, 25);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
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
				htmltext = (player.getLevel() < 38) ? "8042-00.htm" : "8042-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8042-02.htm";
				}
				else if (cond == 2)
				{
					htmltext = (st.getItemsCount(SILVER_CRYSTAL) < 50) ? "8042-03.htm" : "8042-04.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		if (st.dropItems(SILVER_CRYSTAL, 1, 50, 500000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
