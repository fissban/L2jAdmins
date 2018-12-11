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
public class Q409_PathToAnElvenOracle extends Script
{
	// Items
	private static final int CRYSTAL_MEDALLION = 1231;
	private static final int SWINDLER_MONEY = 1232;
	private static final int ALLANA_DIARY = 1233;
	private static final int LIZARD_CAPTAIN_ORDER = 1234;
	private static final int LEAF_OF_ORACLE = 1235;
	private static final int HALF_OF_DIARY = 1236;
	private static final int TAMIL_NECKLACE = 1275;
	
	// NPCs
	private static final int MANUEL = 7293;
	private static final int ALLANA = 7424;
	private static final int PERRIN = 7428;
	
	public Q409_PathToAnElvenOracle()
	{
		super(409, "Path to an Elven Oracle");
		
		registerItems(CRYSTAL_MEDALLION, SWINDLER_MONEY, ALLANA_DIARY, LIZARD_CAPTAIN_ORDER, HALF_OF_DIARY, TAMIL_NECKLACE);
		
		addStartNpc(MANUEL);
		addTalkId(MANUEL, ALLANA, PERRIN);
		
		addKillId(5032, 5033, 152, 5035);
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
		
		if (event.equalsIgnoreCase("7293-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(CRYSTAL_MEDALLION, 1);
		}
		else if (event.equalsIgnoreCase("spawn_lizards"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			addSpawn(5032, -92319, 154235, -3284, 2000, false, 0);
			addSpawn(5033, -92361, 154190, -3284, 2000, false, 0);
			addSpawn(5034, -92375, 154278, -3278, 2000, false, 0);
			return null;
		}
		else if (event.equalsIgnoreCase("7428-06.htm"))
		{
			addSpawn(5035, -93194, 147587, -2672, 2000, false, 0);
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
				if (player.getClassId() != ClassId.ELF_MAGE)
				{
					htmltext = player.getClassId() == ClassId.ORACLE ? "7293-02a.htm" : "7293-02.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "7293-03.htm";
				}
				else if (st.hasItems(LEAF_OF_ORACLE))
				{
					htmltext = "7293-04.htm";
				}
				else
				{
					htmltext = "7293-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case MANUEL:
						if (cond == 1)
						{
							htmltext = "7293-06.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "7293-09.htm";
						}
						else if ((cond > 3) && (cond < 7))
						{
							htmltext = "7293-07.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7293-08.htm";
							st.takeItems(ALLANA_DIARY, 1);
							st.takeItems(CRYSTAL_MEDALLION, 1);
							st.takeItems(LIZARD_CAPTAIN_ORDER, 1);
							st.takeItems(SWINDLER_MONEY, 1);
							st.giveItems(LEAF_OF_ORACLE, 1);
							st.rewardExpAndSp(3200, 1130);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case ALLANA:
						if (cond == 1)
						{
							htmltext = "7424-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7424-02.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(HALF_OF_DIARY, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7424-03.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7424-06.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7424-04.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(HALF_OF_DIARY, -1);
							st.giveItems(ALLANA_DIARY, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7424-05.htm";
						}
						break;
					
					case PERRIN:
						if (cond == 4)
						{
							htmltext = "7428-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7428-04.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(TAMIL_NECKLACE, -1);
							st.giveItems(SWINDLER_MONEY, 1);
						}
						else if (cond > 5)
						{
							htmltext = "7428-05.htm";
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
		
		if (npc.getId() == 5035)
		{
			if (st.getInt("cond") == 4)
			{
				st.set("cond", "5");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(TAMIL_NECKLACE, 1);
			}
		}
		else if (st.getInt("cond") == 2)
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(LIZARD_CAPTAIN_ORDER, 1);
		}
		
		return null;
	}
	
}
