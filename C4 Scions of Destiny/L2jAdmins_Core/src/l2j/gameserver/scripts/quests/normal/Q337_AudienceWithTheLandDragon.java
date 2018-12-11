package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
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
public class Q337_AudienceWithTheLandDragon extends Script
{
	// Variables
	private static boolean jewel1 = false;
	private static boolean jewel2 = false;
	private static boolean jewel3 = false;
	
	// NPCs
	private static final int GABRIELLE = 7753;
	private static final int ORVEN = 7857; // 1
	private static final int KENDRA = 7851; // 2
	private static final int CHAKIRIS = 7705; // 3
	private static final int KAIENA = 7720; // 4
	private static final int MOKE = 7498; // 1st abyssal
	private static final int HELTON = 7678; // 2nd abyssal
	private static final int GILMORE = 7754; // 3rd abyssal
	private static final int THEODRIC = 7755;
	
	// Mobs
	private static final int BLOOD_QUEEN = 12079; // 1
	private static final int SACRIFICE_OF_THE_SACRIFICED = 5171; // 1
	private static final int HARIT_LIZARDMAN_SHAMAN = 644; // 2
	private static final int HARIT_LIZARDMAN_MATRIARCH = 645; // 2
	private static final int HARIT_LIZARDMAN_ZEALOT = 5172; // 2
	private static final int KRANROT = 650; // 3
	private static final int HAMRUT = 649; // 3
	private static final int MARSH_DRAKE = 680; // 4
	private static final int MARSH_STALKER = 679; // 4
	private static final int ABYSSAL_JEWEL_1 = 5165; // 1st abyssal
	private static final int JEWEL_GUARDIAN_MARA = 5168;
	private static final int ABYSSAL_JEWEL_2 = 5166; // 2nd abyssal
	private static final int JEWEL_GUARDIAN_MUSFEL = 5169;
	private static final int CAVE_MAIDEN_1 = 134; // 3rd abyssal
	private static final int CAVE_MAIDEN_2 = 287;
	private static final int CAVE_KEEPER_1 = 246;
	private static final int CAVE_KEEPER_2 = 277;
	private static final int ABYSSAL_JEWEL_3 = 5167;
	private static final int JEWEL_GUARDIAN_PYTON = 5170;
	
	// ITEMs
	private static final int FEATHER_OF_GABRIELLE = 3852;
	private static final int MARK_OF_WATCHMAN = 3864;
	private static final int REMAINS_OF_SACRIFIED = 3857; // 1
	private static final int TOTEM_OF_LAND_DRAGON = 3858; // 2
	private static final int KRANROT_SKIN = 3855; // 3
	private static final int HAMRUT_LEG = 3856; // 3
	private static final int MARSH_DRAKE_TALONS = 3854; // 4
	private static final int MARSH_STALKER_HORN = 3853; // 4
	private static final int FIRST_FRAGMENT_OF_ABYSS_JEWEL = 3859; // 1st abyssal
	private static final int MARA_FANG = 3862;
	private static final int SECOND_FRAGMENT_OF_ABYSS_JEWEL = 3860; // 2nd abyssal
	private static final int MUSFEL_FANG = 3863;
	private static final int HERALD_OF_SLAYER = 3890;
	private static final int THIRD_FRAGMENT_OF_ABYSS_JEWEL = 3861; // 3rd abyssal
	private static final int PORTAL_STONE = 3865;
	
