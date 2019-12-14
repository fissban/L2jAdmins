package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2NpcInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author littlecrow
 */
public class EffectFear extends Effect
{
	private static final int FEAR_RANGE = 500;
	
	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.FEAR;
	}
	
	@Override
	public void onStart()
	{
		if ((getEffected() instanceof L2PcInstance) && (getEffector() instanceof L2PcInstance))
		{
			switch (getSkill().getId())
			{
				case 1376:
				case 1169:
				case 65:
				case 1092:
				case 98:
				case 1272:
				case 1381:
				case 763:
					break;
				default:
					return;
			}
		}
		
		if ((getEffected() instanceof L2NpcInstance) || (getEffected() instanceof L2SiegeFlagInstance) || (getEffected() instanceof L2SiegeSummonInstance))
		{
			return;
		}
		
		if (getEffected().isAfraid())
		{
			return;
		}
		
		getEffected().startFear();
		return;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopFear();
	}
	
	@Override
	public boolean onActionTime()
	{
		if (!(getEffected() instanceof L2PetInstance))
		{
			getEffected().setRunning();
		}
		
		final int victimX = getEffected().getX();
		final int victimY = getEffected().getY();
		final int victimZ = getEffected().getZ();
		
		final int posX = victimX + (((victimX > getEffector().getX()) ? 1 : -1) * FEAR_RANGE);
		final int posY = victimY + (((victimY > getEffector().getY()) ? 1 : -1) * FEAR_RANGE);
		
		getEffected().getAI().setIntention(CtrlIntentionType.MOVE_TO, GeoEngine.getInstance().canMoveToTargetLoc(victimX, victimY, victimZ, posX, posY, victimZ));
		return true;
	}
}
