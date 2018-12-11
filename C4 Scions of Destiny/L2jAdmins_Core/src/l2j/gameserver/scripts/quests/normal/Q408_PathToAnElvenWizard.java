package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
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
public class Q408_PathToAnElvenWizard extends Script
{
	// Items
	private static final int ROSELLA_LETTER = 1218;
	private static final int RED_DOWN = 1219;
	private static final int MAGICAL_POWERS_RUBY = 1220;
	private static final int PURE_AQUAMARINE = 1221;
	private static final int APPETIZING_APPLE = 1222;
	private static final int GOLD_LEAVES = 1223;
	private static final int IMMORTAL_LOVE = 1224;
	private static final int AMETHYST = 1225;
	private static final int NOBILITY_AMETHYST = 1226;
	private static final int FERTILITY_PERIDOT = 1229;
	private static final int ETERNITY_DIAMOND = 1230;
	private static final int CHARM_OF_GRAIN = 1272;
	private static final int SAP_OF_THE_MOTHER_TREE = 1273;
	private static final int LUCKY_POTPOURRI = 1274;
	
	// NPCs
	private static final int ROSELLA = 7414;
	private static final int GREENIS = 7157;
	private static final int THALIA = 7371;
	private static final int NORTHWIND = 7423;
	
	public Q408_PathToAnElvenWizard()
	{
		super(408, "Path to an Elven Wizard");
		
		registerItems(ROSELLA_LETTER, RED_DOWN, MAGICAL_POWERS_RUBY, PURE_AQUAMARINE, APPETIZING_APPLE, GOLD_LEAVES, IMMORTAL_LOVE, AMETHYST, NOBILITY_AMETHYST, FERTILITY_PERIDOT, CHARM_OF_GRAIN, SAP_OF_THE_MOTHER_TREE, LUCKY_POTPOURRI);
		
		addStartNpc(ROSELLA);
		addTalkId(ROSELLA, GREENIS, THALIA, NORTHWIND);
		
		addKillId(47, 19, 466);
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
		
		if (event.equalsIgnoreCase("7414-06.htm"))
		{
			if (player.getClassId() != ClassId.ELF_MAGE)
			{
				htmltext = player.getClassId() == ClassId.ELF_WIZARD ? "7414-02a.htm" : "7414-03.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7414-04.htm";
			}
			else if (st.hasItems(ETERNITY_DIAMOND))
			{
				htmltext = "7414-05.htm";
			}
			else
			{
				st.setState(ScriptStateType.STARTED);
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ACCEPT);
				st.giveItems(FERTILITY_PERIDOT, 1);
			}
		}
		else if (event.equalsIgnoreCase("7414-07.htm"))
		{
			if (!st.hasItems(MAGICAL_POWERS_RUBY))
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(ROSELLA_LETTER, 1);
			}
			else
			{
				htmltext = "7414-10.htm";
			}
		}
		else if (event.equalsIgnoreCase("7414-14.htm"))
		{
			if (!st.hasItems(PURE_AQUAMARINE))
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(APPETIZING_APPLE, 1);
			}
			else
			{
				htmltext = "7414-13.htm";
			}
		}
		else if (event.equalsIgnoreCase("7414-18.htm"))
		{
			if (!st.hasItems(NOBILITY_AMETHYST))
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(IMMORTAL_LOVE, 1);
			}
			else
			{
				htmltext = "7414-17.htm";
			}
		}
		else if (event.equalsIgnoreCase("7157-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ROSELLA_LETTER, 1);
			st.giveItems(CHARM_OF_GRAIN, 1);
		}
		else if (event.equalsIgnoreCase("7371-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(APPETIZING_APPLE, 1);
			st.giveItems(SAP_OF_THE_MOTHER_TREE, 1);
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
				htmltext = "7414-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case ROSELLA:
						if (st.hasItems(MAGICAL_POWERS_RUBY, NOBILITY_AMETHYST, PURE_AQUAMARINE))
						{
							htmltext = "7414-24.htm";
							st.takeItems(FERTILITY_PERIDOT, 1);
							st.takeItems(MAGICAL_POWERS_RUBY, 1);
							st.takeItems(NOBILITY_AMETHYST, 1);
							st.takeItems(PURE_AQUAMARINE, 1);
							st.giveItems(ETERNITY_DIAMOND, 1);
							st.rewardExpAndSp(3200, 1890);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						else if (st.hasItems(ROSELLA_LETTER))
						{
							htmltext = "7414-08.htm";
						}
						else if (st.hasItems(CHARM_OF_GRAIN))
						{
							if (st.getItemsCount(RED_DOWN) == 5)
							{
								htmltext = "7414-25.htm";
							}
							else
							{
								htmltext = "7414-09.htm";
							}
						}
						else if (st.hasItems(APPETIZING_APPLE))
						{
							htmltext = "7414-15.htm";
						}
						else if (st.hasItems(SAP_OF_THE_MOTHER_TREE))
						{
							if (st.getItemsCount(GOLD_LEAVES) == 5)
							{
								htmltext = "7414-26.htm";
							}
							else
							{
								htmltext = "7414-16.htm";
							}
						}
						else if (st.hasItems(IMMORTAL_LOVE))
						{
							htmltext = "7414-19.htm";
						}
						else if (st.hasItems(LUCKY_POTPOURRI))
						{
							if (st.getItemsCount(AMETHYST) == 2)
							{
								htmltext = "7414-27.htm";
							}
							else
							{
								htmltext = "7414-20.htm";
							}
						}
						else
						{
							htmltext = "7414-11.htm";
						}
						break;
					
					case GREENIS:
						if (st.hasItems(ROSELLA_LETTER))
						{
							htmltext = "7157-01.htm";
						}
						else if (st.getItemsCount(RED_DOWN) == 5)
						{
							htmltext = "7157-04.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CHARM_OF_GRAIN, 1);
							st.takeItems(RED_DOWN, -1);
							st.giveItems(MAGICAL_POWERS_RUBY, 1);
						}
						else if (st.hasItems(CHARM_OF_GRAIN))
						{
							htmltext = "7157-03.htm";
						}
						break;
					
					case THALIA:
						if (st.hasItems(APPETIZING_APPLE))
						{
							htmltext = "7371-01.htm";
						}
						else if (st.getItemsCount(GOLD_LEAVES) == 5)
						{
							htmltext = "7371-04.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(GOLD_LEAVES, -1);
							st.takeItems(SAP_OF_THE_MOTHER_TREE, 1);
							st.giveItems(PURE_AQUAMARINE, 1);
						}
						else if (st.hasItems(SAP_OF_THE_MOTHER_TREE))
						{
							htmltext = "7371-03.htm";
						}
						break;
					
					case NORTHWIND:
						if (st.hasItems(IMMORTAL_LOVE))
						{
							htmltext = "7423-01.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(IMMORTAL_LOVE, 1);
							st.giveItems(LUCKY_POTPOURRI, 1);
						}
						else if (st.getItemsCount(AMETHYST) == 2)
						{
							htmltext = "7423-03.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(AMETHYST, -1);
							st.takeItems(LUCKY_POTPOURRI, 1);
							st.giveItems(NOBILITY_AMETHYST, 1);
						}
						else if (st.hasItems(LUCKY_POTPOURRI))
						{
							htmltext = "7423-02.htm";
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
			case 19:
				if (st.hasItems(SAP_OF_THE_MOTHER_TREE))
				{
					st.dropItems(GOLD_LEAVES, 1, 5, 400000);
				}
				break;
			
			case 47:
				if (st.hasItems(LUCKY_POTPOURRI))
				{
					st.dropItems(AMETHYST, 1, 2, 400000);
				}
				break;
			
			case 466:
				if (st.hasItems(CHARM_OF_GRAIN))
				{
					st.dropItems(RED_DOWN, 1, 5, 700000);
				}
				break;
		}
		
		return null;
	}
	
}
