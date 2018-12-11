package l2j.gameserver.model.actor.ai;

import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;

/**
 * @author Yaroslav
 */
public class NextAction
{
	/** After which CtrlEvent is this action supposed to run. */
	private final CtrlEventType event;
	
	/** What is the intention of the action, e.g. if AI gets this CtrlIntention set, NextAction is canceled. */
	private final CtrlIntentionType intention;
	
	/** Wrapper for NextAction content. */
	private final Runnable runnable;
	
	/**
	 * Single constructor.
	 * @param event     : After which the NextAction is triggered.
	 * @param intention : CtrlIntention of the action.
	 * @param runnable  :
	 */
	public NextAction(CtrlEventType event, CtrlIntentionType intention, Runnable runnable)
	{
		this.event = event;
		this.intention = intention;
		this.runnable = runnable;
	}
	
	/**
	 * @return the event
	 */
	public CtrlEventType getEvent()
	{
		return event;
	}
	
	/**
	 * @return the intention
	 */
	public CtrlIntentionType getIntention()
	{
		return intention;
	}
	
	/**
	 * Do action.
	 */
	public void run()
	{
		runnable.run();
	}
}
