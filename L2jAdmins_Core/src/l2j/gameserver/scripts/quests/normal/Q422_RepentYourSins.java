package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q422_RepentYourSins extends Script
{
	// Items
	private static final int RATMAN_SCAVENGER_SKULL = 4326;
	private static final int TUREK_WAR_HOUND_TAIL = 4327;
	private static final int TYRANT_KINGPIN_HEART = 4328;
	private static final int TRISALIM_TARANTULA_VENOM_SAC = 4329;
	
	private static final int QITEM_PENITENT_MANACLES = 4330;
	private static final int MANUAL_OF_MANACLES = 4331;
	private static final int PENITENT_MANACLES = 4425;
	private static final int LEFT_PENITENT_MANACLES = 4426;
	
	private static final int SILVER_NUGGET = 1873;
	private static final int ADAMANTINE_NUGGET = 1877;
	private static final int BLACKSMITH_FRAME = 1892;
	private static final int COKES = 1879;
	private static final int STEEL = 1880;
	
	// NPCs
	private static final int BLACK_JUDGE = 7981;
	private static final int KATARI = 7668;
	private static final int PIOTUR = 7597;
	private static final int CASIAN = 7612;
	private static final int JOAN = 7718;
	private static final int PUSHKIN = 7300;
	// Mobs
	private static final int SCAVENGER_WEREAT = 39;
	private static final int TUREK_WAR_HOUND = 494;
	private static final int TYRANT_KINGPIN = 193;
	private static final int TRISALIM_TARANTULA = 561;
	// Summon
	private static final int SIN_EATER = 12564;
	
	public Q422_RepentYourSins()
	{
		super(422, "Repent Your Sins");
		
		registerItems(RATMAN_SCAVENGER_SKULL, TUREK_WAR_HOUND_TAIL, TYRANT_KINGPIN_HEART, TRISALIM_TARANTULA_VENOM_SAC, MANUAL_OF_MANACLES, PENITENT_MANACLES, QITEM_PENITENT_MANACLES);
		
		addStartNpc(BLACK_JUDGE);
		addTalkId(BLACK_JUDGE, KATARI, PIOTUR, CASIAN, JOAN, PUSHKIN);
		
		addKillId(SCAVENGER_WEREAT, TUREK_WAR_HOUND, TYRANT_KINGPIN, TRISALIM_TARANTULA);
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
		
		if (event.equalsIgnoreCase("Start"))
		{
			st.set("cond", "1");
			if (player.getLevel() <= 20)
			{
				htmltext = "7981-03.htm";
				st.set("cond", "2");
			}
			else if ((player.getLevel() >= 20) && (player.getLevel() <= 30))
			{
				htmltext = "7981-04.htm";
				st.set("cond", "3");
			}
			else if ((player.getLevel() >= 30) && (player.getLevel() <= 40))
			{
				htmltext = "7981-05.htm";
				st.set("cond", "4");
			}
			else
			{
				htmltext = "7981-06.htm";
				st.set("cond", "5");
			}
			st.setState(ScriptStateType.STARTED);
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7981-11.htm"))
		{
			if (!st.hasItems(PENITENT_MANACLES))
			{
				int cond = st.getInt("cond");
				
				// Case you return back the qitem to Black Judge. She rewards you with the pet item.
				if (cond == 15)
				{
					st.set("cond", "16");
					st.set("level", String.valueOf(player.getLevel()));
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(QITEM_PENITENT_MANACLES, -1);
					st.giveItems(PENITENT_MANACLES, 1);
				}
				// Case you return back to Black Judge with leftover of previous quest.
				else if (cond == 16)
				{
					st.set("level", String.valueOf(player.getLevel()));
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.takeItems(LEFT_PENITENT_MANACLES, -1);
					st.giveItems(PENITENT_MANACLES, 1);
				}
			}
		}
		else if (event.equalsIgnoreCase("7981-19.htm"))
		{
			if (st.hasItems(LEFT_PENITENT_MANACLES))
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "16");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
		}
		else if (event.equalsIgnoreCase("Pk"))
		{
			final L2Summon pet = player.getPet();
			
			// If Sin Eater is currently summoned, show a warning.
			if ((pet != null) && (pet.getId() == SIN_EATER))
			{
				htmltext = "7981-16.htm";
			}
			else if (findSinEaterLvl(player) > st.getInt("level"))
			{
				st.takeItems(PENITENT_MANACLES, 1);
				st.giveItems(LEFT_PENITENT_MANACLES, 1);
				
				int removePkAmount = Rnd.get(10) + 1;
				
				// Player's PKs are lower than random amount ; finish the quest.
				if (player.getPkKills() <= removePkAmount)
				{
					htmltext = "7981-15.htm";
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(true);
					
					player.setPkKills(0);
					player.sendPacket(new UserInfo(player));
				}
				// Player's PK are bigger than random amount ; continue the quest.
				else
				{
					htmltext = "7981-14.htm";
					st.set("level", String.valueOf(player.getLevel()));
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					
					player.setPkKills(player.getPkKills() - removePkAmount);
					player.sendPacket(new UserInfo(player));
				}
			}
		}
		else if (event.equalsIgnoreCase("Quit"))
		{
			htmltext = "7981-20.htm";
			
			st.takeItems(RATMAN_SCAVENGER_SKULL, -1);
			st.takeItems(TUREK_WAR_HOUND_TAIL, -1);
			st.takeItems(TYRANT_KINGPIN_HEART, -1);
			st.takeItems(TRISALIM_TARANTULA_VENOM_SAC, -1);
			
			st.takeItems(MANUAL_OF_MANACLES, -1);
			st.takeItems(PENITENT_MANACLES, -1);
			st.takeItems(QITEM_PENITENT_MANACLES, -1);
			
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getAlreadyCompletedMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getPkKills() >= 1)
				{
					htmltext = (st.hasItems(LEFT_PENITENT_MANACLES)) ? "7981-18.htm" : "7981-02.htm";
				}
				else
				{
					htmltext = "7981-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case BLACK_JUDGE:
						if (cond <= 9)
						{
							htmltext = "7981-07.htm";
						}
						else if ((cond > 9) && (cond < 14))
						{
							htmltext = "7981-08.htm";
							st.set("cond", "14");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(MANUAL_OF_MANACLES, 1);
						}
						else if (cond == 14)
						{
							htmltext = "7981-09.htm";
						}
						else if (cond == 15)
						{
							htmltext = "7981-10.htm";
						}
						else if (cond == 16)
						{
							if (st.hasItems(PENITENT_MANACLES))
							{
								htmltext = (findSinEaterLvl(player) > st.getInt("level")) ? "7981-13.htm" : "7981-12.htm";
							}
							else
							{
								htmltext = "7981-18.htm";
							}
						}
						break;
					
					case KATARI:
						if (cond == 2)
						{
							htmltext = "7668-01.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 6)
						{
							if (st.getItemsCount(RATMAN_SCAVENGER_SKULL) < 10)
							{
								htmltext = "7668-02.htm";
							}
							else
							{
								htmltext = "7668-03.htm";
								st.set("cond", "10");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(RATMAN_SCAVENGER_SKULL, -1);
							}
						}
						else if (cond == 10)
						{
							htmltext = "7668-04.htm";
						}
						break;
					
					case PIOTUR:
						if (cond == 3)
						{
							htmltext = "7597-01.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 7)
						{
							if (st.getItemsCount(TUREK_WAR_HOUND_TAIL) < 10)
							{
								htmltext = "7597-02.htm";
							}
							else
							{
								htmltext = "7597-03.htm";
								st.set("cond", "11");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(TUREK_WAR_HOUND_TAIL, -1);
							}
						}
						else if (cond == 11)
						{
							htmltext = "7597-04.htm";
						}
						break;
					
					case CASIAN:
						if (cond == 4)
						{
							htmltext = "7612-01.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 8)
						{
							if (!st.hasItems(TYRANT_KINGPIN_HEART))
							{
								htmltext = "7612-02.htm";
							}
							else
							{
								htmltext = "7612-03.htm";
								st.set("cond", "12");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(TYRANT_KINGPIN_HEART, -1);
							}
						}
						else if (cond == 12)
						{
							htmltext = "7612-04.htm";
						}
						break;
					
					case JOAN:
						if (cond == 5)
						{
							htmltext = "7718-01.htm";
							st.set("cond", "9");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond == 9)
						{
							if (st.getItemsCount(TRISALIM_TARANTULA_VENOM_SAC) < 3)
							{
								htmltext = "7718-02.htm";
							}
							else
							{
								htmltext = "7718-03.htm";
								st.set("cond", "13");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(TRISALIM_TARANTULA_VENOM_SAC, -1);
							}
						}
						else if (cond == 13)
						{
							htmltext = "7718-04.htm";
						}
						break;
					
					case PUSHKIN:
						if ((cond == 14) && (st.getItemsCount(MANUAL_OF_MANACLES) == 1))
						{
							if ((st.getItemsCount(SILVER_NUGGET) < 10) || (st.getItemsCount(STEEL) < 5) || (st.getItemsCount(ADAMANTINE_NUGGET) < 2) || (st.getItemsCount(COKES) < 10) || (st.getItemsCount(BLACKSMITH_FRAME) < 1))
							{
								htmltext = "7300-02.htm";
							}
							else
							{
								htmltext = "7300-01.htm";
								st.set("cond", "15");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								
								st.takeItems(MANUAL_OF_MANACLES, 1);
								st.takeItems(SILVER_NUGGET, 10);
								st.takeItems(ADAMANTINE_NUGGET, 2);
								st.takeItems(COKES, 10);
								st.takeItems(STEEL, 5);
								st.takeItems(BLACKSMITH_FRAME, 1);
								
								st.giveItems(QITEM_PENITENT_MANACLES, 1);
							}
						}
						else if (st.hasAtLeastOneItem(QITEM_PENITENT_MANACLES, PENITENT_MANACLES, LEFT_PENITENT_MANACLES))
						{
							htmltext = "7300-03.htm";
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
		ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case SCAVENGER_WEREAT:
				if (st.getInt("cond") == 6)
				{
					st.dropItemsAlways(RATMAN_SCAVENGER_SKULL, 1, 10);
				}
				break;
			
			case TUREK_WAR_HOUND:
				if (st.getInt("cond") == 7)
				{
					st.dropItemsAlways(TUREK_WAR_HOUND_TAIL, 1, 10);
				}
				break;
			
			case TYRANT_KINGPIN:
				if (st.getInt("cond") == 8)
				{
					st.dropItemsAlways(TYRANT_KINGPIN_HEART, 1, 1);
				}
				break;
			
			case TRISALIM_TARANTULA:
				if (st.getInt("cond") == 9)
				{
					st.dropItemsAlways(TRISALIM_TARANTULA_VENOM_SAC, 1, 3);
				}
				break;
		}
		
		return null;
	}
	
	private static int findSinEaterLvl(L2PcInstance player)
	{
		return player.getInventory().getItemById(PENITENT_MANACLES).getEnchantLevel();
	}
}
