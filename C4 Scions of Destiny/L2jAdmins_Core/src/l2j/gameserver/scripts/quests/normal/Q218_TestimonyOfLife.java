package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
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
public class Q218_TestimonyOfLife extends Script
{
	// NPCs
	private static final int ASTERIOS = 7154;
	private static final int PUSHKIN = 7300;
	private static final int THALIA = 7371;
	private static final int ADONIUS = 7375;
	private static final int ARKENIA = 7419;
	private static final int CARDIEN = 7460;
	private static final int ISAEL = 7655;
	// Monster's
	private static final int ANT_RECRUIT = 82;
	private static final int ANT_PATROL = 84;
	private static final int ANT_GUARD = 86;
	private static final int ANT_SOLDIER = 87;
	private static final int ANT_WARRIOR_CAPTAIN = 88;
	private static final int GRIZZLY = 145;
	private static final int WYRN = 176;
	private static final int MARSH_SPIDER = 233;
	private static final int GUARDIAN_BASILISK = 550;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int UNICORN_OF_EVA = 5077;
	// Items
	private static final int TALINS_SPEAR = 3026;
	private static final int CARDIEN_LETTER = 3141;
	private static final int CAMOMILE_CHARM = 3142;
	private static final int HIERARCH_LETTER = 3143;
	private static final int MOONFLOWER_CHARM = 3144;
	private static final int GRAIL_DIAGRAM = 3145;
	private static final int THALIA_LETTER_1 = 3146;
	private static final int THALIA_LETTER_2 = 3147;
	private static final int THALIA_INSTRUCTIONS = 3148;
	private static final int PUSHKIN_LIST = 3149;
	private static final int PURE_MITHRIL_CUP = 3150;
	private static final int ARKENIA_CONTRACT = 3151;
	private static final int ARKENIA_INSTRUCTIONS = 3152;
	private static final int ADONIUS_LIST = 3153;
	private static final int ANDARIEL_SCRIPTURE_COPY = 3154;
	private static final int STARDUST = 3155;
	private static final int ISAEL_INSTRUCTIONS = 3156;
	private static final int ISAEL_LETTER = 3157;
	private static final int GRAIL_OF_PURITY = 3158;
	private static final int TEARS_OF_UNICORN = 3159;
	private static final int WATER_OF_LIFE = 3160;
	private static final int PURE_MITHRIL_ORE = 3161;
	private static final int ANT_SOLDIER_ACID = 3162;
	private static final int WYRM_TALON = 3163;
	private static final int SPIDER_ICHOR = 3164;
	private static final int HARPY_DOWN = 3165;
	
	private static final int[] TALINS_PIECES =
	{
		3166,
		3167,
		3168,
		3169,
		3170,
		3171
	};
	
