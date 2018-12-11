package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class Q294_CovertBusiness extends Script
{
	// NPCs
	private static final int KEEF = 7534;
	// MOBs
	private static final int BARDED = 370;
	private static final int BLADE_BAT = 480;
	// ITEMs
	private static final int BAT_FANG = 1491;
	// REWARDs
	private static final int RING_OF_RACCOON = 1508;
	
	public Q294_CovertBusiness()
	{
		super(294, "Covert Business");
		
		registerItems(BAT_FANG);
		
		addStartNpc(KEEF);
		addTalkId(KEEF);
		addKillId(BARDED, BLADE_BAT);
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
		
		if (event.equalsIgnoreCase("7534-03.htm"))
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
				if (player.getRace() != Race.DWARF)
				{
					htmltext = "7534-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "7534-01.htm";
				}
				else
				{
					htmltext = "7534-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7534-04.htm";
				}
				else
				{
					htmltext = "7534-05.htm";
					st.takeItems(BAT_FANG, -1);
					st.giveItems(RING_OF_RACCOON, 1);
					st.rewardExpAndSp(0, 600);
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
		
		int count = 1;
		final int chance = Rnd.get(10);
		final boolean isBarded = npc.getId() == BARDED;
		
		if (chance < 3)
		{
			count++;
		}
		else if (chance < (isBarded ? 5 : 6))
		{
			count += 2;
		}
		else if (isBarded && (chance < 7))
		{
			count += 3;
		}
		
		if (st.dropItemsAlways(BAT_FANG, count, 100))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
