package l2j.gameserver.model.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import l2j.Config;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.ManorData;
import l2j.gameserver.instancemanager.sevensigns.SevenSignsManager;
import l2j.gameserver.model.actor.ai.AttackableAI;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.SiegeGuardAI;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.AttackableKnownList;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.drop.DropCategory;
import l2j.gameserver.model.drop.DropInstance;
import l2j.gameserver.model.holder.AbsorbInfoHolder;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptEventType;
import l2j.gameserver.util.Util;
import l2j.util.Rnd;
import main.EngineModsManager;

/**
 * This class manages all NPC that can be attacked.<br>
 * L2Attackable :<br>
 * <li>L2ArtefactInstance</li>
 * <li>L2FriendlyMobInstance</li>
 * <li>L2MonsterInstance</li>
 * <li>L2SiegeGuardInstance</li>
 * @version $Revision: 1.24.2.3.2.16 $ $Date: 2005/04/11 19:11:21 $
 */
public class L2Attackable extends L2Npc
{
	/**
	 * This class contains all AggroInfo of the L2Attackable against the attacker L2Character.<br>
	 * <B><U> Data</U> :</B><br>
	 * <li>attacker : The attacker L2Character concerned by this AggroInfo of this L2Attackable</li>
	 * <li>hate : Hate level of this L2Attackable against the attacker L2Character (hate = damage)</li>
	 * <li>damage : Number of damages that the attacker L2Character gave to this L2Attackable</li>
	 */
	public final class AggroInfo
	{
		/** The attacker L2Character concerned by this AggroInfo of this L2Attackable */
		private L2Character attacker;
		/** Hate level of this L2Attackable against the attacker L2Character (hate = damage) */
		private int hate;
		/** Number of damages that the attacker L2Character gave to this L2Attackable */
		private int damage;
		
		/**
		 * Constructor of AggroInfo.
		 * @param pAttacker
		 */
		public AggroInfo(L2Character pAttacker)
		{
			attacker = pAttacker;
			hate = 0;
			damage = 0;
		}
		
		public L2Character getAttacker()
		{
			return attacker;
		}
		
		public void setAttacker(L2Character attacker)
		{
			this.attacker = attacker;
		}
		
		public int getHate()
		{
			return hate;
		}
		
		public void setHate(int hate)
		{
			this.hate = hate;
		}
		
		public void addHate(int hate)
		{
			this.hate += hate;
		}
		
		public int getDamage()
		{
			return damage;
		}
		
		public void setDamage(int damage)
		{
			this.damage = damage;
		}
		
		public void addDamage(int damage)
		{
			this.damage += damage;
		}
		
		/**
		 * Verify is object is equal to this AggroInfo.
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			
			if (obj instanceof AggroInfo)
			{
				return ((AggroInfo) obj).attacker == attacker;
			}
			
			return false;
		}
		
		/**
		 * Return the Identifier of the attacker L2Character.
		 */
		@Override
		public int hashCode()
		{
			return attacker.getObjectId();
		}
	}
	
	/**
	 * This class contains all RewardInfo of the L2Attackable against the any attacker L2Character, based on amount of damage done.<br>
	 * <B><U> Data</U> :</B><br>
	 * <li>attacker : The attacker L2Character concerned by this RewardInfo of this L2Attackable</li>
	 * <li>dmg : Total amount of damage done by the attacker to this L2Attackable (summon + own)</li>
	 */
	protected final class RewardInfo
	{
		protected L2Character attacker;
		protected int dmg = 0;
		
		public RewardInfo(L2Character pAttacker, int pDmg)
		{
			attacker = pAttacker;
			dmg = pDmg;
		}
		
		public void addDamage(int pDmg)
		{
			dmg += pDmg;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			
			if (obj instanceof RewardInfo)
			{
				return ((RewardInfo) obj).attacker == attacker;
			}
			
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return attacker.getObjectId();
		}
	}
	
	/** Table containing all Items that a Dwarf can Sweep on this L2Attackable */
	private final List<ItemHolder> sweepItems = new ArrayList<>();
	
	/** crops */
	private final List<ItemHolder> harvestItems = new ArrayList<>();
	private boolean seeded;
	private int seedType = 0;
	private L2PcInstance seeder = null;
	
	/** True if an over-hit enabled skill has successfully landed on the L2Attackable */
	private boolean overhit;
	
	/** Stores the extra (over-hit) damage done to the L2Attackable when the attacker uses an over-hit enabled skill */
	private double overhitDamage;
	
	/** Stores the attacker who used the over-hit enabled skill on the L2Attackable */
	private L2Character overhitAttacker;
	
	/** True if a Soul Crystal was successfully used on the L2Attackable */
	private boolean absorbed;
	
	/** The table containing all L2PcInstance that successfully absorbed the soul of this L2Attackable */
	private final Map<Integer, AbsorbInfoHolder> absorbersList = new ConcurrentHashMap<>();
	
	/** Have this L2Attackable to reward Exp and SP on Die? **/
	private boolean mustGiveExpSp;
	
	/**
	 * Constructor of L2Attackable (use L2Character and L2NpcInstance constructor).<br>
	 * <B><U> Actions</U> :</B><br>
	 * <li>Call the L2Character constructor to set the template of the L2Attackable (copy skills from template to object and link calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2Attackable</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template to apply to the NPC
	 */
	public L2Attackable(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2Attackable);
		
