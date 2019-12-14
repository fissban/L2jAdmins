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
public class Q159_ProtectTheWaterSource extends Script
{
	// NPCs
	private static final int ASTERIOS = 7154;
	// MOBs
	private static final int PLAGUE_ZOMBIE = 5017;
	// ITEMs
	private static final int PLAGUE_DUST = 1035;
	private static final int HYACINTH_CHARM_1 = 1071;
	private static final int HYACINTH_CHARM_2 = 1072;
	
	public Q159_ProtectTheWaterSource()
	{
		super(159, "Protect the Water Source");
		
		registerItems(PLAGUE_DUST, HYACINTH_CHARM_1, HYACINTH_CHARM_2);
		
		addStartNpc(ASTERIOS);
		addTalkId(ASTERIOS);
		addKillId(PLAGUE_ZOMBIE);
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
		
		if (event.equalsIgnoreCase("7154-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(HYACINTH_CHARM_1, 1);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7154-00.htm";
				}
				else if (player.getLevel() < 12)
				{
					htmltext = "7154-02.htm";
				}
				else
				{
					htmltext = "7154-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "7154-05.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7154-06.htm";
					st.set("cond", "3");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.takeItems(PLAGUE_DUST, -1);
					st.takeItems(HYACINTH_CHARM_1, 1);
					st.giveItems(HYACINTH_CHARM_2, 1);
				}
				else if (cond == 3)
				{
					htmltext = "7154-07.htm";
				}
				else if (cond == 4)
				{
					htmltext = "7154-08.htm";
					st.takeItems(HYACINTH_CHARM_2, 1);
					st.takeItems(PLAGUE_DUST, -1);
					st.rewardItems(Inventory.ADENA_ID, 18250);
					st.playSound(PlaySoundType.QUEST_FINISH);
					st.exitQuest(false);
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
		
		if ((st.getInt("cond") == 1) && st.dropItems(PLAGUE_DUST, 1, 1, 400000))
		{
			st.set("cond", "2");
		}
		else if ((st.getInt("cond") == 3) && st.dropItems(PLAGUE_DUST, 1, 5, 400000))
		{
			st.set("cond", "4");
		}
		
		return null;
	}
	
}
