package l2j.gameserver.model.skills.effects.type;

import java.util.List;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;
import l2j.util.Rnd;

/**
 * @author Fissban
 */
public class EffectCancel extends Effect
{
	public EffectCancel(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CANCEL;
	}
	
	@Override
	public void onStart()
	{
		// Get all skills effects on the L2Character
		List<Effect> effects = getEffected().getAllEffects();
		
		if (effects.isEmpty())
		{
			return;
		}
		
		int max = getSkill().getMaxNegatedEffects();
		
		// Get the maximum effects that can be cancelled
		if ((max == 0) || (max > effects.size()))
		{
			max = effects.size();
		}
		
		int cont = 0;
		
		if (effects.size() < max)
		{
			max = effects.size();
		}
		
		while (cont < max)
		{
			final Effect e = effects.get(Rnd.get(effects.size()));
			
			if (e == null)
			{
				continue;
			}
			
			switch (e.getSkill().getId())
			{
				case 1323:
				case 1325:
				case 4082:
				case 4215:
				case 4515:
					max--;
					continue;
			}
			
			if (!(e.getEffectType() == EffectType.BUFF))
			{
				max--;
				continue;
			}
			
			int chance = 60; // 60%
			if (e.getSkill().getLevel() > 100)
			{
				chance += Integer.parseInt(String.valueOf(e.getSkill().getLevel()).substring(1));
			}
			
			if (Rnd.get(100) > chance)
			{
				e.exit();
				cont++;
			}
			else
			{
				cont++;
			}
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
