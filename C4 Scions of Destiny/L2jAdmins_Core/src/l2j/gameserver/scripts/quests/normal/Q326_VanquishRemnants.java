package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
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
public class Q326_VanquishRemnants extends Script
{
	// ITEMs
	private static final int RED_CROSS_BADGE = 1359;
	private static final int BLUE_CROSS_BADGE = 1360;
	private static final int BLACK_CROSS_BADGE = 1361;
	
	// Reward
	private static final int BLACK_LION_MARK = 1369;
	
	public Q326_VanquishRemnants()
	{
		super(326, "Vanquish Remnants");
		
		registerItems(RED_CROSS_BADGE, BLUE_CROSS_BADGE, BLACK_CROSS_BADGE);
		
		addStartNpc(7435); // Leopold
		addTalkId(7435);
		
		addKillId(53, 437, 58, 436, 61, 439, 63, 66, 438);
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
		
		if (event.equalsIgnoreCase("7435-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7435-07.htm"))
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
				htmltext = player.getLevel() < 21 ? "7435-01.htm" : "7435-02.htm";
				break;
			
			case STARTED:
				final int redBadges = st.getItemsCount(RED_CROSS_BADGE);
				final int blueBadges = st.getItemsCount(BLUE_CROSS_BADGE);
				final int blackBadges = st.getItemsCount(BLACK_CROSS_BADGE);
				
				final int badgesSum = redBadges + blueBadges + blackBadges;
				
				if (badgesSum > 0)
				{
					st.takeItems(RED_CROSS_BADGE, -1);
					st.takeItems(BLUE_CROSS_BADGE, -1);
					st.takeItems(BLACK_CROSS_BADGE, -1);
					st.rewardItems(Inventory.ADENA_ID, (redBadges * 46) + (blueBadges * 52) + (blackBadges * 58) + (badgesSum >= 10 ? 4320 : 0));
					
					if (badgesSum >= 100)
					{
						if (!st.hasItems(BLACK_LION_MARK))
						{
							htmltext = "7435-06.htm";
							st.giveItems(BLACK_LION_MARK, 1);
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
						else
						{
							htmltext = "7435-09.htm";
						}
					}
					else
					{
						htmltext = "7435-05.htm";
					}
				}
				else
				{
					htmltext = "7435-04.htm";
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
			case 53:
			case 437:
			case 58:
				st.dropItems(RED_CROSS_BADGE, 1, 0, 330000);
				break;
			
			case 436:
			case 61:
			case 439:
			case 63:
				st.dropItems(BLUE_CROSS_BADGE, 1, 0, 160000);
				break;
			
			case 66:
			case 438:
				st.dropItems(BLACK_CROSS_BADGE, 1, 0, 120000);
				break;
		}
		
		return null;
	}
	
}
