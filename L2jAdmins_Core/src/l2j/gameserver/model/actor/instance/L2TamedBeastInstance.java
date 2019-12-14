package l2j.gameserver.model.actor.instance;

import java.util.List;
import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.NpcInfo;
import l2j.gameserver.network.external.server.StopMove;
import l2j.util.Rnd;

// While a tamed beast behaves a lot like a pet (ingame) and does have
// an owner, in all other aspects, it acts like a mob.
// In addition, it can be fed in order to increase its duration.
// This class handles the running tasks, AI, and feed of the mob.
// The (mostly optional) AI on feeding the spawn is handled by the datapack ai script
public final class L2TamedBeastInstance extends L2FeedableBeastInstance
{
	private static final int MAX_DISTANCE_FROM_HOME = 30000;
	private static final int MAX_DISTANCE_FROM_OWNER = 2000;
	private static final int MAX_DURATION = 1200000; // 20 minutes
	private static final int DURATION_CHECK_INTERVAL = 60000; // 1 minute
	private static final int DURATION_INCREASE_INTERVAL = 20000; // 20 secs (gained upon feeding)
	private static final int BUFF_INTERVAL = 5000; // 5 seconds
	
	private int foodSkillId;
	private int remainingTime = MAX_DURATION;
	private int homeX, homeY, homeZ;
	private L2PcInstance owner;
	private Future<?> buffTask = null;
	private Future<?> durationCheckTask = null;
	
