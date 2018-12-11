package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q219_TestimonyOfFate extends Script
{
	// NPCs
	private static final int KAIRA = 7476;
	private static final int METHEUS = 7614;
	private static final int IXIA = 7463;
	private static final int ALDER_SPIRIT = 7613;
	private static final int ROA = 7114;
	private static final int NORMAN = 7210;
	private static final int THIFIELL = 7358;
	private static final int ARKENIA = 7419;
	private static final int BLOODY_PIXY = 12084;
	private static final int BLIGHT_TREANT = 12089;
	// Items
	private static final int KAIRA_LETTER = 3173;
	private static final int METHEUS_FUNERAL_JAR = 3174;
	private static final int KASANDRA_REMAINS = 3175;
	private static final int HERBALISM_TEXTBOOK = 3176;
	private static final int IXIA_LIST = 3177;
	private static final int MEDUSA_ICHOR = 3178;
	private static final int MARSH_SPIDER_FLUIDS = 3179;
	private static final int DEAD_SEEKER_DUNG = 3180;
	private static final int TYRANT_BLOOD = 3181;
	private static final int NIGHTSHADE_ROOT = 3182;
	private static final int BELLADONNA = 3183;
	private static final int ALDER_SKULL_1 = 3184;
	private static final int ALDER_SKULL_2 = 3185;
	private static final int ALDER_RECEIPT = 3186;
	private static final int REVELATIONS_MANUSCRIPT = 3187;
	private static final int KAIRA_RECOMMENDATION = 3189;
	private static final int KAIRA_INSTRUCTIONS = 3188;
	private static final int PALUS_CHARM = 3190;
	private static final int THIFIELL_LETTER = 3191;
	private static final int ARKENIA_NOTE = 3192;
	private static final int PIXY_GARNET = 3193;
	private static final int GRANDIS_SKULL = 3194;
	private static final int KARUL_BUGBEAR_SKULL = 3195;
	private static final int BREKA_OVERLORD_SKULL = 3196;
	private static final int LETO_OVERLORD_SKULL = 3197;
	private static final int RED_FAIRY_DUST = 3198;
	private static final int BLIGHT_TREANT_SEED = 3199;
	private static final int BLACK_WILLOW_LEAF = 3200;
	private static final int BLIGHT_TREANT_SAP = 3201;
	private static final int ARKENIA_LETTER = 3202;
	// Rewards
	private static final int MARK_OF_FATE = 3172;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// Monsters
	private static final int HANGMAN_TREE = 144;
	private static final int MARSH_STAKATO = 157;
	private static final int MEDUSA = 158;
	private static final int TYRANT = 192;
	private static final int TYRANT_KINGPIN = 193;
	private static final int DEAD_SEEKER = 202;
	private static final int MARSH_STAKATO_WORKER = 230;
	private static final int MARSH_STAKATO_SOLDIER = 232;
	private static final int MARSH_SPIDER = 233;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int GRANDIS = 554;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int KARUL_BUGBEAR = 600;
	private static final int BLACK_WILLOW_LURKER = 5079;
	
	// Cond 6 drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(DEAD_SEEKER, 500000);
		CHANCES.put(TYRANT, 500000);
		CHANCES.put(TYRANT_KINGPIN, 600000);
		CHANCES.put(MEDUSA, 500000);
		CHANCES.put(MARSH_STAKATO, 400000);
		CHANCES.put(MARSH_STAKATO_WORKER, 300000);
		CHANCES.put(MARSH_STAKATO_SOLDIER, 500000);
		CHANCES.put(MARSH_STAKATO_DRONE, 600000);
		CHANCES.put(MARSH_SPIDER, 500000);
	}
	
	public Q219_TestimonyOfFate()
	{
		super(219, "Testimony of Fate");
		
		registerItems(KAIRA_LETTER, METHEUS_FUNERAL_JAR, KASANDRA_REMAINS, HERBALISM_TEXTBOOK, IXIA_LIST, MEDUSA_ICHOR, MARSH_SPIDER_FLUIDS, DEAD_SEEKER_DUNG, TYRANT_BLOOD, NIGHTSHADE_ROOT, BELLADONNA, ALDER_SKULL_1, ALDER_SKULL_2, ALDER_RECEIPT, REVELATIONS_MANUSCRIPT, KAIRA_RECOMMENDATION, KAIRA_INSTRUCTIONS, PALUS_CHARM, THIFIELL_LETTER, ARKENIA_NOTE, PIXY_GARNET, GRANDIS_SKULL, KARUL_BUGBEAR_SKULL, BREKA_OVERLORD_SKULL, LETO_OVERLORD_SKULL, RED_FAIRY_DUST, BLIGHT_TREANT_SEED, BLACK_WILLOW_LEAF, BLIGHT_TREANT_SAP, ARKENIA_LETTER);
		
		addStartNpc(KAIRA);
		addTalkId(KAIRA, METHEUS, IXIA, ALDER_SPIRIT, ROA, NORMAN, THIFIELL, ARKENIA, BLOODY_PIXY, BLIGHT_TREANT);
		
		addKillId(HANGMAN_TREE, MARSH_STAKATO, MEDUSA, TYRANT, TYRANT_KINGPIN, DEAD_SEEKER, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_SPIDER, MARSH_STAKATO_DRONE, BREKA_ORC_OVERLORD, GRANDIS, LETO_LIZARDMAN_OVERLORD, KARUL_BUGBEAR, BLACK_WILLOW_LURKER);
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
		
		if (event.equalsIgnoreCase("7476-05.htm"))
		{
			st.startQuest();
			st.giveItems(KAIRA_LETTER, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 98);
		}
		else if (event.equalsIgnoreCase("7114-04.htm"))
		{
			st.setCond(12, true);
			st.takeItems(ALDER_SKULL_2, 1);
			st.giveItems(ALDER_RECEIPT, 1);
		}
		else if (event.equalsIgnoreCase("7476-12.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			
			if (player.getLevel() < 38)
			{
				htmltext = "7476-13.htm";
				st.set("cond", "14");
				st.giveItems(KAIRA_INSTRUCTIONS, 1);
			}
			else
			{
				st.set("cond", "15");
				st.takeItems(REVELATIONS_MANUSCRIPT, 1);
				st.giveItems(KAIRA_RECOMMENDATION, 1);
			}
		}
		else if (event.equalsIgnoreCase("7419-02.htm"))
		{
			st.setCond(17, true);
			st.takeItems(THIFIELL_LETTER, 1);
			st.giveItems(ARKENIA_NOTE, 1);
		}
		else if (event.equalsIgnoreCase("12084-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(PIXY_GARNET, 1);
		}
		else if (event.equalsIgnoreCase("12089-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(BLIGHT_TREANT_SEED, 1);
		}
		else if (event.equalsIgnoreCase("7419-05.htm"))
		{
			st.setCond(18, true);
			st.takeItems(ARKENIA_NOTE, 1);
			st.takeItems(BLIGHT_TREANT_SAP, 1);
			st.takeItems(RED_FAIRY_DUST, 1);
			st.giveItems(ARKENIA_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7476-02.htm";
				}
				else if ((player.getLevel() < 37) || (player.getClassId().level() != 1))
				{
					htmltext = "7476-01.htm";
				}
				else
				{
					htmltext = "7476-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case KAIRA:
						if (cond == 1)
						{
							htmltext = "7476-06.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "7476-07.htm";
						}
						else if ((cond > 3) && (cond < 9))
						{
							htmltext = "7476-08.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7476-09.htm";
							st.setCond(10, true);
							st.takeItems(ALDER_SKULL_1, 1);
							addSpawn(ALDER_SPIRIT, player, false, 0);
						}
						else if ((cond > 9) && (cond < 13))
						{
							htmltext = "7476-10.htm";
						}
						else if (cond == 13)
						{
							htmltext = "7476-11.htm";
						}
						else if (cond == 14)
						{
							if (player.getLevel() < 38)
							{
								htmltext = "7476-14.htm";
							}
							else
							{
								htmltext = "7476-12.htm";
								st.setCond(15, true);
								st.takeItems(KAIRA_INSTRUCTIONS, 1);
								st.takeItems(REVELATIONS_MANUSCRIPT, 1);
								st.giveItems(KAIRA_RECOMMENDATION, 1);
							}
						}
						else if (cond == 15)
						{
							htmltext = "7476-16.htm";
						}
						else if (cond > 15)
						{
							htmltext = "7476-17.htm";
						}
						break;
					
					case METHEUS:
						if (cond == 1)
						{
							htmltext = "7614-01.htm";
							st.setCond(2, true);
							st.takeItems(KAIRA_LETTER, 1);
							st.giveItems(METHEUS_FUNERAL_JAR, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7614-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7614-03.htm";
							st.setCond(4);
							st.setCond(5, true);
							st.takeItems(KASANDRA_REMAINS, 1);
							st.giveItems(HERBALISM_TEXTBOOK, 1);
						}
						else if ((cond > 3) && (cond < 8))
						{
							htmltext = "7614-04.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7614-05.htm";
							st.setCond(9, true);
							st.takeItems(BELLADONNA, 1);
							st.giveItems(ALDER_SKULL_1, 1);
						}
						else if (cond > 8)
						{
							htmltext = "7614-06.htm";
						}
						break;
					
					case IXIA:
						if (cond == 5)
						{
							htmltext = "7463-01.htm";
							st.setCond(6, true);
							st.takeItems(HERBALISM_TEXTBOOK, 1);
							st.giveItems(IXIA_LIST, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7463-02.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7463-03.htm";
							st.setCond(8, true);
							st.takeItems(IXIA_LIST, 1);
							st.takeItems(DEAD_SEEKER_DUNG, -1);
							st.takeItems(MARSH_SPIDER_FLUIDS, -1);
							st.takeItems(MEDUSA_ICHOR, -1);
							st.takeItems(NIGHTSHADE_ROOT, -1);
							st.takeItems(TYRANT_BLOOD, -1);
							st.giveItems(BELLADONNA, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7463-04.htm";
						}
						else if (cond > 8)
						{
							htmltext = "7463-05.htm";
						}
						break;
					
					case ALDER_SPIRIT:
						if (cond == 10)
						{
							htmltext = "7613-01.htm";
							st.setCond(11, true);
							st.giveItems(ALDER_SKULL_2, 1);
							npc.deleteMe();
						}
						break;
					
					case ROA:
						if (cond == 11)
						{
							htmltext = "7114-01.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7114-05.htm";
						}
						else if (cond > 12)
						{
							htmltext = "7114-06.htm";
						}
						break;
					
					case NORMAN:
						if (cond == 12)
						{
							htmltext = "7210-01.htm";
							st.setCond(13, true);
							st.takeItems(ALDER_RECEIPT, 1);
							st.giveItems(REVELATIONS_MANUSCRIPT, 1);
						}
						else if (cond > 12)
						{
							htmltext = "7210-02.htm";
						}
						break;
					
					case THIFIELL:
						if (cond == 15)
						{
							htmltext = "7358-01.htm";
							st.setCond(16, true);
							st.takeItems(KAIRA_RECOMMENDATION, 1);
							st.giveItems(PALUS_CHARM, 1);
							st.giveItems(THIFIELL_LETTER, 1);
						}
						else if (cond == 16)
						{
							htmltext = "7358-02.htm";
						}
						else if (cond == 17)
						{
							htmltext = "7358-03.htm";
						}
						else if (cond == 18)
						{
							htmltext = "7358-04.htm";
							st.takeItems(PALUS_CHARM, 1);
							st.takeItems(ARKENIA_LETTER, 1);
							st.giveItems(MARK_OF_FATE, 1);
							st.rewardExpAndSp(68183, 1750);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case ARKENIA:
						if (cond == 16)
						{
							htmltext = "7419-01.htm";
						}
						else if (cond == 17)
						{
							htmltext = (st.hasItems(BLIGHT_TREANT_SAP) && st.hasItems(RED_FAIRY_DUST)) ? "7419-04.htm" : "7419-03.htm";
						}
						else if (cond == 18)
						{
							htmltext = "7419-06.htm";
						}
						break;
					
					case BLOODY_PIXY:
						if (cond == 17)
						{
							if (st.hasItems(PIXY_GARNET))
							{
								if ((st.getItemsCount(GRANDIS_SKULL) >= 10) && (st.getItemsCount(KARUL_BUGBEAR_SKULL) >= 10) && (st.getItemsCount(BREKA_OVERLORD_SKULL) >= 10) && (st.getItemsCount(LETO_OVERLORD_SKULL) >= 10))
								{
									htmltext = "12084-04.htm";
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(BREKA_OVERLORD_SKULL, -1);
									st.takeItems(GRANDIS_SKULL, -1);
									st.takeItems(KARUL_BUGBEAR_SKULL, -1);
									st.takeItems(LETO_OVERLORD_SKULL, -1);
									st.takeItems(PIXY_GARNET, 1);
									st.giveItems(RED_FAIRY_DUST, 1);
								}
								else
								{
									htmltext = "12084-03.htm";
								}
							}
							else if (st.hasItems(RED_FAIRY_DUST))
							{
								htmltext = "12084-05.htm";
							}
							else
							{
								htmltext = "12084-01.htm";
							}
						}
						else if (cond == 18)
						{
							htmltext = "12084-05.htm";
						}
						break;
					
					case BLIGHT_TREANT:
						if (cond == 17)
						{
							if (st.hasItems(BLIGHT_TREANT_SEED))
							{
								if (st.hasItems(BLACK_WILLOW_LEAF))
								{
									htmltext = "12089-04.htm";
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(BLACK_WILLOW_LEAF, 1);
									st.takeItems(BLIGHT_TREANT_SEED, 1);
									st.giveItems(BLIGHT_TREANT_SAP, 1);
								}
								else
								{
									htmltext = "12089-03.htm";
								}
							}
							else if (st.hasItems(BLIGHT_TREANT_SAP))
							{
								htmltext = "12089-05.htm";
							}
							else
							{
								htmltext = "12089-01.htm";
							}
						}
						else if (cond == 18)
						{
							htmltext = "12089-05.htm";
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
			case HANGMAN_TREE:
				if (st.getCond() == 2)
				{
					st.setCond(3, true);
					st.takeItems(METHEUS_FUNERAL_JAR, 1);
					st.giveItems(KASANDRA_REMAINS, 1);
				}
				break;
			
			case DEAD_SEEKER:
				if ((st.getCond() == 6) && st.dropItems(DEAD_SEEKER_DUNG, 1, 10, CHANCES.get(npcId)))
				{
					if ((st.getItemsCount(TYRANT_BLOOD) >= 10) && (st.getItemsCount(MEDUSA_ICHOR) >= 10) && (st.getItemsCount(NIGHTSHADE_ROOT) >= 10) && (st.getItemsCount(MARSH_SPIDER_FLUIDS) >= 10))
					{
						st.setCond(7);
					}
				}
				break;
			
			case TYRANT:
			case TYRANT_KINGPIN:
				if ((st.getCond() == 6) && st.dropItems(TYRANT_BLOOD, 1, 10, CHANCES.get(npcId)))
				{
					if ((st.getItemsCount(DEAD_SEEKER_DUNG) >= 10) && (st.getItemsCount(MEDUSA_ICHOR) >= 10) && (st.getItemsCount(NIGHTSHADE_ROOT) >= 10) && (st.getItemsCount(MARSH_SPIDER_FLUIDS) >= 10))
					{
						st.setCond(7);
					}
				}
				break;
			
			case MEDUSA:
				if ((st.getCond() == 6) && st.dropItems(MEDUSA_ICHOR, 1, 10, CHANCES.get(npcId)))
				{
					if ((st.getItemsCount(DEAD_SEEKER_DUNG) >= 10) && (st.getItemsCount(TYRANT_BLOOD) >= 10) && (st.getItemsCount(NIGHTSHADE_ROOT) >= 10) && (st.getItemsCount(MARSH_SPIDER_FLUIDS) >= 10))
					{
						st.setCond(7);
					}
				}
				break;
			
			case MARSH_STAKATO:
			case MARSH_STAKATO_WORKER:
			case MARSH_STAKATO_SOLDIER:
			case MARSH_STAKATO_DRONE:
				if ((st.getCond() == 6) && st.dropItems(NIGHTSHADE_ROOT, 1, 10, CHANCES.get(npcId)))
				{
					if ((st.getItemsCount(DEAD_SEEKER_DUNG) >= 10) && (st.getItemsCount(TYRANT_BLOOD) >= 10) && (st.getItemsCount(MEDUSA_ICHOR) >= 10) && (st.getItemsCount(MARSH_SPIDER_FLUIDS) >= 10))
					{
						st.setCond(7);
					}
				}
				break;
			
			case MARSH_SPIDER:
				if ((st.getCond() == 6) && st.dropItems(MARSH_SPIDER_FLUIDS, 1, 10, CHANCES.get(npcId)))
				{
					if ((st.getItemsCount(DEAD_SEEKER_DUNG) >= 10) && (st.getItemsCount(TYRANT_BLOOD) >= 10) && (st.getItemsCount(MEDUSA_ICHOR) >= 10) && (st.getItemsCount(NIGHTSHADE_ROOT) >= 10))
					{
						st.setCond(7);
					}
				}
				break;
			
			case GRANDIS:
				if (st.hasItems(PIXY_GARNET))
				{
					st.dropItemsAlways(GRANDIS_SKULL, 1, 10);
				}
				break;
			
			case LETO_LIZARDMAN_OVERLORD:
				if (st.hasItems(PIXY_GARNET))
				{
					st.dropItemsAlways(LETO_OVERLORD_SKULL, 1, 10);
				}
				break;
			
			case BREKA_ORC_OVERLORD:
				if (st.hasItems(PIXY_GARNET))
				{
					st.dropItemsAlways(BREKA_OVERLORD_SKULL, 1, 10);
				}
				break;
			
			case KARUL_BUGBEAR:
				if (st.hasItems(PIXY_GARNET))
				{
					st.dropItemsAlways(KARUL_BUGBEAR_SKULL, 1, 10);
				}
				break;
			
			case BLACK_WILLOW_LURKER:
				if (st.hasItems(BLIGHT_TREANT_SEED))
				{
					st.dropItemsAlways(BLACK_WILLOW_LEAF, 1, 1);
				}
				break;
		}
		
		return null;
	}
}
