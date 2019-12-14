package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB, CaFi, zarie
 * @originalQuest aCis
 */
public class Q374_WhisperOfDreams_Part1 extends Script
{
	// NPCs
	private static final int MANAKIA = 7515;
	private static final int TORAI = 7557;
	
	// Monsters
	private static final int CAVE_BEAST = 620;
	private static final int DEATH_WAVE = 621;
	
	// Items
	private static final int CAVE_BEAST_TOOTH = 5884;
	private static final int DEATH_WAVE_LIGHT = 5885;
	private static final int SEALED_MYSTERIOUS_STONE = 5886;
	private static final int MYSTERIOUS_STONE = 5887;
	
	// Rewards
	private static final int[][] REWARDS =
	{
		// 0: Dark Crystal, 3x, 2950 adena:
		{
			5486,
			3,
			2950
		},
		// 1: Nightmare, 2x, 18050 adena:
		{
			5487,
			2,
			18050
		},
		// 2: Majestic, 2x, 18050 adena:
		{
			5488,
			2,
			18050
		},
		// 3: Tallum Tunic, 4, 10450 adena:
		{
			5485,
			4,
			10450
		},
		// 4: Tallum Stockings, 6, 15550 adena:
		{
			5489,
			6,
			15550
		}
	};
	
	public Q374_WhisperOfDreams_Part1()
	{
		super(374, "Whisper of Dreams, Part 1");
		
		registerItems(DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH, SEALED_MYSTERIOUS_STONE, MYSTERIOUS_STONE);
		
		addStartNpc(MANAKIA);
		addTalkId(MANAKIA, TORAI);
		
		addKillId(CAVE_BEAST, DEATH_WAVE);
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
		
		// Manakia
		if (event.equalsIgnoreCase("7515-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("condStone", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.startsWith("7515-06-"))
		{
			if ((st.getItemsCount(CAVE_BEAST_TOOTH) >= 65) && (st.getItemsCount(DEATH_WAVE_LIGHT) >= 65))
			{
				htmltext = "7515-06.htm";
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				
				final int[] reward = REWARDS[Integer.parseInt(event.substring(8, 9))];
				
				st.takeItems(CAVE_BEAST_TOOTH, -1);
				st.takeItems(DEATH_WAVE_LIGHT, -1);
				
				st.rewardItems(Inventory.ADENA_ID, reward[2]);
				st.giveItems(reward[0], reward[1]);
			}
			else
			{
				htmltext = "7515-07.htm";
			}
		}
		else if (event.equalsIgnoreCase("7515-08.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		// Torai
		else if (event.equalsIgnoreCase("7557-02.htm"))
		{
			if ((st.getInt("cond") == 2) && st.hasItems(SEALED_MYSTERIOUS_STONE))
			{
				st.set("cond", "3");
				st.takeItems(SEALED_MYSTERIOUS_STONE, -1);
				st.giveItems(MYSTERIOUS_STONE, 1);
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
			else
			{
				htmltext = "7557-03.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 56 ? "7515-01.htm" : "7515-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case MANAKIA:
						if (!st.hasItems(SEALED_MYSTERIOUS_STONE))
						{
							if ((st.getItemsCount(CAVE_BEAST_TOOTH) >= 65) && (st.getItemsCount(DEATH_WAVE_LIGHT) >= 65))
							{
								htmltext = "7515-05.htm";
							}
							else
							{
								htmltext = "7515-04.htm";
							}
						}
						else
						{
							if (cond == 1)
							{
								htmltext = "7515-09.htm";
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								htmltext = "7515-10.htm";
							}
						}
						break;
					
					case TORAI:
						if ((cond == 2) && st.hasItems(SEALED_MYSTERIOUS_STONE))
						{
							htmltext = "7557-01.htm";
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
		// Drop tooth or light to anyone.
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		st.dropItems(npc.getId() == CAVE_BEAST ? CAVE_BEAST_TOOTH : DEATH_WAVE_LIGHT, 1, 65, 500000);
		
		// Drop sealed mysterious stone to party member who still need it.
		partyMember = getRandomPartyMember(player, npc, "condStone", "1");
		if (partyMember == null)
		{
			return null;
		}
		
		st = partyMember.getScriptState(getName());
		
		if (st.dropItems(SEALED_MYSTERIOUS_STONE, 1, 1, 1000))
		{
			st.unset("condStone");
		}
		
		return null;
	}
	
}
