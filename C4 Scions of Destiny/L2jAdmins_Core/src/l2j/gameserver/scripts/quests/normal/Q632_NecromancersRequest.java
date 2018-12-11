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
public class Q632_NecromancersRequest extends Script
{
	// Monsters
	private static final int[] VAMPIRES =
	{
		1568,
		1573,
		1582,
		1585,
		1586,
		1587,
		1588,
		1588,
		1590,
		1590,
		1590,
		1593,
		1593,
		1593
	};
	
	private static final int[] UNDEADS =
	{
		1547,
		1548,
		1549,
		1551,
		1551,
		1555,
		1555,
		1562,
		1571,
		1576,
		1576,
		1579
	};
	
	// Items
	private static final int VAMPIRE_HEART = 7542;
	private static final int ZOMBIE_BRAIN = 7543;
	
	public Q632_NecromancersRequest()
	{
		super(632, "Necromancer's Request");
		
		registerItems(VAMPIRE_HEART, ZOMBIE_BRAIN);
		
		addStartNpc(8522); // Mysterious Wizard
		addTalkId(8522);
		
		addKillId(VAMPIRES);
		addKillId(UNDEADS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8522-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8522-06.htm"))
		{
			if (st.getItemsCount(VAMPIRE_HEART) >= 200)
			{
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(VAMPIRE_HEART, -1);
				st.rewardItems(Inventory.ADENA_ID, 120000);
			}
			else
			{
				htmltext = "8522-09.htm";
			}
		}
		else if (event.equalsIgnoreCase("8522-08.htm"))
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
				htmltext = player.getLevel() < 63 ? "8522-01.htm" : "8522-02.htm";
				break;
			
			case STARTED:
				htmltext = st.getItemsCount(VAMPIRE_HEART) >= 200 ? "8522-05.htm" : "8522-04.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final L2PcInstance partyMember = getRandomPartyMemberState(player, npc, ScriptStateType.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		final ScriptState st = partyMember.getScriptState(getName());
		
		for (final int undead : UNDEADS)
		{
			if (undead == npc.getId())
			{
				st.dropItems(ZOMBIE_BRAIN, 1, 0, 330000);
				return null;
			}
		}
		
		if ((st.getInt("cond") == 1) && st.dropItems(VAMPIRE_HEART, 1, 200, 500000))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
