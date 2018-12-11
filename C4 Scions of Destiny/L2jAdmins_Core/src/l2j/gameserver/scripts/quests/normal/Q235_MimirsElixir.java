package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.MagicSkillUse;
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
public class Q235_MimirsElixir extends Script
{
	// ITEMs
	private static final int STAR_OF_DESTINY = 5011;
	private static final int PURE_SILVER = 6320;
	private static final int TRUE_GOLD = 6321;
	private static final int SAGE_STONE = 6322;
	private static final int BLOOD_FIRE = 6318;
	private static final int MIMIR_ELIXIR = 6319;
	private static final int MAGISTER_MIXING_STONE = 5905;
	// REWARDs
	private static final int SCROLL_ENCHANT_WEAPON_A = 729;
	// NPCs
	private static final int JOAN = 7718;
	private static final int LADD = 7721;
	private static final int MIXING_URN = 8149;
	// MOBs
	private static final int CHIMERA_PIECE = 965;
	private static final int BLODY_GUARDIAN = 1090;
	
	public Q235_MimirsElixir()
	{
		super(235, "Mimir's Elixir");
		
		registerItems(PURE_SILVER, TRUE_GOLD, SAGE_STONE, BLOOD_FIRE, MAGISTER_MIXING_STONE, MIMIR_ELIXIR);
		
		addStartNpc(LADD);
		addTalkId(LADD, JOAN, MIXING_URN);
		
		addKillId(CHIMERA_PIECE, BLODY_GUARDIAN);
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
		
		if (event.equalsIgnoreCase("7721-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7721-12.htm") && st.hasItems(TRUE_GOLD))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(MAGISTER_MIXING_STONE, 1);
		}
		else if (event.equalsIgnoreCase("7721-16.htm") && st.hasItems(MIMIR_ELIXIR))
		{
			player.broadcastPacket(new MagicSkillUse(player, player, 4339, 1, 1, 1));
			
			st.takeItems(MAGISTER_MIXING_STONE, -1);
			st.takeItems(MIMIR_ELIXIR, -1);
			st.takeItems(STAR_OF_DESTINY, -1);
			st.giveItems(SCROLL_ENCHANT_WEAPON_A, 1);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false);
		}
		else if (event.equalsIgnoreCase("7718-03.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8149-02.htm"))
		{
			if (!st.hasItems(MAGISTER_MIXING_STONE))
			{
				htmltext = "8149-havent.htm";
			}
		}
		else if (event.equalsIgnoreCase("8149-03.htm"))
		{
			if (!st.hasItems(MAGISTER_MIXING_STONE, PURE_SILVER))
			{
				htmltext = "8149-havent.htm";
			}
		}
		else if (event.equalsIgnoreCase("8149-05.htm"))
		{
			if (!st.hasItems(MAGISTER_MIXING_STONE, PURE_SILVER, TRUE_GOLD))
			{
				htmltext = "8149-havent.htm";
			}
		}
		else if (event.equalsIgnoreCase("8149-07.htm"))
		{
			if (!st.hasItems(MAGISTER_MIXING_STONE, PURE_SILVER, TRUE_GOLD, BLOOD_FIRE))
			{
				htmltext = "8149-havent.htm";
			}
		}
		else if (event.equalsIgnoreCase("8149-success.htm"))
		{
			if (st.hasItems(MAGISTER_MIXING_STONE, PURE_SILVER, TRUE_GOLD, BLOOD_FIRE))
			{
				st.set("cond", "8");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(PURE_SILVER, -1);
				st.takeItems(TRUE_GOLD, -1);
				st.takeItems(BLOOD_FIRE, -1);
				st.giveItems(MIMIR_ELIXIR, 1);
			}
			else
			{
				htmltext = "8149-havent.htm";
			}
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
				if (player.getLevel() < 75)
				{
					htmltext = "7721-01b.htm";
				}
				else if (!st.hasItems(STAR_OF_DESTINY))
				{
					htmltext = "7721-01a.htm";
				}
				else
				{
					htmltext = "7721-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case LADD:
						if (cond == 1)
						{
							if (st.hasItems(PURE_SILVER))
							{
								htmltext = "7721-08.htm";
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								htmltext = "7721-07.htm";
							}
						}
						else if (cond < 5)
						{
							htmltext = "7721-10.htm";
						}
						else if ((cond == 5) && st.hasItems(TRUE_GOLD))
						{
							htmltext = "7721-11.htm";
						}
						else if ((cond == 6) || (cond == 7))
						{
							htmltext = "7721-13.htm";
						}
						else if ((cond == 8) && st.hasItems(MIMIR_ELIXIR))
						{
							htmltext = "7721-14.htm";
						}
						break;
					
					case JOAN:
						if (cond == 2)
						{
							htmltext = "7718-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7718-04.htm";
						}
						else if ((cond == 4) && st.hasItems(SAGE_STONE))
						{
							htmltext = "7718-05.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SAGE_STONE, -1);
							st.giveItems(TRUE_GOLD, 1);
						}
						else if (cond > 4)
						{
							htmltext = "7718-06.htm";
						}
						break;
					
					// The urn gives the same first htm. Bypasses' events will do all the job.
					case MIXING_URN:
						htmltext = "8149-01.htm";
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
		
		switch (npc.getId())
		{
			case CHIMERA_PIECE:
				if ((st.getInt("cond") == 3) && st.dropItems(SAGE_STONE, 1, 1, 200000))
				{
					st.set("cond", "4");
				}
				break;
			
			case BLODY_GUARDIAN:
				if ((st.getInt("cond") == 6) && st.dropItems(BLOOD_FIRE, 1, 1, 200000))
				{
					st.set("cond", "7");
				}
				break;
		}
		
		return null;
	}
	
}
