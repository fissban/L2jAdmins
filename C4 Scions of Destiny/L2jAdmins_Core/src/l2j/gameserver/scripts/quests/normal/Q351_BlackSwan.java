package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
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
public class Q351_BlackSwan extends Script
{
	// NPCs
	private static final int GOSTA = 7916;
	private static final int IASON_HEINE = 7969;
	private static final int ROMAN = 7897;
	
	// ITEMs
	private static final int ORDER_OF_GOSTA = 4296;
	private static final int LIZARD_FANG = 4297;
	private static final int BARREL_OF_LEAGUE = 4298;
	private static final int BILL_OF_IASON_HEINE = 4310;
	
	public Q351_BlackSwan()
	{
		super(351, "Black Swan");
		
		registerItems(ORDER_OF_GOSTA, BARREL_OF_LEAGUE, LIZARD_FANG);
		
		addStartNpc(GOSTA);
		addTalkId(GOSTA, IASON_HEINE, ROMAN);
		
		addKillId(784, 785, 784, 785);
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
		
		if (event.equalsIgnoreCase("7916-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(ORDER_OF_GOSTA, 1);
		}
		else if (event.equalsIgnoreCase("7969-02a.htm"))
		{
			final int lizardFangs = st.getItemsCount(LIZARD_FANG);
			if (lizardFangs > 0)
			{
				htmltext = "7969-02.htm";
				
				st.takeItems(LIZARD_FANG, -1);
				st.rewardItems(Inventory.ADENA_ID, lizardFangs * 20);
			}
		}
		else if (event.equalsIgnoreCase("7969-03a.htm"))
		{
			final int barrels = st.getItemsCount(BARREL_OF_LEAGUE);
			if (barrels > 0)
			{
				htmltext = "7969-03.htm";
				
				st.takeItems(BARREL_OF_LEAGUE, -1);
				st.rewardItems(BILL_OF_IASON_HEINE, barrels);
				
				// Heine explains than player can speak with Roman in order to exchange bills for rewards.
				if (st.getInt("cond") == 1)
				{
					st.set("cond", "2");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
			}
		}
		else if (event.equalsIgnoreCase("7969-06.htm"))
		{
			// If no more quest items finish the quest for real, else send a "Return" type HTM.
			if (!st.hasItems(BARREL_OF_LEAGUE, LIZARD_FANG))
			{
				htmltext = "7969-07.htm";
				st.playSound(PlaySoundType.QUEST_FINISH);
				st.exitQuest(true);
			}
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
				htmltext = player.getLevel() < 32 ? "7916-00.htm" : "7916-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case GOSTA:
						htmltext = "7916-04.htm";
						break;
					
					case IASON_HEINE:
						htmltext = "7969-01.htm";
						break;
					
					case ROMAN:
						htmltext = st.hasItems(BILL_OF_IASON_HEINE) ? "7897-01.htm" : "7897-02.htm";
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
		
		final int random = Rnd.get(4);
		if (random < 3)
		{
			st.dropItemsAlways(LIZARD_FANG, random < 2 ? 1 : 2, 0);
			st.dropItems(BARREL_OF_LEAGUE, 1, 0, 50000);
		}
		else
		{
			st.dropItems(BARREL_OF_LEAGUE, 1, 0, npc.getId() > 20785 ? 30000 : 40000);
		}
		
		return null;
	}
	
}
