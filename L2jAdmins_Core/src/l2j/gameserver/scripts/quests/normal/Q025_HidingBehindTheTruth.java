package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q025_HidingBehindTheTruth extends Script
{
	// NPCs
	private static final int AGRIPEL = 8348;
	private static final int BENEDICT = 8349;
	private static final int MYSTERIOUS_WIZARD = 8522;
	private static final int TOMBSTONE = 8531;
	private static final int MAID_OF_LIDIA = 8532;
	private static final int BOOK_SHELF = 8533;
	private static final int BOOK_SHELF2 = 8534;
	private static final int BOOK_SHELF3 = 8535;
	private static final int COFFIN = 8536;
	// QUEST MONSTER
	private static final int TRIOLS_PAWN = 5218;
	// ITEMs
	private static final int MAP_OF_FOREST_OF_DEAD = 7063;
	private static final int CONTRACT = 7066;
	private static final int LIDIAS_DRESS = 7155;
	private static final int SUSPICIOUS_TOTEM = 7156;
	private static final int GEMSTONE_KEY = 7157;
	private static final int SUSPICIOUS_TOTEM_DOLL = 7158;
	// REWARDs
	private static final int EARRING_OF_BLESSING = 874;
	private static final int RING_OF_BLESSING = 905;
	private static final int NECKLACE_OF_BLESSING = 936;
	
	public Q025_HidingBehindTheTruth()
	{
		super(25, "Hiding Behind the Truth");
		addStartNpc(BENEDICT);
		addTalkId(BENEDICT, AGRIPEL, MYSTERIOUS_WIZARD, BOOK_SHELF, BOOK_SHELF2, BOOK_SHELF3, MAID_OF_LIDIA, TOMBSTONE, COFFIN);
		addKillId(TRIOLS_PAWN);
		registerItems(SUSPICIOUS_TOTEM, GEMSTONE_KEY, MAP_OF_FOREST_OF_DEAD, SUSPICIOUS_TOTEM_DOLL, CONTRACT, LIDIAS_DRESS);
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
		
		// BENEDICT
		if (event.equalsIgnoreCase("8349-05.htm"))
		{
			htmltext = "";
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8349-10.htm"))
		{
			st.setCond(4, true);
		}
		else if (event.equalsIgnoreCase("8348-02.htm"))
		{
			st.takeItems(SUSPICIOUS_TOTEM);
		}
		else if (event.equalsIgnoreCase("8348-07.htm"))
		{
			st.setCond(5, true);
			st.giveItems(GEMSTONE_KEY, 1);
		}
		else if (event.equalsIgnoreCase("8348-10.htm"))
		{
			st.takeItems(SUSPICIOUS_TOTEM_DOLL);
		}
		else if (event.equalsIgnoreCase("8348-15.htm"))
		{
			st.setCond(17, true);
		}
		else if (event.equalsIgnoreCase("8348-16.htm"))
		{
			st.setCond(18, true);
		}
		else if (event.equalsIgnoreCase("8522-04.htm"))
		{
			st.setCond(6, true);
			st.giveItems(MAP_OF_FOREST_OF_DEAD, 1);
		}
		else if (event.equalsIgnoreCase("8522-12.htm"))
		{
			st.setCond(16, true);
		}
		else if (event.equalsIgnoreCase("8522-15.htm"))
		{
			st.rewardItems(NECKLACE_OF_BLESSING, 1);
			st.rewardItems(EARRING_OF_BLESSING, 1);
			st.rewardExpAndSp(1600000, 0);
			st.exitQuest(false, true);
		}
		// BOOK_SHELF3
		else if (event.equalsIgnoreCase("8535-03.htm"))
		{
			st.setCond(7, true);
			st.playSound(PlaySoundType.QUEST_BEFORE_BATTLE);
			L2Npc triol = addSpawn(TRIOLS_PAWN, 59650, -47613, -2712, 0, false, 120000);
			triol.broadcastNpcSay("That box was sealed by my master. Don't touch it!");
			triol.setTarget(player);
		}
		else if (event.equalsIgnoreCase("8535-05.htm"))
		{
			st.setCond(9, true);
			st.takeItems(GEMSTONE_KEY);
			st.giveItems(CONTRACT, 1);
		}
		// MAID_OF_LIDIA
		else if (event.equalsIgnoreCase("8532-02.htm"))
		{
			st.takeItems(CONTRACT);
		}
		else if (event.equalsIgnoreCase("8532-06.htm"))
		{
			st.setCond(11, true);
		}
		else if (event.equalsIgnoreCase("8532-17.htm"))
		{
			st.setCond(15, true);
		}
		else if (event.equalsIgnoreCase("8532-20.htm"))
		{
			st.rewardItems(RING_OF_BLESSING, 2);
			st.rewardItems(EARRING_OF_BLESSING, 1);
			st.rewardExpAndSp(1500000, 0);
			st.exitQuest(false, true);
		}
		// TOMBSTONE
		else if (event.equalsIgnoreCase("8531-02.htm"))
		{
			st.setCond(12, true);
			addSpawn(COFFIN, 60150, -35866, -675, npc.getHeading(), false, 120000);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q024_InhabitantsOfTheForestOfTheDead");
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = st2.isCompleted() && (player.getLevel() >= 66) ? "8349-02.htm" : "8349-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case BENEDICT:
						if (cond == 1)
						{
							if (st.hasItems(SUSPICIOUS_TOTEM))
							{
								htmltext = "8349-05.htm";
							}
							else
							{
								htmltext = "8349-04.htm";
								st.setCond(2, true);
							}
						}
						else if (cond == 2)
						{
							htmltext = "8349-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8349-05.htm";
						}
						break;
					case MYSTERIOUS_WIZARD:
						if (cond == 2)
						{
							htmltext = "8522-01.htm";
							st.setCond(3, true);
							st.giveItems(SUSPICIOUS_TOTEM, 1);
						}
						else if (cond == 3)
						{
							htmltext = "8522-02.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8522-03.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8522-04.htm";
						}
						else if (cond == 9)
						{
							st.setCond(10, true);
							htmltext = "8522-05.htm";
						}
						else if (cond == 15)
						{
							htmltext = "8522-06.htm";
						}
						else if (cond == 16)
						{
							htmltext = "8522-13.htm";
						}
						else if (cond == 17)
						{
							htmltext = "8522-16.htm";
						}
						else if (cond == 18)
						{
							htmltext = "8522-14.htm";
						}
						break;
					case AGRIPEL:
						if (cond == 4)
						{
							htmltext = st.hasItems(SUSPICIOUS_TOTEM) ? "8348-01.htm" : "8348-03.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8348-08.htm";
						}
						else if (cond == 16)
						{
							htmltext = st.hasItems(SUSPICIOUS_TOTEM_DOLL) ? "8348-09.htm" : "8348-10.htm";
						}
						break;
					case BOOK_SHELF:
					case BOOK_SHELF2:
					case BOOK_SHELF3:
						if (cond == 6)
						{
							htmltext = npc.getId() + "-01.htm";
						}
						else if ((cond == 7) && (npc.getId() == BOOK_SHELF3))
						{
							htmltext = "8535-02.htm";
						}
						else if ((cond == 8) && (npc.getId() == BOOK_SHELF3))
						{
							htmltext = st.hasItems(GEMSTONE_KEY) ? "8535-04.htm" : "8535-05.htm";
						}
						else if ((cond == 9) && (npc.getId() == BOOK_SHELF3))
						{
							htmltext = "8535-06.htm";
						}
						break;
					case MAID_OF_LIDIA:
						if ((cond == 9) || (cond == 10))
						{
							htmltext = st.hasItems(CONTRACT) ? "8532-01.htm" : "8532-02.htm";
						}
						else if (cond == 11)
						{
							htmltext = "8532-06.htm";
						}
						else if (cond == 13)
						{
							htmltext = "8532-07.htm";
							st.setCond(14, true);
							st.takeItems(LIDIAS_DRESS);
						}
						else if (cond == 14)
						{
							htmltext = "8532-08.htm";
						}
						else if ((cond == 15) || (cond == 18))
						{
							htmltext = "8532-18.htm";
						}
						else if (cond == 17)
						{
							htmltext = "8532-19.htm";
						}
						break;
					case TOMBSTONE:
						if (cond == 11)
						{
							htmltext = "8531-01.htm";
						}
						else if (cond == 12)
						{
							htmltext = "8531-02.htm";
						}
						else if (cond == 13)
						{
							htmltext = "8531-03.htm";
						}
						break;
					case COFFIN:
						if (cond == 12)
						{
							htmltext = "8536-01.htm";
							st.setCond(13, true);
							st.giveItems(LIDIAS_DRESS, 1);
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
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "7");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case TRIOLS_PAWN:
				if (st.dropItemsAlways(SUSPICIOUS_TOTEM_DOLL, 1, 1))
				{
					npc.broadcastNpcSay("You've ended my immortal life! You've protected by the feudal lord, aren't you?");
					st.setCond(8);
				}
				break;
		}
		return null;
	}
}
