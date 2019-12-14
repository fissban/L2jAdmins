package l2j.gameserver.model.actor.manager.character.knownlist;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeGuardInstance;
import l2j.gameserver.model.entity.castle.Castle;

public class SiegeGuardKnownList extends AttackableKnownList
{
	public SiegeGuardKnownList(L2SiegeGuardInstance activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public boolean addObject(L2Object object)
	{
		if (!super.addObject(object))
		{
			return false;
		}
		
		// get siege guard
		final L2SiegeGuardInstance guard = (L2SiegeGuardInstance) activeObject;
		
		// Check if siege is in progress
		final Castle castle = guard.getCastle();
		if ((castle != null) && castle.getSiege().isInProgress())
		{
			// get player
			final L2PcInstance player = object.getActingPlayer();
			
			// check player's clan is in siege attacker list
			if ((player != null) && ((player.getClan() == null) || castle.getSiege().isAttacker(player.getClan())))
			{
				// try to set AI to attack
				if (guard.getAI().getIntention() == CtrlIntentionType.IDLE)
				{
					guard.getAI().setIntention(CtrlIntentionType.ACTIVE, null);
				}
			}
		}
		return true;
	}
}
