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
public class Q276_TotemOfTheHestui extends Script
{
	// NPCs
	private static final int TANAPI = 7571;
	// MOBs
	private static final int KASHA_BEAR = 479;
	private static final int KASHA_BEAR_TOTEM_SPIRIT = 5044;
	// ITEMs
	private static final int KASHA_PARASITE = 1480;
	private static final int KASHA_CRYSTAL = 1481;
	// REWARDs
	private static final int HESTUI_TOTEM = 1500;
	private static final int LEATHER_PANTS = 29;
	
	public Q276_TotemOfTheHestui()
	{
		super(276, "Totem of the Hestui");
		
		registerItems(KASHA_PARASITE, KASHA_CRYSTAL);
		
		addStartNpc(TANAPI);
		addTalkId(TANAPI);
		
		addKillId(KASHA_BEAR, KASHA_BEAR_TOTEM_SPIRIT);
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
		
		if (event.equalsIgnoreCase("7571-03.htm"))
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7571-00.htm";
				}
				else if (player.getLevel() < 15)
				{
					htmltext = "7571-01.htm";
				}
				else
				{
					htmltext = "7571-02.htm";
				}
				break;
			
			case STARTED:
				if (st.getInt("cond") == 1)
				{
					htmltext = "7571-04.htm";
				}
				else
				{
					htmltext = "7571-05.htm";
					st.takeItems(KASHA_CRYSTAL, -1);
					st.takeItems(KASHA_PARASITE, -1);
					st.giveItems(HESTUI_TOTEM, 1);
					st.giveItems(LEATHER_PANTS, 1);
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
		
		if (!st.hasItems(KASHA_CRYSTAL))
		{
			switch (npc.getId())
			{
				case KASHA_BEAR:
					final int count = st.getItemsCount(KASHA_PARASITE);
					final int random = Rnd.get(100);
					
					if ((count >= 79) || ((count >= 69) && (random <= 20)) || ((count >= 59) && (random <= 15)) || ((count >= 49) && (random <= 10)) || ((count >= 39) && (random < 2)))
					{
						addSpawn(5044, npc, true, 0);
						st.takeItems(KASHA_PARASITE, count);
					}
					else
					{
						st.dropItemsAlways(KASHA_PARASITE, 1, 0);
					}
					break;
				
				case KASHA_BEAR_TOTEM_SPIRIT:
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
					st.giveItems(KASHA_CRYSTAL, 1);
					break;
			}
		}
		
		return null;
	}
	
}
