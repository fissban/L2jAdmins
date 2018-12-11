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
public class Kusto extends Script
{
	// NPC's
	private static final int HEAD_BLACKSMITH_KUSTO = 7512;
	private static final int HEAD_BLACKSMITH_FLUTTER = 7677;
	private static final int HEAD_BLACKSMITH_VERGARA = 7687;
	private static final int HEAD_BLACKSMITH_FERRIS = 7847;
	private static final int HEAD_BLACKSMITH_ROMAN = 7897;
	// ITEM's
	private static final int MARK_OF_MAESTRO_ID = 2867;
	private static final int MARK_OF_GUILDSMAN_ID = 3119;
	private static final int MARK_OF_PROSPERITY_ID = 3238;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Kusto/";
	
	public Kusto()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HEAD_BLACKSMITH_KUSTO, HEAD_BLACKSMITH_FLUTTER, HEAD_BLACKSMITH_VERGARA, HEAD_BLACKSMITH_FERRIS, HEAD_BLACKSMITH_ROMAN);
		addTalkId(HEAD_BLACKSMITH_KUSTO, HEAD_BLACKSMITH_FLUTTER, HEAD_BLACKSMITH_VERGARA, HEAD_BLACKSMITH_FERRIS, HEAD_BLACKSMITH_ROMAN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case ARTISAN:
				return HTML_PATCH + "7512-01.htm";
			case DWARF_FIGHTER:
				return HTML_PATCH + "7512-09.htm";
			case WARSMITH:
			case BOUNTY_HUNTER:
				return HTML_PATCH + "7512-10.htm";
			default:
				return HTML_PATCH + "7512-11.htm";
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
			case "7512-01.htm":
			case "7512-02.htm":
			case "7512-03.htm":
			case "7512-04.htm":
				return HTML_PATCH + event;
			
			case "class_change_57":
				if (player.getClassId() == ClassId.ARTISAN)
				{
					boolean checkMarks = !st.hasItems(MARK_OF_MAESTRO_ID, MARK_OF_GUILDSMAN_ID, MARK_OF_PROSPERITY_ID);
					
					int level = player.getLevel();
					
					if (level < 40)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7512-05.htm";
						}
						return HTML_PATCH + "7512-06.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7512-07.htm";
					}
					
					st.takeItems(MARK_OF_MAESTRO_ID, MARK_OF_GUILDSMAN_ID, MARK_OF_PROSPERITY_ID);
					player.setClassId(57);
					player.setBaseClass(57);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7512-08.htm";
				}
		}
		return getNoQuestMsg();
	}
}
