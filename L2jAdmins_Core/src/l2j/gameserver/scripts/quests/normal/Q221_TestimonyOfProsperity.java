package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author Reynald0
 */
public class Q221_TestimonyOfProsperity extends Script
{
	// NORMAL ITEMs
	private static final int ADENA = 57;
	private static final int ANIMAL_SKIN = 1867;
	private static final int RECIPE_TITAN_KEY = 3023;
	private static final int KEY_OF_TITAN = 3030;
	
	// QUEST ITEMs
	private static final int CRYSTAL_BROOCH = 3428;
	private static final int FIRST_RING_OF_TESTIMONY = 3239;
	private static final int SECOND_RING_OF_TESTIMONY = 3240;
	private static final int OLD_ACCOUNT_BOOK = 3241;
	private static final int BLESSED_SEED = 3242;
	private static final int EMILY_RECIPE = 3243;
	private static final int LILITH_ELVEN_WAFER = 3244;
	private static final int MAPHR_TABLET_FRAGMENT = 3245;
	private static final int COLLECTION_LICENSE = 3246;
	private static final int LOCKIRIN_FIRST_NOTICE = 3247;
	private static final int LOCKIRIN_SECOND_NOTICE = 3248;
	private static final int LOCKIRIN_THIRD_NOTICE = 3249;
	private static final int LOCKIRIN_FOURTH_NOTICE = 3250;
	private static final int LOCKIRIN_FIFTH_NOTICE = 3251;
	private static final int CONTRIBUTION_OF_SHARI = 3252;
	private static final int CONTRIBUTION_OF_MION = 3253;
	private static final int CONTRIBUTION_OF_MARYSE = 3254;
	private static final int MARYSES_REQUEST = 3255;
	private static final int CONTRIBUTION_OF_TOMA = 3256;
	private static final int RECEIPT_OF_BOLTER = 3257;
	private static final int FIRST_RECEIPT_OF_CONTRIBUTION = 3258;
	private static final int SECOND_RECEIPT_OF_CONTRIBUTION = 3259;
	private static final int THIRD_RECEIPT_OF_CONTRIBUTION = 3260;
	private static final int FOURTH_RECEIPT_OF_CONTRIBUTION = 3261;
	private static final int FIFTH_RECEIPT_OF_CONTRIBUTION = 3262;
	private static final int PROCURATION_OF_TOROCCO = 3263;
	private static final int BRIGHT_LIST = 3264;
	private static final int MANDRAGORA_PETAL = 3265;
	private static final int CRIMSON_MOSS = 3266;
	private static final int MANDRAGORA_BOUQUET = 3267;
	private static final int PARMAN_LETTER = 3269;
	private static final int CLAY_DOUGH = 3270;
	private static final int PATTERN_OF_KEYHOLE = 3271;
	private static final int NIKOLA_LIST = 3272;
	private static final int STAKATO_SHELL = 3273;
	private static final int TOAD_LORD_SAC = 3274;
	private static final int SPIDER_THORN = 3275;
	
	// NPCs
	private static final int PARMAN = 7104;
	private static final int LOCKIRIN = 7531;
	private static final int SPIRON = 7532;
	private static final int SHARI = 7517;
	private static final int BALANKI = 7533;
	private static final int MION = 7519;
	private static final int MARYSE_REDBONNET = 7553;
	private static final int KEEF = 7534;
	private static final int TOROCCO = 7555;
	private static final int FILAUR = 7535;
	private static final int BOLTER = 7554;
	private static final int ARIN = 7536;
	private static final int TOMA = 7556;
	private static final int PIOTUR = 7597;
	private static final int BRIGHT = 7466;
	private static final int EMILY = 7620;
	private static final int WILFORD = 7005;
	private static final int LILITH = 7368;
	private static final int NIKOLA = 7621;
	private static final int BOX_OF_TITAN = 7622;
	
	// MONSTERs
	private static final int MANDRAGORA_SPROUT = 154;
	private static final int MANDRAGORA_SAPLING = 155;
	private static final int MANDRAGORA_BLOSSOM = 156;
	private static final int MANDRAGORA_SPROUT_2 = 223;
	private static final int GIANT_CRIMSON_ANT = 228;
	private static final int MARSH_STAKATO = 157;
	private static final int MARSH_STAKATO_WORKER = 230;
	private static final int MARSH_STAKATO_SOLDIER = 232;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int TOAD_LORD = 231;
	private static final int MARSH_SPIDER = 233;
	
