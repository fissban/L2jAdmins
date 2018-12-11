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

/**
 * Thx L2Acis
 */
public class Q222_TestOfTheDuelist extends Script
{
	// Npc
	private static final int KAIEN = 7623;
	
	// Items
	private static final int ORDER_GLUDIO = 2763;
	private static final int ORDER_DION = 2764;
	private static final int ORDER_GIRAN = 2765;
	private static final int ORDER_OREN = 2766;
	private static final int ORDER_ADEN = 2767;
	private static final int PUNCHER_SHARD = 2768;
	private static final int NOBLE_ANT_FEELER = 2769;
	private static final int DRONE_CHITIN = 2770;
	private static final int DEAD_SEEKER_FANG = 2771;
	private static final int OVERLORD_NECKLACE = 2772;
	private static final int FETTERED_SOUL_CHAIN = 2773;
	private static final int CHIEF_AMULET = 2774;
	private static final int ENCHANTED_EYE_MEAT = 2775;
	private static final int TAMRIN_ORC_RING = 2776;
	private static final int TAMRIN_ORC_ARROW = 2777;
	private static final int FINAL_ORDER = 2778;
	private static final int EXCURO_SKIN = 2779;
	private static final int KRATOR_SHARD = 2780;
	private static final int GRANDIS_SKIN = 2781;
	private static final int TIMAK_ORC_BELT = 2782;
	private static final int LAKIN_MACE = 2783;
	
	// Rewards
	private static final int MARK_OF_DUELIST = 2762;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	
	// Monsters
	private static final int PUNCHER = 85;
	private static final int NOBLE_ANT_LEADER = 90;
	private static final int MARSH_STAKATO_DRONE = 234;
	private static final int DEAD_SEEKER = 202;
	private static final int BREKA_ORC_OVERLORD = 270;
	private static final int FETTERED_SOUL = 552;
	private static final int LETO_LIZARDMAN_OVERLORD = 582;
	private static final int ENCHANTED_MONSTEREYE = 564;
	private static final int TAMLIN_ORC = 601;
	private static final int TAMLIN_ORC_ARCHER = 602;
	private static final int EXCURO = 214;
	private static final int KRATOR = 217;
	private static final int GRANDIS = 554;
	private static final int TIMAK_ORC_OVERLORD = 588;
	private static final int LAKIN = 604;
	
