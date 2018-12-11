package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB, CaFi, zarie
 * @originalQuest aCis
 */
public class Q381_LetsBecomeARoyalMember extends Script
{
	// NPCs
	private static final int SORINT = 7232;
	private static final int SANDRA = 7090;
	
	// Items
	private static final int KAIL_COIN = 5899;
	private static final int COIN_ALBUM = 5900;
	private static final int GOLDEN_CLOVER_COIN = 7569;
	private static final int COIN_COLLECTOR_MEMBERSHIP = 3813;
	
	// MOBs
	private static final int ANCIENT_GARGOYLE = 1018;
	private static final int FALLEN_CHIEFTAIN_VEGUS = 5316;
	
	// Reward
	private static final int ROYAL_MEMBERSHIP = 5898;
	
	public Q381_LetsBecomeARoyalMember()
	{
		super(381, "Lets Become a Royal Member!");
		
		registerItems(KAIL_COIN, GOLDEN_CLOVER_COIN);
		
		addStartNpc(SORINT);
		addTalkId(SORINT, SANDRA);
		
		addKillId(ANCIENT_GARGOYLE, FALLEN_CHIEFTAIN_VEGUS);
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
		
		if (event.equalsIgnoreCase("7090-02.htm"))
		{
			st.set("aCond", "1"); // Alternative cond used for Sandra.
		}
		else if (event.equalsIgnoreCase("7232-03.htm"))
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
				htmltext = (player.getLevel() < 55) || !st.hasItems(COIN_COLLECTOR_MEMBERSHIP) ? "7232-02.htm" : "7232-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case SORINT:
						if (!st.hasItems(KAIL_COIN))
						{
							htmltext = "7232-04.htm";
						}
						else if (!st.hasItems(COIN_ALBUM))
						{
							htmltext = "7232-05.htm";
						}
						else
						{
							htmltext = "7232-06.htm";
							st.takeItems(KAIL_COIN, -1);
							st.takeItems(COIN_ALBUM, -1);
							st.giveItems(ROYAL_MEMBERSHIP, 1);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case SANDRA:
						if (!st.hasItems(COIN_ALBUM))
						{
							if (st.getInt("aCond") == 0)
							{
								htmltext = "7090-01.htm";
							}
							else
							{
								if (!st.hasItems(GOLDEN_CLOVER_COIN))
								{
									htmltext = "7090-03.htm";
								}
								else
								{
									htmltext = "7090-04.htm";
									st.takeItems(GOLDEN_CLOVER_COIN, -1);
									st.giveItems(COIN_ALBUM, 1);
								}
							}
						}
						else
						{
							htmltext = "7090-05.htm";
						}
						break;
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
		
		if ((npc.getId() == ANCIENT_GARGOYLE) && (st.getInt("cond") == 1))
		{
			st.dropItems(KAIL_COIN, 1, 1, 50000);
		}
		else if ((npc.getId() == FALLEN_CHIEFTAIN_VEGUS) && (st.getInt("aCond") == 1))
		{
			st.dropItemsAlways(GOLDEN_CLOVER_COIN, 1, 1);
		}
		
		return null;
	}
	
}
