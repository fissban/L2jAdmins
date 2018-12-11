/*
 * Copyright (C) 2014-2020 L2jAdjmins
 * This file is part of L2jAdmins.
 * L2jAdmins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2jAdmins is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * Original code in python
 * @author fissban, zarie
 */
public class Q334_TheWishingPotion extends Script
{
	// General Rewards
	private static final int NECKLACE_OF_GRACE = 931;
	private static final int HEART_OF_PAAGRIO = 3943;
	//@formatter:off
	private static final int[] REWARD_1 =
	{
		3081, 3076, 3075, 3074, 4917, 3077, 3080, 3079, 3078, 4928, 4931, 4932, 5013, 3067, 3064, 3061, 3062, 3058, 4206, 3065, 3060, 3063, 4208, 3057, 3059, 3066, 4911, 4918, 3092, 3039, 4922, 3091, 3093, 3431
	};
	private static final int[] REWARD_2 =
	{
		3430, 3429, 3073, 3941, 3071, 3069, 3072, 4200, 3068, 3070, 4912, 3100, 3101, 3098, 3094, 3102, 4913, 3095, 3096, 3097, 3099, 3085, 3086, 3082, 4907, 3088, 4207, 3087, 3084, 3083, 4929, 4933, 4919, 3045
	};
	private static final int[] REWARD_3 =
	{
		4923, 4201, 4914, 3942, 3090, 4909, 3089, 4930, 4934, 4920, 3041, 4924, 3114, 3105, 3110, 3104, 3113, 3103, 4204, 3108, 4926, 3112, 3107, 4205, 3109, 3111, 3106, 4925, 3117, 3115, 3118, 3116, 4927
	};
	private static final int[] REWARD_4 =
	{
		1979, 1980, 2952, 2953
	};
	//@formatter:on
	// Quest ingredients and rewards
	private static final int WISH_POTION = 3467;
	private static final int ANCIENT_CROWN = 3468;
	private static final int CERTIFICATE_OF_ROYALTY = 3469;
	private static final int ALCHEMY_TEXT = 3678;
	private static final int SECRET_BOOK = 3679;
	private static final int POTION_RECIPE_1 = 3680;
	private static final int POTION_RECIPE_2 = 3681;
	private static final int MATILDS_ORB = 3682;
	private static final int FORBIDDEN_LOVE_SCROLL = 3683;
	
	private static final int AMBER_SCALE = 3684;
	private static final int WIND_SOULSTONE = 3685;
	private static final int GLASS_EYE = 3686;
	private static final int HORROR_ECTOPLASM = 3687;
	private static final int SILENOS_HORN = 3688;
	private static final int ANT_SOLDIER_APHID = 3689;
	private static final int TYRANTS_CHITIN = 3690;
	private static final int BUGBEAR_BLOOD = 3691;
	// NPCs
	private static final int GRIMA = 5135;
	private static final int SUCCUBUS_OF_SEDUCTION = 5136;
	private static final int GREAT_DEMON_KING = 5138;
	private static final int SECRET_KEEPER_TREE = 5139;
	private static final int SANCHES = 5153;
	private static final int BONAPARTERIUS = 5154;
	private static final int RAMSEBALIUS = 5155;
	private static final int TORAI = 7557;
	private static final int ALCHEMIST_MATILD = 7738;
	private static final int RUPINA = 7742;
	private static final int WISDOM_CHEST = 7743;
	// MOBs
	private static final int WHISPERING_WIND = 78;
	private static final int ANT_SOLDIER = 87;
	private static final int ANT_WARRIOR_CAPTAIN = 88;
	private static final int SILENOS = 168;
	private static final int TYRANT = 192;
	private static final int TYRANT_KINGPIN = 193;
	private static final int AMBER_BASILISK = 199;
	private static final int HORROR_MIST_RIPPER = 227;
	private static final int TURAK_BUGBEAR = 248;
	private static final int TURAK_BUGBEAR_WARRIOR = 249;
	private static final int GLASS_JAGUAR = 250;
	// DROPLIST
	
