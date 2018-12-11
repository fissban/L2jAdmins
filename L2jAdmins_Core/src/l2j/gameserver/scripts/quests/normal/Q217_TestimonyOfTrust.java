package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * Thx L2Acis
 */
public class Q217_TestimonyOfTrust extends Script
{
	// Items
	private static final int LETTER_TO_ELF = 2735;
	private static final int LETTER_TO_DARK_ELF = 2736;
	private static final int LETTER_TO_DWARF = 2737;
	private static final int LETTER_TO_ORC = 2738;
	private static final int LETTER_TO_SERESIN = 2739;
	private static final int SCROLL_OF_DARK_ELF_TRUST = 2740;
	private static final int SCROLL_OF_ELF_TRUST = 2741;
	private static final int SCROLL_OF_DWARF_TRUST = 2742;
	private static final int SCROLL_OF_ORC_TRUST = 2743;
	private static final int RECOMMENDATION_OF_HOLLINT = 2744;
	private static final int ORDER_OF_ASTERIOS = 2745;
	private static final int BREATH_OF_WINDS = 2746;
	private static final int SEED_OF_VERDURE = 2747;
	private static final int LETTER_FROM_THIFIELL = 2748;
	private static final int BLOOD_GUARDIAN_BASILIK = 2749;
	private static final int GIANT_APHID = 2750;
	private static final int STAKATO_FLUIDS = 2751;
	private static final int BASILIK_PLASMA = 2752;
	private static final int HONEY_DEW = 2753;
	private static final int STAKATO_ICHOR = 2754;
	private static final int ORDER_OF_CLAYTON = 2755;
	private static final int PARASITE_OF_LOTA = 2756;
	private static final int LETTER_TO_MANAKIA = 2757;
	private static final int LETTER_OF_MANAKIA = 2758;
	private static final int LETTER_TO_NIKOLA = 2759;
	private static final int ORDER_OF_NIKOLA = 2760;
	private static final int HEARTSTONE_OF_PORTA = 2761;
	// Rewards
	private static final int MARK_OF_TRUST = 2734;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// NPCs
	private static final int HOLLINT = 7191;
	private static final int ASTERIOS = 7154;
	private static final int THIFIELL = 7358;
	private static final int CLAYTON = 7464;
	private static final int SERESIN = 7657;
	private static final int KAKAI = 7565;
	private static final int MANAKIA = 7515;
	private static final int LOCKIRIN = 7531;
	private static final int NIKOLA = 7621;
	private static final int BIOTIN = 7031;
	// Monsters
	private static final int DRYAD = 13;
	private static final int DRYAD_ELDER = 19;
	private static final int LIREIN = 36;
	private static final int LIREIN_ELDER = 44;
	
	private static final int ANT_RECRUIT = 82;
	private static final int ANT_PATROL = 84;
	private static final int ANT_GUARD = 86;
	private static final int ANT_SOLDIER = 87;
	private static final int ANT_WARRIOR_CAPTAIN = 88;
	private static final int MARSH_STAKATO = 157;
	private static final int PORTA = 213;
	private static final int MARSH_STAKATO_WORKER = 230;
	private static final int MARSH_STAKATO_SOLDIER = 232;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int GUARDIAN_BASILIK = 550;
	private static final int WINDSUS = 553;
	private static final int ACTEA_OF_VERDANT_WILDS = 5121;
	private static final int LUELL_OF_ZEPHYR_WINDS = 5120;
	
