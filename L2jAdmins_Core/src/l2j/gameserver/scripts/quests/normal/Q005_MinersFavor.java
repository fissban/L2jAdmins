package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q005_MinersFavor extends Script
{
	// NPCs
	private static final int SHARI = 7517;
	private static final int GARITA = 7518;
	private static final int REED = 7520;
	private static final int BRUNON = 7526;
	private static final int BOLTER = 7554;
	// ITEMs
	private static final int BOLTERS_LIST = 1547;
	private static final int MINING_BOOTS = 1548;
	private static final int MINERS_PICK = 1549;
	private static final int BOOMBOOM_POWDER = 1550;
	private static final int REDSTONE_BEER = 1551;
	private static final int BOLTERS_SMELLY_SOCKS = 1552;
	// REWARD
	private static final int NECKLACE = 906;
	
	public Q005_MinersFavor()
	{
		super(5, "Miner\'s Favor");
		addStartNpc(BOLTER);
		addTalkId(SHARI, GARITA, REED, BRUNON, BOLTER);
		registerItems(BOLTERS_LIST, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER, BOLTERS_SMELLY_SOCKS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		// BOLTER
		if (event.equalsIgnoreCase("7554-03.htm"))
		{
			st.startQuest();
			st.giveItems(BOLTERS_LIST, 1);
			st.giveItems(BOLTERS_SMELLY_SOCKS, 1);
		}
		// BRUNON
		else if (event.equalsIgnoreCase("7526-02.htm"))
		{
			st.giveItems(MINERS_PICK, 1);
			
			if (st.hasItems(MINING_BOOTS, BOOMBOOM_POWDER, REDSTONE_BEER, MINERS_PICK))
			{
				st.setCond(2, true);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				if ((player.getRace() != Race.DWARF) || (player.getLevel() < 2) || (player.getLevel() > 5))
				{
					htmltext = "7554-01.htm";
				}
				else
				{
					htmltext = "7554-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getId())
				{
					case BOLTER:
						if (cond == 2)
						{
							htmltext = "7554-06.htm";
							st.rewardItems(NECKLACE, 1);
							st.exitQuest(false, true);
						}
						else
						{
							htmltext = "7554-04.htm";
						}
						break;
					case GARITA:
						if (st.hasItems(MINING_BOOTS))
						{
							htmltext = "7518-02.htm";
						}
						else
						{
							htmltext = "7518-01.htm";
							st.giveItems(MINING_BOOTS, 1);
						}
						break;
					case SHARI:
						if (st.hasItems(BOOMBOOM_POWDER))
						{
							htmltext = "7517-02.htm";
						}
						else
						{
							htmltext = "7517-01.htm";
							st.giveItems(BOOMBOOM_POWDER, 1);
						}
						break;
					case REED:
						if (st.hasItems(REDSTONE_BEER))
						{
							htmltext = "7520-02.htm";
						}
						else
						{
							htmltext = "7520-01.htm";
							st.giveItems(REDSTONE_BEER, 1);
						}
						break;
					case BRUNON:
						if (st.hasItems(MINERS_PICK))
						{
							htmltext = "7526-02.htm";
						}
						else
						{
							htmltext = "7526-01.htm";
						}
						break;
				}
				
				if (st.hasItems(MINING_BOOTS, BOOMBOOM_POWDER, REDSTONE_BEER, MINERS_PICK))
				{
					st.setCond(2, true);
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
}
