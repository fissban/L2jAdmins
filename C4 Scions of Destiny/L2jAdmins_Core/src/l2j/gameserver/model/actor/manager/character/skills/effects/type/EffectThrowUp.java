package l2j.gameserver.model.actor.manager.character.skills.effects.type;

import l2j.Config;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.model.actor.manager.character.skills.effects.Effect;
import l2j.gameserver.model.actor.manager.character.skills.effects.EffectTemplate;
import l2j.gameserver.model.actor.manager.character.skills.effects.enums.EffectType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Env;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.network.external.server.FlyToLocation;
import l2j.gameserver.network.external.server.FlyToLocation.FlyType;
import l2j.gameserver.network.external.server.ValidateLocation;

/**
 * @author fissban
 */
public class EffectThrowUp extends Effect
{
	private int x, y, z;
	
	public EffectThrowUp(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.THROW_UP;
	}
	
	@Override
	public void onStart()
	{
		// Get current position of the L2Character
		final int curX = getEffected().getX();
		final int curY = getEffected().getY();
		final int curZ = getEffected().getZ();
		
		// Get the difference between effector and effected positions
		double dx = getEffector().getX() - curX;
		double dy = getEffector().getY() - curY;
		double dz = getEffector().getZ() - curZ;
		
		// Calculate distance between effector and effected current position
		double distance = Math.sqrt((dx * dx) + (dy * dy));
		if ((distance < 1) || (distance > 2000))
		{
			return;
		}
		
		int offset = Math.min((int) distance + 600, 1400); // 600 hardcode...radius fly
		double cos, sin;
		
		// approximation for moving futher when z coordinates are different
		// TODO: handle Z axis movement better
		offset += Math.abs(dz);
		if (offset < 5)
		{
			offset = 5;
		}
		
		// Calculate movement angles needed
		sin = dy / distance;
		cos = dx / distance;
		
		// Calculate the new destination with offset included
		x = getEffector().getX() - (int) (offset * cos);
		y = getEffector().getY() - (int) (offset * sin);
		z = getEffected().getZ();
		
		if (Config.PATHFINDING)
		{
			LocationHolder destiny = GeoEngine.getInstance().canMoveToTargetLoc(getEffected().getX(), getEffected().getY(), getEffected().getZ(), x, y, z);
			x = destiny.getX();
			y = destiny.getY();
		}
		
		getEffected().startStunning();
		getEffected().broadcastPacket(new FlyToLocation(getEffected(), x, y, z, FlyType.THROW_UP));
		return;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopStunning(false);
		getEffected().setXYZ(x, y, z);
		getEffected().broadcastPacket(new ValidateLocation(getEffected()));
	}
}
