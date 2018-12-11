package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi The onKill section of that quest is directly written on Q611.
 * @originalQuest aCis
 */
public class Q612_WarWithKetraOrcs extends Script
{
	// Items
	private static final int NEPENTHES_SEED = 7187;
	private static final int MOLAR_OF_KETRA_ORC = 7234;
	
	// Npcs
	
	private static final int ASHAS_VARKA_DURAI = 8377;
	
	public Q612_WarWithKetraOrcs()
	{
		super(612, "War with Ketra Orcs");
		
		registerItems(MOLAR_OF_KETRA_ORC);
		
		addStartNpc(ASHAS_VARKA_DURAI); // Ashas Varka Durai
		addTalkId(ASHAS_VARKA_DURAI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8377-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8377-07.htm"))
		{
			if (st.getItemsCount(MOLAR_OF_KETRA_ORC) >= 100)
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.takeItems(MOLAR_OF_KETRA_ORC, 100);
				st.giveItems(NEPENTHES_SEED, 20);
			}
			else
			{
				htmltext = "8377-08.htm";
			}
		}
		else if (event.equalsIgnoreCase("8377-09.htm"))
		{
			st.takeItems(MOLAR_OF_KETRA_ORC, -1);
			st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = ((player.getLevel() >= 74) && player.isAlliedWithVarka()) ? "8377-01.htm" : "8377-02.htm";
				break;
			
			case STARTED:
				htmltext = (st.hasItems(MOLAR_OF_KETRA_ORC)) ? "8377-04.htm" : "8377-05.htm";
				break;
		}
		
		return htmltext;
	}
	
}
