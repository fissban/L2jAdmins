package l2j.gameserver.scripts.quests.normal;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.base.Race;
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

public class Q104_SpiritOfMirrors extends Script
{
	// ITEMs
	private static final int GALLINS_OAK_WAND = 748;
	private static final int WAND_SPIRITBOUND_1 = 1135;
	private static final int WAND_SPIRITBOUND_2 = 1136;
	private static final int WAND_SPIRITBOUND_3 = 1137;
	// REWARDs
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int WAND_OF_ADEPT = 747;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	// NPCs
	private static final int GALLINT = 7017;
	private static final int ARNOLD = 7041;
	private static final int JOHNSTONE = 7043;
	private static final int KENYOS = 7045;
	// MOBs
	private static final int SPIRIT_OF_MIRROR1 = 5003;
	private static final int SPIRIT_OF_MIRROR2 = 5004;
	private static final int SPIRIT_OF_MIRROR3 = 5005;
	
	public Q104_SpiritOfMirrors()
	{
		super(104, "Spirit of Mirrors");
		
		registerItems(GALLINS_OAK_WAND, WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3);
		
		addStartNpc(GALLINT);
		addTalkId(GALLINT, ARNOLD, JOHNSTONE, KENYOS);
		addKillId(SPIRIT_OF_MIRROR1, SPIRIT_OF_MIRROR2, SPIRIT_OF_MIRROR3);
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
		
		if (event.equalsIgnoreCase("7017-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(GALLINS_OAK_WAND, 1);
			st.giveItems(GALLINS_OAK_WAND, 1);
			st.giveItems(GALLINS_OAK_WAND, 1);
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
				if (player.getRace() != Race.HUMAN)
				{
					htmltext = "7017-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "7017-02.htm";
				}
				else
				{
					htmltext = "7017-06.htm";
				}
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case GALLINT:
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "7017-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7017-05.htm";
							
							st.takeItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3);
							
							st.giveItems(WAND_OF_ADEPT, 1);
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
									st.playSound("tutorial_voice_027");
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
					
					case KENYOS:
					case JOHNSTONE:
					case ARNOLD:
						htmltext = npc.getId() + "-01.htm";
						if (cond == 1)
						{
							st.set("cond", "2");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
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
		
		if (st.getItemEquipped(ParpedollType.RHAND) == GALLINS_OAK_WAND)
		{
			switch (npc.getId())
			{
				case SPIRIT_OF_MIRROR1:
					if (!st.hasItems(WAND_SPIRITBOUND_1))
					{
						st.takeItems(GALLINS_OAK_WAND, 1);
						st.giveItems(WAND_SPIRITBOUND_1, 1);
						
						if (st.hasItems(WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3))
						{
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case SPIRIT_OF_MIRROR2:
					if (!st.hasItems(WAND_SPIRITBOUND_2))
					{
						st.takeItems(GALLINS_OAK_WAND, 1);
						st.giveItems(WAND_SPIRITBOUND_2, 1);
						
						if (st.hasItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_3))
						{
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
				
				case SPIRIT_OF_MIRROR3:
					if (!st.hasItems(WAND_SPIRITBOUND_3))
					{
						st.takeItems(GALLINS_OAK_WAND, 1);
						st.giveItems(WAND_SPIRITBOUND_3, 1);
						
						if (st.hasItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2))
						{
							st.set("cond", "3");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_ITEMGET);
						}
					}
					break;
			}
		}
		
		return null;
	}
	
}
