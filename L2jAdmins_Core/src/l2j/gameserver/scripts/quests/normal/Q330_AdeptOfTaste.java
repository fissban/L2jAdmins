package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q330_AdeptOfTaste extends Script
{
	// NPCs
	private static final int SONIA = 7062;
	private static final int GLYVKA = 7067;
	private static final int ROLLANT = 7069;
	private static final int JACOB = 7073;
	private static final int PANO = 7078;
	private static final int MIRIEN = 7461;
	private static final int JONAS = 7469;
	
	// ITEMs
	private static final int INGREDIENT_LIST = 1420;
	private static final int SONIA_BOTANY_BOOK = 1421;
	private static final int RED_MANDRAGORA_ROOT = 1422;
	private static final int WHITE_MANDRAGORA_ROOT = 1423;
	private static final int RED_MANDRAGORA_SAP = 1424;
	private static final int WHITE_MANDRAGORA_SAP = 1425;
	private static final int JACOB_INSECT_BOOK = 1426;
	private static final int NECTAR = 1427;
	private static final int ROYAL_JELLY = 1428;
	private static final int HONEY = 1429;
	private static final int GOLDEN_HONEY = 1430;
	private static final int PANO_CONTRACT = 1431;
	private static final int HOBGOBLIN_AMULET = 1432;
	private static final int DIONIAN_POTATO = 1433;
	private static final int GLYVKA_BOTANY_BOOK = 1434;
	private static final int GREEN_MARSH_MOSS = 1435;
	private static final int BROWN_MARSH_MOSS = 1436;
	private static final int GREEN_MOSS_BUNDLE = 1437;
	private static final int BROWN_MOSS_BUNDLE = 1438;
	private static final int ROLANT_CREATURE_BOOK = 1439;
	private static final int MONSTER_EYE_BODY = 1440;
	private static final int MONSTER_EYE_MEAT = 1441;
	private static final int JONAS_STEAK_DISH_1 = 1442;
	private static final int JONAS_STEAK_DISH_2 = 1443;
	private static final int JONAS_STEAK_DISH_3 = 1444;
	private static final int JONAS_STEAK_DISH_4 = 1445;
	private static final int JONAS_STEAK_DISH_5 = 1446;
	private static final int MIRIEN_REVIEW_1 = 1447;
	private static final int MIRIEN_REVIEW_2 = 1448;
	private static final int MIRIEN_REVIEW_3 = 1449;
	private static final int MIRIEN_REVIEW_4 = 1450;
	private static final int MIRIEN_REVIEW_5 = 1451;
	
	// Rewards
	private static final int JONAS_SALAD_RECIPE = 1455;
	private static final int JONAS_SAUCE_RECIPE = 1456;
	private static final int JONAS_STEAK_RECIPE = 1457;
	
	// Drop chances
	private static final Map<Integer, int[]> CHANCES = new HashMap<>();
	
	{
		CHANCES.put(204, new int[]
		{
			92,
			100
		});
		CHANCES.put(229, new int[]
		{
			80,
			95
		});
		CHANCES.put(223, new int[]
		{
			70,
			77
		});
		CHANCES.put(154, new int[]
		{
			70,
			77
		});
		CHANCES.put(155, new int[]
		{
			87,
			96
		});
		CHANCES.put(156, new int[]
		{
			77,
			85
		});
	}
	
	public Q330_AdeptOfTaste()
	{
		super(330, "Adept of Taste");
		
		registerItems(INGREDIENT_LIST, RED_MANDRAGORA_SAP, WHITE_MANDRAGORA_SAP, HONEY, GOLDEN_HONEY, DIONIAN_POTATO, GREEN_MOSS_BUNDLE, BROWN_MOSS_BUNDLE, MONSTER_EYE_MEAT, MIRIEN_REVIEW_1, MIRIEN_REVIEW_2, MIRIEN_REVIEW_3, MIRIEN_REVIEW_4, MIRIEN_REVIEW_5, JONAS_STEAK_DISH_1, JONAS_STEAK_DISH_2, JONAS_STEAK_DISH_3, JONAS_STEAK_DISH_4, JONAS_STEAK_DISH_5, SONIA_BOTANY_BOOK, RED_MANDRAGORA_ROOT, WHITE_MANDRAGORA_ROOT, JACOB_INSECT_BOOK, NECTAR, ROYAL_JELLY, PANO_CONTRACT, HOBGOBLIN_AMULET, GLYVKA_BOTANY_BOOK, GREEN_MARSH_MOSS, BROWN_MARSH_MOSS, ROLANT_CREATURE_BOOK, MONSTER_EYE_BODY);
		
		addStartNpc(JONAS); // Jonas
		addTalkId(JONAS, SONIA, GLYVKA, ROLLANT, JACOB, PANO, MIRIEN);
		
		addKillId(147, 154, 155, 156, 204, 154, 226, 228, 229, 265, 266);
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
		
		if (event.equalsIgnoreCase("7469-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(INGREDIENT_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7062-05.htm"))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.takeItems(SONIA_BOTANY_BOOK, 1);
			st.takeItems(RED_MANDRAGORA_ROOT, -1);
			st.takeItems(WHITE_MANDRAGORA_ROOT, -1);
			st.giveItems(RED_MANDRAGORA_SAP, 1);
			
		}
		else if (event.equalsIgnoreCase("7073-05.htm"))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.takeItems(JACOB_INSECT_BOOK, 1);
			st.takeItems(NECTAR, -1);
			st.takeItems(ROYAL_JELLY, -1);
			st.giveItems(HONEY, 1);
		}
		else if (event.equalsIgnoreCase("7067-05.htm"))
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.takeItems(GLYVKA_BOTANY_BOOK, 1);
			st.takeItems(GREEN_MARSH_MOSS, -1);
			st.takeItems(BROWN_MARSH_MOSS, -1);
			st.giveItems(GREEN_MOSS_BUNDLE, 1);
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
				htmltext = player.getLevel() < 24 ? "7469-01.htm" : "7469-02.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case JONAS:
						if (st.hasItems(INGREDIENT_LIST))
						{
							if (!hasAllIngredients(st))
							{
								htmltext = "7469-04.htm";
							}
							else
							{
								int dish;
								
								final int specialIngredientsNumber = st.getItemsCount(WHITE_MANDRAGORA_SAP) + st.getItemsCount(GOLDEN_HONEY) + st.getItemsCount(BROWN_MOSS_BUNDLE);
								
								if (Rnd.nextBoolean())
								{
									htmltext = "7469-05t" + Integer.toString(specialIngredientsNumber + 2) + ".htm";
									dish = 1443 + specialIngredientsNumber;
								}
								else
								{
									htmltext = "7469-05t" + Integer.toString(specialIngredientsNumber + 1) + ".htm";
									dish = 1442 + specialIngredientsNumber;
								}
								
								// Sound according dish.
								st.playSound(dish == JONAS_STEAK_DISH_5 ? PlaySoundType.QUEST_JACKPOT : PlaySoundType.QUEST_ITEMGET);
								
								st.takeItems(INGREDIENT_LIST, 1);
								st.takeItems(RED_MANDRAGORA_SAP, 1);
								st.takeItems(WHITE_MANDRAGORA_SAP, 1);
								st.takeItems(HONEY, 1);
								st.takeItems(GOLDEN_HONEY, 1);
								st.takeItems(DIONIAN_POTATO, 1);
								st.takeItems(GREEN_MOSS_BUNDLE, 1);
								st.takeItems(BROWN_MOSS_BUNDLE, 1);
								st.takeItems(MONSTER_EYE_MEAT, 1);
								st.giveItems(dish, 1);
							}
						}
						else if (st.hasAtLeastOneItem(JONAS_STEAK_DISH_1, JONAS_STEAK_DISH_2, JONAS_STEAK_DISH_3, JONAS_STEAK_DISH_4, JONAS_STEAK_DISH_5))
						{
							htmltext = "7469-06.htm";
						}
						else if (st.hasAtLeastOneItem(MIRIEN_REVIEW_1, MIRIEN_REVIEW_2, MIRIEN_REVIEW_3, MIRIEN_REVIEW_4, MIRIEN_REVIEW_5))
						{
							if (st.hasItems(MIRIEN_REVIEW_1))
							{
								htmltext = "7469-06t1.htm";
								st.takeItems(MIRIEN_REVIEW_1, 1);
								st.rewardItems(Inventory.ADENA_ID, 7500);
								st.rewardExpAndSp(6000, 0);
							}
							else if (st.hasItems(MIRIEN_REVIEW_2))
							{
								htmltext = "7469-06t2.htm";
								st.takeItems(MIRIEN_REVIEW_2, 1);
								st.rewardItems(Inventory.ADENA_ID, 9000);
								st.rewardExpAndSp(7000, 0);
							}
							else if (st.hasItems(MIRIEN_REVIEW_3))
							{
								htmltext = "7469-06t3.htm";
								st.takeItems(MIRIEN_REVIEW_3, 1);
								st.rewardItems(Inventory.ADENA_ID, 5800);
								st.giveItems(JONAS_SALAD_RECIPE, 1);
								st.rewardExpAndSp(9000, 0);
							}
							else if (st.hasItems(MIRIEN_REVIEW_4))
							{
								htmltext = "7469-06t4.htm";
								st.takeItems(MIRIEN_REVIEW_4, 1);
								st.rewardItems(Inventory.ADENA_ID, 6800);
								st.giveItems(JONAS_SAUCE_RECIPE, 1);
								st.rewardExpAndSp(10500, 0);
							}
							else if (st.hasItems(MIRIEN_REVIEW_5))
							{
								htmltext = "7469-06t5.htm";
								st.takeItems(MIRIEN_REVIEW_5, 1);
								st.rewardItems(Inventory.ADENA_ID, 7800);
								st.giveItems(JONAS_STEAK_RECIPE, 1);
								st.rewardExpAndSp(12000, 0);
							}
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case MIRIEN:
						if (st.hasItems(INGREDIENT_LIST))
						{
							htmltext = "7461-01.htm";
						}
						else if (st.hasAtLeastOneItem(JONAS_STEAK_DISH_1, JONAS_STEAK_DISH_2, JONAS_STEAK_DISH_3, JONAS_STEAK_DISH_4, JONAS_STEAK_DISH_5))
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							if (st.hasItems(JONAS_STEAK_DISH_1))
							{
								htmltext = "7461-02t1.htm";
								st.takeItems(JONAS_STEAK_DISH_1, 1);
								st.giveItems(MIRIEN_REVIEW_1, 1);
							}
							else if (st.hasItems(JONAS_STEAK_DISH_2))
							{
								htmltext = "7461-02t2.htm";
								st.takeItems(JONAS_STEAK_DISH_2, 1);
								st.giveItems(MIRIEN_REVIEW_2, 1);
							}
							else if (st.hasItems(JONAS_STEAK_DISH_3))
							{
								htmltext = "7461-02t3.htm";
								st.takeItems(JONAS_STEAK_DISH_3, 1);
								st.giveItems(MIRIEN_REVIEW_3, 1);
							}
							else if (st.hasItems(JONAS_STEAK_DISH_4))
							{
								htmltext = "7461-02t4.htm";
								st.takeItems(JONAS_STEAK_DISH_4, 1);
								st.giveItems(MIRIEN_REVIEW_4, 1);
							}
							else if (st.hasItems(JONAS_STEAK_DISH_5))
							{
								htmltext = "7461-02t5.htm";
								st.takeItems(JONAS_STEAK_DISH_5, 1);
								st.giveItems(MIRIEN_REVIEW_5, 1);
							}
						}
						else if (st.hasAtLeastOneItem(MIRIEN_REVIEW_1, MIRIEN_REVIEW_2, MIRIEN_REVIEW_3, MIRIEN_REVIEW_4, MIRIEN_REVIEW_5))
						{
							htmltext = "7461-04.htm";
						}
						break;
					
					case SONIA:
						if (!st.hasItems(RED_MANDRAGORA_SAP) && !st.hasItems(WHITE_MANDRAGORA_SAP))
						{
							if (!st.hasItems(SONIA_BOTANY_BOOK))
							{
								htmltext = "7062-01.htm";
								st.giveItems(SONIA_BOTANY_BOOK, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else
							{
								if ((st.getItemsCount(RED_MANDRAGORA_ROOT) < 40) || (st.getItemsCount(WHITE_MANDRAGORA_ROOT) < 40))
								{
									htmltext = "7062-02.htm";
								}
								else if (st.getItemsCount(WHITE_MANDRAGORA_ROOT) >= 40)
								{
									htmltext = "7062-06.htm";
									st.takeItems(SONIA_BOTANY_BOOK, 1);
									st.takeItems(RED_MANDRAGORA_ROOT, -1);
									st.takeItems(WHITE_MANDRAGORA_ROOT, -1);
									st.giveItems(WHITE_MANDRAGORA_SAP, 1);
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
								else
								{
									htmltext = "7062-03.htm";
								}
							}
						}
						else
						{
							htmltext = "7062-07.htm";
						}
						break;
					
					case JACOB:
						if (!st.hasItems(HONEY) && !st.hasItems(GOLDEN_HONEY))
						{
							if (!st.hasItems(JACOB_INSECT_BOOK))
							{
								htmltext = "7073-01.htm";
								st.giveItems(JACOB_INSECT_BOOK, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else
							{
								if (st.getItemsCount(NECTAR) < 20)
								{
									htmltext = "7073-02.htm";
								}
								else
								{
									if (st.getItemsCount(ROYAL_JELLY) < 10)
									{
										htmltext = "7073-03.htm";
									}
									else
									{
										htmltext = "7073-06.htm";
										st.takeItems(JACOB_INSECT_BOOK, 1);
										st.takeItems(NECTAR, -1);
										st.takeItems(ROYAL_JELLY, -1);
										st.giveItems(GOLDEN_HONEY, 1);
										st.playSound(PlaySoundType.QUEST_ITEMGET);
									}
								}
							}
						}
						else
						{
							htmltext = "7073-07.htm";
						}
						break;
					
					case PANO:
						if (!st.hasItems(DIONIAN_POTATO))
						{
							if (!st.hasItems(PANO_CONTRACT))
							{
								htmltext = "7078-01.htm";
								st.giveItems(PANO_CONTRACT, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else
							{
								if (st.getItemsCount(HOBGOBLIN_AMULET) < 30)
								{
									htmltext = "7078-02.htm";
								}
								else
								{
									htmltext = "7078-03.htm";
									st.takeItems(PANO_CONTRACT, 1);
									st.takeItems(HOBGOBLIN_AMULET, -1);
									st.giveItems(DIONIAN_POTATO, 1);
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
						}
						else
						{
							htmltext = "7078-04.htm";
						}
						break;
					
					case GLYVKA:
						if (!st.hasItems(GREEN_MOSS_BUNDLE) && !st.hasItems(BROWN_MOSS_BUNDLE))
						{
							if (!st.hasItems(GLYVKA_BOTANY_BOOK))
							{
								st.giveItems(GLYVKA_BOTANY_BOOK, 1);
								htmltext = "7067-01.htm";
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else
							{
								if ((st.getItemsCount(GREEN_MARSH_MOSS) < 20) || (st.getItemsCount(BROWN_MARSH_MOSS) < 20))
								{
									htmltext = "7067-02.htm";
								}
								else if (st.getItemsCount(BROWN_MARSH_MOSS) >= 20)
								{
									htmltext = "7067-06.htm";
									st.takeItems(GLYVKA_BOTANY_BOOK, 1);
									st.takeItems(GREEN_MARSH_MOSS, -1);
									st.takeItems(BROWN_MARSH_MOSS, -1);
									st.giveItems(BROWN_MOSS_BUNDLE, 1);
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
								else
								{
									htmltext = "7067-03.htm";
								}
							}
						}
						else
						{
							htmltext = "7067-07.htm";
						}
						break;
					
					case ROLLANT:
						if (!st.hasItems(MONSTER_EYE_MEAT))
						{
							if (!st.hasItems(ROLANT_CREATURE_BOOK))
							{
								htmltext = "7069-01.htm";
								st.giveItems(ROLANT_CREATURE_BOOK, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else
							{
								if (st.getItemsCount(MONSTER_EYE_BODY) < 30)
								{
									htmltext = "7069-02.htm";
								}
								else
								{
									htmltext = "7069-03.htm";
									st.takeItems(ROLANT_CREATURE_BOOK, 1);
									st.takeItems(MONSTER_EYE_BODY, -1);
									st.giveItems(MONSTER_EYE_MEAT, 1);
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
						}
						else
						{
							htmltext = "7069-04.htm";
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
		
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case 265:
				if (st.hasItems(ROLANT_CREATURE_BOOK))
				{
					st.dropItems(MONSTER_EYE_BODY, Rnd.get(97) < 77 ? 2 : 3, 30, 970000);
				}
				break;
			
			case 266:
				if (st.hasItems(ROLANT_CREATURE_BOOK))
				{
					st.dropItemsAlways(MONSTER_EYE_BODY, Rnd.get(10) < 7 ? 1 : 2, 30);
				}
				break;
			
			case 226:
				if (st.hasItems(GLYVKA_BOTANY_BOOK))
				{
					st.dropItems(Rnd.get(96) < 87 ? GREEN_MARSH_MOSS : BROWN_MARSH_MOSS, 1, 20, 960000);
				}
				break;
			
			case 228:
				if (st.hasItems(GLYVKA_BOTANY_BOOK))
				{
					st.dropItemsAlways(Rnd.get(10) < 9 ? GREEN_MARSH_MOSS : BROWN_MARSH_MOSS, 1, 20);
				}
				break;
			
			case 147:
				if (st.hasItems(PANO_CONTRACT))
				{
					st.dropItemsAlways(HOBGOBLIN_AMULET, 1, 30);
				}
				break;
			
			case 204:
			case 229:
				if (st.hasItems(JACOB_INSECT_BOOK))
				{
					final int random = Rnd.get(100);
					final int[] chances = CHANCES.get(npcId);
					if (random < chances[0])
					{
						st.dropItemsAlways(NECTAR, 1, 20);
					}
					else if (random < chances[1])
					{
						st.dropItemsAlways(ROYAL_JELLY, 1, 10);
					}
				}
				break;
			
			case 223:
			case 154:
			case 155:
			case 156:
				if (st.hasItems(SONIA_BOTANY_BOOK))
				{
					final int random = Rnd.get(100);
					final int[] chances = CHANCES.get(npcId);
					if (random < chances[1])
					{
						st.dropItemsAlways(random < chances[0] ? RED_MANDRAGORA_ROOT : WHITE_MANDRAGORA_ROOT, 1, 40);
					}
				}
				break;
		}
		
		return null;
	}
	
	private static boolean hasAllIngredients(ScriptState st)
	{
		return st.hasItems(DIONIAN_POTATO, MONSTER_EYE_MEAT) && st.hasAtLeastOneItem(WHITE_MANDRAGORA_SAP, RED_MANDRAGORA_SAP) && st.hasAtLeastOneItem(GOLDEN_HONEY, HONEY) && st.hasAtLeastOneItem(BROWN_MOSS_BUNDLE, GREEN_MOSS_BUNDLE);
	}
	
}
