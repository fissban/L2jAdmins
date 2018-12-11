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
public class Q154_SacrificeToTheSea extends Script
{
	// NPCs
	private static final int ROCKSWELL = 7312;
	private static final int CRISTEL = 7051;
	private static final int ROLFE = 7055;
	// ITEMs
	private static final int FOX_FUR = 1032;
	private static final int FOX_FUR_YARN = 1033;
	private static final int MAIDEN_DOLL = 1034;
	// REWARDs
	private static final int EARING = 113;
	// MOBs
	private static final int BEARDER_KELTIR = 481;
	private static final int ELDER_KELTIR = 544;
	private static final int YOUNG_KELTIR = 545;
	
	public Q154_SacrificeToTheSea()
	{
		super(154, "Sacrifice to the Sea");
		
		registerItems(FOX_FUR, FOX_FUR_YARN, MAIDEN_DOLL);
		
		addStartNpc(ROCKSWELL);
		addTalkId(ROCKSWELL, CRISTEL, ROLFE);
		addKillId(BEARDER_KELTIR, ELDER_KELTIR, YOUNG_KELTIR); // can be found near Talking Island.
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
		
		if (event.equalsIgnoreCase("7312-04.htm"))
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
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 2 ? "7312-02.htm" : "7312-03.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case ROCKSWELL:
						if (cond == 1)
						{
							htmltext = "7312-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7312-08.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7312-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7312-07.htm";
							st.takeItems(MAIDEN_DOLL, -1);
							st.giveItems(EARING, 1);
							st.rewardExpAndSp(100, 0);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case CRISTEL:
						if (cond == 1)
						{
							htmltext = st.hasItems(FOX_FUR) ? "7051-01.htm" : "7051-01a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7051-02.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(FOX_FUR, -1);
							st.giveItems(FOX_FUR_YARN, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7051-03.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7051-04.htm";
						}
						break;
					
					case ROLFE:
						if (cond < 3)
						{
							htmltext = "7055-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7055-01.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(FOX_FUR_YARN, 1);
							st.giveItems(MAIDEN_DOLL, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7055-02.htm";
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
		
		if (st.dropItems(FOX_FUR, 1, 10, 400000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
