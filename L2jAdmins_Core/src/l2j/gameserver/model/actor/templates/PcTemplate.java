package l2j.gameserver.model.actor.templates;

import l2j.gameserver.model.StatsSet;
import l2j.gameserver.model.actor.base.ClassId;
import l2j.gameserver.model.actor.base.Race;
import l2j.gameserver.model.holder.LocationHolder;

/**
 * @author mkizub
 */
public class PcTemplate extends CharTemplate
{
	/** The Class object of the L2PcInstance */
	private final ClassId classId;
	
	private final Race race;
	
	private final LocationHolder spawnLoc;
	
	private final int classBaseLevel;
	private final float lvlHpAdd;
	private final float lvlHpMod;
	private final float lvlCpAdd;
	private final float lvlCpMod;
	private final float lvlMpAdd;
	private final float lvlMpMod;
	
	public double collisionHeightFemale;
	public double collisionRadiusFemale;
	
	public double collisionHeightMale;
	public double collisionRadiusMale;
	
	public PcTemplate(StatsSet set)
	{
		super(set);
		
		classId = set.getEnum("name", ClassId.class, ClassId.HUMAN_FIGHTER);
		race = set.getEnum("race", Race.class, Race.HUMAN);
		
		spawnLoc = new LocationHolder(set.getInteger("spawnX"), set.getInteger("spawnY"), set.getInteger("spawnZ"));
		
		classBaseLevel = set.getInteger("classLvl", 0);
		lvlHpAdd = set.getFloat("hpAdd", 0);
		lvlHpMod = set.getFloat("hpMod", 0);
		lvlCpAdd = set.getFloat("cpAdd", 0);
		lvlCpMod = set.getFloat("cpMod", 0);
		lvlMpAdd = set.getFloat("mpAdd", 0);
		lvlMpMod = set.getFloat("mpMod", 0);
		
		collisionRadiusFemale = set.getDouble("collisionRadiusF", 0);
		collisionHeightFemale = set.getDouble("collisionHightF", 0);
		
		collisionRadiusMale = set.getDouble("collisionRadiusM", 0);
		collisionHeightMale = set.getDouble("collisionHightM", 0);
	}
	
	public final int getFallHeight()
	{
		return 333;
	}
	
	public ClassId getClassId()
	{
		return classId;
	}
	
	public Race getRace()
	{
		return race;
	}
	
	public LocationHolder getSpawnLoc()
	{
		return spawnLoc;
	}
	
	public int getClassBaseLevel()
	{
		return classBaseLevel;
	}
	
	public float getLvlHpAdd()
	{
		return lvlHpAdd;
	}
	
	public float getLvlHpMod()
	{
		return lvlHpMod;
	}
	
	public float getLvlCpAdd()
	{
		return lvlCpAdd;
	}
	
	public float getLvlCpMod()
	{
		return lvlCpMod;
	}
	
	public float getLvlMpAdd()
	{
		return lvlMpAdd;
	}
	
	public float getLvlMpMod()
	{
		return lvlMpMod;
	}
	
	public double getCollisionHeightFemale()
	{
		return collisionHeightFemale;
	}
	
	public double getCollisionRadiusFemale()
	{
		return collisionRadiusFemale;
	}
	
	public double getCollisionHeightMale()
	{
		return collisionHeightMale;
	}
	
	public double getCollisionRadiusMale()
	{
		return collisionRadiusMale;
	}
}
