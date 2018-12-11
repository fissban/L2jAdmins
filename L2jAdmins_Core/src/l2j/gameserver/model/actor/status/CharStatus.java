package l2j.gameserver.model.actor.status;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.stat.CharStat;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.gameserver.scripts.ScriptState;
import l2j.util.Rnd;

public class CharStatus
{
	protected static final Logger LOG = Logger.getLogger(CharStatus.class.getName());
	
	private L2Character activeChar;
	
	private double currentCp = 0; // Current CP of the L2Character
	private double currentHp = 0; // Current HP of the L2Character
	private double currentMp = 0; // Current MP of the L2Character
	
	/** Array containing all clients that need to be notified about hp/mp updates of the L2Character */
	private final Set<L2Character> statusListener = new CopyOnWriteArraySet<>();
	
	private Future<?> regTask;
	private int flagsRegenActive = 0;
	private static final byte REGEN_FLAG_CP = 4;
	private static final byte REGEN_FLAG_HP = 1;
	private static final byte REGEN_FLAG_MP = 2;
	
	public CharStatus(L2Character activeChar)
	{
		this.activeChar = activeChar;
	}
	
	public L2Character getActiveChar()
	{
		return activeChar;
	}
	
	/**
	 * Add the object to the list of L2Character that must be informed of HP/MP updates of this L2Character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * Each L2Character owns a list called <B>_statusListener</B> that contains all L2PcInstance to inform of HP/MP updates. Players who must be informed are players that target this L2Character. When a RegenTask is in progress sever just need to go through this list to send Server->Client packet
	 * StatusUpdate.<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Target a PC or NPC</li>
	 * @param object L2Character to add to the listener
	 */
	public synchronized void addStatusListener(L2Character object)
	{
		if (object == getActiveChar())
		{
			return;
		}
		
		statusListener.add(object);
	}
	
	/**
	 * Remove the object from the list of L2Character that must be informed of HP/MP updates of this L2Character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * Each L2Character owns a list called <B>_statusListener</B> that contains all L2PcInstance to inform of HP/MP updates. Players who must be informed are players that target this L2Character. When a RegenTask is in progress sever just need to go through this list to send Server->Client packet
	 * StatusUpdate.<BR>
	 * <B><U> Example of use </U> :</B><BR>
	 * <li>Untarget a PC or NPC</li>
	 * @param object L2Character to add to the listener
	 */
	public synchronized void removeStatusListener(L2Character object)
	{
		statusListener.remove(object);
	}
	
	/**
	 * Return the list of L2Character that must be informed of HP/MP updates of this L2Character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * Each L2Character owns a list called <B>_statusListener</B> that contains all L2PcInstance to inform of HP/MP updates. Players who must be informed are players that target this L2Character. When a RegenTask is in progress sever just need to go through this list to send Server->Client packet
	 * StatusUpdate.<BR>
	 * @return The list of L2Character to inform or null if empty
	 */
	public synchronized Set<L2Character> getStatusListener()
	{
		return statusListener;
	}
	
	public final void reduceCp(int value)
	{
		if (getCurrentCp() > value)
		{
			setCurrentCp(getCurrentCp() - value);
		}
		else
		{
			setCurrentCp(0);
		}
	}
	
	/**
	 * Reduce the current HP of the L2Character and launch the doDie Task if necessary.<BR>
	 * <B><U> Overridden in </U> :</B><BR>
	 * <li>L2Attackable : Update the attacker AggroInfo of the L2Attackable aggroList</li>
	 * @param value
	 * @param attacker The L2Character who attacks
	 */
	public void reduceHp(double value, L2Character attacker)
	{
		reduceHp(value, attacker, true);
	}
	
