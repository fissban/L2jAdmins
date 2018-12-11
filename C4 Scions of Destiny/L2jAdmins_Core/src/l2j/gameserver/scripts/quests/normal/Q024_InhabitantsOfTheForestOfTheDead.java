package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author Reynald0
 */
public class Q024_InhabitantsOfTheForestOfTheDead extends Script
{
	// NPCs
	private static final int DORIAN = 8389;
	private static final int MYSTERIOUS_WIZARD = 8522;
	private static final int TOMBSTONE = 8531;
	private static final int MAID_OF_LIDIA = 8532;
	// ITEMs
	private static final int LIDIA_LETTER = 7065;
	private static final int LIDIA_HAIRPIN = 7148;
	private static final int SUSPICIOUS_TOTEM_DOLL = 7151;
	private static final int FLOWER_BOUQUET = 7152;
	private static final int SILVER_CROSS_OF_EINHASAD = 7153;
	private static final int BROKEN_SILVER_CROSS = 7154;
	// MOBs
	private static final int BONE_SNATCHER = 1557;
	private static final int BONE_SHAPER = 1560;
	private static final int BONE_COLLECTOR = 1563;
	private static final int SKULL_COLLECTOR = 1564;
	private static final int BONE_ANIMATOR = 1565;
	private static final int SKULL_ANIMATOR = 1566;
	private static final int BONE_SLAYER = 1567;
	private static final int DORIAN_VAMPIRE = 10332;
	// REWARD
	private static final int SUSPICIOUS_TOTEM_DOLL_DOLL_2 = 7156;
	
