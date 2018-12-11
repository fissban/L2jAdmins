package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

public class Q257_TheGuardIsBusy extends Script
{
	// NPCs
	private static final int GILBERT = 7039;
	// MOBs
	private static final int ORC_ARCHER = 6;
	private static final int ORC_FIGHTER = 93;
	private static final int ORC_LIEUTENANT = 96;
	private static final int ORC_FIGHTER_LEADER = 98;
	private static final int ORC = 130;
	private static final int ORC_GRUNT = 131;
	private static final int WEREWOLF = 132;
	private static final int WEREWOLF_CHIEFTAIN = 342;
	private static final int WEREWOLF_HUNTER = 343;
	// ITEMs
	private static final int GLUDIO_LORD_MARK = 1084;
	private static final int ORC_AMULET = 752;
	private static final int ORC_NECKLACE = 1085;
	private static final int WEREWOLF_FANG = 1086;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	
	public Q257_TheGuardIsBusy()
	{
		super(257, "The Guard Is Busy");
		
		registerItems(ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG, GLUDIO_LORD_MARK);
		
		addStartNpc(GILBERT);
		addTalkId(GILBERT);
		
		addKillId(ORC_ARCHER, ORC_FIGHTER, ORC_LIEUTENANT, ORC_FIGHTER_LEADER, ORC, ORC_GRUNT, WEREWOLF, WEREWOLF_CHIEFTAIN, WEREWOLF_HUNTER);
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
		
		if (event.equalsIgnoreCase("7039-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(GLUDIO_LORD_MARK, 1);
		}
		else if (event.equalsIgnoreCase("7039-05.htm"))
		{
			st.takeItems(GLUDIO_LORD_MARK, 1);
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
				htmltext = player.getLevel() < 6 ? "7039-01.htm" : "7039-02.htm";
				break;
			
			case STARTED:
				final int amulets = st.getItemsCount(ORC_AMULET);
				final int necklaces = st.getItemsCount(ORC_NECKLACE);
				final int fangs = st.getItemsCount(WEREWOLF_FANG);
				
				if ((amulets + necklaces + fangs) == 0)
				{
					htmltext = "7039-04.htm";
				}
				else
				{
					htmltext = "7039-07.htm";
					
					st.takeItems(ORC_AMULET, -1);
					st.takeItems(ORC_NECKLACE, -1);
					st.takeItems(WEREWOLF_FANG, -1);
					
					int reward = (10 * amulets) + (20 * (necklaces + fangs));
					if ((amulets + necklaces + fangs) >= 10)
					{
						reward += 1000;
					}
					
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
		
		switch (npc.getId())
		{
			case ORC_ARCHER:
			case ORC:
			case ORC_GRUNT:
				st.dropItems(ORC_AMULET, 1, 0, 500000);
				break;
			
			case WEREWOLF:
				st.dropItems(WEREWOLF_FANG, 1, 0, 500000);
				break;
			
			case ORC_FIGHTER:
			case ORC_LIEUTENANT:
			case ORC_FIGHTER_LEADER:
				st.dropItems(ORC_NECKLACE, 1, 0, 500000);
				break;
			
			case WEREWOLF_CHIEFTAIN:
				st.dropItems(WEREWOLF_FANG, 1, 0, 200000);
				break;
			
			case WEREWOLF_HUNTER:
				st.dropItems(WEREWOLF_FANG, 1, 0, 400000);
				break;
		}
		
		return null;
	}
}
