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
public class Q170_DangerousSeduction extends Script
{
	// NPCs
	private static final int VELLIOR = 7305;
	// MOBs
	private static final int MERKENIS = 5022;
	// ITEMs
	private static final int NIGHTMARE_CRYSTAL = 1046;
	
	public Q170_DangerousSeduction()
	{
		super(170, "Dangerous Seduction");
		
		registerItems(NIGHTMARE_CRYSTAL);
		
		addStartNpc(VELLIOR);
		addTalkId(VELLIOR);
		addKillId(MERKENIS);
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
		
		if (event.equalsIgnoreCase("7305-04.htm"))
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7305-00.htm";
				}
				else if (player.getLevel() < 21)
				{
					htmltext = "7305-02.htm";
				}
				else
				{
					htmltext = "7305-03.htm";
				}
				break;
			
			case STARTED:
				if (st.hasItems(NIGHTMARE_CRYSTAL))
				{
					htmltext = "7305-06.htm";
					st.takeItems(NIGHTMARE_CRYSTAL, -1);
					st.rewardItems(Inventory.ADENA_ID, 102680);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
				}
				else
				{
					htmltext = "7305-05.htm";
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
		
		st.set("cond", "2");
		st.playSound(PlaySoundType.QUEST_MIDDLE);
		st.giveItems(NIGHTMARE_CRYSTAL, 1);
		
		return null;
	}
	
}
