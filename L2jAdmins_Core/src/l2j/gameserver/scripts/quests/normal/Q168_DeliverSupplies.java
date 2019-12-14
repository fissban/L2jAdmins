package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q168_DeliverSupplies extends Script
{
	// ITEMs
	private static final int JENNA_LETTER = 1153;
	private static final int SENTRY_BLADE_1 = 1154;
	private static final int SENTRY_BLADE_2 = 1155;
	private static final int SENTRY_BLADE_3 = 1156;
	private static final int OLD_BRONZE_SWORD = 1157;
	// NPCs
	private static final int JENNA = 7349;
	private static final int ROSELYN = 7355;
	private static final int KRISTIN = 7357;
	private static final int HARANT = 7360;
	
	public Q168_DeliverSupplies()
	{
		super(168, "Deliver Supplies");
		
		registerItems(JENNA_LETTER, SENTRY_BLADE_1, SENTRY_BLADE_2, SENTRY_BLADE_3, OLD_BRONZE_SWORD);
		
		addStartNpc(JENNA);
		addTalkId(JENNA, ROSELYN, KRISTIN, HARANT);
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
		
		if (event.equalsIgnoreCase("7349-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(JENNA_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7349-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "7349-01.htm";
				}
				else
				{
					htmltext = "7349-02.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case JENNA:
						if (cond == 1)
						{
							htmltext = "7349-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7349-05.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SENTRY_BLADE_1, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7349-07.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7349-06.htm";
							st.takeItems(OLD_BRONZE_SWORD, 2);
							st.rewardItems(Inventory.ADENA_ID, 820);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case HARANT:
						if (cond == 1)
						{
							htmltext = "7360-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(JENNA_LETTER, 1);
							st.giveItems(SENTRY_BLADE_1, 1);
							st.giveItems(SENTRY_BLADE_2, 1);
							st.giveItems(SENTRY_BLADE_3, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7360-02.htm";
						}
						break;
					
					case ROSELYN:
						if (cond == 3)
						{
							if (st.hasItems(SENTRY_BLADE_2))
							{
								htmltext = "7355-01.htm";
								st.takeItems(SENTRY_BLADE_2, 1);
								st.giveItems(OLD_BRONZE_SWORD, 1);
								if (st.getItemsCount(OLD_BRONZE_SWORD) == 2)
								{
									st.set("cond", "4");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else
							{
								htmltext = "7355-02.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "7355-02.htm";
						}
						break;
					
					case KRISTIN:
						if (cond == 3)
						{
							if (st.hasItems(SENTRY_BLADE_3))
							{
								htmltext = "7357-01.htm";
								st.takeItems(SENTRY_BLADE_3, 1);
								st.giveItems(OLD_BRONZE_SWORD, 1);
								if (st.getItemsCount(OLD_BRONZE_SWORD) == 2)
								{
									st.set("cond", "4");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else
							{
								htmltext = "7357-02.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "7357-02.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
}
