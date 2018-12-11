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
public class Q402_PathToAHumanKnight extends Script
{
	// Items
	private static final int SWORD_OF_RITUAL = 1161;
	private static final int COIN_OF_LORDS_1 = 1162;
	private static final int COIN_OF_LORDS_2 = 1163;
	private static final int COIN_OF_LORDS_3 = 1164;
	private static final int COIN_OF_LORDS_4 = 1165;
	private static final int COIN_OF_LORDS_5 = 1166;
	private static final int COIN_OF_LORDS_6 = 1167;
	private static final int GLUDIO_GUARD_MARK_1 = 1168;
	private static final int BUGBEAR_NECKLACE = 1169;
	private static final int EINHASAD_CHURCH_MARK_1 = 1170;
	private static final int EINHASAD_CRUCIFIX = 1171;
	private static final int GLUDIO_GUARD_MARK_2 = 1172;
	private static final int SPIDER_LEG = 1173;
	private static final int EINHASAD_CHURCH_MARK_2 = 1174;
	private static final int LIZARDMAN_TOTEM = 1175;
	private static final int GLUDIO_GUARD_MARK_3 = 1176;
	private static final int GIANT_SPIDER_HUSK = 1177;
	private static final int EINHASAD_CHURCH_MARK_3 = 1178;
	private static final int HORRIBLE_SKULL = 1179;
	private static final int MARK_OF_ESQUIRE = 1271;
	
	// NPCs
	private static final int SIR_KLAUS_VASPER = 7417;
	private static final int BATHIS = 7332;
	private static final int RAYMOND = 7289;
	private static final int BEZIQUE = 7379;
	private static final int LEVIAN = 7037;
	private static final int GILBERT = 7039;
	private static final int BIOTIN = 7031;
	private static final int SIR_AARON_TANFORD = 7653;
	private static final int SIR_COLLIN_WINDAWOOD = 7311;
	
