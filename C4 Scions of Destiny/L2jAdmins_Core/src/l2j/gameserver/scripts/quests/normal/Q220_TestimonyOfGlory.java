package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Testimony of Glory. Adapted from L2Acis, repaired and implemented by zarie.
 */
public class Q220_TestimonyOfGlory extends Script
{
	// Items
	private static final int VOKIAN_ORDER_1 = 3204;
	private static final int MANASHEN_SHARD = 3205;
	private static final int TYRANT_TALON = 3206;
	private static final int GUARDIAN_BASILISK_FANG = 3207;
	private static final int VOKIAN_ORDER_2 = 3208;
	private static final int NECKLACE_OF_AUTHORITY = 3209;
	private static final int CHIANTA_ORDER_1 = 3210;
	private static final int SCEPTER_OF_BREKA = 3211;
	private static final int SCEPTER_OF_ENKU = 3212;
	private static final int SCEPTER_OF_VUKU = 3213;
	private static final int SCEPTER_OF_TUREK = 3214;
	private static final int SCEPTER_OF_TUNATH = 3215;
	private static final int CHIANTA_ORDER_2 = 3216;
	private static final int CHIANTA_ORDER_3 = 3217;
	private static final int TAMLIN_ORC_SKULL = 3218;
	private static final int TIMAK_ORC_HEAD = 3219;
	private static final int SCEPTER_BOX = 3220;
	private static final int PASHIKA_HEAD = 3221;
	private static final int VULTUS_HEAD = 3222;
	private static final int GLOVE_OF_VOLTAR = 3223;
	private static final int ENKU_OVERLORD_HEAD = 3224;
	private static final int GLOVE_OF_KEPRA = 3225;
	private static final int MAKUM_BUGBEAR_HEAD = 3226;
	private static final int GLOVE_OF_BURAI = 3227;
	private static final int MANAKIA_LETTER_1 = 3228;
	private static final int MANAKIA_LETTER_2 = 3229;
	private static final int KASMAN_LETTER_1 = 3230;
	private static final int KASMAN_LETTER_2 = 3231;
	private static final int KASMAN_LETTER_3 = 3232;
	private static final int DRIKO_CONTRACT = 3233;
	private static final int STAKATO_DRONE_HUSK = 3234;
	private static final int TANAPI_ORDER = 3235;
	private static final int SCEPTER_OF_TANTOS = 3236;
	private static final int RITUAL_BOX = 3237;
	
	// Rewards
	private static final int MARK_OF_GLORY = 3203;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// NPCs
	private static final int KASMAN = 7501;
	private static final int VOKIAN = 7514;
	private static final int MANAKIA = 7515;
	private static final int KAKAI = 7565;
	private static final int TANAPI = 7571;
	private static final int VOLTAR = 7615;
	private static final int KEPRA = 7616;
	private static final int BURAI = 7617;
	private static final int HARAK = 7618;
	private static final int DRIKO = 7619;
	private static final int CHIANTA = 7642;
	
	// Monsters
	private static final int TYRANT = 192;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int GUARDIAN_BASILISK = 550;
	private static final int MANASHEN_GARGOYLE = 563;
	private static final int TIMAK_ORC = 583;
	private static final int TIMAK_ORC_ARCHER = 584;
	private static final int TIMAK_ORC_SOLDIER = 585;
	private static final int TIMAK_ORC_WARRIOR = 586;
	private static final int TIMAK_ORC_SHAMAN = 587;
	private static final int TIMAK_ORC_OVERLORD = 588;
	private static final int TAMLIN_ORC = 601;
	private static final int TAMLIN_ORC_ARCHER = 602;
	private static final int RAGNA_ORC_OVERLORD = 778;
	private static final int RAGNA_ORC_SEER = 779;
	private static final int PASHIKA_SON_OF_VOLTAR = 5080;
	private static final int VULTUS_SON_OF_VOLTAR = 5081;
	private static final int ENKU_ORC_OVERLORD = 5082;
	private static final int MAKUM_BUGBEAR_THUG = 5083;
	private static final int REVENANT_OF_TANTOS_CHIEF = 5086;
	
