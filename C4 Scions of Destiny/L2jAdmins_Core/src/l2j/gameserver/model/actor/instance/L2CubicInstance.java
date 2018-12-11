package l2j.gameserver.model.actor.instance;

import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.CubicData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.SkillHandler;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.instance.L2PcInstance.FlagType;
import l2j.gameserver.model.actor.instance.enums.CubicType;
import l2j.gameserver.model.holder.CubicHolder;
import l2j.gameserver.model.party.Party;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.external.server.MagicSkillUse;
import l2j.gameserver.task.continuous.AttackStanceTaskManager;
import l2j.util.Rnd;

public class L2CubicInstance
{
	protected static final Logger LOG = Logger.getLogger(L2CubicInstance.class.getName());
	
	/** owner for this cubic */
	protected L2PcInstance owner;
	/** cubic info */
	protected CubicHolder cubic;
	/** cubic lvl and skill lvl to use skills */
	protected int level = 1;
	/** true if this cubic given for other player */
	private final boolean givenByOther;
	/** task for disappear cubic */
	private Future<?> disappearTask;
	/** task for general action (heal or attack) */
	private Future<?> actionTask;
	
	public L2CubicInstance(L2PcInstance owner, CubicType type, int level, boolean givenByOther)
	{
		this.owner = owner;
		cubic = CubicData.getInstance().getCubicByType(type);
		
		this.level = level;
		this.givenByOther = givenByOther;
		
		if (type == CubicType.LIFE_CUBIC)
		{
			doAction();
		}
		
		disappearTask = ThreadPoolManager.getInstance().schedule(() ->
		{
			stopAction();
			owner.delCubic(cubic.getType());
			owner.broadcastUserInfo();
		}, cubic.getDisappearTime() * 60000);
	}
	
