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
 * Test of Sagittarius. Adapted from L2Acis, repaired and implemented by zarie.
 */
public class Q224_TestOfSagittarius extends Script
{
	// Items
	private static final int BERNARD_INTRODUCTION = 3294;
	private static final int HAMIL_LETTER_1 = 3295;
	private static final int HAMIL_LETTER_2 = 3296;
	private static final int HAMIL_LETTER_3 = 3297;
	private static final int HUNTER_RUNE_1 = 3298;
	private static final int HUNTER_RUNE_2 = 3299;
	private static final int TALISMAN_OF_KADESH = 3300;
	private static final int TALISMAN_OF_SNAKE = 3301;
	private static final int MITHRIL_CLIP = 3302;
	private static final int STAKATO_CHITIN = 3303;
	private static final int REINFORCED_BOWSTRING = 3304;
	private static final int MANASHEN_HORN = 3305;
	private static final int BLOOD_OF_LIZARDMAN = 3306;
	private static final int CRESCENT_MOON_BOW = 3028;
	private static final int WOODEN_ARROW = 17;
	
	// Rewards
	private static final int MARK_OF_SAGITTARIUS = 3293;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// NPCs
	private static final int BERNARD = 7702;
	private static final int HAMIL = 7626;
	private static final int SIR_ARON_TANFORD = 7653;
	private static final int VOKIAN = 7514;
	private static final int GAUEN = 7717;
	
	// Monsters
	private static final int ANT = 79;
	private static final int ANT_CAPTAIN = 80;
	private static final int ANT_OVERSEER = 81;
	private static final int ANT_RECRUIT = 82;
	private static final int ANT_PATROL = 84;
	private static final int ANT_GUARD = 86;
	private static final int NOBLE_ANT = 89;
	private static final int NOBLE_ANT_LEADER = 90;
	private static final int BREKA_ORC_SHAMAN = 269;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int MARSH_STAKATO_WORKER = 230;
	private static final int MARSH_STAKATO_SOLDIER = 232;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int MARSH_SPIDER = 233;
	private static final int ROAD_SCAVENGER = 551;
	private static final int MANASHEN_GARGOYLE = 563;
	private static final int LETO_LIZARDMAN = 577;
	private static final int LETO_LIZARDMAN_ARCHER = 578;
	private static final int LETO_LIZARDMAN_SOLDIER = 579;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int SERPENT_DEMON_KADESH = 5090;
	
