package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q426_QuestForFishingShot extends Script
{
	private static final int SWEET_FLUID = 7586;
	
	private static final Map<Integer, Integer> MOBS1 = new HashMap<>();
	{
		MOBS1.put(5, 45);
		MOBS1.put(13, 100);
		MOBS1.put(16, 100);
		MOBS1.put(17, 115);
		MOBS1.put(30, 105);
		MOBS1.put(132, 70);
		MOBS1.put(38, 135);
		MOBS1.put(44, 125);
		MOBS1.put(46, 100);
		MOBS1.put(47, 100);
		MOBS1.put(50, 140);
		MOBS1.put(58, 140);
		MOBS1.put(63, 160);
		MOBS1.put(66, 170);
		MOBS1.put(70, 180);
		MOBS1.put(74, 195);
		MOBS1.put(77, 205);
		MOBS1.put(78, 205);
		MOBS1.put(79, 205);
		MOBS1.put(80, 220);
		MOBS1.put(81, 370);
		MOBS1.put(83, 245);
		MOBS1.put(84, 255);
		MOBS1.put(85, 265);
		MOBS1.put(87, 565);
		MOBS1.put(88, 605);
		MOBS1.put(89, 250);
		MOBS1.put(100, 85);
		MOBS1.put(103, 110);
		MOBS1.put(105, 110);
		MOBS1.put(115, 190);
		MOBS1.put(120, 20);
		MOBS1.put(131, 45);
		MOBS1.put(135, 360);
		MOBS1.put(157, 235);
		MOBS1.put(162, 195);
		MOBS1.put(176, 280);
		MOBS1.put(211, 170);
		MOBS1.put(225, 160);
		MOBS1.put(227, 180);
		MOBS1.put(230, 260);
		MOBS1.put(232, 245);
		MOBS1.put(234, 290);
		MOBS1.put(241, 700);
		MOBS1.put(267, 215);
		MOBS1.put(268, 295);
		MOBS1.put(269, 255);
		MOBS1.put(270, 365);
		MOBS1.put(271, 295);
		MOBS1.put(286, 700);
		MOBS1.put(308, 110);
		MOBS1.put(312, 45);
		MOBS1.put(317, 20);
		MOBS1.put(324, 85);
		MOBS1.put(333, 100);
		MOBS1.put(341, 100);
		MOBS1.put(346, 85);
		MOBS1.put(349, 850);
		MOBS1.put(356, 165);
		MOBS1.put(357, 140);
		MOBS1.put(363, 70);
		MOBS1.put(368, 85);
		MOBS1.put(371, 100);
		MOBS1.put(386, 85);
		MOBS1.put(389, 90);
		MOBS1.put(403, 110);
		MOBS1.put(404, 95);
		MOBS1.put(433, 100);
		MOBS1.put(436, 140);
		MOBS1.put(448, 45);
		MOBS1.put(456, 20);
		MOBS1.put(463, 85);
		MOBS1.put(470, 45);
		MOBS1.put(471, 85);
		MOBS1.put(475, 20);
		MOBS1.put(478, 110);
		MOBS1.put(487, 90);
		MOBS1.put(511, 100);
		MOBS1.put(525, 20);
		MOBS1.put(528, 100);
		MOBS1.put(536, 15);
		MOBS1.put(537, 15);
		MOBS1.put(538, 15);
		MOBS1.put(539, 15);
		MOBS1.put(544, 15);
		MOBS1.put(550, 300);
		MOBS1.put(551, 300);
		MOBS1.put(552, 650);
		MOBS1.put(553, 335);
		MOBS1.put(554, 390);
		MOBS1.put(555, 350);
		MOBS1.put(557, 390);
		MOBS1.put(559, 420);
		MOBS1.put(560, 440);
		MOBS1.put(562, 485);
		MOBS1.put(573, 545);
		MOBS1.put(575, 645);
		MOBS1.put(630, 350);
		MOBS1.put(632, 475);
		MOBS1.put(634, 960);
		MOBS1.put(636, 495);
		MOBS1.put(638, 540);
		MOBS1.put(641, 680);
		MOBS1.put(643, 660);
		MOBS1.put(644, 645);
		MOBS1.put(659, 440);
		MOBS1.put(661, 575);
		MOBS1.put(663, 525);
		MOBS1.put(665, 680);
		MOBS1.put(667, 730);
		MOBS1.put(766, 210);
		MOBS1.put(781, 270);
		MOBS1.put(783, 140);
		MOBS1.put(784, 155);
		MOBS1.put(786, 170);
		MOBS1.put(788, 325);
		MOBS1.put(790, 390);
		MOBS1.put(792, 620);
		MOBS1.put(794, 635);
		MOBS1.put(796, 640);
		MOBS1.put(798, 850);
		MOBS1.put(800, 740);
		MOBS1.put(802, 900);
		MOBS1.put(804, 775);
		MOBS1.put(806, 805);
		MOBS1.put(833, 455);
		MOBS1.put(834, 680);
		MOBS1.put(836, 785);
		MOBS1.put(837, 835);
		MOBS1.put(839, 430);
		MOBS1.put(841, 460);
		MOBS1.put(845, 605);
		MOBS1.put(847, 570);
		MOBS1.put(849, 585);
		MOBS1.put(936, 290);
		MOBS1.put(937, 315);
		MOBS1.put(939, 385);
		MOBS1.put(940, 500);
		MOBS1.put(941, 460);
		MOBS1.put(943, 345);
		MOBS1.put(944, 335);
		MOBS1.put(1100, 125);
		MOBS1.put(1101, 155);
		MOBS1.put(1103, 215);
		MOBS1.put(1105, 310);
		MOBS1.put(1107, 600);
		MOBS1.put(1117, 120);
		MOBS1.put(1023, 170);
		MOBS1.put(1024, 175);
		MOBS1.put(1025, 185);
		MOBS1.put(1026, 200);
		MOBS1.put(1034, 195);
		MOBS1.put(1125, 12);
		MOBS1.put(1263, 650);
		MOBS1.put(1520, 880);
		MOBS1.put(1526, 970);
		MOBS1.put(1536, 985);
		MOBS1.put(1602, 555);
		MOBS1.put(1603, 750);
		MOBS1.put(1605, 620);
		MOBS1.put(1606, 875);
		MOBS1.put(1611, 590);
		MOBS1.put(1612, 835);
		MOBS1.put(1617, 615);
		MOBS1.put(1618, 875);
		MOBS1.put(1635, 775);
		MOBS1.put(1638, 165);
		MOBS1.put(1639, 185);
		MOBS1.put(1641, 195);
		MOBS1.put(1644, 170);
	}
	
	private static final Map<Integer, Integer> MOBS2 = new HashMap<>();
	{
		MOBS2.put(579, 420);
		MOBS2.put(639, 280);
		MOBS2.put(646, 145);
		MOBS2.put(648, 120);
		MOBS2.put(650, 460);
		MOBS2.put(651, 260);
		MOBS2.put(652, 335);
		MOBS2.put(657, 630);
		MOBS2.put(658, 570);
		MOBS2.put(808, 50);
		MOBS2.put(809, 865);
		MOBS2.put(832, 700);
		MOBS2.put(979, 980);
		MOBS2.put(991, 665);
		MOBS2.put(994, 590);
		MOBS2.put(1261, 170);
		MOBS2.put(1263, 795);
		MOBS2.put(1508, 100);
		MOBS2.put(1510, 280);
		MOBS2.put(1511, 995);
		MOBS2.put(1512, 995);
		MOBS2.put(1514, 185);
		MOBS2.put(1516, 495);
		MOBS2.put(1517, 495);
		MOBS2.put(1518, 255);
		MOBS2.put(1636, 950);
	}
	
	private static final Map<Integer, Integer> MOBS3 = new HashMap<>();
	{
		MOBS3.put(655, 110);
		MOBS3.put(656, 150);
		MOBS3.put(772, 105);
		MOBS3.put(810, 50);
		MOBS3.put(812, 490);
		MOBS3.put(814, 775);
		MOBS3.put(816, 875);
		MOBS3.put(819, 280);
		MOBS3.put(955, 670);
		MOBS3.put(978, 555);
		MOBS3.put(1058, 355);
		MOBS3.put(1060, 45);
		MOBS3.put(1075, 110);
		MOBS3.put(1078, 610);
		MOBS3.put(1081, 955);
		MOBS3.put(1264, 920);
	}
	
	private static final Map<Integer, Integer> MOBS4 = new HashMap<>();
	{
		MOBS4.put(815, 205);
		MOBS4.put(822, 100);
		MOBS4.put(824, 665);
		MOBS4.put(825, 620);
		MOBS4.put(983, 205);
		MOBS4.put(1314, 145);
		MOBS4.put(1316, 235);
		MOBS4.put(1318, 280);
		MOBS4.put(1320, 355);
		MOBS4.put(1322, 430);
		MOBS4.put(1376, 280);
		MOBS4.put(1378, 375);
		MOBS4.put(21380, 375);
		MOBS4.put(1387, 640);
		MOBS4.put(1393, 935);
		MOBS4.put(1395, 855);
		MOBS4.put(1652, 375);
		MOBS4.put(1655, 640);
		MOBS4.put(1657, 935);
	}
	
	private static final Map<Integer, Integer> MOBS5 = new HashMap<>();
	{
		MOBS5.put(828, 935);
		MOBS5.put(1061, 530);
		MOBS5.put(1069, 825);
		MOBS5.put(1382, 125);
		MOBS5.put(1384, 400);
		MOBS5.put(1390, 750);
		MOBS5.put(1654, 400);
		MOBS5.put(1656, 750);
	}
	
	private static final Map<Integer, int[]> MOBSspecial = new HashMap<>();
	{
		MOBSspecial.put(829, new int[]
		{
			115,
			6
		});
		MOBSspecial.put(859, new int[]
		{
			890,
			8
		});
		MOBSspecial.put(1066, new int[]
		{
			5,
			5
		});
		MOBSspecial.put(1068, new int[]
		{
			565,
			11
		});
		MOBSspecial.put(1071, new int[]
		{
			400,
			12
		});
	}
	
	public Q426_QuestForFishingShot()
	{
		super(426, "Quest for Fishing Shot");
		
		registerItems(SWEET_FLUID);
		
		addStartNpc(8562, 8563, 8564, 8565, 8566, 8567, 8568, 8569, 8570, 8571, 8572, 8573, 8574, 8575, 8576, 8577, 8578, 8579, 8696, 8697);
		addTalkId(8562, 8563, 8564, 8565, 8566, 8567, 8568, 8569, 8570, 8571, 8572, 8573, 8574, 8575, 8576, 8577, 8578, 8579, 8696, 8697);
		
		for (int mob : MOBS1.keySet())
		{
			addKillId(mob);
		}
		for (int mob : MOBS2.keySet())
		{
			addKillId(mob);
		}
		for (int mob : MOBS3.keySet())
		{
			addKillId(mob);
		}
		for (int mob : MOBS4.keySet())
		{
			addKillId(mob);
		}
		for (int mob : MOBS5.keySet())
		{
			addKillId(mob);
		}
		for (int mob : MOBSspecial.keySet())
		{
			addKillId(mob);
		}
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
		
		if (event.equalsIgnoreCase("03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("08.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
			st = newState(player);
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = "01.htm";
				break;
			
			case STARTED:
				htmltext = (st.hasItems(SWEET_FLUID)) ? "05.htm" : "04.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		int npcId = npc.getId();
		int drop = 0;
		int chance = 0;
		
		if (MOBS1.containsKey(npcId))
		{
			chance = MOBS1.get(npcId);
		}
		else if (MOBS2.containsKey(npcId))
		{
			chance = MOBS2.get(npcId);
			drop = 1;
		}
		else if (MOBS3.containsKey(npcId))
		{
			chance = MOBS3.get(npcId);
			drop = 2;
		}
		else if (MOBS4.containsKey(npcId))
		{
			chance = MOBS4.get(npcId);
			drop = 3;
		}
		else if (MOBS5.containsKey(npcId))
		{
			chance = MOBS5.get(npcId);
			drop = 4;
		}
		else if (MOBSspecial.containsKey(npcId))
		{
			chance = MOBSspecial.get(npcId)[0];
			drop = MOBSspecial.get(npcId)[1];
		}
		
		if (Rnd.get(1000) <= chance)
		{
			drop++;
		}
		
		if (drop != 0)
		{
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.rewardItems(SWEET_FLUID, drop);
		}
		return null;
	}
	
}
