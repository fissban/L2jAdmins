package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q152_ShardsOfGolem extends Script
{
	// ITEMs
	private static final int HARRIS_RECEIPT_1 = 1008;
	private static final int HARRIS_RECEIPT_2 = 1009;
	private static final int GOLEM_SHARD = 1010;
	private static final int TOOL_BOX = 1011;
	// REWARDs
	private static final int WOODEN_BREASTPLATE = 23;
	// NPCs
	private static final int HARRIS = 7035;
	private static final int ALTRAN = 7283;
	// MOBs
	private static final int STONE_GOLEM = 16;
	
	public Q152_ShardsOfGolem()
	{
		super(152, "Shards of Golem");
		
		registerItems(HARRIS_RECEIPT_1, HARRIS_RECEIPT_2, GOLEM_SHARD, TOOL_BOX);
		
		addStartNpc(HARRIS);
		addTalkId(HARRIS, ALTRAN);
		
		addKillId(STONE_GOLEM);
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
		
		if (event.equalsIgnoreCase("7035-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(HARRIS_RECEIPT_1, 1);
		}
		else if (event.equalsIgnoreCase("7283-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(HARRIS_RECEIPT_1, 1);
			st.giveItems(HARRIS_RECEIPT_2, 1);
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
				htmltext = player.getLevel() < 10 ? "7035-01a.htm" : "7035-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case HARRIS:
						if (cond < 4)
						{
							htmltext = "7035-03.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7035-04.htm";
							st.takeItems(HARRIS_RECEIPT_2, 1);
							st.takeItems(TOOL_BOX, 1);
							st.giveItems(WOODEN_BREASTPLATE, 1);
							st.rewardExpAndSp(5000, 0);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case ALTRAN:
						if (cond == 1)
						{
							htmltext = "7283-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7283-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7283-04.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(GOLEM_SHARD, -1);
							st.giveItems(TOOL_BOX, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7283-05.htm";
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
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "2");
		if (st == null)
		{
			return null;
		}
		
		if (st.dropItems(GOLEM_SHARD, 1, 5, 300000))
		{
			st.set("cond", "3");
		}
		
		return null;
	}
	
}
