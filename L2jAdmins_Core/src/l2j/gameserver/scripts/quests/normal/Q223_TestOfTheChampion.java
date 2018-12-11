package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.base.ClassId;
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
public class Q223_TestOfTheChampion extends Script
{
	// Items
	private static final int ASCALON_LETTER_1 = 3277;
	private static final int MASON_LETTER = 3278;
	private static final int IRON_ROSE_RING = 3279;
	private static final int ASCALON_LETTER_2 = 3280;
	private static final int WHITE_ROSE_INSIGNIA = 3281;
	private static final int GROOT_LETTER = 3282;
	private static final int ASCALON_LETTER_3 = 3283;
	private static final int MOUEN_ORDER_1 = 3284;
	private static final int MOUEN_ORDER_2 = 3285;
	private static final int MOUEN_LETTER = 3286;
	private static final int HARPY_EGG = 3287;
	private static final int MEDUSA_VENOM = 3288;
	private static final int WINDSUS_BILE = 3289;
	private static final int BLOODY_AXE_HEAD = 3290;
	private static final int ROAD_RATMAN_HEAD = 3291;
	private static final int LETO_LIZARDMAN_FANG = 3292;
	
	// Rewards
	private static final int MARK_OF_CHAMPION = 3276;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// NPCs
	private static final int ASCALON = 7624;
	private static final int GROOT = 7093;
	private static final int MOUEN = 7196;
	private static final int MASON = 7625;
	
	// Monsters
	private static final int HARPY = 145;
	private static final int HARPY_MATRIARCH = 5088;
	private static final int MEDUSA = 158;
	private static final int WINDSUS = 553;
	private static final int ROAD_COLLECTOR = 5089;
	private static final int ROAD_SCAVENGER = 551;
	private static final int LETO_LIZARDMAN = 577;
	private static final int LETO_LIZARDMAN_ARCHER = 578;
	private static final int LETO_LIZARDMAN_SOLDIER = 579;
	private static final int LETO_LIZARDMAN_WARRIOR = 580;
	private static final int LETO_LIZARDMAN_SHAMAN = 581;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int BLOODY_AXE_ELITE = 780;
	
