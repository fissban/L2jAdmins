package l2j.gameserver.scripts.quests.normal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.DatabaseManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;

/**
 * @author fissban, zarie
 */
public class Q503_PursuitClanAmbition extends Script
{
	// Items
	// first part
	private static final int G_LET_MARTIEN = 3866;
	private static final int TH_WYRM_EGGS = 3842;
	private static final int DRAKE_EGGS = 3841;
	private static final int BL_WYRM_EGGS = 3840;
	private static final int MI_DRAKE_EGGS = 3839;
	private static final int BROOCH = 3843;
	private static final int BL_ANVIL_COIN = 3871;
	// second Part
	private static final int B_LET_BALTHAZAR = 3867;
	private static final int RECIPE_POWER_STONE = 3838;
	private static final int POWER_STONES = 3846;
	private static final int NEBULITE_CRYSTALS = 3844;
	private static final int BROKE_POWER_STONE = 3845;
	// third part
	private static final int G_LET_RODEMAI = 3868;
	private static final int IMP_KEYS = 3847;
	private static final int SCEPTER_JUDGEMENT = 3869;
	// the final item
	private static final int PROOF_ASPIRATION = 3870;
	private static final int[] EGG_LIST =
	{
		MI_DRAKE_EGGS,
		BL_WYRM_EGGS,
		DRAKE_EGGS,
		TH_WYRM_EGGS
	};
	// NPC
	private static final int MARTIEN = 7645;
	private static final int ATHRA = 7758;
	private static final int KALIS = 7759;
	private static final int GUSTAF = 7760;
	private static final int FRITZ = 7761;
	private static final int LUTZ = 7762;
	private static final int KURTZ = 7763;
	private static final int KUSTO = 7512;
	private static final int BALTHAZAR = 7764;
	private static final int RODEMAI = 7868;
	private static final int COFFER = 7765;
	private static final int CLEO = 7766;
	
	// MOBs
	private static final int IMPERIAL_SLAVE = 5180;
	private static final int IMPERIAL_GRAVEKEEPER = 5181;
	private static final int GRAVE_KEYMASTER = 5179;
	private static final int GRAVE_GUARD = 668;
	private static final int GIANT_SCOUT = 656;
	private static final int GIANT_SOLDIER = 654;
	private static final int BLITZ_WYRM = 5178;
	private static final int DRAKE_2 = 285;
	private static final int DRAKE_1 = 137;
	private static final int THUNDER_WYRM_2 = 243;
	private static final int THUNDER_WYRM_1 = 282;
	
	private static final int[] NPCS =
	{
		7645,
		7758,
		7759,
		7760,
		7761,
		7762,
		7763,
		7512,
		7764,
		7868,
		7765,
		7766
	};
	private static final String[] STATS =
	{
		"cond",
		"Fritz",
		"Lutz",
		"Kurtz",
		"ImpGraveKeeper"
	};
	// DROPLIST = step,chance,maxcount,item
	// condition,maxcount,chance,itemList = DROPLIST[npcId]
	private static final Map<Integer, AuxDropList> DROPLIST = new HashMap<>();
	
	{
		DROPLIST.put(THUNDER_WYRM_1, new AuxDropList(2, 10, 20, Arrays.asList(TH_WYRM_EGGS)));
		DROPLIST.put(THUNDER_WYRM_2, new AuxDropList(2, 10, 15, Arrays.asList(TH_WYRM_EGGS)));
		DROPLIST.put(DRAKE_1, new AuxDropList(2, 10, 20, Arrays.asList(DRAKE_EGGS)));
		DROPLIST.put(DRAKE_2, new AuxDropList(2, 10, 25, Arrays.asList(DRAKE_EGGS)));
		DROPLIST.put(BLITZ_WYRM, new AuxDropList(2, 10, 100, Arrays.asList(BL_WYRM_EGGS)));
		DROPLIST.put(GIANT_SOLDIER, new AuxDropList(5, 10, 25, Arrays.asList(BROKE_POWER_STONE, POWER_STONES, NEBULITE_CRYSTALS)));
		DROPLIST.put(GIANT_SCOUT, new AuxDropList(5, 10, 35, Arrays.asList(BROKE_POWER_STONE, POWER_STONES, NEBULITE_CRYSTALS)));
		DROPLIST.put(GRAVE_GUARD, new AuxDropList(10, 0, 15, Arrays.asList()));
		DROPLIST.put(GRAVE_KEYMASTER, new AuxDropList(10, 6, 80, Arrays.asList(IMP_KEYS)));
		DROPLIST.put(IMPERIAL_GRAVEKEEPER, new AuxDropList(10, 0, 100, Arrays.asList()));
	}
	
