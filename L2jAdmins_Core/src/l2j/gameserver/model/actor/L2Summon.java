package l2j.gameserver.model.actor;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.handler.ItemHandler;
import l2j.gameserver.handler.ItemHandler.IItemHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable.AggroInfo;
import l2j.gameserver.model.actor.ai.CharacterAI;
import l2j.gameserver.model.actor.ai.SummonAI;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.knownlist.SummonKnownList;
import l2j.gameserver.model.actor.manager.character.itemcontainer.Inventory;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.stat.SummonStat;
import l2j.gameserver.model.actor.manager.character.status.SummonStatus;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.actor.manager.pc.party.Party;
import l2j.gameserver.model.items.ItemWeapon;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcInfo;
import l2j.gameserver.network.external.server.PartySpelled;
import l2j.gameserver.network.external.server.PetDelete;
import l2j.gameserver.network.external.server.PetInfo;
import l2j.gameserver.network.external.server.PetItemList;
import l2j.gameserver.network.external.server.PetStatusUpdate;
import l2j.gameserver.network.external.server.RelationChanged;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.DecayTaskManager;
import main.data.memory.ObjectData;
import main.holders.objects.PlayerHolder;

public abstract class L2Summon extends L2Playable
{
	private L2PcInstance owner;
	
	private int attackRange = 36; // Melee range
	private boolean follow = true;
	private boolean previousFollowStatus = true;
	public boolean isSiegeGolem = false;
	private boolean showSummonAnimation;
	
