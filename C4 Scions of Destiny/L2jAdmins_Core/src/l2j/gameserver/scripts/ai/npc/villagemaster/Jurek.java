package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author CaFi & fissban
 */
public class Jurek extends Script
{
	// NPC's
	private static final int GRAND_MAGISTER_JUREK = 7115;
	private static final int GRAND_MAGISTER_ARKENIAS = 7174;
	private static final int GRAND_MAGISTER_VALLERIA = 7176;
	private static final int GRAND_MAGISTER_SCRAIDE = 7694;
	private static final int GRAND_MAGISTER_DRIKIYAN = 7854;
	// ITEM's
	private static final int MARK_OF_SCHOLAR_ID = 2674;
	private static final int MARK_OF_TRUST_ID = 2734;
	private static final int MARK_OF_MAGUS_ID = 2840;
	private static final int MARK_OF_LIFE_ID = 3140;
	private static final int MARK_OF_WITCHCRFAT_ID = 3307;
	private static final int MARK_OF_SUMMONER_ID = 3336;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Jurek/";
	
	public Jurek()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MAGISTER_JUREK, GRAND_MAGISTER_ARKENIAS, GRAND_MAGISTER_VALLERIA, GRAND_MAGISTER_SCRAIDE, GRAND_MAGISTER_DRIKIYAN);
		addTalkId(GRAND_MAGISTER_JUREK, GRAND_MAGISTER_ARKENIAS, GRAND_MAGISTER_VALLERIA, GRAND_MAGISTER_SCRAIDE, GRAND_MAGISTER_DRIKIYAN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ELF_WIZARD:
				return HTML_PATCH + "7115-01.htm";
			case WIZARD:
				return HTML_PATCH + "7115-08.htm";
			case SORCERER:
			case NECROMANCER:
			case WARLOCK:
			case SPELLSINGER:
			case ELEMENTAL_SUMMONER:
			case WARRIOR:
			case KNIGHT:
			case ROGUE:
			case WARLORD:
			case PALADIN:
				return HTML_PATCH + "7115-39.htm";
			default:
				return HTML_PATCH + "7115-40.htm";
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
			case "7115-01.htm":
			case "7115-02.htm":
			case "7115-03.htm":
			case "7115-04.htm":
			case "7115-05.htm":
			case "7115-06.htm":
			case "7115-07.htm":
			case "7115-08.htm":
			case "7115-09.htm":
			case "7115-10.htm":
			case "7115-11.htm":
			case "7115-12.htm":
			case "7115-13.htm":
			case "7115-14.htm":
			case "7115-15.htm":
			case "7115-16.htm":
			case "7115-17.htm":
				return HTML_PATCH + event;
			
			case "class_change_27":
				if (player.getClassId() == ClassId.ELF_WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_LIFE_ID, MARK_OF_MAGUS_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7115-18.htm";
						}
						return HTML_PATCH + "7115-19.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7115-20.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_LIFE_ID, MARK_OF_MAGUS_ID);
					player.setClassId(27);
					player.setBaseClass(27);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7115-21.htm";
				}
				break;
			
			case "class_change_28":
				if (player.getClassId() == ClassId.ELF_WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_LIFE_ID, MARK_OF_SUMMONER_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7115-22.htm";
						}
						return HTML_PATCH + "7115-23.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7115-24.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_LIFE_ID, MARK_OF_SUMMONER_ID);
					player.setClassId(28);
					player.setBaseClass(28);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7115-25.htm";
				}
				break;
			
			case "class_change_12":
				if (player.getClassId() == ClassId.WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_MAGUS_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7115-26.htm";
						}
						return HTML_PATCH + "7115-27.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7115-28.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_MAGUS_ID);
					player.setClassId(12);
					player.setBaseClass(12);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7115-29.htm";
				}
				break;
			
			case "class_change_13":
				if (player.getClassId() == ClassId.WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_WITCHCRFAT_ID);
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7115-30.htm";
						}
						return HTML_PATCH + "7115-31.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7115-32.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_WITCHCRFAT_ID);
					player.setClassId(13);
					player.setBaseClass(13);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7115-33.htm";
				}
				break;
			
			case "class_change_14":
				if (player.getClassId() == ClassId.WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_SUMMONER_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7115-34.htm";
						}
						return HTML_PATCH + "7115-35.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7115-36.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_TRUST_ID, MARK_OF_SUMMONER_ID);
					player.setClassId(14);
					player.setBaseClass(14);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7115-37.htm";
				}
				break;
		}
		return getNoQuestMsg();
	}
}
