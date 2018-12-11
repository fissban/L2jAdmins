package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * MauroNOB: non-interlude npc is commented, but is not necessary to complete quest.
 * @author        Reynald0
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q618_IntoTheFlame extends Script
{
	// NPCs
	private static final int KLEIN = 8540;
	private static final int HILDA = 8271;
	
	// Monsters
	
	private static final int KOOKABURRA = 1274;
	private static final int BANDERSNATCH = 1282;
	private static final int GRENDEL = 1290;
	
	// Items
	private static final int VACUALITE_ORE = 7265;
	private static final int VACUALITE = 7266;
	
	// Reward
	private static final int FLOATING_STONE = 7267;
	
	public Q618_IntoTheFlame()
	{
		super(618, "Into The Flame");
		
		registerItems(VACUALITE_ORE, VACUALITE);
		
		addStartNpc(KLEIN);
		addTalkId(KLEIN, HILDA);
		
		// Kookaburras, Bandersnatches, Grendels
		addKillId(KOOKABURRA, BANDERSNATCH, GRENDEL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("8540-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8540-05.htm"))
		{
			st.takeItems(VACUALITE, 1);
			st.giveItems(FLOATING_STONE, 1);
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("8271-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8271-05.htm"))
		{
			st.set("cond", "4");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(VACUALITE_ORE, -1);
			st.giveItems(VACUALITE, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		ScriptState st = player.getScriptState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getLevel() < 60) ? "8540-01.htm" : "8540-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case KLEIN:
						htmltext = (cond == 4) ? "8540-04.htm" : "8540-03.htm";
						break;
					
					case HILDA:
						if (cond == 1)
						{
							htmltext = "8271-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8271-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8271-04.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8271-06.htm";
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
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "2");
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		if (st.dropItems(VACUALITE_ORE, 1, 50, 500000))
		{
			st.set("cond", "3");
		}
		
		return null;
	}
	
}
