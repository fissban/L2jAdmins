package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi This quest supports both Q605 && Q606 onKill sections.
 * @originalQuest aCis
 */
public class Q605_AllianceWithKetraOrcs extends Script
{
	private static final String Q606 = "Q606_WarWithVarkaSilenos";
	
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1350, 500000);
		CHANCES.put(1351, 500000);
		CHANCES.put(1353, 509000);
		CHANCES.put(1354, 521000);
		CHANCES.put(1355, 519000);
		CHANCES.put(1357, 500000);
		CHANCES.put(1358, 500000);
		CHANCES.put(1360, 509000);
		CHANCES.put(1361, 518000);
		CHANCES.put(1362, 518000);
		CHANCES.put(1364, 527000);
		CHANCES.put(1365, 500000);
		CHANCES.put(1366, 500000);
		CHANCES.put(1368, 508000);
		CHANCES.put(1369, 628000);
		CHANCES.put(1370, 604000);
		CHANCES.put(1371, 627000);
		CHANCES.put(1372, 604000);
		CHANCES.put(1373, 649000);
		CHANCES.put(1374, 626000);
		CHANCES.put(1375, 626000);
	}
	
	private static final Map<Integer, Integer> CHANCES_MANE = new HashMap<>();
	{
		CHANCES_MANE.put(1350, 500000);
		CHANCES_MANE.put(1353, 510000);
		CHANCES_MANE.put(1354, 522000);
		CHANCES_MANE.put(1355, 519000);
		CHANCES_MANE.put(1357, 529000);
		CHANCES_MANE.put(1358, 529000);
		CHANCES_MANE.put(1360, 539000);
		CHANCES_MANE.put(1362, 548000);
		CHANCES_MANE.put(1364, 558000);
		CHANCES_MANE.put(1365, 568000);
		CHANCES_MANE.put(1366, 568000);
		CHANCES_MANE.put(1368, 568000);
		CHANCES_MANE.put(1369, 664000);
		CHANCES_MANE.put(1371, 713000);
		CHANCES_MANE.put(1373, 738000);
	}
	
	// Quest Items
	private static final int VARKA_BADGE_SOLDIER = 7216;
	private static final int VARKA_BADGE_OFFICER = 7217;
	private static final int VARKA_BADGE_CAPTAIN = 7218;
	
	private static final int KETRA_ALLIANCE_1 = 7211;
	private static final int KETRA_ALLIANCE_2 = 7212;
	private static final int KETRA_ALLIANCE_3 = 7213;
	private static final int KETRA_ALLIANCE_4 = 7214;
	private static final int KETRA_ALLIANCE_5 = 7215;
	
	private static final int TOTEM_OF_VALOR = 7219;
	private static final int TOTEM_OF_WISDOM = 7220;
	
	private static final int VARKA_MANE = 7233;
	
	public Q605_AllianceWithKetraOrcs()
	{
		super(605, "Alliance with Ketra Orcs");
		
		registerItems(VARKA_BADGE_SOLDIER, VARKA_BADGE_OFFICER, VARKA_BADGE_CAPTAIN);
		
		addStartNpc(8371); // Wahkan
		addTalkId(8371);
		
		for (int mobs : CHANCES.keySet())
		{
			addKillId(mobs);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8371-03a.htm"))
		{
			if (player.isAlliedWithVarka())
			{
				htmltext = "8371-02a.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				for (int i = KETRA_ALLIANCE_1; i <= KETRA_ALLIANCE_5; i++)
				{
					if (st.hasItems(i))
					{
						st.set("cond", String.valueOf(i - 7209));
						player.setAllianceWithVarkaKetra(i - 7210);
						return "8371-0" + (i - 7207) + ".htm";
					}
				}
				st.set("cond", "1");
			}
		}
		// Stage 1
		else if (event.equalsIgnoreCase("8371-10-1.htm"))
		{
			if (st.getItemsCount(VARKA_BADGE_SOLDIER) >= 100)
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(VARKA_BADGE_SOLDIER, -1);
				st.giveItems(KETRA_ALLIANCE_1, 1);
				player.setAllianceWithVarkaKetra(1);
			}
			else
			{
				htmltext = "8371-03b.htm";
			}
		}
		// Stage 2
		else if (event.equalsIgnoreCase("8371-10-2.htm"))
		{
			if ((st.getItemsCount(VARKA_BADGE_SOLDIER) >= 200) && (st.getItemsCount(VARKA_BADGE_OFFICER) >= 100))
			{
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(VARKA_BADGE_SOLDIER, -1);
				st.takeItems(VARKA_BADGE_OFFICER, -1);
				st.takeItems(KETRA_ALLIANCE_1, -1);
				st.giveItems(KETRA_ALLIANCE_2, 1);
				player.setAllianceWithVarkaKetra(2);
			}
			else
			{
				htmltext = "8371-12.htm";
			}
		}
		// Stage 3
		else if (event.equalsIgnoreCase("8371-10-3.htm"))
		{
			if ((st.getItemsCount(VARKA_BADGE_SOLDIER) >= 300) && (st.getItemsCount(VARKA_BADGE_OFFICER) >= 200) && (st.getItemsCount(VARKA_BADGE_CAPTAIN) >= 100))
			{
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(VARKA_BADGE_SOLDIER, -1);
				st.takeItems(VARKA_BADGE_OFFICER, -1);
				st.takeItems(VARKA_BADGE_CAPTAIN, -1);
				st.takeItems(KETRA_ALLIANCE_2, -1);
				st.giveItems(KETRA_ALLIANCE_3, 1);
				player.setAllianceWithVarkaKetra(3);
			}
			else
			{
				htmltext = "8371-15.htm";
			}
		}
		// Stage 4
		else if (event.equalsIgnoreCase("8371-10-4.htm"))
		{
			if ((st.getItemsCount(VARKA_BADGE_SOLDIER) >= 300) && (st.getItemsCount(VARKA_BADGE_OFFICER) >= 300) && (st.getItemsCount(VARKA_BADGE_CAPTAIN) >= 200) && (st.getItemsCount(TOTEM_OF_VALOR) >= 1))
			{
				st.set("cond", "5");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(VARKA_BADGE_SOLDIER, -1);
				st.takeItems(VARKA_BADGE_OFFICER, -1);
				st.takeItems(VARKA_BADGE_CAPTAIN, -1);
				st.takeItems(TOTEM_OF_VALOR, -1);
				st.takeItems(KETRA_ALLIANCE_3, -1);
				st.giveItems(KETRA_ALLIANCE_4, 1);
				player.setAllianceWithVarkaKetra(4);
			}
			else
			{
				htmltext = "8371-21.htm";
			}
		}
		// Leave quest
		else if (event.equalsIgnoreCase("8371-20.htm"))
		{
			st.takeItems(KETRA_ALLIANCE_1, -1);
			st.takeItems(KETRA_ALLIANCE_2, -1);
			st.takeItems(KETRA_ALLIANCE_3, -1);
			st.takeItems(KETRA_ALLIANCE_4, -1);
			st.takeItems(KETRA_ALLIANCE_5, -1);
			st.takeItems(TOTEM_OF_VALOR, -1);
			st.takeItems(TOTEM_OF_WISDOM, -1);
			player.setAllianceWithVarkaKetra(0);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getLevel() >= 74)
				{
					htmltext = "8371-01.htm";
				}
				else
				{
					htmltext = "8371-02b.htm";
					st.exitQuest(true);
					player.setAllianceWithVarkaKetra(0);
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					if (st.getItemsCount(VARKA_BADGE_SOLDIER) < 100)
					{
						htmltext = "8371-03b.htm";
					}
					else
					{
						htmltext = "8371-09.htm";
					}
				}
				else if (cond == 2)
				{
					if ((st.getItemsCount(VARKA_BADGE_SOLDIER) < 200) || (st.getItemsCount(VARKA_BADGE_OFFICER) < 100))
					{
						htmltext = "8371-12.htm";
					}
					else
					{
						htmltext = "8371-13.htm";
					}
				}
				else if (cond == 3)
				{
					if ((st.getItemsCount(VARKA_BADGE_SOLDIER) < 300) || (st.getItemsCount(VARKA_BADGE_OFFICER) < 200) || (st.getItemsCount(VARKA_BADGE_CAPTAIN) < 100))
					{
						htmltext = "8371-15.htm";
					}
					else
					{
						htmltext = "8371-16.htm";
					}
				}
				else if (cond == 4)
				{
					if ((st.getItemsCount(VARKA_BADGE_SOLDIER) < 300) || (st.getItemsCount(VARKA_BADGE_OFFICER) < 300) || (st.getItemsCount(VARKA_BADGE_CAPTAIN) < 200) || !st.hasItems(TOTEM_OF_VALOR))
					{
						htmltext = "8371-21.htm";
					}
					else
					{
						htmltext = "8371-22.htm";
					}
				}
				else if (cond == 5)
				{
					if ((st.getItemsCount(VARKA_BADGE_SOLDIER) < 400) || (st.getItemsCount(VARKA_BADGE_OFFICER) < 400) || (st.getItemsCount(VARKA_BADGE_CAPTAIN) < 200) || !st.hasItems(TOTEM_OF_WISDOM))
					{
						htmltext = "8371-17.htm";
					}
					else
					{
						htmltext = "8371-10-5.htm";
						st.set("cond", "6");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(VARKA_BADGE_SOLDIER, 400);
						st.takeItems(VARKA_BADGE_OFFICER, 400);
						st.takeItems(VARKA_BADGE_CAPTAIN, 200);
						st.takeItems(TOTEM_OF_WISDOM, -1);
						st.takeItems(KETRA_ALLIANCE_4, -1);
						st.giveItems(KETRA_ALLIANCE_5, 1);
						player.setAllianceWithVarkaKetra(5);
					}
				}
				else if (cond == 6)
				{
					htmltext = "8371-08.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		
		// Support for Q606.
		ScriptState st = partyMember.getScriptState(Q606);
		if ((st != null) && Rnd.nextBoolean() && CHANCES_MANE.containsKey(npcId))
		{
			st.dropItems(VARKA_MANE, 1, 0, CHANCES_MANE.get(npcId));
			return null;
		}
		
		st = partyMember.getScriptState(getName());
		
		int cond = st.getInt("cond");
		if (cond == 6)
		{
			return null;
		}
		
		switch (npcId)
		{
			case 1350:
			case 1351:
			case 1353:
			case 1354:
			case 1355:
				if (cond == 1)
				{
					st.dropItems(VARKA_BADGE_SOLDIER, 1, 100, CHANCES.get(npcId));
				}
				else if (cond == 2)
				{
					st.dropItems(VARKA_BADGE_SOLDIER, 1, 200, CHANCES.get(npcId));
				}
				else if ((cond == 3) || (cond == 4))
				{
					st.dropItems(VARKA_BADGE_SOLDIER, 1, 300, CHANCES.get(npcId));
				}
				else if (cond == 5)
				{
					st.dropItems(VARKA_BADGE_SOLDIER, 1, 400, CHANCES.get(npcId));
				}
				break;
			
			case 1357:
			case 1358:
			case 1360:
			case 1361:
			case 1362:
			case 1364:
			case 1369:
			case 1370:
				if (cond == 2)
				{
					st.dropItems(VARKA_BADGE_OFFICER, 1, 100, CHANCES.get(npcId));
				}
				else if (cond == 3)
				{
					st.dropItems(VARKA_BADGE_OFFICER, 1, 200, CHANCES.get(npcId));
				}
				else if (cond == 4)
				{
					st.dropItems(VARKA_BADGE_OFFICER, 1, 300, CHANCES.get(npcId));
				}
				else if (cond == 5)
				{
					st.dropItems(VARKA_BADGE_OFFICER, 1, 400, CHANCES.get(npcId));
				}
				break;
			
			case 1365:
			case 1366:
			case 1368:
			case 1371:
			case 1372:
			case 1373:
			case 1374:
			case 1375:
				if (cond == 3)
				{
					st.dropItems(VARKA_BADGE_CAPTAIN, 1, 100, CHANCES.get(npcId));
				}
				else if ((cond == 4) || (cond == 5))
				{
					st.dropItems(VARKA_BADGE_CAPTAIN, 1, 200, CHANCES.get(npcId));
				}
				break;
		}
		
		return null;
	}
	
}
