package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
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
public class Q366_SilverHairedShaman extends Script
{
	// NPC
	private static final int DIETER = 7111;
	// Item
	private static final int HAIR = 5874;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(986, 560000);
		CHANCES.put(987, 660000);
		CHANCES.put(988, 620000);
	}
	
	public Q366_SilverHairedShaman()
	{
		super(366, "Silver Haired Shaman");
		
		registerItems(HAIR);
		
		addStartNpc(DIETER);
		addTalkId(DIETER);
		
		addKillId(986, 987, 988);
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
		
		if (event.equalsIgnoreCase("7111-2.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7111-6.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() < 48 ? "7111-0.htm" : "7111-1.htm";
				break;
			
			case STARTED:
				final int count = st.getItemsCount(HAIR);
				if (count == 0)
				{
					htmltext = "7111-3.htm";
				}
				else
				{
					htmltext = "7111-4.htm";
					st.takeItems(HAIR, -1);
					st.rewardItems(Inventory.ADENA_ID, 12070 + (500 * count));
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		partyMember.getScriptState(getName()).dropItems(HAIR, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
