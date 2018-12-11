package l2j.gameserver.model.skills.effects.type;

import l2j.gameserver.model.skills.effects.Effect;
import l2j.gameserver.model.skills.effects.EffectTemplate;
import l2j.gameserver.model.skills.effects.enums.EffectType;
import l2j.gameserver.model.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectCancelMage extends Effect
{
	public EffectCancelMage(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MAGE_BANE;
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
					case 1059: // Greater Empower
					case 2056: // Scroll of Mystic Empower
					case 4331: // Empower For Novice
					case 4356: // Clan Hall: Empower
					case 1085: // Acumen
					case 2053: // Scroll of Greater Acumen
					case 4329: // Acumen For Novice
					case 4355: // Clan Hall: Acumen
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
