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
public class Q153_DeliverGoods extends Script
{
	// NPCs
	private static final int JACKSON = 7002;
	private static final int SILVIA = 7003;
	private static final int ARNOLD = 7041;
	private static final int RANT = 7054;
	// ITEMs
	private static final int DELIVERY_LIST = 1012;
	private static final int HEAVY_WOOD_BOX = 1013;
	private static final int CLOTH_BUNDLE = 1014;
	private static final int CLAY_POT = 1015;
	private static final int JACKSON_RECEIPT = 1016;
	private static final int SILVIA_RECEIPT = 1017;
	private static final int RANT_RECEIPT = 1018;
	// REWARDs
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int RING_OF_KNOWLEDGE = 875;
	
	public Q153_DeliverGoods()
	{
		super(153, "Deliver Goods");
		
		registerItems(DELIVERY_LIST, HEAVY_WOOD_BOX, CLOTH_BUNDLE, CLAY_POT, JACKSON_RECEIPT, SILVIA_RECEIPT, RANT_RECEIPT);
		
		addStartNpc(ARNOLD);
		addTalkId(JACKSON, SILVIA, ARNOLD, RANT);
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
		
		if (event.equalsIgnoreCase("7041-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(DELIVERY_LIST, 1);
			st.giveItems(CLAY_POT, 1);
			st.giveItems(CLOTH_BUNDLE, 1);
			st.giveItems(HEAVY_WOOD_BOX, 1);
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
				htmltext = player.getLevel() < 2 ? "7041-00.htm" : "7041-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case ARNOLD:
						if (st.getInt("cond") == 1)
						{
							htmltext = "7041-03.htm";
						}
						else if (st.getInt("cond") == 2)
						{
							htmltext = "7041-04.htm";
							st.takeItems(DELIVERY_LIST, 1);
							st.takeItems(JACKSON_RECEIPT, 1);
							st.takeItems(SILVIA_RECEIPT, 1);
							st.takeItems(RANT_RECEIPT, 1);
							st.giveItems(RING_OF_KNOWLEDGE, 1);
							st.giveItems(RING_OF_KNOWLEDGE, 1);
							st.rewardExpAndSp(600, 0);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case JACKSON:
						if (st.hasItems(HEAVY_WOOD_BOX))
						{
							htmltext = "7002-01.htm";
							st.takeItems(HEAVY_WOOD_BOX, 1);
							st.giveItems(JACKSON_RECEIPT, 1);
							
							if (st.hasItems(SILVIA_RECEIPT, RANT_RECEIPT))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else
						{
							htmltext = "7002-02.htm";
						}
						break;
					
					case SILVIA:
						if (st.hasItems(CLOTH_BUNDLE))
						{
							htmltext = "7003-01.htm";
							st.takeItems(CLOTH_BUNDLE, 1);
							st.giveItems(SILVIA_RECEIPT, 1);
							st.giveItems(SOULSHOT_NO_GRADE, 3);
							
							if (st.hasItems(JACKSON_RECEIPT, RANT_RECEIPT))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else
						{
							htmltext = "7003-02.htm";
						}
						break;
					
					case RANT:
						if (st.hasItems(CLAY_POT))
						{
							htmltext = "7054-01.htm";
							st.takeItems(CLAY_POT, 1);
							st.giveItems(RANT_RECEIPT, 1);
							
							if (st.hasItems(JACKSON_RECEIPT, SILVIA_RECEIPT))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else
						{
							htmltext = "7054-02.htm";
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
	
}
