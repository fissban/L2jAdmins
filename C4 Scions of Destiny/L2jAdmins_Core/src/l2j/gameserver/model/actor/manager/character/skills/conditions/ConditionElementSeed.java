package l2j.gameserver.model.actor.manager.character.skills.conditions;

import l2j.gameserver.model.actor.manager.character.skills.effects.type.EffectSeed;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author Advi
 */
public class ConditionElementSeed extends Condition
{
	private static final int[] SEED_SKILLS =
	{
		1285,
		1286,
		1287
	};
	
	private final int[] requiredSeeds;
	
	public ConditionElementSeed(int[] seeds)
	{
		requiredSeeds = seeds;
	}
	
	ConditionElementSeed(int fire, int water, int wind, int various, int any)
	{
		requiredSeeds = new int[5];
		requiredSeeds[0] = fire;
		requiredSeeds[1] = water;
		requiredSeeds[2] = wind;
		requiredSeeds[3] = various;
		requiredSeeds[4] = any;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		int[] seeds = new int[3];
		for (int i = 0; i < seeds.length; i++)
		{
			seeds[i] = (env.getPlayer().getEffect(SEED_SKILLS[i]) instanceof EffectSeed ? ((EffectSeed) env.getPlayer().getEffect(SEED_SKILLS[i])).getPowerSeed() : 0);
			if (seeds[i] >= requiredSeeds[i])
			{
				seeds[i] -= requiredSeeds[i];
			}
			else
			{
				return false;
			}
		}
		
		if (requiredSeeds[3] > 0)
		{
			int count = 0;
			for (int i = 0; (i < seeds.length) && (count < requiredSeeds[3]); i++)
			{
				if (seeds[i] > 0)
				{
					seeds[i]--;
					count++;
				}
			}
			if (count < requiredSeeds[3])
			{
				return false;
			}
		}
		
		if (requiredSeeds[4] > 0)
		{
			int count = 0;
			for (int i = 0; (i < seeds.length) && (count < requiredSeeds[4]); i++)
			{
				count += seeds[i];
			}
			if (count < requiredSeeds[4])
			{
				return false;
			}
		}
		
		return true;
	}
}
