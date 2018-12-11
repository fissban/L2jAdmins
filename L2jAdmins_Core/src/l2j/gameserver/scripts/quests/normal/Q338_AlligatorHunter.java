package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
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
public class Q338_AlligatorHunter extends Script
{
	// Item
	private static final int ALLIGATOR_PELT = 4337;
	
	public Q338_AlligatorHunter()
	{
		super(338, "Alligator Hunter");
		
		registerItems(ALLIGATOR_PELT);
		
		addStartNpc(7892); // Enverun
		addTalkId(7892);
		
		addKillId(135); // Alligator
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
		
		if (event.equalsIgnoreCase("7892-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7892-05.htm"))
		{
			final int pelts = st.getItemsCount(ALLIGATOR_PELT);
			
			int reward = pelts * 60;
			if (pelts > 10)
			{
				reward += 3430;
			}
			
			st.takeItems(ALLIGATOR_PELT, -1);
			st.rewardItems(Inventory.ADENA_ID, reward);
		}
		else if (event.equalsIgnoreCase("7892-08.htm"))
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
				htmltext = player.getLevel() < 40 ? "7892-00.htm" : "7892-01.htm";
				break;
			
			case STARTED:
				htmltext = st.hasItems(ALLIGATOR_PELT) ? "7892-03.htm" : "7892-04.htm";
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
		
		st.dropItemsAlways(ALLIGATOR_PELT, 1, 0);
		
		return null;
	}
	
}
