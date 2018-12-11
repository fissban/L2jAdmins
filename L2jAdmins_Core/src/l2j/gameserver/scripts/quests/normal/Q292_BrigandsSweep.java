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
public class Q292_BrigandsSweep extends Script
{
	// NPCs
	private static final int SPIRON = 7532;
	private static final int BALANKI = 7533;
	// MOBs
	private static final int GOBLIN_BRIGAND = 322;
	private static final int GOBLIN_BRIGAND_LEADER = 323;
	private static final int GOBLIN_BRIGAND_LIEUTENANT = 324;
	private static final int GOBLIN_SNOOPER = 327;
	private static final int GOBLIN_LORD = 528;
	// ITEMs
	private static final int GOBLIN_NECKLACE = 1483;
	private static final int GOBLIN_PENDANT = 1484;
	private static final int GOBLIN_LORD_PENDANT = 1485;
	private static final int SUSPICIOUS_MEMO = 1486;
	private static final int SUSPICIOUS_CONTRACT = 1487;
	
	public Q292_BrigandsSweep()
	{
		super(292, "Brigands Sweep");
		
		registerItems(GOBLIN_NECKLACE, GOBLIN_PENDANT, GOBLIN_LORD_PENDANT, SUSPICIOUS_MEMO, SUSPICIOUS_CONTRACT);
		
		addStartNpc(SPIRON);
		addTalkId(SPIRON, BALANKI);
		
		addKillId(GOBLIN_BRIGAND, GOBLIN_BRIGAND_LEADER, GOBLIN_BRIGAND_LIEUTENANT, GOBLIN_SNOOPER, GOBLIN_LORD);
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
		
		if (event.equalsIgnoreCase("7532-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("7532-06.htm"))
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
				if (player.getRace() != Race.DWARF)
				{
					htmltext = "7532-00.htm";
				}
				else if (player.getLevel() < 5)
				{
					htmltext = "7532-01.htm";
				}
				else
				{
					htmltext = "7532-02.htm";
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case SPIRON:
						final int goblinNecklaces = st.getItemsCount(GOBLIN_NECKLACE);
						final int goblinPendants = st.getItemsCount(GOBLIN_PENDANT);
						final int goblinLordPendants = st.getItemsCount(GOBLIN_LORD_PENDANT);
						final int suspiciousMemos = st.getItemsCount(SUSPICIOUS_MEMO);
						
						final int countAll = goblinNecklaces + goblinPendants + goblinLordPendants;
						
						final boolean hasContract = st.hasItems(SUSPICIOUS_CONTRACT);
						
						if (countAll == 0)
						{
							htmltext = "7532-04.htm";
						}
						else
						{
							if (hasContract)
							{
								htmltext = "7532-10.htm";
							}
							else if (suspiciousMemos > 0)
							{
								if (suspiciousMemos > 1)
								{
									htmltext = "7532-09.htm";
								}
								else
								{
									htmltext = "7532-08.htm";
								}
							}
							else
							{
								htmltext = "7532-05.htm";
							}
							
							st.takeItems(GOBLIN_NECKLACE, -1);
							st.takeItems(GOBLIN_PENDANT, -1);
							st.takeItems(GOBLIN_LORD_PENDANT, -1);
							
							if (hasContract)
							{
								st.set("cond", "1");
								st.takeItems(SUSPICIOUS_CONTRACT, -1);
							}
							
							st.rewardItems(Inventory.ADENA_ID, (12 * goblinNecklaces) + (36 * goblinPendants) + (33 * goblinLordPendants) + (countAll >= 10 ? 1000 : 0) + (hasContract ? 1120 : 0));
						}
						break;
					
					case BALANKI:
						if (!st.hasItems(SUSPICIOUS_CONTRACT))
						{
							htmltext = "7533-01.htm";
						}
						else
						{
							htmltext = "7533-02.htm";
							st.set("cond", "1");
							st.takeItems(SUSPICIOUS_CONTRACT, -1);
							st.rewardItems(Inventory.ADENA_ID, 1500);
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		final int chance = Rnd.get(10);
		
		if (chance > 5)
		{
			switch (npc.getId())
			{
				case GOBLIN_BRIGAND:
				case GOBLIN_SNOOPER:
				case GOBLIN_BRIGAND_LIEUTENANT:
					st.dropItemsAlways(GOBLIN_NECKLACE, 1, 0);
					break;
				
				case GOBLIN_BRIGAND_LEADER:
					st.dropItemsAlways(GOBLIN_PENDANT, 1, 0);
					break;
				
				case GOBLIN_LORD:
					st.dropItemsAlways(GOBLIN_LORD_PENDANT, 1, 0);
					break;
			}
		}
		else if ((chance > 4) && (st.getInt("cond") == 1) && st.dropItemsAlways(SUSPICIOUS_MEMO, 1, 3))
		{
			st.set("cond", "2");
			st.takeItems(SUSPICIOUS_MEMO, -1);
			st.giveItems(SUSPICIOUS_CONTRACT, 1);
		}
		
		return null;
	}
	
}