	public void reduceHp(double value, L2Character attacker, boolean awake)
	{
		if (getActiveChar().isInvul())
		{
			return;
		}
		
		if (getActiveChar().isDead())
		{
			return;
		}
		
		if (awake && getActiveChar().isSleeping())
		{
			getActiveChar().stopSleeping(true);
		}
		
		if (getActiveChar().isStunned() && (Rnd.get(10) == 0))
		{
			getActiveChar().stopStunning(true);
		}
		
		if (value > 0) // Reduce Hp if any
		{
			// If we're dealing with an L2Attackable Instance and the attacker hit it with an over-hit enabled skill, set the over-hit values.
			// Anything else, clear the over-hit flag
			if (getActiveChar() instanceof L2Attackable)
			{
				if (((L2Attackable) getActiveChar()).isOverhit())
				{
					((L2Attackable) getActiveChar()).setOverhitValues(attacker, value);
				}
				else
				{
					((L2Attackable) getActiveChar()).overhitEnabled(false);
				}
			}
			
			setCurrentHp(Math.max(getCurrentHp() - value, 0)); // Set Hp
		}
		else if (getActiveChar() instanceof L2Attackable)
		{
			// If we're dealing with an L2Attackable Instance and the attacker's hit didn't kill the mob, clear the over-hit flag
			((L2Attackable) getActiveChar()).overhitEnabled(false);
		}
		
		if (getActiveChar().getCurrentHp() < 0.5)
		{
			// Stop any attack
			getActiveChar().abortAttack();
			// Stop any cast
			getActiveChar().abortCast();
			// Set target to null and cancel Attack or Cast
			getActiveChar().setTarget(null);
			// Stop movement
			getActiveChar().stopMove(null);
			// Stop HP/MP/CP Regeneration task
			getActiveChar().getStatus().stopHpMpRegeneration();
			
			// Start the doDie process
			getActiveChar().doDie(attacker);
		}
		else
		{
			// If we're dealing with an L2Attackable Instance and the attacker's hit didn't kill the mob, clear the over-hit flag
			if (getActiveChar() instanceof L2Attackable)
			{
				((L2Attackable) getActiveChar()).overhitEnabled(false);
			}
		}
	}
	
	public final void reduceMp(double value)
	{
		value = getCurrentMp() - value;
		if (value < 0)
		{
			value = 0;
		}
		setCurrentMp(value);
	}
	
