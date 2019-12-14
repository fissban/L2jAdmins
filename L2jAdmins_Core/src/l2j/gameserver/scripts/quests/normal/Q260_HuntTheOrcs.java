package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q260_HuntTheOrcs extends Script
{
	// NPC
	private static final int RAYEN = 7221;
	// MOBs
	private static final int KABOO_ORC = 468;
	private static final int KABOO_ORC_ARCHER = 469;
	private static final int KABOO_ORC_GRUNT = 470;
	private static final int KABOO_ORC_FIGHTER = 471;
	private static final int KABOO_ORC_FIGHTER_LEADER = 472;
	private static final int KABOO_ORC_FIGHTER_LIEUTENANT = 473;
	// ITEMs
	private static final int ORC_AMULET = 1114;
	private static final int ORC_NECKLACE = 1115;
	
	public Q260_HuntTheOrcs()
	{
		super(260, "Hunt the Orcs");
		
		registerItems(ORC_AMULET, ORC_NECKLACE);
		
		addStartNpc(RAYEN);
		addTalkId(RAYEN);
		addKillId(KABOO_ORC, KABOO_ORC_ARCHER, KABOO_ORC_GRUNT, KABOO_ORC_FIGHTER, KABOO_ORC_FIGHTER_LEADER, KABOO_ORC_FIGHTER_LIEUTENANT);
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
		
		if (event.equalsIgnoreCase("7221-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7221-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7221-00.htm";
				}
				else if (player.getLevel() < 6)
				{
					htmltext = "7221-01.htm";
				}
				else
				{
					htmltext = "7221-02.htm";
				}
				break;
			
			case STARTED:
				final int amulet = st.getItemsCount(ORC_AMULET);
				final int necklace = st.getItemsCount(ORC_NECKLACE);
				
				if ((amulet == 0) && (necklace == 0))
				{
					htmltext = "7221-04.htm";
				}
				else
				{
					htmltext = "7221-05.htm";
					st.takeItems(ORC_AMULET, -1);
					st.takeItems(ORC_NECKLACE, -1);
					st.rewardItems(Inventory.ADENA_ID, (amulet * 5) + (necklace * 15));
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
			case KABOO_ORC:
			case KABOO_ORC_GRUNT:
			case KABOO_ORC_ARCHER:
				st.dropItems(ORC_AMULET, 1, 0, 500000);
				break;
			
			case KABOO_ORC_FIGHTER:
			case KABOO_ORC_FIGHTER_LEADER:
			case KABOO_ORC_FIGHTER_LIEUTENANT:
				st.dropItems(ORC_NECKLACE, 1, 0, 500000);
				break;
		}
		
		return null;
	}
	
}