	// Checks & Instances
	private static boolean sonsOfVoltar = false;
	private static boolean enkuOrcOverlords = false;
	private static boolean makumBugbearThugs = false;
	
	public Q220_TestimonyOfGlory()
	{
		super(220, "Testimony of Glory");
		
		registerItems(VOKIAN_ORDER_1, MANASHEN_SHARD, TYRANT_TALON, GUARDIAN_BASILISK_FANG, VOKIAN_ORDER_2, NECKLACE_OF_AUTHORITY, CHIANTA_ORDER_1, SCEPTER_OF_BREKA, SCEPTER_OF_ENKU, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH, CHIANTA_ORDER_2, CHIANTA_ORDER_3, TAMLIN_ORC_SKULL, TIMAK_ORC_HEAD, SCEPTER_BOX, PASHIKA_HEAD, VULTUS_HEAD, GLOVE_OF_VOLTAR, ENKU_OVERLORD_HEAD, GLOVE_OF_KEPRA, MAKUM_BUGBEAR_HEAD, GLOVE_OF_BURAI, MANAKIA_LETTER_1, MANAKIA_LETTER_2, KASMAN_LETTER_1, KASMAN_LETTER_2, KASMAN_LETTER_3, DRIKO_CONTRACT, STAKATO_DRONE_HUSK, TANAPI_ORDER, SCEPTER_OF_TANTOS, RITUAL_BOX);
		
		addStartNpc(VOKIAN);
		addTalkId(KASMAN, VOKIAN, MANAKIA, KAKAI, TANAPI, VOLTAR, KEPRA, BURAI, HARAK, DRIKO, CHIANTA);
		
		addAttackId(RAGNA_ORC_OVERLORD, RAGNA_ORC_SEER, REVENANT_OF_TANTOS_CHIEF);
		addKillId(TYRANT, MARSH_STAKATO_DRONE, GUARDIAN_BASILISK, MANASHEN_GARGOYLE, TIMAK_ORC, TIMAK_ORC_ARCHER, TIMAK_ORC_SOLDIER, TIMAK_ORC_WARRIOR, TIMAK_ORC_SHAMAN, TIMAK_ORC_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, RAGNA_ORC_OVERLORD, RAGNA_ORC_SEER, PASHIKA_SON_OF_VOLTAR, VULTUS_SON_OF_VOLTAR, ENKU_ORC_OVERLORD, MAKUM_BUGBEAR_THUG, REVENANT_OF_TANTOS_CHIEF);
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
		
		// VOKIAN
		if (event.equalsIgnoreCase("7514-05a.htm"))
		{
			st.startQuest();
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(VOKIAN_ORDER_1, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 109);
		}
		// CHIANTA
		else if (event.equalsIgnoreCase("7642-03.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(VOKIAN_ORDER_2, 1);
			st.giveItems(CHIANTA_ORDER_1, 1);
		}
		else if (event.equalsIgnoreCase("7642-07.htm"))
		{
			st.takeItems(CHIANTA_ORDER_1, 1);
			st.takeItems(KASMAN_LETTER_1, 1);
			st.takeItems(MANAKIA_LETTER_1, 1);
			st.takeItems(MANAKIA_LETTER_2, 1);
			st.takeItems(SCEPTER_OF_BREKA, 1);
			st.takeItems(SCEPTER_OF_ENKU, 1);
			st.takeItems(SCEPTER_OF_TUNATH, 1);
			st.takeItems(SCEPTER_OF_TUREK, 1);
			st.takeItems(SCEPTER_OF_VUKU, 1);
			if (player.getLevel() >= 37)
			{
				st.set("cond", "6");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(CHIANTA_ORDER_3, 1);
			}
			else
			{
				htmltext = "7642-06.htm";
				st.giveItems(CHIANTA_ORDER_2, 1);
			}
		}
		// KASMAN
		else if (event.equalsIgnoreCase("7501-02.htm") && !st.hasItems(SCEPTER_OF_VUKU))
		{
			if (st.hasItems(KASMAN_LETTER_1))
			{
				htmltext = "7501-04.htm";
			}
			else
			{
				htmltext = "7501-03.htm";
				st.giveItems(KASMAN_LETTER_1, 1);
			}
			st.addRadar(-2150, 124443, -3724);
		}
		else if (event.equalsIgnoreCase("7501-05.htm") && !st.hasItems(SCEPTER_OF_TUREK))
		{
			if (st.hasItems(KASMAN_LETTER_2))
			{
				htmltext = "7501-07.htm";
			}
			else
			{
				htmltext = "7501-06.htm";
				st.giveItems(KASMAN_LETTER_2, 1);
			}
			st.addRadar(-94294, 110818, -3563);
		}
		else if (event.equalsIgnoreCase("7501-08.htm") && !st.hasItems(SCEPTER_OF_TUNATH))
		{
			if (st.hasItems(KASMAN_LETTER_3))
			{
				htmltext = "7501-10.htm";
			}
			else
			{
				htmltext = "7501-09.htm";
				st.giveItems(KASMAN_LETTER_3, 1);
			}
			st.addRadar(-55217, 200628, -3724);
		}
		// MANAKIA
		else if (event.equalsIgnoreCase("7515-02.htm") && !st.hasItems(SCEPTER_OF_BREKA))
		{
			if (st.hasItems(MANAKIA_LETTER_1))
			{
				htmltext = "7515-04.htm";
			}
			else
			{
				htmltext = "7515-03.htm";
				st.giveItems(MANAKIA_LETTER_1, 1);
			}
			st.addRadar(80100, 119991, -2264);
		}
		else if (event.equalsIgnoreCase("7515-05.htm") && !st.hasItems(SCEPTER_OF_ENKU))
		{
			if (st.hasItems(MANAKIA_LETTER_2))
			{
				htmltext = "7515-07.htm";
			}
			else
			{
				htmltext = "7515-06.htm";
				st.giveItems(MANAKIA_LETTER_2, 1);
			}
			st.addRadar(19815, 189703, -3032);
		}
		// VOLTAR
		else if (event.equalsIgnoreCase("7615-04.htm"))
		{
			st.takeItems(MANAKIA_LETTER_1, 1);
			st.giveItems(GLOVE_OF_VOLTAR, 1);
			if (!sonsOfVoltar)
			{
				addSpawn(PASHIKA_SON_OF_VOLTAR, 80117, 120039, -2259, 0, false, 200000);
				addSpawn(VULTUS_SON_OF_VOLTAR, 80058, 120038, -2259, 0, false, 200000);
				sonsOfVoltar = true;
				
				// Resets Sons Of Voltar
				startTimer("voltar_sons_cleanup", 201000, null, player, false);
			}
		}
		// KEPRA
		else if (event.equalsIgnoreCase("7616-05.htm"))
		{
			st.takeItems(MANAKIA_LETTER_2, 1);
			st.giveItems(GLOVE_OF_KEPRA, 1);
			if (!enkuOrcOverlords)
			{
				addSpawn(ENKU_ORC_OVERLORD, 17368, 189752, -3576, 0, false, 200000);
				addSpawn(ENKU_ORC_OVERLORD, 17368, 189960, -3608, 0, false, 200000);
				addSpawn(ENKU_ORC_OVERLORD, 17544, 190088, -3616, 0, false, 200000);
				addSpawn(ENKU_ORC_OVERLORD, 17800, 190152, -3608, 0, false, 200000);
				enkuOrcOverlords = true;
				
				// Resets Enku Orc Overlords
				startTimer("enku_orcs_cleanup", 201000, null, player, false);
			}
		}
		// BURAI
		else if (event.equalsIgnoreCase("7617-04.htm"))
		{
			st.takeItems(KASMAN_LETTER_2, 1);
			st.giveItems(GLOVE_OF_BURAI, 1);
			if (!makumBugbearThugs)
			{
				addSpawn(MAKUM_BUGBEAR_THUG, -94292, 110781, -3701, 0, false, 200000);
				addSpawn(MAKUM_BUGBEAR_THUG, -94293, 110861, -3701, 0, false, 200000);
				makumBugbearThugs = true;
				
				// Resets Makum Bugbear Thugs
				startTimer("makum_bugbears_cleanup", 201000, null, player, false);
			}
		}
		// HARAK
		else if (event.equalsIgnoreCase("7618-03.htm"))
		{
			st.takeItems(KASMAN_LETTER_3, 1);
			st.giveItems(SCEPTER_OF_TUNATH, 1);
			if (st.hasItems(SCEPTER_OF_BREKA, SCEPTER_OF_ENKU, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK))
			{
				st.set("cond", "5");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		// DRIKO
		else if (event.equalsIgnoreCase("7619-03.htm"))
		{
			st.takeItems(KASMAN_LETTER_1, 1);
			st.giveItems(DRIKO_CONTRACT, 1);
		}
		// TANAPI
		else if (event.equalsIgnoreCase("7571-03.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SCEPTER_BOX, 1);
			st.giveItems(TANAPI_ORDER, 1);
		}
		// Clean ups
		else if (event.equalsIgnoreCase("voltar_sons_cleanup"))
		{
			sonsOfVoltar = false;
			return null;
		}
		else if (event.equalsIgnoreCase("enku_orcs_cleanup"))
		{
			enkuOrcOverlords = false;
			return null;
		}
		else if (event.equalsIgnoreCase("makum_bugbears_cleanup"))
		{
			makumBugbearThugs = false;
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7514-01.htm";
				}
				else if (player.getLevel() < 37)
				{
					htmltext = "7514-02.htm";
				}
				else if (player.getClassId().level() != 1)
				{
					htmltext = "7514-01a.htm";
				}
				else
				{
					htmltext = "7514-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case VOKIAN:
						if (cond == 1)
						{
							htmltext = "7514-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7514-08.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(GUARDIAN_BASILISK_FANG, 10);
							st.takeItems(MANASHEN_SHARD, 10);
							st.takeItems(TYRANT_TALON, 10);
							st.takeItems(VOKIAN_ORDER_1, 1);
							st.giveItems(NECKLACE_OF_AUTHORITY, 1);
							st.giveItems(VOKIAN_ORDER_2, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7514-09.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7514-10.htm";
						}
						break;
					
					case CHIANTA:
						if (cond == 3)
						{
							htmltext = "7642-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7642-04.htm";
						}
						else if (cond == 5)
						{
							if (st.hasItems(CHIANTA_ORDER_2))
							{
								if (player.getLevel() >= 37)
								{
									htmltext = "7642-09.htm";
									st.set("cond", "6");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(CHIANTA_ORDER_2, 1);
									st.giveItems(CHIANTA_ORDER_3, 1);
								}
								else
								{
									htmltext = "7642-08.htm";
								}
							}
							else
							{
								htmltext = "7642-05.htm";
							}
						}
						else if (cond == 6)
						{
							htmltext = "7642-10.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7642-11.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CHIANTA_ORDER_3, 1);
							st.takeItems(NECKLACE_OF_AUTHORITY, 1);
							st.takeItems(TAMLIN_ORC_SKULL, 20);
							st.takeItems(TIMAK_ORC_HEAD, 20);
							st.giveItems(SCEPTER_BOX, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7642-12.htm";
						}
						else if (cond > 8)
						{
							htmltext = "7642-13.htm";
						}
						break;
					
					case KASMAN:
						if (st.hasItems(CHIANTA_ORDER_1))
						{
							htmltext = "7501-01.htm";
						}
						else if (cond > 4)
						{
							htmltext = "7501-11.htm";
						}
						break;
					
					case MANAKIA:
						if (st.hasItems(CHIANTA_ORDER_1))
						{
							htmltext = "7515-01.htm";
						}
						else if (cond > 4)
						{
							htmltext = "7515-08.htm";
						}
						break;
					
					case VOLTAR:
						if (cond > 3)
						{
							if (st.hasItems(MANAKIA_LETTER_1))
							{
								htmltext = "7615-02.htm";
								st.removeRadar(80100, 119991, -2264);
							}
							else if (st.hasItems(GLOVE_OF_VOLTAR))
							{
								htmltext = "7615-05.htm";
								if (!sonsOfVoltar)
								{
									addSpawn(PASHIKA_SON_OF_VOLTAR, 80117, 120039, -2259, 0, false, 200000);
									addSpawn(VULTUS_SON_OF_VOLTAR, 80058, 120038, -2259, 0, false, 200000);
									sonsOfVoltar = true;
									
									// Resets Sons Of Voltar
									startTimer("voltar_sons_cleanup", 201000, null, player, false);
								}
							}
							else if (st.hasItems(PASHIKA_HEAD, VULTUS_HEAD))
							{
								htmltext = "7615-06.htm";
								st.takeItems(PASHIKA_HEAD, 1);
								st.takeItems(VULTUS_HEAD, 1);
								st.giveItems(SCEPTER_OF_BREKA, 1);
								if (st.hasItems(SCEPTER_OF_ENKU, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH))
								{
									st.set("cond", "5");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else if (st.hasItems(SCEPTER_OF_BREKA))
							{
								htmltext = "7615-07.htm";
							}
							else if (st.hasItems(CHIANTA_ORDER_1))
							{
								htmltext = "7615-01.htm";
							}
							else if (cond < 9)
							{
								htmltext = "7615-08.htm";
							}
						}
						break;
					
					case KEPRA:
						if (cond > 3)
						{
							if (st.hasItems(MANAKIA_LETTER_2))
							{
								htmltext = "7616-02.htm";
								st.removeRadar(19815, 189703, -3032);
							}
							else if (st.hasItems(GLOVE_OF_KEPRA))
							{
								htmltext = "7616-05.htm";
								if (!enkuOrcOverlords)
								{
									addSpawn(ENKU_ORC_OVERLORD, 17368, 189752, -3576, 0, false, 200000);
									addSpawn(ENKU_ORC_OVERLORD, 17368, 189960, -3608, 0, false, 200000);
									addSpawn(ENKU_ORC_OVERLORD, 17544, 190088, -3616, 0, false, 200000);
									addSpawn(ENKU_ORC_OVERLORD, 17800, 190152, -3608, 0, false, 200000);
									enkuOrcOverlords = true;
									
									// Resets Enku Orc Overlords
									startTimer("enku_orcs_cleanup", 201000, null, player, false);
								}
							}
							else if (st.getItemsCount(ENKU_OVERLORD_HEAD) == 4)
							{
								htmltext = "7616-06.htm";
								st.takeItems(ENKU_OVERLORD_HEAD, 4);
								st.giveItems(SCEPTER_OF_ENKU, 1);
								if (st.hasItems(SCEPTER_OF_BREKA, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH))
								{
									st.set("cond", "5");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else if (st.hasItems(SCEPTER_OF_ENKU))
							{
								htmltext = "7616-07.htm";
							}
							else if (st.hasItems(CHIANTA_ORDER_1))
							{
								htmltext = "7616-01.htm";
							}
							else if (cond < 9)
							{
								htmltext = "7616-08.htm";
							}
						}
						break;
					
					case BURAI:
						if (cond > 3)
						{
							if (st.hasItems(KASMAN_LETTER_2))
							{
								htmltext = "7617-02.htm";
								st.removeRadar(-94294, 110818, -3563);
							}
							else if (st.hasItems(GLOVE_OF_BURAI))
							{
								htmltext = "7617-04.htm";
								if (!makumBugbearThugs)
								{
									addSpawn(MAKUM_BUGBEAR_THUG, -94292, 110781, -3701, 0, false, 200000);
									addSpawn(MAKUM_BUGBEAR_THUG, -94293, 110861, -3701, 0, false, 200000);
									makumBugbearThugs = true;
									
									// Resets Makum Bugbear Thugs
									startTimer("makum_bugbears_cleanup", 201000, null, player, false);
								}
							}
							else if (st.getItemsCount(MAKUM_BUGBEAR_HEAD) == 2)
							{
								htmltext = "7617-05.htm";
								st.takeItems(MAKUM_BUGBEAR_HEAD, 2);
								st.giveItems(SCEPTER_OF_TUREK, 1);
								if (st.hasItems(SCEPTER_OF_BREKA, SCEPTER_OF_VUKU, SCEPTER_OF_ENKU, SCEPTER_OF_TUNATH))
								{
									st.set("cond", "5");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else if (st.hasItems(SCEPTER_OF_TUREK))
							{
								htmltext = "7617-06.htm";
							}
							else if (st.hasItems(CHIANTA_ORDER_1))
							{
								htmltext = "7617-01.htm";
							}
							else if (cond < 8)
							{
								htmltext = "7617-07.htm";
							}
						}
						break;
					
					case HARAK:
						if (cond > 3)
						{
							if (st.hasItems(KASMAN_LETTER_3))
							{
								htmltext = "7618-02.htm";
								st.removeRadar(-55217, 200628, -3724);
							}
							else if (st.hasItems(SCEPTER_OF_TUNATH))
							{
								htmltext = "7618-04.htm";
							}
							else if (st.hasItems(CHIANTA_ORDER_1))
							{
								htmltext = "7618-01.htm";
							}
							else if (cond < 9)
							{
								htmltext = "7618-05.htm";
							}
						}
						break;
					
					case DRIKO:
						if (cond > 3)
						{
							if (st.hasItems(KASMAN_LETTER_1))
							{
								htmltext = "7619-02.htm";
								st.removeRadar(-2150, 124443, -3724);
							}
							else if (st.hasItems(DRIKO_CONTRACT))
							{
								if (st.getItemsCount(STAKATO_DRONE_HUSK) == 30)
								{
									htmltext = "7619-05.htm";
									st.takeItems(DRIKO_CONTRACT, 1);
									st.takeItems(STAKATO_DRONE_HUSK, 30);
									st.giveItems(SCEPTER_OF_VUKU, 1);
									if (st.hasItems(SCEPTER_OF_BREKA, SCEPTER_OF_TUREK, SCEPTER_OF_ENKU, SCEPTER_OF_TUNATH))
									{
										st.set("cond", "5");
										st.playSound(PlaySoundType.QUEST_MIDDLE);
									}
								}
								else
								{
									htmltext = "7619-04.htm";
								}
							}
							else if (st.hasItems(SCEPTER_OF_VUKU))
							{
								htmltext = "7619-06.htm";
							}
							else if (st.hasItems(CHIANTA_ORDER_1))
							{
								htmltext = "7619-01.htm";
							}
							else if (cond < 8)
							{
								htmltext = "7619-07.htm";
							}
						}
						break;
					
					case TANAPI:
						if (cond == 8)
						{
							htmltext = "7571-01.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7571-04.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7571-05.htm";
							st.set("cond", "11");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SCEPTER_OF_TANTOS, 1);
							st.takeItems(TANAPI_ORDER, 1);
							st.giveItems(RITUAL_BOX, 1);
						}
						else if (cond == 11)
						{
							htmltext = "7571-06.htm";
						}
						break;
					
					case KAKAI:
						if ((cond > 7) && (cond < 11))
						{
							htmltext = "7565-01.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7565-02.htm";
							st.takeItems(RITUAL_BOX, 1);
							st.giveItems(MARK_OF_GLORY, 1);
							st.rewardExpAndSp(91457, 2500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.exitQuest(false);
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
			case TYRANT:
				if ((cond == 1) && st.dropItems(TYRANT_TALON, 1, 10, 500000) && ((st.getItemsCount(GUARDIAN_BASILISK_FANG) + st.getItemsCount(MANASHEN_SHARD)) == 20))
				{
					st.set("cond", "2");
				}
				break;
			
			case GUARDIAN_BASILISK:
				if ((cond == 1) && st.dropItems(GUARDIAN_BASILISK_FANG, 1, 10, 500000) && ((st.getItemsCount(TYRANT_TALON) + st.getItemsCount(MANASHEN_SHARD)) == 20))
				{
					st.set("cond", "2");
				}
				break;
			
			case MANASHEN_GARGOYLE:
				if ((cond == 1) && st.dropItems(MANASHEN_SHARD, 1, 10, 750000) && ((st.getItemsCount(TYRANT_TALON) + st.getItemsCount(GUARDIAN_BASILISK_FANG)) == 20))
				{
					st.set("cond", "2");
				}
				break;
			
			case MARSH_STAKATO_DRONE:
				if (st.hasItems(DRIKO_CONTRACT))
				{
					st.dropItems(STAKATO_DRONE_HUSK, 1, 30, 750000);
				}
				break;
			
			case PASHIKA_SON_OF_VOLTAR:
				if (st.hasItems(GLOVE_OF_VOLTAR) && !st.hasItems(PASHIKA_HEAD))
				{
					st.giveItems(PASHIKA_HEAD, 1);
					if (st.hasItems(VULTUS_HEAD))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(GLOVE_OF_VOLTAR, 1);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
				break;
			
			case VULTUS_SON_OF_VOLTAR:
				if (st.hasItems(GLOVE_OF_VOLTAR) && !st.hasItems(VULTUS_HEAD))
				{
					st.giveItems(VULTUS_HEAD, 1);
					if (st.hasItems(PASHIKA_HEAD))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(GLOVE_OF_VOLTAR, 1);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
				break;
			
			case ENKU_ORC_OVERLORD:
				if (st.hasItems(GLOVE_OF_KEPRA) && st.dropItemsAlways(ENKU_OVERLORD_HEAD, 1, 4))
				{
					st.takeItems(GLOVE_OF_KEPRA, 1);
				}
				break;
			
			case MAKUM_BUGBEAR_THUG:
				if (st.hasItems(GLOVE_OF_BURAI) && st.dropItemsAlways(MAKUM_BUGBEAR_HEAD, 1, 2))
				{
					st.takeItems(GLOVE_OF_BURAI, 1);
				}
				break;
			
			case TIMAK_ORC:
			case TIMAK_ORC_ARCHER:
			case TIMAK_ORC_SOLDIER:
			case TIMAK_ORC_WARRIOR:
			case TIMAK_ORC_SHAMAN:
			case TIMAK_ORC_OVERLORD:
				if ((cond == 6) && st.dropItems(TIMAK_ORC_HEAD, 1, 20, 500000 + ((npc.getId() - TIMAK_ORC) * 100000)) && (st.getItemsCount(TAMLIN_ORC_SKULL) == 20))
				{
					st.set("cond", "7");
				}
				break;
			
			case TAMLIN_ORC:
				if ((cond == 6) && st.dropItems(TAMLIN_ORC_SKULL, 1, 20, 500000) && (st.getItemsCount(TIMAK_ORC_HEAD) == 20))
				{
					st.set("cond", "7");
				}
				break;
			
			case TAMLIN_ORC_ARCHER:
				if ((cond == 6) && st.dropItems(TAMLIN_ORC_SKULL, 1, 20, 600000) && (st.getItemsCount(TIMAK_ORC_HEAD) == 20))
				{
					st.set("cond", "7");
				}
				break;
			
			case RAGNA_ORC_OVERLORD:
			case RAGNA_ORC_SEER:
				if (cond == 9)
				{
					npc.broadcastNpcSay("Too late!");
					addSpawn(REVENANT_OF_TANTOS_CHIEF, npc, true, 200000);
				}
				break;
			
			case REVENANT_OF_TANTOS_CHIEF:
				if ((cond == 9) && st.dropItemsAlways(SCEPTER_OF_TANTOS, 1, 1))
				{
					st.set("cond", "10");
					npc.broadcastNpcSay("I'll get revenge someday!!");
				}
				break;
		}
		
		return null;
	}
}
