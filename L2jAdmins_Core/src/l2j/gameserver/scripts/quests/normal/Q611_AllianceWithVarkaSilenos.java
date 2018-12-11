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
 * @author        CaFi This quest supports both Q611 && Q612 onKill sections.
 * @originalQuest aCis
 */
public class Q611_AllianceWithVarkaSilenos extends Script
{
	private static final String Q612 = "Q612_WarWithKetraOrcs";
	
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(1324, 500000);
		CHANCES.put(1325, 500000);
		CHANCES.put(1327, 509000);
		CHANCES.put(1328, 521000);
		CHANCES.put(1329, 519000);
		CHANCES.put(1331, 500000);
		CHANCES.put(1332, 500000);
		CHANCES.put(1334, 509000);
		CHANCES.put(1335, 518000);
		CHANCES.put(1336, 518000);
		CHANCES.put(1338, 527000);
		CHANCES.put(1339, 500000);
		CHANCES.put(1340, 500000);
		CHANCES.put(1342, 508000);
		CHANCES.put(1343, 628000);
		CHANCES.put(1344, 604000);
		CHANCES.put(1345, 627000);
		CHANCES.put(1346, 604000);
		CHANCES.put(1347, 649000);
		CHANCES.put(1348, 626000);
		CHANCES.put(1349, 626000);
	}
	
	private static final Map<Integer, Integer> CHANCES_MOLAR = new HashMap<>();
	{
		CHANCES_MOLAR.put(1324, 500000);
		CHANCES_MOLAR.put(1327, 510000);
		CHANCES_MOLAR.put(1328, 522000);
		CHANCES_MOLAR.put(1329, 519000);
		CHANCES_MOLAR.put(1331, 529000);
		CHANCES_MOLAR.put(1332, 529000);
		CHANCES_MOLAR.put(1334, 539000);
		CHANCES_MOLAR.put(1336, 548000);
		CHANCES_MOLAR.put(1338, 558000);
		CHANCES_MOLAR.put(1339, 568000);
		CHANCES_MOLAR.put(1340, 568000);
		CHANCES_MOLAR.put(1342, 578000);
		CHANCES_MOLAR.put(1343, 664000);
		CHANCES_MOLAR.put(1345, 713000);
		CHANCES_MOLAR.put(1347, 738000);
	}
	
	// Quest Items
	private static final int KETRA_BADGE_SOLDIER = 7226;
	private static final int KETRA_BADGE_OFFICER = 7227;
	private static final int KETRA_BADGE_CAPTAIN = 7228;
	
	private static final int VARKA_ALLIANCE_1 = 7221;
	private static final int VARKA_ALLIANCE_2 = 7222;
	private static final int VARKA_ALLIANCE_3 = 7223;
	private static final int VARKA_ALLIANCE_4 = 7224;
	private static final int VARKA_ALLIANCE_5 = 7225;
	
	private static final int VALOR_FEATHER = 7229;
	private static final int WISDOM_FEATHER = 7230;
	
	private static final int MOLAR_OF_KETRA_ORC = 7234;
	
	public Q611_AllianceWithVarkaSilenos()
	{
		super(611, "Alliance with Varka Silenos");
		
		registerItems(KETRA_BADGE_SOLDIER, KETRA_BADGE_OFFICER, KETRA_BADGE_CAPTAIN);
		
		addStartNpc(8378); // Naran Ashanuk
		addTalkId(8378);
		
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
		
		if (event.equalsIgnoreCase("8378-03a.htm"))
		{
			if (player.isAlliedWithKetra())
			{
				htmltext = "8378-02a.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				for (int i = VARKA_ALLIANCE_1; i <= VARKA_ALLIANCE_5; i++)
				{
					if (st.hasItems(i))
					{
						st.set("cond", String.valueOf(i - 7219));
						player.setAllianceWithVarkaKetra(7220 - i);
						return "8378-0" + (i - 7217) + ".htm";
					}
				}
				st.set("cond", "1");
			}
		}
		// Stage 1
		else if (event.equalsIgnoreCase("8378-10-1.htm"))
		{
			if (st.getItemsCount(KETRA_BADGE_SOLDIER) >= 100)
			{
				st.set("cond", "2");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(KETRA_BADGE_SOLDIER, -1);
				st.giveItems(VARKA_ALLIANCE_1, 1);
				player.setAllianceWithVarkaKetra(-1);
			}
			else
			{
				htmltext = "8378-03b.htm";
			}
		}
		// Stage 2
		else if (event.equalsIgnoreCase("8378-10-2.htm"))
		{
			if ((st.getItemsCount(KETRA_BADGE_SOLDIER) >= 200) && (st.getItemsCount(KETRA_BADGE_OFFICER) >= 100))
			{
				st.set("cond", "3");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(KETRA_BADGE_SOLDIER, -1);
				st.takeItems(KETRA_BADGE_OFFICER, -1);
				st.takeItems(VARKA_ALLIANCE_1, -1);
				st.giveItems(VARKA_ALLIANCE_2, 1);
				player.setAllianceWithVarkaKetra(-2);
			}
			else
			{
				htmltext = "8378-12.htm";
			}
		}
		// Stage 3
		else if (event.equalsIgnoreCase("8378-10-3.htm"))
		{
			if ((st.getItemsCount(KETRA_BADGE_SOLDIER) >= 300) && (st.getItemsCount(KETRA_BADGE_OFFICER) >= 200) && (st.getItemsCount(KETRA_BADGE_CAPTAIN) >= 100))
			{
				st.set("cond", "4");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(KETRA_BADGE_SOLDIER, -1);
				st.takeItems(KETRA_BADGE_OFFICER, -1);
				st.takeItems(KETRA_BADGE_CAPTAIN, -1);
				st.takeItems(VARKA_ALLIANCE_2, -1);
				st.giveItems(VARKA_ALLIANCE_3, 1);
				player.setAllianceWithVarkaKetra(-3);
			}
			else
			{
				htmltext = "8378-15.htm";
			}
		}
		// Stage 4
		else if (event.equalsIgnoreCase("8378-10-4.htm"))
		{
			if ((st.getItemsCount(KETRA_BADGE_SOLDIER) >= 300) && (st.getItemsCount(KETRA_BADGE_OFFICER) >= 300) && (st.getItemsCount(KETRA_BADGE_CAPTAIN) >= 200) && (st.getItemsCount(VALOR_FEATHER) >= 1))
			{
				st.set("cond", "5");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(KETRA_BADGE_SOLDIER, -1);
				st.takeItems(KETRA_BADGE_OFFICER, -1);
				st.takeItems(KETRA_BADGE_CAPTAIN, -1);
				st.takeItems(VALOR_FEATHER, -1);
				st.takeItems(VARKA_ALLIANCE_3, -1);
				st.giveItems(VARKA_ALLIANCE_4, 1);
				player.setAllianceWithVarkaKetra(-4);
			}
			else
			{
				htmltext = "8378-21.htm";
			}
		}
		// Leave quest
		else if (event.equalsIgnoreCase("8378-20.htm"))
		{
			st.takeItems(VARKA_ALLIANCE_1, -1);
			st.takeItems(VARKA_ALLIANCE_2, -1);
			st.takeItems(VARKA_ALLIANCE_3, -1);
			st.takeItems(VARKA_ALLIANCE_4, -1);
			st.takeItems(VARKA_ALLIANCE_5, -1);
			st.takeItems(VALOR_FEATHER, -1);
			st.takeItems(WISDOM_FEATHER, -1);
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
					htmltext = "8378-01.htm";
				}
				else
				{
					htmltext = "8378-02b.htm";
					st.exitQuest(true);
					player.setAllianceWithVarkaKetra(0);
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					if (st.getItemsCount(KETRA_BADGE_SOLDIER) < 100)
					{
						htmltext = "8378-03b.htm";
					}
					else
					{
						htmltext = "8378-09.htm";
					}
				}
				else if (cond == 2)
				{
					if ((st.getItemsCount(KETRA_BADGE_SOLDIER) < 200) || (st.getItemsCount(KETRA_BADGE_OFFICER) < 100))
					{
						htmltext = "8378-12.htm";
					}
					else
					{
						htmltext = "8378-13.htm";
					}
				}
				else if (cond == 3)
				{
					if ((st.getItemsCount(KETRA_BADGE_SOLDIER) < 300) || (st.getItemsCount(KETRA_BADGE_OFFICER) < 200) || (st.getItemsCount(KETRA_BADGE_CAPTAIN) < 100))
					{
						htmltext = "8378-15.htm";
					}
					else
					{
						htmltext = "8378-16.htm";
					}
				}
				else if (cond == 4)
				{
					if ((st.getItemsCount(KETRA_BADGE_SOLDIER) < 300) || (st.getItemsCount(KETRA_BADGE_OFFICER) < 300) || (st.getItemsCount(KETRA_BADGE_CAPTAIN) < 200) || !st.hasItems(VALOR_FEATHER))
					{
						htmltext = "8378-21.htm";
					}
					else
					{
						htmltext = "8378-22.htm";
					}
				}
				else if (cond == 5)
				{
					if ((st.getItemsCount(KETRA_BADGE_SOLDIER) < 400) || (st.getItemsCount(KETRA_BADGE_OFFICER) < 400) || (st.getItemsCount(KETRA_BADGE_CAPTAIN) < 200) || !st.hasItems(WISDOM_FEATHER))
					{
						htmltext = "8378-17.htm";
					}
					else
					{
						htmltext = "8378-10-5.htm";
						st.set("cond", "6");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.takeItems(KETRA_BADGE_SOLDIER, 400);
						st.takeItems(KETRA_BADGE_OFFICER, 400);
						st.takeItems(KETRA_BADGE_CAPTAIN, 200);
						st.takeItems(WISDOM_FEATHER, -1);
						st.takeItems(VARKA_ALLIANCE_4, -1);
						st.giveItems(VARKA_ALLIANCE_5, 1);
						player.setAllianceWithVarkaKetra(-5);
					}
				}
				else if (cond == 6)
				{
					htmltext = "8378-08.htm";
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
		
		// Support for Q612.
		ScriptState st = partyMember.getScriptState(Q612);
		if ((st != null) && Rnd.nextBoolean() && CHANCES_MOLAR.containsKey(npcId))
		{
			st.dropItems(MOLAR_OF_KETRA_ORC, 1, 0, CHANCES_MOLAR.get(npcId));
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
			case 1324:
			case 1325:
			case 1327:
			case 1328:
			case 1329:
				if (cond == 1)
				{
					st.dropItems(KETRA_BADGE_SOLDIER, 1, 100, CHANCES.get(npcId));
				}
				else if (cond == 2)
				{
					st.dropItems(KETRA_BADGE_SOLDIER, 1, 200, CHANCES.get(npcId));
				}
				else if ((cond == 3) || (cond == 4))
				{
					st.dropItems(KETRA_BADGE_SOLDIER, 1, 300, CHANCES.get(npcId));
				}
				else if (cond == 5)
				{
					st.dropItems(KETRA_BADGE_SOLDIER, 1, 400, CHANCES.get(npcId));
				}
				break;
			
			case 1331:
			case 1332:
			case 1334:
			case 1335:
			case 1336:
			case 1338:
			case 1343:
			case 1344:
				if (cond == 2)
				{
					st.dropItems(KETRA_BADGE_OFFICER, 1, 100, CHANCES.get(npcId));
				}
				else if (cond == 3)
				{
					st.dropItems(KETRA_BADGE_OFFICER, 1, 200, CHANCES.get(npcId));
				}
				else if (cond == 4)
				{
					st.dropItems(KETRA_BADGE_OFFICER, 1, 300, CHANCES.get(npcId));
				}
				else if (cond == 5)
				{
					st.dropItems(KETRA_BADGE_OFFICER, 1, 400, CHANCES.get(npcId));
				}
				break;
			
			case 1339:
			case 1340:
			case 1342:
			case 1345:
			case 1346:
			case 1347:
			case 1348:
			case 1349:
				if (cond == 3)
				{
					st.dropItems(KETRA_BADGE_CAPTAIN, 1, 100, CHANCES.get(npcId));
				}
				else if ((cond == 4) || (cond == 5))
				{
					st.dropItems(KETRA_BADGE_CAPTAIN, 1, 200, CHANCES.get(npcId));
				}
				break;
		}
		
		return null;
	}
	
}
