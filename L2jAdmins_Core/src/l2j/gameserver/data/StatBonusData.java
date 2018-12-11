package l2j.gameserver.data;

/**
 * @author fissban
 */
public class StatBonusData
{
	public static final int MAX_STAT_VALUE = 100;
	
	private static final double[] STR_COMPUTE = new double[]
	{
		1.036,
		34.845
	};
	private static final double[] INT_COMPUTE = new double[]
	{
		1.020,
		31.375
	};
	private static final double[] DEX_COMPUTE = new double[]
	{
		1.009,
		19.360
	};
	private static final double[] WIT_COMPUTE = new double[]
	{
		1.050,
		20.000
	};
	private static final double[] CON_COMPUTE = new double[]
	{
		1.030,
		27.632
	};
	private static final double[] MEN_COMPUTE = new double[]
	{
		1.010,
		-0.060
	};
	
	public static final double[] WIT_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] MEN_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] INT_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] STR_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] DEX_BONUS = new double[MAX_STAT_VALUE];
	public static final double[] CON_BONUS = new double[MAX_STAT_VALUE];
	
	public static final double[] BASE_EVASION_ACCURACY = new double[MAX_STAT_VALUE];
	
	protected static final double[] SQRT_MEN_BONUS = new double[MAX_STAT_VALUE];
	protected static final double[] SQRT_CON_BONUS = new double[MAX_STAT_VALUE];
	
	static
	{
		for (int i = 0; i < STR_BONUS.length; i++)
		{
			STR_BONUS[i] = Math.floor((Math.pow(STR_COMPUTE[0], i - STR_COMPUTE[1]) * 100) + .5d) / 100;
		}
		for (int i = 0; i < INT_BONUS.length; i++)
		{
			INT_BONUS[i] = Math.floor((Math.pow(INT_COMPUTE[0], i - INT_COMPUTE[1]) * 100) + .5d) / 100;
		}
		for (int i = 0; i < DEX_BONUS.length; i++)
		{
			DEX_BONUS[i] = Math.floor((Math.pow(DEX_COMPUTE[0], i - DEX_COMPUTE[1]) * 100) + .5d) / 100;
		}
		for (int i = 0; i < WIT_BONUS.length; i++)
		{
			WIT_BONUS[i] = Math.floor((Math.pow(WIT_COMPUTE[0], i - WIT_COMPUTE[1]) * 100) + .5d) / 100;
		}
		for (int i = 0; i < CON_BONUS.length; i++)
		{
			CON_BONUS[i] = Math.floor((Math.pow(CON_COMPUTE[0], i - CON_COMPUTE[1]) * 100) + .5d) / 100;
		}
		for (int i = 0; i < MEN_BONUS.length; i++)
		{
			MEN_BONUS[i] = Math.floor((Math.pow(MEN_COMPUTE[0], i - MEN_COMPUTE[1]) * 100) + .5d) / 100;
		}
		
		for (int i = 0; i < BASE_EVASION_ACCURACY.length; i++)
		{
			BASE_EVASION_ACCURACY[i] = Math.sqrt(i) * 6;
		}
		
		// Precompute square root values
		for (int i = 0; i < SQRT_CON_BONUS.length; i++)
		{
			SQRT_CON_BONUS[i] = Math.sqrt(CON_BONUS[i]);
		}
		for (int i = 0; i < SQRT_MEN_BONUS.length; i++)
		{
			SQRT_MEN_BONUS[i] = Math.sqrt(MEN_BONUS[i]);
		}
	}
}
