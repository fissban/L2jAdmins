package l2j.gameserver.network.external.client;

import l2j.gameserver.data.CastleData;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.SummonAI;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.instance.L2StaticObjectInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.privatestore.PcStoreType;
import l2j.gameserver.model.privatestore.PrivateStoreList;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ChairSit;
import l2j.gameserver.network.external.server.RecipeShopManageList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * This class ...
 * @version $Revision: 1.11.2.7.2.9 $ $Date: 2005/04/06 16:13:48 $
 */
public class RequestActionUse extends AClientPacket
{
	private int actionId;
	private boolean ctrlPressed;
	private boolean shiftPressed;
	
	@Override
	protected void readImpl()
	{
		actionId = readD();
		ctrlPressed = (readD() == 1);
		shiftPressed = (readC() == 1);
	}
	
	@Override
	public void runImpl()
	{
		var activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		// LOG.finest(activeChar.getName() + " request Action use: id " + actionId + " 2:" + ctrlPressed + " 3:" + shiftPressed);
		
		// Don't do anything if player is dead or confused
		if (((activeChar.isFakeDeath() && (actionId != 0)) || activeChar.isDead() || activeChar.isOutOfControl()) && (actionId != 0))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		var pet = activeChar.getPet();
		var target = activeChar.getTarget();
		
		switch (actionId)
		{
			case 0: // -> sitDown/standUp
			{
				if (activeChar.isMounted())
				{
					break;
				}
				
				if ((target != null) && !activeChar.isSitting() && (target instanceof L2StaticObjectInstance) && (((L2StaticObjectInstance) target).getType() == 1) && (CastleData.getInstance().getCastle(target) != null)
					&& activeChar.isInsideRadius(target, L2StaticObjectInstance.INTERACTION_DISTANCE, false, false))
				{
					activeChar.sitDown();
					activeChar.broadcastPacket(new ChairSit(activeChar, ((L2StaticObjectInstance) target).getStaticObjectId()));
					break;
				}
				
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				else
				{
					if (!activeChar.isPendingSitting())
					{
						if (activeChar.isMoving())
						{
							activeChar.setIsPendingSitting(true);
						}
						else
						{
							if (activeChar.isAttackingDisabled() || activeChar.isImmobilized() || activeChar.isCastingNow())
							{
								return;
							}
							
							activeChar.sitDown();
						}
					}
				}
				
				break;
			}
			case 1: // -> run/walking
			{
				if (activeChar.isRunning())
				{
					activeChar.setWalking();
				}
				else
				{
					activeChar.setRunning();
				}
				
				break;
			}
			case 15: // -> pet follow/stop
			case 21: // -> pet follow/stop
			{
				if ((pet != null) && !pet.isMovementDisabled())
				{
					((SummonAI) pet.getAI()).notifyFollowStatusChange();
				}
				break;
			}
			case 16: // -> pet attack
			case 22: // -> pet attack
			{
				if ((target != null) && (pet != null) && (pet != target) && !pet.isMovementDisabled())
				{
					if (activeChar.isInOlympiadMode() && !activeChar.isOlympiadStart())
					{
						// if L2PcInstance is in Olympiad and the match isn't already start, send a Server->Client packet ActionFailed
						activeChar.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
					
					if ((!activeChar.isGM()) && activeChar.isInsidePeaceZone(target))
					{
						activeChar.sendPacket(SystemMessage.TARGET_IN_PEACEZONE);
						return;
					}
					
					if (target.isAutoAttackable(activeChar) || ctrlPressed)
					{
						if (target instanceof L2DoorInstance)
						{
							if (((L2DoorInstance) target).isAutoAttackable(activeChar))
							{
								pet.getAI().setIntention(CtrlIntentionType.ATTACK, target);
							}
						}
						else if (!pet.isSiegeGolem())
						{
							pet.getAI().setIntention(CtrlIntentionType.ATTACK, target);
						}
					}
					else
					{
						pet.setFollowStatus(false);
						pet.getAI().setIntention(CtrlIntentionType.FOLLOW, target);
					}
				}
				break;
			}
			case 17: // -> pet - cancel action
			case 23: // -> pet - cancel action
			{
				if ((pet != null) && !pet.isMovementDisabled())
				{
					pet.getAI().setIntention(CtrlIntentionType.ACTIVE, null);
				}
				
				break;
			}
			case 19: // -> pet unsummon
			{
				if (pet != null)
				{
					// returns pet to control item
					if (pet.isDead())
					{
						activeChar.sendPacket(SystemMessage.DEAD_PET_CANNOT_BE_RETURNED);
						return;
					}
					
					// if it is a pet and not a summon
					if (pet instanceof L2PetInstance)
					{
						if (!pet.isHungry())
						{
							pet.unSummon();
						}
						else
						{
							activeChar.sendPacket(SystemMessage.YOU_CANNOT_RESTORE_HUNGRY_PETS);
						}
					}
				}
				break;
			}
			case 38: // -> pet mount
			{
				activeChar.mountPlayer(pet);
				break;
			}
			case 32: // -> Wild Hog Cannon - Mode Change
			{
				useSkill(4230);
				break;
			}
			case 36: // -> Soulless - Toxic Smoke
			{
				useSkill(4259);
				break;
			}
			case 37:
			{
				if (activeChar.isAlikeDead())
				{
					sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (activeChar.getPrivateStore().isInStoreMode())
				{
					activeChar.getPrivateStore().setStoreType(PcStoreType.NONE);
					activeChar.broadcastUserInfo();
				}
				
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				
				if (activeChar.getPrivateStore().getCreateList() == null)
				{
					activeChar.getPrivateStore().setCreateList(new PrivateStoreList());
				}
				
				activeChar.sendPacket(new RecipeShopManageList(activeChar, true));
				break;
			}
			case 39: // -> Soulless - Parasite Burst
			{
				useSkill(4138);
				break;
			}
			case 41: // -> Wild Hog Cannon - Attack
			{
				if (!(target instanceof L2DoorInstance))
				{
					activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
					return;
				}
				
				useSkill(4230);
				break;
			}
			case 42: // -> Kai the Cat - Self Damage Shield
			{
				useSkill(4378, activeChar);
				break;
			}
			case 43: // -> Unicorn Merrow - Hydro Screw
			{
				useSkill(4137);
				break;
			}
			case 44: // -> Big Boom - Boom Attack
			{
				useSkill(4139);
				break;
			}
			case 45: // -> Unicorn Boxer - Master Recharge
			{
				useSkill(4025, activeChar);
				break;
			}
			case 46: // -> Mew the Cat - Mega Storm Strike
			{
				useSkill(4261);
				break;
			}
			case 47: // -> Silhouette - Steal Blood
			{
				useSkill(4260);
				break;
			}
			case 48: // -> Mechanic Golem - Mech. Cannon
			{
				useSkill(4068);
				break;
			}
			case 51:
			{
				// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
				if (activeChar.isAlikeDead())
				{
					sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (activeChar.getPrivateStore().isInStoreMode())
				{
					activeChar.getPrivateStore().setStoreType(PcStoreType.NONE);
					activeChar.broadcastUserInfo();
				}
				
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				
				if (activeChar.getPrivateStore().getCreateList() == null)
				{
					activeChar.getPrivateStore().setCreateList(new PrivateStoreList());
				}
				
				activeChar.sendPacket(new RecipeShopManageList(activeChar, false));
				break;
			}
			case 52: // -> unsummon
			{
				if ((pet != null) && (pet instanceof L2SummonInstance))
				{
					pet.unSummon();
				}
				break;
			}
			case 53: // -> move to target
			{
				if ((target != null) && (pet != null) && (pet != target) && !pet.isMovementDisabled())
				{
					pet.setFollowStatus(false);
					pet.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(target.getX(), target.getY(), target.getZ(), 0));
				}
				break;
			}
			case 54: // -> move to target hatch/strider
			{
				if ((target != null) && (pet != null) && (pet != target) && !pet.isMovementDisabled())
				{
					pet.getAI().setIntention(CtrlIntentionType.MOVE_TO, new LocationHolder(target.getX(), target.getY(), target.getZ(), 0));
				}
				
				break;
			}
			case 96: // -> Quit Party Command Channel
			{
				LOG.info("96 Accessed");
				break;
			}
			case 97: // -> Request Party Command Channel Info
			{
				LOG.info("97 Accessed");
				break;
			}
			case 1000: // -> Siege Golem - Siege Hammer
			{
				if (!(target instanceof L2DoorInstance))
				{
					activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
					return;
				}
				
				useSkill(4079);
				break;
			}
			case 1001:
				LOG.info("1001 Accessed");
				break;
			case 1003: // -> Wind Hatchling/Strider - Wild Stun
			{
				useSkill(4710); // TODO use correct skill lvl based on pet lvl
				break;
			}
			case 1004: // -> Wind Hatchling/Strider - Wild Defense
			{
				useSkill(4711, activeChar); // TODO use correct skill lvl based on pet lvl
				break;
			}
			case 1005: // -> Star Hatchling/Strider - Bright Burst
			{
				useSkill(4712); // TODO use correct skill lvl based on pet lvl
				break;
			}
			case 1006: // -> Star Hatchling/Strider - Bright Heal
			{
				useSkill(4713, activeChar); // TODO use correct skill lvl based on pet lvl
				break;
			}
			case 1007: // -> Cat Queen - Blessing of Queen
			{
				useSkill(4699, activeChar);
				break;
			}
			case 1008: // -> Cat Queen - Gift of Queen
			{
				useSkill(4700, activeChar);
				break;
			}
			case 1009: // -> Cat Queen - Cure of Queen
			{
				useSkill(4701);
				break;
			}
			case 1010: // -> Unicorn Seraphim - Blessing of Seraphim
			{
				useSkill(4702, activeChar);
				break;
			}
			case 1011: // -> Unicorn Seraphim - Gift of Seraphim
			{
				useSkill(4703, activeChar);
				break;
			}
			case 1012: // -> Unicorn Seraphim - Cure of Seraphim
			{
				useSkill(4704);
				break;
			}
			case 1013: // -> Nightshade - Curse of Shade
			{
				useSkill(4705);
				break;
			}
			case 1014: // -> Nightshade - Mass Curse of Shade
			{
				useSkill(4706);
				break;
			}
			case 1015: // -> Nightshade - Shade Sacrifice
			{
				useSkill(4707);
				break;
			}
			case 1016: // Cursed Man - Cursed Blow
			{
				useSkill(4709);
				break;
			}
			case 1017: // -> Cursed Man - Cursed Strike/Stun
			{
				useSkill(4708);
				break;
			}
			default:
				LOG.warning(activeChar.getName() + ": unhandled action type " + actionId);
		}
	}
	
	/*
	 * Cast a skill for active pet/servitor. Target is specified as a parameter but can be overwrited or ignored depending on skill type.
	 */
	private void useSkill(int skillId, L2Object target)
	{
		var activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		var activeSummon = activeChar.getPet();
		
		if (activeChar.getPrivateStore().isInStoreMode())
		{
			activeChar.sendMessage("Cannot use skills while trading.");
			return;
		}
		
		if (activeSummon != null)
		{
			var skills = activeSummon.getTemplate().getSkills();
			
			if (skills == null)
			{
				return;
			}
			
			if (skills.isEmpty())
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.S1_NOT_AVAILABLE).addString("skill"));
				return;
			}
			
			var skill = skills.get(skillId);
			if (skill == null)
			{
				LOG.warning("Skill " + skillId + " missing from npc_skills.sql for a summon id " + activeSummon.getId());
				return;
			}
			
			activeSummon.setTarget(target);
			activeSummon.useMagic(skill, ctrlPressed, shiftPressed);
		}
	}
	
	/*
	 * Cast a skill for active pet/servitor. Target is retrieved from owner' target, then validated by overloaded method useSkill(int, L2Character).
	 */
	private void useSkill(int skillId)
	{
		var activeChar = getClient().getActiveChar();
		
		if (activeChar != null)
		{
			useSkill(skillId, activeChar.getTarget());
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return true;
	}
}