	public Q224_TestOfSagittarius()
	{
		super(224, "Test of Sagittarius");
		
		registerItems(BERNARD_INTRODUCTION, HAMIL_LETTER_1, HAMIL_LETTER_2, HAMIL_LETTER_3, HUNTER_RUNE_1, HUNTER_RUNE_2, TALISMAN_OF_KADESH, TALISMAN_OF_SNAKE, MITHRIL_CLIP, STAKATO_CHITIN, REINFORCED_BOWSTRING, MANASHEN_HORN, BLOOD_OF_LIZARDMAN, CRESCENT_MOON_BOW);
		
		addStartNpc(BERNARD);
		addTalkId(BERNARD, HAMIL, SIR_ARON_TANFORD, VOKIAN, GAUEN);
		
		addKillId(ANT, ANT_CAPTAIN, ANT_OVERSEER, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, NOBLE_ANT, NOBLE_ANT_LEADER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, MARSH_SPIDER, ROAD_SCAVENGER, MANASHEN_GARGOYLE, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, SERPENT_DEMON_KADESH);
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
		
		// BERNARD
		if (event.equalsIgnoreCase("7702-04a.htm"))
		{
			st.startQuest();
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BERNARD_INTRODUCTION, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 96);
		}
		// HAMIL
		else if (event.equalsIgnoreCase("7626-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BERNARD_INTRODUCTION, 1);
			st.giveItems(HAMIL_LETTER_1, 1);
		}
		else if (event.equalsIgnoreCase("7626-07.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HUNTER_RUNE_1, 10);
			st.giveItems(HAMIL_LETTER_2, 1);
		}
		// SIR_ARON_TANFORD
		else if (event.equalsIgnoreCase("7653-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HAMIL_LETTER_1, 1);
		}
		// VOKIAN
		else if (event.equalsIgnoreCase("7514-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HAMIL_LETTER_2, 1);
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
				if ((player.getClassId() != ClassId.ROGUE) && (player.getClassId() != ClassId.SCOUT) && (player.getClassId() != ClassId.ASSASSIN))
				{
					htmltext = "7702-02.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7702-01.htm";
				}
				else
				{
					htmltext = "7702-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case BERNARD:
						htmltext = "7702-05.htm";
						break;
					
					case HAMIL:
						if (cond == 1)
						{
							htmltext = "7626-01.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "7626-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7626-05.htm";
						}
						else if ((cond > 4) && (cond < 8))
						{
							htmltext = "7626-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7626-09.htm";
							st.set("cond", "9");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(HUNTER_RUNE_2, 10);
							st.giveItems(HAMIL_LETTER_3, 1);
						}
						else if ((cond > 8) && (cond < 12))
						{
							htmltext = "7626-10.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7626-11.htm";
							st.set("cond", "13");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 13)
						{
							htmltext = "7626-12.htm";
						}
						else if (cond == 14)
						{
							htmltext = "7626-13.htm";
							st.takeItems(BLOOD_OF_LIZARDMAN, -1);
							st.takeItems(CRESCENT_MOON_BOW, 1);
							st.takeItems(TALISMAN_OF_KADESH, 1);
							st.giveItems(MARK_OF_SAGITTARIUS, 1);
							st.rewardExpAndSp(54726, 20250);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.exitQuest(false, true);
						}
						break;
					
					case SIR_ARON_TANFORD:
						if (cond == 2)
						{
							htmltext = "7653-01.htm";
						}
						else if (cond > 2)
						{
							htmltext = "7653-03.htm";
						}
						break;
					
					case VOKIAN:
						if (cond == 5)
						{
							htmltext = "7514-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7514-03.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7514-04.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(TALISMAN_OF_SNAKE, 1);
						}
						else if (cond > 7)
						{
							htmltext = "7514-05.htm";
						}
						break;
					
					case GAUEN:
						if (cond == 9)
						{
							htmltext = "7717-01.htm";
							st.set("cond", "10");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(HAMIL_LETTER_3, 1);
						}
						else if (cond == 10)
						{
							htmltext = "7717-03.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7717-02.htm";
							st.set("cond", "12");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(MANASHEN_HORN, 1);
							st.takeItems(MITHRIL_CLIP, 1);
							st.takeItems(REINFORCED_BOWSTRING, 1);
							st.takeItems(STAKATO_CHITIN, 1);
							st.giveItems(CRESCENT_MOON_BOW, 1);
							st.giveItems(WOODEN_ARROW, 10);
						}
						else if (cond > 11)
						{
							htmltext = "7717-04.htm";
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
			case ANT:
			case ANT_CAPTAIN:
			case ANT_OVERSEER:
			case ANT_RECRUIT:
			case ANT_PATROL:
			case ANT_GUARD:
			case NOBLE_ANT:
			case NOBLE_ANT_LEADER:
				if ((cond == 3) && st.dropItems(HUNTER_RUNE_1, 1, 10, 500000))
				{
					st.set("cond", "4");
				}
				break;
			
			case BREKA_ORC_SHAMAN:
			case BREKA_ORC_OVERLORD:
				if ((cond == 6) && st.dropItems(HUNTER_RUNE_2, 1, 10, 500000))
				{
					st.set("cond", "7");
					st.giveItems(TALISMAN_OF_SNAKE, 1);
				}
				break;
			
			case MARSH_STAKATO_WORKER:
			case MARSH_STAKATO_SOLDIER:
			case MARSH_STAKATO_DRONE:
				if ((cond == 10) && st.dropItems(STAKATO_CHITIN, 1, 1, 100000) && st.hasItems(MANASHEN_HORN) && st.hasItems(MITHRIL_CLIP) && st.hasItems(REINFORCED_BOWSTRING))
				{
					st.set("cond", "11");
				}
				break;
			
			case MARSH_SPIDER:
				if ((cond == 10) && st.dropItems(REINFORCED_BOWSTRING, 1, 1, 100000) && st.hasItems(MANASHEN_HORN) && st.hasItems(MITHRIL_CLIP) && st.hasItems(STAKATO_CHITIN))
				{
					st.set("cond", "11");
				}
				break;
			
			case ROAD_SCAVENGER:
				if ((cond == 10) && st.dropItems(MITHRIL_CLIP, 1, 1, 100000) && st.hasItems(MANASHEN_HORN) && st.hasItems(REINFORCED_BOWSTRING) && st.hasItems(STAKATO_CHITIN))
				{
					st.set("cond", "11");
				}
				break;
			
			case MANASHEN_GARGOYLE:
				if ((cond == 10) && st.dropItems(MANASHEN_HORN, 1, 1, 100000) && st.hasItems(REINFORCED_BOWSTRING) && st.hasItems(MITHRIL_CLIP) && st.hasItems(STAKATO_CHITIN))
				{
					st.set("cond", "11");
				}
				break;
			
			case LETO_LIZARDMAN:
			case LETO_LIZARDMAN_ARCHER:
			case LETO_LIZARDMAN_SOLDIER:
			case LETO_LIZARDMAN_WARRIOR:
			case LETO_LIZARDMAN_SHAMAN:
			case LETO_LIZARDMAN_OVERLORD:
				if (cond == 13)
				{
					if (((st.getItemsCount(BLOOD_OF_LIZARDMAN) - 120) * 5) > Rnd.get(100))
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(BLOOD_OF_LIZARDMAN, -1);
						addSpawn(SERPENT_DEMON_KADESH, player, false, 0);
					}
					else
					{
						st.dropItemsAlways(BLOOD_OF_LIZARDMAN, 1, 0);
					}
				}
				break;
			
			case SERPENT_DEMON_KADESH:
				if (cond == 13)
				{
					if (st.getItemEquipped(ParpedollType.RHAND) == CRESCENT_MOON_BOW)
					{
						st.set("cond", "14");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.giveItems(TALISMAN_OF_KADESH, 1);
					}
					else
					{
						addSpawn(SERPENT_DEMON_KADESH, player, false, 0);
					}
				}
				break;
		}
		
		return null;
	}
}
