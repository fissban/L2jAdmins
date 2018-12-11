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
public class Q406_PathToAnElvenKnight extends Script
{
	// Items
	private static final int SORIUS_LETTER = 1202;
	private static final int KLUTO_BOX = 1203;
	private static final int ELVEN_KNIGHT_BROOCH = 1204;
	private static final int TOPAZ_PIECE = 1205;
	private static final int EMERALD_PIECE = 1206;
	private static final int KLUTO_MEMO = 1276;
	
	// NPCs
	private static final int SORIUS = 7327;
	private static final int KLUTO = 7317;
	
	public Q406_PathToAnElvenKnight()
	{
		super(406, "Path to an Elven Knight");
		
		registerItems(SORIUS_LETTER, KLUTO_BOX, TOPAZ_PIECE, EMERALD_PIECE, KLUTO_MEMO);
		
		addStartNpc(SORIUS);
		addTalkId(SORIUS, KLUTO);
		
		addKillId(35, 42, 45, 51, 54, 60, 782);
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
		
		if (event.equalsIgnoreCase("7327-05.htm"))
		{
			if (player.getClassId() != ClassId.ELF_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.ELF_KNIGHT ? "7327-02a.htm" : "7327-02.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7327-03.htm";
			}
			else if (st.hasItems(ELVEN_KNIGHT_BROOCH))
			{
				htmltext = "7327-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7327-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7317-02.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(SORIUS_LETTER, 1);
			st.giveItems(KLUTO_MEMO, 1);
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
				htmltext = "7327-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case SORIUS:
						if (cond == 1)
						{
							htmltext = !st.hasItems(TOPAZ_PIECE) ? "7327-07.htm" : "7327-08.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7327-09.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(SORIUS_LETTER, 1);
						}
						else if ((cond > 2) && (cond < 6))
						{
							htmltext = "7327-11.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7327-10.htm";
							st.takeItems(KLUTO_BOX, 1);
							st.takeItems(KLUTO_MEMO, 1);
							st.giveItems(ELVEN_KNIGHT_BROOCH, 1);
							st.rewardExpAndSp(3200, 2280);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case KLUTO:
						if (cond == 3)
						{
							htmltext = "7317-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = !st.hasItems(EMERALD_PIECE) ? "7317-03.htm" : "7317-04.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7317-05.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(EMERALD_PIECE, -1);
							st.takeItems(TOPAZ_PIECE, -1);
							st.giveItems(KLUTO_BOX, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7317-06.htm";
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
			case 35:
			case 42:
			case 45:
			case 51:
			case 54:
			case 60:
				if ((st.getInt("cond") == 1) && st.dropItems(TOPAZ_PIECE, 1, 20, 700000))
				{
					st.set("cond", "2");
				}
				break;
			
			case 782:
				if ((st.getInt("cond") == 4) && st.dropItems(EMERALD_PIECE, 1, 20, 500000))
				{
					st.set("cond", "5");
				}
				break;
		}
		
		return null;
	}
	
}
