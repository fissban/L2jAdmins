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
public class Q295_DreamingOfTheSkies extends Script
{
	// NPCs
	private static final int ARIN = 7536;
	// MOBs
	private static final int MAGICAL_WEAVER = 153;
	// ITEMs
	private static final int FLOATING_STONE = 1492;
	// REWARDs
	private static final int RING_OF_FIREFLY = 1509;
	
	public Q295_DreamingOfTheSkies()
	{
		super(295, "Dreaming of the Skies");
		
		registerItems(FLOATING_STONE);
		
		addStartNpc(ARIN);
		addTalkId(ARIN);
		addKillId(MAGICAL_WEAVER);
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
		
		if (event.equalsIgnoreCase("7536-03.htm"))
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
				htmltext = player.getLevel() < 11 ? "7536-01.htm" : "7536-02.htm";
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7536-04.htm";
				}
				else
				{
					st.takeItems(FLOATING_STONE, -1);
					
					if (!st.hasItems(RING_OF_FIREFLY))
					{
						htmltext = "7536-05.htm";
						st.giveItems(RING_OF_FIREFLY, 1);
					}
					else
					{
						htmltext = "7536-06.htm";
						st.rewardItems(Inventory.ADENA_ID, 2400);
					}
					
					st.rewardExpAndSp(0, 500);
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
		
		if (st.dropItemsAlways(FLOATING_STONE, Rnd.get(100) > 25 ? 1 : 2, 50))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