	/**
	 * 0..npcId, 1..cond, 2..cond2, 3..chance, 4..itemId
	 */
	private static final int[][] DROPS_ON_KILL =
	{
		{
			SACRIFICE_OF_THE_SACRIFICED,
			1,
			1,
			REMAINS_OF_SACRIFIED
		},
		{
			HARIT_LIZARDMAN_ZEALOT,
			1,
			2,
			TOTEM_OF_LAND_DRAGON
		},
		{
			KRANROT,
			1,
			3,
			KRANROT_SKIN
		},
		{
			HAMRUT,
			1,
			3,
			HAMRUT_LEG
		},
		{
			MARSH_DRAKE,
			1,
			4,
			MARSH_DRAKE_TALONS
		},
		{
			MARSH_STALKER,
			1,
			4,
			MARSH_STALKER_HORN
		},
		{
			JEWEL_GUARDIAN_MARA,
			2,
			5,
			MARA_FANG
		},
		{
			JEWEL_GUARDIAN_MUSFEL,
			2,
			6,
			MUSFEL_FANG
		}
	};
	
	/**
	 * 0..npcId, 1..cond, 2..cond2, 3..itemId, 4..amount of mobs, 5..mob
	 */
	private static final int[][] DROP_ON_ATTACK =
	{
		{
			ABYSSAL_JEWEL_1,
			2,
			5,
			FIRST_FRAGMENT_OF_ABYSS_JEWEL,
			20,
			JEWEL_GUARDIAN_MARA
		},
		{
			ABYSSAL_JEWEL_2,
			2,
			6,
			SECOND_FRAGMENT_OF_ABYSS_JEWEL,
			20,
			JEWEL_GUARDIAN_MUSFEL
		},
		{
			ABYSSAL_JEWEL_3,
			4,
			7,
			THIRD_FRAGMENT_OF_ABYSS_JEWEL,
			3,
			JEWEL_GUARDIAN_PYTON
		},
	};
	
