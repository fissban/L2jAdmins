package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q169_OffspringOfNightmares extends Script
{
	// NPCs
	private static final int VLASTY = 7145;
	// MOBs
	private static final int LESSER_DARK_HORROR = 25;
	private static final int DARK_HORROR = 105;
	// ITEMs
	private static final int CRACKED_SKULL = 1030;
	private static final int PERFECT_SKULL = 1031;
	private static final int BONE_GAITERS = 31;
	
	public Q169_OffspringOfNightmares()
	{
		super(169, "Offspring of Nightmares");
		
		registerItems(CRACKED_SKULL, PERFECT_SKULL);
		
		addStartNpc(VLASTY);
		addTalkId(VLASTY);
		addKillId(LESSER_DARK_HORROR, DARK_HORROR);
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
		
		if (event.equalsIgnoreCase("7145-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7145-08.htm"))
		{
			final int reward = 17000 + (st.getItemsCount(CRACKED_SKULL) * 20);
			st.takeItems(PERFECT_SKULL, -1);
			st.takeItems(CRACKED_SKULL, -1);
			st.giveItems(BONE_GAITERS, 1);
			st.rewardItems(Inventory.ADENA_ID, reward);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7145-00.htm";
				}
				else if (player.getLevel() < 15)
				{
					htmltext = "7145-02.htm";
				}
				else
				{
					htmltext = "7145-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					if (st.hasItems(CRACKED_SKULL))
					{
						htmltext = "7145-06.htm";
					}
					else
					{
						htmltext = "7145-05.htm";
					}
				}
				else if (cond == 2)
				{
					htmltext = "7145-07.htm";
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		if ((st.getInt("cond") == 1) && st.dropItems(PERFECT_SKULL, 1, 1, 200000))
		{
			st.set("cond", "2");
		}
		else
		{
			st.dropItems(CRACKED_SKULL, 1, 0, 500000);
		}
		
		return null;
	}
	
}
