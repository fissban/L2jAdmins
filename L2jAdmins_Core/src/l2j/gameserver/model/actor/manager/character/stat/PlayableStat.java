package l2j.gameserver.model.actor.manager.character.stat;

import l2j.Config;
import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.zone.enums.ZoneType;

public class PlayableStat extends CharStat
{
	public PlayableStat(L2Playable activeChar)
	{
		super(activeChar);
	}
	
	public boolean addExp(long value)
	{
		if (((getExp() + value) < 0) || ((value > 0) && (getExp() == (ExperienceData.getInstance().getMaxLevel() - 1))))
		{
			return true;
		}
		
		if ((getExp() + value) >= getExpForLevel(ExperienceData.getInstance().getMaxLevel()))
		{
			value = getExpForLevel(ExperienceData.getInstance().getMaxLevel()) - 1 - getExp();
		}
		
		setExp(getExp() + (int) value);
		
		byte level = 1;
		for (level = 1; level <= ExperienceData.getInstance().getMaxLevel(); level++)
		{
			if (getExp() >= getExpForLevel(level))
			{
				continue;
			}
			
			level--;
			break;
		}
		
		if (level != getLevel())
		{
			addLevel(level - getLevel());
		}
		
		return true;
	}
	
	public boolean removeExp(long value)
	{
		if ((getExp() - value) < 0)
		{
			value = getExp() - 1;
		}
		
		setExp(getExp() - value);
		
		byte level = 0;
		for (level = 1; level <= ExperienceData.getInstance().getMaxLevel(); level++)
		{
			if (getExp() >= getExpForLevel(level))
			{
				continue;
			}
			level--;
			break;
		}
		if (level != getLevel())
		{
			addLevel((byte) (level - getLevel()));
		}
		return true;
	}
	
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		boolean expAdded = false;
		boolean spAdded = false;
		if (addToExp >= 0)
		{
			expAdded = addExp(addToExp);
		}
		if (addToSp >= 0)
		{
			spAdded = addSp(addToSp);
		}
		
		return expAdded || spAdded;
	}
	
	public boolean removeExpAndSp(long removeExp, int removeSp)
	{
		boolean expRemoved = false;
		boolean spRemoved = false;
		if (removeExp > 0)
		{
			expRemoved = removeExp(removeExp);
		}
		if (removeSp > 0)
		{
			spRemoved = removeSp(removeSp);
		}
		
		return expRemoved || spRemoved;
	}
	
	public boolean addLevel(int i)
	{
		if ((getLevel() + i) > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			if (getLevel() < (ExperienceData.getInstance().getMaxLevel() - 1))
			{
				i = (ExperienceData.getInstance().getMaxLevel() - 1 - getLevel());
			}
			else
			{
				return false;
			}
		}
		
		boolean levelIncreased = ((getLevel() + i) > getLevel());
		i += getLevel();
		setLevel(i);
		
		// Sync up exp with current level
		if ((getExp() >= getExpForLevel(getLevel() + 1)) || (getExpForLevel(getLevel()) > getExp()))
		{
			setExp(getExpForLevel(getLevel()));
		}
		
		if (!levelIncreased)
		{
			return false;
		}
		
		getActiveChar().getStatus().setCurrentHp(getActiveChar().getStat().getMaxHp());
		getActiveChar().getStatus().setCurrentMp(getActiveChar().getStat().getMaxMp());
		
		return true;
	}
	
	public boolean addSp(int value)
	{
		if (value < 0)
		{
			return false;
		}
		
		int currentSp = getSp();
		if (currentSp == Integer.MAX_VALUE)
		{
			return false;
		}
		
		if (currentSp > (Integer.MAX_VALUE - value))
		{
			value = Integer.MAX_VALUE - currentSp;
		}
		
		setSp(currentSp + value);
		return true;
	}
	
	public boolean removeSp(int value)
	{
		int currentSp = getSp();
		if (currentSp < value)
		{
			value = currentSp;
		}
		setSp(getSp() - value);
		return true;
	}
	
	public long getExpForLevel(int level)
	{
		return level;
	}
	
	@Override
	public int getRunSpeed()
	{
		int val = super.getRunSpeed();
		if (getActiveChar().isInsideZone(ZoneType.WATER))
		{
			val /= 2;
		}
		
		if ((getActiveChar() instanceof L2PcInstance) && ((L2PcInstance) getActiveChar()).isGM())
		{
			return val;
		}
		
		if (val > Config.MAX_RUN_SPEED)
		{
			val = Config.MAX_RUN_SPEED;
		}
		
		return val;
	}
	
	@Override
	public L2Playable getActiveChar()
	{
		return (L2Playable) super.getActiveChar();
	}
}
