package l2j.gameserver.model.actor.manager.character.stat;

import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.PetDataData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.stats.enums.StatsType;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.PledgeShowMemberListUpdate;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.scripts.ScriptState;

public class PcStat extends PlayableStat
{
	private int oldMaxCp; // stats watch
	private int oldMaxHp; // stats watch
	private int oldMaxMp; // stats watch
	
	public PcStat(L2PcInstance activeChar)
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
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdateType.EXP, (int) getExp());
		getActiveChar().sendPacket(su);
		
		// Set new karma
		if ((getActiveChar().getKarma() > 0) && (getActiveChar().isGM() || !getActiveChar().isInsideZone(ZoneType.PVP)))
		{
			int karmaLost = getActiveChar().calculateKarmaLost(value);
			if (karmaLost > 0)
			{
				getActiveChar().setKarma(getActiveChar().getKarma() - karmaLost);
			}
		}
		
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the L2PcInstance, remove its Karma (if necessary) and Launch increase level task.<BR>
	 * <B><U> Actions </U> :</B><BR>
	 * <li>Remove Karma when the player kills L2MonsterInstance</li>
	 * <li>Send a Server->Client packet StatusUpdate to the L2PcInstance</li>
	 * <li>Send a Server->Client System Message to the L2PcInstance</li>
	 * <li>If the L2PcInstance increases it's level, send a Server->Client packet SocialAction (broadcast)</li>
	 * <li>If the L2PcInstance increases it's level, manage the increase level task (Max MP, Max MP, Recommandation, Expertise and beginner skills...)</li>
	 * <li>If the L2PcInstance increases it's level, send a Server->Client packet UserInfo to the L2PcInstance</li>
	 * @param addToExp The Experience value to add
	 * @param addToSp  The SP value to add
	 */
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		if (!super.addExpAndSp(addToExp, addToSp))
		{
			return false;
		}
		
		// Send a Server->Client System Message to the L2PcInstance
		getActiveChar().sendPacket(new SystemMessage(SystemMessage.YOU_EARNED_S1_EXP_AND_S2_SP).addNumber((int) addToExp).addNumber(addToSp));
		
		return true;
	}
	
	@Override
	public boolean removeExpAndSp(long removeExp, int removeSp)
	{
		return removeExpAndSp(removeExp, removeSp, true);
	}
	
	public boolean removeExpAndSp(long removeExp, int removeSp, boolean sendMessage)
	{
		if (!super.removeExpAndSp(removeExp, removeSp))
		{
			return false;
		}
		
		if (sendMessage)
		{
			// Send a Server->Client System Message to the L2PcInstance
			getActiveChar().sendPacket(new SystemMessage(SystemMessage.EXP_DECREASED_BY_S1).addNumber((int) removeExp));
			// Send a Server->Client System Message to the L2PcInstance
			getActiveChar().sendPacket(new SystemMessage(SystemMessage.SP_DECREASED_S1).addNumber(removeSp));
		}
		
		// Calculate the current higher Expertise of the L2PcInstance
		getActiveChar().calculateExpertiseLevel();
		getActiveChar().calculateCommonCraftLevel();
		
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
		if (levelIncreased)
		{
			ScriptState qs = getActiveChar().getScriptState("Q255_Tutorial");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("CE40", null, getActiveChar());
			}
			
			getActiveChar().setCurrentCp(getMaxCp());
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), SocialActionType.LEVEL_UP));
			getActiveChar().sendPacket(SystemMessage.YOU_INCREASED_YOUR_LEVEL);
		}
		
		getActiveChar().rewardSkills(); // Give Expertise skill of this level
		
		if (getActiveChar().getClan() != null)
		{
			getActiveChar().getClan().addClanMember(getActiveChar());
			getActiveChar().getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()), getActiveChar());
		}
		
		if (getActiveChar().isInParty())
		{
			getActiveChar().getParty().recalculatePartyLevel(); // Recalculate the party level
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdateType.LEVEL, getLevel());
		su.addAttribute(StatusUpdateType.MAX_CP, getMaxCp());
		su.addAttribute(StatusUpdateType.MAX_HP, getMaxHp());
		su.addAttribute(StatusUpdateType.MAX_MP, getMaxMp());
		getActiveChar().sendPacket(su);
		
		// Update the overloaded status of the L2PcInstance
		getActiveChar().refreshOverloaded();
		// Update the expertise status of the L2PcInstance
		getActiveChar().refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to the L2PcInstance
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));
		
		return levelIncreased;
	}
	
	@Override
	public boolean addSp(int value)
	{
		if (!super.addSp(value))
		{
			return false;
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdateType.SP, getSp());
		getActiveChar().sendPacket(su);
		
		return true;
	}
	
	@Override
	public final long getExpForLevel(int level)
	{
		return ExperienceData.getInstance().getExpForLevel(level);
	}
	
	@Override
	public final L2PcInstance getActiveChar()
	{
		return (L2PcInstance) super.getActiveChar();
	}
	
	@Override
	public final long getExp()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();
		}
		
		return super.getExp();
	}
	
	@Override
	public final void setExp(long value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
		}
		else
		{
			super.setExp(value);
		}
	}
	
	@Override
	public final int getLevel()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();
		}
		
		return super.getLevel();
	}
	
	@Override
	public final void setLevel(int value)
	{
		if (value > (ExperienceData.getInstance().getMaxLevel() - 1))
		{
			value = (ExperienceData.getInstance().getMaxLevel() - 1);
		}
		
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
		}
		else
		{
			super.setLevel(value);
		}
	}
	
	@Override
	public final int getMaxCp()
	{
		// Get the Max CP (base+modifier) of the L2PcInstance
		int val = super.getMaxCp();
		if (val != oldMaxCp)
		{
			oldMaxCp = val;
			
			// Launch a regen task if the new Max HP is higher than the old one
			if (getActiveChar().getStatus().getCurrentCp() != val)
			{
				getActiveChar().getStatus().setCurrentCp(getActiveChar().getStatus().getCurrentCp()); // trigger start of regeneration
			}
		}
		
		return val;
	}
	
	@Override
	public final int getMaxHp()
	{
		// Get the Max HP (base+modifier) of the L2PcInstance
		int val = super.getMaxHp();
		if (val != oldMaxHp)
		{
			oldMaxHp = val;
			
			// Launch a regen task if the new Max HP is higher than the old one
			if (getActiveChar().getStatus().getCurrentHp() != val)
			{
				getActiveChar().getStatus().setCurrentHp(getActiveChar().getStatus().getCurrentHp()); // trigger start of regeneration
			}
		}
		
		return val;
	}
	
	@Override
	public final int getMaxMp()
	{
		// Get the Max MP (base+modifier) of the L2PcInstance
		int val = super.getMaxMp();
		
		if (val != oldMaxMp)
		{
			oldMaxMp = val;
			
			// Launch a regen task if the new Max MP is higher than the old one
			if (getActiveChar().getStatus().getCurrentMp() != val)
			{
				getActiveChar().getStatus().setCurrentMp(getActiveChar().getStatus().getCurrentMp()); // trigger start of regeneration
			}
		}
		
		return val;
	}
	
	@Override
	public final int getSp()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();
		}
		
		return super.getSp();
	}
	
	@Override
	public final void setSp(int value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
		}
		else
		{
			super.setSp(value);
		}
	}
	
	@Override
	public int getRunSpeed()
	{
		if (getActiveChar() == null)
		{
			return 1;
		}
		
		int val = super.getRunSpeed();
		
		L2PcInstance player = getActiveChar();
		if (player.isMounted())
		{
			int baseRunSpd = PetDataData.getInstance().getPetData(player.getMountNpcId(), player.getMountLevel()).getPetSpeed();
			val = (int) Math.round(calcStat(StatsType.RUN_SPEED, baseRunSpd, null, null));
		}
		
		return val;
	}
	
	@Override
	public float getMovementSpeedMultiplier()
	{
		if (getActiveChar().isMounted())
		{
			return getRunSpeed() / PetDataData.getInstance().getPetData(getActiveChar().getMountNpcId(), getActiveChar().getMountLevel()).getPetSpeed();
		}
		
		return super.getMovementSpeedMultiplier();
	}
	
	@Override
	public int getWalkSpeed()
	{
		return (getRunSpeed() * 70) / 100;
	}
}
