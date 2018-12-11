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
public class Tapoy extends Script
{
	// NPC
	private static final int HEAD_BLACKSMITH_TAPOY = 7499;
	// ITEM
	private static final int PASS_FINAL_ID = 1635;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Tapoy/";
	
	public Tapoy()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HEAD_BLACKSMITH_TAPOY);
		addTalkId(HEAD_BLACKSMITH_TAPOY);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DWARF_FIGHTER:
				return HTML_PATCH + "7499-01.htm";
			case SCAVENGER:
			case ARTISAN:
				return HTML_PATCH + "7499-09.htm";
			case BOUNTY_HUNTER:
			case WARSMITH:
				return HTML_PATCH + "7499-10.htm";
			default:
				return HTML_PATCH + "7499-11.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7499-01.htm":
			case "7499-02.htm":
			case "7499-03.htm":
			case "7499-04.htm":
				return HTML_PATCH + event;
			
			case "class_change_56":
				if (player.getClassId() == ClassId.DWARF_FIGHTER)
				{
					ScriptState st = player.getScriptState(getName());
					if (st == null)
					{
						return null;
					}
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (!st.hasItems(PASS_FINAL_ID))
						{
							return HTML_PATCH + "7499-05.htm";
						}
						return HTML_PATCH + "7499-06.htm";
					}
					if (!st.hasItems(PASS_FINAL_ID))
					{
						return HTML_PATCH + "7499-07.htm";
					}
					
					st.takeItems(PASS_FINAL_ID);
					player.setClassId(56);
					player.setBaseClass(56);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7499-08.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
