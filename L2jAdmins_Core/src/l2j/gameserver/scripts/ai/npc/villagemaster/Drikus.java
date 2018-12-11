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
public class Drikus extends Script
{
	// NPC
	private static final int HIGH_PREFECT_DRIKUS = 7505;
	// ITEM's
	private static final int MARK_OF_RAIDER_ID = 1592;
	private static final int KHAVATARI_TOTEM_ID = 1615;
	private static final int MASK_OF_MEDIUM_ID = 1631;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Drikus/";
	
	public Drikus()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PREFECT_DRIKUS);
		addTalkId(HIGH_PREFECT_DRIKUS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ORC_FIGHTER:
				return HTML_PATCH + "7505-01.htm";
			case ORC_MAGE:
				return HTML_PATCH + "7505-06.htm";
			case RAIDER:
			case MONK:
			case SHAMAN:
				return HTML_PATCH + "7505-21.htm";
			case DESTROYER:
			case TYRANT:
			case OVERLORD:
			case WARCRYER:
				return HTML_PATCH + "7505-22.htm";
			default:
				return HTML_PATCH + "7505-23.htm";
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
			case "7505-01.htm":
			case "7505-02.htm":
			case "7505-03.htm":
			case "7505-04.htm":
			case "7505-05.htm":
			case "7505-06.htm":
			case "7505-07.htm":
			case "7505-08.htm":
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
							return HTML_PATCH + "7505-09.htm";
						}
						return HTML_PATCH + "7505-10.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7505-11.htm";
					}
					
					st.takeItems(MARK_OF_RAIDER_ID);
					player.setClassId(45);
					player.setBaseClass(45);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7505-12.htm";
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
							return HTML_PATCH + "7505-13.htm";
						}
						return HTML_PATCH + "7505-14.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7505-15.htm";
					}
					
					st.takeItems(KHAVATARI_TOTEM_ID);
					player.setClassId(47);
					player.setBaseClass(47);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7505-16.htm";
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
							return HTML_PATCH + "7505-17.htm";
						}
						return HTML_PATCH + "7505-18.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7505-19.htm";
					}
					
					st.takeItems(MASK_OF_MEDIUM_ID);
					player.setClassId(50);
					player.setBaseClass(50);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7505-20.htm";
				}
				break;
		}
		
		return getNoQuestMsg();
	}
}