	public Q337_AudienceWithTheLandDragon()
	{
		super(337, "Audience with the Land Dragon");
		
		registerItems(FEATHER_OF_GABRIELLE, MARK_OF_WATCHMAN, REMAINS_OF_SACRIFIED, TOTEM_OF_LAND_DRAGON, KRANROT_SKIN, HAMRUT_LEG, MARSH_DRAKE_TALONS, MARSH_STALKER_HORN, FIRST_FRAGMENT_OF_ABYSS_JEWEL, MARA_FANG, SECOND_FRAGMENT_OF_ABYSS_JEWEL, MUSFEL_FANG, HERALD_OF_SLAYER, THIRD_FRAGMENT_OF_ABYSS_JEWEL);
		
		addStartNpc(GABRIELLE);
		addTalkId(GABRIELLE, ORVEN, KENDRA, CHAKIRIS, KAIENA, MOKE, HELTON, GILMORE, THEODRIC);
		
		addAttackId(ABYSSAL_JEWEL_1, ABYSSAL_JEWEL_2, ABYSSAL_JEWEL_3);
		addKillId(BLOOD_QUEEN, SACRIFICE_OF_THE_SACRIFICED, HARIT_LIZARDMAN_SHAMAN, HARIT_LIZARDMAN_MATRIARCH, HARIT_LIZARDMAN_ZEALOT, KRANROT, HAMRUT, MARSH_DRAKE, MARSH_STALKER, JEWEL_GUARDIAN_MARA, JEWEL_GUARDIAN_MUSFEL, CAVE_MAIDEN_1, CAVE_MAIDEN_2, CAVE_KEEPER_1, CAVE_KEEPER_2, JEWEL_GUARDIAN_PYTON);
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
		
		// Gabrielle
		if (event.equalsIgnoreCase("7753-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.set("drop1", "1");
			st.set("drop2", "1");
			st.set("drop3", "1");
			st.set("drop4", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(FEATHER_OF_GABRIELLE, 1);
		}
		else if (event.equalsIgnoreCase("7753-09.htm"))
		{
			if (st.getItemsCount(MARK_OF_WATCHMAN) >= 4)
			{
				st.set("cond", "2");
				st.set("drop5", "2");
				st.set("drop6", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(MARK_OF_WATCHMAN, 4);
			}
			else
			{
				htmltext = null;
			}
		}
		// Theodric
		else if (event.equalsIgnoreCase("7755-05.htm"))
		{
			if (st.hasItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL))
			{
				st.takeItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL, 1);
				st.takeItems(HERALD_OF_SLAYER, 1);
				st.giveItems(PORTAL_STONE, 1);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				htmltext = null;
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
				htmltext = player.getLevel() < 50 ? "7753-02.htm" : "7753-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case GABRIELLE:
						if (cond == 1)
						{
							htmltext = st.getItemsCount(MARK_OF_WATCHMAN) < 4 ? "7753-06.htm" : "7753-08.htm";
						}
						else if (cond == 2)
						{
							if (st.getItemsCount(MARK_OF_WATCHMAN) < 2)
							{
								htmltext = "7753-10.htm";
							}
							else
							{
								htmltext = "7753-11.htm";
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(FEATHER_OF_GABRIELLE, 1);
								st.takeItems(MARK_OF_WATCHMAN, 1);
								st.giveItems(HERALD_OF_SLAYER, 1);
							}
						}
						else if (cond == 3)
						{
							htmltext = "7753-12.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7753-13.htm";
						}
						break;
					
					case ORVEN:
						if (cond == 1)
						{
							if (st.getInt("drop1") == 1)
							{
								if (st.hasItems(REMAINS_OF_SACRIFIED))
								{
									htmltext = "7857-02.htm";
									st.unset("drop1");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(REMAINS_OF_SACRIFIED, 1);
									st.giveItems(MARK_OF_WATCHMAN, 1);
								}
								else
								{
									htmltext = "7857-01.htm";
								}
							}
							else if (st.getItemsCount(MARK_OF_WATCHMAN) < 4)
							{
								htmltext = "7857-03.htm";
							}
							else
							{
								htmltext = "7857-04.htm";
							}
						}
						break;
					
					case KENDRA:
						if (cond == 1)
						{
							if (st.getInt("drop2") == 1)
							{
								if (st.hasItems(TOTEM_OF_LAND_DRAGON))
								{
									htmltext = "7851-02.htm";
									st.unset("drop2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(TOTEM_OF_LAND_DRAGON, 1);
									st.giveItems(MARK_OF_WATCHMAN, 1);
								}
								else
								{
									htmltext = "7851-01.htm";
								}
							}
							else if (st.getItemsCount(MARK_OF_WATCHMAN) < 4)
							{
								htmltext = "7851-03.htm";
							}
							else
							{
								htmltext = "7851-04.htm";
							}
						}
						break;
					
					case CHAKIRIS:
						if (cond == 1)
						{
							if (st.getInt("drop3") == 1)
							{
								if (st.hasItems(KRANROT_SKIN, HAMRUT_LEG))
								{
									htmltext = "7705-02.htm";
									st.unset("drop3");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(KRANROT_SKIN, 1);
									st.takeItems(HAMRUT_LEG, 1);
									st.giveItems(MARK_OF_WATCHMAN, 1);
								}
								else
								{
									htmltext = "7705-01.htm";
								}
							}
							else if (st.getItemsCount(MARK_OF_WATCHMAN) < 4)
							{
								htmltext = "7705-03.htm";
							}
							else
							{
								htmltext = "7705-04.htm";
							}
						}
						break;
					
					case KAIENA:
						if (cond == 1)
						{
							if (st.getInt("drop4") == 1)
							{
								if (st.hasItems(MARSH_DRAKE_TALONS, MARSH_STALKER_HORN))
								{
									htmltext = "7720-02.htm";
									st.unset("drop4");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
									st.takeItems(MARSH_DRAKE_TALONS, 1);
									st.takeItems(MARSH_STALKER_HORN, 1);
									st.giveItems(MARK_OF_WATCHMAN, 1);
								}
								else
								{
									htmltext = "7720-01.htm";
								}
							}
							else if (st.getItemsCount(MARK_OF_WATCHMAN) < 4)
							{
								htmltext = "7720-03.htm";
							}
							else
							{
								htmltext = "7720-04.htm";
							}
						}
						break;
					
					case MOKE:
						if (cond == 2)
						{
							switch (st.getInt("drop5"))
							{
								case 2:
									htmltext = "7498-01.htm";
									st.set("drop5", "1");
									break;
								
								case 1:
									if (st.hasItems(FIRST_FRAGMENT_OF_ABYSS_JEWEL, MARA_FANG))
									{
										htmltext = "7498-03.htm";
										st.unset("drop5");
										st.playSound(PlaySoundType.QUEST_MIDDLE);
										st.takeItems(FIRST_FRAGMENT_OF_ABYSS_JEWEL, 1);
										st.takeItems(MARA_FANG, 1);
										st.giveItems(MARK_OF_WATCHMAN, 1);
									}
									else
									{
										htmltext = "7498-02.htm";
									}
									break;
								
								case 0:
									if (st.getItemsCount(MARK_OF_WATCHMAN) < 2)
									{
										htmltext = "7498-04.htm";
									}
									else
									{
										htmltext = "7498-05.htm";
									}
									break;
							}
						}
						break;
					
					case HELTON:
						if (cond == 2)
						{
							switch (st.getInt("drop6"))
							{
								case 2:
									htmltext = "7678-01.htm";
									st.set("drop6", "1");
									break;
								
								case 1:
									if (st.hasItems(SECOND_FRAGMENT_OF_ABYSS_JEWEL, MUSFEL_FANG))
									{
										htmltext = "7678-03.htm";
										st.unset("drop6");
										st.playSound(PlaySoundType.QUEST_MIDDLE);
										st.takeItems(SECOND_FRAGMENT_OF_ABYSS_JEWEL, 1);
										st.takeItems(MUSFEL_FANG, 1);
										st.giveItems(MARK_OF_WATCHMAN, 1);
									}
									else
									{
										htmltext = "7678-02.htm";
									}
									break;
								
								case 0:
									if (st.getItemsCount(MARK_OF_WATCHMAN) < 2)
									{
										htmltext = "7678-04.htm";
									}
									else
									{
										htmltext = "7678-05.htm";
									}
									break;
							}
						}
						break;
					
					case GILMORE:
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "7754-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7754-02.htm";
							st.set("cond", "4");
							st.set("drop7", "1");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 4)
						{
							htmltext = !st.hasItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL) ? "7754-04.htm" : "7754-05.htm";
						}
						break;
					
					case THEODRIC:
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "7755-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7755-02.htm";
						}
						else if (cond == 4)
						{
							htmltext = !st.hasItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL) ? "7755-03.htm" : "7755-04.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		final ScriptState st = checkPlayerState(attacker, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return super.onAttack(npc, attacker, damage, isPet);
		}
		
		final int npcId = npc.getId();
		
		for (final int[] npcInfo : DROP_ON_ATTACK)
		{
			if (npcInfo[0] != npcId)
			{
				continue;
			}
			
			if (npcInfo[1] != st.getInt("cond"))
			{
				break;
			}
			
			final double percentHp = ((npc.getCurrentHp() + damage) * 100) / npc.getStat().getMaxHp();
			
			// reward jewel fragment
			if (percentHp < 33)
			{
				if ((Rnd.get(100) < 33) && (st.getInt("drop" + npcInfo[2]) == 1))
				{
					final int itemId = npcInfo[3];
					if (!st.hasItems(itemId))
					{
						st.giveItems(itemId, 1);
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
				}
			}
			// spawn monsters and register spawned
			else if (percentHp < 66)
			{
				if ((Rnd.get(100) < 33) && (st.getInt("drop" + npcInfo[2]) == 1))
				{
					boolean spawn;
					if (npcId == ABYSSAL_JEWEL_3)
					{
						spawn = jewel3;
					}
					else if (npcId == ABYSSAL_JEWEL_2)
					{
						spawn = jewel2;
					}
					else
					{
						spawn = jewel1;
					}
					
					if (spawn)
					{
						for (int i = 0; i < npcInfo[4]; i++)
						{
							final L2Npc mob = addSpawn(npcInfo[5], npc.getX() + Rnd.get(-150, 150), npc.getY() + Rnd.get(-150, 150), npc.getZ(), npc.getHeading(), true, 60000);
							mob.setRunning();
							((L2Attackable) mob).addDamageHate(attacker, 0, 500);
							mob.getAI().setIntention(CtrlIntentionType.ATTACK, attacker);
						}
						
						if (npcId == ABYSSAL_JEWEL_3)
						{
							jewel3 = false;
						}
						else if (npcId == ABYSSAL_JEWEL_2)
						{
							jewel2 = false;
						}
						else
						{
							jewel1 = false;
						}
					}
				}
				
			}
			// reset spawned if npc regenerated to 90% HP and more
			else if (percentHp > 90)
			{
				if (npcId == ABYSSAL_JEWEL_3)
				{
					jewel3 = true;
				}
				else if (npcId == ABYSSAL_JEWEL_2)
				{
					jewel2 = true;
				}
				else
				{
					jewel1 = true;
				}
			}
			break;
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int cond = st.getInt("cond");
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case SACRIFICE_OF_THE_SACRIFICED: // Orven's request
			case HARIT_LIZARDMAN_ZEALOT: // Kendra's request
			case KRANROT:// Chakiris's request
			case HAMRUT:
			case MARSH_DRAKE:// Kaiena's request
			case MARSH_STALKER:
			case JEWEL_GUARDIAN_MARA:// Moke's request
			case JEWEL_GUARDIAN_MUSFEL:// Helton's request
				for (final int[] npcInfo : DROPS_ON_KILL)
				{
					if (npcInfo[0] != npcId)
					{
						continue;
					}
					
					if ((npcInfo[1] == cond) && (st.getInt("drop" + npcInfo[2]) == 1))
					{
						final int itemId = npcInfo[3];
						if (!st.hasItems(itemId))
						{
							st.giveItems(itemId, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				}
				break;
			
			case BLOOD_QUEEN:// Orven's request
				if ((cond == 1) && (st.getInt("drop1") == 1) && !st.hasItems(REMAINS_OF_SACRIFIED))
				{
					for (int i = 0; i < 8; i++)
					{
						addSpawn(SACRIFICE_OF_THE_SACRIFICED, npc.getX() + Rnd.get(-100, 100), npc.getY() + Rnd.get(-100, 100), npc.getZ(), npc.getHeading(), true, 60000);
					}
				}
				break;
			
			case HARIT_LIZARDMAN_SHAMAN:// Kendra's request
			case HARIT_LIZARDMAN_MATRIARCH:
				if ((cond == 1) && (Rnd.get(5) == 0) && (st.getInt("drop2") == 1) && !st.hasItems(TOTEM_OF_LAND_DRAGON))
				{
					for (int i = 0; i < 3; i++)
					{
						addSpawn(HARIT_LIZARDMAN_ZEALOT, npc.getX() + Rnd.get(-50, 50), npc.getY() + Rnd.get(-50, 50), npc.getZ(), npc.getHeading(), true, 60000);
					}
				}
				break;
			
			case CAVE_MAIDEN_1:// Gilmore's request
			case CAVE_MAIDEN_2:
			case CAVE_KEEPER_1:
			case CAVE_KEEPER_2:
				if ((cond == 4) && (Rnd.get(5) == 0) && !st.hasItems(THIRD_FRAGMENT_OF_ABYSS_JEWEL))
				{
					addSpawn(ABYSSAL_JEWEL_3, npc.getX() + Rnd.get(-50, 50), npc.getY() + Rnd.get(-50, 50), npc.getZ(), npc.getHeading(), true, 60000);
				}
				break;
		}
		
		return null;
	}
	
}
