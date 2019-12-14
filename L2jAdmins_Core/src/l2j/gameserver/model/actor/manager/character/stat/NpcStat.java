package l2j.gameserver.model.actor.manager.character.stat;

import l2j.gameserver.model.actor.L2Npc;

public class NpcStat extends CharStat
{
	public NpcStat(L2Npc activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2Npc getActiveChar()
	{
		return (L2Npc) super.getActiveChar();
	}
	
	@Override
	public int getLevel()
	{
		return getActiveChar().getTemplate().getLevel();
	}
	
	@Override
	public float getMovementSpeedMultiplier()
	{
		if (getActiveChar().isRunning())
		{
			return (getRunSpeed() * 1f) / getActiveChar().getTemplate().getBaseRunSpd();
		}
		return (getWalkSpeed() * 1f) / getActiveChar().getTemplate().getBaseWalkSpd();
	}
}
