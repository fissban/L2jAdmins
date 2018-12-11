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
public class Q316_DestroyPlagueCarriers extends Script
{
	// ITEMs
	private static final int WERERAT_FANG = 1042;
	private static final int VAROOL_FOULCLAW_FANG = 1043;
	
	// Monsters
	private static final int SUKAR_WERERAT = 40;
	private static final int SUKAR_WERERAT_LEADER = 47;
	private static final int VAROOL_FOULCLAW = 5020;
	
	public Q316_DestroyPlagueCarriers()
	{
		super(316, "Destroy Plague Carriers");
		
		registerItems(WERERAT_FANG, VAROOL_FOULCLAW_FANG);
		
		addStartNpc(7155); // Ellenia
		addTalkId(7155);
		
		addKillId(SUKAR_WERERAT, SUKAR_WERERAT_LEADER, VAROOL_FOULCLAW);
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
		
		if (event.equalsIgnoreCase("7155-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7155-08.htm"))
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7155-00.htm";
				}
				else if (player.getLevel() < 18)
				{
					htmltext = "7155-02.htm";
				}
				else
				{
					htmltext = "7155-03.htm";
				}
				break;
			
			case STARTED:
				final int ratFangs = st.getItemsCount(WERERAT_FANG);
				final int varoolFangs = st.getItemsCount(VAROOL_FOULCLAW_FANG);
				
				if ((ratFangs + varoolFangs) == 0)
				{
					htmltext = "7155-05.htm";
				}
				else
				{
					htmltext = "7155-07.htm";
					st.takeItems(WERERAT_FANG, -1);
					st.takeItems(VAROOL_FOULCLAW_FANG, -1);
					st.rewardItems(Inventory.ADENA_ID, (ratFangs * 30) + (varoolFangs * 10000) + (ratFangs > 10 ? 5000 : 0));
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
		
		switch (npc.getId())
		{
			case SUKAR_WERERAT:
			case SUKAR_WERERAT_LEADER:
				st.dropItems(WERERAT_FANG, 1, 0, 400000);
				break;
			
			case VAROOL_FOULCLAW:
				st.dropItems(VAROOL_FOULCLAW_FANG, 1, 1, 200000);
				break;
		}
		
		return null;
	}
	
}
