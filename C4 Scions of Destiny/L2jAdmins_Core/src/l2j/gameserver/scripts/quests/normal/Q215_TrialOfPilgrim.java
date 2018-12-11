package l2j.gameserver.scripts.quests.normal;

import l2j.Config;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class Q215_TrialOfPilgrim extends Script
{
	// Npc's
	private static final int SANTIAGO = 7648;
	private static final int ANCESTOR = 7649;
	private static final int GERALD = 7650;
	private static final int DORF = 7651;
	private static final int URUHA = 7652;
	private static final int PETRON = 7036;
	private static final int PRIMOS = 7117;
	private static final int ANDELLIA = 7362;
	private static final int GUARI = 7550;
	private static final int TANAPI = 7571;
	private static final int CASIAN = 7612;
	// Monster's
	private static final int LAVA_SALAMANDER = 5116;
	private static final int NAHIR = 5117;
	private static final int BLACK_WILLOW = 5118;
	// Item's
	private static final int BOOK_OF_SAGE = 2722;
	private static final int VOUCHER_OF_TRIAL = 2723;
	private static final int SPIRIT_OF_FLAME = 2724;
	private static final int ESSENSE_OF_FLAME = 2725;
	private static final int BOOK_OF_GERALD = 2726;
	private static final int GREY_BADGE = 2727;
	private static final int PICTURE_OF_NAHIR = 2728;
	private static final int HAIR_OF_NAHIR = 2729;
	private static final int STATUE_OF_EINHASAD = 2730;
	private static final int BOOK_OF_DARKNESS = 2731;
	private static final int DEBRIS_OF_WILLOW = 2732;
	private static final int TAG_OF_RUMOR = 2733;
	// Reward's
	private static final int MARK_OF_PILGRIM = 2721;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	private static final int ADENA = 57;
	
	public Q215_TrialOfPilgrim()
	{
		super(215, "Trial Of Pilgrim");
		addStartNpc(SANTIAGO);
		addTalkId(SANTIAGO, PETRON, PRIMOS, ANDELLIA, GUARI, TANAPI, CASIAN, ANCESTOR, GERALD, DORF, URUHA);
		addKillId(LAVA_SALAMANDER, NAHIR, BLACK_WILLOW);
		registerItems(BOOK_OF_SAGE, VOUCHER_OF_TRIAL, ESSENSE_OF_FLAME, BOOK_OF_GERALD, TAG_OF_RUMOR, PICTURE_OF_NAHIR, HAIR_OF_NAHIR, BOOK_OF_DARKNESS, DEBRIS_OF_WILLOW, GREY_BADGE, SPIRIT_OF_FLAME, STATUE_OF_EINHASAD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = "";
		
		switch (event)
		{
			case "1":
				htmltext = "7648-04.htm";
				st.startQuest();
				st.giveItems(VOUCHER_OF_TRIAL, 1);
				break;
			
			case "7648_1":
				htmltext = "7648-05.htm";
				break;
			
			case "7648_2":
				htmltext = "7648-06.htm";
				break;
			
			case "7648_3":
				htmltext = "7648-07.htm";
				break;
			
			case "7648_4":
				htmltext = "7648-08.htm";
				break;
			
			case "7648_5":
				htmltext = "7648-05.htm";
				break;
			
			case "7649_1":
				htmltext = "7649-04.htm";
				st.giveItems(SPIRIT_OF_FLAME, 1);
				st.takeItems(ESSENSE_OF_FLAME, 1);
				st.setCond(5);
				break;
			
			case "7650_1":
				if (st.getItemsCount(ADENA) >= (int) (100000 * Config.DROP_CHANCE_ADENA))
				{
					htmltext = "7650-02.htm";
					st.giveItems(BOOK_OF_GERALD, 1);
					st.takeItems(ADENA, (int) (100000 * Config.DROP_CHANCE_ADENA));
					st.setCond(7);
				}
				else
				{
					htmltext = "7650-03.htm";
				}
				break;
			
			case "7650_2":
				htmltext = "7650-03.htm";
				break;
			
			case "7362_1":
				htmltext = "7362-05.htm";
				st.takeItems(BOOK_OF_DARKNESS, 1);
				st.setCond(16);
				break;
			
			case "7362_2":
				htmltext = "7362-04.htm";
				st.setCond(16);
				break;
			
			case "7652_1":
				htmltext = "7652-02.htm";
				st.giveItems(BOOK_OF_DARKNESS, 1);
				st.takeItems(DEBRIS_OF_WILLOW, 1);
				st.setCond(15);
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		String htmltext = getNoQuestMsg();
		
		switch (st.getState())
		{
			case CREATED:
				switch (npc.getId())
				{
					case SANTIAGO:
						switch (st.getPlayer().getClassId())
						{
							case CLERIC:
							case ORACLE:
							case SHILLIEN_ORACLE:
							case SHAMAN:
								
								st.startQuest();
								
								if (st.getPlayer().getLevel() >= 35)
								{
									return "7648-03.htm";
								}
								st.exitQuest(true);
								return "7648-01.htm";
							
							default:
								st.exitQuest(true);
								return "7648-02.htm";
						}
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case SANTIAGO:
						if ((st.getCond() == 1) && st.hasItems(VOUCHER_OF_TRIAL))
						{
							htmltext = "7648-09.htm";
						}
						else if ((st.getCond() == 17) && st.hasItems(BOOK_OF_SAGE))
						{
							st.rewardExpAndSp(77832, 16000);
							st.giveItems(DIMENSIONAL_DIAMOND, 61);
							htmltext = "7648-10.htm";
							st.takeItems(BOOK_OF_SAGE, 1);
							st.giveItems(MARK_OF_PILGRIM, 1);
							st.exitQuest(false, true);
						}
						break;
					
					case TANAPI:
						if ((st.getCond() == 1) && st.hasItems(VOUCHER_OF_TRIAL))
						{
							htmltext = "7571-01.htm";
							st.takeItems(VOUCHER_OF_TRIAL, 1);
							st.setCond(2);
						}
						else if (st.getCond() == 2)
						{
							htmltext = "7571-02.htm";
						}
						else if ((st.getCond() == 5) && st.hasItems(SPIRIT_OF_FLAME))
						{
							htmltext = "7571-03.htm";
						}
						break;
					
					case ANCESTOR:
						if (st.getCond() == 2)
						{
							htmltext = "7649-01.htm";
							st.setCond(3);
						}
						else if (st.getCond() == 3)
						{
							htmltext = "7649-02.htm";
						}
						else if ((st.getCond() == 4) && st.hasItems(ESSENSE_OF_FLAME))
						{
							htmltext = "7649-03.htm";
						}
						break;
					
					case GUARI:
						if ((st.getCond() == 5) && st.hasItems(SPIRIT_OF_FLAME))
						{
							htmltext = "7550-01.htm";
							st.giveItems(TAG_OF_RUMOR, 1);
							st.setCond(6);
						}
						else if (st.getCond() == 6)
						{
							htmltext = "7550-02.htm";
						}
						break;
					
					case GERALD:
						if ((st.getCond() == 6) && st.hasItems(TAG_OF_RUMOR))
						{
							htmltext = "7650-01.htm";
						}
						else if ((st.getCond() >= 8) && st.hasItems(GREY_BADGE, BOOK_OF_GERALD))
						{
							htmltext = "7650-04.htm";
							int rate = (int) (Config.DROP_CHANCE_QUESTS_REWARD);
							if (rate == 0)
							{
								rate = 1;
							}
							st.giveItems(ADENA, (int) ((100000 * Config.DROP_CHANCE_ADENA) / rate));
							st.takeItems(BOOK_OF_GERALD, 1);
						}
						break;
					
					case DORF:
						if ((st.getCond() == 6) && st.hasItems(TAG_OF_RUMOR))
						{
							htmltext = "7651-01.htm";
							st.giveItems(GREY_BADGE, 1);
							st.takeItems(TAG_OF_RUMOR, 1);
							st.setCond(8);
						}
						else if ((st.getCond() == 7) && st.hasItems(TAG_OF_RUMOR))
						{
							htmltext = "7651-02.htm";
							st.giveItems(GREY_BADGE, 1);
							st.takeItems(TAG_OF_RUMOR, 1);
							st.setCond(8);
						}
						else if (st.getCond() == 8)
						{
							htmltext = "7651-03.htm";
						}
						break;
					
					case PRIMOS:
						if (st.getCond() == 8)
						{
							htmltext = "7117-01.htm";
							st.setCond(9);
						}
						else if (st.getCond() == 9)
						{
							htmltext = "7117-02.htm";
						}
						break;
					
					case PETRON:
						if (st.getCond() == 9)
						{
							htmltext = "7036-01.htm";
							st.giveItems(PICTURE_OF_NAHIR, 1);
							st.setCond(10);
						}
						else if (st.getCond() == 10)
						{
							htmltext = "7036-02.htm";
						}
						else if (st.getCond() == 11)
						{
							htmltext = "7036-03.htm";
							st.giveItems(STATUE_OF_EINHASAD, 1);
							st.takeItems(PICTURE_OF_NAHIR, 1);
							st.takeItems(HAIR_OF_NAHIR, 1);
							st.setCond(12);
						}
						else if ((st.getCond() == 12) && st.hasItems(STATUE_OF_EINHASAD))
						{
							htmltext = "7036-04.htm";
						}
						break;
					
					case ANDELLIA:
						if (st.getCond() == 12)
						{
							htmltext = "7362-01.htm";
							st.setCond(13);
						}
						else if (st.getCond() == 13)
						{
							htmltext = "7362-02.htm";
						}
						else if ((st.getCond() == 15) && st.hasItems(BOOK_OF_DARKNESS))
						{
							htmltext = "7362-03.htm";
						}
						else if (st.getCond() == 16)
						{
							htmltext = "7362-06.htm";
						}
						else if ((st.getCond() == 15) && !st.hasItems(BOOK_OF_DARKNESS))
						{
							htmltext = "7362-07.htm";
						}
						break;
					
					case URUHA:
						if ((st.getCond() == 14) && st.hasItems(DEBRIS_OF_WILLOW))
						{
							htmltext = "7652-01.htm";
						}
						else if ((st.getCond() == 15) && st.hasItems(BOOK_OF_DARKNESS))
						{
							htmltext = "7652-03.htm";
						}
						break;
					
					case CASIAN:
						if (st.getCond() == 16)
						{
							htmltext = "7612-01.htm";
							st.giveItems(BOOK_OF_SAGE, 1);
							if (st.hasItems(BOOK_OF_DARKNESS))
							{
								st.takeItems(BOOK_OF_DARKNESS, 1);
							}
							st.setCond(17);
							st.takeItems(GREY_BADGE, 1);
							st.takeItems(SPIRIT_OF_FLAME, 1);
							st.takeItems(STATUE_OF_EINHASAD, 1);
						}
						else if (st.getCond() == 17)
						{
							htmltext = "7612-02.htm";
						}
						break;
				}
			case COMPLETED:
				if (npc.getId() == SANTIAGO)
				{
					htmltext = getAlreadyCompletedMsg();
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st != null)
		{
			if (st.getState() != ScriptStateType.STARTED)
			{
				return null;
			}
			
			switch (npc.getId())
			{
				case LAVA_SALAMANDER:
					if ((st.getCond() == 3) && (st.getItemsCount(ESSENSE_OF_FLAME) == 0))
					{
						if (Rnd.get(5) == 1)
						{
							st.giveItems(ESSENSE_OF_FLAME, 1);
							st.setCond(4, true);
						}
					}
					break;
				
				case NAHIR:
					if ((st.getCond() == 10) && (st.getItemsCount(HAIR_OF_NAHIR) == 0))
					{
						st.giveItems(HAIR_OF_NAHIR, 1);
						st.setCond(11, true);
					}
					break;
				
				case BLACK_WILLOW:
					if ((st.getCond() == 13) && (st.getItemsCount(DEBRIS_OF_WILLOW) == 0))
					{
						if (Rnd.get(5) == 1)
						{
							st.giveItems(DEBRIS_OF_WILLOW, 1);
							st.setCond(14);
						}
					}
					break;
			}
		}
		
		return null;
	}
}
