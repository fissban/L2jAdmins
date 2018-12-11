package l2j.gameserver.model.skills.stats.enums;

import l2j.gameserver.data.StatBonusData;
import l2j.gameserver.model.actor.L2Character;

/**
 * @author  DS
 * @checked fissban
 */
public enum BaseStatsType
{
	STR(new STR()),
	INT(new INT()),
	DEX(new DEX()),
	WIT(new WIT()),
	CON(new CON()),
	MEN(new MEN()),
	NULL(new NULL());
	
	private final BaseStat stat;
	
	public final String getValue()
	{
		return stat.getClass().getSimpleName();
	}
	
	private BaseStatsType(BaseStat s)
	{
		stat = s;
	}
	
	public final double calcBonus(L2Character actor)
	{
		return stat.calcBonus(actor);
	}
	
	public final double calcBonus(int value)
	{
		return stat.calcBonus(value);
	}
	
	private interface BaseStat
	{
		public double calcBonus(L2Character actor);
		
		public double calcBonus(int value);
	}
	
	protected static final class STR implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return StatBonusData.STR_BONUS[actor.getStat().getSTR()];
		}
		
		@Override
		public double calcBonus(int value)
		{
			return StatBonusData.STR_BONUS[value];
		}
	}
	
	protected static final class INT implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return StatBonusData.INT_BONUS[actor.getStat().getINT()];
		}
		
		@Override
		public double calcBonus(int value)
		{
			return StatBonusData.INT_BONUS[value];
		}
	}
	
	protected static final class DEX implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return StatBonusData.DEX_BONUS[actor.getStat().getDEX()];
		}
		
		@Override
		public double calcBonus(int value)
		{
			return StatBonusData.DEX_BONUS[value];
		}
	}
	
	protected static final class WIT implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return StatBonusData.WIT_BONUS[actor.getStat().getWIT()];
		}
		
		@Override
		public double calcBonus(int value)
		{
			return StatBonusData.WIT_BONUS[value];
		}
	}
	
	protected static final class CON implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return StatBonusData.CON_BONUS[actor.getStat().getCON()];
		}
		
		@Override
		public double calcBonus(int value)
		{
			return StatBonusData.CON_BONUS[value];
		}
	}
	
	protected static final class MEN implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return StatBonusData.MEN_BONUS[actor.getStat().getMEN()];
		}
		
		@Override
		public double calcBonus(int value)
		{
			return StatBonusData.MEN_BONUS[value];
		}
	}
	
	protected static final class NULL implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return 1f;
		}
		
		@Override
		public double calcBonus(int value)
		{
			return 1f;
		}
	}
}
