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
public class Q411_PathToAnAssassin extends Script
{
	// Items
	private static final int SHILEN_CALL = 1245;
	private static final int ARKENIA_LETTER = 1246;
	private static final int LEIKAN_NOTE = 1247;
	private static final int MOONSTONE_BEAST_MOLAR = 1248;
	private static final int SHILEN_TEARS = 1250;
	private static final int ARKENIA_RECOMMENDATION = 1251;
	private static final int IRON_HEART = 1252;
	
	// NPCs
	private static final int TRISKEL = 7416;
	private static final int ARKENIA = 7419;
	private static final int LEIKAN = 7382;
	
	public Q411_PathToAnAssassin()
	{
		super(411, "Path to an Assassin");
		
		registerItems(SHILEN_CALL, ARKENIA_LETTER, LEIKAN_NOTE, MOONSTONE_BEAST_MOLAR, SHILEN_TEARS, ARKENIA_RECOMMENDATION);
		
		addStartNpc(TRISKEL);
		addTalkId(TRISKEL, ARKENIA, LEIKAN);
		
		addKillId(5036, 369);
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
		
		if (event.equalsIgnoreCase("7416-05.htm"))
		{
			if (player.getClassId() != ClassId.DARK_ELF_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.ASSASSIN ? "7416-02a.htm" : "7416-02.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7416-03.htm";
			}
			else if (st.hasItems(IRON_HEART))
			{
				htmltext = "7416-04.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				st.giveItems(SHILEN_CALL, 1);
			}
		}
		else if (event.equalsIgnoreCase("7419-05.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SHILEN_CALL, 1);
			st.giveItems(ARKENIA_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7382-03.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ARKENIA_LETTER, 1);
			st.giveItems(LEIKAN_NOTE, 1);
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
				htmltext = "7416-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case TRISKEL:
						if (cond == 1)
						{
							htmltext = "7416-11.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7416-07.htm";
						}
						else if ((cond == 3) || (cond == 4))
						{
							htmltext = "7416-08.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7416-09.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7416-10.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7416-06.htm";
							st.takeItems(ARKENIA_RECOMMENDATION, 1);
							st.giveItems(IRON_HEART, 1);
							st.rewardExpAndSp(3200, 3930);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case ARKENIA:
						if (cond == 1)
						{
							htmltext = "7419-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7419-07.htm";
						}
						else if ((cond == 3) || (cond == 4))
						{
							htmltext = "7419-10.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7419-11.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7419-08.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SHILEN_TEARS, -1);
							st.giveItems(ARKENIA_RECOMMENDATION, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7419-09.htm";
						}
						break;
					
					case LEIKAN:
						if (cond == 2)
						{
							htmltext = "7382-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = !st.hasItems(MOONSTONE_BEAST_MOLAR) ? "7382-05.htm" : "7382-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7382-07.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(MOONSTONE_BEAST_MOLAR, -1);
							st.takeItems(LEIKAN_NOTE, -1);
						}
						else if (cond == 5)
						{
							htmltext = "7382-09.htm";
						}
						else if (cond > 5)
						{
							htmltext = "7382-08.htm";
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
		
		if (npc.getId() == 369)
		{
			if ((st.getInt("cond") == 3) && st.dropItemsAlways(MOONSTONE_BEAST_MOLAR, 1, 10))
			{
				st.set("cond", "4");
			}
		}
		else if (st.getInt("cond") == 5)
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(SHILEN_TEARS, 1);
		}
		
		return null;
	}
	
}
