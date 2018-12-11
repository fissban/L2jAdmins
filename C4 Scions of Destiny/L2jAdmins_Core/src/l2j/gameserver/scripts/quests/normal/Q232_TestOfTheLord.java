package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

public class Q232_TestOfTheLord extends Script
{
	// NPCs
	private static final int SOMAK = 7510;
	private static final int MANAKIA = 7515;
	private static final int JAKAL = 7558;
	private static final int SUMARI = 7564;
	private static final int KAKAI = 7565;
	private static final int VARKEES = 7566;
	private static final int TANTUS = 7567;
	private static final int HATOS = 7568;
	private static final int TAKUNA = 7641;
	private static final int CHIANTA = 7642;
	private static final int FIRST_ORC = 7643;
	private static final int ANCESTOR_MARTANKUS = 7649;
	
	// Items
	private static final int ORDEAL_NECKLACE = 3391;
	private static final int VARKEES_CHARM = 3392;
	private static final int TANTUS_CHARM = 3393;
	private static final int HATOS_CHARM = 3394;
	private static final int TAKUNA_CHARM = 3395;
	private static final int CHIANTA_CHARM = 3396;
	private static final int MANAKIAS_ORDERS = 3397;
	private static final int BREKA_ORC_FANG = 3398;
	private static final int MANAKIAS_AMULET = 3399;
	private static final int HUGE_ORC_FANG = 3400;
	private static final int SUMARIS_LETTER = 3401;
	private static final int URUTU_BLADE = 3402;
	private static final int TIMAK_ORC_SKULL = 3403;
	private static final int SWORD_INTO_SKULL = 3404;
	private static final int NERUGA_AXE_BLADE = 3405;
	private static final int AXE_OF_CEREMONY = 3406;
	private static final int MARSH_SPIDER_FEELER = 3407;
	private static final int MARSH_SPIDER_FEET = 3408;
	private static final int HANDIWORK_SPIDER_BROOCH = 3409;
	private static final int MONSTEREYE_CORNEA = 3410;
	private static final int MONSTEREYE_WOODCARVING = 3411;
	private static final int BEAR_FANG_NECKLACE = 3412;
	private static final int MARTANKUS_CHARM = 3413;
	private static final int RAGNA_ORC_HEAD = 3414;
	private static final int RAGNA_CHIEF_NOTICE = 3415;
	private static final int BONE_ARROW = 1341;
	private static final int IMMORTAL_FLAME = 3416;
	
	// Rewards
	private static final int MARK_LORD = 3390;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// Monsters
	private static final int MARSH_SPIDER = 233;
	private static final int BREKA_ORC_SHAMAN = 269;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int ENCHANTED_MONSTEREYE = 564;
	private static final int TIMAK_ORC = 583;
	private static final int TIMAK_ORC_ARCHER = 584;
	private static final int TIMAK_ORC_SOLDIER = 585;
	private static final int TIMAK_ORC_WARRIOR = 586;
	private static final int TIMAK_ORC_SHAMAN = 587;
	private static final int TIMAK_ORC_OVERLORD = 588;
	private static final int REGNA_ORC_OVERLORD = 778;
	private static final int REGNA_ORC_SEER = 779;
	
	private static L2Npc firstOrc; // Used to avoid to spawn multiple instances.
	
