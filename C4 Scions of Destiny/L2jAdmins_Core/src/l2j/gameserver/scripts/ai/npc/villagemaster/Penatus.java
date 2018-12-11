package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original script in python
 * @author CaFi & fissban
 */
public class Penatus extends Script
{
	// NPC's
	private static final int HIGH_PREFECT_PENATUS = 7513;
	private static final int HIGH_PREFECT_KARIA = 7681;
	private static final int HIGH_PREFECT_GARVARENTZ = 7704;
	private static final int HIGH_PREFECT_LADANZA = 7865;
	private static final int HIGH_PREFECT_TUSHKU = 7913;
	// ITEM's
	private static final int MARK_OF_CHALLENGER_ID = 2627;
	private static final int MARK_OF_PILGRIM_ID = 2721;
	private static final int MARK_OF_DUELIST_ID = 2762;
	private static final int MARK_OF_WARSPIRIT_ID = 2879;
	private static final int MARK_OF_GLORY_ID = 3203;
	private static final int MARK_OF_CHAMPION_ID = 3276;
	private static final int MARK_OF_LORD_ID = 3390;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Penatus/";
	
	public Penatus()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PREFECT_PENATUS, HIGH_PREFECT_KARIA, HIGH_PREFECT_GARVARENTZ, HIGH_PREFECT_LADANZA, HIGH_PREFECT_TUSHKU);
		addTalkId(HIGH_PREFECT_PENATUS, HIGH_PREFECT_KARIA, HIGH_PREFECT_GARVARENTZ, HIGH_PREFECT_LADANZA, HIGH_PREFECT_TUSHKU);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case MONK:
				return HTML_PATCH + "7513-01.htm";
			case RAIDER:
				return HTML_PATCH + "7513-05.htm";
			case SHAMAN:
				return HTML_PATCH + "7513-09.htm";
			case DESTROYER:
			case TYRANT:
			case OVERLORD:
			case WARCRYER:
				return HTML_PATCH + "7513-32.htm";
			case ORC_FIGHTER:
			case ORC_MAGE:
				return HTML_PATCH + "7513-33.htm";
			default:
				return HTML_PATCH + "7513-34.htm";
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
			case "7513-01.htm":
			case "7513-02.htm":
			case "7513-03.htm":
			case "7513-04.htm":
			case "7513-05.htm":
			case "7513-06.htm":
			case "7513-07.htm":
			case "7513-08.htm":
			case "7513-09.htm":
			case "7513-10.htm":
			case "7513-11.htm":
			case "7513-12.htm":
			case "7513-13.htm":
			case "7513-14.htm":
			case "7513-15.htm":
				return HTML_PATCH + event;
			
			case "class_change_48":
				if (player.getClassId() == ClassId.MONK)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_CHALLENGER_ID, MARK_OF_GLORY_ID, MARK_OF_DUELIST_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7513-16.htm";
						}
						return HTML_PATCH + "7513-17.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7513-18.htm";
					}
					
					st.takeItems(MARK_OF_CHALLENGER_ID, MARK_OF_GLORY_ID, MARK_OF_DUELIST_ID);
					player.setClassId(48);
					player.setBaseClass(48);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7513-19.htm";
				}
				break;
			
			case "class_change_46":
				if (player.getClassId() == ClassId.RAIDER)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_CHALLENGER_ID, MARK_OF_GLORY_ID, MARK_OF_CHAMPION_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7513-20.htm";
						}
						return HTML_PATCH + "7513-21.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7513-22.htm";
					}
					
					st.takeItems(MARK_OF_CHALLENGER_ID, MARK_OF_GLORY_ID, MARK_OF_CHAMPION_ID);
					player.setClassId(46);
					player.setBaseClass(46);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7513-23.htm";
				}
				break;
			
			case "class_change_51":
				if (player.getClassId() == ClassId.SHAMAN)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_PILGRIM_ID, MARK_OF_GLORY_ID, MARK_OF_LORD_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7513-24.htm";
						}
						return HTML_PATCH + "7513-25.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7513-26.htm";
					}
					
					st.takeItems(MARK_OF_PILGRIM_ID, MARK_OF_GLORY_ID, MARK_OF_LORD_ID);
					player.setClassId(51);
					player.setBaseClass(51);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7513-27.htm";
				}
				break;
			
			case "class_change_52":
				if (player.getClassId() == ClassId.SHAMAN)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_PILGRIM_ID, MARK_OF_GLORY_ID, MARK_OF_WARSPIRIT_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7513-28.htm";
						}
						return HTML_PATCH + "7513-29.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7513-30.htm";
					}
					
					st.takeItems(MARK_OF_PILGRIM_ID, MARK_OF_GLORY_ID, MARK_OF_WARSPIRIT_ID);
					player.setClassId(52);
					player.setBaseClass(52);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7513-31.htm";
				}
				break;
		}
		return getNoQuestMsg();
	}
}
