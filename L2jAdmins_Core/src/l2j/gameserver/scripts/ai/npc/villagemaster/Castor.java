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
public class Castor extends Script
{
	// NPC
	private static final int HIGH_PREFECT_CASTOR = 7508;
	// ITEM's
	private static final int MARK_OF_RAIDER_ID = 1592;
	private static final int KHAVATARI_TOTEM_ID = 1615;
	private static final int MASK_OF_MEDIUM_ID = 1631;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Castor/";
	
	public Castor()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PREFECT_CASTOR);
		addTalkId(HIGH_PREFECT_CASTOR);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ORC_FIGHTER:
				return HTML_PATCH + "7508-01.htm";
			case ORC_MAGE:
				return HTML_PATCH + "7508-06.htm";
			case RAIDER:
			case MONK:
			case SHAMAN:
				return HTML_PATCH + "7508-21.htm";
			case DESTROYER:
			case TYRANT:
			case OVERLORD:
			case WARCRYER:
				return HTML_PATCH + "7508-22.htm";
			default:
				return HTML_PATCH + "7508-23.htm";
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
			case "7508-01.htm":
			case "7508-02.htm":
			case "7508-03.htm":
			case "7508-04.htm":
			case "7508-05.htm":
			case "7508-06.htm":
			case "7508-07.htm":
			case "7508-08.htm":
				return HTML_PATCH + event;
			
			case "class_change_45":
				if (player.getClassId() == ClassId.ORC_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_RAIDER_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7508-09.htm";
						}
						return HTML_PATCH + "7508-10.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7508-11.htm";
					}
					
					st.takeItems(MARK_OF_RAIDER_ID);
					player.setClassId(45);
					player.setBaseClass(45);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7508-12.htm";
				}
				break;
			
			case "class_change_47":
				if (player.getClassId() == ClassId.ORC_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(KHAVATARI_TOTEM_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7508-13.htm";
						}
						return HTML_PATCH + "7508-14.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7508-15.htm";
					}
					
					st.takeItems(KHAVATARI_TOTEM_ID);
					player.setClassId(47);
					player.setBaseClass(47);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7508-16.htm";
				}
				break;
			
			case "class_change_50":
				if (player.getClassId() == ClassId.ORC_MAGE)
				{
					boolean checkMarks = !st.hasItems(MASK_OF_MEDIUM_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7508-17.htm";
						}
						return HTML_PATCH + "7508-18.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7508-19.htm";
					}
					
					st.takeItems(MASK_OF_MEDIUM_ID);
					player.setClassId(50);
					player.setBaseClass(50);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7508-20.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