	public Q232_TestOfTheLord()
	{
		super(232, "Test of the Lord");
		
		registerItems(VARKEES_CHARM, TANTUS_CHARM, HATOS_CHARM, TAKUNA_CHARM, CHIANTA_CHARM, MANAKIAS_ORDERS, BREKA_ORC_FANG, MANAKIAS_AMULET, HUGE_ORC_FANG, SUMARIS_LETTER, URUTU_BLADE, TIMAK_ORC_SKULL, SWORD_INTO_SKULL, NERUGA_AXE_BLADE, AXE_OF_CEREMONY, MARSH_SPIDER_FEELER, MARSH_SPIDER_FEET, HANDIWORK_SPIDER_BROOCH, MONSTEREYE_CORNEA, MONSTEREYE_WOODCARVING, BEAR_FANG_NECKLACE, MARTANKUS_CHARM, RAGNA_ORC_HEAD, RAGNA_ORC_HEAD, IMMORTAL_FLAME);
		
		addStartNpc(KAKAI);
		addTalkId(KAKAI, CHIANTA, HATOS, SOMAK, SUMARI, TAKUNA, TANTUS, JAKAL, VARKEES, MANAKIA, ANCESTOR_MARTANKUS, FIRST_ORC);
		
		addKillId(MARSH_SPIDER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, ENCHANTED_MONSTEREYE, TIMAK_ORC, TIMAK_ORC_ARCHER, TIMAK_ORC_SOLDIER, TIMAK_ORC_WARRIOR, TIMAK_ORC_SHAMAN, TIMAK_ORC_OVERLORD, REGNA_ORC_OVERLORD, REGNA_ORC_SEER);
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
		
		if (event.equalsIgnoreCase("7565-05.htm"))
		{
			st.startQuest();
			st.giveItems(ORDEAL_NECKLACE, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 92);
		}
		else if (event.equalsIgnoreCase("7565-08.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SWORD_INTO_SKULL, 1);
			st.takeItems(AXE_OF_CEREMONY, 1);
			st.takeItems(MONSTEREYE_WOODCARVING, 1);
			st.takeItems(HANDIWORK_SPIDER_BROOCH, 1);
			st.takeItems(ORDEAL_NECKLACE, 1);
			st.takeItems(HUGE_ORC_FANG, 1);
			st.giveItems(BEAR_FANG_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("7566-02.htm"))
		{
			st.giveItems(VARKEES_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7567-02.htm"))
		{
			st.giveItems(TANTUS_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7558-02.htm"))
		{
			st.takeItems(57, 1000);
			st.giveItems(NERUGA_AXE_BLADE, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7568-02.htm"))
		{
			st.giveItems(HATOS_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7641-02.htm"))
		{
			st.giveItems(TAKUNA_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7642-02.htm"))
		{
			st.giveItems(CHIANTA_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (event.equalsIgnoreCase("7643-02.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			startTimer("f_orc_despawn", 10000, null, player, false);
		}
		else if (event.equalsIgnoreCase("7649-04.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BEAR_FANG_NECKLACE, 1);
			st.giveItems(MARTANKUS_CHARM, 1);
		}
		else if (event.equalsIgnoreCase("7649-07.htm"))
		{
			if (firstOrc == null)
			{
				firstOrc = addSpawn(FIRST_ORC, 21036, -107690, -3038, 200000, false, 0);
			}
		}
		else if (event.equalsIgnoreCase("f_orc_despawn"))
		{
			if (firstOrc != null)
			{
				firstOrc.deleteMe();
				firstOrc = null;
			}
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
					htmltext = "7565-01.htm";
				}
				else if (player.getClassId() != ClassId.SHAMAN)
				{
					htmltext = "7565-02.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7565-03.htm";
				}
				else
				{
					htmltext = "7565-04.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case VARKEES:
						if (st.hasItems(HUGE_ORC_FANG))
						{
							htmltext = "7566-05.htm";
						}
						else if (st.hasItems(VARKEES_CHARM))
						{
							if (st.hasItems(MANAKIAS_AMULET))
							{
								htmltext = "7566-04.htm";
								st.takeItems(VARKEES_CHARM, -1);
								st.takeItems(MANAKIAS_AMULET, -1);
								st.giveItems(HUGE_ORC_FANG, 1);
								
								if (st.hasItems(SWORD_INTO_SKULL, AXE_OF_CEREMONY, MONSTEREYE_WOODCARVING, HANDIWORK_SPIDER_BROOCH, ORDEAL_NECKLACE))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
							else
							{
								htmltext = "7566-03.htm";
							}
						}
						else
						{
							htmltext = "7566-01.htm";
						}
						break;
					
					case MANAKIA:
						if (st.hasItems(HUGE_ORC_FANG))
						{
							htmltext = "7515-05.htm";
						}
						else if (st.hasItems(MANAKIAS_AMULET))
						{
							htmltext = "7515-04.htm";
						}
						else if (st.hasItems(MANAKIAS_ORDERS))
						{
							if (st.getItemsCount(BREKA_ORC_FANG) >= 20)
							{
								htmltext = "7515-03.htm";
								st.takeItems(MANAKIAS_ORDERS, -1);
								st.takeItems(BREKA_ORC_FANG, -1);
								st.giveItems(MANAKIAS_AMULET, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else
							{
								htmltext = "7515-02.htm";
							}
						}
						else
						{
							htmltext = "7515-01.htm";
							st.giveItems(MANAKIAS_ORDERS, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
						break;
					
					case TANTUS:
						if (st.hasItems(AXE_OF_CEREMONY))
						{
							htmltext = "7567-05.htm";
						}
						else if (st.hasItems(TANTUS_CHARM))
						{
							if (st.getItemsCount(BONE_ARROW) >= 1000)
							{
								htmltext = "7567-04.htm";
								st.takeItems(BONE_ARROW, 1000);
								st.takeItems(NERUGA_AXE_BLADE, 1);
								st.takeItems(TANTUS_CHARM, 1);
								st.giveItems(AXE_OF_CEREMONY, 1);
								
								if (st.hasItems(SWORD_INTO_SKULL, MONSTEREYE_WOODCARVING, HANDIWORK_SPIDER_BROOCH, ORDEAL_NECKLACE, HUGE_ORC_FANG))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
							else
							{
								htmltext = "7567-03.htm";
							}
						}
						else
						{
							htmltext = "7567-01.htm";
						}
						break;
					
					case JAKAL:
						if (st.hasItems(AXE_OF_CEREMONY))
						{
							htmltext = "7558-05.htm";
						}
						else if (st.hasItems(NERUGA_AXE_BLADE))
						{
							htmltext = "7558-04.htm";
						}
						else if (st.hasItems(TANTUS_CHARM))
						{
							if (st.getItemsCount(57) >= 1000)
							{
								htmltext = "7558-01.htm";
							}
							else
							{
								htmltext = "7558-03.htm";
							}
						}
						break;
					
					case HATOS:
						if (st.hasItems(SWORD_INTO_SKULL))
						{
							htmltext = "7568-05.htm";
						}
						else if (st.hasItems(HATOS_CHARM))
						{
							if (st.hasItems(URUTU_BLADE) && (st.getItemsCount(TIMAK_ORC_SKULL) >= 10))
							{
								htmltext = "7568-04.htm";
								st.takeItems(HATOS_CHARM, 1);
								st.takeItems(URUTU_BLADE, 1);
								st.takeItems(TIMAK_ORC_SKULL, -1);
								st.giveItems(SWORD_INTO_SKULL, 1);
								
								if (st.hasItems(AXE_OF_CEREMONY, MONSTEREYE_WOODCARVING, HANDIWORK_SPIDER_BROOCH, ORDEAL_NECKLACE, HUGE_ORC_FANG))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
							else
							{
								htmltext = "7568-03.htm";
							}
						}
						else
						{
							htmltext = "7568-01.htm";
						}
						break;
					
					case SUMARI:
						if (st.hasItems(URUTU_BLADE))
						{
							htmltext = "7564-03.htm";
						}
						else if (st.hasItems(SUMARIS_LETTER))
						{
							htmltext = "7564-02.htm";
						}
						else if (st.hasItems(HATOS_CHARM))
						{
							htmltext = "7564-01.htm";
							st.giveItems(SUMARIS_LETTER, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
						break;
					
					case SOMAK:
						if (st.hasItems(SWORD_INTO_SKULL))
						{
							htmltext = "7510-03.htm";
						}
						else if (st.hasItems(URUTU_BLADE))
						{
							htmltext = "7510-02.htm";
						}
						else if (st.hasItems(SUMARIS_LETTER))
						{
							htmltext = "7510-01.htm";
							st.takeItems(SUMARIS_LETTER, 1);
							st.giveItems(URUTU_BLADE, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
						break;
					
					case TAKUNA:
						if (st.hasItems(HANDIWORK_SPIDER_BROOCH))
						{
							htmltext = "7641-05.htm";
						}
						else if (st.hasItems(TAKUNA_CHARM))
						{
							if ((st.getItemsCount(MARSH_SPIDER_FEELER) >= 10) && (st.getItemsCount(MARSH_SPIDER_FEET) >= 10))
							{
								htmltext = "7641-04.htm";
								st.takeItems(MARSH_SPIDER_FEELER, -1);
								st.takeItems(MARSH_SPIDER_FEET, -1);
								st.takeItems(TAKUNA_CHARM, 1);
								st.giveItems(HANDIWORK_SPIDER_BROOCH, 1);
								
								if (st.hasItems(SWORD_INTO_SKULL, AXE_OF_CEREMONY, MONSTEREYE_WOODCARVING, ORDEAL_NECKLACE, HUGE_ORC_FANG))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
							else
							{
								htmltext = "7641-03.htm";
							}
						}
						else
						{
							htmltext = "7641-01.htm";
						}
						break;
					
					case CHIANTA:
						if (st.hasItems(MONSTEREYE_WOODCARVING))
						{
							htmltext = "7642-05.htm";
						}
						else if (st.hasItems(CHIANTA_CHARM))
						{
							if (st.getItemsCount(MONSTEREYE_CORNEA) >= 20)
							{
								htmltext = "7642-04.htm";
								st.takeItems(MONSTEREYE_CORNEA, -1);
								st.takeItems(CHIANTA_CHARM, 1);
								st.giveItems(MONSTEREYE_WOODCARVING, 1);
								
								if (st.hasItems(SWORD_INTO_SKULL, AXE_OF_CEREMONY, HANDIWORK_SPIDER_BROOCH, ORDEAL_NECKLACE, HUGE_ORC_FANG))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
							else
							{
								htmltext = "7642-03.htm";
							}
						}
						else
						{
							htmltext = "7642-01.htm";
						}
						break;
					
					case KAKAI:
						if (cond == 1)
						{
							htmltext = "7565-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7565-07.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7565-09.htm";
						}
						else if ((cond > 3) && (cond < 7))
						{
							htmltext = "7565-10.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7565-11.htm";
							
							st.takeItems(IMMORTAL_FLAME, 1);
							st.giveItems(MARK_LORD, 1);
							st.rewardExpAndSp(92955, 16250);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case ANCESTOR_MARTANKUS:
						if (cond == 3)
						{
							htmltext = "7649-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7649-05.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7649-06.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							
							st.takeItems(MARTANKUS_CHARM, 1);
							st.takeItems(RAGNA_ORC_HEAD, 1);
							st.takeItems(RAGNA_CHIEF_NOTICE, 1);
							st.giveItems(IMMORTAL_FLAME, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7649-07.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7649-08.htm";
						}
						break;
					
					case FIRST_ORC:
						if (cond == 6)
						{
							htmltext = "7643-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7643-03.htm";
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
		ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case ENCHANTED_MONSTEREYE:
				if (st.hasItems(CHIANTA_CHARM))
				{
					st.dropItemsAlways(MONSTEREYE_CORNEA, 1, 20);
				}
				break;
			
			case TIMAK_ORC:
			case TIMAK_ORC_ARCHER:
			case TIMAK_ORC_SOLDIER:
				if (st.hasItems(HATOS_CHARM))
				{
					st.dropItems(TIMAK_ORC_SKULL, 1, 10, 710000);
				}
				break;
			
			case TIMAK_ORC_WARRIOR:
				if (st.hasItems(HATOS_CHARM))
				{
					st.dropItems(TIMAK_ORC_SKULL, 1, 10, 810000);
				}
				break;
			
			case TIMAK_ORC_SHAMAN:
			case TIMAK_ORC_OVERLORD:
				if (st.hasItems(HATOS_CHARM))
				{
					st.dropItemsAlways(TIMAK_ORC_SKULL, 1, 10);
				}
				break;
			
			case MARSH_SPIDER:
				if (st.hasItems(TAKUNA_CHARM))
				{
					st.dropItemsAlways((st.getItemsCount(MARSH_SPIDER_FEELER) >= 10) ? MARSH_SPIDER_FEET : MARSH_SPIDER_FEELER, 1, 10);
				}
				break;
			
			case BREKA_ORC_SHAMAN:
				if (st.hasItems(MANAKIAS_ORDERS))
				{
					st.dropItems(BREKA_ORC_FANG, 1, 20, 410000);
				}
				break;
			
			case BREKA_ORC_OVERLORD:
				if (st.hasItems(MANAKIAS_ORDERS))
				{
					st.dropItems(BREKA_ORC_FANG, 1, 20, 510000);
				}
				break;
			
			case REGNA_ORC_OVERLORD:
			case REGNA_ORC_SEER:
				if (st.hasItems(MARTANKUS_CHARM))
				{
					if (!st.hasItems(RAGNA_CHIEF_NOTICE))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.giveItems(RAGNA_CHIEF_NOTICE, 1);
					}
					else if (!st.hasItems(RAGNA_ORC_HEAD))
					{
						st.set("cond", "5");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.giveItems(RAGNA_ORC_HEAD, 1);
					}
				}
				break;
		}
		
		return null;
	}
}
