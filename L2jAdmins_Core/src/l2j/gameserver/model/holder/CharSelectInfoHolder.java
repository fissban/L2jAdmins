package l2j.gameserver.model.holder;

import l2j.gameserver.model.actor.base.Sex;
import l2j.gameserver.model.actor.manager.character.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.items.enums.ParpedollType;

/**
 * Used to Store data sent to Client for Character Selection screen.
 * @version $Revision: 1.2.2.2.2.4 $ $Date: 2005/03/27 15:29:33 $
 */
public class CharSelectInfoHolder
{
	private String name;
	private int objectId = 0;
	private int charId = 0x00030b7a;
	private int exp = 0;
	private int sp = 0;
	private int clanId = 0;
	private int race = 0;
	private int classId = 0;
	private int baseClassId = 0;
	private long deleteTimer = 0L;
	private long lastAccess = 0L;
	private int face = 0;
	private int hairStyle = 0;
	private int hairColor = 0;
	private Sex sex = Sex.FEMALE;
	private int level = 1;
	private int maxHp = 0;
	private double currentHp = 0;
	private int maxMp = 0;
	private double currentMp = 0;
	private int[][] paperdoll;
	private int karma = 0;
	private int accessLevel = 0;
	private LocationHolder loc = new LocationHolder(0, 0, 0);
	
	/**
	 * @param objectId
	 * @param name
	 */
	public CharSelectInfoHolder(int objectId, String name)
	{
		setObjectId(objectId);
		this.name = name;
		paperdoll = PcInventory.restoreVisibleInventory(objectId);
	}
	
	public int getObjectId()
	{
		return objectId;
	}
	
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}
	
	public int getCharId()
	{
		return charId;
	}
	
	public void setCharId(int charId)
	{
		this.charId = charId;
	}
	
	public int getClanId()
	{
		return clanId;
	}
	
	public void setClanId(int clanId)
	{
		this.clanId = clanId;
	}
	
	public int getClassId()
	{
		return classId;
	}
	
	public int getBaseClassId()
	{
		return baseClassId;
	}
	
	public void setClassId(int classId)
	{
		this.classId = classId;
	}
	
	public void setBaseClassId(int baseClassId)
	{
		this.baseClassId = baseClassId;
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
	
	public long getDeleteTimer()
	{
		return deleteTimer;
	}
	
	public void setDeleteTimer(long deleteTimer)
	{
		this.deleteTimer = deleteTimer;
	}
	
	public long getLastAccess()
	{
		return lastAccess;
	}
	
	public void setLastAccess(long lastAccess)
	{
		this.lastAccess = lastAccess;
	}
	
	public int getExp()
	{
		return exp;
	}
	
	public void setExp(int exp)
	{
		this.exp = exp;
	}
	
	public int getFace()
	{
		return face;
	}
	
	public void setFace(int face)
	{
		this.face = face;
	}
	
	public int getHairColor()
	{
		return hairColor;
	}
	
	public void setHairColor(int hairColor)
	{
		this.hairColor = hairColor;
	}
	
	public int getHairStyle()
	{
		return hairStyle;
	}
	
	public void setHairStyle(int hairStyle)
	{
		this.hairStyle = hairStyle;
	}
	
	public int getPaperdollObjectId(ParpedollType slot)
	{
		return paperdoll[slot.ordinal()][0];
	}
	
	public int getPaperdollItemId(ParpedollType slot)
	{
		return paperdoll[slot.ordinal()][1];
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public int getMaxHp()
	{
		return maxHp;
	}
	
	public void setMaxHp(int maxHp)
	{
		this.maxHp = maxHp;
	}
	
	public int getMaxMp()
	{
		return maxMp;
	}
	
	public void setMaxMp(int maxMp)
	{
		this.maxMp = maxMp;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getRace()
	{
		return race;
	}
	
	public void setRace(int race)
	{
		this.race = race;
	}
	
	public Sex getSex()
	{
		return sex;
	}
	
	public void setSex(Sex sex)
	{
		this.sex = sex;
	}
	
	public int getSp()
	{
		return sp;
	}
	
	public void setSp(int sp)
	{
		this.sp = sp;
	}
	
	public int getEnchantEffect()
	{
		if (paperdoll[ParpedollType.RHAND.ordinal()][2] > 0)
		{
			return paperdoll[ParpedollType.RHAND.ordinal()][2];
		}
		return paperdoll[ParpedollType.LRHAND.ordinal()][2];
	}
	
	public void setKarma(int k)
	{
		karma = k;
	}
	
	public int getKarma()
	{
		return karma;
	}
	
	public void setLoc(int x, int y, int z)
	{
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);
	}
	
	public LocationHolder getLoc()
	{
		return loc;
	}
	
	public void setAccessLevel(int accesLevel)
	{
		accessLevel = accesLevel;
	}
	
	public int getAccessLevel()
	{
		return accessLevel;
	}
}
