package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
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
public class Q258_BringWolfPelts extends Script
{
	// NPCs
	private static final int LECTOR = 7001;
	// MOBs
	private static final int WOLF = 120;
	private static final int ELDER_WOLF = 442;
	// ITEMs
	private static final int WOLF_PELT = 702;
	// REWARDs
	private static final int COTTON_SHIRT = 390;
	private static final int LEATHER_PANTS = 29;
	private static final int LEATHER_SHIRT = 22;
	private static final int SHORT_LEATHER_GLOVES = 1119;
	private static final int TUNIC = 426;
	
	public Q258_BringWolfPelts()
	{
		super(258, "Bring Wolf Pelts");
		
		registerItems(WOLF_PELT);
		
		addStartNpc(LECTOR);
		addTalkId(LECTOR);
		addKillId(WOLF, ELDER_WOLF);
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
		
		if (event.equalsIgnoreCase("7001-03.htm"))
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
		final ScriptState st = player.getScriptState(getName());
		String htmltext = getNoQuestMsg();
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = player.getLevel() < 3 ? "7001-01.htm" : "7001-02.htm";
				break;
			
			case STARTED:
				if (st.getItemsCount(WOLF_PELT) < 40)
				{
					htmltext = "7001-05.htm";
				}
				else
				{
					st.takeItems(WOLF_PELT, -1);
					final int randomNumber = Rnd.get(16);
					
					// Reward is based on a random number (1D16).
					if (randomNumber == 0)
					{
						st.giveItems(COTTON_SHIRT, 1);
					}
					else if (randomNumber < 6)
					{
						st.giveItems(LEATHER_PANTS, 1);
					}
					else if (randomNumber < 9)
					{
						st.giveItems(LEATHER_SHIRT, 1);
					}
					else if (randomNumber < 13)
					{
						st.giveItems(SHORT_LEATHER_GLOVES, 1);
					}
					else
					{
						st.giveItems(TUNIC, 1);
					}
					
					htmltext = "7001-06.htm";
					
					if (randomNumber == 0)
					{
						st.playSound(PlaySoundType.QUEST_JACKPOT);
					}
					else
					{
						st.playSound(PlaySoundType.QUEST_FINISH);
					}
					
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
		
		if (st.dropItemsAlways(WOLF_PELT, 1, 40))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
