package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q615_MagicalPowerOfFire_Part1 extends Script
{
	// NPCs
	private static final int NARAN = 8378;
	private static final int UDAN = 8379;
	private static final int ASEFA_BOX = 8559;
	private static final int EYE = 8684;
	
	// Items
	private static final int THIEF_KEY = 1661;
	private static final int STOLEN_RED_TOTEM = 7242;
	private static final int RED_TOTEM = 7243;
	private static final int DIVINE_STONE = 7081;
	
	public Q615_MagicalPowerOfFire_Part1()
	{
		super(615, "Magical Power of Fire - Part 1");
		
		registerItems(STOLEN_RED_TOTEM);
		
		addStartNpc(NARAN);
		addTalkId(NARAN, UDAN, ASEFA_BOX);
		
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
		
		if (event.equalsIgnoreCase("8378-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("spawned", "0");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8559-03.htm"))
		{
			// You have been discovered ; quest is failed.
			if (st.getInt("spawned") == 1)
			{
				htmltext = "8559-04.htm";
			}
			else if (!st.hasItems(THIEF_KEY))
			{
				htmltext = "8559-02.htm";
			}
			else
			{
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(THIEF_KEY, 1);
				st.giveItems(STOLEN_RED_TOTEM, 1);
			}
		}
		else if (event.equalsIgnoreCase("UdanEyeDespawn"))
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
				htmltext = ((player.getLevel() >= 74) && (player.getAllianceWithVarkaKetra() <= -2)) ? "8378-01.htm" : "8378-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case NARAN:
						htmltext = "8378-04.htm";
						break;
					
					case UDAN:
						if (cond == 1)
						{
							htmltext = "8379-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 2)
						{
							if (st.getInt("spawned") == 0)
							{
								htmltext = "8379-02.htm";
							}
							else
							{
								htmltext = "8379-03.htm";
								st.set("spawned", "0");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						else if ((cond == 3) && st.hasItems(STOLEN_RED_TOTEM))
						{
							htmltext = "8379-04.htm";
							
							st.takeItems(STOLEN_RED_TOTEM, 1);
							st.giveItems(RED_TOTEM, 1);
							st.giveItems(DIVINE_STONE, 1);
							
							st.unset("spawned");
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case ASEFA_BOX:
						if (cond == 2)
						{
							htmltext = "8559-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8559-05.htm";
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
			
			// Spawn Udan's eye.
			L2Npc udanEye = addSpawn(EYE, player, true, 10000);
			if (udanEye != null)
			{
				startTimer("UdanEyeDespawn", 9000, udanEye, player, false);
				udanEye.broadcastNpcSay("You cannot escape Udan's Eye!");
				st.playSound(PlaySoundType.QUEST_GIVEUP);
			}
		}
		
		return null;
	}
	
}
