package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class Q372_LegacyOfInsolence extends Script
{
	// NPCs
	private static final int WALDERAL = 7844;
	private static final int PATRIN = 7929;
	private static final int HOLLY = 7839;
	private static final int CLAUDIA = 8001;
	private static final int DESMOND = 7855;
	
	// Monsters
	private static final int[][] MONSTERS_DROPS =
	{
		// npcId
		{
			817,
			821,
			825,
			829,
			1069,
			1063
		},
		// parchment (red, blue, black, white)
		{
			5966,
			5966,
			5966,
			5967,
			5968,
			5969
		},
		// rate
		{
			300000,
			400000,
			460000,
			400000,
			250000,
			250000
		}
	};
	
	// ITEMs
	private static final int[][] SCROLLS =
	{
		// Walderal => 13 blueprints => parts, recipes.
		{
			5989,
			6001
		},
		// Holly -> 5x Imperial Genealogy -> Dark Crystal parts/Adena
		{
			5984,
			5988
		},
		// Patrin -> 5x Ancient Epic -> Tallum parts/Adena
		{
			5979,
			5983
		},
		// Claudia -> 7x Revelation of the Seals -> Nightmare parts/Adena
		{
			5972,
			5978
		},
		// Desmond -> 7x Revelation of the Seals -> Majestic parts/Adena
		{
			5972,
			5978
		}
	};
	
	// Rewards matrice.
	private static final int[][][] REWARDS_MATRICE =
	{
		// Walderal DC choice
		{
			{
				13,
				5496
			},
			{
				26,
				5508
			},
			{
				40,
				5525
			},
			{
				58,
				5368
			},
			{
				76,
				5392
			},
			{
				100,
				5426
			}
		},
		// Walderal Tallum choice
		{
			{
				13,
				5497
			},
			{
				26,
				5509
			},
			{
				40,
				5526
			},
			{
				58,
				5370
			},
			{
				76,
				5394
			},
			{
				100,
				5428
			}
		},
		// Walderal NM choice
		{
			{
				20,
				5502
			},
			{
				40,
				5514
			},
			{
				58,
				5527
			},
			{
				73,
				5380
			},
			{
				87,
				5404
			},
			{
				100,
				5430
			}
		},
		// Walderal Maja choice
		{
			{
				20,
				5503
			},
			{
				40,
				5515
			},
			{
				58,
				5528
			},
			{
				73,
				5382
			},
			{
				87,
				5406
			},
			{
				100,
				5432
			}
		},
		// Holly DC parts + adenas.
		{
			{
				33,
				5496
			},
			{
				66,
				5508
			},
			{
				89,
				5525
			},
			{
				100,
				57
			}
		},
		// Patrin Tallum parts + adenas.
		{
			{
				33,
				5497
			},
			{
				66,
				5509
			},
			{
				89,
				5526
			},
			{
				100,
				57
			}
		},
		// Claudia NM parts + adenas.
		{
			{
				35,
				5502
			},
			{
				70,
				5514
			},
			{
				87,
				5527
			},
			{
				100,
				57
			}
		},
		// Desmond Maja choice
		{
			{
				35,
				5503
			},
			{
				70,
				5515
			},
			{
				87,
				5528
			},
			{
				100,
				57
			}
		}
	};
	
	public Q372_LegacyOfInsolence()
	{
		super(372, "Legacy of Insolence");
		
		addStartNpc(WALDERAL);
		addTalkId(WALDERAL, PATRIN, HOLLY, CLAUDIA, DESMOND);
		
		addKillId(MONSTERS_DROPS[0]);
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
		
		if (event.equalsIgnoreCase("7844-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7844-05b.htm"))
		{
			if (st.getInt("cond") == 1)
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("7844-07.htm"))
		{
			for (int blueprint = 5989; blueprint <= 6001; blueprint++)
			{
				if (!st.hasItems(blueprint))
				{
					htmltext = "7844-06.htm";
					break;
				}
			}
		}
		else if (event.startsWith("7844-07-"))
		{
			checkAndRewardItems(st, 0, Integer.parseInt(event.substring(9, 10)), WALDERAL);
		}
		else if (event.equalsIgnoreCase("7844-09.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() < 59 ? "7844-01.htm" : "7844-02.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case WALDERAL:
						htmltext = "7844-05.htm";
						break;
					
					case HOLLY:
						htmltext = checkAndRewardItems(st, 1, 4, HOLLY);
						break;
					
					case PATRIN:
						htmltext = checkAndRewardItems(st, 2, 5, PATRIN);
						break;
					
					case CLAUDIA:
						htmltext = checkAndRewardItems(st, 3, 6, CLAUDIA);
						break;
					
					case DESMOND:
						htmltext = checkAndRewardItems(st, 4, 7, DESMOND);
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
		
		final int npcId = npc.getId();
		
		for (int i = 0; i < MONSTERS_DROPS[0].length; i++)
		{
			if (MONSTERS_DROPS[0][i] == npcId)
			{
				partyMember.getScriptState(getName()).dropItems(MONSTERS_DROPS[1][i], 1, 0, MONSTERS_DROPS[2][i]);
				break;
			}
		}
		return null;
	}
	
	private static String checkAndRewardItems(ScriptState st, int itemType, int rewardType, int npcId)
	{
		// Retrieve array with items to check.
		final int[] itemsToCheck = SCROLLS[itemType];
		
		// Check set of items.
		for (int item = itemsToCheck[0]; item <= itemsToCheck[1]; item++)
		{
			if (!st.hasItems(item))
			{
				return npcId + (npcId == WALDERAL ? "-07a.htm" : "-01.htm");
			}
		}
		
		// Remove set of items.
		for (int item = itemsToCheck[0]; item <= itemsToCheck[1]; item++)
		{
			st.takeItems(item, 1);
		}
		
		// Retrieve array with rewards.
		final int[][] rewards = REWARDS_MATRICE[rewardType];
		final int chance = Rnd.get(100);
		
		for (final int[] reward : rewards)
		{
			if (chance < reward[0])
			{
				st.rewardItems(reward[1], 1);
				return npcId + "-02.htm";
			}
		}
		
		return npcId + (npcId == WALDERAL ? "-07a.htm" : "-01.htm");
	}
	
}
