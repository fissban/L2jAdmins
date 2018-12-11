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
public class Q331_ArrowOfVengeance extends Script
{
	// ITEMs
	private static final int HARPY_FEATHER = 1452;
	private static final int MEDUSA_VENOM = 1453;
	private static final int WYRM_TOOTH = 1454;
	
	public Q331_ArrowOfVengeance()
	{
		super(331, "Arrow Of Vengeance");
		
		registerItems(HARPY_FEATHER, MEDUSA_VENOM, WYRM_TOOTH);
		
		addStartNpc(7125); // Belton
		addTalkId(7125);
		
		addKillId(145, 158, 176);
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
		
		if (event.equalsIgnoreCase("7125-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7125-06.htm"))
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
				htmltext = player.getLevel() < 32 ? "7125-01.htm" : "7125-02.htm";
				break;
			
			case STARTED:
				final int harpyFeather = st.getItemsCount(HARPY_FEATHER);
				final int medusaVenom = st.getItemsCount(MEDUSA_VENOM);
				final int wyrmTooth = st.getItemsCount(WYRM_TOOTH);
				
				if ((harpyFeather + medusaVenom + wyrmTooth) > 0)
				{
					htmltext = "7125-05.htm";
					st.takeItems(HARPY_FEATHER, -1);
					st.takeItems(MEDUSA_VENOM, -1);
					st.takeItems(WYRM_TOOTH, -1);
					
					int reward = (harpyFeather * 78) + (medusaVenom * 88) + (wyrmTooth * 92);
					if ((harpyFeather + medusaVenom + wyrmTooth) > 10)
					{
						reward += 3100;
					}
					
					st.rewardItems(57, reward);
				}
				else
				{
					htmltext = "7125-04.htm";
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
		
		switch (npc.getId())
		{
			case 145:
				st.dropItems(HARPY_FEATHER, 1, 0, 500000);
				break;
			
			case 158:
				st.dropItems(MEDUSA_VENOM, 1, 0, 500000);
				break;
			
			case 176:
				st.dropItems(WYRM_TOOTH, 1, 0, 500000);
				break;
		}
		
		return null;
	}
	
}
