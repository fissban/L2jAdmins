package l2j.gameserver.model.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.effects.enums.AbnormalEffectType;
import l2j.gameserver.model.skills.effects.enums.EffectStateType;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.funcs.Func;
import l2j.gameserver.model.skills.funcs.FuncTemplate;
import l2j.gameserver.model.skills.funcs.Lambda;
import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.network.external.server.ExOlympiadSpelledInfo;
import l2j.gameserver.network.external.server.MagicEffectIcons;
import l2j.gameserver.network.external.server.PartySpelled;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.1.2.1.2.12 $ $Date: 2005/04/11 10:06:07 $
 */
public abstract class Effect
{
	protected static final Logger LOG = Logger.getLogger(Effect.class.getName());
	
	private static final List<Func> EMPTY_FUNCTION_SET = Collections.emptyList();
	
	// member effector is the instance of L2Character that cast/used the spell/skill that is
	// causing this effect. Do not confuse with the instance of L2Character that
	// is being affected by this effect.
	private final L2Character effector;
	
	// member effected is the instance of L2Character that was affected
	// by this effect. Do not confuse with the instance of L2Character that
	// casted/used this effect.
	private final L2Character effected;
	
	// the skill that was used.
	private final Skill skill;
	
	// or the items that was used.
	// private final L2Item item;
	
	// the value of an update
	private final Lambda lambda;
	
	// the current state
	private EffectStateType state;
	
	// period, seconds
	private int period;
	protected long periodStartTime;
	protected int periodFirstTime;
	
	// function templates
	private final List<FuncTemplate> funcTemplates;
	
	// initial count
	private final int totalCount;
	
	// counter
	private int count;
	
	// abnormal effect mask
	private final AbnormalEffectType abnormalEffect;
	
	// show icon
	private final boolean icon;
	
	public boolean preventExitUpdate;
	
	private boolean isSelfEffect = false;
	
	private ScheduledFuture<?> currentFuture;
	
	/** The Identifier of the stack group */
	private final String stackType;
	
	/** The position of the effect in the stack group */
	private final float stackOrder;
	
	private boolean inUse = false;
	/** get power from effect */
	public double power;
	/** get rate from effect */
	public double rate;
	
