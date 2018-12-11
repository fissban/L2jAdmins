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
public class Q271_ProofOfValor extends Script
{
	// NPCs
	private static final int RUKAIN = 7577;
	// MOBs
	private static final int WOLF = 475;
	// ITEMs
	private static final int KASHA_WOLF_FANG = 1473;
	// REWARDs
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int NECKLACE_OF_COURAGE = 1506;
	
	public Q271_ProofOfValor()
	{
		super(271, "Proof of Valor");
		
		registerItems(KASHA_WOLF_FANG);
		
		addStartNpc(RUKAIN);
		addTalkId(RUKAIN);
		addKillId(WOLF);
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
		
		if (event.equalsIgnoreCase("7577-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			
			if (st.hasAtLeastOneItem(NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR))
			{
				htmltext = "7577-07.htm";
			}
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7577-00.htm";
				}
				else if (player.getLevel() < 4)
				{
					htmltext = "7577-01.htm";
				}
				else
				{
					htmltext = st.hasAtLeastOneItem(NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR) ? "7577-06.htm" : "7577-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = st.hasAtLeastOneItem(NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR) ? "7577-07.htm" : "7577-04.htm";
				}
				else
				{
					htmltext = "7577-05.htm";
					st.takeItems(KASHA_WOLF_FANG, -1);
					st.giveItems(Rnd.get(100) < 10 ? NECKLACE_OF_VALOR : NECKLACE_OF_COURAGE, 1);
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
		
		if (st.dropItemsAlways(KASHA_WOLF_FANG, Rnd.get(4) == 0 ? 2 : 1, 50))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