	public L2Summon(int objectId, NpcTemplate template, L2PcInstance owner)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2Summon);
		
		showSummonAnimation = true;
		this.owner = owner;
		ai = new SummonAI(this);
		
		setXYZInvisible(owner.getX() + 50, owner.getY() + 100, owner.getZ() + 100);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new SummonKnownList(this));
	}
	
	@Override
	public final SummonKnownList getKnownList()
	{
		return (SummonKnownList) super.getKnownList();
	}
	
	@Override
	public void initStat()
	{
		setStat(new SummonStat(this));
	}
	
	@Override
	public SummonStat getStat()
	{
		return (SummonStat) super.getStat();
	}
	
	@Override
	public void initStatus()
	{
		setStatus(new SummonStatus(this));
	}
	
	@Override
	public SummonStatus getStatus()
	{
		return (SummonStatus) super.getStatus();
	}
	
	@Override
	public CharacterAI getAI()
	{
		if (ai == null)
		{
			synchronized (this)
			{
				if (ai == null)
				{
					ai = new SummonAI(this);
				}
			}
		}
		
		return ai;
	}
	
	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	// this defines the action buttons, 1 for Summon, 2 for Pets
	public abstract int getSummonType();
	
	@Override
	public void updateAbnormalEffect()
	{
		getKnownList().getObjectType(L2PcInstance.class).forEach(player -> player.sendPacket(new NpcInfo(this, player, 1)));
	}
	
	/**
	 * @return Returns the mountable.
	 */
	public boolean isMountable()
	{
		return false;
	}
	
	public long getExpForThisLevel()
	{
		if (getStat().getLevel() >= ExperienceData.getInstance().getMaxLevel())
		{
			return 0;
		}
		return ExperienceData.getInstance().getExpForLevel(getStat().getLevel());
	}
	
	public long getExpForNextLevel()
	{
		if (getStat().getLevel() >= (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			return 0;
		}
		return ExperienceData.getInstance().getExpForLevel(getStat().getLevel() + 1);
	}
	
	public final L2PcInstance getOwner()
	{
		return owner;
	}
	
	public final int getId()
	{
		return getTemplate().getId();
	}
	
	public final short getSoulShotsPerHit()
	{
		if (getTemplate().getSs() > 1)
		{
			return getTemplate().getSs();
		}
		
		return 1;
	}
	
	public final short getSpiritShotsPerHit()
	{
		if (getTemplate().getBss() > 1)
		{
			return getTemplate().getBss();
		}
		
		return 1;
	}
	
	public void followOwner()
	{
		setFollowStatus(true);
	}
	
	@Override
	public void onSpawn()
	{
		ObjectData.get(PlayerHolder.class, getOwner()).setSummon(this);
		
		setFollowStatus(true);
		
		updateAndBroadcastStatus(0);
		
		getOwner().sendPacket(new RelationChanged(this, getOwner().getRelation(getOwner()), false));
		
		for (final L2PcInstance player : getOwner().getKnownList().getObjectTypeInRadius(L2PcInstance.class, 800))
		{
			player.sendPacket(new RelationChanged(this, getOwner().getRelation(player), isAutoAttackable(player)));
		}
		
		setShowSummonAnimation(false); // addVisibleObject created the info packets with summon animation
		revalidateZone(true);
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (getOwner() != null)
		{
			for (final L2Character TgMob : getKnownList().getObjectType(L2Character.class))
			{
				if (TgMob == null)
				{
					continue;
				}
				// get the mobs which have aggro on this instance
				if (TgMob instanceof L2Attackable)
				{
					if (((L2Attackable) TgMob).isDead())
					{
						continue;
					}
					
					final AggroInfo info = ((L2Attackable) TgMob).getAggroList().get(this);
					if (info != null)
					{
						((L2Attackable) TgMob).addDamageHate(getOwner(), info.getDamage(), info.getHate());
					}
				}
			}
			
			// Disable beastshots
			for (int itemId : getOwner().getAutoSoulShot())
			{
				if (BEAST_SPIRITSHOT.contains(itemId) || BEAST_SOULSHOTS.contains(itemId))
				{
					getOwner().disableAutoShot(itemId);
				}
			}
		}
		
		DecayTaskManager.getInstance().addDecayTask(this);
		
		return true;
	}
	
	public void stopDecay()
	{
		DecayTaskManager.getInstance().cancelDecayTask(this);
	}
	
	@Override
	public void updateEffectIcons(boolean partyOnly)
	{
		final PartySpelled ps = new PartySpelled(this);
		
		for (final Effect effect : getAllEffects())
		{
			if ((effect == null) || !effect.getShowIcon())
			{
				continue;
			}
			
			if (effect.getInUse())
			{
				effect.addPartySpelledIcon(ps);
			}
		}
		
		getOwner().sendPacket(ps);
	}
	
	@Override
	public void broadcastStatusUpdate()
	{
		super.broadcastStatusUpdate();
		updateAndBroadcastStatus(1);
	}
	
	public void updateAndBroadcastStatus(int val)
	{
		if (getOwner() == null)
		{
			return;
		}
		
		getOwner().sendPacket(new PetInfo(this, val));
		getOwner().sendPacket(new PetStatusUpdate(this));
		if (isVisible())
		{
			broadcastNpcInfo(val);
		}
		updateEffectIcons(true);
	}
	
	public void broadcastNpcInfo(int val)
	{
		for (final L2PcInstance player : getKnownList().getObjectType(L2PcInstance.class))
		{
			if ((player == null) || (player == getOwner()))
			{
				continue;
			}
			player.sendPacket(new NpcInfo(this, player, val));
			
		}
	}
	
	@Override
	public void deleteMe()
	{
		getAI().stopFollow();
		getOwner().sendPacket(new PetDelete(getSummonType(), getObjectId()));
		
		super.deleteMe();
		
		getKnownList().removeAllObjects();
		getOwner().setPet(null);
	}
	
	public synchronized void unSummon()
	{
		if (isVisible() && !isDead())
		{
			setTarget(null);
			abortCast();
			abortAttack();
			
			// stop HP and MP regeneration
			stopHpMpRegeneration();
			
			store();
			
			giveAllToOwner();
			
			// Stop AI tasks
			if (hasAI())
			{
				getAI().stopAITask();
			}
			
			stopAllEffects();
			
			getKnownList().removeAllObjects();
			
			// Disable beastshots
			for (int itemId : getOwner().getAutoSoulShot())
			{
				if (BEAST_SPIRITSHOT.contains(itemId) || BEAST_SOULSHOTS.contains(itemId))
				{
					getOwner().disableAutoShot(itemId);
				}
			}
			
			decayMe();
			
			getOwner().sendPacket(new PetDelete(getSummonType(), getObjectId()));
			
			getOwner().setPet(null);
		}
	}
	
	public float getExpPenalty()
	{
		return 0;
	}
	
	public int getAttackRange()
	{
		return attackRange;
	}
	
	public void setAttackRange(int range)
	{
		if (range < 36)
		{
			range = 36;
		}
		attackRange = range;
	}
	
	public void setFollowStatus(boolean state)
	{
		follow = state;
		if (follow)
		{
			getAI().setIntention(CtrlIntentionType.FOLLOW, getOwner());
		}
		else
		{
			getAI().setIntention(CtrlIntentionType.IDLE, null);
		}
	}
	
	public boolean getFollowStatus()
	{
		return follow;
	}
	
	public boolean isSiegeGolem()
	{
		return isSiegeGolem;
	}
	
	public boolean isHungry()
	{
		return false;
	}
	
	public int getWeapon()
	{
		return 0;
	}
	
	public int getArmor()
	{
		return 0;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return owner.isAutoAttackable(attacker);
	}
	
	/**
	 * @return Returns the showSummonAnimation.
	 */
	public boolean isShowSummonAnimation()
	{
		return showSummonAnimation;
	}
	
	/**
	 * @param showSummonAnimation The showSummonAnimation to set.
	 */
	public void setShowSummonAnimation(boolean showSummonAnimation)
	{
		this.showSummonAnimation = showSummonAnimation;
	}
	
	@Override
	public boolean isInCombat()
	{
		return getOwner().isInCombat();
	}
	
	public int getControlItemId()
	{
		return 0;
	}
	
	public int getCurrentFed()
	{
		return 0;
	}
	
	public int getMaxFeed()
	{
		return 0;
	}
	
	public int getPetSpeed()
	{
		return getTemplate().getBaseRunSpd();
	}
	
	public ItemWeapon getActiveWeapon()
	{
		return null;
	}
	
	@Override
	public Inventory getInventory()
	{
		return null;
	}
	
	public void doPickupItem(L2Object object)
	{
		//
	}
	
	public void giveAllToOwner()
	{
		//
	}
	
	public void store()
	{
		//
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public ItemWeapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public ItemWeapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	/**
	 * Return the L2Party object of its L2PcInstance owner or null.
	 */
	@Override
	public Party getParty()
	{
		if (owner == null)
		{
			return null;
		}
		return owner.getParty();
	}
	
	@Override
	public boolean isInvul()
	{
		return isInvul || isTeleporting || getOwner().isSpawnProtected();
	}
	
	/**
	 * Return True if the L2Character has a Party in progress.
	 */
	@Override
	public boolean isInParty()
	{
		if (owner == null)
		{
			return false;
		}
		return owner.getParty() != null;
	}
	
	/**
	 * Check if the active Skill can be casted.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Check if the target is correct</li>
	 * <li>Check if the target is in the skill cast range</li>
	 * <li>Check if the summon owns enough HP and MP to cast the skill</li>
	 * <li>Check if all skills are enabled and this skill is enabled</li>
	 * <li>Check if the skill is active</li>
	 * <li>Notify the AI with AI_INTENTION_CAST and target</li>
	 * @param skill    The Skill to use
	 * @param forceUse used to force ATTACK on players
	 * @param dontMove used to prevent movement, if not in range
	 */
	public void useMagic(Skill skill, boolean forceUse, boolean dontMove)
	{
		if ((skill == null) || isDead())
		{
			return;
		}
		
		// Check if the skill is active
		if (skill.isPassive())
		{
			return;
		}
		
		// If a skill is currently being used
		if (isCastingNow())
		{
			return;
		}
		
		// Set current pet skill
		getOwner().setCurrentPetSkill(skill, forceUse, dontMove);
		
		// Get the target for the skill
		L2Object target = null;
		
		switch (skill.getTargetType())
		{
			// OWNER_PET should be cast even if no target has been found
			case TARGET_OWNER_PET:
				target = getOwner();
				break;
			// PARTY, AURA, SELF should be cast even if no target has been found
			case TARGET_PARTY:
			case TARGET_AURA:
			case TARGET_AURA_UNDEAD:
			case TARGET_SELF:
			case TARGET_CORPSE_ALLY:
				target = this;
				break;
			default:
				// Get the first target of the list
				target = skill.getFirstOfTargetList(this);
				break;
		}
		
		// Check the validity of the target
		if (target == null)
		{
			if (getOwner() != null)
			{
				getOwner().sendPacket(SystemMessage.TARGET_CANT_FOUND);
			}
			return;
		}
		
		// Check if this skill is enabled (ex : reuse time)
		if (isAllSkillsDisabled() && (getOwner() != null) && (!getOwner().isGM()))
		{
			return;
		}
		
		// Check if the summon has enough MP
		if (getCurrentMp() < (getStat().getMpConsume(skill) + getStat().getMpInitialConsume(skill)))
		{
			// Send a System Message to the caster
			if (getOwner() != null)
			{
				getOwner().sendPacket(SystemMessage.NOT_ENOUGH_MP);
			}
			return;
		}
		
		// Check if the summon has enough HP
		if (getCurrentHp() <= skill.getHpConsume())
		{
			// Send a System Message to the caster
			if (getOwner() != null)
			{
				getOwner().sendPacket(SystemMessage.NOT_ENOUGH_HP);
			}
			return;
		}
		
		// Check if this is offensive magic skill
		if (skill.isOffensive())
		{
			if (isInsidePeaceZone(target) && (getOwner() != null) && (!getOwner().isGM()))
			{
				// If summon or target is in a peace zone, send a system message TARGET_IN_PEACEZONE
				getOwner().sendPacket(SystemMessage.TARGET_IN_PEACEZONE);
				return;
			}
			
			if ((getOwner() != null) && getOwner().isInOlympiadMode() && !getOwner().isOlympiadStart())
			{
				// if L2PcInstance is in Olympiad and the match isn't already start, send a Server->Client packet ActionFailed
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// Check if the target is attackable
			if (target instanceof L2DoorInstance)
			{
				if (!((L2DoorInstance) target).isAutoAttackable(getOwner()))
				{
					return;
				}
			}
			else
			{
				if (!target.isAttackable() && (getOwner() != null) && (!getOwner().isGM()))
				{
					return;
				}
				
				// Check if a Forced ATTACK is in progress on non-attackable target
				if (!target.isAutoAttackable(this) && !forceUse && (skill.getTargetType() != SkillTargetType.TARGET_AURA) && (skill.getTargetType() != SkillTargetType.TARGET_CLAN) && (skill.getTargetType() != SkillTargetType.TARGET_ALLY) && (skill.getTargetType() != SkillTargetType.TARGET_PARTY)
					&& (skill.getTargetType() != SkillTargetType.TARGET_SELF))
				{
					return;
				}
			}
		}
		
		// Notify the AI with AI_INTENTION_CAST and target
		getAI().setIntention(CtrlIntentionType.CAST, skill, target);
	}
	
	@Override
	public void setIsImmobilized(boolean value)
	{
		super.setIsImmobilized(value);
		
		if (value)
		{
			previousFollowStatus = getFollowStatus();
			// if immobilized temporarily disable follow mode
			if (previousFollowStatus)
			{
				setFollowStatus(false);
			}
		}
		else
		{
			// if not more immobilized restore previous follow mode
			setFollowStatus(previousFollowStatus);
		}
	}
	
	public void setOwner(L2PcInstance newOwner)
	{
		owner = newOwner;
	}
	
	/**
	 * Servitors' skills automatically change their level based on the servitor's level. Until level 70, the servitor gets 1 lv of skill per 10 levels. After that, it is 1 skill level per 5 servitor levels. If the resulting skill level doesn't exist use the max that does exist!
	 */
	@Override
	public void doCast(Skill skill)
	{
		if (!getOwner().checkPvpSkill(getTarget(), skill, true) && (!getOwner().isGM()))
		{
			getOwner().sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			getOwner().sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final int petLevel = getLevel();
		int skillLevel = petLevel / 10;
		if (petLevel >= 70)
		{
			skillLevel += (petLevel - 65) / 10;
		}
		
		// adjust the level for servitors less than lvl 10
		if (skillLevel < 1)
		{
			skillLevel = 1;
		}
		
		final Skill skillToCast = SkillData.getInstance().getSkill(skill.getId(), skillLevel);
		if (skillToCast != null)
		{
			super.doCast(skillToCast);
		}
		else
		{
			super.doCast(skill);
		}
	}
	
	// XXX SHOTS ====================================================================================== //
	
	private static final List<Integer> BEAST_SOULSHOTS = new ArrayList<>();
	
	{
		BEAST_SOULSHOTS.add(6645);// BeastSoulShot
	}
	
	private static final List<Integer> BEAST_SPIRITSHOT = new ArrayList<>();
	
	{
		BEAST_SPIRITSHOT.add(6646);// BeastSpiritShot
		BEAST_SPIRITSHOT.add(6647);// BeastBlessedSpiritShot
	}
	
	private int shotsMask = 0;
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (type == null)
		{
			return;
		}
		
		if (charged)
		{
			shotsMask |= type.getMask();
		}
		else
		{
			shotsMask &= ~type.getMask();
		}
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		ItemInstance item;
		IItemHandler handler;
		
		if ((getOwner().getAutoSoulShot() == null) || getOwner().getAutoSoulShot().isEmpty())
		{
			return;
		}
		
		for (int itemId : getOwner().getAutoSoulShot())
		{
			item = getOwner().getInventory().getItemById(itemId);
			
			if (item != null)
			{
				if (magic && BEAST_SPIRITSHOT.contains(itemId))
				{
					handler = ItemHandler.getHandler(itemId);
					if (handler != null)
					{
						handler.useItem(getOwner(), item);
					}
				}
				if (physical && BEAST_SOULSHOTS.contains(itemId))
				{
					handler = ItemHandler.getHandler(itemId);
					if (handler != null)
					{
						handler.useItem(getOwner(), item);
					}
				}
			}
			else
			{
				getOwner().removeAutoSoulShot(itemId);
			}
		}
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar)
	{
		// Check if the L2PcInstance is the owner of the Pet
		if (activeChar.equals(getOwner()))
		{
			activeChar.sendPacket(new PetInfo(this, 0));
			
			// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
			updateEffectIcons(true);
			
			if (this instanceof L2PetInstance)
			{
				activeChar.sendPacket(new PetItemList((L2PetInstance) this));
			}
		}
		else
		{
			activeChar.sendPacket(new NpcInfo(this, activeChar, 0));
		}
	}
	
	@Override
	public L2PcInstance getActingPlayer()
	{
		return getOwner();
	}
}
