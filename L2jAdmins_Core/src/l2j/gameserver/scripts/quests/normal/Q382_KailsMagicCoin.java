package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

/**
 * @author        MauroNOB, CaFi, zarie
 * @originalQuest aCis
 */
public class Q382_KailsMagicCoin extends Script
{
	// NPCs
	private static final int VERGARA = 7687;
	
	// Monsters
	private final static int FALLEN_ORC = 1017;
	private final static int FALLEN_ORC_ARCHER = 1019;
	private final static int FALLEN_ORC_SHAMAN = 1020;
	private final static int FALLEN_ORC_CAPTAIN = 1022;
	
	// Items
	private final static int ROYAL_MEMBERSHIP = 5898;
	private final static int SILVER_BASILISK = 5961;
	private final static int GOLD_GOLEM = 5962;
	private final static int BLOOD_DRAGON = 5963;
	
	public Q382_KailsMagicCoin()
	{
		super(382, "Kail's Magic Coin");
		
		registerItems(SILVER_BASILISK, GOLD_GOLEM, BLOOD_DRAGON);
		
		addStartNpc(VERGARA);
		addTalkId(VERGARA);
		
		addKillId(FALLEN_ORC, FALLEN_ORC_ARCHER, FALLEN_ORC_SHAMAN, FALLEN_ORC_CAPTAIN);
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
		
		if (event.equalsIgnoreCase("7687-03.htm"))
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
				htmltext = (player.getLevel() < 55) || !st.hasItems(ROYAL_MEMBERSHIP) ? "7687-01.htm" : "7687-02.htm";
				break;
			
			case STARTED:
				htmltext = "7687-04.htm";
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
			case FALLEN_ORC:
				st.dropItems(SILVER_BASILISK, 1, 0, 100000);
				break;
			
			case FALLEN_ORC_ARCHER:
				st.dropItems(GOLD_GOLEM, 1, 0, 100000);
				break;
			
			case FALLEN_ORC_SHAMAN:
				st.dropItems(BLOOD_DRAGON, 1, 0, 100000);
				break;
			
			case FALLEN_ORC_CAPTAIN:
				st.dropItems(5961 + Rnd.get(3), 1, 0, 100000);
				break;
		}
		
		return null;
	}
	
}
