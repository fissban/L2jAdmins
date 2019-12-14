package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
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
public class Q264_KeenClaws extends Script
{
	// NPCs
	private static final int PAYNE = 7136;
	// MOBs
	private static final int GOBLIN = 3;
	private static final int WOLF = 456;
	// ITEMs
	private static final int WOLF_CLAW = 1367;
	// REWARDs
	private static final int LEATHER_SANDALS = 36;
	private static final int WOODEN_HELMET = 43;
	private static final int STOCKINGS = 462;
	private static final int HEALING_POTION = 1061;
	private static final int SHORT_GLOVES = 48;
	private static final int CLOTH_SHOES = 35;
	
	public Q264_KeenClaws()
	{
		super(264, "Keen Claws");
		
		registerItems(WOLF_CLAW);
		
		addStartNpc(PAYNE);
		addTalkId(PAYNE);
		addKillId(GOBLIN, WOLF);
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
		
		if (event.equalsIgnoreCase("7136-03.htm"))
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
				htmltext = player.getLevel() < 3 ? "7136-01.htm" : "7136-02.htm";
				break;
			
			case STARTED:
				final int count = st.getItemsCount(WOLF_CLAW);
				if (count < 50)
				{
					htmltext = "7136-04.htm";
				}
				else
				{
					htmltext = "7136-05.htm";
					st.takeItems(WOLF_CLAW, -1);
					
					final int n = Rnd.get(17);
					if (n == 0)
					{
						st.giveItems(WOODEN_HELMET, 1);
						st.playSound(PlaySoundType.QUEST_JACKPOT);
					}
					else if (n < 2)
					{
						st.giveItems(Inventory.ADENA_ID, 1000);
					}
					else if (n < 5)
					{
						st.giveItems(LEATHER_SANDALS, 1);
					}
					else if (n < 8)
					{
						st.giveItems(STOCKINGS, 1);
						st.giveItems(Inventory.ADENA_ID, 50);
					}
					else if (n < 11)
					{
						st.giveItems(HEALING_POTION, 1);
					}
					else if (n < 14)
					{
						st.giveItems(SHORT_GLOVES, 1);
					}
					else
					{
						st.giveItems(CLOTH_SHOES, 1);
					}
					
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
		
		if (npc.getId() == GOBLIN)
		{
			if (st.dropItems(WOLF_CLAW, Rnd.nextBoolean() ? 2 : 4, 50, 500000))
			{
				st.set("cond", "2");
			}
		}
		else if (st.dropItemsAlways(WOLF_CLAW, Rnd.get(5) < 4 ? 1 : 2, 50))
		{
			st.set("cond", "2");
		}
		
		return null;
	}
	
}
