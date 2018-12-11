package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
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
public class Q246_PossessorOfAPreciousSoul extends Script
{
	// NPCs
	private static final int CARADINE = 8740;
	private static final int OSSIAN = 8741;
	private static final int LADD = 7721;
	// ITEMs
	private static final int WATERBINDER = 7591;
	private static final int EVERGREEN = 7592;
	private static final int RAIN_SONG = 7593;
	private static final int RELIC_BOX = 7594;
	private static final int CARADINE_LETTER_1 = 7678;
	private static final int CARADINE_LETTER_2 = 7679;
	// MOBs
	private static final int PILGRIM_OF_SPLENDOR = 1541;
	private static final int JUDGE_OF_SPLENDOR = 1544;
	private static final int BARAKIEL = 10325;
	
	public Q246_PossessorOfAPreciousSoul()
	{
		super(246, "Possessor of a Precious Soul - 3");
		
		registerItems(WATERBINDER, EVERGREEN, RAIN_SONG, RELIC_BOX);
		
		addStartNpc(CARADINE);
		addTalkId(CARADINE, OSSIAN, LADD);
		addKillId(PILGRIM_OF_SPLENDOR, JUDGE_OF_SPLENDOR, BARAKIEL);
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
		
		// Caradine
		if (event.equalsIgnoreCase("8740-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.takeItems(CARADINE_LETTER_1, 1);
		}
		// Ossian
		else if (event.equalsIgnoreCase("8741-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8741-05.htm"))
		{
			if (st.hasItems(WATERBINDER, EVERGREEN))
			{
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(WATERBINDER, 1);
				st.takeItems(EVERGREEN, 1);
			}
			else
			{
				htmltext = null;
			}
		}
		else if (event.equalsIgnoreCase("8741-08.htm"))
		{
			if (st.hasItems(RAIN_SONG))
			{
				st.set("cond", "6");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(RAIN_SONG, 1);
				st.giveItems(RELIC_BOX, 1);
			}
			else
			{
				htmltext = null;
			}
		}
		// Ladd
		else if (event.equalsIgnoreCase("7721-02.htm"))
		{
			if (st.hasItems(RELIC_BOX))
			{
				st.takeItems(RELIC_BOX, 1);
				st.giveItems(CARADINE_LETTER_2, 1);
				st.rewardExpAndSp(719843, 0);
				player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(false);
			}
			else
			{
				htmltext = null;
			}
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
				if (st.hasItems(CARADINE_LETTER_1))
				{
					htmltext = !player.isSubClassActive() || (player.getLevel() < 65) ? "8740-02.htm" : "8740-01.htm";
				}
				break;
			
			case STARTED:
				if (!player.isSubClassActive())
				{
					break;
				}
				
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case CARADINE:
						if (cond == 1)
						{
							htmltext = "8740-05.htm";
						}
						break;
					
					case OSSIAN:
						if (cond == 1)
						{
							htmltext = "8741-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8741-03.htm";
						}
						else if (cond == 3)
						{
							if (st.hasItems(WATERBINDER, EVERGREEN))
							{
								htmltext = "8741-04.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "8741-06.htm";
						}
						else if (cond == 5)
						{
							if (st.hasItems(RAIN_SONG))
							{
								htmltext = "8741-07.htm";
							}
						}
						else if (cond == 6)
						{
							htmltext = "8741-09.htm";
						}
						break;
					
					case LADD:
						if ((cond == 6) && st.hasItems(RELIC_BOX))
						{
							htmltext = "7721-01.htm";
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
		final int npcId = npc.getId();
		if (npcId == BARAKIEL)
		{
			for (final L2PcInstance plr : getPartyMembers(player, npc, "cond", "4"))
			{
				if (!plr.isSubClassActive())
				{
					continue;
				}
				
				final ScriptState st = plr.getScriptState(getName());
				if (!st.hasItems(RAIN_SONG))
				{
					st.set("cond", "5");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(RAIN_SONG, 1);
				}
			}
		}
		else
		{
			if (!player.isSubClassActive())
			{
				return null;
			}
			
			final ScriptState st = checkPlayerCondition(player, npc, "cond", "2");
			if (st == null)
			{
				return null;
			}
			
			if (Rnd.get(10) < 2)
			{
				final int neklaceOrRing = npcId == PILGRIM_OF_SPLENDOR ? WATERBINDER : EVERGREEN;
				
				if (!st.hasItems(neklaceOrRing))
				{
					st.giveItems(neklaceOrRing, 1);
					
					if (!st.hasItems(npcId == PILGRIM_OF_SPLENDOR ? EVERGREEN : WATERBINDER))
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
					else
					{
						st.set("cond", "3");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
					}
				}
			}
		}
		return null;
	}
	
}
