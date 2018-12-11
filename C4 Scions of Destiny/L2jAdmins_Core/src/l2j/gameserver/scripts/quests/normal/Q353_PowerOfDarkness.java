package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author aCis, MauroNOB, CaFi, zarie
 */
public class Q353_PowerOfDarkness extends Script
{
	// Item
	private static final int STONE = 5862;
	
	public Q353_PowerOfDarkness()
	{
		super(353, "Power of Darkness");
		
		registerItems(STONE);
		
		addStartNpc(8044); // Galman
		addTalkId(8044);
		
		addKillId(244, 245, 283, 284);
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
		
		if (event.equalsIgnoreCase("8044-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8044-08.htm"))
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
				htmltext = player.getLevel() < 55 ? "8044-01.htm" : "8044-02.htm";
				break;
			
			case STARTED:
				final int stones = st.getItemsCount(STONE);
				if (stones == 0)
				{
					htmltext = "8044-05.htm";
				}
				else
				{
					htmltext = "8044-06.htm";
					st.takeItems(STONE, -1);
					st.rewardItems(Inventory.ADENA_ID, 2500 + (230 * stones));
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
		
		st.dropItems(STONE, 1, 0, ((npc.getId() == 244) || (npc.getId() == 283)) ? 480000 : 500000);
		
		return null;
	}
	
}
