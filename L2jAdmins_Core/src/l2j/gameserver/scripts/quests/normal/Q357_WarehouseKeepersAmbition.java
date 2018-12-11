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

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q357_WarehouseKeepersAmbition extends Script
{
	// Item
	private static final int JADE_CRYSTAL = 5867;
	
	// Monsters
	private static final int FOREST_RUNNER = 594;
	private static final int FLINE_ELDER = 595;
	private static final int LIELE_ELDER = 596;
	private static final int VALLEY_TREANT_ELDER = 597;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(FOREST_RUNNER, 400000);
		CHANCES.put(FLINE_ELDER, 410000);
		CHANCES.put(LIELE_ELDER, 440000);
		CHANCES.put(VALLEY_TREANT_ELDER, 650000);
	}
	
	public Q357_WarehouseKeepersAmbition()
	{
		super(357, "Warehouse Keeper's Ambition");
		
		registerItems(JADE_CRYSTAL);
		
		addStartNpc(7686); // Silva
		addTalkId(7686);
		
		addKillId(FOREST_RUNNER, FLINE_ELDER, LIELE_ELDER, VALLEY_TREANT_ELDER);
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
		
		if (event.equalsIgnoreCase("7686-2.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7686-7.htm"))
		{
			final int count = st.getItemsCount(JADE_CRYSTAL);
			if (count == 0)
			{
				htmltext = "7686-4.htm";
			}
			else
			{
				int reward = (count * 425) + 3500;
				if (count >= 100)
				{
					reward += 7400;
				}
				
				st.takeItems(JADE_CRYSTAL, -1);
				st.rewardItems(Inventory.ADENA_ID, reward);
			}
		}
		else if (event.equalsIgnoreCase("7686-8.htm"))
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
				htmltext = player.getLevel() < 47 ? "7686-0a.htm" : "7686-0.htm";
				break;
			
			case STARTED:
				htmltext = !st.hasItems(JADE_CRYSTAL) ? "7686-4.htm" : "7686-6.htm";
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
		
		partyMember.getScriptState(getName()).dropItems(JADE_CRYSTAL, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
}
