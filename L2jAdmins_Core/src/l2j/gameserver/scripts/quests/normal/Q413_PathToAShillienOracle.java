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
public class Q413_PathToAShillienOracle extends Script
{
	// Items
	private static final int SIDRA_LETTER = 1262;
	private static final int BLANK_SHEET = 1263;
	private static final int BLOODY_RUNE = 1264;
	private static final int GARMIEL_BOOK = 1265;
	private static final int PRAYER_OF_ADONIUS = 1266;
	private static final int PENITENT_MARK = 1267;
	private static final int ASHEN_BONES = 1268;
	private static final int ANDARIEL_BOOK = 1269;
	private static final int ORB_OF_ABYSS = 1270;
	
	// NPCs
	private static final int SIDRA = 7330;
	private static final int ADONIUS = 7375;
	private static final int TALBOT = 7377;
	
	public Q413_PathToAShillienOracle()
	{
		super(413, "Path to a Shillien Oracle");
		
		registerItems(SIDRA_LETTER, BLANK_SHEET, BLOODY_RUNE, GARMIEL_BOOK, PRAYER_OF_ADONIUS, PENITENT_MARK, ASHEN_BONES, ANDARIEL_BOOK);
		
		addStartNpc(SIDRA);
		addTalkId(SIDRA, ADONIUS, TALBOT);
		
		addKillId(776, 457, 458, 514, 515);
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
		
		if (event.equalsIgnoreCase("7330-05.htm"))
		{
			if (player.getClassId() != ClassId.DARK_ELF_MAGE)
			{
				htmltext = player.getClassId() == ClassId.SHILLIEN_ORACLE ? "7330-02a.htm" : "7330-03.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7330-02.htm";
			}
			else if (st.hasItems(ORB_OF_ABYSS))
			{
				htmltext = "7330-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7330-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(SIDRA_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7377-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SIDRA_LETTER, 1);
			st.giveItems(BLANK_SHEET, 5);
		}
		else if (event.equalsIgnoreCase("7375-04.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(PRAYER_OF_ADONIUS, 1);
			st.giveItems(PENITENT_MARK, 1);
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
				htmltext = "7330-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case SIDRA:
						if (cond == 1)
						{
							htmltext = "7330-07.htm";
						}
						else if ((cond > 1) && (cond < 4))
						{
							htmltext = "7330-08.htm";
						}
						else if ((cond > 3) && (cond < 7))
						{
							htmltext = "7330-09.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7330-10.htm";
							st.takeItems(ANDARIEL_BOOK, 1);
							st.takeItems(GARMIEL_BOOK, 1);
							st.giveItems(ORB_OF_ABYSS, 1);
							st.rewardExpAndSp(3200, 3120);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case TALBOT:
						if (cond == 1)
						{
							htmltext = "7377-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = st.hasItems(BLOODY_RUNE) ? "7377-04.htm" : "7377-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7377-05.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(BLOODY_RUNE, -1);
							st.giveItems(GARMIEL_BOOK, 1);
							st.giveItems(PRAYER_OF_ADONIUS, 1);
						}
						else if ((cond > 3) && (cond < 7))
						{
							htmltext = "7377-06.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7377-07.htm";
						}
						break;
					
					case ADONIUS:
						if (cond == 4)
						{
							htmltext = "7375-01.htm";
						}
						else if (cond == 5)
						{
							htmltext = st.hasItems(ASHEN_BONES) ? "7375-05.htm" : "7375-06.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7375-07.htm";
							st.set("cond", "7");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ASHEN_BONES, -1);
							st.takeItems(PENITENT_MARK, -1);
							st.giveItems(ANDARIEL_BOOK, 1);
						}
						else if (cond == 7)
						{
							htmltext = "7375-08.htm";
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
		
		if (npc.getId() == 776)
		{
			if (st.getInt("cond") == 2)
			{
				st.takeItems(BLANK_SHEET, 1);
				if (st.dropItemsAlways(BLOODY_RUNE, 1, 5))
				{
					st.set("cond", "3");
				}
			}
		}
		else if ((st.getInt("cond") == 5) && st.dropItemsAlways(ASHEN_BONES, 1, 10))
		{
			st.set("cond", "6");
		}
		
		return null;
	}
	
}
