package l2j.gameserver.scripts.quests.saga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;

/**
 * Original script in python
 * @author fissban, zarie
 */
public class ASaga extends Script
{
	private static final int[] ARCHON_MINIONS =
	{
		1646,
		1647,
		1648,
		1649,
		1650,
		1651
	};
	private static final int[] GUARDIAN_ANGELS =
	{
		5214,
		5215,
		5216
	};
	private static final int[] ARCHON_HELLSIHA_NORM =
	{
		13047,
		13049,
		13050,
		13051,
		13052
	};
	private static final int[] MOBS_NORM =
	{
		// GUARDIAN_ANGELS
		1646,
		1647,
		1648,
		1649,
		1650,
		1651,
		// ARCHON_MINIONS
		5214,
		5215,
		5216,
		// ARCHON_HELLSIHA_NORM
		13047,
		13049,
		13050,
		13051,
		13052
	};
	
	// rewards
	private static final int SECRET_BOOK_OF_GIANTS = 6622;
	private static final int ADENA_AMOUNT = 5000000;
	private static final int EXP_AMOUNT = 2299404;
	
	// All of these are overridden in the subclasses;
	public ClassId classId; // class which is conducting the mission
	public int[] npcs = new int[11];
	public int[] items = new int[11];
	
	public int[] mobs = new int[3];
	public LocationHolder[] mobsSpawnLoc = new LocationHolder[3];
	public String[] mobsSay = new String[18];
	public static Map<L2Npc, String> mobsSpawnList = new HashMap<>();
	
	// this function is called by subclasses in order to add their own NPCs;
	public ASaga(int id, String descr)
	{
		super(id, descr);
	}
	
	public void registerNpcs()
	{
		addStartNpc(npcs[0]);
		addAttackId(mobs[2]);
		addFirstTalkId(npcs[4]);
		addTalkId(npcs);
		addKillId(mobs);
		addKillId(MOBS_NORM);
		registerItems(items);
	}
	
	private static void cast(L2Npc npc, L2PcInstance target, int skillId, int level)
	{
		target.broadcastPacket(new MagicSkillUse(target, target, skillId, level, 6000, 1));
		target.broadcastPacket(new MagicSkillUse(npc, npc, skillId, level, 6000, 1));
	}
	
	private void addSpawn(ScriptState st, L2Npc mob)
	{
		mobsSpawnList.put(mob, st.getPlayer().getName());
	}
	
	private void deleteSpawn(L2Npc mob)
	{
		if (mobsSpawnList.containsKey(mob))
		{
			mobsSpawnList.remove(mob);
			mob.decayMe();
			mob.deleteMe();
		}
	}
	
