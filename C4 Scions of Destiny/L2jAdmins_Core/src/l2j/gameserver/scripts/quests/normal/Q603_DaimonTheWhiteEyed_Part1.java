package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

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
public class Q603_DaimonTheWhiteEyed_Part1 extends Script
{
	// Items
	private static final int EVIL_SPIRIT_BEADS = 7190;
	private static final int BROKEN_CRYSTAL = 7191;
	private static final int UNFINISHED_SUMMON_CRYSTAL = 7192;
	
	// NPCs
	private static final int EYE_OF_ARGOS = 8683;
	private static final int MYSTERIOUS_TABLET_1 = 8548;
	private static final int MYSTERIOUS_TABLET_2 = 8549;
	private static final int MYSTERIOUS_TABLET_3 = 8550;
	private static final int MYSTERIOUS_TABLET_4 = 8551;
	private static final int MYSTERIOUS_TABLET_5 = 8552;
	
	// Monsters
	private static final int CANYON_BANDERSNATCH_SLAVE = 1297;
	private static final int BUFFALO_SLAVE = 1299;
	private static final int GRENDEL_SLAVE = 1304;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(CANYON_BANDERSNATCH_SLAVE, 500000);
		CHANCES.put(BUFFALO_SLAVE, 519000);
		CHANCES.put(GRENDEL_SLAVE, 673000);
	}
	
	public Q603_DaimonTheWhiteEyed_Part1()
	{
		super(603, "Daimon the White-Eyed - Part 1");
		
		registerItems(EVIL_SPIRIT_BEADS, BROKEN_CRYSTAL);
		
		addStartNpc(EYE_OF_ARGOS);
		addTalkId(EYE_OF_ARGOS, MYSTERIOUS_TABLET_1, MYSTERIOUS_TABLET_2, MYSTERIOUS_TABLET_3, MYSTERIOUS_TABLET_4, MYSTERIOUS_TABLET_5);
		
		addKillId(BUFFALO_SLAVE, GRENDEL_SLAVE, CANYON_BANDERSNATCH_SLAVE);
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
		
		// Eye of Argos
		if (event.equalsIgnoreCase("8683-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8683-06.htm"))
		{
			if (st.getItemsCount(BROKEN_CRYSTAL) > 4)
			{
				st.set("cond", "7");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(BROKEN_CRYSTAL, -1);
			}
			else
			{
				htmltext = "8683-07.htm";
			}
		}
		else if (event.equalsIgnoreCase("8683-10.htm"))
		{
			if (st.getItemsCount(EVIL_SPIRIT_BEADS) > 199)
			{
				st.takeItems(EVIL_SPIRIT_BEADS, -1);
				st.giveItems(UNFINISHED_SUMMON_CRYSTAL, 1);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "7");
				htmltext = "8683-11.htm";
			}
		}
		// Mysterious tablets
		else if (event.equalsIgnoreCase("8548-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BROKEN_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("8549-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BROKEN_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("8550-02.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BROKEN_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("8551-02.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BROKEN_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("8552-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BROKEN_CRYSTAL, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() < 73) ? "8683-02.htm" : "8683-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case EYE_OF_ARGOS:
						if (cond < 6)
						{
							htmltext = "8683-04.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8683-05.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8683-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8683-09.htm";
						}
						break;
					
					case MYSTERIOUS_TABLET_1:
						if (cond == 1)
						{
							htmltext = "8548-01.htm";
						}
						else
						{
							htmltext = "8548-03.htm";
						}
						break;
					
					case MYSTERIOUS_TABLET_2:
						if (cond == 2)
						{
							htmltext = "8549-01.htm";
						}
						else if (cond > 2)
						{
							htmltext = "8549-03.htm";
						}
						break;
					
					case MYSTERIOUS_TABLET_3:
						if (cond == 3)
						{
							htmltext = "8550-01.htm";
						}
						else if (cond > 3)
						{
							htmltext = "8550-03.htm";
						}
						break;
					
					case MYSTERIOUS_TABLET_4:
						if (cond == 4)
						{
							htmltext = "8551-01.htm";
						}
						else if (cond > 4)
						{
							htmltext = "8551-03.htm";
						}
						break;
					
					case MYSTERIOUS_TABLET_5:
						if (cond == 5)
						{
							htmltext = "8552-01.htm";
						}
						else if (cond > 5)
						{
							htmltext = "8552-03.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "7");
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		if (st.dropItems(EVIL_SPIRIT_BEADS, 1, 200, CHANCES.get(npc.getId())))
		{
			st.set("cond", "8");
		}
		
		return null;
	}
	
}