	public Q217_TestimonyOfTrust()
	{
		super(217, "Testimony of Trust");
		
		registerItems(LETTER_TO_ELF, LETTER_TO_DARK_ELF, LETTER_TO_DWARF, LETTER_TO_ORC, LETTER_TO_SERESIN, SCROLL_OF_DARK_ELF_TRUST, SCROLL_OF_ELF_TRUST, SCROLL_OF_DWARF_TRUST, SCROLL_OF_ORC_TRUST, RECOMMENDATION_OF_HOLLINT, ORDER_OF_ASTERIOS, BREATH_OF_WINDS, SEED_OF_VERDURE, LETTER_FROM_THIFIELL, BLOOD_GUARDIAN_BASILIK, GIANT_APHID, STAKATO_FLUIDS, BASILIK_PLASMA, HONEY_DEW, STAKATO_ICHOR, ORDER_OF_CLAYTON, PARASITE_OF_LOTA, LETTER_TO_MANAKIA, LETTER_OF_MANAKIA, LETTER_TO_NIKOLA, ORDER_OF_NIKOLA, HEARTSTONE_OF_PORTA);
		
		addStartNpc(HOLLINT);
		addTalkId(HOLLINT, ASTERIOS, THIFIELL, CLAYTON, SERESIN, KAKAI, MANAKIA, LOCKIRIN, NIKOLA, BIOTIN);
		
		addKillId(DRYAD, DRYAD_ELDER, LIREIN, LIREIN_ELDER, ACTEA_OF_VERDANT_WILDS, LUELL_OF_ZEPHYR_WINDS, GUARDIAN_BASILIK, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, ANT_SOLDIER, ANT_WARRIOR_CAPTAIN, MARSH_STAKATO, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, WINDSUS, PORTA);
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
		
		if (event.equalsIgnoreCase("7191-04.htm"))
		{
			st.startQuest();
			st.giveItems(LETTER_TO_ELF, 1);
			st.giveItems(LETTER_TO_DARK_ELF, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 16);
		}
		else if (event.equalsIgnoreCase("7154-03.htm"))
		{
			st.setCond(2, true);
			st.takeItems(LETTER_TO_ELF, 1);
			st.giveItems(ORDER_OF_ASTERIOS, 1);
		}
		else if (event.equalsIgnoreCase("7358-02.htm"))
		{
			st.setCond(5, true);
			st.takeItems(LETTER_TO_DARK_ELF, 1);
			st.giveItems(LETTER_FROM_THIFIELL, 1);
		}
		else if (event.equalsIgnoreCase("7515-02.htm"))
		{
			st.setCond(14, true);
			st.takeItems(LETTER_TO_MANAKIA, 1);
		}
		else if (event.equalsIgnoreCase("7531-02.htm"))
		{
			st.setCond(18, true);
			st.takeItems(LETTER_TO_DWARF, 1);
			st.giveItems(LETTER_TO_NIKOLA, 1);
		}
		else if (event.equalsIgnoreCase("7565-02.htm"))
		{
			st.setCond(13, true);
			st.takeItems(LETTER_TO_ORC, 1);
			st.giveItems(LETTER_TO_MANAKIA, 1);
		}
		else if (event.equalsIgnoreCase("7621-02.htm"))
		{
			st.setCond(19, true);
			st.takeItems(LETTER_TO_NIKOLA, 1);
			st.giveItems(ORDER_OF_NIKOLA, 1);
		}
		else if (event.equalsIgnoreCase("7657-03.htm"))
		{
			if (player.getLevel() < 38)
			{
				htmltext = "7657-02.htm";
				if (st.getCond() == 10)
				{
					st.setCond(11, true);
				}
			}
			else
			{
				st.setCond(12, true);
				st.takeItems(LETTER_TO_SERESIN, 1);
				st.giveItems(LETTER_TO_DWARF, 1);
				st.giveItems(LETTER_TO_ORC, 1);
			}
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
				if (player.getClassId().level() != 1)
				{
					htmltext = "7191-01a.htm";
				}
				else if (player.getRace() != Race.HUMAN)
				{
					htmltext = "7191-02.htm";
				}
				else if (player.getLevel() < 37)
				{
					htmltext = "7191-01.htm";
				}
				else
				{
					htmltext = "7191-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case HOLLINT:
						if (cond < 9)
						{
							htmltext = "7191-08.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7191-05.htm";
							st.setCond(10, true);
							st.takeItems(SCROLL_OF_DARK_ELF_TRUST, 1);
							st.takeItems(SCROLL_OF_ELF_TRUST, 1);
							st.giveItems(LETTER_TO_SERESIN, 1);
						}
						else if ((cond > 9) && (cond < 22))
						{
							htmltext = "7191-09.htm";
						}
						else if (cond == 22)
						{
							htmltext = "7191-06.htm";
							st.setCond(23, true);
							st.takeItems(SCROLL_OF_DWARF_TRUST, 1);
							st.takeItems(SCROLL_OF_ORC_TRUST, 1);
							st.giveItems(RECOMMENDATION_OF_HOLLINT, 1);
						}
						else if (cond == 23)
						{
							htmltext = "7191-07.htm";
						}
						break;
					
					case ASTERIOS:
						if (cond == 1)
						{
							htmltext = "7154-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7154-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7154-05.htm";
							st.setCond(4, true);
							st.takeItems(BREATH_OF_WINDS, 1);
							st.takeItems(SEED_OF_VERDURE, 1);
							st.takeItems(ORDER_OF_ASTERIOS, 1);
							st.giveItems(SCROLL_OF_ELF_TRUST, 1);
						}
						else if (cond > 3)
						{
							htmltext = "7154-06.htm";
						}
						break;
					
					case THIFIELL:
						if (cond == 4)
						{
							htmltext = "7358-01.htm";
						}
						else if ((cond > 4) && (cond < 8))
						{
							htmltext = "7358-05.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7358-03.htm";
							st.setCond(9, true);
							st.takeItems(BASILIK_PLASMA, 1);
							st.takeItems(HONEY_DEW, 1);
							st.takeItems(STAKATO_ICHOR, 1);
							st.giveItems(SCROLL_OF_DARK_ELF_TRUST, 1);
						}
						else if (cond > 8)
						{
							htmltext = "7358-04.htm";
						}
						break;
					
					case CLAYTON:
						if (cond == 5)
						{
							htmltext = "7464-01.htm";
							st.setCond(6, true);
							st.takeItems(LETTER_FROM_THIFIELL, 1);
							st.giveItems(ORDER_OF_CLAYTON, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7464-02.htm";
						}
						else if (cond > 6)
						{
							htmltext = "7464-03.htm";
							if (cond == 7)
							{
								st.setCond(8, true);
								st.takeItems(ORDER_OF_CLAYTON, 1);
							}
						}
						break;
					
					case SERESIN:
						if ((cond == 10) || (cond == 11))
						{
							htmltext = "7657-01.htm";
						}
						else if ((cond > 11) && (cond < 22))
						{
							htmltext = "7657-04.htm";
						}
						else if (cond == 22)
						{
							htmltext = "7657-05.htm";
						}
						break;
					
					case KAKAI:
						if (cond == 12)
						{
							htmltext = "7565-01.htm";
						}
						else if ((cond > 12) && (cond < 16))
						{
							htmltext = "7565-03.htm";
						}
						else if (cond == 16)
						{
							htmltext = "7565-04.htm";
							st.setCond(17, true);
							st.takeItems(LETTER_OF_MANAKIA, 1);
							st.giveItems(SCROLL_OF_ORC_TRUST, 1);
						}
						else if (cond > 16)
						{
							htmltext = "7565-05.htm";
						}
						break;
					
					case MANAKIA:
						if (cond == 13)
						{
							htmltext = "7515-01.htm";
						}
						else if (cond == 14)
						{
							htmltext = "7515-03.htm";
						}
						else if (cond == 15)
						{
							htmltext = "7515-04.htm";
							st.setCond(16, true);
							st.takeItems(PARASITE_OF_LOTA, -1);
							st.giveItems(LETTER_OF_MANAKIA, 1);
						}
						else if (cond > 15)
						{
							htmltext = "7515-05.htm";
						}
						break;
					
					case LOCKIRIN:
						if (cond == 17)
						{
							htmltext = "7531-01.htm";
						}
						else if ((cond > 17) && (cond < 21))
						{
							htmltext = "7531-03.htm";
						}
						else if (cond == 21)
						{
							htmltext = "7531-04.htm";
							st.setCond(22, true);
							st.giveItems(SCROLL_OF_DWARF_TRUST, 1);
						}
						else if (cond == 22)
						{
							htmltext = "7531-05.htm";
						}
						break;
					
					case NIKOLA:
						if (cond == 18)
						{
							htmltext = "7621-01.htm";
						}
						else if (cond == 19)
						{
							htmltext = "7621-03.htm";
						}
						else if (cond == 20)
						{
							htmltext = "7621-04.htm";
							st.setCond(21, true);
							st.takeItems(HEARTSTONE_OF_PORTA, -1);
							st.takeItems(ORDER_OF_NIKOLA, 1);
						}
						else if (cond > 20)
						{
							htmltext = "7621-05.htm";
						}
						break;
					
					case BIOTIN:
						if (cond == 23)
						{
							htmltext = "7031-01.htm";
							st.takeItems(RECOMMENDATION_OF_HOLLINT, 1);
							st.giveItems(MARK_OF_TRUST, 1);
							st.rewardExpAndSp(39571, 2500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
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
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		switch (npcId)
		{
			case DRYAD:
			case DRYAD_ELDER:
				if ((st.getCond() == 2) && !st.hasItems(SEED_OF_VERDURE) && (Rnd.get(100) < 33))
				{
					addSpawn(ACTEA_OF_VERDANT_WILDS, npc, true, 200000);
					st.playSound(PlaySoundType.QUEST_BEFORE_BATTLE);
				}
				break;
			
			case LIREIN:
			case LIREIN_ELDER:
				if ((st.getCond() == 2) && !st.hasItems(BREATH_OF_WINDS) && (Rnd.get(100) < 33))
				{
					addSpawn(LUELL_OF_ZEPHYR_WINDS, npc, true, 200000);
					st.playSound(PlaySoundType.QUEST_BEFORE_BATTLE);
				}
				break;
			
			case ACTEA_OF_VERDANT_WILDS:
				if ((st.getCond() == 2) && !st.hasItems(SEED_OF_VERDURE))
				{
					st.giveItems(SEED_OF_VERDURE, 1);
					if (st.hasItems(BREATH_OF_WINDS))
					{
						st.setCond(3, true);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
				break;
			
			case LUELL_OF_ZEPHYR_WINDS:
				if ((st.getCond() == 2) && !st.hasItems(BREATH_OF_WINDS))
				{
					st.giveItems(BREATH_OF_WINDS, 1);
					if (st.hasItems(SEED_OF_VERDURE))
					{
						st.setCond(3, true);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
				break;
			
			case MARSH_STAKATO:
			case MARSH_STAKATO_WORKER:
			case MARSH_STAKATO_SOLDIER:
			case MARSH_STAKATO_DRONE:
				if ((st.getCond() == 6) && !st.hasItems(STAKATO_ICHOR) && st.dropItemsAlways(STAKATO_FLUIDS, 1, 10))
				{
					st.takeItems(STAKATO_FLUIDS, -1);
					st.giveItems(STAKATO_ICHOR, 1);
					
					if (st.hasItems(BASILIK_PLASMA, HONEY_DEW))
					{
						st.setCond(7);
					}
				}
				break;
			
			case ANT_RECRUIT:
			case ANT_PATROL:
			case ANT_GUARD:
			case ANT_SOLDIER:
			case ANT_WARRIOR_CAPTAIN:
				if ((st.getCond() == 6) && !st.hasItems(HONEY_DEW) && st.dropItemsAlways(GIANT_APHID, 1, 10))
				{
					st.takeItems(GIANT_APHID, -1);
					st.giveItems(HONEY_DEW, 1);
					
					if (st.hasItems(BASILIK_PLASMA, STAKATO_ICHOR))
					{
						st.setCond(7);
					}
				}
				break;
			
			case GUARDIAN_BASILIK:
				if ((st.getCond() == 6) && !st.hasItems(BASILIK_PLASMA) && st.dropItemsAlways(BLOOD_GUARDIAN_BASILIK, 1, 10))
				{
					st.takeItems(BLOOD_GUARDIAN_BASILIK, -1);
					st.giveItems(BASILIK_PLASMA, 1);
					
					if (st.hasItems(HONEY_DEW, STAKATO_ICHOR))
					{
						st.setCond(7);
					}
				}
				break;
			
			case WINDSUS:
				if ((st.getCond() == 14) && st.dropItems(PARASITE_OF_LOTA, 1, 10, 500000))
				{
					st.setCond(15);
				}
				break;
			
			case PORTA:
				if ((st.getCond() == 19) && st.dropItemsAlways(HEARTSTONE_OF_PORTA, 1, 10))
				{
					st.setCond(20);
				}
				break;
		}
		
		return null;
	}
}
