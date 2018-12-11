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
public class Q299_GatherIngredientsForPie extends Script
{
	// NPCs
	private static final int LARA = 7063;
	private static final int BRIGHT = 7466;
	private static final int EMILY = 7620;
	
	// ITEMs
	private static final int FRUIT_BASKET = 7136;
	private static final int AVELLAN_SPICE = 7137;
	private static final int HONEY_POUCH = 7138;
	
	public Q299_GatherIngredientsForPie()
	{
		super(299, "Gather Ingredients for Pie");
		
		registerItems(FRUIT_BASKET, AVELLAN_SPICE, HONEY_POUCH);
		
		addStartNpc(EMILY);
		addTalkId(EMILY, LARA, BRIGHT);
		// FIXME identificar estos npc
		addKillId(934, 935); // Wasp Worker, Wasp Leader
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
		
		if (event.equalsIgnoreCase("7620-1.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7620-3.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HONEY_POUCH, -1);
		}
		else if (event.equalsIgnoreCase("7063-1.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(AVELLAN_SPICE, 1);
		}
		else if (event.equalsIgnoreCase("7620-5.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(AVELLAN_SPICE, 1);
		}
		else if (event.equalsIgnoreCase("7466-1.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(FRUIT_BASKET, 1);
		}
		else if (event.equalsIgnoreCase("7620-7a.htm"))
		{
			if (st.hasItems(FRUIT_BASKET))
			{
				htmltext = "7620-7.htm";
				st.takeItems(FRUIT_BASKET, 1);
				st.rewardItems(57, 25000);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "5");
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
				htmltext = player.getLevel() < 34 ? "7620-0a.htm" : "7620-0.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case EMILY:
						if (cond == 1)
						{
							htmltext = "7620-1a.htm";
						}
						else if (cond == 2)
						{
							if (st.getItemsCount(HONEY_POUCH) >= 100)
							{
								htmltext = "7620-2.htm";
							}
							else
							{
								htmltext = "7620-2a.htm";
								st.exitQuest(true);
							}
						}
						else if (cond == 3)
						{
							htmltext = "7620-3a.htm";
						}
						else if (cond == 4)
						{
							if (st.hasItems(AVELLAN_SPICE))
							{
								htmltext = "7620-4.htm";
							}
							else
							{
								htmltext = "7620-4a.htm";
								st.exitQuest(true);
							}
						}
						else if (cond == 5)
						{
							htmltext = "7620-5a.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7620-6.htm";
						}
						break;
					
					case LARA:
						if (cond == 3)
						{
							htmltext = "7063-0.htm";
						}
						else if (cond > 3)
						{
							htmltext = "7063-1a.htm";
						}
						break;
					
					case BRIGHT:
						if (cond == 5)
						{
							htmltext = "7466-0.htm";
						}
						else if (cond > 5)
						{
							htmltext = "7466-1a.htm";
						}
						break;
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
		
		if (st.dropItems(HONEY_POUCH, 1, 100, npc.getId() == 934 ? 571000 : 625000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
