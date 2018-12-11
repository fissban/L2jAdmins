package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
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
public class Q233_TestOfTheWarSpirit extends Script
{
	// Items
	private static final int VENDETTA_TOTEM = 2880;
	private static final int TAMLIN_ORC_HEAD = 2881;
	private static final int WARSPIRIT_TOTEM = 2882;
	private static final int ORIM_CONTRACT = 2883;
	private static final int PORTA_EYE = 2884;
	private static final int EXCURO_SCALE = 2885;
	private static final int MORDEO_TALON = 2886;
	private static final int BRAKI_REMAINS_1 = 2887;
	private static final int PEKIRON_TOTEM = 2888;
	private static final int TONAR_SKULL = 2889;
	private static final int TONAR_RIBBONE = 2890;
	private static final int TONAR_SPINE = 2891;
	private static final int TONAR_ARMBONE = 2892;
	private static final int TONAR_THIGHBONE = 2893;
	private static final int TONAR_REMAINS_1 = 2894;
	private static final int MANAKIA_TOTEM = 2895;
	private static final int HERMODT_SKULL = 2896;
	private static final int HERMODT_RIBBONE = 2897;
	private static final int HERMODT_SPINE = 2898;
	private static final int HERMODT_ARMBONE = 2899;
	private static final int HERMODT_THIGHBONE = 2900;
	private static final int HERMODT_REMAINS_1 = 2901;
	private static final int RACOY_TOTEM = 2902;
	private static final int VIVYAN_LETTER = 2903;
	private static final int INSECT_DIAGRAM_BOOK = 2904;
	private static final int KIRUNA_SKULL = 2905;
	private static final int KIRUNA_RIBBONE = 2906;
	private static final int KIRUNA_SPINE = 2907;
	private static final int KIRUNA_ARMBONE = 2908;
	private static final int KIRUNA_THIGHBONE = 2909;
	private static final int KIRUNA_REMAINS_1 = 2910;
	private static final int BRAKI_REMAINS_2 = 2911;
	private static final int TONAR_REMAINS_2 = 2912;
	private static final int HERMODT_REMAINS_2 = 2913;
	private static final int KIRUNA_REMAINS_2 = 2914;
	
	// Rewards
	private static final int MARK_OF_WARSPIRIT = 2879;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// NPCs
	private static final int VIVYAN = 7030;
	private static final int SARIEN = 7436;
	private static final int RACOY = 7507;
	private static final int SOMAK = 7510;
	private static final int MANAKIA = 7515;
	private static final int ORIM = 7630;
	private static final int ANCESTOR_MARTANKUS = 7649;
	private static final int PEKIRON = 7682;
	
	// Monsters
	private static final int NOBLE_ANT = 89;
	private static final int NOBLE_ANT_LEADER = 90;
	private static final int MEDUSA = 158;
	private static final int PORTA = 213;
	private static final int EXCURO = 214;
	private static final int MORDEO = 215;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int TAMLIN_ORC = 601;
	private static final int TAMLIN_ORC_ARCHER = 602;
	private static final int STENOA_GORGON_QUEEN = 5108;
	