	private boolean spawnedSlaves = false;
	
	public class AuxDropList
	{
		public int condition;
		public int maxCount;
		public int chance;
		public List<Integer> itemsDrop;
		
		public AuxDropList(int condition, int maxCount, int chance, List<Integer> itemsDrop)
		{
			this.condition = condition;
			this.maxCount = maxCount;
			this.chance = chance;
			this.itemsDrop = itemsDrop;
		}
	}
	
	private static int getLeaderVar(ScriptState st, String var)
	{
		Clan clan = st.getPlayer().getClan();
		if (clan == null)
		{
			return -1;
		}
		L2PcInstance leader = clan.getLeader().getPlayerInstance();
		if (leader != null)
		{
			return leader.getScriptState("Q503_PursuitClanAmbition").getInt(var + "");
		}
		
		int leaderId = st.getPlayer().getClan().getLeaderId();
		int val = 0;
		
		try (Connection con = DatabaseManager.getConnection();
			PreparedStatement offline = con.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND name=?"))
		{
			offline.setInt(1, leaderId);
			offline.setString(2, var);
			offline.setString(3, "Q503_PursuitClanAmbition");
			
			try (ResultSet rs = offline.executeQuery())
			{
				if (rs != null)
				{
					rs.next();
					val = rs.getInt("value");
				}
				else
				{
					val = -1;
				}
			}
			
		}
		catch (Exception e)
		{
			return -1;
		}
		
		return val;
	}
	
	private void setLeaderVar(ScriptState st, String var, String value)
	{
		
		Clan clan = st.getPlayer().getClan();
		if (clan == null)
		{
			return;
		}
		L2PcInstance leader = clan.getLeader().getPlayerInstance();
		if (leader != null)
		{
			leader.getScriptState(getName()).set(var, value);
		}
		
		return;
	}
	
	private static void giveItem(int item, int maxcount, ScriptState st)
	{
		int count = st.getItemsCount(item);
		if (count < maxcount)
		{
			st.giveItems(item, 1);
			if (count == (maxcount - 1))
			{
				st.playSound(PlaySoundType.QUEST_MIDDLE);
			}
			else
			{
				st.playSound(PlaySoundType.QUEST_ITEMGET);
			}
		}
	}
	
	private static String exit503(ScriptState st)
	{
		st.playSound(PlaySoundType.QUEST_FANFARE_2);
		st.takeItems(SCEPTER_JUDGEMENT, -1);
		st.giveItems(PROOF_ASPIRATION, 1);
		st.rewardExpAndSp(0, 250000);
		st.exitQuest(true);
		
		return "Congratulations, you have finished the Pursuit of Clan Ambition.";
	}
	
	public Q503_PursuitClanAmbition()
	{
		super(503, "Pursuit of Clan Ambition");
		
		registerItems(MI_DRAKE_EGGS, BL_WYRM_EGGS, DRAKE_EGGS, TH_WYRM_EGGS, BROOCH, NEBULITE_CRYSTALS, BROKE_POWER_STONE, POWER_STONES, IMP_KEYS, G_LET_MARTIEN, B_LET_BALTHAZAR, G_LET_RODEMAI, SCEPTER_JUDGEMENT);
		
		addStartNpc(GUSTAF);
		addTalkId(NPCS);
		addAttackId(IMPERIAL_GRAVEKEEPER);
		addKillId(IMPERIAL_GRAVEKEEPER, IMPERIAL_GRAVEKEEPER, GRAVE_KEYMASTER, GRAVE_GUARD, GIANT_SCOUT, GIANT_SOLDIER, BLITZ_WYRM, DRAKE_2, DRAKE_1, THUNDER_WYRM_2, THUNDER_WYRM_1);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState("Q503_PursuitClanAmbition");
		if (st == null)
		{
			return null;
		}
		String htmltext = event;
		
		// Events Gustaf
		if (event.equalsIgnoreCase("7760-08.htm"))
		{
			st.giveItems(G_LET_MARTIEN, 1);
			for (String var : STATS)
			{
				st.set(var + "", "1");
			}
			
			st.setState(ScriptStateType.STARTED);
		}
		else if (event.equalsIgnoreCase("7760-12.htm"))
		{
			st.giveItems(B_LET_BALTHAZAR, 1);
			st.set("cond", "4");
		}
		else if (event.equalsIgnoreCase("7760-16.htm"))
		{
			st.giveItems(G_LET_RODEMAI, 1);
			st.set("cond", "7");
		}
		else if (event.equalsIgnoreCase("7760-20.htm"))
		{
			if (st.hasItems(SCEPTER_JUDGEMENT))
			{
				exit503(st);
			}
			else
			{
				htmltext = "<html><body>You don't have the Scepter of Judgement.</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("7760-22.htm"))
		{
			st.set("cond", "13");
		}
		else if (event.equalsIgnoreCase("7760-23.htm"))
		{
			if (st.hasItems(SCEPTER_JUDGEMENT))
			{
				exit503(st);
			}
			else
			{
				htmltext = "<html><body>You don't have the Scepter of Judgement.</body></html>";
			}
		}
		// Events Martien
		else if (event.equalsIgnoreCase("7645-03.htm"))
		{
			st.takeItems(G_LET_MARTIEN, -1);
			st.set("cond", "2");
		}
		// Events Kurtz
		else if (event.equalsIgnoreCase("7763-03.htm"))
		{
			if (st.getInt("Kurtz") == 1)
			{
				htmltext = "7763-02.htm";
				st.giveItems(MI_DRAKE_EGGS, 6);
				st.giveItems(BROOCH, 1);
				st.set("Kurtz", "2");
			}
		}
		// Events Lutz
		else if (event.equalsIgnoreCase("7762-03.htm"))
		{
			int lutz = st.getInt("Lutz");
			if (lutz == 1)
			{
				htmltext = "7762-02.htm";
				st.giveItems(MI_DRAKE_EGGS, 4);
				st.giveItems(BL_WYRM_EGGS, 3);
				st.set("Lutz", "2");
			}
			addSpawn(BLITZ_WYRM, 112268, 112761, -2770, 0, false, 120000);
			addSpawn(BLITZ_WYRM, 112234, 112705, -2770, 0, false, 120000);
		}
		// Events Fritz
		else if (event.equalsIgnoreCase("7761-03.htm"))
		{
			int fritz = st.getInt("Fritz");
			if (fritz == 1)
			{
				htmltext = "7761-02.htm";
				st.giveItems(BL_WYRM_EGGS, 3);
				st.set("Fritz", "2");
			}
			addSpawn(BLITZ_WYRM, 103841, 116809, -3025, 120000);
			addSpawn(BLITZ_WYRM, 103848, 116910, -3020, 120000);
		}
		// Events Kusto
		else if (event.equalsIgnoreCase("7512-03.htm"))
		{
			st.takeItems(BROOCH, 1);
			st.giveItems(BL_ANVIL_COIN, 1);
			st.set("Kurtz", "3");
		}
		// Events Balthazar
		else if (event.equalsIgnoreCase("7764-03.htm"))
		{
			st.takeItems(B_LET_BALTHAZAR, -1);
			st.set("cond", "5");
			st.set("Kurtz", "3");
		}
		else if (event.equalsIgnoreCase("7764-05.htm"))
		{
			st.takeItems(B_LET_BALTHAZAR, -1);
			st.set("cond", "5");
		}
		else if (event.equalsIgnoreCase("7764-06.htm"))
		{
			st.takeItems(BL_ANVIL_COIN, -1);
			st.set("Kurtz", "4");
			st.giveItems(RECIPE_POWER_STONE, 1);
		}
		// Events Rodemai
		else if (event.equalsIgnoreCase("7868-04.htm"))
		{
			st.takeItems(G_LET_RODEMAI, -1);
			st.set("cond", "8");
		}
		else if (event.equalsIgnoreCase("7868-06a.htm"))
		{
			st.set("cond", "10");
		}
		else if (event.equalsIgnoreCase("7868-10.htm"))
		{
			st.set("cond", "12");
		}
		else if (event.equalsIgnoreCase("7766-04.htm"))
		{
			st.set("cond", "9");
			if (npc != null)
			{
				npc.broadcastPacket(new CreatureSay(npc, SayType.ALL, npc.getName(), "Blood and Honor."));
			}
			L2Npc spawnedNpc = addSpawn(KALIS, 160665, 21209, -3710, 90000);
			spawnedNpc.broadcastPacket(new CreatureSay(spawnedNpc, SayType.ALL, spawnedNpc.getName(), "Ambition and Power."));
			spawnedNpc = addSpawn(ATHRA, 160665, 21291, -3710, 90000);
			spawnedNpc.broadcastPacket(new CreatureSay(spawnedNpc, SayType.ALL, spawnedNpc.getName(), "War and Death."));
		}
		else if (event.equalsIgnoreCase("7766-08.htm"))
		{
			htmltext = "7766-08.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		int npcId = npc.getId();
		
		String htmltext = getNoQuestMsg();
		
		// player has level 5 clan already
		if ((st.getPlayer().getClan() != null) && (st.getPlayer().getClan().getLevel() == 5))
		{
			return "<html><body>This quest is only available for clans of level 4.</body></html>";
		}
		
		switch (st.getState())
		{
			case CREATED:
				if (npcId == GUSTAF)
				{
					// adds all the vars for initialization
					for (String var : STATS)
					{
						st.set(var, "0");
					}
					// has Clan
					if (st.getPlayer().getClan() != null)
					{
						// check if player is clan leader
						if (st.getPlayer().isClanLeader())
						{
							int clanLevel = st.getPlayer().getClan().getLevel();
							// if he has the proof already, tell him what to do now
							if (st.hasItems(PROOF_ASPIRATION))
							{
								htmltext = "7760-03.htm";
								st.exitQuest(true);
							}
							else if (clanLevel == 4)
							{
								htmltext = "7760-04.htm";
							}
							else
							// if clanLevel < 4 you cant take it
							{
								htmltext = "7760-02.htm";
								st.exitQuest(true);
							}
						}
						else
						// player isn't a leader
						{
							htmltext = "7760-04t.htm";
							st.exitQuest(true);
						}
					}
					else
					// no Clan;
					{
						htmltext = "7760-01.htm";
						st.exitQuest(true);
					}
					return htmltext;
				}
				
				break;
			
			case STARTED:
				// Leader Area
				if (st.getPlayer().isClanLeader())
				{
					int cond = st.getInt("cond");
					int kurtz = st.getInt("Kurtz");
					int lutz = st.getInt("Lutz");
					int fritz = st.getInt("Fritz");
					
					if (npcId == GUSTAF)
					{
						switch (cond)
						{
							case 1:
								htmltext = "7760-09.htm";
								break;
							case 2:
								htmltext = "7760-10.htm";
								break;
							case 3:
								htmltext = "7760-11.htm";
								break;
							case 4:
								htmltext = "7760-13.htm";
								break;
							case 5:
								htmltext = "7760-14.htm";
								break;
							case 6:
								htmltext = "7760-15.htm";
								break;
							case 7:
								htmltext = "7760-17.htm";
								break;
							case 12:
								htmltext = "7760-19.htm";
								break;
							case 13:
								htmltext = "7760-24.htm";
								break;
							default:
								htmltext = "7760-18.htm";
								break;
						}
					}
					else if (npcId == MARTIEN)
					{
						switch (cond)
						{
							case 1:
								htmltext = "7645-02.htm";
								break;
							case 2:
								if (st.hasItems(EGG_LIST) && (kurtz > 1) && (lutz > 1) && (fritz > 1))
								{
									htmltext = "7645-05.htm";
									st.set("cond", "3");
									st.takeItems(EGG_LIST);
								}
								else
								{
									htmltext = "7645-04.htm";
								}
								break;
							case 3:
								htmltext = "7645-07.htm";
								break;
							default:
								htmltext = "7645-08.htm";
								break;
						}
					}
					else if ((npcId == LUTZ) && (cond == 2))
					{
						htmltext = "7762-01.htm";
					}
					else if ((npcId == KURTZ) && (cond == 2))
					{
						htmltext = "7763-01.htm";
					}
					else if ((npcId == FRITZ) && (cond == 2))
					{
						htmltext = "7761-01.htm";
					}
					else if (npcId == KUSTO)
					{
						switch (cond)
						{
							case 1:
								htmltext = "7512-01.htm";
								break;
							case 2:
								htmltext = "7512-02.htm";
								break;
							default:
								htmltext = "7512-04.htm";
								break;
						}
					}
					else if (npcId == BALTHAZAR)
					{
						switch (cond)
						{
							case 4:
								if (kurtz > 2)
								{
									htmltext = "7764-04.htm";
								}
								else
								{
									htmltext = "7764-02.htm";
								}
								break;
							case 5:
								if ((st.getItemsCount(POWER_STONES) > 9) && (st.getItemsCount(NEBULITE_CRYSTALS) > 9))
								{
									htmltext = "7764-08.htm";
									st.takeItems(POWER_STONES);
									st.takeItems(NEBULITE_CRYSTALS);
									st.takeItems(BROOCH);
									st.set("cond", "6");
								}
								else
								{
									htmltext = "7764-07.htm";
								}
								break;
							case 6:
								htmltext = "7764-09.htm";
								break;
							
						}
					}
					else if (npcId == RODEMAI)
					{
						if (cond == 7)
						{
							htmltext = "7868-02.htm";
						}
						else if (cond == 8)
						{
							htmltext = "7868-05.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7868-06.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7868-08.htm";
						}
						else if (cond == 11)
						{
							htmltext = "7868-09.htm";
						}
						else if (cond == 12)
						{
							htmltext = "7868-11.htm";
						}
					}
					else if (npcId == CLEO)
					{
						if (cond == 8)
						{
							htmltext = "7766-02.htm";
						}
						else if (cond == 9)
						{
							htmltext = "7766-05.htm";
						}
						else if (cond == 10)
						{
							htmltext = "7766-06.htm";
						}
						else if ((cond == 11) || (cond == 12) || (cond == 13))
						{
							htmltext = "7766-07.htm";
						}
					}
					else if (npcId == COFFER)
					{
						if (st.getInt("cond") == 10)
						{
							if (st.getItemsCount(IMP_KEYS) < 6)
							{
								htmltext = "7765-03a.htm";
							}
							else if (st.getInt("ImpGraveKeeper") == 1)
							{
								htmltext = "7765-02.htm";
								st.set("cond", "11");
								st.takeItems(IMP_KEYS, 6);
								st.giveItems(SCEPTER_JUDGEMENT, 1);
							}
							else
							{
								htmltext = "<html><body>You and your Clan didn't kill the Imperial Gravekeeper by your own. Try again.</body></html>";
							}
						}
						else
						{
							htmltext = "<html><body>You already have the Scepter of Judgement.</body></html>";
						}
					}
					else if (npcId == KALIS)
					{
						htmltext = "7759-01.htm";
					}
					else if (npcId == ATHRA)
					{
						htmltext = "7758-01.htm";
					}
					return htmltext;
				}
				
				// Member Area
				int cond = getLeaderVar(st, "cond");
				if (((npcId == MARTIEN) && (cond == 01)) || (cond == 02) || (cond == 3))
				{
					htmltext = "7645-01.htm";
				}
				else if (npcId == RODEMAI)
				{
					if ((cond == 9) || (cond == 10))
					{
						htmltext = "7868-07.htm";
					}
					else if (cond == 7)
					{
						htmltext = "7868-01.htm";
					}
				}
				else if ((npcId == BALTHAZAR) && (cond == 4))
				{
					htmltext = "7764-01.htm";
				}
				else if ((npcId == CLEO) && (cond == 8))
				{
					htmltext = "7766-01.htm";
				}
				else if ((npcId == KUSTO) && (cond > 2) && (cond < 6))
				{
					htmltext = "7512-01a.htm";
				}
				else if ((npcId == COFFER) && (cond == 10))
				{
					htmltext = "7765-01.htm";
				}
				else if (npcId == GUSTAF)
				{
					if (cond == 3)
					{
						htmltext = "7760-11t.htm";
					}
					else if (cond == 4)
					{
						htmltext = "7760-15t.htm";
					}
					else if (cond == 12)
					{
						htmltext = "7760-19t.htm";
					}
					else if (cond == 13)
					{
						htmltext = "7766-24t.htm";
					}
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
		
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if ((npc.getCurrentHp() - damage) <= (npc.getStat().getMaxHp() / 2))
		{
			if (!spawnedSlaves)
			{
				for (int j = 0; j < 8; j++)
				{
					int x = npc.getX() + getRandom(100);
					int y = npc.getY() + getRandom(100);
					
					addSpawn(IMPERIAL_SLAVE, x, y, npc.getZ(), 0, false, 0);
				}
				spawnedSlaves = true;
			}
			else
			{
				if (getRandom(100) <= 20)
				{
					attacker.teleToLocation(179549, 6445, -2708);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getId();
		if (npcId == IMPERIAL_GRAVEKEEPER)
		{
			spawnedSlaves = false;
		}
		
		ScriptState st = killer.getScriptState("Q503_PursuitClanAmbition");
		
		if (st == null)
		{
			return null;
		}
		
		AuxDropList dropList = DROPLIST.get(npcId);
		
		int random = getRandom(100);
		int cond = getLeaderVar(st, "cond");
		if ((cond == dropList.condition) && (random < dropList.chance))
		{
			int maxCount = dropList.maxCount;
			
			if (dropList.itemsDrop.size() > 1)
			{
				int stoneRandom = getRandom(3);
				if (stoneRandom == 0)
				{
					if (getLeaderVar(st, "Kurtz") < 4)
					{
						return null;
					}
					maxCount *= 4;
				}
				
				giveItem(dropList.itemsDrop.get(stoneRandom), maxCount, st);
			}
			else if (!dropList.itemsDrop.isEmpty())
			{
				giveItem(dropList.itemsDrop.get(0), dropList.maxCount, st);
			}
			else if (npcId == IMPERIAL_GRAVEKEEPER)
			{
				L2Npc spawnedNpc = addSpawn(COFFER, killer.getX(), killer.getY(), killer.getZ(), 0, true, 120000);
				spawnedNpc.broadcastPacket(new CreatureSay(spawnedNpc, SayType.ALL, spawnedNpc.getName(), "Curse of the gods on the one that defiles the property of the empire!"));
				setLeaderVar(st, "ImpGraveKeeper", "1");
			}
			else
			{
				addSpawn(GRAVE_KEYMASTER, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
			}
		}
		return null;
	}
}
