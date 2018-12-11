package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

public class Q103_SpiritOfCraftsman extends Script
{
	// ITEMs
	private static final int KARROD_LETTER = 968;
	private static final int CECKTINON_VOUCHER_1 = 969;
	private static final int CECKTINON_VOUCHER_2 = 970;
	private static final int SOUL_CATCHER = 971;
	private static final int PRESERVING_OIL = 972;
	private static final int ZOMBIE_HEAD = 973;
	private static final int STEELBENDER_HEAD = 974;
	private static final int BONE_FRAGMENT = 1107;
	// REWARDs
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int BLOODSABER = 975;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	// NPCs
	private static final int KARROD = 7307;
	private static final int CECKTINON = 7132;
	private static final int HARNE = 7144;
	// MOBs
	private static final int MARSH_ZOMBIE = 15;
	private static final int MARSH_ZOMBIE_LORD = 20;
	private static final int DOOM_SOLDIER = 455;
	private static final int SKELETON_HUNTER = 517;
	private static final int SKELETON_HUNTER_ARCHER = 518;
	
	public Q103_SpiritOfCraftsman()
	{
		super(103, "Spirit of Craftsman");
		
		registerItems(KARROD_LETTER, CECKTINON_VOUCHER_1, CECKTINON_VOUCHER_2, BONE_FRAGMENT, SOUL_CATCHER, PRESERVING_OIL, ZOMBIE_HEAD, STEELBENDER_HEAD);
		
		addStartNpc(KARROD);
		addTalkId(KARROD, CECKTINON, HARNE);
		addKillId(MARSH_ZOMBIE, MARSH_ZOMBIE_LORD, DOOM_SOLDIER, SKELETON_HUNTER, SKELETON_HUNTER_ARCHER);
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
		
		if (event.equalsIgnoreCase("7307-05.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(KARROD_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "7307-01.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "7307-02.htm";
				}
				else
				{
					htmltext = "7307-03.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case KARROD:
						if (cond < 8)
						{
							htmltext = "7307-06.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7307-07.htm";
							st.takeItems(STEELBENDER_HEAD, 1);
							st.giveItems(BLOODSABER, 1);
							st.rewardItems(LESSER_HEALING_POT, 100);
							
							if (player.isMageClass())
							{
								st.giveItems(SPIRITSHOT_NO_GRADE, 500);
							}
							else
							{
								st.giveItems(SOULSHOT_NO_GRADE, 1000);
							}
							
							if (player.isNewbie())
							{
								st.showQuestionMark(26);
								if (player.isMageClass())
								{
									st.playSound("tutorial_voice_027");// FIXME cambie playtutorialvoice por playsound
									st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
								}
								else
								{
									st.playSound("tutorial_voice_026");
									st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
								}
							}
							
							st.giveItems(ECHO_BATTLE, 10);
							st.giveItems(ECHO_LOVE, 10);
							st.giveItems(ECHO_SOLITUDE, 10);
							st.giveItems(ECHO_FEAST, 10);
							st.giveItems(ECHO_CELEBRATION, 10);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case CECKTINON:
						if (cond == 1)
						{
							htmltext = "7132-01.htm";
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(KARROD_LETTER, 1);
							st.giveItems(CECKTINON_VOUCHER_1, 1);
						}
						else if ((cond > 1) && (cond < 5))
						{
							htmltext = "7132-02.htm";
						}
						else if (cond == 5)
						{
							htmltext = "7132-03.htm";
							st.set("cond", "6");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SOUL_CATCHER, 1);
							st.giveItems(PRESERVING_OIL, 1);
						}
						else if (cond == 6)
						{
							htmltext = "7132-04.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7132-05.htm";
							st.set("cond", "8");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(ZOMBIE_HEAD, 1);
							st.giveItems(STEELBENDER_HEAD, 1);
						}
						else if (cond == 8)
						{
							htmltext = "7132-06.htm";
						}
						break;
					
					case HARNE:
						if (cond == 2)
						{
							htmltext = "7144-01.htm";
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CECKTINON_VOUCHER_1, 1);
							st.giveItems(CECKTINON_VOUCHER_2, 1);
						}
						else if (cond == 3)
						{
							htmltext = "7144-02.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7144-03.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(CECKTINON_VOUCHER_2, 1);
							st.takeItems(BONE_FRAGMENT, 10);
							st.giveItems(SOUL_CATCHER, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7144-04.htm";
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
		final ScriptState st = checkPlayerState(player, npc, ScriptStateType.STARTED);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case SKELETON_HUNTER:
			case SKELETON_HUNTER_ARCHER:
			case DOOM_SOLDIER:
				if ((st.getInt("cond") == 3) && st.dropItems(BONE_FRAGMENT, 1, 10, 300000))
				{
					st.set("cond", "4");
				}
				break;
			
			case MARSH_ZOMBIE:
			case MARSH_ZOMBIE_LORD:
				if ((st.getInt("cond") == 6) && st.dropItems(ZOMBIE_HEAD, 1, 1, 300000))
				{
					st.set("cond", "7");
					st.takeItems(PRESERVING_OIL, 1);
				}
				break;
		}
		
		return null;
	}
	
}
