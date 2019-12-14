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
public class Q356_DigUpTheSeaOfSpores extends Script
{
	// ITEMs
	private static final int HERB_SPORE = 5866;
	private static final int CARN_SPORE = 5865;
	
	// Monsters
	private static final int ROTTING_TREE = 558;
	private static final int SPORE_ZOMBIE = 562;
	
	public Q356_DigUpTheSeaOfSpores()
	{
		super(356, "Dig Up the Sea of Spores!");
		
		registerItems(HERB_SPORE, CARN_SPORE);
		
		addStartNpc(7717); // Gauen
		addTalkId(7717);
		
		addKillId(ROTTING_TREE, SPORE_ZOMBIE);
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
		
		if (event.equalsIgnoreCase("7717-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7717-17.htm"))
		{
			st.takeItems(HERB_SPORE, -1);
			st.takeItems(CARN_SPORE, -1);
			st.rewardItems(Inventory.ADENA_ID, 20950);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7717-14.htm"))
		{
			st.takeItems(HERB_SPORE, -1);
			st.takeItems(CARN_SPORE, -1);
			st.rewardExpAndSp(35000, 2600);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7717-12.htm"))
		{
			st.takeItems(HERB_SPORE, -1);
			st.rewardExpAndSp(24500, 0);
		}
		else if (event.equalsIgnoreCase("7717-13.htm"))
		{
			st.takeItems(CARN_SPORE, -1);
			st.rewardExpAndSp(0, 1820);
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
				htmltext = player.getLevel() < 43 ? "7717-01.htm" : "7717-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7717-07.htm";
				}
				else if (cond == 2)
				{
					if (st.getItemsCount(HERB_SPORE) >= 50)
					{
						htmltext = "7717-08.htm";
					}
					else if (st.getItemsCount(CARN_SPORE) >= 50)
					{
						htmltext = "7717-09.htm";
					}
					else
					{
						htmltext = "7717-07.htm";
					}
				}
				else if (cond == 3)
				{
					htmltext = "7717-10.htm";
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
		
		final int cond = st.getInt("cond");
		if (cond < 3)
		{
			switch (npc.getId())
			{
				case ROTTING_TREE:
					if (st.dropItems(HERB_SPORE, 1, 50, 630000))
					{
						st.set("cond", cond == 2 ? "3" : "2");
					}
					break;
				
				case SPORE_ZOMBIE:
					if (st.dropItems(CARN_SPORE, 1, 50, 760000))
					{
						st.set("cond", cond == 2 ? "3" : "2");
					}
					break;
			}
		}
		
		return null;
	}
	
}
