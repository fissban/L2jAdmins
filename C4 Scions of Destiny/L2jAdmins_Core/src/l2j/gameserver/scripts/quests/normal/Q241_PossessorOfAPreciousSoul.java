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
 * @author        MauroNOB
 * @author        CaFi
 * @originalQuest aCis
 */
public class Q241_PossessorOfAPreciousSoul extends Script
{
	// NPCs
	private static final int TALIEN = 8739;
	private static final int GABRIELLE = 7753;
	private static final int GILMORE = 7754;
	private static final int KANTABILON = 8042;
	private static final int STEDMIEL = 7692;
	private static final int VIRGIL = 8742;
	private static final int OGMAR = 8744;
	private static final int RAHORAKTI = 8336;
	private static final int KASSANDRA = 8743;
	private static final int CARADINE = 8740;
	private static final int NOEL = 8272;
	
	// Monsters
	private static final int BARAHAM = 5113;
	private static final int MALRUK_SUCCUBUS_1 = 244;
	private static final int MALRUK_SUCCUBUS_TUREN_1 = 245;
	private static final int MALRUK_SUCCUBUS_2 = 283;
	private static final int MALRUK_SUCCUBUS_TUREN_2 = 284;
	private static final int SPLINTER_STAKATO = 1508;
	private static final int SPLINTER_STAKATO_WALKER = 1509;
	private static final int SPLINTER_STAKATO_SOLDIER = 1510;
	private static final int SPLINTER_STAKATO_DRONE_1 = 1511;
	private static final int SPLINTER_STAKATO_DRONE_2 = 1512;
	
	// ITEMs
	private static final int LEGEND_OF_SEVENTEEN = 7587;
	private static final int MALRUK_SUCCUBUS_CLAW = 7597;
	private static final int ECHO_CRYSTAL = 7589;
	private static final int POETRY_BOOK = 7588;
	private static final int CRIMSON_MOSS = 7598;
	private static final int RAHORAKTI_MEDICINE = 7599;
	private static final int LUNARGENT = 6029;
	private static final int HELLFIRE_OIL = 6033;
	private static final int VIRGIL_LETTER = 7677;
	
