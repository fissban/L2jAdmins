package l2j.gameserver.model.actor;

import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.knownlist.PlayableKnownList;
import l2j.gameserver.model.actor.stat.PlayableStat;
import l2j.gameserver.model.actor.status.PlayableStatus;
import l2j.gameserver.model.actor.templates.CharTemplate;
import l2j.gameserver.model.itemcontainer.Inventory;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.scripts.ScriptState;
import main.EngineModsManager;

/**
 * This class represents all Playable characters in the world.<BR>
 * L2PlayableInstance :<BR>
 * <li>L2PcInstance</li>
 * <li>L2Summon</li>
 */
public abstract class L2Playable extends L2Character
{
	private boolean isNoblesseBlessed = false; // for Noblesse Blessing skill, restores buffs after death
	private boolean getCharmOfLuck = false; // Charm of Luck - During a Raid/Boss war, decreased chance of dropping items
	private boolean isSilentMoving = false; // Silent Move
	
	/**
	 * Constructor of L2PlayableInstance (use L2Character constructor).<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Call the L2Character constructor to create an empty skills slot and link copy basic Calculator set to this L2PlayableInstance</li>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2CharTemplate to apply to the L2PlayableInstance
	 */
	public L2Playable(int objectId, CharTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2Playable);
	}
	
	@Override
	public void initKnownList()
	{
		setKnownList(new PlayableKnownList(this));
	}
	
	@Override
	public PlayableKnownList getKnownList()
	{
		return (PlayableKnownList) super.getKnownList();
	}
	
	@Override
	public void initStat()
	{
		setStat(new PlayableStat(this));
	}
	
	@Override
	public PlayableStat getStat()
	{
		return (PlayableStat) super.getStat();
	}
	
	@Override
	public void initStatus()
	{
		setStatus(new PlayableStatus(this));
	}
	
	@Override
	public PlayableStatus getStatus()
	{
		return (PlayableStatus) super.getStatus();
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		// killing is only possible one time
		synchronized (this)
		{
			if (isDead())
			{
				return false;
			}
			// now reset currentHp to zero
			setCurrentHp(0);
			setIsDead(true);
		}
		
		EngineModsManager.onKill(killer, this, killer instanceof L2Summon);
		EngineModsManager.onDeath(this);
		
		// Set target to null and cancel Attack or Cast
		setTarget(null);
		
		// Stop movement
		stopMove(null);
		
		// Stop HP/MP/CP Regeneration task
		getStatus().stopHpMpRegeneration();
		
		// Same thing if the Character isn't a Noblesse Blessed L2Playable
		if (isNoblesseBlessed())
		{
			stopNoblesseBlessing();
			
			if (getCharmOfLuck())
			{
				stopCharmOfLuck();
			}
		}
		else
		{
			stopAllEffects();
		}
		
		if (killer != null)
		{
			L2PcInstance actingKiller = killer.getActingPlayer();
			
			if (actingKiller != null)
			{
				actingKiller.onKillUpdatePvPKarma(this);
			}
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		broadcastStatusUpdate();
		
		// Notify Quest of L2Playable's death
		final L2PcInstance actingPlayer = getActingPlayer();
		if (actingPlayer != null)
		{
			for (ScriptState qs : actingPlayer.getNotifyQuestOfDeath())
			{
				qs.getQuest().notifyDeath((killer == null ? this : killer), actingPlayer);
			}
		}
		
		// Notify L2Character AI
		if (getAI() != null)
		{
			getAI().notifyEvent(CtrlEventType.DEAD);
		}
		
		getAttackByList().clear();
		return true;
	}
	
	public boolean checkIfPvP(L2PcInstance target)
	{
		L2PcInstance player = getActingPlayer();
		// Active player is null
		if (player == null)
		{
			return false;
		}
		// Active player has karma
		if (player.getKarma() != 0)
		{
			return false;
		}
		
		// Target player is null
		if (target == null)
		{
			return false;
		}
		// Target player is self
		if (target == this)
		{
			return false;
		}
		// Target player has karma
		if (target.getKarma() != 0)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Return True.
	 */
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	public final boolean isNoblesseBlessed()
	{
		return isNoblesseBlessed;
	}
	
	public final void setIsNoblesseBlessed(boolean value)
	{
		isNoblesseBlessed = value;
	}
	
	public final boolean getCharmOfLuck()
	{
		return getCharmOfLuck;
	}
	
	public final void setCharmOfLuck(boolean value)
	{
		getCharmOfLuck = value;
	}
	
	public boolean isSilentMoving()
	{
		return isSilentMoving;
	}
	
	public void setSilentMoving(boolean value)
	{
		isSilentMoving = value;
	}
	
	public final void startNoblesseBlessing()
	{
		setIsNoblesseBlessed(true);
		updateAbnormalEffect();
	}
	
	public final void stopNoblesseBlessing()
	{
		stopEffects(EffectType.NOBLESSE_BLESSING);
		
		setIsNoblesseBlessed(false);
		updateAbnormalEffect();
	}
	
	public final void startCharmOfLuck()
	{
		setCharmOfLuck(true);
		updateAbnormalEffect();
	}
	
	public final void stopCharmOfLuck()
	{
		stopEffects(EffectType.CHARM_OF_LUCK);
		
		setCharmOfLuck(false);
		updateAbnormalEffect();
	}
	
	public void addTimeStamp(Skill skill, long reuse)
	{
		//
	}
	
	public Inventory getInventory()
	{
		return null;
	}
}
