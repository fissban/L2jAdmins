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
public class Q418_PathToAnArtisan extends Script
{
	// Items
	private static final int SILVERA_RING = 1632;
	private static final int FIRST_PASS_CERTIFICATE = 1633;
	private static final int SECOND_PASS_CERTIFICATE = 1634;
	private static final int FINAL_PASS_CERTIFICATE = 1635;
	private static final int BOOGLE_RATMAN_TOOTH = 1636;
	private static final int BOOGLE_RATMAN_LEADER_TOOTH = 1637;
	private static final int KLUTO_LETTER = 1638;
	private static final int FOOTPRINT_OF_THIEF = 1639;
	private static final int STOLEN_SECRET_BOX = 1640;
	private static final int SECRET_BOX = 1641;
	
	// NPCs
	private static final int SILVERA = 7527;
	private static final int KLUTO = 7317;
	private static final int PINTER = 7298;
	// private static final int OBI = 32052;
	// private static final int HITCHI = 31963;
	private static final int LOCKIRIN = 7531;
	// private static final int RYDEL = 31956;
	
	// XXX MauroNOB: non-interlude npc is commented, but is not necessary to complete quest.
	
	public Q418_PathToAnArtisan()
	{
		super(418, "Path to an Artisan");
		
		registerItems(SILVERA_RING, FIRST_PASS_CERTIFICATE, SECOND_PASS_CERTIFICATE, BOOGLE_RATMAN_TOOTH, BOOGLE_RATMAN_LEADER_TOOTH, KLUTO_LETTER, FOOTPRINT_OF_THIEF, STOLEN_SECRET_BOX, SECRET_BOX);
		
		addStartNpc(SILVERA);
		addTalkId(SILVERA, KLUTO, PINTER, LOCKIRIN);
		
		addKillId(389, 390, 17);
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
		
		if (event.equalsIgnoreCase("7527-05.htm"))
		{
			if (player.getClassId() != ClassId.DWARF_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.ARTISAN ? "7527-02a.htm" : "7527-02.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7527-03.htm";
			}
			else if (st.hasItems(FINAL_PASS_CERTIFICATE))
			{
				htmltext = "7527-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7527-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(SILVERA_RING, 1);
		}
		else if (event.equalsIgnoreCase("7527-08a.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BOOGLE_RATMAN_LEADER_TOOTH, -1);
			st.takeItems(BOOGLE_RATMAN_TOOTH, -1);
			st.takeItems(SILVERA_RING, 1);
			st.giveItems(FIRST_PASS_CERTIFICATE, 1);
		}
		/*
		 * else if (event.equalsIgnoreCase("7527-08b.htm")) { st.set("cond", "8"); st.playSound(PlaySoundType.QUEST_MIDDLE); st.takeItems(BOOGLE_RATMAN_LEADER_TOOTH, -1); st.takeItems(BOOGLE_RATMAN_TOOTH, -1); st.takeItems(SILVERA_RING, 1); }
		 */
		else if (event.equalsIgnoreCase("7317-04.htm") || event.equalsIgnoreCase("7317-07.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(KLUTO_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7317-10.htm"))
		{
			st.takeItems(FIRST_PASS_CERTIFICATE, 1);
			st.takeItems(SECOND_PASS_CERTIFICATE, 1);
			st.takeItems(SECRET_BOX, 1);
			st.giveItems(FINAL_PASS_CERTIFICATE, 1);
			st.rewardExpAndSp(3200, 6980);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7317-12.htm") || event.equalsIgnoreCase("7531-05.htm") || event.equalsIgnoreCase("32052-11.htm") || event.equalsIgnoreCase("31963-10.htm") || event.equalsIgnoreCase("31956-04.htm"))
		{
			st.takeItems(FIRST_PASS_CERTIFICATE, 1);
			st.takeItems(SECOND_PASS_CERTIFICATE, 1);
			st.takeItems(SECRET_BOX, 1);
			st.giveItems(FINAL_PASS_CERTIFICATE, 1);
			st.rewardExpAndSp(3200, 3490);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7298-03.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(KLUTO_LETTER, -1);
			st.giveItems(FOOTPRINT_OF_THIEF, 1);
		}
		else if (event.equalsIgnoreCase("7298-06.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(FOOTPRINT_OF_THIEF, -1);
			st.takeItems(STOLEN_SECRET_BOX, -1);
			st.giveItems(SECOND_PASS_CERTIFICATE, 1);
			st.giveItems(SECRET_BOX, 1);
		}
		/*
		 * else if (event.equalsIgnoreCase("32052-06.htm")) { st.set("cond", "9"); st.playSound(PlaySoundType.QUEST_MIDDLE); } else if (event.equalsIgnoreCase("31963-04.htm")) { st.set("cond", "10"); st.playSound(PlaySoundType.QUEST_MIDDLE); } else if (event.equalsIgnoreCase("31963-05.htm")) {
		 * st.set("cond", "11"); st.playSound(PlaySoundType.QUEST_MIDDLE); } else if (event.equalsIgnoreCase("31963-07.htm")) { st.set("cond", "12"); st.playSound(PlaySoundType.QUEST_MIDDLE); }
		 */// CaFi//
		
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
				htmltext = "7527-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case SILVERA:
						if (cond == 1)
						{
							htmltext = "7527-07.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7527-08.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7527-09.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7527-09a.htm";
						}
						break;
					
					case KLUTO:
						if (cond == 3)
						{
							htmltext = "7317-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7317-08.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7317-09.htm";
						}
						break;
					
					case PINTER:
						if (cond == 4)
						{
							htmltext = "7298-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7298-04.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7298-05.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7298-07.htm";
						}
						break;
					/*
					 * case OBI: if (cond == 8) { htmltext = "32052-01.htm"; } else if (cond == 9) { htmltext = "32052-06a.htm"; } else if (cond == 11) { htmltext = "32052-07.htm"; } break; case HITCHI: if (cond == 9) { htmltext = "31963-01.htm"; } else if (cond == 10) { htmltext = "31963-04.htm"; }
					 * else if (cond == 11) { htmltext = "31963-06a.htm"; } else if (cond == 12) { htmltext = "31963-08.htm"; } break;
					 */
					
					case LOCKIRIN:
						if (cond == 10)
						{
							htmltext = "7531-01.htm";
						}
						break;
					
					/*
					 * case RYDEL: if (cond == 12) { htmltext = "31956-01.htm"; } break;
					 */
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
			case 389:
				if ((st.getInt("cond") == 1) && st.dropItems(BOOGLE_RATMAN_TOOTH, 1, 10, 700000) && (st.getItemsCount(BOOGLE_RATMAN_LEADER_TOOTH) == 2))
				{
					st.set("cond", "2");
				}
				break;
			
			case 390:
				if ((st.getInt("cond") == 1) && st.dropItems(BOOGLE_RATMAN_LEADER_TOOTH, 1, 2, 500000) && (st.getItemsCount(BOOGLE_RATMAN_TOOTH) == 10))
				{
					st.set("cond", "2");
				}
				break;
			
			case 17:
				if ((st.getInt("cond") == 5) && st.dropItems(STOLEN_SECRET_BOX, 1, 1, 200000))
				{
					st.set("cond", "6");
				}
				break;
		}
		
		return null;
	}
	
}
