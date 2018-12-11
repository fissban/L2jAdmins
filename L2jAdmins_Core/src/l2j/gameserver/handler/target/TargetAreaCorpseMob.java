package l2j.gameserver.handler.target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2j.gameserver.geoengine.GeoEngine;
import l2j.gameserver.handler.TargetHandler.ITargetTypeHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Playable;
import l2j.gameserver.model.actor.L2Summon;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillTargetType;
import l2j.gameserver.model.zone.enums.ZoneType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class TargetAreaCorpseMob implements ITargetTypeHandler
{
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		if (target == null)
		{
			activeChar.sendPacket(SystemMessage.TARGET_CANT_FOUND);
			return Collections.emptyList();
		}
		
		if (!(target instanceof L2Attackable) || !target.isDead())
		{
			activeChar.sendPacket(SystemMessage.INCORRECT_TARGET);
			return Collections.emptyList();
		}
		
		boolean srcInArena = (activeChar.isInsideZone(ZoneType.PVP) && !activeChar.isInsideZone(ZoneType.SIEGE));
		
		List<L2Object> targetList = new ArrayList<>();
		
		L2PcInstance src = activeChar.getActingPlayer();
		
		if (src == null)
		{
			return Collections.emptyList();
		}
		
		for (L2Character obj : target.getKnownList().getObjectTypeInRadius(L2Character.class, skill.getSkillRadius()))
		{
			if (!((obj instanceof L2Attackable) || (obj instanceof L2Playable)) || obj.isDead() || (obj == activeChar))
			{
				continue;
			}
			
			if (!GeoEngine.getInstance().canSeeTarget(target, obj))
			{
				continue;
			}
			
			L2PcInstance trg = obj.getActingPlayer();
			
			if (trg != null)
			{
				if (trg == src)
				{
					continue;
				}
				
				if (trg.isInsideZone(ZoneType.PEACE))
				{
					continue;
				}
				
				if ((src.getParty() != null) && (trg.getParty() != null))
				{
					if (src.getParty().getLeader().getObjectId() == trg.getParty().getLeader().getObjectId())
					{
						continue;
					}
					
					if ((src.getParty().getCommandChannel() != null) && (trg.getParty().getCommandChannel() != null))
					{
						if (src.getParty().getCommandChannel() == trg.getParty().getCommandChannel())
						{
							continue;
						}
					}
				}
				
				if (!srcInArena && !(trg.isInsideZone(ZoneType.PVP) && !trg.isInsideZone(ZoneType.SIEGE)))
				{
					if ((src.getAllyId() == trg.getAllyId()) && (src.getAllyId() != 0))
					{
						continue;
					}
					
					if ((src.getClan() != null) && (trg.getClan() != null))
					{
						if (src.getClan().getId() == trg.getClan().getId())
						{
							continue;
						}
					}
					
					if (!src.checkPvpSkill(obj, skill, activeChar instanceof L2Summon))
					{
						continue;
					}
				}
			}
			
			targetList.add(obj);
		}
		
		return targetList;
	}
	
	@Override
	public Enum<SkillTargetType> getTargetType()
	{
		return SkillTargetType.TARGET_AREA_CORPSE_MOB;
	}
}