	public synchronized void doAction()
	{
		if (actionTask == null)
		{
			if (cubic.getType() == CubicType.LIFE_CUBIC)
			{
				actionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> healTask(), 0, cubic.getDelayAction() * 1000);
			}
			else
			{
				actionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> actionTask(), 0, cubic.getDelayAction() * 1000);
			}
		}
	}
	
	public CubicType getType()
	{
		return cubic.getType();
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public boolean givenByOther()
	{
		return givenByOther;
	}
	
	private void actionTask()
	{
		if (!checkOwnerState())
		{
			stopAction();
			stopDisappear();
			return;
		}
		
		if (!AttackStanceTaskManager.getInstance().isInAttackStance(owner))
		{
			stopAction();
			return;
		}
		
		if (Rnd.get(1, 100) < cubic.getChance())
		{
			L2Character target = getCubicAttackTarget();
			
			if (target != null)
			{
				handleSkill(target);
			}
		}
	}
	
	private void healTask()
	{
		if (!checkOwnerState())
		{
			stopAction();
			stopDisappear();
			return;
		}
		
		L2Character target = getCubicHealTarget();
		
		if (target != null)
		{
			handleSkill(target);
		}
	}
	
	protected boolean checkOwnerState()
	{
		if (owner == null)
		{
			return false;
		}
		
		if (owner.isDead() || !(owner.isOnline()))
		{
			owner.delCubic(cubic.getType());
			owner.broadcastUserInfo();
			return false;
		}
		
		return true;
	}
	
	public void stopAction()
	{
		if (actionTask != null)
		{
			actionTask.cancel(true);
			actionTask = null;
		}
	}
	
	public void stopDisappear()
	{
		if (disappearTask != null)
		{
			disappearTask.cancel(true);
			disappearTask = null;
		}
	}
	
	protected void handleSkill(L2Character target)
	{
		// Get random skill for cubic skillList.
		final Skill skill = SkillData.getInstance().getSkill(cubic.getSkillsIds().get(Rnd.get(cubic.getSkillsIds().size())), level);
		if (skill != null)
		{
			try
			{
				ISkillHandler handler = SkillHandler.getHandler(skill.getSkillType());
				if (handler != null)
				{
					handler.useSkill(owner, skill, List.of(target));
					// Send a Server->Client packet MagicSkillUse
					owner.broadcastPacket(new MagicSkillUse(owner, target, skill.getId(), level, 0, 0));
				}
				else
				{
					LOG.log(Level.SEVERE, "missing handler for skill " + skill.getId());
				}
			}
			catch (final Exception e)
			{
				LOG.log(Level.SEVERE, "", e);
				e.printStackTrace();
			}
		}
	}
	
	protected L2Character getCubicHealTarget()
	{
		L2Character target = null;
		if (owner.isInParty())
		{
			final Party party = owner.getParty();
			
			if (party != null)
			{
				double percentLeft = 100.0;
				// temporary range check until real behavior of cubics is known/coded
				final int range = 400; // skill.getCastRange();
				
				// Get a list of Party Members
				for (L2PcInstance partyMember : party.getMembers())
				{
					if (partyMember.isDead())
					{
						continue;
					}
					
					if (!owner.isInsideRadius(partyMember, range, true, false))
					{
						continue;
					}
					
					// member is in cubic casting range, check if he need heal and if he have the lowest HP
					if (partyMember.getCurrentHp() < partyMember.getStat().getMaxHp())
					{
						if (percentLeft > (partyMember.getCurrentHp() / partyMember.getStat().getMaxHp()))
						{
							percentLeft = (partyMember.getCurrentHp() / partyMember.getStat().getMaxHp());
							target = partyMember;
						}
					}
				}
			}
		}
		else
		{
			if (owner.getCurrentHp() < owner.getStat().getMaxHp())
			{
				target = owner;
			}
		}
		
		return target;
	}
	
	/**
	 * @return
	 */
	protected L2Character getCubicAttackTarget()
	{
		L2Character target = null;
		
		if ((owner.getTarget() != null) && (owner.getTarget() instanceof L2Character))
		{
			target = (L2Character) owner.getTarget();
		}
		
		if (target != null)
		{
			if (target.isDead() || target.isAlikeDead())
			{
				target = null;
			}
			else if (!owner.isInsideRadius(target, 400, true, false))// skill.getCastRange();
			{
				target = null;
			}
			else if (!GeoEngine.getInstance().canSeeTarget(owner, target))
			{
				target = null;
			}
			else if (owner.isInsidePeaceZone(target))
			{
				target = null;
			}
			else if (target instanceof L2Attackable)
			{
				// If not the monster you have on your list of aggro nullify the target.
				L2Attackable monster = (L2Attackable) target;
				
				if (!monster.getAggroList().containsKey(owner))
				{
					target = null;
				}
			}
			else if (target instanceof L2Playable)
			{
				L2PcInstance targetPlayer = target.getActingPlayer();
				if (targetPlayer == null)
				{
					target = null;
				}
				else if (targetPlayer == owner)
				{
					target = null;
				}
				else if (!(targetPlayer.isStatusPvpFlag(FlagType.NON_PVP) || (targetPlayer.getKarma() != 0)) && !targetPlayer.getAttackByList().contains(owner))
				{
					// If ever we attack does not have to attack the cubic.
					// If you have karma or is in pvpFlag not have to act the cubic.
					target = null;
				}
			}
			
		}
		
		if (target == null)
		{
			for (L2Attackable atk : owner.getKnownList().getObjectTypeInRadius(L2Attackable.class, 400))// skill.getCastRange();
			{
				if (atk.isDead() || atk.isAlikeDead())
				{
					continue;
				}
				if (!atk.getAggroList().containsKey(owner))
				{
					continue;
				}
				if (!GeoEngine.getInstance().canSeeTarget(owner, atk))
				{
					continue;
				}
				if (owner.isInsidePeaceZone(atk))
				{
					continue;
				}
				
				target = atk;
				break;
			}
		}
		
		return target;
	}
}
