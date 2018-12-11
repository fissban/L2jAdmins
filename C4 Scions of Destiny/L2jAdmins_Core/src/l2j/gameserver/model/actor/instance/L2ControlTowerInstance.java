package l2j.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.spawn.Spawn;

public class L2ControlTowerInstance extends L2Npc
{
	private final List<Spawn> guards = new ArrayList<>();
	
	public L2ControlTowerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2ControlTowerInstance);
	}
	
	@Override
	public boolean isAttackable()
	{
		// Attackable during siege by attacker only
		return ((getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress());
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		// Attackable during siege by attacker only
		return ((attacker != null) && (attacker instanceof L2PcInstance) && (getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress() && getCastle().getSiege().isAttacker(((L2PcInstance) attacker).getClan()));
	}
	
	@Override
	public void onForcedAttack(L2PcInstance player)
	{
		onAction(player, true);
	}
	
	public void onDeath()
	{
		if (getCastle().getSiege().isInProgress())
		{
			getCastle().getSiege().getControlTowerMngr().killed(this);
			
			for (Spawn spawn : guards)
			{
				if (spawn == null)
				{
					continue;
				}
				spawn.stopRespawn();
			}
		}
	}
	
	public void registerGuard(Spawn guard)
	{
		guards.add(guard);
	}
}
