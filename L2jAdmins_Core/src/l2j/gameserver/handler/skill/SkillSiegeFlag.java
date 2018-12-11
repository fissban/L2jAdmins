package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.Config;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban, zarie
 */
public class SkillSiegeFlag implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.SIEGEFLAG
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		var target = (L2PcInstance) activeChar;
		if (!checkCondition(target, skill))
		{
			return;
		}
		
		if (target.isAlikeDead())
		{
			return;
		}
		
		var npcTemplate = NpcData.getInstance().getTemplate(skill.getSummonNpcId());
		
		var headquarters = new L2SiegeFlagInstance(target, IdFactory.getInstance().getNextId(), npcTemplate);
		headquarters.setName(npcTemplate.getName());
		headquarters.setTitle(target.getName());
		headquarters.setCurrentHp(headquarters.getStat().getMaxHp());
		headquarters.setCurrentMp(headquarters.getStat().getMaxMp());
		headquarters.setHeading(target.getHeading());
		headquarters.spawnMe(target.getX() + 50, target.getY() + 100, target.getZ());
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
	
	private static boolean checkCondition(L2PcInstance activeChar, Skill skill)
	{
		var siege = SiegeManager.getInstance().getSiege(activeChar);
		
		if (siege == null)
		{
			activeChar.sendMessage("You may only place a siege Headquarter during a siege.");
			return false;
		}
		
		if ((activeChar.getClan() == null) || !activeChar.isClanLeader())
		{
			activeChar.sendMessage("Only clan leaders may place a siege Headquarter.");
			return false;
		}
		
		if (!siege.isAttacker(activeChar.getClan()))
		{
			activeChar.sendMessage("You may only place a siege Headquarter provided that you are an attacker.");
			return false;
		}
		
		if (activeChar.isInsideZone(ZoneType.NOHQ))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.NOT_SET_UP_BASE_HERE));
			return false;
		}
		
		if (siege.getClansListMngr().getClan(SiegeClanType.ATTACKER, activeChar.getClanId()).getFlagsCount() >= Config.SIEGE_FLAG_MAX_COUNT)
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.NOT_ANOTHER_HEADQUARTERS));
			return false;
		}
		
		return true;
	}
}
