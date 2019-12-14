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
public class Q164_BloodFiend extends Script
{
	// NPCs
	private static final int CREAMEES = 7149;
	// MOBs
	private static final int KIRUNAK = 5021;
	// ITEMs
	private static final int KIRUNAK_SKULL = 1044;
	
	public Q164_BloodFiend()
	{
		super(164, "Blood Fiend");
		
		registerItems(KIRUNAK_SKULL);
		
		addStartNpc(CREAMEES);
		addTalkId(CREAMEES);
		
		addKillId(KIRUNAK);
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
		
		if (event.equalsIgnoreCase("7149-04.htm"))
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
				if (player.getRace() == Race.DARK_ELF)
				{
					htmltext = "7149-00.htm";
				}
				else if (player.getLevel() < 21)
				{
					htmltext = "7149-02.htm";
				}
				else
				{
					htmltext = "7149-03.htm";
				}
				break;
			
			case STARTED:
				if (st.hasItems(KIRUNAK_SKULL))
				{
					htmltext = "7149-06.htm";
					st.takeItems(KIRUNAK_SKULL, 1);
					st.rewardItems(Inventory.ADENA_ID, 42130);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
				}
				else
				{
					htmltext = "7149-05.htm";
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
		st.giveItems(KIRUNAK_SKULL, 1);
		
		return null;
	}
	
}
