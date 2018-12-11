package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author fissban, zarie
 */
public class Levian extends Script
{
	// NPC
	private static final int HIGH_PRIESTESS_LEVIAN = 7037;
	// ITEM's
	private static final int MARK_OF_FAITH_ID = 1201;
	private static final int ETERNITY_DIAMOND_ID = 1230;
	private static final int LEAF_OF_ORACLE_ID = 1235;
	private static final int BEAD_OF_SEASON_ID = 1292;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Levian/";
	
	public Levian()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PRIESTESS_LEVIAN);
		addTalkId(HIGH_PRIESTESS_LEVIAN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case HUMAN_MAGE:
				return HTML_PATCH + "7037-08.htm";
			case ELF_MAGE:
				return HTML_PATCH + "7037-01.htm";
			default:
				return HTML_PATCH + "7037-33.htm";
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
			case "7037-01.htm":
			case "7037-02.htm":
			case "7037-03.htm":
			case "7037-04.htm":
			case "7037-05.htm":
			case "7037-06.htm":
			case "7037-07.htm":
			case "7037-08.htm":
			case "7037-09.htm":
			case "7037-10.htm":
			case "7037-11.htm":
			case "7037-12.htm":
			case "7037-13.htm":
			case "7037-14.htm":
				return HTML_PATCH + event;
			
			case "class_change_26":
				if (player.getClassId() == ClassId.ELF_MAGE)
				{
					boolean checkMarks = !st.hasItems(ETERNITY_DIAMOND_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7037-15.htm";
						}
						return HTML_PATCH + "7037-16.htm";
					}
					
					if (checkMarks)
					{
						return HTML_PATCH + "7037-17.htm";
					}
					st.takeItems(ETERNITY_DIAMOND_ID, 1);
					st.getPlayer().setClassId(26);
					st.getPlayer().setBaseClass(26);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7037-18.htm";
				}
				break;
			
			case "class_change_29":
				if (player.getClassId() == ClassId.ELF_MAGE)
				{
					boolean checkMarks = !st.hasItems(LEAF_OF_ORACLE_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7037-19.htm";
						}
						return HTML_PATCH + "7037-20.htm";
					}
					
					if (checkMarks)
					{
						return HTML_PATCH + "7037-21.htm";
					}
					
					st.takeItems(LEAF_OF_ORACLE_ID, 1);
					st.getPlayer().setClassId(29);
					st.getPlayer().setBaseClass(29);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7037-22.htm";
				}
				break;
			
			case "class_change_11":
				if (player.getClassId() == ClassId.HUMAN_MAGE)
				{
					boolean checkMarks = !st.hasItems(BEAD_OF_SEASON_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7037-23.htm";
						}
						return HTML_PATCH + "7037-24.htm";
					}
					
					if (checkMarks)
					{
						return HTML_PATCH + "7037-25.htm";
					}
					st.takeItems(BEAD_OF_SEASON_ID, 1);
					st.getPlayer().setClassId(11);
					st.getPlayer().setBaseClass(11);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7037-26.htm";
				}
				break;
			
			case "class_change_15":
				if (player.getClassId() == ClassId.HUMAN_MAGE)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_FAITH_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7037-27.htm";
						}
						return HTML_PATCH + "7037-28.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7037-29.htm";
					}
					st.takeItems(MARK_OF_FAITH_ID, 1);
					st.getPlayer().setClassId(15);
					st.getPlayer().setBaseClass(15);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7037-30.htm";
				}
				break;
		}
		return getNoQuestMsg();
	}
}
