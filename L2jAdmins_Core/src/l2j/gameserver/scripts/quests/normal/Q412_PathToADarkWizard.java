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
public class Q412_PathToADarkWizard extends Script
{
	// Items
	private static final int SEED_OF_ANGER = 1253;
	private static final int SEED_OF_DESPAIR = 1254;
	private static final int SEED_OF_HORROR = 1255;
	private static final int SEED_OF_LUNACY = 1256;
	private static final int FAMILY_REMAINS = 1257;
	private static final int VARIKA_LIQUOR = 1258;
	private static final int KNEE_BONE = 1259;
	private static final int HEART_OF_LUNACY = 1260;
	private static final int JEWEL_OF_DARKNESS = 1261;
	private static final int LUCKY_KEY = 1277;
	private static final int CANDLE = 1278;
	private static final int HUB_SCENT = 1279;
	
	// NPCs
	private static final int VARIKA = 7421;
	private static final int CHARKEREN = 7415;
	private static final int ANNIKA = 7418;
	private static final int ARKENIA = 7419;
	
	public Q412_PathToADarkWizard()
	{
		super(412, "Path to a Dark Wizard");
		
		registerItems(SEED_OF_ANGER, SEED_OF_DESPAIR, SEED_OF_HORROR, SEED_OF_LUNACY, FAMILY_REMAINS, VARIKA_LIQUOR, KNEE_BONE, HEART_OF_LUNACY, LUCKY_KEY, CANDLE, HUB_SCENT);
		
		addStartNpc(VARIKA);
		addTalkId(VARIKA, CHARKEREN, ANNIKA, ARKENIA);
		
		addKillId(15, 22, 45, 517, 518);
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
		
		if (event.equalsIgnoreCase("7421-05.htm"))
		{
			if (player.getClassId() != ClassId.DARK_ELF_MAGE)
			{
				htmltext = player.getClassId() == ClassId.DARK_ELF_WIZARD ? "7421-02a.htm" : "7421-03.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7421-02.htm";
			}
			else if (st.hasItems(JEWEL_OF_DARKNESS))
			{
				htmltext = "7421-04.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				st.giveItems(SEED_OF_DESPAIR, 1);
			}
		}
		else if (event.equalsIgnoreCase("7421-07.htm"))
		{
			if (st.hasItems(SEED_OF_ANGER))
			{
				htmltext = "7421-06.htm";
			}
			else if (st.hasItems(LUCKY_KEY))
			{
				htmltext = "7421-08.htm";
			}
			else if (st.getItemsCount(FAMILY_REMAINS) == 3)
			{
				htmltext = "7421-18.htm";
			}
		}
		else if (event.equalsIgnoreCase("7421-10.htm"))
		{
			if (st.hasItems(SEED_OF_HORROR))
			{
				htmltext = "7421-09.htm";
			}
			else if (st.getItemsCount(KNEE_BONE) == 2)
			{
				htmltext = "7421-19.htm";
			}
		}
		else if (event.equalsIgnoreCase("7421-13.htm"))
		{
			if (st.hasItems(SEED_OF_LUNACY))
			{
				htmltext = "7421-12.htm";
			}
		}
		else if (event.equalsIgnoreCase("7415-03.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(LUCKY_KEY, 1);
		}
		else if (event.equalsIgnoreCase("7418-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(CANDLE, 1);
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
				htmltext = "7421-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case VARIKA:
						if (st.hasItems(SEED_OF_ANGER, SEED_OF_HORROR, SEED_OF_LUNACY))
						{
							htmltext = "7421-16.htm";
							st.takeItems(SEED_OF_ANGER, 1);
							st.takeItems(SEED_OF_DESPAIR, 1);
							st.takeItems(SEED_OF_HORROR, 1);
							st.takeItems(SEED_OF_LUNACY, 1);
							st.giveItems(JEWEL_OF_DARKNESS, 1);
							st.rewardExpAndSp(3200, 1650);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						else
						{
							htmltext = "7421-17.htm";
						}
						break;
					
					case CHARKEREN:
						if (st.hasItems(SEED_OF_ANGER))
						{
							htmltext = "7415-06.htm";
						}
						else if (!st.hasItems(LUCKY_KEY))
						{
							htmltext = "7415-01.htm";
						}
						else if (st.getItemsCount(FAMILY_REMAINS) == 3)
						{
							htmltext = "7415-05.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(FAMILY_REMAINS, -1);
							st.takeItems(LUCKY_KEY, 1);
							st.giveItems(SEED_OF_ANGER, 1);
						}
						else
						{
							htmltext = "7415-04.htm";
						}
						break;
					
					case ANNIKA:
						if (st.hasItems(SEED_OF_HORROR))
						{
							htmltext = "7418-04.htm";
						}
						else if (!st.hasItems(CANDLE))
						{
							htmltext = "7418-01.htm";
						}
						else if (st.getItemsCount(KNEE_BONE) == 2)
						{
							htmltext = "7418-04.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CANDLE, 1);
							st.takeItems(KNEE_BONE, -1);
							st.giveItems(SEED_OF_HORROR, 1);
						}
						else
						{
							htmltext = "7418-03.htm";
						}
						break;
					
					case ARKENIA:
						if (st.hasItems(SEED_OF_LUNACY))
						{
							htmltext = "7419-03.htm";
						}
						else if (!st.hasItems(HUB_SCENT))
						{
							htmltext = "7419-01.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(HUB_SCENT, 1);
						}
						else if (st.getItemsCount(HEART_OF_LUNACY) == 3)
						{
							htmltext = "7419-03.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(HEART_OF_LUNACY, -1);
							st.takeItems(HUB_SCENT, 1);
							st.giveItems(SEED_OF_LUNACY, 1);
						}
						else
						{
							htmltext = "7419-02.htm";
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
			case 15:
				if (st.hasItems(LUCKY_KEY))
				{
					st.dropItems(FAMILY_REMAINS, 1, 3, 500000);
				}
				break;
			
			case 22:
			case 517:
			case 518:
				if (st.hasItems(CANDLE))
				{
					st.dropItems(KNEE_BONE, 1, 2, 500000);
				}
				break;
			
			case 45:
				if (st.hasItems(HUB_SCENT))
				{
					st.dropItems(HEART_OF_LUNACY, 1, 3, 500000);
				}
				break;
		}
		
		return null;
	}
	
}