package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q355_FamilyHonor extends Script
{
	// NPCs
	private static final int GALIBREDO = 7181;
	private static final int PATRIN = 7929;
	
	// Monsters
	private static final int TIMAK_ORC_TROOP_LEADER = 767;
	private static final int TIMAK_ORC_TROOP_SHAMAN = 768;
	private static final int TIMAK_ORC_TROOP_WARRIOR = 769;
	private static final int TIMAK_ORC_TROOP_ARCHER = 770;
	
	// ITEMs
	private static final int GALIBREDO_BUST = 4252;
	private static final int WORK_OF_BERONA = 4350;
	private static final int STATUE_PROTOTYPE = 4351;
	private static final int STATUE_ORIGINAL = 4352;
	private static final int STATUE_REPLICA = 4353;
	private static final int STATUE_FORGERY = 4354;
	
	// Drop chances
	private static final Map<Integer, int[]> CHANCES = new HashMap<>();
	{
		CHANCES.put(TIMAK_ORC_TROOP_LEADER, new int[]
		{
			44,
			54
		});
		CHANCES.put(TIMAK_ORC_TROOP_SHAMAN, new int[]
		{
			36,
			45
		});
		CHANCES.put(TIMAK_ORC_TROOP_WARRIOR, new int[]
		{
			35,
			43
		});
		CHANCES.put(TIMAK_ORC_TROOP_ARCHER, new int[]
		{
			32,
			42
		});
	}
	
	public Q355_FamilyHonor()
	{
		super(355, "Family Honor");
		
		registerItems(GALIBREDO_BUST);
		
		addStartNpc(GALIBREDO);
		addTalkId(GALIBREDO, PATRIN);
		
		addKillId(TIMAK_ORC_TROOP_LEADER, TIMAK_ORC_TROOP_SHAMAN, TIMAK_ORC_TROOP_WARRIOR, TIMAK_ORC_TROOP_ARCHER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7181-2.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7181-4b.htm"))
		{
			final int count = st.getItemsCount(GALIBREDO_BUST);
			if (count > 0)
			{
				htmltext = "7181-4.htm";
				
				int reward = 2800 + (count * 120);
				if (count >= 100)
				{
					htmltext = "7181-4a.htm";
					reward += 5000;
				}
				
				st.takeItems(GALIBREDO_BUST, count);
				st.rewardItems(Inventory.ADENA_ID, reward);
			}
		}
		else if (event.equalsIgnoreCase("7929-7.htm"))
		{
			if (st.hasItems(WORK_OF_BERONA))
			{
				st.takeItems(WORK_OF_BERONA, 1);
				
				final int appraising = Rnd.get(100);
				if (appraising < 20)
				{
					htmltext = "7929-2.htm";
				}
				else if (appraising < 40)
				{
					htmltext = "7929-3.htm";
					st.giveItems(STATUE_REPLICA, 1);
				}
				else if (appraising < 60)
				{
					htmltext = "7929-4.htm";
					st.giveItems(STATUE_ORIGINAL, 1);
				}
				else if (appraising < 80)
				{
					htmltext = "7929-5.htm";
					st.giveItems(STATUE_FORGERY, 1);
				}
				else
				{
					htmltext = "7929-6.htm";
					st.giveItems(STATUE_PROTOTYPE, 1);
				}
			}
		}
		else if (event.equalsIgnoreCase("7181-6.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 36 ? "7181-0a.htm" : "7181-0.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case GALIBREDO:
						htmltext = st.hasItems(GALIBREDO_BUST) ? "7181-3a.htm" : "7181-3.htm";
						break;
					
					case PATRIN:
						htmltext = "7929-0.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		final int[] chances = CHANCES.get(npc.getId());
		final int random = Rnd.get(100);
		
		if (random < chances[1])
		{
			st.dropItemsAlways(random < chances[0] ? GALIBREDO_BUST : WORK_OF_BERONA, 1, 0);
		}
		
		return null;
	}
	
}
