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
public class Q151_CureForFeverDisease extends Script
{
	// ITEMs
	private static final int POISON_SAC = 703;
	private static final int FEVER_MEDICINE = 704;
	// NPCs
	private static final int ELIAS = 7050;
	private static final int YOHANES = 7032;
	// MOBs
	private static final int GIANT_SPIDER = 103;
	private static final int TALON_SPIDER = 106;
	private static final int BLADE_SPIDER = 108;
	
	public Q151_CureForFeverDisease()
	{
		super(151, "Cure for Fever Disease");
		
		registerItems(FEVER_MEDICINE, POISON_SAC);
		
		addStartNpc(ELIAS);
		addTalkId(ELIAS, YOHANES);
		addKillId(GIANT_SPIDER, TALON_SPIDER, BLADE_SPIDER);
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
		
		if (event.equalsIgnoreCase("7050-03.htm"))
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
				htmltext = player.getLevel() < 15 ? "7050-01.htm" : "7050-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case ELIAS:
						if (cond == 1)
						{
							htmltext = "7050-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7050-05.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7050-06.htm";
							st.takeItems(FEVER_MEDICINE, 1);
							st.giveItems(102, 1);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case YOHANES:
						if (cond == 2)
						{
							htmltext = "7032-01.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(POISON_SAC, 1);
							st.giveItems(FEVER_MEDICINE, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7032-02.htm";
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
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		if (st.dropItems(POISON_SAC, 1, 1, 200000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
