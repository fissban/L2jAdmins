package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * Thx L2Acis
 */
public class Q225_TestOfTheSearcher extends Script
{
	// Items
	private static final int LUTHER_LETTER = 2784;
	private static final int ALEX_WARRANT = 2785;
	private static final int LEIRYNN_ORDER_1 = 2786;
	private static final int DELU_TOTEM = 2787;
	private static final int LEIRYNN_ORDER_2 = 2788;
	private static final int CHIEF_KALKI_FANG = 2789;
	private static final int LEIRYNN_REPORT = 2790;
	private static final int STRANGE_MAP = 2791;
	private static final int LAMBERT_MAP = 2792;
	private static final int ALEX_LETTER = 2793;
	private static final int ALEX_ORDER = 2794;
	private static final int WINE_CATALOG = 2795;
	private static final int TYRA_CONTRACT = 2796;
	private static final int RED_SPORE_DUST = 2797;
	private static final int MALRUKIAN_WINE = 2798;
	private static final int OLD_ORDER = 2799;
	private static final int JAX_DIARY = 2800;
	private static final int TORN_MAP_PIECE_1 = 2801;
	private static final int TORN_MAP_PIECE_2 = 2802;
	private static final int SOLT_MAP = 2803;
	private static final int MAKEL_MAP = 2804;
	private static final int COMBINED_MAP = 2805;
	private static final int RUSTED_KEY = 2806;
	private static final int GOLD_BAR = 2807;
	private static final int ALEX_RECOMMEND = 2808;
	
	// Rewards
	private static final int MARK_OF_SEARCHER = 2809;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// NPCs
	private static final int ALEX = 7291;
	private static final int TYRA = 7420;
	private static final int TREE = 7627;
	private static final int STRONG_WOODEN_CHEST = 7628;
	private static final int LUTHER = 7690;
	private static final int LEIRYNN = 7728;
	private static final int BORYS = 7729;
	private static final int JAX = 7730;
	
	// Monsters
	private static final int HANGMAN_TREE = 144;
	private static final int ROAD_SCAVENGER = 551;
	private static final int GIANT_FUNGUS = 555;
	private static final int DELU_LIZARDMAN_SHAMAN = 781;
	private static final int DELU_CHIEF_KALKIS = 5093;
	private static final int NEER_BODYGUARD = 5092;
	
	private static L2Npc strongWoodenChest; // Used to avoid to spawn multiple instances.
	
