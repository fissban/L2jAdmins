package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
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
public class Q329_CuriosityOfADwarf extends Script
{
	// ITEMs
	private static final int GOLEM_HEARTSTONE = 1346;
	private static final int BROKEN_HEARTSTONE = 1365;
	
	public Q329_CuriosityOfADwarf()
	{
		super(329, "Curiosity of a Dwarf");
		
		addStartNpc(437); // Rolento
		addTalkId(437);
		
		addKillId(83, 85); // Granite golem, Puncher
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
		
		if (event.equalsIgnoreCase("7437-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7437-06.htm"))
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
				htmltext = player.getLevel() < 33 ? "7437-01.htm" : "7437-02.htm";
				break;
			
			case STARTED:
				final int golem = st.getItemsCount(GOLEM_HEARTSTONE);
				final int broken = st.getItemsCount(BROKEN_HEARTSTONE);
				
				if ((golem + broken) == 0)
				{
					htmltext = "7437-04.htm";
				}
				else
				{
					htmltext = "7437-05.htm";
					st.takeItems(GOLEM_HEARTSTONE, -1);
					st.takeItems(BROKEN_HEARTSTONE, -1);
					st.rewardItems(Inventory.ADENA_ID, (broken * 50) + (golem * 1000) + ((golem + broken) > 10 ? 1183 : 0));
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
		
		final int chance = Rnd.get(100);
		if (chance < 2)
		{
			st.dropItemsAlways(GOLEM_HEARTSTONE, 1, 0);
		}
		else if (chance < (npc.getId() == 83 ? 44 : 50))
		{
			st.dropItemsAlways(BROKEN_HEARTSTONE, 1, 0);
		}
		
		return null;
	}
	
}
