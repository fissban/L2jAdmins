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
public class Tronix extends Script
{
	// NPC
	private static final int GRAND_MASTER_TRONIX = 7462;
	// ITEM's
	private static final int GAZE_OF_ABYSS_ID = 1244;
	private static final int IRON_HEART_ID = 1252;
	private static final int JEWEL_OF_DARKNESS_ID = 1261;
	private static final int ORB_OF_ABYSS_ID = 1270;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Tronix/";
	
	public Tronix()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_TRONIX);
		addTalkId(GRAND_MASTER_TRONIX);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DARK_ELF_FIGHTER:
				return HTML_PATCH + "7462-01.htm";
			case DARK_ELF_MAGE:
				return HTML_PATCH + "7462-08.htm";
			case PALUS_KNIGHT:
			case ASSASSIN:
			case DARK_ELF_WIZARD:
			case SHILLIEN_ORACLE:
				return HTML_PATCH + "7462-31.htm";
			case SHILLIEN_KNIGHT:
			case ABYSS_WALKER:
			case BLADE_DANCER:
			case PHANTOM_RANGER:
			case SPELLHOWLER:
			case SHILLIEN_ELDER:
			case PHANTOM_SUMMONER:
				return HTML_PATCH + "7462-32.htm";
			default:
				return HTML_PATCH + "7462-33.htm";
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
			case "7462-01.htm":
			case "7462-02.htm":
			case "7462-03.htm":
			case "7462-04.htm":
			case "7462-05.htm":
			case "7462-06.htm":
			case "7462-07.htm":
			case "7462-08.htm":
			case "7462-09.htm":
			case "7462-10.htm":
			case "7462-11.htm":
			case "7462-12.htm":
			case "7462-13.htm":
			case "7462-14.htm":
				return HTML_PATCH + event;
			case "class_change_32":
				if (player.getClassId() == ClassId.DARK_ELF_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(GAZE_OF_ABYSS_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7462-15.htm";
						}
						return HTML_PATCH + "7462-16.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7462-17.htm";
					}
					
					st.takeItems(GAZE_OF_ABYSS_ID);
					player.setClassId(32);
					player.setBaseClass(32);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7462-18.htm";
				}
				break;
			
			case "class_change_35":
				if (player.getClassId() == ClassId.DARK_ELF_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(IRON_HEART_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7462-19.htm";
						}
						return HTML_PATCH + "7462-20.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7462-21.htm";
					}
					
					st.takeItems(IRON_HEART_ID);
					player.setClassId(35);
					player.setBaseClass(35);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7462-22.htm";
				}
				break;
			
			case "class_change_39":
				if (player.getClassId() == ClassId.DARK_ELF_MAGE)
				{
					boolean checkMarks = !st.hasItems(JEWEL_OF_DARKNESS_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7462-23.htm";
						}
						return HTML_PATCH + "7462-24.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7462-25.htm";
					}
					
					st.takeItems(JEWEL_OF_DARKNESS_ID);
					player.setClassId(39);
					player.setBaseClass(39);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7462-26.htm";
				}
				break;
			
			case "class_change_42":
				if (player.getClassId() == ClassId.DARK_ELF_MAGE)
				{
					boolean checkMarks = !st.hasItems(ORB_OF_ABYSS_ID);
					
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7462-27.htm";
						}
						return HTML_PATCH + "7462-28.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7462-29.htm";
					}
					
					st.takeItems(ORB_OF_ABYSS_ID);
					player.setClassId(42);
					player.setBaseClass(42);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7462-30.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