	public Q225_TestOfTheSearcher()
	{
		super(225, "Test of the Searcher");
		
		registerItems(LUTHER_LETTER, ALEX_WARRANT, LEIRYNN_ORDER_1, DELU_TOTEM, LEIRYNN_ORDER_2, CHIEF_KALKI_FANG, LEIRYNN_REPORT, STRANGE_MAP, LAMBERT_MAP, ALEX_LETTER, ALEX_ORDER, WINE_CATALOG, TYRA_CONTRACT, RED_SPORE_DUST, MALRUKIAN_WINE, OLD_ORDER, JAX_DIARY, TORN_MAP_PIECE_1, TORN_MAP_PIECE_2, SOLT_MAP, MAKEL_MAP, COMBINED_MAP, RUSTED_KEY, GOLD_BAR, ALEX_RECOMMEND);
		
		addStartNpc(LUTHER);
		addTalkId(ALEX, TYRA, TREE, STRONG_WOODEN_CHEST, LUTHER, LEIRYNN, BORYS, JAX);
		
		addAttackId(DELU_LIZARDMAN_SHAMAN);
		addKillId(HANGMAN_TREE, ROAD_SCAVENGER, GIANT_FUNGUS, DELU_LIZARDMAN_SHAMAN, DELU_CHIEF_KALKIS, NEER_BODYGUARD);
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
		
		// LUTHER
		if (event.equalsIgnoreCase("7690-05a.htm"))
		{
			st.startQuest();
			st.giveItems(LUTHER_LETTER, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, (player.getClassId() == ClassId.SCAVENGER) ? 82 : 96);
		}
		// ALEX
		else if (event.equalsIgnoreCase("7291-07.htm"))
		{
			st.set("cond", "8");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(LEIRYNN_REPORT, 1);
			st.takeItems(STRANGE_MAP, 1);
			st.giveItems(ALEX_LETTER, 1);
			st.giveItems(ALEX_ORDER, 1);
			st.giveItems(LAMBERT_MAP, 1);
		}
		// TYRA
		else if (event.equalsIgnoreCase("7420-01a.htm"))
		{
			st.set("cond", "10");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(WINE_CATALOG, 1);
			st.giveItems(TYRA_CONTRACT, 1);
		}
		// JAX
		else if (event.equalsIgnoreCase("7730-01d.htm"))
		{
			st.set("cond", "14");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(OLD_ORDER, 1);
			st.giveItems(JAX_DIARY, 1);
		}
		// TREE
		else if (event.equalsIgnoreCase("7627-01a.htm"))
		{
			if (strongWoodenChest == null)
			{
				if (st.getInt("cond") == 16)
				{
					st.set("cond", "17");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(RUSTED_KEY, 1);
				}
				
				strongWoodenChest = addSpawn(STRONG_WOODEN_CHEST, 10098, 157287, -2406, 0, false, 0);
				startTimer("chest_despawn", 300000, null, player, false);
			}
		}
		// STRONG WOODEN CHEST
		else if (event.equalsIgnoreCase("7628-01a.htm"))
		{
			if (!st.hasItems(RUSTED_KEY))
			{
				htmltext = "7628-02.htm";
			}
			else
			{
				st.set("cond", "18");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(RUSTED_KEY, -1);
				st.giveItems(GOLD_BAR, 20);
				
				strongWoodenChest.deleteMe();
				strongWoodenChest = null;
				cancelTimer("chest_despawn", null, player);
			}
		}
		// STRONG WOODEN CHEST DESPAWN
		else if (event.equalsIgnoreCase("chest_despawn"))
		{
			strongWoodenChest.deleteMe();
			strongWoodenChest = null;
			return null;
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
				if ((player.getClassId() != ClassId.ROGUE) && (player.getClassId() != ClassId.SCOUT) && (player.getClassId() != ClassId.ASSASSIN) && (player.getClassId() != ClassId.SCAVENGER))
				{
					htmltext = "7690-01.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7690-02.htm";
				}
				else
				{
					htmltext = (player.getClassId() == ClassId.SCAVENGER) ? "7690-04.htm" : "7690-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case LUTHER:
						if (cond == 1)
						{
							htmltext = "7690-06.htm";
						}
						else if ((cond > 1) && (cond < 19))
						{
							htmltext = "7690-07.htm";
						}
						else if (cond == 19)
						{
							htmltext = "7690-08.htm";
							st.takeItems(ALEX_RECOMMEND, 1);
							st.giveItems(MARK_OF_SEARCHER, 1);
							st.rewardExpAndSp(37831, 18750);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case ALEX:
						if (cond == 1)
						{
							htmltext = "7291-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(LUTHER_LETTER, 1);
							st.giveItems(ALEX_WARRANT, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7291-02.htm";
						}
						else if ((cond > 2) && (cond < 7))
						{
							htmltext = "7291-03.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7291-04.htm";
						}
						else if ((cond > 7) && (cond < 13))
						{
							htmltext = "7291-08.htm";
						}
						else if ((cond > 12) && (cond < 16))
						{
							htmltext = "7291-09.htm";
						}
						else if ((cond > 15) && (cond < 18))
						{
							htmltext = "7291-10.htm";
						}
						else if (cond == 18)
						{
							htmltext = "7291-11.htm";
							st.set("cond", "19");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ALEX_ORDER, 1);
							st.takeItems(COMBINED_MAP, 1);
							st.takeItems(GOLD_BAR, -1);
							st.giveItems(ALEX_RECOMMEND, 1);
						}
						else if (cond == 19)
						{
							htmltext = "7291-12.htm";
						}
						break;
					
					case LEIRYNN:
						if (cond == 2)
						{
							htmltext = "7728-01.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ALEX_WARRANT, 1);
							st.giveItems(LEIRYNN_ORDER_1, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7728-02.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7728-03.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(DELU_TOTEM, -1);
							st.takeItems(LEIRYNN_ORDER_1, 1);
							st.giveItems(LEIRYNN_ORDER_2, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7728-04.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7728-05.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CHIEF_KALKI_FANG, 1);
							st.takeItems(LEIRYNN_ORDER_2, 1);
							st.giveItems(LEIRYNN_REPORT, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7728-06.htm";
						}
						else if (cond > 7)
						{
							htmltext = "7728-07.htm";
						}
						break;
					
					case BORYS:
						if (cond == 8)
						{
							htmltext = "7729-01.htm";
							st.set("cond", "9");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ALEX_LETTER, 1);
							st.giveItems(WINE_CATALOG, 1);
						}
						else if ((cond > 8) && (cond < 12))
						{
							htmltext = "7729-02.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7729-03.htm";
							st.set("cond", "13");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(MALRUKIAN_WINE, 1);
							st.takeItems(WINE_CATALOG, 1);
							st.giveItems(OLD_ORDER, 1);
						}
						else if (cond == 13)
						{
							htmltext = "7729-04.htm";
						}
						else if (cond > 13)
						{
							htmltext = "7729-05.htm";
						}
						break;
					
					case TYRA:
						if (cond == 9)
						{
							htmltext = "7420-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7420-02.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7420-03.htm";
							st.set("cond", "12");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(RED_SPORE_DUST, -1);
							st.takeItems(TYRA_CONTRACT, 1);
							st.giveItems(MALRUKIAN_WINE, 1);
						}
						else if (cond > 11)
						{
							htmltext = "7420-04.htm";
						}
						break;
					
					case JAX:
						if (cond == 13)
						{
							htmltext = "7730-01.htm";
						}
						else if (cond == 14)
						{
							htmltext = "7730-02.htm";
						}
						else if (cond == 15)
						{
							htmltext = "7730-03.htm";
							st.set("cond", "16");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(LAMBERT_MAP, 1);
							st.takeItems(MAKEL_MAP, 1);
							st.takeItems(JAX_DIARY, 1);
							st.takeItems(SOLT_MAP, 1);
							st.giveItems(COMBINED_MAP, 1);
						}
						else if (cond > 15)
						{
							htmltext = "7730-04.htm";
						}
						break;
					
					case TREE:
						if ((cond == 16) || (cond == 17))
						{
							htmltext = "7627-01.htm";
						}
						break;
					
					case STRONG_WOODEN_CHEST:
						if (cond == 17)
						{
							htmltext = "7628-01.htm";
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
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		ScriptState st = checkPlayerState(attacker, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		if (st.hasItems(LEIRYNN_ORDER_1) && (npc.getScriptValue() != 1))
		{
			npc.setScriptValue(1);
			addSpawn(NEER_BODYGUARD, npc, false, 200000);
		}
		
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st;
		
		switch (npc.getId())
		{
			case DELU_LIZARDMAN_SHAMAN:
				st = checkPlayerCondition(player, npc, "cond", "3");
				if (st == null)
				{
					return null;
				}
				
				if (st.dropItemsAlways(DELU_TOTEM, 1, 10))
				{
					st.set("cond", "4");
				}
				break;
			
			case DELU_CHIEF_KALKIS:
				st = checkPlayerCondition(player, npc, "cond", "5");
				if (st == null)
				{
					return null;
				}
				
				st.set("cond", "6");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(CHIEF_KALKI_FANG, 1);
				st.giveItems(STRANGE_MAP, 1);
				break;
			
			case GIANT_FUNGUS:
				st = checkPlayerCondition(player, npc, "cond", "10");
				if (st == null)
				{
					return null;
				}
				
				if (st.dropItemsAlways(RED_SPORE_DUST, 1, 10))
				{
					st.set("cond", "11");
				}
				break;
			
			case ROAD_SCAVENGER:
				st = checkPlayerCondition(player, npc, "cond", "14");
				if (st == null)
				{
					return null;
				}
				
				if (!st.hasItems(SOLT_MAP) && st.dropItems(TORN_MAP_PIECE_1, 1, 4, 500000))
				{
					st.takeItems(TORN_MAP_PIECE_1, -1);
					st.giveItems(SOLT_MAP, 1);
					
					if (st.hasItems(MAKEL_MAP))
					{
						st.set("cond", "15");
					}
				}
				break;
			
			case HANGMAN_TREE:
				st = checkPlayerCondition(player, npc, "cond", "14");
				if (st == null)
				{
					return null;
				}
				
				if (!st.hasItems(MAKEL_MAP) && st.dropItems(TORN_MAP_PIECE_2, 1, 4, 500000))
				{
					st.takeItems(TORN_MAP_PIECE_2, -1);
					st.giveItems(MAKEL_MAP, 1);
					
					if (st.hasItems(SOLT_MAP))
					{
						st.set("cond", "15");
					}
				}
				break;
		}
		
		return null;
	}
}
