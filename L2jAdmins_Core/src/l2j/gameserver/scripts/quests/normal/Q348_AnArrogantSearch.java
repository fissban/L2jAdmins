package l2j.gameserver.scripts.quests.normal;

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
public class Q348_AnArrogantSearch extends Script
{
	// ITEMs
	private static final int TITAN_POWERSTONE = 4287;
	private static final int HANELLIN_FIRST_LETTER = 4288;
	private static final int HANELLIN_SECOND_LETTER = 4289;
	private static final int HANELLIN_THIRD_LETTER = 4290;
	private static final int FIRST_KEY_OF_ARK = 4291;
	private static final int SECOND_KEY_OF_ARK = 4292;
	private static final int THIRD_KEY_OF_ARK = 4293;
	private static final int BOOK_OF_SAINT = 4397;
	private static final int BLOOD_OF_SAINT = 4398;
	private static final int BOUGH_OF_SAINT = 4399;
	private static final int WHITE_FABRIC_TRIBE = 4294;
	private static final int WHITE_FABRIC_ANGELS = 5232;
	private static final int BLOODED_FABRIC = 4295;
	
	private static final int ANTIDOTE = 1831;
	private static final int HEALING_POTION = 1061;
	
	// NPCs
	private static final int HANELLIN = 7864;
	private static final int CLAUDIA_ATHEBALDT = 8001;
	private static final int MARTIEN = 7645;
	private static final int HARNE = 7144;
	private static final int ARK_GUARDIAN_CORPSE = 7980;
	private static final int HOLY_ARK_OF_SECRECY_1 = 7977;
	private static final int HOLY_ARK_OF_SECRECY_2 = 7978;
	private static final int HOLY_ARK_OF_SECRECY_3 = 7979;
	private static final int GUSTAV_ATHEBALDT = 7760;
	private static final int HARDIN = 7832;
	private static final int IASON_HEINE = 7969;
	
	// Monsters
	private static final int LESSER_GIANT_MAGE = 657;
	private static final int LESSER_GIANT_ELDER = 658;
	private static final int PLANTINUM_TRIBE_SHAMAN = 828;
	private static final int PLANTINUM_TRIBE_OVERLORD = 829;
	private static final int GUARDIAN_ANGEL = 830;
	private static final int SEAL_ANGEL = 831;
	
	// Quest Monsters
	private static final int ANGEL_KILLER = 5184;
	private static final int ARK_GUARDIAN_ELBEROTH = 5182;
	private static final int ARK_GUARDIAN_SHADOW_FANG = 5183;
	
	// NPCs instances, in order to avoid infinite instances creation speaking to chests.
	private L2Npc elberoth;
	private L2Npc shadowFang;
	private L2Npc angelKiller;
	
