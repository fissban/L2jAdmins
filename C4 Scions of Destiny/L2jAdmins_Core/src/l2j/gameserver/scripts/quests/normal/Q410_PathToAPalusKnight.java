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
public class Q410_PathToAPalusKnight extends Script
{
	// Items
	private static final int PALUS_TALISMAN = 1237;
	private static final int LYCANTHROPE_SKULL = 1238;
	private static final int VIRGIL_LETTER = 1239;
	private static final int MORTE_TALISMAN = 1240;
	private static final int PREDATOR_CARAPACE = 1241;
	private static final int ARACHNID_TRACKER_SILK = 1242;
	private static final int COFFIN_OF_ETERNAL_REST = 1243;
	private static final int GAZE_OF_ABYSS = 1244;
	
	// NPCs
	private static final int KALINTA = 7422;
	private static final int VIRGIL = 7329;
	
	// Monsters
	private static final int POISON_SPIDER = 38;
	private static final int ARACHNID_TRACKER = 43;
	private static final int LYCANTHROPE = 49;
	
	public Q410_PathToAPalusKnight()
	{
		super(410, "Path to a Palus Knight");
		
		registerItems(PALUS_TALISMAN, LYCANTHROPE_SKULL, VIRGIL_LETTER, MORTE_TALISMAN, PREDATOR_CARAPACE, ARACHNID_TRACKER_SILK, COFFIN_OF_ETERNAL_REST);
		
		addStartNpc(VIRGIL);
		addTalkId(VIRGIL, KALINTA);
		
		addKillId(POISON_SPIDER, ARACHNID_TRACKER, LYCANTHROPE);
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
		
		if (event.equalsIgnoreCase("7329-05.htm"))
		{
			if (player.getClassId() != ClassId.DARK_ELF_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.PALUS_KNIGHT ? "7329-02a.htm" : "7329-03.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7329-02.htm";
			}
			else if (st.hasItems(GAZE_OF_ABYSS))
			{
				htmltext = "7329-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7329-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(PALUS_TALISMAN, 1);
		}
		else if (event.equalsIgnoreCase("7329-10.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(LYCANTHROPE_SKULL, -1);
			st.takeItems(PALUS_TALISMAN, 1);
			st.giveItems(VIRGIL_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7422-02.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(VIRGIL_LETTER, 1);
			st.giveItems(MORTE_TALISMAN, 1);
		}
		else if (event.equalsIgnoreCase("7422-06.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ARACHNID_TRACKER_SILK, -1);
			st.takeItems(MORTE_TALISMAN, 1);
			st.takeItems(PREDATOR_CARAPACE, -1);
			st.giveItems(COFFIN_OF_ETERNAL_REST, 1);
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
				htmltext = "7329-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case VIRGIL:
						if (cond == 1)
						{
							htmltext = !st.hasItems(LYCANTHROPE_SKULL) ? "7329-07.htm" : "7329-08.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7329-09.htm";
						}
						else if ((cond > 2) && (cond < 6))
						{
							htmltext = "7329-12.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7329-11.htm";
							st.takeItems(COFFIN_OF_ETERNAL_REST, 1);
							st.giveItems(GAZE_OF_ABYSS, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case KALINTA:
						if (cond == 3)
						{
							htmltext = "7422-01.htm";
						}
						else if (cond == 4)
						{
							if (!st.hasItems(ARACHNID_TRACKER_SILK) || !st.hasItems(PREDATOR_CARAPACE))
							{
								htmltext = "7422-03.htm";
							}
							else
							{
								htmltext = "7422-04.htm";
							}
						}
						else if (cond == 5)
						{
							htmltext = "7422-05.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7422-06.htm";
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
			case LYCANTHROPE:
				if ((st.getInt("cond") == 1) && st.dropItemsAlways(LYCANTHROPE_SKULL, 1, 13))
				{
					st.set("cond", "2");
				}
				break;
			
			case ARACHNID_TRACKER:
				if ((st.getInt("cond") == 4) && st.dropItemsAlways(ARACHNID_TRACKER_SILK, 1, 5) && st.hasItems(PREDATOR_CARAPACE))
				{
					st.set("cond", "5");
				}
				break;
			
			case POISON_SPIDER:
				if ((st.getInt("cond") == 4) && st.dropItemsAlways(PREDATOR_CARAPACE, 1, 1) && (st.getItemsCount(ARACHNID_TRACKER_SILK) == 5))
				{
					st.set("cond", "5");
				}
				break;
		}
		
		return null;
	}
	
}
