package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q265_ChainsOfSlavery extends Script
{
	// NPCs
	private static final int KRISTIN = 7357;
	// MOBs
	private static final int IMP = 4;
	private static final int IMP_ELDER = 5;
	// ITEMs
	private static final int SHACKLE = 1368;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q265_ChainsOfSlavery()
	{
		super(265, "Chains of Slavery");
		
		registerItems(SHACKLE);
		
		addStartNpc(KRISTIN);
		addTalkId(KRISTIN);
		
		addKillId(IMP, IMP_ELDER);
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
		
		if (event.equalsIgnoreCase("7357-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7357-06.htm"))
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7357-00.htm";
				}
				else if (player.getLevel() < 6)
				{
					htmltext = "7357-01.htm";
				}
				else
				{
					htmltext = "7357-02.htm";
				}
				break;
			
			case STARTED:
				final int shackles = st.getItemsCount(SHACKLE);
				if (shackles == 0)
				{
					htmltext = "7357-04.htm";
				}
				else
				{
					int reward = 12 * shackles;
					if (shackles > 10)
					{
						reward += 500;
					}
					
					htmltext = "7357-05.htm";
					st.takeItems(SHACKLE, -1);
					st.rewardItems(Inventory.ADENA_ID, reward);
					
					if (player.isNewbie() && (st.getInt("Reward") == 0))
					{
						st.showQuestionMark(26);
						st.set("Reward", "1");
						
						if (player.isMageClass())
						{
							st.playSound("tutorial_voice_027");
							st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
						}
						else
						{
							st.playSound("tutorial_voice_026");
							st.giveItems(SOULSHOT_FOR_BEGINNERS, 6000);
						}
					}
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
		
		st.dropItems(SHACKLE, 1, 0, npc.getId() == IMP ? 500000 : 600000);
		
		return null;
	}
	
}
