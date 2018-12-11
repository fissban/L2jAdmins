package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author mkizub
 */
public class ConditionPlayerState extends Condition
{
	public enum CheckPlayerState
	{
		RESTING,
		MOVING,
		RUNNING,
		FLYING,
		BEHIND,
		FRONT,
		STANDING
	}
	
	final CheckPlayerState check;
	final boolean required;
	
	public ConditionPlayerState(CheckPlayerState check, boolean required)
	{
		this.check = check;
		this.required = required;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		switch (check)
		{
			case RESTING:
				if (env.getPlayer() instanceof L2PcInstance)
				{
					return ((L2PcInstance) env.getPlayer()).isSitting() == required;
				}
				return !required;
			case MOVING:
				return env.getPlayer().isMoving() == required;
			case RUNNING:
				return (env.getPlayer().isMoving() == required) && (env.getPlayer().isRunning() == required);
			case STANDING:
				if (env.getPlayer() != null)
				{
					return (required != (((L2PcInstance) env.getPlayer()).isSitting() || env.getPlayer().isMoving()));
				}
				return (required != env.getPlayer().isMoving());
			case FLYING:
				return env.getPlayer().isFlying() == required;
			case BEHIND:
				return env.getPlayer().isBehindTarget() == required;
			case FRONT:
				return env.getPlayer().isInFrontOfTarget() == required;
		}
		return !required;
	}
}
