package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2DoorInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.Formulas;

/**
 * @author tomciaaa_
 */
public class SkillStrSiegeAssault implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.STRSIEGEASSAULT
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if ((activeChar == null) || !(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		var player = (L2PcInstance) activeChar;
		
		if (!player.isRiding())
		{
			return;
		}
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			
			if (!(target instanceof L2DoorInstance))
			{
				return;
			}
			
			var shld = Formulas.calcShldUse(player, target);
			var crit = Formulas.calcCrit(player.getStat().getCriticalHit(target, skill));
			
			var damage = (int) Formulas.calcPhysDam(player, target, skill, shld, crit, false);
			
			if (damage > 0)
			{
				target.reduceCurrentHp(damage, player);
				player.sendDamageMessage(target, damage, false, false, false);
				
				player.setChargedShot(ShotType.SOULSHOTS, false);
			}
		}
		
		// XXX recien al final destruimos el item? revisar este handler
		ItemInstance itemToTake = player.getInventory().getItemById(skill.getItemConsumeId());
		if (!player.getInventory().destroyItem("Consume", itemToTake.getObjectId(), skill.getItemConsumeCount(), null, true))
		{
			return;
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		if (!SiegeManager.getInstance().checkIfOkToSummon(activeChar, false))
		{
			return false;
		}
		return true;
	}
}
