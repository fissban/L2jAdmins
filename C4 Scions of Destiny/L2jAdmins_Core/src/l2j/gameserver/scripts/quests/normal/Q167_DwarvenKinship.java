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
public class Q167_DwarvenKinship extends Script
{
	// ITEMs
	private static final int CARLON_LETTER = 1076;
	private static final int NORMAN_LETTER = 1106;
	// NPCs
	private static final int COLLETE = 7350;
	private static final int NORMAN = 7210;
	private static final int HAPROCK = 7255;
	
	public Q167_DwarvenKinship()
	{
		super(167, "Dwarven Kinship");
		
		registerItems(CARLON_LETTER, NORMAN_LETTER);
		
		addStartNpc(COLLETE);
		addTalkId(COLLETE, HAPROCK, NORMAN);
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
		
		if (event.equalsIgnoreCase("7350-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(CARLON_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7255-03.htm"))
		{
			st.set("cond", "2");
			st.takeItems(CARLON_LETTER, 1);
			st.giveItems(NORMAN_LETTER, 1);
			st.rewardItems(Inventory.ADENA_ID, 2000);
		}
		else if (event.equalsIgnoreCase("7255-04.htm"))
		{
			st.takeItems(CARLON_LETTER, 1);
			st.rewardItems(Inventory.ADENA_ID, 3000);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false);
		}
		else if (event.equalsIgnoreCase("7210-02.htm"))
		{
			st.takeItems(NORMAN_LETTER, 1);
			st.rewardItems(Inventory.ADENA_ID, 20000);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false);
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
				htmltext = player.getLevel() < 15 ? "7350-02.htm" : "7350-03.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case COLLETE:
						if (cond == 1)
						{
							htmltext = "7350-05.htm";
						}
						break;
					
					case HAPROCK:
						if (cond == 1)
						{
							htmltext = "7255-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7255-05.htm";
						}
						break;
					
					case NORMAN:
						if (cond == 2)
						{
							htmltext = "7210-01.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
}
