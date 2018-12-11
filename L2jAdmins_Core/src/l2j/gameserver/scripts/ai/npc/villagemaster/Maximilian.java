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
public class Maximilian extends Script
{
	// NPC's
	private static final int HIGH_PRIEST_MAXIMILIAN = 7120;
	private static final int HIGH_PRIEST_HOLLINT = 7191;
	private static final int HIGH_PRIEST_ORVEN = 7857;
	private static final int HIGH_PRIEST_SQUILLARI = 7905;
	// ITEM's
	private static final int MARK_OF_PILGRIM_ID = 2721;
	private static final int MARK_OF_TRUST_ID = 2734;
	private static final int MARK_OF_HEALER_ID = 2820;
	private static final int MARK_OF_REFORMER_ID = 2821;
	private static final int MARK_OF_LIFE_ID = 3140;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Maximilian/";
	
	public Maximilian()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HIGH_PRIEST_MAXIMILIAN, HIGH_PRIEST_HOLLINT, HIGH_PRIEST_ORVEN, HIGH_PRIEST_SQUILLARI);
		addTalkId(HIGH_PRIEST_MAXIMILIAN, HIGH_PRIEST_HOLLINT, HIGH_PRIEST_ORVEN, HIGH_PRIEST_SQUILLARI);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ORACLE:
				return HTML_PATCH + "7120-01.htm";
			case CLERIC:
				return HTML_PATCH + "7120-05.htm";
			case ELDER:
			case BISHOP:
			case PROPHET:
				return HTML_PATCH + "7120-25.htm";
			case ELF_MAGE:
				return HTML_PATCH + "7120-24.htm";
			default:
				return HTML_PATCH + "7120-26.htm";
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
			case "7120-01.htm":
			case "7120-02.htm":
			case "7120-03.htm":
			case "7120-04.htm":
			case "7120-05.htm":
			case "7120-06.htm":
			case "7120-07.htm":
			case "7120-08.htm":
			case "7120-09.htm":
			case "7120-10.htm":
			case "7120-11.htm":
				return HTML_PATCH + event;
			
			case "class_change_30":
				if (player.getClassId() == ClassId.ORACLE)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_PILGRIM_ID, MARK_OF_LIFE_ID, MARK_OF_HEALER_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7120-12.htm";
						}
						return HTML_PATCH + "7120-13.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7120-14.htm";
					}
					
					st.takeItems(MARK_OF_PILGRIM_ID, MARK_OF_LIFE_ID, MARK_OF_HEALER_ID);
					player.setClassId(30);
					player.setBaseClass(30);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7120-15.htm";
				}
				break;
			
			case "class_change_16":
				if (player.getClassId() == ClassId.CLERIC)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_PILGRIM_ID, MARK_OF_TRUST_ID, MARK_OF_HEALER_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7120-16.htm";
						}
						return HTML_PATCH + "7120-17.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7120-18.htm";
					}
					
					st.takeItems(MARK_OF_PILGRIM_ID, MARK_OF_TRUST_ID, MARK_OF_HEALER_ID);
					player.setClassId(16);
					player.setBaseClass(16);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7120-19.htm";
				}
				break;
			
			case "class_change_17":
				if (player.getClassId() == ClassId.CLERIC)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_PILGRIM_ID, MARK_OF_TRUST_ID, MARK_OF_REFORMER_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7120-20.htm";
						}
						return HTML_PATCH + "7120-21.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7120-22.htm";
					}
					
					st.takeItems(MARK_OF_PILGRIM_ID, MARK_OF_TRUST_ID, MARK_OF_REFORMER_ID);
					player.setClassId(17);
					player.setBaseClass(17);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7120-23.htm";
				}
				break;
		}
		return getNoQuestMsg();
	}
}
