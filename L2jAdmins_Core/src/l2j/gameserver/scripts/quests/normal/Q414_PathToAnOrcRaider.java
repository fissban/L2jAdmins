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
 * @author        MauroNOB, CaFi, zarie
 * @originalQuest aCis
 */
public class Q414_PathToAnOrcRaider extends Script
{
	// QUEST ITEMs
	private static final int GREEN_BLOOD = 1578;
	private static final int GOBLIN_DWELLING_MAP = 1579;
	private static final int KURUKA_RATMAN_TOOTH = 1580;
	private static final int BETRAYER_REPORT = 1581;
	private static final int HEAD_OF_BETRAYER = 1591;
	private static final int TIMORA_ORC_HEAD = 8544;
	
	// NPCs
	private static final int KASMAN = 7501;
	private static final int KARUKIA = 7570;
	// private static final int TAZEER = 31978;
	
	// MONSTERs
	private static final int GOBLIN_TOMB_RAIDER_LEADER = 320;
	
	// QUEST MONSTERs
	private static final int KURUKA_RATMAN_LEADER = 5045;
	private static final int UMBAR_ORC = 5054;
	// private static final int TIMORA_ORC = 27320;
	
	// REWARD
	private static final int MARK_OF_RAIDER = 1592;
	
	// XXX MauroNOB: non-interlude npc is commented, but is not necessary to complete quest.
	
	public Q414_PathToAnOrcRaider()
	{
		super(414, "Path to an Orc Raider");
		addStartNpc(KARUKIA);
		addTalkId(KARUKIA, KASMAN);
		registerItems(GOBLIN_DWELLING_MAP, GREEN_BLOOD, KURUKA_RATMAN_TOOTH, BETRAYER_REPORT, HEAD_OF_BETRAYER, TIMORA_ORC_HEAD);
		addKillId(GOBLIN_TOMB_RAIDER_LEADER, KURUKA_RATMAN_LEADER, UMBAR_ORC);
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
		
		// KARUKIA
		if (event.equalsIgnoreCase("7570-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(GOBLIN_DWELLING_MAP, 1);
			
		}
		else if (event.equalsIgnoreCase("7570-07a.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(GOBLIN_DWELLING_MAP, 1);
			st.takeItems(KURUKA_RATMAN_TOOTH, 10);
			st.giveItems(BETRAYER_REPORT, 1);
		}
		// else if (event.equalsIgnoreCase("7570-07b.htm"))
		// {
		// st.set("cond", "5");
		// st.playSound(PlaySoundType.QUEST_MIDDLE);
		// st.takeItems(KURUKA_RATMAN_TOOTH, 10);
		// st.giveItems(BETRAYER_REPORT, 1);
		// }
		// TAZEER
		else if (event.equalsIgnoreCase("31978-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
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
				htmltext = player.getLevel() < 18 ? "7570-02.htm" : "7570-01.htm";
				if (st.hasItems(MARK_OF_RAIDER))
				{
					htmltext = "7570-04.htm";
				}
				else if (player.getClassId() == ClassId.RAIDER)
				{
					htmltext = "7570-02a.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case KARUKIA:
						if (cond == 1)
						{
							htmltext = "7570-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7570-07.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7570-09.htm";
						}
						break;
					case KASMAN:
						if (cond == 3)
						{
							htmltext = st.hasItems(HEAD_OF_BETRAYER) ? "7501-02.htm" : "7501-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7501-03.htm";
							st.takeItems(HEAD_OF_BETRAYER, 2);
							st.giveItems(MARK_OF_RAIDER, 1);
							st.rewardExpAndSp(3200, 4230);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
						}
						break;
					/*
					 * case TAZEER: if (cond == 5) { htmltext = "8978-01.htm"; } else if (cond == 6) { htmltext = "8978-03.htm"; } else if (cond == 7) { htmltext = "8978-04.htm"; st.takeItems(TIMORA_ORC_HEAD, 1); st.giveItems(MARK_OF_RAIDER, 1); st.rewardExpAndSp(3200, 4230);
					 * st.playSound(PlaySoundType.QUEST_FINISH); st.exitQuest(false); player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY)); } break;
					 */
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int rnd = Rnd.get(1, 10);
		switch (npc.getId())
		{
			case GOBLIN_TOMB_RAIDER_LEADER:
				if ((st.getInt("cond") == 1) && st.dropItems(GREEN_BLOOD, 1, rnd, 400000))
				{
					st.playSound(PlaySoundType.QUEST_BEFORE_BATTLE);
					st.takeItems(GREEN_BLOOD, rnd);
					addSpawn(KURUKA_RATMAN_LEADER, npc, false, 120000);
				}
				break;
			case KURUKA_RATMAN_LEADER:
				if ((st.getInt("cond") == 1) && st.dropItemsAlways(KURUKA_RATMAN_TOOTH, 1, 10))
				{
					st.set("cond", "2");
				}
				break;
			case UMBAR_ORC:
				if ((st.getInt("cond") == 3) && st.dropItems(HEAD_OF_BETRAYER, 1, 2, 400000))
				{
					st.set("cond", "4");
				}
				break;
			/*
			 * case TIMORA_ORC: if ((st.getInt("cond") == 6) && st.dropItems(TIMORA_ORC_HEAD, 1, 1, 400000)) { st.set("cond", "7"); } break;
			 */
		}
		return null;
	}
	
}
