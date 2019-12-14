package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import java.util.ArrayList;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.util.Rnd;

/**
 * @author littlecrow
 */
public class EffectConfusion extends Effect
{
	private static final int RADIUS = 600;
	
	public EffectConfusion(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CONFUSION;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startConfused();
		
		onActionTime();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopConfused();
	}
	
	@Override
	public boolean onActionTime()
	{
		var targetList = new ArrayList<L2Character>();
		
		// Getting the possible targets
		for (var character : getEffected().getKnownList().getObjectTypeInRadius(L2Character.class, RADIUS))
		{
			if (character != getEffector())
			{
				targetList.add(character);
			}
		}
		
		// if there is no target, exit function
		if (targetList.isEmpty())
		{
			return true;
		}
		
		// Choosing randomly a new target
		var target = targetList.get(Rnd.nextInt(targetList.size()));
		
		// Attacking the target
		getEffected().setTarget(target);
		getEffected().getAI().setIntention(CtrlIntentionType.ATTACK, target);
		
		return true;
	}
}