	public Q024_InhabitantsOfTheForestOfTheDead()
	{
		super(24, "Inhabitants of the Forest of the Dead");
		addStartNpc(DORIAN);
		addSpawnId(DORIAN_VAMPIRE);
		addTalkId(DORIAN, TOMBSTONE, MAID_OF_LIDIA, MYSTERIOUS_WIZARD);
		addKillId(BONE_SNATCHER, BONE_SHAPER, BONE_COLLECTOR, BONE_ANIMATOR, BONE_SLAYER, SKULL_COLLECTOR, SKULL_ANIMATOR, DORIAN_VAMPIRE);
		registerItems(FLOWER_BOUQUET, SILVER_CROSS_OF_EINHASAD, BROKEN_SILVER_CROSS, LIDIA_LETTER, LIDIA_HAIRPIN, SUSPICIOUS_TOTEM_DOLL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = null;
		String htmltext = event;
		
		if (player == null)
		{
			// DORIAN_VAMPIRE
			if (event.equals("dorian"))
			{
				if ((npc == null) || npc.isDead())
				{
					cancelTimers("dorian");
				}
				else
				{
					for (L2PcInstance plr : npc.getKnownList().getObjectTypeInRadius(L2PcInstance.class, 300))
					{
						st = plr.getScriptState(getName());
						if (st == null)
						{
							continue;
						}
						
						if (st.getCond() == 3)
						{
							st.setCond(4, true);
							st.takeItems(SILVER_CROSS_OF_EINHASAD);
							st.giveItems(BROKEN_SILVER_CROSS, 1);
							npc.broadcastNpcSay("That sign!");
						}
					}
				}
			}
		}
		else
		{
			st = player.getScriptState(getName());
			if (st == null)
			{
				return htmltext;
			}
			
			// DORIAN
			if (event.equalsIgnoreCase("8389-03.htm"))
			{
				st.startQuest();
				st.giveItems(FLOWER_BOUQUET, 1);
			}
			else if (event.equalsIgnoreCase("8389-11.htm"))
			{
				st.setCond(3, true);
				st.giveItems(SILVER_CROSS_OF_EINHASAD, 1);
			}
			else if (event.equalsIgnoreCase("8389-16.htm"))
			{
				st.playSound("InterfaceSound.charstat_open_01");
				st.takeItems(BROKEN_SILVER_CROSS);
			}
			else if (event.equalsIgnoreCase("8389-17.htm"))
			{
				st.setCond(5, true);
				st.giveItems(LIDIA_HAIRPIN, 1);
			}
			// TOMBSTONE
			else if (event.equalsIgnoreCase("8531-02.htm"))
			{
				st.setCond(2, true);
				st.takeItems(FLOWER_BOUQUET);
			}
			// MAID_OF_LIDIA
			else if (event.equalsIgnoreCase("8532-04.htm"))
			{
				st.setCond(6, true);
				st.giveItems(LIDIA_LETTER, 1);
			}
			else if (event.equalsIgnoreCase("8532-06.htm"))
			{
				if (!st.hasItems(LIDIA_HAIRPIN))
				{
					htmltext = "8532-17.htm";
					st.setCond(7, true);
				}
			}
			else if (event.equalsIgnoreCase("8532-16.htm"))
			{
				st.setCond(9, true);
				st.takeItems(LIDIA_HAIRPIN);
			}
			// MYSTERIOUS_WIZARD
			else if (event.equalsIgnoreCase("8522-03.htm"))
			{
				st.takeItems(SUSPICIOUS_TOTEM_DOLL);
			}
			else if (event.equalsIgnoreCase("8522-07.htm"))
			{
				st.setCond(11, true);
			}
			else if (event.equalsIgnoreCase("8522-17.htm"))
			{
				st.takeItems(LIDIA_LETTER);
			}
			else if (event.equalsIgnoreCase("8522-19.htm"))
			{
				st.rewardItems(SUSPICIOUS_TOTEM_DOLL_DOLL_2, 1);
				st.exitQuest(false, true);
				player.sendMessage("Congratulations! You are completed this quest! The quest Hiding Behind the Truth become available.");
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		ScriptState st2 = player.getScriptState("Q023_LidiasHeart");
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = st2.isCompleted() && (player.getLevel() >= 65) ? "8389-02.htm" : "8389-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case DORIAN:
						if (cond == 1)
						{
							htmltext = "8389-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8389-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8389-12.htm";
						}
						else if (cond == 4)
						{
							htmltext = st.hasItems(BROKEN_SILVER_CROSS) ? "8389-13.htm" : "8389-16.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8389-18.htm";
						}
						else if (cond == 7)
						{
							if (!st.hasItems(LIDIA_HAIRPIN))
							{
								st.giveItems(LIDIA_HAIRPIN, 1);
								st.setCond(8, true);
							}
							
							htmltext = "8389-20.htm";
						}
						break;
					case TOMBSTONE:
						if (cond == 1)
						{
							htmltext = "8531-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8531-03.htm";
						}
						break;
					case MAID_OF_LIDIA:
						if (cond == 5)
						{
							htmltext = "8532-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8532-05.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8532-17.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8532-06.htm";
						}
						if (cond == 9)
						{
							htmltext = "8532-16.htm";
						}
						break;
					case MYSTERIOUS_WIZARD:
						if (cond == 10)
						{
							htmltext = st.hasItems(SUSPICIOUS_TOTEM_DOLL) ? "8522-01.htm" : "8522-03.htm";
						}
						else if (cond == 11)
						{
							htmltext = "8522-08.htm";
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
		ScriptState st = killer.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		int cond = st.getCond();
		switch (npc.getId())
		{
			case BONE_SNATCHER:
			case BONE_SHAPER:
			case BONE_COLLECTOR:
			case BONE_ANIMATOR:
			case BONE_SLAYER:
			case SKULL_COLLECTOR:
			case SKULL_ANIMATOR:
				if (cond == 9)
				{
					if (st.dropItems(SUSPICIOUS_TOTEM_DOLL, 1, 1, 300000))
					{
						st.setCond(10);
					}
				}
				break;
			case DORIAN_VAMPIRE:
				cancelTimers("dorian");
				break;
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getId())
		{
			case DORIAN_VAMPIRE:
				startTimer("dorian", 3000, npc, null, true);
				break;
		}
		return null;
	}
}
