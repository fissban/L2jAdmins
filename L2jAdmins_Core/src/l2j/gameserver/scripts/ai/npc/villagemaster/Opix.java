package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author CaFI
 */
public class Opix extends Script
{
	// NPC
	private static final int HEAD_BLACKSMITH_OPIX = 7595;
	// ITEM
	private static final int PASS_FINAL_ID = 1635;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Opix/";
	
	public Opix()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(HEAD_BLACKSMITH_OPIX);
		addTalkId(HEAD_BLACKSMITH_OPIX);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DWARF_FIGHTER:
				return HTML_PATCH + "7595-01.htm";
			case SCAVENGER:
			case ARTISAN:
				return HTML_PATCH + "7595-09.htm";
			case BOUNTY_HUNTER:
			case WARSMITH:
				return HTML_PATCH + "7595-10.htm";
			default:
				return HTML_PATCH + "7595-11.htm";
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
			case "7595-01.htm":
			case "7595-02.htm":
			case "7595-03.htm":
			case "7595-04.htm":
				return HTML_PATCH + event;
			case "class_change_45":
				if (player.getClassId() == ClassId.DWARF_FIGHTER)
				{
					boolean checkMarks = !st.hasItems(PASS_FINAL_ID);
					int level = player.getLevel();
					
					if (level <= 19)
					{
						if (checkMarks)
						{
							return HTML_PATCH + "7595-05.htm";
						}
						return HTML_PATCH + "7595-06.htm";
					}
					if (checkMarks)
					{
						return HTML_PATCH + "7595-07.htm";
					}
					
					st.takeItems(PASS_FINAL_ID);
					player.setClassId(56);
					player.setBaseClass(56);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7595-08.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
