package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Q212_TrialOfDuty extends Script
{
	// Npc's
	private static final int HANNAVALT = 7109;
	private static final int DUSTIN = 7116;
	private static final int SIR_COLLIN_WINDAWOOD = 7311;
	private static final int SIR_ARON = 7653;
	private static final int SIR_KIEL = 7654;
	private static final int ISAEL = 7655;
	private static final int SPIRIT_OF_SIR_TALIANUS = 7656;
	// Monster's
	private static final int HANGMAN_TREE = 144;
	private static final int SKELETON_MARAUDER = 190;
	private static final int SKELETON_RAIDER = 191;
	private static final int STRAIN = 200;
	private static final int GHOUL = 201;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int LETO_LIZARDMAN = 577;
	private static final int LETO_LIZARDMAN_ARCHER = 578;
	private static final int LETO_LIZARDMAN_SOLDIER = 579;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int SPIRIT_OF_SIR_HEROD = 5119;
	// Item's
	private static final int LETTER_OF_DUSTIN_ID = 2634;
	private static final int KNIGHTS_TEAR_ID = 2635;
	private static final int MIRROR_OF_ORPIC_ID = 2636;
	private static final int TEAR_OF_CONFESSION_ID = 2637;
	private static final int REPORT_PIECE_ID = 2638;
	private static final int TALIANUSS_REPORT_ID = 2639;
	private static final int TEAR_OF_LOYALTY_ID = 2640;
	private static final int MILITAS_ARTICLE_ID = 2641;
	private static final int SAINTS_ASHES_URN_ID = 2642;
	private static final int ATEBALTS_SKULL_ID = 2643;
	private static final int ATEBALTS_RIBS_ID = 2644;
	private static final int ATEBALTS_SHIN_ID = 2645;
	private static final int LETTER_OF_WINDAWOOD_ID = 2646;
	private static final int OLD_KNIGHT_SWORD_ID = 3027;
	// Rewards
	private static final int DIMENSIONAL_DIAMOND = 7562;
	private static final int MARK_OF_DUTY_ID = 2633;
	
	public Q212_TrialOfDuty()
	{
		super(212, "Trial Of Duty");
		
		addStartNpc(HANNAVALT);
		
		addTalkId(HANNAVALT, DUSTIN, SIR_COLLIN_WINDAWOOD, SIR_ARON, SIR_KIEL, ISAEL, SPIRIT_OF_SIR_TALIANUS);
		
		addKillId(HANGMAN_TREE, SKELETON_MARAUDER, SKELETON_RAIDER, STRAIN);
		addKillId(GHOUL, BREKA_ORC_OVERLORD, SPIRIT_OF_SIR_HEROD, LETO_LIZARDMAN);
		addKillId(LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR);
		addKillId(LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD);
		
		registerItems(LETTER_OF_DUSTIN_ID, KNIGHTS_TEAR_ID, OLD_KNIGHT_SWORD_ID, TEAR_OF_CONFESSION_ID, MIRROR_OF_ORPIC_ID, TALIANUSS_REPORT_ID, TALIANUSS_REPORT_ID, MILITAS_ARTICLE_ID, MILITAS_ARTICLE_ID, MILITAS_ARTICLE_ID, MILITAS_ARTICLE_ID, MILITAS_ARTICLE_ID, MILITAS_ARTICLE_ID, ATEBALTS_SKULL_ID, ATEBALTS_RIBS_ID, ATEBALTS_SHIN_ID, LETTER_OF_WINDAWOOD_ID, TEAR_OF_LOYALTY_ID, SAINTS_ASHES_URN_ID, REPORT_PIECE_ID, REPORT_PIECE_ID);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		switch (event)
		{
			case "1":
				st.startQuest();
				return "7109-04.htm";
			
			case "7116_1":
				return "7116-02.htm";
			
			case "7116_2":
				return "7116-03.htm";
			
			case "7116_3":
				return "7116-04.htm";
			
			case "7116_4":
				st.takeItems(TEAR_OF_LOYALTY_ID, 1);
				st.setCond(14);
				return "7116-05.htm";
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		
		switch (st.getState())
		{
			case CREATED:
				if (npc.getId() == HANNAVALT)
				{
					switch (st.getPlayer().getClassId())
					{
						case KNIGHT:
						case ELF_KNIGHT:
						case PALUS_KNIGHT:
							st.startQuest();
							
							if (st.getPlayer().getLevel() >= 35)
							{
								htmltext = "7109-03.htm";
							}
							else
							{
								htmltext = "7109-01.htm";
								st.exitQuest(true);
							}
							break;
						
						default:
							htmltext = "7109-02.htm";
							st.exitQuest(true);
							break;
					}
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case HANNAVALT:
						if (st.getCond() == 1)
						{
							htmltext = "7109-04.htm";
						}
						else if (st.getCond() == 18)
						{
							st.rewardExpAndSp(79832, 3750);
							st.giveItems(DIMENSIONAL_DIAMOND, 61);
							st.takeItems(LETTER_OF_DUSTIN_ID, 1);
							st.giveItems(MARK_OF_DUTY_ID, 1);
							st.exitQuest(false, true);
							htmltext = "7109-05.htm";
						}
						break;
					
					case SIR_ARON:
						if (st.getCond() == 1)
						{
							if (!st.hasItems(OLD_KNIGHT_SWORD_ID))
							{
								st.giveItems(OLD_KNIGHT_SWORD_ID, 1);
							}
							st.setCond(2);
							htmltext = "7653-01.htm";
						}
						else if ((st.getCond() == 2) && (st.getItemsCount(KNIGHTS_TEAR_ID) == 0))
						{
							htmltext = "7653-02.htm";
						}
						else if ((st.getCond() == 3) && st.hasItems(KNIGHTS_TEAR_ID, OLD_KNIGHT_SWORD_ID))
						{
							st.takeItems(KNIGHTS_TEAR_ID, 1);
							st.takeItems(OLD_KNIGHT_SWORD_ID, 1);
							st.setCond(4);
							htmltext = "7653-03.htm";
						}
						else if (st.getCond() == 4)
						{
							htmltext = "7653-04.htm";
						}
						break;
					
					case SIR_KIEL:
						if (st.getCond() == 4)
						{
							
							st.setCond(5);
							htmltext = "7654-01.htm";
						}
						else if ((st.getCond() == 5) && st.hasItems(TALIANUSS_REPORT_ID))
						{
							htmltext = "7654-02.htm";
						}
						else if ((st.getCond() == 6) && st.hasItems(TALIANUSS_REPORT_ID))
						{
							st.giveItems(MIRROR_OF_ORPIC_ID, 1);
							st.setCond(7);
							htmltext = "7654-03.htm";
						}
						else if (st.getCond() == 7)
						{
							htmltext = "7654-04.htm";
						}
						else if ((st.getCond() == 9) && st.hasItems(TEAR_OF_CONFESSION_ID))
						{
							st.takeItems(TEAR_OF_CONFESSION_ID, 1);
							st.setCond(10);
							htmltext = "7654-05.htm";
						}
						else if (st.getCond() == 10)
						{
							htmltext = "7654-06.htm";
						}
						break;
					
					case SPIRIT_OF_SIR_TALIANUS:
						if ((st.getCond() == 8) && st.hasItems(MIRROR_OF_ORPIC_ID))
						{
							st.takeItems(MIRROR_OF_ORPIC_ID, 1);
							st.takeItems(TALIANUSS_REPORT_ID, 1);
							st.giveItems(TEAR_OF_CONFESSION_ID, 1);
							st.setCond(9);
							htmltext = "7656-01.htm";
						}
						break;
					
					case ISAEL:
						if (st.getCond() == 10)
						{
							if (st.getPlayer().getLevel() >= 36)
							{
								htmltext = "7655-02.htm";
								st.setCond(11);
							}
							else
							{
								htmltext = "7655-01.htm";
							}
						}
						else if (st.getCond() == 13)
						{
							htmltext = "7655-05.htm";
						}
						else if (st.getCond() == 11)
						{
							htmltext = "7655-03.htm";
						}
						else if (st.getCond() == 12)
						{
							htmltext = "7655-04.htm";
							st.takeItems(MILITAS_ARTICLE_ID, st.getItemsCount(MILITAS_ARTICLE_ID));
							st.giveItems(TEAR_OF_LOYALTY_ID, 1);
							st.setCond(13);
						}
						break;
					
					case DUSTIN:
						if ((st.getCond() == 13) && st.hasItems(TEAR_OF_LOYALTY_ID))
						{
							htmltext = "7116-01.htm";
						}
						else if ((st.getCond() == 14) && !(st.hasItems(ATEBALTS_SKULL_ID, ATEBALTS_RIBS_ID, ATEBALTS_SHIN_ID)))
						{
							htmltext = "7116-06.htm";
						}
						else if (st.getCond() == 15)
						{
							st.takeItems(ATEBALTS_SKULL_ID, 1);
							st.takeItems(ATEBALTS_RIBS_ID, 1);
							st.takeItems(ATEBALTS_SHIN_ID, 1);
							st.giveItems(SAINTS_ASHES_URN_ID, 1);
							st.setCond(16);
							htmltext = "7116-07.htm";
						}
						else if ((st.getCond() == 17) && st.hasItems(LETTER_OF_WINDAWOOD_ID))
						{
							st.takeItems(LETTER_OF_WINDAWOOD_ID, 1);
							st.giveItems(LETTER_OF_DUSTIN_ID, 1);
							st.setCond(18);
							htmltext = "7116-08.htm";
						}
						else if (st.getCond() == 16)
						{
							htmltext = "7116-09.htm";
						}
						else if (st.getCond() == 18)
						{
							htmltext = "7116-10.htm";
						}
						break;
					
					case SIR_COLLIN_WINDAWOOD:
						if ((st.getCond() == 16) && st.hasItems(SAINTS_ASHES_URN_ID))
						{
							htmltext = "7311-01.htm";
							st.takeItems(SAINTS_ASHES_URN_ID, 1);
							st.giveItems(LETTER_OF_WINDAWOOD_ID, 1);
							st.setCond(17);
						}
						else if (st.getCond() == 17)
						{
							htmltext = "7311-02.htm";
						}
						
				}
				break;
			
			case COMPLETED:
				return getAlreadyCompletedMsg();
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		if (st != null)
		{
			if (st.getState() != ScriptStateType.STARTED)
			{
				return null;
			}
			
			switch (npc.getId())
			{
				case SKELETON_MARAUDER:
				case SKELETON_RAIDER:
					if (st.getCond() == 2)
					{
						if (Rnd.get(50) < 2)
						{
							addSpawn(SPIRIT_OF_SIR_HEROD, npc, true, 0);
							st.playSound(PlaySoundType.QUEST_BEFORE_BATTLE);
						}
					}
					break;
				
				case SPIRIT_OF_SIR_HEROD:
					if ((st.getCond() == 2) && (player.getActiveWeaponItem() != null) && (player.getActiveWeaponItem().getId() == OLD_KNIGHT_SWORD_ID) && (st.getItemsCount(OLD_KNIGHT_SWORD_ID) > 0))
					{
						st.giveItems(KNIGHTS_TEAR_ID, 1);
						st.setCond(3, true);
					}
					break;
				
				case STRAIN:
					
					if ((st.getCond() == 5) && (st.getItemsCount(REPORT_PIECE_ID) < 10) && (st.getItemsCount(TALIANUSS_REPORT_ID) == 0))
					{
						if (st.getItemsCount(REPORT_PIECE_ID) == 9)
						{
							if (Rnd.get(2) == 1)
							{
								st.takeItems(REPORT_PIECE_ID, st.getItemsCount(REPORT_PIECE_ID));
								st.giveItems(TALIANUSS_REPORT_ID, 1);
								st.setCond(6, true);
							}
						}
						else if (Rnd.get(2) == 1)
						{
							st.giveItems(REPORT_PIECE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case GHOUL:
					
					if ((st.getCond() == 5) && (st.getItemsCount(REPORT_PIECE_ID) < 10) && (st.getItemsCount(TALIANUSS_REPORT_ID) == 0))
					{
						if (st.getItemsCount(REPORT_PIECE_ID) == 9)
						{
							if (Rnd.get(2) == 1)
							{
								st.takeItems(REPORT_PIECE_ID, st.getItemsCount(REPORT_PIECE_ID));
								st.giveItems(TALIANUSS_REPORT_ID, 1);
								st.setCond(6, true);
							}
						}
						else if (Rnd.get(2) == 1)
						{
							st.giveItems(REPORT_PIECE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case HANGMAN_TREE:
					if (st.getCond() == 7)
					{
						if (Rnd.get(100) < 33)
						{
							addSpawn(SPIRIT_OF_SIR_TALIANUS, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, 300000);
							st.setCond(8, true);
						}
					}
					break;
				
				case LETO_LIZARDMAN:
					if ((st.getCond() == 11) && (st.getItemsCount(MILITAS_ARTICLE_ID) < 20))
					{
						if (st.getItemsCount(MILITAS_ARTICLE_ID) == 19)
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.setCond(12, true);
						}
						else
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case LETO_LIZARDMAN_ARCHER:
					if ((st.getCond() == 11) && (st.getItemsCount(MILITAS_ARTICLE_ID) < 20))
					{
						if (st.getItemsCount(MILITAS_ARTICLE_ID) == 19)
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.setCond(12, true);
						}
						else
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case LETO_LIZARDMAN_SOLDIER:
					if ((st.getCond() == 11) && (st.getItemsCount(MILITAS_ARTICLE_ID) < 20))
					{
						if (st.getItemsCount(MILITAS_ARTICLE_ID) == 19)
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.setCond(12, true);
						}
						else
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case LETO_LIZARDMAN_WARRIOR:
					if ((st.getCond() == 11) && (st.getItemsCount(MILITAS_ARTICLE_ID) < 20))
					{
						if (st.getItemsCount(MILITAS_ARTICLE_ID) == 19)
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.setCond(12, true);
						}
						else
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case LETO_LIZARDMAN_SHAMAN:
					if ((st.getCond() == 11) && (st.getItemsCount(MILITAS_ARTICLE_ID) < 20))
					{
						if (st.getItemsCount(MILITAS_ARTICLE_ID) == 19)
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.setCond(12, true);
						}
						else
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case LETO_LIZARDMAN_OVERLORD:
					if ((st.getCond() == 11) && (st.getItemsCount(MILITAS_ARTICLE_ID) < 20))
					{
						if (st.getItemsCount(MILITAS_ARTICLE_ID) == 19)
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.setCond(12, true);
						}
						else
						{
							st.giveItems(MILITAS_ARTICLE_ID, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case BREKA_ORC_OVERLORD:
					if (st.getCond() == 14)
					{
						if (Rnd.get(2) == 1)
						{
							if (st.getItemsCount(ATEBALTS_SKULL_ID) == 0)
							{
								st.giveItems(ATEBALTS_SKULL_ID, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else if (st.getItemsCount(ATEBALTS_RIBS_ID) == 0)
							{
								st.giveItems(ATEBALTS_RIBS_ID, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
							else if (st.getItemsCount(ATEBALTS_SHIN_ID) == 0)
							{
								st.giveItems(ATEBALTS_SHIN_ID, 1);
								st.setCond(15, true);
							}
						}
					}
					break;
			}
		}
		return null;
	}
}
