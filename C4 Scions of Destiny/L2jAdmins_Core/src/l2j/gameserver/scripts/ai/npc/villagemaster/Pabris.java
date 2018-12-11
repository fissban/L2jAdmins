package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author fissban
 */
public class Pabris extends Script
{
	// NPC
	private static final int GRAND_MASTER_PABRIS = 7066;
	// ITEM's
	private static final int MEDALLION_OF_WARRIOR_ID = 1145;
	private static final int SWORD_OF_RITUAL_ID = 1161;
	private static final int BEZIQUES_RECOMMENDATION_ID = 1190;
	private static final int ELVEN_KNIGHT_BROOCH_ID = 1204;
	private static final int REORIA_RECOMMENDATION_ID = 1217;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/pabris/";
	
	public Pabris()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_PABRIS);
		addTalkId(GRAND_MASTER_PABRIS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ELF_FIGHTER:
				return HTML_PATCH + "7066-01.htm";
			case HUMAN_FIGHTER:
				return HTML_PATCH + "7066-08.htm";
			case ELF_KNIGHT:
			case SCOUT:
			case WARRIOR:
			case KNIGHT:
			case ROGUE:
				return HTML_PATCH + "7066-38.htm";
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
				return HTML_PATCH + "7066-39.htm";
			default:
				return HTML_PATCH + "7066-40.htm";
			
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
			case "7066-01.htm":
			case "7066-02.htm":
			case "7066-03.htm":
			case "7066-04.htm":
			case "7066-05.htm":
			case "7066-06.htm":
			case "7066-07.htm":
			case "7066-08.htm":
			case "7066-09.htm":
			case "7066-10.htm":
			case "7066-11.htm":
			case "7066-12.htm":
			case "7066-13.htm":
			case "7066-14.htm":
			case "7066-15.htm":
			case "7066-16.htm":
			case "7066-17.htm":
				return HTML_PATCH + event;
			case "class_change_19":
				if (player.getClassId() == ClassId.ELF_FIGHTER)
				{
					int level = st.getPlayer().getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(ELVEN_KNIGHT_BROOCH_ID))
						{
							return HTML_PATCH + "7066-18.htm";
						}
						return HTML_PATCH + "7066-19.htm";
					}
					
					if (!st.hasItems(ELVEN_KNIGHT_BROOCH_ID))
					{
						return HTML_PATCH + "7066-20.htm";
					}
					
					st.takeItems(ELVEN_KNIGHT_BROOCH_ID);
					st.getPlayer().setClassId(19);
					st.getPlayer().setBaseClass(19);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7066-21.htm";
				}
				break;
			
			case "class_change_22":
				if (player.getClassId() == ClassId.ELF_FIGHTER)
				{
					int level = st.getPlayer().getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(REORIA_RECOMMENDATION_ID))
						{
							return HTML_PATCH + "7066-22.htm";
						}
						return HTML_PATCH + "7066-23.htm";
					}
					
					if (!st.hasItems(REORIA_RECOMMENDATION_ID))
					{
						return HTML_PATCH + "7066-24.htm";
					}
					
					st.takeItems(REORIA_RECOMMENDATION_ID);
					st.getPlayer().setClassId(22);
					st.getPlayer().setBaseClass(22);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7066-25.htm";
				}
				break;
			
			case "class_change_1":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					int level = st.getPlayer().getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(MEDALLION_OF_WARRIOR_ID))
						{
							return HTML_PATCH + "7066-26.htm";
						}
						return HTML_PATCH + "7066-27.htm";
					}
					
					if (!st.hasItems(MEDALLION_OF_WARRIOR_ID))
					{
						return HTML_PATCH + "7066-28.htm";
					}
					st.takeItems(MEDALLION_OF_WARRIOR_ID);
					st.getPlayer().setClassId(1);
					st.getPlayer().setBaseClass(1);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7066-29.htm";
				}
				break;
			
			case "class_change_4":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					int level = st.getPlayer().getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(SWORD_OF_RITUAL_ID))
						{
							return HTML_PATCH + "7066-30.htm";
						}
						return HTML_PATCH + "7066-31.htm";
					}
					
					if (!st.hasItems(SWORD_OF_RITUAL_ID))
					{
						return HTML_PATCH + "7066-32.htm";
					}
					
					st.takeItems(SWORD_OF_RITUAL_ID);
					st.getPlayer().setClassId(4);
					st.getPlayer().setBaseClass(4);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7066-33.htm";
				}
				break;
			
			case "class_change_7":
				if (player.getClassId() == ClassId.HUMAN_FIGHTER)
				{
					int level = st.getPlayer().getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(BEZIQUES_RECOMMENDATION_ID))
						{
							return HTML_PATCH + "7066-34.htm";
						}
						return HTML_PATCH + "7066-35.htm";
					}
					
					if (!st.hasItems(BEZIQUES_RECOMMENDATION_ID))
					{
						return HTML_PATCH + "7066-36.htm";
					}
					
					st.takeItems(BEZIQUES_RECOMMENDATION_ID);
					st.getPlayer().setClassId(7);
					st.getPlayer().setBaseClass(7);
					st.getPlayer().broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7066-37.htm";
				}
				break;
		}
		
		return getNoQuestMsg();
	}
}
