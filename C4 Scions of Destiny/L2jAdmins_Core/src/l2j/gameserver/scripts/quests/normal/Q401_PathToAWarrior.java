package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.items.enums.ParpedollType;
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
public class Q401_PathToAWarrior extends Script
{
	// Items
	private static final int AURON_LETTER = 1138;
	private static final int WARRIOR_GUILD_MARK = 1139;
	private static final int RUSTED_BRONZE_SWORD_1 = 1140;
	private static final int RUSTED_BRONZE_SWORD_2 = 1141;
	private static final int RUSTED_BRONZE_SWORD_3 = 1142;
	private static final int SIMPLON_LETTER = 1143;
	private static final int POISON_SPIDER_LEG = 1144;
	private static final int MEDALLION_OF_WARRIOR = 1145;
	
	// NPCs
	private static final int AURON = 7010;
	private static final int SIMPLON = 7253;
	
	public Q401_PathToAWarrior()
	{
		super(401, "Path to a Warrior");
		
		registerItems(AURON_LETTER, WARRIOR_GUILD_MARK, RUSTED_BRONZE_SWORD_1, RUSTED_BRONZE_SWORD_2, RUSTED_BRONZE_SWORD_3, SIMPLON_LETTER, POISON_SPIDER_LEG);
		
		addStartNpc(AURON);
		addTalkId(AURON, SIMPLON);
		
		addKillId(35, 38, 42, 43);
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
		
		if (event.equalsIgnoreCase("7010-05.htm"))
		{
			if (player.getClassId() != ClassId.HUMAN_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.WARRIOR ? "7010-03.htm" : "7010-02b.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7010-02.htm";
			}
			else if (st.hasItems(MEDALLION_OF_WARRIOR))
			{
				htmltext = "7010-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7010-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(AURON_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7253-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(AURON_LETTER, 1);
			st.giveItems(WARRIOR_GUILD_MARK, 1);
		}
		else if (event.equalsIgnoreCase("7010-11.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(RUSTED_BRONZE_SWORD_2, 1);
			st.takeItems(SIMPLON_LETTER, 1);
			st.giveItems(RUSTED_BRONZE_SWORD_3, 1);
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
				htmltext = "7010-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case AURON:
						if (cond == 1)
						{
							htmltext = "7010-07.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "7010-08.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7010-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7010-12.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7010-13.htm";
							st.takeItems(POISON_SPIDER_LEG, -1);
							st.takeItems(RUSTED_BRONZE_SWORD_3, 1);
							st.giveItems(MEDALLION_OF_WARRIOR, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case SIMPLON:
						if (cond == 1)
						{
							htmltext = "7253-01.htm";
						}
						else if (cond == 2)
						{
							if (!st.hasItems(RUSTED_BRONZE_SWORD_1))
							{
								htmltext = "7253-03.htm";
							}
							else if (st.getItemsCount(RUSTED_BRONZE_SWORD_1) <= 9)
							{
								htmltext = "7253-03b.htm";
							}
						}
						else if (cond == 3)
						{
							htmltext = "7253-04.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(RUSTED_BRONZE_SWORD_1, 10);
							st.takeItems(WARRIOR_GUILD_MARK, 1);
							st.giveItems(RUSTED_BRONZE_SWORD_2, 1);
							st.giveItems(SIMPLON_LETTER, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7253-05.htm";
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
		
		switch (npc.getId())
		{
			case 35:
			case 42:
				if ((st.getInt("cond") == 2) && st.dropItems(RUSTED_BRONZE_SWORD_1, 1, 10, 400000))
				{
					st.set("cond", "3");
				}
				break;
			
			case 38:
			case 43:
				if ((st.getInt("cond") == 5) && (st.getItemEquipped(ParpedollType.RHAND) == RUSTED_BRONZE_SWORD_3))
				{
					if (st.dropItemsAlways(POISON_SPIDER_LEG, 1, 20))
					{
						st.set("cond", "6");
					}
				}
				break;
		}
		
		return null;
	}
	
}
