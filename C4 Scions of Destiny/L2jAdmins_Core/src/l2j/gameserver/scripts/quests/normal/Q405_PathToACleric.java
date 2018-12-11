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
public class Q405_PathToACleric extends Script
{
	// Items
	private static final int LETTER_OF_ORDER_1 = 1191;
	private static final int LETTER_OF_ORDER_2 = 1192;
	private static final int LIONEL_BOOK = 1193;
	private static final int BOOK_OF_VIVYAN = 1194;
	private static final int BOOK_OF_SIMPLON = 1195;
	private static final int BOOK_OF_PRAGA = 1196;
	private static final int CERTIFICATE_OF_GALLINT = 1197;
	private static final int PENDANT_OF_MOTHER = 1198;
	private static final int NECKLACE_OF_MOTHER = 1199;
	private static final int LIONEL_COVENANT = 1200;
	
	// NPCs
	private static final int GALLINT = 7017;
	private static final int ZIGAUNT = 7022;
	private static final int VIVYAN = 7030;
	private static final int PRAGA = 7333;
	private static final int SIMPLON = 7253;
	private static final int LIONEL = 7408;
	
	// Reward
	private static final int MARK_OF_FATE = 1201;
	
	public Q405_PathToACleric()
	{
		super(405, "Path to a Cleric");
		
		registerItems(LETTER_OF_ORDER_1, BOOK_OF_SIMPLON, BOOK_OF_PRAGA, BOOK_OF_VIVYAN, NECKLACE_OF_MOTHER, PENDANT_OF_MOTHER, LETTER_OF_ORDER_2, LIONEL_BOOK, CERTIFICATE_OF_GALLINT, LIONEL_COVENANT);
		
		addStartNpc(ZIGAUNT);
		addTalkId(ZIGAUNT, SIMPLON, PRAGA, VIVYAN, LIONEL, GALLINT);
		
		addKillId(29, 26);
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
		
		if (event.equalsIgnoreCase("7022-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(LETTER_OF_ORDER_1, 1);
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
				if (player.getClassId() != ClassId.HUMAN_MAGE)
				{
					htmltext = player.getClassId() == ClassId.CLERIC ? "7022-02a.htm" : "7022-02.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "7022-03.htm";
				}
				else if (st.hasItems(MARK_OF_FATE))
				{
					htmltext = "7022-04.htm";
				}
				else
				{
					htmltext = "7022-01.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case ZIGAUNT:
						if (cond == 1)
						{
							htmltext = "7022-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7022-08.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(BOOK_OF_PRAGA, 1);
							st.takeItems(BOOK_OF_VIVYAN, 1);
							st.takeItems(BOOK_OF_SIMPLON, 3);
							st.takeItems(LETTER_OF_ORDER_1, 1);
							st.giveItems(LETTER_OF_ORDER_2, 1);
						}
						else if ((cond > 2) && (cond < 6))
						{
							htmltext = "7022-07.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7022-09.htm";
							st.takeItems(LETTER_OF_ORDER_2, 1);
							st.takeItems(LIONEL_COVENANT, 1);
							st.giveItems(MARK_OF_FATE, 1);
							st.rewardExpAndSp(3200, 5610);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case SIMPLON:
						if ((cond == 1) && !st.hasItems(BOOK_OF_SIMPLON))
						{
							htmltext = "7253-01.htm";
							st.playSound(PlaySoundType.QUEST_ITEMGET);
							st.giveItems(BOOK_OF_SIMPLON, 3);
						}
						else if ((cond > 1) || st.hasItems(BOOK_OF_SIMPLON))
						{
							htmltext = "7253-02.htm";
						}
						break;
					
					case PRAGA:
						if (cond == 1)
						{
							if (!st.hasItems(BOOK_OF_PRAGA) && !st.hasItems(NECKLACE_OF_MOTHER) && st.hasItems(BOOK_OF_SIMPLON))
							{
								htmltext = "7333-01.htm";
								st.playSound(PlaySoundType.QUEST_ITEMGET);
								st.giveItems(NECKLACE_OF_MOTHER, 1);
							}
							else if (!st.hasItems(PENDANT_OF_MOTHER))
							{
								htmltext = "7333-02.htm";
							}
							else if (st.hasItems(PENDANT_OF_MOTHER))
							{
								htmltext = "7333-03.htm";
								st.takeItems(NECKLACE_OF_MOTHER, 1);
								st.takeItems(PENDANT_OF_MOTHER, 1);
								st.giveItems(BOOK_OF_PRAGA, 1);
								
								if (st.hasItems(BOOK_OF_VIVYAN))
								{
									st.set("cond", "2");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									st.playSound(PlaySoundType.QUEST_ITEMGET);
								}
							}
						}
						else if ((cond > 1) || st.hasItems(BOOK_OF_PRAGA))
						{
							htmltext = "7333-04.htm";
						}
						break;
					
					case VIVYAN:
						if ((cond == 1) && !st.hasItems(BOOK_OF_VIVYAN) && st.hasItems(BOOK_OF_SIMPLON))
						{
							htmltext = "7030-01.htm";
							st.giveItems(BOOK_OF_VIVYAN, 1);
							
							if (st.hasItems(BOOK_OF_PRAGA))
							{
								st.set("cond", "2");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
						else if ((cond > 1) || st.hasItems(BOOK_OF_VIVYAN))
						{
							htmltext = "7030-02.htm";
						}
						break;
					
					case LIONEL:
						if (cond < 3)
						{
							htmltext = "7408-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7408-01.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(LIONEL_BOOK, 1);
						}
						else if (cond == 4)
						{
							htmltext = "7408-03.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7408-04.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CERTIFICATE_OF_GALLINT, 1);
							st.giveItems(LIONEL_COVENANT, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7408-05.htm";
						}
						break;
					
					case GALLINT:
						if (cond == 4)
						{
							htmltext = "7017-01.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(LIONEL_BOOK, 1);
							st.giveItems(CERTIFICATE_OF_GALLINT, 1);
						}
						else if (cond > 4)
						{
							htmltext = "7017-02.htm";
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
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "1");
		if (st == null)
		{
			return null;
		}
		
		if (st.hasItems(NECKLACE_OF_MOTHER) && !st.hasItems(PENDANT_OF_MOTHER))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(PENDANT_OF_MOTHER, 1);
		}
		
		return null;
	}
	
}
