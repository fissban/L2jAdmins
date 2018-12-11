package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @originalQuest aCis
 * @authors       MauroNOB, CaFi, zarie
 */
public class Q609_MagicalPowerOfWater_Part1 extends Script
{
	// NPCs
	private static final int WAHKAN = 8371;
	private static final int ASEFA = 8372;
	private static final int UDAN_BOX = 8561;
	private static final int EYE = 8685;
	
	// Items
	private static final int THIEF_KEY = 1661;
	private static final int STOLEN_GREEN_TOTEM = 7237;
	private static final int GREEN_TOTEM = 7238;
	private static final int DIVINE_STONE = 7081;
	
	public Q609_MagicalPowerOfWater_Part1()
	{
		super(609, "Magical Power of Water - Part 1");
		
		registerItems(STOLEN_GREEN_TOTEM);
		
		addStartNpc(WAHKAN);
		addTalkId(WAHKAN, ASEFA, UDAN_BOX);
		
		// IDs aggro ranges to avoid, else quest is automatically failed.
		addAggroRangeEnterId(1350, 1351, 1353, 1354, 1355, 1357, 1358, 1360, 1361, 1362, 1369, 1370, 1364, 1365, 1366, 1368, 1371, 1372, 1373, 1374, 1375);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8371-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("spawned", "0");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8561-03.htm"))
		{
			// You have been discovered ; quest is failed.
			if (st.getInt("spawned") == 1)
			{
				htmltext = "8561-04.htm";
			}
			else if (!st.hasItems(THIEF_KEY))
			{
				htmltext = "8561-02.htm";
			}
			else
			{
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(THIEF_KEY, 1);
				st.giveItems(STOLEN_GREEN_TOTEM, 1);
			}
		}
		else if (event.equalsIgnoreCase("AsefaEyeDespawn"))
		{
			npc.broadcastNpcSay("I'll be waiting for your return.");
			return null;
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
				htmltext = ((player.getLevel() >= 74) && (player.getAllianceWithVarkaKetra() >= 2)) ? "8371-01.htm" : "8371-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case WAHKAN:
						htmltext = "8371-04.htm";
						break;
					
					case ASEFA:
						if (cond == 1)
						{
							htmltext = "8372-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 2)
						{
							if (st.getInt("spawned") == 0)
							{
								htmltext = "8372-02.htm";
							}
							else
							{
								htmltext = "8372-03.htm";
								st.set("spawned", "0");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if ((cond == 3) && st.hasItems(STOLEN_GREEN_TOTEM))
						{
							htmltext = "8372-04.htm";
							
							st.takeItems(STOLEN_GREEN_TOTEM, 1);
							st.giveItems(GREEN_TOTEM, 1);
							st.giveItems(DIVINE_STONE, 1);
							
							st.unset("spawned");
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case UDAN_BOX:
						if (cond == 2)
						{
							htmltext = "8561-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8561-05.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return null;
		}
		
		if ((st.getInt("spawned") == 0) && (st.getInt("cond") == 2))
		{
			// Put "spawned" flag to 1 to avoid to spawn another.
			st.set("spawned", "1");
			
			// Spawn Asefa's eye.
			L2Npc asefaEye = addSpawn(EYE, player, true, 10000);
			if (asefaEye != null)
			{
				startTimer("AsefaEyeDespawn", 9000, asefaEye, player, false);
				asefaEye.broadcastNpcSay("You cannot escape Asefa's Eye!");
				st.playSound(PlaySoundType.QUEST_GIVEUP);
			}
		}
		
		return null;
	}
	
}
