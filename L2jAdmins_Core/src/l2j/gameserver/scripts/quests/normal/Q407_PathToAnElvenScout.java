package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q407_PathToAnElvenScout extends Script
{
	// Items
	private static final int REISA_LETTER = 1207;
	private static final int PRIAS_TORN_LETTER_1 = 1208;
	private static final int PRIAS_TORN_LETTER_2 = 1209;
	private static final int PRIAS_TORN_LETTER_3 = 1210;
	private static final int PRIAS_TORN_LETTER_4 = 1211;
	private static final int MORETTI_HERB = 1212;
	private static final int MORETTI_LETTER = 1214;
	private static final int PRIAS_LETTER = 1215;
	private static final int HONORARY_GUARD = 1216;
	private static final int REISA_RECOMMENDATION = 1217;
	private static final int RUSTED_KEY = 1293;
	
	// NPCs
	private static final int REISA = 7328;
	private static final int BABENCO = 7334;
	private static final int MORETTI = 7337;
	private static final int PRIAS = 7426;
	
	public Q407_PathToAnElvenScout()
	{
		super(407, "Path to an Elven Scout");
		
		registerItems(REISA_LETTER, PRIAS_TORN_LETTER_1, PRIAS_TORN_LETTER_2, PRIAS_TORN_LETTER_3, PRIAS_TORN_LETTER_4, MORETTI_HERB, MORETTI_LETTER, PRIAS_LETTER, HONORARY_GUARD, RUSTED_KEY);
		
		addStartNpc(REISA);
		addTalkId(REISA, MORETTI, BABENCO, PRIAS);
		
		addKillId(53, 5031);
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
		
		if (event.equalsIgnoreCase("7328-05.htm"))
		{
			if (player.getClassId() != ClassId.ELF_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.SCOUT ? "7328-02a.htm" : "7328-02.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7328-03.htm";
			}
			else if (st.hasItems(REISA_RECOMMENDATION))
			{
				htmltext = "7328-04.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				st.giveItems(REISA_LETTER, 1);
			}
		}
		else if (event.equalsIgnoreCase("7337-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(REISA_LETTER, -1);
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
				htmltext = "7328-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case REISA:
						if (cond == 1)
						{
							htmltext = "7328-06.htm";
						}
						else if ((cond > 1) && (cond < 8))
						{
							htmltext = "7328-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7328-07.htm";
							st.takeItems(HONORARY_GUARD, -1);
							st.giveItems(REISA_RECOMMENDATION, 1);
							st.rewardExpAndSp(3200, 1000);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case MORETTI:
						if (cond == 1)
						{
							htmltext = "7337-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = !st.hasItems(PRIAS_TORN_LETTER_1) ? "7337-04.htm" : "7337-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7337-06.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(PRIAS_TORN_LETTER_1, -1);
							st.takeItems(PRIAS_TORN_LETTER_2, -1);
							st.takeItems(PRIAS_TORN_LETTER_3, -1);
							st.takeItems(PRIAS_TORN_LETTER_4, -1);
							st.giveItems(MORETTI_HERB, 1);
							st.giveItems(MORETTI_LETTER, 1);
						}
						else if ((cond > 3) && (cond < 7))
						{
							htmltext = "7337-09.htm";
						}
						else if ((cond == 7) && st.hasItems(PRIAS_LETTER))
						{
							htmltext = "7337-07.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(PRIAS_LETTER, -1);
							st.giveItems(HONORARY_GUARD, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7337-08.htm";
						}
						break;
					
					case BABENCO:
						if (cond == 2)
						{
							htmltext = "7334-01.htm";
						}
						break;
					
					case PRIAS:
						if (cond == 4)
						{
							htmltext = "7426-01.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 5)
						{
							htmltext = "7426-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7426-02.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(RUSTED_KEY, -1);
							st.takeItems(MORETTI_HERB, -1);
							st.takeItems(MORETTI_LETTER, -1);
							st.giveItems(PRIAS_LETTER, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7426-04.htm";
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int cond = st.getInt("cond");
		if (npc.getId() == 53)
		{
			if (cond == 2)
			{
				if (!st.hasItems(PRIAS_TORN_LETTER_1))
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.giveItems(PRIAS_TORN_LETTER_1, 1);
				}
				else if (!st.hasItems(PRIAS_TORN_LETTER_2))
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.giveItems(PRIAS_TORN_LETTER_2, 1);
				}
				else if (!st.hasItems(PRIAS_TORN_LETTER_3))
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.giveItems(PRIAS_TORN_LETTER_3, 1);
				}
				else if (!st.hasItems(PRIAS_TORN_LETTER_4))
				{
					st.set("cond", "3");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(PRIAS_TORN_LETTER_4, 1);
				}
			}
		}
		else if (((cond == 4) || (cond == 5)) && st.dropItems(RUSTED_KEY, 1, 1, 600000))
		{
			st.set("cond", "6");
		}
		
		return null;
	}
	
}
