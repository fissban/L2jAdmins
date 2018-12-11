package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.instance.L2GrandBossInstance;

/**
 * @author fissban
 */
public class GrandBossHolder
{
	private int boosId;
	private LocationHolder loc;
	private long respawnTime;
	private double currentHp;
	private double currentMp;
	private int status;
	
	L2GrandBossInstance grandBossInstance = null;
	
	public GrandBossHolder()
	{
		//
	}
	
	public void setBoss(L2GrandBossInstance grandBossInstance)
	{
		this.grandBossInstance = grandBossInstance;
	}
	
	public L2GrandBossInstance getBoss()
	{
		return grandBossInstance;
	}
	
	public int getBoosId()
	{
		return boosId;
	}
	
	public void setBoosId(int boosId)
	{
		this.boosId = boosId;
	}
	
	public void setLoc(int x, int y, int z, int heading)
	{
		loc = new LocationHolder(x, y, z, heading);
	}
	
	public LocationHolder getLoc()
	{
		return loc;
	}
	
	public long getRespawnTime()
	{
		return respawnTime;
	}
	
	public void setRespawnTime(long respawnTime)
	{
		this.respawnTime = respawnTime;
	}
	
	public double getCurrentHp()
	{
		return currentHp;
	}
	
	public void setCurrentHp(double currentHp)
	{
		this.currentHp = currentHp;
	}
	
	public double getCurrentMp()
	{
		return currentMp;
	}
	
	public void setCurrentMp(double currentMp)
	{
		this.currentMp = currentMp;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
}
