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
public class Q306_CrystalsOfFireAndIce extends Script
{
	// ITEMs
	private static final int FLAME_SHARD = 1020;
	private static final int ICE_SHARD = 1021;
	
	// Droplist (npcId, itemId, chance)
	private static final int[][] DROPLIST =
	{
		{
			109,
			FLAME_SHARD,
			300000
		},
		{
			110,
			ICE_SHARD,
			300000
		},
		{
			112,
			FLAME_SHARD,
			400000
		},
		{
			113,
			ICE_SHARD,
			400000
		},
		{
			114,
			FLAME_SHARD,
			500000
		},
		{
			115,
			ICE_SHARD,
			500000
		}
	};
	
	public Q306_CrystalsOfFireAndIce()
	{
		super(306, "Crystals of Fire and Ice");
		
		registerItems(FLAME_SHARD, ICE_SHARD);
		
		addStartNpc(7004); // Katerina
		addTalkId(7004);
		
		addKillId(109, 110, 112, 113, 114, 115);
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
		
		if (event.equalsIgnoreCase("7004-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7004-06.htm"))
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
				htmltext = player.getLevel() < 17 ? "7004-01.htm" : "7004-02.htm";
				break;
			
			case STARTED:
				final int totalItems = st.getItemsCount(FLAME_SHARD) + st.getItemsCount(ICE_SHARD);
				if (totalItems == 0)
				{
					htmltext = "7004-04.htm";
				}
				else
				{
					htmltext = "7004-05.htm";
					st.takeItems(FLAME_SHARD, -1);
					st.takeItems(ICE_SHARD, -1);
					st.rewardItems(Inventory.ADENA_ID, (30 * totalItems) + (totalItems > 10 ? 5000 : 0));
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
		
		for (final int[] drop : DROPLIST)
		{
			if (npc.getId() == drop[0])
			{
				st.dropItems(drop[1], 1, 0, drop[2]);
				break;
			}
		}
		
		return null;
	}
	
}