	public Q223_TestOfTheChampion()
	{
		super(223, "Test of the Champion");
		
		registerItems(MASON_LETTER, MEDUSA_VENOM, WINDSUS_BILE, WHITE_ROSE_INSIGNIA, HARPY_EGG, GROOT_LETTER, MOUEN_LETTER, ASCALON_LETTER_1, IRON_ROSE_RING, BLOODY_AXE_HEAD, ASCALON_LETTER_2, ASCALON_LETTER_3, MOUEN_ORDER_1, ROAD_RATMAN_HEAD, MOUEN_ORDER_2, LETO_LIZARDMAN_FANG);
		
		addStartNpc(ASCALON);
		addTalkId(ASCALON, GROOT, MOUEN, MASON);
		
		addAttackId(HARPY, ROAD_SCAVENGER);
		addKillId(HARPY, MEDUSA, HARPY_MATRIARCH, ROAD_COLLECTOR, ROAD_SCAVENGER, WINDSUS, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, BLOODY_AXE_ELITE);
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
		
		if (event.equals("7624-06.htm"))
		{
			st.startQuest();
			st.giveItems(ASCALON_LETTER_1, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 64);
		}
		else if (event.equals("7624-10.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(MASON_LETTER, 1);
			st.giveItems(ASCALON_LETTER_2, 1);
		}
		else if (event.equals("7624-14.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(GROOT_LETTER, 1);
			st.giveItems(ASCALON_LETTER_3, 1);
		}
		else if (event.equals("7625-03.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ASCALON_LETTER_1, 1);
			st.giveItems(IRON_ROSE_RING, 1);
		}
		else if (event.equals("7093-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ASCALON_LETTER_2, 1);
			st.giveItems(WHITE_ROSE_INSIGNIA, 1);
		}
		else if (event.equals("7196-03.htm"))
		{
			st.set("cond", "10");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ASCALON_LETTER_3, 1);
			st.giveItems(MOUEN_ORDER_1, 1);
		}
		else if (event.equals("7196-06.htm"))
		{
			st.set("cond", "12");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(MOUEN_ORDER_1, 1);
			st.takeItems(ROAD_RATMAN_HEAD, 1);
			st.giveItems(MOUEN_ORDER_2, 1);
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
				final ClassId classId = player.getClassId();
				if ((classId != ClassId.WARRIOR) && (classId != ClassId.RAIDER))
				{
					htmltext = "7624-01.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7624-02.htm";
				}
				else
				{
					htmltext = (classId == ClassId.WARRIOR) ? "7624-03.htm" : "7624-04.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case ASCALON:
						if (cond == 1)
						{
							htmltext = "7624-07.htm";
						}
						else if (cond < 4)
						{
							htmltext = "7624-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7624-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7624-11.htm";
						}
						else if ((cond > 5) && (cond < 8))
						{
							htmltext = "7624-12.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7624-13.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7624-15.htm";
						}
						else if ((cond > 9) && (cond < 14))
						{
							htmltext = "7624-16.htm";
						}
						else if (cond == 14)
						{
							htmltext = "7624-17.htm";
							st.takeItems(MOUEN_LETTER, 1);
							st.giveItems(MARK_OF_CHAMPION, 1);
							st.rewardExpAndSp(117454, 25000);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case MASON:
						if (cond == 1)
						{
							htmltext = "7625-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7625-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7625-05.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(BLOODY_AXE_HEAD, -1);
							st.takeItems(IRON_ROSE_RING, 1);
							st.giveItems(MASON_LETTER, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7625-06.htm";
						}
						else if (cond > 4)
						{
							htmltext = "7625-07.htm";
						}
						break;
					
					case GROOT:
						if (cond == 5)
						{
							htmltext = "7093-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7093-03.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7093-04.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(WHITE_ROSE_INSIGNIA, 1);
							st.takeItems(HARPY_EGG, -1);
							st.takeItems(MEDUSA_VENOM, -1);
							st.takeItems(WINDSUS_BILE, -1);
							st.giveItems(GROOT_LETTER, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7093-05.htm";
						}
						else if (cond > 8)
						{
							htmltext = "7093-06.htm";
						}
						break;
					
					case MOUEN:
						if (cond == 9)
						{
							htmltext = "7196-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7196-04.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7196-05.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7196-07.htm";
						}
						else if (cond == 13)
						{
							htmltext = "7196-08.htm";
							st.set("cond", "14");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(LETO_LIZARDMAN_FANG, -1);
							st.takeItems(MOUEN_ORDER_2, 1);
							st.giveItems(MOUEN_LETTER, 1);
						}
						else if (cond > 13)
						{
							htmltext = "7196-09.htm";
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
		
		switch (npc.getId())
		{
			case HARPY: // Possibility to spawn an HARPY_MATRIARCH.
				if ((st.getCond() == 6) && Rnd.nextBoolean() && !(npc.getScriptValue() == 1))
				{
					final L2Character originalKiller = isPet ? attacker.getPet() : attacker;
					
					// Spawn one or two matriarchs.
					for (int i = 1; i < ((Rnd.get(10) < 7) ? 2 : 3); i++)
					{
						final L2Attackable collector = (L2Attackable) addSpawn(HARPY_MATRIARCH, npc, true, 0);
						
						collector.setRunning();
						collector.addDamageHate(originalKiller, 0, 999);
						collector.getAI().setIntention(CtrlIntentionType.ATTACK, originalKiller);
					}
					npc.setScriptValue(1);
				}
				break;
			
			case ROAD_SCAVENGER: // Possibility to spawn a Road Collector.
				if ((st.getCond() == 10) && Rnd.nextBoolean() && !(npc.getScriptValue() == 1))
				{
					final L2Character originalKiller = isPet ? attacker.getPet() : attacker;
					
					// Spawn one or two collectors.
					for (int i = 1; i < ((Rnd.get(10) < 7) ? 2 : 3); i++)
					{
						final L2Attackable collector = (L2Attackable) addSpawn(ROAD_COLLECTOR, npc, true, 0);
						
						collector.setRunning();
						collector.addDamageHate(originalKiller, 0, 999);
						collector.getAI().setIntention(CtrlIntentionType.ATTACK, originalKiller);
					}
					npc.setScriptValue(1);
				}
				break;
		}
		
		return null;
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
			case BLOODY_AXE_ELITE:
				if ((st.getCond() == 2) && st.dropItemsAlways(BLOODY_AXE_HEAD, 1, 100))
				{
					st.set("cond", "3");
				}
				break;
			
			case HARPY:
			case HARPY_MATRIARCH:
				if ((st.getCond() == 6) && st.dropItems(HARPY_EGG, 1, 30, 500000))
				{
					if ((st.getItemsCount(MEDUSA_VENOM) == 30) && (st.getItemsCount(WINDSUS_BILE) == 30))
					{
						st.set("cond", "7");
					}
				}
				break;
			
			case MEDUSA:
				if ((st.getCond() == 6) && st.dropItems(MEDUSA_VENOM, 1, 30, 500000))
				{
					if ((st.getItemsCount(HARPY_EGG) == 30) && (st.getItemsCount(WINDSUS_BILE) == 30))
					{
						st.set("cond", "7");
					}
				}
				break;
			
			case WINDSUS:
				if ((st.getCond() == 6) && st.dropItems(WINDSUS_BILE, 1, 30, 500000))
				{
					if ((st.getItemsCount(HARPY_EGG) == 30) && (st.getItemsCount(MEDUSA_VENOM) == 30))
					{
						st.set("cond", "7");
					}
				}
				break;
			
			case ROAD_COLLECTOR:
			case ROAD_SCAVENGER:
				if ((st.getCond() == 10) && st.dropItemsAlways(ROAD_RATMAN_HEAD, 1, 100))
				{
					st.set("cond", "11");
				}
				break;
			
			case LETO_LIZARDMAN:
			case LETO_LIZARDMAN_ARCHER:
			case LETO_LIZARDMAN_SOLDIER:
			case LETO_LIZARDMAN_WARRIOR:
			case LETO_LIZARDMAN_SHAMAN:
			case LETO_LIZARDMAN_OVERLORD:
				if ((st.getCond() == 12) && st.dropItems(LETO_LIZARDMAN_FANG, 1, 100, 500000 + (100000 * (npcId - LETO_LIZARDMAN))))
				{
					st.set("cond", "13");
				}
				break;
		}
		
		return null;
	}
}
