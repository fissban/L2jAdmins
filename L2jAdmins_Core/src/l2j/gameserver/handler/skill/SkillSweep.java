package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.task.continuous.DecayTaskManager;

/**
 * @author drunk_
 */
public class SkillSweep implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.SWEEP
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
		
		for (var object : targets)
		{
			if (!(object instanceof L2MonsterInstance))
			{
				continue;
			}
			
			var monster = (L2MonsterInstance) object;
			
			if (!monster.isSpoil())
			{
				continue;
			}
			
			for (var sweepItem : monster.takeSweep())
			{
				if (player.isInParty())
				{
					player.getParty().distributeItem(player, sweepItem, true, monster);
				}
				else
				{
					player.getInventory().addItem("Sweep", sweepItem.getId(), sweepItem.getCount(), object, true);
				}
			}
			
			monster.clearSweep();
			monster.setSpoil(false);
			DecayTaskManager.getInstance().cancelDecayTask(monster);
			monster.deleteMe();
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		if (target instanceof L2Attackable)
		{
			var atkTarget = ((L2Attackable) target);
			if (atkTarget.isDead())
			{
				if (!atkTarget.isSpoil())
				{
					// Send a System Message to the L2PcInstance
					activeChar.sendPacket(SystemMessage.SWEEPER_FAILED_TARGET_NOT_SPOILED);
					return false;
				}
				
				var spoilerId = atkTarget.getIsSpoiledBy();
				
				if ((activeChar.getObjectId() != spoilerId) && !activeChar.isInLooterParty(spoilerId))
				{
					// Send a System Message to the L2PcInstance
					activeChar.sendPacket(SystemMessage.SWEEP_NOT_ALLOWED);
					return false;
				}
			}
		}
		return true;
	}
}
