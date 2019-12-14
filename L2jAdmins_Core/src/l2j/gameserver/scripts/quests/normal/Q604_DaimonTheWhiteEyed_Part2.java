package l2j.gameserver.scripts.quests.normal;

import java.util.ArrayList;
import java.util.List;

import l2j.Config;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

public class Q604_DaimonTheWhiteEyed_Part2 extends Script
{
	// Npcs
	private static final int EYE = 8683;
	private static final int ALTAR = 8541;
	// RaidBoss
	private static final int DAIMON = 10290;
	// Items
	private static final int U_SUMMON = 7192;
	private static final int S_SUMMON = 7193;
	private static final int ESSENCE = 7194;
	// Rewards dye +2int-2men/+2int-2wit/+2men-2int/+2men-2wit/+2wit-2int/+2wit-2men
	private static final int[] REWARDS = new int[]
	{
		4595,
		4596,
		4597,
		4598,
		4599,
		4560
	};
	
	public Q604_DaimonTheWhiteEyed_Part2()
	{
		super(604, "Daimon The White Eyed Part 2");
		addStartNpc(EYE);
		addTalkId(EYE);
		addTalkId(ALTAR);
		addKillId(DAIMON);
		registerItems(7193, 7194);
		
		String test = loadGlobalQuestVar("604_respawn");
		if (Util.isDigit(test))
		{
			long remain = Long.parseLong(test) - System.currentTimeMillis();
			if (remain <= 0)
			{
				addSpawn(ALTAR, 186304, -43744, -3193, 57000, false, 0);
			}
			else
			{
				startTimer("spawn_npc", remain, null, null);
			}
		}
		else
		{
			addSpawn(ALTAR, 186304, -43744, -3193, 57000, false, 0);
		}
		
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		
		if (event == "Daimon the White-Eyed has despawned")
		{
			npc.doDie(npc);
			npc.broadcastNpcSay("Darkness could not have ray?");
			addSpawn(ALTAR, 186304, -43744, -3193, 57000, false, 0);
			return null;
		}
		if (event == "spawn_npc")
		{
			addSpawn(ALTAR, 186304, -43744, -3193, 57000, false, 0);
			return null;
		}
		
		ScriptState st = player.getScriptState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		if (event == "8683-02.htm")
		{
			if (st.getPlayer().getLevel() < 73)
			{
				htmltext = "8683-00b.htm";
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				st.setState(ScriptStateType.STARTED);
				st.takeItems(U_SUMMON, 1);
				st.giveItems(S_SUMMON, 1);
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if (event == "8541-02.htm")
		{
			if (st.getItemsCount(S_SUMMON) == 0)
			{
				htmltext = "8541-04.htm";
			}
			else
			{
				L2Npc spawn = addSpawn(DAIMON, 186320, -43904, -3175);
				npc.deleteMe();
				st.takeItems(S_SUMMON, 1);
				st.set("cond", "2");
				startTimer("Daimon the White-Eyed has despawned", 1200000, spawn, null);
				npc.broadcastNpcSay("Who called me?");
			}
		}
		else if (event == "8683-04.htm")
		{
			if (st.getItemsCount(ESSENCE) >= 1)
			{
				st.takeItems(ESSENCE, 1);
				st.giveItems(REWARDS[getRandom(REWARDS.length)], 5);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
				htmltext = "8683-04.htm";
			}
			else
			{
				htmltext = "8683-05.htm";
				st.exitQuest(true);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
		if (st != null)
		{
			int npcId = npc.getId();
			int cond = st.getInt("cond");
			if (cond == 0)
			{
				if (npcId == EYE)
				{
					if (st.getItemsCount(U_SUMMON) >= 1)
					{
						htmltext = "8683-01.htm";
					}
					else
					{
						htmltext = "8683-00a.htm";
					}
				}
			}
			else if (cond == 1)
			{
				if (npcId == EYE)
				{
					htmltext = "8683-02a.htm";
				}
				else if (npcId == ALTAR)
				{
					htmltext = "8541-01.htm";
				}
			}
			else if (cond == 2)
			{
				if (npcId == ALTAR)
				{
					htmltext = "8541-01.htm";
				}
			}
			else if (cond == 3)
			{
				if (npcId == EYE)
				{
					if (st.getItemsCount(ESSENCE) >= 1)
					{
						htmltext = "8683-03.htm";
					}
					else
					{
						htmltext = "8683-06.htm";
					}
				}
				if (npcId == ALTAR)
				{
					htmltext = "8541-05.htm";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		ScriptState st = killer.getScriptState(getName());
		
		long respawnMinDelay = (long) (43200000 * Config.RAID_MIN_RESPAWN_MULTIPLIER);
		long respawnMaxDelay = (long) (129600000 * Config.RAID_MAX_RESPAWN_MULTIPLIER);
		long respawnDelay = Rnd.get(respawnMinDelay, respawnMaxDelay);
		saveGlobalQuestVar("604_respawn", (System.currentTimeMillis() + respawnDelay) + "");
		startTimer("spawn_npc", respawnDelay, null, null);
		cancelTimer("Daimon the White-Eyed has despawned", npc, null);
		Party party = killer.getParty();
		if (party != null)
		{
			List<ScriptState> partyQuestMembers = new ArrayList<>();
			
			for (L2PcInstance player1 : party.getMembers())
			{
				ScriptState st1 = player1.getScriptState(getName());
				if (st1 != null)
				{
					if ((st1.getState() == ScriptStateType.STARTED) && ((st1.getInt("cond") == 1) || (st1.getInt("cond") == 2)))
					{
						partyQuestMembers.add(st1);
					}
				}
			}
			
			if (partyQuestMembers.isEmpty())
			{
				return null;
			}
			
			st = partyQuestMembers.get(Rnd.get(partyQuestMembers.size()));
			
			if (st.hasItems(S_SUMMON))
			{
				st.takeItems(S_SUMMON, 1);
			}
			
			st.giveItems(ESSENCE, 1);
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else
		{
			st = killer.getScriptState(getName());
			if (st == null)
			{
				return null;
			}
			
			if ((st.getState() == ScriptStateType.STARTED) && ((st.getInt("cond") == 1) || (st.getInt("cond") == 2)))
			{
				if (st.getItemsCount(S_SUMMON) > 0)
				{
					st.takeItems(S_SUMMON, 1);
				}
				st.giveItems(ESSENCE, 1);
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		return null;
	}
}
