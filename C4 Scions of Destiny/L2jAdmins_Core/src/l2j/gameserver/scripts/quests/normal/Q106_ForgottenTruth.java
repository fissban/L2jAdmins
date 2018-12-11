package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q106_ForgottenTruth extends Script
{
	// NPCs
	private static final int THIFIELL = 7358;
	private static final int KARTIA = 7133;
	// ITEMs
	private static final int ONYX_TALISMAN_1 = 984;
	private static final int ONYX_TALISMAN_2 = 985;
	private static final int ANCIENT_SCROLL = 986;
	private static final int ANCIENT_CLAY_TABLET = 987;
	private static final int KARTIA_TRANSLATION = 988;
	// REWARDs
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int ELDRITCH_DAGGER = 989;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	private static final int LESSER_HEALING_POTION = 1060;
	// MOBs
	private static final int TUMRAN_ORC_BRIGAND = 5070;
	
	public Q106_ForgottenTruth()
	{
		super(106, "Forgotten Truth");
		
		registerItems(ONYX_TALISMAN_1, ONYX_TALISMAN_2, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET, KARTIA_TRANSLATION);
		
		addStartNpc(THIFIELL);
		addTalkId(THIFIELL, KARTIA);
		addKillId(TUMRAN_ORC_BRIGAND);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		final String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7358-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(ONYX_TALISMAN_1, 1);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7358-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "7358-02.htm";
				}
				else
				{
					htmltext = "7358-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case THIFIELL:
						if (cond == 1)
						{
							htmltext = "7358-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7358-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7358-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7358-07.htm";
							st.takeItems(KARTIA_TRANSLATION, 1);
							st.giveItems(ELDRITCH_DAGGER, 1);
							st.giveItems(LESSER_HEALING_POTION, 100);
							
							if (player.isMageClass())
							{
								st.giveItems(SPIRITSHOT_NO_GRADE, 500);
							}
							else
							{
								st.giveItems(SOULSHOT_NO_GRADE, 1000);
							}
							
							if (player.isNewbie())
							{
								st.showQuestionMark(26);
								if (player.isMageClass())
								{
									st.playSound("tutorial_voice_027");
									st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
								}
								else
								{
									st.playSound("tutorial_voice_026");
									st.giveItems(SOULSHOT_FOR_BEGINNERS, 6000);
								}
							}
							
							st.giveItems(ECHO_BATTLE, 10);
							st.giveItems(ECHO_LOVE, 10);
							st.giveItems(ECHO_SOLITUDE, 10);
							st.giveItems(ECHO_FEAST, 10);
							st.giveItems(ECHO_CELEBRATION, 10);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case KARTIA:
						if (cond == 1)
						{
							htmltext = "7133-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ONYX_TALISMAN_1, 1);
							st.giveItems(ONYX_TALISMAN_2, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7133-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7133-03.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ONYX_TALISMAN_2, 1);
							st.takeItems(ANCIENT_SCROLL, 1);
							st.takeItems(ANCIENT_CLAY_TABLET, 1);
							st.giveItems(KARTIA_TRANSLATION, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7133-04.htm";
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
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "2");
		if (st == null)
		{
			return null;
		}
		
		if (!st.hasItems(ANCIENT_SCROLL))
		{
			st.dropItems(ANCIENT_SCROLL, 1, 1, 200000);
		}
		else if (st.dropItems(ANCIENT_CLAY_TABLET, 1, 1, 200000))
		{
			st.set("cond", "3");
		}
		
		return null;
	}
	
}