	public Q222_TestOfTheDuelist()
	{
		super(222, "Test of the Duelist");
		
		registerItems(ORDER_GLUDIO, ORDER_DION, ORDER_GIRAN, ORDER_OREN, ORDER_ADEN, FINAL_ORDER, PUNCHER_SHARD, NOBLE_ANT_FEELER, DRONE_CHITIN, DEAD_SEEKER_FANG, OVERLORD_NECKLACE, FETTERED_SOUL_CHAIN, CHIEF_AMULET, ENCHANTED_EYE_MEAT, TAMRIN_ORC_RING, TAMRIN_ORC_ARROW, EXCURO_SKIN, KRATOR_SHARD, GRANDIS_SKIN, TIMAK_ORC_BELT, LAKIN_MACE);
		
		addStartNpc(KAIEN);
		addTalkId(KAIEN);
		
		addKillId(PUNCHER, NOBLE_ANT_LEADER, MARSH_STAKATO_DRONE, DEAD_SEEKER, BREKA_ORC_OVERLORD, FETTERED_SOUL, LETO_LIZARDMAN_OVERLORD, ENCHANTED_MONSTEREYE, TAMLIN_ORC, TAMLIN_ORC_ARCHER, EXCURO, KRATOR, GRANDIS, TIMAK_ORC_OVERLORD, LAKIN);
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
		
		if (event.equalsIgnoreCase("7623-04.htm"))
		{
			if (player.getRace() == Race.ORC)
			{
				htmltext = "7623-05.htm";
			}
		}
		else if (event.equalsIgnoreCase("7623-07.htm"))
		{
			st.startQuest();
			st.set("cond", "2");
			st.giveItems(ORDER_GLUDIO, 1);
			st.giveItems(ORDER_DION, 1);
			st.giveItems(ORDER_GIRAN, 1);
			st.giveItems(ORDER_OREN, 1);
			st.giveItems(ORDER_ADEN, 1);
			st.giveItems(DIMENSIONAL_DIAMOND, 72);
		}
		else if (event.equalsIgnoreCase("7623-16.htm"))
		{
			if (st.getCond() == 3)
			{
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				
				st.takeItems(ORDER_GLUDIO, 1);
				st.takeItems(ORDER_DION, 1);
				st.takeItems(ORDER_GIRAN, 1);
				st.takeItems(ORDER_OREN, 1);
				st.takeItems(ORDER_ADEN, 1);
				
				st.takeItems(PUNCHER_SHARD, -1);
				st.takeItems(NOBLE_ANT_FEELER, -1);
				st.takeItems(DRONE_CHITIN, -1);
				st.takeItems(DEAD_SEEKER_FANG, -1);
				st.takeItems(OVERLORD_NECKLACE, -1);
				st.takeItems(FETTERED_SOUL_CHAIN, -1);
				st.takeItems(CHIEF_AMULET, -1);
				st.takeItems(ENCHANTED_EYE_MEAT, -1);
				st.takeItems(TAMRIN_ORC_RING, -1);
				st.takeItems(TAMRIN_ORC_ARROW, -1);
				
				st.giveItems(FINAL_ORDER, 1);
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
				final int classId = player.getClassId().getId();
				if ((classId != 0x01) && (classId != 0x2f) && (classId != 0x13) && (classId != 0x20))
				{
					htmltext = "7623-02.htm";
				}
				else if (player.getLevel() < 39)
				{
					htmltext = "7623-01.htm";
				}
				else
				{
					htmltext = "7623-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				if (cond == 2)
				{
					htmltext = "7623-07a.htm";
				}
				else if (cond == 3)
				{
					htmltext = "7623-13.htm";
				}
				else if (cond == 4)
				{
					htmltext = "7623-17.htm";
				}
				else if (cond == 5)
				{
					htmltext = "7623-18.htm";
					st.takeItems(FINAL_ORDER, 1);
					st.takeItems(EXCURO_SKIN, -1);
					st.takeItems(KRATOR_SHARD, -1);
					st.takeItems(GRANDIS_SKIN, -1);
					st.takeItems(TIMAK_ORC_BELT, -1);
					st.takeItems(LAKIN_MACE, -1);
					st.giveItems(MARK_OF_DUELIST, 1);
					st.rewardExpAndSp(47015, 20000);
					player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
					st.exitQuest(false, true);
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
		
		if (st.getCond() == 2)
		{
			switch (npc.getId())
			{
				case PUNCHER:
					if (st.dropItemsAlways(PUNCHER_SHARD, 1, 10))
					{
						if ((st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10) && (st.getItemsCount(CHIEF_AMULET) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case NOBLE_ANT_LEADER:
					if (st.dropItemsAlways(NOBLE_ANT_FEELER, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10) && (st.getItemsCount(CHIEF_AMULET) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case MARSH_STAKATO_DRONE:
					if (st.dropItemsAlways(DRONE_CHITIN, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10) && (st.getItemsCount(CHIEF_AMULET) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case DEAD_SEEKER:
					if (st.dropItemsAlways(DEAD_SEEKER_FANG, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10) && (st.getItemsCount(CHIEF_AMULET) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case BREKA_ORC_OVERLORD:
					if (st.dropItemsAlways(OVERLORD_NECKLACE, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10) && (st.getItemsCount(CHIEF_AMULET) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case FETTERED_SOUL:
					if (st.dropItemsAlways(FETTERED_SOUL_CHAIN, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(CHIEF_AMULET) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case LETO_LIZARDMAN_OVERLORD:
					if (st.dropItemsAlways(CHIEF_AMULET, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10)
							&& (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case ENCHANTED_MONSTEREYE:
					if (st.dropItemsAlways(ENCHANTED_EYE_MEAT, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10)
							&& (st.getItemsCount(CHIEF_AMULET) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case TAMLIN_ORC:
					if (st.dropItemsAlways(TAMRIN_ORC_RING, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10)
							&& (st.getItemsCount(CHIEF_AMULET) >= 10) && (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_ARROW) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
				
				case TAMLIN_ORC_ARCHER:
					if (st.dropItemsAlways(TAMRIN_ORC_ARROW, 1, 10))
					{
						if ((st.getItemsCount(PUNCHER_SHARD) >= 10) && (st.getItemsCount(NOBLE_ANT_FEELER) >= 10) && (st.getItemsCount(DRONE_CHITIN) >= 10) && (st.getItemsCount(DEAD_SEEKER_FANG) >= 10) && (st.getItemsCount(OVERLORD_NECKLACE) >= 10) && (st.getItemsCount(FETTERED_SOUL_CHAIN) >= 10)
							&& (st.getItemsCount(CHIEF_AMULET) >= 10) && (st.getItemsCount(ENCHANTED_EYE_MEAT) >= 10) && (st.getItemsCount(TAMRIN_ORC_RING) >= 10))
						{
							st.set("cond", "3");
						}
					}
					break;
			}
		}
		else if (st.getCond() == 4)
		{
			switch (npc.getId())
			{
				case EXCURO:
					if (st.dropItemsAlways(EXCURO_SKIN, 1, 3))
					{
						if ((st.getItemsCount(KRATOR_SHARD) >= 3) && (st.getItemsCount(LAKIN_MACE) >= 3) && (st.getItemsCount(GRANDIS_SKIN) >= 3) && (st.getItemsCount(TIMAK_ORC_BELT) >= 3))
						{
							st.set("cond", "5");
						}
					}
					break;
				
				case KRATOR:
					if (st.dropItemsAlways(KRATOR_SHARD, 1, 3))
					{
						if ((st.getItemsCount(EXCURO_SKIN) >= 3) && (st.getItemsCount(LAKIN_MACE) >= 3) && (st.getItemsCount(GRANDIS_SKIN) >= 3) && (st.getItemsCount(TIMAK_ORC_BELT) >= 3))
						{
							st.set("cond", "5");
						}
					}
					break;
				
				case LAKIN:
					if (st.dropItemsAlways(LAKIN_MACE, 1, 3))
					{
						if ((st.getItemsCount(EXCURO_SKIN) >= 3) && (st.getItemsCount(KRATOR_SHARD) >= 3) && (st.getItemsCount(GRANDIS_SKIN) >= 3) && (st.getItemsCount(TIMAK_ORC_BELT) >= 3))
						{
							st.set("cond", "5");
						}
					}
					break;
				
				case GRANDIS:
					if (st.dropItemsAlways(GRANDIS_SKIN, 1, 3))
					{
						if ((st.getItemsCount(EXCURO_SKIN) >= 3) && (st.getItemsCount(KRATOR_SHARD) >= 3) && (st.getItemsCount(LAKIN_MACE) >= 3) && (st.getItemsCount(TIMAK_ORC_BELT) >= 3))
						{
							st.set("cond", "5");
						}
					}
					break;
				
				case TIMAK_ORC_OVERLORD:
					if (st.dropItemsAlways(TIMAK_ORC_BELT, 1, 3))
					{
						if ((st.getItemsCount(EXCURO_SKIN) >= 3) && (st.getItemsCount(KRATOR_SHARD) >= 3) && (st.getItemsCount(LAKIN_MACE) >= 3) && (st.getItemsCount(GRANDIS_SKIN) >= 3))
						{
							st.set("cond", "5");
						}
					}
					break;
			}
		}
		
		return null;
	}
}
