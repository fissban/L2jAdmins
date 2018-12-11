package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author CaFi
 */
public class Rains extends Script
{
	// NPC
	private static final int GRAND_MASTER_RAINS = 7288;
	// ITEM's
	private static final int MEDALLION_OF_WARRIOR_ID = 1145;
	private static final int SWORD_OF_RITUAL_ID = 1161;
	private static final int BEZIQUES_RECOMMENDATION_ID = 1190;
	private static final int ELVEN_KNIGHT_BROOCH_ID = 1204;
	private static final int REORIA_RECOMMENDATION_ID = 1217;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Rains/";
	
	public Rains()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_RAINS);
		addTalkId(GRAND_MASTER_RAINS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ELF_FIGHTER:
				return HTML_PATCH + "7288-01.htm";
			case HUMAN_FIGHTER:
				return HTML_PATCH + "7288-08.htm";
			case ELF_KNIGHT:
			case SCOUT:
			case WARRIOR:
			case KNIGHT:
			case ROGUE:
				return HTML_PATCH + "7288-38.htm";
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
				return HTML_PATCH + "7288-39.htm";
			default:
				return HTML_PATCH + "7288-40.htm";
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
			case "7288-01.htm":
			case "7288-02.htm":
			case "7288-03.htm":
			case "7288-04.htm":
			case "7288-05.htm":
			case "7288-06.htm":
			case "7288-07.htm":
			case "7288-08.htm":
			case "7288-09.htm":
			case "7288-10.htm":
			case "7288-11.htm":
			case "7288-12.htm":
			case "7288-13.htm":
			case "7288-14.htm":
			case "7288-15.htm":
			case "7288-16.htm":
			case "7288-17.htm":
				return HTML_PATCH + event;
			case "class_change_19":
				if (player.getClassId() == ClassId.ELF_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(ELVEN_KNIGHT_BROOCH_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7288-18.htm";
						}
						return HTML_PATCH + "7288-19.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7288-20.htm";
					}
					
					st.takeItems(ELVEN_KNIGHT_BROOCH_ID);
					player.setClassId(19);
					player.setBaseClass(19);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7288-21.htm";
				}
				break;
			
			case "class_change_22":
				if (player.getClassId() == ClassId.ELF_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(REORIA_RECOMMENDATION_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7288-22.htm";
						}
						return HTML_PATCH + "7288-23.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7288-24.htm";
					}
					
					st.takeItems(REORIA_RECOMMENDATION_ID);
					player.setClassId(22);
					player.setBaseClass(22);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7288-25.htm";
				}
				break;
			
			case "class_change_1":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(MEDALLION_OF_WARRIOR_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7288-26.htm";
						}
						return HTML_PATCH + "7288-27.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7288-28.htm";
					}
					
					st.takeItems(MEDALLION_OF_WARRIOR_ID);
					player.setClassId(1);
					player.setBaseClass(1);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7288-29.htm";
				}
				break;
			
			case "class_change_4":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(SWORD_OF_RITUAL_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7288-30.htm";
						}
						return HTML_PATCH + "7288-31.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7288-32.htm";
					}
					
					st.takeItems(SWORD_OF_RITUAL_ID);
					player.setClassId(4);
					player.setBaseClass(4);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7288-33.htm";
				}
				break;
			
			case "class_change_7":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(BEZIQUES_RECOMMENDATION_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7288-34.htm";
						}
						return HTML_PATCH + "7288-35.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7288-36.htm";
					}
					
					st.takeItems(BEZIQUES_RECOMMENDATION_ID);
					player.setClassId(7);
					player.setBaseClass(7);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7288-37.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