	private static final Map<Integer, DropListChance> DROPLISTS = new HashMap<>();
	
	{
		DROPLISTS.put(AMBER_BASILISK, new DropListChance(AMBER_SCALE, 15));
		DROPLISTS.put(WHISPERING_WIND, new DropListChance(WIND_SOULSTONE, 20));
		DROPLISTS.put(GLASS_JAGUAR, new DropListChance(GLASS_EYE, 35));
		DROPLISTS.put(HORROR_MIST_RIPPER, new DropListChance(HORROR_ECTOPLASM, 15));
		DROPLISTS.put(SILENOS, new DropListChance(SILENOS_HORN, 30));
		DROPLISTS.put(ANT_SOLDIER, new DropListChance(ANT_SOLDIER_APHID, 40));
		DROPLISTS.put(ANT_WARRIOR_CAPTAIN, new DropListChance(ANT_SOLDIER_APHID, 40));
		DROPLISTS.put(TYRANT, new DropListChance(TYRANTS_CHITIN, 50));
		DROPLISTS.put(TYRANT_KINGPIN, new DropListChance(TYRANTS_CHITIN, 50));
		DROPLISTS.put(TURAK_BUGBEAR, new DropListChance(BUGBEAR_BLOOD, 25));
		DROPLISTS.put(TURAK_BUGBEAR_WARRIOR, new DropListChance(BUGBEAR_BLOOD, 25));
	}
	
	// set of random messages
	private static final String[] MESSAGES_SUCCUBUS_OF_SEDUCTION =
	{
		"Do you wanna be loved?",
		"Do you need love?",
		"Let me love you...",
		"Want to know what love is?",
		"Are you in need of love?",
		"Me love you long time"
	};
	private static final String[] MESSAGES_GRIMA =
	{
		"hey hum hum!",
		"boom! boom!",
		"...",
		"Ki ab kya karein hum"
	};
	
	private class DropListChance
	{
		public int itemId;
		public int chance;
		
		public DropListChance(int itemId, int chance)
		{
			this.itemId = itemId;
			this.chance = chance;
		}
	}
	
	public Q334_TheWishingPotion()
	{
		super(334, "The Wishing Potion");
		
		addStartNpc(ALCHEMIST_MATILD);
		addTalkId(ALCHEMIST_MATILD, TORAI, RUPINA, WISDOM_CHEST);
		addKillId(SECRET_KEEPER_TREE, SUCCUBUS_OF_SEDUCTION, GRIMA, SANCHES, RAMSEBALIUS, BONAPARTERIUS, GREAT_DEMON_KING);
		addKillId(AMBER_BASILISK, WHISPERING_WIND, GLASS_JAGUAR, HORROR_MIST_RIPPER, SILENOS);
		addKillId(ANT_SOLDIER, ANT_WARRIOR_CAPTAIN, TYRANT, TYRANT_KINGPIN, TURAK_BUGBEAR, TURAK_BUGBEAR_WARRIOR);
	}
	
