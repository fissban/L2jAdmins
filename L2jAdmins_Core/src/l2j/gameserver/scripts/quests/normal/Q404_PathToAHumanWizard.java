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
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q404_PathToAHumanWizard extends Script
{
	// Items
	private static final int MAP_OF_LUSTER = 1280;
	private static final int KEY_OF_FLAME = 1281;
	private static final int FLAME_EARING = 1282;
	private static final int BROKEN_BRONZE_MIRROR = 1283;
	private static final int WIND_FEATHER = 1284;
	private static final int WIND_BANGEL = 1285;
	private static final int RAMA_DIARY = 1286;
	private static final int SPARKLE_PEBBLE = 1287;
	private static final int WATER_NECKLACE = 1288;
	private static final int RUST_GOLD_COIN = 1289;
	private static final int RED_SOIL = 1290;
	private static final int EARTH_RING = 1291;
	private static final int BEAD_OF_SEASON = 1292;
	
	// NPCs
	private static final int PARINA = 7391;
	private static final int EARTH_SNAKE = 7409;
	private static final int WASTELAND_LIZARDMAN = 7410;
	private static final int FLAME_SALAMANDER = 7411;
	private static final int WIND_SYLPH = 7412;
	private static final int WATER_UNDINE = 7413;
	
	public Q404_PathToAHumanWizard()
	{
		super(404, "Path to a Human Wizard");
		
		registerItems(MAP_OF_LUSTER, KEY_OF_FLAME, FLAME_EARING, BROKEN_BRONZE_MIRROR, WIND_FEATHER, WIND_BANGEL, RAMA_DIARY, SPARKLE_PEBBLE, WATER_NECKLACE, RUST_GOLD_COIN, RED_SOIL, EARTH_RING);
		
		addStartNpc(PARINA);
		addTalkId(PARINA, EARTH_SNAKE, WASTELAND_LIZARDMAN, FLAME_SALAMANDER, WIND_SYLPH, WATER_UNDINE);
		
		addKillId(21, 359, 5030);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7391-08.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7410-03.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BROKEN_BRONZE_MIRROR, 1);
			st.giveItems(WIND_FEATHER, 1);
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
		
		final int cond = st.getInt("cond");
		switch (st.getState())
		{
			case CREATED:
				if (player.getClassId() != ClassId.HUMAN_MAGE)
				{
					htmltext = player.getClassId() == ClassId.WIZARD ? "7391-02a.htm" : "7391-01.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "7391-02.htm";
				}
				else if (st.hasItems(BEAD_OF_SEASON))
				{
					htmltext = "7391-03.htm";
				}
				else
				{
					htmltext = "7391-04.htm";
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case PARINA:
						if (cond < 13)
						{
							htmltext = "7391-05.htm";
						}
						else if (cond == 13)
						{
							htmltext = "7391-06.htm";
							st.takeItems(EARTH_RING, 1);
							st.takeItems(FLAME_EARING, 1);
							st.takeItems(WATER_NECKLACE, 1);
							st.takeItems(WIND_BANGEL, 1);
							st.giveItems(BEAD_OF_SEASON, 1);
							st.rewardExpAndSp(3200, 2020);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case FLAME_SALAMANDER:
						if (cond == 1)
						{
							htmltext = "7411-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(MAP_OF_LUSTER, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7411-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7411-03.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(KEY_OF_FLAME, 1);
							st.takeItems(MAP_OF_LUSTER, 1);
							st.giveItems(FLAME_EARING, 1);
						}
						else if (cond > 3)
						{
							htmltext = "7411-04.htm";
						}
						break;
					
					case WIND_SYLPH:
						if (cond == 4)
						{
							htmltext = "7412-01.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(BROKEN_BRONZE_MIRROR, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7412-02.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7412-03.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(WIND_FEATHER, 1);
							st.giveItems(WIND_BANGEL, 1);
						}
						else if (cond > 6)
						{
							htmltext = "7412-04.htm";
						}
						break;
					
					case WASTELAND_LIZARDMAN:
						if (cond == 5)
						{
							htmltext = "7410-01.htm";
						}
						else if (cond > 5)
						{
							htmltext = "7410-04.htm";
						}
						break;
					
					case WATER_UNDINE:
						if (cond == 7)
						{
							htmltext = "7413-01.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(RAMA_DIARY, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7413-02.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7413-03.htm";
							st.set("cond", "10");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(RAMA_DIARY, 1);
							st.takeItems(SPARKLE_PEBBLE, -1);
							st.giveItems(WATER_NECKLACE, 1);
						}
						else if (cond > 9)
						{
							htmltext = "7413-04.htm";
						}
						break;
					
					case EARTH_SNAKE:
						if (cond == 10)
						{
							htmltext = "7409-01.htm";
							st.set("cond", "11");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(RUST_GOLD_COIN, 1);
						}
						else if (cond == 11)
						{
							htmltext = "7409-02.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7409-03.htm";
							st.set("cond", "13");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(RED_SOIL, 1);
							st.takeItems(RUST_GOLD_COIN, 1);
							st.giveItems(EARTH_RING, 1);
						}
						else if (cond > 12)
						{
							htmltext = "7409-04.htm";
						}
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
		
		switch (npc.getId())
		{
			case 359: // Ratman Warrior
				if ((st.getInt("cond") == 2) && st.dropItems(KEY_OF_FLAME, 1, 1, 800000))
				{
					st.set("cond", "3");
				}
				break;
			
			case 5030: // Water Seer
				if ((st.getInt("cond") == 8) && st.dropItems(SPARKLE_PEBBLE, 1, 2, 800000))
				{
					st.set("cond", "9");
				}
				break;
			
			case 21: // Red Bear
				if ((st.getInt("cond") == 11) && st.dropItems(RED_SOIL, 1, 1, 200000))
				{
					st.set("cond", "12");
				}
				break;
		}
		
		return null;
	}
	
}
