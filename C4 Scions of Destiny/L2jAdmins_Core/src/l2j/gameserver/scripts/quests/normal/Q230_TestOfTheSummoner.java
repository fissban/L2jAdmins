package l2j.gameserver.scripts.quests.normal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * Thx L2Acis
 */
public class Q230_TestOfTheSummoner extends Script
{
	// Items
	private static final int LETO_LIZARDMAN_AMULET = 3337;
	private static final int SAC_OF_REDSPORES = 3338;
	private static final int KARUL_BUGBEAR_TOTEM = 3339;
	private static final int SHARDS_OF_MANASHEN = 3340;
	private static final int BREKA_ORC_TOTEM = 3341;
	private static final int CRIMSON_BLOODSTONE = 3342;
	private static final int TALONS_OF_TYRANT = 3343;
	private static final int WINGS_OF_DRONEANT = 3344;
	private static final int TUSK_OF_WINDSUS = 3345;
	private static final int FANGS_OF_WYRM = 3346;
	private static final int LARA_LIST_1 = 3347;
	private static final int LARA_LIST_2 = 3348;
	private static final int LARA_LIST_3 = 3349;
	private static final int LARA_LIST_4 = 3350;
	private static final int LARA_LIST_5 = 3351;
	private static final int GALATEA_LETTER = 3352;
	private static final int BEGINNER_ARCANA = 3353;
	private static final int ALMORS_ARCANA = 3354;
	private static final int CAMONIELL_ARCANA = 3355;
	private static final int BELTHUS_ARCANA = 3356;
	private static final int BASILLIA_ARCANA = 3357;
	private static final int CELESTIEL_ARCANA = 3358;
	private static final int BRYNTHEA_ARCANA = 3359;
	private static final int CRYSTAL_OF_PROGRESS_1 = 3360;
	private static final int CRYSTAL_OF_INPROGRESS_1 = 3361;
	private static final int CRYSTAL_OF_FOUL_1 = 3362;
	private static final int CRYSTAL_OF_DEFEAT_1 = 3363;
	private static final int CRYSTAL_OF_VICTORY_1 = 3364;
	private static final int CRYSTAL_OF_PROGRESS_2 = 3365;
	private static final int CRYSTAL_OF_INPROGRESS_2 = 3366;
	private static final int CRYSTAL_OF_FOUL_2 = 3367;
	private static final int CRYSTAL_OF_DEFEAT_2 = 3368;
	private static final int CRYSTAL_OF_VICTORY_2 = 3369;
	private static final int CRYSTAL_OF_PROGRESS_3 = 3370;
	private static final int CRYSTAL_OF_INPROGRESS_3 = 3371;
	private static final int CRYSTAL_OF_FOUL_3 = 3372;
	private static final int CRYSTAL_OF_DEFEAT_3 = 3373;
	private static final int CRYSTAL_OF_VICTORY_3 = 3374;
	private static final int CRYSTAL_OF_PROGRESS_4 = 3375;
	private static final int CRYSTAL_OF_INPROGRESS_4 = 3376;
	private static final int CRYSTAL_OF_FOUL_4 = 3377;
	private static final int CRYSTAL_OF_DEFEAT_4 = 3378;
	private static final int CRYSTAL_OF_VICTORY_4 = 3379;
	private static final int CRYSTAL_OF_PROGRESS_5 = 3380;
	private static final int CRYSTAL_OF_INPROGRESS_5 = 3381;
	private static final int CRYSTAL_OF_FOUL_5 = 3382;
	private static final int CRYSTAL_OF_DEFEAT_5 = 3383;
	private static final int CRYSTAL_OF_VICTORY_5 = 3384;
	private static final int CRYSTAL_OF_PROGRESS_6 = 3385;
	private static final int CRYSTAL_OF_INPROGRESS_6 = 3386;
	private static final int CRYSTAL_OF_FOUL_6 = 3387;
	private static final int CRYSTAL_OF_DEFEAT_6 = 3388;
	private static final int CRYSTAL_OF_VICTORY_6 = 3389;
	
	// Rewards
	private static final int MARK_OF_SUMMONER = 3336;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// Npcs
	private static final int LARA = 7063;
	private static final int GALATEA = 7634;
	private static final int ALMORS = 7635;
	private static final int CAMONIELL = 7636;
	private static final int BELTHUS = 7637;
	private static final int BASILLA = 7638;
	private static final int CELESTIEL = 7639;
	private static final int BRYNTHEA = 7640;
	
	// Monsters
	private static final int NOBLE_ANT = 89;
	private static final int NOBLE_ANT_LEADER = 90;
	private static final int WYRM = 176;
	private static final int TYRANT = 192;
	private static final int TYRANT_KINGPIN = 193;
	private static final int BREKA_ORC = 267;
	private static final int BREKA_ORC_ARCHER = 268;
	private static final int BREKA_ORC_SHAMAN = 269;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int BREKA_ORC_WARRIOR = 271;
	private static final int FETTERED_SOUL = 552;
	private static final int WINDSUS = 553;
	private static final int GIANT_FUNGUS = 555;
	private static final int MANASHEN_GARGOYLE = 563;
	private static final int LETO_LIZARDMAN = 577;
	private static final int LETO_LIZARDMAN_ARCHER = 578;
	private static final int LETO_LIZARDMAN_SOLDIER = 579;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int KARUL_BUGBEAR = 600;
	
