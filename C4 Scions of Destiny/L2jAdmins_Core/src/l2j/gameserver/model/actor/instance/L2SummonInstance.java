package l2j.gameserver.model.actor.instance;

import java.util.concurrent.Future;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.itemcontainer.inventory.PcInventory;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.SetSummonRemainTime;
import l2j.gameserver.network.external.server.SystemMessage;

public class L2SummonInstance extends L2Summon
{
	protected static final Logger LOG = Logger.getLogger(L2SummonInstance.class.getName());
	
	private float expPenalty = 0; // exp decrease multiplier (i.e. 0.3 (= 30%) for shadow)
	private int itemConsumeId;
	private int itemConsumeCount;
	private int itemConsumeSteps;
	private int totalLifeTime;
	private int timeLostIdle;
	private int timeLostActive;
	private int timeRemaining;
	private int nextItemConsumeTime;
	public int lastShownTimeRemaining; // Following FbiAgent's example to avoid sending useless packets
	
	private long timeToSpawn;
	
	private Future<?> summonLifeTask;
	
	public L2SummonInstance(int objectId, NpcTemplate template, L2PcInstance owner, Skill skill)
	{
		super(objectId, template, owner);
		
		setInstanceType(InstanceType.L2SummonInstance);
		if (skill != null)
		{
			itemConsumeId = skill.getItemConsumeIdOT();
			itemConsumeCount = skill.getItemConsumeOT();
			itemConsumeSteps = skill.getItemConsumeSteps();
			totalLifeTime = skill.getTotalLifeTime();
			timeLostIdle = skill.getTimeLostIdle();
			timeLostActive = skill.getTimeLostActive();
		}
		else
		{
			itemConsumeId = 0;
			itemConsumeCount = 0;
			itemConsumeSteps = 0;
			totalLifeTime = 1200000; // 20 minutes
			timeLostIdle = 1000;
			timeLostActive = 1000;
		}
		timeRemaining = totalLifeTime;
		lastShownTimeRemaining = totalLifeTime;
		
		if (itemConsumeId == 0)
		{
			nextItemConsumeTime = -1; // do not consume
		}
		else if (itemConsumeSteps == 0)
		{
			nextItemConsumeTime = -1; // do not consume
		}
		else
		{
			nextItemConsumeTime = totalLifeTime - (totalLifeTime / (itemConsumeSteps + 1));
		}
		
		timeToSpawn = System.currentTimeMillis() + 3000;
		
		summonLifeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SummonLifetime(getOwner(), this), 1000, 1000);
	}
	
	@Override
	public final int getLevel()
	{
		return (getTemplate() != null ? getTemplate().getLevel() : 0);
	}
	
	@Override
	public int getSummonType()
	{
		return 1;
	}
	
	public void setExpPenalty(float expPenalty)
	{
		this.expPenalty = expPenalty;
	}
	
	@Override
	public float getExpPenalty()
	{
		return expPenalty;
	}
	
	public int getItemConsumeCount()
	{
		return itemConsumeCount;
	}
	
	public int getItemConsumeId()
	{
		return itemConsumeId;
	}
	
	public int getItemConsumeSteps()
	{
		return itemConsumeSteps;
	}
	
	public int getNextItemConsumeTime()
	{
		return nextItemConsumeTime;
	}
	
	public int getTotalLifeTime()
	{
		return totalLifeTime;
	}
	
	public int getTimeLostIdle()
	{
		return timeLostIdle;
	}
	
	public int getTimeLostActive()
	{
		return timeLostActive;
	}
	
	public int getTimeRemaining()
	{
		return timeRemaining;
	}
	
	public void setNextItemConsumeTime(int value)
	{
		nextItemConsumeTime = value;
	}
	
	public void decNextItemConsumeTime(int value)
	{
		nextItemConsumeTime -= value;
	}
	
	public void decTimeRemaining(int value)
	{
		timeRemaining -= value;
	}
	
	@Override
	public void addExpAndSp(long addToExp, int addToSp)
	{
		getOwner().addExpAndSp(addToExp, addToSp);
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (summonLifeTask != null)
		{
			summonLifeTask.cancel(true);
			summonLifeTask = null;
		}
		
		return true;
	}
	
	private static class SummonLifetime implements Runnable
	{
		private final L2PcInstance activeChar;
		private final L2SummonInstance summon;
		
		SummonLifetime(L2PcInstance activeChar, L2SummonInstance summon)
		{
			this.activeChar = activeChar;
			this.summon = summon;
		}
		
		@Override
		public void run()
		{
			try
			{
				double oldTimeRemaining = summon.getTimeRemaining();
				int maxTime = summon.getTotalLifeTime();
				double newTimeRemaining;
				
				// if pet is attacking
				if (summon.isAttackingNow())
				{
					summon.decTimeRemaining(summon.getTimeLostActive());
				}
				else
				{
					summon.decTimeRemaining(summon.getTimeLostIdle());
				}
				
				newTimeRemaining = summon.getTimeRemaining();
				// check if the summon's lifetime has ran out
				if (newTimeRemaining < 0)
				{
					summon.unSummon();
				}
				else if ((newTimeRemaining <= summon.getNextItemConsumeTime()) && (oldTimeRemaining > summon.getNextItemConsumeTime()))
				{
					summon.decNextItemConsumeTime(maxTime / (summon.getItemConsumeSteps() + 1));
					
					// check if owner has enought itemConsume, if requested
					if ((summon.getItemConsumeCount() > 0) && (summon.getItemConsumeId() != 0) && !summon.isDead() && !summon.getOwner().getInventory().destroyItemByItemId("Consume", summon.getItemConsumeId(), summon.getItemConsumeCount(), activeChar, true))
					{
						summon.unSummon();
					}
				}
				
				// prevent useless packet-sending when the difference isn't visible.
				if ((summon.lastShownTimeRemaining - newTimeRemaining) > (maxTime / 352))
				{
					summon.getOwner().sendPacket(new SetSummonRemainTime(maxTime, (int) newTimeRemaining));
					summon.lastShownTimeRemaining = (int) newTimeRemaining;
				}
			}
			catch (Throwable e)
			{
				LOG.warning("Summon of player [#" + activeChar.getName() + "] has encountered item consumption errors: " + e);
			}
		}
	}
	
	@Override
	public void unSummon()
	{
		// it is verified if I finish the summon animation
		if (timeToSpawn > System.currentTimeMillis())
		{
			return;
		}
		if (summonLifeTask != null)
		{
			summonLifeTask.cancel(true);
			summonLifeTask = null;
		}
		
		super.unSummon();
	}
	
	@Override
	public PcInventory getInventory()
	{
		return getOwner().getInventory();
	}
	
	@Override
	public final void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss)
		{
			getOwner().sendPacket(SystemMessage.MISSED_TARGET);
			return;
		}
		
		// Prevents the double spawm of system messages, if the target is the owning player.
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
			{
				getOwner().sendPacket(SystemMessage.CRITICAL_HIT_BY_SUMMONED_MOB);
			}
			
			if (getOwner().isInOlympiadMode() && (target instanceof L2PcInstance) && ((L2PcInstance) target).isInOlympiadMode() && (((L2PcInstance) target).getOlympiadGameId() == getOwner().getOlympiadGameId()))
			{
				getOwner().dmgDealt += damage;
			}
			
			getOwner().sendPacket(new SystemMessage(SystemMessage.SUMMON_GAVE_DAMAGE_S1).addNumber(damage));
		}
	}
}
