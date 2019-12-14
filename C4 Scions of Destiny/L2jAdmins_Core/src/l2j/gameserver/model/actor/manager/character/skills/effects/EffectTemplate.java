package l2j.gameserver.model.actor.manager.character.skills.effects;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.manager.character.skills.conditions.Condition;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.AbnormalEffectType;
import l2j.gameserver.model.actor.manager.character.skills.funcs.FuncTemplate;
import l2j.gameserver.model.actor.manager.character.skills.funcs.Lambda;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;

/**
 * @author mkizub
 */
public final class EffectTemplate
{
	private Constructor<?> constructor = null;
	
	public final Condition attachCond;
	public final Condition applayCond;
	public final Lambda lambda;
	public final int counter;
	public final int period; // in seconds
	public final AbnormalEffectType abnormalEffect;
	public List<FuncTemplate> funcTemplates = new ArrayList<>();
	
	public final String stackType;
	public final float stackOrder;
	
	public final boolean icon;
	
	public double power;
	public double rate;
	
	private String name;
	
	public EffectTemplate(Condition attachCond, Condition applayCond, String func, Lambda lambda, int counter, int period, AbnormalEffectType abnormalEffect, String stackType, float stackOrder, boolean showIcon, double power, double rate)
	{
		this.attachCond = attachCond;
		this.applayCond = applayCond;
		this.lambda = lambda;
		this.counter = counter;
		this.period = period;
		this.abnormalEffect = abnormalEffect;
		this.stackType = stackType;
		this.stackOrder = stackOrder;
		icon = showIcon;
		
		this.power = power;
		this.rate = rate;
		this.name = func;
		try
		{
			constructor = Class.forName("l2j.gameserver.model.skills.effects.type.Effect" + func).getConstructor(Env.class, EffectTemplate.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Effect getEffect(Env env)
	{
		if ((attachCond != null) && !attachCond.test(env))
		{
			return null;
		}
		try
		{
			return (Effect) constructor.newInstance(env, this);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void attach(FuncTemplate f)
	{
		funcTemplates.add(f);
	}
	
	public String getName()
	{
		return name;
	}
}
