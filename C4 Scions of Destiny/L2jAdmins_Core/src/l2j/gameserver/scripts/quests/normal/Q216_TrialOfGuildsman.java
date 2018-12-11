package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Q216_TrialOfGuildsman extends Script
{
	// Npc's
	private static final int VALKON = 7103;
	private static final int NORMAN = 7210;
	private static final int ALTRAN = 7283;
	private static final int PINTER = 7298;
	private static final int DUNING = 7688;
	// Monster's
	private static final int ANT = 79;
	private static final int ANT_CAPTAIN = 80;
	private static final int ANT_OVERSEER = 81;
	private static final int GRANITE_GOLEM = 83;
	private static final int MANDRAGORA_SPROUT = 154;
	private static final int MANDRAGORA_SAPLING = 155;
	private static final int MANDRAGORA_BLOSSOM = 156;
	private static final int SILENOS = 168;
	private static final int STRAIN = 200;
	private static final int GHOUL = 201;
	private static final int DEAD_SEEKER = 202;
	private static final int MANDRAGORA_SPROUT_1 = 223;
	private static final int BREKA_ORC = 267;
	private static final int BREKA_ORC_ARCHER = 268;
	private static final int BREKA_ORC_SHAMAN = 269;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int BREKA_ORC_WARRIOR = 271;
	// Item's
	private static final int RP_JOURNEYMAN_RING = 3024;
	private static final int RP_AMBER_BEAD = 3025;
	private static final int VALKONS_RECOMMEND = 3120;
	private static final int MANDRAGORA_BERRY = 3121;
	private static final int ALLTRANS_INSTRUCTIONS = 3122;
	private static final int ALLTRANS_RECOMMEND1 = 3123;
	private static final int ALLTRANS_RECOMMEND2 = 3124;
	private static final int NORMANS_INSTRUCTIONS = 3125;
	private static final int NORMANS_RECEIPT = 3126;
	private static final int DUNINGS_INSTRUCTIONS = 3127;
	private static final int DUNINGS_KEY = 3128;
	private static final int NORMANS_LIST = 3129;
	private static final int GRAY_BONE_POWDER = 3130;
	private static final int GRANITE_WHETSTONE = 3131;
	private static final int RED_PIGMENT = 3132;
	private static final int BRAIDED_YARN = 3133;
	private static final int JOURNEYMAN_GEM = 3134;
	private static final int PINTERS_INSTRUCTIONS = 3135;
	private static final int AMBER_BEAD = 3136;
	private static final int AMBER_LUMP = 3137;
	private static final int JOURNEYMAN_DECO_BEADS = 3138;
	private static final int JOURNEYMAN_RING = 3139;
	// Reward's
	private static final int ADENA = 57;
	private static final int MARK_OF_GUILDSMAN = 3119;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// Misc
	private static final Map<L2Npc, Integer> spoiled = new HashMap<>(); // Npc - playerObjectId
	
	public Q216_TrialOfGuildsman()
	{
		super(216, "Trial Of Guildsman");
		addStartNpc(VALKON);
		
		addTalkId(VALKON, NORMAN, ALTRAN, PINTER, DUNING);
		
		addSkillSeeId(ANT, ANT_CAPTAIN, ANT_OVERSEER);
		
		addKillId(MANDRAGORA_SPROUT, MANDRAGORA_SAPLING, MANDRAGORA_BLOSSOM, SILENOS, STRAIN);
		addKillId(GHOUL, DEAD_SEEKER, MANDRAGORA_SPROUT_1, BREKA_ORC, BREKA_ORC_ARCHER);
		addKillId(BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, BREKA_ORC_WARRIOR);
		addKillId(ANT, ANT_CAPTAIN, ANT_OVERSEER, GRANITE_GOLEM);
		
		registerItems(RP_JOURNEYMAN_RING, ALLTRANS_INSTRUCTIONS, VALKONS_RECOMMEND, MANDRAGORA_BERRY, ALLTRANS_RECOMMEND1, DUNINGS_KEY, NORMANS_INSTRUCTIONS, NORMANS_LIST, NORMANS_RECEIPT, ALLTRANS_RECOMMEND2, PINTERS_INSTRUCTIONS, AMBER_BEAD, RP_AMBER_BEAD, DUNINGS_INSTRUCTIONS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return getNoQuestMsg();
		}
		
		switch (event)
		{
			case "1":
				if (st.getItemsCount(ADENA) >= 2000)
				{
					st.startQuest();
					htmltext = "7103-06.htm";
					st.giveItems(VALKONS_RECOMMEND, 1);
					st.takeItems(ADENA, 2000);
				}
				else
				{
					htmltext = "7103-05a.htm";
				}
				break;
			
			case "7103_1":
				htmltext = "7103-04.htm";
				break;
			
			case "7103_2":
				if (st.getItemsCount(ADENA) >= 2000)
				{
					htmltext = "7103-05.htm";
				}
				else
				{
					htmltext = "7103-05a.htm";
				}
				break;
			
			case "7103_3":
				if (st.getState() != ScriptStateType.COMPLETED)
				{
					htmltext = "7103-09a.htm";
					st.rewardExpAndSp(32000, 3900);
					st.exitQuest(false, true);
					st.takeItems(JOURNEYMAN_RING, -1);
					st.takeItems(ALLTRANS_INSTRUCTIONS, 1);
					st.takeItems(RP_JOURNEYMAN_RING, 1);
					st.giveItems(DIMENSIONAL_DIAMOND, 61);
					st.giveItems(MARK_OF_GUILDSMAN, 1);
				}
				break;
			
			case "7103_4":
				htmltext = "7103-09b.htm";
				st.rewardExpAndSp(80933, 12250);
				st.giveItems(DIMENSIONAL_DIAMOND, 61);
				st.exitQuest(false, true);
				st.takeItems(JOURNEYMAN_RING, -1);
				st.takeItems(ALLTRANS_INSTRUCTIONS, 1);
				st.takeItems(RP_JOURNEYMAN_RING, 1);
				st.giveItems(DIMENSIONAL_DIAMOND, 61);
				st.giveItems(MARK_OF_GUILDSMAN, 1);
				break;
			
			case "7103_6a":
				htmltext = "7103-06a.htm";
				break;
			
			case "7103_6b":
				htmltext = "7103-06b.htm";
				break;
			
			case "7103_7c":
				htmltext = "7103-07c.htm";
				st.setCond(3);
				break;
			
			case "7283_1":
				htmltext = "7283-03.htm";
				st.takeItems(VALKONS_RECOMMEND, 1);
				st.takeItems(MANDRAGORA_BERRY, 1);
				st.giveItems(ALLTRANS_INSTRUCTIONS, 1);
				st.giveItems(RP_JOURNEYMAN_RING, 1);
				st.giveItems(ALLTRANS_RECOMMEND1, 1);
				st.giveItems(ALLTRANS_RECOMMEND2, 1);
				st.setCond(5);
				break;
			
			case "7210_1":
				htmltext = "7210-02.htm";
				break;
			
			case "7210_2":
				htmltext = "7210-03.htm";
				break;
			
			case "7103_7a":
				htmltext = "7103-07a.htm";
				st.setCond(3);
				break;
			
			case "7210_3":
				htmltext = "7210-04.htm";
				st.giveItems(NORMANS_INSTRUCTIONS, 1);
				st.takeItems(ALLTRANS_RECOMMEND1, 1);
				st.giveItems(NORMANS_RECEIPT, 1);
				break;
			
			case "7210_4":
				htmltext = "7210-08.htm";
				break;
			
			case "7210_5":
				htmltext = "7210-09.htm";
				break;
			
			case "7210_6":
				htmltext = "7210-10.htm";
				st.takeItems(DUNINGS_KEY, st.getItemsCount(DUNINGS_KEY));
				st.giveItems(NORMANS_LIST, 1);
				st.takeItems(NORMANS_INSTRUCTIONS, 1);
				break;
			
			case "7688_1":
				htmltext = "7688-02.htm";
				st.giveItems(DUNINGS_INSTRUCTIONS, 1);
				st.takeItems(NORMANS_RECEIPT, 1);
				break;
			
			case "7298_1":
				htmltext = "7298-03.htm";
				break;
			
			case "7298_2":
				if (st.getPlayer().getClassId().getId() == 0x36)
				{
					htmltext = "7298-04.htm";
					st.giveItems(PINTERS_INSTRUCTIONS, 1);
					st.takeItems(ALLTRANS_RECOMMEND2, 1);
				}
				else
				{
					htmltext = "7298-05.htm";
					st.giveItems(RP_AMBER_BEAD, 1);
					st.takeItems(ALLTRANS_RECOMMEND2, 1);
					st.giveItems(PINTERS_INSTRUCTIONS, 1);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		int npcId = npc.getId();
		String htmltext = getNoQuestMsg();
		ScriptStateType state = st.getState();
		
		switch (st.getState())
		{
			case CREATED:
				if (npcId == VALKON)
				{
					switch (st.getPlayer().getClassId())
					{
						case ARTISAN:
						case SCAVENGER:
							
							st.startQuest();
							
							if (st.getPlayer().getLevel() < 35)
							{
								htmltext = "7103-02.htm";
								st.exitQuest(true);
							}
							else
							{
								htmltext = "7103-03.htm";
							}
							break;
						
						default:
							htmltext = "7103-01.htm";
							st.exitQuest(true);
							break;
					}
				}
				
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case VALKON:
						if ((st.getCond() == 1) && (state == ScriptStateType.STARTED))
						{
							htmltext = "7103-06.htm";
						}
						else if ((st.getCond() == 2) && st.hasItems(VALKONS_RECOMMEND))
						{
							htmltext = "7103-07.htm";
						}
						else if ((st.getCond() == 3) && st.hasItems(VALKONS_RECOMMEND))
						{
							htmltext = "7103-07c.htm";
						}
						else if ((st.getCond() >= 3) && st.hasItems(ALLTRANS_INSTRUCTIONS))
						{
							if (st.getItemsCount(JOURNEYMAN_RING) < 7)
							{
								htmltext = "7103-08.htm";
							}
							else if (st.getCond() == 6)
							{
								htmltext = "7103-09.htm";
							}
						}
						break;
					
					case ALTRAN:
						if ((st.getCond() == 1) && st.hasItems(VALKONS_RECOMMEND) && !st.hasItems(MANDRAGORA_BERRY))
						{
							htmltext = "7283-01.htm";
							st.setCond(2);
						}
						else if ((st.getCond() == 4) && st.hasItems(VALKONS_RECOMMEND) && st.hasItems(MANDRAGORA_BERRY))
						{
							htmltext = "7283-02.htm";
						}
						else if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS))
						{
							if (st.getItemsCount(JOURNEYMAN_RING) < 7)
							{
								htmltext = "7283-04.htm";
							}
							else
							{
								htmltext = "7283-05.htm";
							}
						}
						break;
					
					case NORMAN:
						if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS))
						{
							if (st.hasItems(ALLTRANS_RECOMMEND1))
							{
								htmltext = "7210-01.htm";
							}
							else if (st.hasItems(NORMANS_INSTRUCTIONS))
							{
								if (st.hasItems(NORMANS_RECEIPT))
								{
									htmltext = "7210-05.htm";
								}
								else if (st.hasItems(DUNINGS_INSTRUCTIONS))
								{
									htmltext = "7210-06.htm";
								}
								else if (st.getItemsCount(DUNINGS_KEY) >= 30)
								{
									htmltext = "7210-07.htm";
								}
							}
							else if (st.hasItems(NORMANS_LIST))
							{
								if ((st.getItemsCount(GRAY_BONE_POWDER) >= 70) && (st.getItemsCount(GRANITE_WHETSTONE) >= 70) && (st.getItemsCount(RED_PIGMENT) >= 70) && (st.getItemsCount(BRAIDED_YARN) >= 70))
								{
									htmltext = "7210-12.htm";
									st.takeItems(NORMANS_LIST, 1);
									st.takeItems(GRAY_BONE_POWDER, st.getItemsCount(GRAY_BONE_POWDER));
									st.takeItems(GRANITE_WHETSTONE, st.getItemsCount(GRANITE_WHETSTONE));
									st.takeItems(RED_PIGMENT, st.getItemsCount(RED_PIGMENT));
									st.takeItems(BRAIDED_YARN, st.getItemsCount(BRAIDED_YARN));
									st.giveItems(JOURNEYMAN_GEM, 7);
									if (st.getItemsCount(JOURNEYMAN_DECO_BEADS) == 7)
									{
										st.setCond(6);
									}
								}
								else
								{
									htmltext = "7210-11.htm";
								}
							}
							else if (!st.hasItems(NORMANS_INSTRUCTIONS) && !st.hasItems(NORMANS_LIST) && (st.hasItems(JOURNEYMAN_GEM) || st.hasItems(JOURNEYMAN_RING)))
							{
								htmltext = "7210-13.htm";
							}
						}
						break;
					
					case DUNING:
						if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS))
						{
							if (st.hasItems(NORMANS_INSTRUCTIONS))
							{
								if (st.hasItems(NORMANS_RECEIPT))
								{
									htmltext = "7688-01.htm";
								}
								else if (st.hasItems(DUNINGS_INSTRUCTIONS))
								{
									htmltext = "7688-03.htm";
								}
								else if (st.getItemsCount(DUNINGS_KEY) >= 30)
								{
									htmltext = "7688-04.htm";
								}
							}
							else if (!st.hasItems(NORMANS_RECEIPT) && !st.hasItems(DUNINGS_INSTRUCTIONS) && !st.hasItems(DUNINGS_KEY))
							{
								htmltext = "7688-01.htm";
							}
						}
						break;
					
					case PINTER:
						if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, ALLTRANS_RECOMMEND2))
						{
							if (st.getPlayer().getLevel() < 36)
							{
								htmltext = "7298-01.htm";
							}
							else
							{
								htmltext = "7298-02.htm";
							}
						}
						else if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, PINTERS_INSTRUCTIONS))
						{
							if (st.getItemsCount(AMBER_BEAD) < 70)
							{
								htmltext = "7298-06.htm";
							}
							else
							{
								htmltext = "7298-07.htm";
								st.takeItems(PINTERS_INSTRUCTIONS, 1);
								st.takeItems(AMBER_BEAD, st.getItemsCount(AMBER_BEAD));
								st.takeItems(RP_AMBER_BEAD, st.getItemsCount(RP_AMBER_BEAD));
								st.giveItems(JOURNEYMAN_DECO_BEADS, 7);
								if (st.getItemsCount(JOURNEYMAN_GEM) == 7)
								{
									st.setCond(6);
								}
							}
						}
						else if ((st.getCond() >= 5) && st.hasItems(ALLTRANS_INSTRUCTIONS) && !st.hasItems(PINTERS_INSTRUCTIONS) && (st.hasItems(JOURNEYMAN_DECO_BEADS) || st.hasItems(JOURNEYMAN_RING)))
						{
							htmltext = "7298-08.htm";
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
		ScriptState st = player.getScriptState(getName());
		
		if (st != null)
		{
			if (st.getState() != ScriptStateType.STARTED)
			{
				return null;
			}
			
			switch (npc.getId())
			{
				
				case MANDRAGORA_SPROUT_1:
					if ((st.getCond() == 3) && st.hasItems(VALKONS_RECOMMEND) && !st.hasItems(MANDRAGORA_BERRY))
					{
						st.giveItems(MANDRAGORA_BERRY, 1);
						st.setCond(4, true);
					}
					break;
				case MANDRAGORA_SPROUT:
				case MANDRAGORA_SAPLING:
				case MANDRAGORA_BLOSSOM:
					if ((st.getCond() == 3) && st.hasItems(VALKONS_RECOMMEND) && !st.hasItems(MANDRAGORA_BERRY))
					{
						st.giveItems(MANDRAGORA_BERRY, 1);
						st.setCond(4, true);
					}
					break;
				
				case BREKA_ORC:
				case BREKA_ORC_ARCHER:
				case BREKA_ORC_WARRIOR:
				case BREKA_ORC_SHAMAN:
				case BREKA_ORC_OVERLORD:
					if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, NORMANS_INSTRUCTIONS, DUNINGS_INSTRUCTIONS))
					{
						if ((Rnd.get(100) <= 30) && (st.getItemsCount(DUNINGS_KEY) <= 29))
						{
							if (st.getItemsCount(DUNINGS_KEY) == 29)
							{
								st.giveItems(DUNINGS_KEY, 1);
								st.takeItems(DUNINGS_INSTRUCTIONS, 1);
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.giveItems(DUNINGS_KEY, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
					}
					break;
				
				case GHOUL:
				case STRAIN:
					if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (st.getItemsCount(GRAY_BONE_POWDER) < 70))
					{
						st.giveItems(GRAY_BONE_POWDER, 2);
						if (st.getItemsCount(GRAY_BONE_POWDER) >= 70)
						{
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case GRANITE_GOLEM:
					if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (st.getItemsCount(GRANITE_WHETSTONE) < 70))
					{
						st.giveItems(GRANITE_WHETSTONE, 7);
						if (st.getItemsCount(GRANITE_WHETSTONE) >= 70)
						{
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case DEAD_SEEKER:
					if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (st.getItemsCount(RED_PIGMENT) < 70))
					{
						st.giveItems(RED_PIGMENT, 7);
						if (st.getItemsCount(RED_PIGMENT) >= 70)
						{
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case SILENOS:
					if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, NORMANS_LIST) && (st.getItemsCount(BRAIDED_YARN) < 70))
					{
						st.giveItems(BRAIDED_YARN, 10);
						if (st.getItemsCount(BRAIDED_YARN) >= 70)
						{
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case ANT:
				case ANT_OVERSEER:
				case ANT_CAPTAIN:
					if (spoiled.containsKey(npc))
					{
						spoiled.remove(npc);
					}
					
					if ((st.getCond() == 5) && st.hasItems(ALLTRANS_INSTRUCTIONS, PINTERS_INSTRUCTIONS))
					{
						if (st.getItemsCount(AMBER_BEAD) < 70)
						{
							if (Rnd.get(100) <= 30)
							{
								st.giveItems(AMBER_BEAD, 1);
								if (st.getItemsCount(AMBER_BEAD) >= 70)
								{
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
								
							}
							else if (player.getClassId() == ClassId.ARTISAN)
							{
								st.giveItems(AMBER_LUMP, 1);
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
					}
					break;
			}
		}
		return null;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance player, Skill skill, List<L2Object> targets, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		if (st != null)
		{
			if (spoiled.containsKey(npc))
			{
				if (player.getObjectId() == spoiled.get(npc))
				{
					return null;
				}
			}
			
			if ((skill.getId() == 254) && (player.getClassId() == ClassId.SCAVENGER))
			{
				if (st.getItemsCount(AMBER_BEAD) < 70)
				{
					spoiled.put(npc, player.getObjectId());
					st.giveItems(AMBER_BEAD, 5);
					
					if (st.getItemsCount(AMBER_BEAD) >= 70)
					{
						st.playSound(PlaySoundType.QUEST_MIDDLE);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
			}
		}
		return null;
	}
}
