package l2j.gameserver.scripts.quests.normal;

import java.util.HashMap;
import java.util.Map;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q328_SenseForBusiness extends Script
{
	// ITEMs
	private static final int MONSTER_EYE_LENS = 1366;
	private static final int MONSTER_EYE_CARCASS = 1347;
	private static final int BASILISK_GIZZARD = 1348;
	
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	
	{
		CHANCES.put(55, 48);
		CHANCES.put(59, 52);
		CHANCES.put(67, 68);
		CHANCES.put(68, 76);
		CHANCES.put(70, 500000);
		CHANCES.put(72, 510000);
	}
	
	public Q328_SenseForBusiness()
	{
		super(328, "Sense for Business");
		
		registerItems(MONSTER_EYE_LENS, MONSTER_EYE_CARCASS, BASILISK_GIZZARD);
		
		addStartNpc(7436); // Sarien
		addTalkId(7436);
		
		addKillId(55, 59, 67, 68, 70, 72);
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
		
		if (event.equalsIgnoreCase("7436-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7436-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
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
				htmltext = player.getLevel() < 21 ? "7436-01.htm" : "7436-02.htm";
				break;
			
			case STARTED:
				final int carcasses = st.getItemsCount(MONSTER_EYE_CARCASS);
				final int lenses = st.getItemsCount(MONSTER_EYE_LENS);
				final int gizzards = st.getItemsCount(BASILISK_GIZZARD);
				
				final int all = carcasses + lenses + gizzards;
				
				if (all == 0)
				{
					htmltext = "7436-04.htm";
				}
				else
				{
					htmltext = "7436-05.htm";
					st.takeItems(MONSTER_EYE_CARCASS, -1);
					st.takeItems(MONSTER_EYE_LENS, -1);
					st.takeItems(BASILISK_GIZZARD, -1);
					st.rewardItems(Inventory.ADENA_ID, (25 * carcasses) + (1000 * lenses) + (60 * gizzards) + (all >= 10 ? 618 : 0));
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
		
		final int npcId = npc.getId();
		final int chance = CHANCES.get(npcId);
		
		if (npcId < 69)
		{
			final int rnd = Rnd.get(100);
			if (rnd < (chance + 1))
			{
				st.dropItemsAlways(rnd < chance ? MONSTER_EYE_CARCASS : MONSTER_EYE_LENS, 1, 0);
			}
		}
		else
		{
			st.dropItems(BASILISK_GIZZARD, 1, 0, chance);
		}
		
		return null;
	}
	
}
