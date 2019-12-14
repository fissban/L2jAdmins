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
public class Q320_BonesTellTheFuture extends Script
{
	// Quest item
	private final int BONE_FRAGMENT = 809;
	
	public Q320_BonesTellTheFuture()
	{
		super(320, "Bones Tell the Future");
		
		registerItems(BONE_FRAGMENT);
		
		addStartNpc(7359); // Kaitar
		addTalkId(7359);
		
		addKillId(517, 518);
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
		
		if (event.equalsIgnoreCase("7359-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		
		return event;
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7359-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "7359-02.htm";
				}
				else
				{
					htmltext = "7359-03.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7359-05.htm";
				}
				else
				{
					htmltext = "7359-06.htm";
					st.takeItems(BONE_FRAGMENT, -1);
					st.rewardItems(Inventory.ADENA_ID, 8470);
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
		
		if (st.dropItems(BONE_FRAGMENT, 1, 10, npc.getId() == 517 ? 180000 : 200000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
