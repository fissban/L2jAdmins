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
public class Q352_HelpRoodRaiseANewPet extends Script
{
	// ITEMs
	private static final int LIENRIK_EGG_1 = 5860;
	private static final int LIENRIK_EGG_2 = 5861;
	
	public Q352_HelpRoodRaiseANewPet()
	{
		super(352, "Help Rood Raise A New Pet!");
		
		registerItems(LIENRIK_EGG_1, LIENRIK_EGG_2);
		
		addStartNpc(8067); // Rood
		addTalkId(8067);
		
		addKillId(286, 787, 786, 787);
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
		
		if (event.equalsIgnoreCase("8067-04.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8067-09.htm"))
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
				htmltext = player.getLevel() < 39 ? "8067-00.htm" : "8067-01.htm";
				break;
			
			case STARTED:
				final int eggs1 = st.getItemsCount(LIENRIK_EGG_1);
				final int eggs2 = st.getItemsCount(LIENRIK_EGG_2);
				
				if ((eggs1 + eggs2) == 0)
				{
					htmltext = "8067-05.htm";
				}
				else
				{
					int reward = 2000;
					if ((eggs1 > 0) && (eggs2 == 0))
					{
						htmltext = "8067-06.htm";
						reward += eggs1 * 34;
						
						st.takeItems(LIENRIK_EGG_1, -1);
						st.rewardItems(Inventory.ADENA_ID, reward);
					}
					else if ((eggs1 == 0) && (eggs2 > 0))
					{
						htmltext = "8067-08.htm";
						reward += eggs2 * 1025;
						
						st.takeItems(LIENRIK_EGG_2, -1);
						st.rewardItems(Inventory.ADENA_ID, reward);
					}
					else if ((eggs1 > 0) && (eggs2 > 0))
					{
						htmltext = "8067-08.htm";
						reward += (eggs1 * 34) + (eggs2 * 1025) + 2000;
						
						st.takeItems(LIENRIK_EGG_1, -1);
						st.takeItems(LIENRIK_EGG_2, -1);
						st.rewardItems(Inventory.ADENA_ID, reward);
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
		
		final int npcId = npc.getId();
		final int random = Rnd.get(100);
		final int chance = (npcId == 786) || (npcId == 1644) ? 44 : 58;
		
		if (random < chance)
		{
			st.dropItemsAlways(LIENRIK_EGG_1, 1, 0);
		}
		else if (random < (chance + 4))
		{
			st.dropItemsAlways(LIENRIK_EGG_2, 1, 0);
		}
		
		return null;
	}
	
}
