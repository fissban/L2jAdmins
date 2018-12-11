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
public class Q107_MercilessPunishment extends Script
{
	// NPCs
	private static final int HATOS = 7568;
	private static final int PARUGON = 7580;
	// ITEMs
	private static final int HATOS_ORDER_1 = 1553;
	private static final int HATOS_ORDER_2 = 1554;
	private static final int HATOS_ORDER_3 = 1555;
	private static final int LETTER_TO_HUMAN = 1557;
	private static final int LETTER_TO_DARKELF = 1556;
	private static final int LETTER_TO_ELF = 1558;
	// REWARDs
	private static final int BUTCHER_SWORD = 1510;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	private static final int LESSER_HEALING_POTION = 1060;
	// MOBs
	private static final int BARANKA = 5041;
	
	public Q107_MercilessPunishment()
	{
		super(107, "Merciless Punishment");
		
		registerItems(HATOS_ORDER_1, HATOS_ORDER_2, HATOS_ORDER_3, LETTER_TO_HUMAN, LETTER_TO_DARKELF, LETTER_TO_ELF);
		
		addStartNpc(HATOS);
		addTalkId(HATOS, PARUGON);
		addKillId(BARANKA); // Baranka's Messenger
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
		
		if (event.equalsIgnoreCase("7568-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(HATOS_ORDER_1, 1);
		}
		else if (event.equalsIgnoreCase("7568-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7568-07.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HATOS_ORDER_1, 1);
			st.giveItems(HATOS_ORDER_2, 1);
		}
		else if (event.equalsIgnoreCase("7568-09.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HATOS_ORDER_2, 1);
			st.giveItems(HATOS_ORDER_3, 1);
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7568-00.htm";
				}
				else if (player.getLevel() < 12)
				{
					htmltext = "7568-01.htm";
				}
				else
				{
					htmltext = "7568-02.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case HATOS:
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "7568-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7568-05.htm";
						}
						else if ((cond == 4) || (cond == 6))
						{
							htmltext = "7568-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7568-08.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7568-10.htm";
							st.takeItems(HATOS_ORDER_3, -1);
							st.takeItems(LETTER_TO_DARKELF, -1);
							st.takeItems(LETTER_TO_HUMAN, -1);
							st.takeItems(LETTER_TO_ELF, -1);
							
							st.giveItems(BUTCHER_SWORD, 1);
							st.giveItems(LESSER_HEALING_POTION, 100);
							
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
					
					case PARUGON:
						htmltext = "7580-01.htm";
						if (cond == 1)
						{
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int cond = st.getInt("cond");
		
		if (cond == 2)
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(LETTER_TO_HUMAN, 1);
		}
		else if (cond == 4)
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(LETTER_TO_DARKELF, 1);
		}
		else if (cond == 6)
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(LETTER_TO_ELF, 1);
		}
		
		return null;
	}
	
}
