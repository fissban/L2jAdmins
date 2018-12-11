package l2j.gameserver.model.actor.status;

import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;

public class NpcStatus extends CharStatus
{
	public NpcStatus(L2Npc activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public final void reduceHp(double value, L2Character attacker, boolean awake)
	{
		if (getActiveChar().isDead())
		{
			return;
		}
		
		// Add attackers to npc's attacker list
		if (attacker != null)
		{
			getActiveChar().addAttackerToAttackByList(attacker);
		}
		
		super.reduceHp(value, attacker, awake);
	}
	
	@Override
	public L2Npc getActiveChar()
	{
		return (L2Npc) super.getActiveChar();
	}
}