	public Q233_TestOfTheWarSpirit()
	{
		super(233, "Test of the War Spirit");
		
		registerItems(VENDETTA_TOTEM, TAMLIN_ORC_HEAD, WARSPIRIT_TOTEM, ORIM_CONTRACT, PORTA_EYE, EXCURO_SCALE, MORDEO_TALON, BRAKI_REMAINS_1, PEKIRON_TOTEM, TONAR_SKULL, TONAR_RIBBONE, TONAR_SPINE, TONAR_ARMBONE, TONAR_THIGHBONE, TONAR_REMAINS_1, MANAKIA_TOTEM, HERMODT_SKULL, HERMODT_RIBBONE, HERMODT_SPINE, HERMODT_ARMBONE, HERMODT_THIGHBONE, HERMODT_REMAINS_1, RACOY_TOTEM, VIVYAN_LETTER, INSECT_DIAGRAM_BOOK, KIRUNA_SKULL, KIRUNA_RIBBONE, KIRUNA_SPINE, KIRUNA_ARMBONE, KIRUNA_THIGHBONE, KIRUNA_REMAINS_1, BRAKI_REMAINS_2, TONAR_REMAINS_2, HERMODT_REMAINS_2, KIRUNA_REMAINS_2);
		
		addStartNpc(SOMAK);
		addTalkId(SOMAK, VIVYAN, SARIEN, RACOY, MANAKIA, ORIM, ANCESTOR_MARTANKUS, PEKIRON);
		addKillId(NOBLE_ANT, NOBLE_ANT_LEADER, MEDUSA, PORTA, EXCURO, MORDEO, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, STENOA_GORGON_QUEEN);
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
		
		// SOMAK
		if (event.equalsIgnoreCase("7510-05e.htm"))
		{
			st.startQuest();
			st.giveItems(DIMENSIONAL_DIAMOND, 92);
		}
		// ORIM
		else if (event.equalsIgnoreCase("7630-04.htm"))
		{
			st.giveItems(ORIM_CONTRACT, 1);
		}
		else if (event.equalsIgnoreCase("7507-02.htm"))
		{
			st.giveItems(RACOY_TOTEM, 1);
		}
		else if (event.equalsIgnoreCase("7030-04.htm"))
		{
			st.giveItems(VIVYAN_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7682-02.htm"))
		{
			st.giveItems(PEKIRON_TOTEM, 1);
		}
		else if (event.equalsIgnoreCase("7515-02.htm"))
		{
			st.giveItems(MANAKIA_TOTEM, 1);
		}
		else if (event.equalsIgnoreCase("7649-03.htm"))
		{
			st.takeItems(TAMLIN_ORC_HEAD, -1);
			st.takeItems(WARSPIRIT_TOTEM, -1);
			st.takeItems(BRAKI_REMAINS_2, -1);
			st.takeItems(HERMODT_REMAINS_2, -1);
			st.takeItems(KIRUNA_REMAINS_2, -1);
			st.takeItems(TONAR_REMAINS_2, -1);
			st.giveItems(MARK_OF_WARSPIRIT, 1);
			st.rewardExpAndSp(63483, 17500);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.exitQuest(false, true);
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
				if (player.getClassId() == ClassId.SHAMAN)
				{
					htmltext = (player.getLevel() < 39) ? "7510-03.htm" : "7510-04.htm";
				}
				else
				{
					htmltext = (player.getRace() == Race.ORC) ? "7510-02.htm" : "7510-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case SOMAK:
						if (cond == 1)
						{
							htmltext = "7510-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7510-07.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(BRAKI_REMAINS_1, 1);
							st.takeItems(HERMODT_REMAINS_1, 1);
							st.takeItems(KIRUNA_REMAINS_1, 1);
							st.takeItems(TONAR_REMAINS_1, 1);
							st.giveItems(VENDETTA_TOTEM, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7510-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7510-09.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(VENDETTA_TOTEM, 1);
							st.giveItems(BRAKI_REMAINS_2, 1);
							st.giveItems(HERMODT_REMAINS_2, 1);
							st.giveItems(KIRUNA_REMAINS_2, 1);
							st.giveItems(TONAR_REMAINS_2, 1);
							st.giveItems(WARSPIRIT_TOTEM, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7510-10.htm";
						}
						break;
					
					case ORIM:
						if ((cond == 1) && !st.hasItems(BRAKI_REMAINS_1))
						{
							if (!st.hasItems(ORIM_CONTRACT))
							{
								htmltext = "7630-01.htm";
							}
							else if ((st.getItemsCount(PORTA_EYE) + st.getItemsCount(EXCURO_SCALE) + st.getItemsCount(MORDEO_TALON)) == 30)
							{
								htmltext = "7630-06.htm";
								st.takeItems(EXCURO_SCALE, 10);
								st.takeItems(MORDEO_TALON, 10);
								st.takeItems(PORTA_EYE, 10);
								st.takeItems(ORIM_CONTRACT, 1);
								st.giveItems(BRAKI_REMAINS_1, 1);
								
								if (st.hasItems(HERMODT_REMAINS_1, KIRUNA_REMAINS_1, TONAR_REMAINS_1))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else
							{
								htmltext = "7630-05.htm";
							}
						}
						else
						{
							htmltext = "7630-07.htm";
						}
						break;
					
					case RACOY:
						if ((cond == 1) && !st.hasItems(KIRUNA_REMAINS_1))
						{
							if (!st.hasItems(RACOY_TOTEM))
							{
								htmltext = "7507-01.htm";
							}
							else if (st.hasItems(VIVYAN_LETTER))
							{
								htmltext = "7507-04.htm";
							}
							else if (st.hasItems(INSECT_DIAGRAM_BOOK))
							{
								if (st.hasItems(KIRUNA_ARMBONE, KIRUNA_RIBBONE, KIRUNA_SKULL, KIRUNA_SPINE, KIRUNA_THIGHBONE))
								{
									htmltext = "7507-06.htm";
									st.takeItems(INSECT_DIAGRAM_BOOK, 1);
									st.takeItems(RACOY_TOTEM, 1);
									st.takeItems(KIRUNA_ARMBONE, 1);
									st.takeItems(KIRUNA_RIBBONE, 1);
									st.takeItems(KIRUNA_SKULL, 1);
									st.takeItems(KIRUNA_SPINE, 1);
									st.takeItems(KIRUNA_THIGHBONE, 1);
									st.giveItems(KIRUNA_REMAINS_1, 1);
									
									if (st.hasItems(BRAKI_REMAINS_1, HERMODT_REMAINS_1, TONAR_REMAINS_1))
									{
										st.set("cond", "2");
										st.playSound(PlaySoundType.QUEST_MIDDLE);
									}
								}
								else
								{
									htmltext = "7507-05.htm";
								}
							}
							else
							{
								htmltext = "7507-03.htm";
							}
						}
						else
						{
							htmltext = "7507-07.htm";
						}
						break;
					
					case VIVYAN:
						if ((cond == 1) && st.hasItems(RACOY_TOTEM))
						{
							if (st.hasItems(VIVYAN_LETTER))
							{
								htmltext = "7030-05.htm";
							}
							else if (st.hasItems(INSECT_DIAGRAM_BOOK))
							{
								htmltext = "7030-06.htm";
							}
							else
							{
								htmltext = "7030-01.htm";
							}
						}
						else
						{
							htmltext = "7030-07.htm";
						}
						break;
					
					case SARIEN:
						if ((cond == 1) && st.hasItems(RACOY_TOTEM))
						{
							if (st.hasItems(VIVYAN_LETTER))
							{
								htmltext = "7436-01.htm";
								st.takeItems(VIVYAN_LETTER, 1);
								st.giveItems(INSECT_DIAGRAM_BOOK, 1);
							}
							else if (st.hasItems(INSECT_DIAGRAM_BOOK))
							{
								htmltext = "7436-02.htm";
							}
						}
						else
						{
							htmltext = "7436-03.htm";
						}
						break;
					
					case PEKIRON:
						if ((cond == 1) && !st.hasItems(TONAR_REMAINS_1))
						{
							if (!st.hasItems(PEKIRON_TOTEM))
							{
								htmltext = "7682-01.htm";
							}
							else if (st.hasItems(TONAR_ARMBONE, TONAR_RIBBONE, TONAR_SKULL, TONAR_SPINE, TONAR_THIGHBONE))
							{
								htmltext = "7682-04.htm";
								st.takeItems(PEKIRON_TOTEM, 1);
								st.takeItems(TONAR_ARMBONE, 1);
								st.takeItems(TONAR_RIBBONE, 1);
								st.takeItems(TONAR_SKULL, 1);
								st.takeItems(TONAR_SPINE, 1);
								st.takeItems(TONAR_THIGHBONE, 1);
								st.giveItems(TONAR_REMAINS_1, 1);
								
								if (st.hasItems(BRAKI_REMAINS_1, HERMODT_REMAINS_1, KIRUNA_REMAINS_1))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else
							{
								htmltext = "7682-03.htm";
							}
						}
						else
						{
							htmltext = "7682-05.htm";
						}
						break;
					
					case MANAKIA:
						if ((cond == 1) && !st.hasItems(HERMODT_REMAINS_1))
						{
							if (!st.hasItems(MANAKIA_TOTEM))
							{
								htmltext = "7515-01.htm";
							}
							else if (st.hasItems(HERMODT_ARMBONE, HERMODT_RIBBONE, HERMODT_SKULL, HERMODT_SPINE, HERMODT_THIGHBONE))
							{
								htmltext = "7515-04.htm";
								st.takeItems(MANAKIA_TOTEM, 1);
								st.takeItems(HERMODT_ARMBONE, 1);
								st.takeItems(HERMODT_RIBBONE, 1);
								st.takeItems(HERMODT_SKULL, 1);
								st.takeItems(HERMODT_SPINE, 1);
								st.takeItems(HERMODT_THIGHBONE, 1);
								st.giveItems(HERMODT_REMAINS_1, 1);
								
								if (st.hasItems(BRAKI_REMAINS_1, KIRUNA_REMAINS_1, TONAR_REMAINS_1))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
							}
							else
							{
								htmltext = "7515-03.htm";
							}
						}
						else
						{
							htmltext = "7515-05.htm";
						}
						break;
					
					case ANCESTOR_MARTANKUS:
						if (cond == 5)
						{
							htmltext = "7649-01.htm";
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
		
		switch (npc.getId())
		{
			case PORTA:
				if (st.hasItems(ORIM_CONTRACT))
				{
					st.dropItemsAlways(PORTA_EYE, 1, 10);
				}
				break;
			
			case EXCURO:
				if (st.hasItems(ORIM_CONTRACT))
				{
					st.dropItemsAlways(EXCURO_SCALE, 1, 10);
				}
				break;
			
			case MORDEO:
				if (st.hasItems(ORIM_CONTRACT))
				{
					st.dropItemsAlways(MORDEO_TALON, 1, 10);
				}
				break;
			
			case NOBLE_ANT:
			case NOBLE_ANT_LEADER:
				if (st.hasItems(INSECT_DIAGRAM_BOOK))
				{
					int rndAnt = Rnd.get(100);
					if (rndAnt > 70)
					{
						if (st.hasItems(KIRUNA_THIGHBONE))
						{
							st.dropItemsAlways(KIRUNA_ARMBONE, 1, 1);
						}
						else
						{
							st.dropItemsAlways(KIRUNA_THIGHBONE, 1, 1);
						}
					}
					else if (rndAnt > 40)
					{
						if (st.hasItems(KIRUNA_SPINE))
						{
							st.dropItemsAlways(KIRUNA_RIBBONE, 1, 1);
						}
						else
						{
							st.dropItemsAlways(KIRUNA_SPINE, 1, 1);
						}
					}
					else if (rndAnt > 10)
					{
						st.dropItemsAlways(KIRUNA_SKULL, 1, 1);
					}
				}
				break;
			
			case LETO_LIZARDMAN_SHAMAN:
			case LETO_LIZARDMAN_OVERLORD:
				if (st.hasItems(PEKIRON_TOTEM) && Rnd.nextBoolean())
				{
					if (!st.hasItems(TONAR_SKULL))
					{
						st.dropItemsAlways(TONAR_SKULL, 1, 1);
					}
					else if (!st.hasItems(TONAR_RIBBONE))
					{
						st.dropItemsAlways(TONAR_RIBBONE, 1, 1);
					}
					else if (!st.hasItems(TONAR_SPINE))
					{
						st.dropItemsAlways(TONAR_SPINE, 1, 1);
					}
					else if (!st.hasItems(TONAR_ARMBONE))
					{
						st.dropItemsAlways(TONAR_ARMBONE, 1, 1);
					}
					else
					{
						st.dropItemsAlways(TONAR_THIGHBONE, 1, 1);
					}
				}
				break;
			
			case MEDUSA:
				if (st.hasItems(MANAKIA_TOTEM) && Rnd.nextBoolean())
				{
					if (!st.hasItems(HERMODT_RIBBONE))
					{
						st.dropItemsAlways(HERMODT_RIBBONE, 1, 1);
					}
					else if (!st.hasItems(HERMODT_SPINE))
					{
						st.dropItemsAlways(HERMODT_SPINE, 1, 1);
					}
					else if (!st.hasItems(HERMODT_ARMBONE))
					{
						st.dropItemsAlways(HERMODT_ARMBONE, 1, 1);
					}
					else
					{
						st.dropItemsAlways(HERMODT_THIGHBONE, 1, 1);
					}
				}
				break;
			
			case STENOA_GORGON_QUEEN:
				if (st.hasItems(MANAKIA_TOTEM))
				{
					st.dropItemsAlways(HERMODT_SKULL, 1, 1);
				}
				break;
			
			case TAMLIN_ORC:
			case TAMLIN_ORC_ARCHER:
				if (st.hasItems(VENDETTA_TOTEM) && st.dropItems(TAMLIN_ORC_HEAD, 1, 13, 500000))
				{
					st.set("cond", "4");
				}
				break;
		}
		
		return null;
	}
}
