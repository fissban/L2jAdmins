package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.Formulas;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author slyce
 */
public class SkillManaDam implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.MANADAM
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (activeChar.isAlikeDead())
		{
			return;
		}
		
		var sps = activeChar.isChargedShot(ShotType.SPIRITSHOTS);
		var bss = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (var object : targets)
		{
			var target = (L2Character) object;
			if (target.isInvul())
			{
				return;
			}
			
			var damage = Formulas.calcManaDam(activeChar, target, skill, sps, bss);
			if (damage > 0)
			{
				var mp = (damage > target.getCurrentMp() ? target.getCurrentMp() : damage);
				target.reduceCurrentMp(mp);
				
				if (target instanceof L2PcInstance)
				{
					// Update current mp status on player
					((L2PcInstance) target).updateCurMp();
					
					target.sendPacket(new SystemMessage(SystemMessage.S2_MP_HAS_BEEN_DRAINED_BY_C1).addString(activeChar.getName()).addNumber((int) mp));
				}
			}
		}
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : (sps ? ShotType.SPIRITSHOTS : null), false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
