package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2ArtefactInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;

/**
 * @author fissban
 */
public class SkillEngrave implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.TAKECASTLE
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		var player = (L2PcInstance) activeChar;
		if (!checkCondition(player, skill))
		{
			return;
		}
		
		if (player.isAlikeDead())
		{
			return;
		}
		
		try
		{
			if (targets.get(0) instanceof L2ArtefactInstance)
			{
				var siege = SiegeManager.getInstance().getSiege(player);
				siege.getCastle().engrave(player.getClan(), targets.get(0).getObjectId());
			}
		}
		catch (Exception e)
		{
			//
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
	
	private static boolean checkCondition(L2PcInstance player, Skill skill)
	{
		if (player == null)
		{
			return false;
		}
		
		var siege = SiegeManager.getInstance().getSiege(player);
		
		if (!player.isSkillDisabled(skill))
		{
			if (siege == null)
			{
				player.sendMessage("You may only use this skill during a siege.");
				return false;
			}
			
			if ((player.getClan() == null) || !player.isClanLeader())
			{
				player.sendMessage("Only clan leaders may use this skill.");
				return false;
			}
			
			if (!siege.isAttacker(player.getClan()))
			{
				player.sendMessage("You may only use this skill provided that you are an attacker.");
				return false;
			}
		}
		
		return true;
	}
}
