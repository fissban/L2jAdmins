package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectCancelWarrior extends Effect
{
	public EffectCancelWarrior(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.WARRIOR_BANE;
	}
	
	@Override
	public void onStart()
	{
		for (final Effect e : getEffected().getAllEffects())
		{
			if (e != null)
			{
				switch (e.getSkill().getId())
				{
					case 1144: // Servitor Wind Walk
					case 1204: // Wind Walk
					case 2058: // Scroll of Wind Walk
					case 4322: // Wind Walk For Novice
					case 4342: // Clan Hall: Wind Walk
					case 1086: // Haste
					case 1141: // Servitor Haste
					case 2033: // Haste potion
					case 2034: // Greater Haste Potion
					case 2054: // Scroll of Haste
					case 4327: // Haste For Novice
					case 4357: // Clan Hall: Haste
						e.exit();
						break;
				}
			}
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
