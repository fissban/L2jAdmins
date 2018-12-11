package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original script in python
 * @author fissban
 */
public class Hannavalt extends Script
{
	// NPC's
	private static final int GRAND_MASTER_HANNAVALT = 7109;
	private static final int GRAND_MASTER_BLACKBIRD = 7187;
	private static final int GRAND_MASTER_SIRIA = 7689;
	private static final int GRAND_MASTER_SEDRICK = 7849;
	private static final int GRAND_MASTER_MARCUS = 7900;
	// ITEM's
	private static final int MARK_OF_CHALLENGER_ID = 2627;
	private static final int MARK_OF_DUTY_ID = 2633;
	private static final int MARK_OF_SEEKER_ID = 2673;
	private static final int MARK_OF_TRUST_ID = 2734;
	private static final int MARK_OF_DUELIST_ID = 2762;
	private static final int MARK_OF_SEARCHER_ID = 2809;
	private static final int MARK_OF_HEALER_ID = 2820;
	private static final int MARK_OF_LIFE_ID = 3140;
	private static final int MARK_OF_CHAMPION_ID = 3276;
	private static final int MARK_OF_SAGITTARIUS_ID = 3293;
	private static final int MARK_OF_WITCHCRAFT_ID = 3307;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Hannavalt/";
	
	public Hannavalt()
	{
		super(-1, "Hannavalt");
		
		addStartNpc(GRAND_MASTER_HANNAVALT, GRAND_MASTER_BLACKBIRD, GRAND_MASTER_SIRIA, GRAND_MASTER_SEDRICK, GRAND_MASTER_MARCUS);
		addTalkId(GRAND_MASTER_HANNAVALT, GRAND_MASTER_BLACKBIRD, GRAND_MASTER_SIRIA, GRAND_MASTER_SEDRICK, GRAND_MASTER_MARCUS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ELF_KNIGHT:
				return HTML_PATCH + "7109-01.htm";
			case KNIGHT:
				return HTML_PATCH + "7109-08.htm";
			case ROGUE:
				return HTML_PATCH + "7109-15.htm";
			case SCOUT:
				return HTML_PATCH + "7109-22.htm";
			case WARRIOR:
				return HTML_PATCH + "7109-29.htm";
			case ELF_FIGHTER:
			case HUMAN_FIGHTER:
				return HTML_PATCH + "7109-76.htm";
			case TEMPLE_KNIGHT:
			case PLAINS_WALKER:
			case SWORD_SINGER:
			case SILVER_RANGER:
			case WARLORD:
			case PALADIN:
			case TREASURE_HUNTER:
			case GLADIATOR:
			case DARK_AVENGER:
			case HAWKEYE:
				return HTML_PATCH + "7109-77.htm";
			default:
				return HTML_PATCH + "7109-78.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		switch (event)
		{
			case "7109-01.htm":
			case "7109-02.htm":
			case "7109-03.htm":
			case "7109-04.htm":
			case "7109-05.htm":
			case "7109-06.htm":
			case "7109-07.htm":
			case "7109-08.htm":
			case "7109-09.htm":
			case "7109-10.htm":
			case "7109-11.htm":
			case "7109-12.htm":
			case "7109-13.htm":
			case "7109-14.htm":
			case "7109-15.htm":
			case "7109-16.htm":
			case "7109-17.htm":
			case "7109-18.htm":
			case "7109-19.htm":
			case "7109-20.htm":
			case "7109-21.htm":
			case "7109-22.htm":
			case "7109-23.htm":
			case "7109-24.htm":
			case "7109-25.htm":
			case "7109-26.htm":
			case "7109-27.htm":
			case "7109-28.htm":
			case "7109-29.htm":
			case "7109-30.htm":
			case "7109-31.htm":
			case "7109-32.htm":
			case "7109-33.htm":
			case "7109-34.htm":
			case "7109-35.htm":
				return HTML_PATCH + event;
			
			case "class_change_20":
				if (player.getClassId() == ClassId.ELF_KNIGHT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_DUTY_ID, MARK_OF_LIFE_ID, MARK_OF_HEALER_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-36.htm";
						}
						return HTML_PATCH + "7109-37.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-38.htm";
					}
					
					st.takeItems(MARK_OF_DUTY_ID, MARK_OF_LIFE_ID, MARK_OF_HEALER_ID);
					st.getPlayer().setClassId(20);
					st.getPlayer().setBaseClass(20);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-39.htm";
				}
				break;
			
			case "class_change_21":
				if (player.getClassId() == ClassId.ELF_KNIGHT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_CHALLENGER_ID, MARK_OF_LIFE_ID, MARK_OF_DUELIST_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-40.htm";
						}
						return HTML_PATCH + "7109-41.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-42.htm";
					}
					