	private static boolean checkIngredients(ScriptState st)
	{
		if (!st.hasItems(AMBER_SCALE))
		{
			return false;
		}
		if (!st.hasItems(WIND_SOULSTONE))
		{
			return false;
		}
		if (!st.hasItems(GLASS_EYE))
		{
			return false;
		}
		if (!st.hasItems(HORROR_ECTOPLASM))
		{
			return false;
		}
		if (!st.hasItems(SILENOS_HORN))
		{
			return false;
		}
		if (!st.hasItems(ANT_SOLDIER_APHID))
		{
			return false;
		}
		if (!st.hasItems(TYRANTS_CHITIN))
		{
			return false;
		}
		if (!st.hasItems(BUGBEAR_BLOOD))
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		player = st.getPlayer();
		
		if (event.equalsIgnoreCase("7738-02.htm"))
		{
			return "7738-02.htm";
		}
		if (event.equalsIgnoreCase("7738-03.htm"))
		{
			st.set("cond", "1");
			st.setState(ScriptStateType.STARTED);
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			if (st.getItemsCount(ALCHEMY_TEXT) >= 2)
			{
				st.takeItems(ALCHEMY_TEXT, -1);
			}
			if (st.getItemsCount(ALCHEMY_TEXT) == 0)
			{
				st.giveItems(ALCHEMY_TEXT, 1);
			}
			return "7738-03.htm";
		}
		if (event.equalsIgnoreCase("7738-06.htm"))
		{
			if (st.getItemsCount(WISH_POTION) > 0)
			{
				return "7738-13.htm";
			}
			st.playSound("ItemSound.quest_accept");
			st.set("cond", "3");
			
			if (st.hasItems(ALCHEMY_TEXT))
			{
				st.takeItems(ALCHEMY_TEXT);
			}
			if (st.hasItems(SECRET_BOOK))
			{
				st.takeItems(SECRET_BOOK);
			}
			if (st.getItemsCount(POTION_RECIPE_1) >= 2)
			{
				st.takeItems(POTION_RECIPE_1);
			}
			if (!st.hasItems(POTION_RECIPE_1))
			{
				st.giveItems(POTION_RECIPE_1, 1);
			}
			if (st.getItemsCount(POTION_RECIPE_2) >= 2)
			{
				st.takeItems(POTION_RECIPE_2);
			}
			if (!st.hasItems(POTION_RECIPE_2))
			{
				st.giveItems(POTION_RECIPE_2, 1);
			}
			if (st.hasItems(MATILDS_ORB))
			{
				return "7738-12.htm";
			}
		}
		if (event.equalsIgnoreCase("7738-09.htm"))
		{
			if (checkIngredients(st))
			{
				return "7738-09.htm";
			}
			return "You don't have required items";
		}
		if (event.equalsIgnoreCase("7738-10.htm"))
		{
			if (checkIngredients(st))
			{
				st.playSound(PlaySoundType.QUEST_FINISH);
				
				st.takeItems(ALCHEMY_TEXT, SECRET_BOOK, POTION_RECIPE_1, POTION_RECIPE_2, AMBER_SCALE, TYRANTS_CHITIN);
				st.takeItems(WIND_SOULSTONE, GLASS_EYE, HORROR_ECTOPLASM, SILENOS_HORN, ANT_SOLDIER_APHID, BUGBEAR_BLOOD);
				
				if (st.getItemsCount(MATILDS_ORB) == 0)
				{
					st.giveItems(MATILDS_ORB, 1);
				}
				
				st.giveItems(WISH_POTION, 1);
				st.set("cond", "5");
			}
			else
			{
				return "You don't have required items";
			}
		}
		else if (event.equalsIgnoreCase("7738-14.htm"))
		{
			// if you dropped or destroyed your wish potion, you are not able to see the wish list;
			if (st.getItemsCount(WISH_POTION) > 0)
			{
				return "7738-15.htm";
			}
		}
		else if (event.equalsIgnoreCase("7738-16.htm"))
		{
			if (st.getItemsCount(WISH_POTION) > 0)
			{
				st.set("wish", "1");
				startTimer("matild_timer1", 3000, npc, player, false);
				st.takeItems(WISH_POTION, 1);
				npc.setBusy(true);
				return "7738-16.htm";
			}
			return "7738-14.htm";
		}
		else if (event.equalsIgnoreCase("7738-17.htm"))
		{
			if (st.getItemsCount(WISH_POTION) > 0)
			{
				st.set("wish", "2");
				startTimer("matild_timer1", 3000, npc, player, false);
				st.takeItems(WISH_POTION, 1);
				npc.setBusy(true);
				return "7738-17.htm";
			}
			return "7738-14.htm";
		}
		else if (event.equalsIgnoreCase("7738-18.htm"))
		{
			if (st.getItemsCount(WISH_POTION) > 0)
			{
				st.set("wish", "3");
				startTimer("matild_timer1", 3000, npc, player, false);
				st.takeItems(WISH_POTION, 1);
				npc.setBusy(true);
				return "7738-18.htm";
			}
			return "7738-14.htm";
		}
		else if (event.equalsIgnoreCase("7738-19.htm"))
		{
			if (st.getItemsCount(WISH_POTION) >= 1)
			{
				st.set("wish", "4");
				startTimer("matild_timer1", 3000, npc, player, false);
				st.takeItems(WISH_POTION, 1);
				npc.setBusy(true);
				return "7738-19.htm";
			}
			return "7738-14.htm";
		}
		else if (event == "matild_timer1")
		{
			npc.broadcastNpcSay("OK, everybody pray fervently!");
			startTimer("matild_timer2", 4000, npc, player, false);
		}
		else if (event == "matild_timer2")
		{
			npc.broadcastNpcSay("Both hands to heaven, everybody yell together!");
			startTimer("matild_timer3", 4000, npc, player, false);
		}
		else if (event == "matild_timer3")
		{
			npc.broadcastNpcSay("One! Two! May your dreams come true!");
			int wish = st.getInt("wish");
			int chance = getRandom(100);
			if (wish == 1)
			{
				if (chance <= 50)
				{
					L2Npc spawnedNpc;
					
					spawnedNpc = addSpawn(SUCCUBUS_OF_SEDUCTION, player, true, 200000);
					spawnedNpc.broadcastNpcSay(MESSAGES_SUCCUBUS_OF_SEDUCTION[getRandom(MESSAGES_SUCCUBUS_OF_SEDUCTION.length)]);
					spawnedNpc = addSpawn(SUCCUBUS_OF_SEDUCTION, player, true, 200000);
					spawnedNpc.broadcastNpcSay(MESSAGES_SUCCUBUS_OF_SEDUCTION[getRandom(MESSAGES_SUCCUBUS_OF_SEDUCTION.length)]);
					spawnedNpc = addSpawn(SUCCUBUS_OF_SEDUCTION, player, true, 200000);
					spawnedNpc.broadcastNpcSay(MESSAGES_SUCCUBUS_OF_SEDUCTION[getRandom(MESSAGES_SUCCUBUS_OF_SEDUCTION.length)]);
				}
				else
				{
					L2Npc spawnedNpc = addSpawn(RUPINA, player, false, 120000);
					spawnedNpc.broadcastNpcSay("Your love... love!");
				}
			}
			else if (wish == 2)
			{
				if (chance <= 33)
				{
					L2Npc spawnedNpc;
					spawnedNpc = addSpawn(GRIMA, player, true, 200000);
					spawnedNpc.broadcastNpcSay(MESSAGES_GRIMA[getRandom(MESSAGES_GRIMA.length)]);
					spawnedNpc = addSpawn(GRIMA, player, true, 200000);
					spawnedNpc.broadcastNpcSay(MESSAGES_GRIMA[getRandom(MESSAGES_GRIMA.length)]);
					spawnedNpc = addSpawn(GRIMA, player, true, 200000);
					spawnedNpc.broadcastNpcSay(MESSAGES_GRIMA[getRandom(MESSAGES_GRIMA.length)]);
					
				}
				else
				{
					st.giveItems(Inventory.ADENA_ID, 10000);
				}
			}
			else if (wish == 3)
			{
				if (chance <= 33)
				{
					st.giveItems(CERTIFICATE_OF_ROYALTY, 1);
				}
				else if (chance >= 66)
				{
					st.giveItems(ANCIENT_CROWN, 1);
				}
				else
				{
					L2Npc spawnedNpc = addSpawn(SANCHES, player, true, 0);
					spawnedNpc.broadcastNpcSay("Who dares to call the dark Monarch?!");
					startTimer("sanches_timer1", 200000, spawnedNpc, player, false);
				}
			}
			else if (wish == 4)
			{
				if (chance <= 33)
				{
					st.giveItems(REWARD_1[getRandom(REWARD_1.length)], 1);
					st.giveItems(REWARD_2[getRandom(REWARD_2.length)], 1);
					st.giveItems(REWARD_3[getRandom(REWARD_3.length)], 1);
					if (getRandom(3) == 0)
					{
						st.giveItems(HEART_OF_PAAGRIO, 1);
					}
				}
				else
				{
					L2Npc spawnedNpc = addSpawn(WISDOM_CHEST, player, true, 120000);
					spawnedNpc.broadcastNpcSay("I contain the wisdom, I am the wisdom box!");
				}
			}
			npc.setBusy(false);
		}
		else if (event == "sanches_timer1")
		{
			npc.broadcastNpcSay("Hehehe, i'm just wasting my time here!");
			npc.deleteMe();
		}
		else if (event == "bonaparterius_timer1")
		{
			npc.broadcastNpcSay("A worth opponent would be a good thing");
			npc.deleteMe();
		}
		else if (event == "ramsebalius_timer1")
		{
			npc.broadcastNpcSay("Your time is up!");
			npc.deleteMe();
		}
		else if (event == "greatdemon_timer1")
		{
			npc.broadcastNpcSay("Do not interrupt my eternal rest again!");
			npc.deleteMe();
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState("Q334_TheWishingPotion");
		
		int npcId = npc.getId();
		
		if ((npcId != ALCHEMIST_MATILD) && (st.getState() == ScriptStateType.CREATED))
		{
			return getNoQuestMsg();
		}
		if ((npcId == TORAI) && st.hasItems(FORBIDDEN_LOVE_SCROLL))
		{
			st.takeItems(FORBIDDEN_LOVE_SCROLL, 1);
			st.giveItems(Inventory.ADENA_ID, 500000);
			return "7557-01.htm";
		}
		if (npcId == WISDOM_CHEST)
		{
			st.giveItems(REWARD_1[getRandom(REWARD_1.length)], 1);
			st.giveItems(REWARD_2[getRandom(REWARD_2.length)], 1);
			st.giveItems(REWARD_3[getRandom(REWARD_3.length)], 1);
			if (getRandom(3) == 0) // FIXME == 0 seguro??
			{
				st.giveItems(HEART_OF_PAAGRIO, 1);
			}
			st.giveItems(4409, 1);
			st.giveItems(4408, 1);
			npc.deleteMe();
			return "7743-0" + (getRandom(6) + 1) + ".htm";
			
		}
		else if (npcId == RUPINA)
		{
			if (getRandom(100) <= 4)
			{
				st.giveItems(NECKLACE_OF_GRACE, 1);
				npc.decayMe();
				return "7742-01.htm";
			}
			st.giveItems(REWARD_4[getRandom(REWARD_4.length)], 1);
			npc.decayMe();
			return "7742-02.htm";
		}
		else if (npcId == ALCHEMIST_MATILD)
		{
			if (npc.isBusy())
			{
				return "7738-20.htm";
			}
			if (st.getPlayer().getLevel() <= 29)
			{
				st.exitQuest(true); // FIXME true ??
				return "7738-21.htm";
			}
			
			int cond = st.getInt("cond");
			
			if ((cond == 5) && st.hasItems(MATILDS_ORB))
			{
				return "7738-11.htm";
			}
			if ((cond == 4) && checkIngredients(st))
			{
				return "7738-08.htm";
			}
			if ((cond == 4) && !(checkIngredients(st)))
			{
				return "7738-07.htm";
			}
			if ((cond == 3) && !(checkIngredients(st)))
			{
				return "7738-07.htm";
			}
			if ((cond == 3) && checkIngredients(st))
			{
				st.set("cond", "4");
				return "7738-08.htm";
			}
			if ((cond == 2) || (st.hasItems(ALCHEMY_TEXT) && st.hasItems(SECRET_BOOK)))
			{
				return "7738-05.htm";
			}
			if ((cond == 1) || (st.hasItems(ALCHEMY_TEXT) && !st.hasItems(SECRET_BOOK)))
			{
				return "7738-04.htm";
			}
			return "7738-01.htm";
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		ScriptState st = killer.getScriptState("Q334_TheWishingPotion");
		if (st == null)
		{
			return null;
		}
		ScriptStateType id = st.getState();
		if (id == ScriptStateType.CREATED)
		{
			return null;
		}
		if (id != ScriptStateType.STARTED)
		{
			st.setState(ScriptStateType.STARTED);
		}
		
		int npcId = npc.getId();
		int cond = st.getInt("cond");
		
		if (DROPLISTS.containsKey(npcId))
		{
			DropListChance dropList = DROPLISTS.get(npcId);
			
			if (getRandom(100) >= dropList.chance)
			{
				st.giveItems(dropList.itemId, 1);
			}
		}
		else if ((npcId == SECRET_KEEPER_TREE) && (cond == 1) && !st.hasItems(SECRET_BOOK))
		{
			st.set("cond", "2");
			st.giveItems(SECRET_BOOK, 1);
			st.playSound(PlaySoundType.QUEST_ITEMGET);
		}
		else if (DROPLISTS.containsKey(npcId) && (cond == 3))
		{
			if ((getRandom(100) <= DROPLISTS.get(npcId).chance) && !st.hasItems(DROPLISTS.get(npcId).itemId))
			{
				st.giveItems(DROPLISTS.get(npcId).itemId, 1);
				if (checkIngredients(st))
				{
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.set("cond", "4");
				}
				else
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
				}
			}
		}
		else
		{
			if (npcId == SUCCUBUS_OF_SEDUCTION)
			{
				if (getRandom(100) <= 3)
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.giveItems(FORBIDDEN_LOVE_SCROLL, 1);
				}
			}
			else if (npcId == GRIMA)
			{
				if (getRandom(100) < 4)
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					if (getRandom(1000) == 0)
					{
						st.giveItems(Inventory.ADENA_ID, 100000000);
					}
					else
					{
						st.giveItems(Inventory.ADENA_ID, 900000);
					}
				}
			}
			else if (npcId == SANCHES)
			{
				if (getTimer("sanches_timer1") != null)
				{
					getTimer("sanches_timer1").cancel();
				}
				if (getRandom(100) <= 50)
				{
					npc.broadcastNpcSay("It's time to come out my Remless... Bonaparterius!");
					L2Npc spawnedNpc = addSpawn(BONAPARTERIUS, npc, true, 0);
					spawnedNpc.broadcastNpcSay("I am the Great Emperor's son!");
					startTimer("bonaparterius_timer1", 600000, spawnedNpc, null, false);
				}
				else
				{
					st.giveItems(REWARD_4[getRandom(REWARD_4.length)], 1);
				}
			}
			else if (npcId == BONAPARTERIUS)
			{
				if (getTimer("bonaparterius_timer1") != null)
				{
					getTimer("bonaparterius_timer1").cancel();
				}
				npc.broadcastNpcSay("Only Ramsebalius would be able to avenge me!");
				if (getRandom(100) <= 50)
				{
					L2Npc spawnedNpc = addSpawn(RAMSEBALIUS, npc, true, 0);
					spawnedNpc.broadcastNpcSay("Meet the absolute ruler!");
					startTimer("ramsebalius_timer1", 600000, spawnedNpc, null, false);
				}
				else
				{
					st.giveItems(REWARD_4[getRandom(REWARD_4.length)], 1);
				}
			}
			else if (npcId == RAMSEBALIUS)
			{
				if (getTimer("ramsebalius_timer1") != null)
				{
					getTimer("ramsebalius_timer1").cancel();
				}
				npc.broadcastNpcSay("You evil piece of...");
				if (getRandom(100) <= 50)
				{
					L2Npc spawnedNpc = addSpawn(GREAT_DEMON_KING, npc, true, 0);
					spawnedNpc.broadcastNpcSay("Who dares to kill my fiendly minion?!");
					startTimer("greatdemon_timer1", 600000, spawnedNpc, null, false);
				}
				else
				{
					st.giveItems(REWARD_4[getRandom(REWARD_4.length)], 1);
				}
			}
			else if (npcId == GREAT_DEMON_KING)
			{
				if (getTimer("greatdemon_timer1") != null)
				{
					getTimer("greatdemon_timer1").cancel();
				}
				st.giveItems(Inventory.ADENA_ID, 1412965);
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
		}
		return null;
	}
}
