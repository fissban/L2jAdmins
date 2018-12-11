
package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * Test Of The Reformer
 * @author zarie
 */
public class Q227_TestOfTheReformer extends Script
{
	// NPCs
	private static final int PRIESTESS_PUPINA = 7118;
	private static final int PREACHER_SLA = 7666;
	private static final int RAMUS = 7667;
	private static final int KATARI = 7668;
	private static final int KAKAN = 7669;
	private static final int NYAKURI = 7670;
	private static final int OL_MAHUM_PILGRIM = 7732;
	// Items
	private static final int BOOK_OF_REFORM = 2822;
	private static final int LETTER_OF_INTRODUCTION = 2823;
	private static final int SLAS_LETTER = 2824;
	private static final int GREETINGS = 2825;
	private static final int OLMAHUMS_MONEY = 2826;
	private static final int KATARIS_LETTER = 2827;
	private static final int NYAKURIS_LETTER = 2828;
	private static final int UNDEAD_LIST = 2829;
	private static final int RAMUSS_LETTER = 2830;
	private static final int RIPPED_DIARY = 2831;
	private static final int HUGE_NAIL = 2832;
	private static final int LETTER_OF_BETRAYER = 2833;
	private static final int BONE_FRAGMENT4 = 2834;
	private static final int BONE_FRAGMENT5 = 2835;
	private static final int BONE_FRAGMENT6 = 2836;
	private static final int BONE_FRAGMENT7 = 2837;
	private static final int BONE_FRAGMENT8 = 2838;
	private static final int BONE_FRAGMENT9 = 2839;
	private static final int KAKANS_LETTER = 3037;
	// Rewards
	private static final int MARK_OF_REFORMER = 2821;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// Monsters
	private static final int MISERY_SKELETON = 22;
	private static final int SKELETON_ARCHER = 100;
	private static final int SKELETON_MARKSMAN = 102;
	private static final int SKELETON_LORD = 104;
	private static final int SILENT_HORROR = 404;
	// Quest Monsters
	private static final int NAMELESS_REVENANT = 5099;
	private static final int ARURAUNE = 5128;
	private static final int OL_MAHUM_INSPECTOR = 5129;
	private static final int OL_MAHUM_BETRAYER = 5130;
	private static final int CRIMSON_WEREWOLF = 5131;
	private static final int KRUDEL_LIZARDMAN = 5132;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q227_TestOfTheReformer()
	{
		super(227, "Test Of The Reformer");
		addStartNpc(PRIESTESS_PUPINA);
		addTalkId(PRIESTESS_PUPINA, PREACHER_SLA, RAMUS, KATARI, KAKAN, NYAKURI, OL_MAHUM_PILGRIM);
		addAttackId(NAMELESS_REVENANT, CRIMSON_WEREWOLF);
		addKillId(MISERY_SKELETON, SKELETON_ARCHER, SKELETON_MARKSMAN, SKELETON_LORD, SILENT_HORROR, NAMELESS_REVENANT, ARURAUNE, OL_MAHUM_INSPECTOR, OL_MAHUM_BETRAYER, CRIMSON_WEREWOLF, KRUDEL_LIZARDMAN);
		addSpawnId(OL_MAHUM_PILGRIM, OL_MAHUM_INSPECTOR, OL_MAHUM_BETRAYER, CRIMSON_WEREWOLF, KRUDEL_LIZARDMAN);
		registerItems(BOOK_OF_REFORM, LETTER_OF_INTRODUCTION, SLAS_LETTER, GREETINGS, OLMAHUMS_MONEY, KATARIS_LETTER, NYAKURIS_LETTER, UNDEAD_LIST, RAMUSS_LETTER, RIPPED_DIARY, HUGE_NAIL, LETTER_OF_BETRAYER, BONE_FRAGMENT4, BONE_FRAGMENT5, BONE_FRAGMENT6, BONE_FRAGMENT7, BONE_FRAGMENT8, BONE_FRAGMENT9, KAKANS_LETTER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		String htmltext = event;
		if (st == null)
		{
			return getNoQuestMsg();
		}
		
		switch (event)
		{
			case "1":
				htmltext = "7118-04.htm";
				st.set("cond", "1");
				st.playSound(PlaySoundType.QUEST_ITEMGET);
				st.giveItems(BOOK_OF_REFORM, 1);
				break;
			
			case "7118_1":
				htmltext = "7118-06.htm";
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(LETTER_OF_INTRODUCTION, 1);
				st.takeItems(BOOK_OF_REFORM, 1);
				st.set("cond", "4");
				st.takeItems(HUGE_NAIL, 1);
				break;
			
			case "7666_1":
				htmltext = "7666-03.htm";
				break;
			
			case "7666_2":
				htmltext = "7666-02.htm";
				break;
			
			case "7666_3":
				htmltext = "7666-04.htm";
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.giveItems(SLAS_LETTER, 1);
				st.takeItems(LETTER_OF_INTRODUCTION, 1);
				st.set("cond", "5");
				break;
			
			case "7669_1":
				htmltext = "7669-02.htm";
				break;
			
			case "7669_2":
				htmltext = "7669-03.htm";
				addSpawn(CRIMSON_WEREWOLF, -9382, -89852, -2333, 0, false, 0);
				break;
			
			case "7669_3":
				htmltext = "7669-05.htm";
				break;
			
			case "7670_1":
				htmltext = "7670-03.htm";
				addSpawn(KRUDEL_LIZARDMAN, 126019, -179983, -1781, 0, false, 0);
				break;
			
			case "7670_2":
				htmltext = "7670-02.htm";
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final ScriptState st = player.getScriptState(getName());
		int npcId = npc.getId();
		String htmltext = getNoQuestMsg();
		
		switch (st.getState())
		{
			case CREATED:
				if (npcId == PRIESTESS_PUPINA)
				{
					switch (st.getPlayer().getClassId())
					{
						case CLERIC:
						case SHILLIEN_ORACLE:
							st.startQuest();
							if (st.getPlayer().getLevel() < MIN_LEVEL)
							{
								htmltext = "7118-01.htm";
								st.exitQuest(true);
							}
							else
							{
								htmltext = "7118-03.htm";
							}
							break;
						
						default:
							htmltext = "7118-02.htm";
							st.exitQuest(true);
							break;
					}
				}
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case PRIESTESS_PUPINA:
						if ((st.getCond() == 3) && st.hasItems(HUGE_NAIL))
						{
							htmltext = "7118-05.htm";
						}
						else if (st.getCond() >= 4)
						{
							htmltext = "7118-07.htm";
						}
						break;
					
					case PREACHER_SLA:
						if ((st.getCond() == 4) && st.hasItems(LETTER_OF_INTRODUCTION))
						{
							htmltext = "7666-01.htm";
						}
						else if ((st.getCond() == 5) && st.hasItems(SLAS_LETTER))
						{
							htmltext = "7666-05.htm";
						}
						else if (st.getCond() == 10)
						{
							htmltext = "7666-06.htm";
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(OLMAHUMS_MONEY, 1);
							st.giveItems(GREETINGS, 3);
							st.set("cond", "11");
						}
						else if ((st.getCond() == 19) && st.hasItems(KATARIS_LETTER) && st.hasItems(KAKANS_LETTER) && st.hasItems(NYAKURIS_LETTER) && st.hasItems(RAMUSS_LETTER))
						{
							htmltext = "7666-07.htm";
							st.takeItems(KATARIS_LETTER, 1);
							st.takeItems(KAKANS_LETTER, 1);
							st.takeItems(NYAKURIS_LETTER, 1);
							st.takeItems(RAMUSS_LETTER, 1);
							st.giveItems(MARK_OF_REFORMER, 1);
							st.giveItems(DIMENSIONAL_DIAMOND, 61);
							st.rewardExpAndSp(164032, 17500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.exitQuest(false, true);
						}
						break;
					
					case KATARI:
						if ((st.getCond() == 5) || (st.getCond() == 6))
						{
							htmltext = "7668-01.htm";
							st.set("cond", "6");
							st.takeItems(SLAS_LETTER, 1);
							addSpawn(OL_MAHUM_PILGRIM, -4015, 40141, -3664, 0, false, 0);
							addSpawn(OL_MAHUM_INSPECTOR, -4034, 40201, -3665, 0, false, 0);
						}
						else if (st.getCond() == 8)
						{
							htmltext = "7668-02.htm";
							addSpawn(OL_MAHUM_BETRAYER, -4106, 40174, -3660, 0, false, 0);
						}
						else if (st.getCond() == 9)
						{
							htmltext = "7668-03.htm";
							st.set("cond", "10");
							st.giveItems(KATARIS_LETTER, 1);
							st.takeItems(LETTER_OF_BETRAYER, 1);
						}
						break;
					
					case OL_MAHUM_PILGRIM:
						if (st.getCond() == 7)
						{
							htmltext = "7732-01.htm";
							st.set("cond", "8");
							st.giveItems(OLMAHUMS_MONEY, 1);
						}
						break;
					
					case KAKAN:
						if ((st.getCond() == 11) && st.hasItems(GREETINGS))
						{
							htmltext = "7669-01.htm";
						}
						else if (st.getCond() == 12)
						{
							htmltext = "7669-04.htm";
							st.set("cond", "13");
							st.giveItems(KAKANS_LETTER, 1);
							st.takeItems(GREETINGS, 1);
						}
						break;
					
					case NYAKURI:
						if ((st.getCond() == 13) && st.hasItems(GREETINGS))
						{
							htmltext = "7670-01.htm";
						}
						else if ((st.getCond() == 15) && st.hasItems(GREETINGS))
						{
							htmltext = "7670-04.htm";
							st.set("cond", "16");
							st.giveItems(NYAKURIS_LETTER, 1);
							st.takeItems(GREETINGS, 1);
						}
						break;
					
					case RAMUS:
						if ((st.getCond() == 16) && st.hasItems(GREETINGS))
						{
							htmltext = "7667-01.htm";
							st.set("cond", "17");
							st.giveItems(UNDEAD_LIST, 1);
							st.takeItems(GREETINGS, 1);
						}
						else if (st.hasItems(BONE_FRAGMENT4) && st.hasItems(BONE_FRAGMENT5) && st.hasItems(BONE_FRAGMENT6) && st.hasItems(BONE_FRAGMENT7) && st.hasItems(BONE_FRAGMENT8))
						{
							htmltext = "7667-03.htm";
							st.set("cond", "19");
							st.takeItems(BONE_FRAGMENT4, 1);
							st.takeItems(BONE_FRAGMENT5, 1);
							st.takeItems(BONE_FRAGMENT6, 1);
							st.takeItems(BONE_FRAGMENT7, 1);
							st.takeItems(BONE_FRAGMENT8, 1);
							st.takeItems(UNDEAD_LIST, 1);
							st.giveItems(RAMUSS_LETTER, 1);
						}
						else if (st.getCond() == 17)
						{
							htmltext = "7667-02.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st != null)
		{
			if (st.getState() != ScriptStateType.STARTED)
			{
				return null;
			}
			
			switch (npc.getId())
			{
				case NAMELESS_REVENANT:
					if ((st.getCond() == 1) && (st.getItemsCount(RIPPED_DIARY) < 7) && (st.getItemsCount(BOOK_OF_REFORM) >= 1))
					{
						if (st.getItemsCount(RIPPED_DIARY) == 6)
						{
							st.set("cond", "2");
							addSpawn(ARURAUNE, 53668, 143283, -3863, 0, false, 0);
							st.takeItems(RIPPED_DIARY, st.getItemsCount(RIPPED_DIARY));
						}
						else
						{
							st.giveItems(RIPPED_DIARY, 1);
						}
						st.playSound(PlaySoundType.QUEST_ITEMGET);
					}
					break;
				
				case ARURAUNE:
					if ((st.getCond() == 2) && (st.getItemsCount(HUGE_NAIL) == 0))
					{
						st.giveItems(HUGE_NAIL, 1);
						st.playSound(PlaySoundType.QUEST_MIDDLE);
						st.set("cond", "3");
					}
					break;
				
				case OL_MAHUM_INSPECTOR:
					if (st.getCond() == 6)
					{
						st.set("cond", "7");
					}
					break;
				
				case OL_MAHUM_BETRAYER:
					if (st.getCond() == 8)
					{
						st.giveItems(LETTER_OF_BETRAYER, 1);
						st.set("cond", "9");
					}
					break;
				
				case CRIMSON_WEREWOLF:
					if (st.getCond() == 11)
					{
						st.set("cond", "12");
					}
					break;
				
				case KRUDEL_LIZARDMAN:
					if (st.getCond() == 13)
					{
						st.set("cond", "15");
					}
					break;
				
				case MISERY_SKELETON:
					if ((st.getCond() == 17) && (st.getItemsCount(BONE_FRAGMENT7) == 0))
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
						st.giveItems(BONE_FRAGMENT7, 1);
					}
					break;
				
				case SKELETON_ARCHER:
					if ((st.getCond() == 17) && (st.getItemsCount(BONE_FRAGMENT8) == 0))
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
						st.giveItems(BONE_FRAGMENT8, 1);
					}
					break;
				
				case SKELETON_MARKSMAN:
					if ((st.getCond() == 17) && (st.getItemsCount(BONE_FRAGMENT6) == 0))
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
						st.giveItems(BONE_FRAGMENT6, 1);
					}
					break;
				
				case SKELETON_LORD:
					if ((st.getCond() == 17) && (st.getItemsCount(BONE_FRAGMENT5) == 0))
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
						st.giveItems(BONE_FRAGMENT5, 1);
					}
					break;
				
				case SILENT_HORROR:
					if ((st.getCond() == 17) && (st.getItemsCount(BONE_FRAGMENT4) == 0))
					{
						st.playSound(PlaySoundType.QUEST_ITEMGET);
						st.giveItems(BONE_FRAGMENT4, 1);
					}
					break;
			}
		}
		return null;
	}
}
