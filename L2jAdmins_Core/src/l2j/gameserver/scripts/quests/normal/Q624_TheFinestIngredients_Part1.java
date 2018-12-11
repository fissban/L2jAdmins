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
public class Q624_TheFinestIngredients_Part1 extends Script
{
	// Mobs
	private static final int NEPENTHES = 1319;
	private static final int ATROX = 1321;
	private static final int ATROXSPAWN = 1317;
	private static final int BANDERSNATCH = 1314;
	
	// Items
	private static final int TRUNK_OF_NEPENTHES = 7202;
	private static final int FOOT_OF_BANDERSNATCHLING = 7203;
	private static final int SECRET_SPICE = 7204;
	
	// Rewards
	private static final int ICE_CRYSTAL = 7080;
	private static final int SOY_SAUCE_JAR = 7205;
	
	public Q624_TheFinestIngredients_Part1()
	{
		super(624, "The Finest Ingredients - Part 1");
		
		registerItems(TRUNK_OF_NEPENTHES, FOOT_OF_BANDERSNATCHLING, SECRET_SPICE);
		
		addStartNpc(8521); // Jeremy
		addTalkId(8521);
		
		addKillId(NEPENTHES, ATROX, ATROXSPAWN, BANDERSNATCH);
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
		
		if (event.equalsIgnoreCase("8521-02.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8521-05.htm"))
		{
			if ((st.getItemsCount(TRUNK_OF_NEPENTHES) >= 50) && (st.getItemsCount(FOOT_OF_BANDERSNATCHLING) >= 50) && (st.getItemsCount(SECRET_SPICE) >= 50))
			{
				st.takeItems(TRUNK_OF_NEPENTHES, -1);
				st.takeItems(FOOT_OF_BANDERSNATCHLING, -1);
				st.takeItems(SECRET_SPICE, -1);
				st.giveItems(ICE_CRYSTAL, 1);
				st.giveItems(SOY_SAUCE_JAR, 1);
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
			else
			{
				st.set("cond", "1");
				htmltext = "8521-07.htm";
			}
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
				htmltext = (player.getLevel() < 73) ? "8521-03.htm" : "8521-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				if (cond == 1)
				{
					htmltext = "8521-06.htm";
				}
				else if (cond == 2)
				{
					if ((st.getItemsCount(TRUNK_OF_NEPENTHES) >= 50) && (st.getItemsCount(FOOT_OF_BANDERSNATCHLING) >= 50) && (st.getItemsCount(SECRET_SPICE) >= 50))
					{
						htmltext = "8521-04.htm";
					}
					else
					{
						htmltext = "8521-07.htm";
					}
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = getRandomPartyMember(player, npc, "1");
		if (partyMember == null)
		{
			return null;
		}
		
		ScriptState st = partyMember.getScriptState(getName());
		
		switch (npc.getId())
		{
			case NEPENTHES:
				if (st.dropItemsAlways(TRUNK_OF_NEPENTHES, 1, 50) && (st.getItemsCount(FOOT_OF_BANDERSNATCHLING) >= 50) && (st.getItemsCount(SECRET_SPICE) >= 50))
				{
					st.set("cond", "2");
				}
				break;
			
			case ATROX:
			case ATROXSPAWN:
				if (st.dropItemsAlways(SECRET_SPICE, 1, 50) && (st.getItemsCount(TRUNK_OF_NEPENTHES) >= 50) && (st.getItemsCount(FOOT_OF_BANDERSNATCHLING) >= 50))
				{
					st.set("cond", "2");
				}
				break;
			
			case BANDERSNATCH:
				if (st.dropItemsAlways(FOOT_OF_BANDERSNATCHLING, 1, 50) && (st.getItemsCount(TRUNK_OF_NEPENTHES) >= 50) && (st.getItemsCount(SECRET_SPICE) >= 50))
				{
					st.set("cond", "2");
				}
				break;
		}
		
		return null;
	}
	
}
