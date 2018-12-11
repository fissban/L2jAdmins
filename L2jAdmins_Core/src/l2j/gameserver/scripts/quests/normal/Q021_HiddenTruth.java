package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.util.Util;

/**
 * @author Reynald0
 */
public class Q021_HiddenTruth extends Script
{
	// NPCs
	private static final int INNOCENTIN = 8328;
	private static final int AGRIPEL = 8348;
	private static final int BENEDICT = 8349;
	private static final int DOMINIC = 8350;
	private static final int MYSTERIOUS_WIZARD = 8522;
	private static final int TOMBSTONE = 8523;
	private static final int GHOST_VON_HELLMAN = 8524;
	private static final int GHOST2_VON_HELLMAN_PAGE = 8525;
	private static final int BROKEN_BOOKSHELF = 8526;
	// ITEM
	private static final int CROSS_EINHASHAD1 = 7140;
	// REWARD
	private static final int CROSS_EINASHAD2 = 7141;
	
	private static final LocationHolder[] ROUTES =
	{
		new LocationHolder(52197, -54205, -3156),
		new LocationHolder(52130, -52116, -3085),
		new LocationHolder(52198, -51331, -3098),
	};
	
	public Q021_HiddenTruth()
	{
		super(21, "Hidden Truth");
		addStartNpc(MYSTERIOUS_WIZARD);
		addFirstTalkId(GHOST2_VON_HELLMAN_PAGE);
		addTalkId(MYSTERIOUS_WIZARD, TOMBSTONE, GHOST_VON_HELLMAN, BROKEN_BOOKSHELF, AGRIPEL, BENEDICT, DOMINIC, DOMINIC, INNOCENTIN);
		registerItems(CROSS_EINHASHAD1);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		// MYSTERIOUS_WIZARD
		if (event.equalsIgnoreCase("8522-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8523-03.htm"))
		{
			st.setCond(2, true);
			L2Npc von = addSpawn(GHOST_VON_HELLMAN, 51432, -54570, -3136, 0, false, 600000);
			von.broadcastNpcSay("Who awoke me?");
			st.playSound("SkillSound5.horror_02");
		}
		// GHOST_VON_HELLMAN
		else if (event.equalsIgnoreCase("8524-06.htm"))
		{
			st.setCond(3, true);
			L2Npc ghost = addSpawn(GHOST2_VON_HELLMAN_PAGE, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 300000);
			startTimer("0", 1, ghost, player);
		}
		else if (Util.isDigit(event)) // ROUTES
		{
			LocationHolder loc = ROUTES[Integer.parseInt(event)];
			if (event.equalsIgnoreCase("0"))
			{
				npc.getAI().setIntention(CtrlIntentionType.MOVE_TO, loc);
				npc.broadcastNpcSay("My master has instructed me to be your guide, " + player.getName());
				startTimer("1", 7500, npc, player);
			}
			else if (event.equalsIgnoreCase("1"))
			{
				npc.getAI().setIntention(CtrlIntentionType.MOVE_TO, loc);
				startTimer("2", 17000, npc, player);
			}
			else if (event.equalsIgnoreCase("2"))
			{
				npc.getAI().setIntention(CtrlIntentionType.MOVE_TO, loc);
				startTimer("cond_4", 10000, null, player);
			}
			
			htmltext = "";
		}
		else if (event.equalsIgnoreCase("cond_4"))
		{
			htmltext = "";
			st.setCond(4, true);
		}
		// BROKEN_BOOKSHELF
		else if (event.equalsIgnoreCase("8526-03.htm"))
		{
			st.playSound("ItemSound.item_drop_equip_armor_cloth");
		}
		else if (event.equalsIgnoreCase("8526-08.htm"))
		{
			if (st.getCond() != 5)
			{
				st.setCond(5, true);
			}
		}
		else if (event.equalsIgnoreCase("8526-12.htm"))
		{
			st.setCond(6, true);
			st.giveItems(CROSS_EINHASHAD1, 1);
		}
		// INNOCENTIN
		else if (event.equalsIgnoreCase("8328-05.htm"))
		{
			st.rewardItems(CROSS_EINASHAD2, 1);
			st.exitQuest(false, true);
			player.sendMessage("Congratulations! You are completed this quest! The quest Tragedy In Von Hellmann Forest become available. Show Cross of Einhasad to High Priest Tifaren.");
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + "-02.htm";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() < 63) ? "8522-01.htm" : "8522-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case MYSTERIOUS_WIZARD:
						if (cond == 1)
						{
							htmltext = "8522-05.htm";
						}
						break;
					case TOMBSTONE:
						if (cond == 1)
						{
							htmltext = "8523-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8523-04.htm";
						}
						break;
					case GHOST_VON_HELLMAN:
						if (cond == 2)
						{
							htmltext = "8524-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8524-07.htm";
						}
						break;
					case BROKEN_BOOKSHELF:
						if (cond == 4)
						{
							htmltext = "8526-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8526-08.htm";
						}
						break;
					case AGRIPEL:
						if (cond == 6)
						{
							htmltext = "8348-01.htm";
						}
						break;
					case BENEDICT:
						if (cond == 6)
						{
							htmltext = "8349-02.htm";
						}
						break;
					case DOMINIC:
						if (cond == 6)
						{
							htmltext = "8350-03.htm";
							st.setCond(7, true);
						}
						break;
					case INNOCENTIN:
						if (cond == 7)
						{
							htmltext = "8328-01.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
}
