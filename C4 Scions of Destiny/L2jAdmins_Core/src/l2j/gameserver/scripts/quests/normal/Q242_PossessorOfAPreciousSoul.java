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
public class Q242_PossessorOfAPreciousSoul extends Script
{
	// NPCs
	private static final int VIRGIL = 8742;
	private static final int KASSANDRA = 8743;
	private static final int OGMAR = 8744;
	private static final int MYSTERIOUS_KNIGHT = 8751;
	private static final int ANGEL_CORPSE = 8752;
	private static final int KALIS = 7759;
	private static final int MATILD = 7738;
	private static final int CORNERSTONE = 8748;
	private static final int FALLEN_UNICORN = 8746;
	private static final int PURE_UNICORN = 8747;
	// MONSTERs
	private static final int RESTRAINER_OF_GLORY = 5317;
	// ITEMs
	private static final int VIRGIL_LETTER = 7677;
	private static final int GOLDEN_HAIR = 7590;
	private static final int SORCERY_INGREDIENT = 7596;
	private static final int ORB_OF_BINDING = 7595;
	private static final int CARADINE_LETTER = 7678;
	
	private static boolean unicorn = false;
	
	public Q242_PossessorOfAPreciousSoul()
	{
		super(242, "Possessor of a Precious Soul - 2");
		
		registerItems(GOLDEN_HAIR, SORCERY_INGREDIENT, ORB_OF_BINDING);
		
		addStartNpc(VIRGIL);
		addTalkId(VIRGIL, KASSANDRA, OGMAR, MYSTERIOUS_KNIGHT, ANGEL_CORPSE, KALIS, MATILD, CORNERSTONE, FALLEN_UNICORN, PURE_UNICORN);
		addKillId(RESTRAINER_OF_GLORY);
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
		
		// Kasandra
		if (event.equalsIgnoreCase("8743-05.htm"))
		{
			st.set("cond", "2");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Ogmar
		else if (event.equalsIgnoreCase("8744-02.htm"))
		{
			st.set("cond", "3");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Mysterious Knight
		else if (event.equalsIgnoreCase("8751-02.htm"))
		{
			st.set("cond", "4");
			st.set("angel", "0");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		// Kalis
		else if (event.equalsIgnoreCase("7759-02.htm"))
		{
			st.set("cond", "7");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("7759-05.htm"))
		{
			if (st.hasItems(SORCERY_INGREDIENT))
			{
				st.set("orb", "0");
				st.set("cornerstone", "0");
				st.set("cond", "9");
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.takeItems(GOLDEN_HAIR, 1);
				st.takeItems(SORCERY_INGREDIENT, 1);
			}
			else
			{
				st.set("cond", "7");
				htmltext = "7759-02.htm";
			}
		}
		// Matild
		else if (event.equalsIgnoreCase("7738-02.htm"))
		{
			st.set("cond", "8");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			st.giveItems(SORCERY_INGREDIENT, 1);
		}
		// Cornerstone
		else if (event.equalsIgnoreCase("8748-03.htm"))
		{
			if (st.hasItems(ORB_OF_BINDING))
			{
				npc.deleteMe();
				st.takeItems(ORB_OF_BINDING, 1);
				
				int cornerstones = st.getInt("cornerstone");
				cornerstones++;
				if (cornerstones == 4)
				{
					st.unset("orb");
					st.unset("cornerstone");
					st.set("cond", "10");
					st.playSound(PlaySoundType.QUEST_MIDDLE);
				}
				else
				{
					st.set("cornerstone", Integer.toString(cornerstones));
				}
			}
			else
			{
				htmltext = null;
			}
		}
		// Spawn Pure Unicorn
		else if (event.equalsIgnoreCase("spu"))
		{
			addSpawn(PURE_UNICORN, 85884, -76588, -3470, 0, false, 0);
			return null;
		}
		// Despawn Pure Unicorn
		else if (event.equalsIgnoreCase("dspu"))
		{
			npc.getSpawn().stopRespawn();
			npc.deleteMe();
			startTimer("sfu", 2000, null, player, false);
			return null;
		}
		// Spawn Fallen Unicorn
		else if (event.equalsIgnoreCase("sfu"))
		{
			npc = addSpawn(FALLEN_UNICORN, 85884, -76588, -3470, 0, false, 0);
			npc.getSpawn().startRespawn();
			return null;
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
				if (st.hasItems(VIRGIL_LETTER))
				{
					if (!player.isSubClassActive() || (player.getLevel() < 60))
					{
						htmltext = "8742-02.htm";
					}
					else
					{
						htmltext = "8742-03.htm";
						st.setState(ScriptStateType.STARTED);
						st.set("cond", "1");
						st.playSound(PlaySoundType.QUEST_ACCEPT);
						st.takeItems(VIRGIL_LETTER, 1);
					}
				}
				break;
			
			case STARTED:
				if (!player.isSubClassActive())
				{
					break;
				}
				
				final int cond = st.getInt("cond");
				switch (npc.getId())
				{
					case VIRGIL:
						if (cond == 1)
						{
							htmltext = "8742-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8742-05.htm";
						}
						break;
					
					case KASSANDRA:
						if (cond == 1)
						{
							htmltext = "8743-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "8743-06.htm";
						}
						else if (cond == 11)
						{
							htmltext = "8743-07.htm";
							st.giveItems(CARADINE_LETTER, 1);
							st.rewardExpAndSp(455764, 0);
							player.broadcastPacket(new SocialAction(player.getObjectId(), SocialActionType.VICTORY));
							st.playSound(PlaySoundType.QUEST_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case OGMAR:
						if (cond == 2)
						{
							htmltext = "8744-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "8744-03.htm";
						}
						break;
					
					case MYSTERIOUS_KNIGHT:
						if (cond == 3)
						{
							htmltext = "8751-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "8751-03.htm";
						}
						else if (cond == 5)
						{
							if (st.hasItems(GOLDEN_HAIR))
							{
								htmltext = "8751-04.htm";
								st.set("cond", "6");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
							else
							{
								htmltext = "8751-03.htm";
								st.set("cond", "4");
							}
						}
						else if (cond == 6)
						{
							htmltext = "8751-05.htm";
						}
						break;
					
					case ANGEL_CORPSE:
						if (cond == 4)
						{
							npc.deleteMe();
							int hair = st.getInt("angel");
							hair++;
							
							if (hair == 4)
							{
								htmltext = "8752-02.htm";
								st.unset("angel");
								st.set("cond", "5");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
								st.giveItems(GOLDEN_HAIR, 1);
							}
							else
							{
								st.set("angel", Integer.toString(hair));
								htmltext = "8752-01.htm";
							}
						}
						else if (cond == 5)
						{
							htmltext = "8752-01.htm";
						}
						break;
					
					case KALIS:
						if (cond == 6)
						{
							htmltext = "7759-01.htm";
						}
						else if (cond == 7)
						{
							htmltext = "7759-03.htm";
						}
						else if (cond == 8)
						{
							if (st.hasItems(SORCERY_INGREDIENT))
							{
								htmltext = "7759-04.htm";
							}
							else
							{
								htmltext = "7759-03.htm";
								st.set("cond", "7");
							}
						}
						else if (cond == 9)
						{
							htmltext = "7759-06.htm";
						}
						break;
					
					case MATILD:
						if (cond == 7)
						{
							htmltext = "7738-01.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7738-03.htm";
						}
						break;
					
					case CORNERSTONE:
						if (cond == 9)
						{
							if (st.hasItems(ORB_OF_BINDING))
							{
								htmltext = "8748-02.htm";
							}
							else
							{
								htmltext = "8748-01.htm";
							}
						}
						break;
					
					case FALLEN_UNICORN:
						if (cond == 9)
						{
							htmltext = "8746-01.htm";
						}
						else if (cond == 10)
						{
							if (!unicorn) // Global variable check to prevent multiple spawns
							{
								unicorn = true;
								npc.getSpawn().stopRespawn(); // Despawn fallen unicorn
								npc.deleteMe();
								startTimer("spu", 3000, npc, player, false);
							}
							htmltext = "8746-02.htm";
						}
						break;
					
					case PURE_UNICORN:
						if (cond == 10)
						{
							st.set("cond", "11");
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							if (unicorn) // Global variable check to prevent multiple spawns
							{
								unicorn = false;
								startTimer("dspu", 3000, npc, player, false);
							}
							htmltext = "8747-01.htm";
						}
						else if (cond == 11)
						{
							htmltext = "8747-02.htm";
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
		final ScriptState st = checkPlayerCondition(player, npc, "cond", "9");
		if ((st == null) || !player.isSubClassActive())
		{
			return null;
		}
		
		int orbs = st.getInt("orb"); // check orbs internally, because player can use them before he gets them all
		if (orbs < 4)
		{
			orbs++;
			st.set("orb", Integer.toString(orbs));
			st.playSound(PlaySoundType.QUEST_ITEMGET);
			st.giveItems(ORB_OF_BINDING, 1);
		}
		
		return null;
	}
	
}
