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
public class Sylvain extends Script
{
	// NPC
	private static final int HIGH_PRIEST_SYLVAIN = 7070;
	// ITEM's
	private static final int MARK_OF_FAITH_ID = 1201;
	private static final int ETERNITY_DIAMOND_ID = 1230;
	private static final int LEAF_OF_ORACLE_ID = 1235;
	private static final int BEAD_OF_SEASON_ID = 1292;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Sylvain/";
	
	public Sylvain()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PRIEST_SYLVAIN);
		addTalkId(HIGH_PRIEST_SYLVAIN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case HUMAN_MAGE:
				return HTML_PATCH + "7070-08.htm";
			case ELF_MAGE:
				return HTML_PATCH + "7070-01.htm";
			case WIZARD:
			case CLERIC:
			case ELF_WIZARD:
			case ORACLE:
				return HTML_PATCH + "7070-31.htm";
			case SORCERER:
			case NECROMANCER:
			case BISHOP:
			case WARLOCK:
			case PROPHET:
			case SPELLSINGER:
			case ELDER:
			case ELEMENTAL_SUMMONER:
				return HTML_PATCH + "7070-32.htm";
			default:
				return HTML_PATCH + "7070-33.htm";
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
			case "7070-01.htm":
			case "7070-02.htm":
			case "7070-03.htm":
			case "7070-04.htm":
			case "7070-05.htm":
			case "7070-06.htm":
			case "7070-07.htm":
			case "7070-08.htm":
			case "7070-09.htm":
			case "7070-10.htm":
			case "7070-11.htm":
			case "7070-12.htm":
			case "7070-13.htm":
			case "7070-14.htm":
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
							return HTML_PATCH + "7070-15.htm";
						}
						return HTML_PATCH + "7070-16.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7070-17.htm";
					}
					
					st.takeItems(ETERNITY_DIAMOND_ID);
					player.setClassId(26);
					player.setBaseClass(26);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7070-18.htm";
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
							return HTML_PATCH + "7070-19.htm";
						}
						return HTML_PATCH + "7070-20.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7070-21.htm";
					}
					
					st.takeItems(LEAF_OF_ORACLE_ID);
					player.setClassId(29);
					player.setBaseClass(29);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7070-22.htm";
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
							return HTML_PATCH + "7070-23.htm";
						}
						return HTML_PATCH + "7070-24.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7070-25.htm";
					}
					
					st.takeItems(BEAD_OF_SEASON_ID);
					player.setClassId(11);
					player.setBaseClass(11);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7070-26.htm";
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
							return HTML_PATCH + "7070-27.htm";
						}
						return HTML_PATCH + "7070-28.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7070-29.htm";
					}
					
					st.takeItems(MARK_OF_FAITH_ID);
					player.setClassId(15);
					player.setBaseClass(15);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7070-30.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
