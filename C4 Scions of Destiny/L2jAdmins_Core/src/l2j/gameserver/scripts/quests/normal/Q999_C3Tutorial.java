package l2j.gameserver.scripts.quests.normal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Q999_C3Tutorial extends Script
{
	// Mobs
	private static final int GREMLIN1 = 1;
	private static final int GREMLIN2 = 5198;
	// Npc
	
	// Items
	private static final int RECOMMENDATION_01 = 1067;
	private static final int RECOMMENDATION_02 = 1068;
	private static final int LEAF_OF_MOTHERTREE = 1069;
	private static final int BLOOD_OF_JUNDIN = 1070;
	private static final int LICENSE_OF_MINER = 1498;
	private static final int VOUCHER_OF_FLAME = 1496;
	private static final int SOULSHOT_NOVICE = 5789;
	private static final int SPIRITSHOT_NOVICE = 5790;
	private static final int BLUE_GEM = 6353;
	
	public class EventsAux
	{
		public final String htmlfile;
		public final LocationHolder radar;
		public final int item;
		
		public final ClassId classId1;
		public final int gift1;
		public final int count1;
		
		public final ClassId classId2;
		public final int gift2;
		public final int count2;
		
		public EventsAux(String htmlfile, LocationHolder radar, int item, ClassId classId1, int gift1, int count1, ClassId classId2, int gift2, int count2)
		{
			this.htmlfile = htmlfile;
			this.radar = radar;
			this.item = item;
			
			this.classId1 = classId1;
			this.gift1 = gift1;
			this.count1 = count1;
			
			this.classId2 = classId2;
			this.gift2 = gift2;
			this.count2 = count2;
		}
	}
	
	private static final Map<String, EventsAux> EVENTS = new HashMap<>();
	{
		EVENTS.put("7008_02", new EventsAux("7008-03.htm", new LocationHolder(-84058, 243239, -3730), RECOMMENDATION_01, null, SOULSHOT_NOVICE, 200, null, 0, 0));
		EVENTS.put("7017_02", new EventsAux("7017-03.htm", new LocationHolder(-84058, 243239, -3730), RECOMMENDATION_02, ClassId.HUMAN_MAGE, SPIRITSHOT_NOVICE, 100, null, 0, 0));
		EVENTS.put("7370_02", new EventsAux("7370-03.htm", new LocationHolder(45491, 48359, -3086), LEAF_OF_MOTHERTREE, ClassId.ELF_MAGE, SPIRITSHOT_NOVICE, 100, ClassId.ELF_FIGHTER, SOULSHOT_NOVICE, 200));
		EVENTS.put("7129_02", new EventsAux("7129-03.htm", new LocationHolder(12116, 16666, -4610), BLOOD_OF_JUNDIN, ClassId.DARK_ELF_MAGE, SPIRITSHOT_NOVICE, 100, ClassId.DARK_ELF_FIGHTER, SOULSHOT_NOVICE, 200));
		EVENTS.put("7528_02", new EventsAux("7528-03.htm", new LocationHolder(115642, -178046, -941), LICENSE_OF_MINER, ClassId.DWARF_FIGHTER, SOULSHOT_NOVICE, 200, null, 0, 0));
		EVENTS.put("7573_02", new EventsAux("7573-03.htm", new LocationHolder(-45067, -113549, -235), VOUCHER_OF_FLAME, ClassId.ORC_MAGE, SPIRITSHOT_NOVICE, 100, ClassId.ORC_FIGHTER, SOULSHOT_NOVICE, 200));
	}
	
	private class TalksAux
	{
		public final int raceId;
		public final List<String> htmlfiles = new ArrayList<>();
		public final int npcTyp;
		public final int item;
		
		public TalksAux(int raceId, List<String> htmlfiles, int npcTyp, int item)
		{
			this.raceId = raceId;
			this.htmlfiles.addAll(htmlfiles);
			this.npcTyp = npcTyp;
			this.item = item;
		}
	}
	
	private static final Map<Integer, TalksAux> TALKS = new HashMap<>();
	{
		TALKS.put(7017, new TalksAux(0, Arrays.asList("7017-01.htm", "7017-02.htm", "7017-04.htm"), 0, 0));
		TALKS.put(7008, new TalksAux(0, Arrays.asList("7008-01.htm", "7008-02.htm", "7008-04.htm"), 0, 0));
		TALKS.put(7370, new TalksAux(1, Arrays.asList("7370-01.htm", "7370-02.htm", "7370-04.htm"), 0, 0));
		TALKS.put(7129, new TalksAux(2, Arrays.asList("7129-01.htm", "7129-02.htm", "7129-04.htm"), 0, 0));
		TALKS.put(7573, new TalksAux(3, Arrays.asList("7573-01.htm", "7573-02.htm", "7573-04.htm"), 0, 0));
		TALKS.put(7528, new TalksAux(4, Arrays.asList("7528-01.htm", "7528-02.htm", "7528-04.htm"), 0, 0));
		TALKS.put(7018, new TalksAux(0, Arrays.asList("7131-01.htm", "0", "7019-03a.htm", "7019-04.htm"), 1, RECOMMENDATION_02));
		TALKS.put(7019, new TalksAux(0, Arrays.asList("7131-01.htm", "0", "7019-03a.htm", "7019-04.htm"), 1, RECOMMENDATION_02));
		TALKS.put(7020, new TalksAux(0, Arrays.asList("7131-01.htm", "0", "7019-03a.htm", "7019-04.htm"), 1, RECOMMENDATION_02));
		TALKS.put(7021, new TalksAux(0, Arrays.asList("7131-01.htm", "0", "7019-03a.htm", "7019-04.htm"), 1, RECOMMENDATION_02));
		TALKS.put(7009, new TalksAux(0, Arrays.asList("7530-01.htm", "7009-03.htm", "0", "7009-04.htm"), 1, RECOMMENDATION_01));
		TALKS.put(7011, new TalksAux(0, Arrays.asList("7530-01.htm", "7009-03.htm", "0", "7009-04.htm"), 1, RECOMMENDATION_01));
		TALKS.put(7012, new TalksAux(0, Arrays.asList("7530-01.htm", "7009-03.htm", "0", "7009-04.htm"), 1, RECOMMENDATION_01));
		TALKS.put(7056, new TalksAux(0, Arrays.asList("7530-01.htm", "7009-03.htm", "0", "7009-04.htm"), 1, RECOMMENDATION_01));
		TALKS.put(7400, new TalksAux(1, Arrays.asList("7131-01.htm", "7400-03.htm", "7400-03a.htm", "7400-04.htm"), 1, LEAF_OF_MOTHERTREE));
		TALKS.put(7401, new TalksAux(1, Arrays.asList("7131-01.htm", "7400-03.htm", "7400-03a.htm", "7400-04.htm"), 1, LEAF_OF_MOTHERTREE));
		TALKS.put(7402, new TalksAux(1, Arrays.asList("7131-01.htm", "7400-03.htm", "7400-03a.htm", "7400-04.htm"), 1, LEAF_OF_MOTHERTREE));
		TALKS.put(7403, new TalksAux(1, Arrays.asList("7131-01.htm", "7400-03.htm", "7400-03a.htm", "7400-04.htm"), 1, LEAF_OF_MOTHERTREE));
		TALKS.put(7131, new TalksAux(2, Arrays.asList("7131-01.htm", "7131-03.htm", "7131-03a.htm", "7131-04.htm"), 1, BLOOD_OF_JUNDIN));
		TALKS.put(7404, new TalksAux(2, Arrays.asList("7131-01.htm", "7131-03.htm", "7131-03a.htm", "7131-04.htm"), 1, BLOOD_OF_JUNDIN));
		TALKS.put(7574, new TalksAux(3, Arrays.asList("7575-01.htm", "7575-03.htm", "7575-03a.htm", "7575-04.htm"), 1, VOUCHER_OF_FLAME));
		TALKS.put(7575, new TalksAux(3, Arrays.asList("7575-01.htm", "7575-03.htm", "7575-03a.htm", "7575-04.htm"), 1, VOUCHER_OF_FLAME));
		TALKS.put(7530, new TalksAux(4, Arrays.asList("7530-01.htm", "7530-03.htm", "0", "7530-04.htm"), 1, LICENSE_OF_MINER));
	}
	
	public Q999_C3Tutorial()
	{
		super(999, "C3Tutorial");
		
		addKillId(GREMLIN1, GREMLIN2);
		for (int startNpc : TALKS.keySet())
		{
			addStartNpc(startNpc);
			addFirstTalkId(startNpc);
			addTalkId(startNpc);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState qs = player.getScriptState("Q999_C3Tutorial");
		
		if (qs == null)
		{
			return null;
		}
		
		ScriptState qsTutorial = player.getScriptState("Q255_Tutorial");
		
		if (qsTutorial == null)
		{
			return null;
		}
		
		String htmltext = event;
		int ex = qsTutorial.getInt("Ex");
		
		if (event == "TimerEx_NewbieHelper")
		{
			if (ex == 0)
			{
				if (player.getClassId().isMage())
				{
					qs.playSound("tutorial_voice_009b");
				}
				else
				{
					qs.playSound("tutorial_voice_009a");
				}
				qsTutorial.set("Ex", "1");
			}
			else if (ex == 3)
			{
				qs.playSound("tutorial_voice_010a");
				qsTutorial.set("Ex", "4");
			}
			return null;
		}
		
		if (event == "TimerEx_GrandMaster")
		{
			if (ex >= 4)
			{
				qs.showQuestionMark(7);
				qs.playSound("ItemSound.quest_tutorial");
				qs.playSound("tutorial_voice_025");
			}
			return null;
		}
		
		EventsAux eventAux = EVENTS.get(event);
		qs.addRadar(eventAux.radar.getX(), eventAux.radar.getY(), eventAux.radar.getZ());
		
		htmltext = eventAux.htmlfile;
		
		if (qs.hasItems(eventAux.item) && (qs.getInt("onlyone") == 0))
		{
			qs.rewardExpAndSp(0, 50);
			qs.takeItems(eventAux.item, 1);
			startTimer("TimerEx_GrandMaster", 60000, null, player);
			
			if (ex <= 3)
			{
				qsTutorial.set("Ex", "4");
			}
			
			if ((eventAux.classId1 != null) && (qs.getPlayer().getClassId() == eventAux.classId1))
			{
				qs.giveItems(eventAux.gift1, eventAux.count1);
				
				if (eventAux.gift1 == SPIRITSHOT_NOVICE)
				{
					qs.playSound("tutorial_voice_027");
				}
				else
				{
					qs.playSound("tutorial_voice_026");
				}
			}
			else if ((eventAux.classId2 != null) && (qsTutorial.getPlayer().getClassId() == eventAux.classId2))
			{
				if (eventAux.gift2 != 0)
				{
					qs.giveItems(eventAux.gift2, eventAux.count2);
					qs.playSound("tutorial_voice_026");
					qs.unset("step");
				}
			}
			qs.set("onlyone", "1");
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState qsC3Tutorial = player.getScriptState("Q999_C3Tutorial");
		ScriptState qsTutorial = player.getScriptState("Q255_Tutorial");
		
		if (qsTutorial == null)
		{
			npc.showChatWindow(player);
			return null;
		}
		if (qsC3Tutorial == null)
		{
			qsC3Tutorial = newState(player);
		}
		
		String htmltext = "";
		
		if (TALKS.containsKey(npc.getId()))
		{
			int ex = qsTutorial.getInt("Ex");
			int step = qsC3Tutorial.getInt("step");
			int onlyOne = qsC3Tutorial.getInt("onlyone");
			
			TalksAux talk = TALKS.get(npc.getId());
			
			if (qsC3Tutorial.getState() == ScriptStateType.CREATED)
			{
				qsC3Tutorial.setState(ScriptStateType.STARTED);
				qsC3Tutorial.set("onlyone", "0");
				
				if (((player.getLevel() >= 10) || (onlyOne == 0)) && (talk.npcTyp == 1))
				{
					htmltext = "7575-05.htm";
				}
			}
			//@formatter:off
			else if (Util.contains(new int[]{7600,7601,7602,7598,7599}, npc.getId()))
			//@formatter:on
			{
				int reward = qsTutorial.getInt("reward");
				if (reward == 0)
				{
					if (player.getClassId().isMage())
					{
						qsC3Tutorial.playSound("tutorial_voice_027");
						qsC3Tutorial.giveItems(SPIRITSHOT_NOVICE, 100);
					}
					else
					{
						qsC3Tutorial.playSound("tutorial_voice_026");
						qsC3Tutorial.giveItems(SOULSHOT_NOVICE, 200);
					}
					qsTutorial.set("reward", "1");
					qsC3Tutorial.setState(ScriptStateType.COMPLETED);
				}
				npc.showChatWindow(player);
				return null;
			}
			else if ((onlyOne == 0) && (player.getLevel() < 10))
			{
				if (player.getRace().ordinal() == talk.raceId)
				{
					htmltext = talk.htmlfiles.get(0);
					if (talk.npcTyp == 1)
					{
						if ((step == 0) && (ex < 0))
						{
							qsTutorial.set("Ex", "0");
							startTimer("TimerEx_NewbieHelper", 10000, null, player);
							if (player.isMageClass())
							{
								qsC3Tutorial.set("step", "1");
								qsC3Tutorial.setState(ScriptStateType.STARTED);
								qsC3Tutorial.playSound("ItemSound.quest_tutorial");
							}
							else
							{
								htmltext = "7530-01.htm";
								qsC3Tutorial.set("step", "1");
								qsC3Tutorial.setState(ScriptStateType.STARTED);
								qsC3Tutorial.playSound("ItemSound.quest_tutorial");
							}
						}
						else if ((step == 1) && (qsC3Tutorial.getItemsCount(talk.item) == 0))
						{
							if (qsC3Tutorial.hasItems(BLUE_GEM))
							{
								qsC3Tutorial.takeItems(BLUE_GEM, -1);
								qsC3Tutorial.giveItems(talk.item, 1);
								qsC3Tutorial.set("step", "2");
								qsTutorial.set("Ex", "3");
								startTimer("TimerEx_NewbieHelper", 10000, null, player);
								qsTutorial.set("ucMemo", "3");
								qsC3Tutorial.playSound("ItemSound.quest_middle");
								if (player.getClassId().isMage())
								{
									qsC3Tutorial.playSound("tutorial_voice_027");
									qsC3Tutorial.giveItems(SPIRITSHOT_NOVICE, 100);
									htmltext = talk.htmlfiles.get(2);
									if (htmltext == "0")
									{
										htmltext = "<html><body>I am sorry. I only help warriors. Please go to another Newbie Helper who may assist you.</body></html>";
									}
								}
								else
								{
									qsC3Tutorial.playSound("tutorial_voice_026");
									qsC3Tutorial.giveItems(SOULSHOT_NOVICE, 200);
									htmltext = talk.htmlfiles.get(1);
									if (htmltext == "0")
									{
										htmltext = "<html><body>I am sorry. I only help mystics. Please go to another Newbie Helper who may assist you.</body></html>";
									}
								}
							}
							else
							{
								if (player.getClassId().isMage())
								{
									htmltext = "7131-02.htm";
									if (player.getRace().ordinal() == 3)
									{
										htmltext = "7575-02.htm";
									}
								}
								else
								{
									htmltext = "7530-02.htm";
								}
							}
						}
						else if (step == 2)
						{
							htmltext = talk.htmlfiles.get(3);
						}
						else if (talk.npcTyp == 0)
						{
							if (step == 1)
							{
								htmltext = talk.htmlfiles.get(0);
							}
							else if (step == 2)
							{
								htmltext = talk.htmlfiles.get(1);
							}
							else if (step == 3)
							{
								htmltext = talk.htmlfiles.get(2);
							}
						}
					}
				}
			}
			else if ((qsC3Tutorial.getState() != ScriptStateType.COMPLETED) && (talk.npcTyp == 0))
			{
				htmltext = npc.getId() + "-04.htm";
			}
		}
		
		if ((htmltext == null) || (htmltext == ""))
		{
			npc.showChatWindow(player);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		ScriptState qsC3Tutorial = player.getScriptState("Q999_C3Tutorial");
		if (qsC3Tutorial != null)
		{
			ScriptState qsTutorial = player.getScriptState("Q255_Tutorial");
			if (qsTutorial == null)
			{
				return null;
			}
			
			int ex = qsTutorial.getInt("Ex");
			
			if ((ex == 0) || (ex == 1))
			{
				qsC3Tutorial.playSound("tutorial_voice_011");
				qsC3Tutorial.showQuestionMark(3);
				qsTutorial.set("Ex", "2");
			}
			if (((ex == 0) || (ex == 1) || (ex == 2)) && (qsC3Tutorial.getItemsCount(BLUE_GEM) == 0))
			{
				if (Rnd.nextBoolean())
				{
					qsC3Tutorial.dropItemsAlways(BLUE_GEM, 1, 0);
					qsC3Tutorial.playSound("ItemSound.quest_tutorial");
					return null;
				}
			}
		}
		return null;
	}
}
