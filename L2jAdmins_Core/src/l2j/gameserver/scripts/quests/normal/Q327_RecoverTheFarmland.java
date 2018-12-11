package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

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
public class Q327_RecoverTheFarmland extends Script
{
	// ITEMs
	private static final int LEIKAN_LETTER = 5012;
	private static final int TUREK_DOGTAG = 1846;
	private static final int TUREK_MEDALLION = 1847;
	private static final int CLAY_URN_FRAGMENT = 1848;
	private static final int BRASS_TRINKET_PIECE = 1849;
	private static final int BRONZE_MIRROR_PIECE = 1850;
	private static final int JADE_NECKLACE_BEAD = 1851;
	private static final int ANCIENT_CLAY_URN = 1852;
	private static final int ANCIENT_BRASS_TIARA = 1853;
	private static final int ANCIENT_BRONZE_MIRROR = 1854;
	private static final int ANCIENT_JADE_NECKLACE = 1855;
	
	// Rewards
	private static final int ADENA = 57;
	private static final int SOULSHOT_D = 1463;
	private static final int SPIRITSHOT_D = 2510;
	private static final int HEALING_POTION = 1061;
	private static final int HASTE_POTION = 734;
	private static final int POTION_OF_ALACRITY = 735;
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int SCROLL_OF_RESURRECTION = 737;
	
	// NPCs
	private static final int LEIKAN = 7382;
	private static final int PIOTUR = 7597;
	private static final int IRIS = 7034;
	private static final int ASHA = 7313;
	private static final int NESTLE = 7314;
	
	// Monsters
	private static final int TUREK_ORC_WARLORD = 495;
	private static final int TUREK_ORC_ARCHER = 496;
	private static final int TUREK_ORC_SKIRMISHER = 497;
	private static final int TUREK_ORC_SUPPLIER = 498;
	private static final int TUREK_ORC_FOOTMAN = 499;
	private static final int TUREK_ORC_SENTINEL = 500;
	private static final int TUREK_ORC_SHAMAN = 501;
	
	// Chances
	private static final int[][] DROPLIST =
	{
		{
			TUREK_ORC_ARCHER,
			140000,
			TUREK_DOGTAG
		},
		{
			TUREK_ORC_SKIRMISHER,
			70000,
			TUREK_DOGTAG
		},
		{
			TUREK_ORC_SUPPLIER,
			120000,
			TUREK_DOGTAG
		},
		{
			TUREK_ORC_FOOTMAN,
			100000,
			TUREK_DOGTAG
		},
		{
			TUREK_ORC_SENTINEL,
			80000,
			TUREK_DOGTAG
		},
		{
			TUREK_ORC_SHAMAN,
			90000,
			TUREK_MEDALLION
		},
		{
			TUREK_ORC_WARLORD,
			180000,
			TUREK_MEDALLION
		}
	};
	
	// Exp
	private static final Map<Integer, Integer> EXP_REWARD = new HashMap<>();
	{
		EXP_REWARD.put(ANCIENT_CLAY_URN, 2766);
		EXP_REWARD.put(ANCIENT_BRASS_TIARA, 3227);
		EXP_REWARD.put(ANCIENT_BRONZE_MIRROR, 3227);
		EXP_REWARD.put(ANCIENT_JADE_NECKLACE, 3919);
	}
	
