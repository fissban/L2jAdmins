package l2j.gameserver.scripts.quests.normal;

/**
 * Proof of Clan Alliance (501)
 * @author ICPNetworks, CaFi, mauronob, zarie, Fissban
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.pc.clan.Clan;
import l2j.gameserver.model.actor.manager.pc.clan.ClanMemberInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.MagicSkillLaunched;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.gameserver.scripts.ScriptStateType;
import l2j.util.Rnd;

public final class Q501_ProofOfClanAlliance extends Script
{
	private static final int NEEDED_MEMBERS = 3;
	private static final int SKILL_POISON = 4082;
	private static final int SKILL_DEATH = 4083;
	private static final int LOYALTY_TIMER = 4 * 1000;
	private static final int CHEST_TIMER = 300 * 1000;
	
	// Quest NPCs
	private static final int SIR_KRISTOF_RODEMAI = 7756;
	private static final int STATUE_OF_OFFERING = 7757;
	private static final int ATHREA = 7758;
	private static final int KALIS = 7759;
	
	// Quest items
	private static final int HERB_OF_HARIT = 3832;
	private static final int HERB_OF_VANOR = 3833;
	private static final int HERB_OF_OEL_MAHUM = 3834;
	private static final int BLOOD_OF_EVA = 3835;
	private static final int SYMBOL_OF_LOYALTY = 3837;
	private static final int ANTIDOTE_RECIPE_LIST = 3872;
	private static final int VOUCHER_OF_FAITH = 3873;
	private static final int ALLIANCE_MANIFESTO = 3874;
	private static final int POTION_OF_RECOVERY = 3889;
	
	// Quest monsters
	private static final int OEL_MAHUM_WITCH_DOCTOR = 576;
	private static final int HARIT_LIZARDMAN_SHAMAN = 644;
	private static final int VANOR_SILENOS_SHAMAN = 685;
	
	private static final int[] CHEST =
	{
		5173,
		5174,
		5175,
		5176,
		5177
	};
	
	private static final String CHEST_KILLED = "##########Bingo!##########";
	
	private static final List<LocationHolder> CHEST_POS = new ArrayList<>();
	{
		CHEST_POS.add(new LocationHolder(102273, 103433, -3512));
		CHEST_POS.add(new LocationHolder(102190, 103379, -3524));
		CHEST_POS.add(new LocationHolder(102107, 103325, -3533));
		CHEST_POS.add(new LocationHolder(102024, 103271, -3500));
		CHEST_POS.add(new LocationHolder(102327, 103350, -3511));
		CHEST_POS.add(new LocationHolder(102244, 103296, -3518));
		CHEST_POS.add(new LocationHolder(102161, 103242, -3529));
		CHEST_POS.add(new LocationHolder(102078, 103188, -3500));
		CHEST_POS.add(new LocationHolder(102381, 103267, -3538));
		CHEST_POS.add(new LocationHolder(102298, 103213, -3532));
		CHEST_POS.add(new LocationHolder(102215, 103159, -3520));
		CHEST_POS.add(new LocationHolder(102132, 103105, -3513));
		CHEST_POS.add(new LocationHolder(102435, 103184, -3515));
		CHEST_POS.add(new LocationHolder(102352, 103130, -3522));
		CHEST_POS.add(new LocationHolder(102269, 103076, -3533));
		CHEST_POS.add(new LocationHolder(102186, 103022, -3541));
	}
	
	protected static final int NEEDED_WINS = 4;
	
	private final Map<Integer, QuestClan> questers;
	private volatile QuestClan minigame;
	
	public Q501_ProofOfClanAlliance()
	{
		super(501, "Proof Of Clan Alliance");
		
		registerItems(BLOOD_OF_EVA, SYMBOL_OF_LOYALTY, ANTIDOTE_RECIPE_LIST, VOUCHER_OF_FAITH, POTION_OF_RECOVERY);
		
		questers = new HashMap<>();
		minigame = null;
		addStartNpc(SIR_KRISTOF_RODEMAI, STATUE_OF_OFFERING);
		addTalkId(SIR_KRISTOF_RODEMAI, STATUE_OF_OFFERING, ATHREA, KALIS);
		addKillId(OEL_MAHUM_WITCH_DOCTOR, HARIT_LIZARDMAN_SHAMAN, VANOR_SILENOS_SHAMAN);
		addKillId(CHEST);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestClan qc = questers.get(player.getClanId());
		if (event.startsWith("loyalty"))
		{
			if ((qc == null) || !qc.checkLeader())
			{
				return null;
			}
			if (qc.isLoyal(player))
			{
				player.getInventory().addItem("Proof Of Clan Alliance", SYMBOL_OF_LOYALTY, 1, npc, true);
			}
			return null;
		}
		else if (event.startsWith("chest"))
		{
			if ((qc == null) || !qc.checkLeader())
			{
				minigame = null;
				return null;
			}
			qc.getChests().timeout();
			minigame = null;
			return null;
		}
		else
		{
			if (player.isClanLeader())
			{
				ScriptState qs = player.getScriptState(getName());
				if ("7756-07.htm".equals(event))
				{
					qc = new QuestClan(player.getClan());
					questers.put(player.getClanId(), qc);
					qs.set("cond", "1");
					qc.setPart(SIR_KRISTOF_RODEMAI);
					qs.setState(ScriptStateType.STARTED);
					qs.playSound(PlaySoundType.QUEST_ACCEPT);
				}
				else if ("7759-03.htm".equals(event))
				{
					qs.set("cond", "2");
					qc.setPart(STATUE_OF_OFFERING);
				}
				else if ("7759-07.htm".equals(event))
				{
					for (int i = 0; i < NEEDED_MEMBERS; i++)
					{
						qs.takeItems(SYMBOL_OF_LOYALTY, 1);
					}
					qs.giveItems(ANTIDOTE_RECIPE_LIST, 1);
					qs.set("cond", "3");
					qc.setPart(KALIS);
					qs.addNotifyOfDeath();
					Skill skill = SkillData.getInstance().getSkill(SKILL_POISON, 1);
					if (skill == null)
					{
						LOG.info("501_PoCA: Missing skill " + SKILL_POISON + ", terminating quest!");
						qs.exitQuest(true);
					}
					else
					{
						skill.getEffects(player, player);
					}
				}
			}
			else
			{
				if ((qc == null) || !qc.checkLeader())
				{
					return null;
				}
				else if ("7757-05.htm".equals(event))
				{
					if (!qc.addLoyalMember(player))
					{
						return "7757-07.htm";
					}
					ScriptState qs = player.getScriptState(getName());
					qs.takeItems(SYMBOL_OF_LOYALTY, -1);
					qs.takeItems(BLOOD_OF_EVA, -1);
					if (Rnd.get(10) > 5)
					{
						player.getInventory().addItem("Proof Of Clan Alliance", SYMBOL_OF_LOYALTY, 1, npc, true);
						return "7757-06.htm";
					}
					Skill skill = SkillData.getInstance().getSkill(SKILL_DEATH, 1);
					if (skill == null)
					{
						// player.doDie(npc);
						LOG.info("501_PoCA: Missing skill " + SKILL_DEATH);
					}
					else
					{
						npc.broadcastPacket(new MagicSkillUse(npc, player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
						npc.broadcastPacket(new MagicSkillLaunched(npc, skill.getId(), skill.getLevel()));
					}
					player.doDie(npc);
					startTimer("loyalty_" + player.getObjectId(), LOYALTY_TIMER, npc, player, false);
					return "7757-05.htm";
				}
				else if ("7758-03.htm".equals(event))
				{
					if (minigame == null)
					{
						minigame = qc;
						qc.setPart(ATHREA);
						for (LocationHolder loc : CHEST_POS)
						{
							addSpawn(CHEST[Rnd.get(CHEST.length)], loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, CHEST_TIMER);
						}
						startTimer("chest_" + qc.getClan().getId(), CHEST_TIMER, npc, player, false);
					}
					else
					{
						return "7758-04.htm";
					}
				}
				else if ("7758-07.htm".equals(event))
				{
					if (minigame == null)
					{
						if (player.getInventory().destroyItemByItemId("Proof Of Clan Alliance", Inventory.ADENA_ID, 10000, player, true))
						{
							qc.getChests().reset();
							minigame = qc;
							qc.setPart(ATHREA);
							for (LocationHolder loc : CHEST_POS)
							{
								addSpawn(CHEST[Rnd.get(CHEST.length)], loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, CHEST_TIMER);
							}
							startTimer("chest_" + qc.getClan().getId(), CHEST_TIMER, npc, player, false);
							return "7758-04.htm";
						}
					}
					else
					{
						return "7758-04.htm";
					}
				}
			}
		}
		return event;
	}
	
	public String onDeath(L2Character killer, L2Character victim, ScriptState qs)
	{
		if (victim instanceof L2PcInstance)
		{
			L2PcInstance leader = victim.getActingPlayer();
			QuestClan qc = questers.remove(leader.getClanId());
			qs.exitQuest(true);
			for (ClanMemberInstance cm : qc.loyal)
			{
				if (cm == null)
				{
					break;
				}
				L2PcInstance member = cm.getPlayerInstance();
				if (member != null)
				{
					ScriptState st = member.getScriptState(getName());
					if (st == null)
					{
						st = newState(member);
					}
					st.exitQuest(true);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (minigame != null)
		{
			ChestInfo ci = minigame.getChests();
			synchronized (ci)
			{
				if (ci.getWins() < NEEDED_WINS)
				{
					if (((ci.getKills() >= (CHEST_POS.size() - NEEDED_WINS)) && ((ci.getKills() - ci.getWins()) == 12)) || (Rnd.get(NEEDED_WINS) == 0))
					{
						ci.incWins();
						npc.broadcastNpcSay(CHEST_KILLED);
					}
				}
				ci.incKills();
			}
		}
		QuestClan qc = questers.get(killer.getClanId());
		if ((qc == null) || !qc.checkLeader() || (qc.getPart() != KALIS) || !qc.isLoyal(killer))
		{
			return null;
		}
		ScriptState qs = killer.getScriptState(getName());
		switch (npc.getId())
		{
			case OEL_MAHUM_WITCH_DOCTOR:
				qs.dropItems(HERB_OF_OEL_MAHUM, 1, 1, 100000);
				break;
			case HARIT_LIZARDMAN_SHAMAN:
				qs.dropItems(HERB_OF_HARIT, 1, 1, 100000);
				break;
			case VANOR_SILENOS_SHAMAN:
				qs.dropItems(HERB_OF_VANOR, 1, 1, 100000);
				break;
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		QuestClan qc = questers.get(talker.getClanId());
		ScriptState qs = talker.getScriptState(getName());
		ScriptStateType state = qs.getState();
		
		if (state == ScriptStateType.COMPLETED)
		{
			return getAlreadyCompletedMsg();
		}
		
		if (qc == null)
		{
			if (state == ScriptStateType.CREATED)
			{
				switch (npc.getId())
				{
					// anyone may speak
					case SIR_KRISTOF_RODEMAI:
					case STATUE_OF_OFFERING:
						break;
					default:
						if (!talker.isClanLeader())
						{
							return null;
						}
				}
			}
			else
			{
				if (!talker.isClanLeader())
				{
					qs.exitQuest(true);
					return null;
				}
				qc = new QuestClan(talker.getClan());
				qc.setPart(SIR_KRISTOF_RODEMAI);
				questers.put(talker.getClanId(), qc);
				qs.set("cond", "1");
				return onTalk(npc, talker);
			}
		}
		else if (!talker.isClanLeader() && !qc.checkLeader())
		{
			return null;
		}
		switch (npc.getId())
		{
			case SIR_KRISTOF_RODEMAI:
				if (state == ScriptStateType.CREATED)
				{
					if (talker.isClanLeader())
					{
						switch (talker.getClan().getLevel())
						{
							case 0:
							case 1:
							case 2:
								return buildReply(npc, 9);
							case 3:
								if (qs.getItemsCount(ALLIANCE_MANIFESTO) > 0)
								{
									return buildReply(npc, 5);
								}
								return buildReply(npc, 1);
							default:
								return buildReply(npc, 2);
						}
					}
					return buildReply(npc, 10);
				}
				else if (state == ScriptStateType.STARTED)
				{
					if (qs.getItemsCount(VOUCHER_OF_FAITH) > 0)
					{
						qs.takeItems(VOUCHER_OF_FAITH, -1);
						qs.exitQuest(false);
						qs.giveItems(ALLIANCE_MANIFESTO, 1);
						qs.rewardExpAndSp(120000, 0);
						qs.playSound(PlaySoundType.QUEST_FINISH);
						talker.broadcastPacket(new SocialAction(talker.getObjectId(), SocialActionType.VICTORY));
						return buildReply(npc, 7);
					}
					return buildReply(npc, 3);
				}
				break;
			case KALIS:
				if (qc == null)
				{
					return null;
				}
				else if (state == ScriptStateType.CREATED)
				{
					ScriptState l = qc.getClan().getLeader().getPlayerInstance().getScriptState(getName());
					if (l.getState() == ScriptStateType.STARTED)
					{
						return buildReply(npc, 12); // ...
					}
				}
				else if (state == ScriptStateType.STARTED)
				{
					int part = qc.getPart();
					long symbols = qs.getItemsCount(SYMBOL_OF_LOYALTY);
					boolean poisoned = (talker.getEffect(SKILL_POISON) != null);
					if (part == SIR_KRISTOF_RODEMAI)
					{
						return buildReply(npc, 1);
					}
					else if ((part == STATUE_OF_OFFERING) && (symbols < NEEDED_MEMBERS))
					{
						return buildReply(npc, 5);
					}
					else if ((symbols >= NEEDED_MEMBERS) && !poisoned)
					{
						return buildReply(npc, 6);
					}
					else if ((part == (KALIS * 2)) && (qs.getItemsCount(HERB_OF_HARIT) > 0) && (qs.getItemsCount(HERB_OF_OEL_MAHUM) > 0) && (qs.getItemsCount(HERB_OF_VANOR) > 0) && poisoned)
					{
						qs.takeItems(ANTIDOTE_RECIPE_LIST, -1);
						qs.takeItems(BLOOD_OF_EVA, -1);
						qs.takeItems(HERB_OF_HARIT, -1);
						qs.takeItems(HERB_OF_OEL_MAHUM, -1);
						qs.takeItems(HERB_OF_VANOR, -1);
						qs.giveItems(VOUCHER_OF_FAITH, 1);
						qs.giveItems(POTION_OF_RECOVERY, 1);
						qs.set("cond", "4");
						qc.setPart(SIR_KRISTOF_RODEMAI * 2);
						return buildReply(npc, 8);
					}
					else if (((part % KALIS) == 0) || (part == ATHREA))
					{
						if (!poisoned)
						{
							qc.setPart(SIR_KRISTOF_RODEMAI);
							return buildReply(npc, 9);
						}
						return buildReply(npc, 10);
					}
					else if (part == (SIR_KRISTOF_RODEMAI * 2))
					{
						return buildReply(npc, 11);
					}
				}
				break;
			case STATUE_OF_OFFERING:
				if (qc == null)
				{
					return null;
				}
				else if (qc.getPart() != STATUE_OF_OFFERING)
				{
					return buildReply(npc, 6); // ...
				}
				else if (!talker.isClanLeader())
				{
					if (talker.getLevel() > 39)
					{
						if (qc.isLoyal(talker))
						{
							return buildReply(npc, 3);
						}
						return buildReply(npc, 1);
					}
					return buildReply(npc, 4);
				}
				else
				{
					return buildReply(npc, 2);
				}
			case ATHREA:
				if (qc == null)
				{
					return null;
				}
				int part = qc.getPart();
				ScriptState l = qc.getClan().getLeader().getPlayerInstance().getScriptState(getName());
				if ((part == KALIS) && (l.getItemsCount(ANTIDOTE_RECIPE_LIST) > 0) && (l.getItemsCount(BLOOD_OF_EVA) == 0))
				{
					return buildReply(npc, 1);
				}
				else if (part == (KALIS * 2))
				{
					return buildReply(npc, 10);
				}
				else if (part == ATHREA)
				{
					switch (qc.getChests().getState())
					{
						case ChestInfo.WON:
							qs.giveItems(BLOOD_OF_EVA, 1);
							qc.setPart(KALIS * 2);
							return buildReply(npc, 9);
						case ChestInfo.FAILED:
							return buildReply(npc, 5); // Tell her that you will pay her 10,000 adena.
						default:
							return buildReply(npc, 10);
					}
				}
				break;
		}
		return null;
	}
	
	private static final String buildReply(L2Npc npc, int answer)
	{
		StringBuilder tb = new StringBuilder();
		tb.append(npc.getId());
		tb.append('-');
		if (answer < 10)
		{
			tb.append('0');
		}
		tb.append(answer);
		tb.append(".htm");
		
		return tb.toString();
	}
	
	private class QuestClan
	{
		private final Clan clan;
		protected final ClanMemberInstance[] loyal;
		private final ChestInfo chests;
		private int part;
		
		QuestClan(Clan clan)
		{
			this.clan = clan;
			loyal = new ClanMemberInstance[NEEDED_MEMBERS];
			chests = new ChestInfo();
		}
		
		public synchronized boolean addLoyalMember(L2PcInstance player)
		{
			ClanMemberInstance cm = getClan().getClanMember(player.getObjectId());
			if (cm == null)
			{
				return false;
			}
			for (int i = 0; i < NEEDED_MEMBERS; i++)
			{
				if (loyal[i] == null)
				{
					loyal[i] = cm;
					return true;
				}
			}
			return false;
		}
		
		public boolean checkLeader()
		{
			L2PcInstance leader = getClan().getLeader().getPlayerInstance();
			if (leader == null)
			{
				return false;
			}
			ScriptState qs = leader.getScriptState(getName());
			if ((qs == null) || !qs.isStarted())
			{
				return false;
			}
			return true;
		}
		
		public boolean isLoyal(L2PcInstance player)
		{
			for (ClanMemberInstance cm : loyal)
			{
				if ((cm != null) && (cm.getPlayerInstance() == player))
				{
					return true;
				}
			}
			return false;
		}
		
		public Clan getClan()
		{
			return clan;
		}
		
		public ChestInfo getChests()
		{
			return chests;
		}
		
		public int getPart()
		{
			return part;
		}
		
		public void setPart(int part)
		{
			this.part = part;
		}
	}
	
	private static class ChestInfo
	{
		private static final int NONE = 0;
		private static final int WON = 1;
		private static final int FAILED = 2;
		private int kills;
		private int wins;
		private int state;
		
		protected ChestInfo()
		{
			reset();
		}
		
		public int getKills()
		{
			return kills;
		}
		
		public void incKills()
		{
			kills++;
		}
		
		public int getWins()
		{
			return wins;
		}
		
		public void incWins()
		{
			wins++;
			if (getWins() == NEEDED_WINS)
			{
				state = WON;
			}
		}
		
		public void timeout()
		{
			if (getState() == NONE)
			{
				state = FAILED;
			}
		}
		
		public int getState()
		{
			return state;
		}
		
		public void reset()
		{
			kills = 0;
			wins = 0;
			state = NONE;
		}
	}
}
