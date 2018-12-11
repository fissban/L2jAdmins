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
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q416_PathToAnOrcShaman extends Script
{
	// QUEST ITEMs
	private static final int FIRE_CHARM = 1616;
	private static final int KASHA_BEAR_PELT = 1617;
	private static final int KASHA_BLADE_SPIDER_HUSK = 1618;
	private static final int FIRST_FIERY_EGG = 1619;
	private static final int HESTUI_MASK = 1620;
	private static final int SECOND_FIERY_EGG = 1621;
	private static final int TOTEM_SPIRIT_CLAW = 1622;
	private static final int TATARU_LETTER_RECOMMENDATION = 1623;
	private static final int FLAME_CHARM = 1624;
	private static final int GRIZZLY_BLOOD = 1625;
	private static final int BLOOD_CAULDRON = 1626;
	private static final int SPIRIT_NET = 1627;
	private static final int BOUND_DURKA_SPIRIT = 1628;
	private static final int DURKA_PARASITE = 1629;
	private static final int TOTEM_SPIRIT_BLOOD = 1630;
	
	// NPCs
	private static final int UMOS = 7502;
	private static final int TATARU_ZU_HESTUI = 7585;
	private static final int HESTUI_TOTEM_SPIRIT = 7592;
	private static final int DUDA_MARA_TOTEM_SPIRIT = 7593;
	
	// MONSTERs
	private static final int POISON_SPIDER = 38;
	private static final int ARACHNID_TRACKER = 43;
	private static final int GRIZZLY_BEAR = 335;
	private static final int SCARLET_SALAMANDER = 415;
	private static final int KASHA_BLADE_SPIDER = 478;
	private static final int KASHA_BEAR = 479;
	
	// QUEST MONSTERs
	private static final int DURKA_SPIRIT = 5056;
	
	// REWARD
	private static final int MASK_OF_MEDIUM = 1631;
	
	public Q416_PathToAnOrcShaman()
	{
		super(416, "Path To An Orc Shaman");
		registerItems(FIRE_CHARM, FIRST_FIERY_EGG, KASHA_BEAR_PELT, KASHA_BLADE_SPIDER_HUSK, HESTUI_MASK, SECOND_FIERY_EGG, TOTEM_SPIRIT_CLAW, TATARU_LETTER_RECOMMENDATION, FLAME_CHARM, GRIZZLY_BLOOD, BLOOD_CAULDRON, SPIRIT_NET, DURKA_PARASITE, BOUND_DURKA_SPIRIT, TOTEM_SPIRIT_BLOOD);
		addStartNpc(TATARU_ZU_HESTUI);
		addTalkId(TATARU_ZU_HESTUI, HESTUI_TOTEM_SPIRIT, UMOS, DUDA_MARA_TOTEM_SPIRIT);
		addKillId(SCARLET_SALAMANDER, KASHA_BEAR, KASHA_BLADE_SPIDER, GRIZZLY_BEAR, ARACHNID_TRACKER, POISON_SPIDER, DURKA_SPIRIT);
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
		
		// TATARU ZU HESTUI
		if (event.equalsIgnoreCase("7585-06.htm"))
		{
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(FIRE_CHARM, 1);
			st.setState(ScriptStateType.STARTED);
		}
		else if (event.equalsIgnoreCase("7585-11.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(TOTEM_SPIRIT_CLAW, 1);
			st.giveItems(TATARU_LETTER_RECOMMENDATION, 1);
		}
		// HESTUI TOTEM SPIRIT
		else if (event.equalsIgnoreCase("7592-03.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HESTUI_MASK, 1);
			st.takeItems(SECOND_FIERY_EGG, 1);
			st.giveItems(TOTEM_SPIRIT_CLAW, 1);
			
		}
		// DUDA-MARA TOTEM SPIRIT
		else if (event.equalsIgnoreCase("7593-03.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(BLOOD_CAULDRON, 1);
			st.giveItems(SPIRIT_NET, 1);
		}
		// SEER UMOS
		else if (event.equalsIgnoreCase("7502-07.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.takeItems(TOTEM_SPIRIT_BLOOD, 1);
			st.giveItems(MASK_OF_MEDIUM, 1);
			st.rewardExpAndSp(3200, 4230);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.exitQuest(false);
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
				htmltext = player.getLevel() < 18 ? "7585-03.htm" : "7585-01.htm";
				if (st.hasItems(MASK_OF_MEDIUM))
				{
					htmltext = "7585-04.htm";
				}
				else if (player.getClassId() == ClassId.SHAMAN)
				{
					htmltext = "7585-02a.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case TATARU_ZU_HESTUI:
						if (cond == 1)
						{
							htmltext = "7585-07.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7585-08.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(FIRE_CHARM, 1);
							st.takeItems(FIRST_FIERY_EGG, 1);
							st.takeItems(KASHA_BLADE_SPIDER_HUSK, 1);
							st.takeItems(KASHA_BEAR_PELT, 1);
							st.giveItems(HESTUI_MASK, 1);
							st.giveItems(SECOND_FIERY_EGG, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7585-09.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7585-10.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7585-12.htm";
						}
						break;
					case HESTUI_TOTEM_SPIRIT:
						if (cond == 3)
						{
							htmltext = "7592-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7592-04.htm";
						}
						break;
					case UMOS:
						if (cond == 5)
						{
							htmltext = "7502-01.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(TATARU_LETTER_RECOMMENDATION, 1);
							st.giveItems(FLAME_CHARM, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7502-02.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7502-03.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(FLAME_CHARM, 1);
							st.takeItems(GRIZZLY_BLOOD, 3);
							st.giveItems(BLOOD_CAULDRON, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7502-04.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7502-06.htm";
						}
						break;
					case DUDA_MARA_TOTEM_SPIRIT:
						if (cond == 8)
						{
							htmltext = "7593-01.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7593-04.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7593-05.htm";
							st.set("cond", "11");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(BOUND_DURKA_SPIRIT, 1);
							st.giveItems(TOTEM_SPIRIT_BLOOD, 1);
						}
						else if (cond == 11)
						{
							htmltext = "7593-06.htm";
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
		
		final int rnd = Rnd.get(1, 10);
		
		switch (npc.getId())
		{
			case SCARLET_SALAMANDER:
				if ((st.getInt("cond") == 1) && st.dropItems(FIRST_FIERY_EGG, 1, 1, 500000))
				{
					if (st.hasItems(KASHA_BEAR_PELT, KASHA_BLADE_SPIDER_HUSK))
					{
						st.set("cond", "2");
					}
				}
				break;
			
			case KASHA_BEAR:
				if ((st.getInt("cond") == 1) && st.dropItems(KASHA_BEAR_PELT, 1, 1, 500000))
				{
					if (st.hasItems(FIRST_FIERY_EGG, KASHA_BLADE_SPIDER_HUSK))
					{
						st.set("cond", "2");
					}
				}
				break;
			case KASHA_BLADE_SPIDER:
				if ((st.getInt("cond") == 1) && st.dropItems(KASHA_BLADE_SPIDER_HUSK, 1, 1, 500000))
				{
					if (st.hasItems(FIRST_FIERY_EGG, KASHA_BEAR_PELT))
					{
						st.set("cond", "2");
					}
				}
				break;
			case GRIZZLY_BEAR:
				if ((st.getInt("cond") == 6) && st.dropItems(GRIZZLY_BLOOD, 1, 3, 500000))
				{
					st.set("cond", "7");
				}
				break;
			case POISON_SPIDER:
			case ARACHNID_TRACKER:
				if ((st.getInt("cond") == 9) && st.dropItems(DURKA_PARASITE, 1, rnd, 700000))
				{
					st.playSound(PlaySoundType.QUEST_BEFORE_BATTLE);
					st.takeItems(DURKA_PARASITE, rnd);
					addSpawn(DURKA_SPIRIT, npc, false, 120000);
				}
				break;
			case DURKA_SPIRIT:
				if ((st.getInt("cond") == 9) && st.dropItems(BOUND_DURKA_SPIRIT, 1, 1, 700000))
				{
					st.set("cond", "10");
					st.takeItems(SPIRIT_NET, 1);
				}
				break;
		}
		
		return null;
	}
	
}
