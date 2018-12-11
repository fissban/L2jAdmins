package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q259_RanchersPlea extends Script
{
	// NPCs
	private static final int EDMOND = 7497;
	private static final int MARIUS = 7405;
	// MOBs
	private static final int GIANT_SPIDER = 103;
	private static final int TALON_SPIDER = 106;
	private static final int BLADE_SPIDER = 108;
	// ITEMs
	private static final int GIANT_SPIDER_SKIN = 1495;
	// REWARDs
	private static final int ADENA = 57;
	private static final int HEALING_POTION = 1061;
	private static final int WOODEN_ARROW = 17;
	
	public Q259_RanchersPlea()
	{
		super(259, "Rancher's Plea");
		
		registerItems(GIANT_SPIDER_SKIN);
		
		addStartNpc(EDMOND);
		addTalkId(EDMOND, MARIUS);
		addKillId(GIANT_SPIDER, TALON_SPIDER, BLADE_SPIDER);
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
		
		if (event.equalsIgnoreCase("7497-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7497-06.htm"))
		{
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("7405-04.htm"))
		{
			if (st.getItemsCount(GIANT_SPIDER_SKIN) >= 10)
			{
				st.takeItems(GIANT_SPIDER_SKIN, 10);
				st.rewardItems(HEALING_POTION, 1);
			}
			else
			{
				htmltext = "<html><body>Incorrect item count</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("7405-05.htm"))
		{
			if (st.getItemsCount(GIANT_SPIDER_SKIN) >= 10)
			{
				st.takeItems(GIANT_SPIDER_SKIN, 10);
				st.rewardItems(WOODEN_ARROW, 50);
			}
			else
			{
				htmltext = "<html><body>Incorrect item count</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("7405-07.htm"))
		{
			if (st.getItemsCount(GIANT_SPIDER_SKIN) >= 10)
			{
				htmltext = "7405-06.htm";
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
				htmltext = player.getLevel() < 15 ? "7497-01.htm" : "7497-02.htm";
				break;
			
			case STARTED:
				final int count = st.getItemsCount(GIANT_SPIDER_SKIN);
				switch (npc.getId())
				{
					case EDMOND:
						if (count == 0)
						{
							htmltext = "7497-04.htm";
						}
						else
						{
							htmltext = "7497-05.htm";
							st.takeItems(GIANT_SPIDER_SKIN, -1);
							st.rewardItems(ADENA, (count >= 10 ? 250 : 0) + (count * 25));
						}
						break;
					
					case MARIUS:
						htmltext = count < 10 ? "7405-01.htm" : "7405-02.htm";
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
		
		st.dropItemsAlways(GIANT_SPIDER_SKIN, 1, 0);
		
		return null;
	}
	
}
