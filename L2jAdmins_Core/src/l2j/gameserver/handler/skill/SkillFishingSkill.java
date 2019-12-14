package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.SystemMessage;

public class SkillFishingSkill implements ISkillHandler
{
	private static final int FISH_EXPERTISE = 1315;
	
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.PUMPING,
			SkillType.REELING
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
		
		var fishTask = player.getFishing().getCombat();
		if (fishTask == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		var weaponItem = player.getActiveWeaponItem();
		var weaponInst = activeChar.getActiveWeaponInstance();
		
		if ((weaponInst == null) || (weaponItem == null))
		{
			return;
		}
		
		var ss = 1;
		var pen = 0;
		if (activeChar.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			ss = 2;
		}
		
		double gradeBonus = 1 + (weaponItem.getCrystalType().ordinal() * 0.1);
		int dmg = (int) (skill.getPower() * gradeBonus * ss);
		
		if (player.getSkillLevel(FISH_EXPERTISE) <= (skill.getLevel() - 2)) // 1315 - Fish Expertise
		{
			// Penalty
			player.sendPacket(SystemMessage.REELING_PUMPING_3_LEVELS_HIGHER_THAN_FISHING_PENALTY);
			
			pen = 50;
			dmg -= pen;
		}
		
		if (ss > 1)
		{
			weaponInst.setChargedShot(ShotType.FISH_SOULSHOTS, false);
		}
		
		if (skill.getSkillType() == SkillType.REELING)
		{
			fishTask.useRealing(dmg, pen);
		}
		else
		{
			// Pumping
			fishTask.usePumping(dmg, pen);
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		// Check fish Skills
		if (activeChar.getFishing().getCombat() == null)
		{
			switch (skill.getSkillType())
			{
				case PUMPING:
					activeChar.sendPacket(SystemMessage.CAN_USE_PUMPING_ONLY_WHILE_FISHING);
					return false;
				case REELING:
					activeChar.sendPacket(SystemMessage.CAN_USE_REELING_ONLY_WHILE_FISHING);
					return false;
			}
		}
		return true;
	}
}
