package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.util.Rnd;

/**
 * Test Of Witchcraft Based on remade by lewzer, repaired and implemented by zarie.
 */
public final class Q229_TestOfWitchcraft extends Script
{
	// NPCs
	private static final int ORIM = 7630;
	private static final int ALEXANDRIA = 7098;
	private static final int IKER = 7110;
	private static final int KAIRA = 7476;
	private static final int LARA = 7063;
	private static final int NESTLE = 7314;
	private static final int RODERIK = 7631;
	private static final int LEOPOLD = 7435;
	private static final int VASPER = 7417;
	private static final int VADIN = 7188;
	private static final int EVERT = 7633;
	private static final int ENDRIGO = 7632;
	
	// Rewards
	private static final int MARK_OF_WITCH_CRAFT = 3307;
	private static final int DIMENSION_DIAMONS = 7562;
	
	// Items
	private static final int SWORD_OF_BINDING = 3029;
	private static final int ORIMS_DIAGRAM = 3308;
	private static final int ALEXANDRIAS_BOOK = 3309;
	private static final int IKERS_LIST = 3310;
	private static final int DIRE_WYRM_FANG = 3311;
	private static final int LETO_LIZARDMAN_CHARM = 3312;
	private static final int ENCHANTED_STONE_GOLEM_HEARTSTONE = 3313;
	private static final int LARAS_MEMO = 3314;
	private static final int NESTLES_MEMO = 3315;
	private static final int LEOPOLDS_JOURNAL = 3316;
	private static final int AKLANTOTH_1ST_GEM = 3317;
	private static final int AKLANTOTH_2ND_GEM = 3318;
	private static final int AKLANTOTH_3RD_GEM = 3319;
	private static final int AKLANTOTH_4TH_GEM = 3320;
	private static final int AKLANTOTH_5TH_GEM = 3321;
	private static final int AKLANTOTH_6TH_GEM = 3322;
	private static final int BRIMSTONE_1ST = 3323;
	private static final int ORIMS_INSTRUCTIONS = 3324;
	private static final int ORIMS_1ST_LETTER = 3325;
	private static final int ORIMS_2ND_LETTER = 3326;
	private static final int SIR_VASPERS_LETTER = 3327;
	private static final int VADINS_CRUCIFIX = 3328;
	private static final int TAMLIN_ORC_AMULET = 3329;
	private static final int VADINS_SANCTIONS = 3330;
	private static final int IKERS_AMULET = 3331;
	private static final int SOULTRAP_CRYSTAL = 3332;
	private static final int PURGATORY_KEY = 3333;
	private static final int ZERUEL_BIND_CRYSTAL = 3334;
	private static final int BRIMSTONE_2ND = 3335;
	
	// Monsters
	private static final int DIRE_WYRM = 557;
	private static final int ENCHANTED_STONE_GOLEM = 565;
	private static final int LETO_LIZARDMAN = 577;
	private static final int LETO_LIZARDMAN_ARCHER = 578;
	private static final int LETO_LIZARDMAN_SOLDIER = 579;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int TAMLIN_ORC = 601;
	private static final int TAMLIN_ORC_ARCHER = 602;
	private static final int NAMELESS_REVENANT = 5099;
	private static final int SKELETAL_MERCENARY = 5100;
	private static final int DREVANUL_PRINCE_ZERUEL = 5101;
	
