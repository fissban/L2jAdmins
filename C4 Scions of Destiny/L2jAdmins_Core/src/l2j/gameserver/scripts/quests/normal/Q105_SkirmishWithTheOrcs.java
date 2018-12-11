
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
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q105_SkirmishWithTheOrcs extends Script
{
	// NPCs
	private static final int KENDELL = 7218;
	// ITEMs
	private static final int KENDELL_ORDER_1 = 1836;
	private static final int KENDELL_ORDER_2 = 1837;
	private static final int KENDELL_ORDER_3 = 1838;
	private static final int KENDELL_ORDER_4 = 1839;
	private static final int KENDELL_ORDER_5 = 1840;
	private static final int KENDELL_ORDER_6 = 1841;
	private static final int KENDELL_ORDER_7 = 1842;
	private static final int KENDELL_ORDER_8 = 1843;
	private static final int KABOO_CHIEF_TORC_1 = 1844;
	private static final int KABOO_CHIEF_TORC_2 = 1845;
	// MONSTERs
	private static final int KABOO_CHIEF_UOPH = 5059;
	private static final int KABOO_CHIEF_KRACHA = 5060;
	private static final int KABOO_CHIEF_BATOH = 5061;
	private static final int KABOO_CHIEF_TANUKIA = 5062;
	private static final int KABOO_CHIEF_TUREL = 5064;
	private static final int KABOO_CHIEF_ROKO = 5065;
	private static final int KABOO_CHIEF_KAMUT = 5067;
	private static final int KABOO_CHIEF_MURTIKA = 5068;
	// REWARDs
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int RED_SUNSET_STAFF = 754;
	private static final int RED_SUNSET_SWORD = 981;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q105_SkirmishWithTheOrcs()
	{
		super(105, "Skirmish with the Orcs");
		
		registerItems(KENDELL_ORDER_1, KENDELL_ORDER_2, KENDELL_ORDER_3, KENDELL_ORDER_4, KENDELL_ORDER_5, KENDELL_ORDER_6, KENDELL_ORDER_7, KENDELL_ORDER_8, KABOO_CHIEF_TORC_1, KABOO_CHIEF_TORC_2);
		
		addStartNpc(KENDELL); // Kendell
		addTalkId(KENDELL);
		addKillId(KABOO_CHIEF_UOPH, KABOO_CHIEF_KRACHA, KABOO_CHIEF_BATOH, KABOO_CHIEF_TANUKIA, KABOO_CHIEF_TUREL, KABOO_CHIEF_ROKO, KABOO_CHIEF_KAMUT, KABOO_CHIEF_MURTIKA);
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
		
		if (event.equalsIgnoreCase("7218-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(Rnd.get(1836, 1839), 1); // Kendell's orders 1 to 4.
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7218-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "7221-01.htm";
				}
				else
				{
					htmltext = "7218-02.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7218-05.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7218-06.htm";
					st.set("cond", "3");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(KABOO_CHIEF_TORC_1, 1);
					st.takeItems(KENDELL_ORDER_1, 1);
					st.takeItems(KENDELL_ORDER_2, 1);
					st.takeItems(KENDELL_ORDER_3, 1);
					st.takeItems(KENDELL_ORDER_4, 1);
					st.giveItems(Rnd.get(1840, 1843), 1); // Kendell's orders 5 to 8.
				}
				else if (cond == 3)
				{
					htmltext = "7218-07.htm";
				}
				else if (cond == 4)
				{
					htmltext = "7218-08.htm";
					st.takeItems(KABOO_CHIEF_TORC_2, 1);
					st.takeItems(KENDELL_ORDER_5, 1);
					st.takeItems(KENDELL_ORDER_6, 1);
					st.takeItems(KENDELL_ORDER_7, 1);
					st.takeItems(KENDELL_ORDER_8, 1);
					
					if (player.isMageClass())
					{
						st.giveItems(RED_SUNSET_STAFF, 1);
					}
					else
					{
						st.giveItems(RED_SUNSET_SWORD, 1);
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
							st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
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
		
		switch (npc.getId())
		{
			case KABOO_CHIEF_UOPH:
			case KABOO_CHIEF_KRACHA:
			case KABOO_CHIEF_BATOH:
			case KABOO_CHIEF_TANUKIA:
				if ((st.getInt("cond") == 1) && st.hasItems(npc.getId() - 3223)) // npcId - 25223 = itemId to verify.
				{
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(KABOO_CHIEF_TORC_1, 1);
				}
				break;
			
			case KABOO_CHIEF_TUREL:
			case KABOO_CHIEF_ROKO:
				if ((st.getInt("cond") == 3) && st.hasItems(npc.getId() - 3224)) // npcId - 25224 = itemId to verify.
				{
					st.set("cond", "4");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(KABOO_CHIEF_TORC_2, 1);
				}
				break;
			
			case KABOO_CHIEF_KAMUT:
			case KABOO_CHIEF_MURTIKA:
				if ((st.getInt("cond") == 3) && st.hasItems(npc.getId() - 3225)) // npcId - 25225 = itemId to verify.
				{
					st.set("cond", "4");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(KABOO_CHIEF_TORC_2, 1);
				}
				break;
		}
		
		return null;
	}
	
}
