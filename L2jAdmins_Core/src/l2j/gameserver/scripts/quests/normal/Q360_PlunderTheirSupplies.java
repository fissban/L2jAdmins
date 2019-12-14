package l2j.gameserver.scripts.quests.normal;

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
public class Q360_PlunderTheirSupplies extends Script
{
	// ITEMs
	private static final int SUPPLY_ITEM = 5872;
	private static final int SUSPICIOUS_DOCUMENT = 5871;
	private static final int RECIPE_OF_SUPPLY = 5870;
	
	private static final int[][][] DROPLIST =
	{
		{
			{
				SUSPICIOUS_DOCUMENT,
				1,
				0,
				50000
			},
			{
				SUPPLY_ITEM,
				1,
				0,
				500000
			}
		},
		{
			{
				SUSPICIOUS_DOCUMENT,
				1,
				0,
				50000
			},
			{
				SUPPLY_ITEM,
				1,
				0,
				660000
			}
		}
	};
	
	public Q360_PlunderTheirSupplies()
	{
		super(360, "Plunder Their Supplies");
		
		registerItems(RECIPE_OF_SUPPLY, SUPPLY_ITEM, SUSPICIOUS_DOCUMENT);
		
		addStartNpc(7873); // Coleman
		addTalkId(7873);
		
		addKillId(666, 669);
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
		
		if (event.equalsIgnoreCase("7873-2.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7873-6.htm"))
		{
			st.takeItems(SUPPLY_ITEM, -1);
			st.takeItems(SUSPICIOUS_DOCUMENT, -1);
			st.takeItems(RECIPE_OF_SUPPLY, -1);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() < 52 ? "7873-0a.htm" : "7873-0.htm";
				break;
			
			case STARTED:
				final int supplyItems = st.getItemsCount(SUPPLY_ITEM);
				if (supplyItems == 0)
				{
					htmltext = "7873-3.htm";
				}
				else
				{
					final int reward = 6000 + (supplyItems * 100) + (st.getItemsCount(RECIPE_OF_SUPPLY) * 6000);
					
					htmltext = "7873-5.htm";
					st.takeItems(SUPPLY_ITEM, -1);
					st.takeItems(RECIPE_OF_SUPPLY, -1);
					st.rewardItems(Inventory.ADENA_ID, reward);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		st.dropMultipleItems(DROPLIST[npc.getId() == 666 ? 0 : 1]);
		
		if (st.getItemsCount(SUSPICIOUS_DOCUMENT) == 5)
		{
			st.takeItems(SUSPICIOUS_DOCUMENT, 5);
			st.giveItems(RECIPE_OF_SUPPLY, 1);
		}
		
		return null;
	}
	
}
