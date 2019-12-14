package l2j.gameserver.model.actor.manager.character.stat;

import l2j.gameserver.model.actor.L2Summon;

public class SummonStat extends PlayableStat
{
	public SummonStat(L2Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2Summon getActiveChar()
	{
		return (L2Summon) super.getActiveChar();
	}
}
