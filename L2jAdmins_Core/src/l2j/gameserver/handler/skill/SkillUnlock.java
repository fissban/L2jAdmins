package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.data.DoorData;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2ChestInstance;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SocialAction;
import l2j.gameserver.network.external.server.SocialAction.SocialActionType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.util.Rnd;

public class SkillUnlock implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.UNLOCK
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		for (var object : targets)
		{
			if (object instanceof L2DoorInstance)
			{
				var door = (L2DoorInstance) object;
				if (!door.isUnlockable())
				{
					activeChar.sendPacket(SystemMessage.UNABLE_TO_UNLOCK_DOOR);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				// FIXME: falta setear q puertas se pueden abrir con skills y cuales no.
				if (Formulas.calculateUnlockChance(skill) && (!door.isOpen()))
				{
					switch (door.getId())
					{
						case 20210001: // CORE door
						case 20210002: // CORE door
						case 20210003: // CORE door
							DoorData.getInstance().getDoor(20210001).openMe();
							DoorData.getInstance().getDoor(20210002).openMe();
							DoorData.getInstance().getDoor(20210003).openMe();
							DoorData.getInstance().getDoor(20210002).closeMeTask();
							DoorData.getInstance().getDoor(20210001).closeMeTask();
							DoorData.getInstance().getDoor(20210003).closeMeTask();
							break;
						default:
							door.openMe();
							door.closeMeTask();
					}
					
					activeChar.sendMessage("Unlocking the door.");
				}
				else
				{
					activeChar.sendPacket(SystemMessage.FAILED_TO_UNLOCK_DOOR);
				}
			}
			else if (object instanceof L2ChestInstance)
			{
				var chest = (L2ChestInstance) object;
				if ((chest.getCurrentHp() <= 0) || chest.isInteracted())
				{
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				var chestChance = 0;
				var chestGroup = 0;
				var chestTrapLimit = 0;
				
				if (chest.getLevel() > 60)
				{
					chestGroup = 4;
				}
				else if (chest.getLevel() > 40)
				{
					chestGroup = 3;
				}
				else if (chest.getLevel() > 30)
				{
					chestGroup = 2;
				}
				else
				{
					chestGroup = 1;
				}
				
				switch (chestGroup)
				{
					case 1:
					{
						if (skill.getLevel() > 10)
						{
							chestChance = 100;
						}
						else if (skill.getLevel() >= 3)
						{
							chestChance = 50;
						}
						else if (skill.getLevel() == 2)
						{
							chestChance = 45;
						}
						else if (skill.getLevel() == 1)
						{
							chestChance = 40;
						}
						
						chestTrapLimit = 10;
					}
						break;
					case 2:
					{
						if (skill.getLevel() > 12)
						{
							chestChance = 100;
						}
						else if (skill.getLevel() >= 7)
						{
							chestChance = 50;
						}
						else if (skill.getLevel() == 6)
						{
							chestChance = 45;
						}
						else if (skill.getLevel() == 5)
						{
							chestChance = 40;
						}
						else if (skill.getLevel() == 4)
						{
							chestChance = 35;
						}
						else if (skill.getLevel() == 3)
						{
							chestChance = 30;
						}
						
						chestTrapLimit = 30;
					}
						break;
					case 3:
					{
						if (skill.getLevel() >= 14)
						{
							chestChance = 50;
						}
						else if (skill.getLevel() == 13)
						{
							chestChance = 45;
						}
						else if (skill.getLevel() == 12)
						{
							chestChance = 40;
						}
						else if (skill.getLevel() == 11)
						{
							chestChance = 35;
						}
						else if (skill.getLevel() == 10)
						{
							chestChance = 30;
						}
						else if (skill.getLevel() == 9)
						{
							chestChance = 25;
						}
						else if (skill.getLevel() == 8)
						{
							chestChance = 20;
						}
						else if (skill.getLevel() == 7)
						{
							chestChance = 15;
						}
						else if (skill.getLevel() == 6)
						{
							chestChance = 10;
						}
						
						chestTrapLimit = 50;
					}
						break;
					case 4:
					{
						if (skill.getLevel() >= 14)
						{
							chestChance = 50;
						}
						else if (skill.getLevel() == 13)
						{
							chestChance = 45;
						}
						else if (skill.getLevel() == 12)
						{
							chestChance = 40;
						}
						else if (skill.getLevel() == 11)
						{
							chestChance = 35;
						}
						
						chestTrapLimit = 80;
					}
						break;
				}
				
				if (Rnd.get(100) <= chestChance)
				{
					activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), SocialActionType.VICTORY));
					chest.setSpecialDrop();
					chest.setMustRewardExpSp(false);
					chest.setInteracted();
					chest.reduceCurrentHp(99999999, activeChar);
				}
				else
				{
					activeChar.broadcastPacket(new SocialAction(activeChar.getObjectId(), SocialActionType.SAD));
					if (Rnd.get(100) < chestTrapLimit)
					{
						chest.chestTrap(activeChar);
					}
					chest.setInteracted();
					chest.addDamageHate(activeChar, 0, 999);
					chest.getAI().setIntention(CtrlIntentionType.ATTACK, activeChar);
				}
			}
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