		mustGiveExpSp = true;
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new AttackableKnownList(this));
	}
	
	@Override
	public AttackableKnownList getKnownList()
	{
		return (AttackableKnownList) super.getKnownList();
	}
	
	/**
	 * Return the L2Character AI of the L2Attackable and if its null create a new one.<br>
	 */
	@Override
	public CharacterAI getAI()
	{
		if (ai == null)
		{
			synchronized (this)
			{
				if (ai == null)
				{
					ai = new AttackableAI(this);
				}
			}
		}
		return ai;
	}
	
	/**
	 * Reduce the current HP of the L2Attackable.
	 * @param damage   The HP decrease value
	 * @param attacker The L2Character who attacks
	 */
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker)
	{
		reduceCurrentHp(damage, attacker, true);
	}
	
	/**
	 * Reduce the current HP of the L2Attackable, update its aggroList and launch the doDie Task if necessary.<br>
	 * @param attacker The L2Character who attacks
	 * @param awake    The awake state (If True : stop sleeping)
	 */
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, boolean awake)
	{
		// Add damage and hate to the attacker AggroInfo of the L2Attackable aggroList
		if (attacker != null)
		{
			addDamage(attacker, (int) damage);
		}
		
		// Reduce the current HP of the L2Attackable and launch the doDie Task if necessary
		super.reduceCurrentHp(damage, attacker, awake);
	}
	
	public synchronized void setMustRewardExpSp(boolean value)
	{
		mustGiveExpSp = value;
	}
	
	public synchronized boolean getMustRewardExpSP()
	{
		return mustGiveExpSp;
	}
	
	/**
	 * Kill the L2Attackable (the corpse disappeared after 7 seconds), distribute rewards (EXP, SP, Drops...) and notify Quest Engine.<br>
	 * <B><U> Actions</U> :</B><br>
	 * <li>Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members</li>
	 * <li>Notify the Quest Engine of the L2Attackable death if necessary</li>
	 * <li>Kill the L2NpcInstance (the corpse disappeared after 7 seconds)</li> <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to L2PetInstance</B></FONT><br>
	 * @param killer The L2Character that has killed the L2Attackable
	 */
	@Override
	public boolean doDie(L2Character killer)
	{
		// Kill the L2NpcInstance (the corpse disappeared after 7 seconds)
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (killer == null)
		{
			return true;
		}
		// Notify the Quest Engine of the L2Attackable death if necessary
		try
		{
			L2PcInstance player = killer.getActingPlayer();
			
			if (player != null)
			{
				EngineModsManager.onKill(killer, this, killer instanceof L2Summon);
				
				if (getTemplate().getEventScript(ScriptEventType.ON_KILL) != null)
				{
					for (final Script quest : getTemplate().getEventScript(ScriptEventType.ON_KILL))
					{
						quest.notifyKill(this, player, killer instanceof L2Summon);
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.log(Level.SEVERE, "", e);
		}
		return true;
	}
	
	/**
	 * Distribute Exp and SP rewards to L2PcInstance (including Summon owner) that hit the L2Attackable and to their Party members.<br>
	 * <B><U> Actions</U> :</B><br>
	 * <li>Get the L2PcInstance owner of the L2SummonInstance (if necessary) and L2Party in progress</li>
	 * <li>Calculate the Experience and SP rewards in function of the level difference</li>
	 * <li>Add Exp and SP rewards to L2PcInstance (including Summon penalty) and to Party members in the known area of the last attacker</li>
	 * @param lastAttacker The L2Character that has killed the L2Attackable
	 */
	@Override
	protected void calculateRewards(L2Character lastAttacker)
	{
		if (lastAttacker == null)
		{
			return;
		}
		
		if (EngineModsManager.onNpcExpSp(this, lastAttacker))
		{
			return;
		}
		
		// Creates an empty list of rewards
		Map<L2Character, RewardInfo> rewards = new ConcurrentHashMap<>();
		
		try
		{
			if (noHasTarget())
			{
				return;
			}
			
			int damage;
			L2Character attacker, ddealer;
			
			L2PcInstance maxDealer = null;
			int maxDamage = 0;
			
			// Go through the aggroList of the L2Attackable
			for (final AggroInfo info : getAggroList().values())
			{
				if (info == null)
				{
					continue;
				}
				
				// Get the L2Character corresponding to this attacker
				attacker = info.getAttacker();
				
				// Get damages done by this attacker
				damage = info.getDamage();
				
				// Prevent unwanted behavior
				if (damage > 1)
				{
					if ((attacker instanceof L2SummonInstance) || (attacker instanceof L2PetInstance))
					{
						ddealer = attacker.getActingPlayer();
					}
					else
					{
						ddealer = attacker;
					}
					
					// Calculate real damages (Summoners should get own damage plus summon's damage)
					RewardInfo reward = rewards.get(ddealer);
					
					if (reward == null)
					{
						reward = new RewardInfo(ddealer, damage);
					}
					else
					{
						reward.addDamage(damage);
					}
					
					rewards.put(ddealer, reward);
					
					// set max dealer owner pet or summon or player
					maxDealer = ddealer.getActingPlayer();
					
					if ((maxDealer != null) && (reward.dmg > maxDamage))
					{
						maxDamage = reward.dmg;
					}
				}
			}
			
			// Manage Base, Quests and Sweep drops of the L2Attackable
			doItemDrop((maxDealer != null) && maxDealer.isOnline() ? maxDealer : lastAttacker);
			
			if (!getMustRewardExpSP())
			{
				return;
			}
			
			if (!rewards.isEmpty())
			{
				Party attackerParty;
				
				long exp = 0;
				int levelDiff;
				int partyDmg;
				int partyLvl;
				float partyMul;
				float penalty;
				
				RewardInfo reward2;
				int sp;
				int[] tmp;
				
				for (final RewardInfo reward : rewards.values())
				{
					if (reward == null)
					{
						continue;
					}
					
					// Penalty applied to the attacker's XP
					penalty = 0;
					
					// Attacker to be rewarded
					attacker = reward.attacker;
					
					// Total amount of damage done
					damage = reward.dmg;
					
					if (attacker instanceof L2Playable)
					{
						attackerParty = attacker.getActingPlayer().getParty();
					}
					else
					{
						return;
					}
					
					// If this attacker is a L2PcInstance with a summoned L2SummonInstance, get Exp Penalty applied for the current summoned L2SummonInstance
					if ((attacker instanceof L2PcInstance) && (attacker.getActingPlayer().getPet() != null))
					{
						penalty = attacker.getActingPlayer().getPet().getExpPenalty();
					}
					
					// We must avoid "over damage", if any
					if (damage > getStat().getMaxHp())
					{
						damage = getStat().getMaxHp();
					}
					
					// If there's NO party in progress
					if (attackerParty == null)
					{
						// Calculate Exp and SP rewards
						if (attacker.getKnownList().getObject(this))
						{
							// Calculate the difference of level between this attacker (L2PcInstance or L2SummonInstance owner) and the L2Attackable
							// mob = 24, atk = 10, diff = -14 (full xp)
							// mob = 24, atk = 28, diff = 4 (some xp)
							// mob = 24, atk = 50, diff = 26 (no xp)
							levelDiff = attacker.getLevel() - getLevel();
							
							tmp = calculateExpAndSp(levelDiff, damage);
							
							var sinEaterExp = false;
							// check if "sin eater" pet
							if ((attacker.getActingPlayer().getPet() != null) && (attacker.getActingPlayer().getPet().getId() == 12564))
							{
								exp = tmp[0];
								sinEaterExp = true;
							}
							else
							{
								// Apply penalty
								exp = (long) (tmp[0] * (1 - penalty));
							}
							
							sp = tmp[1];
							
							// Check for an over-hit enabled strike
							if (attacker instanceof L2PcInstance)
							{
								final L2PcInstance player = (L2PcInstance) attacker;
								if (isOverhit() && (attacker == getOverhitAttacker()))
								{
									player.sendPacket(SystemMessage.OVER_HIT);
									exp += calculateOverhitExp(exp);
								}
							}
							
							if (sinEaterExp)
							{
								// give exp to "sin eater"
								attacker.getActingPlayer().getPet().addExpAndSp(Math.round(attacker.calcStat(StatsType.EXPSP_RATE, exp, null, null)), 0);
								// give sp to player
								attacker.addExpAndSp(0, (int) attacker.calcStat(StatsType.EXPSP_RATE, sp, null, null));
							}
							// In the case of pets (L2PetInstance) they only gain experience if they are to kill the mobs.
							else if (lastAttacker instanceof L2PetInstance)
							{
								lastAttacker.addExpAndSp(Math.round(lastAttacker.calcStat(StatsType.EXPSP_RATE, exp, null, null)), (int) lastAttacker.calcStat(StatsType.EXPSP_RATE, sp, null, null));
							}
							else
							{
								attacker.getActingPlayer().addExpAndSp(Math.round(attacker.getActingPlayer().calcStat(StatsType.EXPSP_RATE, exp, null, null)), (int) attacker.calcStat(StatsType.EXPSP_RATE, sp, null, null));
							}
						}
					}
					else
					{
						// share with party members
						partyDmg = 0;
						partyMul = 1.f;
						partyLvl = 0;
						
						// Get all L2Character that can be rewarded in the party
						final List<L2PcInstance> rewardedMembers = new ArrayList<>();
						
						// Go through all L2PcInstance in the party
						for (final L2PcInstance player : attackerParty.getMembers())
						{
							if ((player == null) || player.isDead())
							{
								continue;
							}
							
							// Get the RewardInfo of this L2PcInstance from L2Attackable rewards
							reward2 = rewards.get(player);
							
							// If the L2PcInstance is in the L2Attackable rewards add its damages to party damages
							if (reward2 != null)
							{
								partyDmg += reward2.dmg;
								rewardedMembers.add(player);
								
								if (player.getLevel() > partyLvl)
								{
									partyLvl = player.getLevel();
								}
								// Remove the L2PcInstance from the L2Attackable rewards
								rewards.remove(player);
							}
							else
							{
								// Add L2PcInstance of the party (that have attacked or not) to members that can be rewarded if it's not dead
								// and in range of the monster.
								if (Util.checkIfInRange(1600, this, player, true))
								{
									rewardedMembers.add(player);
									
									if (player.getLevel() > partyLvl)
									{
										partyLvl = player.getLevel();
									}
								}
							}
							
							// TODO: We need to invent method to:
							// if pet owner_exp_taken == 0
							// add pet to rewardedMembers (or give exp here) if pet done dmg - and give correct exp reward depend on pet dmg;
							// and dont give exp reward from pet dmg to owner/party members;
							// and give exp reward from owner dmg to owner/party members; (exclude pet!)
							// for clarity: if both (owner, pet) done dmg, give reward to both. According to rules mentioned above.
							// else
							// add owner to rewardedMembers (doesn't matter if pet/owner done damage)
							// note: actually we apply penalty in Party#distributeXpAndSp;
							// add exp reward to pet, depend on owner~penalty (exp reward*owner_exp_taken);
							// for clarity: owner get reward with penalty from distributeXpAndSp, pet need to receive part for him;
							// eg. pet with 0.10 penalty and exp to divide for them 1000: pet get 1000*0.10, owner get 1000*0.90;
							
							// Until that time, we must remove reward here, for owner with pet.
							// Current way is definitely wrong.
							final L2Summon summon = player.getPet();
							if ((summon != null) && (summon instanceof L2PetInstance))
							{
								rewardedMembers.remove(summon.getOwner());
							}
						}
						
						// If the party didn't killed this L2Attackable alone
						if (partyDmg < getStat().getMaxHp())
						{
							partyMul = (float) partyDmg / getStat().getMaxHp();
						}
						
						// Avoid "over damage"
						if (partyDmg > getStat().getMaxHp())
						{
							partyDmg = getStat().getMaxHp();
						}
						
						// Calculate the level difference between Party and L2Attackable
						levelDiff = partyLvl - getLevel();
						
						// Calculate Exp and SP rewards
						tmp = calculateExpAndSp(levelDiff, partyDmg);
						exp = tmp[0];
						sp = tmp[1];
						
						exp *= partyMul;
						sp *= partyMul;
						
						// Check for an over-hit enabled strike
						// (When in party, the over-hit exp bonus is given to the whole party and splitted proportionally through the party members)
						if (attacker instanceof L2PcInstance)
						{
							final L2PcInstance player = (L2PcInstance) attacker;
							if (isOverhit() && (attacker == getOverhitAttacker()))
							{
								player.sendPacket(SystemMessage.OVER_HIT);
								exp += calculateOverhitExp(exp);
							}
						}
						
						// Distribute Experience and SP rewards to L2PcInstance Party members in the known area of the last attacker
						if ((partyDmg > 0) && !rewardedMembers.isEmpty())
						{
							attackerParty.distributeXpAndSp(exp, sp, rewardedMembers, partyLvl);
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOG.log(Level.SEVERE, "", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Add damage and hate to the attacker AggroInfo of the L2Attackable aggroList.<br>
	 * @param attacker The L2Character that gave damages to this L2Attackable
	 * @param damage   The number of damages given by the attacker L2Character
	 */
	public void addDamage(L2Character attacker, int damage)
	{
		// Notify the L2Attackable AI with EVT_ATTACKED
		final L2PcInstance player = attacker.getActingPlayer();
		if (player != null)
		{
			if (getTemplate().getEventScript(ScriptEventType.ON_ATTACK) != null)
			{
				for (final Script quest : getTemplate().getEventScript(ScriptEventType.ON_ATTACK))
				{
					quest.notifyAttack(this, player, damage, attacker instanceof L2Summon);
				}
			}
			
			addDamageHate(attacker, damage, damage);
		}
	}
	
	/**
	 * Add damage and hate to the attacker AggroInfo of the L2Attackable aggroList.<br>
	 * @param attacker The L2Character that gave damages to this L2Attackable
	 * @param damage   The number of damages given by the attacker L2Character
	 * @param aggro    The hate (=damage) given by the attacker L2Character
	 */
	public void addDamageHate(L2Character attacker, int damage, int aggro)
	{
		if (attacker == null)
		{
			return;
		}
		
		// Get the AggroInfo of the attacker L2Character from the aggroList of the L2Attackable
		AggroInfo ai = getAggroList().get(attacker);
		
		L2PcInstance player = null;
		
		if (ai == null)
		{
			ai = new AggroInfo(attacker);
			
			getAggroList().put(attacker, ai);
			
			player = attacker.getActingPlayer();
			
			if (player != null)
			{
				if (getTemplate().getEventScript(ScriptEventType.ON_AGGRO_RANGE_ENTER) != null)
				{
					for (Script script : getTemplate().getEventScript(ScriptEventType.ON_AGGRO_RANGE_ENTER))
					{
						script.notifyAggroRangeEnter(this, player, attacker instanceof L2Summon);
					}
				}
			}
		}
		
		if (!isDead())
		{
			// Add new damage and aggro (=damage) to the AggroInfo object
			ai.addDamage(damage);
			// For Baium AI
			if ((attacker instanceof L2Attackable) && (this instanceof L2GrandBossInstance))
			{
				ai.setHate(1);
			}
			else
			{
				ai.addHate(aggro);
			}
			
			// Notify the L2Attackable AI with EVT_ATTACKED
			if (damage > 0)
			{
				getAI().notifyEvent(CtrlEventType.ATTACKED, attacker);
			}
			// Set the intention to the L2Attackable to AI_INTENTION_ACTIVE
			else if ((aggro > 0) && (getAI().getIntention() == CtrlIntentionType.IDLE))
			{
				getAI().setIntention(CtrlIntentionType.ACTIVE);
			}
			else if (ai.getHate() <= 0)
			{
				if (getMostHated() == null)
				{
					aggroList.clear();
					
					getAI().setIntention(CtrlIntentionType.ACTIVE);
					setWalking();
				}
			}
		}
	}
	
	/**
	 * Return the most hated L2Character of the L2Attackable aggroList.
	 * @return (L2Character)
	 */
	public L2Character getMostHated()
	{
		if (noHasTarget() || isAlikeDead())
		{
			return null;
		}
		
		L2Character mostHated = null;
		int maxHate = 0;
		
		// While Iterating over This Map Removing Object is Not Allowed
		for (AggroInfo ai : getAggroList().values())
		{
			if (ai == null)
			{
				continue;
			}
			if (ai.getAttacker().isAlikeDead() || !getKnownList().getObject(ai.getAttacker()) || !ai.getAttacker().isVisible())
			{
				ai.setHate(0);
			}
			
			if (ai.getHate() > maxHate)
			{
				mostHated = ai.getAttacker();
				maxHate = ai.getHate();
			}
		}
		
		return mostHated;
	}
	
	/**
	 * Return the hate level of the L2Attackable against this L2Character contained in aggroList.<br>
	 * @param  target The L2Character whose hate level must be returned
	 * @return
	 */
	public int getHating(L2Character target)
	{
		if (noHasTarget() || (target == null))
		{
			return 0;
		}
		
		final AggroInfo ai = getAggroList().get(target);
		if (ai == null)
		{
			return 0;
		}
		
		if ((ai.getAttacker() instanceof L2PcInstance) && (((L2PcInstance) ai.getAttacker()).getInvisible() || ai.getAttacker().isInvul()))
		{
			// Remove Object Should Use This Method and Can be Blocked While Iterating
			getAggroList().remove(target);
			return 0;
		}
		if (!ai.getAttacker().isVisible())
		{
			getAggroList().remove(target);
			return 0;
		}
		if (ai.getAttacker().isAlikeDead())
		{
			ai.setHate(0);
			return 0;
		}
		
		return ai.getHate();
	}
	
	/**
	 * Clears aggroList hate of the L2Character without removing from the list.
	 * @param target The target to clean from that L2Attackable aggroList.
	 */
	public void stopHating(L2Character target)
	{
		if (target == null)
		{
			return;
		}
		
		AggroInfo ai = getAggroList().get(target);
		if (ai != null)
		{
			ai.setHate(0);
		}
	}
	
	/**
	 * Calculates quantity of items for specific drop acording to current situation <br>
	 * @param  drop          The L2DropData count is being calculated for
	 * @param  lastAttacker  The L2PcInstance that has killed the L2Attackable
	 * @param  deepBlueDrop  Factor to divide the drop chance
	 * @param  levelModifier level modifier in %'s (will be subtracted from drop chance)
	 * @return
	 */
	private ItemHolder calculateSweepDrop(L2PcInstance lastAttacker, DropInstance drop, int levelModifier)
	{
		// Get default drop chance
		int dropChance = drop.getChance();
		
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int deepBlueDrop = 1;
			
			if (levelModifier > 0)
			{
				// We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
				// NOTE: This is valid only for adena drops! Others drops will still obey server's rate
				deepBlueDrop = 3;
				if (drop.getItemId() == Inventory.ADENA_ID)
				{
					deepBlueDrop *= isRaid() ? (int) Config.DROP_CHANCE_RAID : (int) Config.DROP_CHANCE_ITEMS;
					
					// avoid div by 0
					if (deepBlueDrop == 0)
					{
						deepBlueDrop = 1;
					}
				}
			}
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			dropChance = (drop.getChance() - ((drop.getChance() * levelModifier) / 100)) / deepBlueDrop;
		}
		
		// Define a chance to drop depending on the defined configs.
		dropChance = getChanceDropItems(drop, dropChance);
		
		// Set our limits for chance of drop
		if (dropChance < 1)
		{
			dropChance = 1;
		}
		
		if (dropChance > Rnd.get(DropInstance.MAX_CHANCE))
		{
			// Define the number of items to be obtained from what defined within our configs files.
			int dropCount = getAmountDropItems(drop, true);
			
			if (dropCount > 0)
			{
				return new ItemHolder(drop.getItemId(), dropCount);
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates quantity of items for specific drop CATEGORY according to current situation <br>
	 * Only a max of ONE item from a category is allowed to be dropped.
	 * @param  drop          The L2DropData count is being calculated for
	 * @param  lastAttacker  The L2PcInstance that has killed the L2Attackable
	 * @param  categoryDrops
	 * @param  deepBlueDrop  Factor to divide the drop chance
	 * @param  levelModifier level modifier in %'s (will be subtracted from drop chance)
	 * @return
	 */
	private ItemHolder calculateCategoryDrop(L2PcInstance lastAttacker, DropCategory categoryDrops, int levelModifier)
	{
		if (categoryDrops == null)
		{
			return null;
		}
		
		// Get default drop chance for the category (that's the sum of chances for all items in the category)
		// keep track of the base category chance as it'll be used later, if an item is drop from the category.
		// for everything else, use the total "categoryDropChance"
		int categoryDropChance = categoryDrops.getCategoryChance();
		
		if (Config.DEEPBLUE_DROP_RULES)
		{
			// We should multiply by the server's drop rate, so we always get a low chance of drop for deep blue mobs.
			// NOTE: This is valid only for adena drops! Others drops will still obey server's rate
			int deepBlueDrop = (levelModifier > 0) ? 3 : 1;
			
			// Check if we should apply our maths so deep blue mobs will not drop that easy
			categoryDropChance = (categoryDropChance - ((categoryDropChance * levelModifier) / 100)) / deepBlueDrop;
		}
		
		// Applies Drop rates
		categoryDropChance *= isRaid() ? Config.DROP_CHANCE_RAID : Config.DROP_CHANCE_ITEMS;
		
		// Check if an Item from this category must be dropped
		if (Rnd.get(DropInstance.MAX_CHANCE) < Math.max(1, categoryDropChance))
		{
			final DropInstance drop = categoryDrops.dropOne(isRaid());
			if (drop == null)
			{
				return null;
			}
			
			// Define the number of items to be obtained from what defined within our configs files.
			int dropCount = getAmountDropItems(drop, false);
			
			if (dropCount > 0)
			{
				return new ItemHolder(drop.getItemId(), dropCount);
			}
		}
		return null;
	}
	
	/**
	 * Calculates the level modifier for drop<br>
	 * @param  lastAttacker The L2PcInstance that has killed the L2Attackable
	 * @return
	 */
	private int calculateLevelModifierForDrop(L2PcInstance lastAttacker)
	{
		if (Config.DEEPBLUE_DROP_RULES)
		{
			int highestLevel = lastAttacker.getLevel();
			
			// Check to prevent very high level player to nearly kill mob and let low level player do the last hit.
			if ((getAttackByList() != null) && !getAttackByList().isEmpty())
			{
				for (final L2Character atkChar : getAttackByList())
				{
					if ((atkChar != null) && (atkChar.getLevel() > highestLevel))
					{
						highestLevel = atkChar.getLevel();
					}
				}
			}
			
			// According to official data (Prima), deep blue mobs are 9 or more levels below players
			if ((highestLevel - 9) >= getLevel())
			{
				return (highestLevel - (getLevel() + 8)) * 9;
			}
		}
		
		return 0;
	}
	
	public void doItemDrop(L2Character mainDamageDealer)
	{
		doItemDrop(getTemplate(), mainDamageDealer);
	}
	
	/**
	 * Manage Base, Quests and Special Events drops of L2Attackable (called by calculateRewards).<br>
	 * <B><U> Concept</U> :</B><br>
	 * During a Special Event all L2Attackable can drop extra Items. Those extra Items are defined in the table <B>allNpcDateDrops</B> of the EventDroplist. Each Special Event has a start and end date to stop to drop extra Items automaticaly. <br>
	 * <B><U> Actions</U> : </B><br>
	 * <li>Manage drop of Special Events created by GM for a defined period</li>
	 * <li>Get all possible drops of this L2Attackable from L2NpcTemplate and add it Quest drops</li>
	 * <li>For each possible drops (base + quests), calculate which one must be dropped (random)</li>
	 * <li>Get each Item quantity dropped (random)</li>
	 * <li>Create this or these L2ItemInstance corresponding to each Item Identifier dropped</li>
	 * <li>If the autoLoot mode is active and if the L2Character that has killed the L2Attackable is a L2PcInstance, give this or these Item(s) to the L2PcInstance that has killed the L2Attackable</li>
	 * <li>If the autoLoot mode isn't active or if the L2Character that has killed the L2Attackable is not a L2PcInstance, add this or these Item(s) in the world as a visible object at the position where mob was last</li>
	 * @param npcTemplate
	 * @param mainDamageDealer
	 */
	public void doItemDrop(NpcTemplate npcTemplate, L2Character mainDamageDealer)
	{
		if (mainDamageDealer == null)
		{
			return;
		}
		
		L2PcInstance player = null;
		
		player = mainDamageDealer.getActingPlayer();
		
		if (player == null)
		{
			return; // Don't drop anything if the last attacker or owner isn't L2PcInstance
		}
		
		if (EngineModsManager.onNpcDrop(this, mainDamageDealer))
		{
			return;
		}
		
		final int levelModifier = calculateLevelModifierForDrop(player); // level modifier in %'s (will be subtracted from drop chance)
		
		// now throw all categorized drops and handle spoil.
		for (final DropCategory cat : npcTemplate.getDropsCategory())
		{
			ItemHolder item = null;
			if (cat.isSweep())
			{
				// according to sh1ny, seeded mobs can be spoiled and sweep.
				if (isSpoil())
				{
					for (DropInstance drop : cat.getAllDrops())
					{
						item = calculateSweepDrop(player, drop, levelModifier);
						if (item == null)
						{
							continue;
						}
						
						sweepItems.add(item);
					}
				}
			}
			else
			{
				item = calculateCategoryDrop(player, cat, levelModifier);
				
				if (item != null)
				{
					// Check if the autoLoot mode is active
					if (!player.isFullAdenaInventory(item.getId()) && ((isRaid() && Config.AUTO_LOOT_RAIDS) || (Config.AUTO_LOOT && !isRaid())))
					{
						player.doAutoLoot(this, item); // Give this or these Item(s) to the L2PcInstance that has killed the L2Attackable
					}
					else
					{
						dropItem(player, item); // drop the item on the ground
					}
					
					// Broadcast message if RaidBoss was defeated
					if (this instanceof L2RaidBossInstance)
					{
						broadcastPacket(new SystemMessage(SystemMessage.C1_DIED_DROPPED_S3_S2).addString(getName()).addItemName(item.getId()).addNumber(item.getCount()));
					}
				}
			}
		}
	}
	
	/**
	 * Drop reward item.
	 * @param  mainDamageDealer
	 * @param  item
	 * @return
	 */
	public ItemInstance dropItem(L2PcInstance mainDamageDealer, ItemHolder item)
	{
		final int randDropLim = 70;
		
		ItemInstance ditem = null;
		for (int i = 0; i < item.getCount(); i++)
		{
			// Randomize drop position
			final int newX = (getX() + Rnd.get((randDropLim * 2) + 1)) - randDropLim;
			final int newY = (getY() + Rnd.get((randDropLim * 2) + 1)) - randDropLim;
			final int newZ = Math.max(getZ(), mainDamageDealer.getZ()) + 20;
			
			// Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
			ditem = ItemData.getInstance().createItem("Loot", item.getId(), item.getCount(), mainDamageDealer, this);
			ditem.getDropProtection().protect(mainDamageDealer);
			ditem.setProtected(false);
			ditem.dropMe(this, newX, newY, newZ);
			// If stackable, end loop as entire count is included in 1 instance of item
			if (ditem.isStackable() || !Config.MULTIPLE_ITEM_DROP)
			{
				break;
			}
		}
		return ditem;
	}
	
	public ItemInstance dropItem(L2PcInstance lastAttacker, int itemId, int itemCount)
	{
		return dropItem(lastAttacker, new ItemHolder(itemId, itemCount));
	}
	
	/**
	 * Return the active weapon of this L2Attackable (= null).<br>
	 * @return
	 */
	public ItemInstance getActiveWeapon()
	{
		return null;
	}
	
	/**
	 * Clears aggroList hate of the L2Character without removing from the list.<br>
	 * @param target
	 */
	public void clearHating(L2Character target)
	{
		if (noHasTarget())
		{
			return;
		}
		
		final AggroInfo ai = getAggroList().get(target);
		
		if (ai == null)
		{
			return;
		}
		
		ai.setHate(0);
	}
	
	/**
	 * Return True if a Dwarf use Sweep on the L2Attackable and if item can be spoiled.<br>
	 * @return
	 */
	public boolean isSweepActive()
	{
		return !sweepItems.isEmpty();
	}
	
	/**
	 * Return table containing all L2ItemInstance that can be spoiled.<br>
	 * @return
	 */
	public List<ItemHolder> takeSweep()
	{
		return sweepItems;
	}
	
	public void clearSweep()
	{
		sweepItems.clear();
	}
	
	/**
	 * Return table containing all L2ItemInstance that can be harvested.<br>
	 * @return
	 */
	public synchronized List<ItemHolder> takeHarvest()
	{
		return harvestItems;
	}
	
	/**
	 * Set the over-hit flag on the L2Attackable.<br>
	 * @param status The status of the over-hit flag
	 */
	public void overhitEnabled(boolean status)
	{
		overhit = status;
	}
	
	/**
	 * Set the over-hit values like the attacker who did the strike and the ammount of damage done by the skill.<br>
	 * @param attacker The L2Character who hit on the L2Attackable using the over-hit enabled skill
	 * @param damage   The ammount of damage done by the over-hit enabled skill on the L2Attackable
	 */
	public void setOverhitValues(L2Character attacker, double damage)
	{
		// Calculate the over-hit damage
		// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
		final double overhitDmg = (getCurrentHp() - damage) * -1;
		if (overhitDmg < 0)
		{
			// we didn't killed the mob with the over-hit strike. (it wasn't really an over-hit strike)
			// let's just clear all the over-hit related values
			overhitEnabled(false);
			overhitDamage = 0;
			overhitAttacker = null;
			return;
		}
		overhitEnabled(true);
		overhitDamage = overhitDmg;
		overhitAttacker = attacker;
	}
	
	/**
	 * Return the L2Character who hit on the L2Attackable using an over-hit enabled skill.<br>
	 * @return L2Character attacker
	 */
	public L2Character getOverhitAttacker()
	{
		return overhitAttacker;
	}
	
	/**
	 * Return the amount of damage done on the L2Attackable using an over-hit enabled skill.<br>
	 * @return double damage
	 */
	public double getOverhitDamage()
	{
		return overhitDamage;
	}
	
	/**
	 * Return True if the L2Attackable was hit by an over-hit enabled skill.<br>
	 * @return
	 */
	public boolean isOverhit()
	{
		return overhit;
	}
	
	/**
	 * Activate the absorbed soul condition on the L2Attackable.<br>
	 */
	public void absorbSoul()
	{
		absorbed = true;
	}
	
	/**
	 * Return True if the L2Attackable had his soul absorbed.<br>
	 * @return
	 */
	public boolean isAbsorbed()
	{
		return absorbed;
	}
	
	/**
	 * Adds an attacker that successfully absorbed the soul of this L2Attackable into the absorbersList.<br>
	 * params: attacker - a valid L2PcInstance condition - an integer indicating the event when mob dies. This should be: = 0 - "the crystal scatters"; = 1 - "the crystal failed to absorb. nothing happens"; = 2 - "the crystal resonates because you got more than 1 crystal on you"; = 3 - "the crystal
	 * cannot absorb the soul because the mob level is too low"; = 4 - "the crystal successfuly absorbed the soul";
	 * @param attacker
	 * @param crystal
	 */
	public void addAbsorber(L2PcInstance attacker, ItemInstance crystal)
	{
		// If the Creature attacker isn't already in the absorbersList of this L2Attackable, add it
		AbsorbInfoHolder ai = absorbersList.get(attacker.getObjectId());
		if (ai == null)
		{
			// Create absorb info.
			absorbersList.put(attacker.getObjectId(), new AbsorbInfoHolder(crystal.getObjectId()));
		}
		else
		{
			// Add absorb info, unless already registered.
			if (!ai.isRegistered())
			{
				ai.setItemId(crystal.getObjectId());
			}
		}
	}
	
	public void registerAbsorber(L2PcInstance attacker)
	{
		// Get AbsorbInfo for user.
		AbsorbInfoHolder ai = absorbersList.get(attacker.getObjectId());
		if (ai == null)
		{
			return;
		}
		
		// Check item being used and register player to mob's absorber list.
		if (attacker.getInventory().getItemByObjectId(ai.getItemId()) == null)
		{
			return;
		}
		
		// Register AbsorbInfo.
		if (!ai.isRegistered())
		{
			ai.setAbsorbedHpPercent((int) ((100 * getCurrentHp()) / getStat().getMaxHp()));
			ai.setRegistered(true);
		}
	}
	
	public void resetAbsorberList()
	{
		absorbersList.clear();
	}
	
	public AbsorbInfoHolder getAbsorbInfo(int npcObjectId)
	{
		return absorbersList.get(npcObjectId);
	}
	
	/**
	 * Calculate the Experience and SP to distribute to attacker (L2PcInstance, L2SummonInstance or L2Party) of the L2Attackable.<br>
	 * @param  diff   The difference of level between attacker (L2PcInstance, L2SummonInstance or L2Party) and the L2Attackable
	 * @param  damage The damages given by the attacker (L2PcInstance, L2SummonInstance or L2Party)
	 * @return
	 */
	public int[] calculateExpAndSp(int diff, int damage)
	{
		double xp;
		double sp;
		
		if (diff < -5)
		{
			diff = -5; // makes possible to use ALT_GAME_EXPONENT configuration
		}
		xp = (((double) getExpReward() * damage) / getStat().getMaxHp());
		
		if (Config.ALT_GAME_EXPONENT_XP != 0)
		{
			xp *= Math.pow(2., -diff / Config.ALT_GAME_EXPONENT_XP);
		}
		
		sp = (((double) getSpReward() * damage) / getStat().getMaxHp());
		if (Config.ALT_GAME_EXPONENT_SP != 0)
		{
			sp *= Math.pow(2., -diff / Config.ALT_GAME_EXPONENT_SP);
		}
		
		if ((Config.ALT_GAME_EXPONENT_XP == 0) && (Config.ALT_GAME_EXPONENT_SP == 0))
		{
			if (diff > 5)
			{
				final double pow = Math.pow((double) 5 / 6, diff - 5);
				xp = xp * pow;
				sp = sp * pow;
			}
			
			if (xp <= 0)
			{
				xp = 0;
				sp = 0;
			}
			else if (sp <= 0)
			{
				sp = 0;
			}
		}
		
		final int[] tmp =
		{
			(int) xp,
			(int) sp
		};
		
		return tmp;
	}
	
	public long calculateOverhitExp(long normalExp)
	{
		// Get the percentage based on the total of extra (over-hit) damage done relative to the total (maximum) ammount of HP on the L2Attackable
		double overhitPercentage = (getOverhitDamage() * 100) / getStat().getMaxHp();
		
		// Over-hit damage percentages are limited to 25% max
		if (overhitPercentage > 25)
		{
			overhitPercentage = 25;
		}
		
		// Get the overhit exp bonus according to the above over-hit damage percentage
		// (1/1 basis - 13% of over-hit damage, 13% of extra exp is given, and so on...)
		final double overhitExp = (overhitPercentage / 100) * normalExp;
		
		// Return the rounded amount of exp points to be added to the player's normal exp reward
		return Math.round(overhitExp);
	}
	
	/**
	 * Return True.<br>
	 */
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// Clear all aggro char from list
		clearAggroList();
		// Clear overhit value
		overhitEnabled(false);
		
		// Clear Harvester Reward List
		setSeeded(false);
		harvestItems.clear();
		// Clear mob spoil,seed
		setSpoil(false);
		sweepItems.clear();
		
		resetAbsorberList();
		
		setWalking();
		
		// check the region where this mob is, do not activate the AI if region is inactive.
		if (!isInActiveRegion())
		{
			if (this instanceof L2SiegeGuardInstance)
			{
				((SiegeGuardAI) getAI()).stopAITask();
			}
			else
			{
				((AttackableAI) getAI()).stopAITask();
			}
		}
	}
	
	public void setSeeded()
	{
		if ((seedType != 0) && (seeder != null))
		{
			setSeeded(seedType, seeder.getLevel());
		}
	}
	
	public void setSeeded(int id, L2PcInstance seeder)
	{
		if (!seeded)
		{
			seedType = id;
			this.seeder = seeder;
		}
	}
	
	public void setSeeded(int id, int seederLvl)
	{
		seeded = true;
		seedType = id;
		int count = 1;
		
		for (final int skillId : getTemplate().getSkills().keySet())
		{
			switch (skillId)
			{
				case 4303: // Strong type x2
					count *= 2;
					break;
				case 4304: // Strong type x3
					count *= 3;
					break;
				case 4305: // Strong type x4
					count *= 4;
					break;
				case 4306: // Strong type x5
					count *= 5;
					break;
				case 4307: // Strong type x6
					count *= 6;
					break;
				case 4308: // Strong type x7
					count *= 7;
					break;
				case 4309: // Strong type x8
					count *= 8;
					break;
				case 4310: // Strong type x9
					count *= 9;
					break;
			}
		}
		
		final int diff = getLevel() - (ManorData.getInstance().getSeedLevel(seedType) - 5);
		
		// hi-lvl mobs bonus
		if (diff > 0)
		{
			count += diff;
		}
		
		harvestItems.clear();
		harvestItems.add(new ItemHolder(ManorData.getInstance().getCropType(seedType), count * Config.DROP_AMOUNT_MANOR));
	}
	
	public void setSeeded(boolean seeded)
	{
		this.seeded = seeded;
	}
	
	public L2PcInstance getSeeder()
	{
		return seeder;
	}
	
	public int getSeedType()
	{
		return seedType;
	}
	
	public boolean isSeeded()
	{
		return seeded;
	}
	
	/**
	 * Check if the server allows Random Animation.
	 */
	@Override
	public boolean hasRandomAnimation()
	{
		return (Config.MAX_MONSTER_ANIMATION > 0) && !(this instanceof L2GrandBossInstance);
	}
	
	public void seeSpell(L2PcInstance caster, L2Character target, Skill skill)
	{
		final int actorLevel = caster.getLevel();
		double divisor = 0;
		
		if (actorLevel < 10)
		{
			divisor = 15;
		}
		else if ((actorLevel > 9) && (actorLevel < 20))
		{
			divisor = 11.5;
		}
		else if ((actorLevel > 19) && (actorLevel < 30))
		{
			divisor = 8.5;
		}
		else if ((actorLevel > 29) && (actorLevel < 40))
		{
			divisor = 6;
		}
		else if ((actorLevel > 39) && (actorLevel < 50))
		{
			divisor = 4;
		}
		else if ((actorLevel > 49) && (actorLevel < 60))
		{
			divisor = 2.5;
		}
		else if ((actorLevel > 59) && (actorLevel < 70))
		{
			divisor = 1.5;
		}
		else if (actorLevel > 69)
		{
			divisor = 1;
		}
		
		int hate = 0;
		
		// Calculate hate depending on skill type
		switch (skill.getSkillType())
		{
			case MANAHEAL:
			case BALANCE_LIFE:
			{
				if (target.getLastHealAmount() > (getStat().getMaxHp() / 5))
				{
					target.setLastHealAmount(getStat().getMaxHp() / 5);
				}
				
				hate = (int) (target.getLastHealAmount() / divisor);
				break;
			}
			case BUFF:
			{
				hate = (int) ((skill.getLevel() * caster.getStat().getMpConsume(skill)) / divisor);
				break;
			}
		}
		
		// Add extra hate if target is party member
		if ((caster != target) && (skill.getTargetType() == SkillTargetType.TARGET_PARTY))
		{
			if ((getStat().getMaxHp() / 3) < (int) (((getHating(target) - getHating(caster)) + 800) / divisor))
			{
				hate += getStat().getMaxHp() / 3;
			}
			else
			{
				hate += (int) (((getHating(target) - getHating(caster)) + 800) / divisor);
			}
		}
		
		// finally apply hate
		addDamageHate(caster, 0, hate);
	}
	
	// XXX RETURN SPAWN POINT -------------------------------------------------------------------------------
	private boolean isReturningToSpawnPoint = false;
	
	public final boolean isReturningToSpawnPoint()
	{
		return isReturningToSpawnPoint;
	}
	
	public final void setIsReturningToSpawnPoint(boolean value)
	{
		isReturningToSpawnPoint = value;
	}
	
	// AGGRO LIST -------------------------------------------------------------------------------------------
	private final Map<L2Character, AggroInfo> aggroList = new ConcurrentHashMap<>();
	
	/**
	 * Use this to Remove Object from this Map This Should be Synchronized While Iterating over This Map - if u cant iterating and removing object at once
	 * @return
	 */
	public final Map<L2Character, AggroInfo> getAggroList()
	{
		return aggroList;
	}
	
	/**
	 * Return True if the aggroList of this L2Attackable is Empty.<br>
	 * @return
	 */
	public boolean noHasTarget()
	{
		return aggroList.isEmpty();
	}
	
	/**
	 * Return True if the aggroList of this L2Attackable contains the L2Character.<br>
	 * @param  player The L2Character searched in the aggroList of the L2Attackable
	 * @return
	 */
	public boolean containsTarget(L2Character player)
	{
		return aggroList.containsKey(player);
	}
	
	/**
	 * Clear the aggroList of the L2Attackable.<br>
	 */
	public void clearAggroList()
	{
		aggroList.clear();
		
		overhit = false;
		overhitDamage = 0;
		overhitAttacker = null;
	}
	
	// UTIL ----------------------------------------------------------------------------
	
	/**
	 * Define a chance to drop depending on the defined configs.
	 * @param  drop
	 * @param  dropChance
	 * @return
	 */
	private int getChanceDropItems(DropInstance drop, int dropChance)
	{
		if (drop.getItemId() == Inventory.ADENA_ID)
		{
			dropChance *= Config.DROP_CHANCE_ADENA;
		}
		else if (Config.DROP_CHANCE_ITEMS_BY_ID.containsKey(drop.getItemId()))
		{
			dropChance *= Config.DROP_CHANCE_ITEMS_BY_ID.get(drop.getItemId());
		}
		else
		{
			dropChance *= isRaid() ? Config.DROP_CHANCE_RAID : Config.DROP_CHANCE_ITEMS;
		}
		
		return dropChance;
	}
	
	/**
	 * Define the number of items to be obtained from what defined within our configs files.
	 * @param  drop
	 * @param  isSweep
	 * @return
	 */
	private int getAmountDropItems(DropInstance drop, boolean isSweep)
	{
		int itemAmount = 0;
		
		// Get min and max item quantity
		final int minCount = drop.getMinDrop();
		final int maxCount = drop.getMaxDrop();
		
		// Get the item quantity dropped
		if (minCount < maxCount)
		{
			itemAmount = Rnd.get(minCount, maxCount);
		}
		else
		{
			itemAmount = minCount;
		}
		
		int itemId = drop.getItemId();
		
		switch (itemId)
		{
			case Inventory.ADENA_ID:
				itemAmount *= Config.DROP_AMOUNT_ADENA;
				break;
			
			case SevenSignsManager.SEAL_STONE_BLUE_ID:
			case SevenSignsManager.SEAL_STONE_RED_ID:
			case SevenSignsManager.SEAL_STONE_GREEN_ID:
				itemAmount *= Config.DROP_AMOUNT_SEAL_STONE;
				break;
			
			default:
				if (Config.DROP_AMOUNT_ITEMS_BY_ID.containsKey(itemId))
				{
					itemAmount *= Config.DROP_AMOUNT_ITEMS_BY_ID.get(itemId);
				}
				else if (isSweep)
				{
					itemAmount *= Config.DROP_AMOUNT_SPOIL;
				}
				else if (isRaid())
				{
					itemAmount *= Config.DROP_AMOUNT_RAID;
				}
				else
				{
					itemAmount *= Config.DROP_AMOUNT_ITEMS;
				}
		}
		return itemAmount;
	}
}
