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
public class Angus extends Script
{
	// NPC's
	private static final int GRAND_MASTER_BRECSON = 7195;
	private static final int GRAND_MASTER_ANGUS = 7474;
	private static final int GRAND_MASTER_MEDOWN = 7699;
	private static final int GRAND_MASTER_OLTLIN = 7862;
	private static final int GRAND_MASTER_XAIRAKIN = 7910;
	private static final int GRAND_MAGISTER_FAIREN = 7175;
	// ITEM's
	private static final int MARK_OF_CHALLENGER_ID = 2627;
	private static final int MARK_OF_DUTY_ID = 2633;
	private static final int MARK_OF_SEEKER_ID = 2673;
	private static final int MARK_OF_SCHOLAR_ID = 2674;
	private static final int MARK_OF_PILGRIM_ID = 2721;
	private static final int MARK_OF_DUELIST_ID = 2762;
	private static final int MARK_OF_SEARCHER_ID = 2809;
	private static final int MARK_OF_REFORMER_ID = 2821;
	private static final int MARK_OF_MAGUS_ID = 2840;
	private static final int MARK_OF_FATE_ID = 3172;
	private static final int MARK_OF_SAGITTARIUS_ID = 3293;
	private static final int MARK_OF_WITCHCRAFT_ID = 3307;
	private static final int MARK_OF_SUMMONER_ID = 3336;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Angus/";
	
	public Angus()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(GRAND_MASTER_BRECSON, GRAND_MASTER_ANGUS, GRAND_MASTER_MEDOWN, GRAND_MASTER_OLTLIN, GRAND_MASTER_XAIRAKIN, GRAND_MAGISTER_FAIREN);
		addTalkId(GRAND_MASTER_BRECSON, GRAND_MASTER_ANGUS, GRAND_MASTER_MEDOWN, GRAND_MASTER_OLTLIN, GRAND_MASTER_XAIRAKIN, GRAND_MAGISTER_FAIREN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case PALUS_KNIGHT:
				return HTML_PATCH + "7474-01.htm";
			case SHILLIEN_ORACLE:
				return HTML_PATCH + "7474-08.htm";
			case ASSASSIN:
				return HTML_PATCH + "7474-12.htm";
			case DARK_ELF_WIZARD:
				return HTML_PATCH + "7474-19.htm";
			case SHILLIEN_KNIGHT:
			case ABYSS_WALKER:
			case BLADE_DANCER:
			case PHANTOM_RANGER:
			case SPELLHOWLER:
			case SHILLIEN_ELDER:
			case PHANTOM_SUMMONER:
				return HTML_PATCH + "7474-54.htm";
			case DARK_ELF_FIGHTER:
			case DARK_ELF_MAGE:
				return HTML_PATCH + "7474-55.htm";
			default:
				return HTML_PATCH + "7474-56.htm";
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
			case "7474-01.htm":
			case "7474-02.htm":
			case "7474-03.htm":
			case "7474-04.htm":
			case "7474-05.htm":
			case "7474-06.htm":
			case "7474-07.htm":
			case "7474-08.htm":
			case "7474-09.htm":
			case "7474-10.htm":
			case "7474-11.htm":
			case "7474-12.htm":
			case "7474-13.htm":
			case "7474-14.htm":
			case "7474-15.htm":
			case "7474-16.htm":
			case "7474-17.htm":
			case "7474-18.htm":
			case "7474-19.htm":
			case "7474-20.htm":
			case "7474-21.htm":
			case "7474-22.htm":
			case "7474-23.htm":
			case "7474-24.htm":
			case "7474-25.htm":
				return HTML_PATCH + event;
			
			case "class_change_33":
				if (player.getClassId() == ClassId.PALUS_KNIGHT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_DUTY_ID, MARK_OF_FATE_ID, MARK_OF_WITCHCRAFT_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-26.htm";
						}
						return HTML_PATCH + "7474-27.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-28.htm";
					}
					
					st.takeItems(MARK_OF_DUTY_ID, MARK_OF_FATE_ID, MARK_OF_WITCHCRAFT_ID);
					
					player.setClassId(33);
					player.setBaseClass(33);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-29.htm";
				}
				break;
			
			case "class_change_34":
				if (player.getClassId() == ClassId.PALUS_KNIGHT)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_CHALLENGER_ID, MARK_OF_FATE_ID, MARK_OF_DUELIST_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-30.htm";
						}
						return HTML_PATCH + "7474-31.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-32.htm";
					}
					
					st.takeItems(MARK_OF_CHALLENGER_ID, MARK_OF_FATE_ID, MARK_OF_DUELIST_ID);
					
					player.setClassId(34);
					player.setBaseClass(34);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-33.htm";
				}
				break;
			
			case "class_change_43":
				if (player.getClassId() == ClassId.SHILLIEN_ORACLE)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_PILGRIM_ID, MARK_OF_FATE_ID, MARK_OF_REFORMER_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-34.htm";
						}
						return HTML_PATCH + "7474-35.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-36.htm";
					}
					
					st.takeItems(MARK_OF_PILGRIM_ID, MARK_OF_FATE_ID, MARK_OF_REFORMER_ID);
					
					player.setClassId(43);
					player.setBaseClass(43);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-37.htm";
				}
				break;
			
			case "class_change_36":
				if (player.getClassId() == ClassId.ASSASSIN)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEEKER_ID, MARK_OF_FATE_ID, MARK_OF_SEARCHER_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-38.htm";
						}
						return HTML_PATCH + "7474-39.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-40.htm";
					}
					
					st.takeItems(MARK_OF_SEEKER_ID, MARK_OF_FATE_ID, MARK_OF_SEARCHER_ID);
					
					player.setClassId(36);
					player.setBaseClass(36);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-41.htm";
				}
				break;
			
			case "class_change_37":
				if (player.getClassId() == ClassId.ASSASSIN)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SEEKER_ID, MARK_OF_FATE_ID, MARK_OF_SAGITTARIUS_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-42.htm";
						}
						return HTML_PATCH + "7474-43.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-44.htm";
					}
					
					st.takeItems(MARK_OF_SEEKER_ID, MARK_OF_FATE_ID, MARK_OF_SAGITTARIUS_ID);
					player.setClassId(37);
					player.setBaseClass(37);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-45.htm";
				}
				break;
			
			case "class_change_40":
				if (player.getClassId() == ClassId.DARK_ELF_WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_FATE_ID, MARK_OF_MAGUS_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-46.htm";
						}
						return HTML_PATCH + "7474-47.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-48.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_FATE_ID, MARK_OF_MAGUS_ID);
					player.setClassId(40);
					player.setBaseClass(40);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-49.htm";
				}
				break;
			
			case "class_change_41":
				if (player.getClassId() == ClassId.DARK_ELF_WIZARD)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_SCHOLAR_ID, MARK_OF_FATE_ID, MARK_OF_SUMMONER_ID);
					
					int level = player.getLevel();
					
					if (level <= 39)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7474-50.htm";
						}
						return HTML_PATCH + "7474-51.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7474-52.htm";
					}
					
					st.takeItems(MARK_OF_SCHOLAR_ID, MARK_OF_FATE_ID, MARK_OF_SUMMONER_ID);
					player.setClassId(41);
					player.setBaseClass(41);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7474-53.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