	// Quest Monsters
	private static final int PAKO_THE_CAT = 5102;
	private static final int UNICORN_RACER = 5103;
	private static final int SHADOW_TUREN = 5104;
	private static final int MIMI_THE_CAT = 5105;
	private static final int UNICORN_PHANTASM = 5106;
	private static final int SILHOUETTE_TILFO = 5107;
	
	private static final int[][] LARA_LISTS = new int[][]
	{
		{
			LARA_LIST_1,
			SAC_OF_REDSPORES,
			LETO_LIZARDMAN_AMULET
		},
		{
			LARA_LIST_2,
			KARUL_BUGBEAR_TOTEM,
			SHARDS_OF_MANASHEN
		},
		{
			LARA_LIST_3,
			CRIMSON_BLOODSTONE,
			BREKA_ORC_TOTEM
		},
		{
			LARA_LIST_4,
			TUSK_OF_WINDSUS,
			TALONS_OF_TYRANT
		},
		{
			LARA_LIST_5,
			WINGS_OF_DRONEANT,
			FANGS_OF_WYRM
		}
	};
	
	private static final Map<Integer, ProgressDuelMob> duelsInProgress = new ConcurrentHashMap<>();
	
	public Q230_TestOfTheSummoner()
	{
		super(230, "Test of the Summoner");
		
		registerItems(LETO_LIZARDMAN_AMULET, SAC_OF_REDSPORES, KARUL_BUGBEAR_TOTEM, SHARDS_OF_MANASHEN, BREKA_ORC_TOTEM, CRIMSON_BLOODSTONE, TALONS_OF_TYRANT, WINGS_OF_DRONEANT, TUSK_OF_WINDSUS, FANGS_OF_WYRM, LARA_LIST_1, LARA_LIST_2, LARA_LIST_3, LARA_LIST_4, LARA_LIST_5, GALATEA_LETTER, BEGINNER_ARCANA, ALMORS_ARCANA, CAMONIELL_ARCANA, BELTHUS_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA, CRYSTAL_OF_PROGRESS_1, CRYSTAL_OF_INPROGRESS_1, CRYSTAL_OF_FOUL_1, CRYSTAL_OF_DEFEAT_1, CRYSTAL_OF_VICTORY_1, CRYSTAL_OF_PROGRESS_2, CRYSTAL_OF_INPROGRESS_2, CRYSTAL_OF_FOUL_2, CRYSTAL_OF_DEFEAT_2, CRYSTAL_OF_VICTORY_2, CRYSTAL_OF_PROGRESS_3, CRYSTAL_OF_INPROGRESS_3, CRYSTAL_OF_FOUL_3, CRYSTAL_OF_DEFEAT_3, CRYSTAL_OF_VICTORY_3, CRYSTAL_OF_PROGRESS_4, CRYSTAL_OF_INPROGRESS_4, CRYSTAL_OF_FOUL_4, CRYSTAL_OF_DEFEAT_4, CRYSTAL_OF_VICTORY_4, CRYSTAL_OF_PROGRESS_5, CRYSTAL_OF_INPROGRESS_5, CRYSTAL_OF_FOUL_5, CRYSTAL_OF_DEFEAT_5, CRYSTAL_OF_VICTORY_5, CRYSTAL_OF_PROGRESS_6, CRYSTAL_OF_INPROGRESS_6, CRYSTAL_OF_FOUL_6, CRYSTAL_OF_DEFEAT_6, CRYSTAL_OF_VICTORY_6);
		
		addStartNpc(GALATEA);
		addTalkId(GALATEA, ALMORS, CAMONIELL, BELTHUS, BASILLA, CELESTIEL, BRYNTHEA, LARA);
		
		addKillId(NOBLE_ANT, NOBLE_ANT_LEADER, WYRM, TYRANT, TYRANT_KINGPIN, BREKA_ORC, BREKA_ORC_ARCHER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, BREKA_ORC_WARRIOR, FETTERED_SOUL, WINDSUS, GIANT_FUNGUS, MANASHEN_GARGOYLE, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, KARUL_BUGBEAR, PAKO_THE_CAT, UNICORN_RACER, SHADOW_TUREN, MIMI_THE_CAT, UNICORN_PHANTASM, SILHOUETTE_TILFO);
		addAttackId(PAKO_THE_CAT, UNICORN_RACER, SHADOW_TUREN, MIMI_THE_CAT, UNICORN_PHANTASM, SILHOUETTE_TILFO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		// GALATEA
		if (event.equals("7634-08a.htm"))
		{
			st.startQuest();
			st.set("Belthus", "1");
			st.set("Brynthea", "1");
			st.set("Celestiel", "1");
			st.set("Camoniell", "1");
			st.set("Basilla", "1");
			st.set("Almors", "1");
			st.giveItems(GALATEA_LETTER, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 122);
		}
		// LARA
		else if (event.equals("7063-02.htm")) // Lara first time to give a list out
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(GALATEA_LETTER, 1);
			
			final int random = Rnd.get(5);
			
			st.giveItems(LARA_LISTS[random][0], 1);
			st.set("Lara", String.valueOf(random + 1)); // avoid 0
		}
		else if (event.equals("7063-04.htm")) // Lara later to give a list out
		{
			final int random = Rnd.get(5);
			
			st.giveItems(LARA_LISTS[random][0], 1);
			st.set("Lara", String.valueOf(random + 1));
		}
		// ALMORS
		else if (event.equals("7635-02.htm"))
		{
			if (st.hasItems(BEGINNER_ARCANA))
			{
				htmltext = "7635-03.htm";
			}
		}
		else if (event.equals("7635-04.htm"))
		{
			st.set("Almors", "2"); // set state ready to fight
			st.takeItems(CRYSTAL_OF_FOUL_1, -1); // just in case he cheated or lost
			st.takeItems(CRYSTAL_OF_DEFEAT_1, -1);
			st.takeItems(BEGINNER_ARCANA, 1);
			st.giveItems(CRYSTAL_OF_PROGRESS_1, 1); // give Starting Crystal
			
			npc.setTarget(player);
			npc.doCast(SkillData.getInstance().getSkill(4126, 1));
		}
		// CAMONIELL
		else if (event.equals("7636-02.htm"))
		{
			if (st.hasItems(BEGINNER_ARCANA))
			{
				htmltext = "7636-03.htm";
			}
		}
		else if (event.equals("7636-04.htm"))
		{
			st.set("Camoniell", "2");
			st.takeItems(CRYSTAL_OF_FOUL_2, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT_2, -1);
			st.takeItems(BEGINNER_ARCANA, 1);
			st.giveItems(CRYSTAL_OF_PROGRESS_2, 1);
			
			npc.setTarget(player);
			npc.doCast(SkillData.getInstance().getSkill(4126, 1));
		}
		// BELTHUS
		else if (event.equals("7637-02.htm"))
		{
			if (st.hasItems(BEGINNER_ARCANA))
			{
				htmltext = "7637-03.htm";
			}
		}
		else if (event.equals("7637-04.htm"))
		{
			st.set("Belthus", "2");
			st.takeItems(CRYSTAL_OF_FOUL_3, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT_3, -1);
			st.takeItems(BEGINNER_ARCANA, 1);
			st.giveItems(CRYSTAL_OF_PROGRESS_3, 1);
			
			npc.setTarget(player);
			npc.doCast(SkillData.getInstance().getSkill(4126, 1));
		}
		// BASILLA
		else if (event.equals("7638-02.htm"))
		{
			if (st.hasItems(BEGINNER_ARCANA))
			{
				htmltext = "7638-03.htm";
			}
		}
		else if (event.equals("7638-04.htm"))
		{
			st.set("Basilla", "2");
			st.takeItems(CRYSTAL_OF_FOUL_4, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT_4, -1);
			st.takeItems(BEGINNER_ARCANA, 1);
			st.giveItems(CRYSTAL_OF_PROGRESS_4, 1);
			
			npc.setTarget(player);
			npc.doCast(SkillData.getInstance().getSkill(4126, 1));
		}
		// CELESTIEL
		else if (event.equals("7639-02.htm"))
		{
			if (st.hasItems(BEGINNER_ARCANA))
			{
				htmltext = "7639-03.htm";
			}
		}
		else if (event.equals("7639-04.htm"))
		{
			st.set("Celestiel", "2");
			st.takeItems(CRYSTAL_OF_FOUL_5, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT_5, -1);
			st.takeItems(BEGINNER_ARCANA, 1);
			st.giveItems(CRYSTAL_OF_PROGRESS_5, 1);
			
			npc.setTarget(player);
			npc.doCast(SkillData.getInstance().getSkill(4126, 1));
		}
		// BRYNTHEA
		else if (event.equals("7640-02.htm"))
		{
			if (st.hasItems(BEGINNER_ARCANA))
			{
				htmltext = "7640-03.htm";
			}
		}
		else if (event.equals("7640-04.htm"))
		{
			st.set("Brynthea", "2");
			st.takeItems(CRYSTAL_OF_FOUL_6, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT_6, -1);
			st.takeItems(BEGINNER_ARCANA, 1);
			st.giveItems(CRYSTAL_OF_PROGRESS_6, 1);
			
			npc.setTarget(player);
			npc.doCast(SkillData.getInstance().getSkill(4126, 1));
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		final int cond = st.getInt("cond");
		final int npcId = npc.getId();
		
		switch (st.getState())
		{
			case CREATED:
				if ((player.getClassId() != ClassId.WIZARD) && (player.getClassId() != ClassId.ELF_WIZARD) && (player.getClassId() != ClassId.DARK_ELF_WIZARD))
				{
					htmltext = "7634-01.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7634-02.htm";
				}
				else
				{
					htmltext = "7634-03.htm";
				}
				break;
			
			case STARTED:
				switch (npcId)
				{
					case LARA:
						if (cond == 1)
						{
							htmltext = "7063-01.htm";
						}
						else
						{
							if (st.getInt("Lara") == 0)
							{
								htmltext = "7063-03.htm";
							}
							else
							{
								final int[] laraPart = LARA_LISTS[st.getInt("Lara") - 1];
								if ((st.getItemsCount(laraPart[1]) < 30) || (st.getItemsCount(laraPart[2]) < 30))
								{
									htmltext = "7063-05.htm";
								}
								else
								{
									htmltext = "7063-06.htm";
									st.set("cond", "3");
									st.unset("Lara");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(laraPart[0], 1);
									st.takeItems(laraPart[1], -1);
									st.takeItems(laraPart[2], -1);
									st.giveItems(BEGINNER_ARCANA, 2);
								}
							}
						}
						break;
					
					case GALATEA:
						if (cond == 1)
						{
							htmltext = "7634-09.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = (!st.hasItems(BEGINNER_ARCANA)) ? "7634-10.htm" : "7634-11.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7634-12.htm";
							st.takeItems(BEGINNER_ARCANA, -1);
							st.takeItems(ALMORS_ARCANA, -1);
							st.takeItems(BASILLIA_ARCANA, -1);
							st.takeItems(BELTHUS_ARCANA, -1);
							st.takeItems(BRYNTHEA_ARCANA, -1);
							st.takeItems(CAMONIELL_ARCANA, -1);
							st.takeItems(CELESTIEL_ARCANA, -1);
							st.takeItems(LARA_LIST_1, -1);
							st.takeItems(LARA_LIST_2, -1);
							st.takeItems(LARA_LIST_3, -1);
							st.takeItems(LARA_LIST_4, -1);
							st.takeItems(LARA_LIST_5, -1);
							st.giveItems(MARK_OF_SUMMONER, 1);
							st.rewardExpAndSp(148409, 30000);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case ALMORS:
						int almorsStat = st.getInt("Almors");
						if (almorsStat == 1)
						{
							htmltext = "7635-01.htm";
						}
						else if (almorsStat == 2)
						{
							htmltext = "7635-08.htm";
						}
						else if (almorsStat == 3)
						{
							htmltext = "7635-09.htm";
						}
						else if (almorsStat == 4)
						{
							htmltext = "7635-05.htm";
						}
						else if (almorsStat == 5)
						{
							htmltext = "7635-06.htm";
						}
						else if (almorsStat == 6)
						{
							htmltext = "7635-07.htm";
							st.set("Almors", "7");
							st.takeItems(CRYSTAL_OF_VICTORY_1, -1);
							st.giveItems(ALMORS_ARCANA, 1);
							
							if (st.hasItems(CAMONIELL_ARCANA, BELTHUS_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA))
							{
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (almorsStat == 7)
						{
							htmltext = "7635-10.htm";
						}
						break;
					
					case CAMONIELL:
						int camoniellStat = st.getInt("Camoniell");
						if (camoniellStat == 1)
						{
							htmltext = "7636-01.htm";
						}
						else if (camoniellStat == 2)
						{
							htmltext = "7636-08.htm";
						}
						else if (camoniellStat == 3)
						{
							htmltext = "7636-09.htm";
						}
						else if (camoniellStat == 4)
						{
							htmltext = "7636-05.htm";
						}
						else if (camoniellStat == 5)
						{
							htmltext = "7636-06.htm";
						}
						else if (camoniellStat == 6)
						{
							htmltext = "7636-07.htm";
							st.set("Camoniell", "7");
							st.takeItems(CRYSTAL_OF_VICTORY_2, -1);
							st.giveItems(CAMONIELL_ARCANA, 1);
							
							if (st.hasItems(ALMORS_ARCANA, BELTHUS_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA))
							{
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (camoniellStat == 7)
						{
							htmltext = "7636-10.htm";
						}
						break;
					
					case BELTHUS:
						int belthusStat = st.getInt("Belthus");
						if (belthusStat == 1)
						{
							htmltext = "7637-01.htm";
						}
						else if (belthusStat == 2)
						{
							htmltext = "7637-08.htm";
						}
						else if (belthusStat == 3)
						{
							htmltext = "7637-09.htm";
						}
						else if (belthusStat == 4)
						{
							htmltext = "7637-05.htm";
						}
						else if (belthusStat == 5)
						{
							htmltext = "7637-06.htm";
						}
						else if (belthusStat == 6)
						{
							htmltext = "7637-07.htm";
							st.set("Belthus", "7");
							st.takeItems(CRYSTAL_OF_VICTORY_3, -1);
							st.giveItems(BELTHUS_ARCANA, 1);
							
							if (st.hasItems(ALMORS_ARCANA, CAMONIELL_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA))
							{
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (belthusStat == 7)
						{
							htmltext = "7637-10.htm";
						}
						break;
					
					case BASILLA:
						int basillaStat = st.getInt("Basilla");
						if (basillaStat == 1)
						{
							htmltext = "7638-01.htm";
						}
						else if (basillaStat == 2)
						{
							htmltext = "7638-08.htm";
						}
						else if (basillaStat == 3)
						{
							htmltext = "7638-09.htm";
						}
						else if (basillaStat == 4)
						{
							htmltext = "7638-05.htm";
						}
						else if (basillaStat == 5)
						{
							htmltext = "7638-06.htm";
						}
						else if (basillaStat == 6)
						{
							htmltext = "7638-07.htm";
							st.set("Basilla", "7");
							st.takeItems(CRYSTAL_OF_VICTORY_4, -1);
							st.giveItems(BASILLIA_ARCANA, 1);
							
							if (st.hasItems(ALMORS_ARCANA, CAMONIELL_ARCANA, BELTHUS_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA))
							{
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (basillaStat == 7)
						{
							htmltext = "7638-10.htm";
						}
						break;
					
					case CELESTIEL:
						int celestielStat = st.getInt("Celestiel");
						if (celestielStat == 1)
						{
							htmltext = "7639-01.htm";
						}
						else if (celestielStat == 2)
						{
							htmltext = "7639-08.htm";
						}
						else if (celestielStat == 3)
						{
							htmltext = "7639-09.htm";
						}
						else if (celestielStat == 4)
						{
							htmltext = "7639-05.htm";
						}
						else if (celestielStat == 5)
						{
							htmltext = "7639-06.htm";
						}
						else if (celestielStat == 6)
						{
							htmltext = "7639-07.htm";
							st.set("Celestiel", "7");
							st.takeItems(CRYSTAL_OF_VICTORY_5, -1);
							st.giveItems(CELESTIEL_ARCANA, 1);
							
							if (st.hasItems(ALMORS_ARCANA, CAMONIELL_ARCANA, BELTHUS_ARCANA, BASILLIA_ARCANA, BRYNTHEA_ARCANA))
							{
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (celestielStat == 7)
						{
							htmltext = "7639-10.htm";
						}
						break;
					
					case BRYNTHEA:
						int bryntheaStat = st.getInt("Brynthea");
						if (bryntheaStat == 1)
						{
							htmltext = "7640-01.htm";
						}
						else if (bryntheaStat == 2)
						{
							htmltext = "7640-08.htm";
						}
						else if (bryntheaStat == 3)
						{
							htmltext = "7640-09.htm";
						}
						else if (bryntheaStat == 4)
						{
							htmltext = "7640-05.htm";
						}
						else if (bryntheaStat == 5)
						{
							htmltext = "7640-06.htm";
						}
						else if (bryntheaStat == 6)
						{
							htmltext = "7640-07.htm";
							st.set("Brynthea", "7");
							st.takeItems(CRYSTAL_OF_VICTORY_6, -1);
							st.giveItems(BRYNTHEA_ARCANA, 1);
							
							if (st.hasItems(ALMORS_ARCANA, CAMONIELL_ARCANA, BELTHUS_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA))
							{
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if (bryntheaStat == 7)
						{
							htmltext = "7640-10.htm";
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
	public String onDeath(L2Character killer, L2PcInstance player)
	{
		if (!(killer instanceof L2Attackable))
		{
			return null;
		}
		
		ScriptState st = checkPlayerState(player, (L2Npc) killer, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (((L2Npc) killer).getId())
		{
			case PAKO_THE_CAT:
				if (st.getInt("Almors") == 3)
				{
					st.set("Almors", "4");
					st.giveItems(CRYSTAL_OF_DEFEAT_1, 1);
				}
				break;
			
			case UNICORN_RACER:
				if (st.getInt("Camoniell") == 3)
				{
					st.set("Camoniell", "4");
					st.giveItems(CRYSTAL_OF_DEFEAT_2, 1);
				}
				break;
			
			case SHADOW_TUREN:
				if (st.getInt("Belthus") == 3)
				{
					st.set("Belthus", "4");
					st.giveItems(CRYSTAL_OF_DEFEAT_3, 1);
				}
				break;
			
			case MIMI_THE_CAT:
				if (st.getInt("Basilla") == 3)
				{
					st.set("Basilla", "4");
					st.giveItems(CRYSTAL_OF_DEFEAT_4, 1);
				}
				break;
			
			case UNICORN_PHANTASM:
				if (st.getInt("Celestiel") == 3)
				{
					st.set("Celestiel", "4");
					st.giveItems(CRYSTAL_OF_DEFEAT_5, 1);
				}
				break;
			
			case SILHOUETTE_TILFO:
				if (st.getInt("Brynthea") == 3)
				{
					st.set("Brynthea", "4");
					st.giveItems(CRYSTAL_OF_DEFEAT_6, 1);
				}
				break;
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case GIANT_FUNGUS:
				if (st.getInt("Lara") == 1)
				{
					st.dropItems(SAC_OF_REDSPORES, 1, 30, 800000);
				}
				break;
			
			case LETO_LIZARDMAN:
			case LETO_LIZARDMAN_ARCHER:
				if (st.getInt("Lara") == 1)
				{
					st.dropItems(LETO_LIZARDMAN_AMULET, 1, 30, 250000);
				}
				break;
			
			case LETO_LIZARDMAN_SOLDIER:
			case LETO_LIZARDMAN_WARRIOR:
				if (st.getInt("Lara") == 1)
				{
					st.dropItems(LETO_LIZARDMAN_AMULET, 1, 30, 500000);
				}
				break;
			
			case LETO_LIZARDMAN_SHAMAN:
			case LETO_LIZARDMAN_OVERLORD:
				if (st.getInt("Lara") == 1)
				{
					st.dropItems(LETO_LIZARDMAN_AMULET, 1, 30, 750000);
				}
				break;
			
			case MANASHEN_GARGOYLE:
				if (st.getInt("Lara") == 2)
				{
					st.dropItems(SHARDS_OF_MANASHEN, 1, 30, 800000);
				}
				break;
			
			case KARUL_BUGBEAR:
				if (st.getInt("Lara") == 2)
				{
					st.dropItems(KARUL_BUGBEAR_TOTEM, 1, 30, 800000);
				}
				break;
			
			case BREKA_ORC:
			case BREKA_ORC_ARCHER:
			case BREKA_ORC_WARRIOR:
				if (st.getInt("Lara") == 3)
				{
					st.dropItems(BREKA_ORC_TOTEM, 1, 30, 250000);
				}
				break;
			
			case BREKA_ORC_SHAMAN:
			case BREKA_ORC_OVERLORD:
				if (st.getInt("Lara") == 3)
				{
					st.dropItems(BREKA_ORC_TOTEM, 1, 30, 500000);
				}
				break;
			
			case FETTERED_SOUL:
				if (st.getInt("Lara") == 3)
				{
					st.dropItems(CRIMSON_BLOODSTONE, 1, 30, 600000);
				}
				break;
			
			case WINDSUS:
				if (st.getInt("Lara") == 4)
				{
					st.dropItems(TUSK_OF_WINDSUS, 1, 30, 700000);
				}
				break;
			
			case TYRANT:
			case TYRANT_KINGPIN:
				if (st.getInt("Lara") == 4)
				{
					st.dropItems(TALONS_OF_TYRANT, 1, 30, 500000);
				}
				break;
			
			case NOBLE_ANT:
			case NOBLE_ANT_LEADER:
				if (st.getInt("Lara") == 5)
				{
					st.dropItems(WINGS_OF_DRONEANT, 1, 30, 600000);
				}
				break;
			
			case WYRM:
				if (st.getInt("Lara") == 5)
				{
					st.dropItems(FANGS_OF_WYRM, 1, 30, 500000);
				}
				break;
			
			case PAKO_THE_CAT:
				if ((st.getInt("Almors") == 3) && duelsInProgress.containsKey(npcId))
				{
					st.set("Almors", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(CRYSTAL_OF_INPROGRESS_1, -1);
					st.giveItems(CRYSTAL_OF_VICTORY_1, 1);
					npc.broadcastNpcSay("I'm sorry, Lord!");
					st.getPlayer().removeNotifyQuestOfDeath(st);
					duelsInProgress.remove(npcId);
				}
				break;
			
			case UNICORN_RACER:
				if ((st.getInt("Camoniell") == 3) && duelsInProgress.containsKey(npcId))
				{
					st.set("Camoniell", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(CRYSTAL_OF_INPROGRESS_2, -1);
					st.giveItems(CRYSTAL_OF_VICTORY_2, 1);
					npc.broadcastNpcSay("I LOSE");
					st.getPlayer().removeNotifyQuestOfDeath(st);
					duelsInProgress.remove(npcId);
				}
				break;
			
			case SHADOW_TUREN:
				if ((st.getInt("Belthus") == 3) && duelsInProgress.containsKey(npcId))
				{
					st.set("Belthus", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(CRYSTAL_OF_INPROGRESS_3, -1);
					st.giveItems(CRYSTAL_OF_VICTORY_3, 1);
					npc.broadcastNpcSay("Ugh! I lost...!");
					st.getPlayer().removeNotifyQuestOfDeath(st);
					duelsInProgress.remove(npcId);
				}
				break;
			
			case MIMI_THE_CAT:
				if ((st.getInt("Basilla") == 3) && duelsInProgress.containsKey(npcId))
				{
					st.set("Basilla", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(CRYSTAL_OF_INPROGRESS_4, -1);
					st.giveItems(CRYSTAL_OF_VICTORY_4, 1);
					npc.broadcastNpcSay("Lost! Sorry, Lord!");
					st.getPlayer().removeNotifyQuestOfDeath(st);
					duelsInProgress.remove(npcId);
				}
				break;
			
			case UNICORN_PHANTASM:
				if ((st.getInt("Celestiel") == 3) && duelsInProgress.containsKey(npcId))
				{
					st.set("Celestiel", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(CRYSTAL_OF_INPROGRESS_5, -1);
					st.giveItems(CRYSTAL_OF_VICTORY_5, 1);
					npc.broadcastNpcSay("I LOSE");
					st.getPlayer().removeNotifyQuestOfDeath(st);
					duelsInProgress.remove(npcId);
				}
				break;
			
			case SILHOUETTE_TILFO:
				if ((st.getInt("Brynthea") == 3) && duelsInProgress.containsKey(npcId))
				{
					st.set("Brynthea", "6");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(CRYSTAL_OF_INPROGRESS_6, -1);
					st.giveItems(CRYSTAL_OF_VICTORY_6, 1);
					npc.broadcastNpcSay("Ugh! Can this be happening?!");
					st.getPlayer().removeNotifyQuestOfDeath(st);
					duelsInProgress.remove(npcId);
				}
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		ScriptState st = checkPlayerState(attacker, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		st.addNotifyOfDeath();
		
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case PAKO_THE_CAT:
				if ((st.getInt("Almors") == 2) && isPet && (npc.getCurrentHp() == npc.getStat().getMaxHp()))
				{
					st.set("Almors", "3");
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(CRYSTAL_OF_PROGRESS_1, -1);
					st.giveItems(CRYSTAL_OF_INPROGRESS_1, 1);
					npc.broadcastNpcSay("Whhiisshh!");
					duelsInProgress.put(npcId, new ProgressDuelMob(attacker, attacker.getPet()));
				}
				else if ((st.getInt("Almors") == 3) && duelsInProgress.containsKey(npcId))
				{
					ProgressDuelMob duel = duelsInProgress.get(npcId);
					// check if the attacker is the same pet as the one that attacked before.
					if (!isPet || (attacker.getPet() != duel.getPet())) // if a foul occured find the player who had the duel in progress and give a foul crystal
					{
						L2PcInstance foulPlayer = duel.getAttacker();
						if (foulPlayer != null)
						{
							st = foulPlayer.getScriptState(getName());
							if (st != null)
							{
								st.set("Almors", "5");
								st.takeItems(CRYSTAL_OF_PROGRESS_1, -1);
								st.takeItems(CRYSTAL_OF_INPROGRESS_1, -1);
								st.giveItems(CRYSTAL_OF_FOUL_1, 1);
								st.getPlayer().removeNotifyQuestOfDeath(st);
								npc.broadcastNpcSay("Rule violation!");
								npc.doDie(npc);
							}
						}
					}
				}
				break;
			
			case UNICORN_RACER:
				if ((st.getInt("Camoniell") == 2) && isPet && (npc.getCurrentHp() == npc.getStat().getMaxHp()))
				{
					st.set("Camoniell", "3");
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(CRYSTAL_OF_PROGRESS_2, -1);
					st.giveItems(CRYSTAL_OF_INPROGRESS_2, 1);
					npc.broadcastNpcSay("START DUEL");
					duelsInProgress.put(npcId, new ProgressDuelMob(attacker, attacker.getPet()));
				}
				else if ((st.getInt("Camoniell") == 3) && duelsInProgress.containsKey(npcId))
				{
					ProgressDuelMob duel = duelsInProgress.get(npcId);
					if (!isPet || (attacker.getPet() != duel.getPet()))
					{
						L2PcInstance foulPlayer = duel.getAttacker();
						if (foulPlayer != null)
						{
							st = foulPlayer.getScriptState(getName());
							if (st != null)
							{
								st.set("Camoniell", "5");
								st.takeItems(CRYSTAL_OF_PROGRESS_2, -1);
								st.takeItems(CRYSTAL_OF_INPROGRESS_2, -1);
								st.giveItems(CRYSTAL_OF_FOUL_2, 1);
								st.getPlayer().removeNotifyQuestOfDeath(st);
								npc.broadcastNpcSay("RULE VIOLATION");
								npc.doDie(npc);
							}
						}
					}
				}
				break;
			
			case SHADOW_TUREN:
				if ((st.getInt("Belthus") == 2) && isPet && (npc.getCurrentHp() == npc.getStat().getMaxHp()))
				{
					st.set("Belthus", "3");
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(CRYSTAL_OF_PROGRESS_3, -1);
					st.giveItems(CRYSTAL_OF_INPROGRESS_3, 1);
					npc.broadcastNpcSay("So shall we start?!");
					duelsInProgress.put(npcId, new ProgressDuelMob(attacker, attacker.getPet()));
				}
				else if ((st.getInt("Belthus") == 3) && duelsInProgress.containsKey(npcId))
				{
					ProgressDuelMob duel = duelsInProgress.get(npcId);
					if (!isPet || (attacker.getPet() != duel.getPet()))
					{
						L2PcInstance foulPlayer = duel.getAttacker();
						if (foulPlayer != null)
						{
							st = foulPlayer.getScriptState(getName());
							if (st != null)
							{
								st.set("Belthus", "5");
								st.takeItems(CRYSTAL_OF_PROGRESS_3, -1);
								st.takeItems(CRYSTAL_OF_INPROGRESS_3, -1);
								st.giveItems(CRYSTAL_OF_FOUL_3, 1);
								st.getPlayer().removeNotifyQuestOfDeath(st);
								npc.broadcastNpcSay("Rule violation!!!");
								npc.doDie(npc);
							}
						}
					}
				}
				break;
			
			case MIMI_THE_CAT:
				if ((st.getInt("Basilla") == 2) && isPet && (npc.getCurrentHp() == npc.getStat().getMaxHp()))
				{
					st.set("Basilla", "3");
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(CRYSTAL_OF_PROGRESS_4, -1);
					st.giveItems(CRYSTAL_OF_INPROGRESS_4, 1);
					npc.broadcastNpcSay("Whish! Fight!");
					duelsInProgress.put(npcId, new ProgressDuelMob(attacker, attacker.getPet()));
				}
				else if ((st.getInt("Basilla") == 3) && duelsInProgress.containsKey(npcId))
				{
					ProgressDuelMob duel = duelsInProgress.get(npcId);
					if (!isPet || (attacker.getPet() != duel.getPet()))
					{
						L2PcInstance foulPlayer = duel.getAttacker();
						if (foulPlayer != null)
						{
							st = foulPlayer.getScriptState(getName());
							if (st != null)
							{
								st.set("Basilla", "5");
								st.takeItems(CRYSTAL_OF_PROGRESS_4, -1);
								st.takeItems(CRYSTAL_OF_INPROGRESS_4, -1);
								st.giveItems(CRYSTAL_OF_FOUL_4, 1);
								st.getPlayer().removeNotifyQuestOfDeath(st);
								npc.broadcastNpcSay("Rule violation!");
								npc.doDie(npc);
							}
						}
					}
				}
				break;
			
			case UNICORN_PHANTASM:
				if ((st.getInt("Celestiel") == 2) && isPet && (npc.getCurrentHp() == npc.getStat().getMaxHp()))
				{
					st.set("Celestiel", "3");
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(CRYSTAL_OF_PROGRESS_5, -1);
					st.giveItems(CRYSTAL_OF_INPROGRESS_5, 1);
					npc.broadcastNpcSay("START DUEL");
					duelsInProgress.put(npcId, new ProgressDuelMob(attacker, attacker.getPet()));
				}
				else if ((st.getInt("Celestiel") == 3) && duelsInProgress.containsKey(npcId))
				{
					ProgressDuelMob duel = duelsInProgress.get(npcId);
					if (!isPet || (attacker.getPet() != duel.getPet()))
					{
						L2PcInstance foulPlayer = duel.getAttacker();
						if (foulPlayer != null)
						{
							st = foulPlayer.getScriptState(getName());
							if (st != null)
							{
								st.set("Celestiel", "5");
								st.takeItems(CRYSTAL_OF_PROGRESS_5, -1);
								st.takeItems(CRYSTAL_OF_INPROGRESS_5, -1);
								st.giveItems(CRYSTAL_OF_FOUL_5, 1);
								st.getPlayer().removeNotifyQuestOfDeath(st);
								npc.broadcastNpcSay("RULE VIOLATION");
								npc.doDie(npc);
							}
						}
					}
				}
				break;
			
			case SILHOUETTE_TILFO:
				if ((st.getInt("Brynthea") == 2) && isPet && (npc.getCurrentHp() == npc.getStat().getMaxHp()))
				{
					st.set("Brynthea", "3");
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(CRYSTAL_OF_PROGRESS_6, -1);
					st.giveItems(CRYSTAL_OF_INPROGRESS_6, 1);
					npc.broadcastNpcSay("I'll walk all over you!");
					duelsInProgress.put(npcId, new ProgressDuelMob(attacker, attacker.getPet()));
				}
				else if ((st.getInt("Brynthea") == 3) && duelsInProgress.containsKey(npcId))
				{
					ProgressDuelMob duel = duelsInProgress.get(npcId);
					if (!isPet || (attacker.getPet() != duel.getPet()))
					{
						L2PcInstance foulPlayer = duel.getAttacker();
						if (foulPlayer != null)
						{
							st = foulPlayer.getScriptState(getName());
							if (st != null)
							{
								st.set("Brynthea", "5");
								st.takeItems(CRYSTAL_OF_PROGRESS_6, -1);
								st.takeItems(CRYSTAL_OF_INPROGRESS_6, -1);
								st.giveItems(CRYSTAL_OF_FOUL_6, 1);
								st.getPlayer().removeNotifyQuestOfDeath(st);
								npc.broadcastNpcSay("Rule violation!!!");
								npc.doDie(npc);
							}
						}
					}
				}
				break;
		}
		
		return null;
	}
	
	private final class ProgressDuelMob
	{
		private final L2PcInstance attacker;
		private final L2Summon pet;
		
		public ProgressDuelMob(L2PcInstance attacker, L2Summon pet)
		{
			this.attacker = attacker;
			this.pet = pet;
		}
		
		public L2PcInstance getAttacker()
		{
			return attacker;
		}
		
		public L2Summon getPet()
		{
			return pet;
		}
	}
}