	private void giveHallishaMark(ScriptState st)
	{
		if (st.getInt("spawned") == 0)
		{
			if (st.getItemsCount(items[3]) >= 700)
			{
				st.takeItems(items[3], 20);
				int x = st.getPlayer().getX();
				int y = st.getPlayer().getY();
				int z = st.getPlayer().getZ();
				L2Npc archon = addSpawn(mobs[1], x, y, z);
				
				st.set("Archon", String.valueOf(archon.getObjectId()));
				addSpawn(st, archon);
				st.set("spawned", "1");
				startTimer("Archon Hellisha has despawned", 600000, archon, st.getPlayer(), false);
				archon.broadcastNpcSay(mobsSay[13].replace("PLAYERNAME", st.getPlayer().getName()));
				((L2Attackable) archon).addDamageHate(st.getPlayer(), 0, 99999);
				archon.getAI().setIntention(CtrlIntentionType.ATTACK, st.getPlayer(), null);
			}
			else
			{
				st.giveItems(items[3], 1);
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
		}
	}
	
	private ScriptState findRightState(L2Npc mob)
	{
		if (mobsSpawnList.containsKey(mob))
		{
			final L2PcInstance player = L2World.getInstance().getPlayer(mobsSpawnList.get(mob));
			if (player != null)
			{
				return player.getScriptState(getName());
			}
		}
		return null;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (st == null)
		{
			return null;
		}
		
		String htmltext = "";
		
		if (event.equalsIgnoreCase("accept"))
		{
			st.set("cond", "1");
			st.set("spawned", "0");
			st.set("kills", "0");
			st.setState(ScriptStateType.STARTED);
			st.playSound(PlaySoundType.QUEST_ACCEPT);
			st.giveItems(items[10], 1);
			htmltext = "0-03.htm";
		}
		else if (event.equalsIgnoreCase("0-1"))
		{
			if (player.getLevel() < 76)
			{
				htmltext = "0-02.htm";
				st.exitQuest(true);
			}
			else
			{
				htmltext = "0-05.htm";
			}
		}
		else if (event.equalsIgnoreCase("0-2"))
		{
			if (player.getLevel() >= 76)
			{
				st.setState(ScriptStateType.COMPLETED);
				st.set("cond", "0");
				htmltext = "0-07.htm";
				st.takeItems(items[10], -1);
				st.rewardExpAndSp(EXP_AMOUNT, 0);
				st.giveItems(Inventory.ADENA_ID, ADENA_AMOUNT);
				st.giveItems(SECRET_BOOK_OF_GIANTS, 1);
				player.setClassId(classId.getId());
				if (!player.isSubClassActive() && (player.getBaseClass() == classId.getParent().getId()))
				{
					player.setBaseClass(classId);
				}
				player.broadcastUserInfo();
				cast(npc, player, 4339, 1);
			}
			else
			{
				st.takeItems(items[10], -1);
				st.playSound(PlaySoundType.QUEST_MIDDLE);
				st.set("cond", "20");
				htmltext = "0-08.htm";
			}
		}
		else if (event.equalsIgnoreCase("1-3"))
		{
			st.set("cond", "3");
			htmltext = "1-05.htm";
		}
		else if (event.equalsIgnoreCase("1-4"))
		{
			st.set("cond", "4");
			st.takeItems(items[0], 1);
			if (items[11] != 0)
			{
				st.takeItems(items[11], 1);
			}
			st.giveItems(items[1], 1);
			htmltext = "1-06.htm";
		}
		else if (event.equalsIgnoreCase("2-1"))
		{
			st.set("cond", "2");
			htmltext = "2-05.htm";
		}
		else if (event.equalsIgnoreCase("2-2"))
		{
			st.set("cond", "5");
			st.takeItems(items[1], 1);
			st.giveItems(items[4], 1);
			htmltext = "2-06.htm";
		}
		else if (event.equalsIgnoreCase("3-5"))
		{
			htmltext = "3-07.htm";
		}
		else if (event.equalsIgnoreCase("3-6"))
		{
			st.set("cond", "11");
			htmltext = "3-02.htm";
		}
		else if (event.equalsIgnoreCase("3-7"))
		{
			st.set("cond", "12");
			htmltext = "3-03.htm";
		}
		else if (event.equalsIgnoreCase("3-8"))
		{
			st.set("cond", "13");
			st.takeItems(items[2], 1);
			st.giveItems(items[7], 1);
			htmltext = "3-08.htm";
		}
		else if (event.equalsIgnoreCase("4-1"))
		{
			htmltext = "4-010.htm";
		}
		else if (event.equalsIgnoreCase("4-2"))
		{
			st.giveItems(items[9], 1);
			st.set("cond", "18");
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "4-011.htm";
		}
		else if (event.equalsIgnoreCase("4-3"))
		{
			npc.broadcastNpcSay(mobsSay[13].replace("PLAYERNAME", player.getName()));
			st.giveItems(items[9], 1);
			st.set("cond", "18");
			st.set("Quest0", "0");
			deleteSpawn(npc);
			if (getTimer("Mob_2 has despawned") != null)
			{
				getTimer("Mob_2 has despawned").cancel();
			}
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			
			return null;
		}
		else if (event.equalsIgnoreCase("5-1"))
		{
			cast(npc, player, 4546, 1);
			st.set("cond", "6");
			st.takeItems(items[4], 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "5-02.htm";
		}
		else if (event.equalsIgnoreCase("6-1"))
		{
			cast(npc, player, 4546, 1);
			st.set("cond", "8");
			st.takeItems(items[5], 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "6-03.htm";
		}
		else if (event.equalsIgnoreCase("7-1"))
		{
			if (st.getInt("spawned") == 1)
			{
				htmltext = "7-03.htm";
			}
			else if (st.getInt("spawned") == 0)
			{
				L2Npc mob = addSpawn(mobs[0], mobsSpawnLoc[0].getX(), mobsSpawnLoc[0].getY(), mobsSpawnLoc[0].getZ());
				st.set("Mob_1", String.valueOf(mob.getObjectId()));
				st.set("spawned", "1");
				startTimer("Mob_1 Timer 1", 500, mob, player, false);
				startTimer("Mob_1 has despawned", 300000, mob, player, false);
				addSpawn(st, mob);
				htmltext = "7-02.htm";
			}
			else
			{
				htmltext = "7-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7-2"))
		{
			cast(npc, player, 4546, 1);
			st.set("cond", "10");
			st.takeItems(items[6], 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "7-06.htm";
		}
		else if (event.equalsIgnoreCase("8-1"))
		{
			cast(npc, player, 4546, 1);
			st.set("cond", "14");
			st.takeItems(items[7], 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "8-02.htm";
		}
		else if (event.equalsIgnoreCase("9-1"))
		{
			cast(npc, player, 4546, 1);
			st.set("cond", "17");
			st.takeItems(items[8], 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "9-03.htm";
		}
		else if (event.equalsIgnoreCase("10-1"))
		{
			if (st.getInt("Quest0") == 0)
			{
				L2Npc mob1 = addSpawn(mobs[2], mobsSpawnLoc[1].getX(), mobsSpawnLoc[1].getY(), mobsSpawnLoc[1].getZ());
				L2Npc mob2 = addSpawn(npcs[4], mobsSpawnLoc[2].getX(), mobsSpawnLoc[2].getY(), mobsSpawnLoc[2].getZ());
				addSpawn(st, mob1);
				addSpawn(st, mob2);
				st.set("Mob_3", String.valueOf(mob1.getObjectId()));
				st.set("Mob_2", String.valueOf(mob2.getObjectId()));
				st.set("Quest0", "1");
				st.set("Quest", "45");
				startTimer("Mob_3 Timer 1", 500, mob1, player, false);
				startTimer("Mob_3 has despawned", 59000, mob1, player, false);
				startTimer("Mob_2 Timer 1", 500, mob2, player, false);
				startTimer("Mob_2 has despawned", 60000, mob2, player, false);
				htmltext = "10-02.htm";
			}
			else if (st.getInt("Quest") == 45)
			{
				htmltext = "10-03.htm";
			}
			else
			{
				htmltext = "10-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("10-2"))
		{
			st.set("cond", "19");
			st.takeItems(items[9], 1);
			cast(npc, player, 4546, 1);
			st.playSound(PlaySoundType.QUEST_MIDDLE);
			htmltext = "10-06.htm";
		}
		else if (event.equalsIgnoreCase("11-9"))
		{
			st.set("cond", "15");
			htmltext = "11-03.htm";
		}
		else if (event.equalsIgnoreCase("Mob_1 Timer 1"))
		{
			npc.broadcastNpcSay(mobsSay[0].replace("PLAYERNAME", player.getName()));
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_1 has despawned"))
		{
			npc.broadcastNpcSay(mobsSay[1].replace("PLAYERNAME", player.getName()));
			deleteSpawn(npc);
			st.set("spawned", "0");
			return null;
		}
		else if (event.equalsIgnoreCase("Archon of Hellisha has despawned"))
		{
			npc.broadcastNpcSay(mobsSay[6].replace("PLAYERNAME", player.getName()));
			deleteSpawn(npc);
			st.set("spawned", "0");
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_3 Timer 1"))
		{
			if (npc.getKnownList().getObject(npc))
			{
				((L2Attackable) npc).addDamageHate(npc, 0, 99999);
				npc.getAI().setIntention(CtrlIntentionType.ATTACK, npc, null);
				npc.broadcastNpcSay(mobsSay[14].replace("PLAYERNAME", player.getName()));
			}
			else
			{
				startTimer("Mob_3 Timer 1", 500, npc, player, false);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_3 has despawned"))
		{
			npc.broadcastNpcSay(mobsSay[15].replace("PLAYERNAME", player.getName()));
			st.set("Quest0", "0");
			npc.doDie(npc);
			deleteSpawn(npc);
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_2 Timer 1"))
		{
			npc.broadcastNpcSay(mobsSay[7].replace("PLAYERNAME", player.getName()));
			startTimer("Mob_2 Timer 2", 1500, npc, player, false);
			if (st.getInt("Quest") == 45)
			{
				st.set("Quest", "0");
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_2 Timer 2"))
		{
			npc.broadcastNpcSay(mobsSay[8].replace("PLAYERNAME", player.getName()));
			startTimer("Mob_2 Timer 3", 10000, npc, player, false);
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_2 Timer 3"))
		{
			if (st.getInt("Quest0") == 0)
			{
				startTimer("Mob_2 Timer 3", 13000, npc, player, false);
				if (Rnd.nextBoolean())
				{
					npc.broadcastNpcSay(mobsSay[9].replace("PLAYERNAME", player.getName()));
				}
				else
				{
					npc.broadcastNpcSay(mobsSay[10].replace("PLAYERNAME", player.getName()));
				}
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_2 has despawned"))
		{
			st.set("Quest", st.getInt("Quest") + 1);
			if ((st.getInt("Quest0") == 1) || (st.getInt("Quest0") == 2) || (st.getInt("Quest") > 3))
			{
				st.set("Quest0", "0");
				if (st.getInt("Quest0") == 1)
				{
					npc.broadcastNpcSay(mobsSay[11].replace("PLAYERNAME", player.getName()));
				}
				else
				{
					npc.broadcastNpcSay(mobsSay[12].replace("PLAYERNAME", player.getName()));
				}
				npc.doDie(npc);
				deleteSpawn(npc);
			}
			else
			{
				startTimer("Mob_2 has despawned", 1000, npc, player, false);
			}
			return null;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		String htmltext = "";
		
		if (st == null)
		{
			return getNoQuestMsg();
		}
		
		int npcId = npc.getId();
		int cond = st.getInt("cond");
		if ((st.getState() == ScriptStateType.COMPLETED) && (npcId == npcs[0]))
		{
			htmltext = "<html><body>You have already completed this quest!</body></html>";
		}
		else if (st.getPlayer().getClassId() == classId.getParent())
		{
			if (cond == 0)
			{
				if (npcId == npcs[0])
				{
					htmltext = "0-01.htm";
				}
			}
			else if (cond == 1)
			{
				if (npcId == npcs[0])
				{
					htmltext = "0-04.htm";
				}
				else if (npcId == npcs[2])
				{
					htmltext = "2-01.htm";
				}
			}
			else if (cond == 2)
			{
				if (npcId == npcs[2])
				{
					htmltext = "2-02.htm";
				}
				else if (npcId == npcs[1])
				{
					htmltext = "1-01.htm";
				}
			}
			else if (cond == 3)
			{
				if (npcId == npcs[1])
				{
					if (st.getItemsCount(items[0]) > 0)
					{
						if (items[11] == 0)
						{
							htmltext = "1-03.htm";
						}
						else if (st.getItemsCount(items[11]) > 0)
						{
							htmltext = "1-03.htm";
						}
						else
						{
							htmltext = "1-02.htm";
						}
					}
					else
					{
						htmltext = "1-02.htm";
					}
				}
			}
			else if (cond == 4)
			{
				if (npcId == npcs[1])
				{
					htmltext = "1-04.htm";
				}
				else if (npcId == npcs[2])
				{
					htmltext = "2-03.htm";
				}
			}
			else if (cond == 5)
			{
				if (npcId == npcs[2])
				{
					htmltext = "2-04.htm";
				}
				else if (npcId == npcs[5])
				{
					htmltext = "5-01.htm";
				}
			}
			else if (cond == 6)
			{
				if (npcId == npcs[5])
				{
					htmltext = "5-03.htm";
				}
				else if (npcId == npcs[6])
				{
					htmltext = "6-01.htm";
				}
			}
			else if (cond == 7)
			{
				if (npcId == npcs[6])
				{
					htmltext = "6-02.htm";
				}
			}
			else if (cond == 8)
			{
				if (npcId == npcs[6])
				{
					htmltext = "6-04.htm";
				}
				else if (npcId == npcs[7])
				{
					htmltext = "7-01.htm";
				}
			}
			else if (cond == 9)
			{
				if (npcId == npcs[7])
				{
					htmltext = "7-05.htm";
				}
			}
			else if (cond == 10)
			{
				if (npcId == npcs[7])
				{
					htmltext = "7-07.htm";
				}
				else if (npcId == npcs[3])
				{
					htmltext = "3-01.htm";
				}
			}
			else if ((cond == 11) || (cond == 12))
			{
				if (npcId == npcs[3])
				{
					if (st.getItemsCount(items[2]) > 0)
					{
						htmltext = "3-05.htm";
					}
					else
					{
						htmltext = "3-04.htm";
					}
				}
			}
			else if (cond == 13)
			{
				if (npcId == npcs[3])
				{
					htmltext = "3-06.htm";
				}
				else if (npcId == npcs[8])
				{
					htmltext = "8-01.htm";
				}
			}
			else if (cond == 14)
			{
				if (npcId == npcs[8])
				{
					htmltext = "8-03.htm";
				}
				else if (npcId == npcs[11])
				{
					htmltext = "11-01.htm";
				}
			}
			else if (cond == 15)
			{
				if (npcId == npcs[11])
				{
					htmltext = "11-02.htm";
				}
				else if (npcId == npcs[9])
				{
					htmltext = "9-01.htm";
				}
			}
			else if (cond == 16)
			{
				if (npcId == npcs[9])
				{
					htmltext = "9-02.htm";
				}
			}
			else if (cond == 17)
			{
				if (npcId == npcs[9])
				{
					htmltext = "9-04.htm";
				}
				else if (npcId == npcs[10])
				{
					htmltext = "10-01.htm";
				}
			}
			else if (cond == 18)
			{
				if (npcId == npcs[10])
				{
					htmltext = "10-05.htm";
				}
			}
			else if (cond == 19)
			{
				if (npcId == npcs[10])
				{
					htmltext = "10-07.htm";
				}
				if (npcId == npcs[0])
				{
					htmltext = "0-06.htm";
				}
			}
			else if (cond == 20)
			{
				if (npcId == npcs[0])
				{
					if (st.getPlayer().getLevel() >= 76)
					{
						st.setState(ScriptStateType.COMPLETED);
						st.set("cond", "0");
						st.rewardExpAndSp(EXP_AMOUNT, 0);
						st.giveItems(Inventory.ADENA_ID, ADENA_AMOUNT);
						st.giveItems(SECRET_BOOK_OF_GIANTS, 1);
						st.getPlayer().setClassId(classId.getId());
						if (!st.getPlayer().isSubClassActive() && (st.getPlayer().getBaseClass() == classId.getParent().getId()))
						{
							st.getPlayer().setBaseClass(classId);
						}
						st.getPlayer().broadcastUserInfo();
						cast(npc, st.getPlayer(), 4339, 1);// FIXME identificar skill
						htmltext = "0-07.htm";
					}
					else
					{
						htmltext = "0-010.htm";
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		ScriptState st = player.getScriptState(getName());
		int npcId = npc.getId();
		if (st != null)
		{
			int cond = st.getInt("cond");
			if (npcId == npcs[4])
			{
				if (cond == 17)
				{
					ScriptState st2 = findRightState(npc);
					if (st2 != null)
					{
						player.setLastQuestNpcObject(npc.getObjectId());
						if (st == st2)
						{
							if (st.getInt("Tab") == 1)
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-04.htm";
								}
								else if (st.getInt("Quest0") == 1)
								{
									htmltext = "4-06.htm";
								}
							}
							else
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-01.htm";
								}
								else if (st.getInt("Quest0") == 1)
								{
									htmltext = "4-03.htm";
								}
							}
						}
						else
						{
							if (st.getInt("Tab") == 1)
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-05.htm";
								}
								else if (st.getInt("Quest0") == 1)
								{
									htmltext = "4-07.htm";
								}
							}
							else
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-02.htm";
								}
							}
						}
					}
				}
				else if (cond == 18)
				{
					htmltext = "4-08.htm";
				}
			}
			if (htmltext == "")
			{
				npc.showChatWindow(player);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		ScriptState st = killer.getScriptState(getName());
		if (st != null)
		{
			int npcId = npc.getId();
			if (Util.contains(ARCHON_MINIONS, npcId))
			{
				Party party = killer.getParty();
				if (party != null)
				{
					List<ScriptState> partyQuestMembers = new ArrayList<>();
					
					for (L2PcInstance pls : party.getMembers())
					{
						if (pls.getClassId() == classId.getParent())
						{
							if (st.getInt("cond") == 15)
							{
								partyQuestMembers.add(st);
							}
						}
					}
					if (partyQuestMembers.size() > 0)
					{
						ScriptState st1 = partyQuestMembers.get(Rnd.get(partyQuestMembers.size()));
						if (st1 != null)
						{
							giveHallishaMark(st1);
						}
					}
				}
				else
				{
					if (killer.getClassId() == classId.getParent())
					{
						if (st.getInt("cond") == 15)
						{
							giveHallishaMark(st);
						}
					}
				}
			}
			else if (Util.contains(ARCHON_HELLSIHA_NORM, npcId))
			{
				if (killer.getClassId() == classId.getParent())
				{
					if (st.getInt("cond") == 15)
					{
						// This is just a guess....not really sure what it
						// actually says, if ( anything;
						npc.broadcastNpcSay(mobsSay[4].replace("PLAYERNAME", killer.getName()));
						st.giveItems(items[8], 1);
						st.takeItems(items[3], -1);
						st.set("cond", "16");
						st.playSound(PlaySoundType.QUEST_MIDDLE);
					}
				}
			}
			else if (Util.contains(GUARDIAN_ANGELS, npcId))
			{
				if (killer.getClassId() == classId.getParent())
				{
					if (st.getInt("cond") == 6)
					{
						final int kills = st.getInt("kills");
						if (kills < 9)
						{
							st.set("kills", String.valueOf(kills + 1));
						}
						else
						{
							st.playSound(PlaySoundType.QUEST_MIDDLE);
							st.giveItems(items[5], 1);
							st.set("cond", "7");
						}
					}
				}
			}
			else if (npcId != mobs[2])
			{
				int cond = st.getInt("cond");
				if ((npcId == mobs[0]) && (cond == 8))
				{
					ScriptState st1 = findRightState(npc);
					if (st1 != null)
					{
						if (!killer.isInParty())
						{
							if (st == st1)
							{
								npc.broadcastNpcSay(mobsSay[12].replace("PLAYERNAME", killer.getName()));
								st.giveItems(items[6], 1);
								st.set("cond", "9");
								st.playSound(PlaySoundType.QUEST_MIDDLE);
							}
						}
						if (getTimer("Mob_1 has despawned") != null)
						{
							getTimer("Mob_1 has despawned").cancel();
						}
						deleteSpawn(npc);
						st1.set("spawned", "0");
					}
				}
				else if (npcId == mobs[1])
				{
					if (cond == 15)
					{
						ScriptState st1 = findRightState(npc);
						if (st1 != null)
						{
							if (!killer.isInParty())
							{
								if (st == st1)
								{
									npc.broadcastNpcSay(mobsSay[4].replace("PLAYERNAME", killer.getName()));
									st.giveItems(items[8], 1);
									st.takeItems(items[3], -1);
									st.set("cond", "16");
									st.playSound(PlaySoundType.QUEST_MIDDLE);
								}
								else
								{
									npc.broadcastNpcSay(mobsSay[5].replace("PLAYERNAME", killer.getName()));
								}
							}
							if (getTimer("Archon Hellisha has despawned") != null)
							{
								getTimer("Archon Hellisha has despawned").cancel();
							}
							deleteSpawn(npc);
							st1.set("spawned", "0");
						}
					}
				}
			}
			else
			{
				if (npcId == mobs[0])
				{
					ScriptState st1 = findRightState(npc);
					if (st1 != null)
					{
						if (getTimer("Mob_1 has despawned") != null)
						{
							getTimer("Mob_1 has despawned").cancel();
						}
						deleteSpawn(npc);
						st1.set("spawned", "0");
					}
				}
				else if (npcId == mobs[1])
				{
					ScriptState st1 = findRightState(npc);
					if (st1 != null)
					{
						if (getTimer("Archon Hellisha has despawned") != null)
						{
							getTimer("Archon Hellisha has despawned").cancel();
						}
						deleteSpawn(npc);
						st1.set("spawned", "0");
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		ScriptState st = attacker.getScriptState(getName());
		if (st != null)
		{
			if (st.getInt("cond") == 17)
			{
				if (npc.getId() == mobs[2])
				{
					ScriptState st1 = findRightState(npc);
					if (st == st1)
					{
						st.set("Quest0", String.valueOf(st.getInt("Quest0") + 1));
						if (st.getInt("Quest0") == 1)
						{
							npc.broadcastNpcSay(mobsSay[16].replace("PLAYERNAME", attacker.getName()));
						}
						if (st.getInt("Quest0") > 15)
						{
							st.set("Quest0", "1");
							npc.broadcastNpcSay(mobsSay[17].replace("PLAYERNAME", attacker.getName()));
							npc.doDie(npc);
							deleteSpawn(npc);
							if (getTimer("Mob_3 has despawned") != null)
							{
								getTimer("Mob_3 has despawned").cancel();
							}
							st.set("Tab", "1");
						}
					}
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}
