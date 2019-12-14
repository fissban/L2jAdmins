package l2j.gameserver.model.actor.manager.character.stat;

import l2j.gameserver.model.actor.instance.L2BoatInstance;

public class BoatStat extends NpcStat
{
	private float moveSpeed = 0;
	private int rotationSpeed = 0;
	
	public BoatStat(L2BoatInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2BoatInstance getActiveChar()
	{
		return (L2BoatInstance) super.getActiveChar();
	}
	
	@Override
	public final float getMoveSpeed()
	{
		return moveSpeed;
	}
	
	public final void setMoveSpeed(float speed)
	{
		moveSpeed = speed;
	}
	
	public final int getRotationSpeed()
	{
		return rotationSpeed;
	}
	
	public final void setRotationSpeed(int speed)
	{
		rotationSpeed = speed;
	}
}
