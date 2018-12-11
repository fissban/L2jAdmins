package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q300_HuntingLetoLizardman extends Script
{
	// Item
	private static final int BRACELET = 7139;
	
	// Monsters
	private static final int LETO_LIZARDMAN = 577;
	private static final int LETO_LIZARDMAN_ARCHER = 578;
	private static final int LETO_LIZARDMAN_SOLDIER = 579;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(LETO_LIZARDMAN, 300000);
		CHANCES.put(LETO_LIZARDMAN_ARCHER, 320000);
		CHANCES.put(LETO_LIZARDMAN_SOLDIER, 350000);
		CHANCES.put(LETO_LIZARDMAN_WARRIOR, 650000);
		CHANCES.put(LETO_LIZARDMAN_OVERLORD, 700000);
	}
	
	public Q300_HuntingLetoLizardman()
	{
		super(300, "Hunting Leto Lizardman");
		
		registerItems(BRACELET);
		
		addStartNpc(7126); // Rath
		addTalkId(7126);
		
		addKillId(LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_OVERLORD);
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
		
		if (event.equalsIgnoreCase("7126-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7126-05.htm"))
		{
			if (st.getItemsCount(BRACELET) >= 60)
			{
				htmltext = "7126-06.htm";
				st.takeItems(BRACELET, -1);
				
				final int luck = Rnd.get(3);
				if (luck == 0)
				{
					st.rewardItems(Inventory.ADENA_ID, 30000);
				}
				else if (luck == 1)
				{
					st.rewardItems(1867, 50);
				}
				else if (luck == 2)
				{
					st.rewardItems(1872, 50);
				}
				
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
				htmltext = player.getLevel() < 34 ? "7126-01.htm" : "7126-02.htm";
				break;
			
			case STARTED:
				htmltext = st.getInt("cond") == 1 ? "7126-04a.htm" : "7126-04.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
		{
			return null;
		}
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		if (st.dropItems(BRACELET, 1, 60, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
