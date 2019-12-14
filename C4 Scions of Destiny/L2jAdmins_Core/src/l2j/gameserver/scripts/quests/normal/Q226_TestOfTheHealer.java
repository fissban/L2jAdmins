package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.util.Rnd;

/**
 * Test of the Healer. Adapted from L2Acis, repaired and implemented by zarie.
 */
public class Q226_TestOfTheHealer extends Script
{
	// Items
	private static final int REPORT_OF_PERRIN = 2810;
	private static final int KRISTINA_LETTER = 2811;
	private static final int PICTURE_OF_WINDY = 2812;
	private static final int GOLDEN_STATUE = 2813;
	private static final int WINDY_PEBBLES = 2814;
	private static final int ORDER_OF_SORIUS = 2815;
	private static final int SECRET_LETTER_1 = 2816;
	private static final int SECRET_LETTER_2 = 2817;
	private static final int SECRET_LETTER_3 = 2818;
	private static final int SECRET_LETTER_4 = 2819;
	
	// Rewards
	private static final int MARK_OF_HEALER = 2820;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// NPCs
	private static final int BANDELLOS = 7473;
	private static final int PERRIN = 7428;
	private static final int SORIUS = 7327;
	private static final int ALLANA = 7424;
	private static final int ORPHAN_GIRL = 7659;
	private static final int GUPU = 7658;
	private static final int WINDY_SHAORING = 7660;
	private static final int DAURIN_HAMMERCRUSH = 7674;
	private static final int MYSTERIOUS_DARKELF = 7661;
	private static final int KRISTINA = 7665;
	private static final int PIPER_LONGBOW = 7662;
	private static final int SLEIN_SHINING_BLADE = 7663;
	private static final int KAIN_FLYING_KNIFE = 7664;
	
	// Monsters
	private static final int LETO_LIZARDMAN_LEADER = 5123;
	private static final int LETO_LIZARDMAN_ASSASSIN = 5124;
	private static final int LETO_LIZARDMAN_SNIPER = 5125;
	private static final int LETO_LIZARDMAN_WIZARD = 5126;
	private static final int LETO_LIZARDMAN_LORD = 5127;
	private static final int TATOMA = 5134;
	
	private static L2Npc tatoma;
	private static L2Npc letoLeader;
	
