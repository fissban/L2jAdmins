package l2j.gameserver.scripts.ai.npc.villagemaster;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * Original code in python
 * @author CaFi & fissban
 */
public class Ranspo extends Script
{
	// NPC
	private static final int WAREHOUSE_CHIEF_RANSPO = 7594;
	// ITEM
	private static final int RING_OF_RAVEN_ID = 1642;
	// HTML
	private static final String HTML_PATCH = "data/html/villageMaster/Ranspo/";
	
	public Ranspo()
	{
		super(-1, "ai/npc/villagemaster");
		
		addStartNpc(WAREHOUSE_CHIEF_RANSPO);
		addTalkId(WAREHOUSE_CHIEF_RANSPO);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		switch (player.getClassId())
		{
			case DWARF_FIGHTER:
				return HTML_PATCH + "7594-01.htm";
			case SCAVENGER:
			case ARTISAN:
				return HTML_PATCH + "7594-09.htm";
			case BOUNTY_HUNTER:
			case WARSMITH:
				return HTML_PATCH + "7594-10.htm";
			default:
				return HTML_PATCH + "7594-11.htm";
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "7594-01.htm":
			case "7594-02.htm":
			case "7594-03.htm":
			case "7594-04.htm":
				return HTML_PATCH + event;
			case "class_change_54":
				if (player.getClassId() == ClassId.DWARF_FIGHTER)
				{
					ScriptState st = player.getScriptState(getName());
					if (st == null)
					{
						return null;
					}
					if (player.getLevel() <= 19)
					{
						if (!st.hasItems(RING_OF_RAVEN_ID))
						{
							return HTML_PATCH + "7594-05.htm";
						}
						return HTML_PATCH + "7594-06.htm";
					}
					if (!st.hasItems(RING_OF_RAVEN_ID))
					{
						return HTML_PATCH + "7594-07.htm";
					}
					
					st.takeItems(RING_OF_RAVEN_ID);
					player.setClassId(54);
					player.setBaseClass(54);
					player.broadcastUserInfo();
					st.playSound(PlaySoundType.QUEST_FANFARE_2);
					st.playSound(PlaySoundType.CHAR_CHANGE);
					return HTML_PATCH + "7594-08.htm";
				}
				break;
			
		}
		return getNoQuestMsg();
	}
}
