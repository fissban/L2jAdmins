package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
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
public class Q623_TheFinestFood extends Script
{
	// Items
	private static final int LEAF_OF_FLAVA = 7199;
	private static final int BUFFALO_MEAT = 7200;
	private static final int ANTELOPE_HORN = 7201;
	
	// NPC
	private static final int JEREMY = 8521;
	
	// Monsters
	private static final int FLAVA = 1316;
	private static final int BUFFALO = 1315;
	private static final int ANTELOPE = 1318;
	
	public Q623_TheFinestFood()
	{
		super(623, "The Finest Food");
		
		registerItems(LEAF_OF_FLAVA, BUFFALO_MEAT, ANTELOPE_HORN);
		
		addStartNpc(JEREMY);
		addTalkId(JEREMY);
		
		addKillId(FLAVA, BUFFALO, ANTELOPE);
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
		
		if (event.equalsIgnoreCase("8521-02.htm"))
		{
			if (player.getLevel() >= 71)
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "8521-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("8521-05.htm"))
		{
			st.takeItems(LEAF_OF_FLAVA, -1);
			st.takeItems(BUFFALO_MEAT, -1);
			st.takeItems(ANTELOPE_HORN, -1);
			
			final int luck = Rnd.get(100);
			if (luck < 11)
			{
				st.rewardItems(Inventory.ADENA_ID, 25000);
				st.giveItems(6849, 1);
			}
			else if (luck < 23)
			{
				st.rewardItems(Inventory.ADENA_ID, 65000);
				st.giveItems(6847, 1);
			}
			else if (luck < 33)
			{
				st.rewardItems(Inventory.ADENA_ID, 25000);
				st.giveItems(6851, 1);
			}
			else
			{
				st.rewardItems(Inventory.ADENA_ID, 73000);
				st.rewardExpAndSp(230000, 18250);
			}
			
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
				htmltext = "8521-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8521-06.htm";
				}
				else if (cond == 2)
				{
					if ((st.getItemsCount(LEAF_OF_FLAVA) >= 100) && (st.getItemsCount(BUFFALO_MEAT) >= 100) && (st.getItemsCount(ANTELOPE_HORN) >= 100))
					{
						htmltext = "8521-04.htm";
					}
					else
					{
						htmltext = "8521-07.htm";
					}
				}
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
		
		switch (npc.getId())
		{
			case FLAVA:
				if (st.dropItemsAlways(LEAF_OF_FLAVA, 1, 100) && (st.getItemsCount(BUFFALO_MEAT) >= 100) && (st.getItemsCount(ANTELOPE_HORN) >= 100))
				{
					st.set("cond", "2");
				}
				break;
			
			case BUFFALO:
				if (st.dropItemsAlways(BUFFALO_MEAT, 1, 100) && (st.getItemsCount(LEAF_OF_FLAVA) >= 100) && (st.getItemsCount(ANTELOPE_HORN) >= 100))
				{
					st.set("cond", "2");
				}
				break;
			
			case ANTELOPE:
				if (st.dropItemsAlways(ANTELOPE_HORN, 1, 100) && (st.getItemsCount(LEAF_OF_FLAVA) >= 100) && (st.getItemsCount(BUFFALO_MEAT) >= 100))
				{
					st.set("cond", "2");
				}
				break;
		}
		
		return null;
	}
	
}
