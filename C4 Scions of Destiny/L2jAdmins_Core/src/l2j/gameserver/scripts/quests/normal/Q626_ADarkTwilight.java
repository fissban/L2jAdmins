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
public class Q626_ADarkTwilight extends Script
{
	// Items
	private static final int BLOOD_OF_SAINT = 7169;
	
	// NPC
	private static final int HIERARCH = 8517;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1520, 533000);
		CHANCES.put(1523, 566000);
		CHANCES.put(1524, 603000);
		CHANCES.put(1524, 603000);
		CHANCES.put(1526, 587000);
		CHANCES.put(1529, 606000);
		CHANCES.put(1530, 560000);
		CHANCES.put(1531, 669000);
		CHANCES.put(1532, 651000);
		CHANCES.put(1535, 672000);
		CHANCES.put(1536, 597000);
		CHANCES.put(1539, 739000);
		CHANCES.put(1539, 739000);
		CHANCES.put(1531, 669000);
	}
	
	public Q626_ADarkTwilight()
	{
		super(626, "A Dark Twilight");
		
		registerItems(BLOOD_OF_SAINT);
		
		addStartNpc(HIERARCH);
		addTalkId(HIERARCH);
		
		for (final int npcId : CHANCES.keySet())
		{
			addKillId(npcId);
		}
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
		
		if (event.equalsIgnoreCase("8517-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("reward1"))
		{
			if (st.getItemsCount(BLOOD_OF_SAINT) == 300)
			{
				htmltext = "8517-07.htm";
				st.takeItems(BLOOD_OF_SAINT, 300);
				st.rewardExpAndSp(162773, 12500);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(false);
			}
			else
			{
				htmltext = "8517-08.htm";
			}
		}
		else if (event.equalsIgnoreCase("reward2"))
		{
			if (st.getItemsCount(BLOOD_OF_SAINT) == 300)
			{
				htmltext = "8517-07.htm";
				st.takeItems(BLOOD_OF_SAINT, 300);
				st.rewardItems(Inventory.ADENA_ID, 100000);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(false);
			}
			else
			{
				htmltext = "8517-08.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 60 ? "8517-02.htm" : "8517-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8517-05.htm";
				}
				else
				{
					htmltext = "8517-04.htm";
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
		
		if (st.dropItems(BLOOD_OF_SAINT, 1, 300, CHANCES.get(npc.getId())))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