					st.takeItems(MARK_OF_CHALLENGER_ID, MARK_OF_LIFE_ID, MARK_OF_DUELIST_ID);
					st.getPlayer().setClassId(21);
					st.getPlayer().setBaseClass(21);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-43.htm";
				}
				break;
			
			case "class_change_5":
				if (player.getClassId() == ClassId.KNIGHT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_DUTY_ID, MARK_OF_TRUST_ID, MARK_OF_HEALER_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-44.htm";
						}
						return HTML_PATCH + "7109-45.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-46.htm";
					}
					
					st.takeItems(MARK_OF_DUTY_ID, MARK_OF_TRUST_ID, MARK_OF_HEALER_ID);
					st.getPlayer().setClassId(5);
					st.getPlayer().setBaseClass(5);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-47.htm";
				}
				break;
			
			case "class_change_6":
				if (player.getClassId() == ClassId.KNIGHT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_DUTY_ID, MARK_OF_TRUST_ID, MARK_OF_WITCHCRAFT_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-48.htm";
						}
						return HTML_PATCH + "7109-49.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-50.htm";
					}
					
					st.takeItems(MARK_OF_DUTY_ID, 1);
					st.takeItems(MARK_OF_TRUST_ID, 1);
					st.takeItems(MARK_OF_WITCHCRAFT_ID, 1);
					st.getPlayer().setClassId(6);
					st.getPlayer().setBaseClass(6);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-51.htm";
				}
				break;
			
			case "class_change_8":
				if (player.getClassId() == ClassId.ROGUE)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEEKER_ID, MARK_OF_TRUST_ID, MARK_OF_SEARCHER_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-52.htm";
						}
						return HTML_PATCH + "7109-53.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-54.htm";
					}
					
					st.takeItems(MARK_OF_SEEKER_ID, MARK_OF_TRUST_ID, MARK_OF_SEARCHER_ID);
					st.getPlayer().setClassId(8);
					st.getPlayer().setBaseClass(8);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-55.htm";
				}
				break;
			
			case "class_change_9":
				if (player.getClassId() == ClassId.ROGUE)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEEKER_ID, MARK_OF_TRUST_ID, MARK_OF_SAGITTARIUS_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-56.htm";
						}
						return HTML_PATCH + "7109-57.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-58.htm";
					}
					
					st.takeItems(MARK_OF_SEEKER_ID, MARK_OF_TRUST_ID, MARK_OF_SAGITTARIUS_ID);
					st.getPlayer().setClassId(9);
					st.getPlayer().setBaseClass(9);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-59.htm";
				}
				break;
			
			case "class_change_23":
				if (player.getClassId() == ClassId.SCOUT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEEKER_ID, MARK_OF_LIFE_ID, MARK_OF_SEARCHER_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-60.htm";
						}
						return HTML_PATCH + "7109-61.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-62.htm";
					}
					
					st.takeItems(MARK_OF_SEEKER_ID, MARK_OF_LIFE_ID, MARK_OF_SEARCHER_ID);
					st.getPlayer().setClassId(23);
					st.getPlayer().setBaseClass(23);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-63.htm";
				}
				break;
			
			case "class_change_24":
				if (player.getClassId() == ClassId.SCOUT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEEKER_ID, MARK_OF_LIFE_ID, MARK_OF_SAGITTARIUS_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-64.htm";
						}
						return HTML_PATCH + "7109-65.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-66.htm";
					}
					st.takeItems(MARK_OF_SEEKER_ID, MARK_OF_LIFE_ID, MARK_OF_SAGITTARIUS_ID);
					st.getPlayer().setClassId(24);
					st.getPlayer().setBaseClass(24);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-67.htm";
				}
				break;
			
			case "class_change_2":
				if (player.getClassId() == ClassId.WARRIOR)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_CHALLENGER_ID, MARK_OF_TRUST_ID, MARK_OF_DUELIST_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-68.htm";
						}
						return HTML_PATCH + "7109-69.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-70.htm";
					}
					
					st.takeItems(MARK_OF_CHALLENGER_ID, MARK_OF_TRUST_ID, MARK_OF_DUELIST_ID);
					st.getPlayer().setClassId(2);
					st.getPlayer().setBaseClass(2);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-71.htm";
				}
				break;
			
			case "class_change_3":
				if (player.getClassId() == ClassId.WARRIOR)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_CHALLENGER_ID, MARK_OF_TRUST_ID, MARK_OF_CHAMPION_ID);
					
					if (player.getLevel() <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7109-72.htm";
						}
						return HTML_PATCH + "7109-73.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7109-74.htm";
					}
					
					st.takeItems(MARK_OF_CHALLENGER_ID, MARK_OF_TRUST_ID, MARK_OF_CHAMPION_ID);
					st.getPlayer().setClassId(3);
					st.getPlayer().setBaseClass(3);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7109-75.htm";
				}
				break;
		}
		return getNoQuestMsg();
		
	}
}