	// Rewards
	private static final int MARK_OF_LIFE = 3140;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	public Q218_TestimonyOfLife()
	{
		super(218, "Testimony of Life");
		
		registerItems(TALINS_SPEAR, CARDIEN_LETTER, CAMOMILE_CHARM, HIERARCH_LETTER, MOONFLOWER_CHARM, GRAIL_DIAGRAM, THALIA_LETTER_1, THALIA_LETTER_2, THALIA_INSTRUCTIONS, PUSHKIN_LIST, PURE_MITHRIL_CUP, ARKENIA_CONTRACT, ARKENIA_INSTRUCTIONS, ADONIUS_LIST, ANDARIEL_SCRIPTURE_COPY, STARDUST, ISAEL_INSTRUCTIONS, ISAEL_LETTER, GRAIL_OF_PURITY, TEARS_OF_UNICORN, WATER_OF_LIFE, PURE_MITHRIL_ORE, ANT_SOLDIER_ACID, WYRM_TALON, SPIDER_ICHOR, HARPY_DOWN, 3166, 3167, 3168, 3169, 3170, 3171);
		
		addStartNpc(CARDIEN);
		addTalkId(ASTERIOS, PUSHKIN, THALIA, ADONIUS, ARKENIA, CARDIEN, ISAEL);
		
		addKillId(GRIZZLY, WYRN, MARSH_SPIDER, UNICORN_OF_EVA, GUARDIAN_BASILISK, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, ANT_SOLDIER, ANT_WARRIOR_CAPTAIN);
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
		
		if (event.equalsIgnoreCase("7460-04.htm"))
		{
			st.startQuest();
			st.giveItems(CARDIEN_LETTER, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 16);
		}
		else if (event.equalsIgnoreCase("7154-07.htm"))
		{
			st.setCond(2, true);
			st.takeItems(CARDIEN_LETTER, 1);
			st.giveItems(HIERARCH_LETTER, 1);
			st.giveItems(MOONFLOWER_CHARM, 1);
		}
		else if (event.equalsIgnoreCase("7371-03.htm"))
		{
			st.setCond(3, true);
			st.takeItems(HIERARCH_LETTER, 1);
			st.giveItems(GRAIL_DIAGRAM, 1);
		}
		else if (event.equalsIgnoreCase("7371-11.htm"))
		{
			st.takeItems(STARDUST, 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			
			if (player.getLevel() < 38)
			{
				htmltext = "7371-10.htm";
				st.setCond(13);
				st.giveItems(THALIA_INSTRUCTIONS, 1);
			}
			else
			{
				st.setCond(14);
				st.giveItems(THALIA_LETTER_2, 1);
			}
		}
		else if (event.equalsIgnoreCase("7300-06.htm"))
		{
			st.setCond(4, true);
			st.takeItems(GRAIL_DIAGRAM, 1);
			st.giveItems(PUSHKIN_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7300-10.htm"))
		{
			st.setCond(6, true);
			st.takeItems(PUSHKIN_LIST, 1);
			st.takeItems(ANT_SOLDIER_ACID, -1);
			st.takeItems(PURE_MITHRIL_ORE, -1);
			st.takeItems(WYRM_TALON, -1);
			st.giveItems(PURE_MITHRIL_CUP, 1);
		}
		else if (event.equalsIgnoreCase("7419-04.htm"))
		{
			st.setCond(8, true);
			st.takeItems(THALIA_LETTER_1, 1);
			st.giveItems(ARKENIA_CONTRACT, 1);
			st.giveItems(ARKENIA_INSTRUCTIONS, 1);
		}
		else if (event.equalsIgnoreCase("7375-02.htm"))
		{
			st.setCond(9, true);
			st.takeItems(ARKENIA_INSTRUCTIONS, 1);
			st.giveItems(ADONIUS_LIST, 1);
		}
		else if (event.equalsIgnoreCase("7655-02.htm"))
		{
			st.setCond(15, true);
			st.takeItems(THALIA_LETTER_2, 1);
			st.giveItems(ISAEL_INSTRUCTIONS, 1);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7460-01.htm";
				}
				else if ((player.getLevel() < 37) || (player.getClassId().level() != 1))
				{
					htmltext = "7460-02.htm";
				}
				else
				{
					htmltext = "7460-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ASTERIOS:
						if (cond == 1)
						{
							htmltext = "7154-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7154-08.htm";
						}
						else if (cond == 20)
						{
							htmltext = "7154-09.htm";
							st.setCond(21, true);
							st.takeItems(MOONFLOWER_CHARM, 1);
							st.takeItems(WATER_OF_LIFE, 1);
							st.giveItems(CAMOMILE_CHARM, 1);
						}
						else if (cond == 21)
						{
							htmltext = "7154-10.htm";
						}
						break;
					
					case PUSHKIN:
						if (cond == 3)
						{
							htmltext = "7300-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7300-07.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7300-08.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7300-11.htm";
						}
						else if (cond > 6)
						{
							htmltext = "7300-12.htm";
						}
						break;
					
					case THALIA:
						if (cond == 2)
						{
							htmltext = "7371-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7371-04.htm";
						}
						else if ((cond > 3) && (cond < 6))
						{
							htmltext = "7371-05.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7371-06.htm";
							st.setCond(7, true);
							st.takeItems(PURE_MITHRIL_CUP, 1);
							st.giveItems(THALIA_LETTER_1, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7371-07.htm";
						}
						else if ((cond > 7) && (cond < 12))
						{
							htmltext = "7371-08.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7371-09.htm";
						}
						else if (cond == 13)
						{
							if (player.getLevel() < 38)
							{
								htmltext = "7371-12.htm";
							}
							else
							{
								htmltext = "7371-13.htm";
								st.setCond(14, true);
								st.takeItems(THALIA_INSTRUCTIONS, 1);
								st.giveItems(THALIA_LETTER_2, 1);
							}
						}
						else if (cond == 14)
						{
							htmltext = "7371-14.htm";
						}
						else if ((cond > 14) && (cond < 17))
						{
							htmltext = "7371-15.htm";
						}
						else if (cond == 17)
						{
							htmltext = "7371-16.htm";
							st.setCond(18, true);
							st.takeItems(ISAEL_LETTER, 1);
							st.giveItems(GRAIL_OF_PURITY, 1);
						}
						else if (cond == 18)
						{
							htmltext = "7371-17.htm";
						}
						else if (cond == 19)
						{
							htmltext = "7371-18.htm";
							st.setCond(20, true);
							st.takeItems(TEARS_OF_UNICORN, 1);
							st.giveItems(WATER_OF_LIFE, 1);
						}
						else if (cond > 19)
						{
							htmltext = "7371-19.htm";
						}
						break;
					
					case ADONIUS:
						if (cond == 8)
						{
							htmltext = "7375-01.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7375-03.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7375-04.htm";
							st.setCond(11, true);
							st.takeItems(ADONIUS_LIST, 1);
							st.takeItems(HARPY_DOWN, -1);
							st.takeItems(SPIDER_ICHOR, -1);
							st.giveItems(ANDARIEL_SCRIPTURE_COPY, 1);
						}
						else if (cond == 11)
						{
							htmltext = "7375-05.htm";
						}
						else if (cond > 11)
						{
							htmltext = "7375-06.htm";
						}
						break;
					
					case ARKENIA:
						if (cond == 7)
						{
							htmltext = "7419-01.htm";
						}
						else if ((cond > 7) && (cond < 11))
						{
							htmltext = "7419-05.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7419-06.htm";
							st.setCond(12, true);
							st.takeItems(ANDARIEL_SCRIPTURE_COPY, 1);
							st.takeItems(ARKENIA_CONTRACT, 1);
							st.giveItems(STARDUST, 1);
						}
						else if (cond == 12)
						{
							htmltext = "7419-07.htm";
						}
						else if (cond > 12)
						{
							htmltext = "7419-08.htm";
						}
						break;
					
					case CARDIEN:
						if (cond == 1)
						{
							htmltext = "7460-05.htm";
						}
						else if ((cond > 1) && (cond < 21))
						{
							htmltext = "7460-06.htm";
						}
						else if (cond == 21)
						{
							htmltext = "7460-07.htm";
							st.takeItems(CAMOMILE_CHARM, 1);
							st.giveItems(MARK_OF_LIFE, 1);
							st.rewardExpAndSp(104591, 11250);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case ISAEL:
						if (cond == 14)
						{
							htmltext = "7655-01.htm";
						}
						else if (cond == 15)
						{
							htmltext = "7655-03.htm";
						}
						else if (cond == 16)
						{
							if (st.hasItems(TALINS_PIECES))
							{
								htmltext = "7655-04.htm";
								st.setCond(17, true);
								
								for (int itemId : TALINS_PIECES)
								{
									st.takeItems(itemId, 1);
								}
								
								st.takeItems(ISAEL_INSTRUCTIONS, 1);
								st.giveItems(ISAEL_LETTER, 1);
								st.giveItems(TALINS_SPEAR, 1);
							}
							else
							{
								htmltext = "7655-03.htm";
							}
						}
						else if (cond == 17)
						{
							htmltext = "7655-05.htm";
						}
						else if (cond > 17)
						{
							htmltext = "7655-06.htm";
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
			case GUARDIAN_BASILISK:
				if ((st.getCond() == 4) && st.dropItems(PURE_MITHRIL_ORE, 1, 10, 500000))
				{
					if ((st.getItemsCount(WYRM_TALON) >= 20) && (st.getItemsCount(ANT_SOLDIER_ACID) >= 20))
					{
						st.setCond(5);
					}
				}
				break;
			
			case WYRN:
				if ((st.getCond() == 4) && st.dropItems(WYRM_TALON, 1, 20, 500000))
				{
					if ((st.getItemsCount(PURE_MITHRIL_ORE) >= 10) && (st.getItemsCount(ANT_SOLDIER_ACID) >= 20))
					{
						st.setCond(5);
					}
				}
				break;
			
			case ANT_RECRUIT:
			case ANT_PATROL:
			case ANT_GUARD:
			case ANT_SOLDIER:
			case ANT_WARRIOR_CAPTAIN:
				if ((st.getCond() == 4) && st.dropItems(ANT_SOLDIER_ACID, 1, 20, 800000))
				{
					if ((st.getItemsCount(PURE_MITHRIL_ORE) >= 10) && (st.getItemsCount(WYRM_TALON) >= 20))
					{
						st.setCond(5);
					}
				}
				break;
			
			case MARSH_SPIDER:
				if ((st.getCond() == 9) && st.dropItems(SPIDER_ICHOR, 1, 20, 500000) && (st.getItemsCount(HARPY_DOWN) >= 20))
				{
					st.setCond(10);
				}
				break;
			
			case GRIZZLY:
				if ((st.getCond() == 9) && st.dropItems(HARPY_DOWN, 1, 20, 500000) && (st.getItemsCount(SPIDER_ICHOR) >= 20))
				{
					st.set("cond", "10");
				}
				break;
			
			case UNICORN_OF_EVA:
				if ((st.getCond() == 18) && (st.getItemEquipped(ParpedollType.RHAND) == TALINS_SPEAR))
				{
					st.setCond(19, true);
					st.takeItems(GRAIL_OF_PURITY, 1);
					st.takeItems(TALINS_SPEAR, 1);
					st.giveItems(TEARS_OF_UNICORN, 1);
				}
				break;
			
			case LETO_LIZARDMAN_SHAMAN:
			case LETO_LIZARDMAN_OVERLORD:
				if ((st.getCond() == 15) && Rnd.nextBoolean())
				{
					for (int itemId : TALINS_PIECES)
					{
						if (!st.hasItems(itemId))
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.giveItems(itemId, 1);
							return null;
						}
					}
					st.setCond(16, true);
				}
				break;
		}
		
		return null;
	}
}