	public Q229_TestOfWitchcraft()
	{
		super(229, "Test Of Witchcraft");
		
		addStartNpc(ORIM);
		addTalkId(ORIM, LARA, ALEXANDRIA, IKER, VADIN, NESTLE, VASPER, LEOPOLD, KAIRA, RODERIK, ENDRIGO, EVERT);
		addKillId(DIRE_WYRM, ENCHANTED_STONE_GOLEM, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, NAMELESS_REVENANT, SKELETAL_MERCENARY, DREVANUL_PRINCE_ZERUEL);
		addAttackId(NAMELESS_REVENANT, SKELETAL_MERCENARY, DREVANUL_PRINCE_ZERUEL);
		registerItems(SWORD_OF_BINDING, ORIMS_DIAGRAM, ALEXANDRIAS_BOOK, IKERS_LIST, DIRE_WYRM_FANG, LETO_LIZARDMAN_CHARM, ENCHANTED_STONE_GOLEM_HEARTSTONE, LARAS_MEMO, NESTLES_MEMO, LEOPOLDS_JOURNAL, AKLANTOTH_1ST_GEM, AKLANTOTH_2ND_GEM, AKLANTOTH_3RD_GEM, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM, BRIMSTONE_1ST, ORIMS_INSTRUCTIONS, ORIMS_1ST_LETTER, ORIMS_2ND_LETTER, SIR_VASPERS_LETTER, VADINS_CRUCIFIX, TAMLIN_ORC_AMULET, VADINS_SANCTIONS, IKERS_AMULET, SOULTRAP_CRYSTAL, PURGATORY_KEY, ZERUEL_BIND_CRYSTAL, BRIMSTONE_2ND);
		
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7630-08.htm"))
		{
			st.startQuest();
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(ORIMS_DIAGRAM, 1);
			st.giveItems(DIMENSION_DIAMONS, 104);
			
		}
		else if (event.equalsIgnoreCase("7098-03.htm"))
		{
			st.giveItems(ALEXANDRIAS_BOOK, 1);
			st.takeItems(ORIMS_DIAGRAM, 1);
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7110-03.htm"))
		{
			st.giveItems(IKERS_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7476-02.htm"))
		{
			st.giveItems(AKLANTOTH_2ND_GEM, 1);
		}
		else if (event.equalsIgnoreCase("7063-02.htm"))
		{
			if (st.getItemsCount(LARAS_MEMO) < 1)
			{
				st.giveItems(LARAS_MEMO, 1);
			}
		}
		else if (event.equalsIgnoreCase("7314-02.htm"))
		{
			if (st.getItemsCount(NESTLES_MEMO) < 1)
			{
				st.giveItems(NESTLES_MEMO, 1);
			}
		}
		else if (event.equalsIgnoreCase("7435-02.htm"))
		{
			st.takeItems(NESTLES_MEMO, 1);
			st.giveItems(LEOPOLDS_JOURNAL, 1);
		}
		else if (event.equalsIgnoreCase("7630-14.htm"))
		{
			if (st.getItemsCount(BRIMSTONE_1ST) == 0)
			{
				st.takeItems(ALEXANDRIAS_BOOK, 1);
				st.takeItems(AKLANTOTH_1ST_GEM, 1);
				st.takeItems(AKLANTOTH_2ND_GEM, 1);
				st.takeItems(AKLANTOTH_3RD_GEM, 1);
				st.takeItems(AKLANTOTH_4TH_GEM, 1);
				st.takeItems(AKLANTOTH_5TH_GEM, 1);
				st.takeItems(AKLANTOTH_6TH_GEM, 1);
				st.giveItems(BRIMSTONE_1ST, 1);
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.set("id", "1");
				addSpawn(DREVANUL_PRINCE_ZERUEL, player, false, 0);
			}
		}
		else if (event.equalsIgnoreCase("7630-16.htm"))
		{
			htmltext = "7630-16.htm";
			st.takeItems(BRIMSTONE_1ST, 1);
			st.giveItems(ORIMS_INSTRUCTIONS, 1);
			st.giveItems(ORIMS_1ST_LETTER, 1);
			st.giveItems(ORIMS_2ND_LETTER, 1);
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7110-08.htm"))
		{
			if (st.getItemsCount(ORIMS_2ND_LETTER) > 0)
			{
				st.takeItems(ORIMS_2ND_LETTER, 1);
				st.giveItems(SOULTRAP_CRYSTAL, 1);
				st.giveItems(IKERS_AMULET, 1);
				if (st.getItemsCount(SWORD_OF_BINDING) > 0)
				{
					st.set("cond", "7");
				}
			}
		}
		else if (event.equalsIgnoreCase("7417-03.htm"))
		{
			st.takeItems(ORIMS_1ST_LETTER, 1);
			st.giveItems(SIR_VASPERS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7633-02.htm"))
		{
			
			st.set("id", "2");
			st.set("cond", "9");
			if (st.getItemsCount(BRIMSTONE_2ND) == 0)
			{
				st.giveItems(BRIMSTONE_2ND, 1);
			}
			addSpawn(DREVANUL_PRINCE_ZERUEL, player, false, 0);
		}
		else if (event.equalsIgnoreCase("7630-20.htm"))
		{
			st.takeItems(ZERUEL_BIND_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("7630-21.htm"))
		{
			st.takeItems(PURGATORY_KEY, 1);
		}
		else if (event.equalsIgnoreCase("7630-22.htm"))
		{
			st.takeItems(SWORD_OF_BINDING, 1);
			st.takeItems(IKERS_AMULET, 1);
			st.takeItems(ORIMS_INSTRUCTIONS, 1);
			st.giveItems(MARK_OF_WITCH_CRAFT, 1);
			st.rewardExpAndSp(1029122, 70620);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false, true);
		}
		
		return htmltext;
	}
	
	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
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
				if (npc.getId() == ORIM)
				{
					switch (st.getPlayer().getClassId())
					{
						case WIZARD:
						case KNIGHT:
						case PALUS_KNIGHT:
							st.startQuest();
							if (st.getPlayer().getLevel() < 39)
							{
								htmltext = "7630-02.htm";
								st.exitQuest(true);
							}
							else if (st.getPlayer().getClassId() == ClassId.WIZARD)
							{
								htmltext = "7630-03.htm";
							}
							else // knight & palusKnight
							{
								htmltext = "7630-05.htm";
							}
							break;
						
						default:
							htmltext = "7630-02.htm";
							st.exitQuest(true);
							break;
					}
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				
				switch (npc.getId())
				{
					case ORIM:
						switch (cond)
						{
							case 1:
								htmltext = "7630-09.htm";
								break;
							case 2:
								htmltext = "7630-10.htm";
								break;
							case 3:
								htmltext = "7630-11.htm";
								break;
							case 5:
								htmltext = "7630-15.htm";
								break;
							case 6:
								htmltext = "7630-17.htm";
								break;
							case 7:
								htmltext = "7630-18.htm";
								st.set("cond", "8");
								break;
							case 10:
								if (st.getItemsCount(ZERUEL_BIND_CRYSTAL) != 0)
								{
									htmltext = "7630-19.htm";
								}
								else if (st.getItemsCount(PURGATORY_KEY) != 0)
								{
									htmltext = "7630-20.htm";
								}
								else
								{
									htmltext = "7630-21.htm";
								}
								break;
						}
						break;
					
					case ALEXANDRIA:
						switch (cond)
						{
							case 1:
								htmltext = "7098-01.htm";
								break;
							case 2:
								htmltext = "7098-04.htm";
								break;
							default:
								htmltext = "7098-05.htm";
								break;
						}
						break;
					
					case IKER:
						if (cond == 2)
						{
							if ((st.getItemsCount(AKLANTOTH_1ST_GEM) == 0) && (st.getItemsCount(IKERS_LIST) == 0))
							{
								htmltext = "7110-01.htm";
							}
							else if ((st.getItemsCount(IKERS_LIST) > 0) && ((st.getItemsCount(DIRE_WYRM_FANG) < 20) || (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) || (st.getItemsCount(ENCHANTED_STONE_GOLEM_HEARTSTONE) < 20)))
							{
								htmltext = "7110-04.htm";
							}
							else if ((st.getItemsCount(AKLANTOTH_1ST_GEM) == 0) && (st.getItemsCount(IKERS_LIST) > 0))
							{
								st.takeItems(IKERS_LIST, 1);
								st.takeItems(DIRE_WYRM_FANG, 20);
								st.takeItems(LETO_LIZARDMAN_CHARM, 20);
								st.takeItems(ENCHANTED_STONE_GOLEM_HEARTSTONE, 20);
								st.giveItems(AKLANTOTH_1ST_GEM, 1);
								htmltext = "7110-05.htm";
							}
							else if (st.getItemsCount(AKLANTOTH_1ST_GEM) == 1)
							{
								htmltext = "7110-06.htm";
							}
						}
						else if (cond == 6)
						{
							htmltext = "7110-07.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7110-10.htm";
						}
						else
						{
							htmltext = "7110-09.htm";
						}
						break;
					
					case KAIRA:
						if (cond == 2)
						{
							if (st.getItemsCount(AKLANTOTH_2ND_GEM) == 0)
							{
								htmltext = "7476-01.htm";
							}
							else
							{
								htmltext = "7476-03.htm";
							}
						}
						else if (cond > 2)
						{
							htmltext = "7476-04.htm";
						}
						break;
					
					case LARA:
						if (cond == 2)
						{
							if ((st.getItemsCount(LARAS_MEMO) == 0) && (st.getItemsCount(AKLANTOTH_3RD_GEM) == 0))
							{
								htmltext = "7063-01.htm";
							}
							else if ((st.getItemsCount(LARAS_MEMO) == 1) && (st.getItemsCount(AKLANTOTH_3RD_GEM) == 0))
							{
								htmltext = "7063-03.htm";
							}
							else if (st.getItemsCount(AKLANTOTH_3RD_GEM) == 1)
							{
								htmltext = "7063-04.htm";
							}
						}
						else if (cond > 2)
						{
							htmltext = "7063-05.htm";
						}
						break;
					
					case RODERIK:
						if ((cond == 2) && (st.getItemsCount(LARAS_MEMO) > 0))
						{
							htmltext = "7631-01.htm";
						}
						break;
					
					case NESTLE:
						if (cond == 2)
						{
							if ((st.getItemsCount(AKLANTOTH_1ST_GEM) > 0) && (st.getItemsCount(AKLANTOTH_2ND_GEM) > 0) && (st.getItemsCount(AKLANTOTH_3RD_GEM) > 0))
							{
								htmltext = "7314-01.htm";
							}
							else
							{
								htmltext = "7314-04.htm";
							}
						}
						break;
					
					case LEOPOLD:
						if ((cond == 2) && (st.getItemsCount(NESTLES_MEMO) > 0))
						{
							if ((st.getItemsCount(AKLANTOTH_4TH_GEM) + st.getItemsCount(AKLANTOTH_5TH_GEM) + st.getItemsCount(AKLANTOTH_6TH_GEM)) == 0)
							{
								htmltext = "7435-01.htm";
							}
							else
							{
								htmltext = "7435-04.htm";
							}
						}
						else
						{
							htmltext = "7435-05.htm";
						}
						break;
					
					case VASPER:
						if (cond == 6)
						{
							if ((st.getItemsCount(SIR_VASPERS_LETTER) > 0) || (st.getItemsCount(VADINS_CRUCIFIX) > 0))
							{
								htmltext = "7417-04.htm";
							}
							else if (st.getItemsCount(VADINS_SANCTIONS) == 0)
							{
								htmltext = "7417-01.htm";
							}
							else if (st.getItemsCount(VADINS_SANCTIONS) != 0)
							{
								htmltext = "7417-05.htm";
								st.takeItems(VADINS_SANCTIONS, 1);
								st.giveItems(SWORD_OF_BINDING, 1);
								if (st.getItemsCount(SOULTRAP_CRYSTAL) > 0)
								{
									st.set("cond", "7");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
						}
						else if (cond == 7)
						{
							htmltext = "7417-06.htm";
						}
						break;
					
					case VADIN:
						if (cond == 6)
						{
							if (st.getItemsCount(SIR_VASPERS_LETTER) != 0)
							{
								htmltext = "7188-01.htm";
								st.takeItems(SIR_VASPERS_LETTER, 1);
								st.giveItems(VADINS_CRUCIFIX, 1);
							}
							else if ((st.getItemsCount(VADINS_CRUCIFIX) > 0) && (st.getItemsCount(TAMLIN_ORC_AMULET) < 20))
							{
								htmltext = "7188-02.htm";
							}
							else if (st.getItemsCount(TAMLIN_ORC_AMULET) >= 20)
							{
								htmltext = "7188-03.htm";
								st.takeItems(TAMLIN_ORC_AMULET, 20);
								st.takeItems(VADINS_CRUCIFIX, 1);
								st.giveItems(VADINS_SANCTIONS, 1);
							}
							else if (st.getItemsCount(VADINS_SANCTIONS) > 0)
							{
								htmltext = "7188-04.htm";
							}
						}
						else if (cond == 7)
						{
							htmltext = "7188-05.htm";
						}
						break;
					
					case EVERT:
						if ((st.getInt("id") == 2) || ((cond == 8) && (st.getItemsCount(BRIMSTONE_2ND) == 0)))
						{
							htmltext = "7633-01.htm";
						}
						else
						{
							htmltext = "7633-03.htm";
						}
						break;
					
					case ENDRIGO:
						if (cond == 2)
						{
							htmltext = "7632-01.htm";
						}
						break;
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		final int cond = st.getInt("cond");
		
		if ((npcId == DIRE_WYRM) && (cond == 2) && (st.getItemsCount(DIRE_WYRM_FANG) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(DIRE_WYRM_FANG, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == ENCHANTED_STONE_GOLEM) && (80 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(ENCHANTED_STONE_GOLEM_HEARTSTONE) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(ENCHANTED_STONE_GOLEM_HEARTSTONE, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == LETO_LIZARDMAN) && (50 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(LETO_LIZARDMAN_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == LETO_LIZARDMAN_ARCHER) && (50 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(LETO_LIZARDMAN_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == LETO_LIZARDMAN_SOLDIER) && (60 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(LETO_LIZARDMAN_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == LETO_LIZARDMAN_WARRIOR) && (65 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(LETO_LIZARDMAN_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == LETO_LIZARDMAN_SHAMAN) && (70 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(LETO_LIZARDMAN_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == LETO_LIZARDMAN_OVERLORD) && (70 >= Rnd.get(100)) && (cond == 2) && (st.getItemsCount(LETO_LIZARDMAN_CHARM) < 20) && (st.getItemsCount(IKERS_LIST) > 0))
		{
			st.giveItems(LETO_LIZARDMAN_CHARM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == NAMELESS_REVENANT) && (cond == 2) && (st.getItemsCount(AKLANTOTH_3RD_GEM) < 1) && (st.getItemsCount(LARAS_MEMO) > 0))
		{
			st.giveItems(AKLANTOTH_3RD_GEM, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == TAMLIN_ORC) && (50 >= Rnd.get(100)) && (cond == 6) && (st.getItemsCount(TAMLIN_ORC_AMULET) < 20) && (st.getItemsCount(VADINS_CRUCIFIX) > 0))
		{
			st.giveItems(TAMLIN_ORC_AMULET, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((npcId == TAMLIN_ORC_ARCHER) && (55 >= Rnd.get(100)) && (cond == 6) && (st.getItemsCount(TAMLIN_ORC_AMULET) < 20) && (st.getItemsCount(VADINS_CRUCIFIX) > 0))
		{
			st.giveItems(TAMLIN_ORC_AMULET, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if ((cond == 2) && (st.getItemsCount(LEOPOLDS_JOURNAL) > 0) && (npcId == SKELETAL_MERCENARY))
		{
			if ((st.getItemsCount(AKLANTOTH_4TH_GEM) == 0) && (50 >= Rnd.get(100)))
			{
				st.giveItems(AKLANTOTH_4TH_GEM, 1);
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
			if ((st.getItemsCount(AKLANTOTH_5TH_GEM) == 0) && (50 >= Rnd.get(100)))
			{
				st.giveItems(AKLANTOTH_5TH_GEM, 1);
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
			if ((st.getItemsCount(AKLANTOTH_6TH_GEM) == 0) && (50 >= Rnd.get(100)))
			{
				st.giveItems(AKLANTOTH_6TH_GEM, 1);
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
			if ((st.getItemsCount(AKLANTOTH_4TH_GEM) != 0) && (st.getItemsCount(AKLANTOTH_5TH_GEM) != 0) && (st.getItemsCount(AKLANTOTH_6TH_GEM) != 0))
			{
				st.takeItems(LEOPOLDS_JOURNAL, 1);
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if ((cond == 4) && (npcId == DREVANUL_PRINCE_ZERUEL))
		{
			st.set("cond", "5");
			st.unset("id");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if ((cond == 9) && (npcId == DREVANUL_PRINCE_ZERUEL))
		{
			if (st.getItemEquipped(ParpedollType.RHAND) == SWORD_OF_BINDING)
			{
				st.takeItems(BRIMSTONE_2ND, 1);
				st.takeItems(SOULTRAP_CRYSTAL, 1);
				st.giveItems(PURGATORY_KEY, 1);
				st.giveItems(ZERUEL_BIND_CRYSTAL, 1);
				st.unset("id");
				st.set("cond", "10");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				// "You have trapped the Soul of Drevanul Prince Zeruel";
			}
		}
		return null;
	}
}
