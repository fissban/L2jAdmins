package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class Q266_PleasOfPixies extends Script
{
	// NPCs
	private static final int MURIKA = 12091;
	// MOBs
	private static final int GRAY_WOLF = 525;
	private static final int YOUNG_RED_KELTIR = 530;
	private static final int RED_KELTIR = 534;
	private static final int ELDER_RED_KELTIR = 537;
	// ITEMs
	private static final int PREDATOR_FANG = 1334;
	// REWARDs
	private static final int GLASS_SHARD = 1336;
	private static final int EMERALD = 1337;
	private static final int BLUE_ONYX = 1338;
	private static final int ONYX = 1339;
	
	public Q266_PleasOfPixies()
	{
		super(266, "Pleas of Pixies");
		
		registerItems(PREDATOR_FANG);
		
		addStartNpc(MURIKA);
		addTalkId(MURIKA);
		
		addKillId(GRAY_WOLF, YOUNG_RED_KELTIR, RED_KELTIR, ELDER_RED_KELTIR);
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
		
		if (event.equalsIgnoreCase("8852-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "8852-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "8852-01.htm";
				}
				else
				{
					htmltext = "8852-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getItemsCount(PREDATOR_FANG) < 100)
				{
					htmltext = "8852-04.htm";
				}
				else
				{
					htmltext = "8852-05.htm";
					st.takeItems(PREDATOR_FANG, -1);
					
					final int n = Rnd.get(100);
					if (n < 10)
					{
						st.playSound(PlaySoundType.QUEST_JACKPOT);
						st.rewardItems(EMERALD, 1);
					}
					else if (n < 30)
					{
						st.rewardItems(BLUE_ONYX, 1);
					}
					else if (n < 60)
					{
						st.rewardItems(ONYX, 1);
					}
					else
					{
						st.rewardItems(GLASS_SHARD, 1);
					}
					
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(true);
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case GRAY_WOLF:
				if (st.dropItemsAlways(PREDATOR_FANG, Rnd.get(2, 3), 100))
				{
					st.set("cond", "2");
				}
				break;
			
			case YOUNG_RED_KELTIR:
				if (st.dropItems(PREDATOR_FANG, 1, 100, 800000))
				{
					st.set("cond", "2");
				}
				break;
			
			case RED_KELTIR:
				if (st.dropItems(PREDATOR_FANG, Rnd.get(3) == 0 ? 1 : 2, 100, 600000))
				{
					st.set("cond", "2");
				}
				break;
			
			case ELDER_RED_KELTIR:
				if (st.dropItemsAlways(PREDATOR_FANG, 2, 100))
				{
					st.set("cond", "2");
				}
				break;
		}
		
		return null;
	}
	
}
