package l2j.gameserver.model.skills.conditions;

import l2j.gameserver.model.skills.stats.Env;
import l2j.gameserver.task.continuous.GameTimeTaskManager;

/**
 * @author mkizub
 */
public class ConditionGameTime extends Condition
{
	public enum CheckGameTime
	{
		NIGHT
	}
	
	private CheckGameTime check;
	private boolean required;
	
	public ConditionGameTime(CheckGameTime check, boolean required)
	{
		this.check = check;
		this.required = required;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		switch (check)
		{
			case NIGHT:
				return GameTimeTaskManager.getInstance().isNight() == required;
		}
		return !required;
	}
}
