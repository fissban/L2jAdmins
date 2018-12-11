package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author Reynald0
 */
public class Q022_TragedyInVonHellmannForest extends Script
{
	// NPCs
	private static final int INNOCENTIN = 8328;
	private static final int TIFAREN = 8334;
	private static final int WELL = 8527;
	private static final int GHOST_OF_PRIEST = 8528;
	private static final int GHOST_OF_ADVENTURER = 8529;
	
	// MOBs
	private static final int TRAMPLED_MAN = 1553;
	private static final int SLAUGHTER_EXECUTIONER = 1555;
	private static final int SACRIFICED_MAN = 1561;
	private static final int SOUL_OF_WELL = 5217;
	
	// ITEMS
	private static final int CROSS_OF_EINHASAD = 7141;
	private static final int LOST_SKULL_OF_ELF = 7142;
	private static final int LETTER_OF_INNOCENTIN = 7143;
	private static final int JEWEL_OF_ADVENTURER = 7144;
	private static final int JEWEL_OF_ADVENTURER_2 = 7145;
	private static final int SEALED_REPORT_BOX = 7146;
	private static final int REPORT_BOX = 7147;
	
	public Q022_TragedyInVonHellmannForest()
	{
		super(22, "Tragedy in von Hellmann Forest");
		addStartNpc(TIFAREN);
		addTalkId(TIFAREN, INNOCENTIN, GHOST_OF_PRIEST, GHOST_OF_ADVENTURER, WELL);
		addAttackId(SOUL_OF_WELL);
		addKillId(SLAUGHTER_EXECUTIONER, TRAMPLED_MAN, SACRIFICED_MAN);
		registerItems(LOST_SKULL_OF_ELF, LETTER_OF_INNOCENTIN, JEWEL_OF_ADVENTURER, JEWEL_OF_ADVENTURER_2, SEALED_REPORT_BOX, REPORT_BOX);
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
		
		// TIFAREN
		if (event.equalsIgnoreCase("8334-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equalsIgnoreCase("8334-06.htm"))
		{
			if (!st.hasItems(CROSS_OF_EINHASAD))
			{
				htmltext = "8334-08.htm";
				st.setCond(2, true);
			}
			
		}
		else if (event.equalsIgnoreCase("8334-07.htm"))
		{
			st.setCond(4, true);
		}
		else if (event.equalsIgnoreCase("8334-13.htm"))
		{
			if (npc.isBusy())
			{
				htmltext = "8334-11.htm";
				st.setCond(6, true);
			}
			else
			{
				st.setCond(7, true);
				st.takeItems(LOST_SKULL_OF_ELF);
				L2Npc ghost = addSpawn(GHOST_OF_PRIEST, 38395, -49895, -1104, npc.getHeading(), false, 120000);
				ghost.broadcastNpcSay("Did you call me, " + player.getName() + " ?");
				startTimer("busy", 120000, npc, player);
				npc.setBusy(true);
				st.playSound("SkillSound5.horror_01");
			}
		}
		// BUSY TIMER
		else if (event.equalsIgnoreCase("busy"))
		{
			htmltext = "";
			npc.setBusy(false);
		}
		// GHOST_OF_PRIEST
		else if (event.equalsIgnoreCase("8528-09.htm"))
		{
			st.setCond(8, true);
		}
		else if (event.equalsIgnoreCase("8328-04.htm"))
		{
			st.takeItems(CROSS_OF_EINHASAD);
		}
		else if (event.equalsIgnoreCase("8328-10.htm"))
		{
			st.setCond(9, true);
			st.giveItems(LETTER_OF_INNOCENTIN, 1);
		}
		else if (event.equalsIgnoreCase("8328-13.htm"))
		{
			st.setCond(15, true);
			st.takeItems(REPORT_BOX);
		}
		else if (event.equalsIgnoreCase("8328-21.htm"))
		{
			st.setCond(16, true);
		}
		else if (event.equalsIgnoreCase("8529-02.htm"))
		{
			st.takeItems(LETTER_OF_INNOCENTIN);
		}
		else if (event.equalsIgnoreCase("8529-10.htm"))
		{
			st.setCond(10, true);
			st.giveItems(JEWEL_OF_ADVENTURER, 1);
		}
		// WELL
		else if (event.equalsIgnoreCase("8527-02.htm"))
		{
			L2Npc soulWell = addSpawn(SOUL_OF_WELL, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 120000);
			soulWell.setTarget(player);
			soulWell.getAI().setIntention(CtrlIntentionType.ATTACK, player);
			st.playSound("SkillSound5.horror_01");
		}
		return htmltext;
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
				htmltext = player.getLevel() < 63 ? "8334-01.htm" : "8334-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case TIFAREN:
						if ((cond == 1) || (cond == 3))
						{
							htmltext = "8334-05.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8334-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8334-10.htm";
						}
						else if (cond == 6)
						{
							htmltext = npc.isBusy() ? "8334-11.htm" : "8334-10.htm";
						}
						break;
					case INNOCENTIN:
						if (cond == 2)
						{
							if (st.hasItems(CROSS_OF_EINHASAD))
							{
								htmltext = "8328-02.htm";
								st.setCond(3, true);
							}
							else
							{
								htmltext = "8328-01.htm";
							}
						}
						else if (cond == 3)
						{
							htmltext = "8328-02.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8328-03.htm";
						}
						else if (cond == 9)
						{
							htmltext = "8328-11.htm";
						}
						else if (cond == 14)
						{
							htmltext = "8328-12.htm";
						}
						else if (cond == 15)
						{
							htmltext = "8328-14.htm";
						}
						else if (cond == 16)
						{
							htmltext = "8328-22.htm";
							st.exitQuest(false, true);
							player.sendMessage("Congratulations! You are completed this quest! The quest Lidia\'s Heart become available.");
						}
						break;
					case GHOST_OF_PRIEST:
						if ((cond == 5) || (cond == 6))
						{
							htmltext = "8528-02.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8528-01.htm";
						}
						break;
					case GHOST_OF_ADVENTURER:
						if (cond == 9)
						{
							htmltext = "8529-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "8529-10.htm";
						}
						else if (cond == 11)
						{
							htmltext = "8529-14.htm";
							st.setCond(12, true);
						}
						else if (cond == 13)
						{
							htmltext = "8529-15.htm";
							st.setCond(14, true);
							st.takeItems(JEWEL_OF_ADVENTURER_2);
							st.takeItems(SEALED_REPORT_BOX);
							st.giveItems(REPORT_BOX, 1);
						}
						else if (cond == 14)
						{
							htmltext = "8529-16.htm";
						}
						break;
					case WELL:
						if (cond == 10)
						{
							htmltext = "8527-01.htm";
						}
						else if (cond == 12)
						{
							htmltext = "8527-03.htm";
							st.setCond(13, true);
							st.giveItems(SEALED_REPORT_BOX, 1);
						}
						else if (cond == 13)
						{
							htmltext = "8527-04.htm";
						}
						else if (cond == 14)
						{
							htmltext = "8527-05.htm";
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
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		ScriptState st = checkPlayerState(attacker, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		int chance = Rnd.nextInt(200);
		int cond = st.getCond();
		switch (npc.getId())
		{
			case SOUL_OF_WELL:
				if (cond == 10)
				{
					if (chance <= 5)
					{
						st.setCond(11, true);
						st.takeItems(JEWEL_OF_ADVENTURER);
						st.giveItems(JEWEL_OF_ADVENTURER_2, 1);
					}
				}
				break;
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "4");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case SLAUGHTER_EXECUTIONER:
			case TRAMPLED_MAN:
			case SACRIFICED_MAN:
				if (st.dropItems(LOST_SKULL_OF_ELF, 1, 1, 300000))
				{
					st.setCond(5);
				}
				break;
		}
		return null;
	}
}
