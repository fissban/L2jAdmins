package l2j.gameserver.model.actor.instance;

import java.util.concurrent.Future;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.util.Rnd;

/**
 * This class ...
 * @version $Revision: 1.15.2.10.2.16 $ $Date: 2005/04/06 16:13:40 $
 */
public final class L2BabyPetInstance extends L2PetInstance
{
	protected Skill weakHeal;
	protected Skill strongHeal;
	private Future<?> healingTask;
	
	public L2BabyPetInstance(int objectId, NpcTemplate template, L2PcInstance owner, ItemInstance control)
	{
		super(objectId, template, owner, control);
		
		setInstanceType(InstanceType.L2BabyPetInstance);
		
		Skill skill1 = null;
		Skill skill2 = null;
		
		// look through the skills that this template has and find the weak and strong heal.
		for (Skill skill : getTemplate().getSkills().values())
		{
			// just in case, also allow cp heal and mp recharges to be considered here...you never know ;)
			if (skill.isActive() && (skill.getTargetType() == SkillTargetType.TARGET_OWNER_PET))
			{
				boolean hasConcidered = false;
				switch (skill.getSkillType())
				{
					case HEAL:
					case BALANCE_LIFE:
					case COMBATPOINTHEAL:
					case MANAHEAL:
					case MANA_BY_LEVEL:
					case MANARECHARGE:
						hasConcidered = true;
						break;
					case BUFF:
						switch (skill.getEffectType())
						{
							case HEAL_OVER_TIME:
							case MANA_HEAL_OVER_TIME:
								hasConcidered = true;
								break;
						}
						break;
				}
				// only consider two skills. If the pet has more, too bad...they won't be used by its AI.
				// for now assign the first two skills in the order they come. Once we have both skills, re-arrange them
				if (hasConcidered)
				{
					if (skill1 == null)
					{
						skill1 = skill;
					}
					else
					{
						skill2 = skill;
						break;
					}
				}
			}
		}
		
		// process the results. Only store the ID of the skills. The levels are generated on the fly, based on the pet's level!
		if (skill1 != null)
		{
			if (skill2 == null)
			{
				// duplicate so that the same skill will be used in both normal and emergency situations
				weakHeal = skill1;
				strongHeal = skill1;
			}
			else
			{
				// arrange the weak and strong skills appropriately
				if (skill1.getPower() > skill2.getPower())
				{
					weakHeal = skill2;
					strongHeal = skill1;
				}
				else
				{
					weakHeal = skill1;
					strongHeal = skill2;
				}
			}
			
			// start the healing task
			healingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HealTask(this), 0, 1000);
		}
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		if (healingTask != null)
		{
			healingTask.cancel(false);
			healingTask = null;
		}
		return true;
	}
	
	@Override
	public synchronized void unSummon()
	{
		super.unSummon();
		
		if (healingTask != null)
		{
			healingTask.cancel(false);
			healingTask = null;
		}
	}
	
	@Override
	public void doRevive()
	{
		super.doRevive();
		if (healingTask == null)
		{
			healingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HealTask(this), 0, 1000);
		}
	}
	
	private class HealTask implements Runnable
	{
		private final L2BabyPetInstance baby;
		
		public HealTask(L2BabyPetInstance baby)
		{
			this.baby = baby;
		}
		
		@Override
		public void run()
		{
			L2PcInstance owner = baby.getOwner();
			
			// if the owner is dead, merely wait for the owner to be resurrected
			// if the pet is still casting from the previous iteration, allow the cast to complete...
			if (!owner.isDead() && !baby.isCastingNow())
			{
				// casting automatically stops any other action (such as autofollow or a move-to).
				// We need to gather the necessary info to restore the previous state.
				boolean previousFollowStatus = baby.getFollowStatus();
				
				// if the owner's HP is more than 80%, do nothing.
				// if the owner's HP is very low (less than 20%) have a high chance for strong heal
				// otherwise, have a low chance for weak heal
				if (((owner.getCurrentHp() / owner.getStat().getMaxHp()) < 0.2) && (Rnd.get(4) < 3))
				{
					if (!baby.isSkillDisabled(strongHeal))
					{
						baby.useMagic(strongHeal, false, false);
					}
				}
				else if (((owner.getCurrentHp() / owner.getStat().getMaxHp()) < 0.8) && (Rnd.get(4) < 1))
				{
					if (!baby.isSkillDisabled(weakHeal))
					{
						baby.useMagic(weakHeal, false, false);
					}
				}
				
				// calling useMagic changes the follow status, if the babypet actually casts
				// (as opposed to failing due some factors, such as too low MP, etc).
				// if the status has actually been changed, revert it. Else, allow the pet to
				// continue whatever it was trying to do.
				// NOTE: This is important since the pet may have been told to attack a target.
				// reverting the follow status will abort this attack! While aborting the attack
				// in order to heal is natural, it is not acceptable to abort the attack on its own,
				// merely because the timer stroke and without taking any other action...
				if (previousFollowStatus != baby.getFollowStatus())
				{
					setFollowStatus(previousFollowStatus);
				}
			}
		}
	}
}