	public Q348_AnArrogantSearch()
	{
		super(348, "An Arrogant Search");
		
		registerItems(TITAN_POWERSTONE, HANELLIN_FIRST_LETTER, HANELLIN_SECOND_LETTER, HANELLIN_THIRD_LETTER, FIRST_KEY_OF_ARK, SECOND_KEY_OF_ARK, THIRD_KEY_OF_ARK, BOOK_OF_SAINT, BLOOD_OF_SAINT, BOUGH_OF_SAINT, WHITE_FABRIC_TRIBE, WHITE_FABRIC_ANGELS);
		
		addStartNpc(HANELLIN);
		addTalkId(HANELLIN, CLAUDIA_ATHEBALDT, MARTIEN, HARNE, HOLY_ARK_OF_SECRECY_1, HOLY_ARK_OF_SECRECY_2, HOLY_ARK_OF_SECRECY_3, ARK_GUARDIAN_CORPSE, GUSTAV_ATHEBALDT, HARDIN, IASON_HEINE);
		
		addSpawnId(ARK_GUARDIAN_ELBEROTH, ARK_GUARDIAN_SHADOW_FANG, ANGEL_KILLER);
		addAttackId(ARK_GUARDIAN_ELBEROTH, ARK_GUARDIAN_SHADOW_FANG, ANGEL_KILLER, PLANTINUM_TRIBE_SHAMAN, PLANTINUM_TRIBE_OVERLORD);
		
		addKillId(LESSER_GIANT_MAGE, LESSER_GIANT_ELDER, ARK_GUARDIAN_ELBEROTH, ARK_GUARDIAN_SHADOW_FANG, ANGEL_KILLER, PLANTINUM_TRIBE_SHAMAN, PLANTINUM_TRIBE_OVERLORD, GUARDIAN_ANGEL, SEAL_ANGEL);
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
		
		if (event.equalsIgnoreCase("7864-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7864-09.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(TITAN_POWERSTONE, 1);
		}
		else if (event.equalsIgnoreCase("7864-17.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(HANELLIN_FIRST_LETTER, 1);
			st.giveItems(HANELLIN_SECOND_LETTER, 1);
			st.giveItems(HANELLIN_THIRD_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7864-36.htm"))
		{
			st.set("cond", "24");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.rewardItems(Inventory.ADENA_ID, Rnd.get(1, 2) * 12000);
		}
		else if (event.equalsIgnoreCase("7864-37.htm"))
		{
			st.set("cond", "25");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7864-51.htm"))
		{
			st.set("cond", "26");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(WHITE_FABRIC_ANGELS, st.hasItems(BLOODED_FABRIC) ? 9 : 10);
		}
		else if (event.equalsIgnoreCase("7864-58.htm"))
		{
			st.set("cond", "27");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7864-57.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7864-56.htm"))
		{
			st.set("cond", "29");
			st.set("gustav", "0"); // st.unset doesn't work.
			st.set("hardin", "0");
			st.set("iason", "0");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(WHITE_FABRIC_ANGELS, 10);
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
				if (st.hasItems(BLOODED_FABRIC))
				{
					htmltext = "7864-00.htm";
				}
				else if (player.getLevel() < 60)
				{
					htmltext = "7864-01.htm";
				}
				else
				{
					htmltext = "7864-02.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case HANELLIN:
						if (cond == 1)
						{
							htmltext = "7864-02.htm";
						}
						else if (cond == 2)
						{
							htmltext = !st.hasItems(TITAN_POWERSTONE) ? "7864-06.htm" : "7864-07.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7864-09.htm";
						}
						else if ((cond > 4) && (cond < 21))
						{
							htmltext = player.getInventory().hasAtLeastOneItem(BOOK_OF_SAINT, BLOOD_OF_SAINT, BOUGH_OF_SAINT) ? "7864-28.htm" : "7864-24.htm";
						}
						else if (cond == 21)
						{
							htmltext = "7864-29.htm";
							st.set("cond", "22");
							st.takeItems(BOOK_OF_SAINT, 1);
							st.takeItems(BLOOD_OF_SAINT, 1);
							st.takeItems(BOUGH_OF_SAINT, 1);
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 22)
						{
							if (st.hasItems(WHITE_FABRIC_TRIBE))
							{
								htmltext = "7864-31.htm";
							}
							else if ((st.getItemsCount(ANTIDOTE) < 5) || !st.hasItems(HEALING_POTION))
							{
								htmltext = "7864-30.htm";
							}
							else
							{
								htmltext = "7864-31.htm";
								st.takeItems(ANTIDOTE, 5);
								st.takeItems(HEALING_POTION, 1);
								st.giveItems(WHITE_FABRIC_TRIBE, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else if (cond == 24)
						{
							htmltext = "7864-38.htm";
						}
						else if (cond == 25)
						{
							if (st.hasItems(WHITE_FABRIC_TRIBE))
							{
								htmltext = "7864-39.htm";
							}
							else if (st.hasItems(BLOODED_FABRIC))
							{
								htmltext = "7864-49.htm";
								// Use the only fabric on Baium, drop the quest.
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_FINISH);
								st.exitQuest(true);
							}
						}
						else if (cond == 26)
						{
							final int count = st.getItemsCount(BLOODED_FABRIC);
							
							if ((count + st.getItemsCount(WHITE_FABRIC_ANGELS)) < 10)
							{
								htmltext = "7864-54.htm";
								st.takeItems(BLOODED_FABRIC, -1);
								st.rewardItems(Inventory.ADENA_ID, (1000 * count) + 4000);
								st.exitQuest(true);
							}
							else if (count < 10)
							{
								htmltext = "7864-52.htm";
							}
							else if (count >= 10)
							{
								htmltext = "7864-53.htm";
							}
						}
						else if (cond == 27)
						{
							if ((st.getInt("gustav") + st.getInt("hardin") + st.getInt("iason")) == 3)
							{
								htmltext = "7864-60.htm";
								st.set("cond", "28");
								st.rewardItems(Inventory.ADENA_ID, 49000);
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else if (st.hasItems(BLOODED_FABRIC) && (st.getInt("usedonbaium") != 1))
							{
								htmltext = "7864-59.htm";
							}
							else
							{
								htmltext = "7864-61.htm";
								st.playSound(PlaySoundType.QUEST_FINISH);
								st.exitQuest(true);
							}
						}
						else if (cond == 28)
						{
							htmltext = "7864-55.htm";
						}
						else if (cond == 29)
						{
							final int count = st.getItemsCount(BLOODED_FABRIC);
							
							if ((count + st.getItemsCount(WHITE_FABRIC_ANGELS)) < 10)
							{
								htmltext = "7864-54.htm";
								st.takeItems(BLOODED_FABRIC, -1);
								st.rewardItems(Inventory.ADENA_ID, 5000 * count);
								st.playSound(PlaySoundType.QUEST_FINISH);
								st.exitQuest(true);
							}
							else if (count < 10)
							{
								htmltext = "7864-52.htm";
							}
							else if (count >= 10)
							{
								htmltext = "7864-53.htm";
							}
						}
						break;
					
					case GUSTAV_ATHEBALDT:
						if (cond == 27)
						{
							if ((st.getItemsCount(BLOODED_FABRIC) >= 3) && (st.getInt("gustav") == 0))
							{
								st.set("gustav", "1");
								htmltext = "7760-01.htm";
								st.takeItems(BLOODED_FABRIC, 3);
							}
							else if (st.getInt("gustav") == 1)
							{
								htmltext = "7760-02.htm";
							}
							else
							{
								htmltext = "7760-03.htm";
								st.set("usedonbaium", "1");
							}
						}
						break;
					
					case HARDIN:
						if (cond == 27)
						{
							if (st.hasItems(BLOODED_FABRIC) && (st.getInt("hardin") == 0))
							{
								st.set("hardin", "1");
								htmltext = "7832-01.htm";
								st.takeItems(BLOODED_FABRIC, 1);
							}
							else if (st.getInt("hardin") == 1)
							{
								htmltext = "7832-02.htm";
							}
							else
							{
								htmltext = "7832-03.htm";
								st.set("usedonbaium", "1");
							}
						}
						break;
					
					case IASON_HEINE:
						if (cond == 27)
						{
							if ((st.getItemsCount(BLOODED_FABRIC) >= 6) && (st.getInt("iason") == 0))
							{
								st.set("iason", "1");
								htmltext = "7969-01.htm";
								st.takeItems(BLOODED_FABRIC, 6);
							}
							else if (st.getInt("iason") == 1)
							{
								htmltext = "7969-02.htm";
							}
							else
							{
								htmltext = "7969-03.htm";
								st.set("usedonbaium", "1");
							}
						}
						break;
					
					case HARNE:
						if ((cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(BLOOD_OF_SAINT))
							{
								if (st.hasItems(HANELLIN_FIRST_LETTER))
								{
									htmltext = "7144-01.htm";
									st.set("cond", "17");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(HANELLIN_FIRST_LETTER, 1);
									st.addRadar(-418, 44174, -3568);
								}
								else if (!st.hasItems(FIRST_KEY_OF_ARK))
								{
									htmltext = "7144-03.htm";
									st.addRadar(-418, 44174, -3568);
								}
								else
								{
									htmltext = "7144-04.htm";
								}
							}
							else
							{
								htmltext = "7144-05.htm";
							}
						}
						break;
					
					case CLAUDIA_ATHEBALDT:
						if ((cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(BOOK_OF_SAINT))
							{
								if (st.hasItems(HANELLIN_SECOND_LETTER))
								{
									htmltext = "8001-01.htm";
									st.set("cond", "9");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(HANELLIN_SECOND_LETTER, 1);
									st.addRadar(181472, 7158, -2725);
								}
								else if (!st.hasItems(SECOND_KEY_OF_ARK))
								{
									htmltext = "8001-03.htm";
									st.addRadar(181472, 7158, -2725);
								}
								else
								{
									htmltext = "8001-04.htm";
								}
							}
							else
							{
								htmltext = "8001-05.htm";
							}
						}
						break;
					
					case MARTIEN:
						if ((cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(BOUGH_OF_SAINT))
							{
								if (st.hasItems(HANELLIN_THIRD_LETTER))
								{
									htmltext = "7645-01.htm";
									st.set("cond", "13");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(HANELLIN_THIRD_LETTER, 1);
									st.addRadar(50693, 158674, 376);
								}
								else if (!st.hasItems(THIRD_KEY_OF_ARK))
								{
									htmltext = "7645-03.htm";
									st.addRadar(50693, 158674, 376);
								}
								else
								{
									htmltext = "7645-04.htm";
								}
							}
							else
							{
								htmltext = "7645-05.htm";
							}
						}
						break;
					
					case ARK_GUARDIAN_CORPSE:
						if (!st.hasItems(HANELLIN_FIRST_LETTER) && (cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(FIRST_KEY_OF_ARK) && !st.hasItems(BLOOD_OF_SAINT))
							{
								if (st.getInt("angelkiller") == 0)
								{
									htmltext = "7980-01.htm";
									if (angelKiller == null)
									{
										angelKiller = addSpawn(ANGEL_KILLER, npc, false, 0);
									}
									
									if (st.getInt("cond") != 18)
									{
										st.set("cond", "18");
										st.playSound(PlaySoundType.QUEST_MIDDLE);
									}
								}
								else
								{
									htmltext = "7980-02.htm";
									st.giveItems(FIRST_KEY_OF_ARK, 1);
									st.playSound(PlaySoundType.QUEST_ITEMGET);
									
									st.unset("angelkiller");
								}
							}
							else
							{
								htmltext = "7980-03.htm";
							}
						}
						break;
					
					case HOLY_ARK_OF_SECRECY_1:
						if (!st.hasItems(HANELLIN_FIRST_LETTER) && (cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(BLOOD_OF_SAINT))
							{
								if (st.hasItems(FIRST_KEY_OF_ARK))
								{
									htmltext = "7977-02.htm";
									st.set("cond", "20");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									
									st.takeItems(FIRST_KEY_OF_ARK, 1);
									st.giveItems(BLOOD_OF_SAINT, 1);
									
									if (st.hasItems(BOOK_OF_SAINT, BOUGH_OF_SAINT))
									{
										st.set("cond", "21");
									}
								}
								else
								{
									htmltext = "7977-04.htm";
								}
							}
							else
							{
								htmltext = "7977-03.htm";
							}
						}
						break;
					
					case HOLY_ARK_OF_SECRECY_2:
						if (!st.hasItems(HANELLIN_SECOND_LETTER) && (cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(BOOK_OF_SAINT))
							{
								if (!st.hasItems(SECOND_KEY_OF_ARK))
								{
									htmltext = "7978-01.htm";
									if (elberoth == null)
									{
										elberoth = addSpawn(ARK_GUARDIAN_ELBEROTH, npc, false, 0);
									}
								}
								else
								{
									htmltext = "7978-02.htm";
									st.set("cond", "12");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									
									st.takeItems(SECOND_KEY_OF_ARK, 1);
									st.giveItems(BOOK_OF_SAINT, 1);
									
									if (st.hasItems(BLOOD_OF_SAINT, BOUGH_OF_SAINT))
									{
										st.set("cond", "21");
									}
								}
							}
							else
							{
								htmltext = "7978-03.htm";
							}
						}
						break;
					
					case HOLY_ARK_OF_SECRECY_3:
						if (!st.hasItems(HANELLIN_THIRD_LETTER) && (cond >= 5) && (cond <= 22))
						{
							if (!st.hasItems(BOUGH_OF_SAINT))
							{
								if (!st.hasItems(THIRD_KEY_OF_ARK))
								{
									htmltext = "7979-01.htm";
									if (shadowFang == null)
									{
										shadowFang = addSpawn(ARK_GUARDIAN_SHADOW_FANG, npc, false, 0);
									}
								}
								else
								{
									htmltext = "7979-02.htm";
									st.set("cond", "16");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									
									st.takeItems(THIRD_KEY_OF_ARK, 1);
									st.giveItems(BOUGH_OF_SAINT, 1);
									
									if (st.hasItems(BLOOD_OF_SAINT, BOOK_OF_SAINT))
									{
										st.set("cond", "21");
									}
								}
							}
							else
							{
								htmltext = "7979-03.htm";
							}
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getId())
		{
			case ARK_GUARDIAN_ELBEROTH:
				npc.broadcastNpcSay("This does not belong to you. Take your hands out!");
				break;
			
			case ARK_GUARDIAN_SHADOW_FANG:
				npc.broadcastNpcSay("I don't believe it! Grrr!");
				break;
			
			case ANGEL_KILLER:
				npc.broadcastNpcSay("I have the key, do you wish to steal it?");
				break;
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		final ScriptState st = checkPlayerState(attacker, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case ARK_GUARDIAN_ELBEROTH:
				if (npc.getScriptValue() == 0)
				{
					npc.broadcastNpcSay("...I feel very sorry, but I have taken your life.");
					npc.setScriptValue(1);
				}
				break;
			
			case ARK_GUARDIAN_SHADOW_FANG:
				if (npc.getScriptValue() == 0)
				{
					npc.broadcastNpcSay("I will cover this mountain with your blood!");
					npc.setScriptValue(1);
				}
				break;
			
			case ANGEL_KILLER:
				if (npc.getScriptValue() == 0)
				{
					npc.broadcastNpcSay("Haha.. Really amusing! As for the key, search the corpse!");
					npc.setScriptValue(1);
				}
				
				if ((npc.getCurrentHp() / npc.getStat().getMaxHp()) < 0.50)
				{
					npc.abortAttack();
					npc.broadcastNpcSay("Can't get rid of you... Did you get the key from the corpse?");
					npc.decayMe();
					
					st.set("cond", "19");
					st.set("angelkiller", "1");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					
					angelKiller = null;
				}
				break;
			
			case PLANTINUM_TRIBE_OVERLORD:
			case PLANTINUM_TRIBE_SHAMAN:
				final int cond = st.getInt("cond");
				if (((cond == 24) || (cond == 25)) && (Rnd.get(500) < 1) && st.hasItems(WHITE_FABRIC_TRIBE))
				{
					st.takeItems(WHITE_FABRIC_TRIBE, 1);
					st.giveItems(BLOODED_FABRIC, 1);
					
					if (cond != 24)
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_FINISH);
						st.exitQuest(true);
					}
				}
				break;
		}
		
		return null;
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
		
		switch (npc.getId())
		{
			case LESSER_GIANT_ELDER:
			case LESSER_GIANT_MAGE:
				if (cond == 2)
				{
					st.dropItems(TITAN_POWERSTONE, 1, 1, 100000);
				}
				break;
			
			case ARK_GUARDIAN_ELBEROTH:
				if ((cond >= 5) && (cond <= 22) && !st.hasItems(SECOND_KEY_OF_ARK))
				{
					st.set("cond", "11");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(SECOND_KEY_OF_ARK, 1);
					npc.broadcastNpcSay("Oh, dull-witted.. God, they...");
				}
				elberoth = null;
				break;
			
			case ARK_GUARDIAN_SHADOW_FANG:
				if ((cond >= 5) && (cond <= 22) && !st.hasItems(THIRD_KEY_OF_ARK))
				{
					st.set("cond", "15");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(THIRD_KEY_OF_ARK, 1);
					npc.broadcastNpcSay("You do not know.. Seven seals are.. coughs");
				}
				shadowFang = null;
				break;
			
			case SEAL_ANGEL:
			case GUARDIAN_ANGEL:
				if (((cond == 26) || (cond == 29)) && (Rnd.get(4) < 1) && st.hasItems(WHITE_FABRIC_ANGELS))
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(WHITE_FABRIC_ANGELS, 1);
					st.giveItems(BLOODED_FABRIC, 1);
				}
				break;
			
			case ANGEL_KILLER:
				angelKiller = null;
				break;
		}
		
		return null;
	}
	
}
