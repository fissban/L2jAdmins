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
public class Raymond extends Script
{
	// NPC
	private static final int GRAND_MASTER_Raymond = 7289;
	// ITEM's
	private static final int MARK_OF_FAITH_ID = 1201;
	private static final int ETERNITY_DIAMOND_ID = 1230;
	private static final int LEAF_OF_ORACLE_ID = 1235;
	private static final int BEAD_OF_SEASON_ID = 1292;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Raymond/";
	
	public Raymond()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_Raymond);
		addTalkId(GRAND_MASTER_Raymond);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case HUMAN_MAGE:
				return HTML_PATCH + "7289-08.htm";
			case ELF_MAGE:
				return HTML_PATCH + "7289-01.htm";
			case WIZARD:
			case CLERIC:
			case ELF_WIZARD:
			case ORACLE:
				return HTML_PATCH + "7289-31.htm";
			case SORCERER:
			case NECROMANCER:
			case BISHOP:
			case WARLOCK:
			case PROPHET:
			case SPELLSINGER:
			case ELDER:
			case ELEMENTAL_SUMMONER:
				return HTML_PATCH + "7289-32.htm";
			default:
				return HTML_PATCH + "7289-33.htm";
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
			case "7289-01.htm":
			case "7289-02.htm":
			case "7289-03.htm":
			case "7289-04.htm":
			case "7289-05.htm":
			case "7289-06.htm":
			case "7289-07.htm":
			case "7289-08.htm":
			case "7289-09.htm":
			case "7289-10.htm":
			case "7289-11.htm":
			case "7289-12.htm":
			case "7289-13.htm":
			case "7289-14.htm":
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
							return HTML_PATCH + "7289-15.htm";
						}
						return HTML_PATCH + "7289-16.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7289-17.htm";
					}
					
					st.takeItems(ETERNITY_DIAMOND_ID);
					player.setClassId(26);
					player.setBaseClass(26);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					return HTML_PATCH + "7289-18.htm";
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
							return HTML_PATCH + "7289-19.htm";
						}
						return HTML_PATCH + "7289-20.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7289-21.htm";
					}
					
					st.takeItems(LEAF_OF_ORACLE_ID);
					player.setClassId(29);
					player.setBaseClass(29);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7289-22.htm";
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
							return HTML_PATCH + "7289-23.htm";
						}
						return HTML_PATCH + "7289-24.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7289-25.htm";
					}
					
					st.takeItems(BEAD_OF_SEASON_ID);
					player.setClassId(11);
					player.setBaseClass(11);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					return HTML_PATCH + "7289-26.htm";
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
							return HTML_PATCH + "7289-27.htm";
						}
						return HTML_PATCH + "7289-28.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7289-29.htm";
					}
					
					st.takeItems(MARK_OF_FAITH_ID);
					player.setClassId(15);
					player.setBaseClass(15);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7289-30.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
