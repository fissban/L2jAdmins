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
public class Q319_ScentOfDeath extends Script
{
	// Item
	private static final int ZOMBIE_SKIN = 1045;
	
	public Q319_ScentOfDeath()
	{
		super(319, "Scent of Death");
		
		registerItems(ZOMBIE_SKIN);
		
		addStartNpc(7138); // Minaless
		addTalkId(7138);
		
		addKillId(15, 20);
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
		
		if (event.equalsIgnoreCase("7138-04.htm"))
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
				htmltext = player.getLevel() < 11 ? "7138-02.htm" : "7138-03.htm";
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7138-05.htm";
				}
				else
				{
					htmltext = "7138-06.htm";
					st.takeItems(ZOMBIE_SKIN, -1);
					st.rewardItems(Inventory.ADENA_ID, 3350);
					st.rewardItems(1060, 1); // FIXME identificar item
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
		
		if (st.dropItems(ZOMBIE_SKIN, 1, 5, 200000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
