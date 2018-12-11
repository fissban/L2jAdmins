package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q160_NerupasRequest extends Script
{
	// ITEMs
	private static final int SILVERY_SPIDERSILK = 1026;
	private static final int UNOREN_RECEIPT = 1027;
	private static final int CREAMEES_TICKET = 1028;
	private static final int NIGHTSHADE_LEAF = 1029;
	// REWEARDs
	private static final int LESSER_HEALING_POTION = 1060;
	// NPCs
	private static final int NERUPA = 7370;
	private static final int UNOREN = 7147;
	private static final int CREAMEES = 7149;
	private static final int JULIA = 7152;
	
	public Q160_NerupasRequest()
	{
		super(160, "Nerupa's Request");
		
		registerItems(SILVERY_SPIDERSILK, UNOREN_RECEIPT, CREAMEES_TICKET, NIGHTSHADE_LEAF);
		
		addStartNpc(NERUPA);
		addTalkId(NERUPA, UNOREN, CREAMEES, JULIA);
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
		
		if (event.equalsIgnoreCase("7370-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(SILVERY_SPIDERSILK, 1);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "7370-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "7370-02.htm";
				}
				else
				{
					htmltext = "7370-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case NERUPA:
						if (cond < 4)
						{
							htmltext = "7370-05.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7370-06.htm";
							st.takeItems(NIGHTSHADE_LEAF, 1);
							st.rewardItems(LESSER_HEALING_POTION, 5);
							st.rewardExpAndSp(1000, 0);
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case UNOREN:
						if (cond == 1)
						{
							htmltext = "7147-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SILVERY_SPIDERSILK, 1);
							st.giveItems(UNOREN_RECEIPT, 1);
						}
						else if (cond == 2)
						{
							htmltext = "7147-02.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7147-03.htm";
						}
						break;
					
					case CREAMEES:
						if (cond == 2)
						{
							htmltext = "7149-01.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(UNOREN_RECEIPT, 1);
							st.giveItems(CREAMEES_TICKET, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7149-02.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7149-03.htm";
						}
						break;
					
					case JULIA:
						if (cond == 3)
						{
							htmltext = "7152-01.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CREAMEES_TICKET, 1);
							st.giveItems(NIGHTSHADE_LEAF, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7152-02.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
}
