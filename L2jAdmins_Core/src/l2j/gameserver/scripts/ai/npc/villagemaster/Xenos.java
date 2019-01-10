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
public class Xenos extends Script
{
	// Npc
	private static final int GRAND_MASTER_XENOS = 7290;
	// Item
	private static final int GAZE_OF_ABYSS_ID = 1244;
	private static final int IRON_HEART_ID = 1252;
	private static final int JEWEL_OF_DARKNESS_ID = 1261;
	private static final int ORB_OF_ABYSS_ID = 1270;
	// Html
	private static final String HTML_PATCH = "data/html/villageMaster/Xenos/";
	
	public Xenos()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_XENOS);
		addTalkId(GRAND_MASTER_XENOS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DARK_ELF_FIGHTER:
				return HTML_PATCH + "7290-01.htm";
			case DARK_ELF_MAGE:
				return HTML_PATCH + "7290-08.htm";
			case PALUS_KNIGHT:
			case ASSASSIN:
			case DARK_ELF_WIZARD:
			case SHILLIEN_ORACLE:
				return HTML_PATCH + "7290-31.htm";
			case SHILLIEN_KNIGHT:
			case ABYSS_WALKER:
			case BLADE_DANCER:
			case PHANTOM_RANGER:
			case SPELLHOWLER:
			case SHILLIEN_ELDER:
			case PHANTOM_SUMMONER:
				return HTML_PATCH + "7290-32.htm";
			default:
				return HTML_PATCH + "7290-33.htm";
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
			case "7290-01.htm":
			case "7290-02.htm":
			case "7290-03.htm":
			case "7290-04.htm":
			case "7290-05.htm":
			case "7290-06.htm":
			case "7290-07.htm":
			case "7290-08.htm":
			case "7290-09.htm":
			case "7290-10.htm":
			case "7290-11.htm":
			case "7290-12.htm":
			case "7290-13.htm":
			case "7290-14.htm":
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
							return HTML_PATCH + "7290-15.htm";
						}
						return HTML_PATCH + "7290-16.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7290-17.htm";
					}
					
					st.takeItems(GAZE_OF_ABYSS_ID);
					player.setClassId(32);
					player.setBaseClass(32);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7290-18.htm";
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
							return HTML_PATCH + "7290-19.htm";
						}
						return HTML_PATCH + "7290-20.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7290-21.htm";
					}
					
					st.takeItems(IRON_HEART_ID);
					player.setClassId(35);
					player.setBaseClass(35);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7290-22.htm";
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
							return HTML_PATCH + "7290-23.htm";
						}
						return HTML_PATCH + "7290-24.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7290-25.htm";
					}
					
					st.takeItems(JEWEL_OF_DARKNESS_ID);
					player.setClassId(39);
					player.setBaseClass(39);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7290-26.htm";
				}
				break;
			
			case "class_change_42":
				if (player.getClassId() == ClassId.DARK_ELF_MAGE)
				{
					int count = player.getInventory().getItemCount(ORB_OF_ABYSS_ID, -1);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (count == 0)
						{
							return HTML_PATCH + "7290-27.htm";
						}
						return HTML_PATCH + "7290-28.htm";
					}
					if (count == 0)
					{
						return HTML_PATCH + "7290-29.htm";
					}
					
					st.takeItems(ORB_OF_ABYSS_ID);
					player.setClassId(42);
					player.setBaseClass(42);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7290-30.htm";
				}
				break;
		}
		return getNoQuestMsg();
	}
}
