package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q247_PossessorOfAPreciousSoul extends Script
{
	// NPCs
	private static final int CARADINE = 8740;
	private static final int LADY_OF_THE_LAKE = 8745;
	// ITEMs
	private static final int CARADINE_LETTER = 7679;
	private static final int NOBLESS_TIARA = 7694;
	
	public Q247_PossessorOfAPreciousSoul()
	{
		super(247, "Possessor of a Precious Soul - 4");
		
		addStartNpc(CARADINE);
		addTalkId(CARADINE, LADY_OF_THE_LAKE);
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
		
		// Caradine
		if (event.equalsIgnoreCase("8740-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.takeItems(CARADINE_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("8740-05.htm"))
		{
			st.set("cond", "2");
			player.teleToLocation(143209, 43968, -3038);
		}
		// Lady of the lake
		else if (event.equalsIgnoreCase("8745-05.htm"))
		{
			player.setNoble(true);
			st.giveItems(NOBLESS_TIARA, 1);
			st.rewardExpAndSp(93836, 0);
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false);
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
				if (st.hasItems(CARADINE_LETTER))
				{
					htmltext = !player.isSubClassActive() || (player.getLevel() < 75) ? "8740-02.htm" : "8740-01.htm";
				}
				break;
			
			case STARTED:
				if (!player.isSubClassActive())
				{
					break;
				}
				
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case CARADINE:
						if (cond == 1)
						{
							htmltext = "8740-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8740-06.htm";
						}
						break;
					
					case LADY_OF_THE_LAKE:
						if (cond == 2)
						{
							htmltext = player.getLevel() < 75 ? "8745-06.htm" : "8745-01.htm";
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
