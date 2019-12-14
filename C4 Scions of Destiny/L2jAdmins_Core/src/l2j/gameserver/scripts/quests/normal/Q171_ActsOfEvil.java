package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q171_ActsOfEvil extends Script
{
	// ITEMs
	private static final int BLADE_MOLD = 4239;
	private static final int TYRA_BILL = 4240;
	private static final int RANGER_REPORT_1 = 4241;
	private static final int RANGER_REPORT_2 = 4242;
	private static final int RANGER_REPORT_3 = 4243;
	private static final int RANGER_REPORT_4 = 4244;
	private static final int WEAPON_TRADE_CONTRACT = 4245;
	private static final int ATTACK_DIRECTIVES = 4246;
	private static final int CERTIFICATE = 4247;
	private static final int CARGO_BOX = 4248;
	private static final int OL_MAHUM_HEAD = 4249;
	// NPCs
	private static final int ALVAH = 7381;
	private static final int ARODIN = 7207;
	private static final int TYRA = 7420;
	private static final int ROLENTO = 7437;
	private static final int NETI = 7425;
	private static final int BURAI = 7617;
	// MOBs
	private static final int TUMRAN_BUGBEAR = 62;
	private static final int TUMRAN_BUGBEAR_WARRIOR = 64;
	private static final int OL_MAHUM_CAPTAIN = 66;
	private static final int OL_MAHUM_GENERAL = 438;
	private static final int TUREK_ORC_1 = 496;
	private static final int TUREK_ORC_2 = 497;
	private static final int TUREK_ORC_3 = 498;
	private static final int TUREK_ORC_4 = 499;
	private static final int OL_MAHUM_SUPPORT_TROOP = 5190;
	
	// Turek Orcs drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	{
		CHANCES.put(TUREK_ORC_1, 530000);
		CHANCES.put(TUREK_ORC_2, 550000);
		CHANCES.put(TUREK_ORC_3, 510000);
		CHANCES.put(TUREK_ORC_4, 500000);
	}
	
	public Q171_ActsOfEvil()
	{
		super(171, "Acts of Evil");
		
		registerItems(BLADE_MOLD, TYRA_BILL, RANGER_REPORT_1, RANGER_REPORT_2, RANGER_REPORT_3, RANGER_REPORT_4, WEAPON_TRADE_CONTRACT, ATTACK_DIRECTIVES, CERTIFICATE, CARGO_BOX, OL_MAHUM_HEAD);
		
		addStartNpc(ALVAH);
		addTalkId(ALVAH, ARODIN, TYRA, ROLENTO, NETI, BURAI);
		
		addKillId(TUREK_ORC_1, TUREK_ORC_2, TUREK_ORC_3, TUREK_ORC_4, TUMRAN_BUGBEAR, TUMRAN_BUGBEAR_WARRIOR, OL_MAHUM_CAPTAIN, OL_MAHUM_GENERAL, OL_MAHUM_SUPPORT_TROOP);
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
		
		if (event.equalsIgnoreCase("7381-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7207-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7381-04.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7381-07.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(WEAPON_TRADE_CONTRACT, 1);
		}
		else if (event.equalsIgnoreCase("7437-03.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(CARGO_BOX, 1);
			st.giveItems(CERTIFICATE, 1);
		}
		else if (event.equalsIgnoreCase("7617-04.htm"))
		{
			st.set("cond", "10");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ATTACK_DIRECTIVES, 1);
			st.takeItems(CARGO_BOX, 1);
			st.takeItems(CERTIFICATE, 1);
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
				htmltext = player.getLevel() < 27 ? "7381-01a.htm" : "7381-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case ALVAH:
						if (cond < 4)
						{
							htmltext = "7381-02a.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7381-03.htm";
						}
						else if (cond == 5)
						{
							if (st.hasItems(RANGER_REPORT_1, RANGER_REPORT_2, RANGER_REPORT_3, RANGER_REPORT_4))
							{
								htmltext = "7381-05.htm";
								st.set("cond", "6");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(RANGER_REPORT_1, 1);
								st.takeItems(RANGER_REPORT_2, 1);
								st.takeItems(RANGER_REPORT_3, 1);
								st.takeItems(RANGER_REPORT_4, 1);
							}
							else
							{
								htmltext = "7381-04a.htm";
							}
						}
						else if (cond == 6)
						{
							if (st.hasItems(WEAPON_TRADE_CONTRACT, ATTACK_DIRECTIVES))
							{
								htmltext = "7381-06.htm";
							}
							else
							{
								htmltext = "7381-05a.htm";
							}
						}
						else if ((cond > 6) && (cond < 11))
						{
							htmltext = "7381-07a.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7381-08.htm";
							st.rewardItems(Inventory.ADENA_ID, 90000);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case ARODIN:
						if (cond == 1)
						{
							htmltext = "7207-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7207-01a.htm";
						}
						else if (cond == 3)
						{
							if (st.hasItems(TYRA_BILL))
							{
								htmltext = "7207-03.htm";
								st.set("cond", "4");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(TYRA_BILL, 1);
							}
							else
							{
								htmltext = "7207-01a.htm";
							}
						}
						else if (cond > 3)
						{
							htmltext = "7207-03a.htm";
						}
						break;
					
					case TYRA:
						if (cond == 2)
						{
							if (st.getItemsCount(BLADE_MOLD) >= 20)
							{
								htmltext = "7420-01.htm";
								st.set("cond", "3");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(BLADE_MOLD, -1);
								st.giveItems(TYRA_BILL, 1);
							}
							else
							{
								htmltext = "7420-01b.htm";
							}
						}
						else if (cond == 3)
						{
							htmltext = "7420-01a.htm";
						}
						else if (cond > 3)
						{
							htmltext = "7420-02.htm";
						}
						break;
					
					case NETI:
						if (cond == 7)
						{
							htmltext = "7425-01.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else if (cond > 7)
						{
							htmltext = "7425-02.htm";
						}
						break;
					
					case ROLENTO:
						if (cond == 8)
						{
							htmltext = "7437-01.htm";
						}
						else if (cond > 8)
						{
							htmltext = "7437-03a.htm";
						}
						break;
					
					case BURAI:
						if ((cond == 9) && st.hasItems(CERTIFICATE, CARGO_BOX, ATTACK_DIRECTIVES))
						{
							htmltext = "7617-01.htm";
						}
						else if (cond == 10)
						{
							if (st.getItemsCount(OL_MAHUM_HEAD) >= 30)
							{
								htmltext = "7617-05.htm";
								st.set("cond", "11");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(OL_MAHUM_HEAD, -1);
								st.rewardItems(Inventory.ADENA_ID, 8000);
							}
							else
							{
								htmltext = "7617-04a.htm";
							}
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
		
		final int npcId = npc.getId();
		
		switch (npcId)
		{
			case TUREK_ORC_1:
			case TUREK_ORC_2:
			case TUREK_ORC_3:
			case TUREK_ORC_4:
				if ((st.getInt("cond") == 2) && !st.dropItems(BLADE_MOLD, 1, 20, CHANCES.get(npcId)))
				{
					final int count = st.getItemsCount(BLADE_MOLD);
					if ((count == 5) || ((count >= 10) && (Rnd.get(100) < 25)))
					{
						addSpawn(OL_MAHUM_SUPPORT_TROOP, player, false, 0);
					}
				}
				break;
			
			case TUMRAN_BUGBEAR:
			case TUMRAN_BUGBEAR_WARRIOR:
				if (st.getInt("cond") == 5)
				{
					if (!st.hasItems(RANGER_REPORT_1))
					{
						st.giveItems(RANGER_REPORT_1, 1);
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
					else if (Rnd.get(100) < 20)
					{
						if (!st.hasItems(RANGER_REPORT_2))
						{
							st.giveItems(RANGER_REPORT_2, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
						else if (!st.hasItems(RANGER_REPORT_3))
						{
							st.giveItems(RANGER_REPORT_3, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
						else if (!st.hasItems(RANGER_REPORT_4))
						{
							st.giveItems(RANGER_REPORT_4, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
				}
				break;
			
			case OL_MAHUM_GENERAL:
				if ((st.getInt("cond") == 6) && (Rnd.get(100) < 10) && !st.hasItems(WEAPON_TRADE_CONTRACT, ATTACK_DIRECTIVES))
				{
					st.playSound(PlaySoundType.QUEST_ITEMGET);
					st.giveItems(WEAPON_TRADE_CONTRACT, 1);
					st.giveItems(ATTACK_DIRECTIVES, 1);
				}
				break;
			
			case OL_MAHUM_CAPTAIN:
				if (st.getInt("cond") == 10)
				{
					st.dropItems(OL_MAHUM_HEAD, 1, 30, 500000);
				}
				break;
		}
		
		return null;
	}
	
}