	public Q226_TestOfTheHealer()
	{
		super(226, "Test of the Healer");
		
		registerItems(REPORT_OF_PERRIN, KRISTINA_LETTER, PICTURE_OF_WINDY, GOLDEN_STATUE, WINDY_PEBBLES, ORDER_OF_SORIUS, SECRET_LETTER_1, SECRET_LETTER_2, SECRET_LETTER_3, SECRET_LETTER_4);
		
		addStartNpc(BANDELLOS);
		addTalkId(BANDELLOS, SORIUS, ALLANA, PERRIN, GUPU, ORPHAN_GIRL, WINDY_SHAORING, MYSTERIOUS_DARKELF, PIPER_LONGBOW, SLEIN_SHINING_BLADE, KAIN_FLYING_KNIFE, KRISTINA, DAURIN_HAMMERCRUSH);
		
		addKillId(LETO_LIZARDMAN_LEADER, LETO_LIZARDMAN_ASSASSIN, LETO_LIZARDMAN_SNIPER, LETO_LIZARDMAN_WIZARD, LETO_LIZARDMAN_LORD, TATOMA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// BANDELLOS
		if (event.equalsIgnoreCase("7473-04a.htm"))
		{
			st.startQuest();
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(REPORT_OF_PERRIN, 1);
			if (player.getClassId() == ClassId.KNIGHT)
			{
				st.giveItems(DIMENSIONAL_DIAMOND, 104);
			}
			else if (player.getClassId() == ClassId.ELF_KNIGHT)
			{
				st.giveItems(DIMENSIONAL_DIAMOND, 72);
			}
			else if (player.getClassId() == ClassId.CLERIC)
			{
				st.giveItems(DIMENSIONAL_DIAMOND, 60);
			}
			else
			{
				st.giveItems(DIMENSIONAL_DIAMOND, 45);
			}
		}
		else if (event.equalsIgnoreCase("7473-09.htm"))
		{
			st.takeItems(GOLDEN_STATUE, 1);
			st.giveItems(MARK_OF_HEALER, 1);
			st.rewardExpAndSp(134839, 50000);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.exitQuest(false, true);
		}
		// PERRIN
		else if (event.equalsIgnoreCase("7428-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			
			if (tatoma == null)
			{
				tatoma = addSpawn(TATOMA, -93254, 147559, -2679, 0, false, 200000);
				startTimer("tatoma_despawn", 200000, null, player, false);
			}
		}
		// GUPU
		else if (event.equalsIgnoreCase("7658-02.htm"))
		{
			if (st.getItemsCount(Inventory.ADENA_ID) >= 100000)
			{
				st.set("cond", "7");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(Inventory.ADENA_ID, 100000);
				st.giveItems(PICTURE_OF_WINDY, 1);
			}
			else
			{
				htmltext = "7658-05.htm";
			}
		}
		else if (event.equalsIgnoreCase("7658-03.htm"))
		{
			st.set("gupu", "1");
		}
		else if (event.equalsIgnoreCase("7658-07.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// WINDY SHAORING
		else if (event.equalsIgnoreCase("7660-03.htm"))
		{
			st.set("cond", "8");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(PICTURE_OF_WINDY, 1);
			st.giveItems(WINDY_PEBBLES, 1);
		}
		// DAURIN HAMMERCRUSH
		else if (event.equalsIgnoreCase("7674-02.htm"))
		{
			st.set("cond", "11");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ORDER_OF_SORIUS, 1);
			
			if (letoLeader == null)
			{
				letoLeader = addSpawn(LETO_LIZARDMAN_LEADER, -97441, 106585, -3405, 0, false, 200000);
				startTimer("leto_leader_despawn", 200000, null, player, false);
			}
		}
		// KRISTINA
		else if (event.equalsIgnoreCase("7665-02.htm"))
		{
			st.set("cond", "22");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SECRET_LETTER_1, 1);
			st.takeItems(SECRET_LETTER_2, 1);
			st.takeItems(SECRET_LETTER_3, 1);
			st.takeItems(SECRET_LETTER_4, 1);
			st.giveItems(KRISTINA_LETTER, 1);
		}
		// DESPAWNS
		else if (event.equalsIgnoreCase("tatoma_despawn"))
		{
			tatoma.deleteMe();
			tatoma = null;
			return null;
		}
		else if (event.equalsIgnoreCase("leto_leader_despawn"))
		{
			letoLeader.deleteMe();
			letoLeader = null;
			return null;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if ((player.getClassId() != ClassId.KNIGHT) && (player.getClassId() != ClassId.ELF_KNIGHT) && (player.getClassId() != ClassId.CLERIC) && (player.getClassId() != ClassId.ORACLE))
				{
					htmltext = "7473-01.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7473-02.htm";
				}
				else
				{
					htmltext = "7473-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case BANDELLOS:
						if (cond < 23)
						{
							htmltext = "7473-05.htm";
						}
						else
						{
							if (!st.hasItems(GOLDEN_STATUE))
							{
								htmltext = "7473-06.htm";
								st.giveItems(MARK_OF_HEALER, 1);
								st.rewardExpAndSp(118304, 26250);
								player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.exitQuest(false, true);
							}
							else
							{
								htmltext = "7473-07.htm";
							}
						}
						break;
					
					case PERRIN:
						if (cond < 3)
						{
							htmltext = "7428-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7428-03.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(REPORT_OF_PERRIN, 1);
						}
						else
						{
							htmltext = "7428-04.htm";
						}
						break;
					
					case ORPHAN_GIRL:
						htmltext = "7659-0" + Rnd.get(1, 5) + ".htm";
						break;
					
					case ALLANA:
						if (cond == 4)
						{
							htmltext = "7424-01.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond > 4)
						{
							htmltext = "7424-02.htm";
						}
						break;
					
					case GUPU:
						if ((st.getInt("gupu") == 1) && (cond != 9))
						{
							htmltext = "7658-07.htm";
							st.set("cond", "9");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 5)
						{
							htmltext = "7658-01.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 6)
						{
							htmltext = "7658-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7658-04.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7658-06.htm";
							st.takeItems(WINDY_PEBBLES, 1);
							st.giveItems(GOLDEN_STATUE, 1);
						}
						else if (cond > 8)
						{
							htmltext = "7658-07.htm";
						}
						break;
					
					case WINDY_SHAORING:
						if (cond == 7)
						{
							htmltext = "7660-01.htm";
						}
						else if (st.hasItems(WINDY_PEBBLES))
						{
							htmltext = "7660-04.htm";
						}
						break;
					
					case SORIUS:
						if (cond == 9)
						{
							htmltext = "7327-01.htm";
							st.set("cond", "10");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(ORDER_OF_SORIUS, 1);
						}
						else if ((cond > 9) && (cond < 22))
						{
							htmltext = "7327-02.htm";
						}
						else if (cond == 22)
						{
							htmltext = "7327-03.htm";
							st.set("cond", "23");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(KRISTINA_LETTER, 1);
						}
						else if (cond == 23)
						{
							htmltext = "7327-04.htm";
						}
						break;
					
					case DAURIN_HAMMERCRUSH:
						if (cond == 10)
						{
							htmltext = "7674-01.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7674-02a.htm";
							if (letoLeader == null)
							{
								letoLeader = addSpawn(LETO_LIZARDMAN_LEADER, -97441, 106585, -3405, 0, false, 200000);
								startTimer("leto_leader_despawn", 200000, null, player, false);
							}
						}
						else if (cond == 12)
						{
							htmltext = "7674-03.htm";
							st.set("cond", "13");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond > 12)
						{
							htmltext = "7674-04.htm";
						}
						break;
					
					case PIPER_LONGBOW:
					case SLEIN_SHINING_BLADE:
					case KAIN_FLYING_KNIFE:
						if ((cond == 13) || (cond == 14))
						{
							htmltext = npc.getId() + "-01.htm";
						}
						else if ((cond > 14) && (cond < 19))
						{
							htmltext = npc.getId() + "-02.htm";
						}
						else if ((cond > 18) && (cond < 22))
						{
							htmltext = npc.getId() + "-03.htm";
							st.set("cond", "21");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						break;
					
					case MYSTERIOUS_DARKELF:
						if (cond == 13)
						{
							htmltext = "7661-01.htm";
							st.set("cond", "14");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							addSpawn(LETO_LIZARDMAN_ASSASSIN, player, false, 0);
							addSpawn(LETO_LIZARDMAN_ASSASSIN, player, false, 0);
							addSpawn(LETO_LIZARDMAN_ASSASSIN, player, false, 0);
						}
						else if (cond == 14)
						{
							addSpawn(LETO_LIZARDMAN_ASSASSIN, player, false, 0);
							htmltext = "7661-01.htm";
						}
						else if (cond == 15)
						{
							htmltext = "7661-02.htm";
							st.set("cond", "16");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							addSpawn(LETO_LIZARDMAN_SNIPER, player, false, 0);
							addSpawn(LETO_LIZARDMAN_SNIPER, player, false, 0);
							addSpawn(LETO_LIZARDMAN_SNIPER, player, false, 0);
						}
						else if (cond == 16)
						{
							addSpawn(LETO_LIZARDMAN_SNIPER, player, false, 0);
							htmltext = "7661-02.htm";
						}
						else if (cond == 17)
						{
							htmltext = "7661-03.htm";
							st.set("cond", "18");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							addSpawn(LETO_LIZARDMAN_WIZARD, player, false, 0);
							addSpawn(LETO_LIZARDMAN_WIZARD, player, false, 0);
							addSpawn(LETO_LIZARDMAN_LORD, player, false, 0);
						}
						else if (cond == 18)
						{
							addSpawn(LETO_LIZARDMAN_LORD, player, false, 0);
							htmltext = "7661-03.htm";
						}
						else if (cond == 19)
						{
							htmltext = "7661-04.htm";
							st.set("cond", "20");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if ((cond == 20) || (cond == 21))
						{
							htmltext = "7661-04.htm";
						}
						break;
					
					case KRISTINA:
						if ((cond > 18) && (cond < 22))
						{
							htmltext = "7665-01.htm";
						}
						else if (cond > 21)
						{
							htmltext = "7665-04.htm";
						}
						else
						{
							htmltext = "7665-03.htm";
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
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		final int cond = st.getInt("cond");
		switch (npc.getId())
		{
			case TATOMA:
				if ((cond == 1) || (cond == 2))
				{
					st.set("cond", "3");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
				tatoma = null;
				cancelTimer("tatoma_despawn", null, player);
				break;
			
			case LETO_LIZARDMAN_LEADER:
				if ((cond == 10) || (cond == 11))
				{
					st.set("cond", "12");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(SECRET_LETTER_1, 1);
				}
				letoLeader = null;
				cancelTimer("leto_leader_despawn", null, player);
				break;
			
			case LETO_LIZARDMAN_ASSASSIN:
				if ((cond == 13) || (cond == 14))
				{
					st.set("cond", "15");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(SECRET_LETTER_2, 1);
				}
				break;
			
			case LETO_LIZARDMAN_SNIPER:
				if ((cond == 15) || (cond == 16))
				{
					st.set("cond", "17");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(SECRET_LETTER_3, 1);
				}
				break;
			
			case LETO_LIZARDMAN_LORD:
				if ((cond == 17) || (cond == 18))
				{
					st.set("cond", "19");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(SECRET_LETTER_4, 1);
				}
				break;
		}
		
		return null;
	}
}
