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
import l2j.util.Rnd;

/**
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q403_PathToARogue extends Script
{
	// Items
	private static final int BEZIQUE_LETTER = 1180;
	private static final int NETI_BOW = 1181;
	private static final int NETI_DAGGER = 1182;
	private static final int SPARTOI_BONES = 1183;
	private static final int HORSESHOE_OF_LIGHT = 1184;
	private static final int MOST_WANTED_LIST = 1185;
	private static final int STOLEN_JEWELRY = 1186;
	private static final int STOLEN_TOMES = 1187;
	private static final int STOLEN_RING = 1188;
	private static final int STOLEN_NECKLACE = 1189;
	private static final int BEZIQUE_RECOMMENDATION = 1190;
	
	// NPCs
	private static final int BEZIQUE = 7379;
	private static final int NETI = 7425;
	
	public Q403_PathToARogue()
	{
		super(403, "Path to a Rogue");
		
		registerItems(BEZIQUE_LETTER, NETI_BOW, NETI_DAGGER, SPARTOI_BONES, HORSESHOE_OF_LIGHT, MOST_WANTED_LIST, STOLEN_JEWELRY, STOLEN_TOMES, STOLEN_RING, STOLEN_NECKLACE);
		
		addStartNpc(BEZIQUE);
		addTalkId(BEZIQUE, NETI);
		
		addKillId(35, 42, 45, 51, 54, 60, 5038);
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
		
		if (event.equalsIgnoreCase("7379-05.htm"))
		{
			if (player.getClassId() != ClassId.HUMAN_FIGHTER)
			{
				htmltext = player.getClassId() == ClassId.ROGUE ? "7379-02a.htm" : "7379-02.htm";
			}
			else if (player.getLevel() < 19)
			{
				htmltext = "7379-02.htm";
			}
			else if (st.hasItems(BEZIQUE_RECOMMENDATION))
			{
				htmltext = "7379-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7379-06.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(BEZIQUE_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7425-05.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(NETI_BOW, 1);
			st.giveItems(NETI_DAGGER, 1);
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
				htmltext = "7379-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case BEZIQUE:
						if (cond == 1)
						{
							htmltext = "7379-07.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "7379-10.htm";
						}
						else if (cond == 4)
						{
							htmltext = "7379-08.htm";
							st.set("cond", "5");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(HORSESHOE_OF_LIGHT, 1);
							st.giveItems(MOST_WANTED_LIST, 1);
						}
						else if (cond == 5)
						{
							htmltext = "7379-11.htm";
						}
						else if (cond == 6)
						{
							htmltext = "7379-09.htm";
							st.takeItems(NETI_BOW, 1);
							st.takeItems(NETI_DAGGER, 1);
							st.takeItems(STOLEN_JEWELRY, 1);
							st.takeItems(STOLEN_NECKLACE, 1);
							st.takeItems(STOLEN_RING, 1);
							st.takeItems(STOLEN_TOMES, 1);
							st.giveItems(BEZIQUE_RECOMMENDATION, 1);
							st.rewardExpAndSp(3200, 1500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case NETI:
						if (cond == 1)
						{
							htmltext = "7425-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7425-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7425-07.htm";
							st.set("cond", "4");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.takeItems(SPARTOI_BONES, 10);
							st.giveItems(HORSESHOE_OF_LIGHT, 1);
						}
						else if (cond > 3)
						{
							htmltext = "7425-08.htm";
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
		
		if ((st.getItemEquipped(ParpedollType.RHAND) == NETI_BOW) || (st.getItemEquipped(ParpedollType.RHAND) == NETI_DAGGER))
		{
			switch (npc.getId())
			{
				case 35:
				case 45:
				case 51:
					if ((st.getInt("cond") == 2) && st.dropItems(SPARTOI_BONES, 1, 10, 200000))
					{
						st.set("cond", "3");
					}
					break;
				
				case 42:
					if ((st.getInt("cond") == 2) && st.dropItems(SPARTOI_BONES, 1, 10, 300000))
					{
						st.set("cond", "3");
					}
					break;
				
				case 54:
				case 60:
					if ((st.getInt("cond") == 2) && st.dropItems(SPARTOI_BONES, 1, 10, 800000))
					{
						st.set("cond", "3");
					}
					break;
				
				case 5038:
					if (st.getInt("cond") == 5)
					{
						final int randomItem = Rnd.get(STOLEN_JEWELRY, STOLEN_NECKLACE);
						
						if (!st.hasItems(randomItem))
						{
							st.giveItems(randomItem, 1);
							
							if (st.hasItems(STOLEN_JEWELRY, STOLEN_TOMES, STOLEN_RING, STOLEN_NECKLACE))
							{
								st.set("cond", "6");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								st.playSound(PlaySoundType.QUEST_ITEMGET);
							}
						}
					}
					break;
			}
		}
		
		return null;
	}
	
}