	// REWARDs
	private static final int DIMENSIONAL_DIAMOND = 7562;
	private static final int MARK_OF_PROSPERITY = 3238;
	
	public Q221_TestimonyOfProsperity()
	{
		super(221, "Testimony Of Prosperity");
		registerItems(FIRST_RING_OF_TESTIMONY, COLLECTION_LICENSE, LOCKIRIN_FIRST_NOTICE, LOCKIRIN_SECOND_NOTICE, LOCKIRIN_THIRD_NOTICE, LOCKIRIN_FOURTH_NOTICE, LOCKIRIN_FIFTH_NOTICE, CONTRIBUTION_OF_SHARI, CONTRIBUTION_OF_MION, MARYSES_REQUEST, CONTRIBUTION_OF_MARYSE, PROCURATION_OF_TOROCCO, THIRD_RECEIPT_OF_CONTRIBUTION, RECEIPT_OF_BOLTER, FOURTH_RECEIPT_OF_CONTRIBUTION, CONTRIBUTION_OF_TOMA, FIFTH_RECEIPT_OF_CONTRIBUTION, SECOND_RECEIPT_OF_CONTRIBUTION, OLD_ACCOUNT_BOOK, BLESSED_SEED, BRIGHT_LIST, MANDRAGORA_PETAL, CRIMSON_MOSS, MANDRAGORA_BOUQUET, EMILY_RECIPE, CRYSTAL_BROOCH, LILITH_ELVEN_WAFER, SECOND_RING_OF_TESTIMONY, PARMAN_LETTER, CLAY_DOUGH, NIKOLA_LIST, RECIPE_TITAN_KEY, STAKATO_SHELL, TOAD_LORD_SAC, SPIDER_THORN, MAPHR_TABLET_FRAGMENT);
		addStartNpc(PARMAN);
		addTalkId(PARMAN, LOCKIRIN, SPIRON, SHARI, BALANKI, MION, MARYSE_REDBONNET, KEEF, TOROCCO, FILAUR, BOLTER, ARIN, TOMA, PIOTUR, BRIGHT, EMILY, WILFORD, LILITH, NIKOLA, BOX_OF_TITAN);
		addKillId(MANDRAGORA_SPROUT, MANDRAGORA_SAPLING, MANDRAGORA_BLOSSOM, MANDRAGORA_SPROUT_2, GIANT_CRIMSON_ANT, MARSH_STAKATO, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, TOAD_LORD, MARSH_SPIDER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		// PARMAN
		if (event.equalsIgnoreCase("30104-04.htm"))
		{
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(FIRST_RING_OF_TESTIMONY, 1);
			st.setState(ScriptStateType.STARTED);
		}
		else if (event.equalsIgnoreCase("30104-08.htm"))
		{
			if (player.getLevel() >= 38)
			{
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(BLESSED_SEED, 1);
				st.takeItems(EMILY_RECIPE, 1);
				st.takeItems(FIRST_RING_OF_TESTIMONY, 1);
				st.takeItems(LILITH_ELVEN_WAFER, 1);
				st.takeItems(OLD_ACCOUNT_BOOK, 1);
				st.giveItems(PARMAN_LETTER, 1);
				st.giveItems(SECOND_RING_OF_TESTIMONY, 1);
			}
			else
			{
				htmltext = "30104-09.htm";
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("30621-04.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(CLAY_DOUGH, 1);
		}
		else if (event.equalsIgnoreCase("30622-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(CLAY_DOUGH, 1);
			st.giveItems(PATTERN_OF_KEYHOLE, 1);
		}
		else if (event.equalsIgnoreCase("30622-04.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(KEY_OF_TITAN, 1);
			st.takeItems(NIKOLA_LIST, 1);
			st.giveItems(MAPHR_TABLET_FRAGMENT, 1);
		}
		// LOCKIRIN
		else if (event.equalsIgnoreCase("30531-03.htm"))
		{
			st.giveItems(COLLECTION_LICENSE, 1);
			st.giveItems(LOCKIRIN_FIRST_NOTICE, 1);
			st.giveItems(LOCKIRIN_SECOND_NOTICE, 1);
			st.giveItems(LOCKIRIN_THIRD_NOTICE, 1);
			st.giveItems(LOCKIRIN_FOURTH_NOTICE, 1);
			st.giveItems(LOCKIRIN_FIFTH_NOTICE, 1);
		}
		// TOROCCO
		else if (event.equalsIgnoreCase("30555-02.htm"))
		{
			st.giveItems(PROCURATION_OF_TOROCCO, 1);
		}
		else if (event.equalsIgnoreCase("pay"))
		{
			if (player.getInventory().getAdena() >= 5000)
			{
				htmltext = "30534-03b.htm";
				st.takeItems(ADENA, 5000);
				st.takeItems(PROCURATION_OF_TOROCCO, 1);
				st.giveItems(THIRD_RECEIPT_OF_CONTRIBUTION, 1);
			}
			else
			{
				htmltext = "30534-03a.htm";
			}
		}
		// PIOTUR
		else if (event.equalsIgnoreCase("30597-02.htm"))
		{
			st.giveItems(BLESSED_SEED, 1);
			
			if (st.hasItems(OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILY_RECIPE, LILITH_ELVEN_WAFER))
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		// BRIGHT
		else if (event.equalsIgnoreCase("30466-03.htm"))
		{
			st.giveItems(BRIGHT_LIST, 1);
		}
		else if (event.equalsIgnoreCase("30620-03.htm"))
		{
			st.giveItems(EMILY_RECIPE, 1);
			st.takeItems(MANDRAGORA_BOUQUET, 1);
			
			if (st.hasItems(OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILY_RECIPE, LILITH_ELVEN_WAFER))
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		// WILFORD
		else if (event.equalsIgnoreCase("30005-04.htm"))
		{
			st.giveItems(CRYSTAL_BROOCH, 1);
		}
		else if (event.equalsIgnoreCase("30368-03.htm"))
		{
			st.takeItems(CRYSTAL_BROOCH, 1);
			st.giveItems(LILITH_ELVEN_WAFER, 1);
			
			if (st.hasItems(OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILY_RECIPE, LILITH_ELVEN_WAFER))
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
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
				if (player.getRace() != Race.DWARF)
				{
					htmltext = "30104-01.htm";
				}
				else if ((player.getLevel() < 37) || (player.getClassId().level() != 1))
				{
					htmltext = "30104-02.htm";
				}
				else
				{
					htmltext = "30104-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case PARMAN:
						if (cond == 1)
						{
							htmltext = "30104-05.htm";
						}
						else if (cond == 2)
						{
							if (st.hasItems(OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILY_RECIPE, LILITH_ELVEN_WAFER))
							{
								htmltext = "30104-06.htm";
							}
						}
						else if ((cond == 3) && (player.getLevel() >= 38))
						{
							htmltext = "30104-06.htm";
						}
						else if ((cond == 4) && (player.getLevel() >= 38))
						{
							htmltext = "30104-10.htm";
						}
						else if (cond == 9)
						{
							htmltext = "30104-12.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(MAPHR_TABLET_FRAGMENT, 1);
							st.takeItems(SECOND_RING_OF_TESTIMONY, 1);
							st.giveItems(DIMENSIONAL_DIAMOND, 16);
							st.giveItems(MARK_OF_PROSPERITY, 1);
							st.rewardExpAndSp(120000, 1000);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false);
						}
						break;
					case LOCKIRIN:
						if (cond == 1)
						{
							if (st.hasItems(OLD_ACCOUNT_BOOK))
							{
								htmltext = "30531-06.htm";
							}
							else if (st.hasItems(FIRST_RECEIPT_OF_CONTRIBUTION, SECOND_RECEIPT_OF_CONTRIBUTION, THIRD_RECEIPT_OF_CONTRIBUTION, FOURTH_RECEIPT_OF_CONTRIBUTION, FIFTH_RECEIPT_OF_CONTRIBUTION))
							{
								htmltext = "30531-05.htm";
								st.takeItems(FIRST_RECEIPT_OF_CONTRIBUTION, 1);
								st.takeItems(SECOND_RECEIPT_OF_CONTRIBUTION, 1);
								st.takeItems(THIRD_RECEIPT_OF_CONTRIBUTION, 1);
								st.takeItems(FOURTH_RECEIPT_OF_CONTRIBUTION, 1);
								st.takeItems(FIFTH_RECEIPT_OF_CONTRIBUTION, 1);
								st.takeItems(COLLECTION_LICENSE, 1);
								st.giveItems(OLD_ACCOUNT_BOOK, 1);
								
								if (st.hasItems(OLD_ACCOUNT_BOOK, BLESSED_SEED, EMILY_RECIPE, LILITH_ELVEN_WAFER))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30531-04.htm";
							}
							else
							{
								htmltext = "30531-01.htm";
							}
						}
						break;
					case SPIRON:
						if (cond == 1)
						{
							if (st.hasItems(COLLECTION_LICENSE, LOCKIRIN_FIRST_NOTICE))
							{
								htmltext = "30532-01.htm";
								st.takeItems(LOCKIRIN_FIRST_NOTICE, 1);
							}
							else if (st.getItemsCount(CONTRIBUTION_OF_SHARI) == 1)
							{
								htmltext = "30532-03.htm";
								st.takeItems(CONTRIBUTION_OF_SHARI, 1);
								st.giveItems(FIRST_RECEIPT_OF_CONTRIBUTION, 1);
							}
							else if (st.getItemsCount(FIRST_RECEIPT_OF_CONTRIBUTION) == 1)
							{
								htmltext = "30532-04.htm";
							}
							else if (st.getItemsCount(COLLECTION_LICENSE) == 1)
							{
								htmltext = "30532-02.htm";
							}
						}
						break;
					case SHARI:
						if (cond == 1)
						{
							if (st.hasItems(LOCKIRIN_FIRST_NOTICE))
							{
								htmltext = getNoQuestMsg();
							}
							else if (st.hasItems(CONTRIBUTION_OF_SHARI))
							{
								htmltext = "30517-02.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30517-01.htm";
								st.giveItems(CONTRIBUTION_OF_SHARI, 1);
							}
						}
						break;
					case BALANKI:
						if (cond == 1)
						{
							if (st.hasItems(COLLECTION_LICENSE, LOCKIRIN_SECOND_NOTICE))
							{
								htmltext = "30533-01.htm";
								st.takeItems(LOCKIRIN_SECOND_NOTICE, 1);
							}
							else if (st.hasItems(SECOND_RECEIPT_OF_CONTRIBUTION))
							{
								htmltext = "30533-04.htm";
							}
							else if (st.hasItems(CONTRIBUTION_OF_MION, CONTRIBUTION_OF_MARYSE))
							{
								htmltext = "30533-03.htm";
								st.takeItems(CONTRIBUTION_OF_MARYSE, 1);
								st.takeItems(CONTRIBUTION_OF_MION, 1);
								st.giveItems(SECOND_RECEIPT_OF_CONTRIBUTION, 1);
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30533-02.htm";
							}
						}
						break;
					case MION:
						if (cond == 1)
						{
							if (st.hasItems(LOCKIRIN_SECOND_NOTICE))
							{
								htmltext = getNoQuestMsg();
							}
							else if (st.hasItems(CONTRIBUTION_OF_MION))
							{
								htmltext = "30519-02.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30519-01.htm";
								st.giveItems(CONTRIBUTION_OF_MION, 1);
							}
						}
						break;
					case MARYSE_REDBONNET:
						if (cond == 1)
						{
							if (st.hasItems(LOCKIRIN_SECOND_NOTICE))
							{
								htmltext = getNoQuestMsg();
							}
							else if (st.hasItems(CONTRIBUTION_OF_MARYSE))
							{
								htmltext = "30553-04.htm";
							}
							else if (st.getItemsCount(ANIMAL_SKIN) >= 100)
							{
								st.takeItems(ANIMAL_SKIN, 100);
								st.takeItems(MARYSES_REQUEST, 1);
								st.giveItems(CONTRIBUTION_OF_MARYSE, 1);
								htmltext = "30553-03.htm";
							}
							else if (st.hasItems(MARYSES_REQUEST))
							{
								htmltext = "30553-02.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30553-01.htm";
								st.giveItems(MARYSES_REQUEST, 1);
							}
						}
						break;
					case KEEF:
						if (cond == 1)
						{
							if (st.hasItems(COLLECTION_LICENSE, LOCKIRIN_THIRD_NOTICE))
							{
								st.takeItems(LOCKIRIN_THIRD_NOTICE, 1);
								htmltext = "30534-01.htm";
							}
							else if (st.hasItems(THIRD_RECEIPT_OF_CONTRIBUTION))
							{
								htmltext = "30534-04.htm";
							}
							else if (st.hasItems(PROCURATION_OF_TOROCCO))
							{
								htmltext = "30534-03.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30534-02.htm";
							}
						}
						break;
					case TOROCCO:
						if (cond == 1)
						{
							if (st.hasItems(LOCKIRIN_THIRD_NOTICE))
							{
								htmltext = getNoQuestMsg();
							}
							else if (st.hasItems(PROCURATION_OF_TOROCCO))
							{
								htmltext = "30555-03.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30555-01.htm";
							}
						}
						break;
					case FILAUR:
						if (cond == 1)
						{
							if (st.hasItems(COLLECTION_LICENSE, LOCKIRIN_FOURTH_NOTICE))
							{
								htmltext = "30535-01.htm";
								st.takeItems(LOCKIRIN_FOURTH_NOTICE, 1);
							}
							else if (st.hasItems(RECEIPT_OF_BOLTER))
							{
								htmltext = "30535-03.htm";
								st.takeItems(RECEIPT_OF_BOLTER, 1);
								st.giveItems(FOURTH_RECEIPT_OF_CONTRIBUTION, 1);
							}
							else if (st.hasItems(FOURTH_RECEIPT_OF_CONTRIBUTION))
							{
								htmltext = "30535-04.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30535-02.htm";
							}
						}
						break;
					case BOLTER:
						if (cond == 1)
						{
							if (st.hasItems(LOCKIRIN_FIRST_NOTICE))
							{
								htmltext = getNoQuestMsg();
							}
							else if (st.hasItems(RECEIPT_OF_BOLTER))
							{
								htmltext = "30554-02.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30554-01.htm";
								st.giveItems(RECEIPT_OF_BOLTER, 1);
							}
						}
						break;
					case ARIN:
						if (cond == 1)
						{
							if (st.hasItems(COLLECTION_LICENSE, LOCKIRIN_FIFTH_NOTICE))
							{
								htmltext = "30536-01.htm";
								st.takeItems(LOCKIRIN_FIFTH_NOTICE, 1);
							}
							else if (st.hasItems(CONTRIBUTION_OF_TOMA))
							{
								htmltext = "30536-03.htm";
								st.takeItems(CONTRIBUTION_OF_TOMA, 1);
								st.giveItems(FIFTH_RECEIPT_OF_CONTRIBUTION, 1);
							}
							else if (st.hasItems(FIFTH_RECEIPT_OF_CONTRIBUTION))
							{
								htmltext = "30536-04.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30536-02.htm";
							}
						}
						break;
					case TOMA:
						if (cond == 1)
						{
							if (st.hasItems(LOCKIRIN_FIFTH_NOTICE))
							{
								htmltext = getNoQuestMsg();
							}
							else if (st.hasItems(CONTRIBUTION_OF_TOMA))
							{
								htmltext = "30556-02.htm";
							}
							else if (st.hasItems(COLLECTION_LICENSE))
							{
								htmltext = "30556-01.htm";
								st.giveItems(CONTRIBUTION_OF_TOMA, 1);
							}
						}
						break;
					case PIOTUR:
						if (cond == 1)
						{
							htmltext = st.hasItems(BLESSED_SEED) ? "30597-03.htm" : "30597-01.htm";
						}
						break;
					case BRIGHT:
						if (cond == 1)
						{
							if (st.hasItems(EMILY_RECIPE))
							{
								htmltext = "30466-07.htm";
							}
							else if (st.hasItems(MANDRAGORA_BOUQUET))
							{
								htmltext = "30466-06.htm";
							}
							else if ((st.getItemsCount(MANDRAGORA_PETAL) == 20) && (st.getItemsCount(CRIMSON_MOSS) == 10))
							{
								htmltext = "30466-05.htm";
								st.takeItems(MANDRAGORA_PETAL, 20);
								st.takeItems(CRIMSON_MOSS, 10);
								st.takeItems(BRIGHT_LIST, 1);
								st.giveItems(MANDRAGORA_BOUQUET, 1);
							}
							else if (st.hasItems(BRIGHT_LIST))
							{
								htmltext = "30466-04.htm";
							}
							else
							{
								htmltext = "30466-01.htm";
							}
						}
						else
						{
							htmltext = "30466-08.htm";
						}
						break;
					case EMILY:
						if (cond == 1)
						{
							if (st.hasItems(EMILY_RECIPE))
							{
								htmltext = "30620-04.htm";
							}
							else if (st.hasItems(MANDRAGORA_BOUQUET))
							{
								htmltext = "30620-01.htm";
							}
						}
						break;
					case WILFORD:
						if (cond == 1)
						{
							htmltext = st.hasItems(CRYSTAL_BROOCH) ? "30005-05.htm" : "30005-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30005-06.htm";
						}
						break;
					case LILITH:
						if (cond == 1)
						{
							htmltext = st.hasItems(LILITH_ELVEN_WAFER) ? "30368-04.htm" : "30368-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30368-04.htm";
						}
						else
						{
							htmltext = "30368-05.htm";
						}
						break;
					case NIKOLA:
						if (cond == 4)
						{
							htmltext = "30621-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30621-05.htm";
						}
						else if (cond == 6)
						{
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.set("cond", "7");
							htmltext = "30621-06.htm";
							st.takeItems(PARMAN_LETTER, 1);
							st.takeItems(PATTERN_OF_KEYHOLE, 1);
							st.giveItems(RECIPE_TITAN_KEY, 1);
							st.giveItems(NIKOLA_LIST, 1);
						}
						else if (cond == 7)
						{
							htmltext = "30621-07.htm";
						}
						else if (cond == 8)
						{
							htmltext = "30621-08.htm";
						}
						else if (cond == 9)
						{
							htmltext = "30621-09.htm";
						}
						break;
					case BOX_OF_TITAN:
						if (cond == 5)
						{
							htmltext = "30622-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30622-02.htm";
						}
						else if (cond == 8)
						{
							htmltext = "30622-03.htm";
						}
						else if (cond == 9)
						{
							htmltext = "30622-04.htm";
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case MANDRAGORA_BLOSSOM:
			case MANDRAGORA_SAPLING:
			case MANDRAGORA_SPROUT:
			case MANDRAGORA_SPROUT_2:
				if ((st.getInt("cond") == 1) && st.hasItems(BRIGHT_LIST))
				{
					st.dropItems(MANDRAGORA_PETAL, 1, 20, 500000);
				}
				break;
			case GIANT_CRIMSON_ANT:
				if ((st.getInt("cond") == 1) && st.hasItems(BRIGHT_LIST))
				{
					st.dropItems(CRIMSON_MOSS, 1, 10, 500000);
				}
				break;
			case MARSH_STAKATO:
			case MARSH_STAKATO_DRONE:
			case MARSH_STAKATO_SOLDIER:
			case MARSH_STAKATO_WORKER:
				if ((st.getInt("cond") == 7) && st.dropItems(STAKATO_SHELL, 1, 20, 350000))
				{
					if ((st.getItemsCount(SPIDER_THORN) >= 10) && (st.getItemsCount(STAKATO_SHELL) >= 20) && (st.getItemsCount(TOAD_LORD_SAC) >= 10))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.set("cond", "8");
					}
				}
				
				break;
			case TOAD_LORD:
				if ((st.getInt("cond") == 7) && st.dropItems(TOAD_LORD_SAC, 1, 10, 400000))
				{
					if ((st.getItemsCount(SPIDER_THORN) >= 10) && (st.getItemsCount(STAKATO_SHELL) >= 20) && (st.getItemsCount(TOAD_LORD_SAC) >= 10))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.set("cond", "8");
					}
					
				}
				break;
			case MARSH_SPIDER:
				if ((st.getInt("cond") == 7) && st.dropItems(SPIDER_THORN, 1, 10, 400000))
				{
					if ((st.getItemsCount(SPIDER_THORN) >= 10) && (st.getItemsCount(STAKATO_SHELL) >= 20) && (st.getItemsCount(TOAD_LORD_SAC) >= 10))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.set("cond", "8");
					}
				}
				break;
		}
		
		return null;
	}
	
}
