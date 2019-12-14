package l2j.gameserver.model.actor.instance;

import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.entity.castle.siege.Siege;
import l2j.gameserver.model.entity.castle.siege.SiegeClanHolder;
import l2j.gameserver.model.entity.castle.siege.type.SiegeClanType;

public class L2SiegeFlagInstance extends L2Npc
{
	private final L2PcInstance player;
	private final Siege siege;
	
	public L2SiegeFlagInstance(L2PcInstance player, int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2SiegeFlagInstance);
		
		this.player = player;
		
		siege = SiegeManager.getInstance().getSiege(player);
		
		if ((player.getClan() == null) || (siege == null))
		{
			deleteMe();
		}
		else
		{
			SiegeClanHolder sc = siege.getClansListMngr().getClan(SiegeClanType.ATTACKER, player.getClan().getId());
			if (sc == null)
			{
				deleteMe();
			}
			else
			{
				sc.addFlag(this);
			}
		}
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
		return ((attacker != null) && (attacker instanceof L2PcInstance) && (getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress());
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}
		
		SiegeClanHolder sc = siege.getClansListMngr().getClan(SiegeClanType.ATTACKER, player.getClan().getId());
		if (sc != null)
		{
			sc.removeFlag(this);
		}
		return true;
	}
	
	@Override
	public void onForcedAttack(L2PcInstance player)
	{
		onAction(player, true);
	}
}