	public Q327_RecoverTheFarmland()
	{
		super(327, "Recover the Farmland");
		
		registerItems(LEIKAN_LETTER);
		
		addStartNpc(LEIKAN, PIOTUR);
		addTalkId(LEIKAN, PIOTUR, IRIS, ASHA, NESTLE);
		
		addKillId(TUREK_ORC_WARLORD, TUREK_ORC_ARCHER, TUREK_ORC_SKIRMISHER, TUREK_ORC_SUPPLIER, TUREK_ORC_FOOTMAN, TUREK_ORC_SENTINEL, TUREK_ORC_SHAMAN);
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
		
		// Piotur
		if (event.equalsIgnoreCase("7597-03.htm") && (st.getInt("cond") < 1))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7597-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		// Leikan
		else if (event.equalsIgnoreCase("7382-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(LEIKAN_LETTER, 1);
		}
		// Asha
		else if (event.equalsIgnoreCase("7313-02.htm"))
		{
			if (st.getItemsCount(CLAY_URN_FRAGMENT) >= 5)
			{
				st.takeItems(CLAY_URN_FRAGMENT, 5);
				if (Rnd.get(6) < 5)
				{
					htmltext = "7313-03.htm";
					st.rewardItems(ANCIENT_CLAY_URN, 1);
				}
				else
				{
					htmltext = "7313-10.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("7313-04.htm"))
		{
			if (st.getItemsCount(BRASS_TRINKET_PIECE) >= 5)
			{
				st.takeItems(BRASS_TRINKET_PIECE, 5);
				if (Rnd.get(7) < 6)
				{
					htmltext = "7313-05.htm";
					st.rewardItems(ANCIENT_BRASS_TIARA, 1);
				}
				else
				{
					htmltext = "7313-10.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("7313-06.htm"))
		{
			if (st.getItemsCount(BRONZE_MIRROR_PIECE) >= 5)
			{
				st.takeItems(BRONZE_MIRROR_PIECE, 5);
				if (Rnd.get(7) < 6)
				{
					htmltext = "7313-07.htm";
					st.rewardItems(ANCIENT_BRONZE_MIRROR, 1);
				}
				else
				{
					htmltext = "7313-10.htm";
				}
			}
		}
		else if (event.equalsIgnoreCase("7313-08.htm"))
		{
			if (st.getItemsCount(JADE_NECKLACE_BEAD) >= 5)
			{
				st.takeItems(JADE_NECKLACE_BEAD, 5);
				if (Rnd.get(8) < 7)
				{
					htmltext = "7313-09.htm";
					st.rewardItems(ANCIENT_JADE_NECKLACE, 1);
				}
				else
				{
					htmltext = "7313-10.htm";
				}
			}
		}
		// Iris
		else if (event.equalsIgnoreCase("7034-03.htm"))
		{
			final int n = st.getItemsCount(CLAY_URN_FRAGMENT);
			if (n == 0)
			{
				htmltext = "7034-02.htm";
			}
			else
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(CLAY_URN_FRAGMENT, n);
				st.rewardExpAndSp(n * 307, 0);
			}
		}
		else if (event.equalsIgnoreCase("7034-04.htm"))
		{
			final int n = st.getItemsCount(BRASS_TRINKET_PIECE);
			if (n == 0)
			{
				htmltext = "7034-02.htm";
			}
			else
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(BRASS_TRINKET_PIECE, n);
				st.rewardExpAndSp(n * 368, 0);
			}
		}
		else if (event.equalsIgnoreCase("7034-05.htm"))
		{
			final int n = st.getItemsCount(BRONZE_MIRROR_PIECE);
			if (n == 0)
			{
				htmltext = "7034-02.htm";
			}
			else
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(BRONZE_MIRROR_PIECE, n);
				st.rewardExpAndSp(n * 368, 0);
			}
		}
		else if (event.equalsIgnoreCase("7034-06.htm"))
		{
			final int n = st.getItemsCount(JADE_NECKLACE_BEAD);
			if (n == 0)
			{
				htmltext = "7034-02.htm";
			}
			else
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(JADE_NECKLACE_BEAD, n);
				st.rewardExpAndSp(n * 430, 0);
			}
		}
		else if (event.equalsIgnoreCase("7034-07.htm"))
		{
			boolean isRewarded = false;
			
			for (int i = 1852; i < 1856; i++)
			{
				final int n = st.getItemsCount(i);
				if (n > 0)
				{
					st.takeItems(i, n);
					st.rewardExpAndSp(n * EXP_REWARD.get(i), 0);
					isRewarded = true;
				}
			}
			if (!isRewarded)
			{
				htmltext = "7034-02.htm";
			}
			else
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
		}
		// Nestle
		else if (event.equalsIgnoreCase("7314-03.htm"))
		{
			if (!st.hasItems(ANCIENT_CLAY_URN))
			{
				htmltext = "7314-07.htm";
			}
			else
			{
				st.takeItems(ANCIENT_CLAY_URN, 1);
				st.rewardItems(SOULSHOT_D, 70 + Rnd.get(41));
			}
		}
		else if (event.equalsIgnoreCase("7314-04.htm"))
		{
			if (!st.hasItems(ANCIENT_BRASS_TIARA))
			{
				htmltext = "7314-07.htm";
			}
			else
			{
				st.takeItems(ANCIENT_BRASS_TIARA, 1);
				final int rnd = Rnd.get(100);
				if (rnd < 40)
				{
					st.rewardItems(HEALING_POTION, 1);
				}
				else if (rnd < 84)
				{
					st.rewardItems(HASTE_POTION, 1);
				}
				else
				{
					st.rewardItems(POTION_OF_ALACRITY, 1);
				}
			}
		}
		else if (event.equalsIgnoreCase("7314-05.htm"))
		{
			if (!st.hasItems(ANCIENT_BRONZE_MIRROR))
			{
				htmltext = "7314-07.htm";
			}
			else
			{
				st.takeItems(ANCIENT_BRONZE_MIRROR, 1);
				st.rewardItems(Rnd.get(100) < 59 ? SCROLL_OF_ESCAPE : SCROLL_OF_RESURRECTION, 1);
			}
		}
		else if (event.equalsIgnoreCase("7314-06.htm"))
		{
			if (!st.hasItems(ANCIENT_JADE_NECKLACE))
			{
				htmltext = "7314-07.htm";
			}
			else
			{
				st.takeItems(ANCIENT_JADE_NECKLACE, 1);
				st.rewardItems(SPIRITSHOT_D, 50 + Rnd.get(41));
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
				htmltext = npc.getId() + (player.getLevel() < 25 ? "-01.htm" : "-02.htm");
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case PIOTUR:
						if (!st.hasItems(LEIKAN_LETTER))
						{
							if (st.hasAtLeastOneItem(TUREK_DOGTAG, TUREK_MEDALLION))
							{
								htmltext = "7597-05.htm";
								
								if (cond < 4)
								{
									st.set("cond", "4");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								
								final int dogtag = st.getItemsCount(TUREK_DOGTAG);
								final int medallion = st.getItemsCount(TUREK_MEDALLION);
								
								st.takeItems(TUREK_DOGTAG, -1);
								st.takeItems(TUREK_MEDALLION, -1);
								st.rewardItems(ADENA, (dogtag * 40) + (medallion * 50) + ((dogtag + medallion) >= 10 ? 619 : 0));
							}
							else
							{
								htmltext = "7597-04.htm";
							}
						}
						else
						{
							htmltext = "7597-03a.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(LEIKAN_LETTER, 1);
						}
						break;
					
					case LEIKAN:
						if (cond == 2)
						{
							htmltext = "7382-04.htm";
						}
						else if ((cond == 3) || (cond == 4))
						{
							htmltext = "7382-05.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 5)
						{
							htmltext = "7382-05.htm";
						}
						break;
					
					default:
						htmltext = npc.getId() + "-01.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		for (final int[] npcData : DROPLIST)
		{
			if (npcData[0] == npc.getId())
			{
				st.dropItemsAlways(npcData[2], 1, -1);
				st.dropItems(Rnd.get(1848, 1851), 1, 0, npcData[1]);
				break;
			}
		}
		
		return null;
	}
	
}
