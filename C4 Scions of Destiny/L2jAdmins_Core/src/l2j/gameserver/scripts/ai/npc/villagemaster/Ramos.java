package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author CaFi, fissban, zarie
 */
public class Ramos extends Script
{
	// NPC
	private static final int GRAND_MASTER_RAMOS = 7373;
	// ITEM's
	private static final int MEDALLION_OF_WARRIOR_ID = 1145;
	private static final int SWORD_OF_RITUAL_ID = 1161;
	private static final int BEZIQUES_RECOMMENDATION_ID = 1190;
	private static final int ELVEN_KNIGHT_BROOCH_ID = 1204;
	private static final int REORIA_RECOMMENDATION_ID = 1217;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Ramos/";
	
	public Ramos()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_RAMOS);
		addTalkId(GRAND_MASTER_RAMOS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ELF_FIGHTER:
				return HTML_PATCH + "7373-01.htm";
			case HUMAN_FIGHTER:
				return HTML_PATCH + "7373-08.htm";
			default:
				return HTML_PATCH + "7373-40.htm";
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
			case "7373-01.htm":
			case "7373-02.htm":
			case "7373-03.htm":
			case "7373-04.htm":
			case "7373-05.htm":
			case "7373-06.htm":
			case "7373-07.htm":
			case "7373-08.htm":
			case "7373-09.htm":
			case "7373-10.htm":
			case "7373-11.htm":
			case "7373-12.htm":
			case "7373-13.htm":
			case "7373-14.htm":
			case "7373-15.htm":
			case "7373-16.htm":
			case "7373-17.htm":
				return HTML_PATCH + event;
			
			case "class_change_19":
				if (player.getClassId() == ClassId.ELF_FIGHTER)
				{
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(ELVEN_KNIGHT_BROOCH_ID))
						{
							return HTML_PATCH + "7373-18.htm";
						}
						return HTML_PATCH + "7373-19.htm";
					}
					if (!st.hasItems(ELVEN_KNIGHT_BROOCH_ID))
					{
						return HTML_PATCH + "7373-20.htm";
					}
					
					st.takeItems(ELVEN_KNIGHT_BROOCH_ID);
					player.setClassId(19);
					player.setBaseClass(19);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7373-21.htm";
				}
				break;
			
			case "class_change_22":
				if (player.getClassId() == ClassId.ELF_FIGHTER)
				{
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(REORIA_RECOMMENDATION_ID))
						{
							return HTML_PATCH + "7373-22.htm";
						}
						return HTML_PATCH + "7373-23.htm";
					}
					if (!st.hasItems(REORIA_RECOMMENDATION_ID))
					{
						return HTML_PATCH + "7373-24.htm";
					}
					
					st.takeItems(REORIA_RECOMMENDATION_ID);
					player.setClassId(22);
					player.setBaseClass(22);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7373-25.htm";
				}
				break;
			
			case "class_change_1":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(MEDALLION_OF_WARRIOR_ID))
						{
							return HTML_PATCH + "7373-26.htm";
						}
						return HTML_PATCH + "7373-27.htm";
					}
					if (!st.hasItems(MEDALLION_OF_WARRIOR_ID))
					{
						return HTML_PATCH + "7373-28.htm";
					}
					
					st.takeItems(MEDALLION_OF_WARRIOR_ID);
					player.setClassId(1);
					player.setBaseClass(1);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7373-29.htm";
				}
				break;
			
			case "class_change_4":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(SWORD_OF_RITUAL_ID))
						{
							return HTML_PATCH + "7373-30.htm";
						}
						return HTML_PATCH + "7373-31.htm";
					}
					if (!st.hasItems(SWORD_OF_RITUAL_ID))
					{
						return HTML_PATCH + "7373-32.htm";
					}
					
					st.takeItems(SWORD_OF_RITUAL_ID);
					player.setClassId(4);
					player.setBaseClass(4);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7373-33.htm";
				}
				break;
			
			case "class_change_7":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(BEZIQUES_RECOMMENDATION_ID))
						{
							return HTML_PATCH + "7373-34.htm";
						}
						return HTML_PATCH + "7373-35.htm";
					}
					if (!st.hasItems(BEZIQUES_RECOMMENDATION_ID))
					{
						return HTML_PATCH + "7373-36.htm";
					}
					
					st.takeItems(BEZIQUES_RECOMMENDATION_ID);
					player.setClassId(7);
					player.setBaseClass(7);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7373-37.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