	/**
	 * Start the HP/MP/CP Regeneration task.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Calculate the regenerate task period</li>
	 * <li>Launch the HP/MP/CP Regeneration task with Medium priority</li>
	 */
	public synchronized final void startHpMpRegeneration()
	{
		if ((regTask == null) && !getActiveChar().isDead())
		{
			// Get the Regeneration period
			int period = Formulas.getRegeneratePeriod(getActiveChar());
			
			// Create the HP/MP/CP Regeneration task
			regTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> regenTask(), period, period);
		}
	}
	
	/**
	 * Stop the HP/MP/CP Regeneration task.<BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <li>Set the RegenActive flag to False</li>
	 * <li>Stop the HP/MP/CP Regeneration task</li>
	 */
	public synchronized final void stopHpMpRegeneration()
	{
		if (regTask != null)
		{
			// Stop the HP/MP/CP Regeneration task
			regTask.cancel(false);
			regTask = null;
			
			// Set the RegenActive flag to false
			flagsRegenActive = 0;
		}
	}
	
	public final double getCurrentCp()
	{
		return currentCp;
	}
	
	public final void setCurrentCp(double newCp)
	{
		setCurrentCp(newCp, true);
	}
	
	public final void setCurrentCp(double newCp, boolean broadcastPacket)
	{
		// Get the Max CP of the L2Character
		final int maxCp = getActiveChar().getStat().getMaxCp();
		
		synchronized (this)
		{
			if (getActiveChar().isDead())
			{
				return;
			}
			
			if (newCp < 0)
			{
				newCp = 0;
			}
			
			if (newCp >= maxCp)
			{
				// Set the RegenActive flag to false
				currentCp = maxCp;
				flagsRegenActive &= ~REGEN_FLAG_CP;
				
				// Stop the HP/MP/CP Regeneration task
				if (flagsRegenActive == 0)
				{
					stopHpMpRegeneration();
				}
			}
			else
			{
				// Set the RegenActive flag to true
				currentCp = newCp;
				flagsRegenActive |= REGEN_FLAG_CP;
				
				// Start the HP/MP/CP Regeneration task with Medium priority
				startHpMpRegeneration();
			}
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		if (broadcastPacket)
		{
			getActiveChar().broadcastStatusUpdate();
		}
	}
	
	public final double getCurrentHp()
	{
		return currentHp;
	}
	
	public final void setCurrentHp(double newHp)
	{
		setCurrentHp(newHp, true);
	}
	
	public final void setCurrentHp(double newHp, boolean broadcastPacket)
	{
		// Get the Max HP of the L2Character
		final double maxHp = getActiveChar().getStat().getMaxHp();
		
		synchronized (this)
		{
			if (getActiveChar().isDead())
			{
				return;
			}
			
			if (newHp >= maxHp)
			{
				// Set the RegenActive flag to false
				currentHp = maxHp;
				flagsRegenActive &= ~REGEN_FLAG_HP;
				
				// Stop the HP/MP/CP Regeneration task
				if (flagsRegenActive == 0)
				{
					stopHpMpRegeneration();
				}
			}
			else
			{
				// Set the RegenActive flag to true
				currentHp = newHp;
				flagsRegenActive |= REGEN_FLAG_HP;
				
				// Start the HP/MP/CP Regeneration task with Medium priority
				startHpMpRegeneration();
			}
		}
		
		if (getActiveChar() instanceof L2PcInstance)
		{
			if (getCurrentHp() <= (maxHp * .3))
			{
				ScriptState qs = ((L2PcInstance) getActiveChar()).getScriptState("Q255_Tutorial");
				if (qs != null)
				{
					qs.getQuest().notifyEvent("CE45", null, ((L2PcInstance) getActiveChar()));
				}
			}
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		if (broadcastPacket)
		{
			getActiveChar().broadcastStatusUpdate();
		}
	}
	
	public final void setCurrentHpMp(double newHp, double newMp)
	{
		setCurrentHp(newHp, false);
		setCurrentMp(newMp, true); // send the StatusUpdate only once
	}
	
	public final double getCurrentMp()
	{
		return currentMp;
	}
	
	public final void setCurrentMp(double newMp)
	{
		setCurrentMp(newMp, true);
	}
	
	public final void setCurrentMp(double newMp, boolean broadcastPacket)
	{
		// Get the Max MP of the L2Character
		final int maxMp = getActiveChar().getStat().getMaxMp();
		
		synchronized (this)
		{
			if (getActiveChar().isDead())
			{
				return;
			}
			
			if (newMp >= maxMp)
			{
				// Set the RegenActive flag to false
				currentMp = maxMp;
				flagsRegenActive &= ~REGEN_FLAG_MP;
				
				// Stop the HP/MP/CP Regeneration task
				if (flagsRegenActive == 0)
				{
					stopHpMpRegeneration();
				}
			}
			else
			{
				// Set the RegenActive flag to true
				currentMp = newMp;
				flagsRegenActive |= REGEN_FLAG_MP;
				
				// Start the HP/MP/CP Regeneration task with Medium priority
				startHpMpRegeneration();
			}
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		if (broadcastPacket)
		{
			getActiveChar().broadcastStatusUpdate();
		}
	}
	
	/** Task of HP/MP/CP regeneration */
	public void regenTask()
	{
		try
		{
			CharStat charstat = getActiveChar().getStat();
			// Modify the current CP of the L2Character and broadcast Server->Client packet StatusUpdate
			if (getCurrentCp() < charstat.getMaxCp())
			{
				setCurrentCp(getCurrentCp() + Formulas.calcCpRegen(getActiveChar()), false);
			}
			// Modify the current HP of the L2Character and broadcast Server->Client packet StatusUpdate
			if (getCurrentHp() < charstat.getMaxHp())
			{
				setCurrentHp(getCurrentHp() + Formulas.calcHpRegen(getActiveChar()), false);
			}
			// Modify the current MP of the L2Character and broadcast Server->Client packet StatusUpdate
			if (getCurrentMp() < charstat.getMaxMp())
			{
				setCurrentMp(getCurrentMp() + Formulas.calcMpRegen(getActiveChar()), false);
			}
			if (!getActiveChar().isInActiveRegion())
			{
				// no broadcast necessary for characters that are in inactive regions.
				// stop regeneration for characters who are filled up and in an inactive region.
				if ((getCurrentCp() == charstat.getMaxCp()) && (getCurrentHp() == charstat.getMaxHp()) && (getCurrentMp() == charstat.getMaxMp()))
				{
					stopHpMpRegeneration();
				}
			}
			else
			{
				getActiveChar().broadcastStatusUpdate(); // send the StatusUpdate packet
			}
		}
		catch (Throwable e)
		{
			LOG.log(Level.SEVERE, "", e);
		}
		
	}
}