	public Q402_PathToAHumanKnight()
	{
		super(402, "Path to a Human Knight");
		
		registerItems(MARK_OF_ESQUIRE, COIN_OF_LORDS_1, COIN_OF_LORDS_2, COIN_OF_LORDS_3, COIN_OF_LORDS_4, COIN_OF_LORDS_5, COIN_OF_LORDS_6, GLUDIO_GUARD_MARK_1, BUGBEAR_NECKLACE, EINHASAD_CHURCH_MARK_1, EINHASAD_CRUCIFIX, GLUDIO_GUARD_MARK_2, SPIDER_LEG, EINHASAD_CHURCH_MARK_2, LIZARDMAN_TOTEM, GLUDIO_GUARD_MARK_3, GIANT_SPIDER_HUSK, EINHASAD_CHURCH_MARK_3, LIZARDMAN_TOTEM, GLUDIO_GUARD_MARK_3, GIANT_SPIDER_HUSK, EINHASAD_CHURCH_MARK_3, HORRIBLE_SKULL);
		
		addStartNpc(SIR_KLAUS_VASPER);
		addTalkId(SIR_KLAUS_VASPER, BATHIS, RAYMOND, BEZIQUE, LEVIAN, GILBERT, BIOTIN, SIR_AARON_TANFORD, SIR_COLLIN_WINDAWOOD);
		
		addKillId(775, 5024, 38, 43, 50, 30, 27, 24, 103, 106, 108, 404);
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
		
		if (event.equalsIgnoreCase("7417-05.htm"))
		{
			if (player.getClassId() != ClassId.HUMAN_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.KNIGHT ? "7417-02a.htm" : "7417-03.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7417-02.htm";
			}
			else if (st.hasItems(SWORD_OF_RITUAL))
			{
				htmltext = "7417-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7417-08.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(MARK_OF_ESQUIRE, 1);
		}
		else if (event.equalsIgnoreCase("7332-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(GLUDIO_GUARD_MARK_1, 1);
		}
		else if (event.equalsIgnoreCase("7289-03.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(EINHASAD_CHURCH_MARK_1, 1);
		}
		else if (event.equalsIgnoreCase("7379-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(GLUDIO_GUARD_MARK_2, 1);
		}
		else if (event.equalsIgnoreCase("7037-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(EINHASAD_CHURCH_MARK_2, 1);
		}
		else if (event.equalsIgnoreCase("7039-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(GLUDIO_GUARD_MARK_3, 1);
		}
		else if (event.equalsIgnoreCase("7031-02.htm"))
		{
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(EINHASAD_CHURCH_MARK_3, 1);
		}
		else if (event.equalsIgnoreCase("7417-13.htm") || event.equalsIgnoreCase("7417-14.htm"))
		{
			final int coinCount = st.getItemsCount(COIN_OF_LORDS_1) + st.getItemsCount(COIN_OF_LORDS_2) + st.getItemsCount(COIN_OF_LORDS_3) + st.getItemsCount(COIN_OF_LORDS_4) + st.getItemsCount(COIN_OF_LORDS_5) + st.getItemsCount(COIN_OF_LORDS_6);
			
			st.takeItems(COIN_OF_LORDS_1, -1);
			st.takeItems(COIN_OF_LORDS_2, -1);
			st.takeItems(COIN_OF_LORDS_3, -1);
			st.takeItems(COIN_OF_LORDS_4, -1);
			st.takeItems(COIN_OF_LORDS_5, -1);
			st.takeItems(COIN_OF_LORDS_6, -1);
			st.takeItems(MARK_OF_ESQUIRE, 1);
			st.giveItems(SWORD_OF_RITUAL, 1);
			st.rewardExpAndSp(3200, 1500 + (1920 * (coinCount - 3)));
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
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
				htmltext = "7417-01.htm";
				break;
			
			case STARTED:
				switch (npc.getId())
				{
					case SIR_KLAUS_VASPER:
						final int coins = st.getItemsCount(COIN_OF_LORDS_1) + st.getItemsCount(COIN_OF_LORDS_2) + st.getItemsCount(COIN_OF_LORDS_3) + st.getItemsCount(COIN_OF_LORDS_4) + st.getItemsCount(COIN_OF_LORDS_5) + st.getItemsCount(COIN_OF_LORDS_6);
						if (coins < 3)
						{
							htmltext = "7417-09.htm";
						}
						else if (coins == 3)
						{
							htmltext = "7417-10.htm";
						}
						else if ((coins > 3) && (coins < 6))
						{
							htmltext = "7417-11.htm";
						}
						else if (coins == 6)
						{
							htmltext = "7417-12.htm";
							st.takeItems(COIN_OF_LORDS_1, -1);
							st.takeItems(COIN_OF_LORDS_2, -1);
							st.takeItems(COIN_OF_LORDS_3, -1);
							st.takeItems(COIN_OF_LORDS_4, -1);
							st.takeItems(COIN_OF_LORDS_5, -1);
							st.takeItems(COIN_OF_LORDS_6, -1);
							st.takeItems(MARK_OF_ESQUIRE, 1);
							st.giveItems(SWORD_OF_RITUAL, 1);
							st.rewardExpAndSp(3200, 7260);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case BATHIS:
						if (st.hasItems(COIN_OF_LORDS_1))
						{
							htmltext = "7332-05.htm";
						}
						else if (st.hasItems(GLUDIO_GUARD_MARK_1))
						{
							if (st.getItemsCount(BUGBEAR_NECKLACE) < 10)
							{
								htmltext = "7332-03.htm";
							}
							else
							{
								htmltext = "7332-04.htm";
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(BUGBEAR_NECKLACE, -1);
								st.takeItems(GLUDIO_GUARD_MARK_1, 1);
								st.giveItems(COIN_OF_LORDS_1, 1);
							}
						}
						else
						{
							htmltext = "7332-01.htm";
						}
						break;
					
					case RAYMOND:
						if (st.hasItems(COIN_OF_LORDS_2))
						{
							htmltext = "7289-06.htm";
						}
						else if (st.hasItems(EINHASAD_CHURCH_MARK_1))
						{
							if (st.getItemsCount(EINHASAD_CRUCIFIX) < 12)
							{
								htmltext = "7289-04.htm";
							}
							else
							{
								htmltext = "7289-05.htm";
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(EINHASAD_CRUCIFIX, -1);
								st.takeItems(EINHASAD_CHURCH_MARK_1, 1);
								st.giveItems(COIN_OF_LORDS_2, 1);
							}
						}
						else
						{
							htmltext = "7289-01.htm";
						}
						break;
					
					case BEZIQUE:
						if (st.hasItems(COIN_OF_LORDS_3))
						{
							htmltext = "7379-05.htm";
						}
						else if (st.hasItems(GLUDIO_GUARD_MARK_2))
						{
							if (st.getItemsCount(SPIDER_LEG) < 20)
							{
								htmltext = "7379-03.htm";
							}
							else
							{
								htmltext = "7379-04.htm";
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(SPIDER_LEG, -1);
								st.takeItems(GLUDIO_GUARD_MARK_2, 1);
								st.giveItems(COIN_OF_LORDS_3, 1);
							}
						}
						else
						{
							htmltext = "7379-01.htm";
						}
						break;
					
					case LEVIAN:
						if (st.hasItems(COIN_OF_LORDS_4))
						{
							htmltext = "7037-05.htm";
						}
						else if (st.hasItems(EINHASAD_CHURCH_MARK_2))
						{
							if (st.getItemsCount(LIZARDMAN_TOTEM) < 20)
							{
								htmltext = "7037-03.htm";
							}
							else
							{
								htmltext = "7037-04.htm";
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(LIZARDMAN_TOTEM, -1);
								st.takeItems(EINHASAD_CHURCH_MARK_2, 1);
								st.giveItems(COIN_OF_LORDS_4, 1);
							}
						}
						else
						{
							htmltext = "7037-01.htm";
						}
						break;
					
					case GILBERT:
						if (st.hasItems(COIN_OF_LORDS_5))
						{
							htmltext = "7039-05.htm";
						}
						else if (st.hasItems(GLUDIO_GUARD_MARK_3))
						{
							if (st.getItemsCount(GIANT_SPIDER_HUSK) < 20)
							{
								htmltext = "7039-03.htm";
							}
							else
							{
								htmltext = "7039-04.htm";
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(GIANT_SPIDER_HUSK, -1);
								st.takeItems(GLUDIO_GUARD_MARK_3, 1);
								st.giveItems(COIN_OF_LORDS_5, 1);
							}
						}
						else
						{
							htmltext = "7039-01.htm";
						}
						break;
					
					case BIOTIN:
						if (st.hasItems(COIN_OF_LORDS_6))
						{
							htmltext = "7031-05.htm";
						}
						else if (st.hasItems(EINHASAD_CHURCH_MARK_3))
						{
							if (st.getItemsCount(HORRIBLE_SKULL) < 10)
							{
								htmltext = "7031-03.htm";
							}
							else
							{
								htmltext = "7031-04.htm";
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.takeItems(HORRIBLE_SKULL, -1);
								st.takeItems(EINHASAD_CHURCH_MARK_3, 1);
								st.giveItems(COIN_OF_LORDS_6, 1);
							}
						}
						else
						{
							htmltext = "7031-01.htm";
						}
						break;
					
					case SIR_AARON_TANFORD:
						htmltext = "7653-01.htm";
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
			case 775: // Bugbear Raider
				if (st.hasItems(GLUDIO_GUARD_MARK_1))
				{
					st.dropItemsAlways(BUGBEAR_NECKLACE, 1, 10);
				}
				break;
			
			case 5024: // Undead Priest
				if (st.hasItems(EINHASAD_CHURCH_MARK_1))
				{
					st.dropItems(EINHASAD_CRUCIFIX, 1, 12, 500000);
				}
				break;
			
			case 38: // Poison Spider
			case 43: // Arachnid Tracker
			case 50: // Arachnid Predator
				if (st.hasItems(GLUDIO_GUARD_MARK_2))
				{
					st.dropItemsAlways(SPIDER_LEG, 1, 20);
				}
				break;
			
			case 30: // Langk Lizardman
			case 27: // Langk Lizardman Scout
			case 24: // Langk Lizardman Warrior
				if (st.hasItems(EINHASAD_CHURCH_MARK_2))
				{
					st.dropItems(LIZARDMAN_TOTEM, 1, 20, 500000);
				}
				break;
			
			case 103: // Giant Spider
			case 106: // Talon Spider
			case 108: // Blade Spider
				if (st.hasItems(GLUDIO_GUARD_MARK_3))
				{
					st.dropItems(GIANT_SPIDER_HUSK, 1, 20, 400000);
				}
				break;
			
			case 404: // Silent Horror
				if (st.hasItems(EINHASAD_CHURCH_MARK_3))
				{
					st.dropItems(HORRIBLE_SKULL, 1, 10, 400000);
				}
				break;
		}
		
		return null;
	}
	
}