	protected Effect(Env env, EffectTemplate template)
	{
		state = EffectStateType.CREATED;
		
		skill = env.getSkill();
		effected = env.getTarget();
		effector = env.getPlayer();
		
		lambda = template.lambda;
		funcTemplates = template.funcTemplates;
		count = template.counter;
		totalCount = template.counter;
		period = template.period;
		if (env.isSkillMastery())
		{
			period *= 2;
		}
		abnormalEffect = template.abnormalEffect;
		stackType = template.stackType;
		stackOrder = template.stackOrder;
		periodStartTime = System.currentTimeMillis();
		periodFirstTime = 0;
		icon = template.icon;
		power = template.power;
		rate = template.rate;
		// scheduleEffect();
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getTotalCount()
	{
		return totalCount;
	}
	
	public void setCount(int newCount)
	{
		count = newCount;
	}
	
	public void setFirstTime(int newFirstTime)
	{
		periodFirstTime = Math.min(newFirstTime, period);
		periodStartTime = System.currentTimeMillis() - (periodFirstTime * 1000);
	}
	
	public boolean getShowIcon()
	{
		return icon;
	}
	
	public int getPeriod()
	{
		return period;
	}
	
	public int getTime()
	{
		return (int) ((System.currentTimeMillis() - periodStartTime) / 1000);
	}
	
	public int getTaskTime()
	{
		if (count == totalCount)
		{
			return 0;
		}
		return (Math.abs((count - totalCount) + 1) * period) + getTime() + 1;
	}
	
	public boolean getInUse()
	{
		return inUse;
	}
	
	public void setInUse(boolean inUse)
	{
		this.inUse = inUse;
		
		if (inUse)
		{
			onStart();
		}
		else
		{
			onExit();
		}
	}
	
	public String getStackType()
	{
		return stackType;
	}
	
	public float getStackOrder()
	{
		return stackOrder;
	}
	
	public final Skill getSkill()
	{
		return skill;
	}
	
	public final L2Character getEffector()
	{
		return effector;
	}
	
	public final L2Character getEffected()
	{
		return effected;
	}
	
	public boolean isSelfEffect()
	{
		return isSelfEffect;
	}
	
	public void setSelfEffect()
	{
		isSelfEffect = true;
	}
	
	public final double calc()
	{
		final Env env = new Env();
		env.setPlayer(effector);
		env.setTarget(effected);
		env.setSkill(skill);
		return lambda.calc(env);
	}
	
	private synchronized void startEffectTask()
	{
		if (period > 0)
		{
			stopEffectTask();
			final int initialDelay = Math.max((period - periodFirstTime) * 1000, 5);
			if (count > 1)
			{
				currentFuture = ThreadPoolManager.scheduleAtFixedRate(new EffectTask(), initialDelay, period * 1000);
			}
			else
			{
				currentFuture = ThreadPoolManager.schedule(new EffectTask(), initialDelay);
			}
		}
		if (state == EffectStateType.ACTING)
		{
			effected.addEffect(this);
		}
	}
	
	/**
	 * Stop the L2Effect task and send Server->Client update packet.<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Cancel the effect in the the abnormal effect map of the L2Character</li>
	 * <li>Stop the task of the L2Effect, remove it and update client magic icon</li>
	 */
	public final void exit()
	{
		exit(false);
	}
	
	public final void exit(boolean preventUpdate)
	{
		preventExitUpdate = preventUpdate;
		state = EffectStateType.FINISHING;
		scheduleEffect();
	}
	
	/**
	 * Stop the task of the L2Effect, remove it and update client magic icon.<br>
	 * <b><u> Actions</u> :</b><br>
	 * <li>Cancel the task</li>
	 * <li>Stop and remove L2Effect from L2Character and update client magic icon</li>
	 */
	public synchronized void stopEffectTask()
	{
		// Cancel the task
		if (currentFuture != null)
		{
			currentFuture.cancel(false);
			currentFuture = null;
		}
		
		// To avoid possible NPE caused by player crash
		if (effected != null)
		{
			effected.removeEffect(this);
		}
		else
		{
			LOG.warning("Effected is null for skill " + skill.getId() + " on effect " + getEffectType());
		}
	}
	
	/**
	 * returns effect type
	 * @return
	 */
	public abstract EffectType getEffectType();
	
	/**
	 * Start the effect in the the abnormal effect map of the effected L2Character.
	 */
	public void onStart()
	{
		//
	}
	
	/**
	 * Cancel the effect in the the abnormal effect map of the effected L2Character.
	 */
	public void onExit()
	{
		//
	}
	
	/**
	 * Return true for continuation of this effect
	 * @return
	 */
	public abstract boolean onActionTime();
	
	public final void rescheduleEffect()
	{
		if (state != EffectStateType.ACTING)
		{
			scheduleEffect();
		}
		else
		{
			if (period != 0)
			{
				startEffectTask();
				return;
			}
		}
	}
	
	public final void scheduleEffect()
	{
		switch (state)
		{
			case CREATED:
			{
				state = EffectStateType.ACTING;
				
				if (skill.isEnemyOnly() && (getEffected() instanceof L2PcInstance))
				{
					getEffected().sendPacket(new SystemMessage(SystemMessage.YOU_FEEL_S1_EFFECT).addString(skill.getName()));
				}
				
				// Start abnormal effect
				if (abnormalEffect != AbnormalEffectType.NULL)
				{
					getEffected().startAbnormalEffect(abnormalEffect);
				}
				
				if (period != 0)
				{
					startEffectTask();
					return;
				}
				
				// effects not having count or period should start
				onStart();
			}
			case ACTING:
			{
				if (count > 0)
				{
					count--;
					
					if (getInUse())
					{
						// effect has to be in use
						if (onActionTime() && (count > 0))
						{
							return; // false causes effect to finish right away
						}
					}
					else if (count > 0)
					{
						return;
					}
				}
				state = EffectStateType.FINISHING;
			}
			case FINISHING:
			{
				// If the time left is equal to zero, send the message
				if ((totalCount > 1) && icon && (getEffected() instanceof L2PcInstance))
				{
					getEffected().sendPacket(new SystemMessage(SystemMessage.S1_HAS_WORN_OFF).addString(skill.getName()));
				}
				
				// Stop abnormal effect
				if (abnormalEffect != AbnormalEffectType.NULL)
				{
					getEffected().stopAbnormalEffect(abnormalEffect);
				}
				
				// Stop the task of the L2Effect, remove it and update client magic icon
				stopEffectTask();
				
				// Cancel the effect in the the abnormal effect map of the L2Character
				onExit();
			}
		}
	}
	
	public List<Func> getStatFuncs()
	{
		if (funcTemplates == null)
		{
			return EMPTY_FUNCTION_SET;
		}
		final List<Func> funcs = new ArrayList<>(funcTemplates.size());
		
		final Env env = new Env();
		env.setPlayer(getEffector());
		env.setTarget(getEffected());
		env.setSkill(getSkill());
		
		for (final FuncTemplate t : funcTemplates)
		{
			final Func f = t.getFunc(env, this); // effect is owner
			if (f != null)
			{
				funcs.add(f);
			}
		}
		
		return funcs;
	}
	
	public final void addIcon(MagicEffectIcons mi)
	{
		if (state != EffectStateType.ACTING)
		{
			return;
		}
		
		final ScheduledFuture<?> future = currentFuture;
		
		if (totalCount > 1)
		{
			mi.addEffect(getSkill().getId(), getLevel(), -1);
		}
		else if (future != null)
		{
			mi.addEffect(getSkill().getId(), getLevel(), (int) future.getDelay(TimeUnit.MILLISECONDS));
		}
	}
	
	public final void addPartySpelledIcon(PartySpelled ps)
	{
		if (state != EffectStateType.ACTING)
		{
			return;
		}
		
		final ScheduledFuture<?> future = currentFuture;
		if (future == null)
		{
			return;
		}
		
		ps.addPartySpelledEffect(getSkill().getId(), getLevel(), (int) future.getDelay(TimeUnit.MILLISECONDS));
	}
	
	public final void addOlympiadSpelledIcon(ExOlympiadSpelledInfo os)
	{
		if (state != EffectStateType.ACTING)
		{
			return;
		}
		
		final ScheduledFuture<?> future = currentFuture;
		if (future == null)
		{
			return;
		}
		
		os.addEffect(getSkill().getId(), getLevel(), (int) future.getDelay(TimeUnit.MILLISECONDS));
	}
	
	public final class EffectTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				periodFirstTime = 0;
				periodStartTime = System.currentTimeMillis();
				
				scheduleEffect();
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE, "", e);
			}
		}
	}
	
	public int getLevel()
	{
		return getSkill().getLevel();
	}
	
	/**
	 * Get power from effect
	 * @return
	 */
	public double getPower()
	{
		return power;
	}
	
	/**
	 * Get rate from effect
	 * @return
	 */
	public double getRate()
	{
		return rate;
	}
}
