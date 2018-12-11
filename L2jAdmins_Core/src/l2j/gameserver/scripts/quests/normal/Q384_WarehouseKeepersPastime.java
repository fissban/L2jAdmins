package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q384_WarehouseKeepersPastime extends Script
{
	// NPCs
	private static final int CLIFF = 7182;
	private static final int BAXT = 7685;
	
	// Items
	private static final int MEDAL = 5964;
	
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(947, 160000); // Connabi
		CHANCES.put(948, 180000); // Bartal
		CHANCES.put(945, 120000); // Cadeine
		CHANCES.put(946, 150000); // Sanhidro
		CHANCES.put(635, 150000); // Carinkain
		CHANCES.put(773, 610000); // Conjurer Bat Lord
		CHANCES.put(774, 600000); // Conjurer Bat
		CHANCES.put(760, 240000); // Dragon Bearer Archer
		CHANCES.put(758, 240000); // Dragon Bearer Chief
		CHANCES.put(759, 230000); // Dragon Bearer Warrior
		CHANCES.put(242, 220000); // Dustwind Gargoyle
		CHANCES.put(281, 220000); // Dustwind Gargoyle (2)
		CHANCES.put(556, 140000); // Giant Monstereye
		CHANCES.put(668, 210000); // Grave Guard
		CHANCES.put(241, 220000); // Hunter Gargoyle
		CHANCES.put(286, 220000); // Hunter Gargoyle (2)
		CHANCES.put(949, 190000); // Luminun
		CHANCES.put(950, 200000); // Innersen
		CHANCES.put(942, 90000); // Nightmare Guide
		CHANCES.put(943, 120000); // Nightmare Keeper
		CHANCES.put(944, 110000); // Nightmare Lord
		CHANCES.put(559, 140000); // Rotting Golem
		CHANCES.put(243, 210000); // Thunder Wyrm
		CHANCES.put(282, 210000); // Thunder Wyrm (2)
		CHANCES.put(677, 340000); // Tulben
		CHANCES.put(605, 150000); // Weird Drake
	}
	
	private static final int[][] INDEX_MAP =
	{
		{
			1,
			2,
			3
		}, // line 1
		{
			4,
			5,
			6
		}, // line 2
		{
			7,
			8,
			9
		}, // line 3
		{
			1,
			4,
			7
		}, // column 1
		{
			2,
			5,
			8
		}, // column 2
		{
			3,
			6,
			9
		}, // column 3
		{
			1,
			5,
			9
		}, // diagonal 1
		{
			3,
			5,
			7
		}, // diagonal 2
	};
	
	private static final int[][] rewards_10_win =
	{
		{
			16,
			1888
		}, // Synthetic Cokes
		{
			32,
			1887
		}, // Varnish of Purity
		{
			50,
			1894
		}, // Crafted Leather
		{
			80,
			952
		}, // Scroll: Enchant Armor (C)
		{
			89,
			1890
		}, // Mithril Alloy
		{
			98,
			1893
		}, // Oriharukon
		{
			100,
			951
		}
		// Scroll: Enchant Weapon (C)
	};
	
	private static final int[][] rewards_10_lose =
	{
		{
			50,
			4041
		}, // Mold Hardener
		{
			80,
			952
		}, // Scroll: Enchant Armor (C)
		{
			98,
			1892
		}, // Blacksmith's Frame
		{
			100,
			917
		}
		// Necklace of Mermaid
	};
	
	private static final int[][] rewards_100_win =
	{
		{
			50,
			883
		}, // Aquastone Ring
		{
			80,
			951
		}, // Scroll: Enchant Weapon (C)
		{
			98,
			852
		}, // Moonstone Earring
		{
			100,
			401
		}
		// Drake Leather Armor
	};
	
	private static final int[][] rewards_100_lose =
	{
		{
			50,
			951
		}, // Scroll: Enchant Weapon (C)
		{
			80,
			500
		}, // Great Helmet
		{
			98,
			2437
		}, // Drake Leather Boots
		{
			100,
			135
		}
		// Samurai Longsword
	};
	
	public Q384_WarehouseKeepersPastime()
	{
		super(384, "Warehouse Keeper's Pastime");
		
		registerItems(MEDAL);
		
		addStartNpc(CLIFF);
		addTalkId(CLIFF, BAXT);
		
		for (final int npcId : CHANCES.keySet())
		{
			addKillId(npcId);
		}
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
		
		final int npcId = npc.getId();
		if (event.equalsIgnoreCase("7182-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase(npcId + "-08.htm"))
		{
			st.playSound(PlaySoundType.QUEST_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase(npcId + "-11.htm"))
		{
			if (st.getItemsCount(MEDAL) < 10)
			{
				htmltext = npcId + "-12.htm";
			}
			else
			{
				st.set("bet", "10");
				st.set("board", Util.scrambleString("123456789"));
				st.takeItems(MEDAL, 10);
			}
		}
		else if (event.equalsIgnoreCase(npcId + "-13.htm"))
		{
			if (st.getItemsCount(MEDAL) < 100)
			{
				htmltext = npcId + "-12.htm";
			}
			else
			{
				st.set("bet", "100");
				st.set("board", Util.scrambleString("123456789"));
				st.takeItems(MEDAL, 100);
			}
		}
		else if (event.startsWith("select_1-")) // first pick
		{
			// Register the first char.
			st.set("playerArray", event.substring(9));
			
			// Send back the finalized HTM with dynamic content.
			htmltext = fillBoard(st, getHtmlText(npcId + "-14.htm"));
		}
		else if (event.startsWith("select_2-")) // pick #2-5
		{
			// Stores the current event for future use.
			final String number = event.substring(9);
			
			// Restore the player array.
			final String playerArray = st.get("playerArray");
			
			// Verify if the given number is already on the player array, if yes, it's invalid, otherwise register it.
			if (Util.contains(playerArray.split(""), number))
			{
				htmltext = fillBoard(st, getHtmlText(npcId + "-" + String.valueOf(14 + (2 * playerArray.length())) + ".htm"));
			}
			else
			{
				// Stores the final String.
				st.set("playerArray", playerArray.concat(number));
				
				htmltext = fillBoard(st, getHtmlText(npcId + "-" + String.valueOf(11 + (2 * (playerArray.length() + 1))) + ".htm"));
			}
		}
		else if (event.startsWith("select_3-")) // pick #6
		{
			// Stores the current event for future use.
			final String number = event.substring(9);
			
			// Restore the player array.
			final String playerArray = st.get("playerArray");
			
			// Verify if the given number is already on the player array, if yes, it's invalid, otherwise calculate reward.
			if (Util.contains(playerArray.split(""), number))
			{
				htmltext = fillBoard(st, getHtmlText(npcId + "-26.htm"));
			}
			else
			{
				// No need to store the String on player db, but still need to update it.
				final String[] playerChoice = playerArray.concat(number).split("");
				
				// Transform the generated board (9 string length) into a 2d matrice (3x3 int).
				final String[] board = st.get("board").split("");
				
				// test for all line combination
				int winningLines = 0;
				
				for (final int[] map : INDEX_MAP)
				{
					// test line combination
					boolean won = true;
					for (final int index : map)
					{
						won &= Util.contains(playerChoice, board[index]);
					}
					
					// cut the loop, when you won
					if (won)
					{
						winningLines++;
					}
				}
				
				if (winningLines == 3)
				{
					htmltext = getHtmlText(npcId + "-23.htm");
					
					final int chance = Rnd.get(100);
					for (final int[] reward : st.get("bet") == "10" ? rewards_10_win : rewards_100_win)
					{
						if (chance < reward[0])
						{
							st.giveItems(reward[1], 1);
							if (reward[1] == 2437)
							{
								st.giveItems(2463, 1);
							}
							
							break;
						}
					}
				}
				else if (winningLines == 0)
				{
					htmltext = getHtmlText(npcId + "-25.htm");
					
					final int chance = Rnd.get(100);
					for (final int[] reward : st.get("bet") == "10" ? rewards_10_lose : rewards_100_lose)
					{
						if (chance < reward[0])
						{
							st.giveItems(reward[1], 1);
							break;
						}
					}
				}
				else
				{
					htmltext = getHtmlText(npcId + "-24.htm");
				}
				
				for (int i = 1; i < 10; i++)
				{
					htmltext = htmltext.replace("<?Cell" + i + "?>", board[i]);
					htmltext = htmltext.replace("<?FontColor" + i + "?>", Util.contains(playerChoice, board[i]) ? "ff0000" : "ffffff");
				}
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
				htmltext = player.getLevel() < 40 ? "7182-04.htm" : "7182-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case CLIFF:
						htmltext = st.getItemsCount(MEDAL) < 10 ? "7182-06.htm" : "7182-07.htm";
						break;
					
					case BAXT:
						htmltext = st.getItemsCount(MEDAL) < 10 ? "7685-01.htm" : "7685-02.htm";
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
		
		partyMember.getScriptState(getName()).dropItems(MEDAL, 1, 0, CHANCES.get(npc.getId()));
		
		return null;
	}
	
	private static final String fillBoard(ScriptState st, String htmltext)
	{
		final String[] playerArray = st.get("playerArray").split("");
		final String[] board = st.get("board").split("");
		
		for (int i = 1; i < 10; i++)
		{
			htmltext = htmltext.replace("<?Cell" + i + "?>", Util.contains(playerArray, board[i]) ? board[i] : "?");
		}
		
		return htmltext;
	}
	
}