	public L2TamedBeastInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2TamedBeastInstance);
		setHome(this);
	}
	
	public L2TamedBeastInstance(int objectId, NpcTemplate template, L2PcInstance owner, int foodSkillId, int x, int y, int z)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2TamedBeastInstance);
		setCurrentHp(getStat().getMaxHp());
		setCurrentMp(getStat().getMaxMp());
		setOwner(owner);
		setFoodType(foodSkillId);
		setHome(x, y, z);
		this.spawnMe(x, y, z);
	}
	
	public void onReceiveFood()
	{
		// Eating food extends the duration by 20secs, to a max of 20minutes
		remainingTime = remainingTime + DURATION_INCREASE_INTERVAL;
		if (remainingTime > MAX_DURATION)
		{
			remainingTime = MAX_DURATION;
		}
	}
	
	public LocationHolder getHome()
	{
		return new LocationHolder(homeX, homeY, homeZ);
	}
	
	public void setHome(int x, int y, int z)
	{
		homeX = x;
		homeY = y;
		homeZ = z;
	}
	
	public void setHome(L2Character c)
	{
		setHome(c.getX(), c.getY(), c.getZ());
	}
	
	public int getRemainingTime()
	{
		return remainingTime;
	}
	
	public void setRemainingTime(int duration)
	{
		remainingTime = duration;
	}
	
	public int getFoodType()
	{
		return foodSkillId;
	}
	
	public void setFoodType(int foodItemId)
	{
		if (foodItemId > 0)
		{
			foodSkillId = foodItemId;
			
			// start the duration checks
			// start the buff tasks
			if (durationCheckTask != null)
			{
				durationCheckTask.cancel(true);
			}
			durationCheckTask = ThreadPoolManager.scheduleAtFixedRate(new CheckDuration(this), DURATION_CHECK_INTERVAL, DURATION_CHECK_INTERVAL);
		}
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		getAI().stopFollow();
		buffTask.cancel(true);
		durationCheckTask.cancel(true);
		
		// clean up variables
		if (owner != null)
		{
			owner.setTrainedBeast(null);
		}
		buffTask = null;
		durationCheckTask = null;
		owner = null;
		foodSkillId = 0;
		remainingTime = 0;
		return true;
	}
	
	public L2PcInstance getOwner()
	{
		return owner;
	}
	
	public void setOwner(L2PcInstance owner)
	{
		if (owner != null)
		{
			this.owner = owner;
			setTitle(owner.getName());
			// broadcast the new title
			broadcastPacket(new NpcInfo(this, owner));
			owner.setTrainedBeast(this);
			// always and automatically follow the owner.
			getAI().startFollow(owner, 100);
			// instead of calculating this value each time, let's get this now and pass it on
			int totalBuffsAvailable = 0;
			for (Skill skill : getTemplate().getSkills().values())
			{
				// if the skill is a buff, check if the owner has it already [ owner.getEffect(Skill skill) ]
				if (skill.getSkillType() == SkillType.BUFF)
				{
					totalBuffsAvailable++;
				}
			}
			
			// start the buff tasks
			if (buffTask != null)
			{
				buffTask.cancel(true);
			}
			buffTask = ThreadPoolManager.scheduleAtFixedRate(new CheckOwnerBuffs(this, totalBuffsAvailable), BUFF_INTERVAL, BUFF_INTERVAL);
		}
		else
		{
			doDespawn(); // despawn if no owner
		}
	}
	
	public boolean isTooFarFromHome()
	{
		return !(this.isInsideRadius(homeX, homeY, homeZ, MAX_DISTANCE_FROM_HOME, true, true));
	}
	
	public void doDespawn()
	{
		// stop running tasks
		getAI().stopFollow();
		buffTask.cancel(true);
		durationCheckTask.cancel(true);
		stopHpMpRegeneration();
		
		// clean up variables
		if (owner != null)
		{
			owner.setTrainedBeast(null);
		}
		setTarget(null);
		buffTask = null;
		durationCheckTask = null;
		owner = null;
		foodSkillId = 0;
		remainingTime = 0;
		
		// remove the spawn
		deleteMe();
	}
	
	// notification triggered by the owner when the owner is attacked.
	// tamed mobs will heal/recharge or debuff the enemy according to their skills
	public void onOwnerGotAttacked(L2Character attacker)
	{
		// check if the owner is no longer around...if so, despawn
		if ((owner == null) || (!owner.isOnline()))
		{
			doDespawn();
			return;
		}
		// if the owner is too far away, stop anything else and immediately run towards the owner.
		if (!owner.isInsideRadius(this, MAX_DISTANCE_FROM_OWNER, true, true))
		{
			getAI().startFollow(owner);
			return;
		}
		// if the owner is dead, do nothing...
		if (owner.isDead())
		{
			return;
		}
		
		// if the tamed beast is currently in the middle of casting, let it complete its skill...
		if (isCastingNow())
		{
			return;
		}
		
		float HPRatio = ((float) owner.getCurrentHp()) / owner.getStat().getMaxHp();
		
		// if the owner has a lot of HP, then debuff the enemy with a random debuff among the available skills
		// use of more than one debuff at this moment is acceptable
		if (HPRatio >= 0.8)
		{
			for (Skill skill : getTemplate().getSkills().values())
			{
				// if the skill is a debuff, check if the attacker has it already [ attacker.getEffect(Skill skill) ]
				if ((skill.getSkillType() == SkillType.DEBUFF) && (Rnd.get(3) < 1) && (attacker.getEffect(skill) != null))
				{
					sitCastAndFollow(skill, attacker);
				}
			}
		}
		// for HP levels between 80% and 50%, do not react to attack events (so that MP can regenerate a bit)
		// for lower HP ranges, heal or recharge the owner with 1 skill use per attack.
		else if (HPRatio < 0.5)
		{
			int chance = 1;
			if (HPRatio < 0.25)
			{
				chance = 2;
			}
			
			// if the owner has a lot of HP, then debuff the enemy with a random debuff among the available skills
			for (Skill skill : getTemplate().getSkills().values())
			{
				// if the skill is a buff, check if the owner has it already [ owner.getEffect(Skill skill) ]
				if ((Rnd.get(5) < chance))
				{
					boolean hasConcidered = false;
					switch (skill.getSkillType())
					{
						case HEAL:
						case BALANCE_LIFE:
						case COMBATPOINTHEAL:
						case MANAHEAL:
						case MANA_BY_LEVEL:
						case MANARECHARGE:
							hasConcidered = true;
							break;
						case BUFF:
							switch (skill.getEffectType())
							{
								case HEAL_OVER_TIME:
								case MANA_HEAL_OVER_TIME:
									hasConcidered = true;
									break;
							}
							break;
					}
					
					if (hasConcidered)
					{
						sitCastAndFollow(skill, owner);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Prepare and cast a skill: First smoothly prepare the beast for casting, by abandoning other actions Next, call super.doCast(skill) in order to actually cast the spell Finally, return to auto-following the owner.
	 * @param skill
	 * @param target
	 * @see          l2j.gameserver.model.actor.L2Character#doCast(l2j.gameserver.model.actor.manager.character.skills.Skill)
	 */
	protected void sitCastAndFollow(Skill skill, L2Character target)
	{
		stopMove(null);
		broadcastPacket(new StopMove(this));
		getAI().setIntention(CtrlIntentionType.IDLE);
		
		setTarget(target);
		doCast(skill);
		getAI().setIntention(CtrlIntentionType.FOLLOW, owner);
	}
	
	private class CheckDuration implements Runnable
	{
		private final L2TamedBeastInstance tamedBeast;
		
		CheckDuration(L2TamedBeastInstance tamedBeast)
		{
			this.tamedBeast = tamedBeast;
		}
		
		@Override
		public void run()
		{
			int foodTypeSkillId = tamedBeast.getFoodType();
			L2PcInstance owner = tamedBeast.getOwner();
			tamedBeast.setRemainingTime(tamedBeast.getRemainingTime() - DURATION_CHECK_INTERVAL);
			
			// I tried to avoid this as much as possible...but it seems I can't avoid hardcoding
			// ids further, except by carrying an additional variable just for these two lines...
			// Find which food item needs to be consumed.
			ItemInstance item = null;
			if (foodTypeSkillId == 2188)
			{
				item = owner.getInventory().getItemById(6643);
			}
			else if (foodTypeSkillId == 2189)
			{
				item = owner.getInventory().getItemById(6644);
			}
			
			// if the owner has enough food, call the item handler (use the food and triffer all necessary actions)
			if ((item != null) && (item.getCount() >= 1))
			{
				L2Object oldTarget = owner.getTarget();
				owner.setTarget(tamedBeast);
				// emulate a call to the owner using food, but bypass all checks for range, etc
				// this also causes a call to the AI tasks handling feeding, which may call onReceiveFood as required.
				owner.callSkill(SkillData.getInstance().getSkill(foodTypeSkillId, 1), List.of(tamedBeast));
				owner.setTarget(oldTarget);
			}
			else
			{
				// if the owner has no food, the beast immediately despawns, except when it was only
				// newly spawned. Newly spawned beasts can last up to 5 minutes
				if (tamedBeast.getRemainingTime() < (MAX_DURATION - 300000))
				{
					tamedBeast.setRemainingTime(-1);
				}
			}
			
			/*
			 * There are too many conflicting reports about whether distance from home should be taken into consideration. Disabled for now. if (tamedBeast.isTooFarFromHome()) tamedBeast.setRemainingTime(-1);
			 */
			if (tamedBeast.getRemainingTime() <= 0)
			{
				tamedBeast.doDespawn();
			}
		}
	}
	
	private class CheckOwnerBuffs implements Runnable
	{
		private final L2TamedBeastInstance tamedBeast;
		private final int numBuffs;
		
		CheckOwnerBuffs(L2TamedBeastInstance tamedBeast, int numBuffs)
		{
			this.tamedBeast = tamedBeast;
			this.numBuffs = numBuffs;
		}
		
		@Override
		public void run()
		{
			L2PcInstance owner = tamedBeast.getOwner();
			
			// check if the owner is no longer around...if so, despawn
			if ((owner == null) || (!owner.isOnline()))
			{
				doDespawn();
				return;
			}
			// if the owner is too far away, stop anything else and immediately run towards the owner.
			if (!isInsideRadius(owner, MAX_DISTANCE_FROM_OWNER, true, true))
			{
				getAI().startFollow(owner);
				return;
			}
			// if the owner is dead, do nothing...
			if (owner.isDead())
			{
				return;
			}
			// if the tamed beast is currently casting a spell, do not interfere (do not attempt to cast anything new yet).
			if (isCastingNow())
			{
				return;
			}
			
			int totalBuffsOnOwner = 0;
			int i = 0;
			int rand = Rnd.get(numBuffs);
			Skill buffToGive = null;
			
			// get this npc's skills: getSkills()
			for (Skill skill : tamedBeast.getTemplate().getSkills().values())
			{
				// if the skill is a buff, check if the owner has it already [ owner.getEffect(Skill skill) ]
				if (skill.getSkillType() == SkillType.BUFF)
				{
					if (i == rand)
					{
						buffToGive = skill;
					}
					i++;
					if (owner.getEffect(skill) != null)
					{
						totalBuffsOnOwner++;
					}
				}
			}
			// if the owner has less than 60% of this beast's available buff, cast a random buff
			if (((numBuffs * 2) / 3) > totalBuffsOnOwner)
			{
				tamedBeast.sitCastAndFollow(buffToGive, owner);
			}
			getAI().setIntention(CtrlIntentionType.FOLLOW, tamedBeast.getOwner());
		}
	}
}
