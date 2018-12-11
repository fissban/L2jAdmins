package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q263_OrcSubjugation extends Script
{
	// NPC
	private static final int KAYLEEN = 7346;
	// MOBs
	private static final int BALOR_ORC_ARCHER = 385;
	private static final int BALOR_ORC_FIGHTER = 386;
	private static final int BALOR_ORC_FIGHTER_LEADER = 387;
	private static final int BALOR_ORC_LIEUTENANT = 388;
	// ITEMs
	private static final int ORC_AMULET = 1116;
	private static final int ORC_NECKLACE = 1117;
	
	public Q263_OrcSubjugation()
	{
		super(263, "Orc Subjugation");
		
		registerItems(ORC_AMULET, ORC_NECKLACE);
		
		addStartNpc(KAYLEEN);
		addTalkId(KAYLEEN);
		addKillId(BALOR_ORC_ARCHER, BALOR_ORC_FIGHTER, BALOR_ORC_FIGHTER_LEADER, BALOR_ORC_LIEUTENANT);
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
		
		if (event.equalsIgnoreCase("7346-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7346-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7346-00.htm";
				}
				else if (player.getLevel() < 8)
				{
					htmltext = "7346-01.htm";
				}
				else
				{
					htmltext = "7346-02.htm";
				}
				break;
			
			case STARTED:
				final int amulet = st.getItemsCount(ORC_AMULET);
				final int necklace = st.getItemsCount(ORC_NECKLACE);
				
				if ((amulet == 0) && (necklace == 0))
				{
					htmltext = "7346-04.htm";
				}
				else
				{
					htmltext = "7346-05.htm";
					st.takeItems(ORC_AMULET, -1);
					st.takeItems(ORC_NECKLACE, -1);
					st.rewardItems(Inventory.ADENA_ID, (amulet * 20) + (necklace * 30));
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
		
		st.dropItems(npc.getId() == BALOR_ORC_ARCHER ? ORC_AMULET : ORC_NECKLACE, 1, 0, 500000);
		
		return null;
	}
	
}
