package l2j.gameserver.handler.target;

import java.util.Collections;
import java.util.List;

import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2PetInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillTargetType;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class TargetCorpsePlayer implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return Collections.emptyList();
		}
		
		if (!target.isAlikeDead())
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return Collections.emptyList();
		}
		
		if (skill.getSkillType() == SkillType.RESURRECT)
		{
			L2Character dead = null;
			if (target instanceof L2PcInstance)
			{
				dead = target;
			}
			else if (target instanceof L2PetInstance)
			{
				dead = target;
			}
			
			if (SiegeManager.getInstance().getSiege(dead) != null)
			{
				// Like L2Off
				// You can only revive another character during the siege if both are part of it.
				if (SiegeManager.getInstance().getSiege(activeChar) == null)
				{
					activeChar.sendPacket(SystemMessage.CANNOT_BE_RESURRECTED_DURING_SIEGE);
					return Collections.emptyList();
				}
			}
			
			if (dead instanceof L2PcInstance)
			{
				if (((L2PcInstance) dead).getRequestRevive().isReviveRequested())
				{
					if (((L2PcInstance) dead).getRequestRevive().isRevivingPet())
					{
						activeChar.sendPacket(SystemMessage.MASTER_CANNOT_RES); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
					}
					else
					{
						activeChar.sendPacket(SystemMessage.RES_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
					}
					
					return Collections.emptyList();
				}
			}
			
			if (dead instanceof L2PetInstance)
			{
				if (((L2PetInstance) dead).getOwner() != activeChar)
				{
					activeChar.sendMessage("You are not the owner of this pet.");
					return Collections.emptyList();
				}
			}
		}
		
		return List.of(target);
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_CORPSE_PLAYER;
	}
}
