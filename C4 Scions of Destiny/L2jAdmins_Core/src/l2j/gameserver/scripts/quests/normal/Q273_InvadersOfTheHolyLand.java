package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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
public class Q273_InvadersOfTheHolyLand extends Script
{
	// NPCs
	private static final int VARKEES = 7566;
	// MOBs
	private static final int RAKECLAW_IMP = 311;
	private static final int RAKECLAW_IMP_HUNTER = 312;
	private static final int RAKECLAW_IMP_CHIEFTAIN = 313;
	// ITEMs
	private static final int BLACK_SOULSTONE = 1475;
	private static final int RED_SOULSTONE = 1476;
	// REWARDs
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q273_InvadersOfTheHolyLand()
	{
		super(273, "Invaders of the Holy Land");
		
		registerItems(BLACK_SOULSTONE, RED_SOULSTONE);
		
		addStartNpc(VARKEES);
		addTalkId(VARKEES);
		
		addKillId(RAKECLAW_IMP, RAKECLAW_IMP_HUNTER, RAKECLAW_IMP_CHIEFTAIN);
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
		
		if (event.equalsIgnoreCase("7566-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7566-07.htm"))
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "7566-00.htm";
				}
				else if (player.getLevel() < 6)
				{
					htmltext = "7566-01.htm";
				}
				else
				{
					htmltext = "7566-02.htm";
				}
				break;
			
			case STARTED:
				final int red = st.getItemsCount(RED_SOULSTONE);
				final int black = st.getItemsCount(BLACK_SOULSTONE);
				
				if ((red + black) == 0)
				{
					htmltext = "7566-04.htm";
				}
				else
				{
					if (red == 0)
					{
						htmltext = "7566-05.htm";
					}
					else
					{
						htmltext = "7566-06.htm";
					}
					
					final int reward = (black * 3) + (red * 10) + (black >= 10 ? red >= 1 ? 1800 : 1500 : 0);
					
					st.takeItems(BLACK_SOULSTONE, -1);
					st.takeItems(RED_SOULSTONE, -1);
					st.rewardItems(Inventory.ADENA_ID, reward);
					
					if (player.isNewbie() && (st.getInt("Reward") == 0))
					{
						st.giveItems(SOULSHOT_FOR_BEGINNERS, 6000);
						st.playSound("tutorial_voice_026");
						st.set("Reward", "1");
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
		
		int probability = 77;
		if (npcId == RAKECLAW_IMP)
		{
			probability = 90;
		}
		else if (npcId == RAKECLAW_IMP_HUNTER)
		{
			probability = 87;
		}
		
		if (Rnd.get(100) <= probability)
		{
			st.dropItemsAlways(BLACK_SOULSTONE, 1, 0);
		}
		else
		{
			st.dropItemsAlways(RED_SOULSTONE, 1, 0);
		}
		
		return null;
	}
	
}
