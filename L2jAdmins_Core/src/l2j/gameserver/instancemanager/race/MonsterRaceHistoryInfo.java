package l2j.gameserver.instancemanager.race;

public class MonsterRaceHistoryInfo
{
	private final int raceId;
	private int first;
	private int second;
	private double oddRate;
	
	public MonsterRaceHistoryInfo(int raceId, int first, int second, double oddRate)
	{
		this.raceId = raceId;
		this.first = first;
		this.second = second;
		this.oddRate = oddRate;
	}
	
	public int getRaceId()
	{
		return raceId;
	}
	
	public int getFirst()
	{
		return first;
	}
	
	public int getSecond()
	{
		return second;
	}
	
	public double getOddRate()
	{
		return oddRate;
	}
	
	public void setFirst(int first)
	{
		this.first = first;
	}
	
	public void setSecond(int second)
	{
		this.second = second;
	}
	
	public void setOddRate(double oddRate)
	{
		this.oddRate = oddRate;
	}
}