	public Q241_PossessorOfAPreciousSoul()
	{
		super(241, "Possessor of a Precious Soul - 1");
		
		registerItems(LEGEND_OF_SEVENTEEN, MALRUK_SUCCUBUS_CLAW, ECHO_CRYSTAL, POETRY_BOOK, CRIMSON_MOSS, RAHORAKTI_MEDICINE);
		
		addStartNpc(TALIEN);
		addTalkId(TALIEN, GABRIELLE, GILMORE, KANTABILON, STEDMIEL, VIRGIL, OGMAR, RAHORAKTI, KASSANDRA, CARADINE, NOEL);
		
		addKillId(BARAHAM, MALRUK_SUCCUBUS_1, MALRUK_SUCCUBUS_2, MALRUK_SUCCUBUS_TUREN_1, MALRUK_SUCCUBUS_TUREN_2, SPLINTER_STAKATO, SPLINTER_STAKATO_WALKER, SPLINTER_STAKATO_SOLDIER, SPLINTER_STAKATO_DRONE_1, SPLINTER_STAKATO_DRONE_2);
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
		
		// Talien
		if (event.equalsIgnoreCase("8739-03.htm"))
		{
			st.setState(ScriptStateType.STARTED);
			st.set("cond", "1");
			st.playSound(PlaySoundType.QUEST_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8739-07.htm"))
		{
			st.set("cond", "5");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(LEGEND_OF_SEVENTEEN, 1);
		}
		else if (event.equalsIgnoreCase("8739-10.htm"))
		{
			st.set("cond", "9");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(ECHO_CRYSTAL, 1);
		}
		else if (event.equalsIgnoreCase("8739-13.htm"))
		{
			st.set("cond", "11");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(POETRY_BOOK, 1);
		}
		// Gabrielle
		else if (event.equalsIgnoreCase("7753-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Gilmore
		else if (event.equalsIgnoreCase("7754-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Kantabilon
		else if (event.equalsIgnoreCase("8042-02.htm"))
		{
			st.set("cond", "6");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8042-05.htm"))
		{
			st.set("cond", "8");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(MALRUK_SUCCUBUS_CLAW, -1);
			st.giveItems(ECHO_CRYSTAL, 1);
		}
		// Stedmiel
		else if (event.equalsIgnoreCase("7692-02.htm"))
		{
			st.set("cond", "10");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(POETRY_BOOK, 1);
		}
		// Virgil
		else if (event.equalsIgnoreCase("8742-02.htm"))
		{
			st.set("cond", "12");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8742-05.htm"))
		{
			st.set("cond", "18");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Ogmar
		else if (event.equalsIgnoreCase("8744-02.htm"))
		{
			st.set("cond", "13");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Rahorakti
		else if (event.equalsIgnoreCase("8336-02.htm"))
		{
			st.set("cond", "14");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8336-05.htm"))
		{
			st.set("cond", "16");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(CRIMSON_MOSS, -1);
			st.giveItems(RAHORAKTI_MEDICINE, 1);
		}
		// Kassandra
		else if (event.equalsIgnoreCase("8743-02.htm"))
		{
			st.set("cond", "17");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.takeItems(RAHORAKTI_MEDICINE, 1);
		}
		// Caradine
		else if (event.equalsIgnoreCase("8740-02.htm"))
		{
			st.set("cond", "19");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8740-05.htm"))
		{
			st.giveItems(VIRGIL_LETTER, 1);
			st.rewardExpAndSp(263043, 0);
			
			player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
			st.playSound(PlaySoundType.QUEST_FINISH);
			st.exitQuest(false);
		}
		// Noel
		else if (event.equalsIgnoreCase("8272-02.htm"))
		{
			st.set("cond", "20");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("8272-05.htm"))
		{
			if (st.hasItems(HELLFIRE_OIL) && (st.getItemsCount(LUNARGENT) >= 5))
			{
				st.set("cond", "21");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(LUNARGENT, 5);
				st.takeItems(HELLFIRE_OIL, 1);
			}
			else
			{
				htmltext = "8272-07.htm";
			}
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
				htmltext = !player.isSubClassActive() || (player.getLevel() < 50) ? "8739-02.htm" : "8739-01.htm";
				break;
			
			case STARTED:
				if (!player.isSubClassActive())
				{
					break;
				}
				
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case TALIEN:
						if (cond == 1)
						{
							htmltext = "8739-04.htm";
						}
						else if ((cond == 2) || (cond == 3))
						{
							htmltext = "8739-05.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8739-06.htm";
						}
						else if (cond == 5)
						{
							htmltext = "8739-08.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8739-09.htm";
						}
						else if (cond == 9)
						{
							htmltext = "8739-11.htm";
						}
						else if (cond == 10)
						{
							htmltext = "8739-12.htm";
						}
						else if (cond == 11)
						{
							htmltext = "8739-14.htm";
						}
						break;
					
					case GABRIELLE:
						if (cond == 1)
						{
							htmltext = "7753-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "7753-03.htm";
						}
						break;
					
					case GILMORE:
						if (cond == 2)
						{
							htmltext = "7754-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "7754-03.htm";
						}
						break;
					
					case KANTABILON:
						if (cond == 5)
						{
							htmltext = "8042-01.htm";
						}
						else if (cond == 6)
						{
							htmltext = "8042-03.htm";
						}
						else if (cond == 7)
						{
							htmltext = "8042-04.htm";
						}
						else if (cond == 8)
						{
							htmltext = "8042-06.htm";
						}
						break;
					
					case STEDMIEL:
						if (cond == 9)
						{
							htmltext = "7692-01.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7692-03.htm";
						}
						break;
					
					case VIRGIL:
						if (cond == 11)
						{
							htmltext = "8742-01.htm";
						}
						else if (cond == 12)
						{
							htmltext = "8742-03.htm";
						}
						else if (cond == 17)
						{
							htmltext = "8742-04.htm";
						}
						else if (cond == 18)
						{
							htmltext = "8742-06.htm";
						}
						break;
					
					case OGMAR:
						if (cond == 12)
						{
							htmltext = "8744-01.htm";
						}
						else if (cond == 13)
						{
							htmltext = "8744-03.htm";
						}
						break;
					
					case RAHORAKTI:
						if (cond == 13)
						{
							htmltext = "8336-01.htm";
						}
						else if (cond == 14)
						{
							htmltext = "8336-03.htm";
						}
						else if (cond == 15)
						{
							htmltext = "8336-04.htm";
						}
						else if (cond == 16)
						{
							htmltext = "8336-06.htm";
						}
						break;
					
					case KASSANDRA:
						if (cond == 16)
						{
							htmltext = "8743-01.htm";
						}
						else if (cond == 17)
						{
							htmltext = "8743-03.htm";
						}
						break;
					
					case CARADINE:
						if (cond == 18)
						{
							htmltext = "8740-01.htm";
						}
						else if (cond == 19)
						{
							htmltext = "8740-03.htm";
						}
						else if (cond == 21)
						{
							htmltext = "8740-04.htm";
						}
						break;
					
					case NOEL:
						if (cond == 19)
						{
							htmltext = "8272-01.htm";
						}
						else if (cond == 20)
						{
							if (st.hasItems(HELLFIRE_OIL) && (st.getItemsCount(LUNARGENT) >= 5))
							{
								htmltext = "8272-04.htm";
							}
							else
							{
								htmltext = "8272-03.htm";
							}
						}
						else if (cond == 21)
						{
							htmltext = "8272-06.htm";
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
		if ((st == null) || !player.isSubClassActive())
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case BARAHAM:
				if (st.getInt("cond") == 3)
				{
					st.set("cond", "4");
					st.giveItems(LEGEND_OF_SEVENTEEN, 1);
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
				break;
			
			case MALRUK_SUCCUBUS_1:
			case MALRUK_SUCCUBUS_2:
				if ((st.getInt("cond") == 6) && st.dropItems(MALRUK_SUCCUBUS_CLAW, 1, 10, 100000))
				{
					st.set("cond", "7");
				}
				break;
			
			case MALRUK_SUCCUBUS_TUREN_1:
			case MALRUK_SUCCUBUS_TUREN_2:
				if ((st.getInt("cond") == 6) && st.dropItems(MALRUK_SUCCUBUS_CLAW, 1, 10, 120000))
				{
					st.set("cond", "7");
				}
				break;
			
			case SPLINTER_STAKATO:
			case SPLINTER_STAKATO_WALKER:
			case SPLINTER_STAKATO_SOLDIER:
			case SPLINTER_STAKATO_DRONE_1:
			case SPLINTER_STAKATO_DRONE_2:
				if ((st.getInt("cond") == 14) && st.dropItems(CRIMSON_MOSS, 1, 5, 100000))
				{
					st.set("cond", "15");
				}
				break;
		}
		return null;
	}
	
}
