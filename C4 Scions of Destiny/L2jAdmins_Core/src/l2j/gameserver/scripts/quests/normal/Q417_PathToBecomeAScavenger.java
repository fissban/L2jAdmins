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
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q417_PathToBecomeAScavenger extends Script
{
	// Items
	private static final int RING_OF_RAVEN = 1642;
	private static final int PIPPI_LETTER = 1643;
	private static final int RAUT_TELEPORT_SCROLL = 1644;
	private static final int SUCCUBUS_UNDIES = 1645;
	private static final int MION_LETTER = 1646;
	private static final int BRONK_INGOT = 1647;
	private static final int SHARI_AXE = 1648;
	private static final int ZIMENF_POTION = 1649;
	private static final int BRONK_PAY = 1650;
	private static final int SHARI_PAY = 1651;
	private static final int ZIMENF_PAY = 1652;
	private static final int BEAR_PICTURE = 1653;
	private static final int TARANTULA_PICTURE = 1654;
	private static final int HONEY_JAR = 1655;
	private static final int BEAD = 1656;
	private static final int BEAD_PARCEL_1 = 1657;
	
	// NPCs
	private static final int RAUT = 7316;
	private static final int SHARI = 7517;
	private static final int MION = 7519;
	private static final int PIPPI = 7524;
	private static final int BRONK = 7525;
	private static final int ZIMENF = 7538;
	private static final int TOMA = 7556;
	private static final int TORAI = 7557;
	
	// Monsters
	private static final int HUNTER_TARANTULA = 403;
	private static final int PLUNDER_TARANTULA = 508;
	private static final int HUNTER_BEAR = 777;
	private static final int HONEY_BEAR = 5058;
	
	public Q417_PathToBecomeAScavenger()
	{
		super(417, "Path To Become A Scavenger");
		
		registerItems(PIPPI_LETTER, RAUT_TELEPORT_SCROLL, SUCCUBUS_UNDIES, MION_LETTER, BRONK_INGOT, SHARI_AXE, ZIMENF_POTION, BRONK_PAY, SHARI_PAY, ZIMENF_PAY, BEAR_PICTURE, TARANTULA_PICTURE, HONEY_JAR, BEAD, BEAD_PARCEL_1);
		
		addStartNpc(PIPPI);
		addTalkId(RAUT, SHARI, MION, PIPPI, BRONK, ZIMENF, TOMA, TORAI);
		
		addKillId(HUNTER_TARANTULA, PLUNDER_TARANTULA, HUNTER_BEAR, HONEY_BEAR);
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
		
		// PIPPI
		if (event.equalsIgnoreCase("7524-05.htm"))
		{
			if (player.getClassId() != ClassId.DWARF_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.SCAVENGER ? "7524-02a.htm" : "7524-08.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7524-02.htm";
			}
			else if (st.hasItems(RING_OF_RAVEN))
			{
				htmltext = "7524-04.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				st.giveItems(PIPPI_LETTER, 1);
			}
		}
		// MION
		else if (event.equalsIgnoreCase("7519_1"))
		{
			final int random = Rnd.get(3);
			
			htmltext = "7519-0" + (random + 2) + ".htm";
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(PIPPI_LETTER, -1);
			st.giveItems(ZIMENF_POTION - random, 1);
		}
		else if (event.equalsIgnoreCase("7519_2"))
		{
			final int random = Rnd.get(3);
			
			htmltext = "7519-0" + (random + 2) + ".htm";
			st.takeItems(BRONK_PAY, -1);
			st.takeItems(SHARI_PAY, -1);
			st.takeItems(ZIMENF_PAY, -1);
			st.giveItems(ZIMENF_POTION - random, 1);
		}
		else if (event.equalsIgnoreCase("7519-07.htm"))
		{
			st.set("id", String.valueOf(st.getInt("id") + 1));
		}
		else if (event.equalsIgnoreCase("7519-09.htm"))
		{
			final int id = st.getInt("id");
			if ((id / 10) < 2)
			{
				htmltext = "7519-07.htm";
				st.set("id", String.valueOf(id + 1));
			}
			else if ((id / 10) == 2)
			{
				st.set("id", String.valueOf(id + 1));
			}
			else if ((id / 10) >= 3)
			{
				htmltext = "7519-10.htm";
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(SHARI_AXE, -1);
				st.takeItems(ZIMENF_POTION, -1);
				st.takeItems(BRONK_INGOT, -1);
				st.giveItems(MION_LETTER, 1);
			}
		}
		else if (event.equalsIgnoreCase("7519-11.htm") && Rnd.nextBoolean())
		{
			htmltext = "7519-06.htm";
		}
		else if (event.equalsIgnoreCase("7556-05b.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BEAD, -1);
			st.takeItems(TARANTULA_PICTURE, 1);
			st.giveItems(BEAD_PARCEL_1, 1);
		}
		// RAUT
		else if (event.equalsIgnoreCase("7316-02.htm") || event.equalsIgnoreCase("7316-03.htm"))
		{
			st.set("cond", "10");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BEAD_PARCEL_1, 1);
			st.giveItems(RAUT_TELEPORT_SCROLL, 1);
		}
		// TORAI
		else if (event.equalsIgnoreCase("7557-03.htm"))
		{
			st.set("cond", "11");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(RAUT_TELEPORT_SCROLL, 1);
			st.giveItems(SUCCUBUS_UNDIES, 1);
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
				htmltext = "7524-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case PIPPI:
						if (cond == 1)
						{
							htmltext = "7524-06.htm";
						}
						else if (cond > 1)
						{
							htmltext = "7524-07.htm";
						}
						break;
					
					case MION:
						if (st.hasItems(PIPPI_LETTER))
						{
							htmltext = "7519-01.htm";
						}
						else if (st.hasAtLeastOneItem(BRONK_INGOT, SHARI_AXE, ZIMENF_POTION))
						{
							final int id = st.getInt("id");
							if ((id / 10) == 0)
							{
								htmltext = "7519-05.htm";
							}
							else
							{
								htmltext = "7519-08.htm";
							}
						}
						else if (st.hasAtLeastOneItem(BRONK_PAY, SHARI_PAY, ZIMENF_PAY))
						{
							final int id = st.getInt("id");
							if (id < 50)
							{
								htmltext = "7519-12.htm";
							}
							else
							{
								htmltext = "7519-15.htm";
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(BRONK_PAY, -1);
								st.takeItems(SHARI_PAY, -1);
								st.takeItems(ZIMENF_PAY, -1);
								st.giveItems(MION_LETTER, 1);
							}
						}
						else if (cond == 4)
						{
							htmltext = "7519-13.htm";
						}
						else if (cond > 4)
						{
							htmltext = "7519-14.htm";
						}
						break;
					
					case SHARI:
						if (st.hasItems(SHARI_AXE))
						{
							final int id = st.getInt("id");
							if (id < 20)
							{
								htmltext = "7517-01.htm";
							}
							else
							{
								htmltext = "7517-02.htm";
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							st.set("id", String.valueOf(id + 10));
							st.takeItems(SHARI_AXE, 1);
							st.giveItems(SHARI_PAY, 1);
						}
						else if (st.hasItems(SHARI_PAY))
						{
							htmltext = "7517-03.htm";
						}
						break;
					
					case BRONK:
						if (st.hasItems(BRONK_INGOT))
						{
							final int id = st.getInt("id");
							if (id < 20)
							{
								htmltext = "7525-01.htm";
							}
							else
							{
								htmltext = "7525-02.htm";
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							st.set("id", String.valueOf(id + 10));
							st.takeItems(BRONK_INGOT, 1);
							st.giveItems(BRONK_PAY, 1);
						}
						else if (st.hasItems(BRONK_PAY))
						{
							htmltext = "7525-03.htm";
						}
						break;
					
					case ZIMENF:
						if (st.hasItems(ZIMENF_POTION))
						{
							final int id = st.getInt("id");
							if (id < 20)
							{
								htmltext = "7538-01.htm";
							}
							else
							{
								htmltext = "7538-02.htm";
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							st.set("id", String.valueOf(id + 10));
							st.takeItems(ZIMENF_POTION, 1);
							st.giveItems(ZIMENF_PAY, 1);
						}
						else if (st.hasItems(ZIMENF_PAY))
						{
							htmltext = "7538-03.htm";
						}
						break;
					
					case TOMA:
						if (cond == 4)
						{
							htmltext = "7556-01.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(MION_LETTER, 1);
							st.giveItems(BEAR_PICTURE, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7556-02.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7556-03.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(HONEY_JAR, -1);
							st.takeItems(BEAR_PICTURE, 1);
							st.giveItems(TARANTULA_PICTURE, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7556-04.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7556-05a.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7556-06a.htm";
						}
						else if ((cond == 10) || (cond == 11))
						{
							htmltext = "7556-07.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7556-06c.htm";
						}
						break;
					
					case RAUT:
						if (cond == 9)
						{
							htmltext = "7316-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7316-04.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7316-05.htm";
							st.takeItems(SUCCUBUS_UNDIES, 1);
							st.giveItems(RING_OF_RAVEN, 1);
							st.rewardExpAndSp(3200, 7080);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case TORAI:
						if (cond == 10)
						{
							htmltext = "7557-01.htm";
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
			case HUNTER_BEAR:
				if (st.getInt("cond") == 5)
				{
					final int step = st.getInt("step");
					if (step > 20)
					{
						if (((step - 20) * 10) >= Rnd.get(100))
						{
							addSpawn(HONEY_BEAR, npc, false, 300000);
							st.unset("step");
						}
						else
						{
							st.set("step", String.valueOf(step + 1));
						}
					}
					else
					{
						st.set("step", String.valueOf(step + 1));
					}
				}
				break;
			
			case HONEY_BEAR:
				if ((st.getInt("cond") == 5) && (npc.getIsSpoiledBy() == player.getObjectId()) && st.dropItemsAlways(HONEY_JAR, 1, 5))
				{
					st.set("cond", "6");
				}
				break;
			
			case HUNTER_TARANTULA:
			case PLUNDER_TARANTULA:
				if ((st.getInt("cond") == 7) && (npc.getIsSpoiledBy() == player.getObjectId()) && st.dropItems(BEAD, 1, 20, npc.getId() == HUNTER_TARANTULA ? 333333 : 600000))
				{
					st.set("cond", "8");
				}
				break;
		}
		
		return null;
	}
	
}
