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
public class Q296_TarantulasSpiderSilk extends Script
{
	// NPCs
	private static final int MION = 7519;
	private static final int DEFENDER_NATHAN = 7548;
	
	// Quest Items
	private static final int TARANTULA_SPIDER_SILK = 1493;
	private static final int TARANTULA_SPINNERETTE = 1494;
	
	// ITEMs
	private static final int RING_OF_RACCOON = 1508;
	private static final int RING_OF_FIREFLY = 1509;
	
	public Q296_TarantulasSpiderSilk()
	{
		super(296, "Tarantula's Spider Silk");
		
		registerItems(TARANTULA_SPIDER_SILK, TARANTULA_SPINNERETTE);
		
		addStartNpc(MION);
		addTalkId(MION, DEFENDER_NATHAN);
		addKillId(394, 403, 508); // Crimson Tarantula, Hunter Tarantula, PlunderTarantula
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("7519-03.htm"))
		{
			if (st.hasAtLeastOneItem(RING_OF_RACCOON, RING_OF_FIREFLY))
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
			}
			else
			{
				htmltext = "7519-03a.htm";
			}
		}
		else if (event.equalsIgnoreCase("7519-06.htm"))
		{
			st.takeItems(TARANTULA_SPIDER_SILK, -1);
			st.takeItems(TARANTULA_SPINNERETTE, -1);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7548-02.htm"))
		{
			final int count = st.getItemsCount(TARANTULA_SPINNERETTE);
			if (count > 0)
			{
				htmltext = "7548-03.htm";
				st.takeItems(TARANTULA_SPINNERETTE, -1);
				st.giveItems(TARANTULA_SPIDER_SILK, count * (15 + Rnd.get(10)));
			}
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
				htmltext = player.getLevel() < 15 ? "7519-01.htm" : "7519-02.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case MION:
						final int count = st.getItemsCount(TARANTULA_SPIDER_SILK);
						if (count == 0)
						{
							htmltext = "7519-04.htm";
						}
						else
						{
							htmltext = "7519-05.htm";
							st.takeItems(TARANTULA_SPIDER_SILK, -1);
							st.rewardItems(Inventory.ADENA_ID, (count >= 10 ? 2000 : 0) + (count * 30));
						}
						break;
					
					case DEFENDER_NATHAN:
						htmltext = "7548-01.htm";
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
		
		final int rnd = Rnd.get(100);
		if (rnd > 95)
		{
			st.dropItemsAlways(TARANTULA_SPINNERETTE, 1, 0);
		}
		else if (rnd > 45)
		{
			st.dropItemsAlways(TARANTULA_SPIDER_SILK, 1, 0);
		}
		
		return null;
	}
	
}
