package l2j.gameserver.model.actor.stat;

import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.SystemMessage;

public class PetStat extends SummonStat
{
	public PetStat(L2PetInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean addExp(long value)
	{
		if (!super.addExp(value))
		{
			return false;
		}
		
		getActiveChar().updateAndBroadcastStatus(1);
		return true;
	}
	
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		if (!super.addExpAndSp(addToExp, addToSp))
		{
			return false;
		}
		
		getActiveChar().updateAndBroadcastStatus(1);
		getActiveChar().getOwner().sendPacket(new SystemMessage(SystemMessage.PET_EARNED_S1_EXP).addNumber((int) addToExp));
		
		return true;
	}
	
	@Override
	public final boolean addLevel(int value)
	{
		if ((getLevel() + value) > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			return false;
		}
		
		boolean levelIncreased = super.addLevel(value);
		
		// Sync up exp with current level
		if ((getExp() > getExpForLevel(getLevel() + 1)) || (getExp() < getExpForLevel(getLevel())))
		{
			setExp(ExperienceData.getInstance().getExpForLevel(getLevel()));
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdateType.LEVEL, getLevel());
		su.addAttribute(StatusUpdateType.MAX_HP, getMaxHp());
		su.addAttribute(StatusUpdateType.MAX_MP, getMaxMp());
		getActiveChar().broadcastPacket(su);
		
		if (levelIncreased)
		{
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), SocialActionType.LEVEL_UP));
		}
		
		getActiveChar().updateAndBroadcastStatus(1);
		
		if (getActiveChar().getControlItem() != null)
		{
			getActiveChar().getControlItem().setEnchantLevel(getLevel());
		}
		
		return levelIncreased;
	}
	
	@Override
	public final long getExpForLevel(int level)
	{
		return PetDataData.getInstance().getPetData(getActiveChar().getId(), level).getPetMaxExp();
	}
	
	@Override
	public L2PetInstance getActiveChar()
	{
		return (L2PetInstance) super.getActiveChar();
	}
	
	public final int getFeedBattle()
	{
		return getActiveChar().getPetData().getPetFedBattle();
	}
	
	public final int getFeedNormal()
	{
		return getActiveChar().getPetData().getPetFedNormal();
	}
	
	@Override
	public void setLevel(int value)
	{
		getActiveChar().stopFeed();
		super.setLevel(value);
		
		getActiveChar().setPetData(PetDataData.getInstance().getPetData(getActiveChar().getTemplate().getId(), getLevel()));
		getActiveChar().startFeed();
		
		if (getActiveChar().getControlItem() != null)
		{
			getActiveChar().getControlItem().setEnchantLevel(getLevel());
		}
	}
	
	public final int getMaxFeed()
	{
		return getActiveChar().getPetData().getPetMaxFed();
	}
	
	@Override
	public int getMaxHp()
	{
		return (int) calcStat(StatsType.MAX_HP, getActiveChar().getPetData().getPetMaxHP(), null, null);
	}
	
	@Override
	public int getMaxMp()
	{
		return (int) calcStat(StatsType.MAX_MP, getActiveChar().getPetData().getPetMaxMP(), null, null);
	}
	
	@Override
	public int getMAtk(L2Character target, Skill skill)
	{
		double attack = getActiveChar().getPetData().getPetMAtk();
		
		if (skill != null)
		{
			attack += skill.getPower();
		}
		
		return (int) calcStat(StatsType.MAGICAL_ATTACK, attack, target, skill);
	}
	
	@Override
	public int getMDef(L2Character target, Skill skill)
	{
		return (int) calcStat(StatsType.MAGICAL_DEFENCE, getActiveChar().getPetData().getPetMDef(), target, skill);
	}
	
	@Override
	public int getPAtk(L2Character target)
	{
		return (int) calcStat(StatsType.PHYSICAL_ATTACK, getActiveChar().getPetData().getPetPAtk(), target, null);
	}
	
	@Override
	public int getPDef(L2Character target)
	{
		return (int) calcStat(StatsType.PHYSICAL_DEFENCE, getActiveChar().getPetData().getPetPDef(), target, null);
	}
	
	@Override
	public int getAccuracy()
	{
		return (int) calcStat(StatsType.ACCURACY_COMBAT, getActiveChar().getPetData().getPetAccuracy(), null, null);
	}
	
	@Override
	public int getCriticalHit(L2Character target, Skill skill)
	{
		return (int) calcStat(StatsType.PHYSICAL_CRITICAL_RATE, getActiveChar().getPetData().getPetCritical(), target, null);
	}
	
	@Override
	public int getEvasionRate(L2Character target)
	{
		return (int) calcStat(StatsType.EVASION_RATE, getActiveChar().getPetData().getPetEvasion(), target, null);
	}
	
	@Override
	public int getRunSpeed()
	{
		return (int) calcStat(StatsType.RUN_SPEED, getActiveChar().getPetData().getPetSpeed(), null, null);
	}
	
	@Override
	public int getWalkSpeed()
	{
		return getRunSpeed() / 2;
	}
	
	@Override
	public float getMovementSpeedMultiplier()
	{
		if (getActiveChar() == null)
		{
			return 1;
		}
		
		float val = (getRunSpeed() * 1f) / getActiveChar().getPetData().getPetSpeed();
		if (!getActiveChar().isRunning())
		{
			val = val / 2;
		}
		return val;
	}
	
	@Override
	public int getPAtkSpd()
	{
		int val = (int) calcStat(StatsType.PHYSICAL_ATTACK_SPEED, getActiveChar().getPetData().getPetAtkSpeed(), null, null);
		if (!getActiveChar().isRunning())
		{
			val = val / 2;
		}
		return val;
	}
	
	@Override
	public int getMAtkSpd()
	{
		int val = (int) calcStat(StatsType.MAGICAL_ATTACK_SPEED, getActiveChar().getPetData().getPetCastSpeed(), null, null);
		if (!getActiveChar().isRunning())
		{
			val = val / 2;
		}
		return val;
	}
}
