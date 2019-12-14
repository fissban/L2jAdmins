package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;

public class EffectManaHealOverTime extends Effect
{
	public EffectManaHealOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MANA_HEAL_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double mp = getEffected().getCurrentMp();
		double maxmp = getEffected().getStat().getMaxMp();
		mp += calc();
		if (mp > maxmp)
		{
			mp = maxmp;
		}
		getEffected().setCurrentMp(mp);
		StatusUpdate sump = new StatusUpdate(getEffected().getObjectId());
		sump.addAttribute(StatusUpdateType.CUR_MP, (int) mp);
		getEffected().sendPacket(sump);
		return true;
	}
}
