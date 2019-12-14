package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author fissban
 */
public class EffectBuff extends EffectHateDamage
{
	private static int HATE = 100;
	
	public EffectBuff(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BUFF;
	}
	
	@Override
	public void onStart()
	{
		// increase hate for mobs in knownlist
		hateForKnownList();
	}
	
	@Override
	public void onExit()
	{
		//
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	protected void mobSeeSpell(L2Attackable mob, L2PcInstance caster, L2Character target, Skill skill)
	{
		mob.addDamageHate(caster, 0, HATE);
	}
}
