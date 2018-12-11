package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 * @author Reynald0
 */
public class Q003_WillTheSealBeBroken extends Script
{
	// NPC
	private static final int TALLOTH = 7141;
	// ITEMs
	private static final int ONYX_BEAST_EYE = 1081;
	private static final int TAINT_STONE = 1082;
	private static final int SUCCUBU_BLOOD = 1083;
	// MOBs
	private static final int OMEN_BEAST = 31;
	private static final int TAINTED_ZOMBIE = 41;
	private static final int STINK_ZOMBIE = 46;
	private static final int LESSER_SUCCUBUS = 48;
	private static final int LESSER_SUCCUBUS_TUREN = 52;
	private static final int LESSER_SUCCUBUS_TILFO = 57;
	// REWARD
	private static final int SCROLL_ENCHANT_ARMOR_D = 956;
	
	public Q003_WillTheSealBeBroken()
	{
		super(3, "Will the Seal be Broken?");
		addStartNpc(TALLOTH);
		addTalkId(TALLOTH);
		addKillId(OMEN_BEAST, TAINTED_ZOMBIE, STINK_ZOMBIE, LESSER_SUCCUBUS, LESSER_SUCCUBUS_TUREN, LESSER_SUCCUBUS_TILFO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		// TALLOTH
		if (event.equalsIgnoreCase("7141-03.htm"))
		{
			st.startQuest();
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
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
					htmltext = "7141-00.htm";
				}
				else if (player.getLevel() < 16)
				{
					htmltext = "7141-01.htm";
				}
				else
				{
					htmltext = "7141-02.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "7141-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "7141-06.htm";
					st.takeItems(ONYX_BEAST_EYE, 1);
					st.takeItems(SUCCUBU_BLOOD, 1);
					st.takeItems(TAINT_STONE, 1);
					st.giveItems(SCROLL_ENCHANT_ARMOR_D, 1);
					st.exitQuest(false, true);
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = checkPlayerCondition(killer, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case OMEN_BEAST:
				if (st.dropItems(ONYX_BEAST_EYE, 1, 1, 300000) && st.hasItems(TAINT_STONE, SUCCUBU_BLOOD))
				{
					st.setCond(2);
				}
				break;
			
			case TAINTED_ZOMBIE:
			case STINK_ZOMBIE:
				if (st.dropItems(TAINT_STONE, 1, 1, 300000) && st.hasItems(ONYX_BEAST_EYE, SUCCUBU_BLOOD))
				{
					st.setCond(2);
				}
				break;
			
			case LESSER_SUCCUBUS:
			case LESSER_SUCCUBUS_TILFO:
			case LESSER_SUCCUBUS_TUREN:
				if (st.dropItems(SUCCUBU_BLOOD, 1, 1, 300000) && st.hasItems(ONYX_BEAST_EYE, TAINT_STONE))
				{
					st.setCond(2);
				}
				break;
		}
		return null;
	}
}
