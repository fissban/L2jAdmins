package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class Q420_LittleWing extends Script
{
	// Needed items
	private static final int COAL = 1870;
	private static final int CHARCOAL = 1871;
	private static final int SILVER_NUGGET = 1873;
	private static final int STONE_OF_PURITY = 1875;
	private static final int GEMSTONE_D = 2130;
	private static final int GEMSTONE_C = 2131;
	
	// Items
	private static final int FAIRY_DUST = 3499;
	
	private static final int FAIRY_STONE = 3816;
	private static final int DELUXE_FAIRY_STONE = 3817;
	private static final int FAIRY_STONE_LIST = 3818;
	private static final int DELUXE_FAIRY_STONE_LIST = 3819;
	private static final int TOAD_LORD_BACK_SKIN = 3820;
	private static final int JUICE_OF_MONKSHOOD = 3821;
	private static final int SCALE_OF_DRAKE_EXARION = 3822;
	private static final int EGG_OF_DRAKE_EXARION = 3823;
	private static final int SCALE_OF_DRAKE_ZWOV = 3824;
	private static final int EGG_OF_DRAKE_ZWOV = 3825;
	private static final int SCALE_OF_DRAKE_KALIBRAN = 3826;
	private static final int EGG_OF_DRAKE_KALIBRAN = 3827;
	private static final int SCALE_OF_WYVERN_SUZET = 3828;
	private static final int EGG_OF_WYVERN_SUZET = 3829;
	private static final int SCALE_OF_WYVERN_SHAMHAI = 3830;
	private static final int EGG_OF_WYVERN_SHAMHAI = 3831;
	
	// Rewards
	private static final int DRAGONFLUTE_OF_WIND = 3500;
	private static final int DRAGONFLUTE_OF_STAR = 3501;
	private static final int DRAGONFLUTE_OF_TWILIGHT = 3502;
	private static final int HATCHLING_SOFT_LEATHER = 3912;
	private static final int FOOD_FOR_HATCHLING = 4038;
	
	// NPCs
	private static final int MARIA = 7608;
	private static final int CRONOS = 7610;
	private static final int BYRON = 7711;
	private static final int MYMYU = 7747;
	private static final int EXARION = 7748;
	private static final int ZWOV = 7749;
	private static final int KALIBRAN = 7750;
	private static final int SUZET = 7751;
	private static final int SHAMHAI = 7752;
	private static final int COOPER = 7829;
	
	public Q420_LittleWing()
	{
		super(420, "Little Wing");
		
		registerItems(FAIRY_STONE, DELUXE_FAIRY_STONE, FAIRY_STONE_LIST, DELUXE_FAIRY_STONE_LIST, TOAD_LORD_BACK_SKIN, JUICE_OF_MONKSHOOD, SCALE_OF_DRAKE_EXARION, EGG_OF_DRAKE_EXARION, SCALE_OF_DRAKE_ZWOV, EGG_OF_DRAKE_ZWOV, SCALE_OF_DRAKE_KALIBRAN, EGG_OF_DRAKE_KALIBRAN, SCALE_OF_WYVERN_SUZET, EGG_OF_WYVERN_SUZET, SCALE_OF_WYVERN_SHAMHAI, EGG_OF_WYVERN_SHAMHAI);
		
		addStartNpc(COOPER);
		addTalkId(MARIA, CRONOS, BYRON, MYMYU, EXARION, ZWOV, KALIBRAN, SUZET, SHAMHAI, COOPER);
		
		addKillId(202, 231, 233, 270, 551, 580, 589, 590, 591, 592, 593, 594, 595, 596, 597, 598, 599);
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
		
		// COOPER
		if (event.equalsIgnoreCase("7829-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		// CRONOS
		else if (event.equalsIgnoreCase("7610-05.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(FAIRY_STONE_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7610-06.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(DELUXE_FAIRY_STONE_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7610-12.htm"))
		{
			st.set("cond", "2");
			st.set("deluxestone", "1");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(FAIRY_STONE_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7610-13.htm"))
		{
			st.set("cond", "2");
			st.set("deluxestone", "1");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(DELUXE_FAIRY_STONE_LIST, 1);
		}
		// MARIA
		else if (event.equalsIgnoreCase("7608-03.htm"))
		{
			if (!checkItems(st, false))
			{
				htmltext = "7608-01.htm"; // Avoid to continue while trade or drop mats before clicking bypass
			}
			else
			{
				st.takeItems(COAL, 10);
				st.takeItems(CHARCOAL, 10);
				st.takeItems(GEMSTONE_D, 1);
				st.takeItems(SILVER_NUGGET, 3);
				st.takeItems(TOAD_LORD_BACK_SKIN, -1);
				st.takeItems(FAIRY_STONE_LIST, 1);
				st.giveItems(FAIRY_STONE, 1);
			}
		}
		else if (event.equalsIgnoreCase("7608-05.htm"))
		{
			if (!checkItems(st, true))
			{
				htmltext = "7608-01.htm"; // Avoid to continue while trade or drop mats before clicking bypass
			}
			else
			{
				st.takeItems(COAL, 10);
				st.takeItems(CHARCOAL, 10);
				st.takeItems(GEMSTONE_C, 1);
				st.takeItems(STONE_OF_PURITY, 1);
				st.takeItems(SILVER_NUGGET, 5);
				st.takeItems(TOAD_LORD_BACK_SKIN, -1);
				st.takeItems(DELUXE_FAIRY_STONE_LIST, 1);
				st.giveItems(DELUXE_FAIRY_STONE, 1);
			}
		}
		// BYRON
		else if (event.equalsIgnoreCase("7711-03.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			if (st.hasItems(DELUXE_FAIRY_STONE))
			{
				htmltext = "7711-04.htm";
			}
		}
		// MIMYU
		else if (event.equalsIgnoreCase("7747-02.htm"))
		{
			st.set("mimyu", "1");
			st.takeItems(FAIRY_STONE, 1);
		}
		else if (event.equalsIgnoreCase("7747-04.htm"))
		{
			st.set("mimyu", "1");
			st.takeItems(DELUXE_FAIRY_STONE, 1);
			st.giveItems(FAIRY_DUST, 1);
		}
		else if (event.equalsIgnoreCase("7747-07.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(JUICE_OF_MONKSHOOD, 1);
		}
		else if (event.equalsIgnoreCase("7747-12.htm") && !st.hasItems(FAIRY_DUST))
		{
			htmltext = "7747-15.htm";
			giveRandomPet(st, false);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7747-13.htm"))
		{
			giveRandomPet(st, st.hasItems(FAIRY_DUST));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7747-14.htm"))
		{
			if (st.hasItems(FAIRY_DUST))
			{
				st.takeItems(FAIRY_DUST, 1);
				giveRandomPet(st, true);
				if (Rnd.get(20) == 1)
				{
					st.giveItems(HATCHLING_SOFT_LEATHER, 1);
				}
				else
				{
					htmltext = "7747-14t.htm";
					st.giveItems(FOOD_FOR_HATCHLING, 20);
				}
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = "7747-13.htm";
			}
		}
		// EXARION
		else if (event.equalsIgnoreCase("7748-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(JUICE_OF_MONKSHOOD, 1);
			st.giveItems(SCALE_OF_DRAKE_EXARION, 1);
		}
		// ZWOV
		else if (event.equalsIgnoreCase("7749-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(JUICE_OF_MONKSHOOD, 1);
			st.giveItems(SCALE_OF_DRAKE_ZWOV, 1);
		}
		// KALIBRAN
		else if (event.equalsIgnoreCase("7750-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(JUICE_OF_MONKSHOOD, 1);
			st.giveItems(SCALE_OF_DRAKE_KALIBRAN, 1);
		}
		else if (event.equalsIgnoreCase("7750-05.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(EGG_OF_DRAKE_KALIBRAN, 19);
			st.takeItems(SCALE_OF_DRAKE_KALIBRAN, 1);
		}
		// SUZET
		else if (event.equalsIgnoreCase("7751-03.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(JUICE_OF_MONKSHOOD, 1);
			st.giveItems(SCALE_OF_WYVERN_SUZET, 1);
		}
		// SHAMHAI
		else if (event.equalsIgnoreCase("7752-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(JUICE_OF_MONKSHOOD, 1);
			st.giveItems(SCALE_OF_WYVERN_SHAMHAI, 1);
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
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() >= 35) ? "7829-01.htm" : "7829-03.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case COOPER:
						htmltext = "7829-04.htm";
						break;
					
					case CRONOS:
						if (cond == 1)
						{
							htmltext = "7610-01.htm";
						}
						else if (st.getInt("deluxestone") == 2)
						{
							htmltext = "7610-10.htm";
						}
						else if (cond == 2)
						{
							if (st.hasAtLeastOneItem(FAIRY_STONE, DELUXE_FAIRY_STONE))
							{
								if (st.getInt("deluxestone") == 1)
								{
									htmltext = "7610-14.htm";
								}
								else
								{
									htmltext = "7610-08.htm";
									st.set("cond", "3");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else
							{
								htmltext = "7610-07.htm";
							}
						}
						else if (cond == 3)
						{
							htmltext = "7610-09.htm";
						}
						else if ((cond == 4) && st.hasAtLeastOneItem(FAIRY_STONE, DELUXE_FAIRY_STONE))
						{
							htmltext = "7610-11.htm";
						}
						break;
					
					case MARIA:
						if (st.hasAtLeastOneItem(FAIRY_STONE, DELUXE_FAIRY_STONE))
						{
							htmltext = "7608-06.htm";
						}
						else if (cond == 2)
						{
							if (st.hasItems(FAIRY_STONE_LIST))
							{
								htmltext = (checkItems(st, false)) ? "7608-02.htm" : "7608-01.htm";
							}
							else if (st.hasItems(DELUXE_FAIRY_STONE_LIST))
							{
								htmltext = (checkItems(st, true)) ? "7608-04.htm" : "7608-01.htm";
							}
						}
						break;
					
					case BYRON:
						final int deluxestone = st.getInt("deluxestone");
						if (deluxestone == 1)
						{
							if (st.hasItems(FAIRY_STONE))
							{
								htmltext = "7711-05.htm";
								st.set("cond", "4");
								st.unset("deluxestone");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else if (st.hasItems(DELUXE_FAIRY_STONE))
							{
								htmltext = "7711-06.htm";
								st.set("cond", "4");
								st.unset("deluxestone");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								htmltext = "7711-10.htm";
							}
						}
						else if (deluxestone == 2)
						{
							htmltext = "7711-09.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7711-01.htm";
						}
						else if (cond == 4)
						{
							if (st.hasItems(FAIRY_STONE))
							{
								htmltext = "7711-07.htm";
							}
							else if (st.hasItems(DELUXE_FAIRY_STONE))
							{
								htmltext = "7711-08.htm";
							}
						}
						break;
					
					case MYMYU:
						if (cond == 4)
						{
							if (st.getInt("mimyu") == 1)
							{
								htmltext = "7747-06.htm";
							}
							else if (st.hasItems(FAIRY_STONE))
							{
								htmltext = "7747-01.htm";
							}
							else if (st.hasItems(DELUXE_FAIRY_STONE))
							{
								htmltext = "7747-03.htm";
							}
						}
						else if (cond == 5)
						{
							htmltext = "7747-08.htm";
						}
						else if (cond == 6)
						{
							final int eggs = st.getItemsCount(EGG_OF_DRAKE_EXARION) + st.getItemsCount(EGG_OF_DRAKE_ZWOV) + st.getItemsCount(EGG_OF_DRAKE_KALIBRAN) + st.getItemsCount(EGG_OF_WYVERN_SUZET) + st.getItemsCount(EGG_OF_WYVERN_SHAMHAI);
							if (eggs < 20)
							{
								htmltext = "7747-09.htm";
							}
							else
							{
								htmltext = "7747-10.htm";
							}
						}
						else if (cond == 7)
						{
							htmltext = "7747-11.htm";
						}
						break;
					
					case EXARION:
						if (cond == 5)
						{
							htmltext = "7748-01.htm";
						}
						else if (cond == 6)
						{
							if (st.getItemsCount(EGG_OF_DRAKE_EXARION) < 20)
							{
								htmltext = "7748-03.htm";
							}
							else
							{
								htmltext = "7748-04.htm";
								st.set("cond", "7");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(EGG_OF_DRAKE_EXARION, 19);
								st.takeItems(SCALE_OF_DRAKE_EXARION, 1);
							}
						}
						else if (cond == 7)
						{
							htmltext = "7748-05.htm";
						}
						break;
					
					case ZWOV:
						if (cond == 5)
						{
							htmltext = "7749-01.htm";
						}
						else if (cond == 6)
						{
							if (st.getItemsCount(EGG_OF_DRAKE_ZWOV) < 20)
							{
								htmltext = "7749-03.htm";
							}
							else
							{
								htmltext = "7749-04.htm";
								st.set("cond", "7");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(EGG_OF_DRAKE_ZWOV, 19);
								st.takeItems(SCALE_OF_DRAKE_ZWOV, 1);
							}
						}
						else if (cond == 7)
						{
							htmltext = "7749-05.htm";
						}
						break;
					
					case KALIBRAN:
						if (cond == 5)
						{
							htmltext = "7750-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = (st.getItemsCount(EGG_OF_DRAKE_KALIBRAN) < 20) ? "7750-03.htm" : "7750-04.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7750-06.htm";
						}
						break;
					
					case SUZET:
						if (cond == 5)
						{
							htmltext = "7751-01.htm";
						}
						else if (cond == 6)
						{
							if (st.getItemsCount(EGG_OF_WYVERN_SUZET) < 20)
							{
								htmltext = "7751-04.htm";
							}
							else
							{
								htmltext = "7751-05.htm";
								st.set("cond", "7");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(EGG_OF_WYVERN_SUZET, 19);
								st.takeItems(SCALE_OF_WYVERN_SUZET, 1);
							}
						}
						else if (cond == 7)
						{
							htmltext = "7751-06.htm";
						}
						break;
					
					case SHAMHAI:
						if (cond == 5)
						{
							htmltext = "7752-01.htm";
						}
						else if (cond == 6)
						{
							if (st.getItemsCount(EGG_OF_WYVERN_SHAMHAI) < 20)
							{
								htmltext = "7752-03.htm";
							}
							else
							{
								htmltext = "7752-04.htm";
								st.set("cond", "7");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(EGG_OF_WYVERN_SHAMHAI, 19);
								st.takeItems(SCALE_OF_WYVERN_SHAMHAI, 1);
							}
						}
						else if (cond == 7)
						{
							htmltext = "7752-05.htm";
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
		ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case 231:
				if (st.hasItems(FAIRY_STONE_LIST))
				{
					st.dropItems(TOAD_LORD_BACK_SKIN, 1, 10, 300000);
				}
				else if (st.hasItems(DELUXE_FAIRY_STONE_LIST))
				{
					st.dropItems(TOAD_LORD_BACK_SKIN, 1, 20, 300000);
				}
				break;
			
			case 580:
				if (st.hasItems(SCALE_OF_DRAKE_EXARION) && !st.dropItems(EGG_OF_DRAKE_EXARION, 1, 20, 500000))
				{
					npc.broadcastNpcSay("If the eggs get taken, we're dead!");
				}
				break;
			
			case 233:
				if (st.hasItems(SCALE_OF_DRAKE_ZWOV))
				{
					st.dropItems(EGG_OF_DRAKE_ZWOV, 1, 20, 500000);
				}
				break;
			
			case 551:
				if (st.hasItems(SCALE_OF_DRAKE_KALIBRAN) && !st.dropItems(EGG_OF_DRAKE_KALIBRAN, 1, 20, 500000))
				{
					npc.broadcastNpcSay("Hey! Everybody watch the eggs!");
				}
				break;
			
			case 270:
				if (st.hasItems(SCALE_OF_WYVERN_SUZET) && !st.dropItems(EGG_OF_WYVERN_SUZET, 1, 20, 500000))
				{
					npc.broadcastNpcSay("I thought I'd caught one share... Whew!");
				}
				break;
			
			case 202:
				if (st.hasItems(SCALE_OF_WYVERN_SHAMHAI))
				{
					st.dropItems(EGG_OF_WYVERN_SHAMHAI, 1, 20, 500000);
				}
				break;
			
			case 589:
			case 590:
			case 591:
			case 592:
			case 593:
			case 594:
			case 595:
			case 596:
			case 597:
			case 598:
			case 599:
				if (st.hasItems(DELUXE_FAIRY_STONE) && (Rnd.get(100) < 30))
				{
					st.set("deluxestone", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(DELUXE_FAIRY_STONE, 1);
					npc.broadcastNpcSay("The stone... the Elven stone... broke...");
				}
				break;
		}
		return null;
	}
	
	private static boolean checkItems(ScriptState st, boolean isDeluxe)
	{
		// Conditions required for both cases.
		if ((st.getItemsCount(COAL) < 10) || (st.getItemsCount(CHARCOAL) < 10))
		{
			return false;
		}
		
		if (isDeluxe)
		{
			if ((st.getItemsCount(GEMSTONE_C) >= 1) && (st.getItemsCount(SILVER_NUGGET) >= 5) && (st.getItemsCount(STONE_OF_PURITY) >= 1) && (st.getItemsCount(TOAD_LORD_BACK_SKIN) >= 20))
			{
				return true;
			}
		}
		else
		{
			if ((st.getItemsCount(GEMSTONE_D) >= 1) && (st.getItemsCount(SILVER_NUGGET) >= 3) && (st.getItemsCount(TOAD_LORD_BACK_SKIN) >= 10))
			{
				return true;
			}
		}
		return false;
	}
	
	private static void giveRandomPet(ScriptState st, boolean hasFairyDust)
	{
		int pet = DRAGONFLUTE_OF_TWILIGHT;
		int chance = Rnd.get(100);
		if (st.hasItems(EGG_OF_DRAKE_EXARION))
		{
			st.takeItems(EGG_OF_DRAKE_EXARION, 1);
			if (hasFairyDust)
			{
				if (chance < 45)
				{
					pet = DRAGONFLUTE_OF_WIND;
				}
				else if (chance < 75)
				{
					pet = DRAGONFLUTE_OF_STAR;
				}
			}
			else if (chance < 50)
			{
				pet = DRAGONFLUTE_OF_WIND;
			}
			else if (chance < 85)
			{
				pet = DRAGONFLUTE_OF_STAR;
			}
		}
		else if (st.hasItems(EGG_OF_WYVERN_SUZET))
		{
			st.takeItems(EGG_OF_WYVERN_SUZET, 1);
			if (hasFairyDust)
			{
				if (chance < 55)
				{
					pet = DRAGONFLUTE_OF_WIND;
				}
				else if (chance < 85)
				{
					pet = DRAGONFLUTE_OF_STAR;
				}
			}
			else if (chance < 65)
			{
				pet = DRAGONFLUTE_OF_WIND;
			}
			else if (chance < 95)
			{
				pet = DRAGONFLUTE_OF_STAR;
			}
		}
		else if (st.hasItems(EGG_OF_DRAKE_KALIBRAN))
		{
			st.takeItems(EGG_OF_DRAKE_KALIBRAN, 1);
			if (hasFairyDust)
			{
				if (chance < 60)
				{
					pet = DRAGONFLUTE_OF_WIND;
				}
				else if (chance < 90)
				{
					pet = DRAGONFLUTE_OF_STAR;
				}
			}
			else if (chance < 70)
			{
				pet = DRAGONFLUTE_OF_WIND;
			}
			else
			{
				pet = DRAGONFLUTE_OF_STAR;
			}
		}
		else if (st.hasItems(EGG_OF_WYVERN_SHAMHAI))
		{
			st.takeItems(EGG_OF_WYVERN_SHAMHAI, 1);
			if (hasFairyDust)
			{
				if (chance < 70)
				{
					pet = DRAGONFLUTE_OF_WIND;
				}
				else
				{
					pet = DRAGONFLUTE_OF_STAR;
				}
			}
			else if (chance < 85)
			{
				pet = DRAGONFLUTE_OF_WIND;
			}
			else
			{
				pet = DRAGONFLUTE_OF_STAR;
			}
		}
		else if (st.hasItems(EGG_OF_DRAKE_ZWOV))
		{
			st.takeItems(EGG_OF_DRAKE_ZWOV, 1);
			if (hasFairyDust)
			{
				if (chance < 90)
				{
					pet = DRAGONFLUTE_OF_WIND;
				}
				else
				{
					pet = DRAGONFLUTE_OF_STAR;
				}
			}
			else
			{
				pet = DRAGONFLUTE_OF_WIND;
			}
		}
		
		st.giveItems(pet, 1);
	}
	
}
