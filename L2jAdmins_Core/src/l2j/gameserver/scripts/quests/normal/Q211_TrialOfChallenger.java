package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Q211_TrialOfChallenger extends Script
{
	// Npc's
	private static final int KASH = 7644;
	private static final int FILAUR = 7535;
	private static final int MARTIEN = 7645;
	private static final int RALDO = 7646;
	private static final int CHEST_OF_SHYSLASSYS = 7647;
	// Monster's
	private static final int SHYSLASSYS = 5110;
	// private static final int CAVE_BASILISK = 5111;
	private static final int GORR = 5112;
	private static final int BARAHAM = 5113;
	private static final int SUCCUBUS_QUEEN = 5114;
	// Item's
	private static final int LETTER_OF_KASH = 2628;
	private static final int SCROLL_OF_SHYSLASSY = 2631;
	private static final int WATCHERS_EYE1 = 2629;
	private static final int BROKEN_KEY = 2632;
	private static final int MITHRIL_SCALE_GAITERS_MATERIAL = 2918;
	private static final int BRIGANDINE_GAUNTLET_PATTERN = 2927;
	private static final int MANTICOR_SKIN_GAITERS_PATTERN = 1943;
	private static final int GAUNTLET_OF_REPOSE_OF_THE_SOUL_PATTERN = 1946;
	private static final int IRON_BOOTS_DESIGN = 1940;
	private static final int TOME_OF_BLOOD_PAGE = 2030;
	private static final int ELVEN_NECKLACE_BEADS = 1904;
	private static final int WHITE_TUNIC_PATTERN = 1936;
	private static final int ADENA = 57;
	private static final int MARK_OF_CHALLENGER = 2627;
	private static final int WATCHERS_EYE2 = 2630;
	
	public Q211_TrialOfChallenger()
	{
		super(211, "Trial Of Challenger");
		
		addStartNpc(KASH);
		addTalkId(KASH, FILAUR, MARTIEN, RALDO, CHEST_OF_SHYSLASSYS);
		addKillId(SHYSLASSYS, GORR, BARAHAM, SUCCUBUS_QUEEN);
		registerItems(SCROLL_OF_SHYSLASSY, LETTER_OF_KASH, WATCHERS_EYE1, BROKEN_KEY, WATCHERS_EYE2);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		switch (event)
		{
			case "1":
				st.startQuest();
				return "7644-05.htm";
			
			case "7644_1":
				return "7644-04.htm";
			
			case "7645_1":
				st.takeItems(LETTER_OF_KASH, 1);
				st.setCond(4, true);
				return "7645-02.htm";
			
			case "7647_1":
				if (st.getItemsCount(BROKEN_KEY) == 1)
				{
					st.giveItems(SCROLL_OF_SHYSLASSY, 1);
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					if (Rnd.get(10) < 2)
					{
						st.takeItems(BROKEN_KEY, 1);
						st.playSound(PlaySoundType.QUEST_JACKPOT);
						int n = Rnd.get(100);
						if (n > 90)
						{
							st.giveItems(MITHRIL_SCALE_GAITERS_MATERIAL, 1);
							st.giveItems(BRIGANDINE_GAUNTLET_PATTERN, 1);
							st.giveItems(MANTICOR_SKIN_GAITERS_PATTERN, 1);
							st.giveItems(GAUNTLET_OF_REPOSE_OF_THE_SOUL_PATTERN, 1);
							st.giveItems(IRON_BOOTS_DESIGN, 1);
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (n > 70)
						{
							st.giveItems(TOME_OF_BLOOD_PAGE, 1);
							st.giveItems(ELVEN_NECKLACE_BEADS, 1);
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (n > 40)
						{
							st.giveItems(WHITE_TUNIC_PATTERN, 1);
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.giveItems(IRON_BOOTS_DESIGN, 1);
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						return "7647-03.htm";
					}
					
					st.takeItems(BROKEN_KEY, 1);
					st.giveItems(ADENA, Rnd.get(1000) + 1);
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					return "7647-02.htm";
				}
				
				st.takeItems(BROKEN_KEY, 1);
				return "7647-04.htm";
			
			case "7646_1":
				return "7646-02.htm";
			
			case "7646_2":
				return "7646-03.htm";
			
			case "7646_3":
				st.setCond(8, true);
				st.takeItems(WATCHERS_EYE2, 1);
				return "7646-04.htm";
			
			case "7646_4":
				st.setCond(8, true);
				st.takeItems(WATCHERS_EYE2, 1);
				return "7646-06.htm";
		}
		
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		switch (st.getState())
		{
			case CREATED:
				switch (npc.getId())
				{
					case KASH:
						switch (st.getPlayer().getClassId())
						{
							case WARRIOR:
							case ELF_KNIGHT:
							case PALUS_KNIGHT:
							case RAIDER:
							case MONK:
								
								st.startQuest();
								
								if (st.getPlayer().getLevel() >= 35)
								{
									return "7644-03.htm";
								}
								st.exitQuest(true);
								return "7644-01.htm";
							
							default:
								st.exitQuest(true);
								return "7644-02.htm";
						}
				}
				break;
			case STARTED:
				switch (npc.getId())
				{
					case KASH:
						if (st.getCond() == 1)
						{
							return "7644-06.htm";
						}
						if ((st.getCond() == 2) && st.hasItems(SCROLL_OF_SHYSLASSY))
						{
							st.takeItems(SCROLL_OF_SHYSLASSY, 1);
							st.giveItems(LETTER_OF_KASH, 1);
							st.setCond(3, true);
							return "7644-07.htm";
						}
						if ((st.getCond() == 1) && st.hasItems(LETTER_OF_KASH))
						{
							return "7644-08.htm";
						}
						if (st.getCond() >= 7)
						{
							return "7644-09.htm";
						}
						
						break;
					
					case MARTIEN:
						if ((st.getCond() == 3) && st.hasItems(LETTER_OF_KASH))
						{
							return "7645-01.htm";
						}
						if ((st.getCond() == 4) && !st.hasItems(WATCHERS_EYE1))
						{
							return "7645-03.htm";
						}
						if ((st.getCond() == 5) && st.hasItems(WATCHERS_EYE1))
						{
							st.takeItems(WATCHERS_EYE1, 1);
							st.setCond(6, true);
							return "7645-04.htm";
						}
						if (st.getCond() == 6)
						{
							return "7645-05.htm";
						}
						if (st.getCond() >= 7)
						{
							return "7645-06.htm";
						}
						
						break;
					
					case CHEST_OF_SHYSLASSYS:
						if (st.getCond() == 2)
						{
							return "7647-01.htm";
						}
						break;
					
					case RALDO:
						if ((st.getCond() == 7) && st.hasItems(WATCHERS_EYE2))
						{
							return "7646-01.htm";
						}
						if (st.getCond() == 7)
						{
							return "7646-06a.htm";
						}
						if (st.getCond() == 10)
						{
							st.rewardExpAndSp(72394, 11250);
							st.giveItems(7562, 8);
							st.takeItems(BROKEN_KEY, 1);
							st.giveItems(7562, 8);
							st.giveItems(MARK_OF_CHALLENGER, 1);
							st.exitQuest(false, true); // TODO es repetible? no lo creo
							return "7646-07.htm";
						}
						break;
					
					case FILAUR:
						if (st.getCond() == 7)
						{
							if (st.getPlayer().getLevel() >= 36)
							{
								st.addRadar(176560, -184969, -3729);
								st.setCond(8, true);
								return "7535-01.htm";
							}
							return "7535-03.htm";
						}
						else if (st.getCond() == 8)
						{
							st.addRadar(176560, -184969, -3729);
							st.setCond(9, true);
							return "7535-02.htm";
						}
				}
				break;
			
			case COMPLETED:
				return getAlreadyCompletedMsg();
		}
		
		return getNoQuestMsg();
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
				case SHYSLASSYS:
					if ((st.getCond() == 1) && (st.getItemsCount(BROKEN_KEY) == 0))
					{
						st.giveItems(BROKEN_KEY, 1);
						addSpawn(CHEST_OF_SHYSLASSYS, npc, true, 0);
						st.setCond(2, true);
					}
					break;
				
				case GORR:
					if ((st.getCond() == 4) && (st.getItemsCount(WATCHERS_EYE1) == 0))
					{
						st.giveItems(WATCHERS_EYE1, 1);
						st.setCond(5, true);
					}
					break;
				
				case BARAHAM:
					if ((st.getCond() == 6) && (st.getItemsCount(WATCHERS_EYE2) == 0))
					{
						st.giveItems(WATCHERS_EYE2, 1);
						st.setCond(7, true);
						addSpawn(RALDO, npc, true, 0);
					}
					break;
				
				case SUCCUBUS_QUEEN:
					if (st.getCond() == 9)
					{
						st.setCond(10, true);
						addSpawn(RALDO, npc, true, 0);
					}
					break;
			}
		}
		
		return getNoQuestMsg();
	}
}
