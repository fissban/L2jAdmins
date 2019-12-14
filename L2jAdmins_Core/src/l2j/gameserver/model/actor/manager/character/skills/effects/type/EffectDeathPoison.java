package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.ScriptState;

public class EffectDeathPoison extends Effect
{
	public EffectDeathPoison(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public void onStart()
	{
		getEffected().startRooted();
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopRooting(false);
		
		ScriptState qs = ((L2PcInstance) getEffected()).getScriptState("Q501_ProofOfClanAlliance");
		if ((qs != null) && qs.getQuest().getName().equals("Part4"))
		{
			qs.exitQuest(true);
		}
	}
	
	@Override
	public boolean onActionTime()
	{
		if (getEffected().isDead())
		{
			return false;
		}
		
		double damage = calc();
		if (damage >= (getEffected().getCurrentHp() - 1))
		{
			if (getSkill().isToggle())
			{
				getEffected().sendPacket(SystemMessage.SKILL_REMOVED_DUE_LACK_HP);
				return false;
			}
		}
		
		boolean awake = !(getEffected() instanceof L2Attackable) && !((getSkill().getTargetType() == SkillTargetType.TARGET_SELF) && getSkill().isToggle());
		
		getEffected().reduceCurrentHp(damage, getEffector(), awake);
		return true;
	}
}
